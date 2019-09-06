/**
 * 
 */
package com.yysports.cas.server.service;

import com.yysports.cas.server.dto.CasAccountInsertData;
import com.yysports.cas.server.dto.SystemAccountData;

/**
 * @author RAY
 *
 */
public interface CasAccountMappingService {

	/**
	 * 取得帳號對應內容
	 * 
	 * @param domainAccount 域帳號
	 * @param mappingSystem 對應系統名稱
	 * @return
	 */
	public SystemAccountData getMappingData(String domainAccount, String mappingSystem);

	/**
	 * 新增帳號對應
	 * 
	 * @param clientData
	 */
	public void creatMappingAccount(CasAccountInsertData clientData);
}
