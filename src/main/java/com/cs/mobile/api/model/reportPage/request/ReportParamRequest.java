package com.cs.mobile.api.model.reportPage.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "ReportParamRequest", description = "时线，日线，月线")
public class ReportParamRequest extends PageRequest implements Serializable {
    @ApiModelProperty(value = "类型(1-时线,2-日线,3-月线)", required = true)
    private int type;
}
