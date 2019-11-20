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
@ApiModel(value = "RankDetailResponse", description = "排行榜详情")
public class RankDetailResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "门店名称", required = true)
    private String storeName;
    @ApiModelProperty(value = "门店ID", required = true)
    private String storeId;
    @ApiModelProperty(value = "区域名称", required = true)
    private String areaName;
    @ApiModelProperty(value = "区域ID", required = true)
    private String areaId;
    @ApiModelProperty(value = "品类名称", required = true)
    private String deptName;
    @ApiModelProperty(value = "大类ID", required = true)
    private String deptId;
    @ApiModelProperty(value = "大类名称", required = true)
    private String majorDeptName;
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSales = "0";
    @ApiModelProperty(value = "毛利额", required = true)
    private String totalRate = "0";
    @ApiModelProperty(value = "销售额的同比", required = true)
    private String saleSameRate;
    @ApiModelProperty(value = "毛利额的同比", required = true)
    private String profitSameRate;
    //销售额的可比
    @ApiModelProperty(value = "销售额的可比", required = true)
    private String saleCompareRate;
    //毛利额的可比
    @ApiModelProperty(value = "毛利额的可比", required = true)
    private String profitCompareRate;
    @ApiModelProperty(value = "客流", required = true)
    private String customerNum = "0";

    @JsonIgnore
    private String totalSalesIn = "0";
    @JsonIgnore
    private String totalRateIn = "0";
    @JsonIgnore
    private String saleSameRateIn;
    @JsonIgnore
    private String profitSameRateIn;
    //销售额的可比
    @JsonIgnore
    private String saleCompareRateIn;
    //毛利额的可比
    @JsonIgnore
    private String profitCompareRateIn;

    public String getSaleCompareRate() {
        if(StringUtils.isNotEmpty(saleCompareRate)){
            BigDecimal value = new BigDecimal(saleCompareRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return saleCompareRate;
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

    public String getTotalSales() {
        BigDecimal value = StringUtils.isEmpty(totalSales) ? BigDecimal.ZERO : new BigDecimal(totalSales).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalRate() {
        BigDecimal value = StringUtils.isEmpty(totalRate) ? BigDecimal.ZERO : new BigDecimal(totalRate).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getSaleSameRate() {
        if(StringUtils.isNotEmpty(saleSameRate)){
            BigDecimal value = new BigDecimal(saleSameRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
            return value.toString();
        }else{
            return saleSameRate;
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
