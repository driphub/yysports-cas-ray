/**
 * 
 */
package com.yysports.cas.login.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.filter.GenericFilterBean;

import com.yysports.cas.login.config.property.CasProperties;

/**
 * @author RAY
 *
 */
public class AbstractSecurityCasFilter extends GenericFilterBean
		implements ApplicationContextAware {
	
	@Autowired
	@Qualifier("casProperties")
	protected CasProperties casProperties;

	protected ApplicationContext applicationContext;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
