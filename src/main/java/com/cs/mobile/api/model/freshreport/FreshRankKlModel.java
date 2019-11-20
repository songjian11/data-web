package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class FreshRankKlModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String storeId;
    //主键ID
    private String id;
    //客流
    private String kl;
}
