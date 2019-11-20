package com.cs.mobile.api.model.freshreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

@Data
@ApiModel(value = "FreshRankGradeResponse", description = "生鲜排名名次")
public class FreshRankGradeResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "判断是否显示排名(0-否，1-全司，2-省级，3-区域级，4-门店级)", required = true)
    private Integer type = 0;
    @ApiModelProperty(value = "名次", required = true)
    private Integer rankNum;
    @ApiModelProperty(value = "数量", required = true)
    private Integer count;
    @ApiModelProperty(value = "名次", required = true)
    private Integer rankNumIn;
    @ApiModelProperty(value = "数量", required = true)
    private Integer countIn;

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
