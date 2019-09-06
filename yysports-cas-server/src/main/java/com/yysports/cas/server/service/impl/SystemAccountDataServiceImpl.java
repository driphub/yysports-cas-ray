/**
 * 
 */
package com.yysports.cas.server.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yysports.cas.server.dto.SystemAccountData;
import com.yysports.cas.server.service.SystemAccountDataService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author RAY
 *
 */
@Service
@Slf4j
public class SystemAccountDataServiceImpl implements SystemAccountDataService {

	@Autowired
	@Qualifier("redisTemplateAccount")
	RedisTemplate<String, SystemAccountData> template;
	
	@Value("${session.data.expired.time:1200}")
	private Long dataExpiredTime;

	@Override
	public SystemAccountData getSystemAccountData(String key) {
		return template.opsForValue().get(key);
	}

	@Override
	public Boolean saveSystemAccountData(SystemAccountData data) {
		try {
			template.opsForValue().set(data.getUid(), data, dataExpiredTime, TimeUnit.SECONDS);
			return true;
		} catch (Exception ex) {
			log.error(
					"SystemAccountDataServiceImpl.saveSystemAccountData param: {}, cause exception: {}",
					data, ex);
			return false;
		}
	}

	@Override
	public void removeSystemAccountData(String key) {
		template.delete(key);
	}

}
