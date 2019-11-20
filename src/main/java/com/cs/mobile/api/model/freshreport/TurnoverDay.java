package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class TurnoverDay implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sale = "0";

    private String cost = "0";
}
