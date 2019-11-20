package com.cs.mobile.api.dao.freshreport;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.freshreport.*;
import com.cs.mobile.api.model.freshreport.request.FreshRankRequest;
import com.cs.mobile.api.model.freshreport.request.FreshReportBaseRequest;
import com.cs.mobile.api.model.salereport.GoalSale;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;
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
public class FreshReportRmsDao extends AbstractDao {
    private static final RowMapper<MonthStatistics> TOTAL_FRESH_RM = new BeanPropertyRowMapper<MonthStatistics>(MonthStatistics.class);
    private static final RowMapper<SalesList> TOTAL_FRESH_SALES_RM = new BeanPropertyRowMapper<SalesList>(SalesList.class);
    private static final RowMapper<FreshRankInfo> TOTAL_FRESH_PROVINCE_RM = new BeanPropertyRowMapper<FreshRankInfo>(FreshRankInfo.class);
    private static final RowMapper<HblTurnoverDay> TOTAL_HIS_FRESH_DEPT_ACTUAL_TURNOVERDAYS_RM = new BeanPropertyRowMapper<HblTurnoverDay>(HblTurnoverDay.class);
    private static final RowMapper<GoalSale> TOTAL_GOAL_SALE_RM =
            new BeanPropertyRowMapper<GoalSale>(GoalSale.class);
    private static final RowMapper<FreshRankKlModel> TOTAL_FRESHRANKKL_RM = new BeanPropertyRowMapper<FreshRankKlModel>(FreshRankKlModel.class);
    private static final String GET_FRESH_HIS_PREFIX = "SELECT /*+PARALLEL(8)*/ " +
            " nvl(sum(sale_value),0) as totalSales," +
            " nvl(sum(invadj_cost),0) as lossPrice," +
            " nvl(sum(sale_value-sale_cost+invadj_cost-wac+fund_amount),0) as totalProfitPrice, " +
            " nvl(sum(sale_value_in),0) as totalSalesIn," +
            " nvl(sum(invadj_cost_in),0) as lossPriceIn," +
            " nvl(sum(sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in),0) as totalProfitPriceIn " +
            " FROM rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_FRESH_HIS_PREFIX_TIME = "SELECT /*+PARALLEL(8)*/  " +
            "             to_char(saledate,'yyyyMMdd') as time, " +
            "             nvl(sum(sale_value),0) as totalSales, " +
            "             nvl(sum(invadj_cost),0) as lossPrice, " +
            "             nvl(sum(sale_value-sale_cost+invadj_cost-wac+fund_amount),0) as totalProfitPrice,  " +
            " nvl(sum(sale_value_in),0) as totalSalesIn," +
            " nvl(sum(invadj_cost_in),0) as lossPriceIn," +
            " nvl(sum(sale_value_in-sale_cost_in+invadj_cost_in-wac_in+fund_amount_in),0) as totalProfitPriceIn " +
            "             FROM rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_FRESH_HIS_PREFIX_PROVINCE_COMPARE = "SELECT /*+PARALLEL(8)*/  " +
            "              nvl(sum(sale_value), 0) as totalSales, " +
            "              nvl(sum(invadj_cost), 0) as lossPrice,  " +
            "              (sum(sale_value) - sum(sale_cost)) as totalScanningProfitPrice,  " +
            "              nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalProfitPrice,  " +

            "              nvl(sum(sale_value_in), 0) as totalSalesIn, " +
            "              nvl(sum(invadj_cost_in), 0) as lossPriceIn,  " +
            "              (sum(sale_value_in) - sum(sale_cost_in)) as totalScanningProfitPriceIn,  " +
            "              nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalProfitPriceIn,  " +
            "              mall_name as compareMark,  " +
            "              area as id " +
            "              from rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_FRESH_HIS_PREFIX_REGION_COMPARE = "SELECT /*+PARALLEL(8)*/  " +
            "              nvl(sum(sale_value), 0) as totalSales, " +
            "              nvl(sum(invadj_cost), 0) as lossPrice,  " +
            "              (sum(sale_value) - sum(sale_cost)) as totalScanningProfitPrice,  " +
            "              nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalProfitPrice,  " +

