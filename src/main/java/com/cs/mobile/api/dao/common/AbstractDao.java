package com.cs.mobile.api.dao.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import com.cs.mobile.api.model.common.PageResult;

/**
 * 持久层抽象类
 * 
 * @author wells.wong
 * @date 2018年11月18日
 */
public abstract class AbstractDao {

	public enum Sort {

		DESC("DESC"), ASC("ASC");

		String name;

		Sort(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static final String DESC = "DESC";
	public static final String ASC = "ASC";

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	/**
	 * 
	 * 
	 * @param page
	 *            页码
	 * @param pageSize
	 *            每页大小
	 * @return
	 * 
	 * @author wells
	 */
	protected Integer getFirstIndex(Integer page, Integer pageSize) {
		Integer rvalue = 0;
		if (page > 0) {
			rvalue = (page - 1) * pageSize;
		}
		return rvalue;
	}

	/**
	 * 分页查询（默认按升序排序）
	 * 
	 * @param sql
	 * @param page
	 * @param pageSize
	 * @param orderByField
	 * @param args
	 * @return
	 * 
	 * @author wells
	 */
	protected <T> PageResult<T> queryByPage(String sql, Class<T> resultClass, int page, int pageSize,
			String orderByField, Object... args) {
		return this.queryByPage(sql, resultClass, page, pageSize, orderByField, Sort.ASC, args);
	}

	/**
	 * 分页查询（按指定方式排序）
	 * 
	 * @param sql
	 * @param page
	 * @param pageSize
	 * @param orderByField
	 * @param sortType
	 * @param args
	 * @return
	 * 
	 * @author wells
	 */
	@SuppressWarnings("deprecation")
	protected <T> PageResult<T> queryByPage(String sql, Class<T> resultClass, int page, int pageSize,
			String orderByField, Sort sortType, Object... args) {
		PageResult<T> result = new PageResult<T>();
		result.setPage(page);
		result.setPageSize(pageSize);
		StringBuilder countSql = new StringBuilder(" select count(1) from (");
		countSql.append(sql).append(" ) tmp_count ");
		int total = jdbcTemplate.queryForObject(countSql.toString(), args, Integer.class);
		result.setTotal(total);
		if (total == 0) {
			result.setDatas(new ArrayList<T>(0));
			return result;
		}
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("SELECT * FROM ( SELECT TMP_PAGE.*, ROWNUM ROW_ID FROM  ( ");
		pageSql.append(sql);
		pageSql.append(" ORDER BY ");
		pageSql.append(orderByField).append(" ").append(sortType.getName());
		pageSql.append(" ) TMP_PAGE ");
		pageSql.append(" ) WHERE ROW_ID <= ");
		pageSql.append(page * pageSize);
		pageSql.append(" AND ROW_ID > ");
		pageSql.append((page - 1) * pageSize);
		RowMapper<T> rowmapper = new BeanPropertyRowMapper<T>(resultClass);
		List<T> datas = jdbcTemplate.query(pageSql.toString(), rowmapper, args);
		result.setDatas(datas);
		return result;
	}

	protected <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... params) {
		List<T> results = null;
		try {
			results = jdbcTemplate.query(sql, new RowMapperResultSetExtractor<T>(rowMapper, 1), params);
		} catch (EmptyResultDataAccessException e) {

		}
		return requiredSingleResult(results);
	}

	public static <T> T requiredSingleResult(Collection<T> results) throws IncorrectResultSizeDataAccessException {
		int size = (results != null ? results.size() : 0);
		if (size == 0) {
			return null;
		}
		if (results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, size);
		}
		return results.iterator().next();
	}

	/**
	 * 查询数据列表
	 * 
	 * @param sql
	 * @param rowMapper
	 * @param params
	 * @return
	 *
	 * @author wells
	 */
	protected <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... params) {
		List<T> results = null;
		try {
			results = jdbcTemplate.query(sql, rowMapper, params);
		} catch (EmptyResultDataAccessException e) {
			results = new ArrayList<T>(0);
		}
		return results;
	}

	/**
	 * 不带引号的拼接
	 * @param list
	 * @return
	 */
	protected String listToStrNotHaveQuotationMark(List<String> list){
		String result = "";
		StringBuilder sb = new StringBuilder();
		if(null != list && list.size() > 0){
			for(String str : list){
				sb.append(str).append(",");
			}
			result = sb.toString();
		}
		if(result.endsWith(",")){
			result = result.substring(0,result.lastIndexOf(","));
		}
		return result;
	}

	/**
	 * 带引号的拼接
	 * @param list
	 * @return
	 */
	protected String listToStrHaveQuotationMark(List<String> list){
		String result = "";
		StringBuilder sb = new StringBuilder();
		if(null != list && list.size() > 0){
			for(String str : list){
				sb.append("'").append(str).append("',");
			}
			result = sb.toString();
		}
		if(result.endsWith(",")){
			result = result.substring(0,result.lastIndexOf(","));
		}
		return result;
	}
}
