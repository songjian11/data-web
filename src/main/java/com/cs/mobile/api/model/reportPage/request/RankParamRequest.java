package com.cs.mobile.api.model.reportPage.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "RankParamRequest", description = "排行榜列表")
public class RankParamRequest extends PageRequest implements Serializable {
    @ApiModelProperty(value = "类型(0-按照用户权限查询,1-门店,2-品类,3-区域,4-大类，查询列表的时候用)", required = false)
    private int type = 0;
    //权限等级(0-全司,1-省份,2-区域,3-门店)
    @JsonIgnore
    private int grade;
}
