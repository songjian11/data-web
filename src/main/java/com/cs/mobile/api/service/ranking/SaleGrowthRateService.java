package com.cs.mobile.api.service.ranking;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.cs.mobile.api.model.partner.progress.AccountingItemResult;
import com.cs.mobile.api.model.partner.progress.ShareDetail;
import com.cs.mobile.api.model.ranking.SaleComStoreDTO;
import com.cs.mobile.api.model.ranking.SaleDeptItemDTO;
import com.cs.mobile.api.model.user.UserInfo;

public interface SaleGrowthRateService {
	/**
	 * 查询大店销售排名
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public List<SaleComStoreDTO> getStoreIncreaseList(Map<String, String> paramMap, UserInfo userInfo) throws Exception;

	/**
	 * 查询小店销售排名
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public List<SaleComStoreDTO> getComIncreaseList(Map<String, String> paramMap, UserInfo userInfo) throws Exception;

	/**
	 * 保存排名点赞
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public void saveGiveRanking(Map<String, String> paramMap) throws Exception;

	/**
	 * 获取店群列表-下拉框
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public List<SaleComStoreDTO> getStoreGroupList(Map<String, String> paramMap) throws Exception;

	/**
	 * 获取小店列表-下拉框
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public List<SaleComStoreDTO> getComStoreList(Map<String, String> paramMap) throws Exception;

	/**
	 * 单品排名top10-数据列表
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public List<SaleDeptItemDTO> getDeptItemTop10(Map<String, String> paramMap) throws Exception;

	/**
	 * 奖金池-明细处理
	 * 
	 * @param accountingActual
	 * @param accountingGoal
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public void operationAccounting(AccountingItemResult accountingActual, AccountingItemResult accountingGoal)
			throws Exception;

	/**
	 * 奖金池-计算器
	 * 
	 * @param share
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public AccountingItemResult refreshCalculation(BigDecimal goalProfit, ShareDetail share,
			Map<String, Object> paramMap) throws Exception;
}
