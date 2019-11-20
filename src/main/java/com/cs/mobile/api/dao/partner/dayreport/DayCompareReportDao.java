package com.cs.mobile.api.dao.partner.dayreport;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.dayreport.DeptSale;
import com.cs.mobile.api.model.partner.dayreport.TimeSale;

/**
 * 当日比较报表
 * 
 * @author wells
 * @date 2019年3月14日
 */
@Repository
public class DayCompareReportDao extends AbstractDao {
	private static final RowMapper<DeptSale> DEPT_SALE_RM = new BeanPropertyRowMapper<>(DeptSale.class);
	private static final RowMapper<TimeSale> TIME_SALE_RM = new BeanPropertyRowMapper<>(TimeSale.class);
	// 历史某天，全司的时段客流、时段销售
	private static final String GET_ALL_H_TIME_SALE = "SELECT /*+PARALLEL(a,16)*/  substr(tran_datetime, 1, 2) as time, "
			+ "       count(distinct tran_seq_no) as pf_value,        sum(a.total_real_amt) as sale_value "
			+ "  FROM zypp.sale_item a, zypp.inf_store b  where a.business_date = to_date(?,'yyyy-mm-dd') "
			+ "   and a.store = b.store "
			+ "	 and a.store in(select store_id from zypp.inf_csmb_company group by store_id) "
			+ "group by substr(tran_datetime,1,2)";
	// 历史某天，某省份的时段客流、时段销售
	private static final String GET_PROVINCE_H_TIME_SALE = "SELECT /*+PARALLEL(a,16)*/  substr(tran_datetime, 1, 2) as time, "
			+ "       count(distinct tran_seq_no) as pf_value,        sum(a.total_real_amt) as sale_value "
			+ "  FROM zypp.sale_item a, zypp.inf_store b  where a.business_date = to_date(?,'yyyy-mm-dd') "
			+ "   and a.store = b.store    and b.area = ? "
			+ "	 and a.store in(select store_id from zypp.inf_csmb_company group by store_id) "
			+ "group by substr(tran_datetime,1,2)";
	// 历史某天，某区域的时段客流、时段销售
	private static final String GET_AREA_H_TIME_SALE = "SELECT /*+PARALLEL(8)*/  substr(tran_datetime, 1, 2) as time, "
			+ "       count(distinct tran_seq_no) as pf_value,        sum(a.total_real_amt) as sale_value "
			+ "  FROM zypp.sale_item a, zypp.inf_store b  where a.business_date = to_date(?,'yyyy-mm-dd') "
			+ "   and a.store = b.store    and b.region = ? group by substr(tran_datetime,1,2)";
	// 历史某天，某门店的时段客流、时段销售
	private static final String GET_STORE_H_TIME_SALE = "SELECT /*+PARALLEL(8)*/  substr(tran_datetime, 1, 2) as time, "
			+ "       count(distinct tran_seq_no) as pf_value,        sum(a.total_real_amt) as sale_value "
			+ "  FROM zypp.sale_item a  where a.business_date = to_date(?,'yyyy-mm-dd')    and a.store=? "
			+ "group by substr(tran_datetime,1,2)";
	// 历史某天，某小店的时段客流、时段销售
	private static final String GET_COM_H_TIME_SALE = "SELECT  /*+PARALLEL(8)*/ substr(tran_datetime, 1, 2) as time, "
			+ "       count(distinct tran_seq_no) as pf_value,        sum(a.total_real_amt) as sale_value "
			+ "  FROM zypp.sale_item a, zypp.inf_csmb_company b,zypp.inf_item c "
			+ " where a.business_date = to_date(?,'yyyy-mm-dd')   and a.store = b.store_id and a.item=c.item  "
			+ "  and b.dept_id=c.dept   and b.com_id=?    and a.store=? " + "group by substr(tran_datetime,1,2)";

