package com.cs.mobile.api.dao.salereport;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.salereport.*;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class SaleReportZyDao extends AbstractDao {
    private static final RowMapper<BaseSaleModel> TOTAL_BASE_SALE_RM = new BeanPropertyRowMapper<BaseSaleModel>(BaseSaleModel.class);
    private static final RowMapper<ChannlModel> TOTAL_CHANNL_RM = new BeanPropertyRowMapper<ChannlModel>(ChannlModel.class);
    private static final RowMapper<SaleCompositionModel> TOTAL_SALE_COMPOSITION_RM = new BeanPropertyRowMapper<SaleCompositionModel>(SaleCompositionModel.class);
    private static final RowMapper<ItemRankModel> TOTAL_ITEM_SALE_RANK_RM = new BeanPropertyRowMapper<ItemRankModel>(ItemRankModel.class);
    private static final RowMapper<ChannelTableModel> TOTAL_CHANNELTABLEMODEL_RM = new BeanPropertyRowMapper<ChannelTableModel>(ChannelTableModel.class);
    private static final String GET_HIS_HOUR_BASE_SALE_PREFIX = "select /*+PARALLEL(A,8)*/ nvl(sum(a.total_real_amt_ecl),0) as totalSale, " +
            "       nvl(sum(a.total_cost_ecl),0) as totalCost, " +
            "       nvl(sum(case when b.mall_name = 1 then a.total_real_amt_ecl end),0) as totalCompareSale, " +
            "       nvl(sum(case when b.mall_name = 1 then a.total_cost_ecl end),0) as totalCompareCost, " +
            "       nvl(sum(a.total_real_amt),0) as totalSaleIn, " +
            "       nvl(sum(a.total_cost),0) as totalCostIn, " +
            "       nvl(sum(case when b.mall_name = 1 then a.total_real_amt end),0) as totalCompareSaleIn, " +
            "       nvl(sum(case when b.mall_name = 1 then a.total_cost end),0) as totalCompareCostIn " +
            "  from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a, zypp.inf_store b " +
            "  where a.store = b.store ";

    private static final String GET_HIS_CHANNLE_NOT_DEPT_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " (case when a.channel in('WX1','WX2','WX3','WX') then 'weixinPay' " +
            " when a.channel in('BTGO','BTGO1','BTGO10','BTGO2','BTGO3','BTGO4','BTGO5','BTGO6','SAPP2','SAPP') then 'betterPay' " +
            " when a.channel in('DD1','DD2') then 'otherPay' " +
            " when a.channel in('JD','JD1') then 'jingdongPay' " +
            " when a.channel in('KJG') then 'scancodePay' " +
            " when a.channel in('VIP','YUNPOS','BKSLS') or a.channel is null then 'normalPay' " +
            " when a.channel in('MT') then 'meituanPay'end) as type, " +
            " sum(a.total_real_amt_ecl) as totalSale, " +
            " sum(a.total_real_amt) as totalSaleIn " +
            " from zypp.sale_head a,zypp.inf_store b " +
            " where a.store = b.store " +
            " and a.vip_no is not null ";

    private static final String GET_HIS_CHANNLE_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " (case when a.channel in('WX1','WX2','WX3','WX') then 'weixinPay' " +
            " when a.channel in('BTGO','BTGO1','BTGO10','BTGO2','BTGO3','BTGO4','BTGO5','BTGO6','SAPP2','SAPP') then 'betterPay' " +
            " when a.channel in('DD1','DD2') then 'otherPay' " +
            " when a.channel in('JD','JD1') then 'jingdongPay' " +
            " when a.channel in('KJG') then 'scancodePay' " +
            " when a.channel in('VIP','YUNPOS','BKSLS') or a.channel is null then 'normalPay' " +
            " when a.channel in('MT') then 'meituanPay'end) as type, " +
            " nvl(sum(a.total_real_amt),0) as totalSaleIn, " +
            " nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSale " +
            "  from zypp.sale_item a, zypp.inf_item b, zypp.inf_store c " +
            " where a.item = b.item " +
            "   and a.store = c.store " +
            "   and a.vip_no is not null ";

    private static final String GET_HIS_SALE_COMPOSITION_PREFIX = "SELECT  " +
            "  /*+PARALLEL(A,8)*/  " +
            "  d.RETAIL_TYPE as type,  " +
            "  nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSale, " +
            "  nvl(sum(a.total_real_amt),0) as totalSaleIn, " +
            "  nvl(sum(a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100)),0) as totalCostIn,  " +
            "  nvl(sum(a.unit_cost_ecl * a.qty),0) as totalCost  " +
            "  FROM zypp.sale_item a, zypp.inf_item b, zypp.inf_store c,zypp.code_sales_type d " +
            " WHERE a.SALES_TYPE = d.SALES_TYPE_ID(+) " +
            "   and a.store = c.store  " +
            "   and a.item = b.item   ";

    private static final String GET_HIS_COMPARE_ITEM_RATE_RANK_PREFIX = "SELECT /*+PARALLEL(A,8)*/  " +
            " b.dept as deptId, " +
            " a.ITEM as itemId, " +
            " nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSale,  " +
            " nvl(sum(a.unit_cost_ecl * a.qty), 0) as totalCost, " +
            " nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)) - sum(a.unit_cost_ecl * a.qty), 0) as totalScanningRate, " +
            " sum(case when c.mall_name = 1 then a.total_real_amt / (1 + a.sale_vat_rate / 100) else 0 end) as totalCompareSale, " +
            " sum(case when c.mall_name = 1 then a.total_real_amt / (1 + a.sale_vat_rate / 100) - (a.unit_cost_ecl * a.qty) else 0 end) as totalCompareScanningRate, " +

            " nvl(sum(a.total_real_amt),0) as totalSaleIn,  " +
            " nvl(sum(a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100)), 0) as totalCostIn, " +
            " nvl(sum(a.total_real_amt) - sum(a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100)), 0) as totalScanningRateIn, " +
            " sum(case when c.mall_name = 1 then a.total_real_amt else 0 end) as totalCompareSaleIn, " +
            " sum(case when c.mall_name = 1 then a.total_real_amt - a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100) else 0 end) as totalCompareScanningRateIn " +
            " FROM zypp.sale_item a, zypp.inf_item b, zypp.inf_store c " +
            " where a.store = c.store  " +
            " and a.item = b.item ";

    private static final String GET_HIS_CHANNLE_ALL_HAVE_DEPT_PREFIX = "select /*+PARALLEL(A,8)*/  " +
            "  max(d.channel_parent_desc) as channelParentName, " +
            "  decode(a.channel,null,'BKSLS',a.channel) as channel, " +
            "  max(d.channel_desc) as channelName, " +
            "  nvl(sum(a.total_real_amt),0) as totalSaleIn,   " +
            "  nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSale  " +
            " from zypp.sale_item a, zypp.inf_item b, zypp.inf_store c, zypp.code_channel d   " +
            " where a.item = b.item  " +
            " and a.store = c.store " +
            " and a.channel = d.channel(+) " +
            " and a.vip_no is not null ";

    private static final String GET_HIS_CHANNLE_ALL_NOT_DEPT_PREFIX = "select /*+PARALLEL(A,8)*/   " +
            "  max(c.channel_parent_desc) as channelParentName, " +
            "  decode(a.channel,null,'BKSLS',a.channel) as channel, " +
            "  max(c.channel_desc) as channelName, " +
            "  sum(a.total_real_amt_ecl) as totalSale,  " +
            "  sum(a.total_real_amt) as totalSaleIn  " +
            "  from zypp.sale_head a,zypp.inf_store b, zypp.code_channel c " +
            "  where a.store = b.store " +
            "  and a.channel = c.channel(+) " +
            "  and a.vip_no is not null ";

    private static final String GET_CHANNELTABLE_PREFIX = "select channel_parent as channelParent," +
            " channel_parent_desc as channelParentDesc, " +
            " channel," +
            " channel_desc as channelDesc " +
            " from zypp.code_channel ";

    /**
     * 查询渠道字典
     * @return
     */
    public List<ChannelTableModel> queryChannelTable(){
       return super.queryForList(GET_CHANNELTABLE_PREFIX,TOTAL_CHANNELTABLEMODEL_RM,null);
    }

    /**
     * 查询大类单品排名（精确到时分秒，包含可比和同比，毛利额是扫描毛利额）
     * @param request
     * @param depts
     * @param items
     * @param hms(时分秒)
     * @param start
     * @param end
     * @return
     */
    public List<ItemRankModel> queryHisItemRankModelForTime(BaseSaleRequest request,
                                                            String depts,
                                                            String items,
                                                            String hms,
                                                            Date start,
                                                            Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_COMPARE_ITEM_RATE_RANK_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(StringUtils.isNotEmpty(hms)){
            sb.append(" and a.tran_datetime >= '00:00:00' ");
            sb.append(" and a.tran_datetime <= '").append(hms).append("' ");
        }
        if(null == end){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and c.area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and c.area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and c.region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and c.region in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and c.store = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and c.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接单品
        if(StringUtils.isNotEmpty(items)){
            sb.append(" and a.ITEM in ( ");
            sb.append(items);
            sb.append(" ) ");
        }
        sb.append(" group by a.ITEM,b.DEPT");
        return super.queryForList(sb.toString(),TOTAL_ITEM_SALE_RANK_RM,paramList.toArray());
    }

    /**
     * 查询历史销售构成(销售和成本)
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<SaleCompositionModel> querySaleComposition(BaseSaleRequest request,Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_SALE_COMPOSITION_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and c.area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and c.area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and c.region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and c.region in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and c.store = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and c.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and b.dept = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and b.dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" GROUP BY d.RETAIL_TYPE");
        return super.queryForList(sb.toString(),TOTAL_SALE_COMPOSITION_RM,paramList.toArray());
    }

    /**
     * 查询渠道构成（不支持大类，只到门店级别）
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ChannlModel> queryHisChannlNotDept(BaseSaleRequest request,Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_CHANNLE_NOT_DEPT_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and b.store = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by (case when a.channel in('WX1','WX2','WX3','WX') then 'weixinPay' " +
                " when a.channel in('BTGO','BTGO1','BTGO10','BTGO2','BTGO3','BTGO4','BTGO5','BTGO6','SAPP2','SAPP') then 'betterPay' " +
                " when a.channel in('DD1','DD2') then 'otherPay' " +
                " when a.channel in('JD','JD1') then 'jingdongPay' " +
                " when a.channel in('KJG') then 'scancodePay' " +
                " when a.channel in('VIP','YUNPOS','BKSLS') or a.channel is null then 'normalPay' " +
                " when a.channel in('MT') then 'meituanPay'end)");
        List<ChannlModel> result = super.queryForList(sb.toString(),TOTAL_CHANNL_RM,paramList.toArray());
        return result;
    }

    /**
     * 查询所有渠道构成（不支持大类，只到门店级别）
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ChannlModel> queryHisAllChannlNotDept(BaseSaleRequest request,Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_CHANNLE_ALL_NOT_DEPT_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and b.store = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by decode(a.channel,null,'BKSLS',a.channel) ");
        List<ChannlModel> result = super.queryForList(sb.toString(),TOTAL_CHANNL_RM,paramList.toArray());
        return result;
    }


    /**
     * 查询渠道构成(支持大类，速度相对较慢)
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ChannlModel> queryHisChannl(BaseSaleRequest request,Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_CHANNLE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by (case when a.channel in('WX1','WX2','WX3','WX') then 'weixinPay' " +
                " when a.channel in('BTGO','BTGO1','BTGO10','BTGO2','BTGO3','BTGO4','BTGO5','BTGO6','SAPP2','SAPP') then 'betterPay' " +
                " when a.channel in('DD1','DD2') then 'otherPay' " +
                " when a.channel in('JD','JD1') then 'jingdongPay' " +
                " when a.channel in('KJG') then 'scancodePay' " +
                " when a.channel in('VIP','YUNPOS','BKSLS') or a.channel is null then 'normalPay' " +
                " when a.channel in('MT') then 'meituanPay'end)");
        List<ChannlModel> result = super.queryForList(sb.toString(),TOTAL_CHANNL_RM,paramList.toArray());
        return result;
    }

    /**
     * 查询所有渠道构成(支持大类，速度相对较慢)
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ChannlModel> queryHisAllChannlHaveDept(BaseSaleRequest request,Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_CHANNLE_ALL_HAVE_DEPT_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by decode(a.channel,null,'BKSLS',a.channel) ");
        List<ChannlModel> result = super.queryForList(sb.toString(),TOTAL_CHANNL_RM,paramList.toArray());
        return result;
    }



    /**
     * 查询历史同比/可比销售额，同比/可比成本
     * @param request
     * @param start
     * @param end
     * @param hour
     * @return
     */
    public BaseSaleModel queryHisCommonBaseDataForHour(BaseSaleRequest request, Date start, Date end,String hour){
        StringBuilder sb = new StringBuilder(GET_HIS_HOUR_BASE_SALE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.hour <= '").append(hour).append("' ");
        sb.append(" and a.hour >= '").append("00").append("' ");
        if(null == end){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
            .append("' ");
        }else{
            sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and b.store = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        BaseSaleModel result = super.queryForObject(sb.toString(),TOTAL_BASE_SALE_RM,paramList.toArray());
        return result;
    }

}
