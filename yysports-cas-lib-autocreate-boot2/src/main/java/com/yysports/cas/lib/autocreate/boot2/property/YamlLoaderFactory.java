/**
 * 
 */
package com.yysports.cas.lib.autocreate.boot2.property;

import java.io.IOException;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import java.util.*;

/**
 * @author dream
 *
 */
public class YamlLoaderFactory extends DefaultPropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource)
			throws IOException {
		if (resource == null) {
			return super.createPropertySource(name, resource);
		}
		List<PropertySource<?>> sources = new YamlPropertySourceLoader()
				.load(resource.getResource().getFilename(), resource.getResource());
		if (sources.size() == 0) {
			return super.createPropertySource(name, resource);
		}
		return sources.get(0);
	}
}
