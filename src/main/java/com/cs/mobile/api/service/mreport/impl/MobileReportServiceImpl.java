package com.cs.mobile.api.service.mreport.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.goods.GoodsDao;
import com.cs.mobile.api.dao.mreport.MobileReportDao;
import com.cs.mobile.api.dao.reportPage.ReportUserDeptDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.mreport.AreaGroupReport;
import com.cs.mobile.api.model.mreport.AreaGroupReportDto;
import com.cs.mobile.api.model.mreport.StoreDayDeptReport;
import com.cs.mobile.api.model.mreport.StoreDayTimeReport;
import com.cs.mobile.api.model.mreport.response.AreaGroupReportResp;
import com.cs.mobile.api.model.mreport.response.DateTitleResp;
import com.cs.mobile.api.model.mreport.response.PermeatioResp;
import com.cs.mobile.api.model.mreport.response.StoreDayDeptReportResp;
import com.cs.mobile.api.model.mreport.response.StoreDayTimeReportResp;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.service.mreport.MobileReportService;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.OperationUtil;

/**
 * 移动报表服务
 * 
 * @author wells
 * @date 2019年1月17日
 */
@Service
public class MobileReportServiceImpl implements MobileReportService {
	@Autowired
	GoodsDao goodsDao;
	@Autowired
	MobileReportDao mobileReportDao;
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
	@Autowired
	private ReportUserDeptDao reportUserDeptDao;

	@Override
	public List<Organization> getAllProvince() throws Exception {
		return mobileReportDao.getAllProvince();
	}

	@Override
	public List<Organization> getAreaByP(String provinceId) throws Exception {
		return mobileReportDao.getAreaByP(provinceId);
	}

	@Override
	public PageResult<PermeatioResp> getPermeatioResp(String provinceId, String areaId, int page, int pageSize)
			throws Exception {
		return mobileReportDao.getPermeatioResp(provinceId, areaId, page, pageSize);
	}

	@Override
	public DateTitleResp getDateTitleResp() throws Exception {
		return mobileReportDao.getDateTitleResp();
	}

	@Override
	public AreaGroupReportResp getAreaGroupReportResp() throws Exception {
		List<AreaGroupReport> list = mobileReportDao.getAreaGroupReport();
		AreaGroupReportResp result = new AreaGroupReportResp();
		List<AreaGroupReportDto> dtoList = new ArrayList<AreaGroupReportDto>();
		Map<String, List<AreaGroupReport>> map = list.stream()
				.collect(Collectors.groupingBy(AreaGroupReport::getAreaGroupName));
		for (Map.Entry<String, List<AreaGroupReport>> entry : map.entrySet()) {
			if ("总计".equals(entry.getKey())) {
				result.setTotal(entry.getValue().get(0));
			} else {
				AreaGroupReportDto areaGroupReportDto = new AreaGroupReportDto();
				List<AreaGroupReport> areaGroupReportList = new ArrayList<AreaGroupReport>();
				for (AreaGroupReport areaGroupReport : entry.getValue()) {
					if (areaGroupReport.getAreaName().contains("合计")) {
						areaGroupReportDto.setTotal(areaGroupReport);
					} else {
						areaGroupReportList.add(areaGroupReport);
					}
				}
				areaGroupReportDto.setAreaGroupName(entry.getKey());
				areaGroupReportDto.setList(areaGroupReportList);
				dtoList.add(areaGroupReportDto);
			}
		}
		result.setList(dtoList);
		return result;
	}

