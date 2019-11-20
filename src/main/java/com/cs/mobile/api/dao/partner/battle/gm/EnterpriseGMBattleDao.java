package com.cs.mobile.api.dao.partner.battle.gm;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.BaseReport;

/**
 * 全司毛利战报<br>
 * 全司管理员角色数据<br>
 * 查看全司下所有省份数据
 * 
 * @author wells
 * @date 2019年1月5日
 */
@Repository
public class EnterpriseGMBattleDao extends AbstractDao {
	private static final RowMapper<BaseReport> BASE_REPORT_RM = new BeanPropertyRowMapper<>(BaseReport.class);
	// 当日实时总毛利列表
	private static final String GET_TODAY_GM_LIST = "select sale.SALE_HOUR AS time,store.PROVINCE_ID as org_id,store.PROVINCE_NAME as org_name, "
			+ "NVL((sale.GP_ECL+SALE.AMT_ECL*NVL(income.IN_VALUES,0)-SALE.AMT_ECL*NVL(income.DC_VALUES,0)),0) AS value  "
			+ ",store.store_id,store.IS_COMPARE from CSMB_DEPT_SALES sale "
			+ "left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "left join (select * from CSMB_INCOME_VALUES  where ym=to_char(sysdate,'yyyy-mm')) income "
			+ "on sale.DEPT_ID=income.DEPT_ID and income.province_id=store.province_Id "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE";
	// 当日实时前台毛利列表
	private static final String GET_TODAY_FGM_LIST = "select sale.SALE_HOUR AS time,store.PROVINCE_ID as org_id,store.PROVINCE_NAME as org_name, "
			+ "NVL(sale.GP_ECL,0) AS value,store.STORE_ID ,store.IS_COMPARE from CSMB_COM_SALES sale "
			+ "left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and sale.COM_ID<>'ALL'";

	// 当月历史毛利列表
	private static final String GET_CURMONTH_GM_LIST = "select to_char(history.SALE_DATE,'yyyy-mm-dd') AS time,store.PROVINCE_ID as org_id, "
			+ "((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)) as value "
			+ ",store.PROVINCE_NAME as org_name,store.STORE_ID,store.IS_COMPARE "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store " + "on history.STORE_ID=store.STORE_ID "
			+ "left join (select * from CSMB_INCOME_VALUES  where ym=to_char(sysdate,'yyyy-mm')) income "
			+ "on history.DEPT_ID=income.DEPT_ID and income.province_id=store.province_Id "
			+ "where to_char(sysdate,'yyyy-mm')=to_char(history.SALE_DATE,'yyyy-mm')";

	// 去年今日毛利总额
	private static final String GET_LASTYEARDAY_GM_TOTAL = "select NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*income.IN_VALUES-history.SALE_VALUE*income.DC_VALUES),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd')";

	// 去年今日可比毛利总额
	private static final String GET_LASTYEARDAY_C_GM_TOTAL = "select NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*income.IN_VALUES-history.SALE_VALUE*income.DC_VALUES),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and store.IS_COMPARE=1 ";

	// 上个月的当天毛利总额
	private static final String GET_LASTMONTHDAY_GM_TOTAL = "select NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*income.IN_VALUES-history.SALE_VALUE*income.DC_VALUES),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm-dd')";

	// 去年本月毛利总额
	private static final String GET_LASTYEARMONTH_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm')";

	// 去年本月可比毛利总额
	private static final String GET_LASTYEARMONTH_C_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and store.IS_COMPARE=1";

	// 上个月毛利总额
	private static final String GET_LASTMONTH_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm')";

	// 去年今日列表
	private static final String GET_LASTYEAR_DAY_LIST = "SELECT  /*+PARALLEL(8)*/  s.area as org_id "
			+ ",s.area_name as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100) - a.unit_cost_ecl * a.qty) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c,zypp.inf_store s "
			+ " where a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ "  and a.store = b.store_id and a.item=c.item  and a.store = s.store "
			+ "  and b.dept_id=c.dept and (b.com_id<>'ALL' or b.com_name='整店')  "
			+ "   and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')  group by s.area,s.area_name";

	// 去年本月列表
	private static final String GET_LASTYEAR_MONTH_LIST = "SELECT  /*+PARALLEL(8)*/  s.area as org_id "
			+ ",s.area_name as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100) - a.unit_cost_ecl * a.qty) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c,zypp.inf_store s " + " where  " + " ( "
			+ " (a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ " and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')) " + " or  "
			+ " (to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') <>trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date >=trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date < to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd')) "
			+ " ) and a.store = b.store_id and a.item=c.item  and a.store = s.store "
			+ "  and b.dept_id=c.dept and (b.com_id<>'ALL' or b.com_name='整店')   group by s.area,s.area_name";

	/**
	 * 当日实时总毛利列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodayGMList() throws Exception {
		return super.queryForList(GET_TODAY_GM_LIST, BASE_REPORT_RM);
	}

	/**
	 * 当日实时前台毛利列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodayFGMList() throws Exception {
		return super.queryForList(GET_TODAY_FGM_LIST, BASE_REPORT_RM);
	}

	/**
	 * 当月历史毛利列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getCurMonthGMList() throws Exception {
		return super.queryForList(GET_CURMONTH_GM_LIST, BASE_REPORT_RM);
	}

	/**
	 * 去年今日毛利总额
	 * 
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayGMTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_GM_TOTAL, BigDecimal.class);
	}

	/**
	 * 去年今日可比毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayCGMTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_C_GM_TOTAL, BigDecimal.class);
	}

	/**
	 * 上个月的当天毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthDayGMTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTMONTHDAY_GM_TOTAL, BigDecimal.class);
	}

	/**
	 * 去年本月毛利总额
	 * 
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthGMTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_GM_TOTAL, BigDecimal.class);
	}

	/**
	 * 去年本月可比毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthCGMTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_C_GM_TOTAL, BigDecimal.class);
	}

	/**
	 * 上个月毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthGMTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTMONTH_GM_TOTAL, BigDecimal.class);
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
	public List<BaseReport> getLastYearDayList() throws Exception {
		return super.queryForList(GET_LASTYEAR_DAY_LIST, BASE_REPORT_RM);
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
	public List<BaseReport> getLastYearMonthList() throws Exception {
		return super.queryForList(GET_LASTYEAR_MONTH_LIST, BASE_REPORT_RM);
	}

}
