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
@ApiModel(value = "NotFreshResponse", description = "非生鲜")
public class NotFreshResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //销售额s
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSales = "0";
    //毛利率
    @ApiModelProperty(value = "毛利率", required = true)
    private String totalProfit = "0";

    @ApiModelProperty(value = "毛利额", required = true)
    private String totalProfitPrice = "0";

    //销售额达成率
    @ApiModelProperty(value = "销售额达成率", required = true)
    private String salesAchievingRate = "0";
    //客单价
    @ApiModelProperty(value = "客单价", required = true)
    private String customerSingerPrice = "0";
    //来客数
    @ApiModelProperty(value = "来客数", required = true)
    private String customerNum = "0";
    //扫描毛利率
    @ApiModelProperty(value = "损耗", required = true)
    private String lossPrice = "0";
    //库存金额
    @ApiModelProperty(value = "损耗率", required = true)
    private String lossRate = "0";
    @ApiModelProperty(value = "是否有非生鲜权限(0-否，1-是)", required = true)
    private String haveNotFresh = "1";

    @JsonIgnore
    private String totalSalesIn = "0";
    //毛利率
    @JsonIgnore
    private String totalProfitIn = "0";

    @JsonIgnore
    private String totalProfitPriceIn = "0";

    //销售额达成率
    @JsonIgnore
    private String salesAchievingRateIn = "0";
    //客单价
    @JsonIgnore
    private String customerSingerPriceIn = "0";
    //扫描毛利率
    @JsonIgnore
    private String lossPriceIn = "0";
    //库存金额
    @JsonIgnore
    private String lossRateIn = "0";


    public String getTotalSales() {
        BigDecimal value = StringUtils.isEmpty(totalSales) ? BigDecimal.ZERO : new BigDecimal(totalSales).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalProfit() {
        BigDecimal value = StringUtils.isEmpty(totalProfit) ? BigDecimal.ZERO : new BigDecimal(totalProfit).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalProfitPrice() {
        BigDecimal value = StringUtils.isEmpty(totalProfitPrice) ? BigDecimal.ZERO : new BigDecimal(totalProfitPrice).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getSalesAchievingRate() {
        BigDecimal value = StringUtils.isEmpty(salesAchievingRate) ? BigDecimal.ZERO : new BigDecimal(salesAchievingRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getCustomerSingerPrice() {
        BigDecimal value = StringUtils.isEmpty(customerSingerPrice) ? BigDecimal.ZERO : new BigDecimal(customerSingerPrice).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getCustomerNum() {
        return customerNum;
    }

    public String getLossPrice() {
        BigDecimal value = StringUtils.isEmpty(lossPrice) ? BigDecimal.ZERO : new BigDecimal(lossPrice).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getLossRate() {
        BigDecimal value = StringUtils.isEmpty(lossRate) ? BigDecimal.ZERO : new BigDecimal(lossRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
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
