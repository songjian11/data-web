package com.cs.mobile.api.model.freshreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "BaseRankResponse", description = "生鲜排行榜基本属性")
public class BaseRankResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "id", required = true)
    private String id;
    @ApiModelProperty(value = "id对应字段名称", required = true)
    private String type;
}
