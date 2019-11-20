package com.cs.mobile.api.dao.scm;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.scm.ItemTaxRate;
import com.cs.mobile.api.model.scm.PoAsnDetail;
import com.cs.mobile.api.model.scm.PoAsnHead;
import com.cs.mobile.api.model.scm.PoDetail;
import com.cs.mobile.api.model.scm.PoHead;
import com.cs.mobile.api.model.scm.PoWareHouse;
import com.cs.mobile.api.model.scm.response.ItemResp;
import com.cs.mobile.api.model.scm.response.OrderListResp;
import com.cs.mobile.api.model.scm.response.PoAsnDetailResp;
import com.cs.mobile.api.model.scm.response.PoPrepareItemResp;
import com.cs.mobile.api.model.scm.response.SupplierResp;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.common.constant.UserTypeEnum;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.github.pagehelper.util.StringUtil;

/**
 * 基地回货管理DAO
 *
 * @author wells.wong
 * @date 2019年7月26日
 */
@Repository
public class PoMangerDao extends AbstractDao {
    private static final RowMapper<ItemResp> ITEM_RM = new BeanPropertyRowMapper<>(ItemResp.class);
    private static final RowMapper<SupplierResp> SUPPLIER_RM = new BeanPropertyRowMapper<>(SupplierResp.class);
    private static final RowMapper<PoWareHouse> WH_RM = new BeanPropertyRowMapper<>(PoWareHouse.class);
    private static final RowMapper<PoHead> PO_HEAD_RM = new BeanPropertyRowMapper<>(PoHead.class);
    private static final RowMapper<PoDetail> PO_DETAIL_RM = new BeanPropertyRowMapper<>(PoDetail.class);
    private static final RowMapper<ItemTaxRate> ITEM_TAX_RATE_RM = new BeanPropertyRowMapper<>(ItemTaxRate.class);
    private static final RowMapper<PoPrepareItemResp> PO_PREPARE_ITEM_RM = new BeanPropertyRowMapper<>(
            PoPrepareItemResp.class);
    private static final RowMapper<PoAsnDetailResp> PO_ASN_DETAIL_RM = new BeanPropertyRowMapper<>(
            PoAsnDetailResp.class);
    private static final RowMapper<PoAsnHead> PO_ASN_HEAD_RM = new BeanPropertyRowMapper<>(PoAsnHead.class);
    private static final String GET_ITEM_INFO = "SELECT m.item,m.item_Desc,nvl(u.uom_desc,m.standard_uom) uom_desc,s" +
            ".supplier,l.unit_cost  "
            + "FROM item_master m,uom_class u,ITEM_SUPPLIER s, item_supp_country_loc l,wh where u.uom(+)=m" +
            ".standard_uom and status='A' AND ITEM_LEVEL=TRAN_LEVEL and s.item=m.item "
            + "and l.item=m.item and l.supplier=s.supplier and l.loc=wh.primary_vwh and m.item=? and s.supplier=? and" +
            " wh.wh=? ";
    private static final String GET_SUPPLIER_INFO = "SELECT supplier,sup_name FROM sups where supplier=?";
    private static final String GET_ALL_WH = "select * from CSMB_PO_WH";
    private static final String GET_PO_HEAD = "select * from CSMB_PO_HEAD WHERE PO_SN=?";
    private static final String GET_PO_DETAIL_LIST = "select * from CSMB_PO_DETAIL WHERE PO_SN=?";
    private static final String GET_PO_DETAIL = "select * from CSMB_PO_DETAIL WHERE PO_SN=? AND item=?";
    private static final String GET_ITEM_TAX_RATE = "select item,vat_rate from v_cmx_cost_vat_item where item=?";
    private static final String GET_ASN_DETAIL = "select asn_detail.item,asn_detail.item_desc,detail.UOM_DESC," +
            "asn_detail.po_asn_qty, "
            + "detail.unit_price,asn_head.freight,asn_head.total_qty,detail.STANDARD_OF_PACKAGE "
            + "from CSMB_PO_ASN_DETAIL asn_detail  "
            + "left join CSMB_PO_ASN_HEAD asn_head on asn_detail.PO_ASN_SN=asn_head.PO_ASN_SN  "
            + "left join csmb_po_detail detail on detail.po_sn=asn_head.po_sn and detail.item=asn_detail.item "
            + "where asn_head.po_asn_status='02' and asn_head.po_asn_sn=? ";
    private static final String GET_ASN_HEAD = "select * from csmb_po_asn_head where po_asn_sn=? ";
    private static final String GET_TOTAL_SURPLUS_QTY = "select sum(NVL(SURPLUS_QTY, 0)) from CSMB_PO_DETAIL where " +
            "po_sn=? ";

