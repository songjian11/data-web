package com.cs.mobile.api.model.market.response;

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
@ApiModel(value = "TrendReportResponse", description = "趋势报表")
public class TrendReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //名称
    @ApiModelProperty(value = "名称",required = true)
    private String name;

    //销售
    @ApiModelProperty(value = "销售",required = true)
    private String sale;

    //毛利率
    @ApiModelProperty(value = "毛利率",required = true)
    private String rate;

    //可比销售
    @ApiModelProperty(value = "可比销售",required = true)
    private String compareSale;

    //可比毛利率
    @ApiModelProperty(value = "可比毛利率",required = true)
    private String compareRate;

    @ApiModelProperty(value = "单位",required = true)
    private String unit = "万";

    //含税销售
    @JsonIgnore
    private String saleIn;

    //含税毛利率
    @JsonIgnore
    private String rateIn;

    //含税可比销售
    @JsonIgnore
    private String compareSaleIn;

    //含税可比毛利率
    @JsonIgnore
    private String compareRateIn;

    public String getSale() {
        String value = sale;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getRate() {
        String value = rate;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getCompareSale() {
        String value = compareSale;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getCompareRate() {
        String value = compareRate;
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