	/**
	 * 门店时段报表
	 * 
	 * @param storeId
	 * @param ymd
	 * @return
	 * @throws Exception
	 */
	public StoreDayTimeReportResp getDayTimeReport(String storeId, String ymd) throws Exception {
		try {
			this.changeDataSource(storeId);
			List<StoreDayTimeReport> storeDayTimeReportList = mobileReportDao.getDayTimeReport(ymd);
			StoreDayTimeReportResp storeDayTimeReportResp = new StoreDayTimeReportResp();
			List<StoreDayTimeReport> resultList = new ArrayList<StoreDayTimeReport>();
			BigDecimal totalSaleValue = BigDecimal.ZERO;// 未税销售汇总
			BigDecimal totalSaleValueIn = BigDecimal.ZERO;// 含税销售汇总
			Long totalPfCount = 0L;// 客流汇总
			StoreDayTimeReport result = null;
			for (StoreDayTimeReport storeDayTimeReport : storeDayTimeReportList) {
				result = new StoreDayTimeReport();
				result.setTime(storeDayTimeReport.getTime());
				totalSaleValue = totalSaleValue.add(storeDayTimeReport.getSaleValue());
				totalSaleValueIn = totalSaleValueIn.add(storeDayTimeReport.getSaleValueIn());
				totalPfCount = totalPfCount.longValue() + storeDayTimeReport.getPfCount().longValue();
				result.setSaleValue(storeDayTimeReport.getSaleValue().setScale(2, BigDecimal.ROUND_HALF_EVEN));
				result.setSaleValueIn(storeDayTimeReport.getSaleValueIn().setScale(2, BigDecimal.ROUND_HALF_EVEN));
				result.setPfCount(storeDayTimeReport.getPfCount());
				result.setPerPrice(OperationUtil
						.divideHandler(storeDayTimeReport.getSaleValueIn(),
								new BigDecimal(storeDayTimeReport.getPfCount()))
						.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				resultList.add(result);
			}
			storeDayTimeReportResp.setTotalSaleValue(totalSaleValueIn.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			storeDayTimeReportResp.setTotalPfCount(totalPfCount);
			storeDayTimeReportResp.setTotalPerPrice(
					totalSaleValueIn.divide(new BigDecimal(totalPfCount), 2, BigDecimal.ROUND_HALF_EVEN));
			storeDayTimeReportResp.setStoreDayTimeReportList(resultList);
			return storeDayTimeReportResp;
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	/**
	 * 门店大类报表
	 * 
	 * @param storeId
	 * @param ymd
	 * @return
	 * @throws Exception
	 */
	public StoreDayDeptReportResp getDayDeptReport(String storeId, String ymd) throws Exception {
		List<UserDept> allDeptList = reportUserDeptDao.getAllDept();
		Map<Integer, String> allDeptMap = allDeptList.stream()
				.collect(Collectors.toMap(UserDept::getDeptId, UserDept::getDeptName, (key1, key2) -> key2));
		try {
			this.changeDataSource(storeId);
			List<StoreDayDeptReport> StoreDayDeptReportList = mobileReportDao.getDayDeptReport(ymd);
			StoreDayDeptReportResp storeDayDeptReportResp = new StoreDayDeptReportResp();
			// BigDecimal totalSaleValue =
			// StoreDayDeptReportList.stream().map(StoreDayDeptReport::getSaleValue)
			// .reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal totalSaleValueIn = StoreDayDeptReportList.stream().map(StoreDayDeptReport::getSaleValueIn)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			List<StoreDayDeptReport> resultList = new ArrayList<StoreDayDeptReport>();
			StoreDayDeptReport result = null;
			for (StoreDayDeptReport storeDayDeptReport : StoreDayDeptReportList) {
				result = new StoreDayDeptReport();
				result.setDept(storeDayDeptReport.getDept());
				result.setDeptName(allDeptMap.get(Integer.parseInt(storeDayDeptReport.getDept())));
				result.setSaleValue(storeDayDeptReport.getSaleValue().setScale(2, BigDecimal.ROUND_HALF_EVEN));
				result.setSaleValueIn(storeDayDeptReport.getSaleValueIn().setScale(2, BigDecimal.ROUND_HALF_EVEN));
				result.setPermeationRatio(storeDayDeptReport.getPermeationRatio());
				result.setSaleRatio(OperationUtil.divideHandler(
						storeDayDeptReport.getSaleValueIn().multiply(new BigDecimal(100)), totalSaleValueIn) + "%");
				resultList.add(result);
			}
			storeDayDeptReportResp.setTotalSaleValue(totalSaleValueIn);
			storeDayDeptReportResp.setStoreDayDeptReportList(resultList);
			return storeDayDeptReportResp;
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
}
