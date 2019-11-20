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
import java.util.List;

@Data
@ApiModel(value = "freshSpecialReportResponse",description = "报表")
public class FreshSpecialReportResponse implements Serializable {
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
    /*@ApiModelProperty(value = "环比销售金额",required = true)
    private String monthSale;*/
    @ApiModelProperty(value = "销售金额可比率",required = true)
    private String compareSaleRate;
    /*@ApiModelProperty(value = "销售金额环比率",required = true)
    private String monthSaleRate;*/
    @ApiModelProperty(value = "毛利额",required = true)
    private String rate;
    @ApiModelProperty(value = "毛利额单位",required = true)
    private String rateUnit = "元";
    @ApiModelProperty(value = "可比毛利额",required = true)
    private String compareRate;
    @ApiModelProperty(value = "可比毛利额单位",required = true)
    private String compareRateUnit = "元";
    @ApiModelProperty(value = "同期可比毛利额",required = true)
    private String hisCompareRate;
    @ApiModelProperty(value = "同期可比毛利额单位",required = true)
    private String hisCompareRateUnit = "元";
    /*@ApiModelProperty(value = "环比毛利额",required = true)
    private String monthRate;*/
    @ApiModelProperty(value = "毛利率",required = true)
    private String profit;
    @ApiModelProperty(value = "可比毛利率",required = true)
    private String compareProfit;
    @ApiModelProperty(value = "同期可比毛利率",required = true)
    private String hisCompareProfit;
    /*@ApiModelProperty(value = "环比毛利率",required = true)
    private String monthProfit;*/
    @ApiModelProperty(value = "毛利率可比率(即，(当期可比毛利率同期可比毛利率)-1)",required = true)
    private String compareProfitRate;
    /*@ApiModelProperty(value = "毛利率环比率(即，(当期毛利率/环比毛利率)-1)",required = true)
    private String monthProfitRate;*/
    @ApiModelProperty(value = "字段名称(说明ID所属类)",required = true)
    private String fieldName;
    @ApiModelProperty(value = "用户权限等级(1-全司，2-省，3-区域，4-门店)",required = true)
    private String grade;
    @ApiModelProperty(value = "昨日订货金额",required = true)
    private String orderAmount;
    @ApiModelProperty(value = "昨日库存金额",required = true)
    private String stockAmount;
    @ApiModelProperty(value = "昨日订货金额单位",required = true)
    private String orderAmountUnit = "元";
    @ApiModelProperty(value = "昨日库存金额单位",required = true)
    private String stockAmountUnit = "元";
    @JsonIgnore
    private String saleIn;
    @JsonIgnore
    private String compareSaleIn;
    @JsonIgnore
    private String hisCompareSaleIn;
    /*@JsonIgnore
    private String monthSaleIn;*/
    @JsonIgnore
    private String compareSaleRateIn;
    /*@JsonIgnore
    private String monthSaleRateIn;*/
    @JsonIgnore
    private String rateIn;
    @JsonIgnore
    private String compareRateIn;
    @JsonIgnore
    private String hisCompareRateIn;
    /*@JsonIgnore
    private String monthRateIn;*/
    @JsonIgnore
    private String profitIn;
    @JsonIgnore
    private String compareProfitIn;
    @JsonIgnore
    private String hisCompareProfitIn;
    /*@JsonIgnore
    private String monthProfitIn;*/
    @JsonIgnore
    private String compareProfitRateIn;
    /*@JsonIgnore
    private String monthProfitRateIn;*/
    @JsonIgnore
    private String orderAmountIn;

    @ApiModelProperty(value = "子类列",required = true)
    private List<SubFreshSpecialReportResponse> list;

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

    public String getRate() {
        String value = rate;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                rateUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                rateUnit = "亿";
            }
        }
        return value;
    }

    public String getCompareRate() {
        String value = compareRate;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                compareRateUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                compareRateUnit = "亿";
            }
        }
        return value;
    }

    public String getHisCompareRate() {
        String value = hisCompareRate;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                hisCompareRateUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                hisCompareRateUnit = "亿";
            }
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

    public String getOrderAmount() {
        String value = orderAmount;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                orderAmountUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                orderAmountUnit = "亿";
            }
        }
        return value;
    }

    public String getStockAmount() {
        String value = stockAmount;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                stockAmountUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                stockAmountUnit = "亿";
            }
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
        if(null != list && list.size() > 0){
            for(SubFreshSpecialReportResponse sub : list){
                sub.setTaxData();
            }
        }
    }
}
