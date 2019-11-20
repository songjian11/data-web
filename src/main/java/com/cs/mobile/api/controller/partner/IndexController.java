package com.cs.mobile.api.controller.partner;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.partner.battle.Accounting;
import com.cs.mobile.api.model.partner.progress.AccountingItemResult;
import com.cs.mobile.api.model.partner.progress.ProgressReportResult;
import com.cs.mobile.api.model.partner.progress.ShareDetail;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.IndexService;
import com.cs.mobile.api.service.partner.assess.BonusService;
import com.cs.mobile.api.service.partner.progress.AreaProgressService;
import com.cs.mobile.api.service.partner.progress.ComProgressService;
import com.cs.mobile.api.service.partner.progress.ProvinceProgressService;
import com.cs.mobile.api.service.partner.progress.StoreProgressService;
import com.cs.mobile.api.service.user.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Api(value = "index", tags = { "首页接口" })
@RestController
@RequestMapping("/api/index")
@Slf4j
public class IndexController extends AbstractApiController {

	@Autowired
	IndexService indexService;
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
	@Autowired
	BonusService bonusService;

	@ApiOperation(value = "首页各项指标", notes = "销售，毛利，利润，费用，分享金额")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getIndexValue")
	public DataResult<ProgressReportResult> getIndexValue(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaId, String storeId, String comId, String startDate, String endDate,
			String personId) {
		ProgressReportResult progressReportResult = null;

		// 记录访问日志
		try {
			userService.addPersonLog(personId, "首页/我的目标");
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
				progressReportResult = comProgressService.getProgressReport(userInfo.getPartnerUserInfo().getGroupId(),
						storeId, comId, startDate, endDate);
			} else if (3 == type) {
				progressReportResult = storeProgressService.getProgressReport(areaId, startDate, endDate);
			} else if (4 == type) {
				progressReportResult = areaProgressService.getProgressReport(provinceId, startDate, endDate);
			} else if (5 == type) {
				progressReportResult = provinceProgressService.getProgressReport(startDate, endDate);
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(progressReportResult);
	}

	@ApiOperation(value = "首页历史记录", notes = "销售，利润，分享金额")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getHistory")
	public DataResult<ProgressReportResult> getHistory(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaId, String storeId, String comId, String ym, String personId) {
		ProgressReportResult progressReportResult = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "首页/历史记录");
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
			if (1 == type) {
				progressReportResult = bonusService.getComBonusByComId(comId, storeId, ym);
			} else if (2 == type) {
				progressReportResult = bonusService.getComBonusByStoreId(storeId, ym);
			} else if (3 == type) {
				progressReportResult = bonusService.getComBonusByAreaId(areaId, ym);
			} else if (4 == type) {
				progressReportResult = bonusService.getComBonusByProvinceId(provinceId, ym);
			} else if (5 == type) {
				progressReportResult = bonusService.getAllComBonus(ym);
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(progressReportResult);
	}

	@ApiOperation(value = "分享明细", notes = "费用合计，收入合计，考核利润，各项费用")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getShareDetail")
	public DataResult<ShareDetail> getShareDetail(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaId, String storeId, String comId, String startDate, String endDate) {
		ShareDetail shareDetail = null;
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
				shareDetail = comProgressService.getProgressReportDetail(userInfo.getPartnerUserInfo().getGroupId(),
						storeId, comId, startDate, endDate);
			} else if (3 == type) {
				shareDetail = storeProgressService.getProgressReportDetail(areaId, startDate, endDate);
			} else if (4 == type) {
				shareDetail = areaProgressService.getProgressReportDetail(provinceId, startDate, endDate);
			} else if (5 == type) {
				shareDetail = provinceProgressService.getProgressReportDetail(startDate, endDate);
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(shareDetail);
	}

	@ApiOperation(value = "核算标准明细", notes = "科目，目标，实际")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getAccountingDesc")
	public DataResult<AccountingItemResult> getAccountingDesc(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaId, String storeId, String comId, String startDate, String endDate) {
		AccountingItemResult accountingItemResult = null;
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
				accountingItemResult = comProgressService.getAccountingItem(userInfo.getPartnerUserInfo().getGroupId(),
						storeId, comId, startDate, endDate);
			} else if (3 == type) {
				accountingItemResult = storeProgressService.getAccountingItem(areaId, startDate, endDate);
			} else if (4 == type) {
				accountingItemResult = areaProgressService.getAccountingItem(provinceId, startDate, endDate);
			} else if (5 == type) {
				accountingItemResult = provinceProgressService.getAccountingItem(startDate, endDate);
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(accountingItemResult);
	}

	@ApiOperation(value = "核算标准表格", notes = "表格")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getAccountingTable")
	public DataResult<List<Accounting>> getAccountingTable(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaId, String storeId, String comId, String startDate, String endDate) {
		List<Accounting> list = null;
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
				list = comProgressService.getAccountingList(storeId, comId);
			} else if (3 == type) {
				list = storeProgressService.getAccountingList(areaId);
			} else if (4 == type) {
				list = areaProgressService.getAccountingList(provinceId);
			} else if (5 == type) {
				list = provinceProgressService.getAccountingList();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}
}