package com.cs.mobile.api.dao.salereport;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.reportPage.HomeAppliance;
import com.cs.mobile.api.model.salereport.*;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//@Slf4j
@Repository
public class SaleReportCsmbDao extends AbstractDao {
    private static final RowMapper<BaseSaleModel> TOTAL_BASE_SALE_RM = new BeanPropertyRowMapper<BaseSaleModel>(BaseSaleModel.class);
    private static final RowMapper<HomeAppliance> TOTAL_HOME_RM = new BeanPropertyRowMapper<HomeAppliance>(HomeAppliance.class);
    private static final RowMapper<ChannlModel> TOTAL_CHANNL_RM = new BeanPropertyRowMapper<ChannlModel>(ChannlModel.class);
    private static final RowMapper<SaleTrendModel> TOTAL_SALE_TREND_RM = new BeanPropertyRowMapper<SaleTrendModel>(SaleTrendModel.class);
    private static final RowMapper<SaleCompositionModel> TOTAL_SALE_COMPOSITION_RM = new BeanPropertyRowMapper<SaleCompositionModel>(SaleCompositionModel.class);
    private static final RowMapper<ItemRankModel> TOTAL_ITEM_SALE_RANK_RM = new BeanPropertyRowMapper<ItemRankModel>(ItemRankModel.class);
    private static final String GET_CUR_BASE_SALE_PREFIX = "select nvl(sum(gp_ecl),0) as totalScanningRate, " +
            "       nvl(sum(amt_ecl),0) as totalSale,   " +
            "       nvl(sum(case when mall_name = 1 then amt_ecl end),0) as totalCompareSale,  " +
            "       nvl(sum(case when mall_name = 1 then gp_ecl end),0) as totalCompareScanningRate, " +
            "       nvl(sum(gp),0) as totalScanningRateIn, " +
            "       nvl(sum(amt),0) as totalSaleIn,   " +
            "       nvl(sum(case when mall_name = 1 then amt end),0) as totalCompareSaleIn,  " +
            "       nvl(sum(case when mall_name = 1 then gp end),0) as totalCompareScanningRateIn   " +
            "       from CSMB_DEPT_SALES where 1=1 ";

    private static final String GET_HIS_BASE_SALE_PREFIX = "select /*+PARALLEL(A,8)*/ nvl(sum(a.sale_value), 0) as totalSale,  " +
            "            nvl(sum(a.sale_value-a.sale_cost+a.invadj_cost-a.wac+a.fund_amount),0) as totalFrontDeskRate,  " +
            "            nvl(sum(a.sale_cost),0) as totalCost,   " +
            "            nvl(sum(a.sale_value_in), 0) as totalSaleIn,  " +
            "            nvl(sum(a.sale_value_in-a.sale_cost_in+a.invadj_cost_in-a.wac_in+a.fund_amount_in),0) as totalFrontDeskRateIn,  " +
            "            nvl(sum(a.sale_cost_in),0) as totalCostIn " +
            "            from csmb_dept_sales_history a, csmb_store b  " +
            "            where a.store_id = b.store_id ";

    private static final String GET_HOME_PREFIX = "select nvl(sum(a.total_retail), 0) as totalSale, " +
            "       nvl(sum(a.total_cost), 0) as totalCost, " +
            "       nvl(sum(a.total_retail_in), 0) as totalSaleIn, " +
            "       nvl(sum(a.total_cost_in), 0) as totalCostIn " +
            "  from csmb_store_dept_hour_sale a, csmb_store b " +
            " where a.store = to_char(b.store_id) ";

    private static final String GET_CUR_CHANNLE_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " (case when a.channel in('WX1','WX2','WX3','WX') then 'weixinPay' " +
            " when a.channel in('BTGO','BTGO1','BTGO10','BTGO2','BTGO3','BTGO4','BTGO5','BTGO6','SAPP2','SAPP') then 'betterPay' " +
            " when a.channel in('DD1','DD2') then 'otherPay' " +
            " when a.channel in('JD','JD1') then 'jingdongPay' " +
            " when a.channel in('KJG') then 'scancodePay' " +
            " when a.channel in('VIP','YUNPOS','BKSLS') or a.channel is null then 'normalPay' " +
            " when a.channel in('MT') then 'meituanPay'end) as type, " +
            " sum(a.AMT_ECL) as totalSale, " +
            " sum(a.AMT) as totalSaleIn " +
            " from CSMB_STORE_SALEDETAIL a " +
            " where a.vipno is not null ";

    private static final String GET_HIS_SALETREND_PREFIX = "select /*+PARALLEL(A,8)*/   " +
            "            to_char(a.sale_date,'yyyyMMdd') as time,  " +
            "            nvl(sum(a.sale_value), 0) as totalSale,  " +
            "            nvl(sum(a.sale_value-a.sale_cost+a.invadj_cost-a.wac+a.fund_amount),0) as totalFrontDeskRate,  " +
            "            nvl(sum(a.sale_cost),0) as totalCost, " +
            "            nvl(sum(a.sale_value_in), 0) as totalSaleIn,  " +
            "            nvl(sum(a.sale_value_in-a.sale_cost_in+a.invadj_cost_in-a.wac_in+a.fund_amount_in),0) as totalFrontDeskRateIn,  " +
            "            nvl(sum(a.sale_cost_in),0) as totalCostIn " +
            "            from csmb_dept_sales_history a, csmb_store b  " +
            "            where a.store_id = b.store_id ";

