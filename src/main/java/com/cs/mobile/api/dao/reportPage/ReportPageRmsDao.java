package com.cs.mobile.api.dao.reportPage;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.freshreport.TurnoverDay;
import com.cs.mobile.api.model.reportPage.*;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
import com.cs.mobile.api.model.reportPage.request.RankParamRequest;
import com.cs.mobile.api.model.reportPage.request.ReportParamRequest;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 查询生鲜模块数据和周转天数和库存金额
 * 对应rms_dg库
 */
//@Slf4j
@Repository
public class ReportPageRmsDao extends AbstractDao {
    private static final RowMapper<Fresh> TOTAL_FRESH_RM = new BeanPropertyRowMapper<Fresh>(Fresh.class);
    private static final RowMapper<TotalSalesAndProfit> TOTAL_SALE_AND_PROFIT_RM = new BeanPropertyRowMapper<TotalSalesAndProfit>(TotalSalesAndProfit.class);
    private static final RowMapper<ReportDetail> REPORT_DETAIL_RM = new BeanPropertyRowMapper<ReportDetail>(ReportDetail.class);
    private static final RowMapper<RankDetail> RANK_DETAIL_RM = new BeanPropertyRowMapper<RankDetail>(RankDetail.class);
    private static final RowMapper<TurnoverDay> TURNOVERDAY_RM = new BeanPropertyRowMapper<TurnoverDay>(TurnoverDay.class);
    private static final RowMapper<CategoryKlModel> CATEGORYKL_RM = new BeanPropertyRowMapper<CategoryKlModel>(CategoryKlModel.class);
    private static final String GET_FRESH_HIS_PREFIX = "SELECT /*+PARALLEL(8)*/ " +
            " nvl(sum(sale_value),0) as totalSales," +
            " nvl(sum(invadj_cost),0) as lossPrice," +
            " nvl(sum(sale_value-sale_cost+invadj_cost-wac+fund_amount),0) as totalProfitPrice, " +
            " nvl(sum(sale_value_in),0) as totalSalesIn," +
            " nvl(sum(invadj_cost_in),0) as lossPriceIn," +
            " nvl(sum(sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in),0) as totalProfitPriceIn " +
            " FROM rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_STOCK_PREFIX = " select /*+PARALLEL(A,8)*/ nvl(sum(soh_amt),0) as stockPrice ,nvl((sum(soh_amt) / sum(total_cost)),0) as turnoverDays " +
            "    from cmx.BIP_CHYTB_INV_DAYS_TRUNOVER a, rms.v_bi_inf_store v " +
            "   where a.loc = v.store(+) ";

    private static final String GET_SALE_PREFIX = "select /*+PARALLEL(8)*/ " +
            " nvl(sum(sale_value),0) as totalSales, " +
            " nvl(sum(sale_value-sale_cost+invadj_cost-wac+fund_amount),0) as totalFrontDeskRate, " +
            " nvl(sum(sale_cost),0) as totalCost, " +
            " nvl(sum(case when mall_name = 1 then sale_value-sale_cost+invadj_cost-wac+fund_amount end),0) as totalCompareFrontDeskRate, " +
            " nvl(sum(case when mall_name = 1 then sale_value end),0) as totalCompareSale, " +
            " nvl(sum(case when mall_name = 1 then sale_cost end),0) as totalCompareCost, " +
            " nvl(sum(sale_value_in),0) as totalSalesIn, " +
            " nvl(sum(sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in),0) as totalFrontDeskRateIn, " +
            " nvl(sum(sale_cost_in),0) as totalCostIn, " +
            " nvl(sum(case when mall_name = 1 then sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in end),0) as totalCompareFrontDeskRateIn, " +
            " nvl(sum(case when mall_name = 1 then sale_value_in end),0) as totalCompareSaleIn, " +
            " nvl(sum(case when mall_name = 1 then sale_cost_in end),0) as totalCompareCostIn " +
            " from v_cmx_daily_gp where 1=1 ";

