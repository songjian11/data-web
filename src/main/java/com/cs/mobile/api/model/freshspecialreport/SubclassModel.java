package com.cs.mobile.api.model.freshspecialreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class SubclassModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //大类ID
    private String deptId;
    //中类ID
    private String classId;
    //小类ID
    private String subclassId;
    //小类名称
    private String subclassName;
}
