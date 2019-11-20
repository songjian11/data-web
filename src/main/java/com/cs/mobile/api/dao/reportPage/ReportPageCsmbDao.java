package com.cs.mobile.api.dao.reportPage;

import com.cs.mobile.api.dao.common.AbstractDao;
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
 * 查询首页的实时数据
 * 对应csmb库
 */
@Slf4j
@Repository
public class ReportPageCsmbDao extends AbstractDao {
    private static final RowMapper<TotalSalesAndProfit> TOTAL_SALE_AND_PROFIT_RM = new BeanPropertyRowMapper<TotalSalesAndProfit>(TotalSalesAndProfit.class);
    private static final RowMapper<HomeAppliance> TOTAL_HOME_RM = new BeanPropertyRowMapper<HomeAppliance>(HomeAppliance.class);
    private static final RowMapper<Fresh> TOTAL_FRESH_RM = new BeanPropertyRowMapper<Fresh>(Fresh.class);
    private static final RowMapper<RankDetail> RANK_DETAIL_RM = new BeanPropertyRowMapper<RankDetail>(RankDetail.class);
    private static final RowMapper<ReportDetail> REPORT_DETAIL_RM = new BeanPropertyRowMapper<ReportDetail>(ReportDetail.class);
    private static final RowMapper<MemberPermeabilityPo> MEMBER_RM = new BeanPropertyRowMapper<MemberPermeabilityPo>(MemberPermeabilityPo.class);

    private static final String GET_MEMBER_PREFIX = "SELECT decode(vipno, null, 'N', 'Y') as vipMark,(case when channel in ('YUNPOS', 'VIP', 'WX', 'WX1', 'WX2', 'WX3', 'DD1', 'JD1', 'KJG') or channel is null then 'off' else 'on' end) as onlineMark, " +
            "  count(distinct a.store || saledate || billno || posid) as count " +
            "  FROM csmb_store_saledetail a where 1=1 ";

    private static final String GET_REAL_TOTAL_SALES_PROFIT_PREFIX = "select nvl(sum(gp_ecl), 0) as totalRate, " +
            "       nvl(sum(amt_ecl), 0) as totalSales, " +
            "       nvl(sum(case when mall_name = 1 then amt_ecl end),0) as totalCompareSale, " +
            "       nvl(sum(case when mall_name = 1 then gp_ecl end),0) as totalCompareScanningRate, " +
            "       nvl(sum(gp), 0) as totalRateIn, " +
            "       nvl(sum(amt), 0) as totalSalesIn, " +
            "       nvl(sum(case when mall_name = 1 then amt end),0) as totalCompareSaleIn, " +
            "       nvl(sum(case when mall_name = 1 then gp end),0) as totalCompareScanningRateIn " +
            "  from CSMB_DEPT_SALES ";

    private static final String GET_REAL_TOTAL_SALES_PROFIT_PREFIX_STORE = " select a.store_id as storeId, " +
            "       max(b.store_name) as storeName, " +
            "       nvl(sum(a.amt_ecl), 0) as totalSales, " +
            "       sum(case when mall_name = 1 then a.amt_ecl end) as totalCompareSales, " +
            "       sum(case when mall_name = 1 then a.gp_ecl end) as totalCompareRate, " +
            "       nvl(sum(a.gp_ecl), 0) as totalRate, " +
            "       nvl(sum(a.amt), 0) as totalSalesIn, " +
            "       sum(case when mall_name = 1 then a.amt end) as totalCompareSalesIn," +
            "       sum(case when mall_name = 1 then a.gp end) as totalCompareRateIn, " +
            "       nvl(sum(a.gp), 0) as totalRateIn " +
            "  from CSMB_DEPT_SALES a, csmb_store b " +
            " where a.store_id = b.store_id ";

    private static final String GET_REAL_TOTAL_SALES_PROFIT_PREFIX_HOUR = " select sale_hour as timePoint, " +
            "       nvl(sum(gp_ecl), 0) as totalRate, " +
            "       nvl(sum(amt_ecl), 0) as totalSales, " +
            "       nvl(sum(gp), 0) as totalRateIn, " +
            "       nvl(sum(amt), 0) as totalSalesIn " +
            "  from CSMB_DEPT_SALES ";

    private static final String GET_REAL_TOTAL_SALES_PROFIT_PREFIX_DAY = " select substr(sale_date, 7, 2) as timePoint, " +
            "       sale_date as time, " +
            "       nvl(sum(gp_ecl), 0) as totalRate, " +
            "       nvl(sum(amt_ecl), 0) as totalSales, " +
            "       nvl(sum(gp), 0) as totalRateIn, " +
            "       nvl(sum(amt), 0) as totalSalesIn " +
            "  from CSMB_DEPT_SALES  ";

    private static final String GET_REAL_TOTAL_SALES_PROFIT_PREFIX_MONTH = " select substr(sale_date, 5, 2) as timePoint, " +
            "        nvl(sum(gp_ecl), 0) as totalRate, " +
            "        nvl(sum(amt_ecl), 0) as totalSales, " +
            "        nvl(sum(gp), 0) as totalRateIn, " +
            "        nvl(sum(amt), 0) as totalSalesIn " +
            "   from CSMB_DEPT_SALES ";

    private static final String GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX = "SELECT " +
            "      COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as customerNum, " +
            "       nvl(sum(gp_ecl),0) as totalRate, " +
            "       nvl(SUM(AMT_ECL),0) as totalSales, " +
            "       nvl(sum(case when mall_name = 1 then amt_ecl end),0) as totalCompareSale, " +
            "       nvl(sum(case when mall_name = 1 then gp_ecl end),0) as totalCompareScanningRate, " +
            "       nvl(sum(gp),0) as totalRateIn, " +
            "       nvl(SUM(AMT),0) as totalSalesIn, " +
            "       nvl(sum(case when mall_name = 1 then amt end),0) as totalCompareSaleIn, " +
            "       nvl(sum(case when mall_name = 1 then gp end),0) as totalCompareScanningRateIn " +
            "  FROM CSMB_STORE_SALEDETAIL ";

    private static final String GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX_DEPT = "SELECT " +
            "      COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as customerNum, " +
            "       nvl(sum(gp_ecl),0) as totalRate, " +
            "       sum(case when mall_name = 1 then gp_ecl end) as totalCompareRate, " +
            "       sum(case when mall_name = 1 then AMT_ECL end) as totalCompareSales," +
            "       nvl(SUM(AMT_ECL),0) as totalSales, " +
            "       nvl(sum(gp),0) as totalRateIn, " +
            "       sum(case when mall_name = 1 then gp end) as totalCompareRateIn, " +
            "       sum(case when mall_name = 1 then AMT end) as totalCompareSalesIn," +
            "       nvl(SUM(AMT),0) as totalSalesIn, " +
            "       dept as deptId " +
            "  FROM CSMB_STORE_SALEDETAIL ";

