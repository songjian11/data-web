package com.cs.mobile.api.dao.partner.battle.sale;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.BaseReport;

/**
 * 省份销售战报<br>
 * 省份管理员角色数据<br>
 * 查看省份下所有区域数据
 * 
 * @author wells
 * @date 2019年1月5日
 */
@Repository
public class ProvinceSaleBattleDao extends AbstractDao {
	private static final RowMapper<BaseReport> BASE_REPORT_RM = new BeanPropertyRowMapper<>(BaseReport.class);
	// 当日实时销售列表
	private static final String GET_TODAY_SALE_LIST = "select sale.SALE_HOUR AS time,store.AREA_ID as org_id,store.AREA_NAME as org_name, "
			+ "store.IS_COMPARE,sale.store_id, NVL(sale.AMT_ECL,0) AS value from CSMB_COM_SALES sale "
			+ "left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "left join (select store_id,com_id,com_name from CSMB_COMPANY group by store_id,com_id,com_name) company "
			+ "on sale.COM_ID=company.COM_ID and sale.STORE_ID=COMPANY.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and store.PROVINCE_ID=? and (company.COM_ID<>'ALL' or company.COM_NAME='整店') ";

	// 当月历史销售列表
	private static final String GET_CURMONTH_SALE_LIST = "select to_char(history.SALE_DATE,'yyyy-mm-dd') AS time,NVL(history.SALE_VALUE,0) AS value, "
			+ "store.AREA_ID as org_id,store.AREA_NAME as org_name,store.store_id,store.IS_COMPARE "
			+ " from CSMB_DEPT_SALES_HISTORY history left join CSMB_STORE store "
			+ "on history.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyy-mm')=to_char(HISTORY.SALE_DATE,'yyyy-mm') and store.PROVINCE_ID=? ";

	// 去年今日销售总额
	private static final String GET_LASTYEARDAY_SALE_TOTAL = "select NVL(sum(history.sale_value),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and store.PROVINCE_ID=? ";

	// 去年今日可比销售额
	private static final String GET_LASTYEARDAY_C_SALE_TOTAL = "select NVL(sum(history.sale_value),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and store.PROVINCE_ID=? and store.IS_COMPARE=1";

	// 上个月的当天销售总额
	private static final String GET_LASTMONTHDAY_SALE_TOTAL = "select NVL(sum(history.SALE_VALUE),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm-dd') "
			+ "and store.PROVINCE_ID=? ";

	// 去年本月销售总额
	private static final String GET_LASTYEARMONTH_SALE_TOTAL = "select NVL(sum(SALE_VALUE),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and store.PROVINCE_ID=? ";

	// 去年本月可比销售总额
	private static final String GET_LASTYEARMONTH_C_SALE_TOTAL = "select NVL(sum(SALE_VALUE),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and store.PROVINCE_ID=? and store.IS_COMPARE=1";

	// 上个月销售总额
	private static final String GET_LASTMONTH_SALE_TOTAL = "select NVL(sum(history.SALE_VALUE),0) from CSMB_DEPT_SALES_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') "
			+ "and store.PROVINCE_ID=? ";

	// 去年今日列表
	private static final String GET_LASTYEAR_DAY_LIST = "SELECT  /*+PARALLEL(8)*/  s.region as org_id "
			+ ",s.region_name as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c,zypp.inf_store s "
			+ " where a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ "  and a.store = b.store_id and a.item=c.item  and a.store = s.store "
			+ "  and b.dept_id=c.dept and s.area = ? " + "	and (b.com_id<>'ALL' or b.com_name='整店')  "
			+ "   and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')  group by s.region,s.region_name " + "";

	// 去年本月列表
	private static final String GET_LASTYEAR_MONTH_LIST = "SELECT  /*+PARALLEL(8)*/  s.region as org_id "
			+ ",s.region_name as org_name,sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)) as value "
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
	 * 当日实时销售列表
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodaySaleList(String areaId) throws Exception {
		return super.queryForList(GET_TODAY_SALE_LIST, BASE_REPORT_RM, areaId);
	}

	/**
	 * 当月历史销售列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getCurMonthSaleList(String areaId) throws Exception {
		return super.queryForList(GET_CURMONTH_SALE_LIST, BASE_REPORT_RM, areaId);
	}

	/**
	 * 去年今日销售总额
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDaySaleTotal(String areaId) throws Exception {
		Object[] args = { areaId };
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_SALE_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年今日可比销售总额
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayCSaleTotal(String areaId) throws Exception {
		Object[] args = { areaId };
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_C_SALE_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月的当天销售总额
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthDaySaleTotal(String areaId) throws Exception {
		Object[] args = { areaId };
		return jdbcTemplate.queryForObject(GET_LASTMONTHDAY_SALE_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年本月销售总额
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthSaleTotal(String areaId) throws Exception {
		Object[] args = { areaId };
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_SALE_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年本月可比销售总额
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthCSaleTotal(String areaId) throws Exception {
		Object[] args = { areaId };
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_C_SALE_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月销售总额
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthSaleTotal(String areaId) throws Exception {
		Object[] args = { areaId };
		return jdbcTemplate.queryForObject(GET_LASTMONTH_SALE_TOTAL, args, BigDecimal.class);
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
