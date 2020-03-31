package com.yysports.cas.admin.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yysports.frameworks.admin.web.filter.AuthFilter;
import com.yysports.frameworks.admin.web.filter.CasAuthFilter;
import com.yysports.frameworks.admin.web.filter.RequestWrapperEnvInitFilter;
import com.yysports.frameworks.admin.web.filter.WebAdminRequestWrapperFilter;

@Configuration
public class ApplicationFilterConfig {

	@Bean
	public WebAdminRequestWrapperFilter webAdminRequestWrapperFilter() {
		return new WebAdminRequestWrapperFilter();
	}

	@Bean
	public AuthFilter authFilter() {
		return new AuthFilter();
	}

	@Bean
	public CasAuthFilter casAuthFilter() {
		return new CasAuthFilter();
	}

	@Bean
	public RequestWrapperEnvInitFilter requestWrapperEnvInitFilter() {
		return new RequestWrapperEnvInitFilter();
	}
}
