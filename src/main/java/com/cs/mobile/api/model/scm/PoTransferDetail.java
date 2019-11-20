package com.cs.mobile.api.model.scm;

import com.cs.mobile.common.utils.OperationUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PoTransferDetail implements Serializable {
    private static final long serialVersionUID = -3969722271675939983L;
    /**
     * 发货单号
     */
    private String poAsnSn;
    /**
     * 行号
     */
    private String seqNo;
    /**
     * 商品编码
     */
    private String item;
    /**
     * 商品条码
     */
    private String barcode = "";
    /**
     * 商品单位
     */
    private String uomDesc;
    /**
     * 未税到仓单价
     */
    private String sendUnitPriceEcl;
    /**
     * 含税到仓单价
     */
    private String sendUnitPriceInc;
    /**
     * 进项税率
     */
    private BigDecimal taxRate;
    /**
     * 单位售价
     */
    private String unitRetail = "0";
    /**
     * 发货数量
     */
    private String poAsnQty;
    /**
     * 取消数量
     */
    private String qtyCancelled = "0";
    /**
     * 发布时间
     */
    private String publish_time = "";
    /**
     * 附加属性
     */
    private String addAttribute = "";
    /**
     * 重量单价
     */
    private BigDecimal unitPrice;
    /**
     * 运费
     */
    private BigDecimal freight;
    /**
     * 总数量
     */
    private BigDecimal totalQty;
    /**
     * 标准件
     */
    private BigDecimal standardOfPackage;

    /**
     * 获取发货单行组装数据
     *
     * @param
     * @return java.lang.String
     * @author wells.wong
     * @date 2019/9/5
     */
    public String getTransferDetailData() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getPoAsnSn())
                .append("|").append(this.getSeqNo())
                .append("|").append(this.getItem())
                .append("|").append(this.getBarcode())
                .append("|").append(this.getUomDesc())
                .append("|").append(this.getSendUnitPriceEcl())
                .append("|").append(this.getSendUnitPriceInc())
                .append("|").append(this.getTaxRate())
                .append("|").append(this.getUnitRetail())
                .append("|").append(this.getPoAsnQty())
                .append("|").append(this.getQtyCancelled())
                .append("|").append(this.getPublish_time())
                .append("|").append(this.getAddAttribute())
                .append("\n");
        return sb.toString();
    }

    public String getSendUnitPriceEcl() {
        BigDecimal ecl = unitPrice.add(OperationUtil.divideHandler(this.freight, this.totalQty));
        if (!"KG".equals(uomDesc.toUpperCase())) {//非kg商品到仓单价 = （重量单价 + 运费平摊） * 标准件
            ecl = ecl.multiply(this.standardOfPackage);
        }
        BigDecimal taxRate = this.getTaxRate() == null ? BigDecimal.ZERO :
                this.getTaxRate().divide(new BigDecimal(100));
        return OperationUtil.divideHandler(ecl, new BigDecimal(1).add(taxRate)).toString();
    }

    public String getSendUnitPriceInc() {
        //系统里保存的是含税单价
        if ("KG".equals(uomDesc.toUpperCase())) {// kg商品到仓单价 = 重量单价 + 运费平摊
            return unitPrice.add(OperationUtil.divideHandler(this.freight, this.totalQty)).toString();
        } else {// 非kg商品金额 = 到仓单价 * 件数
            return unitPrice.add(OperationUtil.divideHandler(this.freight, this.totalQty)).multiply(this.standardOfPackage).toString();
        }
    }

    public String getPoAsnQty() {
        if ("KG".equals(uomDesc.toUpperCase())) {// kg商品
            return this.poAsnQty;
        } else {// 非kg商品金额 = 发货数量/ 标准件数
            return OperationUtil.divideHandler(new BigDecimal(this.poAsnQty), this.standardOfPackage).toString();
        }
    }
}
