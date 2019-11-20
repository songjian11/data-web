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
 * 全司下所有省份首页各项指数
 * 
 * @author wells
 * @date 2019年1月6日
 */
@Repository
public class ProvinceProgressDao extends AbstractDao {
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
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and (company.COM_ID<>'ALL' or company.COM_NAME='整店')";

	// 小店下所有大类历史销售
	private static final String GET_HISTORY_DEPT_SALE = "select to_char(history.SALE_DATE,'yyyy-mm-dd') AS time,history.SALE_VALUE AS value,company.DEPT_ID as org_id "
			+ ",company.DEPT_ID as org_name, "
			+ "(history.SALE_VALUE-history.SALE_COST+history.INVADJ_COST-history.WAC+history.FUND_AMOUNT) "
			+ "as gp_value ,history.sale_cost,store.STORE_ID,COMPANY.COM_ID "
			+ "from CSMB_DEPT_SALES_HISTORY history left join CSMB_COMPANY company "
			+ "on company.STORE_ID=HISTORY.STORE_ID and COMPANY.DEPT_ID=HISTORY.DEPT_ID "
			+ "left join CSMB_STORE store on history.STORE_ID=store.STORE_ID "
			+ "where to_char(history.SALE_DATE,'yyyy-mm-dd')>=? "
			+ "and to_char(history.SALE_DATE,'yyyy-mm-dd')<=?  and (company.COM_ID<>'ALL' or company.COM_NAME='整店')";
	// 小店分配比例
	private static final String GET_COM_DISTRIBUTION_CNF = "select d.PROVINCE_ID,sg.store_id,d.com_id,d.D_VALUES as value  "
			+ "from (SELECT G.*,S.PROVINCE_ID FROM CSMB_STORE_GROUP G LEFT JOIN CSMB_STORE S  "
			+ "ON G.STORE_ID=S.STORE_ID) sg left join CSMB_DISTRIBUTION_VALUES d  on d.group_id=sg.group_id "
			+ "AND d.PROVINCE_ID=sg.province_Id";
	// 查询大类库存
	private static final String GET_DEPT_STOCK = "select stock.*,company.com_id from CSMB_DEPT_STOCK stock "
			+ "left join CSMB_STORE store on stock.STORE_ID=store.STORE_ID left join CSMB_COMPANY company "
			+ "on company.STORE_ID=stock.STORE_ID and COMPANY.DEPT_ID=stock.DEPT_ID "
			+ "where to_char(STOCK_DATE,'yyyy-mm-dd')=? and (company.COM_ID<>'ALL' or company.COM_NAME='整店')";
	// 查询大类标准周转天数
	private static final String GET_DEPT_TURNOVER_DAYS = "select * from CSMB_TURNOVER_DAYS where t_ym=?";

	/**
	 * 获取当日小店所有大类实时销售
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<BaseReportGpExt> getTodayDeptSale() throws Exception {
		return super.queryForList(GET_TODAY_DEPT_SALE, BASE_REPORT_GP_EXT_RM);
	}

	/**
	 * 小店下所有大类历史销售
	 * 
	 * @param beginYm
	 * @param endYm
	 * @return
	 * @throws Exception
	 */
	public List<BaseReportGpExt> getHistoryDeptSale(String beginYmd, String endYmd) throws Exception {
		return super.queryForList(GET_HISTORY_DEPT_SALE, BASE_REPORT_GP_EXT_RM, beginYmd, endYmd);
	}

	/**
	 * 小店分配比例
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<DistributionValues> getComDistributionCnf() throws Exception {
		return super.queryForList(GET_COM_DISTRIBUTION_CNF, DISTRIBUTION_VALUES_RM);
	}

	/**
	 * 查询大类库存
	 * 
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<DeptStock> getDeptStock(String ymd) throws Exception {
		return super.queryForList(GET_DEPT_STOCK, DEPT_STOCK_RM, ymd);
	}

	/**
	 * 查询大类标准周转天数
	 * 
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<TurnOverDays> getDeptTurnoverDays(String ym) throws Exception {
		return super.queryForList(GET_DEPT_TURNOVER_DAYS, TURN_OVERDAYS_RM, ym);
	}
}