    private static final String GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX_HOUR = "SELECT " +
            "      substr(saletime,1,2) as timePoint, " +
            "       nvl(sum(gp_ecl),0) as totalRate, " +
            "       nvl(SUM(amt_ecl),0) as totalSales, " +
            "       nvl(sum(gp),0) as totalRateIn, " +
            "       nvl(SUM(amt),0) as totalSalesIn " +
            "  FROM CSMB_STORE_SALEDETAIL " ;


    private static final String GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX_DAY = "SELECT " +
            "      substr(saledate, 7,2) as timePoint, " +
            "      saledate as time, " +
            "      nvl(sum(gp_ecl),0) as totalRate, " +
            "      nvl(SUM(AMT_ECL),0) as totalSales, " +
            "      nvl(sum(gp),0) as totalRateIn, " +
            "      nvl(SUM(AMT),0) as totalSalesIn " +
            "  FROM CSMB_STORE_SALEDETAIL ";


    private static final String GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX_MONTH = "SELECT " +
            "      substr(saledate, 5,2) as timePoint, " +
            "       nvl(sum(gp_ecl),0) as totalRate, " +
            "       nvl(SUM(amt_ecl),0) as totalSales, " +
            "       nvl(sum(gp),0) as totalRateIn, " +
            "       nvl(SUM(amt),0) as totalSalesIn " +
            "  FROM CSMB_STORE_SALEDETAIL " ;

    private static final String GET_HOME_PREFIX = "select nvl(sum(a.total_retail), 0) as totalSale, " +
            "       nvl(sum(a.total_cost), 0) as totalCost, " +
            "       nvl(sum(a.total_retail_in), 0) as totalSaleIn, " +
            "       nvl(sum(a.total_cost_in), 0) as totalCostIn " +
            "  from csmb_store_dept_hour_sale a, csmb_store b " +
            " where a.store = to_char(b.store_id) ";

    private static final String GET_STOCK_NUM_PREFIX = "select /*+PARALLEL(A,8)*/ nvl(sum(stock_num),0) as stockNum from csmb_dept_stock a,csmb_store b where a.store_id = b.store_id ";

    private static final String GET_REAL_KL_PREFIX = "SELECT nvl(sum(kl),0) as customerNum FROM  CSMB_STORE_KL ";

    private static final String GET_REAL_KL_PREFIX_STORE = "SELECT store as storeId, nvl(sum(kl),0) as customerNum FROM  CSMB_STORE_KL ";
    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_DEPT = " select /*+PARALLEL(A,8)*/ " +
            "        a.dept_id as deptId, " +
            "        nvl(sum(a.kl), 0) as customerNum, " +
            "        sum(case when c.is_compare = 1 then b.sale_value end) as totalCompareSales, " +
            "        sum(case when c.is_compare = 1 then NVL(b.sale_value,0)-nvl(b.sale_cost,0)+nvl(b.invadj_cost,0)-nvl(b.wac,0)+nvl(b.fund_amount,0) end) as totalCompareFrontDeskRate, " +
            "        sum(case when c.is_compare = 1 then b.sale_cost end) as totalCompareCost,"+
            "        nvl(sum(b.sale_value), 0) as totalSales, " +
            "        nvl(sum(b.sale_cost),0) as totalCost, " +
            "        sum(case when c.is_compare = 1 then b.sale_value_in end) as totalCompareSalesIn, " +
            "        sum(case when c.is_compare = 1 then NVL(b.sale_value_in,0)-nvl(b.sale_cost_in,0)+nvl(b.invadj_cost_in,0)-nvl(b.wac_in,0)+nvl(b.fund_amount_in,0) end) as totalCompareFrontDeskRateIn, " +
            "        sum(case when c.is_compare = 1 then b.sale_cost_in end) as totalCompareCostIn,"+
            "        nvl(sum(b.sale_value_in), 0) as totalSalesIn, " +
            "        nvl(sum(b.sale_cost_in),0) as totalCostIn " +
            "   from csmb_dept_kl_history a, csmb_dept_sales_history b, csmb_store c " +
            "  where a.store_id = b.store_id " +
            "    and b.store_id = c.store_id " +
            "    and a.dept_id = b.dept_id " +
            "    and a.sale_date = b.sale_date ";

    private static final String GET_HIS_RANK_DEPT_PREFIX_DEPT = "select /*+PARALLEL(A,8)*/ nvl(sum(a.sale_value), 0) as totalSales, " +
            "  a.dept_id as deptId, " +
            "  nvl(sum(NVL(a.sale_value,0)-nvl(a.sale_cost,0)+nvl(a.invadj_cost,0)-nvl(a.wac,0)+nvl(a.fund_amount,0)),0) as totalFrontDeskRate, " +
            "  sum(case when b.is_compare = 1 then a.sale_value end) as totalCompareSales, " +
            "  sum(case when b.is_compare = 1 then NVL(a.sale_value,0)-nvl(a.sale_cost,0)+nvl(a.invadj_cost,0)-nvl(a.wac,0)+nvl(a.fund_amount,0) end) as totalCompareFrontDeskRate, " +
            "  sum(case when b.is_compare = 1 then a.sale_cost end) as totalCompareCost,"+
            "  nvl(sum(a.sale_cost),0) as totalCost,  " +
            "  nvl(sum(a.sale_value_in), 0) as totalSalesIn, " +
            "  nvl(sum(NVL(a.sale_value_in,0)-nvl(a.sale_cost_in,0)+nvl(a.invadj_cost_in,0)-nvl(a.wac_in,0)+nvl(a.fund_amount_in,0)),0) as totalFrontDeskRateIn, " +
            "  sum(case when b.is_compare = 1 then a.sale_value_in end) as totalCompareSalesIn, " +
            "  sum(case when b.is_compare = 1 then NVL(a.sale_value_in,0)-nvl(a.sale_cost_in,0)+nvl(a.invadj_cost_in,0)-nvl(a.wac_in,0)+nvl(a.fund_amount_in,0) end) as totalCompareFrontDeskRateIn, " +
            "  sum(case when b.is_compare = 1 then a.sale_cost_in end) as totalCompareCostIn,"+
            "  nvl(sum(a.sale_cost_in),0) as totalCostIn  " +
            "  from csmb_dept_sales_history a, csmb_store b " +
            "  where a.store_id = b.store_id ";

