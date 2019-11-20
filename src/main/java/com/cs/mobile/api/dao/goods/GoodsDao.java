package com.cs.mobile.api.dao.goods;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.goods.GoodsInfo;
import com.cs.mobile.api.model.goods.GoodsSale;
import com.cs.mobile.api.model.goods.TodaySale;

@Repository
public class GoodsDao extends AbstractDao {
	private static final RowMapper<GoodsDataSourceConfig> DATA_SOURCE_CNF_RM = new BeanPropertyRowMapper<>(
			GoodsDataSourceConfig.class);
	private static final RowMapper<GoodsInfo> GOODS_INFO_RM = new BeanPropertyRowMapper<>(GoodsInfo.class);
	private static final RowMapper<TodaySale> TODAY_SALE_RM = new BeanPropertyRowMapper<>(TodaySale.class);
	private static final RowMapper<Organization> ORG_RM = new BeanPropertyRowMapper<>(Organization.class);
	private static final RowMapper<GoodsSale> GOODS_SALE_RM = new BeanPropertyRowMapper<>(GoodsSale.class);

	// 根据门店ID获取数据源SQL
	private static final String GET_DATA_SOURCE = "select t.*,s.chain from tnsnames t left join csmb_store s on t.store=s.store_id where store=? ";

	// 查询商品信息SQL
	private static final String GET_GOODS_INFO = "select info.item,info.item_desc,info.dept,info.dept_desc,"
			+ "info.class goods_class,info.class_desc,info.subclass,info.subclass_desc,info.packsize,info.selling_uom,"
			+ "info.supp_pack_size,info.inner_pack_size,info.shelflife,info.area,info.retail,info.promo_retail,"
			+ "info.promo_date,info.vipPrice,info.real_soh,info.in_transit_qty,info.day_sale,info.promo_day_sale,"
			+ "info.four_week_sale,info.last_received,info.incometaxrate,info.saletaxrate,info.nbbtype,info.nbotype,"
			+ "info.item_type,info.business_mode,info.logistics_delivery_model,info.status,info.sellable_ind,"
			+ "info.orderable_ind,info.primary_supp,info.supp_name,info.promo_type,info.returnable_ind,"
			+ "info.real_time_sale,info.barcode,info.weight_ind,info.lblType,"
			+ "round(info.real_soh/info.day_sale,2) available_sale_Day,d.av_cost ,d.dg0137 unit_cost "
			+ "from PDA_GOODS_INFO info left join dg001 d on info.item=d.dg0101 where info.item=? ";

	// 查询商品编码SQL
	private static final String GET_ITEM = "select pk_quick_sale.getiteminfo(?) from dual";
	// 获取商品当天销售
	private static final String GET_TODAY_SALE = "select sum(amt) amt,SUM(amt / (1 + DG0133 / 100)) amt_ecl, SUM((CASE "
			+ "               WHEN goodstype = '01' OR standard_gross_margin IS NULL THEN "
			+ "                amt - NVL((SELECT sum(scan_qnty * d.av_cost) "
			+ "                            FROM pack_item, dg001 d "
			+ "                           WHERE pack_id = sd0102 "
			+ "                             AND d.dg0101 = item_id), "
			+ "                          av_cost) * sd0104                ELSE "
			+ "                amt * DECODE(SIGN(SD0105 - SD0103),                              -1, "
			+ "                             DG001.PROMO_GROSS_MARGIN, "
			+ "                             dg001.standard_gross_margin) / 100              END) ) GP, "
			+ "         SUM((CASE " + "               WHEN goodstype = '01' OR standard_gross_margin IS NULL THEN "
			+ "                amt - NVL((SELECT sum(scan_qnty * d.av_cost) "
			+ "                            FROM pack_item, dg001 d "
			+ "                           WHERE pack_id = sd0102 "
			+ "                             AND d.dg0101 = item_id), "
			+ "                          av_cost) * sd0104                ELSE "
			+ "                amt * DECODE(SIGN(SD0105 - SD0103),                              -1, "
			+ "                             DG001.PROMO_GROSS_MARGIN, "
			+ "                             dg001.standard_gross_margin) / 100 "
			+ "             END) / (1 + DG0133 / 100)) GP_ecl           from sd001 ,dg001 "
			+ "         where  dg0101=sd0102 and dg0104<>'9101'          and dg0101=? "
			+ "           and sd0101 = to_char(sysdate,'yyyymmdd')";