    /**
     * 获取单据
     *
     * @param beginDate
     * @param endDate
     * @param poSn
     * @param supplier
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public PageResult<OrderListResp> getOrderList(UserInfo userInfo, String status, String beginDate, String endDate,
                                                  String poSn, String supplier, int page, int pageSize) throws Exception {
        StringBuffer sql = new StringBuffer("select * from ( ");
        List<Object> params = new ArrayList<>();
        if (userInfo.getTypeList().contains(UserTypeEnum.JD_PURCHASER.getType())) { // 基地采购:录单及发货
            // 基地采购查看状态：全部、未提交、待审核、待发货、已发货
            if ("0".equals(status)) {// 全部
                sql.append(
                        " select supplier,po_sn,null as po_asn_sn,create_time as po_create_time,null as " +
                                "po_asn_create_time, "
                                + "po_status,null as po_asn_status from CSMB_PO_HEAD where PO_STATUS in('01','02'," +
                                "'03') "
                                + " and CREATOR_ID=?  union all "
                                + "select po.supplier,po.po_sn,po_asn_sn,po.create_time as po_create_time, asn" +
                                ".create_time as po_asn_create_time, "
                                + "po.po_status,asn.po_asn_status "
                                + "from CSMB_PO_ASN_HEAD asn left join CSMB_PO_HEAD po on asn.po_sn=po.po_sn "
                                + "where asn.PO_ASN_STATUS='02' and po.CREATOR_ID=? ");
                params.add(userInfo.getPersonId());
                params.add(userInfo.getPersonId());
            } else if ("1".equals(status) || "2".equals(status) || "3".equals(status)) {// 未提交、待审核、待发货
                sql.append(
                        " select supplier,po_sn,null as po_asn_sn,create_time as po_create_time,null as " +
                                "po_asn_create_time, "
                                + "po_status,null as po_asn_status from CSMB_PO_HEAD where PO_STATUS = ?  and " +
                                "CREATOR_ID=? ");
                params.add("0" + status);
                params.add(userInfo.getPersonId());
            } else if ("4".equals(status)) {// 已发货
                sql.append(
                        " select po.supplier,po.po_sn,po_asn_sn,po.create_time as po_create_time, asn.create_time as " +
                                "po_asn_create_time, "
                                + "po.po_status,asn.po_asn_status "
                                + "from CSMB_PO_ASN_HEAD asn left join CSMB_PO_HEAD po on asn.po_sn=po.po_sn "
                                + "where asn.PO_ASN_STATUS='02' and po.CREATOR_ID=? ");
                params.add(userInfo.getPersonId());
            } else {
                ExceptionUtils.wapperBussinessException("查询的状态异常");
            }
        } else if (userInfo.getTypeList().contains(UserTypeEnum.ZB_PURCHASER.getType())) {// 总部采购：审核
            // 总部采购查看状态：全部、待审核、待发货、已发货
            if ("0".equals(status)) {// 全部
                sql.append(
                        " select supplier,po_sn,null as po_asn_sn,create_time as po_create_time,null as " +
                                "po_asn_create_time, "
                                + "po_status,null as po_asn_status from CSMB_PO_HEAD where PO_STATUS in('02','03') "
                                + " union all "
                                + "select po.supplier,po.po_sn,po_asn_sn,po.create_time as po_create_time, asn" +
                                ".create_time as po_asn_create_time, "
                                + "po.po_status,asn.po_asn_status "
                                + "from CSMB_PO_ASN_HEAD asn left join CSMB_PO_HEAD po on asn.po_sn=po.po_sn "
                                + "where asn.PO_ASN_STATUS='02' ");
            } else if ("2".equals(status) || "3".equals(status)) {// 待审核、待发货
                sql.append(
                        " select supplier,po_sn,null as po_asn_sn,create_time as po_create_time,null as " +
                                "po_asn_create_time, "
                                + "po_status,null as po_asn_status from CSMB_PO_HEAD where PO_STATUS = ? ");
                params.add("0" + status);
            } else if ("4".equals(status)) {// 已发货
                sql.append(
                        " select po.supplier,po.po_sn,po_asn_sn,po.create_time as po_create_time, asn.create_time as " +
                                "po_asn_create_time, "
                                + "po.po_status,asn.po_asn_status "
                                + "from CSMB_PO_ASN_HEAD asn left join CSMB_PO_HEAD po on asn.po_sn=po.po_sn "
                                + "where asn.PO_ASN_STATUS='02' ");
            } else {
                ExceptionUtils.wapperBussinessException("查询的状态异常");
            }
        } else {
            ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
        }

        sql.append(") where 1=1 ");
        if (StringUtil.isNotEmpty(beginDate)) {
            sql.append(" and TO_CHAR(po_create_time,'yyyy-MM-dd')>=? ");
            params.add(beginDate);
        }
        if (StringUtil.isNotEmpty(endDate)) {
            sql.append(" and TO_CHAR(po_create_time,'yyyy-MM-dd')<=? ");
            params.add(endDate);
        }
        if (StringUtil.isNotEmpty(poSn)) {
            sql.append(" and po_sn=? ");
            params.add(poSn);
        }
        if (StringUtil.isNotEmpty(supplier)) {
            sql.append(" and supplier=? ");
            params.add(supplier);
        }
        return super.queryByPage(sql.toString(), OrderListResp.class, page, pageSize, "po_create_time", Sort.DESC,
                params.toArray());
    }

    /**
     * 获取审核订单
     *
     * @param userInfo
     * @param status
     * @param beginDate
     * @param endDate
     * @param poSn
     * @param supplier
     * @param page
     * @param pageSize
     * @return com.cs.mobile.api.model.common.PageResult<com.cs.mobile.api.model.scm.response.OrderListResp>
     * @author wells.wong
     * @date 2019/10/15
     */
    public PageResult<OrderListResp> getAuditOrderList(UserInfo userInfo, String status, String beginDate,
                                                       String endDate,
                                                       String poSn, String supplier, int page, int pageSize) throws Exception {

        StringBuffer sql = new StringBuffer("select * from ( ");
        List<Object> params = new ArrayList<>();
        if (userInfo.getTypeList().contains(UserTypeEnum.ZB_PURCHASER.getType())) {// 总部采购：审核
            // 总部采购查看状态：全部、待审核、待发货、已发货
            if ("0".equals(status)) {// 全部
                sql.append(
                        " select supplier,po_sn,null as po_asn_sn,create_time as po_create_time,null as " +
                                "po_asn_create_time, "
                                + "po_status,null as po_asn_status from CSMB_PO_HEAD where PO_STATUS in('02','03') "
                                + " union all "
                                + "select po.supplier,po.po_sn,po_asn_sn,po.create_time as po_create_time, asn" +
                                ".create_time as po_asn_create_time, "
                                + "po.po_status,asn.po_asn_status "
                                + "from CSMB_PO_ASN_HEAD asn left join CSMB_PO_HEAD po on asn.po_sn=po.po_sn "
                                + "where asn.PO_ASN_STATUS='02' ");
            } else if ("2".equals(status) || "3".equals(status)) {// 待审核、待发货
                sql.append(
                        " select supplier,po_sn,null as po_asn_sn,create_time as po_create_time,null as " +
                                "po_asn_create_time, "
                                + "po_status,null as po_asn_status from CSMB_PO_HEAD where PO_STATUS = ? ");
                params.add("0" + status);
            } else if ("4".equals(status)) {// 已发货
                sql.append(
                        " select po.supplier,po.po_sn,po_asn_sn,po.create_time as po_create_time, asn.create_time as " +
                                "po_asn_create_time, "
                                + "po.po_status,asn.po_asn_status "
                                + "from CSMB_PO_ASN_HEAD asn left join CSMB_PO_HEAD po on asn.po_sn=po.po_sn "
                                + "where asn.PO_ASN_STATUS='02' ");
            } else {
                ExceptionUtils.wapperBussinessException("查询的状态异常");
            }
        } else {
            ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
        }

        sql.append(") where 1=1 ");
        if (StringUtil.isNotEmpty(beginDate)) {
            sql.append(" and TO_CHAR(po_create_time,'yyyy-MM-dd')>=? ");
            params.add(beginDate);
        }
        if (StringUtil.isNotEmpty(endDate)) {
            sql.append(" and TO_CHAR(po_create_time,'yyyy-MM-dd')<=? ");
            params.add(endDate);
        }
        if (StringUtil.isNotEmpty(poSn)) {
            sql.append(" and po_sn=? ");
            params.add(poSn);
        }
        if (StringUtil.isNotEmpty(supplier)) {
            sql.append(" and supplier=? ");
            params.add(supplier);
        }
        return super.queryByPage(sql.toString(), OrderListResp.class, page, pageSize, "po_create_time", Sort.DESC,
                params.toArray());
    }