	// 当日全司实时客流、实时销售
	private static final String GET_ALL_TIME_SALE = "select SALE_HOUR as time,sum(KL) as pf_value,sum(NVL(AMT_ECL,0)) as sale_value "
			+ "from CSMB_COM_SALES where to_char(sysdate,'yyyymmdd')=SALE_DATE and COM_ID='ALL' "
			+ "group by SALE_HOUR order by time ";
	// 当日某个省份实时客流、实时销售
	private static final String GET_PROVINCE_TIME_SALE = "select sale.SALE_HOUR as time,sum(sale.KL) as pf_value,sum(NVL(sale.AMT_ECL,0)) as sale_value "
			+ "from CSMB_COM_SALES sale  left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=SALE_DATE  and store.PROVINCE_ID=? and sale.COM_ID='ALL' "
			+ "group by SALE_HOUR order by time";
	// 当日某个区域实时客流、实时销售
	private static final String GET_AREA_TIME_SALE = "select sale.SALE_HOUR as time,sum(sale.KL) as pf_value,sum(NVL(sale.AMT_ECL,0)) as sale_value "
			+ "from CSMB_COM_SALES sale  left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=SALE_DATE  and store.AREA_ID=? and sale.COM_ID='ALL' "
			+ "group by SALE_HOUR order by time";
	// 当日某个大店实时客流、实时销售
	private static final String GET_STORE_TIME_SALE = "select sale.SALE_HOUR as time,sum(sale.KL) as pf_value,sum(NVL(sale.AMT_ECL,0)) as sale_value "
			+ "from CSMB_COM_SALES sale  left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=SALE_DATE  and store.store_id=? and sale.COM_ID='ALL' "
			+ "group by SALE_HOUR order by time";
	// 当日某个小店实时客流、实时销售
	private static final String GET_COM_TIME_SALE = "select sale.SALE_HOUR as time,sum(sale.KL) as pf_value,sum(NVL(sale.AMT_ECL,0)) as sale_value "
			+ "from CSMB_COM_SALES sale  left join CSMB_STORE store on sale.STORE_ID=store.STORE_ID "
			+ "where to_char(sysdate,'yyyymmdd')=SALE_DATE  and store.store_id=? and sale.COM_ID=? "
			+ "group by SALE_HOUR order by time";

	// 全司实时销售、去年今日历史销售
	private static final String GET_ALL_DEPT_SALE = "select t.dept_Id,t.dept_id as dept_name,sum(t.cur_value) as cur_value,sum(nvl(history.sale_value,0)) as history_value from "
			+ "(select sale.DEPT_ID,sale.store_id, sum(nvl(sale.AMT_ECL,0)) AS cur_value "
			+ "from CSMB_DEPT_SALES sale  left join CSMB_COMPANY company "
			+ "on company.STORE_ID=sale.STORE_ID and COMPANY.DEPT_ID=sale.DEPT_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE group by sale.dept_id,sale.store_Id)t  "
			+ "left join CSMB_DEPT_SALES_HISTORY history "
			+ "on t.store_id=history.store_id and t.dept_id=history.dept_id where history.sale_date=to_date(?,'yyyy-mm-dd') "
			+ "group by t.dept_Id";
	// 某个省份实时销售、去年今日历史销售
	private static final String GET_PROVINCE_DEPT_SALE = "select t.dept_Id,t.dept_id as dept_name,sum(t.cur_value)  as cur_value,sum(nvl(history.sale_value,0)) as history_value from "
			+ "(select sale.DEPT_ID,sale.store_id, sum(nvl(sale.AMT_ECL,0)) AS cur_value "
			+ "from CSMB_DEPT_SALES sale  left join CSMB_COMPANY company "
			+ "on company.STORE_ID=sale.STORE_ID and COMPANY.DEPT_ID=sale.DEPT_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE "
			+ "and sale.store_id in(select store_id from CSMB_STORE where province_id=?) "
			+ "group by sale.dept_id,sale.store_Id)t  left join CSMB_DEPT_SALES_HISTORY history "
			+ "on t.store_id=history.store_id and t.dept_id=history.dept_id where history.sale_date=to_date(?,'yyyy-mm-dd') "
			+ "group by t.dept_Id";
	// 某个区域实时销售、去年今日历史销售
	private static final String GET_AREA_DEPT_SALE = "select t.dept_Id,t.dept_id as dept_name,sum(t.cur_value) as cur_value,sum(nvl(history.sale_value,0)) as history_value from "
			+ "(select sale.DEPT_ID,sale.store_id, sum(nvl(sale.AMT_ECL,0)) AS cur_value "
			+ "from CSMB_DEPT_SALES sale  left join CSMB_COMPANY company "
			+ "on company.STORE_ID=sale.STORE_ID and COMPANY.DEPT_ID=sale.DEPT_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE "
			+ "and sale.store_id in(select store_id from CSMB_STORE where area_id=?) "
			+ "group by sale.dept_id,sale.store_Id)t  left join CSMB_DEPT_SALES_HISTORY history "
			+ "on t.store_id=history.store_id and t.dept_id=history.dept_id where history.sale_date=to_date(?,'yyyy-mm-dd') "
			+ "group by t.dept_Id ";
	// 某个大店实时销售、去年今日历史销售
	private static final String GET_STORE_DEPT_SALE = "select t.dept_Id,t.dept_id as dept_name,t.cur_value,nvl(history.sale_value,0) as history_value from "
			+ "(select sale.DEPT_ID,sale.store_id, sum(nvl(sale.AMT_ECL,0)) AS cur_value "
			+ "from CSMB_DEPT_SALES sale  left join CSMB_COMPANY company "
			+ "on company.STORE_ID=sale.STORE_ID and COMPANY.DEPT_ID=sale.DEPT_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and sale.store_id=? "
			+ "group by sale.dept_id,sale.store_Id)t  left join CSMB_DEPT_SALES_HISTORY history "
			+ "on t.store_id=history.store_id and t.dept_id=history.dept_id where history.sale_date=to_date(?,'yyyy-mm-dd')";
	// 某个小店实时销售、去年今日历史销售
	private static final String GET_COM_DEPT_SALE = "select t.dept_Id,t.dept_id as dept_name,t.cur_value,nvl(history.sale_value,0) as history_value from "
			+ "(select sale.DEPT_ID,sale.store_id, sum(nvl(sale.AMT_ECL,0)) AS cur_value "
			+ "from CSMB_DEPT_SALES sale  left join CSMB_COMPANY company "
			+ "on company.STORE_ID=sale.STORE_ID and COMPANY.DEPT_ID=sale.DEPT_ID "
			+ "where to_char(sysdate,'yyyymmdd')=sale.SALE_DATE and company.COM_ID=? and sale.store_id=? "
			+ "group by sale.dept_id,sale.store_Id)t  left join CSMB_DEPT_SALES_HISTORY history "
			+ "on t.store_id=history.store_id and t.dept_id=history.dept_id where history.sale_date=to_date(?,'yyyy-mm-dd')";

