package com.cs.mobile.api.model.scm;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "shopPriceTrend", description = "商品价格趋势")
public class ShopPriceTrend{
    private String item;

    private String itemName;

    private String money;

    private String month;
}
