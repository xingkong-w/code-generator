package ${packagePath}.${moduleName}.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.jd.ept.walmart.biz.dao.${className}DAO;
import com.jd.ept.walmart.domain.entity.${className};
import com.jd.ept.walmart.biz.service.commodity.${className}Service;
import lombok.extern.slf4j.Slf4j;

/**
 * ${comments}
 * @author ${author}
 * @email ${email}
 * @date ${datetime}
 */
@Slf4j
@Service("${classname}Service")
public class ${className}ServiceImpl implements ${className}Service {
	@Autowired
	private ${className}DAO ${classname}Dao;

	@Override
	public ${className} queryObject(${pk.attrType} ${pk.attrname}){
		return ${classname}Dao.queryObject(${pk.attrname});
	}

	@Override
	public List<${className}> queryList(Map<String, Object> map){
		return ${classname}Dao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map){
		return ${classname}Dao.queryTotal(map);
	}

	@Override
	public void save(${className} ${classname}){
		${classname}Dao.save(${classname});
	}

	@Override
	public void update(${className} ${classname}){
		${classname}Dao.updateEntry(${classname});
	}



}
