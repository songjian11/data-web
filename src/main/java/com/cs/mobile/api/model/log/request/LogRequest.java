package com.cs.mobile.api.model.log.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "LogRequest", description = "日志分析")
public class LogRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "时间(yyyy-MM-dd)", required = false)
    private String time;
    @ApiModelProperty(value = "接口请求地址", required = true)
    private String url;
    @ApiModelProperty(value = "文件地址", required = false)
    private String address;
}
