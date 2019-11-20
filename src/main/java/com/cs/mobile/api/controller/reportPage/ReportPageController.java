package com.cs.mobile.api.controller.reportPage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.common.CsmbOrg;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.dailyreport.request.DailyReportRequest;
import com.cs.mobile.api.model.freshreport.response.MemberListResponse;
import com.cs.mobile.api.model.freshreport.response.TurnoverDayResponse;
import com.cs.mobile.api.model.reportPage.CsmbStoreModel;
import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
import com.cs.mobile.api.model.reportPage.request.RankParamRequest;
import com.cs.mobile.api.model.reportPage.request.ReportParamRequest;
import com.cs.mobile.api.model.reportPage.response.FreshResponse;
import com.cs.mobile.api.model.reportPage.response.HomeApplianceResponse;
import com.cs.mobile.api.model.reportPage.response.MemberPermeabilityResponse;
import com.cs.mobile.api.model.reportPage.response.NotFreshResponse;
import com.cs.mobile.api.model.reportPage.response.RankInfoResponse;
import com.cs.mobile.api.model.reportPage.response.ReportDataResponse;
import com.cs.mobile.api.model.reportPage.response.TotalSalesAndProfitResponse;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.api.model.salereport.response.AchievingRateResponse;
import com.cs.mobile.api.model.user.User;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.common.CommonService;
import com.cs.mobile.api.service.reportPage.ReportOrgService;
import com.cs.mobile.api.service.reportPage.ReportPageService;
import com.cs.mobile.api.service.reportPage.ReportUserDeptService;
import com.cs.mobile.api.service.salereport.SaleReportService;
import com.cs.mobile.api.service.user.UserService;
import com.cs.mobile.common.constant.UserTypeEnum;
import com.cs.mobile.common.exception.api.ExceptionUtils;

import ch.qos.logback.classic.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Api(value = "reportPage", tags = { "报表首页" })
@RestController
@RequestMapping("/api/headPage")
@Slf4j
public class ReportPageController extends AbstractApiController {
	@Autowired
	private ReportPageService headPageService;
	@Autowired
	private ReportOrgService reportOrgService;
	@Autowired
	private ReportUserDeptService reportUserDeptService;
	@Autowired
	private SaleReportService saleReportService;
	@Autowired
	UserService userService;
	@Autowired
	CommonService commonService;
	

	@ApiOperation(value = "总销售额和总毛利率查询", notes = "总销售额和总毛利率查询")
	@PostMapping("/queryTotalSalesAndProfit")
	public DataResult<TotalSalesAndProfitResponse> queryTotalSalesAndProfit(HttpServletRequest request,
																			HttpServletResponse response,
																			@RequestBody @ApiParam(value = "首页", required = true, name = "pageRequest") PageRequest pageRequest, String personId) {
		TotalSalesAndProfitResponse totalSalesAndProfit = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "报表首页");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(pageRequest, userInfo);
            setValue(reportCommonParam, pageRequest);
			totalSalesAndProfit = headPageService.queryTotalSalesAndProfit(pageRequest);
			//切换含税数据
			//(0-否，1-是)
			if(1 == pageRequest.getTaxType()){
				totalSalesAndProfit.setTaxData();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(totalSalesAndProfit);
	}

