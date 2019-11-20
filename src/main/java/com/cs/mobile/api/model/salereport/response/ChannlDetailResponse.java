package com.cs.mobile.api.model.salereport.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;

@Data
@ApiModel(value = "ChannlDetailResponse", description = "渠道构成明细")
public class ChannlDetailResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "渠道父类名称", required = true)
    private String channelParentName;
    @ApiModelProperty(value = "渠道", required = true)
    private String channel;
    @ApiModelProperty(value = "渠道名称", required = true)
    private String channelName;
    //销售
    @ApiModelProperty(value = "销售额", required = true)
    private String totalSale;
    @ApiModelProperty(value = "单位", required = true)
    private String unit = "元";
    //含税销售
    @JsonIgnore
    private String totalSaleIn;

    public String getTotalSale() {
        BigDecimal value = new BigDecimal(totalSale);
        if(value.abs().compareTo(new BigDecimal("10000")) < 0){
            this.unit = "元";
            value = value.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP);
        }else if(value.abs().compareTo(new BigDecimal("10000")) >= 0){
            this.unit = "万";
            value = value.divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP);
        }
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
