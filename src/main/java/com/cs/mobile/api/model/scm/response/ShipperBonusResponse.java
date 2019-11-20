package com.cs.mobile.api.model.scm.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ShipperBonusResponse", description = "发货人奖金明细")
public class ShipperBonusResponse {
    @ApiModelProperty(value = "发货人编号", required = true)
    private String shipperCode;
    @ApiModelProperty(value = "发货人名称", required = true)
    private String shipperName;
    @ApiModelProperty(value = "奖金", required = true)
    private String money;
}
