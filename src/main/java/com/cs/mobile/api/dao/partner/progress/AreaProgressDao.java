package com.cs.mobile.api.dao.partner.progress;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.battle.BaseReportGpExt;
import com.cs.mobile.api.model.partner.progress.DeptStock;
import com.cs.mobile.api.model.partner.progress.DistributionValues;
import com.cs.mobile.api.model.partner.progress.TurnOverDays;

/**
 * 省份下所有区域首页各项指数
 * 
 * @author wells
 * @date 2019年1月6日
 */
@Repository
public class AreaProgressDao extends AbstractDao {
	private static final RowMapper<BaseReportGpExt> BASE_REPORT_GP_EXT_RM = new BeanPropertyRowMapper<>(
			BaseReportGpExt.class);
	private static final RowMapper<DeptStock> DEPT_STOCK_RM = new BeanPropertyRowMapper<>(DeptStock.class);
	private static final RowMapper<TurnOverDays> TURN_OVERDAYS_RM = new BeanPropertyRowMapper<>(TurnOverDays.class);
	private static final RowMapper<DistributionValues> DISTRIBUTION_VALUES_RM = new BeanPropertyRowMapper<>(
			DistributionValues.class);
	// 当日小店所有大类实时销售
	private static final String GET_TODAY_DEPT_SALE = "select sale.SALE_HOUR AS time,sale.DEPT_ID as org_id,sale.DEPT_ID as org_name, "
			+ "sale.AMT_ECL AS value,sale.GP_ECL as gp_value,store.STORE_ID,COMPANY.COM_ID "
			+ "from CSMB_DEPT_SALES sale left join CSMB_COMPANY company "
			+ "on company.STORE_ID=sale.STORE_ID and COMPANY.DEPT_ID=sale.DEPT_ID "
			+ "left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE "
			+ "and (company.COM_ID<>'ALL' or company.COM_NAME='整店') and store.PROVINCE_ID=?";

	// 小店下所有大类历史销售
	private static final String GET_HISTORY_DEPT_SALE = "select to_char(history.SALE_DATE,'yyyy-mm-dd') AS time,history.SALE_VALUE AS value,company.DEPT_ID as org_id "
			+ ",company.DEPT_ID as org_name, "
			+ "(history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "as gp_value ,history.sale_cost,store.STORE_ID,COMPANY.COM_ID "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_COMPANY company "
			+ "on company.STORE_ID=HISTORY.STORE_ID and COMPANY.DEPT_ID=HISTORY.DEPT_ID "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')>=? " + "and to_char(history.SALE_DATE,'yyyy-mm-dd')<=? "
			+ "and (company.COM_ID<>'ALL' or company.COM_NAME='整店') and store.PROVINCE_ID=?";
	// 小店分配比例
	private static final String GET_COM_DISTRIBUTION_CNF = "select g.store_id,d.com_id,d.D_VALUES as value "
			+ "from CSMB_STORE_GROUP g left join CSMB_DISTRIBUTION_VALUES d on d.group_id=g.group_id "
			+ "left join CSMB_STORE store on g.STORE_ID=store.STORE_ID where store.PROVINCE_ID=?";
	// 查询大类库存
	private static final String GET_DEPT_STOCK = "select stock.*,company.com_id from CSMB_DEPT_STOCK stock "
			+ "left join CSMB_STORE store on stock.STORE_ID=store.STORE_ID left join CSMB_COMPANY company "
			+ "on company.STORE_ID=stock.STORE_ID and COMPANY.DEPT_ID=stock.DEPT_ID "
			+ "where to_char(STOCK_DATE,'yyyy-mm-dd')=? and store.PROVINCE_ID=? and (company.COM_ID<>'ALL' or company.COM_NAME='整店')";
	// 查询大类标准周转天数
	private static final String GET_DEPT_TURNOVER_DAYS = "select td.* from CSMB_TURNOVER_DAYS td "
			+ "left join CSMB_STORE store on td.STORE_ID=store.STORE_ID where t_ym=? and store.PROVINCE_ID=?";

	/**
	 * 获取当日小店所有大类实时销售
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 */
	public List<BaseReportGpExt> getTodayDeptSale(String provinceId) throws Exception {
		return super.queryForList(GET_TODAY_DEPT_SALE, BASE_REPORT_GP_EXT_RM, provinceId);
	}

	/**
	 * 小店下所有大类历史销售
	 * 
	 * @param provinceId
	 * @param beginYm
	 * @param endYm
	 * @return
	 * @throws Exception
	 */
	public List<BaseReportGpExt> getHistoryDeptSale(String provinceId, String beginYmd, String endYmd)
			throws Exception {
		return super.queryForList(GET_HISTORY_DEPT_SALE, BASE_REPORT_GP_EXT_RM, beginYmd, endYmd, provinceId);
	}

	/**
	 * 小店分配比例
	 * 
	 * @param groupId
	 * @param comId
	 * @return
	 * @throws Exception
	 */
	public List<DistributionValues> getComDistributionCnf(String provinceId) throws Exception {
		return super.queryForList(GET_COM_DISTRIBUTION_CNF, DISTRIBUTION_VALUES_RM, provinceId);
	}

	/**
	 * 查询大类库存
	 * 
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<DeptStock> getDeptStock(String provinceId, String ymd) throws Exception {
		return super.queryForList(GET_DEPT_STOCK, DEPT_STOCK_RM, ymd, provinceId);
	}

	/**
	 * 查询大类标准周转天数
	 * 
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<TurnOverDays> getDeptTurnoverDays(String provinceId, String ym) throws Exception {
		return super.queryForList(GET_DEPT_TURNOVER_DAYS, TURN_OVERDAYS_RM, ym, provinceId);
	}
}