    private static final String GET_SALE_PREFIX_STORE_DEPT = "select /*+PARALLEL(8)*/ " +
            " store as storeId, " +
            " dept as deptId, " +
            " max(store_name) as storeName, " +
            " nvl(sum(sale_value),0) as totalSales, " +
            " nvl(sum(sale_value-sale_cost+invadj_cost-wac+fund_amount),0) as totalFrontDeskRate, " +
            " nvl(sum(sale_cost),0) as totalCost, " +
            " nvl(sum(case when mall_name = 1 then sale_value-sale_cost+invadj_cost-wac+fund_amount end),0) as totalCompareFrontDeskRate, " +
            " nvl(sum(case when mall_name = 1 then sale_value end),0) as totalCompareSales, " +
            " nvl(sum(case when mall_name = 1 then sale_cost end),0) as totalCompareCost, " +
            " nvl(sum(sale_value_in),0) as totalSalesIn, " +
            " nvl(sum(sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in),0) as totalFrontDeskRateIn, " +
            " nvl(sum(sale_cost_in),0) as totalCostIn, " +
            " nvl(sum(case when mall_name = 1 then sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in end),0) as totalCompareFrontDeskRateIn, " +
            " nvl(sum(case when mall_name = 1 then sale_value_in end),0) as totalCompareSalesIn, " +
            " nvl(sum(case when mall_name = 1 then sale_cost_in end),0) as totalCompareCostIn " +
            " from v_cmx_daily_gp where 1=1 ";

    private static final String GET_SALE_PREFIX_DAY = "select /*+PARALLEL(8)*/ " +
            " to_char(saledate,'dd') as timePoint, " +
            " to_char(saledate,'yyyyMMdd') as time, " +
            " nvl(sum(sale_value),0) as totalSales, " +
            " nvl(sum(sale_value-sale_cost+invadj_cost-wac+fund_amount),0) as totalFrontDeskRate, " +
            " nvl(sum(sale_cost),0) as totalCost, " +
            " nvl(sum(sale_value_in),0) as totalSalesIn, " +
            " nvl(sum(sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in),0) as totalFrontDeskRateIn, " +
            " nvl(sum(sale_cost_in),0) as totalCostIn " +
            " from v_cmx_daily_gp where 1=1 ";

    private static final String GET_SALE_PREFIX_MONTH = "select /*+PARALLEL(8)*/ " +
            " to_char(saledate,'MM') as timePoint, " +
            " nvl(sum(sale_value),0) as totalSales, " +
            " nvl(sum(sale_value-sale_cost+invadj_cost-wac+fund_amount),0) as totalFrontDeskRate, " +
            " nvl(sum(sale_cost),0) as totalCost, " +
            " nvl(sum(sale_value_in),0) as totalSalesIn, " +
            " nvl(sum(sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in),0) as totalFrontDeskRateIn, " +
            " nvl(sum(sale_cost_in),0) as totalCostIn " +
            " from v_cmx_daily_gp where 1=1 ";

    private static final String GET_SALE_PREFIX_STORE = "select /*+PARALLEL(8)*/ " +
            " store as storeId, " +
            " max(store_name) as storeName, " +
            " nvl(sum(sale_value),0) as totalSales, " +
            " nvl(sum(sale_value-sale_cost+invadj_cost-wac+fund_amount),0) as totalFrontDeskRate, " +
            " sum(case when mall_name = 1 then sale_value end) as totalCompareSales, " +
            " sum(case when mall_name = 1 then sale_value - sale_cost + invadj_cost - wac + fund_amount end) as totalCompareFrontDeskRate, " +
            " sum(case when mall_name = 1 then sale_cost end) as totalCompareCost," +
            " nvl(sum(sale_cost),0) as totalCost, " +

