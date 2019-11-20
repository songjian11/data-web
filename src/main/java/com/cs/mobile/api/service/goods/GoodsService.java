package com.cs.mobile.api.service.goods;

import java.util.List;

import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.goods.response.GoodsInfoResp;
import com.cs.mobile.api.model.goods.response.GoodsSaleReportResp;

public interface GoodsService {
	/**
	 * 获取商品信息
	 * 
	 * @param storeId
	 * @param barcode
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月4日
	 */
	public GoodsInfoResp getGoodsInfo(String storeId, String barcode) throws Exception;

	/**
	 * 根据区域ID获取所有门店
	 * 
	 * @author wells
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getStoreByArea(String areaId) throws Exception;

	/**
	 * 根据省份获取所有区域
	 * 
	 * @author wells
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAreaByProvince(String provinceId) throws Exception;

	/**
	 * 获取所有省份
	 * 
	 * @author wells
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAllProvince() throws Exception;

	/**
	 * 根据商品编码查询商品本月销售报表
	 * 
	 * @param storeId
	 * @param item
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月6日
	 */
	public GoodsSaleReportResp getMonthReprot(String storeId, String item) throws Exception;

	/**
	 * 根据商品编码查询商品本年销售报表
	 * 
	 * @param storeId
	 * @param item
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月6日
	 */
	public GoodsSaleReportResp getYearReprot(String storeId, String item) throws Exception;
}
