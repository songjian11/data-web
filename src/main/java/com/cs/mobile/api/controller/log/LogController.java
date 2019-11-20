package com.cs.mobile.api.controller.log;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.freshreport.request.FreshReportBaseRequest;
import com.cs.mobile.api.model.freshreport.response.MonthStatisticsResponse;
import com.cs.mobile.api.model.log.request.LogRequest;
import com.cs.mobile.api.model.log.response.LogSummaryListResponse;
import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "log", tags = { "日志分析" })
@RestController
@RequestMapping("/api/log")
public class LogController extends AbstractApiController {
    @Autowired
    private LogService logService;

    @ApiOperation(value = "汇总接口最大消耗时间和最小时间消耗", notes = "汇总接口最大消耗时间和最小时间消耗")
    @PostMapping("/queryLogSummaryList")
    public DataResult<LogSummaryListResponse> queryLogSummaryList(HttpServletRequest request,
                                                                            HttpServletResponse response,
                                                                            @RequestBody @ApiParam(value = "日志分析参数", required = true, name = "param") LogRequest param) {
        LogSummaryListResponse logSummaryListResponse = null;
        try {
            logSummaryListResponse = logService.queryLogSummaryList(param);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(logSummaryListResponse);
    }
}
