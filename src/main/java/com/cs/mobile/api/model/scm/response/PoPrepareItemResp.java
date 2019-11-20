package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发货行信息
 * 
 * @author wells.wong
 * @date 2019年7月25日
 *
 */
@Data
@ApiModel(value = "PoPrepareItemResp", description = "发货行信息")
public class PoPrepareItemResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "商品名称", required = true)
	private String itemDesc;
	@ApiModelProperty(value = "重量单价（元/KG）", required = true)
	private BigDecimal unitPrice;
	@ApiModelProperty(value = "件数单价（元/件）", required = true)
	private BigDecimal perPrice;
	@ApiModelProperty(value = "回货单位", required = true)
	private String uomDesc;
	@ApiModelProperty(value = "回货数量【作为默认值，发货人可以修改】", required = true)
	private BigDecimal poQty;
	@ApiModelProperty(value = "标准件", required = true)
	private BigDecimal standardOfPackage;
	@ApiModelProperty(value = "件数【作为默认值，根据回货数量的修改而变动 公式：件数=回货数量/标准件 保留2位小数】", required = true)
	private BigDecimal numberOfPackage;
	@ApiModelProperty(value = "备注", required = false)
	private String remark;

	public BigDecimal getNumberOfPackage() {
		if (null != standardOfPackage) {
			this.numberOfPackage = poQty.divide(standardOfPackage, 2, BigDecimal.ROUND_HALF_UP);
		}
		return this.numberOfPackage;
	}

}
