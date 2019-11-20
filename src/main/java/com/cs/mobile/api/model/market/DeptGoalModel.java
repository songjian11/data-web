package com.cs.mobile.api.model.market;

import lombok.Data;

import java.io.Serializable;

/**
 * 大类目标值
 */
@Data
public class DeptGoalModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //大类ID
    private String deptId;
    //销售
    private String sale;
    //毛利额
    private String rate;
}
