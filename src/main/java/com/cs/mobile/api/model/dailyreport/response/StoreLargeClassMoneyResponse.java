package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreLargeClassMoneyResponse", description = "门店大类库存金额")
public class StoreLargeClassMoneyResponse {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "大类编码", required = true)
    private String dept;
	
	@ApiModelProperty(value = "大类名称", required = true)
    private String deptName;
    
	@ApiModelProperty(value = "库存金额", required = true)
    private String realSohAmt;
    
    @ApiModelProperty(value = "占比", required = true)
    private String amtPercent;
    
    @ApiModelProperty(value = "单位", required = true)
    private String unit;
    
    @ApiModelProperty(value = "标准周转天数", required = true)
    private String standardDays;
    
    @ApiModelProperty(value = "实际周转天数", required = true)
    private String actualDays;
    
}
