package com.cs.mobile.api.dao.freshreport;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.freshreport.*;
import com.cs.mobile.api.model.freshreport.request.FreshRankRequest;
import com.cs.mobile.api.model.freshreport.request.FreshReportBaseRequest;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
import com.cs.mobile.api.model.salereport.BaseSaleModel;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Repository
public class FreshReportCsmbDao extends AbstractDao {
    private static final RowMapper<MonthStatistics> TOTAL_FRESH_RM = new BeanPropertyRowMapper<MonthStatistics>(MonthStatistics.class);
    private static final RowMapper<FreshRankInfo> TOTAL_FRESH_PROVINCE_RM = new BeanPropertyRowMapper<FreshRankInfo>(FreshRankInfo.class);
    private static final RowMapper<OrganizationForFresh> TOTAL_ORGANIZATION_RM = new BeanPropertyRowMapper<OrganizationForFresh>(OrganizationForFresh.class);
    private static final RowMapper<TurnoverDay> TOTAL_TURNOVERDAY_RM = new BeanPropertyRowMapper<TurnoverDay>(TurnoverDay.class);
    private static final RowMapper<FreshKl> TOTAL_FRESH_KL_RM = new BeanPropertyRowMapper<FreshKl>(FreshKl.class);
    private static final RowMapper<FreshBaseModel> TOTAL_FRESHBASEMODEL_RM = new BeanPropertyRowMapper<FreshBaseModel>(FreshBaseModel.class);
    private static final RowMapper<FreshRankKlModel> TOTAL_FRESHRANKKL_RM = new BeanPropertyRowMapper<FreshRankKlModel>(FreshRankKlModel.class);
    private static final RowMapper<HblTurnoverDay> TOTAL_HBLTURNOVERDAY_RM = new BeanPropertyRowMapper<HblTurnoverDay>(HblTurnoverDay.class);
    private static final RowMapper<FreshDeptKl> TOTAL_FRESH_DEPT_KL_RM = new BeanPropertyRowMapper<FreshDeptKl>(FreshDeptKl.class);
    private static final RowMapper<FreshRankGrade> TOTAL_FRESH_RANK_GRADE_RM = new BeanPropertyRowMapper<FreshRankGrade>(FreshRankGrade.class);
    private static final RowMapper<MemberModel> TOTAL_MEMBERMODEL_RM = new BeanPropertyRowMapper<MemberModel>(MemberModel.class);
    private static final RowMapper<BaseSaleModel> TOTAL_BASE_SALE_RM = new BeanPropertyRowMapper<BaseSaleModel>(BaseSaleModel.class);
    private static final String GET_FRESH_PREFIX = "select nvl(sum(case when retail_type = 3 then amt_ecl else 0 end),0) as discountPrice, " +
            " nvl(sum(amt_ecl),0) totalSales, " +
            " nvl(sum(GP_ECL),0) as totalProfitPrice, " +
            " nvl(sum(case when retail_type = 3 then amt else 0 end),0) as discountPriceIn, " +
            " nvl(sum(amt),0) totalSalesIn, " +
            " nvl(sum(GP),0) as totalProfitPriceIn " +
            " from CSMB_STORE_TYPE_SALE where 1=1 ";

    private static final String GET_FRESH_PREFIX_PROVINCE = "select nvl(sum(case when retail_type = 3 then amt_ecl else 0 end),0) as discountPrice, " +
            "       nvl(sum(amt_ecl),0) totalSales,  " +
            "       nvl(sum(GP_ECL),0) as totalProfitPrice, " +
            "       nvl(sum(case when retail_type = 3 then amt else 0 end),0) as discountPriceIn, " +
            "       nvl(sum(amt),0) totalSalesIn,  " +
            "       nvl(sum(GP),0) as totalProfitPriceIn, " +
            "       mall_name as compareMark, " +
            "       area as id  " +
            "  from CSMB_STORE_TYPE_SALE  " +
            "  where 1=1 ";

    private static final String GET_FRESH_PREFIX_REGION = "select nvl(sum(case when retail_type = 3 then amt_ecl else 0 end),0) as discountPrice, " +
            "       nvl(sum(amt_ecl),0) totalSales,  " +
            "       nvl(sum(GP_ECL),0) as totalProfitPrice, " +
            "       nvl(sum(case when retail_type = 3 then amt else 0 end),0) as discountPriceIn, " +
            "       nvl(sum(amt),0) totalSalesIn,  " +
            "       nvl(sum(GP),0) as totalProfitPriceIn, " +
            "       mall_name as compareMark, " +
            "       region as id  " +
            "  from CSMB_STORE_TYPE_SALE  " +
            "  where 1=1 ";

