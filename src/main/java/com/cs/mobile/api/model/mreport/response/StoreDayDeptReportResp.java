package com.cs.mobile.api.model.mreport.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.cs.mobile.api.model.mreport.StoreDayDeptReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreDayDeptReportResp", description = "门店大类报表响应对象")
public class StoreDayDeptReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "未税销售金额汇总", required = true)
	private BigDecimal totalSaleValue;
	@ApiModelProperty(value = "大类报表列表", required = true)
	private List<StoreDayDeptReport> storeDayDeptReportList;
}
