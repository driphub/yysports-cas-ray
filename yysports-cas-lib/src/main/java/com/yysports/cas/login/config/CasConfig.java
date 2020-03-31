package com.yysports.cas.login.config;

import java.lang.reflect.Constructor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestContextListener;
import com.google.common.base.Strings;
import com.yysports.cas.comm.property.ApplicationProperties;
import com.yysports.cas.comm.property.CasProperties;
import com.yysports.cas.comm.utils.CasContextUtils;
import com.yysports.cas.login.filter.CasFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author RAY
 *
 */
@Configuration
@Configurable
@EnableConfigurationProperties(ApplicationProperties.class)
@ComponentScan(basePackages = { "com.yysports.cas.comm" })
@Slf4j
public class CasConfig implements ResourceLoaderAware {

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Value("${pousheng.system.cas.filterUrlPattern:/*}")
	private String filterUrlPattern;

	@Bean("casProperties")
	public CasProperties casProperties() {
		String envProfile = ApplicationProperties.getActProfile();
		String envName = "prod".equalsIgnoreCase(envProfile)
				|| "production".equalsIgnoreCase(envProfile) ? "prod" : "default";
		return bindPropertiesToTarget(CasProperties.class, "system.cas",
				"classpath:cas-setting-" + envName + ".yml");
	}
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
	
//	@Bean RequestContextFilter requestContextFilter() {
//		return new RequestContextFilter();
//	}

	/**
	 * filter priority
	 * 
	 * @return
	 */
	@Bean("registerCasFilter")
	public FilterRegistrationBean registerCasFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(casFilter());
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		registrationBean.addUrlPatterns(filterUrlPattern);
		registrationBean.setName("CasFilter");
		return registrationBean;
	}

//    private Filter expiredSessionFilter() {
//        SessionManagementFilter smf = new SessionManagementFilter(new HttpSessionSecurityContextRepository());
//        smf.setInvalidSessionStrategy((request, response) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session go BOOM!"));               
//        return smf;
//    }

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private <T> T bindPropertiesToTarget(Class<T> clazz, String prefix, String location) {
		try {
			Constructor<T> constructor = clazz.getConstructor();
			T newInstance = constructor.newInstance();

			PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<>(
					newInstance);

			PropertySourcesLoader loader = new PropertySourcesLoader();
			loader.load(this.resourceLoader.getResource(location));

			factory.setPropertySources(loader.getPropertySources());
			factory.setConversionService(new DefaultConversionService());
			if (!Strings.isNullOrEmpty(prefix)) {
				factory.setTargetName(prefix);
			}
			factory.bindPropertiesToTarget();
			return newInstance;

		} catch (Exception ex) {
			String targetClass = ClassUtils.getShortName(clazz);
			log.info("CasConfig.bindPropertiesToTarget cause exception ex: {}", ex);
			throw new BeanCreationException(clazz.getSimpleName(), "Could not bind properties to "
					+ targetClass + " (" + clazz.getSimpleName() + ")", ex);
		}
	}

}