	/**
	 * 历史某天，全司的时段客流、时段销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getAllHTimeSale(String lastYearDay) throws Exception {
		return super.queryForList(GET_ALL_H_TIME_SALE, TIME_SALE_RM, lastYearDay);
	}

	/**
	 * 历史某天，某省份的时段客流、时段销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getProvinceHTimeSale(String lastYearDay, String provinceId) throws Exception {
		return super.queryForList(GET_PROVINCE_H_TIME_SALE, TIME_SALE_RM, lastYearDay, provinceId);
	}

	/**
	 * 历史某天，某区域的时段客流、时段销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getAreaHTimeSale(String lastYearDay, String areaId) throws Exception {
		return super.queryForList(GET_AREA_H_TIME_SALE, TIME_SALE_RM, lastYearDay, areaId);
	}

	/**
	 * 历史某天，某门店的时段客流、时段销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getStoreHTimeSale(String lastYearDay, String storeId) throws Exception {
		return super.queryForList(GET_STORE_H_TIME_SALE, TIME_SALE_RM, lastYearDay, storeId);
	}

	/**
	 * 历史某天，某小店的时段客流、时段销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getComHTimeSale(String lastYearDay, String comId, String storeId) throws Exception {
		return super.queryForList(GET_COM_H_TIME_SALE, TIME_SALE_RM, lastYearDay, comId, storeId);
	}

	/**
	 * 当日全司实时客流、实时销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getAllTimeSale() throws Exception {
		return super.queryForList(GET_ALL_TIME_SALE, TIME_SALE_RM);
	}

	/**
	 * 当日某个省份实时客流、实时销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getProvinceTimeSale(String provinceId) throws Exception {
		return super.queryForList(GET_PROVINCE_TIME_SALE, TIME_SALE_RM, provinceId);
	}

	/**
	 * 当日某个区域实时客流、实时销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getAreaTimeSale(String areaId) throws Exception {
		return super.queryForList(GET_AREA_TIME_SALE, TIME_SALE_RM, areaId);
	}

	/**
	 * 当日某个大店实时客流、实时销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getStoreTimeSale(String storeId) throws Exception {
		return super.queryForList(GET_STORE_TIME_SALE, TIME_SALE_RM, storeId);
	}

	/**
	 * 当日某个小店实时客流、实时销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeSale> getComTimeSale(String storeId, String comId) throws Exception {
		return super.queryForList(GET_COM_TIME_SALE, TIME_SALE_RM, storeId, comId);
	}

	/**
	 * 全司实时销售、去年今日历史销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<DeptSale> getAllDeptSale(String lastYearDay) throws Exception {
		return super.queryForList(GET_ALL_DEPT_SALE, DEPT_SALE_RM, lastYearDay);
	}

	/**
	 * 某个省份实时销售、去年今日历史销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<DeptSale> getProvinceDeptSale(String provinceId, String lastYearDay) throws Exception {
		return super.queryForList(GET_PROVINCE_DEPT_SALE, DEPT_SALE_RM, provinceId, lastYearDay);
	}

	/**
	 * 某个区域实时销售、去年今日历史销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<DeptSale> getAreaDeptSale(String areaId, String lastYearDay) throws Exception {
		return super.queryForList(GET_AREA_DEPT_SALE, DEPT_SALE_RM, areaId, lastYearDay);
	}

	/**
	 * 某个大店实时销售、去年今日历史销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<DeptSale> getStoreDeptSale(String storeId, String lastYearDay) throws Exception {
		return super.queryForList(GET_STORE_DEPT_SALE, DEPT_SALE_RM, storeId, lastYearDay);
	}

	/**
	 * 某个小店实时销售、去年今日历史销售
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<DeptSale> getComDeptSale(String comId, String storeId, String lastYearDay) throws Exception {
		return super.queryForList(GET_COM_DEPT_SALE, DEPT_SALE_RM, comId, storeId, lastYearDay);
	}

}
