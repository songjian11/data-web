package com.cs.mobile.api.model.partner.assess.response;

import java.io.Serializable;

import com.cs.mobile.api.model.partner.progress.ShareDetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "BonusAssessReportResp", description = "奖金核算报表")
public class BonusAssessReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "机构编码", required = true)
	private String orgCode;
	@ApiModelProperty(value = "机构名称", required = true)
	private String orgName;
	@ApiModelProperty(value = "目标详情", required = true)
	private ShareDetail goalDetail;
	@ApiModelProperty(value = "考核详情", required = true)
	private ShareDetail assessDetail;
}
