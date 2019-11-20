package com.cs.mobile.api.model.freshspecialreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 生鲜总客流
 */
@Data
public class FreshAllKlModel implements Serializable{
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String kl;
}
