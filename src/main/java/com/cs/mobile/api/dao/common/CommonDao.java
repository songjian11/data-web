package com.cs.mobile.api.dao.common;

import java.util.List;

import com.cs.mobile.api.model.common.CsmbOrg;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.model.common.CalendarMapping;
import com.cs.mobile.api.model.common.ComStore;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.partner.progress.IncomeConfig;

@Repository
public class CommonDao extends AbstractDao {

	private static final RowMapper<Organization> ORG_RM = new BeanPropertyRowMapper<>(Organization.class);
	private static final RowMapper<ComStore> COMSTORE_RM = new BeanPropertyRowMapper<>(ComStore.class);
	private static final RowMapper<IncomeConfig> INCOME_CONFIG_RM = new BeanPropertyRowMapper<>(IncomeConfig.class);
	private static final RowMapper<CalendarMapping> CALENDAR_MAPPING_RM = new BeanPropertyRowMapper<>(
			CalendarMapping.class);
	private static final RowMapper<CsmbOrg> CSMB_STORE_RM = new BeanPropertyRowMapper<>(CsmbOrg.class);
	// 获取所有大类后台收入率及DC收入率配置
	private static final String GET_ALL_INCOME_CNF = "select S.STORE_ID,I.DEPT_ID,I.IN_VALUES,I.DC_VALUES from CSMB_INCOME_VALUES I "
			+ "RIGHT JOIN (select STORE_ID,PROVINCE_ID from CSMB_STORE where store_id in(select store_id from CSMB_STORE_GROUP)) S "
			+ "ON I.PROVINCE_ID=S.PROVINCE_ID WHERE YM=? ";
	// 根据门店获取后台收入率及DC收入率配置
	private static final String GET_ALL_INCOME_CNF_BY_STOREID = "select S.STORE_ID,I.DEPT_ID,I.IN_VALUES,I.DC_VALUES from CSMB_INCOME_VALUES I "
			+ "RIGHT JOIN (select STORE_ID,PROVINCE_ID from CSMB_STORE where store_id in(select store_id from CSMB_STORE_GROUP)) S "
			+ "ON I.PROVINCE_ID=S.PROVINCE_ID WHERE YM=? AND S.STORE_ID=? ";
	// 根据区域获取后台收入率及DC收入率配置
	private static final String GET_ALL_INCOME_CNF_BY_AREAID = "select S.STORE_ID,I.DEPT_ID,I.IN_VALUES,I.DC_VALUES from CSMB_INCOME_VALUES I "
			+ "RIGHT JOIN (select STORE_ID,PROVINCE_ID from CSMB_STORE where area_id=? and store_id in(select store_id from CSMB_STORE_GROUP)) S "
			+ "ON I.PROVINCE_ID=S.PROVINCE_ID WHERE YM=? ";
	// 根据省份获取后台收入率及DC收入率配置
	private static final String GET_ALL_INCOME_CNF_BY_PROVINCEID = "select S.STORE_ID,I.DEPT_ID,I.IN_VALUES,I.DC_VALUES from CSMB_INCOME_VALUES I "
			+ "RIGHT JOIN (select STORE_ID,PROVINCE_ID from CSMB_STORE where province_id=? and store_id in(select store_id from CSMB_STORE_GROUP)) S "
			+ "ON I.PROVINCE_ID=S.PROVINCE_ID WHERE YM=? ";
	// 根据区域ID获取所有门店
	private static final String GET_STORE_BY_AREA = "select store_id as org_id,store_name as org_name from CSMB_STORE where area_id=? and store_id in(select store_id from CSMB_COMPANY)";
	// 根据省份获取所有区域
	private static final String GET_AREA_BY_PROVINCE = "select DISTINCT(area_id) as org_id,area_name as org_name from CSMB_STORE where province_id=? and store_id in(select store_id from CSMB_COMPANY)";
	// 获取所有省份
	private static final String GET_ALL_PROVINCE = "select DISTINCT(PROVINCE_ID) as org_id,PROVINCE_NAME as org_name from CSMB_STORE where store_id in(select store_id from CSMB_COMPANY)";
	// 获取门店可比标识
	private static final String GET_STORE_COMPARE = "select NVL(IS_COMPARE,0) from csmb_store where store_id=? ";