	// 根据区域ID获取所有门店
	private static final String GET_STORE_BY_AREA = "select store_id as org_id,store_name as org_name from CSMB_STORE "
			+ "where area_id=? and store_close_date is null order by store_id";
	// 根据省份获取所有区域
	private static final String GET_AREA_BY_PROVINCE = "select DISTINCT(area_id) as org_id,area_name as org_name "
			+ "from CSMB_STORE where province_id=? and store_close_date is null order by area_id";
	// 获取所有省份(只查询11【大卖场】、13【家电】业态)
	private static final String GET_ALL_PROVINCE = "select DISTINCT(PROVINCE_ID) as org_id,PROVINCE_NAME as org_name "
			+ "from CSMB_STORE where store_close_date is null and chain in(11,13) order by province_id ";
	// 获取商品本月销售
	private static final String GET_CUR_MONTH_SALE = "SELECT saledate time, "
			+ "       sum(h.sale_qty) sale_qty, sum(h.sale_value) sale_value,    "
			+ "       sum(h.sale_value - h.sale_cost + h.fund_amount + h.invadj_cost - h.wac) gp "
			+ "  FROM rpt_daily_gp h  where item = ? "
			+ "   and saledate between to_char(trunc(sysdate - 1, 'mm'), 'yyyymmdd') and "
			+ "       to_char(trunc(sysdate - 1), 'yyyymmdd') group by saledate order by saledate";
	// 获取商品本年销售
	private static final String GET_CUR_YEAR_SALE = "SELECT substr(saledate,1,6) time, "
			+ "       sum(h.sale_qty) sale_qty,        sum(h.sale_value) sale_value, "
			+ "       sum(h.sale_value - h.sale_cost + h.fund_amount + h.invadj_cost - h.wac) gp "
			+ "  FROM rpt_daily_gp h  where item = ? "
			+ "   and saledate between to_char(trunc(sysdate - 1, 'yyyy'), 'yyyymmdd') and "
			+ "       to_char(trunc(sysdate - 1), 'yyyymmdd') group by substr(saledate,1,6) order by time";

	/**
	 * 根据门店ID获取数据源
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月4日
	 */
	public GoodsDataSourceConfig getDataSourceByStoreId(String storeId) throws Exception {
		return super.queryForObject(GET_DATA_SOURCE, DATA_SOURCE_CNF_RM, storeId);
	}

	/**
	 * 根据商品编码查询商品信息
	 * 
	 * @param item
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月4日
	 */
	public GoodsInfo getGoodsInfo(String item) throws Exception {
		return super.queryForObject(GET_GOODS_INFO, GOODS_INFO_RM, item);
	}

	/**
	 * 调用oracle函数获取商品编码
	 * 
	 * @param param</br>
	 * @describe 输入参数举例:{"barcode":"6954767433073"}
	 * @return {"rspCod":"00","rspMsg":"成功","data":{"amount":"6.80","barcode":"6954767433073","bn":"800002453","dept":"21","sellUom":"瓶","price":"6.80","productName":"雪碧2L汽水","weight":"","quantity":"1"}}
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月4日
	 */
	public String getItem(String param) throws Exception {
		String result = this.jdbcTemplate.query(GET_ITEM, new Object[] { param }, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					return rs.getString(1);
				} else {
					return null;
				}
			}
		});
		return result;
	}

	/**
	 * 获取当日销售数据
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月4日
	 */
	public TodaySale getTodaySale(String storeId) throws Exception {
		return super.queryForObject(GET_TODAY_SALE, TODAY_SALE_RM, storeId);
	}

	/**
	 * 根据区域ID获取所有门店
	 * 
	 * @author wells
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getStoreByArea(String areaId) throws Exception {
		return super.queryForList(GET_STORE_BY_AREA, ORG_RM, areaId);
	}

	/**
	 * 根据省份获取所有区域
	 * 
	 * @author wells
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAreaByProvince(String provinceId) throws Exception {
		return super.queryForList(GET_AREA_BY_PROVINCE, ORG_RM, provinceId);
	}

	/**
	 * 获取所有省份
	 * 
	 * @author wells
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAllProvince() throws Exception {
		return super.queryForList(GET_ALL_PROVINCE, ORG_RM);
	}

	/**
	 * 获取商品当月销售
	 * 
	 * @param item
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月6日
	 */
	public List<GoodsSale> getCurMonthSale(String item) throws Exception {
		return super.queryForList(GET_CUR_MONTH_SALE, GOODS_SALE_RM, item);
	}

	/**
	 * 获取商品本年销售
	 * 
	 * @param item
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月6日
	 */
	public List<GoodsSale> getCurYearSale(String item) throws Exception {
		return super.queryForList(GET_CUR_YEAR_SALE, GOODS_SALE_RM, item);
	}
}
