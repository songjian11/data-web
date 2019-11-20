package com.cs.mobile.api.model.partner.assess;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @author wells
 * @time 2018年12月19日
 */
@Data
@ApiModel(value = "AssessResult", description = "考核表对象")
public class AssessResult implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = true)
	private String resultYm;
	@ApiModelProperty(value = "门店编码", required = true)
	private String storeId;
	@ApiModelProperty(value = "小店编码", required = true)
	private String comId;
	@ApiModelProperty(value = "科目", required = true)
	private String subject;
	@ApiModelProperty(value = "科目值", required = true)
	private BigDecimal subValues;

	public String getSerialNo() {
		return this.storeId + "_" + this.comId;
	}
}