            " nvl(sum(sale_value_in),0) as totalSalesIn, " +
            " nvl(sum(sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in),0) as totalFrontDeskRateIn, " +
            " sum(case when mall_name = 1 then sale_value_in end) as totalCompareSalesIn, " +
            " sum(case when mall_name = 1 then sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in end) as totalCompareFrontDeskRateIn, " +
            " sum(case when mall_name = 1 then sale_cost_in end) as totalCompareCostIn," +
            " nvl(sum(sale_cost_in),0) as totalCostIn " +
            " from v_cmx_daily_gp where 1=1 ";

    private static final String GET_SALE_PREFIX_DEPT = "select /*+PARALLEL(8)*/ " +
            " dept as deptId, " +
            " nvl(sum(sale_value),0) as totalSales, " +
            " nvl(sum(sale_value-sale_cost+invadj_cost-wac+fund_amount),0) as totalFrontDeskRate, " +
            " sum(case when mall_name = 1 then sale_value end) as totalCompareSales, " +
            " sum(case when mall_name = 1 then sale_value - sale_cost + invadj_cost - wac + fund_amount end) as totalCompareFrontDeskRate, " +
            " sum(case when mall_name = 1 then sale_cost end) as totalCompareCost," +
            " nvl(sum(sale_cost),0) as totalCost, " +
            " nvl(sum(sale_value_in),0) as totalSalesIn, " +
            " nvl(sum(sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in),0) as totalFrontDeskRateIn, " +
            " sum(case when mall_name = 1 then sale_value_in end) as totalCompareSalesIn, " +
            " sum(case when mall_name = 1 then sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in end) as totalCompareFrontDeskRateIn, " +
            " sum(case when mall_name = 1 then sale_cost_in end) as totalCompareCostIn," +
            " nvl(sum(sale_cost_in),0) as totalCostIn " +
            " from v_cmx_daily_gp where 1=1 ";

    private static final String GET_HIS_FRESH_ACTUAL_TURNOVERDAYS_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            "   nvl(sum(soh_amt),0 ) as sale, " +
            "   nvl(sum(total_cost),0)  as cost " +
            "  from cmx.BIP_CHYTB_INV_DAYS_TRUNOVER a, rms.v_bi_inf_store v " +
            " where a.loc = v.store(+) ";

    private static final String GET_HIS_CATEGORY_KL_PREFIX = "select  " +
            "  a.cate,  " +
            "  sum(day_store_kl) as kl " +
            "  from cmx.cmx_realtime_kl_hist a, rms.v_bi_inf_store b " +
            "  where 1 = 1 " +
            "  and a.store = b.store ";

