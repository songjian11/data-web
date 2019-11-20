package com.cs.mobile.api.dao.mreport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.mreport.AreaGroupReport;
import com.cs.mobile.api.model.mreport.StoreDayDeptReport;
import com.cs.mobile.api.model.mreport.StoreDayTimeReport;
import com.cs.mobile.api.model.mreport.response.DateTitleResp;
import com.cs.mobile.api.model.mreport.response.PermeatioResp;

@Repository
public class MobileReportDao extends AbstractDao {
	private static final RowMapper<Organization> ORGANIZATION_RM = new BeanPropertyRowMapper<>(Organization.class);
	private static final RowMapper<DateTitleResp> DATE_TITLE_RM = new BeanPropertyRowMapper<>(DateTitleResp.class);
	private static final RowMapper<AreaGroupReport> AREA_GROUP_REPORT_RM = new BeanPropertyRowMapper<>(
			AreaGroupReport.class);
	private static final RowMapper<StoreDayTimeReport> DAY_TIME_REPORT_RM = new BeanPropertyRowMapper<>(
			StoreDayTimeReport.class);
	private static final RowMapper<StoreDayDeptReport> DAY_DEPT_REPORT_RM = new BeanPropertyRowMapper<>(
			StoreDayDeptReport.class);

	// 获取所有省份
	private static final String GET_ALL_PROVINCE = "select 省份  as org_id ,省份  as org_name from csmb_CHYVI_FTP_ZTKJGWXKL_ZHLS group by 省份";
	// 根据省份获取所有区域
	private static final String GET_AREA_BY_P = "select 区域 as org_id,  区域 as org_name from csmb_CHYVI_FTP_ZTKJGWXKL_ZHLS where 省份=?  group by 区域";
	// 查询日期标题
	private static final String GET_DATE_TITLE_RESP = "select distinct 日期 as cur_date, 去年日期 as last_year_date, 去年月日期 as last_year_begin_date, "
			+ "去年月至今 as last_year_end_date from csmb_WZ_CBGK_ZB ";

	//
	private static final String GET_AREA_GROUP_REPORT = "select  "
			+ "      decode( 大战区||'战区','战区','总计', 大战区||'战区') as area_group_name, "
			+ "       decode( 大战区||'战区','战区','总计', nvl(区域,大战区||'战区合计')) as area_name,         "
			+ "    decode(sum(昨日销售额),0 ,null,  to_char( sum(昨日毛利额) / "
			+ "       sum(昨日销售额)*100,'fm999990.0')||'%')  as yesterday_front_gp_ratio,         "
			+ "       decode(sum(去年昨日销售额),0 ,null,    to_char( sum(去年昨日毛利额) / "
			+ "       sum(去年昨日销售额)*100,'fm999990.0')||'%')  as last_year_day_front_gp_ratio,         "
			+ "        to_char( decode(sum(昨日销售额),0 ,null, (sum(昨日毛利额) / "
			+ "       sum(昨日销售额))-  decode(sum(去年昨日销售额),0 ,null,sum(去年昨日毛利额) / "
			+ "       sum(去年昨日销售额)))*100,'fm999990.0')||'%'  as day_gp_rise,         "
			+ "    decode(sum(月至今销售额),0 ,null,   to_char(    sum(月至今毛利额) / "
			+ "       sum(月至今销售额)*100,'fm999990.0')||'%')  as front_gp_ratio,         "
			+ "        decode(sum(去年月至今销售额),0 ,null,  to_char(  sum(去年月至今毛利额) / "
			+ "       sum(去年月至今销售额)*100,'fm999990.0')||'%')  as last_year_front_gp_ratio,         "
			+ "      to_char(     ( decode(sum(月至今销售额),0 ,null, sum(月至今毛利额) / "
			+ "       sum(月至今销售额))-decode(sum(去年月至今销售额),0 ,null,sum(去年月至今毛利额) / "
			+ "       sum(去年月至今销售额)))*100,'fm999990.0')||'%'  as month_gp_rise         "
			+ "  from csmb_WZ_CBGK_ZB group by                rollup(大战区,区域)";

	private static final String GET_DAY_TIME_REPORT = "select substr(SD0108, 1, 2) time,  "
			+ "sum(AMT) sale_value_in, sum(AMT / (1 + DG0133 / 100)) sale_value,  "
			+ "count(distinct sd0101 || sd0106 || sd0109) pf_count  FROM sd001, dg001 "
			+ "where sd0102 = dg0101 and dg0104 <> 9101 and sd0101 = ? "
			+ "group by substr(SD0108, 1, 2) order by time";

