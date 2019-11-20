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
@ApiModel(value = "ItemRateRankResponse", description = "大类单品毛利额排名")
public class ItemRateRankResponse implements Serializable {
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
    //毛利额
    @ApiModelProperty(value = "毛利额", required = true)
    private String totalRate = "0";
    //环比
    @ApiModelProperty(value = "环比", required = true)
    private String monthRateProfit;
    //可比
    @ApiModelProperty(value = "可比", required = true)
    private String compareRateProfit;
    //毛利额
    @JsonIgnore
    private String totalRateIn = "0";
    //环比
    @JsonIgnore
    private String monthRateProfitIn;
    //可比
    @JsonIgnore
    private String compareRateProfitIn;
    //排名
    @ApiModelProperty(value = "排名", required = true)
    private String rn;

    public String getTotalRate() {
        BigDecimal value = StringUtils.isEmpty(totalRate) ? BigDecimal.ZERO : new BigDecimal(totalRate).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getMonthRateProfit() {
        if(StringUtils.isNotEmpty(monthRateProfit)){
            BigDecimal value = new BigDecimal(monthRateProfit).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return monthRateProfit;
        }
    }

    public String getCompareRateProfit() {
        if(StringUtils.isNotEmpty(compareRateProfit)){
            BigDecimal value = new BigDecimal(compareRateProfit).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return compareRateProfit;
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
