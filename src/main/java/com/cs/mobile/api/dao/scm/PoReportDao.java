package com.cs.mobile.api.dao.scm;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.scm.response.PoItemDReportResp;
import com.cs.mobile.api.model.scm.response.PoItemReportResp;
import com.cs.mobile.api.model.scm.response.PoPurchaserDReportResp;
import com.cs.mobile.api.model.scm.response.PoSupReportResp;
import com.cs.mobile.api.model.scm.response.PoSupplierDReportResp;
import com.github.pagehelper.util.StringUtil;

/**
 * 基地回货报表
 * 
 * @author wells.wong
 * @date 2019年7月23日
 *
 */
@Repository
public class PoReportDao extends AbstractDao {

	/**
	 * 分页获取单品维度的基地回货报表
	 *
	 * @param beginDate
	 * @param endDate
	 * @param item
	 * @param supplier
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月23日
	 *
	 */
	public PageResult<PoItemReportResp> getItemReportPage(String beginDate, String endDate, String item,
			String supplier, int page, int pageSize) throws Exception {
		StringBuffer sql = new StringBuffer(
				"select asn_detail.item,asn_detail.item_desc,TO_CHAR(head.audit_time,'yyyy-MM-dd') as order_date, "
						+ "TO_CHAR(asn_head.create_time,'yyyy-MM-dd') as send_date,head.supplier,head.sup_name,detail.unit_price "
						+ ",detail.unit_price+(asn_head.freight/asn_head.total_qty) as send_unit_price, "
						+ "sum(asn_detail.po_asn_qty) as send_qty, "
						+ "sum((detail.unit_price+(asn_head.freight/asn_head.total_qty))*asn_detail.po_asn_qty) as send_price "
						+ "from CSMB_PO_ASN_DETAIL asn_detail  "
						+ "left join CSMB_PO_ASN_HEAD asn_head on asn_detail.PO_ASN_SN=asn_head.PO_ASN_SN  "
						+ "left join csmb_po_detail detail on detail.po_sn=asn_head.po_sn and detail.item=asn_detail.item "
						+ "left join csmb_po_head head on head.po_sn=detail.po_sn "
						+ "where asn_head.po_asn_status='02' ");
		List<Object> params = new ArrayList<>();
		if (StringUtil.isNotEmpty(beginDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')>=? ");
			params.add(beginDate);
		}
		if (StringUtil.isNotEmpty(endDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')<=? ");
			params.add(endDate);
		}
		if (StringUtil.isNotEmpty(item)) {
			sql.append(" and asn_detail.item=? ");
			params.add(item);
		}
		if (StringUtil.isNotEmpty(supplier)) {
			sql.append(" and head.supplier=? ");
			params.add(supplier);
		}
		sql.append(" group by asn_detail.item,asn_detail.item_desc,TO_CHAR(head.audit_time,'yyyy-MM-dd'), "
				+ "TO_CHAR(asn_head.create_time,'yyyy-MM-dd'),head.supplier,head.sup_name,detail.unit_price "
				+ ",detail.unit_price+(asn_head.freight/asn_head.total_qty)");
		return super.queryByPage(sql.toString(), PoItemReportResp.class, page, pageSize, "send_date", Sort.ASC,
				params.toArray());
	}

	/**
	 * 分页获取基地维度的基地回货报表
	 *
	 * @param beginDate
	 * @param endDate
	 * @param supplier
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月23日
	 *
	 */
	public PageResult<PoSupReportResp> getSupReportPage(String beginDate, String endDate, String supplier, int page,
			int pageSize) throws Exception {
		StringBuffer sql = new StringBuffer("select head.supplier,head.sup_name,detail.tax_rate, "
				+ "sum(asn_detail.po_asn_qty) as send_qty, "
				+ "sum((detail.unit_price+(asn_head.freight/asn_head.total_qty))*asn_detail.po_asn_qty) as send_price, "
				+ "sum(round(((detail.unit_price+(asn_head.freight/asn_head.total_qty))*asn_detail.po_asn_qty)/(1+detail.tax_rate/100),6)) as no_tax_price, "
				+ "sum((detail.unit_price+(asn_head.freight/asn_head.total_qty))*asn_detail.po_asn_qty-round(((detail.unit_price+(asn_head.freight/asn_head.total_qty))*asn_detail.po_asn_qty)/(1+detail.tax_rate/100),6)) as tax_money "
				+ "from CSMB_PO_ASN_DETAIL asn_detail  "
				+ "left join CSMB_PO_ASN_HEAD asn_head on asn_detail.PO_ASN_SN=asn_head.PO_ASN_SN  "
				+ "left join csmb_po_detail detail on detail.po_sn=asn_head.po_sn and detail.item=asn_detail.item "
				+ "left join csmb_po_head head on head.po_sn=detail.po_sn where asn_head.po_asn_status='02' ");
		List<Object> params = new ArrayList<>();
		if (StringUtil.isNotEmpty(beginDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')>=? ");
			params.add(beginDate);
		}
		if (StringUtil.isNotEmpty(endDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')<=? ");
			params.add(endDate);
		}
		if (StringUtil.isNotEmpty(supplier)) {
			sql.append(" and head.supplier=? ");
			params.add(supplier);
		}
		sql.append(" group by head.supplier,head.sup_name,detail.tax_rate ");
		return super.queryByPage(sql.toString(), PoSupReportResp.class, page, pageSize, "tax_rate", params.toArray());
	}

	public PageResult<PoItemDReportResp> getPoItemDReport(String beginDate, String endDate, String supplier,
			String item, String poSn, int page, int pageSize) throws Exception {
		StringBuffer sql = new StringBuffer(
				"select to_char(asn_head.create_time,'yyyy-MM-dd') as send_date,asn_detail.item,asn_detail.item_desc, "
						+ "sum(asn_detail.po_asn_qty) as send_qty,sum(round(detail.unit_price*asn_detail.po_asn_qty,2)) as send_money , "
						+ "count(distinct concat(asn_head.car_number,asn_head.po_asn_sn)) as car_count "
						+ " ,round(detail.unit_price,2) as unit_price,round(detail.unit_price+asn_head.freight/asn_head.total_qty,2) as send_price "
						+ "from csmb_po_asn_head asn_head  "
						+ "left join csmb_po_head head on asn_head.po_sn=head.po_sn "
						+ "left join csmb_po_asn_detail asn_detail on asn_detail.po_asn_sn=asn_head.po_asn_sn "
						+ "left join csmb_po_detail detail on detail.po_sn=head.po_sn and detail.item=asn_detail.item where po_asn_status='02'");
		List<Object> params = new ArrayList<>();
		if (StringUtil.isNotEmpty(beginDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')>=? ");
			params.add(beginDate);
		}
		if (StringUtil.isNotEmpty(endDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')<=? ");
			params.add(endDate);
		}
		if (StringUtil.isNotEmpty(supplier)) {
			sql.append(" and head.supplier=? ");
			params.add(supplier);
		}
		if (StringUtil.isNotEmpty(item)) {
			sql.append(" and asn_detail.item=? ");
			params.add(item);
		}
		if (StringUtil.isNotEmpty(poSn)) {
			sql.append(" and asn_head.po_asn_sn=? ");
			params.add(poSn);
		}
		sql.append(
				" group by to_char(asn_head.create_time,'yyyy-MM-dd'),asn_detail.item,asn_detail.item_desc,detail.unit_price,round(detail.unit_price+asn_head.freight/asn_head.total_qty,2) ");
		return super.queryByPage(sql.toString(), PoItemDReportResp.class, page, pageSize, "send_date", Sort.DESC,
				params.toArray());
	}

	public PageResult<PoSupplierDReportResp> getPoSupplierDReport(String beginDate, String endDate, String supplier,
			int page, int pageSize) throws Exception {
		StringBuffer sql = new StringBuffer(
				"select head.supplier,head.sup_name,sum(asn_detail.po_asn_qty) as send_qty,sum(round(detail.unit_price*asn_detail.po_asn_qty,2)) as send_money  "
						+ "from csmb_po_asn_head asn_head  "
						+ "left join csmb_po_head head on asn_head.po_sn=head.po_sn "
						+ "left join csmb_po_asn_detail asn_detail on asn_detail.po_asn_sn=asn_head.po_asn_sn "
						+ "left join csmb_po_detail detail on detail.po_sn=head.po_sn and detail.item=asn_detail.item "
						+ "where po_asn_status='02' ");
		List<Object> params = new ArrayList<>();
		if (StringUtil.isNotEmpty(beginDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')>=? ");
			params.add(beginDate);
		}
		if (StringUtil.isNotEmpty(endDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')<=? ");
			params.add(endDate);
		}
		if (StringUtil.isNotEmpty(supplier)) {
			sql.append(" and head.supplier=? ");
			params.add(supplier);
		}
		sql.append(" group by head.supplier,head.sup_name ");
		return super.queryByPage(sql.toString(), PoSupplierDReportResp.class, page, pageSize, "supplier", Sort.DESC,
				params.toArray());
	}

	public PageResult<PoPurchaserDReportResp> getPoPurchaserDReport(String beginDate, String endDate, String purchaser,
			String carType, int page, int pageSize) throws Exception {
		StringBuffer sql = new StringBuffer(
				"select to_char(asn_head.create_time,'yyyy-MM-dd') as send_date,asn_head.creator as purchaser,asn_head.car_type, "
						+ "sum(asn_detail.po_asn_qty) as send_qty,sum(round(detail.unit_price*asn_detail.po_asn_qty,2)) as send_money "
						+ ",count(distinct asn_detail.item) as sku_count,count(distinct concat(asn_head.car_number,asn_head.po_asn_sn)) as car_count "
						+ " from csmb_po_asn_head asn_head  "
						+ "left join csmb_po_head head on asn_head.po_sn=head.po_sn "
						+ "left join csmb_po_asn_detail asn_detail on asn_detail.po_asn_sn=asn_head.po_asn_sn "
						+ "left join csmb_po_detail detail on detail.po_sn=head.po_sn and detail.item=asn_detail.item "
						+ "where po_asn_status='02' ");
		List<Object> params = new ArrayList<>();
		if (StringUtil.isNotEmpty(beginDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')>=? ");
			params.add(beginDate);
		}
		if (StringUtil.isNotEmpty(endDate)) {
			sql.append(" and TO_CHAR(asn_head.create_time,'yyyy-MM-dd')<=? ");
			params.add(endDate);
		}
		if (StringUtil.isNotEmpty(purchaser)) {
			sql.append(" and asn_head.creator=? ");
			params.add(purchaser);
		}
		if (StringUtil.isNotEmpty(carType)) {
			sql.append(" and asn_head.car_type=? ");
			params.add(carType);
		}
		sql.append(" group by to_char(asn_head.create_time,'yyyy-MM-dd'),asn_head.creator,asn_head.car_type ");
		return super.queryByPage(sql.toString(), PoPurchaserDReportResp.class, page, pageSize, "send_date", Sort.DESC,
				params.toArray());
	}
}
