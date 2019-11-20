package com.cs.mobile.api.model.salereport;

import lombok.Data;

import java.io.Serializable;
@Data
public class ChannelTableModel implements Serializable{
    private static final long serialVersionUID = 1L;
    //父渠道类型编码
    private String channelParent;
    //父渠道类型名称
    private String channelParentDesc;
    //渠道类型编码
    private String channel;
    //渠道类型名称
    private String channelDesc;
}
