package com.cs.mobile.api.model.freshreport.response;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@ApiModel(value = "FreshDeptKlResponse", description = "生鲜大类客流")
public class FreshDeptKlResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "大类ID", required = true)
    private String deptId;
    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;
    @ApiModelProperty(value = "客流", required = true)
    private String kl ;
    @ApiModelProperty(value = "昨日客流", required = true)
    private String yesterDayKl ;
    @ApiModelProperty(value = "客流日比", required = true)
    private String klRate ;
    @ApiModelProperty(value = "客流周比", required = true)
    private String weekKlRate ;
    @ApiModelProperty(value = "客流环比", required = true)
    private String monthKlRate ;

    public String getKlRate() {
        if(StringUtils.isNotEmpty(klRate)){
            BigDecimal value = new BigDecimal(klRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return klRate;
        }
    }

    public String getWeekKlRate() {
        if(StringUtils.isNotEmpty(weekKlRate)){
            BigDecimal value = new BigDecimal(weekKlRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return weekKlRate;
        }
    }

    public String getMonthKlRate() {
        if(StringUtils.isNotEmpty(monthKlRate)){
            BigDecimal value = new BigDecimal(monthKlRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return monthKlRate;
        }
    }
}
