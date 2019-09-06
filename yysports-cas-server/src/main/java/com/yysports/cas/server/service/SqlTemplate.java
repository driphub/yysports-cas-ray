package com.yysports.cas.server.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Strings;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Transactional
public class SqlTemplate {

	@Autowired
	JdbcTemplate template;

	// java8 time DateTimeFormatter
	private static final DateTimeFormatter DATETIME_FORMATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	// 格式化LOG字串
	private final String fmtLog = "SQL-Statement: %s;";

	/**
	 * 2019.05.24 紀錄LOG
	 * 
	 * @param sql       sql
	 * @param condition 查詢郩件
	 * @param enableLog 紀錄LOG
	 */
	public void logSqlStatement(final String sql, Object[] condition, Boolean enableLog) {
		if (enableLog != true) {
			return;
		}
		if (condition == null || condition.length == 0) {
			log.info(String.format(fmtLog, sql));
		} else {
			String rtnStr = sql;
			for (Object obj : condition) {
				Object val = obj;
				if (val instanceof Date) {
					// Date to yyyy-MM-dd.....字串
					val = ((Date) val).toInstant().atZone(ZoneId.systemDefault()).format(DATETIME_FORMATE);
				}
				String data = (val == null) ? "null" : "\'" + java.util.Objects.toString(val) + "\'";
				rtnStr = rtnStr.replaceFirst("\\?", data);
			}
			log.info(String.format(fmtLog, rtnStr));
		}
	}

	/**
	 * 2019.05.24 查詢多筆多欄位
	 * 
	 * @param sql       SQL字串
	 * @param condition 查詢條件
	 * @return empty-list or 多筆資料
	 */
	public List<Map<String, Object>> getListMapData(final String sql) {
		return template.queryForList(sql);
	}

	/**
	 * 2019.05.24 查詢多筆多欄位
	 * 
	 * @param sql       SQL字串
	 * @param enableLog 紀錄查詢SQL
	 * @return empty-list or 多筆資料
	 */
	public List<Map<String, Object>> getListMapData(final String sql, boolean enableLog) {
		logSqlStatement(sql, null, enableLog);
		return getListMapData(sql);
	}

	/**
	 * 2019.05.24 查詢多筆多欄位
	 * 
	 * @param sql       SQL字串
	 * @param condition 查詢條件
	 * @return empty-list or 多筆資料
	 */
	public List<Map<String, Object>> getListMapData(final String sql, Object[] condition) {
		return template.queryForList(sql, condition);
	}

	/**
	 * 2019.05.24 查詢多筆多欄位
	 * 
	 * @param sql       SQL字串
	 * @param condition 查詢條件
	 * @param enableLog 紀錄查詢SQL
	 * @return empty-list or 多筆資料
	 */
	public List<Map<String, Object>> getListMapData(final String sql, Object[] condition, boolean enableLog) {
		logSqlStatement(sql, null, enableLog);
		return getListMapData(sql, condition);
	}

	/**
	 * 單欄位多筆資料
	 * 
	 * @param sql       SQL語法
	 * @param condition 查詢條件
	 * @return null or 多筆資料
	 */
	public List<String> getListStrData(final String sql, Object[] condition) {
		return template.queryForList(sql, condition, String.class);
	}

	/**
	 * 單欄位多筆資料
	 * 
	 * @param sql       SQL語法
	 * @param condition 查詢條件
	 * @return null or 多筆資料
	 */
	public List<String> getListStrData(final String sql, Object[] condition, Boolean enableLog) {
		logSqlStatement(sql, condition, enableLog);
		return getListStrData(sql, condition);
	}

	/**
	 * 取的單筆單欄位資料，若回傳多筆會有EXCEPTION
	 * 
	 * @param sql       SQL語法
	 * @param condition 查詢條件
	 * @return String
	 */
	public String getSingleStr(final String sql, Object[] condition) {
		List<String> listStr = getListStrData(sql, condition);
		return (listStr == null || listStr.isEmpty()) ? "" : listStr.get(0).trim();
	}

	/**
	 * 2019.05.24 取單筆單欄位資料
	 * 
	 * @param sql       SQL語法
	 * @param condition 查詢條件
	 * @param enableLog 紀錄LOG
	 * @return String
	 */
	public String getSingleStr(final String sql, Object[] condition, Boolean enableLog) {
		logSqlStatement(sql, condition, enableLog);
		return getSingleStr(sql, condition);
	}

	/**
	 * 取的單筆單欄位資料
	 * 
	 * @param sql SQL語法
	 * @return String
	 */
	public String getSingleStr(final String sql) {
		List<String> listStr = template.queryForList(sql, String.class);
		return (listStr == null || listStr.isEmpty()) ? "" : listStr.get(0).trim();
	}

	/**
	 * SELECT COUNT
	 * 
	 * @param sql       SQL語法
	 * @param condition 條件
	 * @return long or 0
	 */
	public long getCount(final String sql, Object[] condition) {
		String str = getSingleStr(sql, condition);
		return (Strings.isNullOrEmpty(str)) ? 0 : Long.valueOf(str);
	}

	/**
	 * SELECT COUNT
	 * 
	 * @param sql       SQL語法
	 * @param condition 條件
	 * @param enableLog 紀錄查詢語句
	 * @return long or 0
	 */
	public long getCount(final String sql, Object[] condition, Boolean enableLog) {
		logSqlStatement(sql, condition, enableLog);
		return getCount(sql, condition);
	}

	/**
	 * 2019.05.24 自定義查詢
	 * 
	 * @param sql       SQL語法
	 * @param condition 查詢條件
	 * @param rowMapper
	 * @return List<T>
	 */
	public <T> List<T> query(final String sql, Object[] condition, RowMapper<T> rowMapper) {
		return template.query(sql, condition, rowMapper);
	}

	/**
	 * 2019.05.24 自定義查詢
	 * 
	 * @param <T>
	 * @param sql       SQL語法
	 * @param condition 查詢條件
	 * @param rowMapper
	 * @param enableLog 紀錄查詢語句
	 * @return List<T>
	 */
	public <T> List<T> query(final String sql, Object[] condition, RowMapper<T> rowMapper, Boolean enableLog) {
		logSqlStatement(sql, condition, enableLog);
		return query(sql, condition, rowMapper);
	}

	/**
	 * 2019.05.24 自定義查詢
	 * 
	 * @param <T>
	 * @param sql         SQL語法
	 * @param condition   查詢條件
	 * @param elementType Class
	 * @return List<T>
	 */
	public <T> List<T> queryList(final String sql, Object[] condition, Class<T> elementType) {
		return template.queryForList(sql, elementType, condition);
	}

	/**
	 * 
	 * @param <T>
	 * @param sql         SQL語法
	 * @param condition   查詢條件
	 * @param elementType Class
	 * @param enableLog   紀錄LOG
	 * @return List<T>
	 */
	public <T> List<T> queryList(final String sql, Object[] condition, Class<T> elementType, Boolean enableLog) {
		logSqlStatement(sql, condition, enableLog);
		return template.queryForList(sql, elementType, condition);
	}

	/**
	 * 新增資料
	 * @param sql
	 * @return
	 */
	public int insertBySql(String sql) {
		return template.update(sql);
	}
	/**
	 * SQL UPDATE
	 * @param sql
	 * @param condition
	 * @return
	 */
	public int updateBySql(final String sql, Object[] condition) {
		// logSqlStatement(sql, condition, true);
		return template.update(sql, condition);
	}
}
