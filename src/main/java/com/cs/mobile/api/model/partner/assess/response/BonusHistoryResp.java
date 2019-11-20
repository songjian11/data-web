package com.cs.mobile.api.model.partner.assess.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 奖金历史记录
 * 
 * @author wells
 * @date 2019年3月30日
 */
@Data
@ApiModel(value = "BonusHistoryResp", description = "奖金历史记录")
public class BonusHistoryResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = true)
	private String ym;
	@ApiModelProperty(value = "门店编码", required = true)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = true)
	private String storeName;
	@ApiModelProperty(value = "小店编码", required = true)
	private String comId;
	@ApiModelProperty(value = "小店名称", required = true)
	private String comName;
	@ApiModelProperty(value = "标题", required = true)
	private String bonusTitle;
	@ApiModelProperty(value = "奖金", required = true)
	private BigDecimal bonus;
	@ApiModelProperty(value = "奖金人员明细", required = true)
	private List<PersonBonusItemResp> personBonusList;

	public String getBonusTitle() {
		return this.bonusTitle = this.ym.substring(0, 4) + "年" + this.ym.substring(5, 7) + "合伙人奖金明细";
	}

}
