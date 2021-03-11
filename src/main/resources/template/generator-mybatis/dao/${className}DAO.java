package com.jd.ept.walmart.biz.dao;

import com.jd.ept.walmart.data.dbs.EptOpenDAO;
import com.jd.ept.walmart.data.dbs.MapperNamespace;
import com.jd.ept.walmart.domain.entity.AppWalmartCommodityExcelTask;
import org.springframework.stereotype.Repository;
import com.jd.ept.walmart.domain.entity.${className};
import java.util.List;
import java.util.Map;

/**
 * @author zhengmingming9
 * @date 2021-03-11 14:35
 * @description: TODO
 */
@Repository
@MapperNamespace("com.jd.ept.walmart.biz.dao.mapper.${className}Mapper")
public class ${className}DAO extends EptOpenDAO<${className}> {
    public ${className} queryObject(Long id){
        return selectOne("queryObject",id);
    }

    public List<${className}> queryList(Map<String, Object> map){
        return selectList("queryList",map);
    }

    public int queryTotal(Map<String, Object> map){
        return selectOne("queryTotal",map);
    }

    public int save(${className} appWalmartCommodityExcelTask){
        return insertByMapper("save",appWalmartCommodityExcelTask);
    }

    public int updateEntry(${className} ${classname}){
        return updateByMapper("update",${classname});
    }

}