    /**
     * 获取自己发布的订单
     *
     * @param userInfo
     * @param status
     * @param beginDate
     * @param endDate
     * @param poSn
     * @param supplier
     * @param page
     * @param pageSize
     * @return com.cs.mobile.api.model.common.PageResult<com.cs.mobile.api.model.scm.response.OrderListResp>
     * @author wells.wong
     * @date 2019/10/15
     */
    public PageResult<OrderListResp> getMyOrderList(UserInfo userInfo, String status, String beginDate,
                                                    String endDate,
                                                    String poSn, String supplier, int page, int pageSize) throws Exception {

        StringBuffer sql = new StringBuffer("select * from ( ");
        List<Object> params = new ArrayList<>();
        if (userInfo.getTypeList().contains(UserTypeEnum.JD_PURCHASER.getType())) { // 基地采购:录单及发货
            // 基地采购查看状态：全部、未提交、待审核、待发货、已发货
            if ("0".equals(status)) {// 全部
                sql.append(
                        " select supplier,po_sn,null as po_asn_sn,create_time as po_create_time,null as " +
                                "po_asn_create_time, "
                                + "po_status,null as po_asn_status from CSMB_PO_HEAD where PO_STATUS in('01','02'," +
                                "'03') "
                                + " and CREATOR_ID=?  union all "
                                + "select po.supplier,po.po_sn,po_asn_sn,po.create_time as po_create_time, asn" +
                                ".create_time as po_asn_create_time, "
                                + "po.po_status,asn.po_asn_status "
                                + "from CSMB_PO_ASN_HEAD asn left join CSMB_PO_HEAD po on asn.po_sn=po.po_sn "
                                + "where asn.PO_ASN_STATUS='02' and po.CREATOR_ID=? ");
                params.add(userInfo.getPersonId());
                params.add(userInfo.getPersonId());
            } else if ("1".equals(status) || "2".equals(status) || "3".equals(status)) {// 未提交、待审核、待发货
                sql.append(
                        " select supplier,po_sn,null as po_asn_sn,create_time as po_create_time,null as " +
                                "po_asn_create_time, "
                                + "po_status,null as po_asn_status from CSMB_PO_HEAD where PO_STATUS = ?  and " +
                                "CREATOR_ID=? ");
                params.add("0" + status);
                params.add(userInfo.getPersonId());
            } else if ("4".equals(status)) {// 已发货
                sql.append(
                        " select po.supplier,po.po_sn,po_asn_sn,po.create_time as po_create_time, asn.create_time as " +
                                "po_asn_create_time, "
                                + "po.po_status,asn.po_asn_status "
                                + "from CSMB_PO_ASN_HEAD asn left join CSMB_PO_HEAD po on asn.po_sn=po.po_sn "
                                + "where asn.PO_ASN_STATUS='02' and po.CREATOR_ID=? ");
                params.add(userInfo.getPersonId());
            } else {
                ExceptionUtils.wapperBussinessException("查询的状态异常");
            }
        } else {
            ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
        }

        sql.append(") where 1=1 ");
        if (StringUtil.isNotEmpty(beginDate)) {
            sql.append(" and TO_CHAR(po_create_time,'yyyy-MM-dd')>=? ");
            params.add(beginDate);
        }
        if (StringUtil.isNotEmpty(endDate)) {
            sql.append(" and TO_CHAR(po_create_time,'yyyy-MM-dd')<=? ");
            params.add(endDate);
        }
        if (StringUtil.isNotEmpty(poSn)) {
            sql.append(" and po_sn=? ");
            params.add(poSn);
        }
        if (StringUtil.isNotEmpty(supplier)) {
            sql.append(" and supplier=? ");
            params.add(supplier);
        }
        return super.queryByPage(sql.toString(), OrderListResp.class, page, pageSize, "po_create_time", Sort.DESC,
                params.toArray());
    }

