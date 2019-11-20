package com.cs.mobile.api.controller.ranking;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cs.mobile.api.model.partner.progress.AccountingItemResult;
import com.cs.mobile.api.model.partner.progress.CostVal;
import com.cs.mobile.api.model.partner.progress.ShareDetail;
import com.cs.mobile.api.model.ranking.SaleComStoreDTO;
import com.cs.mobile.api.model.ranking.SaleDeptItemDTO;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.partner.progress.AreaProgressService;
import com.cs.mobile.api.service.partner.progress.ComProgressService;
import com.cs.mobile.api.service.partner.progress.ProvinceProgressService;
import com.cs.mobile.api.service.partner.progress.StoreProgressService;
import com.cs.mobile.api.service.ranking.SaleGrowthRateService;
import com.cs.mobile.api.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.controller.partner.UserController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Api(value = "销售增长率排名接口", tags = { "销售增长率排名接口" })
@RestController
@RequestMapping("/api/growthRate")
@Slf4j
public class SaleGrowthRateController extends AbstractApiController {

	@Autowired
	SaleGrowthRateService saleGrowthRateService;

	@Autowired
	ComProgressService comProgressService;

	@Autowired
	StoreProgressService storeProgressService;

	@Autowired
	AreaProgressService areaProgressService;

	@Autowired
	ProvinceProgressService provinceProgressService;

	@Autowired
	UserService userService;

	@ApiOperation(value = "大店增长率排名", notes = "销售增长率排名")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "groupId", value = "店群", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "personId", value = "用户ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "keyword", value = "关键字", dataType = "String") })
	@GetMapping("/storeIncreaseList")
	public DataResult<List<SaleComStoreDTO>> getStoreIncreaseList(HttpServletRequest request,
			HttpServletResponse response, String groupId, String keyword) {
		List<SaleComStoreDTO> list = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			Map paramMap = new HashMap<String, String>();
			paramMap.put("userId", userInfo.getPersonId());// 用户ID
			paramMap.put("groupId", groupId);// 店群ID
			paramMap.put("keyword", keyword);// 关键字
			list = saleGrowthRateService.getStoreIncreaseList(paramMap, userInfo);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	@ApiOperation(value = "小店增长率排名", notes = "销售增长率排名")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "groupId", value = "店群ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "comId", value = "小店ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "personId", value = "用户ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "keyword", value = "关键字", dataType = "String") })
	@GetMapping("/comIncreaseList")
	public DataResult<List<SaleComStoreDTO>> getComIncreaseList(HttpServletRequest request,
			HttpServletResponse response, String groupId, String comId, String keyword, String personId) {
		List<SaleComStoreDTO> list = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "小店排名");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}

		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			Map paramMap = new HashMap<String, String>();
			paramMap.put("userId", userInfo.getPersonId());// 用户ID
			paramMap.put("groupId", groupId);// 店群ID
			paramMap.put("comId", comId);// 小店ID
			paramMap.put("keyword", keyword);// 关键字
			list = saleGrowthRateService.getComIncreaseList(paramMap, userInfo);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	@ApiOperation(value = "排名点赞", notes = "销售增长率排名")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "personId", value = "用户ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "storeId", value = "大店ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "comId", value = "小店ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "ifGive", value = "是否点赞", required = true, dataType = "String") })
	@PostMapping("/saveGiveRanking")
	public DataResult<String> saveGiveRanking(HttpServletRequest request, HttpServletResponse response, String storeId,
			String comId, String ifGive) {
		String result = "失败";
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			Map paramMap = new HashMap<String, String>();
			paramMap.put("userId", userInfo.getPersonId());// 用户ID
			paramMap.put("storeId", storeId);// 大店ID
			paramMap.put("comId", comId);// 小店ID
			paramMap.put("ifGive", ifGive);// 是否点赞
			saleGrowthRateService.saveGiveRanking(paramMap);
			result = "成功";
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(result);
	}

