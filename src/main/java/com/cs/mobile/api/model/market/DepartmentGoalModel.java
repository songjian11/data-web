package com.cs.mobile.api.model.market;

import lombok.Data;

import java.io.Serializable;

/**
 * 部门目标值
 */
@Data
public class DepartmentGoalModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //部门ID
    private String purchaseDept;
    //部门名称
    private String purchaseDeptName;
    //销售
    private String sale;
    //毛利额
    private String rate;
}
