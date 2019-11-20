package com.cs.mobile.api.model.mreport;

import java.io.Serializable;
import java.util.List;

import com.cs.mobile.api.model.mreport.AreaGroupReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AreaGroupReportDto", description = "大区报表对象")
public class AreaGroupReportDto implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "大战区", required = true)
	private String areaGroupName;
	@ApiModelProperty(value = "明细列表", required = true)
	private List<AreaGroupReport> list;
	@ApiModelProperty(value = "总计", required = true)
	private AreaGroupReport total;
}
