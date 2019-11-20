package com.cs.mobile.api.model.freshreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class HblTurnoverDay implements Serializable {
    private static final long serialVersionUID = 1L;
    //大类ID
    private String deptId;
    //周转天数
    private String hblTurnoverDays;
    //实际周转天数
    private String turnoverDays = "0";
}
