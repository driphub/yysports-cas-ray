/**
 * 
 */
package com.yysports.cas.lib.autocreate.boot2.property;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;

/**
 * @author dream
 *
 */
public class EnviroementInit implements EnvironmentPostProcessor {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment,
			SpringApplication application) {
		// TODO Auto-generated method stub
		String[] activeProfiles = environment.getActiveProfiles();

		YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
		
		for (String prifile : activeProfiles) {
			System.out.println("prifile: " + prifile);
			if ("prod".equalsIgnoreCase(prifile) || "production".equalsIgnoreCase(prifile)) {
				yaml.setResources(new ClassPathResource("cas-setting-prod.yml"));
			} else {
				yaml.setResources(new ClassPathResource("cas-setting-default.yml"));
			}
			yaml.afterPropertiesSet();
			environment.getPropertySources().addLast(new PropertiesPropertySource("cas-setting", yaml.getObject()));
		}
	}

}
