/**
 * 
 */
package com.yysports.cas.admin.web.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author RAY
 *
 */
@Configuration
public class ProjectConfig extends WebMvcConfigurerAdapter {
	

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		Iterables.removeIf(converters, input -> {
            if (input instanceof StringHttpMessageConverter) {
                return true;
            }
            return false;
		});

		// force utf-8 encode
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(
				Charsets.UTF_8);
		stringHttpMessageConverter
				.setSupportedMediaTypes(Lists.newArrayList(MediaType.TEXT_PLAIN, MediaType.ALL));
		converters.add(1, stringHttpMessageConverter);

	}

	@Bean
	@Primary
	public ObjectMapper nonNullObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return objectMapper;
	}

	@Bean
	public HttpPutFormContentFilter httpPutFormContentFilter() {
		return new HttpPutFormContentFilter();
	}

}
