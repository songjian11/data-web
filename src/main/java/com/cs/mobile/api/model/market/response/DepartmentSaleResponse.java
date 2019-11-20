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
@ApiModel(value = "DepartmentSaleResponse", description = "市场报表")
public class DepartmentSaleResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //ID
    @ApiModelProperty(value = "ID",required = true)
    private String id;
    //名称
    @ApiModelProperty(value = "名称",required = true)
    private String name;
    //负责人名称
    @ApiModelProperty(value = "负责人名称",required = true)
    private String personName;
    //销售目标
    @ApiModelProperty(value = "销售目标",required = true)
    private String saleGoal;

    //销售达成率
    @ApiModelProperty(value = "销售达成率",required = true)
    private String saleGoalRate;
    //销售
    @ApiModelProperty(value = "销售",required = true)
    private String sale;
    //同期销售
    @ApiModelProperty(value = "同期销售",required = true)
    private String hisSale;
    //销售增长率
    @ApiModelProperty(value = "销售增长率",required = true)
    private String saleAddRate;
    //毛利额
    @ApiModelProperty(value = "毛利额",required = true)
    private String profit;
    //同期毛利额
    @ApiModelProperty(value = "同期毛利额",required = true)
    private String hisProfit;
    //毛利额增长率
    @ApiModelProperty(value = "毛利率增长率",required = true)
    private String profitAddRate;

    //可比销售达成率
    @ApiModelProperty(value = "可比销售达成率",required = true)
    private String compareSaleGoalRate;
    //可比销售
    @ApiModelProperty(value = "可比销售",required = true)
    private String compareSale;
    //可比同期销售
    @ApiModelProperty(value = "可比同期销售",required = true)
    private String compareHisSale;
    //可比销售增长率
    @ApiModelProperty(value = "可比销售增长率",required = true)
    private String compareSaleAddRate;
    //可比毛利额
    @ApiModelProperty(value = "可比毛利额",required = true)
    private String compareProfit;
    //可比同期毛利额
    @ApiModelProperty(value = "可比同期毛利额",required = true)
    private String compareHisProfit;
    //可比毛利额增长率
    @ApiModelProperty(value = "可比毛利率增长率",required = true)
    private String compareProfitAddRate;

    //渗透率
    @ApiModelProperty(value = "渗透率",required = true)
    private String permeability;

    @ApiModelProperty(value = "单位",required = true)
    private String unit = "万";

    //含税销售达成率
    private String saleGoalRateIn;
    //含税销售
    @JsonIgnore
    private String saleIn;
    //含税同期销售
    @JsonIgnore
    private String hisSaleIn;
    //含税销售增长率
    @JsonIgnore
    private String saleAddRateIn;
    //含税毛利额
    @JsonIgnore
    private String profitIn;
    //含税同期毛利额
    @JsonIgnore
    private String hisProfitIn;
    //含税毛利率增长率
    @JsonIgnore
    private String profitAddRateIn;

    //含税可比销售达成率
    private String compareSaleGoalRateIn;
    //含税可比销售
    @JsonIgnore
    private String compareSaleIn;
    //含税可比同期销售
    @JsonIgnore
    private String compareHisSaleIn;
    //含税可比销售增长率
    @JsonIgnore
    private String compareSaleAddRateIn;
    //含税可比毛利额
    @JsonIgnore
    private String compareProfitIn;
    //含税可比同期毛利额
    @JsonIgnore
    private String compareHisProfitIn;
    //含税可比毛利率增长率
    @JsonIgnore
    private String compareProfitAddRateIn;

    public String getSaleGoal() {
        String value = saleGoal;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getSaleGoalRate() {
        String value = saleGoalRate;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getSale() {
        String value = sale;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getHisSale() {
        String value = hisSale;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getSaleAddRate() {
        String value = saleAddRate;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getProfit() {
        String value = profit;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getHisProfit() {
        String value = hisProfit;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getProfitAddRate() {
        String value = profitAddRate;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getCompareSaleGoalRate() {
        String value = compareSaleGoalRate;
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

    public String getCompareHisSale() {
        String value = compareHisSale;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getCompareSaleAddRate() {
        String value = compareSaleAddRate;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getCompareProfit() {
        String value = compareProfit;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getCompareHisProfit() {
        String value = compareHisProfit;
        if(StringUtils.isNotEmpty(value)){
            value = new BigDecimal(value).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return value;
    }

    public String getCompareProfitAddRate() {
        String value = compareProfitAddRate;
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
