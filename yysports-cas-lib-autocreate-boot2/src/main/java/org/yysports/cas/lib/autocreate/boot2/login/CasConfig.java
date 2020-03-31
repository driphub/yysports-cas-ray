package org.yysports.cas.lib.autocreate.boot2.login;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextListener;
import org.yysports.cas.lib.autocreate.boot2.filter.CasFilter;
import com.yysports.cas.comm.property.ApplicationProperties;
import com.yysports.cas.comm.utils.CasContextUtils;
import com.yysports.cas.lib.autocreate.boot2.property.CasProperties;
import com.yysports.cas.lib.autocreate.boot2.property.YamlLoaderFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author RAY
 *
 */
@Configuration
@Configurable
@EnableConfigurationProperties({ ApplicationProperties.class, CasProperties.class })
@ComponentScan(basePackages = { "com.yysports.cas.lib.autocreate.boot2", "com.yysports.cas.comm.property" })
@SuppressWarnings("unused")
@Slf4j
public class CasConfig {

	@Value("${pousheng.system.cas.filterUrlPattern:/*}")
	private String filterUrlPattern;

//	/**
//	 * 自定義Property檔
//	 * 
//	 * @return
//	 */
//	@Profile({ "dev", "default" })
//	@Bean("casProperties")
//	public CasProperties casPropertiesStandslone() {
//		return bindPropertiesToTarget(CasProperties.class, "system.cas",
//				"classpath:cas-setting-default.yml");
//	}
//
//	@Profile("prod")
//	@Bean("casProperties")
//	public CasProperties casPropertiesProd() {
//		return bindPropertiesToTarget(CasProperties.class, "system.cas",
//				"classpath:cas-setting-prod.yml");
//	}

	@Bean("casContextUtils")
	@Scope("prototype")
	public CasContextUtils casContextUtils() {
		return new CasContextUtils();
	}

	/**
	 * main filter for CAS CasFilter
	 * 
	 * @return
	 */
	@Bean("casFilter")
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public CasFilter casFilter() {
		return new CasFilter();
	}

	/**
	 * 避免多線程，拿不到httpservletrequest問題
	 * 
	 * @return
	 */
	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	/**
	 * filter priority
	 * 
	 * @return
	 */
	@Bean("registerCasFilter")
	public FilterRegistrationBean<CasFilter> registerCasFilter() {
		FilterRegistrationBean<CasFilter> registrationBean = new FilterRegistrationBean<CasFilter>();
		registrationBean.setFilter(casFilter());
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		registrationBean.addUrlPatterns(filterUrlPattern);
		registrationBean.setName("CasFilter");
		return registrationBean;
	}

//	// 加载YML格式自定义配置文件
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer properties() {
//		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//		YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
//		// yaml.setResources(new FileSystemResource("classpath:cas-setting.yml"));//
//		// File引入
//		yaml.setResources(new ClassPathResource("cas-setting.yml"));
//		configurer.setProperties(yaml.getObject());
//		return configurer;
//	}

//	@Profile({ "default", "dev", "development", "prepub", "pre" })
//	@Component("casProperties")
//	@Primary
//	@PropertySource(value = { "classpath:cas-setting-default.yml" },
//			factory = YamlLoaderFactory.class)
//	static class DefaultProperties extends CasProperties {
//
//	}
//
//	@Profile({ "prod", "production" })
//	@Component("casProperties")
//	@Primary
//	@PropertySource(value = { "classpath:cas-setting-prod.yml" },
//			factory = YamlLoaderFactory.class)
//	static class ProdProperties extends CasProperties {
//
//	}
	
}
