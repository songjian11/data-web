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
@ApiModel(value = "freshSpecialReportTrendResponse", description = "生鲜趋势明细")
public class FreshSpecialReportTrendResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @ApiModelProperty(value = "毛利额", required = true)
    private String rate;
    @ApiModelProperty(value = "毛利额单位", required = true)
    private String rateUnit = "元";
    @ApiModelProperty(value = "渗透率", required = true)
    private String permeability;
    @JsonIgnore
    private String rateIn;

    public String getRate() {
        String value = rate;
        if(StringUtils.isNotEmpty(value)){
            BigDecimal v = new BigDecimal(value);
            /*if(v.abs().compareTo(new BigDecimal("10000")) < 0){
                value = v.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString();
            }else if(v.abs().compareTo(new BigDecimal("100000000")) < 0){
                value = v.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                rateUnit = "万";
            }else{
                value = v.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP).toString();
                rateUnit = "亿";
            }*/
            value = v.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP).toString();
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
