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
@ApiModel(value = "FreshSpecialReportRankResponse",description = "生鲜排行榜明细")
public class FreshSpecialReportRankResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "名称",required = true)
    private String name;
    @ApiModelProperty(value = "销售金额",required = true)
    private String sale;
    @ApiModelProperty(value = "毛利率",required = true)
    private String profit;
    @ApiModelProperty(value = "渗透率",required = true)
    private String permeability;
    @ApiModelProperty(value = "期末库存数量",required = true)
    private String stock;
    @ApiModelProperty(value = "期末库存金额",required = true)
    private String stockAmount;
    @ApiModelProperty(value = "期末库存金额单位",required = true)
    private String stockAmountUnit = "元";
    @ApiModelProperty(value = "销售金额可比率",required = true)
    private String compareSaleRate;
    @ApiModelProperty(value = "昨日销售金额",required = true)
    private String yesterdaySale;
    @ApiModelProperty(value = "昨日销售金额单位",required = true)
    private String yesterdaySaleUnit = "元";
    @ApiModelProperty(value = "月至今销售金额",required = true)
    private String monthSale;
    @ApiModelProperty(value = "月至今销售金额单位",required = true)
    private String monthSaleUnit = "元";
    @ApiModelProperty(value = "订货量",required = true)
    private String orderNum;
    @ApiModelProperty(value = "订货量金额",required = true)
    private String orderAmount;
    @ApiModelProperty(value = "订货量金额单位",required = true)
    private String orderAmountUnit = "元";
    @JsonIgnore
    private String saleIn;
    @JsonIgnore
    private String profitIn;
    @JsonIgnore
    private String compareSaleRateIn;
    @JsonIgnore
    private String yesterdaySaleIn;
    @JsonIgnore
    private String monthSaleIn;
    @JsonIgnore
    private String orderAmountIn;

    public String getOrderAmount() {
        String value = orderAmount;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            /*if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "亿";
            }*/
            value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
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

    public String getPermeability() {
        String value = permeability;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getStockAmount() {
        String value = stockAmount;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            /*if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "亿";
            }*/
            value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
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

    public String getYesterdaySale() {
        String value = yesterdaySale;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            /*if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "亿";
            }*/
            value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getMonthSale() {
        String value = monthSale;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            /*if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                saleUnit = "亿";
            }*/
            value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
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