    private static final String GET_FRESH_PREFIX_STORE = "select nvl(sum(case when retail_type = 3 then amt_ecl else 0 end),0) as discountPrice, " +
            "       nvl(sum(amt_ecl),0) totalSales,  " +
            "       nvl(sum(GP_ECL),0) as totalProfitPrice, " +
            "       nvl(sum(case when retail_type = 3 then amt else 0 end),0) as discountPriceIn, " +
            "       nvl(sum(amt),0) totalSalesIn,  " +
            "       nvl(sum(GP),0) as totalProfitPriceIn, " +
            "       mall_name as compareMark, " +
            "       store as id  " +
            "  from CSMB_STORE_TYPE_SALE  " +
            "  where 1=1 ";

    private static final String GET_FRESH_PREFIX_DEPT = "select nvl(sum(case when retail_type = 3 then amt_ecl else 0 end),0) as discountPrice, " +
            "       nvl(sum(amt_ecl),0) totalSales,  " +
            "       nvl(sum(GP_ECL),0) as totalProfitPrice, " +
            "       nvl(sum(case when retail_type = 3 then amt else 0 end),0) as discountPriceIn, " +
            "       nvl(sum(amt),0) totalSalesIn,  " +
            "       nvl(sum(GP),0) as totalProfitPriceIn, " +
            "       mall_name as compareMark, " +
            "       dept as id  " +
            "  from CSMB_STORE_TYPE_SALE  " +
            "  where 1=1 ";

    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_DEPT = " select /*+PARALLEL(B,8)*/ " +
            "        nvl(sum(b.sale_value), 0) as totalSales, " +
            "        nvl(sum(b.sale_value - b.sale_cost + b.invadj_cost - b.wac + b.fund_amount), 0) as totalProfitPrice, " +
            "        nvl(sum(b.sale_value_in), 0) as totalSalesIn, " +
            "        nvl(sum(b.sale_value_in - b.sale_cost_in + b.invadj_cost_in - b.wac_in + b.fund_amount_in), 0) as totalProfitPriceIn " +
            "   from csmb_dept_sales_history b, csmb_store c " +
            "  where b.store_id = c.store_id ";

    private static final String GET_REAL_TOTAL_SALES_PROFIT_PREFIX = "select nvl(sum(gp_ecl),0) as totalProfitPrice,nvl(sum(amt_ecl),0) as totalSales, nvl(sum(gp),0) as totalProfitPriceIn,nvl(sum(amt),0) as totalSalesIn from CSMB_DEPT_SALES ";

    private static final String GET_ALL_NAME = "select province_id   as provinceId, " +
            "    province_name as provinceName, " +
            "    area_id       as areaId, " +
            "    area_name     as areaName, " +
            "    store_id      as storeId, " +
            "    store_name    as storeName " +
            "    from CSMB_STORE ";

    private static final String GET_TURNOVERDAY_NAME = "SELECT nvl(sum(a.days),0) as hblTurnoverDays FROM CSMB_TURNOVER_DAYS a,csmb_store b " +
            "    where a.store_id = b.store_id ";

    private static final String GET_FRESH_KL_PREFIX = "SELECT  " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as freshKlCount, " +
            " COUNT(DISTINCT case when vipno is not null then STORE || SALEDATE || BILLNO || POSID end) as freshMemberCount " +
            " FROM CSMB_STORE_SALEDETAIL where 1=1 ";


    private static final String GET_FRESH_PROVINCE_KL_PREFIX = "SELECT  " +
            " area as id, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " COUNT(DISTINCT case when vipno is not null then STORE || SALEDATE || BILLNO || POSID end) as freshMemberCount " +
            " FROM CSMB_STORE_SALEDETAIL where 1=1 ";

    private static final String GET_FRESH_AREA_KL_PREFIX = "SELECT  " +
            " region as id, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " COUNT(DISTINCT case when vipno is not null then STORE || SALEDATE || BILLNO || POSID end) as freshMemberCount " +
            " FROM CSMB_STORE_SALEDETAIL where 1=1 ";

    private static final String GET_FRESH_STORE_KL_PREFIX = "SELECT  " +
            " store as id, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " COUNT(DISTINCT case when vipno is not null then STORE || SALEDATE || BILLNO || POSID end) as freshMemberCount " +
            " FROM CSMB_STORE_SALEDETAIL where 1=1 ";