	@ApiOperation(value = "家电总销售额和总毛利率查询", notes = "家电总销售额和总毛利率查询")
	@PostMapping("/queryHomeAppliance")
	public DataResult<HomeApplianceResponse> queryHomeAppliance(HttpServletRequest request,
																HttpServletResponse response,
																@RequestBody @ApiParam(value = "首页基本参数", required = true, name = "pageRequest") PageRequest pageRequest) {
		DataResult<HomeApplianceResponse> dataResult = null;
		try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(pageRequest, userInfo);
            setValue(reportCommonParam, pageRequest);
			HomeApplianceResponse homeApplianceResponse = headPageService.queryHomeAppliance(pageRequest);
			//切换含税数据
			//(0-否，1-是)
			if(1 == pageRequest.getTaxType()){
				homeApplianceResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(homeApplianceResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "生鲜", notes = "生鲜")
	@PostMapping("/queryFresh")
	public DataResult<FreshResponse> queryFresh(HttpServletRequest request, HttpServletResponse response,
										@RequestBody @ApiParam(value = "首页基本参数", required = true, name = "pageRequest") PageRequest pageRequest) {
		DataResult<FreshResponse> dataResult = null;
		try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(pageRequest, userInfo);
            setValue(reportCommonParam, pageRequest);
			pageRequest.setPersonId(userInfo.getPersonId());
			FreshResponse freshResponse = headPageService.queryFresh(pageRequest);
			//切换含税数据
			//(0-否，1-是)
			if(1 == pageRequest.getTaxType()){
				freshResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(freshResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "非生鲜", notes = "非生鲜")
	@PostMapping("/queryNotFresh")
	public DataResult<NotFreshResponse> queryNotFresh(HttpServletRequest request, HttpServletResponse response,
											  @RequestBody @ApiParam(value = "首页基本参数", required = true, name = "pageRequest") PageRequest pageRequest) {
		DataResult<NotFreshResponse> dataResult = null;
		try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(pageRequest, userInfo);
            setValue(reportCommonParam, pageRequest);
			pageRequest.setPersonId(userInfo.getPersonId());
			NotFreshResponse notFreshResponse = headPageService.queryNotFresh(pageRequest);
			//切换含税数据
			//(0-否，1-是)
			if(1 == pageRequest.getTaxType()){
				notFreshResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(notFreshResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "时线，日线，月线", notes = "时线，日线，月线")
	@PostMapping("/queryReportData")
	public DataResult<ReportDataResponse> queryReportData(HttpServletRequest request, HttpServletResponse response,
			@RequestBody @ApiParam(value = "时线，日线，月线", required = true, name = "reportParamRequest") ReportParamRequest reportParamRequest) {
		DataResult<ReportDataResponse> dataResult = null;
		try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(reportParamRequest, userInfo);
            setValue(reportCommonParam, reportParamRequest);
			ReportDataResponse reportDataResponse = headPageService.queryReportData(reportParamRequest);
			//切换含税数据
			//(0-否，1-是)
			if(1 == reportParamRequest.getTaxType()){
				reportDataResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(reportDataResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "查询时线，日线，月线同期", notes = "时线，日线，月线同期")
	@PostMapping("/querySameReportData")
	public DataResult<ReportDataResponse> querySameReportData(HttpServletRequest request, HttpServletResponse response,
														  @RequestBody @ApiParam(value = "时线，日线，月线", required = true, name = "reportParamRequest") ReportParamRequest reportParamRequest) {
		DataResult<ReportDataResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(reportParamRequest, userInfo);
			setValue(reportCommonParam, reportParamRequest);
			ReportDataResponse reportDataResponse = headPageService.querySameReportData(reportParamRequest);
			//切换含税数据
			//(0-否，1-是)
			if(1 == reportParamRequest.getTaxType()){
				reportDataResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(reportDataResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "排行榜", notes = "排行榜")
	@PostMapping("/queryRankInfo")
	public DataResult<RankInfoResponse> queryRankInfo(HttpServletRequest request, HttpServletResponse response,
			@RequestBody @ApiParam(value = "首页基本参数", required = true, name = "pageRequest") RankParamRequest pageRequest) {
		DataResult<RankInfoResponse> dataResult = null;
		try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(pageRequest, userInfo);
            setValue(reportCommonParam, pageRequest);
            int grade = 0;
			if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
					|| userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
				grade = 0;
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
				grade = 1;
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
				grade = 2;
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
				grade = 3;
			} else {
				ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
			}
			pageRequest.setGrade(grade);
			RankInfoResponse rankInfoResponse = headPageService.queryRankInfo(pageRequest);
			rankInfoResponse.setGrade(grade);
			//切换含税数据
			//(0-否，1-是)
			if(1 == pageRequest.getTaxType()){
				rankInfoResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(rankInfoResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "排行榜列表", notes = "排行榜列表")
	@PostMapping("/queryRankList")
	public DataResult<RankInfoResponse> queryRankList(HttpServletRequest request, HttpServletResponse response,
													  @RequestBody @ApiParam(value = "排行榜列表基本参数", required = true, name = "rankParamRequest") RankParamRequest rankParamRequest) {
		DataResult<RankInfoResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(rankParamRequest, userInfo);
			setValue(reportCommonParam, rankParamRequest);
			int grade = 0;
			if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
					|| userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
				grade = 0;
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
				grade = 1;
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
				grade = 2;
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
				grade = 3;
			} else {
				ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
			}
			rankParamRequest.setGrade(grade);
			RankInfoResponse rankInfoResponse = headPageService.queryRankList(rankParamRequest);
			rankInfoResponse.setGrade(grade);
			//切换含税数据
			//(0-否，1-是)
			if(1 == rankParamRequest.getTaxType()){
				rankInfoResponse.setTaxData();
			}
			dataResult = DataHandler.jsonResult(rankInfoResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "会员渗透率", notes = "会员渗透率")
	@PostMapping("/queryMemberPermeability")
	public DataResult<MemberPermeabilityResponse> queryMemberPermeability(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody @ApiParam(value = "首页基本参数", required = true, name = "pageRequest") PageRequest pageRequest) {
		DataResult<MemberPermeabilityResponse> dataResult = null;
		try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(pageRequest, userInfo);
            setValue(reportCommonParam, pageRequest);
			dataResult = DataHandler.jsonResult(headPageService.queryMember(pageRequest));
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "会员渗透率明细", notes = "会员渗透率明细")
	@PostMapping("/queryMemberDetail")
	public DataResult<MemberListResponse> queryMemberDetail(HttpServletRequest request,
															HttpServletResponse response,
															@RequestBody @ApiParam(value = "首页基本参数", required = true, name = "pageRequest") PageRequest pageRequest) {
		DataResult<MemberListResponse> dataResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(pageRequest, userInfo);
			setValue(reportCommonParam, pageRequest);
			dataResult = DataHandler.jsonResult(headPageService.queryMemberDetail(pageRequest));
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}

	@ApiOperation(value = "根据区域ID获取所有门店", notes = "根据区域ID获取所有门店")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "parentId", value = "区域ID", required = true, dataType = "String") })
	@GetMapping("/getStoreByArea")
	public DataResult<List<Organization>> getStoreByArea(HttpServletRequest request, HttpServletResponse response,
			String parentId) {
		List<Organization> orgList = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			orgList = reportOrgService.getStoreByArea(parentId, userInfo);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(orgList);
	}

	@ApiOperation(value = "根据省份获取所有区域", notes = "根据省份获取所有区域")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "parentId", value = "省份ID", required = true, dataType = "String") })
	@GetMapping("/getAreaByProvince")
	public DataResult<List<Organization>> getAreaByProvince(HttpServletRequest request, HttpServletResponse response,
			String parentId) {
		List<Organization> orgList = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			orgList = reportOrgService.getAreaByProvince(parentId, userInfo);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(orgList);
	}

	@ApiOperation(value = "获取所有省份", notes = "获取所有省份")
	@GetMapping("/getAllProvince")
	public DataResult<List<Organization>> getAllProvince(HttpServletRequest request, HttpServletResponse response) {
		List<Organization> orgList = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			orgList = reportOrgService.getAllProvince(userInfo);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(orgList);
	}

	@ApiOperation(value = "获取所有品类", notes = "获取所有品类")
	@GetMapping("/getAllCategory")
	public DataResult<Set<String>> getAllCategory(HttpServletRequest request, HttpServletResponse response) {
		Set<String> result = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			result = reportUserDeptService.getAllCategoryByPersonId(userInfo.getPersonId());
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(result);
	}

	@ApiOperation(value = "获取非加工生鲜品类", notes = "获取非加工生鲜品类")
	@GetMapping("/getFreshCategory")
	public DataResult<Set<String>> getFreshCategory(HttpServletRequest request, HttpServletResponse response) {
		Set<String> result = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			result = reportUserDeptService.getAllCategoryByPersonId(userInfo.getPersonId());
			if(null != result && result.contains("非加工生鲜")){
				result = new HashSet<>();
				result.add("非加工生鲜");
			}else{
				result = new HashSet<>();
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(result);
	}

	@ApiOperation(value = "根据品类获取大类", notes = "根据品类获取大类")
	@PostMapping("/getUserDeptByCategory")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "category", value = "品类", required = true, dataType = "String") })
	public DataResult<Set<String>> getUserDeptByCategory(HttpServletRequest request, HttpServletResponse response,
			String category) {
		List<UserDept> result = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			result = reportUserDeptService.getUserDeptByCategory(userInfo.getPersonId(), category);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(result);
	}

	@ApiOperation(value = "根据非加工生鲜获取大类", notes = "根据非加工生鲜获取大类")
	@PostMapping("/getUserFreshDeptByCategory")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "category", value = "品类", required = true, dataType = "String") ,@ApiImplicitParam(paramType = "query", name = "type", value = "是否需要32大类(0-否，1-是)", required = true, dataType = "String")})
	public DataResult<Set<String>> getUserFreshDeptByCategory(HttpServletRequest request, HttpServletResponse response,
														 String category,String type, String personId) {
		List<UserDept> result = new ArrayList<>();
		// 记录访问日志
  		try {
  			userService.addPersonLog(personId, "渗透率跟踪-小类");
  		} catch (Exception e) {
  			log.error("访问日志保存出错", e);
  		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			List<UserDept> list = reportUserDeptService.getUserDeptByCategory(userInfo.getPersonId(), category);
			if(null != list && list.size() > 0){
				for(UserDept userDept : list){
					if("0".equals(type)){
						if(35 == userDept.getDeptId().intValue()
								|| 36 == userDept.getDeptId().intValue() || 37 == userDept.getDeptId().intValue()){
							result.add(userDept);
							continue;
						}
					}else if("1".equals(type)){
						if(32 == userDept.getDeptId().intValue() || 35 == userDept.getDeptId().intValue()
								|| 36 == userDept.getDeptId().intValue() || 37 == userDept.getDeptId().intValue()){
							result.add(userDept);
							continue;
						}
					}
				}
			}
			if(null != result && result.size() > 0){
				result = result.stream().sorted(new Comparator<UserDept>() {
					@Override
					public int compare(UserDept o1, UserDept o2) {
						BigDecimal deptId1 = new BigDecimal(String.valueOf(o1.getDeptId().intValue()));
						BigDecimal deptId2 = new BigDecimal(String.valueOf(o2.getDeptId().intValue()));
						return deptId1.compareTo(deptId2);
					}
				}).collect(Collectors.toList());
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(result);
	}

	@ApiOperation(value = "查询销售达成率", notes = "查询销售达成率")
	@PostMapping("/querySaleAchievingRate")
	public DataResult<AchievingRateResponse> querySaleAchievingRate(HttpServletRequest request,
																	HttpServletResponse response,
																	@RequestBody @ApiParam(value = "销售达成率参数", required = true, name = "param") BaseSaleRequest param){
		AchievingRateResponse achievingRateResponse = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			achievingRateResponse = saleReportService.querySaleAchievingRate(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(achievingRateResponse);
	}

	@ApiOperation(value = "查询毛利额达成率", notes = "毛利额达成率")
	@PostMapping("/queryRateAchievingRate")
	public DataResult<AchievingRateResponse> queryRateAchievingRate(HttpServletRequest request,
																	HttpServletResponse response,
																	@RequestBody @ApiParam(value = "毛利额达成率参数", required = true, name = "param") BaseSaleRequest param){
		AchievingRateResponse achievingRateResponse = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			achievingRateResponse = saleReportService.queryRateAchievingRate(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(achievingRateResponse);
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
			TurnoverDayResponse turnoverDayResponse = headPageService.queryActualTurnoverDay(param);
			dataResult = DataHandler.jsonResult(turnoverDayResponse);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return dataResult;
	}
	
	@ApiOperation(value = "根据id获取组织名称", notes = "获取组织对应名称")
	@PostMapping("/getOrganization")
	public DataResult<String> getOrganization(HttpServletRequest request,
										            HttpServletResponse response,String personId,
										            @RequestBody 
										            @ApiParam(value = "根据id 获取组织信息", required = true, name = "param") DailyReportRequest param) {
		CsmbStoreModel organization = new CsmbStoreModel(); 
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "组织架构名称查询");
		} catch (

		Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request); 
			List<User> userOrgList = null;
			List<CsmbStoreModel> csmbList = headPageService.getOrganization();
			
			// 全司管理员、超级管理员
			if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())|| userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {
				if(param.getOrgId().length()==3) {//省份级别
					if(null != csmbList && csmbList.size() > 0){
			            for(CsmbStoreModel model : csmbList){
			            	if(model.getProvinceId().equals(param.getOrgId())) {
			            		BeanUtils.copyProperties(model,organization);
			            		organization.setAreaId(null);
			            		organization.setAreaName(null);
			            		organization.setStoreId(null);
			            		organization.setStoreName(null);
			            		break;
			            	}
			            }
			        }
				}else if(param.getOrgId().length()==5) {//区域级别
					if(null != csmbList && csmbList.size() > 0){
			            for(CsmbStoreModel model : csmbList){
			            	if(model.getAreaId().equals(param.getOrgId())) {
			            		CsmbStoreModel csmbOrg = new CsmbStoreModel(); 
			            		BeanUtils.copyProperties(model,csmbOrg);
			            		organization.setStoreId(null);
			            		organization.setStoreName(null);
			            		break;
			            	}
			            }
			        }
				}else if(param.getOrgId().length()==6) {//门店级别
					if(null != csmbList && csmbList.size() > 0){
			            for(CsmbStoreModel model : csmbList){
			            	if(model.getStoreId().equals(param.getOrgId())) {
			            		BeanUtils.copyProperties(model,organization);
			            		break;
			            	}
			            }
			        }
				}
				
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
				userOrgList = userService.getUserOrgListByPersonId(userInfo.getPersonId(),UserTypeEnum.MREPORT_PROVINCEADMIN.getType());
				List<CsmbOrg> csmbOrg = commonService.queryAllCsmbOrg(userOrgList);
				for(CsmbOrg org : csmbOrg) {
					if(org.getAreaId().equals(param.getOrgId())) {//省份级别
						for(CsmbStoreModel model : csmbList){
			            	if(model.getProvinceId().equals(param.getOrgId())) {
			            		BeanUtils.copyProperties(model,organization);
			            		organization.setAreaId(null);
			            		organization.setAreaName(null);
			            		organization.setStoreId(null);
			            		organization.setStoreName(null);
			            		break;
			            	}
			            }
					}else if(org.getAreaId().equals(param.getOrgId())) {//区域级别
						 for(CsmbStoreModel model : csmbList){
				            	if(model.getAreaId().equals(param.getOrgId())) {
				            		BeanUtils.copyProperties(model,organization);
				            		organization.setStoreId(null);
				            		organization.setStoreName(null);
				            		break;
				            	}
				            }
					}else if(org.getStoreId().equals(param.getOrgId())) {//门店级别
						 for(CsmbStoreModel model : csmbList){
				            	if(model.getStoreId().equals(param.getOrgId())) {
				            		BeanUtils.copyProperties(model,organization);
				            		break;
				            	}
				            }
					}
				}

			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
				userOrgList = userService.getUserOrgListByPersonId(userInfo.getPersonId(),UserTypeEnum.MREPORT_AREAADMIN.getType());
				List<CsmbOrg> csmbOrg = commonService.queryAllCsmbOrg(userOrgList);
				for(CsmbOrg org : csmbOrg) {
					if(org.getAreaId().equals(param.getOrgId())) {//省份级别
						for(CsmbStoreModel model : csmbList){
			            	if(model.getProvinceId().equals(param.getOrgId())) {
			            		BeanUtils.copyProperties(model,organization);
			            		organization.setAreaId(null);
			            		organization.setAreaName(null);
			            		organization.setStoreId(null);
			            		organization.setStoreName(null);
			            		break;
			            	}
			            }
					}else if(org.getAreaId().equals(param.getOrgId())) {//区域级别
						 for(CsmbStoreModel model : csmbList){
				            	if(model.getAreaId().equals(param.getOrgId())) {
				            		BeanUtils.copyProperties(model,organization);
				            		organization.setStoreId(null);
				            		organization.setStoreName(null);
				            		break;
				            	}
				            }
					}else if(org.getStoreId().equals(param.getOrgId())) {//门店级别
						 for(CsmbStoreModel model : csmbList){
				            	if(model.getStoreId().equals(param.getOrgId())) {
				            		BeanUtils.copyProperties(model,organization);
				            		break;
				            	}
				            }
					}
				}
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
				userOrgList = userService.getUserOrgListByPersonId(userInfo.getPersonId(),UserTypeEnum.MREPORT_STOREADMIN.getType());
				List<CsmbOrg> csmbOrg = commonService.queryAllCsmbOrg(userOrgList);
				for(CsmbOrg org : csmbOrg) {
					if(org.getProvinceId().equals(param.getOrgId())) {//省份级别
						for(CsmbStoreModel model : csmbList){
			            	if(model.getProvinceId().equals(param.getOrgId())) {
			            		BeanUtils.copyProperties(model,organization);
			            		organization.setAreaId(null);
			            		organization.setAreaName(null);
			            		organization.setStoreId(null);
			            		organization.setStoreName(null);
			            		break;
			            	}
			            }
					}else if(org.getAreaId().equals(param.getOrgId())) {//区域级别
						 for(CsmbStoreModel model : csmbList){
				            	if(model.getAreaId().equals(param.getOrgId())) {
				            		BeanUtils.copyProperties(model,organization);
				            		organization.setStoreId(null);
				            		organization.setStoreName(null);
				            		break;
				            	}
				            }
					}else if(org.getStoreId().equals(param.getOrgId())) {//门店级别
						 for(CsmbStoreModel model : csmbList){
				            	if(model.getStoreId().equals(param.getOrgId())) {
				            		BeanUtils.copyProperties(model,organization);
				            		break;
				            	}
				            }
					}
				}
					
			} else {
				ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
			}
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(organization);
	}
	
}
