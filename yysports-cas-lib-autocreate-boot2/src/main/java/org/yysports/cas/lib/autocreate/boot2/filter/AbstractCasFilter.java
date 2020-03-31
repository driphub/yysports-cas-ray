/**
 * 
 */
package org.yysports.cas.lib.autocreate.boot2.filter;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParserFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import com.google.common.base.Charsets;
import com.yysports.cas.comm.utils.CasContextUtils;
import com.yysports.cas.comm.utils.RestUtils;
import com.yysports.cas.lib.autocreate.boot2.property.CasProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * @author RAY
 *
 */
@Component
@SuppressWarnings("unused")
public abstract class AbstractCasFilter extends OncePerRequestFilter
		implements ApplicationContextAware {

	@Autowired
	@Qualifier("casProperties")
	protected CasProperties casProperties;

	protected ApplicationContext context;
	/**
	 * 除外請求
	 */
	protected String excludedUris = "/swagger-ui.html|/webjars/springfox-swagger-ui|/swagger-resources|/v2/api-docs";
	private static final List<String> INGORE_FILE_LIST = new ArrayList<>(
			Arrays.asList("css", "htm", "js", "jpg", "jpeg", "png", "txt", "doc", "docx", "xls",
					"xlsx", "ppt", "pptx", "pdf", "tiff", "swf", "flv", "mp4", "mvb", "rmvb",
					"wma", "mp3", "rar", "zip", "7z", "exe", "aiff", "avi", "shtml", "xhtml",
					"shtm", "hbs", "map", "scss", "ttf", "eot", "svg", "woff", "woff2"));
	/**
	 * rest 物件
	 */
	protected RestUtils restUtils = RestUtils.createRest(true);

	public AbstractCasFilter() {
		super();
	}

	public void setAllowUntrusted(boolean allowUntrusted) {
		this.restUtils = RestUtils.createRest(allowUntrusted);
	}

	@PostConstruct
	public void init() {
		if (!Objects.isNull(casProperties.getAppProperty().getApExcludedUrls())) {
			excludedUris = excludedUris + "|"
					+ String.join("|", casProperties.getAppProperty().getApExcludedUrls());
		}
		excludedUris = excludedUris + "|" + String.join("|", INGORE_FILE_LIST);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

	/**
	 * ignore URLs
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
//		if ("GET".equalsIgnoreCase(request.getMethod())) {
//			return true;
//		}
		return Pattern.compile(excludedUris).matcher(request.getRequestURI()).find();
	}

	/**
	 * 導向到CAS SERVER
	 * 
	 * @param request     HttpServletRequest
	 * @param response    HttpServletResponse
	 * @param filterChain FilterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void setRedirectCasLogin(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String casLoginString = casProperties.getLoginUrl() + "?service=" + getService(request);
		response.sendRedirect(casLoginString);
		// filterChain.doFilter(request, response);
	}

	/**
	 * 取得目前的request路徑，並編碼
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected String getService(HttpServletRequest request) throws UnsupportedEncodingException {
		StringBuffer url = request.getRequestURL();

		String originHost = CasContextUtils.ORIGIN_HOST;
		if (request.getQueryString() != null) {
			url.append("?");
			int ticketLoc = request.getQueryString().indexOf("ticket=");
			if (ticketLoc == -1) {
				url.append(request.getQueryString());
			} else {
				if (request.getQueryString().indexOf("&ticket=") != -1) {
					url.append(request.getQueryString().substring(0,
							request.getQueryString().indexOf("&ticket=")));
				}
			}
		}
		if (request.getHeader("ORIGIN-HOST") != null) { // 傳出ORIGINA-HOST
			if (request.getQueryString() != null) {
				if (!request.getQueryString().contains(originHost)) {
					url.append("&").append(originHost).append("=")
							.append(request.getHeader("ORIGIN-HOST"));
				}
			} else {
				url.append("?").append(originHost).append("=")
						.append(request.getHeader("ORIGIN-HOST"));
			}
		}
		return encodedUrl(url.toString());
	}

	/**
	 * 取得CAS SERVER回傳的內容
	 * 
	 * @param xml
	 * @param element
	 * @return
	 */
	protected String getTextForElement(final String xml, final String element) {
		XMLReader reader = null;
		try {
			reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			reader.setFeature("http://xml.org/sax/features/namespaces", true);
			reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);
		} catch (final Exception e) {
			throw new RuntimeException("XMLReader creation failed", e);
		}
		final StringBuilder builder = new StringBuilder();

		final DefaultHandler handler = new DefaultHandler() {

			private boolean foundElement = false;

			@Override
			public void startElement(final String uri, final String localName, final String qName,
					final Attributes attributes) throws SAXException {
				if (localName.equals(element)) {
					this.foundElement = true;
				}
			}

			@Override
			public void endElement(final String uri, final String localName, final String qName)
					throws SAXException {
				if (localName.equals(element)) {
					this.foundElement = false;
				}
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				if (this.foundElement) {
					builder.append(ch, start, length);
				}
			}
		};

		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);

		try {
			reader.parse(new InputSource(new StringReader(xml)));
		} catch (final Exception e) {
			return null;
		}

		return builder.toString();
	}

	/**
	 * 編碼URL為UTF8
	 * 
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected String encodedUrl(String url) throws UnsupportedEncodingException {
		return URLEncoder.encode(url.toString(), "UTF-8");
	}
}
