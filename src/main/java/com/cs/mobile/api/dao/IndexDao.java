package com.cs.mobile.api.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.util.StringUtils;
import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.Accounting;
import com.cs.mobile.api.model.partner.progress.CostVal;
import com.cs.mobile.api.model.partner.progress.ProgressReport;

@Repository
public class IndexDao extends AbstractDao {

	private static final RowMapper<CostVal> COST_RM = new BeanPropertyRowMapper<>(CostVal.class);

	private static final RowMapper<ProgressReport> INDEX_RM = new BeanPropertyRowMapper<>(ProgressReport.class);

	private static final RowMapper<Accounting> Account_RM = new BeanPropertyRowMapper<>(Accounting.class);

	/**
	 * 查询首页-销售（实际，目标）
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public ProgressReport getIndexSale(Map<String,Object> paramMap) throws Exception {
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		String yyyyMm = startDate.substring(0,7);//年月
		int days = Integer.valueOf(endDate.substring(8));//天数
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		StringBuffer sql = new StringBuffer();
		sql.append("select '销售' typeName,");
		sql.append("       nvl(t.actualVal,0)/10000 actualVal,");
		sql.append("       nvl(t.goalVal,0)/10000 goalVal,");
		sql.append("       nvl(t.goalCountVal,0)/10000 goalCountVal,");
		sql.append("       nvl(t.actualVal-t.goalVal,0)/10000 diffVal,");
		sql.append("       nvl(t.goalVal/t.goalCountVal,0)*100 goalRatio,");
		sql.append("       nvl(t.actualVal/t.goalCountVal,0)*100 actualRatio,");
		sql.append("       nvl((t.actualVal-t.goalVal)/t.goalCountVal,0)*100 diffRatio ");
		sql.append("from (  ");
		sql.append("    select nvl(sum(b.amt_ecl),0)+nvl(sum(a.sale_value),0) actualVal,");
		sql.append("           sum(g.sub_values)/to_number(to_char(last_day(to_date(?,'yyyy-MM-dd')),'dd'))*? goalVal,");//当前目标
		sql.append("           sum(g.sub_values) goalCountVal ");//总目标
		sql.append("      from CSMB_DEPT_SALES_HISTORY a ");//大类销售历史表
		sql.append("     inner join CSMB_COMPANY c on (a.dept_id = c.dept_id and a.store_id = c.store_id) ");//小店架构表（大店-小店-大类）
		sql.append("      left join CSMB_DEPT_SALES b on (a.dept_id = b.dept_id and a.store_id = b.store_id)");//大类销售实时表
		sql.append("      left join CSMB_GOAL g on (c.store_id = g.store_id and c.com_id = g.com_id and a.dept_id = g.dept_id and g.subject='销售' and g.goal_ym='"+yyyyMm+"')");//目标表-科目（销售）
		sql.append("     where a.sale_date between to_date(?,'yyyy/mm/dd') and to_date(?,'yyyy/mm/dd') ");
		sql.append("       and to_date(b.sale_date,'yyyy-MM-dd') between to_date(?,'yyyy/mm/dd') and to_date(?,'yyyy/mm/dd') ");
		sql.append("       and c.store_id = ? ");
		sql.append("       and c.com_id = ? ");
		sql.append(") t");
		return super.queryForObject(sql.toString(), INDEX_RM, endDate, days, startDate, endDate, startDate, endDate, storeId, comId);
	}

	/**
	 * 查询首页-前台毛利（实际，目标）
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public ProgressReport getIndexFontGp(Map<String,Object> paramMap) throws Exception {
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		String yyyyMm = startDate.substring(0,7);//年月
		int days = Integer.valueOf(endDate.substring(8));//天数
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		//实际前台毛利=销售金额（未税）-销售成本（未税）-券金额（未税）-损耗（未税）-成本调整（未税）-销售补差（未税）+未税未券扫描毛利
		StringBuffer sql = new StringBuffer();
		sql.append("select '前台毛利' typeName, ");
		sql.append("       nvl(t.actualVal,0)/10000 actualVal, ");
		sql.append("       nvl(t.goalVal,0)/10000 goalVal, ");
		sql.append("       nvl(t.goalCountVal,0)/10000 goalCountVal, ");
		sql.append("       nvl((t.actualVal-t.goalVal),0)/10000 diffVal, ");
		sql.append("       nvl(t.goalVal/t.goalCountVal,0)*100 goalRatio,");
		sql.append("       nvl(t.actualVal/t.goalCountVal,0)*100 actualRatio, ");
		sql.append("       nvl((t.actualVal-t.goalVal)/t.goalCountVal,0)*100 diffRatio ");
		sql.append("from (  ");
		sql.append("    select sum(nvl(a.sale_value,0))-sum(nvl(a.sale_cost,0))-sum(nvl(a.coupon_amt,0))+sum(nvl(a.invadj_cost,0))-sum(nvl(a.wac,0))+sum(nvl(a.fund_amount,0)) +sum(nvl(b.gp_ecl,0)) actualVal,");//实际前台毛利
		sql.append("           sum(g.sub_values)/to_number(to_char(last_day(to_date(?,'yyyy-MM-dd')),'dd'))*? goalVal, ");//目标毛利计算
		sql.append("           sum(g.sub_values) goalCountVal ");
		sql.append("      from CSMB_DEPT_SALES_HISTORY a ");//大类销售历史表
		sql.append("     inner join CSMB_COMPANY c on (a.dept_id = c.dept_id and a.store_id = c.store_id) ");//小店架构表（大店-小店-大类）
		sql.append("      left join CSMB_DEPT_SALES b on (a.dept_id = b.dept_id and a.store_id = b.store_id)");//大类销售实时表
		sql.append("      left join CSMB_GOAL g on (c.store_id = g.store_id and c.com_id=g.com_id and a.dept_id = g.dept_id and g.subject='前台毛利' and g.goal_ym='"+yyyyMm+"')");//目标表-科目(前台毛利)
		sql.append("     where a.sale_date between to_date(?,'yyyy/mm/dd') and to_date(?,'yyyy/mm/dd') ");
		sql.append("       and to_date(b.sale_date,'yyyy-MM-dd') between to_date(?,'yyyy/mm/dd') and to_date(?,'yyyy/mm/dd') ");
		sql.append("       and c.store_id = ? ");//大店ID
		sql.append("       and c.com_id = ? ");//ALL.大店，其他为小店
		sql.append(") t");
		return super.queryForObject(sql.toString(), INDEX_RM, endDate, days, startDate, endDate, startDate, endDate, storeId, comId);
	}

	/**
	 * 查询首页-后台毛利（实际，目标）
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public ProgressReport getIndexAfterGp(Map<String,Object> paramMap) throws Exception {
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		int days = Integer.valueOf(endDate.substring(8));//天数
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		//实际后台毛利=实际销售额*各大类固定后台收入率【用各大类的销售额*各大类的后台收入率再求和】
		BigDecimal afterGp = this.getCostActualAfterGp(paramMap);//实际后台毛利
		StringBuffer sql = new StringBuffer();
		sql.append("select '后台毛利' typeName, ");
		sql.append("       nvl(t.actualVal,0)/10000 actualVal, ");
		sql.append("       nvl(t.goalVal,0)/10000 goalVal, ");
		sql.append("       nvl(t.goalCountVal,0)/10000 goalCountVal, ");
		sql.append("       nvl((t.actualVal-t.goalVal),0)/10000 diffVal, ");
		sql.append("       nvl(t.goalVal/t.goalCountVal,0)*100 goalRatio,");
		sql.append("       nvl(t.actualVal/t.goalCountVal,0)*100 actualRatio, ");
		sql.append("       nvl((t.actualVal-t.goalVal)/t.goalCountVal,0)*100 diffRatio ");
		sql.append("from (  ");
		sql.append("    select "+afterGp+" actualVal,");//实际后台毛利
		sql.append("           sum(a.sub_values)/to_number(to_char(last_day(to_date(?,'yyyy-MM-dd')),'dd'))*? goalVal, ");//当前目标毛利计算
		sql.append("           sum(a.sub_values) goalCountVal ");//总目标毛利
		sql.append("      from CSMB_GOAL a ");//目标表-科目（后台毛利）
		sql.append("     where a.subject = '后台毛利' ");//后台毛利
		sql.append("       and a.goal_ym = ? ");//月份
		sql.append("       and a.store_id = ? ");//大店ID
		sql.append("       and a.com_id = ? ");//ALL.大店，其他为小店
		sql.append(") t");
		return super.queryForObject(sql.toString(), INDEX_RM, endDate, days, startDate.substring(0,7), storeId, comId);
	}

	/**
	 * 查询首页-费用（实际，目标）
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public ProgressReport getIndexCost(Map<String,Object> paramMap) throws Exception {
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		int days = Integer.valueOf(endDate.substring(8));//天数
		String yyyyMm = startDate.substring(0,7);//年月
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		//实际费用=当月已完结取考核表，当月未完结取目标表
		StringBuffer sql = new StringBuffer();
		sql.append("select '费用' typeName,");
		sql.append("       nvl(t.actualVal,0)/10000 actualVal, ");
		sql.append("       nvl(t.goalVal,0)/10000 goalVal, ");
		sql.append("       nvl(t.goalCountVal,0)/10000 goalCountVal, ");
		sql.append("       nvl(t.actualVal-t.goalVal,0)/10000 diffVal,");
		sql.append("       nvl(t.goalVal/t.goalCountVal,0)*100 goalRatio,");
		sql.append("       nvl(t.actualVal/t.goalCountVal,0)*100 actualRatio,");
		sql.append("       nvl((t.actualVal-t.goalVal)/t.goalCountVal,0)*100 diffRatio ");
		sql.append("from (  ");
		sql.append("    select sum(nvl(r.sub_values,a.sub_values))/to_number(to_char(last_day(to_date(?,'yyyy-MM-dd')),'dd'))*? actualVal,");
		sql.append("           sum(a.sub_values)/to_number(to_char(last_day(to_date(?,'yyyy-MM-dd')),'dd'))*? goalVal, ");//当前目标
		sql.append("           sum(a.sub_values) goalCountVal ");//总目标
		sql.append("      from CSMB_GOAL a ");//目标表-科目（费用）
		sql.append("      left join CSMB_RESULT r on (a.store_id = r.store_id and a.com_id = r.com_id and r.result_ym='"+yyyyMm+"')");//考核表-科目（费用）
		sql.append("     where a.subject = '总费用' ");//费用
		sql.append("       and a.goal_ym = ? ");//月份
		sql.append("       and a.store_id = ? ");
		sql.append("       and a.com_id = ? ");
		sql.append(") t");
		return super.queryForObject(sql.toString(), INDEX_RM, endDate, days, endDate, days, yyyyMm, storeId, comId);
	}

	/**
	 * 查询首页-分配比例
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getIndexShare(Map<String,Object> paramMap) throws Exception {
		String groupId = String.valueOf(paramMap.get("groupId"));//店群ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		String[] param = {groupId, comId};
		//分享金额=超额利润（实际利润-目标利润）*分配比例
		StringBuffer sql = new StringBuffer();
		sql.append(" select max(a.d_values) ");
		sql.append("   from CSMB_DISTRIBUTION_VALUES a ");
		sql.append("  where a.group_id = ? ");
		sql.append("    and a.com_id = ? ");
		BigDecimal proportion = jdbcTemplate.queryForObject(sql.toString(), param , BigDecimal.class);
		return null == proportion ? new BigDecimal(0) : proportion;
	}

	//费用-招商（目标）
	public BigDecimal getCostGoalAttract(Map<String,Object> paramMap){
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		String days = endDate.substring(8);//天数
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		String[] param = {endDate, days, startDate.substring(0,7), storeId, comId};
		//招商-实时招商收入为0，历史招商收入从考核表获取
		StringBuffer sql = new StringBuffer();
		sql.append("select nvl(ROUND(sum(a.sub_values)/to_number(to_char(last_day(to_date(?,'yyyy-MM-dd')),'dd'))*?/10000,2),0) attract ");//每天等份*天数
		sql.append("  from csmb_result a ");//考核表-科目（招商）
		sql.append(" where a.subject = '招商' ");//招商
		sql.append("   and a.result_ym = ? ");//月份
		sql.append("   and a.store_id = ? ");
		sql.append("   and a.com_id = ? ");

		BigDecimal attract = jdbcTemplate.queryForObject(sql.toString(), param , BigDecimal.class);
		return null == attract ? new BigDecimal(0) : attract;
	}

	//费用-后台毛利（实际）
	public BigDecimal getCostActualAfterGp(Map<String,Object> paramMap){
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		StringBuffer sql = new StringBuffer();
		String[] param = {startDate, endDate,startDate, endDate, storeId, comId};
		sql.append("select nvl(ROUND(sum((nvl(b.amt,0)+nvl(a.sale_value,0))*d.in_values),2),0) actualVal ");
		sql.append("  from CSMB_DEPT_SALES_HISTORY a");//大类销售历史表
		sql.append(" inner join CSMB_COMPANY c on (a.dept_id = c.dept_id and a.store_id = c.store_id) ");//小店架构表（大店-小店-大类）
		sql.append("  left join CSMB_DEPT_SALES b on (c.store_id = b.store_id and a.dept_id = b.dept_id) ");//大类销售实时表
		sql.append("  left join CSMB_INCOME_VALUES d on a.dept_id = d.dept_id ");//后台收入率配置表
		sql.append(" where a.sale_date between to_date(?,'yyyy/mm/dd') and to_date(?,'yyyy/mm/dd') ");
		sql.append("   and to_date(b.sale_date,'yyyy-MM-dd') between to_date(?,'yyyy/mm/dd') and to_date(?,'yyyy/mm/dd') ");
		sql.append("   and c.store_id = ? ");//大店ID
		sql.append("   and c.com_id = ? ");//小店ID
		BigDecimal actualVal = jdbcTemplate.queryForObject(sql.toString(), param , BigDecimal.class);
		return null == actualVal ? new BigDecimal(0) : actualVal;
	}

	//费用-各项（人力，营业，折旧，水电，租赁，其他）（实际）
	public CostVal getCostActualList(Map<String,Object> paramMap){
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		int days = Integer.valueOf(endDate.substring(8));//天数
		String yyyyMm = startDate.substring(0,7);//年月
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("  nvl(t.manpower/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 manpower,");
		sql.append("  nvl(t.business/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 business,");
		sql.append("  nvl(t.depreciation/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 depreciation,");
		sql.append("  nvl(t.hydropower/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 hydropower,");
		sql.append("  nvl(t.lease/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 lease,");
		sql.append("  nvl(t.other/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 other ");
		sql.append("from ( ");
		sql.append("  select ");
		sql.append("    nvl(sum(decode(sign(instr(b.subject,'人力')-0),0,0,b.sub_values)),sum(decode(sign(instr(a.subject,'人力')-0),0,0,a.sub_values))) manpower,");
		sql.append("    nvl(sum(decode(sign(instr(b.subject,'其他')-0),0,0,b.sub_values)),sum(decode(sign(instr(a.subject,'其他')-0),0,0,a.sub_values))) business，");
		sql.append("    nvl(sum(decode(sign(instr(b.subject,'营业')-0),0,0,b.sub_values)),sum(decode(sign(instr(a.subject,'营业')-0),0,0,a.sub_values))) depreciation, ");
		sql.append("    nvl(sum(decode(sign(instr(b.subject,'折旧')-0),0,0,b.sub_values)),sum(decode(sign(instr(a.subject,'折旧')-0),0,0,a.sub_values))) hydropower, ");
		sql.append("    nvl(sum(decode(sign(instr(b.subject,'水电')-0),0,0,b.sub_values)),sum(decode(sign(instr(a.subject,'水电')-0),0,0,a.sub_values))) lease, ");
		sql.append("    nvl(sum(decode(sign(instr(b.subject,'租赁')-0),0,0,b.sub_values)),sum(decode(sign(instr(a.subject,'租赁')-0),0,0,a.sub_values))) other ");
		sql.append("   from CSMB_GOAL a ");//目标表-科目（人力，营业，折旧，水电，租赁，其他）
		sql.append("   left join csmb_result b on (a.store_id = b.store_id and a.com_id = b.com_id and b.result_ym='"+yyyyMm+"')");//考核表-科目（人力，营业，折旧，水电，租赁，其他）
		sql.append("  where a.goal_ym = ? ");//月份
		sql.append("    and a.store_id = ? ");//大店ID
		sql.append("    and a.com_id = ? ");//小店ID
		sql.append(") t ");
		return super.queryForObject(sql.toString(), COST_RM, yyyyMm, storeId, comId);
	}

	//费用-各项（人力，营业，折旧，水电，租赁，其他）（目标）
	public CostVal getCostGoalList(Map<String,Object> paramMap){
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		int days = Integer.valueOf(endDate.substring(8));//天数
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("  nvl(t.manpower/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 manpower,");
		sql.append("  nvl(t.business/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 business,");
		sql.append("  nvl(t.depreciation/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 depreciation,");
		sql.append("  nvl(t.hydropower/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 hydropower,");
		sql.append("  nvl(t.lease/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 lease,");
		sql.append("  nvl(t.other/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+",0)/10000 other ");
		sql.append("from ( ");
		sql.append(" select sum(decode(sign(instr(a.subject,'人力')-0),0,0,a.sub_values)) manpower,");
		sql.append("        sum(decode(sign(instr(a.subject,'其他')-0),0,0,a.sub_values)) business，");
		sql.append("        sum(decode(sign(instr(a.subject,'营业')-0),0,0,a.sub_values)) depreciation, ");
		sql.append("        sum(decode(sign(instr(a.subject,'折旧')-0),0,0,a.sub_values)) hydropower, ");
		sql.append("        sum(decode(sign(instr(a.subject,'水电')-0),0,0,a.sub_values)) lease, ");
		sql.append("        sum(decode(sign(instr(a.subject,'租赁')-0),0,0,a.sub_values)) other ");
		sql.append("   from CSMB_GOAL a ");//目标表-科目（人力，营业，折旧，水电，租赁，其他）
		sql.append("  where a.goal_ym = ? ");//月份
		sql.append("    and a.store_id = ? ");//大店ID
		sql.append("    and a.com_id = ? ");//小店ID
		sql.append(") t ");
		return super.queryForObject(sql.toString(), COST_RM, startDate.substring(0,7), storeId, comId);
	}

	//费用-库存（实际）
	public BigDecimal getCostActualStock(Map<String,Object> paramMap){
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		String yyyyMm = startDate.substring(0,7);//年月
		int days = Integer.valueOf(endDate.substring(8));//天数
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		StringBuffer sql = new StringBuffer();
		String[] param = {yyyyMm, startDate, endDate, storeId, comId};
		sql.append("select ROUND(sum((b.stock_money - a.sale_cost/"+days+"*d.days)*0.004)/10000,2) ");
		sql.append("  from CSMB_DEPT_SALES_HISTORY a ");//大类销售历史表
		sql.append(" inner join CSMB_COMPANY c on (a.dept_id = c.dept_id and a.store_id = c.store_id) ");//小店架构表（小店-大类）
		sql.append("  left join CSMB_DEPT_STOCK b on (a.store_id = b.store_id and a.dept_id = b.dept_id and b.stock_date='"+yyyyMm+"') ");//大类库存表
		sql.append("  left join CSMB_TURNOVER_DAYS d on (a.store_id = d.store_id and a.dept_id = d.dept_id and d.t_ym = ?) ");//标准周转天数配置表
		sql.append(" where a.sale_date between to_date(?,'yyyy/mm/dd') and to_date(?,'yyyy/mm/dd') ");
		sql.append("   and c.store_id = ? ");//大店ID
		sql.append("   and c.com_id = ? ");//小店ID
		BigDecimal actualVal = jdbcTemplate.queryForObject(sql.toString(), param , BigDecimal.class);
		return null == actualVal ? new BigDecimal(0) : actualVal;
	}

	//费用-库存（目标）
	public BigDecimal getCostGoalStock(Map<String,Object> paramMap){
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		String days = endDate.substring(8);//天数
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		StringBuffer sql = new StringBuffer();
		String[] param = {endDate, days, startDate.substring(0,7), storeId, comId};
		sql.append("select ROUND(sum(a.sub_values)/to_number(to_char(last_day(to_date(?,'yyyy-MM-dd')),'dd'))/10000,2)*? ");//每天等份*天数
		sql.append("  from CSMB_GOAL a ");//目标表-科目（库存）
		sql.append(" where a.subject = '库存' ");//库存
		sql.append("   and a.goal_ym = ? ");//月份
		sql.append("   and a.store_id = ? ");//大店ID
		sql.append("   and a.com_id = ? ");//小店ID
		BigDecimal attract = jdbcTemplate.queryForObject(sql.toString(), param , BigDecimal.class);
		return null == attract ? new BigDecimal(0) : attract;
	}

	//核算标准表格
	public List<Accounting> getAccountingList(Map<String,Object> paramMap) throws Exception {
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		String startDate = String.valueOf(paramMap.get("startDate"));//开始时间
		String endDate = String.valueOf(paramMap.get("endDate"));//结束时间
		String yyyyMm = startDate.substring(0,7);//年月
		String days = endDate.substring(8);//天数
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店
		BigDecimal proportion = this.getIndexShare(paramMap);//分配比例

		StringBuffer sql = new StringBuffer();
		String[] param = {startDate, endDate,startDate, endDate, storeId, comId};
		sql.append("select ");//月份
		sql.append("   t.ymonth,");//月份
		sql.append("   t.saleval/10000 saleval,");//销售
		sql.append("   t.attractval/10000 attractval,");//招商
		sql.append("   (t.acostval+t.stock)/10000 acostval,");//实际费用
		sql.append("   (t.fontgpval+t.aftergpval-t.acostval)/10000 aprofitval,");//实际利润
		sql.append("   (t.goalgpval-t.gcostval)/10000 gprofitval,");//目标利润
		sql.append("   ((t.fontgpval+t.aftergpval-t.acostval)-(t.goalgpval-t.gcostval))/10000 cprofitval,");//超额利润
		sql.append("   ((t.fontgpval+t.aftergpval-t.acostval)-(t.goalgpval-t.gcostval))*"+proportion+" shareval,");//分享金额=实际利润-目标利润*分享比例
		sql.append("   decode(t.saleval,0,0,((t.fontgpval+t.aftergpval-t.acostval)/t.saleval)*100) profitrate,");//实际利润率
		sql.append("   decode(t.saleval,0,0,(t.loss/t.saleval)*100) lossrate,");//损耗率
		sql.append("   decode(t.saleval,0,0,(t.fontgpval/t.saleval)*100) gprate ");//前台毛利率
		sql.append("from (");
		sql.append("     select ");
		sql.append("        to_char(a.sale_date,'yyyy-MM') ymonth,");//月份
		sql.append("        nvl(sum((p.stock_money - a.sale_cost/"+days+"*d.days)*0.004),0) stock,");//库存费用-实际
		sql.append("        nvl(sum(b.amt_ecl),0)+nvl(sum(a.sale_value),0) saleval,");//销售
		sql.append("        sum(zr.sub_values)/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+" attractval,");//招商
		sql.append("        sum(nvl(nvl(sr.sub_values,ga.sub_values),0))/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+" acostval,");//实际费用
		sql.append("        sum(nvl(ga.sub_values,0))/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+" gcostval,");//目标费用
		sql.append("        sum(a.sale_value)-sum(a.sale_cost)-sum(a.coupon_amt)+sum(a.invadj_cost)-sum(a.wac)+sum(a.fund_amount)+sum(nvl(b.gp_ecl,0)) fontgpval,");//前台毛利
		sql.append("        nvl(sum((nvl(b.amt,0)+nvl(a.sale_value,0))*h.in_values),0) aftergpval,");//后台毛利
		sql.append("        sum(nvl(gg.sub_values,0))/to_number(to_char(last_day(to_date('"+endDate+"','yyyy-MM-dd')),'dd'))*"+days+" goalgpval,");//目标毛利
		sql.append("        sum(a.INVADJ_COST) loss ");//损耗
		sql.append("   from CSMB_DEPT_SALES_HISTORY a");//大类销售历史表
		sql.append("   left join CSMB_DEPT_SALES b on (a.dept_id = b.dept_id and a.store_id = b.store_id) ");//大类销售实时表
		sql.append("   left join CSMB_INCOME_VALUES h on a.dept_id = h.dept_id ");//后台收入率配置表
		sql.append("  inner join CSMB_COMPANY c on (a.dept_id = c.dept_id and a.store_id = c.store_id) ");//小店架构表（大店-小店-大类）
		sql.append("   left join CSMB_DEPT_STOCK p on (a.store_id = p.store_id and a.dept_id = p.dept_id and p.stock_date='"+yyyyMm+"') ");//大类库存表
		sql.append("   left join CSMB_TURNOVER_DAYS d on (a.store_id = d.store_id and a.dept_id = d.dept_id and d.t_ym = '"+yyyyMm+"') ");//标准周转天数配置表
		sql.append("   left join CSMB_GOAL ga on (c.store_id = ga.store_id and c.com_id = ga.com_id and c.dept_id = ga.dept_id and ga.subject in ('人力','营业','折旧','水电','租赁','其他','库存') and ga.goal_ym='"+yyyyMm+"')");//目标表-科目（总费用）
		sql.append("   left join CSMB_GOAL gg on (c.store_id = gg.store_id and c.com_id = gg.com_id and c.dept_id = gg.dept_id and gg.subject='总毛利' and gg.goal_ym='"+yyyyMm+"')");//目标表-科目（总毛利）
		sql.append("   left join csmb_result zr on (c.store_id = zr.store_id and c.com_id = zr.com_id and c.dept_id = zr.dept_id and zr.subject='招商' and zr.result_ym='"+yyyyMm+"')");//考核表-科目（招商）
		sql.append("   left join csmb_result sr on (c.store_id = sr.store_id and c.com_id = sr.com_id and c.dept_id = sr.dept_id and sr.subject in ('人力','营业','折旧','水电','租赁','其他') and sr.result_ym='"+yyyyMm+"')");//考核表-科目（总费用）
		sql.append("   where a.sale_date between to_date(?,'yyyy/mm/dd') and to_date(?,'yyyy/mm/dd') ");//时间条件
		sql.append("     and to_date(b.sale_date,'yyyy-MM-dd') between to_date(?,'yyyy/mm/dd') and to_date(?,'yyyy/mm/dd') ");
		sql.append("     and c.store_id = ? ");//大店ID
		sql.append("     and c.com_id = ? ");//小店ID
		sql.append("   group by to_char(a.sale_date,'yyyy-MM') ");//月份分组
		sql.append(") t");
		return super.queryForList(sql.toString(), Account_RM, param);
	}
}
