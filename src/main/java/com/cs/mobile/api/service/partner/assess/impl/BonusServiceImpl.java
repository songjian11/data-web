package com.cs.mobile.api.service.partner.assess.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cs.mobile.api.dao.partner.assess.BonusCalculateDao;
import com.cs.mobile.api.dao.partner.assess.BonusDao;
import com.cs.mobile.api.dao.partner.assess.BonusReportDao;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.goal.GoalSummary;
import com.cs.mobile.api.model.goal.GoalValue;
import com.cs.mobile.api.model.partner.PartnerPerson;
import com.cs.mobile.api.model.partner.assess.AssessMergeCnf;
import com.cs.mobile.api.model.partner.assess.AssessResult;
import com.cs.mobile.api.model.partner.assess.AssessResultSummary;
import com.cs.mobile.api.model.partner.assess.BonusReport;
import com.cs.mobile.api.model.partner.assess.ComBonus;
import com.cs.mobile.api.model.partner.assess.PersonBonus;
import com.cs.mobile.api.model.partner.assess.PersonBonusExt;
import com.cs.mobile.api.model.partner.assess.response.BonusAssessReportResp;
import com.cs.mobile.api.model.partner.assess.response.BonusHistoryResp;
import com.cs.mobile.api.model.partner.assess.response.ComAuditItemResp;
import com.cs.mobile.api.model.partner.assess.response.ComPersonBonusResp;
import com.cs.mobile.api.model.partner.assess.response.PersonBonusItemResp;
import com.cs.mobile.api.model.partner.assess.response.StoreAuditResp;
import com.cs.mobile.api.model.partner.progress.CostVal;
import com.cs.mobile.api.model.partner.progress.ProgressReport;
import com.cs.mobile.api.model.partner.progress.ProgressReportResult;
import com.cs.mobile.api.model.partner.progress.ShareDetail;
import com.cs.mobile.api.service.partner.assess.BonusService;
import com.cs.mobile.common.constant.PersonBonusStatusEnum;
import com.cs.mobile.common.constant.PositionEnum;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.OperationUtil;
import com.github.pagehelper.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BonusServiceImpl implements BonusService {
	@Autowired
	private BonusDao bonusDao;
	@Autowired
	private BonusCalculateDao bonusCalculateDao;
	@Autowired
	private BonusReportDao bonusReportDao;

	/**
	 * 根据小店获取人员奖金情况（不包括大店店长）
	 * 
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public ComPersonBonusResp getComPersonBonus(String storeId, String comId) throws Exception {
		List<PersonBonusExt> personBonusList = bonusDao.getLastMonthComPersonBonus(storeId, comId);
		BigDecimal unDistrBonus = BigDecimal.ZERO;
		BigDecimal distrBonus = BigDecimal.ZERO;
		BigDecimal totalBonus = BigDecimal.ZERO;
		List<PersonBonusItemResp> personBonusItemRespList = new ArrayList<PersonBonusItemResp>();
		ComPersonBonusResp comPersonBonusResp = new ComPersonBonusResp();
		for (PersonBonusExt personBonus : personBonusList) {
			distrBonus = distrBonus.add(personBonus.getAfterMoney());
			totalBonus = personBonus.getBonus();
			comPersonBonusResp.setStatus(personBonus.getAuditStatus());
			PersonBonusItemResp personBonusItemResp = new PersonBonusItemResp();
			BeanUtils.copyProperties(personBonus, personBonusItemResp);
			personBonusItemRespList.add(personBonusItemResp);
		}
		unDistrBonus = totalBonus.subtract(distrBonus).setScale(2, BigDecimal.ROUND_HALF_UP);
		comPersonBonusResp.setPersonBonusList(personBonusItemRespList);
		comPersonBonusResp.setTotalBonus(totalBonus);
		comPersonBonusResp.setUnDistrBonus(unDistrBonus);
		return comPersonBonusResp;
	}

	/**
	 * 调整人员奖金
	 * 
	 * @param personBonusList
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	@Transactional
	public void modifyComPersonBonus(List<PersonBonusItemResp> personBonusList) throws Exception {
		// TODO:可以优化为批量更新来提高效率
		for (PersonBonusItemResp personBonusItemResp : personBonusList) {
			personBonusItemResp.setStatus(PersonBonusStatusEnum.WAIT_AUDIT.getType());
			bonusDao.updateComBonusStatus(personBonusItemResp, null);
			bonusDao.modifyPersonBonus(personBonusItemResp);
			// TODO:保存历史记录
		}
	}

	/**
	 * 分页分页查询奖金历史列表
	 * 
	 * @param page
	 * @param pageSize
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public PageResult<BonusHistoryResp> getBonusHistory(int page, int pageSize, String storeId, String comId)
			throws Exception {
		return bonusDao.getHistory(page, pageSize, storeId, comId);
	}

	/**
	 * 审核奖金
	 * 
	 * @param ym
	 * @param storeId
	 * @param comId
	 * @param status
	 * @param reason
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	@Transactional
	public void auditBonus(String ym, String storeId, String comId, int status, String reason) throws Exception {
		bonusDao.updateComBonusStatus(ym, storeId, comId, status, reason);
		bonusDao.updatePersonBonusStatus(ym, storeId, comId, status);
		// TODO:保存历史记录
	}

	/**
	 * 获取上月门店审核列表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public StoreAuditResp getStoreAuditList(String storeId) throws Exception {
		List<PersonBonusExt> personBonusList = bonusDao.getLastMonthComBonus(storeId);
		StoreAuditResp storeAuditResp = new StoreAuditResp();
		List<ComAuditItemResp> comAuditList = new ArrayList<ComAuditItemResp>();
		String storeName = "";
		String ym = "";
		Map<String, List<PersonBonusExt>> personBonusMap = personBonusList.stream()
				.collect(Collectors.groupingBy(PersonBonusExt::getComId));
		for (Map.Entry<String, List<PersonBonusExt>> entry : personBonusMap.entrySet()) {
			ComAuditItemResp comAuditItemResp = new ComAuditItemResp();
			List<PersonBonusExt> data = entry.getValue();
			List<PersonBonusItemResp> personBonusItemList = new ArrayList<PersonBonusItemResp>();
			for (PersonBonusExt personBonusExt : data) {
				storeName = personBonusExt.getStoreName();
				ym = personBonusExt.getYm();
				PersonBonusItemResp personBonusItemResp = new PersonBonusItemResp();
				// 循环产生小店奖金明细
				BeanUtils.copyProperties(personBonusExt, comAuditItemResp);
				// 循环产生人员奖金明细
				BeanUtils.copyProperties(personBonusExt, personBonusItemResp);
				personBonusItemList.add(personBonusItemResp);
			}
			comAuditItemResp.setPersonBonusList(personBonusItemList);
			comAuditList.add(comAuditItemResp);
		}
		storeAuditResp.setStoreId(storeId);
		storeAuditResp.setStoreName(storeName);
		storeAuditResp.setYm(ym);
		storeAuditResp.setComAuditList(comAuditList);
		return storeAuditResp;
	}

	/**
	 * 查询当前正在执行的任务数量
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int getTaskCount() throws Exception {
		return bonusCalculateDao.getTaskCount();
	}

	/**
	 * 更新任务
	 * 
	 * @param batchNo
	 * @param status
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int updateTask(int status) throws Exception {
		return bonusCalculateDao.updateTask(status);
	}

	/**
	 * 插入新任务
	 * 
	 * @param batchNo
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int addTask(String batchNo) throws Exception {
		return bonusCalculateDao.addTask(batchNo);
	}

	/**
	 * 获取小店奖金数据
	 * 
	 * @param comId
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getComBonusByComId(String comId, String storeId, String ym) throws Exception {
		ComBonus comBonus = bonusDao.getComBonusByComId(comId, storeId, ym);
		return this.historyHandler(comBonus);
	}

	/**
	 * 获取大店奖金数据
	 * 
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getComBonusByStoreId(String storeId, String ym) throws Exception {
		ComBonus comBonus = bonusDao.getComBonusByStoreId(storeId, ym);
		return this.historyHandler(comBonus);
	}

	/**
	 * 获取区域奖金数据
	 * 
	 * @param areaId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getComBonusByAreaId(String areaId, String ym) throws Exception {
		ComBonus comBonus = bonusDao.getComBonusByAreaId(areaId, ym);
		return this.historyHandler(comBonus);
	}

	/**
	 * 获取省份奖金数据
	 * 
	 * @param provinceId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getComBonusByProvinceId(String provinceId, String ym) throws Exception {
		ComBonus comBonus = bonusDao.getComBonusByProvinceId(provinceId, ym);
		return this.historyHandler(comBonus);
	}

	/**
	 * 获取所有奖金数据
	 * 
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getAllComBonus(String ym) throws Exception {
		ComBonus comBonus = bonusDao.getAllComBonus(ym);
		return this.historyHandler(comBonus);
	}

	private ProgressReportResult historyHandler(ComBonus comBonus) throws Exception {
		ProgressReportResult progressReportResult = null;
		if (comBonus != null) {
			// 销售
			ProgressReport sale = new ProgressReport.Builder().typeName("销售")
					.ratio(comBonus.getGoalSale(), comBonus.getSale())
					.goalCountVal(OperationUtil.divideHandler(comBonus.getGoalSale(), new BigDecimal(10000)))
					.goalVal(OperationUtil.divideHandler(comBonus.getGoalSale(), new BigDecimal(10000)))
					.actualVal(OperationUtil.divideHandler(comBonus.getSale(), new BigDecimal(10000)))
					.diffVal(OperationUtil.divideHandler(
							(comBonus.getSale() == null ? BigDecimal.ZERO : comBonus.getSale()).subtract(
									comBonus.getGoalSale() == null ? BigDecimal.ZERO : comBonus.getGoalSale()),
							new BigDecimal(10000)))
					.unit("万").build();
			// 利润
			ProgressReport profit = new ProgressReport.Builder().typeName("利润")
					.ratio(comBonus.getGoalProfit(), comBonus.getActualProfit())
					.goalCountVal(OperationUtil.divideHandler(comBonus.getGoalProfit(), new BigDecimal(10000)))
					.goalVal(OperationUtil.divideHandler(comBonus.getGoalProfit(), new BigDecimal(10000)))
					.actualVal(OperationUtil.divideHandler(comBonus.getActualProfit(), new BigDecimal(10000)))
					.diffVal(OperationUtil.divideHandler(
							(comBonus.getActualProfit() == null ? BigDecimal.ZERO : comBonus.getActualProfit())
									.subtract(comBonus.getGoalProfit() == null ? BigDecimal.ZERO
											: comBonus.getGoalProfit()),
							new BigDecimal(10000)))
					.unit("万").build();
			progressReportResult = new ProgressReportResult(null, profit, null, sale, comBonus.getBonus(), "元");

		}
		return progressReportResult;
	}

	/**
	 * 产生奖金数据
	 * 
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	@Transactional
	public void createData() throws Exception {
		// 查询小店奖金表数据状态为初始化完成的列表
		List<ComBonus> comBonusList = bonusCalculateDao.getWaitDealBonusList();
		if (comBonusList.size() == 0) {
			ExceptionUtils.wapperBussinessException("本次没有等待处理的小店奖金数据");
		}
		// 查询考核表
		List<AssessResult> assessResultList = bonusCalculateDao.getLastMonthAssessResult();
		// 查询合伙人体制内人员
		List<PartnerPerson> partnerPersonList = bonusCalculateDao.getPartnerPerson();
		// 查询目标表
		List<AssessMergeCnf> assessMergeCnf = bonusCalculateDao.getAssessMergerCnf();
		// 查询目标表
		List<GoalValue> goalList = bonusCalculateDao.getLastMonthGoal(assessMergeCnf);
		Map<String, ComBonus> comBonusMap = new HashMap<String, ComBonus>();
		this.comBonusHandler(comBonusList, comBonusMap);
		Map<String, GoalSummary> goalValueMap = this.goalHandler(goalList);
		Map<String, List<PartnerPerson>> partnerPersonMap = partnerPersonList.stream()
				.collect(Collectors.groupingBy(PartnerPerson::getSerialNo));
		Map<String, AssessResultSummary> assessResultMap = this.assessHandler(assessResultList);
		this.calculateHandler(comBonusMap, goalValueMap, partnerPersonMap, assessResultMap);
		// 更新任务
		this.updateTask(2);
	}

	private void calculateHandler(Map<String, ComBonus> comBonusMap, Map<String, GoalSummary> goalValueMap,
			Map<String, List<PartnerPerson>> partnerPersonMap, Map<String, AssessResultSummary> assessResultMap)
			throws Exception {
		List<PersonBonus> addPersonBonusList = new ArrayList<PersonBonus>();
		List<ComBonus> updateComBonusList = new ArrayList<ComBonus>();
		// 循环小店分享金额生产数据
		for (Map.Entry<String, ComBonus> entry : comBonusMap.entrySet()) {
			ComBonus comBonus = entry.getValue();
			GoalSummary goalSummary = goalValueMap.get(entry.getKey());
			List<PartnerPerson> personList = partnerPersonMap.get(entry.getKey());
			AssessResultSummary assessResultSummary = assessResultMap.get(entry.getKey());
			BigDecimal goalProfit = BigDecimal.ZERO;
			BigDecimal goalSale = BigDecimal.ZERO;
			if (goalSummary == null) {
				log.info("========key:【{}】不存在目标数据，所有目标数据设置为0", entry.getKey());
			} else {
				// 目标总毛利=目标前台毛利+目标后台收入-DC成本;月目标利润=月目标总毛利+月目标招商收入-月目标总费用
				goalProfit = goalSummary.getFrontGpTotalGoal().add(goalSummary.getAfterGpTotalGoal())
						.subtract(goalSummary.getDcTotalGoal()).add(goalSummary.getAttractTotalGoal())
						.subtract(goalSummary.getCostTotalGoal());
				goalSale = goalSummary.getSaleTotalGoal();
			}
			BigDecimal actualProfit = BigDecimal.ZERO;
			BigDecimal frontGp = BigDecimal.ZERO;
			BigDecimal cost = BigDecimal.ZERO;
			BigDecimal attract = BigDecimal.ZERO;
			BigDecimal sale = BigDecimal.ZERO;
			if (assessResultSummary == null) {
				log.info("========key:【{}】不存在考核数据，所有考核数据设置为0", entry.getKey());
			} else {
				// 实际利润=销售毛利额+后台-DC成本-总费用+招商收入
				actualProfit = assessResultSummary.getFrontGpTotal().add(assessResultSummary.getAfterGpTotal())
						.subtract(assessResultSummary.getDcTotal()).subtract(assessResultSummary.getCostTotal())
						.add(assessResultSummary.getAttractTotal());
				frontGp = assessResultSummary.getFrontGpTotal();
				cost = assessResultSummary.getCostTotal();
				attract = assessResultSummary.getAttractTotal();
				sale = assessResultSummary.getSaleTotal();
			}
			comBonus.setFrontGp(frontGp);// 销售毛利额就是前台毛利
			comBonus.setCost(cost);
			comBonus.setAttract(attract);
			comBonus.setSale(sale);
			comBonus.setActualProfit(actualProfit);
			comBonus.setGoalProfit(goalProfit);
			comBonus.setGoalSale(goalSale);
			int comMangerCount = 0;
			int storeMangerCount = 0;
			int partnerCount = 0;
			int personTotalCount = 0;
			if (personList != null && personList.size() > 0) {
				for (PartnerPerson partnerPerson : personList) {
					if (PositionEnum.PARTNER.getType() == Integer.parseInt(partnerPerson.getPositionId())) {
						partnerCount++;
					}
					if (PositionEnum.COM_MANAGER.getType() == Integer.parseInt(partnerPerson.getPositionId())) {
						comMangerCount++;
					}
					if (PositionEnum.STORE_MANAGER.getType() == Integer.parseInt(partnerPerson.getPositionId())) {
						storeMangerCount++;
					}
				}
				personTotalCount = comMangerCount * 3 + storeMangerCount * 6 + partnerCount * 1;
				for (PartnerPerson partnerPerson : personList) {
					BigDecimal bonus = BigDecimal.ZERO;
					if (PositionEnum.PARTNER.getType() == Integer.parseInt(partnerPerson.getPositionId())) {
						bonus = comBonus.getBonus().multiply(
								new BigDecimal(1).divide(new BigDecimal(personTotalCount), 2, BigDecimal.ROUND_FLOOR))
								.setScale(2, BigDecimal.ROUND_FLOOR);
						if (comBonus.getBonus().compareTo(BigDecimal.ZERO) == 1) {
							log.info("当前为合伙人：bonus:" + comBonus.getBonus() + "personTotalCount:" + personTotalCount
									+ "计算结果：" + bonus);
						}
					}
					if (PositionEnum.COM_MANAGER.getType() == Integer.parseInt(partnerPerson.getPositionId())) {
						bonus = comBonus.getBonus().multiply(
								new BigDecimal(3).divide(new BigDecimal(personTotalCount), 2, BigDecimal.ROUND_FLOOR))
								.setScale(2, BigDecimal.ROUND_FLOOR);
						comBonus.setManagerId(partnerPerson.getPersonId());
						comBonus.setManagerName(partnerPerson.getName());
						if (comBonus.getBonus().compareTo(BigDecimal.ZERO) == 1) {
							log.info("当前为小店店长：bonus:" + comBonus.getBonus() + "personTotalCount:" + personTotalCount
									+ "计算结果：" + bonus);
						}
					}
					if (PositionEnum.STORE_MANAGER.getType() == Integer.parseInt(partnerPerson.getPositionId())) {
						bonus = comBonus.getBonus().multiply(
								new BigDecimal(6).divide(new BigDecimal(personTotalCount), 2, BigDecimal.ROUND_FLOOR))
								.setScale(2, BigDecimal.ROUND_FLOOR);
						if (comBonus.getBonus().compareTo(BigDecimal.ZERO) == 1) {
							log.info("当前为大店店长：bonus:" + comBonus.getBonus() + "personTotalCount:" + personTotalCount
									+ "计算结果：" + bonus);
						}
					}
					PersonBonus personBonus = new PersonBonus(DateUtil.getLastMonth(), partnerPerson.getPersonId(),
							partnerPerson.getName(), partnerPerson.getStoreId(), partnerPerson.getStoreName(),
							partnerPerson.getComId(), partnerPerson.getComName(), partnerPerson.getPositionId(), bonus,
							bonus, "", PersonBonusStatusEnum.WAIT_SUBMIT.getType());
					// 组装插入人员明细数据
					addPersonBonusList.add(personBonus);
				}
			} else {
				log.info("========key:【{}】不存在合伙人员，不进行奖金分配处理", entry.getKey());
			}
			// 组装更新小店奖金明细表数据
			comBonus.setDataStatus(1);// 更新为运算完成
			comBonus.setAuditStatus(0);// 更新为待提交
			updateComBonusList.add(comBonus);
		}
		// 批量插入人员明细
		if (addPersonBonusList.size() > 0) {
			bonusCalculateDao.batchAddPersonBonus(addPersonBonusList);
		}
		// 批量更新小店奖金明细表数据
		if (updateComBonusList.size() > 0) {
			bonusCalculateDao.batchUpdateComBonus(updateComBonusList);
		}
	}

	/**
	 * 奖金计算核心处理
	 * 
	 * @param comBonusList
	 * @param comBonusMap
	 * @author wells
	 * @date 2019年4月4日
	 */
	private void comBonusHandler(List<ComBonus> comBonusList, Map<String, ComBonus> comBonusMap) {
		comBonusList.stream().forEach(comBonus -> {
			comBonusMap.put(comBonus.getStoreId() + "_" + comBonus.getComId(), comBonus);
		});
	}

	/**
	 * 处理单个小店(包括后勤小店)目标数据
	 * 
	 * @param goalValueList
	 * @param totalSaleGoal
	 * @param totalFrontGpGoal
	 * @param totalAfterGpGoal
	 * @param totalDcGoal
	 * @param totalCostGoal
	 */
	private Map<String, GoalSummary> goalHandler(List<GoalValue> goalList) {
		Map<String, GoalSummary> result = new HashMap<String, GoalSummary>();
		Map<String, List<GoalValue>> goalValueMap = goalList.stream()
				.collect(Collectors.groupingBy(GoalValue::getSerialNo));
		if (goalList != null && goalList.size() > 0) {
			for (Map.Entry<String, List<GoalValue>> entry : goalValueMap.entrySet()) {
				List<GoalValue> goalValueList = entry.getValue();
				BigDecimal saleTotalGoal = BigDecimal.ZERO;
				BigDecimal frontGpTotalGoal = BigDecimal.ZERO;
				BigDecimal afterGpTotalGoal = BigDecimal.ZERO;
				BigDecimal dcTotalGoal = BigDecimal.ZERO;
				BigDecimal costTotalGoal = BigDecimal.ZERO;
				BigDecimal attractTotalGoal = BigDecimal.ZERO;
				CostVal costVal = new CostVal();
				for (GoalValue goalValue : goalValueList) {
					if ("销售".equals(goalValue.getSubject())) {
						saleTotalGoal = goalValue.getSubValues();
					}
					if ("毛利额".equals(goalValue.getSubject())) {
						frontGpTotalGoal = goalValue.getSubValues();
					}
					if ("后台".equals(goalValue.getSubject())) {
						afterGpTotalGoal = goalValue.getSubValues();
					}
					if ("招商收入".equals(goalValue.getSubject())) {
						attractTotalGoal = goalValue.getSubValues();
					}
					if ("DC成本".equals(goalValue.getSubject())) {
						dcTotalGoal = goalValue.getSubValues();
					}
					if ("其它费用".equals(goalValue.getSubject())) {
						costVal.setOther(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
					if ("折旧费用".equals(goalValue.getSubject())) {
						costVal.setDepreciation(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
					if ("租赁费用".equals(goalValue.getSubject())) {
						costVal.setLease(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
					if ("销售-水电费用".equals(goalValue.getSubject())) {
						costVal.setHydropower(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
					if ("销售-人力成本".equals(goalValue.getSubject())) {
						costVal.setManpower(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
				}
				GoalSummary goalSummary = new GoalSummary(costVal, saleTotalGoal, frontGpTotalGoal, afterGpTotalGoal,
						attractTotalGoal, dcTotalGoal, costTotalGoal);
				result.put(entry.getKey(), goalSummary);
			}
		}
		return result;
	}

	/**
	 * 处理单个小店(包括后勤小店)考核数据
	 * 
	 * @param assessList
	 * @return
	 * @author wells
	 * @date 2019年4月3日
	 */
	private Map<String, AssessResultSummary> assessHandler(List<AssessResult> assessList) {
		Map<String, AssessResultSummary> result = new HashMap<String, AssessResultSummary>();
		Map<String, List<AssessResult>> assessMap = assessList.stream()
				.collect(Collectors.groupingBy(AssessResult::getSerialNo));
		if (assessList != null && assessList.size() > 0) {
			for (Map.Entry<String, List<AssessResult>> entry : assessMap.entrySet()) {
				List<AssessResult> assessResultList = entry.getValue();
				BigDecimal saleTotal = BigDecimal.ZERO;
				BigDecimal frontGpTotal = BigDecimal.ZERO;
				BigDecimal afterGpTotal = BigDecimal.ZERO;
				BigDecimal attractTotal = BigDecimal.ZERO;
				BigDecimal dcTotalGoal = BigDecimal.ZERO;
				BigDecimal costTotal = BigDecimal.ZERO;
				BigDecimal stock = BigDecimal.ZERO;
				CostVal costVal = new CostVal();
				for (AssessResult assessResult : assessResultList) {
					if ("销售".equals(assessResult.getSubject())) {
						saleTotal = assessResult.getSubValues();
					}
					if ("毛利额".equals(assessResult.getSubject())) {
						frontGpTotal = assessResult.getSubValues();
					}
					if ("后台".equals(assessResult.getSubject())) {
						afterGpTotal = assessResult.getSubValues();
					}
					if ("招商收入".equals(assessResult.getSubject())) {
						attractTotal = assessResult.getSubValues();
					}
					if ("DC成本".equals(assessResult.getSubject())) {
						dcTotalGoal = assessResult.getSubValues();
					}
					if ("其它费用".equals(assessResult.getSubject())) {
						costVal.setOther(assessResult.getSubValues());
						costTotal = costTotal.add(assessResult.getSubValues());
					}
					if ("折旧费用".equals(assessResult.getSubject())) {
						costVal.setDepreciation(assessResult.getSubValues());
						costTotal = costTotal.add(assessResult.getSubValues());
					}
					if ("租赁费用".equals(assessResult.getSubject())) {
						costVal.setLease(assessResult.getSubValues());
						costTotal = costTotal.add(assessResult.getSubValues());
					}
					if ("销售-水电费用".equals(assessResult.getSubject())) {
						costVal.setHydropower(assessResult.getSubValues());
						costTotal = costTotal.add(assessResult.getSubValues());
					}
					if ("销售-人力成本".equals(assessResult.getSubject())) {
						costVal.setManpower(assessResult.getSubValues());
						costTotal = costTotal.add(assessResult.getSubValues());
					}
					if ("库存资金占用成本".equals(assessResult.getSubject())) {
						stock = assessResult.getSubValues();
					}
				}
				AssessResultSummary assessResultSummary = new AssessResultSummary(costVal, saleTotal, frontGpTotal,
						afterGpTotal, attractTotal, dcTotalGoal, costTotal, stock);
				result.put(entry.getKey(), assessResultSummary);
			}
		}
		return result;
	}

	/**
	 * 获取指定月份奖金考核报表
	 *
	 * @param ym
	 * @param provinceId
	 * @param areaId
	 * @param storeId
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月9日
	 *
	 */
	public List<BonusAssessReportResp> getBonusAssessReportByYm(String ym, String provinceId, String areaId,
			String storeId) throws Exception {
		List<BonusAssessReportResp> resultList = new ArrayList<BonusAssessReportResp>();
		// 查询奖金数据
		List<BonusReport> bonusReportList = bonusReportDao.getBonusReportByYmd(ym, provinceId, areaId, storeId);
		if (bonusReportList == null || bonusReportList.size() == 0) {
			ExceptionUtils.wapperBussinessException("奖金数据不存在");
		}
		// 查询考核数据
		List<AssessResult> assessResultList = bonusReportDao.getAssessResultByYmd(ym, provinceId, areaId, storeId);
		// 查询目标合并数据
		List<AssessMergeCnf> assessMergeCnfList = bonusReportDao.getAssessMergerCnfByYm(ym);
		// 查询目标数据
		List<GoalValue> goalList = bonusReportDao.getGoalByYmd(assessMergeCnfList, ym, provinceId, areaId, storeId);
		Map<String, GoalSummary> goalValueMap = this.goalHandler(goalList);
		Map<String, AssessResultSummary> assessResultMap = this.assessHandler(assessResultList);
		if (StringUtil.isNotEmpty(storeId)) {// 以小店为维度
			this.comDataHandler(goalValueMap, assessResultMap, bonusReportList, resultList);
		} else {
			Map<String, List<BonusReport>> bonusMap = null;
			BonusAssessReportResp result = null;
			String dType = null;
			if (StringUtil.isNotEmpty(areaId)) {// 以门店为维度
				dType = "store";
				bonusMap = bonusReportList.stream().collect(Collectors.groupingBy(BonusReport::getStoreId));
			} else if (StringUtil.isNotEmpty(provinceId)) {// 以区域为维度
				dType = "area";
				bonusMap = bonusReportList.stream().collect(Collectors.groupingBy(BonusReport::getAreaId));
			} else {// 以省份为维度
				dType = "province";
				bonusMap = bonusReportList.stream().collect(Collectors.groupingBy(BonusReport::getProvinceId));
			}
			for (Map.Entry<String, List<BonusReport>> entry : bonusMap.entrySet()) {
				result = new BonusAssessReportResp();
				List<BonusReport> list = entry.getValue();
				this.mulitComDataHandler(dType, goalValueMap, assessResultMap, result, list);
				resultList.add(result);
			}

		}
		return resultList;
	}

	private void comDataHandler(Map<String, GoalSummary> goalValueMap, Map<String, AssessResultSummary> assessResultMap,
			List<BonusReport> bonusReportList, List<BonusAssessReportResp> resultList) {
		BonusAssessReportResp result = null;
		GoalSummary goalSummary = null;
		ShareDetail goalDetail = null;
		AssessResultSummary assessResultSummary = null;
		ShareDetail assessDetail = null;
		for (BonusReport bonusReport : bonusReportList) {
			result = new BonusAssessReportResp();
			result.setOrgCode(bonusReport.getComId());
			result.setOrgName(bonusReport.getComName());
			goalSummary = goalValueMap.get(bonusReport.getStoreId() + "_" + bonusReport.getComId());
			// 目标总毛利=目标前台毛利+目标后台收入-目标DC成本
			BigDecimal goalCountGp = goalSummary.getFrontGpTotalGoal().add(goalSummary.getAfterGpTotalGoal())
					.subtract(goalSummary.getDcTotalGoal());
			// 目标利润=目标总毛利+月目标招商收入-月目标总费用
			BigDecimal goalCheckProfit = goalCountGp.add(goalSummary.getAttractTotalGoal())
					.subtract(goalSummary.getCostTotalGoal());
			// 目标总收入=目标总毛利+目标招商收入
			BigDecimal goalIncomeCount = goalCountGp.add(goalSummary.getAttractTotalGoal());
			goalSummary.getCostValObject().unitConvert(new BigDecimal(10000));
			goalDetail = new ShareDetail(goalSummary.getSaleTotalGoal().divide(new BigDecimal(10000)),
					goalSummary.getAttractTotalGoal().divide(new BigDecimal(10000)), goalSummary.getCostValObject(),
					BigDecimal.ZERO, goalSummary.getAfterGpTotalGoal().divide(new BigDecimal(10000)),
					goalSummary.getFrontGpTotalGoal().divide(new BigDecimal(10000)),
					goalCountGp.divide(new BigDecimal(10000)), BigDecimal.ZERO, BigDecimal.ZERO,
					goalIncomeCount.divide(new BigDecimal(10000)),
					goalSummary.getCostTotalGoal().divide(new BigDecimal(10000)),
					goalCheckProfit.divide(new BigDecimal(10000)), "万", "元");

			assessResultSummary = assessResultMap.get(bonusReport.getStoreId() + "_" + bonusReport.getComId());
			// 实际总毛利=实际前台毛利+实际后台收入-实际DC成本
			BigDecimal countGp = assessResultSummary.getFrontGpTotal().add(assessResultSummary.getAfterGpTotal())
					.subtract(assessResultSummary.getDcTotal());
			// 实际总收入=实际总毛利+实际招商收入
			BigDecimal incomeCount = goalCountGp.add(assessResultSummary.getAttractTotal());
			// 实际利润=实际总收入-总费用
			BigDecimal checkProfit = incomeCount.subtract(assessResultSummary.getCostTotal());
			// 超额利润=实际利润-目标利润
			BigDecimal excessProfit = checkProfit.subtract(goalCheckProfit);
			assessResultSummary.getCostValObject().unitConvert(new BigDecimal(10000));
			assessDetail = new ShareDetail(assessResultSummary.getSaleTotal().divide(new BigDecimal(10000)),
					assessResultSummary.getAttractTotal().divide(new BigDecimal(10000)),
					assessResultSummary.getCostValObject(),
					assessResultSummary.getStock().divide(new BigDecimal(10000)),
					assessResultSummary.getAfterGpTotal().divide(new BigDecimal(10000)),
					assessResultSummary.getFrontGpTotal().divide(new BigDecimal(10000)),
					countGp.divide(new BigDecimal(10000)), excessProfit.divide(new BigDecimal(10000)),
					bonusReport.getBonus(), incomeCount.divide(new BigDecimal(10000)),
					assessResultSummary.getCostTotal().divide(new BigDecimal(10000)),
					checkProfit.divide(new BigDecimal(10000)), "万", "元");
			result.setGoalDetail(goalDetail);
			result.setAssessDetail(assessDetail);
			resultList.add(result);
		}
	}

	private void mulitComDataHandler(String dType, Map<String, GoalSummary> goalValueMap,
			Map<String, AssessResultSummary> assessResultMap, BonusAssessReportResp result,
			List<BonusReport> bonusReportList) {
		GoalSummary goalSummary = null;
		AssessResultSummary assessResultSummary = null;
		CostVal goalCostVal = new CostVal();
		BigDecimal goalCostCount = BigDecimal.ZERO;
		CostVal costVal = new CostVal();
		BigDecimal costCount = BigDecimal.ZERO;
		ShareDetail goalDetail = new ShareDetail();
		ShareDetail assessDetail = new ShareDetail();

		for (BonusReport bonusReport : bonusReportList) {
			result.setOrgCode("store".equals(dType) ? bonusReport.getStoreId()
					: "area".equals(dType) ? bonusReport.getAreaId()
							: "province".equals(dType) ? bonusReport.getProvinceId() : null);
			result.setOrgName("store".equals(dType) ? bonusReport.getStoreName()
					: "area".equals(dType) ? bonusReport.getAreaName()
							: "province".equals(dType) ? bonusReport.getProvinceName() : null);
			goalSummary = goalValueMap.get(bonusReport.getStoreId() + "_" + bonusReport.getComId());
			assessResultSummary = assessResultMap.get(bonusReport.getStoreId() + "_" + bonusReport.getComId());
			goalCostVal.add(goalSummary.getCostValObject());
			goalCostCount = goalCostCount.add(goalSummary.getCostTotalGoal());
			// 目标总毛利=目标前台毛利+目标后台收入-目标DC成本
			BigDecimal goalCountGp = goalSummary.getFrontGpTotalGoal().add(goalSummary.getAfterGpTotalGoal())
					.subtract(goalSummary.getDcTotalGoal());
			// 目标利润=目标总毛利+月目标招商收入-月目标总费用
			BigDecimal goalCheckProfit = goalCountGp.add(goalSummary.getAttractTotalGoal())
					.subtract(goalSummary.getCostTotalGoal());
			// 目标总收入=目标总毛利+目标招商收入
			BigDecimal goalIncomeCount = goalCountGp.add(goalSummary.getAttractTotalGoal());
			goalDetail.add(goalSummary.getSaleTotalGoal(), goalSummary.getAttractTotalGoal(), BigDecimal.ZERO,
					goalSummary.getAfterGpTotalGoal(), goalSummary.getFrontGpTotalGoal(), goalCountGp, BigDecimal.ZERO,
					BigDecimal.ZERO, goalIncomeCount, goalCostCount, goalCheckProfit);
			// 实际总毛利=实际前台毛利+实际后台收入-实际DC成本
			BigDecimal countGp = assessResultSummary.getFrontGpTotal().add(assessResultSummary.getAfterGpTotal())
					.subtract(assessResultSummary.getDcTotal());
			// 实际总收入=实际总毛利+实际招商收入
			BigDecimal incomeCount = goalCountGp.add(assessResultSummary.getAttractTotal());
			// 实际利润=实际总收入-总费用
			BigDecimal checkProfit = incomeCount.subtract(assessResultSummary.getCostTotal());
			// 超额利润=实际利润-目标利润
			BigDecimal excessProfit = checkProfit.subtract(goalCheckProfit);
			costVal.add(assessResultSummary.getCostValObject());
			costCount = costCount.add(assessResultSummary.getCostTotal());
			assessDetail.add(assessResultSummary.getSaleTotal(), assessResultSummary.getAttractTotal(),
					assessResultSummary.getStock(), assessResultSummary.getAfterGpTotal(),
					assessResultSummary.getFrontGpTotal(), countGp, excessProfit, bonusReport.getBonus(), incomeCount,
					costCount, checkProfit);
		}
		goalCostVal.unitConvert(new BigDecimal(10000));
		costVal.unitConvert(new BigDecimal(10000));
		goalDetail.unitConvert(new BigDecimal(10000));
		goalDetail.setCostList(goalCostVal);
		assessDetail.unitConvert(new BigDecimal(10000));
		assessDetail.setCostList(costVal);
		result.setAssessDetail(assessDetail);
		result.setGoalDetail(goalDetail);

	}

	public static void main(String[] args) {
		BigDecimal bonusT = new BigDecimal(360.77);
		int personTotalCount = 3;
		BigDecimal bonus = bonusT
				.multiply(new BigDecimal(3).divide(new BigDecimal(personTotalCount), 2, BigDecimal.ROUND_FLOOR));
		System.out.println("bonus:" + bonus);
		System.out.println(bonusT.multiply(new BigDecimal(1)).setScale(2, BigDecimal.ROUND_FLOOR));
		System.out.println(new BigDecimal(360.7).subtract(new BigDecimal(31.10)).setScale(2, BigDecimal.ROUND_HALF_UP));
	}
}
