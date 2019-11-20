package com.cs.mobile.api.dao.partner.battle.gm;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.BaseReport;

/**
 * 小店毛利战报<br>
 * 小店角色数据<br>
 * 查看小店下所有大类数据
 * 
 * @author wells
 * @date 2019年1月5日
 */
@Repository
public class ComGMBattleDao extends AbstractDao {
	private static final RowMapper<BaseReport> BASE_REPORT_RM = new BeanPropertyRowMapper<>(BaseReport.class);
	// 当日实时总毛利列表
	private static final String GET_TODAY_GM_LIST = "select sale.SALE_HOUR AS time,sale.DEPT_ID as org_id,sale.DEPT_ID as org_name, "
			+ "sale.GP_ECL+SALE.AMT_ECL*NVL(income.IN_VALUES,0)-SALE.AMT_ECL*NVL(income.DC_VALUES,0) AS value  "
			+ "from CSMB_DEPT_SALES sale left join CSMB_COMPANY company "
			+ "on company.STORE_ID=sale.STORE_ID and COMPANY.DEPT_ID=sale.DEPT_ID "
			+ "left join (select * from CSMB_INCOME_VALUES  where ym=to_char(sysdate,'yyyy-mm')  "
			+ "and province_id=(select province_id from csmb_store where store_id=?) ) income "
			+ "on sale.DEPT_ID=income.DEPT_ID where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE "
			+ "and company.COM_ID=? and sale.store_id=?";
	// 当日实时前台毛利列表
	private static final String GET_TODAY_FGM_LIST = "select sale.SALE_HOUR AS time,sale.DEPT_ID as org_id,sale.DEPT_ID as org_name, "
			+ "sale.GP_ECL AS value from CSMB_DEPT_SALES sale left join CSMB_COMPANY company "
			+ "on company.STORE_ID=sale.STORE_ID and COMPANY.DEPT_ID=sale.DEPT_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and company.COM_ID=? and sale.store_id=?";

	// 当月历史毛利列表
	private static final String GET_CURMONTH_GM_LIST = "select time,org_id,NVL(sum(value),0) AS value,org_name "
			+ "from (select to_char(history.SALE_DATE,'yyyy-mm-dd') AS time,history.DEPT_ID as org_id, "
			+ "((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)) as value "
			+ ",history.DEPT_ID as org_name from CSMB_DEPT_SALES_HISTORY history " + "left join CSMB_COMPANY company "
			+ "on company.STORE_ID=HISTORY.STORE_ID and COMPANY.DEPT_ID=HISTORY.DEPT_ID "
			+ "left join (select * from CSMB_INCOME_VALUES  where ym=to_char(sysdate,'yyyy-mm') "
			+ "and province_id=(select province_id from csmb_store where store_id=?) ) income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(sysdate,'yyyy-mm')=to_char(history.SALE_DATE,'yyyy-mm') "
			+ "and company.COM_ID=? and history.store_id=?) group by time,org_id,org_name";

	// 去年今日毛利总额
	private static final String GET_LASTYEARDAY_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_COMPANY company "
			+ "on company.STORE_ID=HISTORY.STORE_ID and COMPANY.DEPT_ID=HISTORY.DEPT_ID "
			+ "left join CSMB_INCOME_VALUES income on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and company.COM_ID=? and history.store_id=?";

	// 上个月的当天毛利总额
	private static final String GET_LASTMONTHDAY_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_COMPANY company "
			+ "on company.STORE_ID=HISTORY.STORE_ID and COMPANY.DEPT_ID=HISTORY.DEPT_ID "
			+ "left join CSMB_INCOME_VALUES income on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm-dd') "
			+ "and company.COM_ID=? and history.store_id=?";

	// 去年本月毛利总额
	private static final String GET_LASTYEARMONTH_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_COMPANY company "
			+ "on company.STORE_ID=HISTORY.STORE_ID and COMPANY.DEPT_ID=HISTORY.DEPT_ID "
			+ "left join CSMB_INCOME_VALUES income on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and company.COM_ID=? and history.store_id=?";

	// 上个月毛利总额
	private static final String GET_LASTMONTH_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_COMPANY company "
			+ "on company.STORE_ID=HISTORY.STORE_ID and COMPANY.DEPT_ID=HISTORY.DEPT_ID "
			+ "left join CSMB_INCOME_VALUES income on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') "
			+ "and company.COM_ID=? and history.store_id=?";

	// 去年今日列表
	private static final String GET_LASTYEAR_DAY_LIST = "SELECT  /*+PARALLEL(8)*/  b.dept_id as org_id "
			+ ",b.dept_id as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100) - a.unit_cost_ecl * a.qty) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c "
			+ " where a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ "  and a.store = b.store_id and a.item=c.item  and b.dept_id=c.dept " + "  and b.com_id=?  and a.store=? "
			+ "   and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')  group by b.dept_id";

	// 去年本月列表
	private static final String GET_LASTYEAR_MONTH_LIST = "SELECT  /*+PARALLEL(8)*/  b.dept_id as org_id "
			+ ",b.dept_id as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100) - a.unit_cost_ecl * a.qty) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c " + " where  " + " ( "
			+ " (a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ " and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')) " + " or  "
			+ " (to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') <>trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date >=trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date < to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd')) "
			+ " ) and a.store = b.store_id and a.item=c.item  and b.dept_id=c.dept "
			+ "  and b.com_id=?  and a.store=?  group by b.dept_id";

	/**
	 * 当日实时总毛利列表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodayGMList(String storeId, String comId) throws Exception {
		return super.queryForList(GET_TODAY_GM_LIST, BASE_REPORT_RM, storeId, comId, storeId);
	}

	/**
	 * 当日实时前台毛利列表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodayFGMList(String storeId, String comId) throws Exception {
		return super.queryForList(GET_TODAY_FGM_LIST, BASE_REPORT_RM, comId, storeId);
	}

	/**
	 * 当月历史毛利列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getCurMonthGMList(String storeId, String comId) throws Exception {
		return super.queryForList(GET_CURMONTH_GM_LIST, BASE_REPORT_RM, storeId, comId, storeId);
	}

	/**
	 * 去年今日毛利总额
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayGMTotal(String storeId, String comId) throws Exception {
		Object[] args = { comId, storeId };
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_GM_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月的当天毛利总额
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthDayGMTotal(String storeId, String comId) throws Exception {
		Object[] args = { comId, storeId };
		return jdbcTemplate.queryForObject(GET_LASTMONTHDAY_GM_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年本月毛利总额
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthGMTotal(String storeId, String comId) throws Exception {
		Object[] args = { comId, storeId };
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_GM_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月毛利总额
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthGMTotal(String storeId, String comId) throws Exception {
		Object[] args = { comId, storeId };
		return jdbcTemplate.queryForObject(GET_LASTMONTH_GM_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年今日列表
	 * 
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月5日
	 */
	public List<BaseReport> getLastYearDayList(String storeId, String comId) throws Exception {
		return super.queryForList(GET_LASTYEAR_DAY_LIST, BASE_REPORT_RM, comId, storeId);
	}

	/**
	 * 去年本月列表
	 * 
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月5日
	 */
	public List<BaseReport> getLastYearMonthList(String storeId, String comId) throws Exception {
		return super.queryForList(GET_LASTYEAR_MONTH_LIST, BASE_REPORT_RM, comId, storeId);
	}

}
