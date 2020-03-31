/**
 * 
 */
package com.yysports.cas.comm.dto;

import java.io.Serializable;
import java.math.BigInteger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author dream
 *
 */
@Data
@ToString
@ApiModel(description = "域帳號與應用系統帳號對應查詢物件")
public class SystemAccountQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 應用系統帳號ID
	 */
	@ApiModelProperty(value = "應用系統帳號ID")
	private BigInteger accountId;

	/**
	 * 應用系統帳號名稱
	 */
	@ApiModelProperty(value = "應用系統帳號名稱")
	private String accountName;

	/**
	 * 應用系統名稱
	 */
	@ApiModelProperty(value = "應用系統名稱")
	private String mappingSystem;

	/**
	 * 域帳號名稱
	 */
	@ApiModelProperty(value = "域帳號名稱", example = "david.wang")
	private String domainAccount;

	/**
	 * 運行環境名稱
	 */
	@ApiModelProperty(value = "運行環境名稱", example = "DEV")
	private String environmentName;
}
