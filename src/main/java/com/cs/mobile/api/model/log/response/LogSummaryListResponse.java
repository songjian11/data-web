package com.cs.mobile.api.model.log.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "LogSummaryListResponse", description = "日志汇总(展示不同接口请求的最大消耗时间和最小时间)")
public class LogSummaryListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "日志汇总(展示不同接口请求的最大消耗时间和最小时间)", required = true)
    private List<LogSummaryResponse> list;

    @Override
    public String toString() {
        return "{" +
                "\"list\":" + list + "}";
    }
}
