package com.cs.mobile.api.model.freshreport.response;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "FreshDeptPermeabilityResponse", description = "生鲜大类渗透率")
public class FreshDeptPermeabilityResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "大类ID", required = true)
    private String deptId;
    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;
    @ApiModelProperty(value = "渗透率", required = true)
    private String permeability ;
    @ApiModelProperty(value = "昨日渗透率", required = true)
    private String yesterdayPermeability ;
    @ApiModelProperty(value = "渗透率日比", required = true)
    private String permeabilityRate ;
    @ApiModelProperty(value = "渗透率周比", required = true)
    private String weekPermeabilityRate ;
    @ApiModelProperty(value = "渗透率环比", required = true)
    private String monthPermeabilityRate ;

    public String getPermeability() {
        if(StringUtils.isNotEmpty(permeability)){
            BigDecimal value = new BigDecimal(permeability).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return permeability;
        }
    }

    public String getPermeabilityRate() {
        if(StringUtils.isNotEmpty(permeabilityRate)){
            BigDecimal value = new BigDecimal(permeabilityRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return permeabilityRate;
        }
    }

    public String getYesterdayPermeability() {
        if(StringUtils.isNotEmpty(yesterdayPermeability)){
            BigDecimal value = new BigDecimal(yesterdayPermeability).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return yesterdayPermeability;
        }
    }

    public String getWeekPermeabilityRate() {
        if(StringUtils.isNotEmpty(weekPermeabilityRate)){
            BigDecimal value = new BigDecimal(weekPermeabilityRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return weekPermeabilityRate;
        }
    }

    public String getMonthPermeabilityRate() {
        if(StringUtils.isNotEmpty(monthPermeabilityRate)){
            BigDecimal value = new BigDecimal(monthPermeabilityRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return monthPermeabilityRate;
        }
    }
}
