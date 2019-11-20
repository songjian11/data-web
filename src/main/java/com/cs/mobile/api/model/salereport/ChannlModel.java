package com.cs.mobile.api.model.salereport;

import lombok.Data;

import java.io.Serializable;
@Data
public class ChannlModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String channelParentName;

    private String channel;

    private String channelName;

    private String type;
    //销售
    private String totalSale;
    //含税销售
    private String totalSaleIn;
}
