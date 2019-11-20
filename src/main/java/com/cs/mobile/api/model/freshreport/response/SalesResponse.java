package com.cs.mobile.api.model.freshreport.response;

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
@ApiModel(value = "SalesListResponse", description = "销售列表列")
public class SalesResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //销售额
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSales;
    //毛利额
    @ApiModelProperty(value = "毛利额", required = true)
    private String totalProfitPrice;
    //毛利率
    @ApiModelProperty(value = "毛利率", required = true)
    private String totalProfit;

    //销售额比
    @ApiModelProperty(value = "销售额比", required = true)
    private String totalSalesRate;
    //毛利额比
    @ApiModelProperty(value = "毛利额比", required = true)
    private String totalProfitPriceRate;
    //毛利率比
    @ApiModelProperty(value = "毛利率比", required = true)
    private String totalProfitRate;



    @JsonIgnore
    private String totalSalesIn;
    //毛利额
    @JsonIgnore
    private String totalProfitPriceIn;
    //毛利率
    @JsonIgnore
    private String totalProfitIn;
    //销售额比
    @JsonIgnore
    private String totalSalesRateIn;
    //毛利额比
    @JsonIgnore
    private String totalProfitPriceRateIn;
    //毛利率比
    @JsonIgnore
    private String totalProfitRateIn;

    public String getTotalSales() {
        BigDecimal value = StringUtils.isEmpty(totalSales) ? BigDecimal.ZERO : new BigDecimal(totalSales).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalProfitPrice() {
        BigDecimal value = StringUtils.isEmpty(totalProfitPrice) ? BigDecimal.ZERO : new BigDecimal(totalProfitPrice).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalProfit() {
        if(StringUtils.isNotEmpty(totalProfit)){
            BigDecimal value = new BigDecimal(totalProfit).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return totalProfit;
        }
    }

    public String getTotalSalesRate() {
        if(StringUtils.isNotEmpty(totalSalesRate)){
            BigDecimal value = new BigDecimal(totalSalesRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return totalSalesRate;
        }
    }

    public String getTotalProfitPriceRate() {
        if(StringUtils.isNotEmpty(totalProfitPriceRate)){
            BigDecimal value = new BigDecimal(totalProfitPriceRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return totalProfitPriceRate;
        }
    }

    public String getTotalProfitRate() {
        if(StringUtils.isNotEmpty(totalProfitRate)){
            BigDecimal value = new BigDecimal(totalProfitRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return totalProfitRate;
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
