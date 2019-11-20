package com.cs.mobile.api.model.freshreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
@ApiModel(value = "MemberListResponse", description = "线上和线下会员明细")
public class MemberListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "线上", required = true)
    private List<MemberDetailResponse> online;
    @ApiModelProperty(value = "线下", required = true)
    private List<MemberDetailResponse> offline;
}
