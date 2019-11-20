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
@ApiModel(value = "ReportDetailResponse", description = "时线，日线，月线的销售额和毛利率信息")
public class ReportDetailResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "时间点", required = true)
    private String timePoint;
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSales = "0";
    @ApiModelProperty(value = "毛利率", required = true)
    private String totalprofit = "0";
    @ApiModelProperty(value = "具体时间（日线的时候使用，查看时间点属于哪年哪月哪天）", required = true)
    private String time;

    @JsonIgnore
    private String totalSalesIn = "0";
    @JsonIgnore
    private String totalprofitIn = "0";

    public String getTotalSales() {
        BigDecimal value = StringUtils.isEmpty(totalSales) ? BigDecimal.ZERO : new BigDecimal(totalSales).divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getTotalprofit() {
        BigDecimal value = StringUtils.isEmpty(totalprofit) ? BigDecimal.ZERO : new BigDecimal(totalprofit).multiply(new BigDecimal(100)).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
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
