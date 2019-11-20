package com.cs.mobile.api.dao.salereport;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.salereport.BaseSaleModel;
import com.cs.mobile.api.model.salereport.GoalSale;
import com.cs.mobile.api.model.salereport.ItemRankModel;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtil;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//@Slf4j
@Repository
public class SaleReportRmsDao extends AbstractDao {
    private static final RowMapper<BaseSaleModel> TOTAL_BASE_SALE_RM =
            new BeanPropertyRowMapper<BaseSaleModel>(BaseSaleModel.class);
    private static final RowMapper<GoalSale> TOTAL_GOAL_SALE_RM =
            new BeanPropertyRowMapper<GoalSale>(GoalSale.class);
    private static final RowMapper<ItemRankModel> TOTAL_ITEM_SALE_RANK_RM = new BeanPropertyRowMapper<ItemRankModel>(ItemRankModel.class);

    private static final String GET_HIS_BASE_SALE_PREFIX = "SELECT /*+PARALLEL(8)*/  " +
            "            nvl(sum(sale_value), 0) as totalSale, " +
            "            nvl(sum(sale_cost), 0) as totalCost,  " +
            "            nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalFrontDeskRate,  " +
            "            nvl(sum(case when mall_name = 1 then sale_value end),0) as totalCompareSale,  " +
            "            nvl(sum(case when mall_name = 1 then sale_cost end),0) as totalCompareCost,  " +
            "            nvl(sum(case when mall_name = 1 then sale_value - sale_cost + invadj_cost - wac + fund_amount end),0) as totalCompareFrontDeskRate, " +
            "            nvl(sum(sale_value_in), 0) as totalSaleIn, " +
            "            nvl(sum(sale_cost_in), 0) as totalCostIn,  " +
            "            nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalFrontDeskRateIn,  " +
            "            nvl(sum(case when mall_name = 1 then sale_value_in end),0) as totalCompareSaleIn,  " +
            "            nvl(sum(case when mall_name = 1 then sale_cost_in end),0) as totalCompareCostIn,  " +
            "            nvl(sum(case when mall_name = 1 then sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in end),0) as totalCompareFrontDeskRateIn   " +
            "            from rms.v_cmx_daily_gp  " +
            "            where 1 = 1  ";

    private static final String GET_GOAL_SALE_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " sum(case when a.account_name = '销售收入' then a.amount*10000 end) as sale, " +
            " sum(case when a.account_name = '销售毛利额' then a.amount*10000 end) as rate " +
            " from cmx_hbl_cate_data_manual_imp a,rms.v_bi_inf_store b " +
            " where a.store = b.store ";

    private static final String GET_HIS_ITEM_SALE_RANK_PREFIX = "SELECT * FROM ( " +
            " SELECT /*+PARALLEL(8)*/ P.dept as deptId, " +
            "       P.ITEM as itemId, " +
            "       MAX(P.ITEM_DESC) as itemName, " +
            "       SUM(P.SALE_VALUE) totalSale, " +
            "       sum(case when mall_name = 1 then P.SALE_VALUE end) as totalCompareSale, " +
            "       SUM(P.SALE_VALUE_in) totalSaleIn, " +
            "       sum(case when mall_name = 1 then P.SALE_VALUE_in end) as totalCompareSaleIn, " +
            "       ROW_NUMBER() OVER(PARTITION BY DEPT ORDER BY SUM(P.SALE_VALUE) DESC) RN " +
            "  FROM V_CMX_DAILY_GP P " +
            " WHERE 1=1 ";

    private static final String GET_HIS_ITEM_RATE_RANK_PREFIX = "SELECT * FROM ( " +
            " SELECT /*+PARALLEL(8)*/ P.dept as deptId, " +
            "       P.ITEM as itemId, " +
            "       MAX(P.ITEM_DESC) as itemName, " +
            "       SUM(P.SALE_VALUE)-SUM(P.SALE_COST) as totalScanningRate, " +
            "       nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalFrontDeskRate, " +
            "       sum(case when mall_name = 1 then sale_value - sale_cost + invadj_cost - wac + fund_amount end) as totalCompareFrontDeskRate, " +
            "       SUM(P.SALE_VALUE_in)-SUM(P.SALE_COST_in) as totalScanningRateIn, " +
            "       nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalFrontDeskRateIn, " +
            "       sum(case when mall_name = 1 then sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in end) as totalCompareFrontDeskRateIn, " +
            "       ROW_NUMBER() OVER(PARTITION BY DEPT ORDER BY SUM(P.SALE_VALUE)-SUM(P.SALE_COST) DESC) RN " +
            "  FROM V_CMX_DAILY_GP P " +
            " WHERE 1=1 ";

