package com.cs.mobile.api.controller.freshspecialreport;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.freshspecialreport.request.FreshSpecialReportRequest;
import com.cs.mobile.api.model.freshspecialreport.response.FreshSpecialReportRankListResponse;
import com.cs.mobile.api.model.freshspecialreport.response.FreshSpecialReportResponse;
import com.cs.mobile.api.model.freshspecialreport.response.FreshSpecialReportTrendListResponse;
import com.cs.mobile.api.model.freshspecialreport.response.SubFreshSpecialReportResponse;
import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.freshspecialreport.FreshSpecialReportService;
import com.cs.mobile.api.service.reportPage.ReportUserDeptService;
import com.cs.mobile.api.service.user.UserService;
import com.cs.mobile.common.constant.UserTypeEnum;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Api(value = "freshSpecialReport", tags = { "生鲜35,36,37专题报表" })
@RestController
@RequestMapping("/api/freshSpecialReport")
@Slf4j
public class FreshSpecialReportController extends AbstractApiController {
    @Autowired
    private FreshSpecialReportService freshSpecialReportService;
    @Autowired
    private ReportUserDeptService reportUserDeptService;
    @Autowired
	UserService userService;

    @ApiOperation(value = "查询专题报表页", notes = "查询专题报表页")
    @PostMapping("/queryFreshSpecialReport")
    public DataResult<FreshSpecialReportResponse> queryFreshSpecialReport(HttpServletRequest request,
                                                                          HttpServletResponse response,
                                                                          @RequestBody @ApiParam(value = "查询专题报表页参数", required = true, name = "param") FreshSpecialReportRequest param, String personId){
        FreshSpecialReportResponse freshSpecialReportResponse = null;
     // 记录访问日志
  		try {
  			userService.addPersonLog(personId, "渗透率跟踪-小类");
  		} catch (Exception e) {
  			log.error("访问日志保存出错", e);
  		}
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            int grade = 0;
            if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
                grade = 1;
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
                grade = 2;
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
                grade = 3;
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
                grade = 4;
            } else {
                ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
            }
            freshSpecialReportResponse = freshSpecialReportService.queryFreshSpecialReport(param);
            freshSpecialReportResponse.setGrade(grade + "");
            //切换含税数据
            //(0-否，1-是)
            if(1 == param.getTaxType()){
                freshSpecialReportResponse.setTaxData();
            }

            if(null != freshSpecialReportResponse){//子列表按照渗透率排序
                List<SubFreshSpecialReportResponse> list = freshSpecialReportResponse.getList();
                if(null != list && list.size() > 0){
                    list = list.stream().sorted(new Comparator<SubFreshSpecialReportResponse>() {
                        @Override
                        public int compare(SubFreshSpecialReportResponse o1, SubFreshSpecialReportResponse o2) {
                            BigDecimal value1 = new BigDecimal(StringUtils.isEmpty(o1.getPermeability())?"-1":o1.getPermeability());
                            BigDecimal value2 = new BigDecimal(StringUtils.isEmpty(o2.getPermeability())?"-1":o2.getPermeability());
                            return value2.compareTo(value1);
                        }
                    }).collect(Collectors.toList());
                    freshSpecialReportResponse.setList(list);
                }
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(freshSpecialReportResponse);
    }

    @ApiOperation(value = "查询区域生鲜专题报表趋势", notes = "查询区域生鲜专题报表趋势")
    @PostMapping("/queryAreaFreshSpecialReportTrend")
    public DataResult<FreshSpecialReportTrendListResponse> queryAreaFreshSpecialReportTrend(HttpServletRequest request,
                                                                                   HttpServletResponse response,
                                                                                   @RequestBody @ApiParam(value = "查询区域生鲜专题报表趋势参数", required = true, name = "param") FreshSpecialReportRequest param, String personId){
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = null;
        // 记录访问日志
 		try {
 			userService.addPersonLog(personId, "渗透率跟踪排行榜-区域");
 		} catch (Exception e) {
 			log.error("访问日志保存出错", e);
 		}
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            freshSpecialReportTrendListResponse = freshSpecialReportService.queryAreaFreshSpecialReportTrend(param);
            // 设置用户权限等级
            if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
                freshSpecialReportTrendListResponse.setGrade(1);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
                freshSpecialReportTrendListResponse.setGrade(2);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
                freshSpecialReportTrendListResponse.setGrade(3);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
                freshSpecialReportTrendListResponse.setGrade(4);
            } else {
                ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(freshSpecialReportTrendListResponse);
    }


    @ApiOperation(value = "查询门店生鲜专题报表趋势", notes = "查询门店生鲜专题报表趋势")
    @PostMapping("/queryStoreFreshSpecialReportTrend")
    public DataResult<FreshSpecialReportTrendListResponse> queryStoreFreshSpecialReportTrend(HttpServletRequest request,
                                                                                            HttpServletResponse response,
                                                                                            @RequestBody @ApiParam(value = "查询门店生鲜专题报表趋势参数", required = true, name = "param") FreshSpecialReportRequest param, String personId){
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = null;
        // 记录访问日志
  		try {
  			userService.addPersonLog(personId, "渗透率跟踪排行榜-门店");
  		} catch (Exception e) {
  			log.error("访问日志保存出错", e);
  		}
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            freshSpecialReportTrendListResponse = freshSpecialReportService.queryStoreFreshSpecialReportTrend(param);
            // 设置用户权限等级
            if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
                freshSpecialReportTrendListResponse.setGrade(1);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
                freshSpecialReportTrendListResponse.setGrade(2);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
                freshSpecialReportTrendListResponse.setGrade(3);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
                freshSpecialReportTrendListResponse.setGrade(4);
            } else {
                ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(freshSpecialReportTrendListResponse);
    }

