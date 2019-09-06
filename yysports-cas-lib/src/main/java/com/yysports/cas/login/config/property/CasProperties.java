package com.yysports.cas.login.config.property;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

//@Component
// @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:cas-setting.yml")
//@ConfigurationProperties(prefix = "system.cas")
public class CasProperties {

	private String serverUrl;

	private String loginUrl;

	private String logoutUrl;

	private String cookieSessionId;
	/**
	 * CAS 帳號對應，SESSION服務器位址
	 */
	private String restServer;

	/**
	 * cookie domain
	 */
	private String cookieDomain;
	/**
	 * cookie path
	 */
	private String cookiePath;
	/**
	 * cookie過期時間
	 */
	private Integer expiredTime;

	/**
	 * 應用訪問地址 (必填)
	 */
	@Value("${app.apServerUrl}")
	private String apServerUrl;

	/**
	 * 應用登入地址 (選填)
	 */
	@Value("${app.apLoginUrl:${app.apServerUrl}/login}")
	private String apLoginUrl;

	/**
	 * 應用登出地址 (選填)
	 */
	@Value("${app.apLogoutUrl:${app.apServerUrl}/logout}")
	private String apLogoutUrl;

	/**
	 * 應用cookies(選填)
	 */
	@Value("${app.apCookies:null}")
	private String[] apCookies;

	/**
	 * 應用除外URLs (選填)
	 */
	@Value("${app.excludedUrls:null}")
	private String[] apExcludedUrls;

	/**
	 * 應用系統名稱(必填)
	 */
	@Value("${app.name}")
	private String apName;

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

	public String getCookieSessionId() {
		return cookieSessionId;
	}

	public void setCookieSessionId(String cookieSessionId) {
		this.cookieSessionId = cookieSessionId;
	}

	public String getApServerUrl() {
		return apServerUrl;
	}

	public void setApServerUrl(String apServerUrl) {
		this.apServerUrl = apServerUrl;
	}

	public String getApLoginUrl() {
		return apLoginUrl;
	}

	public void setApLoginUrl(String apLoginUrl) {
		this.apLoginUrl = apLoginUrl;
	}

	public String getApLogoutUrl() {
		return apLogoutUrl;
	}

	public void setApLogoutUrl(String apLogoutUrl) {
		this.apLogoutUrl = apLogoutUrl;
	}

	public String[] getApCookies() {
		return apCookies;
	}

	public void setApCookies(String[] apCookies) {
		this.apCookies = apCookies;
	}

	public String[] getApExcludedUrls() {
		return apExcludedUrls;
	}

	public void setApExcludedUrls(String[] apExcludedUrls) {
		this.apExcludedUrls = apExcludedUrls;
	}

	public String getRestServer() {
		return restServer;
	}

	public void setRestServer(String restServer) {
		this.restServer = restServer;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
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

	@Override
	public String toString() {
		return "CasProperties [serverUrl=" + serverUrl + ", loginUrl=" + loginUrl + ", logoutUrl="
				+ logoutUrl + ", cookieSessionId=" + cookieSessionId + ", restServer="
				+ restServer + ", cookieDomain=" + cookieDomain + ", cookiePath=" + cookiePath
				+ ", expiredTime=" + expiredTime + ", apServerUrl=" + apServerUrl
				+ ", apLoginUrl=" + apLoginUrl + ", apLogoutUrl=" + apLogoutUrl + ", apCookies="
				+ Arrays.toString(apCookies) + ", apExcludedUrls="
				+ Arrays.toString(apExcludedUrls) + ", apName=" + apName + "]";
	}

}
