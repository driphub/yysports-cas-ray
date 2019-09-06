package com.yysports.cas.login.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.yysports.cas.login.utils.CasContextUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CasFilter extends AbstractCasFilter {

	@Value("${spring.profiles.active:default}")
	private String envProfile;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		String requestUri = request.getRequestURI();
		if (!"prod".equalsIgnoreCase(envProfile) && !"production".equalsIgnoreCase(envProfile)) {
			log.info("requestUri: " + requestUri);
			log.info("request.getQueryString(): " + request.getQueryString());
			log.info("request.getServletPath(): " + request.getServletPath());
			log.info("request.getRequestURL(): " + request.getRequestURL());
			log.info("request.getContextPath(): " + request.getContextPath());
			log.info("casProperties: " + casProperties);
			log.info("cookies: " + new Gson().toJson(request.getCookies()));
		}

		// ignore type
//		if (requestUri.lastIndexOf(".") != -1 && staticFileExtensionList
//				.contains(requestUri.substring(requestUri.lastIndexOf(".") + 1))) {
//			filterChain.doFilter(request, response);
//			return;
//		}
		CasContextUtils casContextUtils = super.applicationContext.getBean("casContextUtils",
				CasContextUtils.class);
		// 登出
		if (casContextUtils.isLogoutRequest()) {
			// 清COOKIE
			casContextUtils.expiredCookies(request, response);

			String casLogoutString = super.casProperties.getLogoutUrl() + "?service=" + UriUtils
					.encodePath(super.casProperties.getApLogoutUrl(), Charsets.UTF_8.toString());
			response.sendRedirect(casLogoutString);
			filterChain.doFilter(request, response);
			return;
		}

		// 取REDIS內中的資料，判斷驗證狀態。如果已經登入，不處理
		if (casContextUtils.isAucated() == true) {
			filterChain.doFilter(request, response);
			return;
		}
		// 判斷TICKET
		String ticket = request.getParameter("ticket");
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
		final String domainAccountName = super.getTextForElement(respStr, "user");
		log.info("CAS getUserName: {}", domainAccountName);
		// 建立REDIS和COOKIE
		try {
			casContextUtils.createUserInServer(domainAccountName, response);
		} catch (Exception ex) {
			log.error("casContextUtils.createUserInServer cause exception: {}", ex);
			throw new RuntimeException("建立USER SESSION發生錯誤。", ex);
		}

		filterChain.doFilter(request, response);
	}

}
