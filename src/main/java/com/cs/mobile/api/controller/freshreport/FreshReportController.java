package com.cs.mobile.api.controller.freshreport;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.freshreport.request.FreshRankRequest;
import com.cs.mobile.api.model.freshreport.request.FreshReportBaseRequest;
import com.cs.mobile.api.model.freshreport.response.*;
import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
import com.cs.mobile.api.model.reportPage.response.MemberPermeabilityResponse;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.api.model.salereport.response.AchievingRateResponse;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.freshreport.FreshReportService;
import com.cs.mobile.api.service.user.UserService;
import com.cs.mobile.common.constant.UserTypeEnum;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
@SuppressWarnings("unchecked")
@Api(value = "freshreport", tags = { "生鲜专题报表" })
@RestController
@RequestMapping("/api/freshreport")
public class FreshReportController extends AbstractApiController {
	@Autowired
	private FreshReportService freshReportService;
	@Autowired
	UserService userService;
	/**
	 * 查询生鲜的月销售额
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 */
	@ApiOperation(value = "月销售总额", notes = "月销售总额")
	@PostMapping("/queryMonthStatistics")
	public DataResult<MonthStatisticsResponse> queryMonthStatisticsResponse(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody @ApiParam(value = "月销售总额参数", required = true, name = "param") FreshReportBaseRequest param, String personId) {
		MonthStatisticsResponse monthStatisticsResponse = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "生鲜专题");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
				
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			param.setPersonId(userInfo.getPersonId());
			monthStatisticsResponse = freshReportService.queryMonthStatisticsResponse(param);
			//切换含税数据
			//(0-否，1-是)
			if(1 == param.getTaxType()){
				monthStatisticsResponse.setTaxData();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(monthStatisticsResponse);
	}

