package com.cs.mobile.api.controller.partner;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.partner.assess.response.BonusAssessReportResp;
import com.cs.mobile.api.model.partner.assess.response.BonusHistoryResp;
import com.cs.mobile.api.model.partner.assess.response.ComPersonBonusResp;
import com.cs.mobile.api.model.partner.assess.response.PersonBonusItemResp;
import com.cs.mobile.api.model.partner.assess.response.StoreAuditResp;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.partner.assess.BonusService;
import com.cs.mobile.api.service.user.UserService;
import com.cs.mobile.common.constant.PersonBonusStatusEnum;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.DateUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({ "unchecked", "rawtypes" })
@Api(value = "bonusPool", tags = { "奖金池接口" })
@RestController
@RequestMapping("/api/bonusPool")
public class BonusPoolController extends AbstractApiController {
	@Autowired
	private BonusService bonusService;
	@Autowired
	UserService userService;

	@ApiOperation(value = "获取小店人员奖金接口", notes = "获取小店人员奖金接口")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getComPersonBonus")
	public DataResult<ComPersonBonusResp> getComPersonBonus(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaId, String storeId, String comId, String personId) {
		ComPersonBonusResp comPersonBonusResp = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "奖金池分配");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}

		try {
			comPersonBonusResp = bonusService.getComPersonBonus(storeId, comId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(comPersonBonusResp);
	}

	@ApiOperation(value = "小店人员奖金调整接口", notes = "小店人员奖金调整接口")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String"),
			@ApiImplicitParam(name = "personBonusListStr", value = "调整对象字符串", required = true, dataType = "String") })
	@PostMapping("/modifyComPersonBonus")
	public DataResult modifyComPersonBonus(HttpServletRequest request, HttpServletResponse response,
			String personBonusListStr) {
		try {
			List<PersonBonusItemResp> personBonusList = JSON.parseArray(personBonusListStr, PersonBonusItemResp.class);
			bonusService.modifyComPersonBonus(personBonusList);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult("调整成功");
	}

	@ApiOperation(value = "获取上月门店审核列表接口", notes = "获取上月门店审核列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getStoreAuditList")
	public DataResult<StoreAuditResp> getStoreAuditList(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaId, String storeId, String personId) {
		StoreAuditResp storeAuditResp = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "奖金池审核");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}

		try {
			storeAuditResp = bonusService.getStoreAuditList(storeId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(storeAuditResp);
	}

	@ApiOperation(value = "奖金分配审核接口", notes = "奖金分配审核接口")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "type", value = "1：通过；2：不通过", required = true, dataType = "String") })
	@PostMapping("/auditBonus")
	public DataResult auditBonus(HttpServletRequest request, HttpServletResponse response, String provinceId,
			String areaId, String storeId, String ym, int type, String comId, String reason) {
		try {
			int status;
			if (type == 1) {// 审核通过
				status = PersonBonusStatusEnum.AUDIT_SUCCEED.getType();
			} else {// 审核不通过
				status = PersonBonusStatusEnum.AUDIT_FAIL.getType();
			}
			bonusService.auditBonus(ym, storeId, comId, status, reason);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult("审核完成");
	}

	@ApiOperation(value = "分页查询奖金历史列表", notes = "分页查询奖金历史列表")
	@GetMapping("/getHistory")
	public DataResult<List<BonusHistoryResp>> getHistory(HttpServletRequest request, HttpServletResponse response,
			int page, int pageSize, String storeId, String comId, String personId) {
		PageResult<BonusHistoryResp> pageResult = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "奖金池查询");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			pageResult = bonusService.getBonusHistory(page, pageSize, storeId, comId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(pageResult);
	}

	@PostMapping("/createData")
	public DataResult createData(HttpServletRequest request, HttpServletResponse response) {
		try {
			int taskCount = bonusService.getTaskCount();
			if (taskCount > 0) {// 有任务正在执行
				ExceptionUtils.wapperBussinessException("同时只能允许一个任务执行，目前已经有任务在执行");
			}
			bonusService.addTask(DateUtil.getCurrentDateTime16Str());
			new Thread() {
				public void run() {
					try {
						bonusService.createData();
					} catch (Exception e) {
						try {
							bonusService.updateTask(3);
						} catch (Exception e1) {
							log.error("奖金池创建数据异常时更新状态异常", e1);
						}
						log.error("奖金池创建数据异常", e);
					}
				}
			}.start();
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult("执行成功，等待完成");
	}

	@ApiOperation(value = "获取指定月核算表接口", notes = "获取指定月核算表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "ym", value = "年月【如：2019-01】", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "provinceId", value = "省份ID", required = false, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "areaId", value = "区域ID", required = false, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "storeId", value = "门店ID", required = false, dataType = "String") })
	@GetMapping("/getBonusAssessReportByYm")
	public DataResult<List<BonusAssessReportResp>> getBonusAssessReportByYm(HttpServletRequest request,
			HttpServletResponse response, String ym, String provinceId, String areaId, String storeId, String personId) {
		List<BonusAssessReportResp> bonusAssessReportRespList = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "核算明细");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
				
		try {
			UserInfo userInfo = getCurUserInfo(request);// 当前登录人
			Map userRoleMap = this.getPartnerUserRoleMap(userInfo, provinceId, areaId, storeId, null);
			if (userRoleMap.get("provinceId") != null) {
				provinceId = userRoleMap.get("provinceId").toString();
			}
			if (userRoleMap.get("areaId") != null) {
				areaId = userRoleMap.get("areaId").toString();
			}
			if (userRoleMap.get("storeId") != null) {
				storeId = userRoleMap.get("storeId").toString();
			}
			bonusAssessReportRespList = bonusService.getBonusAssessReportByYm(ym, provinceId, areaId, storeId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(bonusAssessReportRespList);
	}

	// @ApiOperation(value = "获取门店季度奖金接口", notes = "获取门店季度奖金接口")
	// @ApiImplicitParams({
	// @ApiImplicitParam(paramType = "query", name = "token", value = "用户token",
	// required = true, dataType = "String"),
	// @ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required
	// = true, dataType = "String") })
	// @GetMapping("/getStoreQBonus")
	// public DataResult<StoreBonusResp> getStoreQBonus(HttpServletRequest request,
	// HttpServletResponse response,
	// String provinceId, String areaId, String storeId, String year, int quarter) {
	// StoreBonusResp storeBonusResp = null;
	// try {
	//
	// } catch (Exception e) {
	// return super.handleException(request, response, e);
	// }
	// return DataHandler.jsonResult(storeBonusResp);
	// }

}