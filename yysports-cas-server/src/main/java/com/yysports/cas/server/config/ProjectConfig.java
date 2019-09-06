/**
 * 
 */
package com.yysports.cas.server.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.yysports.cas.server.dto.SystemAccountData;

/**
 * @author RAY
 *
 */
@Configuration
public class ProjectConfig extends WebMvcConfigurerAdapter {
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		Iterables.removeIf(converters, new Predicate<HttpMessageConverter<?>>() {
			@Override
			public boolean apply(HttpMessageConverter<?> input) {
				if (input instanceof StringHttpMessageConverter) {
					return true;
				}
				return false;
			}
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

	@Bean("redisTemplateAccount")
	public RedisTemplate<String, SystemAccountData> redisTemplateAccount(
			RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, SystemAccountData> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		Jackson2JsonRedisSerializer<SystemAccountData> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
				SystemAccountData.class);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		return template;
	}
}