	private static final String GET_ALL_CALENDAR_MAPPING = "select to_char(a_day,'yyyy-mm-dd') as a_day,to_char(b_day,'yyyy-mm-dd') as b_day from  cmx.cmx_rpt_calendar_map";

	private static final String CSMB_STORE_PREFIX = "select province_id   as provinceId, " +
			"       province_name as provinceName, " +
			"       area_id       as areaId, " +
			"       area_name     as areaName, " +
			"       store_id      as storeId, " +
			"       store_name    as storeName " +
			"  from csmb_store ";

	public List<CsmbOrg> queryAllCsmbOrg(){
		return super.queryForList(CSMB_STORE_PREFIX,CSMB_STORE_RM,null);
	}
	/**
	 * 根据门店ID获取下面所有小店列表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public List<ComStore> getComStoreList(String storeId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append(" a.store_id storeId,");
		sql.append(" a.com_id comId,");
		sql.append(" a.com_name comName ");
		sql.append(" from CSMB_COMPANY a ");
		sql.append(" where a.store_id = ? ");
		sql.append(" group by a.store_id,a.com_id,a.com_name ");
		return super.queryForList(sql.toString(), COMSTORE_RM, storeId);
	}

	/**
	 * 后台收入率及DC收入率配置
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<IncomeConfig> getAllIncomeCnf(String ym) throws Exception {
		return super.queryForList(GET_ALL_INCOME_CNF, INCOME_CONFIG_RM, ym);
	}

	/**
	 * 根据门店获取后台收入率及DC收入率配置
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<IncomeConfig> getAllIncomeCnfByStoreId(String ym, String storeId) throws Exception {
		return super.queryForList(GET_ALL_INCOME_CNF_BY_STOREID, INCOME_CONFIG_RM, ym, storeId);
	}

	/**
	 * 根据区域获取后台收入率及DC收入率配置
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<IncomeConfig> getAllIncomeCnfByAreaId(String ym, String areaId) throws Exception {
		return super.queryForList(GET_ALL_INCOME_CNF_BY_AREAID, INCOME_CONFIG_RM, areaId, ym);
	}

	/**
	 * 根据省份获取后台收入率及DC收入率配置
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<IncomeConfig> getAllIncomeCnfByProvinceId(String ym, String provinceId) throws Exception {
		return super.queryForList(GET_ALL_INCOME_CNF_BY_PROVINCEID, INCOME_CONFIG_RM, provinceId, ym);
	}

	/**
	 * 根据区域ID获取所有门店
	 * 
	 * @author wells
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getStoreByArea(String areaId) throws Exception {
		return super.queryForList(GET_STORE_BY_AREA, ORG_RM, areaId);
	}

	/**
	 * 根据省份获取所有区域
	 * 
	 * @author wells
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAreaByProvince(String provinceId) throws Exception {
		return super.queryForList(GET_AREA_BY_PROVINCE, ORG_RM, provinceId);
	}

	/**
	 * 获取所有省份
	 * 
	 * @author wells
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAllProvince() throws Exception {
		return super.queryForList(GET_ALL_PROVINCE, ORG_RM);
	}

	/**
	 * 根据门店ID获取门店可比标识
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月7日
	 */
	public Integer getStoreCompare(String storeId) throws Exception {
		Object[] args = { storeId };
		return jdbcTemplate.queryForObject(GET_STORE_COMPARE, args, Integer.class);
	}

	/**
	 * 获取所有日期映射关系
	 *
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月19日
	 *
	 */
	public List<CalendarMapping> getAllCalendarMapping() throws Exception {
		return super.queryForList(GET_ALL_CALENDAR_MAPPING, CALENDAR_MAPPING_RM);
	}
}
