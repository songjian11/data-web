package com.cs.mobile.api.dao.partner.runindex;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.runindex.RunBaseReport;
import com.cs.mobile.api.model.partner.runindex.RunIndexResult;
import com.cs.mobile.api.model.partner.runindex.WorkLevelConfig;

/**
 * 经营指数报表DAO
 * 
 * @author wells.wong
 * @date 2018年11月24日
 */
@Repository
public class RunIndexDao extends AbstractDao {
	private static final RowMapper<RunBaseReport> RUN_BASEREPORT_RM = new BeanPropertyRowMapper<>(RunBaseReport.class);
	private static final RowMapper<RunIndexResult> RUN_REPORTRESULT_RM = new BeanPropertyRowMapper<>(
			RunIndexResult.class);
	private static final RowMapper<WorkLevelConfig> WORKLEVEL_CONFIG_RM = new BeanPropertyRowMapper<>(
			WorkLevelConfig.class);
	// 大店规模（设备电费、面积、人数）
	private static final String GET_STORE_SCALE = "select * from "
			+ "(select NVL(sum(area),0) as area_total from CSMB_AREA where  STORE_ID=?), "
			+ "(select NVL(sum(e_charge),0) as e_charge_total from CSMB_ELECTRIC_CHARGE where STORE_ID=?), "
			+ "(select count(1) as person_total from CSMB_PERSON where store_id=?)";

	// 上个月大店销售金额
	private static final String GET_LASTMONTH_STORE_SALE = "select NVL(SUM(SALE_VALUE),0) from CSMB_DEPT_SALES_HISTORY "
			+ "where to_char(SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') and STORE_ID=?";

	// 上个月大店目标劳效额
	private static final String GET_LASTMONTH_STORE_WORKRATIOTARGET = "select NVL(SUM(sub_values),0) from CSMB_GOAL "
			+ "where GOAL_YM=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') and subject='目标劳效' and STORE_ID=?";

	// 上个月大店下所有人员的总时长
	private static final String GET_LASTMONTH_STOR_EWORKDAYS = "select nvl(sum(BUSINESSDATA),0) from CSMB_WORK_ITEM "
			+ "where to_char(to_date(concat(REPLACE(year,'年',''), "
			+ "case when length(REPLACE(period,'月',''))=1 then concat('0',REPLACE(period,'月','')) "
			+ "else REPLACE(period,'月','') END),'yyyy-mm'),'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') "
			+ "and dept_code=?";

	// 选择区间月的大店销售额
	private static final String GET_SCOPE_STORE_SALE = "select to_char(SALE_DATE,'yyyy-mm') as time, NVL(SUM(SALE_VALUE),0) as value "
			+ "from CSMB_DEPT_SALES_HISTORY where to_char(SALE_DATE,'yyyy-mm')>=? "
			+ "and to_char(SALE_DATE,'yyyy-mm')<=? and STORE_ID=? group by to_char(SALE_DATE,'yyyy-mm')";

	// 选择区间月的大店人员总时长
	private static final String GET_SCOPE_STORE_WORKDAYS = "select to_char(to_date(concat(REPLACE(year,'年',''), "
			+ "case when length(REPLACE(period,'月',''))=1 then concat('0',REPLACE(period,'月','')) "
			+ "else REPLACE(period,'月','') END),'yyyy-mm'),'yyyy-mm') as time,BUSINESSDATA as value from CSMB_WORK_ITEM "
			+ "where to_char(to_date(concat(REPLACE(year,'年',''), "
			+ "case when length(REPLACE(period,'月',''))=1 then concat('0',REPLACE(period,'月','')) "
			+ "else REPLACE(period,'月','') END),'yyyy-mm'),'yyyy-mm')>=? "
			+ "and to_char(to_date(concat(REPLACE(year,'年',''), "
			+ "case when length(REPLACE(period,'月',''))=1 then concat('0',REPLACE(period,'月','')) "
			+ "else REPLACE(period,'月','') END),'yyyy-mm'),'yyyy-mm')<=? and dept_code=?";

	// 标准档位
	private static final String GET_WORKLEVEL_CONFIG_LIST = "select * from CSMB_WORK_CONFIG where group_id=? "
			+ "and position_name='大店店长'";

	/**
	 * 大店规模（设备电费、面积、人数）查询
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public RunIndexResult getStoreScale(String storeId) throws Exception {
		return super.queryForObject(GET_STORE_SCALE, RUN_REPORTRESULT_RM, storeId, storeId, storeId);
	}

	/**
	 * 上个月大店销售金额
	 * 
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthStoreSaleTotal(String storeId) throws Exception {
		Object[] args = { storeId };
		return jdbcTemplate.queryForObject(GET_LASTMONTH_STORE_SALE, args, BigDecimal.class);
	}

	/**
	 * 上个月大店目标劳效额
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthStoreWorkRatioTarget(String storeId) throws Exception {
		Object[] args = { storeId };
		return jdbcTemplate.queryForObject(GET_LASTMONTH_STORE_WORKRATIOTARGET, args, BigDecimal.class);
	}

	/**
	 * 上个月大店下所有人员的总时长
	 */
	public BigDecimal getLastMonthStoreWorkDays(String storeId) throws Exception {
		Object[] args = { storeId };
		return jdbcTemplate.queryForObject(GET_LASTMONTH_STOR_EWORKDAYS, args, BigDecimal.class);
	}

	/**
	 * 选择区间月的大店销售额
	 * 
	 * @param beginYm
	 * @param endYm
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public List<RunBaseReport> getScopeStoreSale(String beginYm, String endYm, String storeId) throws Exception {
		return super.queryForList(GET_SCOPE_STORE_SALE, RUN_BASEREPORT_RM, beginYm, endYm, storeId);
	}

	/**
	 * 选择区间月的大店人员总时长
	 * 
	 * @param beginYm
	 * @param endYm
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public List<RunBaseReport> getScopeStoreWorkDays(String beginYm, String endYm, String storeId) throws Exception {
		return super.queryForList(GET_SCOPE_STORE_WORKDAYS, RUN_BASEREPORT_RM, beginYm, endYm, storeId);
	}

	/**
	 * 标准档位
	 * 
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public List<WorkLevelConfig> getWorkLevelConfigList(String groupId) throws Exception {
		return super.queryForList(GET_WORKLEVEL_CONFIG_LIST, WORKLEVEL_CONFIG_RM, groupId);
	}

}
