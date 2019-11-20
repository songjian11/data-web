package com.cs.mobile.api.model.freshreport.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "MemberDetailResponse", description = "会员渗透率明细")
public class MemberDetailResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //@ApiModelProperty(value = "是否会员", required = true)
    @JsonIgnore
    private String vipMark;
    //渠道编码
    @ApiModelProperty(value = "渠道编码", required = true)
    private String channel;
    @ApiModelProperty(value = "渠道名称", required = true)
    private String channelName;
    @ApiModelProperty(value = "客流", required = true)
    private String count;
    @ApiModelProperty(value = "单位", required = true)
    private String unit = "人";

    public String getCount() {
        BigDecimal value = new BigDecimal(count);
        if(value.compareTo(new BigDecimal("10000")) < 0){
            unit = "人";
        }else if(value.compareTo(new BigDecimal("10000")) >= 0){
            unit = "万";
            value = value.divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP);
        }
        return value.toString();
    }
}
