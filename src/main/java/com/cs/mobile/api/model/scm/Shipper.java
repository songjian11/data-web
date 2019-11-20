package com.cs.mobile.api.model.scm;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "Shipper", description = "发货人")
public class Shipper {
    //发货人编号
    private String shipperCode;
    //发货人名称
    private String shipperName;
}
