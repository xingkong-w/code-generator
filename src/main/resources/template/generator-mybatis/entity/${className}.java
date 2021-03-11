package com.jd.ept.walmart.domain.entity;

import java.util.Date;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

#if(${hasBigDecimal})
import java.math.BigDecimal;
#end


/**
 * ${comments}
 * 
 * @author ${author}
 * @email ${email}
 * @date ${datetime}
 */
@Entity
@Table(name="${tableName}")
@Data
public class ${className} {

#foreach ($column in $columns)
		#if($column.columnName == $pk.columnName)
	@Id
	private $column.attrType $column.attrname;
		#end
		#if($column.columnName != $pk.columnName)
	@Column(name="${column.columnName}")
	private $column.attrType $column.attrname;
		#end
#end


}
