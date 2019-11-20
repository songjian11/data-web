package com.cs.mobile.api.model.scm.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
@ApiModel(value = "ShipperBonusListResponse", description = "发货人奖金列表")
public class ShipperBonusListResponse {
    @ApiModelProperty(value = "发货人奖金列表", required = true)
    private List<ShipperBonusResponse> list;
}
