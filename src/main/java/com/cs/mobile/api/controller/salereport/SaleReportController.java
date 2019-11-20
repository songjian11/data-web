package com.cs.mobile.api.controller.salereport;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.controller.mreport.MobileReportController;
import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.cs.mobile.api.model.reportPage.response.HomeApplianceResponse;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.api.model.salereport.response.*;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.salereport.SaleReportService;
import com.cs.mobile.api.service.user.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("unchecked")
@Api(value = "saleReport", tags = { "销售专题" })
@RestController
@RequestMapping("/api/saleReport")
@Slf4j
public class SaleReportController extends AbstractApiController {
    @Autowired
    private SaleReportService saleReportService;
    @Autowired
	UserService userService;

    @ApiOperation(value = "销售模块(销售额,可比,环比,同比)", notes = "销售模块")
    @PostMapping("/queryAllSaleSum")
    public DataResult<AllSaleSumResponse> queryAllSaleSum(HttpServletRequest request,
                                                          HttpServletResponse response,
                                                          @RequestBody @ApiParam(value = "销售模块参数", required = true, name = "param") BaseSaleRequest param) {
        AllSaleSumResponse allSaleSumResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            allSaleSumResponse = saleReportService.queryAllSaleSum(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                allSaleSumResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(allSaleSumResponse);
    }

    @ApiOperation(value = "毛利额模块(毛利额,可比,环比,同比)", notes = "毛利额模块")
    @PostMapping("/queryAllRateSum")
    public DataResult<AllRateSumResponse> queryAllRateSum(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestBody @ApiParam(value = "毛利额模块参数", required = true, name = "param") BaseSaleRequest param){
        AllRateSumResponse allRateSumResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            allRateSumResponse = saleReportService.queryAllRateSum(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                allRateSumResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(allRateSumResponse);
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

    @ApiOperation(value = "查询本年和本月销售额", notes = "本年和本月销售额")
    @PostMapping("/querySaleMonthAndYear")
    public DataResult<SaleMonthAndYearResponse> querySaleMonthAndYear(HttpServletRequest request,
                                                                       HttpServletResponse response,
                                                                       @RequestBody @ApiParam(value = "本年和本月销售额参数", required = true, name = "param") BaseSaleRequest param){
        SaleMonthAndYearResponse saleMonthAndYearResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            saleMonthAndYearResponse = saleReportService.querySaleMonthAndYear(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                saleMonthAndYearResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(saleMonthAndYearResponse);
    }

    @ApiOperation(value = "查询本年和本月毛利额", notes = "本年和本月毛利额")
    @PostMapping("/queryRateMonthAndYear")
    public DataResult<RateMonthAndYearResponse> queryRateMonthAndYear(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      @RequestBody @ApiParam(value = "本年和本月毛利额参数", required = true, name = "param") BaseSaleRequest param){
        RateMonthAndYearResponse rateMonthAndYearResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            rateMonthAndYearResponse = saleReportService.queryRateMonthAndYear(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                rateMonthAndYearResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(rateMonthAndYearResponse);
    }

    @ApiOperation(value = "查询家电模块", notes = "家电模块")
    @PostMapping("/queryHomeAppliance")
    public DataResult<HomeApplianceResponse> queryHomeAppliance(HttpServletRequest request,
                                                                HttpServletResponse response,
                                                                @RequestBody @ApiParam(value = "家电模块参数", required = true, name = "param") BaseSaleRequest param){
        HomeApplianceResponse homeApplianceResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            homeApplianceResponse = saleReportService.queryHomeAppliance(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                homeApplianceResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(homeApplianceResponse);
    }

    @ApiOperation(value = "查询渠道构成", notes = "渠道构成")
    @PostMapping("/queryChannlResponse")
    public DataResult<ChannlResponse> queryChannlResponse(HttpServletRequest request,
                                                                HttpServletResponse response,
                                                                @RequestBody @ApiParam(value = "渠道构成参数", required = true, name = "param") BaseSaleRequest param, String personId){
        ChannlResponse channlResponse = null;
     // 记录访问日志
		try {
			userService.addPersonLog(personId, "销售界面");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            channlResponse = saleReportService.queryChannlResponse(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                channlResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(channlResponse);
    }

    @ApiOperation(value = "查询销售趋势", notes = "销售趋势")
    @PostMapping("/querySaleTrendResponse")
    public DataResult<SaleTrendListResponse> querySaleTrendResponse(HttpServletRequest request,
                                                          HttpServletResponse response,
                                                          @RequestBody @ApiParam(value = "销售趋势参数", required = true, name = "param") BaseSaleRequest param){
        SaleTrendListResponse saleTrendListResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            saleTrendListResponse = saleReportService.querySaleTrendResponse(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                saleTrendListResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(saleTrendListResponse);
    }

    @ApiOperation(value = "查询销售构成", notes = "销售构成")
    @PostMapping("/querySaleComposition")
    public DataResult<SaleCompositionListResponse> querySaleComposition(HttpServletRequest request,
                                                                    HttpServletResponse response,
                                                                    @RequestBody @ApiParam(value = "销售构成参数", required = true, name = "param") BaseSaleRequest param){
        SaleCompositionListResponse saleCompositionListResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            saleCompositionListResponse = saleReportService.querySaleComposition(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                saleCompositionListResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(saleCompositionListResponse);
    }

    @ApiOperation(value = "查询单品销售排名", notes = "单品销售")
    @PostMapping("/queryItemSaleRankList")
    public DataResult<ItemSaleRankListResponse> queryItemSaleRankList(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      @RequestBody @ApiParam(value = "单品销售排名参数", required = true, name = "param") BaseSaleRequest param){
        ItemSaleRankListResponse itemSaleRankListResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            itemSaleRankListResponse = saleReportService.queryItemSaleRankList(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                itemSaleRankListResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(itemSaleRankListResponse);
    }

    @ApiOperation(value = "查询单品毛利额排名", notes = "单品毛利额")
    @PostMapping("/queryItemRateRankList")
    public DataResult<ItemRateRankListResponse> queryItemRateRankList(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      @RequestBody @ApiParam(value = "单品毛利额排名参数", required = true, name = "param") BaseSaleRequest param){
        ItemRateRankListResponse itemRateRankListResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            itemRateRankListResponse = saleReportService.queryItemRateRankList(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                itemRateRankListResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(itemRateRankListResponse);
    }

    @ApiOperation(value = "查询渠道构成明细", notes = "渠道构成明细")
    @PostMapping("/queryChannlDetailResponse")
    public DataResult<ChannlDetailListResponse> queryChannlDetailResponse(HttpServletRequest request,
                                                          HttpServletResponse response,
                                                          @RequestBody @ApiParam(value = "渠道构成明细参数", required = true, name = "param") BaseSaleRequest param, String personId){
        ChannlDetailListResponse channlDetailListResponse = null;
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "销售界面");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }

        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            channlDetailListResponse = saleReportService.queryChannlDetailResponse(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                channlDetailListResponse.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(channlDetailListResponse);
    }
}