    private static final String GET_CUR_SALETREND_PREFIX = "select sale_date as time, " +
            " nvl(sum(gp_ecl),0) as totalRate, " +
            " nvl(sum(amt_ecl),0) as totalSale,  " +
            " nvl(sum(gp),0) as totalRateIn, " +
            " nvl(sum(amt),0) as totalSaleIn  " +
            " from CSMB_DEPT_SALES " +
            " where 1=1 ";

    private static final String GET_CUR_SALE_COMPOSITION_PREFIX = "select  " +
            " a.retail_type as type, " +
            " nvl(sum(a.amt_ecl),0) totalSale, " +
            " nvl(sum(a.GP_ECL),0) as totalScanningRate, " +
            " nvl(sum(a.amt),0) totalSaleIn, " +
            " nvl(sum(a.GP),0) as totalScanningRateIn " +
            " from CSMB_STORE_TYPE_SALE a,csmb_store b " +
            " where a.store = b.store_id ";

    private static final String GET_CUR_ITEM_SALE_RANK_PREFIX = "SELECT * FROM ( " +
            " SELECT P.dept as deptId, " +
            "       P.ITEM as itemId, " +
            "       MAX(P.ITEM_DESC) as itemName, " +
            "       SUM(P.AMT_ECL) as totalSale, " +
            "       sum(case when mall_name = 1 then P.AMT_ECL end) as totalCompareSale, " +
            "       SUM(P.AMT) as totalSaleIn, " +
            "       sum(case when mall_name = 1 then P.AMT end) as totalCompareSaleIn, " +
            "       ROW_NUMBER() OVER(PARTITION BY DEPT ORDER BY SUM(P.AMT_ECL) DESC) RN " +
            "  FROM CSMB_STORE_SALEDETAIL P " +
            " WHERE 1=1 ";

    private static final String GET_CUR_ITEM_RATE_RANK_PREFIX = "SELECT * FROM ( " +
            " SELECT P.dept as deptId, " +
            "       P.ITEM as itemId, " +
            "       MAX(P.ITEM_DESC) as itemName, " +
            "       SUM(P.gp_ecl) as totalScanningRate, " +
            "       sum(case when mall_name = 1 then P.gp_ecl end) as totalCompareScanningRate, " +
            "       SUM(P.gp) as totalScanningRateIn, " +
            "       sum(case when mall_name = 1 then P.gp end) as totalCompareScanningRateIn, " +
            "       ROW_NUMBER() OVER(PARTITION BY DEPT ORDER BY SUM(P.gp_ecl) DESC) RN " +
            "  FROM CSMB_STORE_SALEDETAIL P " +
            "  WHERE 1=1 ";

    private static final String GET_CUR_CHANNLE_ALL_PREFIX = "select max(b.channel_parent_desc) as channelParentName, " +
            "  decode(a.channel,null,'BKSLS',a.channel) as channel, " +
            "  max(b.channel_desc) as channelName, " +
            "  sum(a.AMT_ECL) as totalSale,  " +
            "  sum(a.AMT) as totalSaleIn " +
            "  from CSMB_STORE_SALEDETAIL a,code_channel b " +
            "  where a.vipno is not null  " +
            "  and a.CHANNEL = b.channel(+) ";

