package com.yysports.cas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yysports.cas.comm.dto.SystemAccountData;
import com.yysports.cas.server.service.SystemAccountDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(value = "cas server session handle API",
		description = "cas server session handle API SessionHandleApi")
@Slf4j
@RestController
@RequestMapping(value = "/api/cas-session", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SessionHandleApi {

	@Autowired
	AccountMappingApi accApi;

	@Autowired
	SystemAccountDataService service;

	@Value("${session.prefix}")
	private String sessionPrefix;

	/**
	 * 新增一筆資料到REDIS
	 * 
	 * @return
	 */
	@ApiOperation(value = "新增一筆資料到REDIS")
	@PostMapping("/session")
	public ResponseEntity<Boolean> addSession(@RequestBody SystemAccountData data) {

		data.setUid(getSessionKey(data.getUid()));
		log.info("SessionHandleApi.addSession params: {}", data);
		Boolean result = service.saveSystemAccountData(data);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "移除REDIS的SESSION資料")
	@ApiImplicitParam(name = "sessionId", value = "sessionId", required = true,
			paramType = "query")
	@DeleteMapping("/session")
	public ResponseEntity<Boolean> removeSession(
			@RequestParam(required = true) String sessionId) {
		final String sessionKey = getSessionKey(sessionId);
		log.info("SessionHandleApi.removeSession params: {}", sessionKey);
		service.removeSystemAccountData(sessionKey);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	/**
	 * 檢查REDIS庫裡面的SESSION
	 * 
	 * @param sessionId
	 * @return
	 */
	@ApiOperation(value = "查詢REDIS中的資料")
	@ApiImplicitParam(name = "sessionId", value = "sessionId", required = true,
			paramType = "query")
	@GetMapping("/session")
	public ResponseEntity<SystemAccountData> getSession(
			@RequestParam(required = true) String sessionId) {

		final String sessionKey = getSessionKey(sessionId);
		log.info("SessionHandleApi.getSession params: {}", sessionKey);
		SystemAccountData data = service.getSystemAccountData(sessionKey);
		if (data == null) {
			return ResponseEntity.status(204).body(null);
		}

		return ResponseEntity.ok(data);
	}

	/**
	 * 取得REDIS KEY
	 * 
	 * @param sessionId
	 * @return
	 */
	private String getSessionKey(final String sessionId) {
		return sessionPrefix + sessionId;
	}
}
