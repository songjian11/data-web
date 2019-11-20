package com.cs.mobile.api.model.partner.progress;

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
@ApiModel(value = "AssessItemResult", description = "考核表明细对象")
public class AssessItemResult implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = false)
	private String resultYm;
	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "小店编码", required = false)
	private String comId;
	@ApiModelProperty(value = "销售", required = false)
	private BigDecimal sale;
	@ApiModelProperty(value = "毛利额", required = false)
	private BigDecimal frontGp;
	@ApiModelProperty(value = "后台", required = false)
	private BigDecimal afterGp;
	@ApiModelProperty(value = "招商收入", required = false)
	private BigDecimal attract;
	@ApiModelProperty(value = "DC成本", required = false)
	private BigDecimal dcCost;
	@ApiModelProperty(value = "其它费用", required = false)
	private BigDecimal otherCost;
	@ApiModelProperty(value = "折旧费用", required = false)
	private BigDecimal depreciationCost;
	@ApiModelProperty(value = "租赁费用", required = false)
	private BigDecimal leaseCost;
	@ApiModelProperty(value = "销售-水电费用", required = false)
	private BigDecimal hydropowerCost;
	@ApiModelProperty(value = "销售-人力成本", required = false)
	private BigDecimal manpowerCost;
	@ApiModelProperty(value = "库存资金占用成本", required = false)
	private BigDecimal stockCost;
	@ApiModelProperty(value = "分享金额", required = false)
	private BigDecimal shareval;
	@ApiModelProperty(value = "总费用", required = false)
	private BigDecimal totalCost;

	public BigDecimal getTotalCost() {
		return this.getOtherCost().add(this.getDepreciationCost()).add(this.getLeaseCost())
				.add(this.getHydropowerCost()).add(this.getManpowerCost()).add(this.getStockCost());
	}

	public BigDecimal getSale() {
		return null == sale ? BigDecimal.ZERO : sale.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getFrontGp() {
		return null == frontGp ? BigDecimal.ZERO : frontGp.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getAfterGp() {
		return null == afterGp ? BigDecimal.ZERO : afterGp.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getAttract() {
		return null == attract ? BigDecimal.ZERO : attract.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getDcCost() {
		return null == dcCost ? BigDecimal.ZERO : dcCost.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getOtherCost() {
		return null == otherCost ? BigDecimal.ZERO : otherCost.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getDepreciationCost() {
		return null == depreciationCost ? BigDecimal.ZERO : depreciationCost.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getLeaseCost() {
		return null == leaseCost ? BigDecimal.ZERO : leaseCost.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getHydropowerCost() {
		return null == hydropowerCost ? BigDecimal.ZERO : hydropowerCost.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getManpowerCost() {
		return null == manpowerCost ? BigDecimal.ZERO : manpowerCost.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getStockCost() {
		return null == stockCost ? BigDecimal.ZERO : stockCost.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getShareval() {
		return null == shareval ? BigDecimal.ZERO : shareval.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