    private static final String GET_FRESH_DEPT_KL_PREFIX = "SELECT  " +
            " dept as id, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " COUNT(DISTINCT case when vipno is not null then STORE || SALEDATE || BILLNO || POSID end) as freshMemberCount " +
            " FROM CSMB_STORE_SALEDETAIL where 1=1 ";

    private static final String GET_FRESH_TOTAL_KL_PREFIX = "SELECT  " +
            " nvl(sum(kl),0) as allKlCount  " +
            " FROM CSMB_STORE_KL " +
            " where 1=1 ";

    private static final String GET_FRESH_TOTAL_PROVINCE_KL_PREFIX = "SELECT  " +
            " area as id, " +
            " nvl(sum(kl),0) as kl  " +
            " FROM CSMB_STORE_KL " +
            " where 1=1 ";

    private static final String GET_FRESH_TOTAL_AREA_KL_PREFIX = "SELECT  " +
            " region as id, " +
            " nvl(sum(kl),0) as kl  " +
            " FROM CSMB_STORE_KL " +
            " where 1=1 ";

    private static final String GET_FRESH_TOTAL_STORE_KL_PREFIX = "SELECT  " +
            " store as id, " +
            " nvl(sum(kl),0) as kl  " +
            " FROM CSMB_STORE_KL " +
            " where 1=1 ";

    private static final String GET_HIS_FRESH_BASE_PREFIX = "select /*+PARALLEL(A,8)*/ nvl(sum(a.sale_value), 0) as totalSales, " +
            "  nvl(sum(a.sale_value-a.sale_cost+a.invadj_cost-a.wac+a.fund_amount),0) as totalFrontDeskProfitPrice, " +
            "  nvl(sum(a.sale_value) - sum(a.sale_cost),0) as totalScanningProfitPrice,  " +
            "  nvl(sum(a.sale_value_in), 0) as totalSalesIn, " +
            "  nvl(sum(a.sale_value_in-a.sale_cost_in+a.invadj_cost_in-a.wac_in+a.fund_amount_in),0) as totalFrontDeskProfitPriceIn, " +
            "  nvl(sum(a.sale_value_in) - sum(a.sale_cost_in),0) as totalScanningProfitPriceIn  " +
            "  from csmb_dept_sales_history a, csmb_store b " +
            "  where a.store_id = b.store_id ";

    private static final String GET_HBLTURNOVERDAY_PREFIX = "SELECT  " +
            "  a.dept_id as deptId, " +
            "  max(a.days) as hblTurnoverDays " +
            "  FROM CSMB_TURNOVER_DAYS a, csmb_store b " +
            " where a.store_id = b.store_id ";


    private static final String GET_CUR_FRESH_DEPT_KL_PREFIX = "SELECT /*+PARALLEL(8)*/  " +
            " dept as deptId, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as freshKlCount, " +
            " COUNT(DISTINCT case when vipno is not null then STORE || SALEDATE || BILLNO || POSID end) as freshMemberCount " +
            " FROM CSMB_STORE_SALEDETAIL " +
            " where 1=1 ";

    private static final String GET_CUR_FRESH_RANK_GRADE_PROVINCE_PREFIX = "select  " +
            " area as id, " +
            " nvl(sum(amt_ecl),0) as totalSale,   " +
            " nvl(sum(gp_ecl),0) as totalScanningRate, " +
            " nvl(sum(amt),0) as totalSaleIn,   " +
            " nvl(sum(gp),0) as totalScanningRateIn " +
            " from CSMB_DEPT_SALES  " +
            " where 1=1 ";

    private static final String GET_CUR_FRESH_RANK_GRADE_REGION_PREFIX = "select  " +
            " region as id, " +
            " nvl(sum(amt_ecl),0) as totalSale,   " +
            " nvl(sum(gp_ecl),0) as totalScanningRate, " +
            " nvl(sum(amt),0) as totalSaleIn,   " +
            " nvl(sum(gp),0) as totalScanningRateIn " +
            " from CSMB_DEPT_SALES  " +
            " where 1=1 ";

    private static final String GET_CUR_FRESH_RANK_GRADE_STORE_PREFIX = "select  " +
            " store_id as id, " +
            " nvl(sum(amt_ecl),0) as totalSale,   " +
            " nvl(sum(gp_ecl),0) as totalScanningRate, " +
            " nvl(sum(amt),0) as totalSaleIn,   " +
            " nvl(sum(gp),0) as totalScanningRateIn " +
            " from CSMB_DEPT_SALES  " +
            " where 1=1 ";

