package com.cs.mobile.api.service.partner.dayreport.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.partner.dayreport.DayCompareReportDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.partner.dayreport.DeptSale;
import com.cs.mobile.api.model.partner.dayreport.TimeSale;
import com.cs.mobile.api.model.partner.dayreport.response.CompareData;
import com.cs.mobile.api.model.partner.dayreport.response.TimeReport;
import com.cs.mobile.api.service.partner.dayreport.DayCompareReportService;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.OperationUtil;

@Service
public class DayCompareReportServiceImpl implements DayCompareReportService {
	@Autowired
	private DayCompareReportDao dayCompareReportDao;
	@Value("${store.db.zyppdb.userName}")
	private String userName;
	@Value("${store.db.zyppdb.password}")
	private String password;
	@Value("${store.db.zyppdb.sid}")
	private String sid;
	@Value("${store.db.zyppdb.host}")
	private String host;
	@Value("${store.db.zyppdb.port}")
	private String port;
	@Autowired
	DruidProperties druidProperties;

	@Override
	public List<TimeReport> getAllTimeList() throws Exception {
		try {
			String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
			List<TimeSale> timeSaleList = dayCompareReportDao.getAllTimeSale();
			this.changeDataSource();
			List<TimeSale> historyTimeSaleList = dayCompareReportDao.getAllHTimeSale(lastYearDay);
			return this.timeSaleHandler(historyTimeSaleList, timeSaleList);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	@Override
	public List<TimeReport> getProvinceTimeList(String provinceId) throws Exception {
		try {
			String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
			List<TimeSale> timeSaleList = dayCompareReportDao.getProvinceTimeSale(provinceId);
			this.changeDataSource();
			List<TimeSale> historyTimeSaleList = dayCompareReportDao.getProvinceHTimeSale(lastYearDay, provinceId);
			return this.timeSaleHandler(historyTimeSaleList, timeSaleList);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	@Override
	public List<TimeReport> getAreaTimeList(String areaId) throws Exception {
		try {
			String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
			List<TimeSale> timeSaleList = dayCompareReportDao.getAreaTimeSale(areaId);
			this.changeDataSource();
			List<TimeSale> historyTimeSaleList = dayCompareReportDao.getAreaHTimeSale(lastYearDay, areaId);
			return this.timeSaleHandler(historyTimeSaleList, timeSaleList);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	@Override
	public List<TimeReport> getStoreTimeList(String storeId) throws Exception {
		try {
			String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
			List<TimeSale> timeSaleList = dayCompareReportDao.getStoreTimeSale(storeId);
			this.changeDataSource();
			List<TimeSale> historyTimeSaleList = dayCompareReportDao.getStoreHTimeSale(lastYearDay, storeId);
			return this.timeSaleHandler(historyTimeSaleList, timeSaleList);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	@Override
	public List<TimeReport> getComTimeList(String storeId, String comId) throws Exception {
		try {
			String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
			List<TimeSale> timeSaleList = dayCompareReportDao.getComTimeSale(storeId, comId);
			this.changeDataSource();
			List<TimeSale> historyTimeSaleList = dayCompareReportDao.getComHTimeSale(lastYearDay, comId, storeId);
			return this.timeSaleHandler(historyTimeSaleList, timeSaleList);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	@Override
	public List<CompareData> getAllDeptList() throws Exception {
		String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
		List<DeptSale> deptSaleList = dayCompareReportDao.getAllDeptSale(lastYearDay);
		return this.deptSaleHandler(deptSaleList);
	}

	@Override
	public List<CompareData> getProvinceDeptList(String provinceId) throws Exception {
		String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
		List<DeptSale> deptSaleList = dayCompareReportDao.getProvinceDeptSale(provinceId, lastYearDay);
		return this.deptSaleHandler(deptSaleList);
	}

	@Override
	public List<CompareData> getAreaDeptList(String areaId) throws Exception {
		String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
		List<DeptSale> deptSaleList = dayCompareReportDao.getAreaDeptSale(areaId, lastYearDay);
		return this.deptSaleHandler(deptSaleList);
	}

	@Override
	public List<CompareData> getStoreDeptList(String storeId) throws Exception {
		String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
		List<DeptSale> deptSaleList = dayCompareReportDao.getStoreDeptSale(storeId, lastYearDay);
		return this.deptSaleHandler(deptSaleList);
	}

	@Override
	public List<CompareData> getComDeptList(String storeId, String comId) throws Exception {
		String lastYearDay = DateUtil.tragetDate(DateUtil.getDate("yyyy-MM-dd"), "yyyy-MM-dd", -364, Calendar.DATE);
		List<DeptSale> deptSaleList = dayCompareReportDao.getComDeptSale(comId, storeId, lastYearDay);
		return this.deptSaleHandler(deptSaleList);
	}

	private void changeDataSource() throws Exception {
		GoodsDataSourceConfig goodsDataSourceConfig = new GoodsDataSourceConfig();
		goodsDataSourceConfig.setHost(host);
		goodsDataSourceConfig.setPort(port);
		goodsDataSourceConfig.setSid(sid);
		goodsDataSourceConfig.setStore(sid);
		DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, userName, password,
				druidProperties);
		DataSourceHolder.setDataSource(dataSourceBuilder);
	}

	private List<TimeReport> timeSaleHandler(List<TimeSale> historyTimeSaleList, List<TimeSale> timeSaleList) {
		List<TimeReport> result = new ArrayList<TimeReport>();
		Map<String, TimeSale> historyMap = new HashMap<String, TimeSale>();
		historyTimeSaleList.stream().forEach(timeSale -> {
			historyMap.put(timeSale.getTime(), timeSale);
		});
		timeSaleList.stream().forEach(curTimeSale -> {
			TimeSale historyTimeSale = historyMap.get(curTimeSale.getTime());
			TimeReport timeReport = new TimeReport();
			BigDecimal historyPf = historyTimeSale == null ? BigDecimal.ZERO
					: historyTimeSale.getPfValue().setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal historySale = historyTimeSale == null ? BigDecimal.ZERO
					: historyTimeSale.getSaleValue().setScale(2, BigDecimal.ROUND_HALF_UP);

			CompareData passengerFlow = new CompareData(curTimeSale.getTime(), curTimeSale.getTime(),
					curTimeSale.getPfValue().setScale(2, BigDecimal.ROUND_HALF_UP), historyPf, "人",
					OperationUtil.divideHandler(curTimeSale.getPfValue().subtract(historyPf), historyPf)
							.multiply(new BigDecimal(100)),
					"%");
			CompareData sale = new CompareData(curTimeSale.getTime(), curTimeSale.getTime(),
					curTimeSale.getSaleValue().setScale(2, BigDecimal.ROUND_HALF_UP), historySale, "元",
					OperationUtil.divideHandler(curTimeSale.getSaleValue().subtract(historySale), historySale)
							.multiply(new BigDecimal(100)),
					"%");
			BigDecimal historyPerPrice = OperationUtil.divideHandler(historySale, historyPf);
			BigDecimal curPerPrice = OperationUtil.divideHandler(curTimeSale.getSaleValue(), curTimeSale.getPfValue());
			CompareData perPrice = new CompareData(curTimeSale.getTime(), curTimeSale.getTime(), curPerPrice,
					historyPerPrice, "元",
					OperationUtil.divideHandler(curPerPrice.subtract(historyPerPrice), historyPerPrice)
							.multiply(new BigDecimal(100)),
					"%");
			timeReport.setTime(curTimeSale.getTime());
			timeReport.setPassengerFlow(passengerFlow);
			timeReport.setSale(sale);
			timeReport.setPerPrice(perPrice);
			result.add(timeReport);
		});
		return result.stream().sorted(Comparator.comparing(TimeReport::getCompare)).collect(Collectors.toList());
	}

	private List<CompareData> deptSaleHandler(List<DeptSale> deptSaleList) {
		List<CompareData> compareDataList = new ArrayList<CompareData>();
		deptSaleList.stream().forEach(deptSale -> {
			CompareData compareData = new CompareData(deptSale.getDeptId(), deptSale.getDeptName(),
					deptSale.getCurValue().setScale(2, BigDecimal.ROUND_HALF_UP),
					deptSale.getHistoryValue().setScale(2, BigDecimal.ROUND_HALF_UP), "元",
					OperationUtil.divideHandler(deptSale.getCurValue().subtract(deptSale.getHistoryValue()),
							deptSale.getHistoryValue()).multiply(new BigDecimal(100)),
					"%");
			compareDataList.add(compareData);
		});
		return compareDataList;
	}
}
