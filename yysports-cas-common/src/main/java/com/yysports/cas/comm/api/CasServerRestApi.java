/**
 * 
 */
package com.yysports.cas.comm.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.yysports.cas.comm.dto.CasAccountData;
import com.yysports.cas.comm.dto.SystemAccountData;
import com.yysports.cas.comm.property.CasProperties;
import com.yysports.cas.comm.utils.RestUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dream
 *
 */
@Slf4j
@Component
public class CasServerRestApi {

	/**
	 * rest物件
	 */
	public RestUtils rest = RestUtils.createRest(false);

	@Autowired
	@Qualifier("casProperties")
	private CasProperties casProperties;

	/**
	 * 域帳號與應用系統帳號對應API requestMapping
	 */
	private String accMappingUrl;
	/**
	 * cas server session handle API requestMapping
	 */
	private String sessionHandleUrl;

	@PostConstruct
	private void init() {
		accMappingUrl = casProperties.getRestServer() + "/api/cas-account-mapping";
		sessionHandleUrl = casProperties.getRestServer() + "/api/cas-session";

	}

	/**
	 * 使用域帳號和應用系統名稱查詢應用系統帳號對應
	 * 
	 * @param domainAcc
	 * @return
	 */
	public SystemAccountData getSystemAccount(String domainAcc, String env) {
		log.info("accMappingUrl: {}, casProperties: {}", accMappingUrl, casProperties);
		// 使用域帳號和應用系統名稱查詢應用系統帳號對應
		final String getAccUrl = accMappingUrl + "/system-account?" + "domainAccount=" + domainAcc
				+ "&mappingSystem=" + casProperties.getAppProperty().getApName() + "&envName="
				+ env;

		log.info("createUserInServer getAccUrl: {}", getAccUrl);

		SystemAccountData accData = rest.createGet(getAccUrl, SystemAccountData.class);

		return accData;
	}

	/**
	 * 建立域帳號對應
	 * 
	 * @param data
	 * @return
	 */
	public boolean createAccountMapping(CasAccountData data) {

		final String postUrl = accMappingUrl + "/system-account";

		String result = rest.createPost(postUrl, data, String.class);
		if ("SUCCESS".equalsIgnoreCase(result)) {
			return true;
		}
		log.error("CasServerUtils.createAccountMapping cause exception, param: {}, exption: {}",
				data, result);
		return false;
	}

	/**
	 * 域帳號對應查詢成功，新增一筆資料到REDIS
	 * 
	 * @param data
	 * @return
	 */
	public Boolean addCasSession(SystemAccountData data) {
		// 建立REDIS對應
		log.info("CasServerUtils.addSession RequestBody: {}", data);

		return rest.createPost(sessionHandleUrl + "/session", data, Boolean.class);
	}

	/**
	 * 移除CAS REDIS的SESSION資料
	 * 
	 * @param sessionId
	 * @return
	 */
	public Boolean removeCasSession(String sessionId) {
		final String url = sessionHandleUrl + "/session?sessionId=" + sessionId;
		log.info("CasServerRestApi.removeCasSession url: {}", url);
		return rest.createDelete(url, Boolean.class);
	}

	/**
	 * 查詢CAS REDIS中的資料
	 * 
	 * @param sessionId
	 * @return
	 */
	public SystemAccountData getCasSession(String sessionId) {
		final String url = sessionHandleUrl + "/session?sessionId=" + sessionId;
		log.info("CasServerRestApi.getCasSession url: {}", url);

		return rest.createGet(url, SystemAccountData.class);
	}
}
