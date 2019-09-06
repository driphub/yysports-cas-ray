/**
 * 
 */
package com.yysports.cas.login.dto;

import java.io.Serializable;
import java.math.BigInteger;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author RAY
 *
 */
@Data
@ApiModel(description = "域帳號與應用系統帳號對應資料")
public class SystemAccountData implements Serializable {

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
	 * 帳號啟用狀態，0:關閉，1:啟用
	 */
	@ApiModelProperty(value = "帳號啟用狀態，0:關閉，1:啟用")
	private Integer isActive;

	/**
	 * redis session key
	 */
	@ApiModelProperty(value = "sessionKey", hidden = true)
	private String uid;

	/**
	 * 查詢預帳號對應報錯時的錯誤提示
	 */
	@ApiModelProperty(value = "錯預訊息提示")
	private String errMsg;

}
