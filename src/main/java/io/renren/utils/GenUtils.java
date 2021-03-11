package io.renren.utils;

import io.renren.entity.ColumnEntity;
import io.renren.entity.TableEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月19日 下午11:40:24
 */
public class GenUtils {

    private static final String DIR_TEMPLATE = "template";

    /**
     * 读取template下所有文件
     *
     * @return
     */
    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();
        File tplDir = new File(GenUtils.class.getResource("/" + DIR_TEMPLATE).getPath());
        if (tplDir.exists()) {
            appendTemplate(tplDir, templates, DIR_TEMPLATE);
        } else {
            throw new RuntimeException("找不到template目录:" + DIR_TEMPLATE);
        }
        return templates;
    }

    /**
     * 生成代码
     *
     * @param packageName 包名
     * @param modelName   模块名称
     * @param tabPrefix   表名前缀
     * @param table       表信息
     * @param columns     列信息
     * @param zip
     */
    public static void generatorCode(String packageName, String modelName,
                                     String tabPrefix, Map<String, String> table,
                                     List<Map<String, String>> columns, ZipOutputStream zip) {
        //配置信息
        Configuration config = getConfig();
        boolean hasBigDecimal = false;
        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.get("tableName"));
        tableEntity.setComments(table.get("tableComment"));
        //表名转换成Java类名
        tabPrefix = StringUtils.isEmpty(tabPrefix) ? config.getString("tablePrefix") : tabPrefix;
        String className = tableToJava(tableEntity.getTableName(), tabPrefix);
        tableEntity.setClassName(className);
        tableEntity.setClassname(StringUtils.uncapitalize(className));

        //列信息
        List<ColumnEntity> columsList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));

            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));

            //列的数据类型，转换成Java类型
            String attrType = config.getString(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && attrType.equals("BigDecimal")) {
                hasBigDecimal = true;
            }
            //是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columsList.add(columnEntity);
        }
        tableEntity.setColumns(columsList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        String mainPath = config.getString("mainPath");
        mainPath = StringUtils.isBlank(mainPath) ? "io.renren" : mainPath;
        packageName = StringUtils.isEmpty(packageName) ? config.getString("package") : packageName;
        modelName = StringUtils.isEmpty(modelName) ? config.getString("moduleName") : modelName;
        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);


        map.put("mainPath", mainPath);
        map.put("packagePath", packageName);
        map.put("moduleName", modelName);
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                //添加到zip
                zip.putNextEntry(new ZipEntry(
                        getFileName(template, tableEntity.getClassName(), packageName, modelName)));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RRException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }


    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix) && tableName.startsWith(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new RRException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName) {
        String basePath = FilenameUtils.getPath(template) + FilenameUtils.getBaseName(template);
        String extension = FilenameUtils.getExtension(template);
        return basePath.replace("${className}", className).
                replace("${classname}", className.toLowerCase()).
                replace("${packagePath}", packageName).
                replace("${moduleName}", moduleName).
                replace(".", File.separator) + "." + extension;
    }

    private static void appendTemplate(File tplDir, List<String> templates, String parentDir) {
        String tmpPath;
        for (File file : tplDir.listFiles()) {
            tmpPath = parentDir + File.separatorChar + file.getName();
            if (file.isDirectory()) {
                appendTemplate(file, templates, tmpPath);
            } else {
                templates.add(tmpPath);
            }
        }
    }

    public static void main(String[] args) {
        String template = "template\\interface\\main\\java\\${packagePath}\\${className}.java";
        System.out.println(FilenameUtils.getFullPathNoEndSeparator(template));
        String fileName = getFileName(template, "ShopRole", "net.sinedu.company.shop", "shop");
        System.out.println(fileName);
    }
}
