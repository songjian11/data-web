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
@ApiModel(value = "FreshRankInfoResponse", description = "生鲜属性")
public class FreshRankInfoResponse extends BaseRankResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSales = "0";
    @ApiModelProperty(value = "销售可比月增长率", required = true)
    private String salesCompareMonthGrowthRate;
    @ApiModelProperty(value = "销售同比月增长率", required = true)
    private String salesSameMonthGrowthRate;
    @ApiModelProperty(value = "扫描毛利率", required = true)
    private String sysRate;
    @ApiModelProperty(value = "毛利可比月增长率", required = true)
    private String profitCompareMonthGrowthRate;
    @ApiModelProperty(value = "毛利同比月增长率", required = true)
    private String profitSameMonthGrowthRate;
    @ApiModelProperty(value = "上月损耗率", required = true)
    private String lossRate;
    @ApiModelProperty(value = "上月折价率", required = true)
    private String discountRate;
    @ApiModelProperty(value = "客流渗透率", required = true)
    private String klRate;

    @JsonIgnore
    private String totalSalesIn = "0";
    @JsonIgnore
    private String salesCompareMonthGrowthRateIn;
    @JsonIgnore
    private String salesSameMonthGrowthRateIn;
    @JsonIgnore
    private String sysRateIn;
    @JsonIgnore
    private String profitCompareMonthGrowthRateIn;
    @JsonIgnore
    private String profitSameMonthGrowthRateIn;
    @JsonIgnore
    private String lossRateIn;
    @JsonIgnore
    private String discountRateIn;

    public String getKlRate() {
        if(StringUtils.isNotEmpty(klRate)){
            BigDecimal value = new BigDecimal(klRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return klRate;
        }
    }

    public String getTotalSales() {
        BigDecimal value = StringUtils.isEmpty(totalSales) ? BigDecimal.ZERO : new BigDecimal(totalSales).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getSalesCompareMonthGrowthRate() {
        if(StringUtils.isNotEmpty(salesCompareMonthGrowthRate)){
            BigDecimal value = new BigDecimal(salesCompareMonthGrowthRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return salesCompareMonthGrowthRate;
        }
    }

    public String getSalesSameMonthGrowthRate() {
        if(StringUtils.isNotEmpty(salesSameMonthGrowthRate)){
            BigDecimal value = new BigDecimal(salesSameMonthGrowthRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return salesSameMonthGrowthRate;
        }
    }

    public String getSysRate() {
        if(StringUtils.isNotEmpty(sysRate)){
            BigDecimal value = new BigDecimal(sysRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return sysRate;
        }
    }

    public String getProfitCompareMonthGrowthRate() {
        if(StringUtils.isNotEmpty(profitCompareMonthGrowthRate)){
            BigDecimal value = new BigDecimal(profitCompareMonthGrowthRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return profitCompareMonthGrowthRate;
        }
    }

    public String getProfitSameMonthGrowthRate() {
        if(StringUtils.isNotEmpty(profitSameMonthGrowthRate)){
            BigDecimal value = new BigDecimal(profitSameMonthGrowthRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return profitSameMonthGrowthRate;
        }
    }

    public String getLossRate() {
        if(StringUtils.isNotEmpty(lossRate)){
            BigDecimal value = new BigDecimal(lossRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return lossRate;
        }
    }

    public String getDiscountRate() {
        if(StringUtils.isNotEmpty(discountRate)){
            BigDecimal value = new BigDecimal(discountRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return discountRate;
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
