package com.cs.mobile.api.model.freshreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
@ApiModel(value = "HblTurnoverDayListResponse", description = "标准周准天列")
public class HblTurnoverDayListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "周转天数", required = true)
    private List<HblTurnoverDayResponse> result;
}
