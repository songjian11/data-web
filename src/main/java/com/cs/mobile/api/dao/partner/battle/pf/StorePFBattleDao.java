package com.cs.mobile.api.dao.partner.battle.pf;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.BaseReport;

/**
 * 大店客流战报<br>
 * 大店角色数据<br>
 * 查看大店下所有小店数据
 * 
 * @author wells
 * @date 2019年1月5日
 */
@Repository
public class StorePFBattleDao extends AbstractDao {
	private static final RowMapper<BaseReport> BASE_REPORT_RM = new BeanPropertyRowMapper<>(BaseReport.class);
	// 当日实时客流列表
	private static final String GET_TODAY_PF_LIST = "select sale.SALE_HOUR AS time,sale.COM_ID as org_id,company.COM_NAME as org_name, "
			+ "sale.KL AS value from CSMB_COM_SALES sale "
			+ "left join (select store_id,com_id,com_name from CSMB_COMPANY group by store_id,com_id,com_name) company "
			+ "on sale.COM_ID=company.COM_ID and sale.STORE_ID=COMPANY.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and sale.store_id=? ";

	// 当月历史客流列表
	private static final String GET_CURMONTH_PF_LIST = "select to_char(history.SALE_DATE,'yyyy-mm-dd') AS time,history.KL AS value,history.COM_ID as org_id "
			+ ",company.COM_NAME as org_name from CSMB_COM_KL_HISTORY history left join CSMB_COMPANY company "
			+ "on company.com_id=history.COM_ID and company.STORE_ID=HISTORY.STORE_ID "
			+ "where to_char(sysdate,'yyyy-mm')=to_char(history.SALE_DATE,'yyyy-mm') and history.store_id=?";

	// 去年今日客流总额
	private static final String GET_LASTYEARDAY_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY "
			+ "where to_char(SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd') "
			+ "and store_id=? and COM_ID='ALL'";

	// 上个月的当天客流总额
	private static final String GET_LASTMONTHDAY_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY "
			+ "where to_char(SALE_DATE,'yyyy-mm-dd')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm-dd') "
			+ "and store_id=? and COM_ID='ALL'";

	// 去年本月客流总额
	private static final String GET_LASTYEARMONTH_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY "
			+ "where to_char(SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "and store_id=? and COM_ID='ALL'";

	// 上个月客流总额
	private static final String GET_LASTMONTH_PF_TOTAL = "select NVL(sum(KL),0) from CSMB_COM_KL_HISTORY "
			+ "where to_char(SALE_DATE,'yyyy-mm')=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') "
			+ "and store_id=? and COM_ID='ALL'";

	// 去年今日列表
	private static final String GET_LASTYEAR_DAY_LIST = "SELECT  /*+PARALLEL(8)*/  b.com_id as org_id "
			+ ",b.com_name as org_name,count(distinct tran_seq_no) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c "
			+ " where a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ "  and a.store = b.store_id and a.item=c.item  and b.dept_id=c.dept "
			+ "   and a.store=?  and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS') " + "   group by b.com_id,b.com_name "
			+ "";

	// 去年本月列表
	private static final String GET_LASTYEAR_MONTH_LIST = "SELECT  /*+PARALLEL(8)*/  b.com_id as org_id "
			+ ",b.com_name as org_name,count(distinct tran_seq_no) as value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c " + " where  " + " ( "
			+ " (a.business_date = to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') "
			+ " and tran_datetime<=TO_CHAR(SYSDATE,'HH24:MI:SS')) " + " or  "
			+ " (to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd') <>trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date >=trunc(ADD_MONTHS(sysdate, -12), 'mm')  "
			+ "  and a.business_date < to_date(to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm-dd'),'yyyy-mm-dd')) "
			+ " ) and a.store = b.store_id and a.item=c.item  and b.dept_id=c.dept "
			+ "   and a.store=?  group by b.com_id,b.com_name";

	/**
	 * 当日实时客流列表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getTodayPFList(String storeId) throws Exception {
		return super.queryForList(GET_TODAY_PF_LIST, BASE_REPORT_RM, storeId);
	}

	/**
	 * 当月历史客流列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReport> getCurMonthPFList(String storeId) throws Exception {
		return super.queryForList(GET_CURMONTH_PF_LIST, BASE_REPORT_RM, storeId);
	}

	/**
	 * 去年今日客流总额
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearDayPFTotal(String storeId) throws Exception {
		Object[] args = { storeId };
		return jdbcTemplate.queryForObject(GET_LASTYEARDAY_PF_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月的当天客流总额
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthDayPFTotal(String storeId) throws Exception {
		Object[] args = { storeId };
		return jdbcTemplate.queryForObject(GET_LASTMONTHDAY_PF_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 去年本月客流总额
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastYearMonthPFTotal(String storeId) throws Exception {
		Object[] args = { storeId };
		return jdbcTemplate.queryForObject(GET_LASTYEARMONTH_PF_TOTAL, args, BigDecimal.class);
	}

	/**
	 * 上个月客流总额
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getLastMonthPFTotal(String storeId) throws Exception {
		Object[] args = { storeId };
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
	public List<BaseReport> getLastYearDayList(String storeId) throws Exception {
		return super.queryForList(GET_LASTYEAR_DAY_LIST, BASE_REPORT_RM, storeId);
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
	public List<BaseReport> getLastYearMonthList(String storeId) throws Exception {
		return super.queryForList(GET_LASTYEAR_MONTH_LIST, BASE_REPORT_RM, storeId);
	}
}
