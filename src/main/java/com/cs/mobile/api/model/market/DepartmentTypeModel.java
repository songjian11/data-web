package com.cs.mobile.api.model.market;

import lombok.Data;

import java.io.Serializable;

/**
 * 部门+品类+大类
 */
@Data
public class DepartmentTypeModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //大类ID
    private String deptId;
    //大类名称
    private String deptName;
    //品类名称
    private String categoryName;
    //部门ID
    private String purchaseDept;
    //部门名称
    private String purchaseDeptName;

}
