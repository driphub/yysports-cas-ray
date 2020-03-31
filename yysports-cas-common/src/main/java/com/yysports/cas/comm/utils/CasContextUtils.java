/**
 * 
 */
package com.yysports.cas.comm.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.yysports.cas.comm.api.CasServerRestApi;
import com.yysports.cas.comm.dto.CasAccountData;
import com.yysports.cas.comm.dto.SystemAccountData;
import com.yysports.cas.comm.property.ApplicationProperties;
import com.yysports.cas.comm.property.CasProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author RAY
 *
 */
@Slf4j
public class CasContextUtils {

	private HttpServletRequest request;

//	@Autowired
//	HttpServletResponse response;

	@Autowired
	@Qualifier("casProperties")
	private CasProperties casProperties;

	/**
	 * SystemAccountData user 物件
	 */
	private static SystemAccountData user = null;

	/**
	 * 授權狀態
	 */
	private Boolean authcated = false;
	/**
	 * 登出
	 */
	private Boolean logoutRequest = false;

	/**
	 * 呼叫方的HOST NAME
	 */
	private String remoteHost;

	/**
	 * 呼叫CAS SERVER時，傳出HOST字串
	 */
	public static final String ORIGIN_HOST = "origin";

	/**
	 * cas api 接口
	 */
	@Autowired
	private CasServerRestApi casApi;

	private Map<String, String> envMap = new HashMap<String, String>();

	/**
	 * init
	 */
	@PostConstruct
	private void init() {
		this.request = ((ServletRequestAttributes) (RequestContextHolder
				.currentRequestAttributes())).getRequest();

		remoteHost = Objects.toString(request.getHeader("ORIGIN-HOST"), "");

		if (Strings.isNullOrEmpty(remoteHost)) {
			remoteHost = Objects.toString(request.getParameter(ORIGIN_HOST), "");
		}
		log.info("remoteHost: {}", remoteHost);

		if (envMap.isEmpty()) {
			String[][] envArr = { { "dev", "DEV" }, { "development", "DEV" }, { "pre", "PREPUB" },
					{ "prepub", "PREPUB" }, { "prd", "PRD" }, { "production", "PRD" },
					{ "test", "DEV" } };
			for (String[] arr : envArr) {
				envMap.put(arr[0], arr[1]);
				envMap.put(arr[0].toUpperCase(), arr[1]);
			}
		}

		this.logoutRequest = request.getRequestURI()
				.contains(casProperties.getAppProperty().getApLogoutUrl());
		if (logoutRequest) { // 登出
			return;
		}
		// 取REDIS內中的資料
		SystemAccountData accData = getSystemAccountData();
		log.info("查詢REDIS，取回使用者SESSION資料，SystemAccountData: " + accData);
		if (accData != null) {
			user = accData;
			this.authcated = true; // 認證狀態
		}
	}

	/**
	 * destroy
	 */
	@PreDestroy
	public void destory() {
		user = null;
	}

	/**
	 * 已認證
	 * 
	 * @return
	 */
	public Boolean isAucated() {
		return authcated;
	}

	/**
	 * 請求登出
	 * 
	 * @return
	 */
	public Boolean isLogoutRequest() {
		return logoutRequest;
	}

	/**
	 * 建立USER物件
	 * 
	 * @param domainAcc 域帳號名稱
	 * @param resp      HttpServletResponse
	 * @throws IOException
	 */
	public void createUserInfo(final String domainAcc, HttpServletResponse resp)
			throws IOException {

		// 使用域帳號和應用系統名稱查詢應用系統帳號對應
		final String envName = envMap.get(ApplicationProperties.getActProfile());
		if (envName == null) {
			log.info("環境變量，未設定在定義中，傳入環境變量值: {}", ApplicationProperties.getActProfile());
		}
		SystemAccountData accData = casApi.getSystemAccount(domainAcc, envName);

		if (accData == null) {
			this.setErrorSystemAccountData("域账号: " + domainAcc + "，和子系统账号对应表查无数据，请建立对应!");
			return;
		}
		if (accData.getIsActive() != 1) {
			accData.setErrMsg("域账号: " + domainAcc + "，此帳號已禁用，請解鎖");
			user = accData;
			return;
		}

		// 寫入使用者資料到REDIS，並建立COOKIE
		this.createRedisAndAddCookie(accData, resp);
	}

