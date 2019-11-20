package com.cs.mobile.api.service.ranking.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.IndexDao;
import com.cs.mobile.api.dao.goods.GoodsDao;
import com.cs.mobile.api.dao.ranking.SaleGrowthDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.goal.Goal;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.partner.progress.AccountingItemResult;
import com.cs.mobile.api.model.partner.progress.CostVal;
import com.cs.mobile.api.model.partner.progress.ProgressReport;
import com.cs.mobile.api.model.partner.progress.ShareDetail;
import com.cs.mobile.api.model.ranking.SaleComStoreDTO;
import com.cs.mobile.api.model.ranking.SaleDeptItemDTO;
import com.cs.mobile.api.model.ranking.SaleItemDTO;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.ranking.SaleGrowthRateService;
import com.cs.mobile.common.exception.api.ExceptionUtils;

/**
 * 销售排名
 * 
 * @author jiangliang
 * @date 2019年3月30日
 */
@Service
public class SaleGrowthRateServiceImpl implements SaleGrowthRateService {

	@Autowired
	IndexDao indexDao;

	@Autowired
	SaleGrowthDao saleGrowthDao;

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

	/**
	 * 大店增长率排名
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<SaleComStoreDTO> getStoreIncreaseList(Map<String, String> paramMap, UserInfo userInfo)
			throws Exception {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		ca.add(Calendar.DATE, -1);// 昨天
		String endDay = new SimpleDateFormat("dd").format(ca.getTime());// 结束天
		String nowYMonth = new SimpleDateFormat("yyyy-MM").format(ca.getTime());
		String upYMonth = (Integer.valueOf(nowYMonth.substring(0, 4)) - 1) + "-" + nowYMonth.substring(5);

		paramMap.put("YMonth", nowYMonth);// 当前年-月
		paramMap.put("endDate", nowYMonth + "-" + endDay);
		paramMap.put("startDate", nowYMonth + "-01");
		List<SaleComStoreDTO> nowList = saleGrowthDao.getStoreIncreaseList(paramMap, userInfo);// 当前年月-销售列表
		List<Goal> goalList = saleGrowthDao.getStoreGoalList(paramMap);// 当前年月-目标列表

		paramMap.put("YMonth", upYMonth);// 去年-同月
		paramMap.put("endDate", upYMonth + "-" + endDay);
		paramMap.put("startDate", upYMonth + "-01");
		List<SaleComStoreDTO> upList = saleGrowthDao.getStoreIncreaseList(paramMap, userInfo);// 去前年月-销售列表
		for (SaleComStoreDTO nowDTO : nowList) {
			// 计算月份同比-增长率
			for (SaleComStoreDTO upDTO : upList) {
				if (nowDTO.getStoreId().equals(upDTO.getStoreId())
						&& 1 == upDTO.getSaleActualValue().compareTo(new BigDecimal(0))) {
					BigDecimal increaseRate = nowDTO.getSaleActualValue().subtract(upDTO.getSaleActualValue())
							.divide(upDTO.getSaleActualValue(), 4, BigDecimal.ROUND_HALF_UP);
					nowDTO.setSaleUpValue(upDTO.getSaleActualValue());
					nowDTO.setIncreaseRate(increaseRate);
				}
			}
			// 计算达成率
			for (Goal goalDTO : goalList) {
				if (nowDTO.getStoreId().equals(goalDTO.getStoreId())
						&& 1 == goalDTO.getSubValues().compareTo(new BigDecimal(0))) {
					BigDecimal reachRate = nowDTO.getSaleActualValue()
							.divide(goalDTO.getSubValues().divide(new BigDecimal(10000)), 4, BigDecimal.ROUND_HALF_UP);
					nowDTO.setSaleGoalValue(goalDTO.getSubValues());
					nowDTO.setReachRate(reachRate);
				}
			}
		}
		// 根据销售增长率排序
		Collections.sort(nowList, new Comparator<SaleComStoreDTO>() {
			@Override
			public int compare(SaleComStoreDTO dto1, SaleComStoreDTO dto2) {
				// 返回值为int类型，大于0表示正序，小于0表示逆序
				return (1 == dto1.getIncreaseRate().compareTo(dto2.getIncreaseRate()) ? -1 : 1);
			}
		});
		String keyword = paramMap.get("keyword");// 关键字过滤
		List<SaleComStoreDTO> rsList = new ArrayList<>();
		for (int i = 0; i < nowList.size(); i++) {
			SaleComStoreDTO saleComStoreDTO = nowList.get(i);
			saleComStoreDTO.setIndex(i + 1);
			if (StringUtils.isNotEmpty(keyword)) {
				if (saleComStoreDTO.getStoreName().indexOf(keyword) > -1) {
					rsList.add(saleComStoreDTO);
				}
			} else {
				rsList.add(saleComStoreDTO);
			}
		}
		return rsList;
	}

	/**
	 * 小店增长率排名
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<SaleComStoreDTO> getComIncreaseList(Map<String, String> paramMap, UserInfo userInfo) throws Exception {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		ca.add(Calendar.DATE, -1);// 昨天
		String endDay = new SimpleDateFormat("dd").format(ca.getTime());// 结束天
		String nowYMonth = new SimpleDateFormat("yyyy-MM").format(ca.getTime());
		String upYMonth = (Integer.valueOf(nowYMonth.substring(0, 4)) - 1) + "-" + nowYMonth.substring(5);

		paramMap.put("YMonth", nowYMonth);// 当前年-月
		paramMap.put("endDate", nowYMonth + "-" + endDay);
		paramMap.put("startDate", nowYMonth + "-01");
		List<SaleComStoreDTO> nowList = saleGrowthDao.getComIncreaseList(paramMap, userInfo);// 当前年月-销售列表
		paramMap.put("YMonth", upYMonth);// 去年-同月
		paramMap.put("endDate", upYMonth + "-" + endDay);
		paramMap.put("startDate", upYMonth + "-01");
		List<SaleComStoreDTO> upList = saleGrowthDao.getComIncreaseList(paramMap, userInfo);// 去前年月-销售列表

		for (SaleComStoreDTO nowDTO : nowList) {
			// 计算月份同比-增长率
			for (SaleComStoreDTO upDTO : upList) {
				if (nowDTO.getStoreId().equals(upDTO.getStoreId())
						&& 1 == upDTO.getSaleActualValue().compareTo(new BigDecimal(0))) {
					BigDecimal increaseRate = nowDTO.getSaleActualValue().subtract(upDTO.getSaleActualValue())
							.divide(upDTO.getSaleActualValue(), 4, BigDecimal.ROUND_HALF_UP);
					nowDTO.setSaleUpValue(upDTO.getSaleActualValue());
					nowDTO.setIncreaseRate(increaseRate);
				}
			}
			// 计算达成率
			paramMap.put("storeId", nowDTO.getStoreId());
			paramMap.put("comId", nowDTO.getComId());
			paramMap.put("YMonth", nowYMonth);// 当前年-月
			Goal comGoal = saleGrowthDao.getComGoalVal(paramMap);
			if (null != comGoal && 1 == comGoal.getSubValues().compareTo(new BigDecimal(0))) {
				nowDTO.setSaleGoalValue(comGoal.getSubValues());
				nowDTO.setReachRate(nowDTO.getSaleActualValue()
						.divide(comGoal.getSubValues().divide(new BigDecimal(10000)), 4, BigDecimal.ROUND_HALF_UP));
			}
		}
		// 根据销售增长率排序
		Collections.sort(nowList, new Comparator<SaleComStoreDTO>() {
			@Override
			public int compare(SaleComStoreDTO dto1, SaleComStoreDTO dto2) {
				// 返回值为int类型，大于0表示正序，小于0表示逆序
				return (1 == dto1.getIncreaseRate().compareTo(dto2.getIncreaseRate()) ? -1 : 1);
			}
		});
		String keyword = paramMap.get("keyword");// 关键字过滤
		List<SaleComStoreDTO> rsList = new ArrayList<>();
		for (int i = 0; i < nowList.size(); i++) {
			SaleComStoreDTO saleComStoreDTO = nowList.get(i);
			saleComStoreDTO.setIndex(i + 1);
			if (StringUtils.isNotEmpty(keyword)) {
				if (saleComStoreDTO.getStoreName().indexOf(keyword) > -1) {
					rsList.add(saleComStoreDTO);
				}
			} else {
				rsList.add(saleComStoreDTO);
			}
		}
		return rsList;
	}

	/**
	 * 保存排行榜点赞
	 * 
	 * @param paramMap
	 * @throws Exception
	 */
	@Override
	public void saveGiveRanking(Map<String, String> paramMap) throws Exception {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		ca.add(Calendar.DATE, -1);// 昨天
		String nowYMonth = new SimpleDateFormat("yyyy-MM").format(ca.getTime());
		paramMap.put("YMonth", nowYMonth);
		saleGrowthDao.saveGiveRanking(paramMap);
	}

