package com.cs.mobile.api.model.scm;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "shopInfo", description = "商品信息")
public class ShopInfo {
    private String item;

    private String itemName;
}
