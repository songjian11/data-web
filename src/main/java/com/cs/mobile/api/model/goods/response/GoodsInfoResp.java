package com.cs.mobile.api.model.goods.response;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品详情返回对象
 * 
 * @author wells
 * @date 2019年3月1日
 */
@Data
@ApiModel(value = "GoodsInfoResp", description = "商品详情返回对象")
public class GoodsInfoResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "商品名称", required = true)
	private String itemDesc;
	@ApiModelProperty(value = "促销状态", required = true)
	private String promoType;
	@ApiModelProperty(value = "可销标识", required = true)
	private String sellableInd;
	@ApiModelProperty(value = "可订标识", required = true)
	private String orderableInd;
	@ApiModelProperty(value = "退货标识", required = true)
	private String returnableInd;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "条形码", required = true)
	private String barcode;
	@ApiModelProperty(value = "销售单位", required = true)
	private String sellingUom;
	@ApiModelProperty(value = "售价", required = true)
	private BigDecimal retail;
	@ApiModelProperty(value = "促销价", required = true)
	private BigDecimal promoRetail;
	@ApiModelProperty(value = "会员价", required = true)
	private BigDecimal vipPrice;
	@ApiModelProperty(value = "当日销售金额", required = true)
	private BigDecimal daySaleValue;
	@ApiModelProperty(value = "前四周销量", required = true)
	private BigDecimal fourWeekSale;
	@ApiModelProperty(value = "当日未税扫描毛利率", required = true)
	private String gpEcl;
	@ApiModelProperty(value = "实时库存", required = true)
	private BigDecimal realSoh;
	@ApiModelProperty(value = "在途数量", required = true)
	private BigDecimal inTransitQty;
	@ApiModelProperty(value = "可销天数", required = true)
	private BigDecimal availableSaleDay;
	@ApiModelProperty(value = "正常日均销量", required = true)
	private BigDecimal daySale;
	@ApiModelProperty(value = "大类名称", required = true)
	private String deptDesc;
	@ApiModelProperty(value = "中类名称", required = true)
	private String classDesc;
	@ApiModelProperty(value = "小类名称", required = true)
	private String subClassDesc;
	@ApiModelProperty(value = "产地", required = true)
	private String area;
	@ApiModelProperty(value = "保质期", required = true)
	private String shelfLife;
	@ApiModelProperty(value = "主供应商名称", required = true)
	private String suppName;
	@ApiModelProperty(value = "大包装数", required = true)
	private BigDecimal suppPackSize;
	@ApiModelProperty(value = "内包装数", required = true)
	private BigDecimal innerPackSize;
	@ApiModelProperty(value = "平均成本", required = true)
	private BigDecimal avCost;
	@ApiModelProperty(value = "协议进价", required = true)
	private BigDecimal unitCost;
	@ApiModelProperty(value = "销项税", required = true)
	private String saleTaxRate;
	@ApiModelProperty(value = "进项税", required = true)
	private String incomeTaxRate;

}
