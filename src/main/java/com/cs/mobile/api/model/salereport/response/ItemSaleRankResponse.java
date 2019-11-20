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
@ApiModel(value = "ItemSaleRankResponse", description = "大类单品销售排名")
public class ItemSaleRankResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //大类ID
    @ApiModelProperty(value = "大类ID", required = true)
    private String deptId;
    //大类名称
    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;
    //单品ID
    @ApiModelProperty(value = "单品ID", required = true)
    private String itemId;
    //单品名称
    @ApiModelProperty(value = "单品名称", required = true)
    private String itemName;
    //销售额
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSale = "0";
    //环比
    @ApiModelProperty(value = "环比", required = true)
    private String monthSaleProfit;
    //可比
    @ApiModelProperty(value = "可比", required = true)
    private String compareSaleProfit;

    //销售额
    @JsonIgnore
    private String totalSaleIn = "0";
    //环比
    @JsonIgnore
    private String monthSaleProfitIn;
    //可比
    @JsonIgnore
    private String compareSaleProfitIn;

    //排名
    @ApiModelProperty(value = "排名", required = true)
    private String rn;

    public String getTotalSale() {
        BigDecimal value = StringUtils.isEmpty(totalSale) ? BigDecimal.ZERO : new BigDecimal(totalSale).divide(BigDecimal.ONE,2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getMonthSaleProfit() {
        if(StringUtils.isNotEmpty(monthSaleProfit)){
            BigDecimal value = new BigDecimal(monthSaleProfit).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return monthSaleProfit;
        }
    }

    public String getCompareSaleProfit() {
        if(StringUtils.isNotEmpty(compareSaleProfit)){
            BigDecimal value = new BigDecimal(compareSaleProfit).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return compareSaleProfit;
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