            "              nvl(sum(sale_value_in), 0) as totalSalesIn, " +
            "              nvl(sum(invadj_cost_in), 0) as lossPriceIn,  " +
            "              (sum(sale_value_in) - sum(sale_cost_in)) as totalScanningProfitPriceIn,  " +
            "              nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalProfitPriceIn,  " +
            "              mall_name as compareMark,  " +
            "              region as id " +
            "              from rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_FRESH_HIS_PREFIX_STORE_COMPARE = "SELECT /*+PARALLEL(8)*/  " +
            "              nvl(sum(sale_value), 0) as totalSales, " +
            "              nvl(sum(invadj_cost), 0) as lossPrice,  " +
            "              (sum(sale_value) - sum(sale_cost)) as totalScanningProfitPrice,  " +
            "              nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalProfitPrice,  " +

            "              nvl(sum(sale_value_in), 0) as totalSalesIn, " +
            "              nvl(sum(invadj_cost_in), 0) as lossPriceIn,  " +
            "              (sum(sale_value_in) - sum(sale_cost_in)) as totalScanningProfitPriceIn,  " +
            "              nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalProfitPriceIn,  " +

            "              mall_name as compareMark,  " +
            "              store as id " +
            "              from rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_FRESH_HIS_PREFIX_DEPT_COMPARE = "SELECT /*+PARALLEL(8)*/  " +
            "              nvl(sum(sale_value), 0) as totalSales, " +
            "              nvl(sum(invadj_cost), 0) as lossPrice,  " +
            "              (sum(sale_value) - sum(sale_cost)) as totalScanningProfitPrice,  " +
            "              nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalProfitPrice,  " +

            "              nvl(sum(sale_value_in), 0) as totalSalesIn, " +
            "              nvl(sum(invadj_cost_in), 0) as lossPriceIn,  " +
            "              (sum(sale_value_in) - sum(sale_cost_in)) as totalScanningProfitPriceIn,  " +
            "              nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalProfitPriceIn,  " +
            "              mall_name as compareMark,  " +
            "              dept as id " +
            "              from rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_FRESH_HIS_PREFIX_PROVINCE = "SELECT /*+PARALLEL(8)*/  " +
            "              nvl(sum(sale_value), 0) as totalSales, " +
            "              nvl(sum(invadj_cost), 0) as lossPrice,  " +
            "              nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalProfitPrice,  " +

            "              nvl(sum(sale_value_in), 0) as totalSalesIn, " +
            "              nvl(sum(invadj_cost_in), 0) as lossPriceIn,  " +
            "              nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalProfitPriceIn,  " +

            "              area as id " +
            "              from rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_FRESH_HIS_PREFIX_REGION = "SELECT /*+PARALLEL(8)*/  " +
            "              nvl(sum(sale_value), 0) as totalSales, " +
            "              nvl(sum(invadj_cost), 0) as lossPrice,  " +
            "              nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalProfitPrice,  " +

            "              nvl(sum(sale_value_in), 0) as totalSalesIn, " +
            "              nvl(sum(invadj_cost_in), 0) as lossPriceIn,  " +
            "              nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalProfitPriceIn,  " +

            "              region as id " +
            "              from rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_FRESH_HIS_PREFIX_STORE = "SELECT /*+PARALLEL(8)*/  " +
            "              nvl(sum(sale_value), 0) as totalSales, " +
            "              nvl(sum(invadj_cost), 0) as lossPrice,  " +
            "              nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalProfitPrice,  " +

            "              nvl(sum(sale_value_in), 0) as totalSalesIn, " +
            "              nvl(sum(invadj_cost_in), 0) as lossPriceIn,  " +
            "              nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalProfitPriceIn,  " +