    private static final String GET_HIS_FRESH_RANK_GRADE_PROVINCE_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " b.province_id as id, " +
            " nvl(sum(a.sale_value), 0) as totalSale,  " +
            " nvl(sum(a.sale_value) - sum(a.sale_cost),0) as totalScanningRate,   " +
            " nvl(sum(a.sale_value_in), 0) as totalSaleIn,  " +
            " nvl(sum(a.sale_value_in) - sum(a.sale_cost_in),0) as totalScanningRateIn   " +
            " from csmb_dept_sales_history a, csmb_store b  " +
            " where a.store_id = b.store_id ";

    private static final String GET_HIS_FRESH_RANK_GRADE_REGION_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " b.area_id as id, " +
            " nvl(sum(a.sale_value), 0) as totalSale,  " +
            " nvl(sum(a.sale_value) - sum(a.sale_cost),0) as totalScanningRate,   " +
            " nvl(sum(a.sale_value_in), 0) as totalSaleIn,  " +
            " nvl(sum(a.sale_value_in) - sum(a.sale_cost_in),0) as totalScanningRateIn   " +
            " from csmb_dept_sales_history a, csmb_store b  " +
            " where a.store_id = b.store_id ";

    private static final String GET_HIS_FRESH_RANK_GRADE_STORE_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " b.store_id as id, " +
            " nvl(sum(a.sale_value), 0) as totalSale,  " +
            " nvl(sum(a.sale_value) - sum(a.sale_cost),0) as totalScanningRate,   " +
            " nvl(sum(a.sale_value_in), 0) as totalSaleIn,  " +
            " nvl(sum(a.sale_value_in) - sum(a.sale_cost_in),0) as totalScanningRateIn   " +
            " from csmb_dept_sales_history a, csmb_store b  " +
            " where a.store_id = b.store_id ";

    private static final String GET_CUR_MEMBER_PREFIX = "SELECT decode(a.vipno, null, 'N', 'Y') as vipMark, " +
            "       decode(a.channel,null,'BKSLS',a.channel) as channel, " +
            "       max(b.channel_desc) as channelName, " +
            "       count(distinct a.store || saledate || billno || posid) as count " +
            "  FROM csmb_store_saledetail a,code_channel b " +
            "  where a.CHANNEL = b.channel(+) ";

    private static final String GET_HIS_BASE_SALE_PREFIX = "select /*+PARALLEL(A,8)*/ nvl(sum(a.sale_value), 0) as totalSale,  " +
            "            nvl(sum(a.sale_value-a.sale_cost+a.invadj_cost-a.wac+a.fund_amount),0) as totalFrontDeskRate,  " +
            "            nvl(sum(a.sale_cost),0) as totalCost,   " +
            "            nvl(sum(a.sale_value_in), 0) as totalSaleIn,  " +
            "            nvl(sum(a.sale_value_in-a.sale_cost_in+a.invadj_cost_in-a.wac_in+a.fund_amount_in),0) as totalFrontDeskRateIn,  " +
            "            nvl(sum(a.sale_cost_in),0) as totalCostIn " +
            "            from csmb_dept_sales_history a, csmb_store b  " +
            "            where a.store_id = b.store_id ";
    private static final String GET_CUR_BASE_SALE_PREFIX = "select nvl(sum(gp_ecl),0) as totalScanningRate, " +
            "       nvl(sum(amt_ecl),0) as totalSale,   " +
            "       nvl(sum(case when mall_name = 1 then amt_ecl end),0) as totalCompareSale,  " +
            "       nvl(sum(case when mall_name = 1 then gp_ecl end),0) as totalCompareScanningRate, " +
            "       nvl(sum(gp),0) as totalScanningRateIn, " +
            "       nvl(sum(amt),0) as totalSaleIn,   " +
            "       nvl(sum(case when mall_name = 1 then amt end),0) as totalCompareSaleIn,  " +
            "       nvl(sum(case when mall_name = 1 then gp end),0) as totalCompareScanningRateIn   " +
            "       from CSMB_DEPT_SALES where 1=1 ";

    private static final String GET_FRESH_SALE_TOTAL_KL_PREFIX = "SELECT nvl(sum(kl), 0) as kl  " +
            " FROM CSMB_STORE_KL  " +
            " where 1 = 1 ";

    private static final String GET_FRESH_SALE_KL_PREFIX = "SELECT COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl " +
            "  FROM CSMB_STORE_SALEDETAIL " +
            " where 1 = 1 ";

