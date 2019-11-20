package com.cs.mobile.api.controller.partner;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.partner.PartnerUserInfo;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.model.user.User;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.common.RedisService;
import com.cs.mobile.api.service.reportPage.ReportUserDeptService;
import com.cs.mobile.api.service.user.UserService;
import com.cs.mobile.common.constant.UserTypeEnum;
import com.cs.mobile.common.exception.api.ExceptionUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Api(value = "用户接口", tags = {"用户接口"})
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController extends AbstractApiController {

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @Autowired
    private ReportUserDeptService reportUserDeptService;

    @ApiOperation(value = "根据工号获取用户信息", notes = "根据工号获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "personId", value = "工号", required = true, dataType = "String")})
    @GetMapping("/info")
    public DataResult<UserInfo> getUserInfo(HttpServletRequest request, HttpServletResponse response, String personId) {
        PartnerUserInfo partnerUserInfo = null;
        UserInfo userInfo = null;
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "中间页");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }

        try {
            List<User> userList = userService.getUserListByPersonId(personId);
            // modify by wells.wong 20190627 加入调拨应用，放开次权限，以保证有调拨权限但是不存在BI智慧用户表里的用户能访问调拨应用
            if (userList == null || userList.size() == 0) {
                return DataHandler.jsonResult(666, "该用户可能有调拨权限", "");
                // ExceptionUtils.wapperBussinessException("您没有访问权限");
            }
            userInfo = this.userHandler(userList);
            if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_STOREADMIN.getType())) {// 管理员大店店长
                partnerUserInfo = userService.getSUserInfoByPersonId(personId,
                        UserTypeEnum.PARTNER_STOREADMIN.getType());
            } else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_AREAADMIN.getType())) {// 区域管理员
                partnerUserInfo = userService.getAUserInfoByPersonId(personId,
                        UserTypeEnum.PARTNER_AREAADMIN.getType());
            } else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_SOTRE.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.PARTNER_COM.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.PARTNER_COMMON.getType())) {// 大店店长,小店店长,合伙人
                partnerUserInfo = userService.getUserInfoByPersonId(personId);
            } else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_PROVINCEADMIN.getType())) {// 省份管理员
                partnerUserInfo = userService.getPUserInfoByPersonId(personId,
                        UserTypeEnum.PARTNER_PROVINCEADMIN.getType());
            } else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_ENTERPRISEADMIN.getType())
                    || userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())) {// 全司管理员、超级管理员
                partnerUserInfo = new PartnerUserInfo();
                partnerUserInfo.setEnterpriseId("0");
                partnerUserInfo.setEnterpriseName("超市事业部");
            }
            userInfo.setPartnerUserInfo(partnerUserInfo);
            if (userInfo == null) {
                ExceptionUtils.wapperBussinessException("用户信息异常");
            } else if (partnerUserInfo != null && "收银小店".equals(partnerUserInfo.getComName())) {
                ExceptionUtils.wapperBussinessException("收银小店暂未开放，敬请期待");
            }
            // 用户信息保存到redis一个月
            redisService.setObject("user:" + personId, userInfo, 2 * 60 * 60);// 用户信息保存到redis 2个小时
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(userInfo);
    }

    private UserInfo userHandler(List<User> userList) throws Exception {
        UserInfo userInfo = new UserInfo();
        List<Integer> typeList = new ArrayList<Integer>();
        for (User user : userList) {
            userInfo.setName(user.getName());
            userInfo.setPersonId(user.getPersonId());
            typeList.add(user.getType());
        }
        userInfo.setTypeList(typeList);
        List<UserDept> userDeptList = reportUserDeptService.getUserDeptList(userInfo.getPersonId());
        /*
         * 合伙人权限级别最低 modify by wells.wong
         * 合伙人权限：<br> 合伙人大店店长、合伙人小店店长、普通合伙人、合伙人区域管理员、 <br>
         * 合伙人管理员大店店长、合伙人省份管理员、合伙人全司管理员<br>
         */
        if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_SOTRE.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.PARTNER_COM.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.PARTNER_COMMON.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.PARTNER_AREAADMIN.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.PARTNER_STOREADMIN.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.PARTNER_PROVINCEADMIN.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.PARTNER_ENTERPRISEADMIN.getType())) {
            userInfo.setIndexType(1);
        }
        // 如果是基地回货权限，先设置首页为4，后面再判断其他权限，有其他权限则以后面的权限为准
        if (userInfo.getTypeList().contains(UserTypeEnum.JD_PURCHASER.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.ZB_PURCHASER.getType())) {
            userInfo.setIndexType(4);
        }
        // 首页优先级 合伙人首页 > 生鲜专题 > 报表首页
        /*
         * 报表权限：<br> 超级管理员、移动报表全司管理员、移动报表省份管理员、移动报表区域管理员、移动报表门店管理员
         */
        if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())
                || userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {
            userInfo.setIndexType(2);
            // 生鲜专题权限（报表权限的子权限）
            int count = 0;
            for (UserDept userDept : userDeptList) {
                if (userDept.getDeptId().intValue() == 0) {
                    count = 0;
                    // modify by wells.wong 20190730 超级管理员进入首页报表
                    if (!userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())) {
                        userInfo.setIndexType(3);
                    }
                    userInfo.getTypeList().add(UserTypeEnum.MREPORT_FRESH.getType());
                    break;
                } else if (userDept.getDeptId().intValue() == 32 || userDept.getDeptId().intValue() == 35
                        || userDept.getDeptId().intValue() == 36 || userDept.getDeptId().intValue() == 37) {
                    count++;
                }
            }
            if (count >= 4) {
                userInfo.setIndexType(3);
                userInfo.getTypeList().add(UserTypeEnum.MREPORT_FRESH.getType());
            }
        }

        /*
         * 移动报表门店管理员权限且不是店长跳转报表中心
         * songj-20190923
         */
        if(userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())
                && 0 == userService.isStoreManager(userInfo.getPersonId())){
           userInfo.setIndexType(6);
        }

        /*
         * 判断是否包含湖南报表权限<br> 是则可以查看市场部专属
         */
        if (userInfo.getTypeList().contains(UserTypeEnum.SC_REPORT.getType())) {
            userInfo.setIndexType(5);
        }

        return userInfo;
    }
}
