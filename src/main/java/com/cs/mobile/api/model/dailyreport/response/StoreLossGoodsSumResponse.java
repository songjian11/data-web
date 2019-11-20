package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreLossGoodsSumResponse", description = "门店负毛利商品top30汇总")
public class StoreLossGoodsSumResponse {
	private static final long serialVersionUID = 1L;
	
    @ApiModelProperty(value = "数量汇总", required = true)
    private String saleQtySum;
    
    @ApiModelProperty(value = "未销售金额汇总", required = true)
    private String gpRateSum;
    
    @ApiModelProperty(value = "毛利率汇总", required = true)
    private String gpSum;
    
    @ApiModelProperty(value = "损失额汇总", required = true)
    private String groupLossSum;

}