    /**
     * 查询大类客流
     * @param request
     * @param start
     * @param end
     * @return
     */
    public FreshRankKlModel querySaleKl(FreshReportBaseRequest request,Date start,Date end){
        StringBuilder sb = new StringBuilder(GET_FRESH_SALE_KL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and saledate = '");
            sb.append(DateUtils.parseDateToStr("yyyyMMdd",start));
            sb.append("' ");
        }else{
            sb.append(" and saledate = '");
            sb.append(DateUtils.parseDateToStr("yyyyMMdd",end));
            sb.append("' ");
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
        return super.queryForObject(sb.toString(), TOTAL_FRESHRANKKL_RM, paramList.toArray());
    }

    /**
     * 查询实时总客流
     * @param request
     * @param start
     * @param end
     * @return
     */
    public FreshRankKlModel querySaleTotalKl(FreshReportBaseRequest request,Date start,Date end){
        StringBuilder sb = new StringBuilder(GET_FRESH_SALE_TOTAL_KL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and saledate = '");
            sb.append(DateUtils.parseDateToStr("yyyyMMdd",start));
            sb.append("' ");
        }else{
            sb.append(" and saledate = '");
            sb.append(DateUtils.parseDateToStr("yyyyMMdd",end));
            sb.append("' ");
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
        return super.queryForObject(sb.toString(), TOTAL_FRESHRANKKL_RM, paramList.toArray());
    }

    /**
     * 查询客流（1-生鲜客流，2-总可流）
     * @param request
     * @param type
     * @return
     */
    public List<FreshRankKlModel> queryCurFreshRankKlCount(FreshReportBaseRequest request,Date start,Date end, int type,int mark){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        if(1 == type){
            switch (mark){
                case 1:
                    sb.append(GET_FRESH_PROVINCE_KL_PREFIX);
                    break;
                case 2:
                    sb.append(GET_FRESH_AREA_KL_PREFIX);
                    break;
                case 3:
                    sb.append(GET_FRESH_STORE_KL_PREFIX);
                    break;
                case 4:
                    sb.append(GET_FRESH_DEPT_KL_PREFIX);
                    break;
                default:
                    sb.append("");
                    break;
            }
            if(null == end){
                sb.append(" and saledate = '");
                sb.append(DateUtils.parseDateToStr("yyyyMMdd",start));
                sb.append("' ");
            }else{
                sb.append(" and saledate = '");
                sb.append(DateUtils.parseDateToStr("yyyyMMdd",end));
                sb.append("' ");
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
            switch (mark){
                case 1:
                    sb.append(" group by area ");
                    break;
                case 2:
                    sb.append(" group by region ");
                    break;
                case 3:
                    sb.append(" group by store ");
                    break;
                case 4:
                    sb.append(" group by dept ");
                    break;
                default:
                    sb.append("");
                    break;
            }
        }else{
            switch (mark){
                case 1:
                    sb.append(GET_FRESH_TOTAL_PROVINCE_KL_PREFIX);
                    break;
                case 2:
                    sb.append(GET_FRESH_TOTAL_AREA_KL_PREFIX);
                    break;
                case 3:
                    sb.append(GET_FRESH_TOTAL_STORE_KL_PREFIX);
                    break;
                case 4:
                    sb.append(GET_FRESH_TOTAL_STORE_KL_PREFIX);
                    break;
                default:
                    sb.append("");
                    break;
            }
            if(null == end){
                sb.append(" and saledate = '");
                sb.append(DateUtils.parseDateToStr("yyyyMMdd",start));
                sb.append("' ");
            }else{
                sb.append(" and saledate = '");
                sb.append(DateUtils.parseDateToStr("yyyyMMdd",end));
                sb.append("' ");
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

            switch (mark){
                case 1:
                    sb.append(" group by area ");
                    break;
                case 2:
                    sb.append(" group by region ");
                    break;
                case 3:
                    sb.append(" group by store ");
                    break;
                case 4:
                    sb.append(" group by store ");
                    break;
                default:
                    sb.append("");
                    break;
            }
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHRANKKL_RM,paramList.toArray());
    }

    /**
     * 查询历史销售额，前台毛利额和成本
     * @return
     */
    public BaseSaleModel queryHistoryBaseSaleModel(FreshReportBaseRequest request,Date start,Date end){
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

    /**
     * 查询实时销售额和扫描毛利额
     * @return
     */
    public BaseSaleModel queryCurBaseSaleModel(FreshReportBaseRequest request,Date start,Date end){
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
     * 查询vip数量明细
     * @param pageRequest
     * @return
     */
    public List<MemberModel> queryCurMemberDetail(PageRequest pageRequest, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_CUR_MEMBER_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.saledate = ? ");
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", start));
        }else{
            sb.append(" and a.saledate <= ? ");
            sb.append(" and a.saledate >= ? ");
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", end));
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", start));
        }
        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){

                sb.append(" and a.area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){

                sb.append(" and a.region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and a.store  = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){

                sb.append(" and a.store in ( ");
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
        sb.append(" group by decode(a.vipno, null, 'N', 'Y'),decode(a.channel,null,'BKSLS',a.channel) ");
        return super.queryForList(sb.toString(), TOTAL_MEMBERMODEL_RM, paramList.toArray());
    }

    /**
     * 查询历史销售额和扫描毛利额，按照省级或者区域级或者门店级分组
     * @param request
     * @param start
     * @param type(1-全司，2-省级，3-区域级，4-门店级)
     * @return
     */
    public List<FreshRankGrade> queryHisFreshRankGrade(FreshReportBaseRequest request, Date start, Date end, int type){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        switch (type){
            case 1://全司
                return null;
            case 2://省级
                sb.append(GET_HIS_FRESH_RANK_GRADE_PROVINCE_PREFIX);
                break;
            case 3://区域级
                sb.append(GET_HIS_FRESH_RANK_GRADE_REGION_PREFIX);
                break;
            case 4://门店级
                sb.append(GET_HIS_FRESH_RANK_GRADE_STORE_PREFIX);
                break;
            default:
                return null;
        }
        if(null == end){
            sb.append(" and a.sale_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }else{
            sb.append(" and a.sale_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
            sb.append(" and a.sale_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        //拼接省份
        if(2 != type){
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
        }


        //拼接区域
        if(3 != type){
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
        }

        //拼接门店
        if(4 != type){
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
        switch (type){
            case 1://全司
                return null;
            case 2://省级
                sb.append(" group by b.province_id");
                break;
            case 3://区域级
                sb.append(" group by b.area_id");
                break;
            case 4://门店级
                sb.append(" group by b.store_id");
                break;
            default:
                return null;
        }
        return super.queryForList(sb.toString(),TOTAL_FRESH_RANK_GRADE_RM,paramList.toArray());
    }

    /**
     * 查询实时销售额和扫描毛利额，按照省级或者区域级或者门店级分组
     * @param request
     * @param start
     * @param type(1-全司，2-省级，3-区域级，4-门店级)
     * @return
     */
    public List<FreshRankGrade> queryCurFreshRankGrade(FreshReportBaseRequest request, Date start, int type){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        switch (type){
            case 1://全司
                return null;
            case 2://省级
                sb.append(GET_CUR_FRESH_RANK_GRADE_PROVINCE_PREFIX);
                break;
            case 3://区域级
                sb.append(GET_CUR_FRESH_RANK_GRADE_REGION_PREFIX);
                break;
            case 4://门店级
                sb.append(GET_CUR_FRESH_RANK_GRADE_STORE_PREFIX);
                break;
            default:
                return null;
        }
        sb.append(" and sale_date = '").append(DateUtils.parseDateToStr("yyyyMMdd",start)).append("' ");
        //拼接省份
        if(2 != type){
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
        }

        //拼接区域
        if(3 != type){
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
        }

        //拼接门店
        if(4 != type){
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
        switch (type){
            case 1://全司
                return null;
            case 2://省级
                sb.append(" group by area");
                break;
            case 3://区域级
                sb.append(" group by region");
                break;
            case 4://门店级
                sb.append(" group by store_id");
                break;
            default:
                return null;
        }
        return super.queryForList(sb.toString(),TOTAL_FRESH_RANK_GRADE_RM,paramList.toArray());
    }

    /**
     * 查询标准周转天数
     * @param request
     * @param start
     * @return
     */
    public List<HblTurnoverDay> queryHblTurnoverDay(FreshReportBaseRequest request, Date start){
        StringBuilder sb = new StringBuilder(GET_HBLTURNOVERDAY_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.t_ym = '").append(DateUtils.parseDateToStr("yyyy-MM",start)).append("' ");
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
        sb.append(" group by a.dept_id");
        return super.queryForList(sb.toString(),TOTAL_HBLTURNOVERDAY_RM,paramList.toArray());
    }

    /**
     * 查询实时大类客流和会员客流
     * @param request
     * @param start
     * @return
     */
    public List<FreshDeptKl> queryCurFreshDeptKl(FreshReportBaseRequest request, Date start){
        StringBuilder sb = new StringBuilder(GET_CUR_FRESH_DEPT_KL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd",start)).append("' ");
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
        sb.append(" group by dept");
        return super.queryForList(sb.toString(),TOTAL_FRESH_DEPT_KL_RM,paramList.toArray());
    }


    /**
     * 查询历史销售额，前台毛利额和扫描毛利额
     * @return
     */
    public FreshBaseModel queryHisCommonBaseData(FreshReportBaseRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_FRESH_BASE_PREFIX);
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
        FreshBaseModel result = super.queryForObject(sb.toString(),TOTAL_FRESHBASEMODEL_RM,paramList.toArray());
        return result;
    }

    /**
     * 查询客流（1-生鲜客流，2-总可流）
     * @param request
     * @param type
     * @return
     */
    public FreshKl queryCurFreshKlCount(FreshReportBaseRequest request, int type){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        if(1 == type){
            sb.append(GET_FRESH_KL_PREFIX);
            if(null == request.getEnd()){
                sb.append(" and saledate = '");
                sb.append(DateUtils.parseDateToStr("yyyyMMdd",request.getStart()));
                sb.append("' ");
            }else{
                sb.append(" and saledate = '");
                sb.append(DateUtils.parseDateToStr("yyyyMMdd",request.getEnd()));
                sb.append("' ");
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
        }else{
            sb.append(GET_FRESH_TOTAL_KL_PREFIX);
            if(null == request.getEnd()){
                sb.append(" and saledate = '");
                sb.append(DateUtils.parseDateToStr("yyyyMMdd",request.getStart()));
                sb.append("' ");
            }else{
                sb.append(" and saledate = '");
                sb.append(DateUtils.parseDateToStr("yyyyMMdd",request.getEnd()));
                sb.append("' ");
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
        }
        FreshKl freshKl = super.queryForObject(sb.toString(),TOTAL_FRESH_KL_RM,paramList.toArray());
        return freshKl;
    }


    public TurnoverDay queryTurnoverDay(PageRequest request){
        StringBuilder sb = new StringBuilder(GET_TURNOVERDAY_NAME);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.t_ym = ?");
        paramList.add(DateUtils.parseDateToStr("yyyy-MM",request.getStart()));
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
                sb.append(" and a.dept_id  = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){

                sb.append(" and a.dept_id in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        TurnoverDay turnoverDay = super.queryForObject(sb.toString(),TOTAL_TURNOVERDAY_RM,paramList.toArray());
        return turnoverDay;
    }

    /**
     * 查询所有组织架构
     * @return
     */
    public List<OrganizationForFresh> queryAllOrganization(){
        List<OrganizationForFresh> list = super.queryForList(GET_ALL_NAME, TOTAL_ORGANIZATION_RM);
        return list;
    }

    /**
     * 查询当天排行榜数据(可比分组)
     * @param param
     * @return
     */
    public List<FreshRankInfo> queryFreshRankInfo(FreshRankRequest param){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getRankProfixSql(param, paramList, param.getMark()));
        sb.append(getRankSql(param, paramList));
        sb.append(getRankGroupSql(param.getMark()));
        List<FreshRankInfo> list = super.queryForList(sb.toString(),TOTAL_FRESH_PROVINCE_RM,paramList.toArray());
        return list;
    }

    /**
     * 查询当天生鲜(销售，毛利，当天损耗默认为零，折价额)
     * @param request
     * @return
     */
    public MonthStatistics queryCurrentFreshInfo(FreshReportBaseRequest request){
        List<Object> paramList = new ArrayList<Object>();
        String sql = null;
        sql = currentFreshSqlByDept(request, paramList);
        MonthStatistics monthStatistics = super.queryForObject(sql, TOTAL_FRESH_RM, paramList.toArray());
        return monthStatistics;
    }

    /**
     * 查询总的历史数据(销售，毛利)
     * @param request
     * @return
     */
    public MonthStatistics queryHisAllData(FreshReportBaseRequest request){
        MonthStatistics result = null;
        List<Object> paramList = new ArrayList<>();
        StringBuilder sb = new StringBuilder(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_DEPT);

        if(null == request.getEnd()){
            sb.append(" and b.sale_date = date '");
            sb.append(DateUtil.getDateFormat(request.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and b.sale_date <= date '");
            sb.append(DateUtil.getDateFormat(request.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and b.sale_date >= date '");
            sb.append(DateUtil.getDateFormat(request.getStart(),"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != request.getProvinceIds() && request.getProvinceIds().size() > 0){
            if(request.getProvinceIds().size() == 1){
                sb.append(" and c.province_id = ? ");
                paramList.add(request.getProvinceIds().get(0));
            }else if(request.getProvinceIds().size() > 1){
                sb.append(" and c.province_id in ( ");
                sb.append(request.getProvinceId());
                sb.append(" ) ");

            }
        }

        //拼接区域
        if(null != request.getAreaIds() && request.getAreaIds().size() > 0){
            if(request.getAreaIds().size() == 1){
                sb.append(" and c.area_id = ? ");
                paramList.add(request.getAreaIds().get(0));
            }else if(request.getAreaIds().size() > 1){
                sb.append(" and c.area_id in ( ");
                sb.append(request.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != request.getStoreIds() && request.getStoreIds().size() > 0){
            if(request.getStoreIds().size() == 1){
                sb.append(" and c.store_id  = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and c.store_id in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        /*if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and b.dept_id = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and b.dept_id in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }*/
        result = super.queryForObject(sb.toString(), TOTAL_FRESH_RM, paramList.toArray());
        return result;
    }

    /**
     * 查询当天总销售额和毛利额
     * @param request
     * @return
     */
    public MonthStatistics queryCurrentTotalData(FreshReportBaseRequest request){
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder gpSql = new StringBuilder();
        gpSql.append(getCurrentTotalDataSqlPrefix(request, paramList));
        gpSql.append(getCurrentTotalDataSql(request, paramList));
        //查询gp
        MonthStatistics gp = super.queryForObject(gpSql.toString(),TOTAL_FRESH_RM ,paramList.toArray());
        return gp;
    }

    /**
     * 当天的总数据sqlPrefix
     * @return
     */
    private String getCurrentTotalDataSqlPrefix(FreshReportBaseRequest request, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_REAL_TOTAL_SALES_PROFIT_PREFIX);
        //查询当天数据
        sb.append(" where sale_date  = ? ");
        if(request.getEnd() == null){
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", request.getStart()));
        }else{
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", new Date()));
        }
        return sb.toString();
    }

    /**
     * 当天的总数据sql
     * @param request
     * @return
     */
    private String getCurrentTotalDataSql(FreshReportBaseRequest request, List<Object> paramList){
        StringBuilder sb = new StringBuilder();
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
        /*if(null != request.getDeptIds() && request.getDeptIds().size() > 0){
            if(request.getDeptIds().size() == 1){
                sb.append(" and dept_id  = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){

                sb.append(" and dept_id in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }*/
        return sb.toString();
    }


    /**
     * 当天生鲜sql
     * @param request
     * @param paramList
     * @return
     */
    private String currentFreshSqlByDept(FreshReportBaseRequest request, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_FRESH_PREFIX);
        sb.append(" and saledate = ? ");
        if(request.getEnd() == null){
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", request.getStart()));
        }else{
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", new Date()));
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
                sb.append(" and dept  = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){

                sb.append(" and dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }

    /**
     * 查询排行榜前缀sql
     * @param request
     * @param paramList
     * @param type
     * @return
     */
    private String getRankProfixSql(FreshReportBaseRequest request,List<Object> paramList,int type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb.append(GET_FRESH_PREFIX_PROVINCE);
                break;
            case 2:
                sb.append(GET_FRESH_PREFIX_REGION);
                break;
            case 3:
                sb.append(GET_FRESH_PREFIX_STORE);
                break;
            case 4:
                sb.append(GET_FRESH_PREFIX_DEPT);
                break;
            default:
                sb.append("");
                break;
        }
        sb.append(" and saledate = ? ");
        if(request.getEnd() == null){
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", request.getStart()));
        }else{
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", new Date()));
        }
        return sb.toString();
    }

    private String getRankSql(FreshReportBaseRequest request, List<Object> paramList){
        StringBuilder sb = new StringBuilder();
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
                sb.append(" and dept  = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){

                sb.append(" and dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }

    private String getRankGroupSql(int type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb.append(" group by area,mall_name");
                break;
            case 2:
                sb.append(" group by region,mall_name");
                break;
            case 3:
                sb.append(" group by store,mall_name");
                break;
            case 4:
                sb.append(" group by dept,mall_name");
                break;
            default:
                sb.append("");
                break;
        }
        return sb.toString();
    }
}