    /**
     * 查询所有渠道分组
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ChannlModel> queryCurChannlForAll(BaseSaleRequest request, Date start, Date end) {
        StringBuilder sb = new StringBuilder(GET_CUR_CHANNLE_ALL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }else{
            sb.append(" and a.saledate <= '").append(DateUtils.parseDateToStr("yyyyMMdd",end))
                    .append("' ");
            sb.append(" and a.saledate >= '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
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
     * 查询大类单品毛利额排名
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ItemRankModel> queryCurRateItemRank(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_CUR_ITEM_RATE_RANK_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and p.saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }else{
            sb.append(" and p.saledate <= '").append(DateUtils.parseDateToStr("yyyyMMdd",end))
                    .append("' ");
            sb.append(" and p.saledate >= '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and p.AREA = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and p.AREA in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and p.REGION = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and p.REGION in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and P.STORE = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and P.STORE in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and p.dept = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and p.dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" GROUP BY P.ITEM,DEPT) WHERE RN<=10");
        return super.queryForList(sb.toString(),TOTAL_ITEM_SALE_RANK_RM,paramList.toArray());
    }

    /**
     * 查询大类单品销售排名
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ItemRankModel> queryCurSaleItemRank(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_CUR_ITEM_SALE_RANK_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and p.saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }else{
            sb.append(" and p.saledate <= '").append(DateUtils.parseDateToStr("yyyyMMdd",end))
                    .append("' ");
            sb.append(" and p.saledate >= '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and p.AREA = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and p.AREA in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and p.REGION = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and p.REGION in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and P.STORE = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and P.STORE in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and p.dept = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and p.dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" GROUP BY P.ITEM,DEPT) WHERE RN<=10");
        return super.queryForList(sb.toString(),TOTAL_ITEM_SALE_RANK_RM,paramList.toArray());
    }

    /**
     * 查询实时销售构成
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<SaleCompositionModel> querySaleComposition(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_CUR_SALE_COMPOSITION_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }else{
            sb.append(" and a.saledate <= '").append(DateUtils.parseDateToStr("yyyyMMdd",end))
                    .append("' ");
            sb.append(" and a.saledate >= '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.province_id = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.province_id in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and b.area_id = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and b.area_id in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and b.store_id = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store_id in ( ");
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
        sb.append(" group by a.retail_type");
        return super.queryForList(sb.toString(),TOTAL_SALE_COMPOSITION_RM,paramList.toArray());
    }

    /**
     * 查询实时销售趋势
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<SaleTrendModel> queryCurSaleTrendModel(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_CUR_SALETREND_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and sale_date = ").append(DateUtils.parseDateToStr("yyyyMMdd",start));
        }else{
            sb.append(" and sale_date <= ").append(DateUtils.parseDateToStr("yyyyMMdd",end));
            sb.append(" and sale_date >= ").append(DateUtils.parseDateToStr("yyyyMMdd",start));
        }

        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and region in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and store_id = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and store_id in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and dept_id = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and dept_id in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by sale_date");
        return super.queryForList(sb.toString(),TOTAL_SALE_TREND_RM,paramList.toArray());
    }

    /**
     * 查询历史销售趋势
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<SaleTrendModel> queryHisSaleTrendModel(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_SALETREND_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.sale_date = date'")
                    .append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and a.sale_date <= date'")
                    .append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.sale_date >= date'")
                    .append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.province_id = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.province_id in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and b.area_id = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and b.area_id in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and b.store_id = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store_id in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and a.dept_id = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and a.dept_id in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append("  group by a.sale_date");
        return super.queryForList(sb.toString(),TOTAL_SALE_TREND_RM,paramList.toArray());
    }

    /**
     * 查询实时渠道构成(支持大类，速度相对较慢)
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ChannlModel> queryCurChannl(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_CUR_CHANNLE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }else{
            sb.append(" and a.saledate <= '").append(DateUtils.parseDateToStr("yyyyMMdd",end))
                    .append("' ");
            sb.append(" and a.saledate >= '").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
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
     * 查询家电
     * @param request
     * @return
     */
    public HomeAppliance queryHomeApplianceInfo(BaseSaleRequest request,Date start,Date end){
        HomeAppliance homeAppliance = null;
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder(GET_HOME_PREFIX);

        if(null == end){
            sb.append(" and a.tran_Date = date '");
            sb.append(DateUtil.getDateFormat(start,"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and a.tran_Date <= date '");
            sb.append(DateUtil.getDateFormat(end,"yyyy-MM-dd")).append("' ");
            sb.append(" and a.tran_Date >= date '");
            sb.append(DateUtil.getDateFormat(start,"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.province_id = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){

                sb.append(" and b.province_id in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and b.area_id = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){

                sb.append(" and b.area_id in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and b.store_id = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){

                sb.append(" and b.store_id in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and a.dept  = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
//        log.info("====================sql:" + sb.toString());
//        og.info("====================sql:" + sb.toString());
        homeAppliance = super.queryForObject(sb.toString(), TOTAL_HOME_RM, paramList.toArray());
        return homeAppliance;
    }

    /**
     * 查询实时销售额和扫描毛利额
     * @return
     */
    public BaseSaleModel queryCurCommonBaseData(BaseSaleRequest request,Date start,Date end){
        StringBuilder sb = new StringBuilder(GET_CUR_BASE_SALE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and sale_date = ").append(DateUtils.parseDateToStr("yyyyMMdd",start));
        }else{
            sb.append(" and sale_date <= ").append(DateUtils.parseDateToStr("yyyyMMdd",end));
            sb.append(" and sale_date >= ").append(DateUtils.parseDateToStr("yyyyMMdd",start));
        }

        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and area = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and area in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and region = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and region in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and store_id = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and store_id in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and dept_id = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and dept_id in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        BaseSaleModel result = super.queryForObject(sb.toString(),TOTAL_BASE_SALE_RM,paramList.toArray());
        return result;
    }

    /**
     * 查询历史销售额，前台毛利额和成本
     * @return
     */
    public BaseSaleModel queryHisCommonBaseData(BaseSaleRequest request,Date start,Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_BASE_SALE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.sale_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
            .append("' ");
        }else{
            sb.append(" and a.sale_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.sale_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }

        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.province_id = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.province_id in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and b.area_id = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and b.area_id in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and b.store_id = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store_id in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and a.dept_id = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and a.dept_id in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        BaseSaleModel result = super.queryForObject(sb.toString(),TOTAL_BASE_SALE_RM,paramList.toArray());
        return result;
    }
}
