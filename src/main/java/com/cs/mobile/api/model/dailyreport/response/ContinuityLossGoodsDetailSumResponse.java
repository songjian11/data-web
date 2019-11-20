package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ContinuityLossGoodsDetailSumResponse", description = "门店连续负毛利商品下钻")
public class ContinuityLossGoodsDetailSumResponse {
	private static final long serialVersionUID = 1L;
	
	
	
	@ApiModelProperty(value = "数量", required = true)
	private String saleQtySum;
    
	@ApiModelProperty(value = "销售额", required = true)
	private String gpRateSum;
    
    @ApiModelProperty(value = "毛利率", required = true)
    private String grossMarginSum;
    
    @ApiModelProperty(value = "损失额", required = true)
    private String grossLossSum;

    
}
