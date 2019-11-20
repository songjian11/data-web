package com.cs.mobile.api.controller.scm;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.scm.response.StoreOrderReportResponse;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.scm.StoreOrderReportService;
import com.cs.mobile.api.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Api(value = "storeOrderReport", tags = { "门店要货报表接口" })
@RestController
@RequestMapping("/api/storeOrderReport")
@Slf4j
public class StoreOrderReportController extends AbstractApiController {
    @Autowired
    private StoreOrderReportService storeOrderReportService;
    @Autowired
    UserService userService;

    @ApiOperation(value = "门店要货报表", notes = "门店要货报表")
    @GetMapping("/queryStoreOrderReport")
    public DataResult<StoreOrderReportResponse> queryStoreOrderReport(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      String deptIds) {
        StoreOrderReportResponse result = null;
        // 记录访问日志
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            userService.addPersonLog(userInfo.getPersonId(), "门店要货报表");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            result = storeOrderReportService.queryStoreOrderReport(deptIds);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(result);
    }
}
