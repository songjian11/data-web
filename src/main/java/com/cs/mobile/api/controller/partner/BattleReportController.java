package com.cs.mobile.api.controller.partner;

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
import com.cs.mobile.api.model.partner.battle.BattleReportResult;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.partner.battle.GrossMarginBattleService;
import com.cs.mobile.api.service.partner.battle.PassengerFlowBattleService;
import com.cs.mobile.api.service.partner.battle.SaleBattleService;
import com.cs.mobile.api.service.user.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 战报接口
 * 
 * @author wells
 * @date 2018年11月19日
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Api(value = "battleReport", tags = { "战报接口" })
@RestController
@RequestMapping("/api/battle")
@Slf4j
public class BattleReportController extends AbstractApiController {

	@Autowired
	SaleBattleService saleBattleService;
	@Autowired
	PassengerFlowBattleService passengerFlowBattleService;
	@Autowired
	GrossMarginBattleService grossMarginBattleService;
	@Autowired
	UserService userService;

	@ApiOperation(value = "销售日战报", notes = "销售日战报")
	@GetMapping("/daySale")
	public DataResult<BattleReportResult> daySale(HttpServletRequest request, HttpServletResponse response,
			String enterpriseId, String provinceId, String areaId, String storeId, String comId, String personId) {
		BattleReportResult battleResultReport = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "销售/目标跟进（选日）");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}

		try {
			UserInfo userInfo = this.getCurUserInfo(request);
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
				battleResultReport = saleBattleService.getDeptTodaySaleReport(storeId, comId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (2 == type) {
				battleResultReport = saleBattleService.getComTodaySaleReport(storeId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (3 == type) {
				battleResultReport = saleBattleService.getStoreTodaySaleReport(areaId);
			} else if (4 == type) {
				battleResultReport = saleBattleService.getAreaTodaySaleReport(provinceId);
			} else if (5 == type) {
				battleResultReport = saleBattleService.getProvinceTodaySaleReport();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(battleResultReport);
	}

	@ApiOperation(value = "销售月战报", notes = "销售月战报")
	@GetMapping("/monthSale")
	public DataResult<BattleReportResult> monthSale(HttpServletRequest request, HttpServletResponse response,
			String enterpriseId, String provinceId, String areaId, String storeId, String comId, String personId) {
		BattleReportResult battleResultReport = null;

		// 记录访问日志
		try {
			userService.addPersonLog(personId, "销售/目标跟进(选月)");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}

		try {
			UserInfo userInfo = this.getCurUserInfo(request);
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
				battleResultReport = saleBattleService.getDeptCurMonthSaleReport(storeId, comId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (2 == type) {
				battleResultReport = saleBattleService.getComCurMonthSaleReport(storeId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (3 == type) {
				battleResultReport = saleBattleService.getStoreCurMonthSaleReport(areaId);
			} else if (4 == type) {
				battleResultReport = saleBattleService.getAreaCurMonthSaleReport(provinceId);
			} else if (5 == type) {
				battleResultReport = saleBattleService.getProvinceCurMonthSaleReport();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(battleResultReport);
	}

	@ApiOperation(value = "毛利日战报", notes = "毛利日战报")
	@GetMapping("/dayGm")
	public DataResult<BattleReportResult> dayGm(HttpServletRequest request, HttpServletResponse response,
			String enterpriseId, String provinceId, String areaId, String storeId, String comId) {
		BattleReportResult battleResultReport = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
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
				battleResultReport = grossMarginBattleService.getDeptTodayGMReport(storeId, comId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (2 == type) {
				battleResultReport = grossMarginBattleService.getComTodayGMReport(storeId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (3 == type) {
				battleResultReport = grossMarginBattleService.getStoreTodayGMReport(areaId);
			} else if (4 == type) {
				battleResultReport = grossMarginBattleService.getAreaTodayGMReport(provinceId);
			} else if (5 == type) {
				battleResultReport = grossMarginBattleService.getProvinceTodayGMReport();
			}

		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(battleResultReport);
	}

	@ApiOperation(value = "毛利月战报", notes = "毛利月战报")
	@GetMapping("/monthGm")
	public DataResult<BattleReportResult> monthGm(HttpServletRequest request, HttpServletResponse response,
			String enterpriseId, String provinceId, String areaId, String storeId, String comId) {
		BattleReportResult battleResultReport = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
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
				battleResultReport = grossMarginBattleService.getDeptCurMonthGMReport(storeId, comId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (2 == type) {
				battleResultReport = grossMarginBattleService.getComCurMonthGMReport(storeId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (3 == type) {
				battleResultReport = grossMarginBattleService.getStoreCurMonthGMReport(areaId);
			} else if (4 == type) {
				battleResultReport = grossMarginBattleService.getAreaCurMonthGMReport(provinceId);
			} else if (5 == type) {
				battleResultReport = grossMarginBattleService.getProvinceCurMonthGMReport();
			}

		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(battleResultReport);
	}

	@ApiOperation(value = "客流日战报", notes = "客流日战报")
	@GetMapping("/dayPf")
	public DataResult<BattleReportResult> dayPf(HttpServletRequest request, HttpServletResponse response,
			String enterpriseId, String provinceId, String areaId, String storeId, String comId) {
		BattleReportResult battleResultReport = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
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
				battleResultReport = passengerFlowBattleService.getDeptTodayPFReport(storeId, comId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (2 == type) {
				battleResultReport = passengerFlowBattleService.getComTodayPFReport(storeId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (3 == type) {
				battleResultReport = passengerFlowBattleService.getStoreTodayPFReport(areaId);
			} else if (4 == type) {
				battleResultReport = passengerFlowBattleService.getAreaTodayPFReport(provinceId);
			} else if (5 == type) {
				battleResultReport = passengerFlowBattleService.getProvinceTodayPFReport();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(battleResultReport);
	}

	@ApiOperation(value = "客流月战报", notes = "客流月战报")
	@GetMapping("/monthPf")
	public DataResult<BattleReportResult> monthPf(HttpServletRequest request, HttpServletResponse response,
			String enterpriseId, String provinceId, String areaId, String storeId, String comId) {
		BattleReportResult battleResultReport = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
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
				battleResultReport = passengerFlowBattleService.getDeptCurMonthPFReport(storeId, comId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (2 == type) {
				battleResultReport = passengerFlowBattleService.getComCurMonthPFReport(storeId,
						userInfo.getPartnerUserInfo().getIsCompare());
			} else if (3 == type) {
				battleResultReport = passengerFlowBattleService.getStoreCurMonthPFReport(areaId);
			} else if (4 == type) {
				battleResultReport = passengerFlowBattleService.getAreaCurMonthPFReport(provinceId);
			} else if (5 == type) {
				battleResultReport = passengerFlowBattleService.getProvinceCurMonthPFReport();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(battleResultReport);
	}

}
