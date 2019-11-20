package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;
import java.util.Date;

import com.github.pagehelper.util.StringUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 单据列表
 * 
 * @author wells.wong
 * @date 2019年7月25日
 *
 */
@Data
@ApiModel(value = "OrderListResp", description = "单据列表")
public class OrderListResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "订单号", required = true)
	private String poSn;
	@ApiModelProperty(value = "发货单号【只有发货单才有】", required = true)
	private String poAsnSn;
	@ApiModelProperty(value = "单据状态", required = true)
	private String statusName;
	@ApiModelProperty(value = "订单创建时间", required = false)
	private Date poCreateTime;
	@ApiModelProperty(value = " 发货单创建时间【即发货时间】", required = false)
	private Date poAsnCreateTime;
	@ApiModelProperty(value = "订单状态", required = true)
	private String poStatus;
	@ApiModelProperty(value = "发货单状态", required = true)
	private String poAsnStatus;

	public String getStatusName() {
		if (StringUtil.isNotEmpty(this.poAsnSn)) {// 发货单
			switch (this.poAsnStatus) {
			case "02":
				this.statusName = "已发货";
				break;
			case "05":
				this.statusName = "已作废";
				break;
			default:
				break;
			}
		} else {// 采购单
			switch (this.poStatus) {
			case "01":
				this.statusName = "未提交";
				break;
			case "02":
				this.statusName = "待审核";
				break;
			case "03":
				this.statusName = "待发货";
				break;
			default:
				break;
			}
		}
		return this.statusName;
	}
}
