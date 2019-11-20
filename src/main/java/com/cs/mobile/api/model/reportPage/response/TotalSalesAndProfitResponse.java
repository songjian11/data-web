package com.cs.mobile.api.model.reportPage.response;

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
@ApiModel(value = "TotalSalesAndProfitResponse", description = "总销售额和总毛利率信")
public class TotalSalesAndProfitResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //销售额
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSales = "0";
    //毛利率
    @ApiModelProperty(value = "毛利率", required = true)
    private String totalProfit = "0";
    //销售额同比
    @ApiModelProperty(value = "销售额同比", required = true)
    private String salesSameRate ;
    //毛利率同比
    @ApiModelProperty(value = "毛利率同比", required = true)
    private String profitSameRate ;
    //销售额可比
    @ApiModelProperty(value = "销售额可比", required = true)
    private String salesCompareRate ;
    //毛利率可比
    @ApiModelProperty(value = "毛利率可比", required = true)
    private String profitCompareRate ;
    //客单价
    @ApiModelProperty(value = "客单价", required = true)
    private String customerSingerPrice = "0";
    //来客数
    @ApiModelProperty(value = "来客数", required = true)
    private String customerNum = "0";
    //扫描毛利率
    @ApiModelProperty(value = "扫描毛利率", required = true)
    private String scanningProfitRate = "0";
    //库存金额
    @ApiModelProperty(value = "库存金额", required = true)
    private String stockPrice = "0";
    //库存数量
    @ApiModelProperty(value = "库存数量", required = true)
    private String stockNum = "0";

    //库存数量单位
    @ApiModelProperty(value = "库存数量单位", required = true)
    private String stockNumUnit = "个";



    @JsonIgnore
    private String totalSalesIn = "0";
    //毛利率
    @JsonIgnore
    private String totalProfitIn = "0";
    //销售额同比
    @JsonIgnore
    private String salesSameRateIn ;
    //毛利率同比
    @JsonIgnore
    private String profitSameRateIn ;
    //销售额可比
    @JsonIgnore
    private String salesCompareRateIn ;
    //毛利率可比
    @JsonIgnore
    private String profitCompareRateIn ;
    //客单价
    @JsonIgnore
    private String customerSingerPriceIn = "0";
    //扫描毛利率
    @JsonIgnore
    private String scanningProfitRateIn = "0";




    public String getTotalSales() {
        BigDecimal value = StringUtils.isEmpty(totalSales) ? BigDecimal.ZERO : new BigDecimal(totalSales).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalProfit() {
        BigDecimal value = StringUtils.isEmpty(totalProfit) ? BigDecimal.ZERO : new BigDecimal(totalProfit).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getSalesSameRate() {
        if(StringUtils.isNotEmpty(salesSameRate)){
            BigDecimal value = new BigDecimal(salesSameRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return salesSameRate;
        }
    }

    public String getProfitSameRate() {
        if(StringUtils.isNotEmpty(profitSameRate)){
            BigDecimal value = new BigDecimal(profitSameRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return profitSameRate;
        }
    }

    public String getSalesCompareRate() {
        if(StringUtils.isNotEmpty(salesCompareRate)){
            BigDecimal value = new BigDecimal(salesCompareRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return salesCompareRate;
        }
    }

    public String getProfitCompareRate() {
        if(StringUtils.isNotEmpty(profitCompareRate)){
            BigDecimal value = new BigDecimal(profitCompareRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return profitCompareRate;
        }
    }

    public String getCustomerSingerPrice() {
        BigDecimal value = StringUtils.isEmpty(customerSingerPrice) ? BigDecimal.ZERO : new BigDecimal(customerSingerPrice).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getScanningProfitRate() {
        BigDecimal value = StringUtils.isEmpty(scanningProfitRate) ? BigDecimal.ZERO : new BigDecimal(scanningProfitRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getStockPrice() {
        BigDecimal value = StringUtils.isEmpty(stockPrice) ? BigDecimal.ZERO : new BigDecimal(stockPrice).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getStockNum() {
        String value = "0个";
        String lenStr = "";
        if(stockNum.contains(".")){
            lenStr = stockNum.substring(0, stockNum.lastIndexOf("."));
        }else{
            lenStr = stockNum;
        }
        if(lenStr.length() < 5){
            value = StringUtils.isEmpty(stockNum) ? "0" : new BigDecimal(stockNum).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP).toString();
            this.stockNumUnit = "个";
        }else if(lenStr.length() >= 5  && lenStr.length() < 9){
            value = StringUtils.isEmpty(stockNum) ? "0" : new BigDecimal(stockNum).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString() ;
            this.stockNumUnit = "万";
        }else{
            value = StringUtils.isEmpty(stockNum) ? "0" : new BigDecimal(stockNum).divide(new BigDecimal(100000000),2, BigDecimal.ROUND_HALF_UP).toString();
            this.stockNumUnit = "亿";
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
