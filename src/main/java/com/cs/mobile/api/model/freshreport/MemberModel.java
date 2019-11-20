package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class MemberModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vipMark;

    private String channel;

    private String channelName;

    private String count;
}