    private static final String GET_HIS_RANK_CATEGORY_PREFIX = "select /*+PARALLEL(B,8)*/ " +
            "        a.new_division_name as deptName,  " +
            "        sum(case when c.is_compare = 1 then b.sale_value end) as totalCompareSales,  " +
            "        sum(case when c.is_compare = 1 then b.sale_cost end) as totalCompareCost, " +
            "        sum(case when c.is_compare = 1 then NVL(b.sale_value,0)-nvl(b.sale_cost,0)+nvl(b.invadj_cost,0)-nvl(b.wac,0)+nvl(b.fund_amount,0) end) as totalCompareFrontDeskRate,  " +
            "        nvl(sum(b.sale_value), 0) as totalSales,  " +
            "        nvl(sum(b.sale_cost),0) as totalCost,  " +
            "        sum(NVL(b.sale_value,0)-nvl(b.sale_cost,0)+nvl(b.invadj_cost,0)-nvl(b.wac,0)+nvl(b.fund_amount,0)) as totalFrontDeskRate,  " +
            "        sum(case when c.is_compare = 1 then b.sale_value_in end) as totalCompareSalesIn,  " +
            "        sum(case when c.is_compare = 1 then NVL(b.sale_value_in,0)-nvl(b.sale_cost_in,0)+nvl(b.invadj_cost_in,0)-nvl(b.wac_in,0)+nvl(b.fund_amount_in,0) end) as totalCompareFrontDeskRateIn,  " +
            "        sum(case when c.is_compare = 1 then b.sale_cost_in end) as totalCompareCostIn, " +
            "        nvl(sum(b.sale_value_in), 0) as totalSalesIn,  " +
            "        nvl(sum(b.sale_cost_in),0) as totalCostIn,  " +
            "        sum(NVL(b.sale_value_in,0)-nvl(b.sale_cost_in,0)+nvl(b.invadj_cost_in,0)-nvl(b.wac_in,0)+nvl(b.fund_amount_in,0)) as totalFrontDeskRateIn  " +
            "              from csmb_dept_sales_history b, csmb_code_dept a, csmb_store c  " +
            "              where b.dept_id = a.dept  " +
            "               and b.store_id = c.store_id ";

    private static final String GET_CUR_RANK_CATEGORY_PREFIX = "select COUNT(DISTINCT a.STORE || a.SALEDATE || a.BILLNO || a.POSID) as customerNum,  " +
            "       nvl(sum(a.gp_ecl),0) as totalRate,  " +
            "       sum(case when mall_name = 1 then a.gp_ecl end) as totalCompareRate,  " +
            "       sum(case when mall_name = 1 then a.AMT_ECL end) as totalCompareSales, " +
            "       nvl(SUM(a.AMT_ECL),0) as totalSales,  " +
            "       nvl(sum(a.gp),0) as totalRateIn,  " +
            "       sum(case when mall_name = 1 then a.gp end) as totalCompareRateIn,  " +
            "       sum(case when mall_name = 1 then a.AMT end) as totalCompareSalesIn, " +
            "       nvl(SUM(a.AMT),0) as totalSalesIn,  " +
            "       b.new_division_name as deptName " +
            " from csmb_store_saledetail a, csmb_code_dept b " +
            " where a.dept = b.dept ";

    private static final String GET_SALE_DAY_PREFIX = "select /*+PARALLEL(A,8)*/  " +
            "            to_char(a.sale_date,'dd') as timePoint, " +
            "            to_char(a.sale_date,'yyyyMMdd') as time, " +
            "            nvl(sum(a.sale_value), 0) as totalSales,  " +
            "            nvl(sum(a.sale_cost),0) as totalCost, " +
            "            nvl(sum(NVL(a.sale_value,0)-nvl(a.sale_cost,0)+nvl(a.invadj_cost,0)-nvl(a.wac,0)+nvl(a.fund_amount,0)),0) as totalFrontDeskRate,  " +
            "            sum(case when b.is_compare = 1 then a.sale_value end) as totalCompareSales,  " +
            "            sum(case when b.is_compare = 1 then a.sale_cost end) as totalCompareCost, " +
            "            sum(case when b.is_compare = 1 then NVL(a.sale_value,0)-nvl(a.sale_cost,0)+nvl(a.invadj_cost,0)-nvl(a.wac,0)+nvl(a.fund_amount,0) end) as totalCompareFrontDeskRate, " +
            "            nvl(sum(a.sale_value_in), 0) as totalSalesIn,  " +
            "            nvl(sum(a.sale_cost_in),0) as totalCostIn, " +
            "            nvl(sum(NVL(a.sale_value_in,0)-nvl(a.sale_cost_in,0)+nvl(a.invadj_cost_in,0)-nvl(a.wac_in,0)+nvl(a.fund_amount_in,0)),0) as totalFrontDeskRateIn,  " +
            "            sum(case when b.is_compare = 1 then a.sale_value_in end) as totalCompareSalesIn,  " +
            "            sum(case when b.is_compare = 1 then a.sale_cost_in end) as totalCompareCostIn, " +
            "            sum(case when b.is_compare = 1 then NVL(a.sale_value_in,0)-nvl(a.sale_cost_in,0)+nvl(a.invadj_cost_in,0)-nvl(a.wac_in,0)+nvl(a.fund_amount_in,0) end) as totalCompareFrontDeskRateIn " +
            " from csmb_dept_sales_history a, csmb_store b  " +
            " where a.store_id = b.store_id ";

    private static final String GET_SALE_MONTH_PREFIX = "select /*+PARALLEL(A,8)*/  " +
            "            to_char(a.sale_date,'MM') as timePoint, " +
            "            nvl(sum(a.sale_value), 0) as totalSales,  " +
            "            nvl(sum(a.sale_cost),0) as totalCost, " +
            "            nvl(sum(NVL(a.sale_value,0)-nvl(a.sale_cost,0)+nvl(a.invadj_cost,0)-nvl(a.wac,0)+nvl(a.fund_amount,0)),0) as totalFrontDeskRate,  " +
            "            sum(case when b.is_compare = 1 then a.sale_value end) as totalCompareSales,  " +
            "            sum(case when b.is_compare = 1 then a.sale_cost end) as totalCompareCost, " +
            "            sum(case when b.is_compare = 1 then NVL(a.sale_value,0)-nvl(a.sale_cost,0)+nvl(a.invadj_cost,0)-nvl(a.wac,0)+nvl(a.fund_amount,0) end) as totalCompareFrontDeskRate, " +
            "            nvl(sum(a.sale_value_in), 0) as totalSalesIn,  " +
            "            nvl(sum(a.sale_cost_in),0) as totalCostIn, " +
            "            nvl(sum(NVL(a.sale_value_in,0)-nvl(a.sale_cost_in,0)+nvl(a.invadj_cost_in,0)-nvl(a.wac_in,0)+nvl(a.fund_amount_in,0)),0) as totalFrontDeskRateIn,  " +
            "            sum(case when b.is_compare = 1 then a.sale_value_in end) as totalCompareSalesIn,  " +
            "            sum(case when b.is_compare = 1 then a.sale_cost_in end) as totalCompareCostIn, " +
            "            sum(case when b.is_compare = 1 then NVL(a.sale_value_in,0)-nvl(a.sale_cost_in,0)+nvl(a.invadj_cost_in,0)-nvl(a.wac_in,0)+nvl(a.fund_amount_in,0) end) as totalCompareFrontDeskRateIn " +
            " from csmb_dept_sales_history a, csmb_store b  " +
            " where a.store_id = b.store_id ";

