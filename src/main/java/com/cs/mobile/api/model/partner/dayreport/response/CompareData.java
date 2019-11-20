package com.cs.mobile.api.model.partner.dayreport.response;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 当日比较报表数据对象
 * 
 * @author wells
 * @date 2019年3月13日
 */
@Data
@ApiModel(value = "CompareData", description = "当日比较数据对象")
public class CompareData implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "维度（如：时间值、大类编码）", required = true)
	private String fieldCode;
	@ApiModelProperty(value = "维度名称（如：时间值、大类名）", required = true)
	private String fieldName;
	@ApiModelProperty(value = "本期值", required = true)
	private BigDecimal curValue;
	@ApiModelProperty(value = "对比值", required = true)
	private BigDecimal historyValue;
	@ApiModelProperty(value = "值单位", required = true)
	private String valueUnit;
	@ApiModelProperty(value = "比率值（如：50%时此值为50）", required = true)
	private BigDecimal riseRatio;
	@ApiModelProperty(value = "比率单位（%）", required = true)
	private String ratioUnit;

	public CompareData(String fieldCode, String fieldName, BigDecimal curValue, BigDecimal historyValue,
			String valueUnit, BigDecimal riseRatio, String ratioUnit) {
		super();
		this.fieldCode = fieldCode;
		this.fieldName = fieldName;
		this.curValue = curValue;
		this.historyValue = historyValue;
		this.valueUnit = valueUnit;
		this.riseRatio = riseRatio;
		this.ratioUnit = ratioUnit;
	}
}
