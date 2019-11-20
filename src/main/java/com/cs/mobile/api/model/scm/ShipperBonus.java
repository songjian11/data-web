package com.cs.mobile.api.model.scm;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "ShipperBonus", description = "发货员奖金信息")
public class ShipperBonus {
    //发货人编号
    private String shipperCode;
    //发货人名称
    private String shipperName;
    //车型
    private String carType;
    //车型对应的发货次数
    private String carNum;
}
