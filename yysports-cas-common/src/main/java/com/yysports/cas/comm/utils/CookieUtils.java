/**
 * 
 */
package com.yysports.cas.comm.utils;

import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dream
 *
 */
@Slf4j
public class CookieUtils {
	/**
	 * 根據cookie name拿cookie
	 * 
	 * @param cookieName
	 * @return Cookie
	 */
	public static Cookie getCookieByName(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			log.info("CasContextUtils.getCookieValByName， 沒有COOKIE資料，cookie name: {}", cookieName);
			return null;
		}
		for (Cookie cookie : cookies) {
			if (Objects.equals(cookieName, cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	/**
	 * 新增資料到cookie
	 * 
	 * @param response HttpServletResponse
	 * @param name     cookie name
	 * @param val      cookie value
	 * @param maxAge   cookie maxAge
	 * @param domain   cookie domain
	 * @param path     cookie path
	 */
	public static void addCookie(HttpServletResponse response, String name, String val,
			int maxAge, String domain, String path) {
		// 新增資料到cookie
		Cookie cookie = new Cookie(name, val);
		cookie.setMaxAge(maxAge);
		cookie.setDomain(domain);
		cookie.setPath(path);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}

	/**
	 * 清空COOKIE
	 * 
	 * @param response HttpServletResponse
	 * @param cookie
	 */
	public static void expireCookie(HttpServletResponse response, Cookie cookie) {
		// 清空COOKIE
		cookie.setValue("");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	/**
	 * 清空COOKIE
	 * 
	 * @param response HttpServletResponse
	 * @param name     cookie name
	 * @param domain   cookie domain
	 * @param path     cookie path
	 */
	public static void expireCookie(HttpServletResponse response, String name, String domain,
			String path) {
		Cookie cookie = new Cookie(name, "");
		cookie.setPath(path);
		cookie.setDomain(domain);
		expireCookie(response, cookie);
	}
}
