/**
 * 
 */
package com.yysports.cas.comm.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author RAY
 *
 */
@Data
@ApiModel(description = "域帳號與應用系統帳號對應資料")
public class CasAccountData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 域帳號名稱
	 */
	@ApiModelProperty(value = "ID", example = "1,2,3,4,5... ")
	private Long id;
	/**
	 * 域帳號名稱
	 */
	@ApiModelProperty(value = "域帳號名稱", example = "david.wang")
	private String domainAccount;

	/**
	 * 應用系統名稱
	 */
	@ApiModelProperty(value = "應用系統代碼")
	private String mappingSystem;

	/**
	 * 應用系統帳號ID
	 */
	@ApiModelProperty(value = "應用系統帳號ID")
	private BigInteger mappingAccountId;

	/**
	 * 應用系統帳號名稱
	 */
	@ApiModelProperty(value = "應用系統帳號名稱")
	private String mappingAccountName;

	/**
	 * 帳號啟用狀態，0:關閉，1:啟用
	 */
	@ApiModelProperty(value = "帳號啟用狀態，0:關閉，1:啟用", allowableValues = "0,1", example = "1")
	private Integer isActive;

	/**
	 * 資料異動日期
	 */
	@ApiModelProperty(value = "資料異動日期")
	private Date updateDate;

	/**
	 * 運行環境名稱
	 */
	@ApiModelProperty(value = "運行環境名稱", example = "DEV")
	private String environmentName;
}
