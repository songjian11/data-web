package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ContinuityLossGoodsResponse", description = "门店连续负毛利商品")
public class ContinuityLossGoodsResponse {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "大类编码", required = true)
    private String dept;
	
	@ApiModelProperty(value = "商品编码", required = true)
    private String item;
    
	@ApiModelProperty(value = "商品名称", required = true)
    private String itemDesc;
    
    @ApiModelProperty(value = "销售类型", required = true)
    private String saleTypeDesc;
    
    @ApiModelProperty(value = "次数", required = true)
    private String frequency;
    
}
