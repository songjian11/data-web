package com.cs.mobile.api.model.scm.response;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "ShopPriceTrendResponse", description = "商品价格明细")
public class ShopPriceTrendResponse {
    @ApiModelProperty(value = "商品编号", required = true)
    private String item;
    @ApiModelProperty(value = "商品名称", required = true)
    private String itemName;
    @ApiModelProperty(value = "价格", required = true)
    private String money;
    @ApiModelProperty(value = "月份", required = true)
    private String month;

    public String getMoney() {
        BigDecimal value = StringUtils.isEmpty(money) ? BigDecimal.ZERO : new BigDecimal(money).divide(BigDecimal.ONE,2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }
}
