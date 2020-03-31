/**
 * 
 */
package com.yysports.cas.server.api;

import java.math.BigInteger;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yysports.cas.comm.dto.CasAccountData;
import com.yysports.cas.comm.dto.SystemAccountData;
import com.yysports.cas.comm.dto.SystemAccountQuery;
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
@RequestMapping(value = "/api/cas-account-mapping",
		produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
			@ApiImplicitParam(name = "mappingSystem", value = "應用系統名稱，MIDDLE，MEMBER，EC....",
					required = true, paramType = "query"),
			@ApiImplicitParam(name = "envName", value = "運行環境名稱，DEV，PRD，PREUB....",
					required = true, paramType = "query") })
	@GetMapping(value = "/system-account")
	public ResponseEntity<SystemAccountData> getSystemAccount(
			@RequestParam(required = true) String domainAccount,
			@RequestParam(required = true) String mappingSystem,
			@RequestParam(required = true) String envName) {

		SystemAccountQuery data = new SystemAccountQuery();
		data.setDomainAccount(domainAccount);
		data.setMappingSystem(mappingSystem);
		data.setEnvironmentName(envName);
		return this.getSystemAccount(data);
	}

	/**
	 * 2019.08.07 使用域帳號和應用系統名稱查詢應用系統帳號對應
	 * 
	 * @param domainAccount    域帳號
	 * @param mappingSystemKey 應用系統名稱
	 * @return
	 */
	@ApiOperation(value = "使用所有對應摻數查詢應用系統帳號對應")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "domainAccount", value = "域帳號", required = true,
					paramType = "query"),
			@ApiImplicitParam(name = "mappingSystem", value = "應用系統名稱，MIDDLE，MEMBER，EC....",
					required = true, paramType = "query"),
			@ApiImplicitParam(name = "envName", value = "運行環境名稱，DEV，PRD，PREUB....",
					required = true, paramType = "query"),
			@ApiImplicitParam(name = "accountName", value = "應用系統帳號名稱", required = false,
					paramType = "query"),
			@ApiImplicitParam(name = "accountId", value = "應用系統帳號ID", required = false,
					paramType = "query") })
	@GetMapping(value = "/system-account-params")
	public ResponseEntity<SystemAccountData> getSystemAccount(
			@RequestParam(required = true) String domainAccount,
			@RequestParam(required = true) String mappingSystem,
			@RequestParam(required = true) String envName,
			@RequestParam(required = false) String accountName,
			@RequestParam(required = false) BigInteger accountId) {

		SystemAccountQuery data = new SystemAccountQuery();
		data.setDomainAccount(domainAccount);
		data.setMappingSystem(mappingSystem);
		data.setAccountId(accountId);
		data.setAccountName(accountName);
		data.setEnvironmentName(envName);
		return this.getSystemAccount(data);
	}

	/**
	 * 使用物件查詢應用系統帳號對應
	 * 
	 * @param data
	 * @return
	 */
	private ResponseEntity<SystemAccountData> getSystemAccount(SystemAccountQuery data) {

		log.info("AccountMappingApi.getSystemAccount params: {}", data);
		SystemAccountData result = service.getMappingData(data);
		if (result == null) {
			return new ResponseEntity<SystemAccountData>(result, HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "新增域帳號和應用系統帳號對應")
	@PostMapping(value = "/system-account")
	public ResponseEntity<String> createAccountMapping(@RequestBody CasAccountData clientData) {

		try {
			service.creatMappingAccount(clientData);

			log.info("AccountMappingApi.createAccountMapping insert success param: {}",
					clientData);
			return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
		} catch (DuplicateKeyException exp) {
			log.error("createAccountMapping cause DuplicateKeyException: ", exp);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("数据已存在，不可新增重复数据");
		} catch (Exception exp) {
			log.error("createAccountMapping cause exception: ", exp);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("资料新增失败");
		}
	}

	@ApiOperation(value = "修改域帳號和應用系統帳號對應")
	@PutMapping(value = "/system-account/{id}")
	public ResponseEntity<String> updateAccountMapping(@PathVariable("id") Long id,
			@RequestBody CasAccountData clientData) {
		if (Objects.isNull(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID字段不能为空值");
		}
		clientData.setId(id);
		try {
			service.updateMappingAccount(clientData);

			log.info("AccountMappingApi.updateAccountMapping success param: {}",
					clientData);
			return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
		} catch (Exception exp) {
			log.error("AccountMappingApi.updateAccountMapping cause exception: ", exp);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("资料修改失败");
		}
	}

	@ApiOperation(value = "刪除域帳號和應用系統帳號對應")
	@DeleteMapping(value = "/system-account")
	public ResponseEntity<String> deleteAccountMapping(@RequestBody CasAccountData clientData) {
		if (Objects.isNull(clientData.getId()) && (Objects.isNull(clientData.getEnvironmentName())
				&& Objects.isNull(clientData.getDomainAccount())
				&& Objects.isNull(clientData.getMappingSystem())
				&& Objects.isNull(clientData.getMappingAccountName()))) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("必要字段为空值");
		}
		try {
			service.deleteMappingAccount(clientData);
			log.info("AccountMappingApi.deleteAccountMapping insert success param: {}",
					clientData);
			return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
		} catch (Exception exp) {
			log.error("AccountMappingApi.deleteAccountMapping cause exception: ", exp);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("资料删除失败");
		}
	}
}
