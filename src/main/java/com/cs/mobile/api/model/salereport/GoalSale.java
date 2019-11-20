package com.cs.mobile.api.model.salereport;

import lombok.Data;

import java.io.Serializable;
@Data
public class GoalSale implements Serializable {
    private static final long serialVersionUID = 1L;
    //目标销售
    private String sale = "0";
    //目标毛利额
    private String rate = "0";
}
