package com.cs.mobile.api.model.freshreport.response;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "FreshDeptMemberPermeabilityResponse", description = "生鲜大类会员渗透率")
public class FreshDeptMemberPermeabilityResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "大类ID", required = true)
    private String deptId;
    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;
    @ApiModelProperty(value = "会员渗透率", required = true)
    private String memberPermeability ;
    @ApiModelProperty(value = "昨日会员渗透率", required = true)
    private String yesterdayMemberPermeability ;
    @ApiModelProperty(value = "会员渗透率日比", required = true)
    private String memberPermeabilityRate ;
    @ApiModelProperty(value = "会员渗透率周比", required = true)
    private String weekMemberPermeabilityRate ;
    @ApiModelProperty(value = "会员渗透率环比", required = true)
    private String monthMemberPermeabilityRate ;

    public String getMemberPermeability() {
        if(StringUtils.isNotEmpty(memberPermeability)){
            BigDecimal value = new BigDecimal(memberPermeability).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return memberPermeability;
        }
    }

    public String getMemberPermeabilityRate() {
        if(StringUtils.isNotEmpty(memberPermeabilityRate)){
            BigDecimal value = new BigDecimal(memberPermeabilityRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return memberPermeabilityRate;
        }
    }

    public String getYesterdayMemberPermeability() {
        if(StringUtils.isNotEmpty(yesterdayMemberPermeability)){
            BigDecimal value = new BigDecimal(yesterdayMemberPermeability).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return yesterdayMemberPermeability;
        }
    }

    public String getWeekMemberPermeabilityRate() {
        if(StringUtils.isNotEmpty(weekMemberPermeabilityRate)){
            BigDecimal value = new BigDecimal(weekMemberPermeabilityRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return weekMemberPermeabilityRate;
        }
    }

    public String getMonthMemberPermeabilityRate() {
        if(StringUtils.isNotEmpty(monthMemberPermeabilityRate)){
            BigDecimal value = new BigDecimal(monthMemberPermeabilityRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return monthMemberPermeabilityRate;
        }
    }
}