	@ApiOperation(value = "会员渗透率", notes = "会员渗透率")
	@PostMapping("/queryMemberPermeability")
	public DataResult<MemberPermeabilityResponse> queryMemberPermeability(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody @ApiParam(value = "会员渗透率参数", required = true, name = "pageRequest") PageRequest pageRequest) {
		DataResult<MemberPermeabilityResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(pageRequest, userInfo);
			setValue(reportCommonParam, pageRequest);
			pageRequest.setPersonId(userInfo.getPersonId());
			dataResult = DataHandler.jsonResult(freshReportService.queryMember(pageRequest));
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	/*@ApiOperation(value = "生鲜会员渗透率明细", notes = "生鲜会员渗透率明细")
	@PostMapping("/queryMemberDetail")
	public DataResult<MemberListResponse> queryMemberDetail(HttpServletRequest request,
															HttpServletResponse response,
															@RequestBody @ApiParam(value = "生鲜基本参数", required = true, name = "pageRequest") FreshReportBaseRequest param) {
		DataResult<MemberListResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			dataResult = DataHandler.jsonResult(freshReportService.queryMemberDetail(param));
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}*/

	@ApiOperation(value = "销售列表", notes = "销售列表")
	@PostMapping("/querySalesList")
	public DataResult<SalesListResponse> querySalesList(HttpServletRequest request, HttpServletResponse response,
			@RequestBody @ApiParam(value = "销售列表参数", required = true, name = "param") FreshReportBaseRequest param) {
		DataResult<SalesListResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			param.setPersonId(userInfo.getPersonId());
			SalesListResponse salesListResponse = freshReportService.querySalesList(param);
			//切换含税数据
			//(0-否，1-是)
			if(1 == param.getTaxType()){
				salesListResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(salesListResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "查询毛利额达成率", notes = "毛利额达成率")
	@PostMapping("/queryFreshRateAchievingRate")
	public DataResult<AchievingRateResponse> queryFreshRateAchievingRate(HttpServletRequest request,
																	HttpServletResponse response,
																	@RequestBody @ApiParam(value = "毛利额达成率参数", required = true, name = "param") FreshReportBaseRequest param){
		AchievingRateResponse achievingRateResponse = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			param.setPersonId(userInfo.getPersonId());
			achievingRateResponse = freshReportService.queryRateAchievingRate(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(achievingRateResponse);
	}

	@ApiOperation(value = "实时生鲜排行榜", notes = "实时生鲜排行榜")
	@PostMapping("/queryFreshRankList")
	public DataResult<FreshRankResponse> queryFreshRankList(HttpServletRequest request, HttpServletResponse response,
			@RequestBody @ApiParam(value = "实时生鲜排行榜参数", required = true, name = "param") FreshRankRequest param) {
		DataResult<FreshRankResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			param.setPersonId(userInfo.getPersonId());
			if(0 == param.getMark()){
				if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
						|| userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
					param.setMark(1);
				} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
					param.setMark(2);
				} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
					param.setMark(3);
				} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
					param.setMark(4);
				} else {
					ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
				}
			}

			FreshRankResponse freshRankResponse = freshReportService.queryFreshRank(param);
			freshRankResponse.setMark(param.getMark());
			//切换含税数据
			//(0-否，1-是)
			if(1 == param.getTaxType()){
				freshRankResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(freshRankResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "销售客流总列表", notes = "销售客流总列表")
	@PostMapping("/queryFreshklStaticsList")
	public DataResult<FreshklStaticsListResponse> queryFreshklStaticsList(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody @ApiParam(value = "销售客流总列表", required = true, name = "param") FreshReportBaseRequest param) {
		DataResult<FreshklStaticsListResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			param.setPersonId(userInfo.getPersonId());
			FreshklStaticsListResponse freshklStaticsListResponse = freshReportService.queryFreshklStaticsList(param);
			dataResult = DataHandler.jsonResult(freshklStaticsListResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "查询周转天数列表", notes = "查询周转天数列表")
	@PostMapping("/queryHblTurnoverDay")
	public DataResult<HblTurnoverDayListResponse> queryHblTurnoverDay(HttpServletRequest request,
																	  HttpServletResponse response,
																	  @RequestBody @ApiParam(value = "周转天数列表参数", required = true, name = "param") FreshReportBaseRequest param) {
		DataResult<HblTurnoverDayListResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			param.setPersonId(userInfo.getPersonId());
			HblTurnoverDayListResponse hblTurnoverDayListResponse = freshReportService.queryHblTurnoverDay(param);
			dataResult = DataHandler.jsonResult(hblTurnoverDayListResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "查询生鲜大类客流列表", notes = "查询生鲜大类客流列表")
	@PostMapping("/queryFreshDeptKlList")
	public DataResult<FreshDeptKlListResponse> queryFreshDeptKlList(HttpServletRequest request,
																	  HttpServletResponse response,
																	  @RequestBody @ApiParam(value = "生鲜大类客流列表参数", required = true, name = "param") FreshReportBaseRequest param) {
		DataResult<FreshDeptKlListResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			param.setPersonId(userInfo.getPersonId());
			FreshDeptKlListResponse freshDeptKlListResponse = freshReportService.queryFreshDeptKlList(param);
			dataResult = DataHandler.jsonResult(freshDeptKlListResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}


	@ApiOperation(value = "查询排名名次", notes = "查询排名名次")
	@PostMapping("/queryFreshRankGrade")
	public DataResult<FreshRankGradeResponse> queryFreshRankGrade(HttpServletRequest request, HttpServletResponse response,
															@RequestBody @ApiParam(value = "排名名次参数", required = true, name = "param") FreshReportBaseRequest param) {
		DataResult<FreshRankGradeResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			param.setPersonId(userInfo.getPersonId());
			int type = 1;
			if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
					|| userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
				type = 1;
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
				type = 2;
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
				type = 3;
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
				type = 4;
			} else {
				ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
			}
			FreshRankGradeResponse freshRankGradeResponse = freshReportService.queryFreshRankGrade(param,type);
			//切换含税数据
			//(0-否，1-是)
			if(1 == param.getTaxType()){
				freshRankGradeResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(freshRankGradeResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}


	@ApiOperation(value = "查询实际汇总周转天数", notes = "查询实际汇总周转天数")
	@PostMapping("/queryActualTurnoverDay")
	public DataResult<TurnoverDayResponse> queryActualTurnoverDay(HttpServletRequest request,
																	  HttpServletResponse response,
																	  @RequestBody @ApiParam(value = "实际汇总周转天数参数", required = true, name = "param") PageRequest param) {
		DataResult<TurnoverDayResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			param.setPersonId(userInfo.getPersonId());
			TurnoverDayResponse turnoverDayResponse = freshReportService.queryActualTurnoverDay(param);
			dataResult = DataHandler.jsonResult(turnoverDayResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}
}