    private static final String GET_HIS_REGION_RANK_PREFIX = "select /*+PARALLEL(A,8)*/  " +
            "            b.area_id as areaId,  " +
            "            max(b.area_name) as areaName, " +
            "            nvl(sum(a.sale_value), 0) as totalSales,   " +
            "            sum(a.sale_cost) as totalCost, " +
            "            nvl(sum(a.sale_value - a.sale_cost + a.invadj_cost - a.wac + a.fund_amount),0) as totalFrontDeskRate,    " +
            "            sum(case when b.is_compare = 1 then a.sale_value end) as totalCompareSales,   " +
            "            sum(case when b.is_compare = 1 then a.sale_cost end) as totalCompareCost,   " +
            "            sum(case when b.is_compare = 1 then a.sale_value - a.sale_cost + a.invadj_cost - a.wac + a.fund_amount end) as totalCompareFrontDeskRate,        " +
            "            nvl(sum(a.sale_value_in), 0) as totalSalesIn,   " +
            "            nvl(sum(a.sale_cost_in),0) as totalCostIn, " +
            "            nvl(sum(a.sale_value_in-a.sale_cost_in+a.invadj_cost_in-a.wac_in+a.fund_amount_in),0) as totalFrontDeskRateIn,         " +
            "            sum(case when b.is_compare = 1 then a.sale_value_in end) as totalCompareSalesIn,   " +
            "            sum(case when b.is_compare = 1 then a.sale_cost_in end) as totalCompareCostIn,   " +
            "            sum(case when b.is_compare = 1 then a.sale_value_in - a.sale_cost_in + a.invadj_cost_in - a.wac_in + a.fund_amount_in end) as totalCompareFrontDeskRateIn   " +
            "            from csmb_dept_sales_history a, csmb_store b   " +
            "            where a.store_id = b.store_id ";

    private static final String GET_AREA_RANK_PREFIX = " select a.region as areaId, " +
            "       max(b.area_name) as areaName, " +
            "       nvl(sum(a.amt_ecl), 0) as totalSales, " +
            "       sum(case when mall_name = 1 then a.amt_ecl end) as totalCompareSales, " +
            "       sum(case when mall_name = 1 then a.gp_ecl end) as totalCompareRate, " +
            "       nvl(sum(a.gp_ecl), 0) as totalRate, " +
            "       nvl(sum(a.amt), 0) as totalSalesIn, " +
            "       sum(case when mall_name = 1 then a.amt end) as totalCompareSalesIn," +
            "       sum(case when mall_name = 1 then a.gp end) as totalCompareRateIn, " +
            "       nvl(sum(a.gp), 0) as totalRateIn " +
            "  from CSMB_DEPT_SALES a, csmb_store b " +
            " where a.store_id = b.store_id ";

    private static final String GET_REGION_KL_PREFIX = " SELECT region as areaId, nvl(sum(kl),0) as customerNum FROM  CSMB_STORE_KL ";