	/**
	 * 建立USER物件，若無域帳號對應，則建立一筆
	 * 
	 * @param domainAccount 域帳號名稱
	 * @param resp          HttpServletResponse
	 * @throws IOException
	 */
	public void createWithAddUser(final String domainAccount, HttpServletResponse resp)
			throws IOException {

		// 使用域帳號和應用系統名稱查詢應用系統帳號對應
		SystemAccountData accData = casApi.getSystemAccount(domainAccount,
				envMap.get(ApplicationProperties.getActProfile()));

		// 若無域帳號對應，則建立一筆
		if (accData == null) {
			CasAccountData insertData = new CasAccountData();
			insertData.setDomainAccount(domainAccount);
			insertData.setIsActive(1);
			insertData.setMappingAccountId(
					BigInteger.valueOf(new Random(System.currentTimeMillis()).nextLong()));
			insertData.setMappingAccountName(domainAccount); // 帳號預設用域帳號
			insertData.setMappingSystem(casProperties.getAppProperty().getApName());
			boolean result = casApi.createAccountMapping(insertData);

			accData = new SystemAccountData();
			if (result == false) {
				this.setErrorSystemAccountData("域账号: " + domainAccount + "，建立子系统账号对应表失敗!");
				return;
			}
			accData.setAccountId(insertData.getMappingAccountId());
			accData.setAccountName(domainAccount);
			accData.setDomainAccount(domainAccount);
			accData.setMappingSystem(insertData.getMappingSystem());
		}

		// 寫入使用者資料到REDIS，並建立COOKIE
		this.createRedisAndAddCookie(accData, resp);

	}

	/**
	 * 設定含錯誤訊息的使用者帳號資料
	 * 
	 * @param errMsg 錯誤訊息
	 */
	private void setErrorSystemAccountData(String errMsg) {
		SystemAccountData accData = new SystemAccountData();
		accData.setErrMsg(errMsg);
		log.info("CasContextUtils.createUserInServer cause error, msg: {}", errMsg);
		user = accData;
	}

	/**
	 * 寫入使用者資料到REDIS，並建立COOKIE
	 * 
	 * @param accData
	 * @param resp
	 */
	private void createRedisAndAddCookie(SystemAccountData accData, HttpServletResponse resp) {
		// session key
		final String uuid = UUID.randomUUID().toString();
		accData.setUid(uuid);
		// 建立REDIS對應
		Boolean result = casApi.addCasSession(accData);

		log.info("在REDIS中建立使用者訊息，建立結果: {}", result);
		if (result) {
			user = accData;
			// 新增資料到cookie
			CookieUtils.addCookie(resp, getWriteCookieName(), uuid,
					casProperties.getExpiredTime() - 30,
					casProperties.getAppProperty().getApCookieDomain(),
					casProperties.getCookiePath());
		} else {

		}
	}

	/**
	 * 清除COOKIES
	 * 
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public void expiredCookies(HttpServletRequest request, HttpServletResponse response) {
		log.info("清除COOKIE和REDIS SESSION START");

		Cookie authCookie = CookieUtils.getCookieByName(request, getWriteCookieName());

		if (authCookie == null) {
			return;
		}

		// 清空REDIS資料
		String sessionKey = authCookie.getValue();
		casApi.removeCasSession(sessionKey);

		// 清空COOKIE
		CookieUtils.expireCookie(response, getWriteCookieName(),
				casProperties.getAppProperty().getApCookieDomain(),
				casProperties.getCookiePath());
		log.info("清除COOKIE和REDIS SESSION END");
	}

	/**
	 * 取REDIS中的資料
	 * 
	 * @param request HttpServletRequest
	 * @return boolean
	 */
	private SystemAccountData getSystemAccountData() {

		Cookie authCookie = CookieUtils.getCookieByName(request, getWriteCookieName());
		log.info("目前請求中，authCookie值: " + new Gson().toJson(authCookie));
		if (authCookie == null || authCookie.getMaxAge() == 0) {
			return null;
		}

		return casApi.getCasSession(authCookie.getValue());
	}

	/**
	 * CAS 儲存的 cookie字串
	 * 
	 * @return
	 */
	private String getWriteCookieName() {
		String str = casProperties.getApSessionId() + "_" + ApplicationProperties.getActProfile();
		if (!Strings.isNullOrEmpty(remoteHost.split("\\.")[0])) {
			return str + "_" + remoteHost.split("\\.")[0];
		}
		return str;
	}

	/**
	 * 取得User物件
	 * 
	 * @return
	 */
	public static SystemAccountData getCurrentUser() {
		return user;
	}

	/**
	 * 取得原始呼叫位址
	 * 
	 * @param request
	 * @return
	 * @throws UnknownHostException
	 */
	public String getRemoteAddr(HttpServletRequest request) {
//		String ip = request.getHeader("x-forwarded-for");
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getHeader("Proxy-Client-IP");
//		}
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getHeader("WL-Proxy-Client-IP");
//		}
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getRemoteHost();
//		}
		String ip = request.getParameter("host");
		if (!Strings.isNullOrEmpty(ip)) {
			return ip;
		}
		ip = request.getHeader("ORIGIN-HOST");
		if (ip != null) {
			return "http://" + ip;
		}
		try {
			InetAddress addr = InetAddress.getByName(request.getRemoteAddr());
			return "http://" + addr.getCanonicalHostName();
		} catch (UnknownHostException ex) {
			log.info("CasContextUtils.getRemoteAddr cause exception!", ex);
		}
		return "http://" + request.getRemoteHost();
	}

}
