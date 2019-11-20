package com.cs.mobile.api.model.goods.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GoodsSaleReportResp", description = "商品销售报表")
public class GoodsSaleReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "总销售数量", required = true)
	private BigDecimal totalSaleQty;
	@ApiModelProperty(value = "总销售金额", required = true)
	private BigDecimal totalSaleValue;
	@ApiModelProperty(value = "总销售单位", required = true)
	private String totalSaleValueUnit;
	@ApiModelProperty(value = "总前台毛利率", required = true)
	private String totalGpp;
	@ApiModelProperty(value = "销售时间轴数据", required = true)
	private List<TimeLineReportResp> saleTimeLine;
	@ApiModelProperty(value = "毛利率时间轴数据", required = true)
	private List<TimeLineReportResp> gpTimeLine;
}