	private static final String GET_DAY_DEPT_REPORT = "with t1 as "
			+ " (select count(distinct l.SALEDATE || l.BILLNO || l.POSID) total_kl "
			+ "    from xl_dy_saledetail_v l    where SALEDATE = ? "
			+ "     and dept <> 9101) ,    t2 as ( select dept dept,        sum(case "
			+ "             when SALEDATE < to_char(sysdate-1/24, 'yyyymmdd') then               0 "
			+ "             else               l.AMT            end) sale_value_in,  round(sum(case "
			+ "                   when SALEDATE < to_char(sysdate-1/24, 'yyyymmdd') then "
			+ "                    0                    else                     l.AMT_ECL "
			+ "                 end),              2) sale_value,        round(sum(case "
			+ "                   when SALEDATE < to_char(sysdate-1/24, 'yyyymmdd') then "
			+ "                    0                    else                     l.gp "
			+ "                 end),              2) sgp,        round(sum(case "
			+ "                   when SALEDATE < to_char(sysdate-1/24, 'yyyymmdd') then "
			+ "                    0                    else                     l.gp_ecl "
			+ "                 end),              2) sgp_ecl,         "
			+ "       to_char(count(distinct l.SALEDATE || l.BILLNO || l.POSID) / "
			+ "               max(total_kl) * 100, "
			+ "               'fm999999999990.09') || '%' permeation_ratio   from xl_dy_saledetail_v l, t1 "
			+ " where l.SALEDATE = ?    and dept <> 9101  group by dept), t3 as (select b.dg0104 dept, "
			+ "                               sum(SALE_VALUE_IN) sale_value_in, "
			+ "                               sum(SALE_VALUE) sale_value, "
			+ "                               sum(SALE_VALUE_IN - a.sale_cost_in + "
			+ "                                   a.fund_amount_in + a.invadj_cost_in - "
			+ "                                   a.wac_in) front_gp_in, "
			+ "                               sum(SALE_VALUE - a.sale_cost + a.fund_amount + "
			+ "                                   a.invadj_cost - a.wac) front_gp, "
			+ "                               decode(sum(a.SALE_VALUE), "
			+ "                                      0,                                       null, "
			+ "                                      to_char(sum(SALE_VALUE - a.sale_cost + "
			+ "                                                  a.fund_amount + "
			+ "                                                  a.invadj_cost - a.wac) / "
			+ "                                              sum(SALE_VALUE), "
			+ "                                              'fm999999999990.09') || '%') front_gp_ratio "
			+ "                          from rpt_daily_gp a, dg001 b "
			+ "                         where a.item = b.dg0101 " + "                           and a.saledate = ?  "
			+ "                         group by dg0104)  select nvl(t2.dept, t3.dept) dept, "
			+ "       nvl(t2.sale_value_in, 0) + nvl(t3.sale_value_in, 0) sale_value_in, "
			+ "       nvl(t2.sale_value, 0) + nvl(t3.sale_value, 0) sale_value, "
			+ "       nvl(t2.sgp, 0) + nvl(t3.front_gp_in, 0) gp_in, "
			+ "       nvl(t2.sgp_ecl, 0) + nvl(t3.front_gp, 0) gp, "
			+ "       decode((nvl(t2.sale_value, 0) + nvl(t3.sale_value, 0)),               0, "
			+ "              null,               to_char((nvl(t2.sgp_ecl, 0) + nvl(t3.front_gp, 0)) / "
			+ "                      (nvl(t2.sale_value, 0) + nvl(t3.sale_value, 0)), "
			+ "                      'fm999999999990.09') || '%') gp_ratio,        permeation_ratio "
			+ "  from t2   full join t3     on t2.dept = t3.dept order by 1";

	/**
	 * 查询日期标题
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月29日
	 */
	public DateTitleResp getDateTitleResp() throws Exception {
		return super.queryForObject(GET_DATE_TITLE_RESP, DATE_TITLE_RM);
	}

	/**
	 * 获取大区战报
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月29日
	 */
	public List<AreaGroupReport> getAreaGroupReport() throws Exception {
		return super.queryForList(GET_AREA_GROUP_REPORT, AREA_GROUP_REPORT_RM);
	}

	/**
	 * 分页查询渗透率数据
	 * 
	 * @param provinceId
	 * @param areaId
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月29日
	 */
	public PageResult<PermeatioResp> getPermeatioResp(String provinceId, String areaId, int page, int pageSize)
			throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		sql.append(
				"select 省份 as province_name,区域 as area_name,门店编码 as store_id,门店名称 as store_name,当日整体客流 as day_all_kl, "
						+ "当日扫码购客流 as day_scan_kl,当日微信自助收银客流 as day_wx_kl,当日扫码购渗透率 as day_scan_permeation, "
						+ "当日微信自助渗透率 as day_wx_permeation,  "
						+ "当日智慧收银渗透率 as day_smart_permeation,月累计整体客流 as month_all_kl,月累计扫码购客流 as month_scan_kl, "
						+ "月累微信自助收银客流 as month_wx_kl,月累计扫码购渗透率 as month_scan_permeation,月累计微信自助渗透率 as month_wx_permeation, "
						+ "月累计智慧收银渗透率 as month_smart_permeation from csmb_CHYVI_FTP_ZTKJGWXKL_ZHLS where 1=1 ");
		if (StringUtils.isNotEmpty(provinceId) && !"0".equals(provinceId)) {
			sql.append(" AND 省份=? ");
			params.add(provinceId);
		}
		if (StringUtils.isNotEmpty(areaId) && !"0".equals(areaId)) {
			sql.append(" AND 区域=? ");
			params.add(areaId);
		}
		return super.queryByPage(sql.toString(), PermeatioResp.class, page, pageSize, "省份", params.toArray());
	}

	/**
	 * 获取所有省份
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月29日
	 */
	public List<Organization> getAllProvince() throws Exception {
		return super.queryForList(GET_ALL_PROVINCE, ORGANIZATION_RM);
	}

	/**
	 * 根据省份获取所有区域
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月29日
	 */
	public List<Organization> getAreaByP(String provinceId) throws Exception {
		return super.queryForList(GET_AREA_BY_P, ORGANIZATION_RM, provinceId);
	}

	/**
	 * 查询门店日时段报表
	 * 
	 * @param ymd
	 * @return
	 * @throws Exception
	 */
	public List<StoreDayTimeReport> getDayTimeReport(String ymd) throws Exception {
		return super.queryForList(GET_DAY_TIME_REPORT, DAY_TIME_REPORT_RM, ymd);
	}

	/**
	 * 查询门店日大类时段报表
	 * 
	 * @param ymd
	 * @return
	 * @throws Exception
	 */
	public List<StoreDayDeptReport> getDayDeptReport(String ymd) throws Exception {
		return super.queryForList(GET_DAY_DEPT_REPORT, DAY_DEPT_REPORT_RM, ymd, ymd, ymd);
	}

}
