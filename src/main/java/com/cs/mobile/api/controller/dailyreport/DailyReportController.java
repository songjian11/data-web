package com.cs.mobile.api.controller.dailyreport;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.dailyreport.request.DailyReportRequest;
import com.cs.mobile.api.model.dailyreport.request.FreshDailyReportRequest;
import com.cs.mobile.api.model.dailyreport.response.*;
import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.dailyreport.DailyReportService;
import com.cs.mobile.api.service.reportPage.ReportUserDeptService;
import com.cs.mobile.api.service.user.UserService;
import com.cs.mobile.common.constant.UserTypeEnum;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Api(value = "dailyReport", tags = { "日报" })
@RestController
@RequestMapping("/api/dailyReport")
@Slf4j
public class DailyReportController extends AbstractApiController {
	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private ReportUserDeptService reportUserDeptService;
	@Autowired
	UserService userService;

	@ApiOperation(value = "日报列表", notes = "日报列表")
	@PostMapping("/queryDailyReport")
	public DataResult<DailyReportResponse> queryDailyReport(HttpServletRequest request, HttpServletResponse response,
			String personId,
			@RequestBody @ApiParam(value = "日报列表参数", required = true, name = "param") DailyReportRequest param) {
		PageResult<DailyReportResponse> dataResult = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "业绩战报-机构");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			// 查询梯度,初始化时传默认值0,下钻时加1(0-默认值，1-全司，2-省份，3-区域,4-门店)
			// 默认值时，自动取用户对应梯度
			if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
					|| userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
				if (0 == param.getMark()) {
					param.setMark(1);
				}
				param.setGrade(1);
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
				if (0 == param.getMark()) {
					param.setMark(2);
				}
				param.setGrade(2);
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
				if (0 == param.getMark()) {
					param.setMark(3);
				}
				param.setGrade(3);
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
				if (0 == param.getMark()) {
					param.setMark(4);
				}
				param.setGrade(4);
			} else {
				ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
			}
			dataResult = dailyReportService.queryDailyReport(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(dataResult);
	}

    
    /***
     *	 大类下钻列表
     * @param request
     * @param response
     * @param personId
     * @param param
     * @return
     */
    @ApiOperation(value = "大类下钻列表", notes = "日报列表")
    @PostMapping("/queryCategoryDailyReport")
    public DataResult<DailyReportResponse> queryCategoryDailyReport(HttpServletRequest request,
                                                              HttpServletResponse response,String personId,
                                                              @RequestBody 
                                                              @ApiParam(value = "大类列表参数", required = true, name = "param") DailyReportRequest param) {
		CategoryDailyReportListResponse categoryDailyReportListResponse = null; 
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "业绩战报-大类");
		} catch (

		Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
        	ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
			categoryDailyReportListResponse = dailyReportService.queryCategoryDrillDown(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(categoryDailyReportListResponse);
	}

	@ApiOperation(value = "生鲜战区报表", notes = "生鲜战区报表")
	@PostMapping("/queryFreshDailyReport")
	public DataResult<FreshDailyReportListResponse> queryFreshDailyReport(HttpServletRequest request,
			HttpServletResponse response, String personId,
			@RequestBody @ApiParam(value = "生鲜战区报表参数", required = true, name = "param") FreshDailyReportRequest param) {
		FreshDailyReportListResponse freshDailyReportListResponse = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "生鲜战报");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			if(0 == param.getIsType() && (0 == param.getIsQueryType() || 1 == param.getIsQueryType())){
				param.setIsQueryType(1);
				param.setCategory("非加工生鲜");
			}
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = getDepts(param, userInfo);
			setValue(reportCommonParam, param);
			freshDailyReportListResponse = dailyReportService.queryFreshDailyReport(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(freshDailyReportListResponse);
	}

	@ApiOperation(value = "生鲜门店战区报表", notes = "生鲜门店战区报表")
	@PostMapping("/queryStoreFreshDailyReport")
	public DataResult<StoreFreshDailyReportListResponse> queryStoreFreshDailyReport(HttpServletRequest request,
																			 HttpServletResponse response, String personId,
																			 @RequestBody @ApiParam(value = "生鲜门店战区报表参数", required = true, name = "param") FreshDailyReportRequest param) {
		StoreFreshDailyReportListResponse storeFreshDailyReportListResponse = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "生鲜战报-门店");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			if(0 == param.getIsType() && (0 == param.getIsQueryType() || 1 == param.getIsQueryType())){
				param.setIsQueryType(1);
				param.setCategory("非加工生鲜");
			}
			UserInfo userInfo = this.getCurUserInfo(request);
			ReportCommonParam reportCommonParam = getDepts(param, userInfo);
			setValue(reportCommonParam, param);
			storeFreshDailyReportListResponse = dailyReportService.queryStoreFreshDailyReport(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(storeFreshDailyReportListResponse);
	}

	@ApiOperation(value = "获取非加工生鲜大类", notes = "获取非加工生鲜大类")
	@PostMapping("/getFreshDept")
	public DataResult<Set<String>> getUserFreshDept(HttpServletRequest request, HttpServletResponse response) {
		List<UserDept> result = new ArrayList<>();
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			result = reportUserDeptService.getUserDeptByCategory(userInfo.getPersonId(), "非加工生鲜");
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(result);
	}
	
	@ApiOperation(value = "获取门店负毛利top30", notes = "获取门店负毛利top30")
	@PostMapping("/getStoreLossGoods")
	public DataResult<StoreLossGoodsListResponse> getStoreLossGoods(HttpServletRequest request,
										            HttpServletResponse response,String personId,
										            @RequestBody 
										            @ApiParam(value = "门店负毛利商品参数", required = true, name = "param") DailyReportRequest param) {
		StoreLossGoodsListResponse storeLossGoodsResponse = null; 
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "门店负毛利商品查询");
		} catch (

		Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request); 
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			storeLossGoodsResponse = dailyReportService.queryStoreLossGoods(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(storeLossGoodsResponse);
	}
	
	@ApiOperation(value = "门店负毛利商品下钻明细", notes = "获取门店负毛利商品下钻明细")
	@PostMapping("/getStoreLossGoodsDetail")
	public DataResult<StoreLossGoodsListResponse> getStoreLossGoodsDetail(HttpServletRequest request,
										            HttpServletResponse response,String personId,
										            @RequestBody 
										            @ApiParam(value = "门店负毛利商品参数", required = true, name = "param") DailyReportRequest param) {
		StoreLossGoodsListResponse storeLossGoodsListResponse = null; 
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "门店负毛利商品明细查询");
		} catch (

		Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request); 
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			storeLossGoodsListResponse = dailyReportService.getStoreLossGoodsDetail(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(storeLossGoodsListResponse);
	}
	
	@ApiOperation(value = "商品毛利率卡片", notes = "获取商品毛利率卡片")
	@PostMapping("/getGrossProfitGoods")
	public DataResult<StoreLossGoodsListResponse> getGrossProfitGoods(HttpServletRequest request,
										            HttpServletResponse response,String personId,
										            @RequestBody 
										            @ApiParam(value = "商品毛利率卡片参数", required = true, name = "param") DailyReportRequest param) {
		StoreLossGoodsListResponse storeLossGoodsListResponse = null; 
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "商品毛利率卡片查询");
		} catch (

		Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request); 
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			storeLossGoodsListResponse = dailyReportService.getGrossProfitGoods(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(storeLossGoodsListResponse);
	}
	
	@ApiOperation(value = "获取门店连续负毛利商品", notes = "获取门店连续负毛利商品")
	@PostMapping("/getContinuityLossGoods")
	public DataResult<ContinuityLossGoodsListResponse> getContinuityLossGoods(HttpServletRequest request,
										            HttpServletResponse response,String personId,
										            @RequestBody 
										            @ApiParam(value = "门店连续负毛利商品参数", required = true, name = "param") DailyReportRequest param) {
		ContinuityLossGoodsListResponse continuityLossGoodsListResponse = null; 
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "门店连续负毛利商品查询");
		} catch (

		Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request); 
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			continuityLossGoodsListResponse = dailyReportService.getContinuityLossGoods(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(continuityLossGoodsListResponse);
	}
	
	@ApiOperation(value = "门店连续负毛利商品下钻", notes = "获取门店连续负毛利商品下钻")
	@PostMapping("/getContinuityLossGoodsDetail")
	public DataResult<ContinuityLossGoodsListResponse> getContinuityLossGoodsDetail(HttpServletRequest request,
										            HttpServletResponse response,String personId,
										            @RequestBody 
										            @ApiParam(value = "门店连续负毛利商品下钻参数", required = true, name = "param") DailyReportRequest param) {
		ContinuityLossGoodsListResponse continuityLossGoodsListResponse = null; 
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "门店连续负毛利商品下钻明细");
		} catch (

		Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request); 
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			continuityLossGoodsListResponse = dailyReportService.getContinuityLossGoodsDetail(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(continuityLossGoodsListResponse);
	}
	
	@ApiOperation(value = "门店大类库存金额", notes = "获取门店大类库存金额")
	@PostMapping("/getStoreLargeClassMoney")
	public DataResult<StoreLargeClassMoneyListResponse> getStoreLargeClassMoney(HttpServletRequest request,
										            HttpServletResponse response,String personId,
										            @RequestBody 
										            @ApiParam(value = "门店大类库存金额", required = true, name = "param") DailyReportRequest param) {
		StoreLargeClassMoneyListResponse storeLargeClassMoneyListResponse = null; 
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "门店大类库存金额");
		} catch (

		Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			UserInfo userInfo = this.getCurUserInfo(request); 
			ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
			setValue(reportCommonParam, param);
			storeLargeClassMoneyListResponse = dailyReportService.getStoreLargeClassMoney(param);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(storeLargeClassMoneyListResponse);
	}
}