            "              store as id " +
            "              from rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_FRESH_HIS_PREFIX_DEPT = "SELECT /*+PARALLEL(8)*/  " +
            "              nvl(sum(sale_value), 0) as totalSales, " +
            "              nvl(sum(invadj_cost), 0) as lossPrice,  " +
            "              nvl(sum(sale_value - sale_cost + invadj_cost - wac + fund_amount), 0) as totalProfitPrice,  " +

            "              nvl(sum(sale_value_in), 0) as totalSalesIn, " +
            "              nvl(sum(invadj_cost_in), 0) as lossPriceIn,  " +
            "              nvl(sum(sale_value_in - sale_cost_in + invadj_cost_in - wac_in + fund_amount_in), 0) as totalProfitPriceIn,  " +

            "              dept as id " +
            "              from rms.v_cmx_daily_gp where 1=1 ";

    private static final String GET_HIS_FRESH_DEPT_ACTUAL_TURNOVERDAYS_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            "  a.dept as deptId, " +
            "   (sum(soh_amt) / sum(total_cost)) as turnoverDays " +
            "  from cmx.BIP_CHYTB_INV_DAYS_TRUNOVER a, rms.v_bi_inf_store v " +
            "  where a.loc = v.store(+) ";

    private static final String GET_GOAL_SALE_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " sum(case when a.account_name = '销售收入' then a.amount*10000 end) as sale, " +
            " sum(case when a.account_name = '销售毛利额' then a.amount*10000 end) as rate " +
            " from cmx_hbl_cate_data_manual_imp a,rms.v_bi_inf_store b " +
            " where a.store = b.store ";

    private static final String GET_FRESH_KL_PROVINCE_PREFIX = "select b.AREA as id, sum(day_store_kl) as kl " +
            "  from cmx.cmx_realtime_kl_hist a, rms.v_bi_inf_store b " +
            " where a.cate_level = 2 " +
            "   and a.store = b.store ";

    private static final String GET_FRESH_KL_AREA_PREFIX = "select b.REGION as id, sum(day_store_kl) as kl " +
            "  from cmx.cmx_realtime_kl_hist a, rms.v_bi_inf_store b " +
            " where a.cate_level = 2 " +
            "   and a.store = b.store ";

    private static final String GET_FRESH_KL_STORE_PREFIX = "select b.STORE as id, sum(day_store_kl) as kl " +
            "  from cmx.cmx_realtime_kl_hist a, rms.v_bi_inf_store b " +
            " where a.cate_level = 2 " +
            "   and a.store = b.store ";

    private static final String GET_FRESH_KL_DEPT_PREFIX = "select a.cate as id, sum(day_store_kl) as kl " +
            "  from cmx.cmx_realtime_kl_hist a, rms.v_bi_inf_store b " +
            " where a.cate_level = 2 " +
            "   and a.store = b.store ";

    private static final String GET_FRESH_SALE_ONE_KL_PREFIX = "select sum(day_store_kl) as kl " +
            "  from cmx.cmx_realtime_kl_hist a, rms.v_bi_inf_store b " +
            " where a.cate_level = 2 " +
            "   and a.store = b.store ";