    /**
     * 获取商品信息
     *
     * @param item
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public ItemResp getItemInfo(String item, String supplier, String whCode) throws Exception {
        return super.queryForObject(GET_ITEM_INFO, ITEM_RM, item, supplier, whCode);
    }

    /**
     * 获取基地信息
     *
     * @param supplier
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public SupplierResp getSupplierInfo(String supplier) throws Exception {
        return super.queryForObject(GET_SUPPLIER_INFO, SUPPLIER_RM, supplier);
    }

    /**
     * 获取所有仓库
     *
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public List<PoWareHouse> getAllWh() throws Exception {
        return super.queryForList(GET_ALL_WH, WH_RM);
    }

    /**
     * 保存订单头信息
     *
     * @param poHead
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public int insertHead(PoHead poHead) throws Exception {
        StringBuffer sql = new StringBuffer(
                "INSERT INTO CSMB_PO_HEAD(PO_SN, SUPPLIER, SUP_NAME, WH_CODE, WH_NAME, VALIDITY_PERIOID, "
                        + "EXP_ARRIVAL_DATE, PURCHASER, PO_STATUS, PO_TYPE, REMARK, CREATOR_ID, CREATOR, "
                        + "CREATE_TIME,AUDITOR_ID,AUDITOR,AUDIT_TIME,BIDDING_SN) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?,?,?,?)");
        Object[] args = {poHead.getPoSn(), poHead.getSupplier(), poHead.getSupName(), poHead.getWhCode(),
                poHead.getWhName(), poHead.getValidityPerioid(), poHead.getExpArrivalDate(), poHead.getPurchaser(),
                poHead.getPoStatus(), poHead.getPoType(), poHead.getRemark(), poHead.getCreatorId(),
                poHead.getCreator(), poHead.getCreateTime(), poHead.getAuditorId(), poHead.getAuditor(),
                poHead.getAuditTime(), poHead.getBiddingSn()};
        return jdbcTemplate.update(sql.toString(), args);
    }

    /**
     * 查询订单头信息
     *
     * @param poSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public PoHead getPoHead(String poSn) throws Exception {
        return super.queryForObject(GET_PO_HEAD, PO_HEAD_RM, poSn);
    }

    /**
     * 查询单个订单行信息
     *
     * @param poSn
     * @param item
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月27日
     */
    public PoDetail getPoDetail(String poSn, String item) throws Exception {
        return super.queryForObject(GET_PO_DETAIL, PO_DETAIL_RM, poSn, item);
    }

