package com.cs.mobile.api.model.salereport.response;

import com.cs.mobile.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;

@Data
@ApiModel(value = "AchievingRateResponse", description = "达成率")
public class AchievingRateResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "销售额达成率", required = true)
    private String saleAchievingRate;

    @ApiModelProperty(value = "毛利额达成率", required = true)
    private String rateAchievingRate;

    public String getSaleAchievingRate() {
        if(StringUtils.isNotEmpty(saleAchievingRate)){
            BigDecimal value = new BigDecimal(saleAchievingRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return saleAchievingRate;
        }
    }

    public String getRateAchievingRate() {
        if(StringUtils.isNotEmpty(rateAchievingRate)){
            BigDecimal value = new BigDecimal(rateAchievingRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return rateAchievingRate;
        }
    }
}
