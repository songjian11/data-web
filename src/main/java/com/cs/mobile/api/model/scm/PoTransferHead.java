package com.cs.mobile.api.model.scm;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PoTransferHead implements Serializable {
    private static final long serialVersionUID = 6325303714093615768L;
    /**
     * 发货单号
     */
    private String poAsnSn;
    /**
     * 采购类型
     */
    private String orderType = "N/B";
    /**
     * 部门
     */
    private String groupNo = "";
    /**
     * 发货人
     */
    private String creator;
    /**
     * 基地编码
     */
    private String supplier;
    /**
     * 地点类型
     */
    private String locType = "W";
    /**
     * 入库仓库
     */
    private String whCode;
    /**
     * 促销号
     */
    private String promotion = "";
    /**
     * 发货时间
     */
    private String createTime;
    /**
     * 不早于日期
     */
    private String notBeforeDate = "";
    /**
     * 不晚于日期
     */
    private String notAfterDate = "";
    /**
     * 最早装运日期
     */
    private String earliestShipDate = "";
    /**
     * 最晚装运日期
     */
    private String latestShipDate = "";
    /**
     * 关闭日期
     */
    private String closeDate = "";
    /**
     * 状态
     */
    private String status = "A";
    /**
     * 订单审核日期
     */
    private String auditTime;
    /**
     * 订单审核人
     */
    private String auditor;
    /**
     * 订单类型
     * 直送1，直通2，基地发货3
     */
    private String poType = "3";
    /**
     * 到货时间
     */
    private String expArrivalDate;
    /**
     * 备注
     */
    private String commentDesc = "基地发货单";

    private String stockCategory = "";
    /**
     * 经营方式
     */
    private String businessType = "";
    /**
     * 修改类型
     */
    private String modType = "U";
    /**
     * 场次信息
     */
    private String cc = "";
    /**
     * 失效日期
     */
    private String expiredDate = "";
    /**
     * 发布时间
     */
    private String publishTime = "";
    /**
     * 附加属性
     */
    private String addAttribute = "";

    /**
     * 行数据
     */
    private List<PoTransferDetail> poTransferDetailList;

    /**
     * 获取发货单头组装数据
     *
     * @param
     * @return java.lang.String
     * @author wells.wong
     * @date 2019/9/5
     */
    public String getTransferHeadData() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getPoAsnSn())
                .append("|").append(this.getOrderType())
                .append("|").append(this.getGroupNo())
                .append("|").append(this.getCreator())
                .append("|").append(this.getSupplier())
                .append("|").append(this.getLocType())
                .append("|").append(this.getWhCode())
                .append("|").append(this.getPromotion())
                .append("|").append(this.getCreateTime())
                .append("|").append(this.getNotBeforeDate())
                .append("|").append(this.getNotAfterDate())
                .append("|").append(this.getEarliestShipDate())
                .append("|").append(this.getLatestShipDate())
                .append("|").append(this.getCloseDate())
                .append("|").append(this.getStatus())
                .append("|").append(this.getAuditTime())
                .append("|").append(this.getAuditor())
                .append("|").append(this.getPoType())
                .append("|").append(this.getExpArrivalDate())
                .append("|").append(this.getCommentDesc())
                .append("|").append(this.getStockCategory())
                .append("|").append(this.getBusinessType())
                .append("|").append(this.getModType())
                .append("|").append(this.getCc())
                .append("|").append(this.getExpiredDate())
                .append("|").append(this.getPublishTime())
                .append("|").append(this.getAddAttribute())
                .append("\r\n");
        return sb.toString();
    }
}
