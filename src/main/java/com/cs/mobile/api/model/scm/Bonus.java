package com.cs.mobile.api.model.scm;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "Bonus", description = "车型和奖金关系表")
public class Bonus {
    //车型
    private String carType;
    //奖金
    private String bonus;
}