    /**
     * 查询历史单一大类客流
     * @param request
     * @param start
     * @param end
     * @return
     */
    public FreshRankKlModel queryHisSaleOneKl(FreshReportBaseRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_FRESH_SALE_ONE_KL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == start){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }else{
            sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
            sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.AREA = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.AREA in ( ");
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
                sb.append(" and b.STORE  = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.STORE in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and a.cate  = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and a.cate in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        return super.queryForObject(sb.toString(), TOTAL_FRESHRANKKL_RM, paramList.toArray());
    }

    /**
     * 查询排行榜单一生鲜客流
     * @param request
     * @return
     */
    public List<FreshRankKlModel> queryHisFreshRankKlForOne(FreshReportBaseRequest request, Date start, Date end, int mark){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        switch (mark){
            case 1:
                sb.append(GET_FRESH_KL_PROVINCE_PREFIX);
                break;
            case 2:
                sb.append(GET_FRESH_KL_AREA_PREFIX);
                break;
            case 3:
                sb.append(GET_FRESH_KL_STORE_PREFIX);
                break;
            case 4:
                sb.append(GET_FRESH_KL_DEPT_PREFIX);
                break;
            default:
                sb.append("");
                break;
        }
        if(null == start){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }else{
            sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
            sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and b.AREA = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and b.AREA in ( ");
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
                sb.append(" and b.STORE  = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.STORE in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        //拼接大类
        if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and a.cate  = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and a.cate in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }

        switch (mark){
            case 1:
                sb.append(" group by b.AREA ");
                break;
            case 2:
                sb.append(" group by b.REGION ");
                break;
            case 3:
                sb.append(" group by b.STORE ");
                break;
            case 4:
                sb.append(" group by a.cate ");
                break;
            default:
                sb.append("");
                break;
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHRANKKL_RM,paramList.toArray());
    }


    /**
     * 查询销售和毛利额目标
     * @param request
     * @param start
     * @param end
     * @return
     */
    public GoalSale queryGoalSale(FreshReportBaseRequest request, Date start, Date end){
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

    /**
     * 查询实际周转天数
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<HblTurnoverDay> queryActualTurnoverdays(FreshReportBaseRequest param,Date start,Date end){
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder(GET_HIS_FRESH_DEPT_ACTUAL_TURNOVERDAYS_PREFIX);
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
        sb.append(" group by a.dept having sum(total_cost)>0");
        return super.queryForList(sb.toString(),TOTAL_HIS_FRESH_DEPT_ACTUAL_TURNOVERDAYS_RM,paramList.toArray());
    }

    /**
     * 查询历史生鲜数据(销售，损耗，毛利)
     * @param param
     * @return
     */
    public MonthStatistics queryHisFreshInfo(FreshReportBaseRequest param){
        List<Object> paramList = new ArrayList<Object>();
        String sql = getFreshHisSql(param, paramList);
        MonthStatistics monthStatistics = super.queryForObject(sql, TOTAL_FRESH_RM, paramList.toArray());
        return monthStatistics;
    }

    /**
     * 查询历史多个单点时间的生鲜数据(销售，损耗，毛利)
     * @param param
     * @return
     */
    public List<SalesList> queryHisFreshSalesInfo(FreshReportBaseRequest param){
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder timeSb = new StringBuilder();
        //时间点
        timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",param.getStart())).append("', ");
        //昨日
        timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addDays(param.getStart(),-1))).append("', ");
        //周
        timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addWeeks(param.getStart(),-1))).append("', ");
        //月
        timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addMonths(param.getStart(),-1))).append("'");
        String sql = getFreshHisSalesSql(param,paramList,timeSb.toString());
        sql = sql + " group by saledate";
        List<SalesList> salesList = super.queryForList(sql, TOTAL_FRESH_SALES_RM, paramList.toArray());
        return salesList;
    }

    /**
     * 查询历史排行榜数据
     * @param param
     * @return
     */
    public List<FreshRankInfo> queryFreshRankInfo(FreshRankRequest param){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getRankProfixSql(param, param.getMark()));
        sb.append(getRankSql(param, paramList));
        sb.append(getRankGroupSql(param.getMark()));
        List<FreshRankInfo> list = super.queryForList(sb.toString(),TOTAL_FRESH_PROVINCE_RM,paramList.toArray());
        return list;
    }

    /**
     * 查询历史排行榜数据(按照可比分组)
     * @param param
     * @return
     */
    public List<FreshRankInfo> queryFreshRankCompareInfo(FreshRankRequest param){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getRankCompareProfixSql(param, param.getMark()));
        sb.append(getRankSql(param, paramList));
        sb.append(getRankCompareGroupSql(param.getMark()));
        List<FreshRankInfo> list = super.queryForList(sb.toString(),TOTAL_FRESH_PROVINCE_RM,paramList.toArray());
        return list;
    }

    /**
     * 历史生鲜sql
     * @param param
     * @param paramList
     * @return
     */
    private String getFreshHisSql(FreshReportBaseRequest param, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_FRESH_HIS_PREFIX);
        if(null == param.getEnd()){
            sb.append(" and saledate = date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and saledate <= date '");
            sb.append(DateUtil.getDateFormat(param.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and saledate >= date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){

                sb.append(" and region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and dept  = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }

    /**
     * 历史生鲜销售列表sql
     * @param param
     * @param paramList
     * @return
     */
    private String getFreshHisSalesSql(FreshReportBaseRequest param, List<Object> paramList,String timeStr){
        StringBuilder sb = new StringBuilder(GET_FRESH_HIS_PREFIX_TIME);
        if(StringUtils.isNotEmpty(timeStr)){
            sb.append(" and saledate in (").append(timeStr).append(") ");
//            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }else if(null == param.getEnd()){
            sb.append(" and saledate = date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and saledate <= date '");
            sb.append(DateUtil.getDateFormat(param.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and saledate >= date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){

                sb.append(" and region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and dept  = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }

    /**
     * 查询排行榜前缀sql
     * @param param
     * @param type
     * @return
     */
    private String getRankProfixSql(FreshReportBaseRequest param, int type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb.append(GET_FRESH_HIS_PREFIX_PROVINCE);
                break;
            case 2:
                sb.append(GET_FRESH_HIS_PREFIX_REGION);
                break;
            case 3:
                sb.append(GET_FRESH_HIS_PREFIX_STORE);
                break;
            case 4:
                sb.append(GET_FRESH_HIS_PREFIX_DEPT);
                break;
            default:
                sb.append("");
                break;
        }
        if(null == param.getEnd()){
            sb.append(" and saledate = date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and saledate <= date '");
            sb.append(DateUtil.getDateFormat(param.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and saledate >= date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }
        return sb.toString();
    }

    /**
     * 查询排行榜前缀sql
     * @param param
     * @param type
     * @return
     */
    private String getRankCompareProfixSql(FreshReportBaseRequest param, int type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb.append(GET_FRESH_HIS_PREFIX_PROVINCE_COMPARE);
                break;
            case 2:
                sb.append(GET_FRESH_HIS_PREFIX_REGION_COMPARE);
                break;
            case 3:
                sb.append(GET_FRESH_HIS_PREFIX_STORE_COMPARE);
                break;
            case 4:
                sb.append(GET_FRESH_HIS_PREFIX_DEPT_COMPARE);
                break;
            default:
                sb.append("");
                break;
        }
        if(null == param.getEnd()){
            sb.append(" and saledate = date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and saledate <= date '");
            sb.append(DateUtil.getDateFormat(param.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and saledate >= date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }
        return sb.toString();
    }

    private String getRankSql(FreshReportBaseRequest param, List<Object> paramList){
        StringBuilder sb = new StringBuilder();
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }
        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){

                sb.append(" and region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }
        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }
        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and dept  = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }

    private String getRankCompareGroupSql(int type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb.append(" group by area, mall_name");
                break;
            case 2:
                sb.append(" group by region, mall_name");
                break;
            case 3:
                sb.append(" group by store, mall_name");
                break;
            case 4:
                sb.append(" group by dept, mall_name");
                break;
            default:
                sb.append("");
                break;
        }
        return sb.toString();
    }

    private String getRankGroupSql(int type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb.append(" group by area");
                break;
            case 2:
                sb.append(" group by region");
                break;
            case 3:
                sb.append(" group by store");
                break;
            case 4:
                sb.append(" group by dept");
                break;
            default:
                sb.append("");
                break;
        }
        return sb.toString();
    }

}
