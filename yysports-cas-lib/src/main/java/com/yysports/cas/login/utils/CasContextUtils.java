/**
 * 
 */
package com.yysports.cas.login.utils;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.yysports.cas.login.config.property.CasProperties;
import com.yysports.cas.login.dto.SystemAccountData;
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

	@Autowired
	HttpServletResponse response;

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
	 * rest 物件
	 */
	private static RestUtils restUtils = RestUtils.createRest(false);

	/**
	 * init
	 */
	@PostConstruct
	private void init() {
		this.request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
		this.logoutRequest = request.getRequestURI().contains(casProperties.getApLogoutUrl());
		if (!logoutRequest) { // 登出
			return;
		}
		// 取REDIS內中的資料
		SystemAccountData accData = getSystemAccountData();
		log.info("SystemAccountData: " + accData);
		if (accData != null) {
			user = accData;
			this.authcated = true;
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
	public void createUserInServer(final String domainAcc, HttpServletResponse resp)
			throws IOException {

		// 使用域帳號和應用系統名稱查詢應用系統帳號對應
		final String getAccUrl = casProperties.getRestServer()
				+ "/api/cas-account-mapping/system-account?" + "domainAccount=" + domainAcc
				+ "&mappingSystem=" + casProperties.getApName();

		log.info("createUserInServer getAccUrl: {}", getAccUrl);

		SystemAccountData accData = restUtils.createGet(getAccUrl, SystemAccountData.class);
		
		if (accData == null) {
			accData = new SystemAccountData();
			final String msg = "预账号: " + domainAcc + "，和子系统账号对应表查无数据，请建立对应!";
			accData.setErrMsg(msg);
			log.info("CasContextUtils.createUserInServer cause error, msg: {}", msg);
			return;
		}

		// session key
		final String uuid = UUID.randomUUID().toString();
		accData.setUid(uuid);

		// 建立REDIS對應
		log.info("createUserInServer RequestBody: {}", accData);

		final String result = restUtils.createPost(
				casProperties.getRestServer() + "/api/cas-session/session", accData,
				String.class);
		
		log.info("在REDIS中建立使用者訊息，建立結果: {}", result);
		if ("TRUE".equalsIgnoreCase(result)) {
			user = accData;
			// 新增資料到cookie
			Cookie cookie = new Cookie(casProperties.getCookieSessionId(), uuid);
			cookie.setMaxAge(casProperties.getExpiredTime() - 30);
			// cookie.setDomain(casProperties.getCookieDomain());
			cookie.setPath(casProperties.getCookiePath());
			cookie.setHttpOnly(true);
			resp.addCookie(cookie);
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
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return;
		}
		for (Cookie cookie : cookies) {
			if (casProperties.getCookieSessionId().equals(cookie.getName())) {
				// 清空REDIS資料
				String sessionKey = cookie.getValue();
				restUtils.createDelete(casProperties.getRestServer()
						+ "/api/cas-session/session?sessionId=" + sessionKey, String.class);
				// 清空COOKIE
				cookie.setValue("");
				cookie.setPath(casProperties.getCookiePath());
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				break;
			}
		}
	}

	/**
	 * 取REDIS中的資料
	 * 
	 * @param request HttpServletRequest
	 * @return boolean
	 */
	private SystemAccountData getSystemAccountData() {

		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		Cookie authCookie = null;
		for (Cookie cookie : cookies) {
			if (Objects.equals(casProperties.getCookieSessionId(), cookie.getName())) {
				authCookie = cookie;
			}
		}
		if (authCookie == null) {
			return null;
		}

		final String url = casProperties.getRestServer() + "/api/cas-session/session?sessionId="
				+ authCookie.getValue();
		log.info("getSystemAccountData url: {}", url);

		SystemAccountData accData = restUtils.createGet(url, SystemAccountData.class);

		return accData;
	}

	/**
	 * 取得User物件
	 * 
	 * @return
	 */
	public static SystemAccountData getCurrentUser() {
		return user;
	}

}
