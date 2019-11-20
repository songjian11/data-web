package com.cs.mobile.api.dao.partner.battle.gm;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.BaseReport;

/**
 * 省份毛利战报<br>
 * 省份管理员角色数据<br>
 * 查看省份下所有区域数据
 * 
 * @author wells
 * @date 2019年1月5日
 */
@Repository
public class ProvinceGMBattleDao extends AbstractDao {
	private static final RowMapper<BaseReport> BASE_REPORT_RM = new BeanPropertyRowMapper<>(BaseReport.class);
	// 当日实时总毛利列表
	private static final String GET_TODAY_GM_LIST = "select sale.SALE_HOUR AS time,store.AREA_ID as org_id,store.AREA_NAME as org_name, "
			+ "NVL((sale.GP_ECL+SALE.AMT_ECL*NVL(income.IN_VALUES,0)-SALE.AMT_ECL*NVL(income.DC_VALUES,0)),0) AS value  "
			+ ",store.store_id,store.IS_COMPARE from CSMB_DEPT_SALES sale "
			+ "left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "left join (select * from CSMB_INCOME_VALUES  where ym=to_char(sysdate,'yyyy-mm')) income "
			+ "on sale.DEPT_ID=income.DEPT_ID and income.province_id=store.province_Id "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and store.PROVINCE_ID=? ";
	// 当日实时前台毛利列表
	private static final String GET_TODAY_FGM_LIST = "select sale.SALE_HOUR AS time,store.AREA_ID as org_id,store.AREA_NAME as org_name, "
			+ "NVL(sale.GP_ECL,0) AS value,store.STORE_ID ,store.IS_COMPARE from CSMB_COM_SALES sale "
			+ "left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE  and store.PROVINCE_ID=?";

	// 当月历史毛利列表
	private static final String GET_CURMONTH_GM_LIST = "select to_char(history.SALE_DATE,'yyyy-mm-dd') AS time,store.AREA_ID as org_id, "
			+ "((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)) as value "
			+ ",store.AREA_NAME as org_name,store.STORE_ID,store.IS_COMPARE "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "left join (select * from CSMB_INCOME_VALUES  where ym=to_char(sysdate,'yyyy-mm')) income "
			+ "on history.DEPT_ID=income.DEPT_ID and income.province_id=store.province_Id "
			+ "where to_char(sysdate,'yyyy-mm')=to_char(history.SALE_DATE,'yyyy-mm') and store.PROVINCE_ID=? ";

	// 去年今日毛利总额
	private static final String GET_LASTYEARDAY_GM_TOTAL = "select NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*income.IN_VALUES-history.SALE_VALUE*income.DC_VALUES),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and store.PROVINCE_ID=?";

	// 去年今日可比毛利总额
	private static final String GET_LASTYEARDAY_C_GM_TOTAL = "select NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*income.IN_VALUES-history.SALE_VALUE*income.DC_VALUES),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and store.PROVINCE_ID=1 and store.IS_COMPARE=?";

	// 上个月的当天毛利总额
	private static final String GET_LASTMONTHDAY_GM_TOTAL = "select NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*income.IN_VALUES-history.SALE_VALUE*income.DC_VALUES),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm-dd') "
			+ "and store.PROVINCE_ID=?";

	// 去年本月毛利总额
	private static final String GET_LASTYEARMONTH_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and store.PROVINCE_ID=?";

	// 去年本月可比毛利总额
	private static final String GET_LASTYEARMONTH_C_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and store.PROVINCE_ID=1 and store.IS_COMPARE=?";

	// 上个月毛利总额
	private static final String GET_LASTMONTH_GM_TOTAL = "select "
			+ "NVL(sum((history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "+history.SALE_VALUE*NVL(income.IN_VALUES,0)-history.SALE_VALUE*NVL(income.DC_VALUES,0)),0) "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID left join CSMB_INCOME_VALUES income "
			+ "on history.DEPT_ID=income.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') "
			+ "and store.PROVINCE_ID=?";

	// 去年今日列表
	private static final String GET_LASTYEAR_DAY_LIST = "SELECT  /*+PARALLEL(8)*/  s.region as org_id "
			+ ",s.region_name as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100) - a.unit_cost_ecl * a.qty) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c,zypp.inf_store s "
			+ " where a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ "  and a.store = b.store_id and a.item=c.item  and a.store = s.store "
			+ "  and b.dept_id=c.dept and s.area = ? " + "	and (b.com_id<>'ALL' or b.com_name='整店')  "
			+ "   and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')  group by s.region,s.region_name";

	// 去年本月列表
	private static final String GET_LASTYEAR_MONTH_LIST = "SELECT  /*+PARALLEL(8)*/  s.region as org_id "
			+ ",s.region_name as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100) - a.unit_cost_ecl * a.qty) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c,zypp.inf_store s " + " where  " + " ( "
			+ " (a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ " and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')) " + " or  "
			+ " (to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') <>trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date >=trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date < to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd')) "
			+ " ) and a.store = b.store_id and a.item=c.item  and a.store = s.store "
			+ "  and b.dept_id=c.dept and s.area = ? and (b.com_id<>'ALL' or b.com_name='整店')  "
			+ "   group by s.region,s.region_name";

	/**
	 * 当日实时总毛利列表
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodayGMList(String provinceId) throws Exception {
		return super.queryForList(GET_TODAY_GM_LIST, BASE_REPORT_RM, provinceId);
	}

	/**
	 * 当日实时前台毛利列表
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodayFGMList(String provinceId) throws Exception {
		return super.queryForList(GET_TODAY_FGM_LIST, BASE_REPORT_RM, provinceId);
	}

	/**
	 * 当月历史毛利列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getCurMonthGMList(String provinceId) throws Exception {
		return super.queryForList(GET_CURMONTH_GM_LIST, BASE_REPORT_RM, provinceId);
	}

	/**
	 * 去年今日毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayGMTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_GM_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年今日可比毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayCGMTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_C_GM_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月的当天毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthDayGMTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTMONTHDAY_GM_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年本月毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthGMTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_GM_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年本月可比毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthCGMTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_C_GM_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月毛利总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthGMTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
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
	public List<BaseReport> getLastYearDayList(String provinceId) throws Exception {
		return super.queryForList(GET_LASTYEAR_DAY_LIST, BASE_REPORT_RM, provinceId);
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
	public List<BaseReport> getLastYearMonthList(String provinceId) throws Exception {
		return super.queryForList(GET_LASTYEAR_MONTH_LIST, BASE_REPORT_RM, provinceId);
	}
}
