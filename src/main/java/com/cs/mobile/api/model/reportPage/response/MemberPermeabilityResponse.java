package com.cs.mobile.api.model.reportPage.response;

import com.cs.mobile.api.model.freshreport.response.MemberDetailResponse;
import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "MemberPermeabilityResponse", description = "会员渗透率")
public class MemberPermeabilityResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "线下会员数量", required = true)
    private String offline = "0";

    @ApiModelProperty(value = "线上会员数量", required = true)
    private String online = "0";

    @ApiModelProperty(value = "会员数量", required = true)
    private String member = "0";

    @ApiModelProperty(value = "非会员数量", required = true)
    private String noMember = "0";

    @ApiModelProperty(value = "线上", required = true)
    private List<MemberDetailResponse> onlineList;

    @ApiModelProperty(value = "线下", required = true)
    private List<MemberDetailResponse> offlineList;
}
