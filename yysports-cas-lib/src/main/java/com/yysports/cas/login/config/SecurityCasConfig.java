/**
 * 
 */
package com.yysports.cas.login.config;

import java.lang.reflect.Constructor;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import com.google.common.base.Strings;
import com.yysports.cas.login.config.property.CasProperties;
import com.yysports.cas.login.utils.CasContextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author RAY
 *
 */
@Configuration
@Configurable
@Slf4j
public class SecurityCasConfig implements ResourceLoaderAware {
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Value("${spring.profiles.active:default}")
	private String envProfile;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Bean("casContextUtils")
	@Scope("prototype")
	public CasContextUtils casContextUtils() {
		return new CasContextUtils();
	}

	@Bean("casProperties")
	public CasProperties casProperties() {
		String envName = "prod".equalsIgnoreCase(envProfile)
				|| "production".equalsIgnoreCase("envProfile") ? "prod" : "default";
		return bindPropertiesToTarget(CasProperties.class, "system.cas",
				"classpath:cas-setting-" + envName + ".yml");
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
