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
@ApiModel(value = "RateMonthAndYearResponse", description = "本月和本年的毛利额")
public class RateMonthAndYearResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "本月毛利额", required = true)
    private String totalMonthRate = "0";

    @ApiModelProperty(value = "本月毛利额单位", required = true)
    private String totalMonthRateUnit = "万";

    @ApiModelProperty(value = "本年毛利额", required = true)
    private String totalYearRate = "0";
    @ApiModelProperty(value = "本年毛利额单位", required = true)
    private String totalYearRateUnit = "万";

    @JsonIgnore
    private String totalMonthRateIn = "0";

    @JsonIgnore
    private String totalYearRateIn = "0";

    public String getTotalMonthRate() {
        BigDecimal value = StringUtils.isEmpty(totalMonthRate) ? BigDecimal.ZERO : new BigDecimal(totalMonthRate);
        BigDecimal valueAbs = value.abs();
        if(valueAbs.compareTo(new BigDecimal(10000)) <= 0){
            totalMonthRateUnit = "元";
            value = value.divide(new BigDecimal(1),2,BigDecimal.ROUND_HALF_UP);
        }else if(valueAbs.abs().compareTo(new BigDecimal(10000)) > 0
                && valueAbs.compareTo(new BigDecimal(100000000)) <= 0){
            totalMonthRateUnit = "万";
            value = value.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
        }else{
            totalMonthRateUnit = "亿";
            value = value.divide(new BigDecimal(100000000),2,BigDecimal.ROUND_HALF_UP);
        }
        return value.toString();
    }

    public String getTotalYearRate() {
        BigDecimal value = StringUtils.isEmpty(totalYearRate) ? BigDecimal.ZERO : new BigDecimal(totalYearRate);
        BigDecimal valueAbs = value.abs();
        if(valueAbs.compareTo(new BigDecimal(10000)) <= 0){
            totalYearRateUnit = "元";
            value = value.divide(new BigDecimal(1),2,BigDecimal.ROUND_HALF_UP);
        }else if(valueAbs.abs().compareTo(new BigDecimal(10000)) > 0
                && valueAbs.compareTo(new BigDecimal(100000000)) <= 0){
            totalYearRateUnit = "万";
            value = value.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
        }else{
            totalYearRateUnit = "亿";
            value = value.divide(new BigDecimal(100000000),2,BigDecimal.ROUND_HALF_UP);
        }
        return value.toString();
    }

    public void setTaxData() throws IllegalAccessException {
        Field[] fields = FieldUtils.getAllFields(this.getClass());
        for(int i=0; i < fields.length; i++){
            Field field = fields[i];
            field.setAccessible(true);
            if(field.getName().contains("In")){
                for(int j=0; j < fields.length; j++){
                    Field ff = fields[j];
                    ff.setAccessible(true);
                    String name = ff.getName() + "In";
                    if(name.equals(field.getName())){
                        ff.set(this,field.get(this));
                        break;
                    }
                }
            }
        }
    }
}
