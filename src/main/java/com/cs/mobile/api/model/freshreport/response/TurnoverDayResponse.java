package com.cs.mobile.api.model.freshreport.response;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "TurnoverDayResponse", description = "周转天数")
public class TurnoverDayResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //实际周转天数
    @ApiModelProperty(value = "实际周转天数", required = true)
    private String turnoverDays;

    public String getTurnoverDays() {
        if(StringUtils.isNotEmpty(turnoverDays)){
            BigDecimal value = new BigDecimal(turnoverDays).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return turnoverDays;
        }
    }
}
