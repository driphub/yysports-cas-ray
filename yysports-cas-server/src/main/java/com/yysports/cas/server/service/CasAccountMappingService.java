/**
 * 
 */
package com.yysports.cas.server.service;

import com.yysports.cas.comm.dto.CasAccountData;
import com.yysports.cas.comm.dto.SystemAccountData;
import com.yysports.cas.comm.dto.SystemAccountQuery;

/**
 * @author RAY
 *
 */
public interface CasAccountMappingService {

	/**
	 * 取得帳號對應內容
	 * 
	 * @param data 查詢物件
	 * @return
	 */
	public SystemAccountData getMappingData(SystemAccountQuery data);

	/**
	 * 新增帳號對應
	 * 
	 * @param clientData
	 */
	public void creatMappingAccount(CasAccountData clientData);

	/**
	 * 更新帳號對應
	 * @param clientData
	 */
	public void updateMappingAccount(CasAccountData clientData);

	/**
	 * 刪除帳號對應
	 * @param clientData
	 */
	public void deleteMappingAccount(CasAccountData clientData);
}
