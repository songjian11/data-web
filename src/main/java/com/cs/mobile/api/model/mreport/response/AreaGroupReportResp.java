package com.cs.mobile.api.model.mreport.response;

import java.io.Serializable;
import java.util.List;

import com.cs.mobile.api.model.mreport.AreaGroupReport;
import com.cs.mobile.api.model.mreport.AreaGroupReportDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AreaGroupReportResp", description = "大区报表返回对象")
public class AreaGroupReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "明细列表", required = true)
	private List<AreaGroupReportDto> list;
	@ApiModelProperty(value = "总计数据", required = true)
	private AreaGroupReport total;
	@ApiModelProperty(value = "类别取值", required = true)
	private String deptScope = "32、35、36、37大类";
	@ApiModelProperty(value = "时间取值", required = true)
	private String timeScope = "星期对比星期";
}
