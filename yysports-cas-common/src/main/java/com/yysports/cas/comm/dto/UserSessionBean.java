/**
 * 
 */
package com.yysports.cas.comm.dto;

import java.io.Serializable;

import com.yysports.cas.comm.utils.EncryptUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author RAY
 *
 */
@ApiModel(description = "建立Oauth2,Session資料物件")
@Slf4j
public class UserSessionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 字串連接字元
	 */
	private static final String JOINTER = "&!";
	/**
	 * 使用者資料
	 */
	@ApiModelProperty(value = "使用者帳號訊息")
	private String userData;

	/**
	 * System.currentTime
	 */
	@ApiModelProperty(value = "應用系統時間")
	private Long stamp;

	/**
	 * sessionid
	 */
	@ApiModelProperty(value = "sessionId")
	private String id;

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	public Long getStamp() {
		return stamp;
	}

	public void setStamp(Long stamp) {
		this.stamp = stamp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * singature
	 */
	@ApiModelProperty(value = "singature", hidden = true)
	public String getSingature() {
		try {
			return EncryptUtils.encryptString(this.getJoingtString());
		} catch (Exception ex) {
			log.info("UserSessionBean.getSingature cause exception params: {}, ex: {}", this, ex);
			return null;
		}
	}

	/**
	 * 加密用字串
	 * 
	 * @return
	 */
	@ApiModelProperty(value = "joingtString", hidden = true)
	public String getJoingtString() {
		return userData + JOINTER + stamp + JOINTER + id;
	}

	@Override
	public String toString() {
		return "UserSessionBean [userData=" + userData + ", stamp=" + stamp + ", id=" + id + "]";
	}

}