	/**
	 * 大店下拉框列表
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<SaleComStoreDTO> getStoreGroupList(Map<String, String> paramMap) throws Exception {
		return saleGrowthDao.getStoreGroupList(paramMap);
	}

	/**
	 * 小店下拉框列表
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<SaleComStoreDTO> getComStoreList(Map<String, String> paramMap) throws Exception {
		return saleGrowthDao.getComStoreList(paramMap);
	}

	/**
	 * 小店下品类排名top10
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<SaleDeptItemDTO> getDeptItemTop10(Map<String, String> paramMap) throws Exception {
		try {
			Calendar ca = Calendar.getInstance();
			ca.setTime(new Date());
			ca.add(Calendar.DATE, -1);// 昨天
			String endDate = new SimpleDateFormat("yyyyMMdd").format(ca.getTime());
			paramMap.put("startDate", endDate.substring(0, 6) + "01");
			paramMap.put("endDate", endDate);
			this.changeDataSource(paramMap.get("storeId"));
			List<SaleItemDTO> itemList = saleGrowthDao.getDeptItemTop10(paramMap);

			List<String> deptIdList = new ArrayList<>();
			List<SaleDeptItemDTO> deptItemList = new ArrayList<>();
			// 封装大类列表
			for (SaleItemDTO itemDTO : itemList) {
				if (!deptIdList.contains(itemDTO.getDeptId())) {
					SaleDeptItemDTO deptDTO = new SaleDeptItemDTO();
					deptDTO.setDeptId(itemDTO.getDeptId());
					deptDTO.setDeptName(itemDTO.getDeptName());
					deptIdList.add(itemDTO.getDeptId());
					deptItemList.add(deptDTO);
				}
			}
			// 封装大类列表下品类top10
			for (SaleDeptItemDTO deptDTO : deptItemList) {
				List<SaleItemDTO> item = new ArrayList<>();
				for (SaleItemDTO itemDTO : itemList) {
					if (deptDTO.getDeptId().equals(itemDTO.getDeptId())) {
						item.add(itemDTO);
					}
				}
				deptDTO.setItemList(item);
			}
			return deptItemList;
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	@Override
	public void operationAccounting(AccountingItemResult actual, AccountingItemResult goal) {
		actual.getSale().setGoalVal(goal.getSale().getGoalVal());// 销售
		actual.getFontGp().setGoalVal(goal.getFontGp().getGoalVal());// 前台毛利
		actual.getAfterGp().setGoalVal(goal.getAfterGp().getGoalVal());// 后台毛利
		actual.getCountGp().setGoalVal(goal.getCountGp().getGoalVal());// 总毛利
		actual.getAttract().setGoalVal(goal.getAttract().getGoalVal());// 招商
		actual.getManpower().setGoalVal(goal.getManpower().getGoalVal());// 人力
		actual.getDepreciation().setGoalVal(goal.getDepreciation().getGoalVal());// 折旧
		actual.getHydropower().setGoalVal(goal.getHydropower().getGoalVal());// 水电
		actual.getLease().setGoalVal(goal.getLease().getGoalVal());// 租赁
		actual.getOther().setGoalVal(goal.getOther().getGoalVal());// 其他
		actual.getStock().setGoalVal(goal.getStock().getGoalVal());// 库存
		actual.getCheckProfit().setGoalVal(goal.getCheckProfit().getGoalVal());// 考核利润
	}

	/**
	 * 奖金池计算器
	 * 
	 * @param goalProfit
	 * @param shareDetail
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public AccountingItemResult refreshCalculation(BigDecimal goalProfit, ShareDetail shareDetail,
			Map<String, Object> paramMap) throws Exception {
		BigDecimal proportion = indexDao.getIndexShare(paramMap);// 分配比例
		CostVal cost = shareDetail.getCostList();// 各项费用
		shareDetail.setFrontGp(shareDetail.getFrontGp());
		shareDetail.setAfterGp(shareDetail.getAfterGp());
		// 总毛利 modify by wells 20190531
		// shareDetail.setCountGp(shareDetail.getFrontGp().add(shareDetail.getAfterGp()));
		// 收入合计=招商+前台毛利+后台毛利
		BigDecimal incomeCountVal = shareDetail.getAttract().add(shareDetail.getCountGp());
		shareDetail.setIncomeCount(incomeCountVal);
		// 费用合计=库存+人力+租赁+折旧+水电+其他
		BigDecimal costCountVal = shareDetail.getStock().add(cost.getDepreciation()).add(cost.getHydropower())
				.add(cost.getLease()).add(cost.getManpower()).add(cost.getOther());
		shareDetail.setCostCount(costCountVal);
		// 实际利润=总毛利-总费用
		BigDecimal actualProfit = shareDetail.getCountGp().subtract(costCountVal);
		// 考核利润=收入合计-费用合计
		shareDetail.setCheckProfit(incomeCountVal.subtract(costCountVal));
		// 超额利润 = 实际利润-目标利润
		BigDecimal excessProfitVal = actualProfit.subtract(goalProfit);
		// 分享金额=超额利润*分享比例
		BigDecimal shareVal = excessProfitVal.multiply(proportion).setScale(2, BigDecimal.ROUND_HALF_UP);
		shareDetail
				.setShareval((1 == shareVal.compareTo(new BigDecimal(0)) && 1 == goalProfit.compareTo(BigDecimal.ZERO))
						? shareVal.multiply(new BigDecimal(10000))
						: new BigDecimal(0));// 分享金额=超额利润*分配比例

		ProgressReport sale = new ProgressReport("销售", shareDetail.getSale(), new BigDecimal(0));
		ProgressReport countGp = new ProgressReport("总毛利", shareDetail.getCountGp(), new BigDecimal(0));
		ProgressReport checkProfit = new ProgressReport("考核利润", shareDetail.getCheckProfit(), new BigDecimal(0));
		ProgressReport excessProfit = new ProgressReport("超额利润", excessProfitVal, new BigDecimal(0));
		ProgressReport share = new ProgressReport("分享金额", shareDetail.getShareval(), new BigDecimal(0));
		share.setUnit("元");

		ProgressReport fontGp = new ProgressReport("前台毛利", shareDetail.getFrontGp(), new BigDecimal(0));
		ProgressReport afterGp = new ProgressReport("后台毛利", shareDetail.getAfterGp(), new BigDecimal(0));
		ProgressReport attract = new ProgressReport("招商", shareDetail.getAttract(), new BigDecimal(0));
		ProgressReport manpower = new ProgressReport("人力成本", shareDetail.getCostList().getManpower(),
				new BigDecimal(0));
		ProgressReport depreciation = new ProgressReport("折旧", shareDetail.getCostList().getDepreciation(),
				new BigDecimal(0));
		ProgressReport hydropower = new ProgressReport("水电", shareDetail.getCostList().getHydropower(),
				new BigDecimal(0));
		ProgressReport lease = new ProgressReport("租赁", shareDetail.getCostList().getLease(), new BigDecimal(0));
		ProgressReport other = new ProgressReport("其他", shareDetail.getCostList().getOther(), new BigDecimal(0));
		ProgressReport stock = new ProgressReport("库存", shareDetail.getStock(), new BigDecimal(0));
		String unit = "万";
		AccountingItemResult accountingItem = new AccountingItemResult(sale, fontGp, afterGp, attract, countGp,
				manpower, depreciation, hydropower, lease, other, stock, checkProfit, excessProfit, share,
				incomeCountVal, costCountVal, unit);
		return accountingItem;
	}

	/**
	 * 切换指定大店数据源
	 * 
	 * @param storeId
	 * @throws Exception
	 */
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

	public static void main(String[] args) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		ca.add(Calendar.DATE, -1);// 昨天
		String endDay = new SimpleDateFormat("dd").format(ca.getTime());
		System.out.println(endDay);
	}
}
