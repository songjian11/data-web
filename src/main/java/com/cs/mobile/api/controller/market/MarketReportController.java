package com.cs.mobile.api.controller.market;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.market.request.MarkReportRequest;
import com.cs.mobile.api.model.market.response.DepartmentSaleListResponse;
import com.cs.mobile.api.model.market.response.TrendReportListResponse;
import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.market.MarketReportService;
import com.cs.mobile.common.utils.StringUtils;
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

@Slf4j
@Api(value = "marketReportController", tags = { "市场专题报表" })
@RestController
@RequestMapping("/api/marketReportController")
public class MarketReportController extends AbstractApiController {
    @Autowired
    private MarketReportService marketReportService;

    @ApiOperation(value = "市场部门汇总统计", notes = "市场部门汇总统计")
    @PostMapping("/queryCurrentDepartmentSales")
    public DataResult<DepartmentSaleListResponse> queryCurrentDepartmentSales(HttpServletRequest request,
                                                                              HttpServletResponse response,
                                                                              @RequestBody @ApiParam(value = "市场部门汇总统计参数", required = true, name = "param") MarkReportRequest param) {
        DepartmentSaleListResponse res = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            if(StringUtils.isEmpty(param.getProvinceId())){
                param.setProvinceId("101");
            }
            res = marketReportService.queryCurrentDepartmentSales(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                res.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(res);
    }


    @ApiOperation(value = "市场部门趋势", notes = "市场部门趋势")
    @PostMapping("/queryDepartmentTrendReport")
    public DataResult<TrendReportListResponse> queryDepartmentTrendReport(HttpServletRequest request,
                                                                              HttpServletResponse response,
                                                                              @RequestBody @ApiParam(value = "市场部门趋势", required = true, name = "param") MarkReportRequest param) {
        TrendReportListResponse res = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            if(StringUtils.isEmpty(param.getProvinceId())){
                param.setProvinceId("101");
            }
            res = marketReportService.queryDepartmentTrendReport(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                res.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(res);
    }

    @ApiOperation(value = "市场大类汇总统计", notes = "市场大类汇总统计")
    @PostMapping("/queryCurrentDeptSales")
    public DataResult<DepartmentSaleListResponse> queryCurrentDeptSales(HttpServletRequest request,
                                                                        HttpServletResponse response,
                                                                        @RequestBody @ApiParam(value = "市场大类汇总统计参数", required = true, name = "param") MarkReportRequest param) {
        DepartmentSaleListResponse res = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            if(StringUtils.isEmpty(param.getProvinceId())){
                param.setProvinceId("101");
            }
            res = marketReportService.queryCurrentDeptSales(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                res.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(res);
    }

    @ApiOperation(value = "市场大类趋势", notes = "市场大类趋势")
    @PostMapping("/queryDeptTrendReport")
    public DataResult<TrendReportListResponse> queryDeptTrendReport(HttpServletRequest request,
                                                                        HttpServletResponse response,
                                                                        @RequestBody @ApiParam(value = "市场大类趋势", required = true, name = "param") MarkReportRequest param) {
        TrendReportListResponse res = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            if(StringUtils.isEmpty(param.getProvinceId())){
                param.setProvinceId("101");
            }
            res = marketReportService.queryDeptTrendReport(param);
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                res.setTaxData();
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(res);
    }
}
