package ${packagePath}.${moduleName}.service;

import com.jd.ept.walmart.biz.dao.${className}DAO;
import com.jd.ept.walmart.domain.entity.${className};
import java.util.List;
import java.util.Map;

/**
 * ${comments}
 * 
 * @author ${author}
 * @email ${email}
 * @date ${datetime}
 */
public interface ${className}Service {
	
	${className} queryObject(${pk.attrType} ${pk.attrname});
	
	List<${className}> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(${className} ${classname});
	
	void update(${className} ${classname});
	

}
