package com.yysports.cas.server.service;

import com.yysports.cas.comm.dto.SystemAccountData;

public interface SystemAccountDataService {

	/**
	 * 用KEY去REDIS查資料
	 * 
	 * @param key
	 * @return
	 */
	public SystemAccountData getSystemAccountData(String key);

	/**
	 * 儲存REDIS資料
	 * 
	 * @param data
	 * @return
	 */
	public Boolean saveSystemAccountData(SystemAccountData data);

	/**
	 * 移除REDIS資料
	 * 
	 * @param key
	 */
	public void removeSystemAccountData(String key);
}
