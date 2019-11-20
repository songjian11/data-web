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
@ApiModel(value = "AllSaleSumResponse", description = "销售模块汇总")
public class AllSaleSumResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSale = "0";

    @ApiModelProperty(value = "销售额可比率", required = true)
    private String saleCompareRate;

    @ApiModelProperty(value = "销售额环比", required = true)
    private String saleMonthRate;

    @ApiModelProperty(value = "销售额同比", required = true)
    private String saleSameRate;

    @JsonIgnore
    private String totalSaleIn = "0";

    @JsonIgnore
    private String saleCompareRateIn;

    @JsonIgnore
    private String saleMonthRateIn;

    @JsonIgnore
    private String saleSameRateIn;

    public String getTotalSale() {
        BigDecimal value = StringUtils.isEmpty(totalSale) ? BigDecimal.ZERO : new BigDecimal(totalSale).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getSaleCompareRate() {
        if(StringUtils.isNotEmpty(saleCompareRate)){
            BigDecimal value = new BigDecimal(saleCompareRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return saleCompareRate;
        }
    }

    public String getSaleMonthRate() {
        if(StringUtils.isNotEmpty(saleMonthRate)){
            BigDecimal value = new BigDecimal(saleMonthRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return saleMonthRate;
        }
    }

    public String getSaleSameRate() {
        if(StringUtils.isNotEmpty(saleSameRate)){
            BigDecimal value = new BigDecimal(saleSameRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return saleSameRate;
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
