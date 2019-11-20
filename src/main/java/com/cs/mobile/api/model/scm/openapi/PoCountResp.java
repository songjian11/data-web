package com.cs.mobile.api.model.scm.openapi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "PoCountResp", description = "回货单数量")
public class PoCountResp implements Serializable {
    private static final long serialVersionUID = 7539705671297215083L;
    @ApiModelProperty(value = "回货单数量", required = true)
    Integer count;
}
