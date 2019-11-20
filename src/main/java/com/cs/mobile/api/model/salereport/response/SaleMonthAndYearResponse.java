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
@ApiModel(value = "SaleMonthAndYearResponse", description = "本月和本年的销售额")
public class SaleMonthAndYearResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "本月销售额", required = true)
    private String totalMonthSale = "0";
    @ApiModelProperty(value = "本月销售额单位", required = true)
    private String totalMonthSaleUnit = "万";

    @ApiModelProperty(value = "本年销售额", required = true)
    private String totalYearSale = "0";
    @ApiModelProperty(value = "本年销售额单位", required = true)
    private String totalYearSaleUnit = "万";

    @JsonIgnore
    private String totalMonthSaleIn = "0";

    @JsonIgnore
    private String totalYearSaleIn = "0";

    public String getTotalMonthSale() {
        BigDecimal value = StringUtils.isEmpty(totalMonthSale) ? BigDecimal.ZERO : new BigDecimal(totalMonthSale);
        BigDecimal valueAbs = value.abs();
        if(valueAbs.compareTo(new BigDecimal(10000)) <= 0){
            totalMonthSaleUnit = "元";
            value = value.divide(new BigDecimal(1),2,BigDecimal.ROUND_HALF_UP);
        }else if(valueAbs.abs().compareTo(new BigDecimal(10000)) > 0
                && valueAbs.compareTo(new BigDecimal(100000000)) <= 0){
            totalMonthSaleUnit = "万";
            value = value.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
        }else{
            totalMonthSaleUnit = "亿";
            value = value.divide(new BigDecimal(100000000),2,BigDecimal.ROUND_HALF_UP);
        }
        return value.toString();
    }

    public String getTotalYearSale() {
        BigDecimal value = StringUtils.isEmpty(totalYearSale) ? BigDecimal.ZERO : new BigDecimal(totalYearSale);
        BigDecimal valueAbs = value.abs();
        if(valueAbs.compareTo(new BigDecimal(10000)) <= 0){
            totalYearSaleUnit = "元";
            value = value.divide(new BigDecimal(1),2,BigDecimal.ROUND_HALF_UP);
        }else if(valueAbs.abs().compareTo(new BigDecimal(10000)) > 0
                && valueAbs.compareTo(new BigDecimal(100000000)) <= 0){
            totalYearSaleUnit = "万";
            value = value.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
        }else{
            totalYearSaleUnit = "亿";
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
