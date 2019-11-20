package com.cs.mobile.api.model.freshreport.response;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "HblTurnoverDayResponse", description = "标准周准天")
public class HblTurnoverDayResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //大类ID
    @ApiModelProperty(value = "大类ID", required = true)
    private String deptId;
    //大类名称
    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;
    //周转天数
    @ApiModelProperty(value = "标准周转天数", required = true)
    private String hblTurnoverDays;
    //周转天数
    @ApiModelProperty(value = "实际周转天数", required = true)
    private String turnoverDays;

    public String getTurnoverDays() {
        BigDecimal value = StringUtils.isEmpty(turnoverDays) ? BigDecimal.ZERO : new BigDecimal(turnoverDays).divide(BigDecimal.ONE,0, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }
}
