package com.cs.mobile.api.model.freshspecialreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class ClassModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //大类ID
    private String deptId;
    //中类ID
    private String classId;
    //中类名称
    private String className;
}