	@ApiOperation(value = "店群下拉框", notes = "销售增长率排名")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "personId", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getStoreGroupList")
	public DataResult<List<SaleComStoreDTO>> getStoreGroupList(HttpServletRequest request,
			HttpServletResponse response) {
		List<SaleComStoreDTO> list = null;
		try {
			Map paramMap = new HashMap<String, String>();
			list = saleGrowthRateService.getStoreGroupList(paramMap);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	@ApiOperation(value = "小店下拉框", notes = "销售增长率排名")
	@ApiImplicitParams({ @ApiImplicitParam(paramType = "query", name = "groupId", value = "店群ID", dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "storeId", value = "大店ID", dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "comId", value = "小店ID", dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "personId", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getComStoreList")
	public DataResult<List<SaleComStoreDTO>> getComStoreList(HttpServletRequest request, HttpServletResponse response,
			String groupId, String storeId, String comId, String personId) {
		List<SaleComStoreDTO> list = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "大店排名");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}

		try {
			Map paramMap = new HashMap<String, String>();
			paramMap.put("groupId", groupId);// 店群ID
			paramMap.put("storeId", storeId);// 大店ID
			paramMap.put("comId", comId);// 小店ID
			list = saleGrowthRateService.getComStoreList(paramMap);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	@ApiOperation(value = "大类单品TOP10", notes = "销售增长率排名")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "storeId", value = "大店ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "comId", value = "小店ID", dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "personId", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getCategoryTop10")
	public DataResult<List<SaleDeptItemDTO>> getDeptItemTop10(HttpServletRequest request, HttpServletResponse response,
			String storeId, String comId, String personId) {
		List<SaleDeptItemDTO> list = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "大类单品Top10");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		
		try {
			Map paramMap = new HashMap<String, String>();
			paramMap.put("storeId", storeId);// 大店ID
			paramMap.put("comId", comId);// 小店ID
			list = saleGrowthRateService.getDeptItemTop10(paramMap);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	@ApiOperation(value = "奖金池明细", notes = "科目，目标，实际")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "personId", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getAccountingDesc")
	public DataResult<AccountingItemResult> getAccountingDesc(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaId, String storeId, String comId, String startDate, String endDate,
			String personId) {
		AccountingItemResult accountingGoal = null;
		AccountingItemResult accountingActual = null;
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastDate = new SimpleDateFormat("yyyy-MM-dd").format(ca.getTime());// 月最后一天

		// 记录访问日志
		try {
			userService.addPersonLog(personId, "计算器访问");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}

		try {
			UserInfo userInfo = getCurUserInfo(request);// 当前登录人
			Map userRoleMap = this.getPartnerUserRoleMap(userInfo, provinceId, areaId, storeId, comId);
			int type = 0;
			if (userRoleMap.get("type") != null) {
				type = Integer.parseInt(userRoleMap.get("type").toString());
			}
			if (userRoleMap.get("provinceId") != null) {
				provinceId = userRoleMap.get("provinceId").toString();
			}
			if (userRoleMap.get("areaId") != null) {
				areaId = userRoleMap.get("areaId").toString();
			}
			if (userRoleMap.get("storeId") != null) {
				storeId = userRoleMap.get("storeId").toString();
			}
			if (userRoleMap.get("comId") != null) {
				comId = userRoleMap.get("comId").toString();
			}
			if (1 == type || 2 == type) {
				accountingGoal = comProgressService.getAccountingItem(userInfo.getPartnerUserInfo().getGroupId(),
						storeId, comId, startDate, lastDate);
				accountingActual = comProgressService.getAccountingItem(userInfo.getPartnerUserInfo().getGroupId(),
						storeId, comId, startDate, endDate);
			} else if (3 == type) {
				accountingGoal = storeProgressService.getAccountingItem(areaId, startDate, lastDate);
				accountingActual = storeProgressService.getAccountingItem(areaId, startDate, endDate);
			} else if (4 == type) {
				accountingGoal = areaProgressService.getAccountingItem(provinceId, startDate, lastDate);
				accountingActual = areaProgressService.getAccountingItem(provinceId, startDate, endDate);
			} else if (5 == type) {
				accountingGoal = provinceProgressService.getAccountingItem(startDate, lastDate);
				accountingActual = provinceProgressService.getAccountingItem(startDate, endDate);
			}
			saleGrowthRateService.operationAccounting(accountingActual, accountingGoal);// 封装-月目标值和实际值
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(accountingActual);
	}

	@ApiOperation(value = "奖金池计算器接口", notes = "奖金池计算器")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "personId", value = "用户ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "groupId", value = "店群ID", dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "comId", value = "小店ID", dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "moneyStr", value = "各项金额字符串", required = true, dataType = "String") })
	@PostMapping("/refreshCalculation")
	public DataResult<AccountingItemResult> refreshCalculation(HttpServletRequest request, HttpServletResponse response,
			String moneyStr, String groupId, String comId, String personId) {
		CostVal cost = new CostVal();
		ShareDetail share = new ShareDetail();
		AccountingItemResult accountingItem = null;

		// 记录访问日志
		try {
			userService.addPersonLog(personId, "计算器（计算）");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}

		try {
			Map paramMap = new HashMap<String, Object>();
			paramMap.put("groupId", groupId);// 店群ID
			paramMap.put("comId", comId);// 小店ID
			JSONObject moneyObj = JSON.parseObject(moneyStr);
			BigDecimal goalProfit = moneyObj.getBigDecimal("goalProfit");// 月目标利润
			BigDecimal countGp = BigDecimal.ZERO;// 总毛利
			Object gpChangeObj = moneyObj.get("gpChange");
			if (gpChangeObj != null) {
				if (Integer.parseInt(gpChangeObj.toString()) == 1) {// 计算器改变了毛利值
					countGp = moneyObj.getBigDecimal("frontGp").add(moneyObj.getBigDecimal("afterGp"));
				} else {// 计算器没有改变
					countGp = moneyObj.getBigDecimal("countGp");
				}
			}
			share.setCountGp(countGp);
			share.setSale(moneyObj.getBigDecimal("sale"));// 销售
			share.setFrontGp(moneyObj.getBigDecimal("frontGp"));// 前台毛利
			share.setAfterGp(moneyObj.getBigDecimal("afterGp"));// 后台毛利
			share.setAttract(moneyObj.getBigDecimal("attract"));// 招商收入
			cost.setManpower(moneyObj.getBigDecimal("manpower"));// 人力
			cost.setLease(moneyObj.getBigDecimal("lease"));// 租赁
			cost.setDepreciation(moneyObj.getBigDecimal("depreciation"));// 折旧
			cost.setHydropower(moneyObj.getBigDecimal("hydropower"));// 水电
			cost.setOther(moneyObj.getBigDecimal("other"));// 其他
			share.setCostList(cost);
			share.setStock(moneyObj.getBigDecimal("stock"));// 库存
			accountingItem = saleGrowthRateService.refreshCalculation(goalProfit, share, paramMap);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(accountingItem);
	}
}
