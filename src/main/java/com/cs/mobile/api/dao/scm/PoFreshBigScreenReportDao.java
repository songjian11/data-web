package com.cs.mobile.api.dao.scm;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.scm.*;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class PoFreshBigScreenReportDao extends AbstractDao {

    /**
     * 查询商品价格top5信息
     * @param depts
     * @param time
     * @return
     */
    public List<ShopInfo> queryShopPriceTopFiveInfo(List<String> depts,
                                               Date time) {
        StringBuilder sb = new StringBuilder("select * from (select a.item, " +
                "       max(a.item_desc) as itemName " +
                "  from CSMB_PO_DETAIL a, inf_item b, CSMB_PO_ASN_HEAD c " +
                " where a.item = b.item " +
                " and a.po_sn = c.po_sn " +
                " and c.po_asn_status = '02' ");
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        if(null != time){
            sb.append(" and c.create_time >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", DateUtil.getFirstDayForYear(time))).append("' ");
        }
        sb.append(" group by a.item " +
                " order by (sum((case " +
                "                 when a.uom_desc = 'KG' then " +
                "                  nvl(a.UNIT_PRICE, 0) " +
                "                 else " +
                "                  nvl(a.PER_PRICE, 0) " +
                "               end) + (case when c.total_qty != 0 then nvl(c.freight, 0)/c.total_qty else 0 end))) desc) " +
                " where rownum <= 5 ");
        return super.queryForList(sb.toString(), new BeanPropertyRowMapper<>(ShopInfo.class),null);
    }

    /**
     * 查询商品价格趋势信息
     * @param items
     * @param time
     * @return
     */
    public List<ShopPriceTrend> queryShopPriceTrendInfo(List<String> items,
                                                        Date time){
        StringBuilder sb = new StringBuilder("select a.item, " +
                "       max(a.item_desc) as itemName, " +
                "       (sum(case " +
                "              when a.uom_desc = 'KG' then " +
                "               nvl(a.UNIT_PRICE, 0) " +
                "              else " +
                "               nvl(a.PER_PRICE, 0) " +
                "            end) + sum(nvl(c.freight, 0)) / sum(nvl(c.total_qty, 0))) as money, " +
                "       to_char(c.create_time, 'MM') as month " +
                "  from CSMB_PO_DETAIL a, CSMB_PO_ASN_HEAD c " +
                " where a.po_sn = c.po_sn " +
                " and c.po_asn_status = '02' ");
        if(null != items && items.size() > 0){
            if(1 == items.size()){
                sb.append(" and a.item = '").append(items.get(0)).append("' ");
            }else{
                sb.append(" and a.item in (").append(listToStrHaveQuotationMark(items)).append(") ");
            }
        }
        if(null != time){
            sb.append(" and c.create_time >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", DateUtil.getFirstDayForYear(time))).append("' ");
        }
        sb.append(" group by a.item, to_char(c.create_time, 'MM')");
        return super.queryForList(sb.toString(),new BeanPropertyRowMapper<>(ShopPriceTrend.class),null);
    }

    /**
     * 查询车型和奖金的关系表
     * @return
     */
    public List<Bonus> queryBonus(){
        StringBuilder sb = new StringBuilder("select car_type as carType,bonus from CSMB_PO_BONUS");
        return super.queryForList(sb.toString(),new BeanPropertyRowMapper<>(Bonus.class),null);
    }

    /**
     * 查询发货人的发货信息
     * @param shippers(发货人列表)
     * @return
     */
    public List<ShipperBonus> queryShipperBonus(List<String> shippers){
        StringBuilder sb = new StringBuilder("select a.creator_id as shipperCode, " +
                "       max(a.creator) as shipperName, " +
                "       a.car_type as carType, " +
                "       count(a.car_type) as carNum " +
                "  from CSMB_PO_ASN_HEAD a " +
                " where a.po_asn_status = '02' ");
        if(null != shippers && shippers.size() > 0){
            if(1 == shippers.size()){
                sb.append(" and a.creator_id = '").append(shippers.get(0)).append("' ");
            }else{
                sb.append(" and a.creator_id in (").append(listToStrHaveQuotationMark(shippers)).append(") ");
            }
        }
        sb.append(" group by a.creator_id, a.car_type ");
        return super.queryForList(sb.toString(),new BeanPropertyRowMapper<>(ShipperBonus.class),null);
    }

    /**
     * 查询发货车数top10的发货人
     * @return
     */
    public List<Shipper> queryShipperTop10(){
        StringBuilder sb = new StringBuilder("select * " +
                "  from (select a.creator_id as shipperCode, max(a.creator) as shipperName " +
                "          from CSMB_PO_ASN_HEAD a " +
                "          where a.po_asn_status = '02'  " +
                "         group by a.creator_id " +
                "         order by count(a.po_asn_sn) desc) " +
                " where rownum <= 10 ");
        return super.queryForList(sb.toString(),new BeanPropertyRowMapper<>(Shipper.class),null);
    }
}
