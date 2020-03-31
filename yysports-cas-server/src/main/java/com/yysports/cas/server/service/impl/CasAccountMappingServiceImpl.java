/**
 * 
 */
package com.yysports.cas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.yysports.cas.comm.dto.CasAccountData;
import com.yysports.cas.comm.dto.SystemAccountData;
import com.yysports.cas.comm.dto.SystemAccountQuery;
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
	 * @param data 查詢物件
	 * @return
	 */
	@Override
	public SystemAccountData getMappingData(SystemAccountQuery data) {

		StringBuffer sql = new StringBuffer("SELECT domain_account domainAccount, ");
		sql.append("mapping_system mappingSystem, mapping_account_id accountId,");
		sql.append(" mapping_account_name accountName, environment_name environmentName,");
		sql.append(" isActive FROM cas_account_mapping ");
		sql.append("WHERE domain_account = ? AND mapping_system = ? ");

		List<Object> cond = Lists.newArrayList(data.getDomainAccount(), data.getMappingSystem());
		if (!Strings.isNullOrEmpty(data.getAccountName())) {
			sql.append(" AND mapping_account_name = ? ");
			cond.add(data.getAccountName());
		}
		if (data.getAccountId() != null) {
			sql.append(" AND mapping_account_id = ? ");
			cond.add(data.getAccountId());
		}

		if (data.getEnvironmentName() != null) {
			sql.append(" AND environment_name = ? ");
			cond.add(data.getEnvironmentName());
		}

		List<SystemAccountData> result = template.query(sql.toString(), cond.toArray(),
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
	@Transactional
	public void creatMappingAccount(CasAccountData clientData) {

		/**
		 * INSERT INTO cas_account_mapping (domain_account, mapping_system,
		 * mapping_account_id, mapping_account_name) VALUES (?,?,?,?);
		 */
		StringBuffer sql = new StringBuffer("INSERT INTO cas_account_mapping (domain_account, ");
		sql.append("mapping_system, mapping_account_id, mapping_account_name, environment_name)");
		sql.append(" VALUES ('").append(clientData.getDomainAccount()).append("','");
		sql.append(clientData.getMappingSystem()).append("',");
		sql.append(clientData.getMappingAccountId()).append(",'");
		sql.append(clientData.getMappingAccountName()).append("','");
		sql.append(clientData.getEnvironmentName()).append("')");

		template.insertBySql(sql.toString());
	}

	/**
	 * 更新帳號對應
	 * 
	 * @param clientData
	 */
	@Override
	public void updateMappingAccount(CasAccountData clientData) {
		// TODO Auto-generated method stub

		StringBuffer sql = new StringBuffer("UPDATE cas_account_mapping SET ");
		if (!Strings.isNullOrEmpty(clientData.getDomainAccount())) {
			sql.append("domain_account='").append(clientData.getDomainAccount()).append("',");
		}
		if (!Strings.isNullOrEmpty(clientData.getMappingSystem())) {
			sql.append("mapping_system='").append(clientData.getMappingSystem()).append("',");
		}
		if (!Objects.isNull(clientData.getMappingAccountId())) {
			sql.append("mapping_account_id='").append(clientData.getMappingAccountId())
					.append("',");
		}
		if (!Strings.isNullOrEmpty(clientData.getMappingAccountName())) {
			sql.append("mapping_account_name='").append(clientData.getMappingAccountName())
					.append("',");
		}
		if (!Objects.isNull(clientData.getIsActive())) {
			sql.append("isActive=").append(clientData.getIsActive()).append(",");
		}
		if (!Strings.isNullOrEmpty(clientData.getEnvironmentName())) {
			sql.append("environment_name='").append(clientData.getEnvironmentName()).append("',");
		}
		sql.append(" UPDATE_DATE=NOW() WHERE ID=?");

		template.updateBySql(sql.toString(), new Object[] { clientData.getId() });
	}

	/**
	 * 刪除帳號對應
	 * 
	 * @param clientData
	 */
	@Override
	public void deleteMappingAccount(CasAccountData clientData) {
		// TODO Auto-generated method stub

		StringBuffer sql = new StringBuffer("DELETE FROM cas_account_mapping WHERE ");
		if (!Objects.isNull(clientData.getId())) {
			sql.append(" ID=?");
			template.updateBySql(sql.toString(), new Object[] { clientData.getId() });
		} else {
			sql.append(" domain_account=? AND mapping_system=? AND ");
			sql.append("mapping_account_name=? AND environment_name=? ");

			template.updateBySql(sql.toString(),
					new Object[] { clientData.getDomainAccount(), clientData.getMappingSystem(),
							clientData.getMappingAccountName(),
							clientData.getEnvironmentName() });

		}
	}

}
