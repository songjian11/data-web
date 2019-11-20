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
import com.cs.mobile.api.model.partner.dayreport.response.CompareData;
import com.cs.mobile.api.model.partner.dayreport.response.TimeReport;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.partner.dayreport.DayCompareReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 今日和去年今日比较报表
 * 
 * @author wells
 * @date 2019年3月13日
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Api(value = "dayCompare", tags = { "今日和去年今日比较报表接口" })
@RestController
@RequestMapping("/api/report/dayCompare")
public class DayCompareReportController extends AbstractApiController {
	@Autowired
	private DayCompareReportService dayCompareReportService;

	@ApiOperation(value = "获取大类数据", notes = "获取大类数据")
	@GetMapping("/getDeptList")
	public DataResult<List<CompareData>> getDeptList(HttpServletRequest request, HttpServletResponse response,
			String enterpriseId, String provinceId, String areaId, String storeId, String comId) {
		List<CompareData> compareDataList = null;
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
				compareDataList = dayCompareReportService.getComDeptList(storeId, comId);
			} else if (2 == type) {
				compareDataList = dayCompareReportService.getStoreDeptList(storeId);
			} else if (3 == type) {
				compareDataList = dayCompareReportService.getAreaDeptList(areaId);
			} else if (4 == type) {
				compareDataList = dayCompareReportService.getProvinceDeptList(provinceId);
			} else if (5 == type) {
				compareDataList = dayCompareReportService.getAllDeptList();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(compareDataList);
	}

	@ApiOperation(value = "获取时间段数据", notes = "获取时间段数据")
	@GetMapping("/getTimeList")
	public DataResult<List<TimeReport>> getTimeList(HttpServletRequest request, HttpServletResponse response,
			String enterpriseId, String provinceId, String areaId, String storeId, String comId) {
		List<TimeReport> timeReportList = null;
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
				timeReportList = dayCompareReportService.getComTimeList(storeId, comId);
			} else if (2 == type) {
				timeReportList = dayCompareReportService.getStoreTimeList(storeId);
			} else if (3 == type) {
				timeReportList = dayCompareReportService.getAreaTimeList(areaId);
			} else if (4 == type) {
				timeReportList = dayCompareReportService.getProvinceTimeList(provinceId);
			} else if (5 == type) {
				timeReportList = dayCompareReportService.getAllTimeList();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(timeReportList);
	}
}
