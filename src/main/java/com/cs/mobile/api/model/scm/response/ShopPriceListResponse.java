package com.cs.mobile.api.model.scm.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "ShopPriceListResponse", description = "商品top5价格列表")
public class ShopPriceListResponse {
    @ApiModelProperty(value = "商品top5价格列表", required = true)
    private Map<String, List<ShopPriceTrendResponse>> dataMap;
}
