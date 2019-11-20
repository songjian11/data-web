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
@ApiModel(value = "SaleCompositionResponse", description = "销售构成")
public class SaleCompositionResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSale;

    @ApiModelProperty(value = "毛利率", required = true)
    private String totalRateProfit;

    @JsonIgnore
    private String totalSaleIn;

    @JsonIgnore
    private String totalRateProfitIn;

    @ApiModelProperty(value = "类型(1-正常 2-大宗 3-折价,5-促销)", required = true)
    private String type;

    public String getTotalSale() {
        BigDecimal value = StringUtils.isEmpty(totalSale) ? BigDecimal.ZERO : new BigDecimal(totalSale).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalRateProfit() {
        if(StringUtils.isNotEmpty(totalRateProfit)){
            BigDecimal value = new BigDecimal(totalRateProfit).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return totalRateProfit;
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
