/**
 * 
 */
package com.yysports.cas.login.config.property;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author RAY
 *
 */
@Slf4j
public class YamlPropertySourceFactory1 implements PropertySourceFactory {


	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource)
			throws IOException {

		String sourceName = name != null ? name : resource.getResource().getFilename();
		PropertySource<?> propertySource = new YamlPropertySourceLoader().load(sourceName,
				resource.getResource(), "prod");
		log.info("name: " + sourceName);
		log.info("resource: " + resource);
		//log.info("envProfile: " + envProfile);
		log.info("propertySource: " + propertySource);
		return propertySource;
	}

}
