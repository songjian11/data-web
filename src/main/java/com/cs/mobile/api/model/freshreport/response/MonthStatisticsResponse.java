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
@ApiModel(value = "MonthStatisticsResponse", description = "生鲜专题月统计")
public class MonthStatisticsResponse  implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "月累计销售", required = true)
    private String totalSales;
    @ApiModelProperty(value = "月累计毛利额", required = true)
    private String totalRate;
    @ApiModelProperty(value = "月累计毛利率", required = true)
    private String totalProfitRate;
    @ApiModelProperty(value = "渗透率", required = true)
    private String permeability;
    @ApiModelProperty(value = "渗透率", required = true)
    private String permeabilityRate;
    @ApiModelProperty(value = "损耗率", required = true)
    private String lossRate;
    @ApiModelProperty(value = "毛利贡献度", required = true)
    private String  profitcontribution;
    @ApiModelProperty(value = "折价率", required = true)
    private String discountRate;
    @ApiModelProperty(value = "毛利额达成率", required = true)
    private String rateAchievingRate = "100";
    @JsonIgnore
    private String totalSalesIn;
    @JsonIgnore
    private String totalRateIn;
    @JsonIgnore
    private String totalProfitRateIn;
    @JsonIgnore
    private String permeabilityIn;
    @JsonIgnore
    private String lossRateIn;
    @JsonIgnore
    private String  profitcontributionIn;
    @JsonIgnore
    private String discountRateIn;

    public String getPermeabilityRate() {
        BigDecimal value = StringUtils.isEmpty(permeabilityRate) ? BigDecimal.ZERO : new BigDecimal(permeabilityRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalSales() {
        BigDecimal value = StringUtils.isEmpty(totalSales) ? BigDecimal.ZERO : new BigDecimal(totalSales).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalRate() {
        BigDecimal value = StringUtils.isEmpty(totalRate) ? BigDecimal.ZERO : new BigDecimal(totalRate).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalProfitRate() {
        BigDecimal value = StringUtils.isEmpty(totalProfitRate) ? BigDecimal.ZERO : new BigDecimal(totalProfitRate).multiply(new BigDecimal(100)).divide(BigDecimal.ONE,2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getPermeability() {
        BigDecimal value = StringUtils.isEmpty(permeability) ? BigDecimal.ZERO : new BigDecimal(permeability).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getLossRate() {
        BigDecimal value = StringUtils.isEmpty(lossRate) ? BigDecimal.ZERO : new BigDecimal(lossRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getProfitcontribution() {
        BigDecimal value = StringUtils.isEmpty(profitcontribution) ? BigDecimal.ZERO : new BigDecimal(profitcontribution).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getDiscountRate() {
        BigDecimal value = StringUtils.isEmpty(discountRate) ? BigDecimal.ZERO : new BigDecimal(discountRate).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
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
