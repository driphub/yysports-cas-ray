/**
 * 
 */
package com.yysports.cas.comm.property;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author dream
 *
 */
@Component("applicationProperties")
@ConfigurationProperties(prefix = "pousheng.system.cas")
public class ApplicationProperties {

	/**
	 * init
	 */
	@PostConstruct
	private void init() {
		if (apLoginUrl == null) {
			apLoginUrl = apServerUrl + "/login";
		}
		if (apLogoutUrl == null) {
			apLogoutUrl = apServerUrl + "/logout";
		}
	}

	/**
	 * 目前運行環境
	 */
	private static String actProfile;

	/**
	 * 取分號最後一段 ${spring.profiles.active:default}
	 * 
	 * @return
	 */
	public static String getActProfile() {
		return actProfile;
	}

	@Value("${spring.profiles.active:default}")
	public void setActProfile(String actProfile) {
		// 取分號最後一段
		if (actProfile != null && actProfile.contains(",")) {
			actProfile = actProfile.substring(actProfile.lastIndexOf(",") + 1,
					actProfile.length());
		}
		ApplicationProperties.actProfile = actProfile;
	}

	/**
	 * 應用訪問地址 (必填)
	 */
	private String apServerUrl;

	/**
	 * 應用登入地址 (選填)
	 */
	private String apLoginUrl;

	/**
	 * 應用登出地址 (選填)
	 */
	private String apLogoutUrl;

	/**
	 * 應用cookies(選填)
	 */
	private String[] apCookies;

	/**
	 * 應用除外URLs (選填)
	 */
	private String[] apExcludedUrls;

	/**
	 * Cookie 域名
	 */
	private String apCookieDomain;

	/**
	 * 應用系統名稱(必填)
	 */
	private String apName;

	/**
	 * 自訂義CAS回調接口
	 */
	@Value("${pousheng.system.cas.customService:false}")
	private boolean customService;

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

	public String getApCookieDomain() {
		return apCookieDomain;
	}

	public void setApCookieDomain(String apCookieDomain) {
		this.apCookieDomain = apCookieDomain;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public boolean isCustomService() {
		return customService;
	}

	public void setCustomService(boolean customService) {
		this.customService = customService;
	}

	@Override
	public String toString() {
		return "ApplicationProperties [apServerUrl=" + apServerUrl + ", apLoginUrl=" + apLoginUrl
				+ ", apLogoutUrl=" + apLogoutUrl + ", apCookies=" + Arrays.toString(apCookies)
				+ ", apExcludedUrls=" + Arrays.toString(apExcludedUrls) + ", apCookieDomain="
				+ apCookieDomain + ", apName=" + apName + ", customService=" + customService
				+ ", actProfile=" + actProfile + "]";
	}

}
