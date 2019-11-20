package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cs.mobile.common.utils.OperationUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发货行返回信息
 * 
 * @author wells.wong
 * @date 2019年7月25日
 *
 */
@Data
@ApiModel(value = "PoAsnDetailResp", description = "发货行返回信息")
public class PoAsnDetailResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "商品名称", required = true)
	private String itemDesc;
	@ApiModelProperty(value = "商品单位", required = true)
	private String uomDesc;
	@ApiModelProperty(value = "到仓单价", required = true)
	private BigDecimal sendUnitPrice;
	@ApiModelProperty(value = "数量", required = true)
	private BigDecimal poAsnQty;
	@ApiModelProperty(value = "总费用", required = true)
	private BigDecimal totalFee;
	@ApiModelProperty(value = "重量单价", required = true)
	private BigDecimal unitPrice;
	@ApiModelProperty(value = "运费", required = true)
	private BigDecimal freight;
	@ApiModelProperty(value = "总数量", required = true)
	private BigDecimal totalQty;
	@ApiModelProperty(value = "标准件", required = true)
	private BigDecimal standardOfPackage;
	@ApiModelProperty(value = "件数", required = true)
	private BigDecimal numberOfPackage;

	public BigDecimal getSendUnitPrice() {
		if ("KG".equals(uomDesc.toUpperCase())) {// kg商品到仓单价 = 重量单价 + 运费平摊
			this.sendUnitPrice = unitPrice.add(OperationUtil.divideHandler(this.freight, this.totalQty));
		} else {// 非kg商品到仓单价 = （重量单价 + 运费平摊） * 标准件
			this.sendUnitPrice = unitPrice.add(OperationUtil.divideHandler(this.freight, this.totalQty))
					.multiply(this.standardOfPackage);
		}
		return this.sendUnitPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getNumberOfPackage() {
		return OperationUtil.divideHandler(this.poAsnQty, this.standardOfPackage);
	}

	public BigDecimal getTotalFee() {
		if ("KG".equals(uomDesc.toUpperCase())) {// kg商品金额 = 到仓单价 * 重量
			return this.getSendUnitPrice().multiply(this.poAsnQty).setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {// 非kg商品金额 = 到仓单价 * 件数
			return this.getSendUnitPrice().multiply(this.getNumberOfPackage()).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
	}

	public static void main(String[] args) {
		System.out
				.println(new BigDecimal(14).add(OperationUtil.divideHandler(new BigDecimal(100), new BigDecimal(12000)))
						.multiply(new BigDecimal(0.5)).setScale(2, BigDecimal.ROUND_HALF_UP));
	}
}
