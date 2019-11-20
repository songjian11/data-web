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
@ApiModel(value = "SaleTrendResponse", description = "销售趋势")
public class SaleTrendResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //时间点（yyyyMMdd）
    @ApiModelProperty(value = "时间轴(yyyyMMdd)", required = true)
    private String time;
    //销售额
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSale ="0";
    //毛利率
    @ApiModelProperty(value = "毛利率", required = true)
    private String totalProfit;

    @JsonIgnore
    private String totalSaleIn ="0";
    //毛利率
    @JsonIgnore
    private String totalProfitIn;

    public String getTotalSale() {
        BigDecimal value = StringUtils.isEmpty(totalSale) ? BigDecimal.ZERO : new BigDecimal(totalSale).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
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
