/**
 * 
 */
package com.yysports.cas.comm.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author RAY
 *
 */
@ToString(callSuper = true)
@Getter
@Setter
@ApiModel(description = "域帳號與應用系統帳號對應資料", parent = SystemAccountQuery.class)
public class SystemAccountData extends SystemAccountQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 帳號啟用狀態，0:關閉，1:啟用
	 */
	@ApiModelProperty(value = "帳號啟用狀態，0:關閉，1:啟用")
	private Integer isActive;

	/**
	 * redis session key
	 */
	@ApiModelProperty(value = "sessionKey")
	private String uid;

	@ApiModelProperty(value = "cas ticket")
	private String casTicket;

	@ApiModelProperty(value = "錯誤訊息")
	private String errMsg;

}
