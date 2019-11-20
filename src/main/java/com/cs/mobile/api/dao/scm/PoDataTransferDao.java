package com.cs.mobile.api.dao.scm;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.scm.PoTransferDetail;
import com.cs.mobile.api.model.scm.PoTransferHead;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 基地回货订单同步DAO
 *
 * @author wells.wong
 * @date 2019/9/5
 */
@Repository
public class PoDataTransferDao extends AbstractDao {
    private static final RowMapper<PoTransferDetail> PO_DETAIL_RM = new BeanPropertyRowMapper<>(PoTransferDetail.class);
    private static final RowMapper<PoTransferHead> PO_HEAD_RM = new BeanPropertyRowMapper<>(PoTransferHead.class);

    /**
     * 获取需要同步的订单数据
     * 期望到货时间前一天为今天之前的
     * 每次查询20条
     *
     * @param
     * @return java.util.List<com.cs.mobile.api.model.scm.PoTransferHead>
     * @author wells.wong
     * @date 2019/9/5
     */
    public List<PoTransferHead> getPoTransferData() {

        String sql = "select asn_head.po_asn_sn,asn_head.creator,head.supplier,head.wh_code,to_char(asn_head" +
                ".create_time,'yyyyMMdd') as create_time " +
                ",to_char(head.audit_time,'yyyyMMdd') as audit_time,head.auditor,to_char(head.exp_arrival_date," +
                "'yyyyMMdd') as exp_arrival_date " +
                ",to_char(head.exp_arrival_date+7,'yyyyMMdd') as not_after_date " +
                "from csmb_po_asn_head asn_head  " +
                "left join csmb_po_head head " +
                "on asn_head.po_sn=head.po_sn " +
                "where asn_head.TRANSFER_STATUS=0 and exp_arrival_date<=sysdate+1 and rownum<=20";
        List<PoTransferHead> list = super.queryForList(sql, PO_HEAD_RM);
        if (list != null && list.size() > 0) {
            for (PoTransferHead poTransferHead : list) {
                poTransferHead.setPoTransferDetailList(this.getPoDetailByHead(poTransferHead.getPoAsnSn()));
            }
        }
        return list;
    }

    /**
     * 根据发货单号查询发货单行信息
     *
     * @param poAsnSn
     * @return java.util.List<com.cs.mobile.api.model.scm.PoTransferDetail>
     * @author wells.wong
     * @date 2019/9/5
     */
    private List<PoTransferDetail> getPoDetailByHead(String poAsnSn) {

        String sql = "select asn_detail.item,detail.uom_desc,asn_head.freight " +
                ",detail.unit_price,detail.standard_of_package,asn_head.total_qty,detail.tax_rate,asn_detail" +
                ".po_asn_qty " +
                "from csmb_po_asn_detail asn_detail " +
                "left join csmb_po_asn_head asn_head " +
                "on asn_head.po_asn_sn=asn_detail.po_asn_sn " +
                "left join csmb_po_detail detail " +
                "on detail.po_sn=asn_head.po_sn " +
                "and detail.item=asn_detail.item " +
                "where asn_detail.po_asn_sn=? order by asn_detail.item";
        return super.queryForList(sql, PO_DETAIL_RM, poAsnSn);
    }

    public int updateTransferStatus(List<String> snList, String transferFileName) {
        StringBuilder sql = new StringBuilder();
        sql.append("update CSMB_PO_ASN_HEAD set TRANSFER_STATUS=1,TRANSFER_TIME=sysdate,TRANSFER_FILE='" + transferFileName + "' where po_asn_sn in(");
        for (int i = 0; i < snList.size(); i++) {
            sql.append("'");
            sql.append(snList.get(i));
            sql.append("'");
            if (i < snList.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        return super.jdbcTemplate.update(sql.toString());
    }


}
