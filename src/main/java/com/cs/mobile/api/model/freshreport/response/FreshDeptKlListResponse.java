package com.cs.mobile.api.model.freshreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "FreshDeptKlListResponse", description = "生鲜大类客流列表")
public class FreshDeptKlListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客流行", required = true)
    private List<FreshDeptKlResponse> kl;

    @ApiModelProperty(value = "渗透率行", required = true)
    private List<FreshDeptPermeabilityResponse> permeability;

    @ApiModelProperty(value = "会员渗透率行", required = true)
    private List<FreshDeptMemberPermeabilityResponse> memberPermeability;
}
