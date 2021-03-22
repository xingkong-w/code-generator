package com.jd.ept.walmart.domain.entity;

import java.util.Date;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;

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
@Builder
public class ${className} {

#foreach ($column in $columns)
		#if($column.columnName == $pk.columnName)
	@Id
	private $column.attrType $column.attrname;
		#end
		#if($column.columnName != $pk.columnName)
	/**
	 *$column.comments
	 */
	@Column(name="${column.columnName}")
	private $column.attrType $column.attrname;
		#end
#end


}
