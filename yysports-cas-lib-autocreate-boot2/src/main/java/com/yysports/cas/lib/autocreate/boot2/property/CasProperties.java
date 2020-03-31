package com.yysports.cas.lib.autocreate.boot2.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.yysports.cas.comm.property.ApplicationProperties;



@Component("casProperties")
@Primary
//@PropertySource(value = { "classpath:cas-setting.yml" },
//		factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "system.cas")
// @EnableConfigurationProperties(ApplicationProperties.class)
public class CasProperties {

	@Autowired
	@Qualifier("applicationProperties")
	ApplicationProperties appProperty;

	private String serverUrl;

	private String loginUrl;

	private String logoutUrl;

	private String cookieSessionId;
	/**
	 * CAS 帳號對應，SESSION服務器位址
	 */
	private String restServer;

	/**
	 * cookie path
	 */
	private String cookiePath;
	/**
	 * cookie過期時間
	 */
	private Integer expiredTime;

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getApSessionId() {
		return cookieSessionId + "_" + appProperty.getApName();
	}

	public void setCookieSessionId(String cookieSessionId) {
		this.cookieSessionId = cookieSessionId;
	}

	public String getRestServer() {
		return restServer;
	}

	public void setRestServer(String restServer) {
		this.restServer = restServer;
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

	public Integer getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Integer expiredTime) {
		this.expiredTime = expiredTime;
	}

	public ApplicationProperties getAppProperty() {
		return appProperty;
	}

	public void setAppProperty(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
	}

	@Override
	public String toString() {
		return "CasProperties [appProperty=" + appProperty + ", serverUrl=" + serverUrl
				+ ", loginUrl=" + loginUrl + ", logoutUrl=" + logoutUrl + ", cookieSessionId="
				+ cookieSessionId + ", restServer=" + restServer + ", cookiePath=" + cookiePath
				+ ", expiredTime=" + expiredTime + "]";
	}

}
