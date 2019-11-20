package com.cs.mobile.api.controller.mreport;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.controller.ranking.SaleGrowthRateController;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.mreport.response.AreaGroupReportResp;
import com.cs.mobile.api.model.mreport.response.DateTitleResp;
import com.cs.mobile.api.model.mreport.response.PermeatioResp;
import com.cs.mobile.api.model.mreport.response.StoreDayDeptReportResp;
import com.cs.mobile.api.model.mreport.response.StoreDayTimeReportResp;
import com.cs.mobile.api.service.mreport.MobileReportService;
import com.cs.mobile.api.service.user.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Api(value = "mreport", tags = { "移动报表接口" })
@RestController
@RequestMapping("/api/mreport")
@Slf4j
public class MobileReportController extends AbstractApiController {
	@Autowired
	MobileReportService mobileReportService;
	
	@Autowired
	UserService userService;

	@ApiOperation(value = "获取所有省份", notes = "获取所有省份")
	@GetMapping("/getAllProvince")
	public DataResult<List<Organization>> getAllProvince(HttpServletRequest request, HttpServletResponse response) {
		List<Organization> list = null;
		try {
			list = mobileReportService.getAllProvince();
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	@ApiOperation(value = "根据省份获取所有区域", notes = "根据省份获取所有区域")
	@GetMapping("/getAreaByP")
	public DataResult<List<Organization>> getAreaByP(HttpServletRequest request, HttpServletResponse response,
			String provinceId) {
		List<Organization> list = null;
		try {
			list = mobileReportService.getAreaByP(provinceId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	@ApiOperation(value = "获取大区战报表头信息", notes = "获取大区战报表头信息")
	@GetMapping("/getDateTitle")
	public DataResult<DateTitleResp> getDateTitle(HttpServletRequest request, HttpServletResponse response,
			String provinceId) {
		DateTitleResp dateTitleResp = null;
		try {
			dateTitleResp = mobileReportService.getDateTitleResp();
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(dateTitleResp);
	}

	@ApiOperation(value = "获取大区战报表头信息", notes = "获取大区战报表头信息")
	@GetMapping("/getAreaGroupReport")
	public DataResult<AreaGroupReportResp> getAreaGroupReport(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String personId) {
		AreaGroupReportResp areaGroupReportResp = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "大区战报");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		
		try {
			areaGroupReportResp = mobileReportService.getAreaGroupReportResp();
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(areaGroupReportResp);
	}

	@ApiOperation(value = "分页获取渗透率报表", notes = "分页获取渗透率报表")
	@PostMapping("/getPermeatio")
	public DataResult<List<PermeatioResp>> getPermeatio(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaId, int page, int pageSize, String personId) {
		PageResult<PermeatioResp> pageResult = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "渗透率报表");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			pageResult = mobileReportService.getPermeatioResp(provinceId, areaId, page, pageSize);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(pageResult);
	}

	@ApiOperation(value = "门店时段报表", notes = "门店时段报表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "storeId", value = "门店ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "ymd", value = "年月日【格式：20190101】", required = true, dataType = "String") })
	@GetMapping("/getDayTimeReport")
	public DataResult<StoreDayTimeReportResp> getDayTimeReport(HttpServletRequest request, HttpServletResponse response,
			String storeId, String ymd) {
		StoreDayTimeReportResp storeDayTimeReportResp = null;
		try {
			storeDayTimeReportResp = mobileReportService.getDayTimeReport(storeId, ymd);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(storeDayTimeReportResp);
	}

	@ApiOperation(value = "门店大类报表", notes = "门店大类报表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "storeId", value = "门店ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "ymd", value = "年月日【格式：20190101】", required = true, dataType = "String") })
	@GetMapping("/getDayDeptReport")
	public DataResult<StoreDayDeptReportResp> getDayDeptReport(HttpServletRequest request, HttpServletResponse response,
			String storeId, String ymd, String personId) {
		StoreDayDeptReportResp storeDayDeptReportResp = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "大类时段趋势");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			storeDayDeptReportResp = mobileReportService.getDayDeptReport(storeId, ymd);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(storeDayDeptReportResp);
	}

}