    /**
     * 查询订单行信息列表
     *
     * @param poSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public List<PoDetail> getPoDetailList(String poSn) throws Exception {
        return super.queryForList(GET_PO_DETAIL_LIST, PO_DETAIL_RM, poSn);
    }

    /**
     * 保存订单行信息
     *
     * @param poDetail
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public int insertDetail(PoDetail poDetail) throws Exception {
        StringBuffer sql = new StringBuffer(
                "INSERT INTO CSMB_PO_DETAIL(PO_SN, ITEM, ITEM_DESC, STANDARD_OF_PACKAGE, UOM_DESC, TAX_RATE, "
                        + "NUMBER_OF_PACKAGE, PO_QTY, UNIT_PRICE, SURPLUS_QTY, CREATOR_ID, CREATOR, "
                        + "CREATE_TIME,REMARK,PER_PRICE,PREDICT_ARRIVAL_PRICE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?)");
        Object[] args = {poDetail.getPoSn(), poDetail.getItem(), poDetail.getItemDesc(),
                poDetail.getStandardOfPackage(), poDetail.getUomDesc(), poDetail.getTaxRate(),
                poDetail.getNumberOfPackage(), poDetail.getPoQty(), poDetail.getUnitPrice(), poDetail.getSurplusQty(),
                poDetail.getCreatorId(), poDetail.getCreator(), poDetail.getCreateTime(), poDetail.getRemark(),
                poDetail.getPerPrice(), poDetail.getPredictArrivalPrice()};
        return jdbcTemplate.update(sql.toString(), args);
    }

    /**
     * 获取商品税率
     *
     * @param item
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public ItemTaxRate getItemTaxRate(String item) throws Exception {
        return super.queryForObject(GET_ITEM_TAX_RATE, ITEM_TAX_RATE_RM, item);
    }

    /**
     * 更新订单状态
     *
     * @param status
     * @param poSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public int updatePoStatus(String status, String poSn) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("update CSMB_PO_HEAD set po_status=?,UPDATE_TIME=SYSDATE  where po_sn=? ");
        Object[] args = {status, poSn};
        return jdbcTemplate.update(sql.toString(), args);
    }

    /**
     * 审核订单
     *
     * @param status
     * @param poSn
     * @param auditorId
     * @param auditor
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月27日
     */
    public int auditPoStatus(String status, String poSn, String auditorId, String auditor) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("update CSMB_PO_HEAD set po_status=?,AUDIT_TIME=SYSDATE,auditor_id=?,auditor=?  where po_sn=? ");
        Object[] args = {status, auditorId, auditor, poSn,};
        return jdbcTemplate.update(sql.toString(), args);
    }

    /**
     * 根据订单号获取单个准备发货的商品</br>
     * modify by wells.wong 剩余数量为0的不要反回
     *
     * @param poSn
     * @param historyItem
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public PoPrepareItemResp getPrepareItem(String poSn, String historyItem) throws Exception {
        StringBuffer sql = new StringBuffer(
                "select item,item_desc,unit_price,per_price,uom_desc,NVL(SURPLUS_QTY, 0) as po_qty,standard_of_package,"
                        + " remark from csmb_po_detail where rownum=1 and NVL(SURPLUS_QTY, 0)>0 and po_sn=? ");
        if (StringUtil.isNotEmpty(historyItem)) {
            sql.append(" and item not in( ");
            sql.append(historyItem);
            sql.append(" ) ");
        }
        return super.queryForObject(sql.toString(), PO_PREPARE_ITEM_RM, poSn);
    }

    /**
     * 获取已发货单的发货行
     *
     * @param poAsnSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public List<PoAsnDetailResp> getPoAsnDetailList(String poAsnSn) throws Exception {
        return super.queryForList(GET_ASN_DETAIL, PO_ASN_DETAIL_RM, poAsnSn);
    }

    /**
     * 根据发货订单号获取发货单头信息
     *
     * @param poAsnSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public PoAsnHead getPoAsnHead(String poAsnSn) throws Exception {
        return super.queryForObject(GET_ASN_HEAD, PO_ASN_HEAD_RM, poAsnSn);
    }

    /**
     * 保存发货头信息
     *
     * @param poAsnHead
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public int insertAsnHead(PoAsnHead poAsnHead) throws Exception {
        StringBuffer sql = new StringBuffer("INSERT INTO CSMB_PO_ASN_HEAD(PO_ASN_SN, PO_SN, TRANSPORT_COMPANY, "
                + "FREIGHT, TOTAL_QTY, DRIVER, CAR_NUMBER, DRVIER_PHONE, CAR_TYPE, "
                + "PO_ASN_STATUS, CREATOR_ID, CREATOR, CREATE_TIME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
        Object[] args = {poAsnHead.getPoAsnSn(), poAsnHead.getPoSn(), poAsnHead.getTransportCompany(),
                poAsnHead.getFreight(), poAsnHead.getTotalQty(), poAsnHead.getDriver(), poAsnHead.getCarNumber(),
                poAsnHead.getDrvierPhone(), poAsnHead.getCarType(), poAsnHead.getPoAsnStatus(),
                poAsnHead.getCreatorId(), poAsnHead.getCreator(), poAsnHead.getCreateTime()};
        return jdbcTemplate.update(sql.toString(), args);
    }

    /**
     * 批量插入发货行信息
     *
     * @param poAsnDetailList
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public int batchInserAsnDetail(List<PoAsnDetail> poAsnDetailList) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO CSMB_PO_ASN_DETAIL(PO_ASN_SN, ITEM, ITEM_DESC, PO_ASN_QTY, "
                + "CREATOR_ID, CREATOR, CREATE_TIME, REMARK) VALUES  (?,?,?,?,?,?,?,?)");
        int[] updatedCountArray = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, poAsnDetailList.get(i).getPoAsnSn());
                ps.setString(2, poAsnDetailList.get(i).getItem());
                ps.setString(3, poAsnDetailList.get(i).getItemDesc());
                ps.setBigDecimal(4, poAsnDetailList.get(i).getPoAsnQty());
                ps.setString(5, poAsnDetailList.get(i).getCreatorId());
                ps.setString(6, poAsnDetailList.get(i).getCreator());
                ps.setDate(7, new Date(poAsnDetailList.get(i).getCreateTime().getTime()));
                ps.setString(8, poAsnDetailList.get(i).getRemark());
            }

            @Override
            public int getBatchSize() {
                return poAsnDetailList.size();
            }
        });
        int sumInsertedCount = 0;
        for (int count : updatedCountArray) {
            sumInsertedCount += count;
        }
        return sumInsertedCount;
    }

    /**
     * 批量修改订单行信息
     *
     * @param poDetailList
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public int batchUpdateDetail(List<PoDetail> poDetailList) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append(
                "update CSMB_PO_DETAIL set UPDATE_TIME=SYSDATE ,po_qty=?,number_of_package=? where po_sn=? and item=?" +
                        " ");
        int[] updatedCountArray = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBigDecimal(1, poDetailList.get(i).getPoQty());
                ps.setBigDecimal(2, poDetailList.get(i).getNumberOfPackage());
                ps.setString(3, poDetailList.get(i).getPoSn());
                ps.setString(4, poDetailList.get(i).getItem());
            }

            @Override
            public int getBatchSize() {
                return poDetailList.size();
            }
        });
        int sumInsertedCount = 0;
        for (int count : updatedCountArray) {
            sumInsertedCount += count;
        }
        return sumInsertedCount;
    }

    /**
     * 批量修改订单行剩余数量
     *
     * @param poDetailList
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    public int batchUpdateSurplusQty(List<PoDetail> poDetailList) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("update CSMB_PO_DETAIL set UPDATE_TIME=SYSDATE ,SURPLUS_QTY=? where po_sn=? and item=? ");
        int[] updatedCountArray = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBigDecimal(1, poDetailList.get(i).getSurplusQty());
                ps.setString(2, poDetailList.get(i).getPoSn());
                ps.setString(3, poDetailList.get(i).getItem());
            }

            @Override
            public int getBatchSize() {
                return poDetailList.size();
            }
        });
        int sumInsertedCount = 0;
        for (int count : updatedCountArray) {
            sumInsertedCount += count;
        }
        return sumInsertedCount;
    }

    public BigDecimal getTotalSurplusQty(String poSn) throws Exception {
        Object[] args = {poSn};
        return jdbcTemplate.queryForObject(GET_TOTAL_SURPLUS_QTY, args, BigDecimal.class);
    }

    /**
     * 生产订单序列
     *
     * @param
     * @return java.lang.Long
     * @author wells.wong
     * @date 2019/9/16
     */
    public Long getSn() throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT S_CSMB_PO_SN.NEXTVAL FROM DUAL");
        return jdbcTemplate.queryForObject(sql.toString(), Long.class);
    }
}
