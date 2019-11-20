package com.cs.mobile.api.model.mreport.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.cs.mobile.api.model.mreport.StoreDayTimeReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreDayTimeReportResp", description = "门店时段报表响应对象")
public class StoreDayTimeReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "未税销售金额汇总", required = true)
	private BigDecimal totalSaleValue;
	@ApiModelProperty(value = "客流汇总", required = true)
	private Long totalPfCount;
	@ApiModelProperty(value = "客单汇总", required = true)
	private BigDecimal totalPerPrice;
	@ApiModelProperty(value = "时段报表列表", required = true)
	private List<StoreDayTimeReport> storeDayTimeReportList;

}
