package com.cs.mobile.api.model.goods;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品详情
 * 
 * @author wells
 * @date 2019年3月1日
 */
@Data
@ApiModel(value = "GoodsInfo", description = "商品详情")
public class GoodsInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "商品名称", required = true)
	private String itemDesc;
	@ApiModelProperty(value = "大类", required = true)
	private String dept;
	@ApiModelProperty(value = "大类名称", required = true)
	private String deptDesc;
	@ApiModelProperty(value = "中类", required = true)
	private String goodsClass;
	@ApiModelProperty(value = "中类名称", required = true)
	private String classDesc;
	@ApiModelProperty(value = "小类", required = true)
	private String subClass;
	@ApiModelProperty(value = "小类名称", required = true)
	private String subClassDesc;
	@ApiModelProperty(value = "规格", required = true)
	private String packSize;
	@ApiModelProperty(value = "销售单位", required = true)
	private String sellingUom;
	@ApiModelProperty(value = "大包装数", required = true)
	private BigDecimal suppPackSize;
	@ApiModelProperty(value = "内包装数", required = true)
	private BigDecimal innerPackSize;
	@ApiModelProperty(value = "保质期", required = true)
	private String shelfLife;
	@ApiModelProperty(value = "产地", required = true)
	private String area;
	@ApiModelProperty(value = "售价", required = true)
	private BigDecimal retail;
	@ApiModelProperty(value = "促销价", required = true)
	private BigDecimal promoRetail;
	@ApiModelProperty(value = "促销期间", required = true)
	private String promoDate;
	@ApiModelProperty(value = "会员价", required = true)
	private BigDecimal vipPrice;
	@ApiModelProperty(value = "实时库存", required = true)
	private BigDecimal realSoh;
	@ApiModelProperty(value = "在途数量", required = true)
	private BigDecimal inTransitQty;
	@ApiModelProperty(value = "正常日均销量", required = true)
	private BigDecimal daySale;
	@ApiModelProperty(value = "促销日均销量", required = true)
	private BigDecimal promoDaySale;
	@ApiModelProperty(value = "前四周销量", required = true)
	private BigDecimal fourWeekSale;
	@ApiModelProperty(value = "最近入库日期", required = true)
	private String lastReceived;
	@ApiModelProperty(value = "进项税", required = true)
	private String incomeTaxRate;
	@ApiModelProperty(value = "销项税", required = true)
	private String saleTaxRate;
	@ApiModelProperty(value = "NBB标识", required = true)
	private String nbbType;
	@ApiModelProperty(value = "NBO标识", required = true)
	private String nboType;
	@ApiModelProperty(value = "库存状态", required = true)
	private String itemType;
	@ApiModelProperty(value = "经营方式", required = true)
	private String businessMode;
	@ApiModelProperty(value = "物流模式", required = true)
	private String logisticsDeliveryModel;
	@ApiModelProperty(value = "商品状态", required = true)
	private String status;
	@ApiModelProperty(value = "可销标识", required = true)
	private String sellableInd;
	@ApiModelProperty(value = "可订标识", required = true)
	private String orderableInd;
	@ApiModelProperty(value = "主供应商编码", required = true)
	private String primarySupp;
	@ApiModelProperty(value = "主供应商名称", required = true)
	private String suppName;
	@ApiModelProperty(value = "退货标识", required = true)
	private String returnableInd;
	@ApiModelProperty(value = "促销状态", required = true)
	private String promoType;
	@ApiModelProperty(value = "当日销量", required = true)
	private String realTimeSale;
	@ApiModelProperty(value = "条形码", required = true)
	private String barcode;
	@ApiModelProperty(value = "是否是称重商品", required = true)
	private String weightInd;
	@ApiModelProperty(value = "电子标签模板号", required = true)
	private String lblType;
	@ApiModelProperty(value = "可销天数", required = true)
	private BigDecimal availableSaleDay;
	@ApiModelProperty(value = "平均成本", required = true)
	private BigDecimal avCost;
	@ApiModelProperty(value = "协议进价", required = true)
	private BigDecimal unitCost;

}
