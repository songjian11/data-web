package com.cs.mobile.api.model.log.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "LogSummaryResponse", description = "日志汇总(展示接口请求的最大消耗时间和最小时间)")
public class LogSummaryResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "最大消耗时间的请求时间", required = true)
    private String requestMaxTime;
    @ApiModelProperty(value = "最小消耗时间的请求时间", required = true)
    private String requestMinTime;
    @ApiModelProperty(value = "请求地址", required = true)
    private String requestUrl;
    @ApiModelProperty(value = "请求次数", required = true)
    private String requestCount;
    @ApiModelProperty(value = "最大消耗时间", required = true)
    private String maxConsumTime;
    @ApiModelProperty(value = "最小消耗时间", required = true)
    private String minConsumTime;

    @Override
    public String toString() {
        return "{" +
                "\"最大耗时请求时间\":\"" + requestMaxTime + '\"' +
                ", \"最小耗时请求时间\":\"" + requestMinTime + '\"' +
                ", \"请求地址\":\"" + requestUrl + '\"' +
                ", \"请求数量\":\"" + requestCount + '\"' +
                ", \"最大耗时\":\"" + maxConsumTime + '\"' +
                ", \"最小耗时\":\"" + minConsumTime + '\"' +
                '}';
    }
}