package com.cs.mobile.api.model.freshreport.response;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "FreshklStaticsResponse", description = "生鲜客流")
public class FreshklStaticsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "客流", required = true)
    private String kl = "0";
    @ApiModelProperty(value = "渗透率", required = true)
    private String permeability;
    @ApiModelProperty(value = "会员渗透率", required = true)
    private String memberPermeability;

    @ApiModelProperty(value = "客流比", required = true)
    private String klRate;
    @ApiModelProperty(value = "渗透率比", required = true)
    private String permeabilityRate;
    @ApiModelProperty(value = "会员渗透率比", required = true)
    private String memberPermeabilityRate;

    public String getPermeability() {
        if(StringUtils.isNotEmpty(permeability)){
            BigDecimal value = new BigDecimal(permeability).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return permeability;
        }
    }

    public String getMemberPermeability() {
        if(StringUtils.isNotEmpty(memberPermeability)){
            BigDecimal value = new BigDecimal(memberPermeability).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return memberPermeability;
        }
    }

    public String getKlRate() {
        if(StringUtils.isNotEmpty(klRate)){
            BigDecimal value = new BigDecimal(klRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return klRate;
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

    public String getMemberPermeabilityRate() {
        if(StringUtils.isNotEmpty(memberPermeabilityRate)){
            BigDecimal value = new BigDecimal(memberPermeabilityRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return memberPermeabilityRate;
        }
    }
}
