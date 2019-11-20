package com.cs.mobile.api.dao.partner.progress;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.BaseReportGpExt;
import com.cs.mobile.api.model.partner.progress.DeptStock;
import com.cs.mobile.api.model.partner.progress.TurnOverDays;

/**
 * 大店及小店首页各项指标
 * 
 * @author wells
 * @date 2019年1月6日
 */
@Repository
public class ComProgressDao extends AbstractDao {
	private static final RowMapper<BaseReportGpExt> BASE_REPORT_GP_EXT_RM = new BeanPropertyRowMapper<>(
			BaseReportGpExt.class);
	private static final RowMapper<DeptStock> DEPT_STOCK_RM = new BeanPropertyRowMapper<>(DeptStock.class);
	private static final RowMapper<TurnOverDays> TURN_OVERDAYS_RM = new BeanPropertyRowMapper<>(TurnOverDays.class);
	// 当日小店所有大类实时销售
	private static final String GET_TODAY_DEPT_SALE = "select sale.SALE_HOUR AS time,sale.DEPT_ID "
			+ "as org_id,sale.DEPT_ID as org_name, "
			+ "sale.AMT_ECL AS value,sale.GP_ECL as gp_value from CSMB_DEPT_SALES sale "
			+ "left join CSMB_COMPANY company on company.STORE_ID=sale.STORE_ID and COMPANY.DEPT_ID=sale.DEPT_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and company.COM_ID=? and sale.store_id=?";

	// 小店下所有大类历史销售
	private static final String GET_HISTORY_DEPT_SALE = "select to_char(history.SALE_DATE,'yyyy-mm-dd') "
			+ "AS time,history.SALE_VALUE AS value,company.DEPT_ID as org_id ,company.DEPT_ID as org_name, "
			+ "(history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "as gp_value ,history.sale_cost from CSMB_DEPT_SALES_HISTORY history left join CSMB_COMPANY company "
			+ "on company.STORE_ID=HISTORY.STORE_ID and COMPANY.DEPT_ID=HISTORY.DEPT_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')>=? and to_char(history.SALE_DATE,'yyyy-mm-dd')<=? "
			+ "and company.COM_ID=? and company.store_id=?";
	// 小店分配比例
	private static final String GET_COM_DISTRIBUTION_CNF = "select nvl(max(D_VALUES),0) from CSMB_DISTRIBUTION_VALUES "
			+ "where group_id=? and com_id=? and province_id=(select province_id from CSMB_STORE where STORE_ID=?) ";
	// 查询大类库存
	private static final String GET_DEPT_STOCK = "select stock.* from CSMB_DEPT_STOCK stock "
			+ "left join CSMB_COMPANY company "
			+ "on company.STORE_ID=stock.STORE_ID and COMPANY.DEPT_ID=stock.DEPT_ID "
			+ "where to_char(stock.STOCK_DATE,'yyyy-mm-dd')=? and stock.STORE_ID=?  and company.com_id=? ";
	// 查询大类标准周转天数
	private static final String GET_DEPT_TURNOVER_DAYS = "select * from CSMB_TURNOVER_DAYS where t_ym=? and STORE_ID=? ";

	/**
	 * 获取当日小店所有大类实时销售
	 * 
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 */
	public List<BaseReportGpExt> getTodayDeptSale(String storeId, String comId) throws Exception {
		return super.queryForList(GET_TODAY_DEPT_SALE, BASE_REPORT_GP_EXT_RM, comId, storeId);
	}

	/**
	 * 小店下所有大类历史销售
	 * 
	 * @param storeId
	 * @param comId
	 * @param beginYm
	 * @param endYm
	 * @return
	 * @throws Exception
	 */
	public List<BaseReportGpExt> getHistoryDeptSale(String storeId, String comId, String beginYmd, String endYmd)
			throws Exception {
		return super.queryForList(GET_HISTORY_DEPT_SALE, BASE_REPORT_GP_EXT_RM, beginYmd, endYmd, comId, storeId);
	}

	/**
	 * 小店分配比例 modify by wells 20190426 分配比例查询增加省份条件
	 * 
	 * @param storeId
	 * @param groupId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月26日
	 */
	public BigDecimal getComDistributionCnf(String storeId, String groupId, String comId) throws Exception {
		Object[] args = { groupId, comId, storeId };
		return jdbcTemplate.queryForObject(GET_COM_DISTRIBUTION_CNF, args, BigDecimal.class);
	}

	/**
	 * 查询大类库存
	 * 
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<DeptStock> getDeptStock(String storeId, String comId, String ymd) throws Exception {
		return super.queryForList(GET_DEPT_STOCK, DEPT_STOCK_RM, ymd, storeId, comId);
	}

	/**
	 * 查询大类标准周转天数
	 * 
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<TurnOverDays> getDeptTurnoverDays(String storeId, String ym) throws Exception {
		return super.queryForList(GET_DEPT_TURNOVER_DAYS, TURN_OVERDAYS_RM, ym, storeId);
	}
}
