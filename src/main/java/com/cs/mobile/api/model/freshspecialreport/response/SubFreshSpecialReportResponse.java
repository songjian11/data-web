package com.cs.mobile.api.model.freshspecialreport.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;

@Data
@ApiModel(value = "subFreshSpecialReportResponse",description = "子类详情")
public class SubFreshSpecialReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ID",required = true)
    private String id;
    @ApiModelProperty(value = "名称",required = true)
    private String name;
    @ApiModelProperty(value = "渗透率",required = true)
    private String permeability;
    @ApiModelProperty(value = "销售金额",required = true)
    private String sale;
    @ApiModelProperty(value = "销售金额单位",required = true)
    private String saleUnit = "元";
    @ApiModelProperty(value = "可比销售金额",required = true)
    private String compareSale;
    @ApiModelProperty(value = "可比销售金额单位",required = true)
    private String compareSaleUnit = "元";
    @ApiModelProperty(value = "同期可比销售金额",required = true)
    private String hisCompareSale;
    @ApiModelProperty(value = "同期可比销售金额单位",required = true)
    private String hisCompareSaleUnit = "元";
    @ApiModelProperty(value = "销售金额可比率",required = true)
    private String compareSaleRate;
    @ApiModelProperty(value = "毛利率",required = true)
    private String profit;
    @ApiModelProperty(value = "可比毛利率",required = true)
    private String compareProfit;
    @ApiModelProperty(value = "同期可比毛利率",required = true)
    private String hisCompareProfit;
    @ApiModelProperty(value = "毛利率增长率(即，(当期可比毛利率/同期可比毛利率)-1)",required = true)
    private String compareProfitRate;
    @ApiModelProperty(value = "字段名称(说明ID所属类)",required = true)
    private String fieldName;
    @ApiModelProperty(value = "是否有销售(0-否，1-是)",required = true)
    private String haveSale = "0";
    @JsonIgnore
    private String saleIn;
    @JsonIgnore
    private String compareSaleIn;
    @JsonIgnore
    private String hisCompareSaleIn;
    @JsonIgnore
    private String compareSaleRateIn;
    @JsonIgnore
    private String profitIn;
    @JsonIgnore
    private String compareProfitIn;
    @JsonIgnore
    private String hisCompareProfitIn;
    @JsonIgnore
    private String compareProfitRateIn;

    public String getPermeability() {
        String value = permeability;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getSale() {
        String value = sale;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "亿";
            }
        }
        return value;
    }

    public String getCompareSale() {
        String value = compareSale;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                compareSaleUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                compareSaleUnit = "亿";
            }
        }
        return value;
    }

    public String getHisCompareSale() {
        String value = hisCompareSale;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                hisCompareSaleUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                hisCompareSaleUnit = "亿";
            }
        }
        return value;
    }

    public String getCompareSaleRate() {
        String value = compareSaleRate;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getProfit() {
        String value = profit;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getCompareProfit() {
        String value = compareProfit;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getHisCompareProfit() {
        String value = hisCompareProfit;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getCompareProfitRate() {
        String value = compareProfitRate;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
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
