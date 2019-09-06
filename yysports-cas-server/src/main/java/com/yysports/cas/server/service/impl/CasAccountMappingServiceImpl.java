/**
 * 
 */
package com.yysports.cas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.yysports.cas.server.dto.*;
import com.yysports.cas.server.service.CasAccountMappingService;
import com.yysports.cas.server.service.SqlTemplate;

import java.util.*;


/**
 * @author RAY
 *
 */
@Service
public class CasAccountMappingServiceImpl implements CasAccountMappingService {

	@Autowired
	SqlTemplate template;

	/**
	 * 取得帳號對應內容
	 * 
	 * @param domainAccount 域帳號
	 * @param mappingSystem 對應系統名稱
	 * @return
	 */
	@Override
	public SystemAccountData getMappingData(String domainAccount, String mappingSystem) {

		String sql = "SELECT domain_account domainAccount, mapping_system mappingSystem,"
				+ " mapping_account_id accountId, mapping_account_name accountName, "
				+ "isActive FROM cas_account_mapping "
				+ "WHERE domain_account = ? AND mapping_system = ?";

		List<SystemAccountData> result = template.query(sql,
				new Object[] { domainAccount, mappingSystem },
				new BeanPropertyRowMapper<SystemAccountData>(SystemAccountData.class));
		if (CollectionUtils.isEmpty(result)) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * 新增帳號對應
	 * 
	 * @param clientData
	 */
	@Override
	public void creatMappingAccount(CasAccountInsertData clientData) {

		/**
		 * INSERT INTO cas_account_mapping (domain_account, mapping_system,
		 * mapping_account_id, mapping_account_name) VALUES (?,?,?,?);
		 */
		StringBuffer sql = new StringBuffer("INSERT INTO cas_account_mapping (domain_account, ");
		sql.append("mapping_system, mapping_account_id, mapping_account_name)");
		sql.append(" VALUES ('").append(clientData.getDomainAccount()).append("','");
		sql.append(clientData.getMappingSystem().name()).append("',");
		sql.append(clientData.getMappingAccountId()).append(",'");
		sql.append(clientData.getMappingAccountName()).append("')");

		template.insertBySql(sql.toString());
	}

}