    /**
     * 根据品类查询客流
     * @param rankParamRequest
     * @param categorys
     * @return
     */
    public List<CategoryKlModel> queryHisCategoryKl(RankParamRequest rankParamRequest,String categorys,Date start,Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_CATEGORY_KL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.business_date = date '")
                    .append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and a.business_date <= date '")
                    .append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and a.business_date >= date '")
                    .append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != rankParamRequest.getProvinceIds() && rankParamRequest.getProvinceIds().size() > 0){
            if(rankParamRequest.getProvinceIds().size() == 1){
                sb.append(" and b.AREA = ? ");
                paramList.add(rankParamRequest.getProvinceIds().get(0));
            }else if(rankParamRequest.getProvinceIds().size() > 1){

                sb.append(" and b.AREA in ( ");
                sb.append(rankParamRequest.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != rankParamRequest.getAreaIds() && rankParamRequest.getAreaIds().size() > 0){
            if(rankParamRequest.getAreaIds().size() == 1){
                sb.append(" and b.REGION = ? ");
                paramList.add(rankParamRequest.getAreaIds().get(0));
            }else if(rankParamRequest.getAreaIds().size() > 1){

                sb.append(" and b.REGION in ( ");
                sb.append(rankParamRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != rankParamRequest.getStoreIds() && rankParamRequest.getStoreIds().size() > 0){
            if(rankParamRequest.getStoreIds().size() == 1){
                sb.append(" and b.STORE = ? ");
                paramList.add(rankParamRequest.getStoreIds().get(0));
            }else if(rankParamRequest.getStoreIds().size() > 1){
                sb.append(" and b.STORE in ( ");
                sb.append(rankParamRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        if(StringUtils.isNotEmpty(categorys)){
            sb.append(" and a.cate in (").append(categorys).append(") ");
        }
        sb.append(" group by a.cate ");
        return super.queryForList(sb.toString(),CATEGORYKL_RM,paramList.toArray());
    }

    public TurnoverDay queryActualTurnoverDay(PageRequest param,Date start,Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_FRESH_ACTUAL_TURNOVERDAYS_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.vdate = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }else{
            sb.append(" and a.vdate <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
            sb.append(" and a.vdate >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and v.AREA = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and v.AREA in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }
        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and v.REGION = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){

                sb.append(" and v.REGION in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }
        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and v.STORE = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and v.STORE in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }
        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept  = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        return super.queryForObject(sb.toString(),TURNOVERDAY_RM,paramList.toArray());
    }

    /**
     * 查询历史生鲜(1-生鲜，2-非生鲜)
     * @param pageRequest
     * @return
     */
    public Fresh queryHisFreshInfo(PageRequest pageRequest, String fresh, int type){
        List<Object> paramList = new ArrayList<Object>();
        String sql = getFreshHisSql(pageRequest, paramList, fresh, type);
        Fresh freshResponse = super.queryForObject(sql, TOTAL_FRESH_RM, paramList.toArray());
        /*if(null != freshResponse){
            BigDecimal sales = new BigDecimal(StringUtils.isEmpty(freshResponse.getTotalSales())? "0" : freshResponse.getTotalSales());
            BigDecimal loss = new BigDecimal(StringUtils.isEmpty(freshResponse.getLossPrice()) ? "0" : freshResponse.getLossPrice());
            BigDecimal profitPrice = new BigDecimal(StringUtils.isEmpty(freshResponse.getTotalProfitPrice()) ? "0" : freshResponse.getTotalProfitPrice());
            BigDecimal salesIn = new BigDecimal(StringUtils.isEmpty(freshResponse.getTotalSalesIn()) ? "0" : freshResponse.getTotalSalesIn());
            BigDecimal lossIn = new BigDecimal(StringUtils.isEmpty(freshResponse.getLossPriceIn()) ? "0" : freshResponse.getLossPriceIn());
            BigDecimal profitPriceIn = new BigDecimal(StringUtils.isEmpty(freshResponse.getTotalProfitPriceIn()) ? "0" : freshResponse.getTotalProfitPriceIn());
            if(sales.compareTo(BigDecimal.ZERO)!=0){
                freshResponse.setTotalProfit(profitPrice.divide(sales,4, BigDecimal.ROUND_HALF_UP).toString());
                freshResponse.setLossRate(loss.divide(sales,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(salesIn.compareTo(BigDecimal.ZERO)!=0){
                freshResponse.setLossRateIn(lossIn.divide(salesIn,4, BigDecimal.ROUND_HALF_UP).toString());
                freshResponse.setTotalProfitIn(profitPriceIn.divide(salesIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
        }*/

        return freshResponse;
    }

    public TotalSalesAndProfit getStockPriceAndDay(PageRequest pageRequest){
        TotalSalesAndProfit totalSalesAndProfit = new TotalSalesAndProfit();
        List<Object> paramMoneyList = new ArrayList<>();

        String StockMoneySql = getStockMoneySql(pageRequest, paramMoneyList);

        TotalSalesAndProfit money = queryInfo(StockMoneySql, paramMoneyList);
        if(null != money){
            totalSalesAndProfit.setStockPrice(money.getStockPrice());
        }
        return totalSalesAndProfit;
    }

    /**
     * 库存周转金额sql
     * @param pageRequest
     * @param paramList
     * @return
     */
    public String getStockMoneySql(PageRequest pageRequest, List<Object> paramList){
        StringBuilder sb = new StringBuilder();
        sb.append(GET_STOCK_PREFIX);

        if(DateUtils.getBetweenDay(pageRequest.getStart(), new Date()) >= 0){
            //查历史的某一天数据
            sb.append(" and a.vdate = date '");
            sb.append(DateUtil.getDateFormat(DateUtils.addDays(pageRequest.getStart(),-1),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and a.vdate = date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and v.area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){

                sb.append(" and v.area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");

            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and v.region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){

                sb.append(" and v.region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and v.store  = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){

                sb.append(" and v.store in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and a.dept  = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" having sum(a.total_cost)>0");
        return sb.toString();
    }

    /**
     * 库存周转天数sql
     * @param pageRequest
     * @param paramList
     * @return
     */
    public String getStockSql(PageRequest pageRequest, List<Object> paramList){
        StringBuilder sb = new StringBuilder();
        sb.append(GET_STOCK_PREFIX);
        if(null == pageRequest.getEnd()){
            //月初到昨日
            if(DateUtils.getBetweenDay(pageRequest.getStart(), new Date()) >= 0){
                sb.append(" and a.vdate <= date '");
                sb.append(DateUtil.getDateFormat(DateUtils.addDays(pageRequest.getStart(), -1),"yyyy-MM-dd")).append("' ");
                sb.append(" and a.vdate >= date '");
                sb.append(DateUtil.getDateFormat(DateUtils.dateTime(DateUtils.YYYY_MM_DD, DateUtil.getTheFirstDayOfCurrentMonth()),"yyyy-MM-dd")).append("' ");
            }else{
                sb.append(" and a.vdate <= date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
                sb.append(" and a.vdate >= date '");
                sb.append(DateUtil.getDateFormat(DateUtils.dateTime(DateUtils.YYYY_MM_DD, DateUtil.getTheFirstDayOfCurrentMonth()),"yyyy-MM-dd")).append("' ");
            }
        }else{
            //查历史的某一段数据
            sb.append(" and a.vdate <= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and a.vdate >= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and v.area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){

                sb.append(" and v.area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");

            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and v.region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){

                sb.append(" and v.region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and v.store  = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){

                sb.append(" and v.store in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and a.dept  = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" having sum(a.total_cost)>0");
        return sb.toString();
    }

    private TotalSalesAndProfit queryInfo(String sql, List<Object> paramList){
        return super.queryForObject(sql, TOTAL_SALE_AND_PROFIT_RM, paramList.toArray());
    }

    /**
     * 历史生鲜sql(1-生鲜，2-非生鲜)
     * @param pageRequest
     * @param paramList
     * @return
     */
    private String getFreshHisSql(PageRequest pageRequest, List<Object> paramList,String fresh, int type){
        StringBuilder sb = new StringBuilder(GET_FRESH_HIS_PREFIX);
        if(null == pageRequest.getEnd()){
            sb.append(" and saledate = date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and saledate <= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and saledate >= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){
                sb.append(" and area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){

                sb.append(" and region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and store = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and store in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and dept  = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){
                sb.append(" and dept in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }

        //拼接not生鲜大类
        if(2 == type){
            sb.append(" and dept not in ( ");
            sb.append(fresh);
            sb.append(" ) ");

        }
        return sb.toString();
    }

    /**
     * 查询排行榜同期分组数据(4-门店，5-大类)
     * @param pageRequest
     * @return
     */
    public List<RankDetail> queryHisRankDetail(PageRequest pageRequest, int type){
        List<RankDetail> result = null;
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getCommenProfixSql(pageRequest, paramList, type));
        sb.append(getCommenSql(pageRequest, paramList, "", type));
        sb.append(getCommenGroupSql(type));
        result = super.queryForList(sb.toString(), RANK_DETAIL_RM, paramList.toArray());
        return result;
    }

    /**
     * 历史数据前缀(2-日线,3-月线,4-门店,5-大类,6-门店和大类分组，其它默认查询汇总)
     * @param pageRequest
     * @return
     */
    public String getCommenProfixSql(PageRequest pageRequest, List<Object> paramList, Integer type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 2:
                sb = sb.append(GET_SALE_PREFIX_DAY);
                break;
            case 3:
                sb = sb.append(GET_SALE_PREFIX_MONTH);
                break;
            case 4:
                sb = sb.append(GET_SALE_PREFIX_STORE);
                break;
            case 5:
                sb = sb.append(GET_SALE_PREFIX_DEPT);
                break;
            case 6:
                sb = sb.append(GET_SALE_PREFIX_STORE_DEPT);
                break;
            default:
                sb = sb.append(GET_SALE_PREFIX);
                break;
        }
        if(null == pageRequest.getEnd()){
            sb.append(" and saledate = date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and saledate <= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and saledate >= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }
        return sb.toString();
    }

    /**
     * type默认为0，排除生鲜则type=-2
     * @param pageRequest
     * @param paramList
     * @param fresh
     * @param type
     * @return
     */
    private String getCommenSql(PageRequest pageRequest, List<Object> paramList, String fresh, int type){
        StringBuilder sb = new StringBuilder();
        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){
                sb.append(" and area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){
                sb.append(" and region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and store = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and store in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and dept  = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){
                sb.append(" and dept in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }

        //拼接not生鲜大类
        if(-2 == type){
            sb.append(" and dept not in ( ");
            sb.append(fresh);
            sb.append(" ) ");

        }

        return sb.toString();
    }

    /**
     * 历史增加可比拼接

     * @return
     */
    private String getCompareCommonSql(){
        return " and mall_name = 1 ";
    }

    /**
     * 类型(2-日线,3-月线,4-门店,5-大类)进行分组（历史）
     * @param type
     * @return
     */
    private String getCommenGroupSql(int type){
        String sb = "";
        switch (type){
            case 2:
                sb = " group by saledate ";
                break;
            case 3:
                sb = " group by to_char(saledate,'MM') ";
                break;
            case 4:
                sb = " group by store ";
                break;
            case 5:
                sb = " group by dept ";
                break;
            case 6:
                sb = " group by store, dept ";
                break;
            default:
                sb = "";
                break;
        }
        return sb;
    }

    private String getCommenOrderSql(int type){
        String sb = "";
        switch (type){
            case 2:
                sb = " order by saledate ";
                break;
            case 3:
                sb = " order by to_char(saledate,'MM') ";
                break;
            default:
                sb = "";
                break;
        }
        return sb;
    }

    public List<ReportDetail> queryHisReportDetail(ReportParamRequest param){
        List<ReportDetail> result = null;
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getCommenProfixSql(param, paramList, param.getType()));
        sb.append(getCommenSql(param, paramList, "",param.getType()));
        sb.append(getCommenGroupSql(param.getType()));
        sb.append(getCommenOrderSql(param.getType()));
        result = super.queryForList(sb.toString(), REPORT_DETAIL_RM, paramList.toArray());
        if(null != result){
            for(ReportDetail v : result){
                BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(v.getTotalSales())?"0":v.getTotalSales());
                BigDecimal frontDestRate = new BigDecimal(StringUtils.isEmpty(v.getTotalFrontDeskRate())?"0":v.getTotalFrontDeskRate());

                BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(v.getTotalSalesIn())?"0":v.getTotalSalesIn());
                BigDecimal frontDestRateIn = new BigDecimal(StringUtils.isEmpty(v.getTotalFrontDeskRateIn())?"0":v.getTotalFrontDeskRateIn());
                //历史数据使用前台毛利率
                if(totalSale.compareTo(BigDecimal.ZERO)!=0){
                    v.setTotalprofit((frontDestRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString()));
                }
                if(totalSaleIn.compareTo(BigDecimal.ZERO)!=0){
                    v.setTotalprofitIn((frontDestRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString()));
                }
            }
        }

        return result;
    }

    public TotalSalesAndProfit queryHisCommenData(PageRequest pageRequest){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        //历史数据
        sb.append(getCommenProfixSql(pageRequest, paramList, -1));
        sb.append(getCommenSql(pageRequest, paramList,"", 0));
        TotalSalesAndProfit totalSalesAndProfit = queryInfo(sb.toString(), paramList);
        if(null != totalSalesAndProfit){
            BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSales())?"0"
                    : totalSalesAndProfit.getTotalSales());
            BigDecimal frontDeskRate = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalFrontDeskRate())?"0"
                    : totalSalesAndProfit.getTotalFrontDeskRate());
            BigDecimal totalCost = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalCost())?"0"
                    : totalSalesAndProfit.getTotalCost());

            BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSalesIn())?"0"
                    : totalSalesAndProfit.getTotalSalesIn());
            BigDecimal frontDeskRateIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalFrontDeskRateIn())?"0"
                    : totalSalesAndProfit.getTotalFrontDeskRateIn());
            BigDecimal totalCostIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalCostIn())?"0"
                    : totalSalesAndProfit.getTotalCostIn());
            if(totalSale.compareTo(BigDecimal.ZERO)!=0){
                //前台毛利率
                totalSalesAndProfit.setFrontDeskProfitRate(frontDeskRate.
                        divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                //扫描毛利率
                totalSalesAndProfit.setScanningProfitRate((totalSale.subtract(totalCost)).
                        divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
            }

            if(totalSaleIn.compareTo(BigDecimal.ZERO)!=0){
                //前台毛利率
                totalSalesAndProfit.setFrontDeskProfitRateIn(frontDeskRateIn.
                        divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                //扫描毛利率
                totalSalesAndProfit.setScanningProfitRateIn((totalSaleIn.subtract(totalCostIn)).
                        divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            //扫描毛利额
            totalSalesAndProfit.setTotalRate(totalSale.subtract(totalCost).toString());
            totalSalesAndProfit.setTotalRateIn(totalSaleIn.subtract(totalCostIn).toString());
        }
        return totalSalesAndProfit;
    }

    public TotalSalesAndProfit queryHisCompareData(PageRequest pageRequest){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        //历史数据
        sb.append(getCommenProfixSql(pageRequest, paramList, -1));
        sb.append(getCommenSql(pageRequest, paramList,"", 0));
        sb.append(getCompareCommonSql());

        TotalSalesAndProfit totalSalesAndProfit = queryInfo(sb.toString(), paramList);
        if(null != totalSalesAndProfit){
            BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSales())?"0"
                    : totalSalesAndProfit.getTotalSales());
            BigDecimal totalCost = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalCost())?"0"
                    : totalSalesAndProfit.getTotalCost());

            BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSalesIn())?"0"
                    : totalSalesAndProfit.getTotalSalesIn());
            BigDecimal totalCostIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalCostIn())?"0"
                    : totalSalesAndProfit.getTotalCostIn());
            if(totalSale.compareTo(BigDecimal.ZERO)!=0){
                totalSalesAndProfit.setTotalProfit((totalSale.subtract(totalCost)).divide(totalSale,4,
                        BigDecimal.ROUND_HALF_UP).toString());
                totalSalesAndProfit.setScanningProfitRate((totalSale.subtract(totalCost)).divide(totalSale,4,
                        BigDecimal.ROUND_HALF_UP).toString());
            }

            if(totalSaleIn.compareTo(BigDecimal.ZERO)!=0){
                totalSalesAndProfit.setTotalProfitIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4,
                        BigDecimal.ROUND_HALF_UP).toString());
                totalSalesAndProfit.setScanningProfitRateIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4,
                        BigDecimal.ROUND_HALF_UP).toString());
            }
            totalSalesAndProfit.setTotalRate(totalSale.subtract(totalCost).toString());
            totalSalesAndProfit.setTotalRateIn(totalSaleIn.subtract(totalCostIn).toString());
        }
        return totalSalesAndProfit;
    }
}