    @ApiOperation(value = "查询商品生鲜专题报表趋势", notes = "查询商品生鲜专题报表趋势")
    @PostMapping("/queryShopFreshSpecialReportTrend")
    public DataResult<FreshSpecialReportTrendListResponse> queryShopFreshSpecialReportTrend(HttpServletRequest request,
                                                                                             HttpServletResponse response,
                                                                                             @RequestBody @ApiParam(value = "查询商品生鲜专题报表趋势参数", required = true, name = "param") FreshSpecialReportRequest param, String personId){
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = null;
     // 记录访问日志
  		try {
  			userService.addPersonLog(personId, "渗透率跟踪排行榜-门店");
  		} catch (Exception e) {
  			log.error("访问日志保存出错", e);
  		}
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            freshSpecialReportTrendListResponse = freshSpecialReportService.queryShopFreshSpecialReportTrend(param);
            // 设置用户权限等级
            if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
                freshSpecialReportTrendListResponse.setGrade(1);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
                freshSpecialReportTrendListResponse.setGrade(2);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
                freshSpecialReportTrendListResponse.setGrade(3);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
                freshSpecialReportTrendListResponse.setGrade(4);
            } else {
                ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(freshSpecialReportTrendListResponse);
    }

    @ApiOperation(value = "查询区域生鲜专题排行榜", notes = "查询区域生鲜专题排行榜")
    @PostMapping("/queryAreaFreshSpecialReportRank")
    public DataResult<FreshSpecialReportRankListResponse> queryAreaFreshSpecialReportRank(HttpServletRequest request,
                                                                                          HttpServletResponse response,
                                                                                          @RequestBody @ApiParam(value = "查询区域生鲜专题排行榜参数", required = true, name = "param") FreshSpecialReportRequest param){
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            freshSpecialReportRankListResponse = freshSpecialReportService.queryAreaFreshSpecialReportRank(param);
            // 设置用户权限等级
            if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
                freshSpecialReportRankListResponse.setGrade(1);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
                freshSpecialReportRankListResponse.setGrade(2);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
                freshSpecialReportRankListResponse.setGrade(3);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
                freshSpecialReportRankListResponse.setGrade(4);
            } else {
                ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(freshSpecialReportRankListResponse);
    }

    @ApiOperation(value = "查询门店生鲜专题排行榜", notes = "查询门店生鲜专题排行榜")
    @PostMapping("/queryStoreFreshSpecialReportRank")
    public DataResult<FreshSpecialReportRankListResponse> queryStoreFreshSpecialReportRank(HttpServletRequest request,
                                                                                          HttpServletResponse response,
                                                                                          @RequestBody @ApiParam(value = "查询门店生鲜专题排行榜参数", required = true, name = "param") FreshSpecialReportRequest param){
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            freshSpecialReportRankListResponse = freshSpecialReportService.queryStoreFreshSpecialReportRank(param);
            // 设置用户权限等级
            if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
                freshSpecialReportRankListResponse.setGrade(1);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
                freshSpecialReportRankListResponse.setGrade(2);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
                freshSpecialReportRankListResponse.setGrade(3);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
                freshSpecialReportRankListResponse.setGrade(4);
            } else {
                ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(freshSpecialReportRankListResponse);
    }

    @ApiOperation(value = "查询商品生鲜专题排行榜", notes = "查询商品生鲜专题排行榜")
    @PostMapping("/queryShopFreshSpecialReportRank")
    public DataResult<FreshSpecialReportRankListResponse> queryShopFreshSpecialReportRank(HttpServletRequest request,
                                                                                           HttpServletResponse response,
                                                                                           @RequestBody @ApiParam(value = "查询商品生鲜专题排行榜参数", required = true, name = "param") FreshSpecialReportRequest param){
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = null;
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            ReportCommonParam reportCommonParam = reportRoleHandler(param, userInfo);
            setValue(reportCommonParam, param);
            freshSpecialReportRankListResponse = freshSpecialReportService.queryShopFreshSpecialReportRank(param);
            // 设置用户权限等级
            if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
                freshSpecialReportRankListResponse.setGrade(1);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
                freshSpecialReportRankListResponse.setGrade(2);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
                freshSpecialReportRankListResponse.setGrade(3);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
                freshSpecialReportRankListResponse.setGrade(4);
            } else {
                ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(freshSpecialReportRankListResponse);
    }

    @ApiOperation(value = "获取35，36，37大类", notes = "获取35，36，37大类")
    @PostMapping("/getUserFreshDept")
    public DataResult<Set<String>> getUserFreshDept(HttpServletRequest request, HttpServletResponse response) {
        List<UserDept> result = new ArrayList<>();
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            List<UserDept> list = reportUserDeptService.getUserDeptByCategory(userInfo.getPersonId(), "非加工生鲜");
            if(null != list && list.size() > 0){
                for(UserDept userDept : list){
                    if(35 == userDept.getDeptId().intValue()
                            || 36 == userDept.getDeptId().intValue() || 37 == userDept.getDeptId().intValue()){
                        result.add(userDept);
                        continue;
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
}
