package com.cs.mobile.api.model.freshreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "FreshklStaticsListResponse", description = "生鲜客流统计列表")
public class FreshklStaticsListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "今日", required = true)
    private FreshklStaticsResponse cur;
    @ApiModelProperty(value = "昨日", required = true)
    private FreshklStaticsResponse yestaday;
    @ApiModelProperty(value = "日比", required = true)
    private FreshklStaticsResponse dayRate;
    @ApiModelProperty(value = "周比", required = true)
    private FreshklStaticsResponse weekRate;
    @ApiModelProperty(value = "环比", required = true)
    private FreshklStaticsResponse monthRate;
}
