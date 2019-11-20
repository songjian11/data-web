package com.cs.mobile.api.model.freshreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

@Data
@ApiModel(value = "SalesListResponse", description = "销售列表")
public class SalesListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "当天数据", required = true)
    private SalesResponse current;
    @ApiModelProperty(value = "昨日数据", required = true)
    private SalesResponse yesterday;
    @ApiModelProperty(value = "日比", required = true)
    private SalesResponse yesterdayRate;
    @ApiModelProperty(value = "周比", required = true)
    private SalesResponse weekRate;
    @ApiModelProperty(value = "环比", required = true)
    private SalesResponse monthRate;

    public void setTaxData() throws IllegalAccessException {
        this.current.setTaxData();
        this.yesterday.setTaxData();
        this.yesterdayRate.setTaxData();
        this.weekRate.setTaxData();
        this.monthRate.setTaxData();
    }
}
