package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ContinuityLossGoodsDetailResponse", description = "门店连续负毛利商品下钻")
public class ContinuityLossGoodsDetailResponse {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "销售日期", required = true)
	private String saleDate;
	
	@ApiModelProperty(value = "数量", required = true)
	private String saleQty;
    
	@ApiModelProperty(value = "销售额", required = true)
	private String gpRate;
    
    @ApiModelProperty(value = "毛利率", required = true)
    private String grossMargin;
    
    @ApiModelProperty(value = "损失额", required = true)
    private String grossLoss;
    
    @ApiModelProperty(value = "单位", required = true)
    private String unit;
    
}
