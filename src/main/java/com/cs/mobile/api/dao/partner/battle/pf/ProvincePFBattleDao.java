package com.cs.mobile.api.dao.partner.battle.pf;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.BaseReport;

/**
 * 省份客流战报<br>
 * 省份管理员角色数据<br>
 * 查看省份下所有区域数据
 * 
 * @author wells
 * @date 2019年1月5日
 */
@Repository
public class ProvincePFBattleDao extends AbstractDao {
	private static final RowMapper<BaseReport> BASE_REPORT_RM = new BeanPropertyRowMapper<>(BaseReport.class);
	// 当日实时客流列表
	private static final String GET_TODAY_PF_LIST = "select sale.SALE_HOUR AS time,store.AREA_ID as org_id,store.AREA_NAME as org_name, "
			+ "sale.KL AS value,sale.STORE_ID ,store.IS_COMPARE from CSMB_COM_SALES sale "
			+ "left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE  and sale.COM_ID='ALL' " + "and store.PROVINCE_ID=?";

	// 当月历史客流列表
	private static final String GET_CURMONTH_PF_LIST = "select to_char(history.SALE_DATE,'yyyy-mm-dd') AS time,history.KL AS value, "
			+ "store.AREA_ID as org_id,store.AREA_NAME as org_name,store.STORE_ID ,store.IS_COMPARE "
			+ "from CSMB_COM_KL_HISTORY history left join CSMB_STORE store " + "on history.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyy-mm')=to_char(history.SALE_DATE,'yyyy-mm') "
			+ "and store.PROVINCE_ID=? and history.COM_ID='ALL'";

	// 去年今日客流总额
	private static final String GET_LASTYEARDAY_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and store.PROVINCE_ID=? and history.COM_ID='ALL'";

	// 去年今日可比客流总额
	private static final String GET_LASTYEARDAY_C_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and store.PROVINCE_ID=? and history.COM_ID='ALL' and store.IS_COMPARE=1";

	// 上个月的当天客流总额
	private static final String GET_LASTMONTHDAY_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm-dd') "
			+ "and store.PROVINCE_ID=? and history.COM_ID='ALL'";

	// 去年本月客流总额
	private static final String GET_LASTYEARMONTH_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and store.AREA_ID=? and history.COM_ID='ALL'";

	// 去年本月可比客流总额
	private static final String GET_LASTYEARMONTH_C_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and store.PROVINCE_ID=? and history.COM_ID='ALL' and store.IS_COMPARE=1";

	// 上个月客流总额
	private static final String GET_LASTMONTH_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY history "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') "
			+ "and store.PROVINCE_ID=? and history.COM_ID='ALL'";

	// 去年今日列表
	private static final String GET_LASTYEAR_DAY_LIST = "SELECT  /*+PARALLEL(8)*/  s.region as org_id "
			+ ",s.region_name as org_name,count(distinct tran_seq_no) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c,zypp.inf_store s "
			+ " where a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ "  and a.store = b.store_id and a.item=c.item  and a.store = s.store "
			+ "  and b.dept_id=c.dept and s.area = ? " + "	and (b.com_id<>'ALL' or b.com_name='整店')  "
			+ "   and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')  group by s.region,s.region_name";

	// 去年本月列表
	private static final String GET_LASTYEAR_MONTH_LIST = "SELECT  /*+PARALLEL(8)*/  s.region as org_id "
			+ ",s.region_name as org_name,count(distinct tran_seq_no) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c,zypp.inf_store s " + " where  " + " ( "
			+ " (a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ " and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')) " + " or  "
			+ " (to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') <>trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date >=trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date < to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd')) "
			+ " ) and a.store = b.store_id and a.item=c.item  and a.store = s.store "
			+ "  and b.dept_id=c.dept and s.area = ? and (b.com_id<>'ALL' or b.com_name='整店')  " + "   group by s.region,s.region_name";

	/**
	 * 当日实时客流列表
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodayPFList(String provinceId) throws Exception {
		return super.queryForList(GET_TODAY_PF_LIST, BASE_REPORT_RM, provinceId);
	}

	/**
	 * 当月历史客流列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getCurMonthPFList(String provinceId) throws Exception {
		return super.queryForList(GET_CURMONTH_PF_LIST, BASE_REPORT_RM, provinceId);
	}

	/**
	 * 去年今日客流总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayPFTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_PF_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年今日可比客流总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayCPFTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_C_PF_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月的当天客流总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthDayPFTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTMONTHDAY_PF_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年本月客流总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthPFTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_PF_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年本月可比客流总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthCPFTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_C_PF_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月客流总额
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthPFTotal(String provinceId) throws Exception {
		Object[] args = { provinceId };
		return jdbcTemplate.queryForObject(GET_LASTMONTH_PF_TOTAL, args, BigDecimal.class);
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