    public List<RankDetail> queryHisAreaRankDetail(PageRequest param,Date start,Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_REGION_RANK_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.sale_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }else{
            sb.append(" and a.sale_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
            sb.append(" and a.sale_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and b.province_id = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){

                sb.append(" and b.province_id in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and b.area_id = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){

                sb.append(" and b.area_id in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept_id = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){

                sb.append(" and a.dept_id in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by b.area_id ");
        return super.queryForList(sb.toString(),RANK_DETAIL_RM,paramList.toArray());
    }

    /**
     * 查询历史日线和月线（优化版）
     * @param param
     * @param start
     * @param end
     * @param type(2-日线，3-月线)
     * @return
     */
    public List<ReportDetail> queryHistoryReportDetail(ReportParamRequest param,Date start,Date end,int type){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        if(2 == type){
            sb.append(GET_SALE_DAY_PREFIX);
        }else if(3 == type){
            sb.append(GET_SALE_MONTH_PREFIX);
        }
        if(null == end){
            sb.append(" and a.sale_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }else{
            sb.append(" and a.sale_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
            sb.append(" and a.sale_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and b.province_id = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){

                sb.append(" and b.province_id in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and b.area_id = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){

                sb.append(" and b.area_id in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and b.store_id = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){

                sb.append(" and b.store_id in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept_id = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){

                sb.append(" and a.dept_id in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(2 == type){
            sb.append(" group by sale_date ");
        }else if(3 == type){
            sb.append(" group by to_char(a.sale_date,'MM') ");
        }
        return super.queryForList(sb.toString(),REPORT_DETAIL_RM,paramList.toArray());
    }

    /**
     * 查询实时品类数据
     * @param pageRequest
     * @param start
     * @param end
     * @return
     */
    public List<RankDetail> queryCurCategoryRank(RankParamRequest pageRequest,Date start,Date end){
        StringBuilder sb = new StringBuilder(GET_CUR_RANK_CATEGORY_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.saledate = '")
                    .append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
        }else{
            sb.append(" and a.saledate <= '")
                    .append(DateUtils.parseDateToStr("yyyyMMdd",end))
                    .append("' ");
            sb.append(" and a.saledate >= '")
                    .append(DateUtils.parseDateToStr("yyyyMMdd",start))
                    .append("' ");
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
                sb.append(" and a.store = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        if(StringUtils.isNotEmpty(pageRequest.getQueryCategorys())){
            sb.append(" and b.new_division_name in (").append(pageRequest.getQueryCategorys()).append(") ");
        }
        sb.append(" group by b.new_division_name ");
        return super.queryForList(sb.toString(),RANK_DETAIL_RM,paramList.toArray());
    }

    /**
     * 查询历史品类数据(不包含客流)
     * @param pageRequest
     * @param start
     * @param end
     * @return
     */
    public List<RankDetail> queryHisCategoryRank(RankParamRequest pageRequest,Date start,Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_RANK_CATEGORY_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and b.sale_date = date '")
                    .append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }else{
            sb.append(" and b.sale_date <= date '")
                    .append(DateUtils.parseDateToStr("yyyy-MM-dd",end))
                    .append("' ");
            sb.append(" and b.sale_date >= date '")
                    .append(DateUtils.parseDateToStr("yyyy-MM-dd",start))
                    .append("' ");
        }
        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and c.province_id = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){

                sb.append(" and c.province_id in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and c.area_id = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){

                sb.append(" and c.area_id in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and c.store_id = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and c.store_id in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        if(StringUtils.isNotEmpty(pageRequest.getQueryCategorys())){
            sb.append(" and a.new_division_name in (").append(pageRequest.getQueryCategorys()).append(") ");
        }
        sb.append(" group by a.new_division_name ");
        return super.queryForList(sb.toString(),RANK_DETAIL_RM,paramList.toArray());
    }

    public List<RankDetail> queryRankDetailForDept(RankParamRequest pageRequest){
        StringBuilder sb = new StringBuilder(GET_HIS_RANK_DEPT_PREFIX_DEPT);
        List<Object> paramList = new ArrayList<Object>();
        if(null == pageRequest.getEnd()){
            sb.append(" and a.sale_date = date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd"))
                    .append("' ");
        }else{
            sb.append(" and a.sale_date <= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd"))
                    .append("' ");
            sb.append(" and a.sale_date >= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd"))
                    .append("' ");
        }
        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and b.province_id = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){

                sb.append(" and b.province_id in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and b.area_id = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){

                sb.append(" and b.area_id in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and b.store_id = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){

                sb.append(" and b.store_id in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and a.dept_id = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){

                sb.append(" and a.dept_id in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by a.dept_id");
        List<RankDetail> list = super.queryForList(sb.toString(),RANK_DETAIL_RM,paramList.toArray());
        return list;
    }

    /**
     * 查询vip数量
     * @param pageRequest
     * @return
     */
    public List<MemberPermeabilityPo> queryMember(PageRequest pageRequest){
        StringBuilder sb = new StringBuilder(GET_MEMBER_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == pageRequest.getEnd()){
            sb.append(" and saledate = ? ");
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getStart()));
        }else{
            sb.append(" and saledate <= ? ");
            sb.append(" and saledate >= ? ");
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getEnd()));
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getStart()));
        }
        sb.append(getCommonCountSql(pageRequest, paramList));
        sb.append(" group by decode(vipno, null, 'N', 'Y'),(case when channel in ('YUNPOS', 'VIP', 'WX', 'WX1', 'WX2', 'WX3', 'DD1', 'JD1', 'KJG') or channel is null then 'off' else 'on' end)");

        return super.queryForList(sb.toString(), MEMBER_RM, paramList.toArray());
    }

    /**
     * 查询排行榜实时分组数据(1-门店，2-大类)
     * @param pageRequest
     * @return
     */
    public List<RankDetail> queryCurrentRankDetail(PageRequest pageRequest, int type){
        List<RankDetail> result = new ArrayList<RankDetail>();
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        List<Object> klparamList = new ArrayList<Object>();
        if(1 == type){
            //查询客流
            sb.append(getCurrentStoreSql(pageRequest, paramList));
            result = super.queryForList(sb.toString(), RANK_DETAIL_RM, paramList.toArray());
            if(null != result && result.size() > 0){
                //查询客流
                StringBuilder klSql = new StringBuilder();
                //查询不包含大类的数据
                klSql.append(getCurrentKlAndStoreSqlProfix(pageRequest, klparamList));
                klSql.append(getCurrentCommenKlSql(pageRequest, klparamList));
                klSql.append(" group by store ");
                List<RankDetail> klList = super.queryForList(klSql.toString(), RANK_DETAIL_RM, klparamList.toArray());
                if(null != klList && klList.size() > 0){
                    for(RankDetail kl : klList){
                        for(RankDetail re : result){
                            if(kl.getStoreId().equals(re.getStoreId())){
                                re.setCustomerNum(kl.getCustomerNum());
                            }
                        }
                    }
                }
            }
        }else{
            sb.append(getCurrentCommenSqlProfixByDept(pageRequest, paramList, 4));
            sb.append(getCurrentCommenSqlByDept(pageRequest, paramList));
            sb.append(getCurrentCommenGroupSqlByDept(4));
            result = super.queryForList(sb.toString(), RANK_DETAIL_RM, paramList.toArray());
        }
        return result;
    }

    /**
     * 查询区域排行榜实时分组数据
     * @param pageRequest
     * @return
     */
    public List<RankDetail> queryCurrentAreaRankDetail(PageRequest pageRequest){
        List<RankDetail> result = new ArrayList<RankDetail>();
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        List<Object> klparamList = new ArrayList<Object>();
        //查询客流
        sb.append(getCurrentAreaSql(pageRequest, paramList));
        result = super.queryForList(sb.toString(), RANK_DETAIL_RM, paramList.toArray());
        if(null != result && result.size() > 0){
            //查询客流
            StringBuilder klSql = new StringBuilder(GET_REGION_KL_PREFIX);
            if(pageRequest.getEnd() == null){
                sb.append(" where saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getStart())).append("' ");
            }else{
                sb.append(" where saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd", new Date())).append("' ");
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
            klSql.append(" group by region ");
            List<RankDetail> klList = super.queryForList(klSql.toString(), RANK_DETAIL_RM, klparamList.toArray());
            if(null != klList && klList.size() > 0){
                for(RankDetail kl : klList){
                    for(RankDetail re : result){
                        if(kl.getAreaId().equals(re.getAreaId())){
                            re.setCustomerNum(kl.getCustomerNum());
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查询当前日线，时线，月线（包含大类）
     * @param param
     * @return
     */
    public List<ReportDetail> currentReportDetailByDept(ReportParamRequest param){
        List<ReportDetail> result = null;
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getCurrentCommenSqlProfixByDept(param, paramList, param.getType()));
        sb.append(getCurrentCommenSqlByDept(param, paramList));
        sb.append(getCurrentCommenGroupSqlByDept(param.getType()));
        sb.append(getCurrentCommenOrderSqlByDept(param.getType()));
        result = super.queryForList(sb.toString(), REPORT_DETAIL_RM, paramList.toArray());
        if(null != result){
            for(ReportDetail v : result){
                BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(v.getTotalSales())?"0":v.getTotalSales());
                BigDecimal totalRate = new BigDecimal(StringUtils.isEmpty(v.getTotalRate())?"0":v.getTotalRate());

                BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(v.getTotalSalesIn())?"0":v.getTotalSalesIn());
                BigDecimal totalRateIn = new BigDecimal(StringUtils.isEmpty(v.getTotalRateIn())?"0":v.getTotalRateIn());
                if(totalSale.compareTo(BigDecimal.ZERO) != 0){
                    v.setTotalprofit(totalRate.divide(totalSale, 4, BigDecimal.ROUND_HALF_UP).toString());
                }

                if(totalSaleIn.compareTo(BigDecimal.ZERO) != 0){
                    v.setTotalprofitIn(totalRateIn.divide(totalSaleIn, 4, BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        }
        return result;
    }

    /**
     * 查询当前日线，时线，月线（不包含大类）
     * @param param
     * @return
     */
    public List<ReportDetail> currentSingleReportDetail(ReportParamRequest param){
        List<ReportDetail> result = null;
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder gpSql = new StringBuilder();
        gpSql.append(getCurrentGpAndAmtSqlProfix(param, paramList, param.getType()));
        gpSql.append(getCurrentCommenGpAndAmtSql(param, paramList));
        gpSql.append(getCurrentCommenGpAndAmtGroupSql(param.getType()));
        gpSql.append(getCurrentCommenGpAndAmtOrderSql(param.getType()));
        result = super.queryForList(gpSql.toString(), REPORT_DETAIL_RM, paramList.toArray());
        if(null != result){
            for(ReportDetail v : result){
                BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(v.getTotalSales())?"0":v.getTotalSales());
                BigDecimal totalRate = new BigDecimal(StringUtils.isEmpty(v.getTotalRate())?"0":v.getTotalRate());

                BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(v.getTotalSalesIn())?"0":v.getTotalSalesIn());
                BigDecimal totalRateIn = new BigDecimal(StringUtils.isEmpty(v.getTotalRateIn())?"0":v.getTotalRateIn());
                if(totalSale.compareTo(BigDecimal.ZERO) !=0){
                    v.setTotalprofit(totalRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                }

                if(totalSaleIn.compareTo(BigDecimal.ZERO) !=0){
                    v.setTotalprofitIn(totalRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        }
        return result;
    }

    /**
     * 查询当天生鲜(1-生鲜，2-非生鲜)
     * @param pageRequest
     * @return
     */
    public Fresh queryCurrentFreshInfo(PageRequest pageRequest, String fresh, int type){
        List<Object> paramList = new ArrayList<Object>();
        Fresh result = new Fresh();
        StringBuilder sb = new StringBuilder();
        sb.append(getCurrentCommenSqlProfixByDept(pageRequest, paramList, -1));
        sb.append(getCurrentCommenSqlByDept(pageRequest, paramList));
        if(2 == type){
            sb.append(" and dept not in ( ");
            sb.append(fresh);
            sb.append(" ) ");
        }
        TotalSalesAndProfit totalSalesAndProfit = super.queryForObject(sb.toString(),
                TOTAL_SALE_AND_PROFIT_RM, paramList.toArray());
        if(null != totalSalesAndProfit){

            BigDecimal sales = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSales()) ? "0"
                    : totalSalesAndProfit.getTotalSales());

            BigDecimal profitPrice = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalRate()) ? "0"
                    : totalSalesAndProfit.getTotalRate());

            BigDecimal salesIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSalesIn()) ? "0"
                    : totalSalesAndProfit.getTotalSalesIn());

            BigDecimal profitPriceIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalRateIn()) ? "0"
                    : totalSalesAndProfit.getTotalRateIn());

            BigDecimal customerNum = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getCustomerNum()) ? "0"
                    : totalSalesAndProfit.getCustomerNum());

            result.setCustomerNum(customerNum.toString());
            
            result.setTotalSales(sales.toString());
            result.setTotalProfitPrice(profitPrice.toString());
            if(sales.compareTo(BigDecimal.ZERO) != 0) {
            	result.setTotalProfit(profitPrice.divide(sales,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(customerNum.compareTo(BigDecimal.ZERO) != 0) {
            	result.setCustomerSingerPrice(sales.divide(customerNum,2, BigDecimal.ROUND_HALF_UP).toString());
            }
            

            result.setTotalSalesIn(salesIn.toString());
            result.setTotalProfitPriceIn(profitPriceIn.toString());
            if(salesIn.compareTo(BigDecimal.ZERO) != 0) {
            	result.setTotalProfitIn(profitPriceIn.divide(salesIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(customerNum.compareTo(BigDecimal.ZERO) != 0) {
            	result.setCustomerSingerPriceIn(salesIn.divide(customerNum,2, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        return result;
    }

    /**
     * 查询家电
     * @param pageRequest
     * @return
     */
    public HomeAppliance queryHomeApplianceInfo(PageRequest pageRequest){
        HomeAppliance homeAppliance = null;
        List<Object> paramList = new ArrayList<Object>();
        String sql = getHomeApplianceSql(pageRequest, paramList);
        homeAppliance = super.queryForObject(sql, TOTAL_HOME_RM, paramList.toArray());
        if(null != homeAppliance){
            BigDecimal sales = new BigDecimal(StringUtils.isEmpty(homeAppliance.getTotalSale()) ? "0" : homeAppliance.getTotalSale());
            BigDecimal cost = new BigDecimal(StringUtils.isEmpty(homeAppliance.getTotalCost()) ? "0" : homeAppliance.getTotalCost());

            BigDecimal salesIn = new BigDecimal(StringUtils.isEmpty(homeAppliance.getTotalSaleIn()) ? "0" : homeAppliance.getTotalSaleIn());
            BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(homeAppliance.getTotalCostIn()) ? "0" : homeAppliance.getTotalCostIn());
            if(sales.compareTo(BigDecimal.ZERO) != 0){
                homeAppliance.setTotalProfit(sales.subtract(cost).divide(sales,4, BigDecimal.ROUND_HALF_UP).toString());
            }

            if(salesIn.compareTo(BigDecimal.ZERO) != 0){
                homeAppliance.setTotalProfitIn(salesIn.subtract(costIn).divide(salesIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            homeAppliance.setTotalRate(sales.subtract(cost).toString());
            homeAppliance.setTotalRateIn(salesIn.subtract(costIn).toString());
        }
        return homeAppliance;
    }

    /**
     * 查询库存数量
     * @param pageRequest
     * @return
     */
    public TotalSalesAndProfit getStockNum(PageRequest pageRequest){
        List<Object> paramList = new ArrayList<>();
        String stockNumSql = getStockNumSql(pageRequest, paramList);
        return queryInfo(stockNumSql, paramList);
    }

    /**
     * 查询当前总客流（不包含大类）
     * @param pageRequest
     * @return
     */
    public TotalSalesAndProfit currentSingleKl(PageRequest pageRequest){
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder klSql = new StringBuilder();
        //查询不包含大类的数据
        klSql.append(getCurrentKlSqlProfix(pageRequest, paramList));
        klSql.append(getCurrentCommenKlSql(pageRequest, paramList));
        //查询客流
        TotalSalesAndProfit kl = queryInfo(klSql.toString(), paramList);
        return kl;
    }

    /**
     * 查询实时可比（包含大类）
     * @param pageRequest
     * @return
     */
    public TotalSalesAndProfit queryCurrentCompareDataHaveDept(PageRequest pageRequest){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getCurrentCommenSqlProfixByDept(pageRequest, paramList, -1));
        sb.append(getCurrentCommenSqlByDept(pageRequest, paramList));
        sb.append(getCompareCommonSql());
        TotalSalesAndProfit response = queryInfo(sb.toString(), paramList);
        if(null != response){
            BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(response.getTotalSales())?"0":response.getTotalSales());
            BigDecimal totalRate = new BigDecimal(StringUtils.isEmpty(response.getTotalRate())?"0":response.getTotalRate());

            BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(response.getTotalSalesIn())?"0":response.getTotalSalesIn());
            BigDecimal totalRateIn = new BigDecimal(StringUtils.isEmpty(response.getTotalRateIn())?"0":response.getTotalRateIn());
            if(totalSale.compareTo(BigDecimal.ZERO) != 0){
                response.setTotalProfit(totalRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                response.setScanningProfitRate(totalRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
            }

            if(totalSaleIn.compareTo(BigDecimal.ZERO) != 0){
                response.setTotalProfitIn(totalRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                response.setScanningProfitRateIn(totalRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        return  queryInfo(sb.toString(), paramList);
    }

    /**
     * 查询实时可比（不包含大类）
     * @param pageRequest
     * @return
     */
    public TotalSalesAndProfit queryCurrentCompareDataNotDept(PageRequest pageRequest){
        StringBuilder gpSql = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        gpSql.append(getCurrentGpAndAmtSqlProfix(pageRequest, paramList, -1));
        gpSql.append(getCurrentCommenGpAndAmtSql(pageRequest, paramList));
        gpSql.append(getCompareCommonSql());
        TotalSalesAndProfit response = queryInfo(gpSql.toString(), paramList);
        if(null != response){
            BigDecimal totalRate = new BigDecimal(StringUtils.isEmpty(response.getTotalRate())?"0":response.getTotalRate());
            BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(response.getTotalSales())?"0":response.getTotalSales());

            BigDecimal totalRateIn = new BigDecimal(StringUtils.isEmpty(response.getTotalRateIn())?"0":response.getTotalRateIn());
            BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(response.getTotalSalesIn())?"0":response.getTotalSalesIn());
            if(totalSale.compareTo(BigDecimal.ZERO) != 0){
                response.setTotalProfit(totalRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                response.setScanningProfitRate(totalRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(totalSaleIn.compareTo(BigDecimal.ZERO) != 0){
                response.setTotalProfitIn(totalRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                response.setScanningProfitRateIn(totalRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        return  response;
    }

    /**
     * 查询当前总销售额和毛利率（不包含大类）
     * @param pageRequest
     * @return
     */
    public TotalSalesAndProfit currentTotalGpAndAmtNotDept(PageRequest pageRequest){
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder gpSql = new StringBuilder();
        gpSql.append(getCurrentGpAndAmtSqlProfix(pageRequest, paramList, -1));
        gpSql.append(getCurrentCommenGpAndAmtSql(pageRequest, paramList));
        //查询gp
        TotalSalesAndProfit gp = queryInfo(gpSql.toString(), paramList);
        if(null != gp){
            BigDecimal totalRate = new BigDecimal(StringUtils.isEmpty(gp.getTotalRate()) ? "0":gp.getTotalRate());
            BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(gp.getTotalSales()) ? "0":gp.getTotalSales());
            BigDecimal totalRateIn = new BigDecimal(StringUtils.isEmpty(gp.getTotalRateIn()) ? "0":gp.getTotalRateIn());
            BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(gp.getTotalSalesIn()) ? "0":gp.getTotalSalesIn());
            if(totalSale.compareTo(BigDecimal.ZERO) != 0){
                gp.setTotalProfit(totalRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                gp.setScanningProfitRate(totalRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(totalSaleIn.compareTo(BigDecimal.ZERO) != 0){
                gp.setTotalProfitIn(totalRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                gp.setScanningProfitRateIn(totalRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        return gp;
    }

    /**
     * 查询当前总销售额和毛利率（包含大类）
     * @param pageRequest
     * @return
     */
    public TotalSalesAndProfit currentTotalGpAndAmtHaveDept(PageRequest pageRequest){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getCurrentCommenSqlProfixByDept(pageRequest, paramList, -1));
        sb.append(getCurrentCommenSqlByDept(pageRequest, paramList));
        TotalSalesAndProfit response = queryInfo(sb.toString(), paramList);
        if(null != response){
            BigDecimal totalRate = new BigDecimal(StringUtils.isEmpty(response.getTotalRate()) ? "0" : response.getTotalRate());
            BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(response.getTotalSales()) ? "0" : response.getTotalSales());
            BigDecimal totalRateIn = new BigDecimal(StringUtils.isEmpty(response.getTotalRateIn()) ? "0" : response.getTotalRateIn());
            BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(response.getTotalSalesIn()) ? "0" : response.getTotalSalesIn());
            if(totalSale.compareTo(BigDecimal.ZERO) != 0){
                response.setTotalProfit(totalRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                response.setScanningProfitRate(totalRate.divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(totalSaleIn.compareTo(BigDecimal.ZERO) != 0){
                response.setTotalProfitIn(totalRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                response.setScanningProfitRateIn(totalRateIn.divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        return response;
    }

    /**
     * 库存数量sql
     * @param pageRequest
     * @param paramList
     * @return
     */
    public String getStockNumSql(PageRequest pageRequest,List<Object> paramList){
        StringBuilder sb = new StringBuilder();
        sb.append(GET_STOCK_NUM_PREFIX);

        if(DateUtils.getBetweenDay(pageRequest.getStart(), new Date()) >= 0){
            //查历史的某一天数据
            sb.append(" and a.stock_date = date '");
            sb.append(DateUtil.getDateFormat(DateUtils.addDays(pageRequest.getStart(),-1),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and a.stock_date = date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }


        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and b.province_id  = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){

                sb.append(" and b.province_id in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and b.area_id  = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){

                sb.append(" and b.area_id in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and a.store_id = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){

                sb.append(" and a.store_id in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and a.dept_id = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){

                sb.append(" and a.dept_id in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }



    /**
     * 实时数据销售额和毛利率前缀（不查询大类的时候）(1-时线,2-日线,3-月线,其它默认查询汇总)
     * @return
     */
    private String getCurrentGpAndAmtSqlProfix(PageRequest pageRequest, List<Object> paramList, Integer type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb = sb.append(GET_REAL_TOTAL_SALES_PROFIT_PREFIX_HOUR);
                break;
            case 2:
                sb = sb.append(GET_REAL_TOTAL_SALES_PROFIT_PREFIX_DAY);
                break;
            case 3:
                sb = sb.append(GET_REAL_TOTAL_SALES_PROFIT_PREFIX_MONTH);
                break;
            default:
                sb = sb.append(GET_REAL_TOTAL_SALES_PROFIT_PREFIX);
                break;
        }
        //查询当天数据
        sb.append(" where sale_date  = ? ");
        if(pageRequest.getEnd() == null){
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getStart()));
        }else{
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", new Date()));
        }
        return sb.toString();
    }

    /**
     * 实时数据销售额和毛利率省，区域，门店拼接（不查询大类的时候）
     * @param pageRequest
     * @return
     */
    private String getCurrentCommenGpAndAmtSql(PageRequest pageRequest, List<Object> paramList){
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
                sb.append(" and store_id = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){

                sb.append(" and store_id in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }

    /**
     * 增加可比拼接
     * @return
     */
    private String getCompareCommonSql(){
        return " and mall_name = 1 ";
    }

    /**
     * 实时数据包含大类前缀(1-时线,2-日线,3-月线,4-大类排行榜，其它默认查询汇总)
     * @return
     */
    private String getCurrentCommenSqlProfixByDept(PageRequest pageRequest, List<Object> paramList, Integer type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb = sb.append(GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX_HOUR);
                break;
            case 2:
                sb = sb.append(GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX_DAY);
                break;
            case 3:
                sb = sb.append(GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX_MONTH);
                break;
            case 4:
                sb = sb.append(GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX_DEPT);
                break;
            default:
                sb = sb.append(GET_REAL_TOTAL_DEPT_SALES_PROFIT_PREFIX);
                break;
        }
        sb.append(" where saledate  = ? ");
        if(pageRequest.getEnd() == null){
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getStart()));
        }else{
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", new Date()));
        }
        return sb.toString();
    }

    /**
     * 实时数据包含大类省，区域，门店，大类拼接
     * @param pageRequest
     * @return
     */
    private String getCurrentCommenSqlByDept(PageRequest pageRequest, List<Object> paramList){
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
        return sb.toString();
    }

    /**
     * 实时数据客流前缀（不查询大类的时候）
     * @return
     */
    private String getCurrentKlSqlProfix(PageRequest pageRequest, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_REAL_KL_PREFIX);
        sb.append(" where saledate = ? ");
        if(pageRequest.getEnd() == null){
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getStart()));
        }else{
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", new Date()));
        }
        return sb.toString();
    }

    /**
     * 实时数据客流前缀（不查询大类的时候）
     * @return
     */
    private String getCurrentKlAndStoreSqlProfix(PageRequest pageRequest, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_REAL_KL_PREFIX_STORE);
        sb.append(" where saledate = ? ");
        if(pageRequest.getEnd() == null){
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getStart()));
        }else{
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", new Date()));
        }
        return sb.toString();
    }

    /**
     * 实时数据客流省，区域，门店拼接（不查询大类的时候）
     * @param pageRequest
     * @return
     */
    private String getCurrentCommenKlSql(PageRequest pageRequest, List<Object> paramList){
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
                sb.append(" and store  = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){

                sb.append(" and store in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }

    /**
     * 家电sql
     * @param pageRequest
     * @param paramList
     * @return
     */
    private String getHomeApplianceSql(PageRequest pageRequest, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_HOME_PREFIX);
        if(null == pageRequest.getEnd()){
            sb.append(" and a.tran_Date = date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and a.tran_Date <= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and a.tran_Date >= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and b.province_id = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){

                sb.append(" and b.province_id in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and b.area_id = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){

                sb.append(" and b.area_id in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and b.store_id = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){

                sb.append(" and b.store_id in ( ");
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
        return sb.toString();
    }


    private TotalSalesAndProfit queryInfo(String sql, List<Object> paramList){
        return super.queryForObject(sql, TOTAL_SALE_AND_PROFIT_RM, paramList.toArray());
    }

    /**
     * 类型(1-时线,2-日线,3-月线)进行分组（不包含大类）
     * @param type
     * @return
     */
    private String getCurrentCommenGpAndAmtGroupSql(int type){
        String sb = "";
        switch (type){
            case 1:
                sb = " group by sale_hour ";
                break;
            case 2:
                sb = " group by sale_date ";
                break;
            case 3:
                sb = " group by substr(sale_date, 5,2) ";
                break;
            default:
                sb = "";
                break;
        }
        return sb;
    }

    /**
     * 类型(1-时线,2-日线,3-月线)进行分组（不包含大类）
     * @param type
     * @return
     */
    private String getCurrentCommenGpAndAmtOrderSql(int type){
        String sb = "";
        switch (type){
            case 1:
                sb = " order by sale_hour ";
                break;
            case 2:
                sb = " order by sale_date ";
                break;
            case 3:
                sb = " order by substr(sale_date, 5,2) ";
                break;
            default:
                sb = "";
                break;
        }
        return sb;
    }

    /**
     * 类型(1-时线,2-日线,3-月线,4-4-大类排行榜)进行分组（包含大类）
     * @param type
     * @return
     */
    private String getCurrentCommenGroupSqlByDept(int type){
        String sb = "";
        switch (type){
            case 1:
                sb = " group by substr(saletime, 1, 2) ";
                break;
            case 2:
                sb = " group by saledate ";
                break;
            case 3:
                sb = " group by substr(saledate, 5, 2) ";
                break;
            case 4:
                sb = " group by dept ";
                break;
            default:
                sb = "";
                break;
        }
        return sb;
    }

    /**
     * 类型(1-时线,2-日线,3-月线,4-4-大类排行榜)进行分组（包含大类）
     * @param type
     * @return
     */
    private String getCurrentCommenOrderSqlByDept(int type){
        String sb = "";
        switch (type){
            case 1:
                sb = " order by substr(saletime, 1, 2) ";
                break;
            case 2:
                sb = " order by saledate ";
                break;
            case 3:
                sb = " order by substr(saledate, 5, 2) ";
                break;
            default:
                sb = "";
                break;
        }
        return sb;
    }

    /**
     * 查询当天门店数据sql
     * @param pageRequest
     * @param paramList
     * @return
     */
    private String getCurrentStoreSql(PageRequest pageRequest, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_REAL_TOTAL_SALES_PROFIT_PREFIX_STORE);
        sb.append(" and a.sale_date  = ? ");
        if(pageRequest.getEnd() == null){
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getStart()));
        }else {
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", new Date()));
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
                sb.append(" and a.store_id = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){

                sb.append(" and a.store_id in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and a.dept_id  = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){

                sb.append(" and a.dept_id in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by a.store_id ");
        return sb.toString();
    }

    /**
     * 查询当天区域数据sql
     * @param pageRequest
     * @param paramList
     * @return
     */
    private String getCurrentAreaSql(PageRequest pageRequest, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_AREA_RANK_PREFIX);
        sb.append(" and a.sale_date  = ? ");
        if(pageRequest.getEnd() == null){
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", pageRequest.getStart()));
        }else {
            paramList.add(DateUtils.parseDateToStr("yyyyMMdd", new Date()));
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
        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and a.dept_id  = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){

                sb.append(" and a.dept_id in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by a.region ");
        return sb.toString();
    }

    private String getCommonCountSql(PageRequest pageRequest, List<Object> paramList){
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
                sb.append(" and store  = ? ");
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
        return sb.toString();
    }

    /**
     * 排行榜大类历史查询
     * @param pageRequest
     * @return
     */
    public List<RankDetail> queryHisRankDetailForDept(PageRequest pageRequest){
        List<RankDetail> result = null;
        List<Object> paramList = new ArrayList<>();
        StringBuilder sb = new StringBuilder(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_DEPT);

        if(null == pageRequest.getEnd()){
            sb.append(" and b.sale_date = date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and b.sale_date <= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and b.sale_date >= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and c.province_id = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){
                sb.append(" and c.province_id in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");

            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and c.area_id = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){
                sb.append(" and c.area_id in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and c.store_id  = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and c.store_id in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and a.dept_id = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){
                sb.append(" and a.dept_id in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }

        sb.append(" group by a.dept_id");
        result = super.queryForList(sb.toString(), RANK_DETAIL_RM, paramList.toArray());
        return result;
    }


}
