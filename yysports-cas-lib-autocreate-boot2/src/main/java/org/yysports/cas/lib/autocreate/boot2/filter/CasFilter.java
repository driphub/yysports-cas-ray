package org.yysports.cas.lib.autocreate.boot2.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.yysports.cas.comm.property.ApplicationProperties;
import com.yysports.cas.comm.utils.CasContextUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CasFilter extends AbstractCasFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		String profile = ApplicationProperties.getActProfile();
		String requestUri = request.getRequestURI();
		if (!"prod".equalsIgnoreCase(profile) && !"production".equalsIgnoreCase(profile)) {
			log.info("requestUri: " + requestUri);
			log.info("request.getQueryString(): " + request.getQueryString());
			log.info("request.getServletPath(): " + request.getServletPath());
			log.info("request.getRequestURL(): " + request.getRequestURL());
			log.info("request.getContextPath(): " + request.getContextPath());
			log.info("casProperties: " + casProperties);
			log.info("cookies: " + new Gson().toJson(request.getCookies()));
		}

		CasContextUtils casContextUtils = super.context.getBean("casContextUtils",
				CasContextUtils.class);

		log.info("casContextUtils.isLogoutRequest(): " + casContextUtils.isLogoutRequest());
		// 登出
		if (casContextUtils.isLogoutRequest()) {
			// 清COOKIE
			casContextUtils.expiredCookies(request, response);

			// 2019.09.24 RAY: 電商自訂義登出回調接口
			if (casProperties.getAppProperty().isCustomService()) {

				final String casLogoutString = super.casProperties.getLogoutUrl() + "?service="
						+ encodedUrl(casContextUtils.getRemoteAddr(request));
				response.sendRedirect(casLogoutString);
			} else {
				String serviceUrl = super.casProperties.getAppProperty().getApLoginUrl();
				// 前端傳入的迴向位址
				if (!Strings.isNullOrEmpty(request.getQueryString())) {
					serviceUrl = serviceUrl + "?" + request.getQueryString();
				}
				final String casLogoutString = super.casProperties.getLogoutUrl() + "?service="
						+ encodedUrl(serviceUrl);
				response.sendRedirect(casLogoutString);
			}
			// filterChain.doFilter(request, response);
			return;
		}
		log.info("使用者授權狀態: " + casContextUtils.isAucated());
		// 取REDIS內中的資料，判斷驗證狀態。如果已經登入，不處理
		if (casContextUtils.isAucated() == true) {
			filterChain.doFilter(request, response);
			return;
		}
		// 判斷TICKET
		String ticket = request.getParameter("ticket");
		log.info("CAS TICKET: {}", ticket);
		if (Strings.isNullOrEmpty(ticket)) {
			super.setRedirectCasLogin(request, response, filterChain);
			return;
		}
		// validate ticket
		final String ticketUrl = super.casProperties.getServerUrl() + "/serviceValidate?service="
				+ super.getService(request) + "&ticket=" + ticket;

		String respStr = super.restUtils.createGet(ticketUrl, String.class);

		// failed
		if (Strings.isNullOrEmpty(respStr) || !respStr.contains("authenticationSuccess")) {
			super.setRedirectCasLogin(request, response, filterChain);
			return;
		}
		log.info("request.getUserPrincipal(): " + new Gson().toJson(request.getUserPrincipal()));
		final String domainAccountName = super.getTextForElement(respStr, "user");
		log.info("CAS getUserName: {}", domainAccountName);
		// 建立REDIS和COOKIE
		try {
			casContextUtils.createWithAddUser(domainAccountName, response);;
		} catch (Exception ex) {
			log.error("casContextUtils.createUserInServer cause exception: {}", ex);
			throw new RuntimeException("建立USER SESSION發生錯誤。", ex);
		}

		filterChain.doFilter(request, response);
	}

}
