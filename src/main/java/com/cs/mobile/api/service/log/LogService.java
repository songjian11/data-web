package com.cs.mobile.api.service.log;

import com.cs.mobile.api.model.log.request.LogRequest;
import com.cs.mobile.api.model.log.response.LogSummaryListResponse;

public interface LogService {
    /**
     * 获取接口的最大消耗时间和最小消耗时间
     * @param logRequest
     * @return
     * @throws Exception
     */
    public LogSummaryListResponse queryLogSummaryList(LogRequest logRequest)throws Exception;
}
