package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GrossProfitGoodsResponse", description = "单品毛利率卡片")
public class GrossProfitGoodsResponse {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "门店毛利率", required = true)
    private String storeGp;
	
	@ApiModelProperty(value = "区域毛利率", required = true)
    private String areaGp;
    
	@ApiModelProperty(value = "省份毛利率", required = true)
    private String provinceGp;
    
    @ApiModelProperty(value = "全司毛利率", required = true)
    private String allGp;
    
  
    
    

}