    private static final String GET_HIS_COMPARE_ITEM_RATE_RANK_PREFIX = "SELECT /*+PARALLEL(8)*/ P.dept as deptId, " +
            "       P.ITEM as itemId, " +
            "       SUM(P.SALE_VALUE) as totalSale, " +
            "       SUM(P.SALE_VALUE)-SUM(P.SALE_COST) as totalScanningRate, " +
            "       nvl(sum(P.sale_value - P.sale_cost + P.invadj_cost - P.wac + P.fund_amount), 0) as totalFrontDeskRate,  " +
            "       sum(case when p.mall_name =1 then P.SALE_VALUE end) as totalCompareSale, " +
            "       sum(case when p.mall_name =1 then P.SALE_VALUE - P.SALE_COST end) as totalCompareScanningRate, " +
            "       sum(case when p.mall_name =1 then P.sale_value - P.sale_cost + P.invadj_cost - P.wac + P.fund_amount end) as totalCompareFrontDeskRate, " +
            "       SUM(P.SALE_VALUE_in) as totalSaleIn, " +
            "       SUM(P.SALE_VALUE)-SUM(P.SALE_COST_in) as totalScanningRateIn, " +
            "       nvl(sum(P.sale_value_in - P.sale_cost_in + P.invadj_cost_in - P.wac_in + P.fund_amount_in), 0) as totalFrontDeskRateIn,  " +
            "       sum(case when p.mall_name =1 then P.SALE_VALUE_in end) as totalCompareSaleIn, " +
            "       sum(case when p.mall_name =1 then P.SALE_VALUE_in - P.SALE_COST_in end) as totalCompareScanningRateIn, " +
            "       sum(case when p.mall_name =1 then P.sale_value_in - P.sale_cost_in + P.invadj_cost_in - P.wac_in + P.fund_amount_in end) as totalCompareFrontDeskRateIn " +
            "  FROM V_CMX_DAILY_GP P " +
            "  WHERE 1=1 ";

    /**
     * 查询历史和可比大类单品毛利额排行榜
     * @param request
     * @param items
     * @param start
     * @param end
     * @return
     */
    public List<ItemRankModel> queryHisAndCompareRateItemRank(BaseSaleRequest request,String depts,String items, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_COMPARE_ITEM_RATE_RANK_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and p.saledate = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and p.saledate <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and p.saledate >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
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
        /*if(StringUtils.isNotEmpty(depts)){
            sb.append(" and p.dept in ( ");
            sb.append(depts);
            sb.append(" ) ");
        }*/

        //拼接单品
        if(StringUtils.isNotEmpty(items)){
            sb.append(" and p.ITEM in ( ");
            sb.append(items);
            sb.append(" ) ");
        }
        sb.append(" GROUP BY P.ITEM,DEPT");
//        log.info("==============sql:" + sb.toString());
        return super.queryForList(sb.toString(),TOTAL_ITEM_SALE_RANK_RM,paramList.toArray());
    }

    /**
     * 查询历史大类单品毛利额排行榜
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ItemRankModel> queryHisRateItemRank(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_ITEM_RATE_RANK_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and p.saledate = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and p.saledate <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and p.saledate >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
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
     * 查询历史大类单品销售排行榜
     * @param request
     * @param start
     * @param end
     * @return
     */
    public List<ItemRankModel> queryHisSaleItemRank(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_ITEM_SALE_RANK_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and p.saledate = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and p.saledate <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and p.saledate >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
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
     * 查询历史可比/同比销售，可比/同比成本，可比/同比前台毛利额
     * @param request
     * @param start
     * @param end
     * @return
     */
    public BaseSaleModel queryHisCommonCompareBaseData(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_BASE_SALE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and saledate = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and saledate <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and saledate >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
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
                sb.append(" and store = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and dept = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        BaseSaleModel result = super.queryForObject(sb.toString(),TOTAL_BASE_SALE_RM,paramList.toArray());
        return result;
    }

    /**
     * 查询销售和毛利额目标
     * @param request
     * @param start
     * @param end
     * @return
     */
    public GoalSale queryGoalSale(BaseSaleRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_GOAL_SALE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.year = '")
                    .append(DateUtils.parseDateToStr("yyyy",start))
                    .append("' ");
            sb.append(" and a.month = '")
                    .append(DateUtils.parseDateToStr("MM",start))
                    .append("' ");
        }else{
            sb.append(" and ((a.year = '")
                    .append(DateUtils.parseDateToStr("yyyy",start))
                    .append("' ");
            sb.append(" and a.month = '")
                    .append(DateUtils.parseDateToStr("MM",start))
                    .append("') ");

            sb.append(" or (a.year = '")
                    .append(DateUtils.parseDateToStr("yyyy",end))
                    .append("' ");
            sb.append(" and a.month = '")
                    .append(DateUtils.parseDateToStr("MM",end))
                    .append("')) ");
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
                sb.append(" and b.REGION = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and b.REGION in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and b.STORE = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.STORE in ( ");
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
        GoalSale result = super.queryForObject(sb.toString(),TOTAL_GOAL_SALE_RM,paramList.toArray());
        return result;
    }
}
