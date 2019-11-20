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
@ApiModel(value = "AllRateSumResponse", description = "毛利额模块汇总")
public class AllRateSumResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "毛利额", required = true)
    private String totalRate = "0";

    @ApiModelProperty(value = "毛利额可比率", required = true)
    private String rateCompareRate;

    @ApiModelProperty(value = "毛利额环比", required = true)
    private String rateMonthRate ;

    @ApiModelProperty(value = "毛利额同比", required = true)
    private String rateSameRate ;

    @JsonIgnore
    private String totalRateIn = "0";

    @JsonIgnore
    private String rateCompareRateIn;

    @JsonIgnore
    private String rateMonthRateIn ;

    @JsonIgnore
    private String rateSameRateIn ;

    public String getTotalRate() {
        BigDecimal value = StringUtils.isEmpty(totalRate) ? BigDecimal.ZERO : new BigDecimal(totalRate).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getRateCompareRate() {
        if(StringUtils.isNotEmpty(rateCompareRate)){
            BigDecimal value = new BigDecimal(rateCompareRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return rateCompareRate;
        }
    }

    public String getRateMonthRate() {
        if(StringUtils.isNotEmpty(rateMonthRate)){
            BigDecimal value = new BigDecimal(rateMonthRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return rateMonthRate;
        }
    }

    public String getRateSameRate() {
        if(StringUtils.isNotEmpty(rateSameRate)){
            BigDecimal value = new BigDecimal(rateSameRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return rateSameRate;
        }
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
