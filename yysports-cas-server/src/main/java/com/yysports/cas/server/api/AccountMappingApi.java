/**
 * 
 */
package com.yysports.cas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yysports.cas.server.dto.CasAccountInsertData;
import com.yysports.cas.server.dto.SystemAccountData;
import com.yysports.cas.server.service.CasAccountMappingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author RAY
 *
 */
@Api(value = "域帳號與應用系統帳號對應API", description = "域帳號與應用系統帳號對應API AccountMappingApi")
@Slf4j
@RestController
@RequestMapping(value = "/api/cas-account-mapping", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountMappingApi {

	@Autowired
	CasAccountMappingService service;

	/**
	 * 2019.08.07 使用域帳號和應用系統名稱查詢應用系統帳號對應
	 * 
	 * @param domainAccount    域帳號
	 * @param mappingSystemKey 應用系統名稱
	 * @return
	 */
	@ApiOperation(value = "使用域帳號和應用系統名稱查詢應用系統帳號對應")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "domainAccount", value = "域帳號", required = true,
					paramType = "query"),
			@ApiImplicitParam(name = "mappingSystem", value = "應用系統名稱，MIDDLE:中台，MEMBER:會員中心，EC:電商",
					required = true, paramType = "query", example = "MIDDLE or MEMBER or EC",
					allowableValues = "MIDDLE,MEMBER,EC") })
	@GetMapping(value = "/system-account")
	public ResponseEntity<SystemAccountData> getSystemAccount(
			@RequestParam(required = true) String domainAccount,
			@RequestParam(required = true) String mappingSystem) {
		log.info("getSystemAccount domainAccount: {}, mappingSystem: {}", domainAccount,
				mappingSystem);
		SystemAccountData result = service.getMappingData(domainAccount, mappingSystem);
		
		if (result == null) {
			return new ResponseEntity<SystemAccountData>(result, HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "新增域帳號和應用系統帳號對應")
	@ApiImplicitParam(name = "clientData", value = "帳號對應資訊", dataType = "CasAccountInsertData",
			paramType = "body")
	@PostMapping(value = "/system-account")
	public ResponseEntity<String> createAccountMapping(
			@RequestBody CasAccountInsertData clientData) {

		try {
			service.creatMappingAccount(clientData);
		} catch (DuplicateKeyException exp) {
			log.error("createAccountMapping cause DuplicateKeyException: ", exp);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("数据已存在，不可新增重复数据");
		} catch (Exception exp) {
			log.error("createAccountMapping cause exception: ", exp);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("资料新增失败");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("SUCCESS");
	}
	
}
