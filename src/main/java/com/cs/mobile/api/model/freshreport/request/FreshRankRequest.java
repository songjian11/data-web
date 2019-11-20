package com.cs.mobile.api.model.freshreport.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "FreshRankRequest", description = "生鲜专题报表")
public class FreshRankRequest extends FreshReportBaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "查询数据等级（1-省，2-区，3-门店，4-大类）", required = true)
    private int mark = 0;
}
