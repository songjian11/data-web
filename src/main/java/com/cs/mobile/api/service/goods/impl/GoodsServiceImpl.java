package com.cs.mobile.api.service.goods.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cs.mobile.api.dao.goods.GoodsDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.goods.GoodsInfo;
import com.cs.mobile.api.model.goods.GoodsSale;
import com.cs.mobile.api.model.goods.TodaySale;
import com.cs.mobile.api.model.goods.response.GoodsInfoResp;
import com.cs.mobile.api.model.goods.response.GoodsSaleReportResp;
import com.cs.mobile.api.model.goods.response.TimeLineReportResp;
import com.cs.mobile.api.service.goods.GoodsService;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.OperationUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {
	@Autowired
	GoodsDao goodsDao;
	@Autowired
	DruidProperties druidProperties;
	@Value("${store.db.cs.userName}")
	private String csUserName;
	@Value("${store.db.cs.password}")
	private String csPassword;
	@Value("${store.db.jd.userName}")
	private String jdUserName;
	@Value("${store.db.jd.password}")
	private String jdPassword;

	@Override
	public GoodsInfoResp getGoodsInfo(String storeId, String barcode) throws Exception {
		try {
			this.changeDataSource(storeId);
			String item = this.getItem(barcode);
			if (StringUtils.isEmpty(item)) {
				ExceptionUtils.wapperBussinessException("商品信息不存在");
			}
			GoodsInfo goodsInfo = goodsDao.getGoodsInfo(item);
			if (goodsInfo == null) {
				ExceptionUtils.wapperBussinessException("商品信息不存在");
			}
			GoodsInfoResp goodsInfoResp = new GoodsInfoResp();
			BeanUtils.copyProperties(goodsInfo, goodsInfoResp);
			TodaySale todaySale = goodsDao.getTodaySale(storeId);
			if (todaySale != null) {
				if (todaySale.getAmtEcl() != null) {
					goodsInfoResp.setDaySaleValue(todaySale.getAmtEcl());
					if (todaySale.getGpEcl() != null) {
						goodsInfoResp.setGpEcl(todaySale.getGpEcl()
								.divide(todaySale.getAmtEcl(), 2, BigDecimal.ROUND_HALF_UP).toString());
					}
				}
			}
			return goodsInfoResp;
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private void changeDataSource(String storeId) throws Exception {
		GoodsDataSourceConfig goodsDataSourceConfig = goodsDao.getDataSourceByStoreId(storeId);
		if (goodsDataSourceConfig != null) {
			if ("11".equals(goodsDataSourceConfig.getChain())) {
				DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, csUserName,
						csPassword, druidProperties);
				DataSourceHolder.setDataSource(dataSourceBuilder);
			} else if ("13".equals(goodsDataSourceConfig.getChain())) {
				DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, jdUserName,
						jdPassword, druidProperties);
				DataSourceHolder.setDataSource(dataSourceBuilder);
			} else {
				ExceptionUtils.wapperBussinessException("暂时不支持该业态");
			}

		} else {
			ExceptionUtils.wapperBussinessException("未获取到门店数据源");
		}
	}

	private String getItem(String barcode) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("barcode", barcode);
		String param = JSON.toJSONString(map);
		String itemResp = goodsDao.getItem(param);
		log.info("调用oracle函数获取商品编码返回结果:" + itemResp);
		JSONObject jsonObject = JSON.parseObject(itemResp);
		String rspCod = jsonObject.getString("rspCod");
		if ("00".equals(rspCod)) {// 成功
			JSONObject jsonData = jsonObject.getJSONObject("data");
			if (jsonData == null) {
				ExceptionUtils.wapperBussinessException("商品信息不存在");
			}
			return jsonData.getString("bn");
		} else {// 失败
			log.error("调用oracle函数获取商品编码失败:" + jsonObject.getString("rspMsg"));
			ExceptionUtils.wapperBussinessException(jsonObject.getString("rspMsg"));
		}
		return null;
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
		return goodsDao.getStoreByArea(areaId);
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
		return goodsDao.getAreaByProvince(provinceId);
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
		return goodsDao.getAllProvince();
	}

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
	public GoodsSaleReportResp getMonthReprot(String storeId, String item) throws Exception {
		try {
			this.changeDataSource(storeId);
			List<GoodsSale> goodsSaleList = goodsDao.getCurMonthSale(item);
			return this.goodsSaleReportCore("month", goodsSaleList);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

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
	public GoodsSaleReportResp getYearReprot(String storeId, String item) throws Exception {
		try {
			this.changeDataSource(storeId);
			List<GoodsSale> goodsSaleList = goodsDao.getCurYearSale(item);
			return this.goodsSaleReportCore("year", goodsSaleList);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private GoodsSaleReportResp goodsSaleReportCore(String type, List<GoodsSale> goodsSaleList) {
		GoodsSaleReportResp goodsSaleReportResp = new GoodsSaleReportResp();
		List<TimeLineReportResp> saleTimeLine = new ArrayList<TimeLineReportResp>();
		List<TimeLineReportResp> gpTimeLine = new ArrayList<TimeLineReportResp>();
		BigDecimal totalSaleQty = BigDecimal.ZERO;
		BigDecimal totalSaleValue = BigDecimal.ZERO;
		BigDecimal totalGp = BigDecimal.ZERO;
		for (GoodsSale goodsSale : goodsSaleList) {
			String time = goodsSale.getTime();
			// 封装销售报表数据
			TimeLineReportResp saleTimeLineReportResp = new TimeLineReportResp();
			saleTimeLineReportResp.setTime(time);
			saleTimeLineReportResp.setUnit("元");
			saleTimeLineReportResp.setValue(goodsSale.getSaleValue().setScale(2, BigDecimal.ROUND_HALF_UP));
			if ("month".equals(type)) {
				String day = time.substring(time.length() - 2, time.length());
				String month = time.substring(time.length() - 4, time.length() - 2);
				saleTimeLineReportResp
						.setShowTime(String.format("%d-%d", Integer.parseInt(month), Integer.parseInt(day)));
			} else if ("year".equals(type)) {
				String month = time.substring(time.length() - 2, time.length());
				saleTimeLineReportResp.setShowTime(String.format("%d月", Integer.parseInt(month)));
			}
			saleTimeLine.add(saleTimeLineReportResp);
			// 封装毛利报表数据
			if (goodsSale.getSaleValue().compareTo(BigDecimal.ZERO) != 0) {
				TimeLineReportResp gpTimeLineReportResp = new TimeLineReportResp();
				gpTimeLineReportResp.setTime(time);
				gpTimeLineReportResp.setUnit("%");
				if ("month".equals(type)) {
					String day = time.substring(time.length() - 2, time.length());
					String month = time.substring(time.length() - 4, time.length() - 2);
					gpTimeLineReportResp
							.setShowTime(String.format("%d-%d", Integer.parseInt(month), Integer.parseInt(day)));
				} else if ("year".equals(type)) {
					String month = time.substring(time.length() - 2, time.length());
					gpTimeLineReportResp.setShowTime(String.format("%d月", Integer.parseInt(month)));
				}
				gpTimeLineReportResp.setValue(OperationUtil.divideHandler(goodsSale.getGp(), goodsSale.getSaleValue())
						.multiply(new BigDecimal(100)));
				gpTimeLine.add(gpTimeLineReportResp);
			}
			totalSaleQty = totalSaleQty.add(goodsSale.getSaleQty());
			totalSaleValue = totalSaleValue.add(goodsSale.getSaleValue());
			totalGp = totalGp.add(goodsSale.getGp());
		}
		goodsSaleReportResp.setGpTimeLine(gpTimeLine);
		goodsSaleReportResp.setSaleTimeLine(saleTimeLine);
		goodsSaleReportResp.setTotalSaleQty(totalSaleQty.setScale(2, BigDecimal.ROUND_HALF_UP));
		goodsSaleReportResp.setTotalSaleValue(totalSaleValue.setScale(2, BigDecimal.ROUND_HALF_UP));
		goodsSaleReportResp.setTotalSaleValueUnit("元");
		if (totalSaleValue.compareTo(BigDecimal.ZERO) != 0) {
			goodsSaleReportResp.setTotalGpp(String.format("%s%s",
					OperationUtil.divideHandler(totalGp, totalSaleValue).multiply(new BigDecimal(100)).toString(),
					"%"));
		} else {
			goodsSaleReportResp.setTotalGpp("--");
		}
		return goodsSaleReportResp;
	}
}
