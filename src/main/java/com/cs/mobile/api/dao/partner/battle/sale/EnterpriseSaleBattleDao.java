package com.cs.mobile.api.dao.partner.battle.sale;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.BaseReport;

/**
 * 全司销售战报<br>
 * 全司管理员角色数据<br>
 * 查看全司下所有省份数据
 * 
 * @author wells
 * @date 2019年1月5日
 */
@Repository
public class EnterpriseSaleBattleDao extends AbstractDao {
	private static final RowMapper<BaseReport> BASE_REPORT_RM = new BeanPropertyRowMapper<>(BaseReport.class);
	// 当日实时销售列表
	private static final String GET_TODAY_SALE_LIST = "select sale.SALE_HOUR AS time,store.PROVINCE_ID as org_id,store.PROVINCE_NAME as org_name,"
			+ "NVL(sale.AMT_ECL,0) AS value,sale.store_id,store.IS_COMPARE from CSMB_COM_SALES sale "
			+ "left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "left join (select store_id,com_id,com_name from CSMB_COMPANY group by store_id,com_id,com_name) company "
			+ "on sale.COM_ID=company.COM_ID and sale.STORE_ID=COMPANY.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and (company.COM_ID<>'ALL' or company.COM_NAME='整店')";

	// 当月历史销售列表
	private static final String GET_CURMONTH_SALE_LIST = "select to_char(history.SALE_DATE,'yyyy-mm-dd') AS time,history.SALE_VALUE AS value,store.PROVINCE_ID as org_id "
			+ ",store.PROVINCE_NAME as org_name,store.store_id,store.IS_COMPARE "
			+ " from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyy-mm')=to_char(HISTORY.SALE_DATE,'yyyy-mm')";

	// 去年今日销售总额
	private static final String GET_LASTYEARDAY_SALE_TOTAL = "select NVL(sum(history.sale_value),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd')";

	// 去年今日可比销售额
	private static final String GET_LASTYEARDAY_C_SALE_TOTAL = "select NVL(sum(history.sale_value),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and store.IS_COMPARE=1";

	// 上个月的当天销售总额
	private static final String GET_LASTMONTHDAY_SALE_TOTAL = "select NVL(sum(history.SALE_VALUE),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm-dd')";

	// 去年本月销售总额
	private static final String GET_LASTYEARMONTH_SALE_TOTAL = "select NVL(sum(SALE_VALUE),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm')";

	// 去年本月可比销售总额
	private static final String GET_LASTYEARMONTH_C_SALE_TOTAL = "select NVL(sum(SALE_VALUE),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and store.IS_COMPARE=1";

	// 上个月销售总额
	private static final String GET_LASTMONTH_SALE_TOTAL = "select NVL(sum(history.SALE_VALUE),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') ";

	// 去年今日列表
	private static final String GET_LASTYEAR_DAY_LIST = "SELECT  /*+PARALLEL(8)*/  s.area as org_id "
			+ ",s.area_name as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c,zypp.inf_store s "
			+ " where a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ "  and a.store = b.store_id and a.item=c.item  and a.store = s.store "
			+ "  and b.dept_id=c.dept and (b.com_id<>'ALL' or b.com_name='整店')  "
			+ "   and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')  group by s.area,s.area_name";

	// 去年本月列表
	private static final String GET_LASTYEAR_MONTH_LIST = "SELECT  /*+PARALLEL(8)*/  s.area as org_id "
			+ ",s.area_name as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c,zypp.inf_store s " + " where  " + " ( "
			+ " (a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ " and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')) " + " or  "
			+ " (to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') <>trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date >=trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date < to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd')) "
			+ " ) and a.store = b.store_id and a.item=c.item  and a.store = s.store "
			+ "  and b.dept_id=c.dept and (b.com_id<>'ALL' or b.com_name='整店')   group by s.area,s.area_name";

	/**
	 * 当日实时销售列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodaySaleList() throws Exception {
		return super.queryForList(GET_TODAY_SALE_LIST, BASE_REPORT_RM);
	}

	/**
	 * 当月历史销售列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getCurMonthSaleList() throws Exception {
		return super.queryForList(GET_CURMONTH_SALE_LIST, BASE_REPORT_RM);
	}

	/**
	 * 去年今日销售总额
	 * 
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDaySaleTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_SALE_TOTAL, BigDecimal.class);
	}

	/**
	 * 去年今日可比销售总额
	 * 
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayCSaleTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_C_SALE_TOTAL, BigDecimal.class);
	}

	/**
	 * 上个月的当天销售总额
	 * 
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthDaySaleTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTMONTHDAY_SALE_TOTAL, BigDecimal.class);
	}

	/**
	 * 去年本月销售总额
	 * 
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthSaleTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_SALE_TOTAL, BigDecimal.class);
	}

	/**
	 * 去年本月可比销售总额
	 * 
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthCSaleTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_C_SALE_TOTAL, BigDecimal.class);
	}

	/**
	 * 上个月销售总额
	 * 
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthSaleTotal() throws Exception {
		return jdbcTemplate.queryForObject(GET_LASTMONTH_SALE_TOTAL, BigDecimal.class);
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
