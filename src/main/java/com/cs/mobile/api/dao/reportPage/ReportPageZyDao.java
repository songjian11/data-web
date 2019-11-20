package com.cs.mobile.api.dao.reportPage;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
import com.cs.mobile.api.model.reportPage.request.RankParamRequest;
import com.cs.mobile.api.model.reportPage.request.ReportParamRequest;
import com.cs.mobile.api.model.reportPage.RankDetail;
import com.cs.mobile.api.model.reportPage.ReportDetail;
import com.cs.mobile.api.model.reportPage.TotalSalesAndProfit;
import com.cs.mobile.api.model.reportPage.MemberPermeabilityPo;
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
 * 查询历史数据
 * 对应zypp库
 */
//@Slf4j
@Repository
public class ReportPageZyDao extends AbstractDao {
    private static final RowMapper<TotalSalesAndProfit> TOTAL_SALE_AND_PROFIT_RM = new BeanPropertyRowMapper<TotalSalesAndProfit>(TotalSalesAndProfit.class);
    private static final RowMapper<RankDetail> RANK_DETAIL_RM = new BeanPropertyRowMapper<RankDetail>(RankDetail.class);
    private static final RowMapper<ReportDetail> REPORT_DETAIL_RM = new BeanPropertyRowMapper<ReportDetail>(ReportDetail.class);
    private static final RowMapper<MemberPermeabilityPo> MEMBER_RM = new BeanPropertyRowMapper<MemberPermeabilityPo>(MemberPermeabilityPo.class);
    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX = "SELECT /*+PARALLEL(A,8)*/  " +
            "       count(distinct a.tran_seq_no) as customerNum, " +
            "       nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSales, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty), 0) as totalCost, " +
            "       nvl(sum(a.total_real_amt),0) as totalSalesIn, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100)), 0) as totalCostIn " +
            "       FROM zypp.sale_item a, zypp.inf_item b, zypp.inf_store c ";

    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_STORE = "SELECT /*+PARALLEL(A,8)*/  " +
            "       count(distinct a.tran_seq_no) as customerNum, " +
            "       c.store as storeId , " +
            "       max(c.store_name) as storeName, " +
            "       nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSales, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty),0) as totalCost, " +
            "       nvl(sum(a.total_real_amt),0) as totalSalesIn, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100)), 0) as totalCostIn " +
            "       FROM zypp.sale_item a, zypp.inf_item b, zypp.inf_store c ";

    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_HOUR = "SELECT /*+PARALLEL(A,8)*/  " +
            "       substr(a.tran_datetime, 1, 2) as timePoint, " +
            "       nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSales, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty),0) as totalCost, " +
            "       nvl(sum(a.total_real_amt),0) as totalSalesIn, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100)), 0) as totalCostIn " +
            "       FROM zypp.sale_item a, zypp.inf_item b, zypp.inf_store c ";



    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_HOUR_PROPERTY = "    select a.hour as timePoint, " +
            "    nvl(sum(a.total_real_amt_ecl),0) as totalSales, " +
            "    nvl(sum(a.total_cost_ecl),0) as totalCost, " +
            "    nvl(sum(a.total_real_amt),0) as totalSalesIn, " +
            "    nvl(sum(a.total_cost),0) as totalCostIn " +
            "    from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a, zypp.inf_store b " +
            "    where a.store = b.store ";

    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_DAY = "SELECT /*+PARALLEL(A,8)*/  " +
            "       to_char(a.business_date,'dd') as timePoint, " +
            "       to_char(a.business_date,'yyyyMMdd') as time, " +
            "       nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSales, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty),0) as totalCost, " +
            "       nvl(sum(a.total_real_amt),0) as totalSalesIn, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100)), 0) as totalCostIn " +
            "       FROM zypp.sale_item a, zypp.inf_item b, zypp.inf_store c ";

    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_MONTH = "SELECT /*+PARALLEL(A,8)*/  " +
            "       to_char(a.business_date,'MM') as timePoint, " +
            "       nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSales, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty),0) as totalCost, " +
            "       nvl(sum(a.total_real_amt),0) as totalSalesIn, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100)), 0) as totalCostIn " +
            "       FROM zypp.sale_item a, zypp.inf_item b, zypp.inf_store c ";

    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_STORE_NOT_DEPT = "SELECT /*+PARALLEL(A,8)*/ " +
            "  b.store as storeId, " +
            "  max(b.store_name) as storeName," +
            "  COUNT(DISTINCT a.tran_seq_no) AS customerNum, " +
            "  NVL(SUM(a.total_real_amt_ecl), 0) AS totalSales, " +
            "  NVL(SUM(a.total_cost_ecl), 0) AS totalCost, " +
            "  NVL(SUM(a.total_real_amt), 0) AS totalSalesIn, " +
            "  NVL(SUM(a.total_cost), 0) AS totalCostIn " +
            "  FROM zypp.sale_head a, zypp.inf_store b " +
            "  WHERE a.store = b.store ";

    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_STORE_NOT_DEPT_COUNT = "SELECT /*+PARALLEL(A,8)*/ " +
            "  COUNT(a.tran_seq_no) AS customerNum, " +
            "  NVL(SUM(a.total_real_amt_ecl), 0) AS totalSales, " +
            "  NVL(SUM(a.total_cost_ecl), 0) AS totalCost, " +
            "  NVL(SUM(a.total_real_amt), 0) AS totalSalesIn, " +
            "  NVL(SUM(a.total_cost), 0) AS totalCostIn " +
            "  FROM zypp.sale_head a, zypp.inf_store b " +
            "  WHERE a.store = b.store ";

    private static final String GET_MEMBER_PREFIX_HIS = "select /*+PARALLEL(A,8)*/ decode(vip_no, null, 'N', 'Y') as vipMark, " +
            "       (case when channel in ('YUNPOS', 'VIP','WX','WX1','WX2', 'WX3','DD1','JD1','KJG') or channel is null then 'off' else 'on' end) as onlineMark," +
            "       count(distinct a.tran_seq_no) as count " +
            "       from zypp.sale_item a, zypp.inf_item b,zypp.inf_store c " +
            "       where a.item=b.item and a.store=c.store ";

    private static final String GET_MEMBER_PREFIX_HIS_NOT_DEPT = " select /*+PARALLEL(A,8)*/ decode(vip_no, null, 'N', 'Y') as vipMark, " +
            "       (case when channel in ('YUNPOS', 'VIP','WX','WX1','WX2', 'WX3','DD1','JD1','KJG') or channel is null then 'off' else 'on' end) as onlineMark, " +
            "       count(a.tran_seq_no) as count " +
            "       from zypp.sale_head a,zypp.inf_store c " +
            "       where a.store=c.store ";

    private static final String GET_MEMBER_PREFIX_SAME_TIME = "select " +
            " sum(a.total_real_amt_ecl) as totalSales, " +
            " (sum(a.total_real_amt_ecl)-sum(a.total_cost_ecl)) as totalRate, " +
            " sum(a.total_real_amt) as totalSalesIn, " +
            " (sum(a.total_real_amt)-sum(a.total_cost)) as totalRateIn " +
            " from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a,zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_MEMBER_PREFIX_SAME_TIME_STORE = "select " +
            "       a.store as storeId, " +
            "       sum(a.total_real_amt_ecl) as totalSales, " +
            "       sum(case when b.mall_name = 1 then a.total_real_amt_ecl end) as totalCompareSales, " +
            "       sum(case when b.mall_name = 1 then a.total_cost_ecl end) as totalCompareCost, " +
            "       sum(a.total_cost_ecl) as totalCost, " +
            "       sum(a.total_real_amt) as totalSalesIn, " +
            "       sum(case when b.mall_name = 1 then a.total_real_amt end) as totalCompareSalesIn, " +
            "       sum(case when b.mall_name = 1 then a.total_cost end) as totalCompareCostIn, " +
            "       sum(a.total_cost) as totalCostIn " +
            "  from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a, zypp.inf_store b " +
            " where a.store = b.store";

    private static final String GET_MEMBER_PREFIX_SAME_TIME_DEPT = "select " +
            " a.dept as deptId, " +
            " sum(a.total_real_amt_ecl) as totalSales, " +
            " sum(case when b.mall_name = 1 then a.total_real_amt_ecl end) as totalCompareSales, " +
            " sum(case when b.mall_name = 1 then a.total_cost_ecl end) as totalCompareCost, " +
            " sum(a.total_cost_ecl) as totalCost, " +
            " sum(a.total_real_amt) as totalSalesIn, " +
            " sum(case when b.mall_name = 1 then a.total_real_amt end) as totalCompareSalesIn, " +
            " sum(case when b.mall_name = 1 then a.total_cost end) as totalCompareCostIn, " +
            " sum(a.total_cost) as totalCostIn " +
            " from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a,zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_HIS_RANK_CATEGORY_PREFIX = "select  " +
            "            c.new_division_name as deptName,  " +
            "            sum(a.total_real_amt_ecl) as totalSales,  " +
            "            sum(case when b.mall_name = 1 then a.total_real_amt_ecl end) as totalCompareSales,  " +
            "            sum(case when b.mall_name = 1 then a.total_cost_ecl end) as totalCompareCost,  " +
            "            sum(a.total_cost_ecl) as totalCost,  " +
            "            sum(a.total_real_amt) as totalSalesIn,  " +
            "            sum(case when b.mall_name = 1 then a.total_real_amt end) as totalCompareSalesIn,  " +
            "            sum(case when b.mall_name = 1 then a.total_cost end) as totalCompareCostIn,  " +
            "            sum(a.total_cost) as totalCostIn  " +
            "            from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a,zypp.inf_store b,zypp.code_dept c  " +
            "      where a.store = b.store  " +
            "          and a.dept = c.dept ";

    private static final String GET_HIS_REGION_RANK_NOT_DEPT_PREFIX = "SELECT /*+PARALLEL(A,8)*/ " +
            "  b.region as areaId, " +
            "  max(b.region_name) as areaName," +
            "  COUNT(DISTINCT a.tran_seq_no) AS customerNum, " +
            "  NVL(SUM(a.total_real_amt_ecl), 0) AS totalSales, " +
            "  NVL(SUM(a.total_cost_ecl), 0) AS totalCost, " +
            "  NVL(SUM(a.total_real_amt), 0) AS totalSalesIn, " +
            "  NVL(SUM(a.total_cost), 0) AS totalCostIn " +
            "  FROM zypp.sale_head a, zypp.inf_store b " +
            "  WHERE a.store = b.store ";

    private static final String GET_HIS_REGION_RANK_HAVE_DEPT_PREFIX = "SELECT /*+PARALLEL(A,8)*/  " +
            "       count(distinct a.tran_seq_no) as customerNum, " +
            "       c.region as areaId , " +
            "       max(c.region_name) as areaName, " +
            "       nvl(sum(a.total_real_amt / (1 + a.sale_vat_rate / 100)),0) as totalSales, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty),0) as totalCost, " +
            "       nvl(sum(a.total_real_amt),0) as totalSalesIn, " +
            "       nvl(sum(a.unit_cost_ecl * a.qty*(1+a.cost_vat_rate/100)), 0) as totalCostIn " +
            "       FROM zypp.sale_item a, zypp.inf_item b, zypp.inf_store c ";

    private static final String GET_MEMBER_SAME_TIME_REGION_PREFIX = "select " +
            "       b.region as areaId, " +
            "       sum(a.total_real_amt_ecl) as totalSales, " +
            "       sum(case when b.mall_name = 1 then a.total_real_amt_ecl end) as totalCompareSales, " +
            "       sum(case when b.mall_name = 1 then a.total_cost_ecl end) as totalCompareCost, " +
            "       sum(a.total_cost_ecl) as totalCost, " +
            "       sum(a.total_real_amt) as totalSalesIn, " +
            "       sum(case when b.mall_name = 1 then a.total_real_amt end) as totalCompareSalesIn, " +
            "       sum(case when b.mall_name = 1 then a.total_cost end) as totalCompareCostIn, " +
            "       sum(a.total_cost) as totalCostIn " +
            "  from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a, zypp.inf_store b " +
            " where a.store = b.store";

    /**
     * 查询历史品类数据(不包含客流)
     * @param pageRequest
     * @param start
     * @param end
     * @return
     */
    public List<RankDetail> queryHisCategoryRank(RankParamRequest pageRequest,String hour, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_RANK_CATEGORY_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(StringUtils.isNotEmpty(hour)){
            sb.append(" and a.hour <= '").append(hour).append("' ");
        }
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
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){

                sb.append(" and b.area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){

                sb.append(" and b.region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and b.store = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        if(StringUtils.isNotEmpty(pageRequest.getQueryCategorys())){
            sb.append(" and c.new_division_name in (").append(pageRequest.getQueryCategorys()).append(") ");
        }
        sb.append(" group by c.new_division_name ");
        return super.queryForList(sb.toString(),RANK_DETAIL_RM,paramList.toArray());
    }

    public List<RankDetail> querySameRankDetailForTime(RankParamRequest pageRequest, String startHour, String endHour, int type){
        StringBuilder sb = new StringBuilder();
        if(1 == type){
            sb.append(GET_MEMBER_PREFIX_SAME_TIME_STORE);
        }else{
            sb.append(GET_MEMBER_PREFIX_SAME_TIME_DEPT);
        }
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.hour <= ? ");
        paramList.add(endHour);
        sb.append(" and a.hour >= ? ");
        paramList.add(startHour);
        if(null != pageRequest.getEnd()){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getEnd())).append("' ");
        }else{
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getStart())).append("' ");
        }
        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");

            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and b.store = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
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
        if(1 == type){
            sb.append(" group by a.store");
        }else{
            sb.append(" group by a.dept");
        }
        List<RankDetail> list = super.queryForList(sb.toString(),RANK_DETAIL_RM,
                paramList.toArray());
        return list;
    }

    public List<RankDetail> querySameAreaRankDetailForTime(RankParamRequest pageRequest, String startHour, String endHour){
        StringBuilder sb = new StringBuilder(GET_MEMBER_SAME_TIME_REGION_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.hour <= ? ");
        paramList.add(endHour);
        sb.append(" and a.hour >= ? ");
        paramList.add(startHour);
        if(null != pageRequest.getEnd()){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getEnd())).append("' ");
        }else{
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getStart())).append("' ");
        }
        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");

            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(pageRequest.getAreaId());
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
        sb.append(" group by b.region ");
        List<RankDetail> list = super.queryForList(sb.toString(),RANK_DETAIL_RM,
                paramList.toArray());
        return list;
    }

    public TotalSalesAndProfit querySameTotalSalesAndProfitForHour(PageRequest pageRequest,String startHour,String endHour){
        StringBuilder sb = new StringBuilder(GET_MEMBER_PREFIX_SAME_TIME);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.hour <= ? ");
        paramList.add(endHour);
        sb.append(" and a.hour >= ? ");
        paramList.add(startHour);
        if(null != pageRequest.getEnd()){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getEnd())).append("' ");
        }else{
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getStart())).append("' ");
        }
        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");

            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and b.store  = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
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
        TotalSalesAndProfit totalSalesAndProfit = super.queryForObject(sb.toString(),TOTAL_SALE_AND_PROFIT_RM,paramList.toArray());
        return totalSalesAndProfit;
    }

    public TotalSalesAndProfit querySameCompareTotalSalesAndProfitForHour(PageRequest pageRequest,String startHour,String endHour){
        StringBuilder sb = new StringBuilder(GET_MEMBER_PREFIX_SAME_TIME);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.hour <= ? ");
        paramList.add(endHour);
        sb.append(" and a.hour >= ? ");
        paramList.add(startHour);
        if(null != pageRequest.getEnd()){
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getEnd())).append("' ");
        }else{
            sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getStart())).append("' ");
        }
        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");

            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and b.store  = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
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
        sb.append(" and b.mall_name = 1");
        TotalSalesAndProfit totalSalesAndProfit = super.queryForObject(sb.toString(),TOTAL_SALE_AND_PROFIT_RM,paramList.toArray());
        return totalSalesAndProfit;
    }

    public List<MemberPermeabilityPo> queryHisMember(PageRequest param){
        List<MemberPermeabilityPo> result = null;
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder();
        if(null == param.getDeptIds() || param.getDeptIds().size() == 0){
            sb.append(GET_MEMBER_PREFIX_HIS_NOT_DEPT);
            if(null == param.getEnd()){
                sb.append(" and a.business_date = date '");
                sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",param.getStart())).append("' ");
            }else{
                sb.append(" and a.business_date <= date '");
                sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",param.getEnd())).append("' ");
                sb.append(" and a.business_date >= date '");
                sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",param.getStart())).append("' ");
            }

            //拼接省份
            if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
                if(param.getProvinceIds().size() == 1){
                    sb.append(" and c.area = ? ");
                    paramList.add(param.getProvinceIds().get(0));
                }else if(param.getProvinceIds().size() > 1){
                    sb.append(" and c.area in ( ");
                    sb.append(param.getProvinceId());
                    sb.append(" ) ");

                }
            }

            //拼接区域
            if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
                if(param.getAreaIds().size() == 1){
                    sb.append(" and c.region = ? ");
                    paramList.add(param.getAreaIds().get(0));
                }else if(param.getAreaIds().size() > 1){
                    sb.append(" and c.region in ( ");
                    sb.append(param.getAreaId());
                    sb.append(" ) ");
                }
            }

            //拼接门店
            if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
                if(param.getStoreIds().size() == 1){
                    sb.append(" and c.store  = ? ");
                    paramList.add(param.getStoreIds().get(0));
                }else if(param.getStoreIds().size() > 1){
                    sb.append(" and c.store in ( ");
                    sb.append(param.getStoreId());
                    sb.append(" ) ");
                }
            }
            sb.append(" group by decode(a.vip_no, null, 'N', 'Y'), " +
                    " (case when channel in ('YUNPOS', 'VIP', 'WX', 'WX1', 'WX2', 'WX3', 'DD1', 'JD1', 'KJG') or channel is null then 'off' else 'on' end)");
        }else{
            sb.append(GET_MEMBER_PREFIX_HIS);
            if(null == param.getEnd()){
                sb.append(" and a.business_date = date '");
                sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",param.getStart())).append("' ");
            }else{
                sb.append(" and a.business_date <= date '");
                sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",param.getEnd())).append("' ");
                sb.append(" and a.business_date >= date '");
                sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",param.getStart())).append("' ");
            }

            //拼接省份
            if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
                if(param.getProvinceIds().size() == 1){
                    sb.append(" and c.area = ? ");
                    paramList.add(param.getProvinceIds().get(0));
                }else if(param.getProvinceIds().size() > 1){
                    sb.append(" and c.area in ( ");
                    sb.append(param.getProvinceId());
                    sb.append(" ) ");

                }
            }

            //拼接区域
            if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
                if(param.getAreaIds().size() == 1){
                    sb.append(" and c.region = ? ");
                    paramList.add(param.getAreaIds().get(0));
                }else if(param.getAreaIds().size() > 1){
                    sb.append(" and c.region in ( ");
                    sb.append(param.getAreaId());
                    sb.append(" ) ");
                }
            }

            //拼接门店
            if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
                if(param.getStoreIds().size() == 1){
                    sb.append(" and c.store  = ? ");
                    paramList.add(param.getStoreIds().get(0));
                }else if(param.getStoreIds().size() > 1){
                    sb.append(" and c.store in ( ");
                    sb.append(param.getStoreId());
                    sb.append(" ) ");
                }
            }

            //拼接大类
            if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                if(param.getDeptIds().size() == 1){
                    sb.append(" and b.dept  = ? ");
                    paramList.add(param.getDeptIds().get(0));
                }else if(param.getDeptIds().size() > 1){
                    sb.append(" and b.dept in ( ");
                    sb.append(param.getDeptId());
                    sb.append(" ) ");
                }
            }

            sb.append(" group by decode(a.vip_no, null, 'N', 'Y'), " +
                    " (case when channel in ('YUNPOS', 'VIP', 'WX', 'WX1', 'WX2', 'WX3', 'DD1', 'JD1', 'KJG') or channel is null then 'off' else 'on' end)");
        }
        result = super.queryForList(sb.toString(), MEMBER_RM, paramList.toArray());
        return result;
    }
    /**
     * 查询排行榜同期分组数据(4-门店，5-大类)
     * @param pageRequest
     * @return
     */
    public List<RankDetail> querySameRankDetail(PageRequest pageRequest, int type){
        List<RankDetail> result = null;
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getHisCommenProfixSql(pageRequest, paramList, type));
        sb.append(getHisCommenSql(pageRequest, paramList));
        sb.append(getHisCommenGroupSql(type));
        result = super.queryForList(sb.toString(), RANK_DETAIL_RM, paramList.toArray());
        return result;
    }

    /**
     * 查询排行榜历史分组数据（4-门店，5-大类）
     * @param pageRequest
     * @return
     */
    public List<RankDetail> queryHisRankDetail(PageRequest pageRequest, int type){
        List<RankDetail> result = null;
        if(type == 4){
            StringBuilder sb = new StringBuilder();
            List<Object> paramList = new ArrayList<Object>();
            if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
                sb.append(getHisCommenProfixSql(pageRequest, paramList, type));
                sb.append(getHisCommenSql(pageRequest, paramList));
                sb.append(getHisCommenGroupSql(type));
            }else{
                sb.append(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_STORE_NOT_DEPT);

                if(null == pageRequest.getEnd()){
                    sb.append(" and a.business_date = date '");
                    sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
                }else{
                    sb.append(" and a.business_date <= date '");
                    sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
                    sb.append(" and a.business_date >= date '");
                    sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
                }

                //拼接省份
                if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
                    if(pageRequest.getProvinceIds().size() == 1){
                        sb.append(" and b.area = ? ");
                        paramList.add(pageRequest.getProvinceIds().get(0));
                    }else if(pageRequest.getProvinceIds().size() > 1){
                        sb.append(" and b.area in ( ");
                        sb.append(pageRequest.getProvinceId());
                        sb.append(" ) ");

                    }
                }

                //拼接区域
                if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
                    if(pageRequest.getAreaIds().size() == 1){
                        sb.append(" and b.region = ? ");
                        paramList.add(pageRequest.getAreaIds().get(0));
                    }else if(pageRequest.getAreaIds().size() > 1){
                        sb.append(" and b.region in ( ");
                        sb.append(pageRequest.getAreaId());
                        sb.append(" ) ");
                    }
                }

                //拼接门店
                if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
                    if(pageRequest.getStoreIds().size() == 1){
                        sb.append(" and b.store  = ? ");
                        paramList.add(pageRequest.getStoreIds().get(0));
                    }else if(pageRequest.getStoreIds().size() > 1){
                        sb.append(" and b.store in ( ");
                        sb.append(pageRequest.getStoreId());
                        sb.append(" ) ");
                    }
                }
                sb.append(" group by b.store ");
            }
            result = super.queryForList(sb.toString(), RANK_DETAIL_RM, paramList.toArray());
        }
        return result;
    }

    /**
     * 查询区域排行榜历史分组数据
     * @param pageRequest
     * @return
     */
    public List<RankDetail> queryHisAreaRankDetail(PageRequest pageRequest){
        List<RankDetail> result = null;
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            sb.append(GET_HIS_REGION_RANK_HAVE_DEPT_PREFIX);
            sb.append(" where a.store = c.store and a.item = b.item ");
            if(null == pageRequest.getEnd()){
                sb.append(" and a.business_date = date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
            }else{
                sb.append(" and a.business_date <= date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
                sb.append(" and a.business_date >= date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
            }
            //拼接省份
            if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
                if(pageRequest.getProvinceIds().size() == 1){
                    sb.append(" and c.area = ? ");
                    paramList.add(pageRequest.getProvinceIds().get(0));
                }else if(pageRequest.getProvinceIds().size() > 1){
                    sb.append(" and c.area in ( ");
                    sb.append(pageRequest.getProvinceId());
                    sb.append(" ) ");

                }
            }

            //拼接区域
            if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
                if(pageRequest.getAreaIds().size() == 1){
                    sb.append(" and c.region = ? ");
                    paramList.add(pageRequest.getAreaIds().get(0));
                }else if(pageRequest.getAreaIds().size() > 1){
                    sb.append(" and c.region in ( ");
                    sb.append(pageRequest.getAreaId());
                    sb.append(" ) ");
                }
            }
            //拼接大类
            if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
                if(pageRequest.getDeptIds().size() == 1){
                    sb.append(" and b.dept  = ? ");
                    paramList.add(pageRequest.getDeptIds().get(0));
                }else if(pageRequest.getDeptIds().size() > 1){
                    sb.append(" and b.dept in ( ");
                    sb.append(pageRequest.getDeptId());
                    sb.append(" ) ");
                }
            }
            sb.append(" group by c.region ");
        }else{
            sb.append(GET_HIS_REGION_RANK_NOT_DEPT_PREFIX);

            if(null == pageRequest.getEnd()){
                sb.append(" and a.business_date = date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
            }else{
                sb.append(" and a.business_date <= date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
                sb.append(" and a.business_date >= date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
            }

            //拼接省份
            if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
                if(pageRequest.getProvinceIds().size() == 1){
                    sb.append(" and b.area = ? ");
                    paramList.add(pageRequest.getProvinceIds().get(0));
                }else if(pageRequest.getProvinceIds().size() > 1){
                    sb.append(" and b.area in ( ");
                    sb.append(pageRequest.getProvinceId());
                    sb.append(" ) ");

                }
            }

            //拼接区域
            if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
                if(pageRequest.getAreaIds().size() == 1){
                    sb.append(" and b.region = ? ");
                    paramList.add(pageRequest.getAreaIds().get(0));
                }else if(pageRequest.getAreaIds().size() > 1){
                    sb.append(" and b.region in ( ");
                    sb.append(pageRequest.getAreaId());
                    sb.append(" ) ");
                }
            }
            sb.append(" group by b.region ");
        }
        result = super.queryForList(sb.toString(), RANK_DETAIL_RM, paramList.toArray());
        return result;
    }
    /**
     * 查询同比日线，时线，月线（不包含大类）
     * @param param
     * @return
     */
    public List<ReportDetail> querySameReportDetail(ReportParamRequest param){
        List<ReportDetail> result = null;
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getHisCommenProfixSql(param, paramList, param.getType()));
        sb.append(getHisCommenSql(param, paramList));
        sb.append(getHisCommenGroupSql(param.getType()));
        sb.append(getHisCommenOrderSql(param.getType()));
        result = super.queryForList(sb.toString(), REPORT_DETAIL_RM, paramList.toArray());
        if(null != result){
            for(ReportDetail v : result){
                BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(v.getTotalSales())?"0":v.getTotalSales());
                BigDecimal totalCost = new BigDecimal(StringUtils.isEmpty(v.getTotalCost())?"0":v.getTotalCost());

                BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(v.getTotalSalesIn())?"0":v.getTotalSalesIn());
                BigDecimal totalCostIn = new BigDecimal(StringUtils.isEmpty(v.getTotalCostIn())?"0":v.getTotalCostIn());
                if(totalSale.compareTo(BigDecimal.ZERO)!=0){
                    v.setTotalprofit((totalSale.subtract(totalCost)).divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                }
                if(totalSaleIn.compareTo(BigDecimal.ZERO)!=0){
                    v.setTotalprofitIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        }
        return result;
    }

    /**
     * 查询日线，时线，月线的历史数据
     * @return
     */
    public List<ReportDetail> queryHisReportDetail(ReportParamRequest param){
        List<ReportDetail> result = null;
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getHisCommenProfixSql(param, paramList, param.getType()));
        sb.append(getHisCommenSql(param, paramList));
        sb.append(getHisCommenGroupSql(param.getType()));
        sb.append(getHisCommenOrderSql(param.getType()));
        result = super.queryForList(sb.toString(), REPORT_DETAIL_RM, paramList.toArray());
        if(null != result){
            for(ReportDetail v : result){
                BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(v.getTotalSales())?"0":v.getTotalSales());
                BigDecimal totalCost = new BigDecimal(StringUtils.isEmpty(v.getTotalCost())?"0":v.getTotalCost());

                BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(v.getTotalSalesIn())?"0":v.getTotalSalesIn());
                BigDecimal totalCostIn = new BigDecimal(StringUtils.isEmpty(v.getTotalCostIn())?"0":v.getTotalCostIn());
                if(totalSale.compareTo(BigDecimal.ZERO)!=0){
                    v.setTotalprofit((totalSale.subtract(totalCost)).divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                }
                if(totalSaleIn.compareTo(BigDecimal.ZERO)!=0){
                    v.setTotalprofitIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        }
        return result;
    }

    public List<ReportDetail> queryHisReportDetailForHour(ReportParamRequest param){
        List<ReportDetail> result = null;
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_HOUR_PROPERTY);

        if(null == param.getEnd()){
            sb.append(" and a.business_date = date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and a.business_date <= date '");
            sb.append(DateUtil.getDateFormat(param.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and a.business_date >= date '");
            sb.append(DateUtil.getDateFormat(param.getStart(),"yyyy-MM-dd")).append("' ");
        }

        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and b.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and b.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");

            }
        }
        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and b.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and b.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }
        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and b.store  = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
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

        sb.append("  group by a.hour ");
        sb.append("  order by a.hour ");

        result = super.queryForList(sb.toString(), REPORT_DETAIL_RM, paramList.toArray());
        if(null != result){
            for(ReportDetail v : result){
                BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(v.getTotalSales())?"0":v.getTotalSales());
                BigDecimal totalCost = new BigDecimal(StringUtils.isEmpty(v.getTotalCost())?"0":v.getTotalCost());
                BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(v.getTotalSalesIn())?"0":v.getTotalSalesIn());
                BigDecimal totalCostIn = new BigDecimal(StringUtils.isEmpty(v.getTotalCostIn())?"0":v.getTotalCostIn());
                if(totalSale.compareTo(BigDecimal.ZERO)!=0){
                    v.setTotalprofit((totalSale.subtract(totalCost)).divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                }
                if(totalSaleIn.compareTo(BigDecimal.ZERO)!=0){
                    v.setTotalprofitIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        }
        return result;
    }

    /**
     * 历史可比
     * @param pageRequest
     * @return
     */
    public TotalSalesAndProfit queryHisCompareData(PageRequest pageRequest){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        //历史数据
        sb.append(getHisCommenProfixSql(pageRequest, paramList, -1));
        sb.append(getHisCommenSql(pageRequest, paramList));
        sb.append(getHisCompareCommonSql());
        TotalSalesAndProfit totalSalesAndProfit = queryInfo(sb.toString(), paramList);
        if(null != totalSalesAndProfit){
            BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSales())?"0": totalSalesAndProfit.getTotalSales());
            BigDecimal totalCost = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalCost())?"0": totalSalesAndProfit.getTotalCost());

            BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSalesIn())?"0": totalSalesAndProfit.getTotalSalesIn());
            BigDecimal totalCostIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalCostIn())?"0": totalSalesAndProfit.getTotalCostIn());
            if(totalSale.compareTo(BigDecimal.ZERO)!=0){
                totalSalesAndProfit.setTotalProfit((totalSale.subtract(totalCost)).divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                totalSalesAndProfit.setScanningProfitRate((totalSale.subtract(totalCost)).divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(totalSaleIn.compareTo(BigDecimal.ZERO)!=0){
                totalSalesAndProfit.setTotalProfitIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                totalSalesAndProfit.setScanningProfitRateIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            totalSalesAndProfit.setTotalRate(totalSale.subtract(totalCost).toString());
            totalSalesAndProfit.setTotalRateIn(totalSaleIn.subtract(totalCostIn).toString());
        }
        return totalSalesAndProfit;
    }

    /**
     * type=2-表示把生鲜排除
     * @param pageRequest
     * @param fresh
     * @param type
     * @return
     */
    public TotalSalesAndProfit queryHisTotalData(PageRequest pageRequest, String fresh, int type){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        if(type == -1 && (null == pageRequest.getDeptIds() || (pageRequest.getDeptIds().size() == 0))){
            sb.append(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_STORE_NOT_DEPT_COUNT);

            if(null == pageRequest.getEnd()){
                sb.append(" and a.business_date = date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
            }else{
                sb.append(" and a.business_date <= date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
                sb.append(" and a.business_date >= date '");
                sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
            }

            //拼接省份
            if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
                if(pageRequest.getProvinceIds().size() == 1){
                    sb.append(" and b.area = ? ");
                    paramList.add(pageRequest.getProvinceIds().get(0));
                }else if(pageRequest.getProvinceIds().size() > 1){
                    sb.append(" and b.area in ( ");
                    sb.append(pageRequest.getProvinceId());
                    sb.append(" ) ");

                }
            }

            //拼接区域
            if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
                if(pageRequest.getAreaIds().size() == 1){
                    sb.append(" and b.region = ? ");
                    paramList.add(pageRequest.getAreaIds().get(0));
                }else if(pageRequest.getAreaIds().size() > 1){
                    sb.append(" and b.region in ( ");
                    sb.append(pageRequest.getAreaId());
                    sb.append(" ) ");
                }
            }

            //拼接门店
            if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
                if(pageRequest.getStoreIds().size() == 1){
                    sb.append(" and b.store  = ? ");
                    paramList.add(pageRequest.getStoreIds().get(0));
                }else if(pageRequest.getStoreIds().size() > 1){
                    sb.append(" and b.store in ( ");
                    sb.append(pageRequest.getStoreId());
                    sb.append(" ) ");
                }
            }
        }else{
            sb.append(getHisCommenProfixSql(pageRequest, paramList, -1));
            sb.append(getHisCommenSql(pageRequest, paramList));
        }
        if(2 == type){
            sb.append(getHisNotFreshSql(fresh));
        }
        //查询销售额，毛利率，客流
        TotalSalesAndProfit totalSalesAndProfit = queryInfo(sb.toString(), paramList);
        if(null != totalSalesAndProfit){
            BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSales())?"0": totalSalesAndProfit.getTotalSales());
            BigDecimal totalCost = new BigDecimal(totalSalesAndProfit.getTotalCost());

            BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalSalesIn())?"0": totalSalesAndProfit.getTotalSalesIn());
            BigDecimal totalCostIn = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit.getTotalCostIn())?"0":totalSalesAndProfit.getTotalCostIn());
            if(totalSale.compareTo(BigDecimal.ZERO) != 0){
                totalSalesAndProfit.setTotalProfit((totalSale.subtract(totalCost)).divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                totalSalesAndProfit.setScanningProfitRate((totalSale.subtract(totalCost)).divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(totalSaleIn.compareTo(BigDecimal.ZERO) != 0){
                totalSalesAndProfit.setTotalProfitIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                totalSalesAndProfit.setScanningProfitRateIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            totalSalesAndProfit.setTotalRate(totalSale.subtract(totalCost).toString());
            totalSalesAndProfit.setTotalRateIn(totalSaleIn.subtract(totalCostIn).toString());
        }
        return totalSalesAndProfit;
    }

    /**
     * 历史数据前缀(1-时线,2-日线,3-月线,4-门店,5-大类,其它默认查询汇总)
     * @param pageRequest
     * @return
     */
    public String getHisCommenProfixSql(PageRequest pageRequest, List<Object> paramList, Integer type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb = sb.append(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_HOUR);
                break;
            case 2:
                sb = sb.append(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_DAY);
                break;
            case 3:
                sb = sb.append(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_MONTH);
                break;
            case 4:
                sb = sb.append(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_STORE);
                break;
            /*case 5:
                sb = sb.append(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_DEPT);
                break;*/
            default:
                sb = sb.append(GET_HIS_TOTAL_SALES_PROFIT_PREFIX);
                break;
        }
        sb.append(" where a.store = c.store and a.item = b.item ");

        if(null == pageRequest.getEnd()){
            sb.append(" and a.business_date = date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and a.business_date <= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and a.business_date >= date '");
            sb.append(DateUtil.getDateFormat(pageRequest.getStart(),"yyyy-MM-dd")).append("' ");
        }
        return sb.toString();
    }

    /**
     *  历史数据省，区域，门店，大类拼接
     * @param pageRequest
     * @return
     */
    public String getHisCommenSql(PageRequest pageRequest, List<Object> paramList){
        StringBuilder sb = new StringBuilder();
        //拼接省份
        if(null != pageRequest.getProvinceIds() && pageRequest.getProvinceIds().size() > 0){
            if(pageRequest.getProvinceIds().size() == 1){
                sb.append(" and c.area = ? ");
                paramList.add(pageRequest.getProvinceIds().get(0));
            }else if(pageRequest.getProvinceIds().size() > 1){
                sb.append(" and c.area in ( ");
                sb.append(pageRequest.getProvinceId());
                sb.append(" ) ");

            }
        }

        //拼接区域
        if(null != pageRequest.getAreaIds() && pageRequest.getAreaIds().size() > 0){
            if(pageRequest.getAreaIds().size() == 1){
                sb.append(" and c.region = ? ");
                paramList.add(pageRequest.getAreaIds().get(0));
            }else if(pageRequest.getAreaIds().size() > 1){
                sb.append(" and c.region in ( ");
                sb.append(pageRequest.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != pageRequest.getStoreIds() && pageRequest.getStoreIds().size() > 0){
            if(pageRequest.getStoreIds().size() == 1){
                sb.append(" and c.store  = ? ");
                paramList.add(pageRequest.getStoreIds().get(0));
            }else if(pageRequest.getStoreIds().size() > 1){
                sb.append(" and c.store in ( ");
                sb.append(pageRequest.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != pageRequest.getDeptIds() && pageRequest.getDeptIds().size() > 0){
            if(pageRequest.getDeptIds().size() == 1){
                sb.append(" and b.dept  = ? ");
                paramList.add(pageRequest.getDeptIds().get(0));
            }else if(pageRequest.getDeptIds().size() > 1){
                sb.append(" and b.dept in ( ");
                sb.append(pageRequest.getDeptId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }

    public String getHisNotFreshSql(String fresh){
        StringBuilder sb = new StringBuilder();
        //拼接大类
        sb.append(" and b.dept not in ( ");
        sb.append(fresh);
        sb.append(" ) ");
        return sb.toString();
    }

    /**
     * 历史增加可比拼接

     * @return
     */
    private String getHisCompareCommonSql(){
        return " and c.mall_name = 1 ";
    }

    /**
     * 类型(1-时线,2-日线,3-月线,4-门店)进行分组（历史）
     * @param type
     * @return
     */
    private String getHisCommenGroupSql(int type){
        String sb = "";
        switch (type){
            case 1:
                sb = " group by substr(a.tran_datetime, 1, 2) ";
                break;
            case 2:
                sb = " group by a.business_date ";
                break;
            case 3:
                sb = " group by to_char(a.business_date,'MM') ";
                break;
            case 4:
                sb = " group by c.store ";
                break;
            case 5:
                sb = " group by a.dept ";
                break;
            default:
                sb = "";
                break;
        }
        return sb;
    }

    private String getHisCommenOrderSql(int type){
        String sb = "";
        switch (type){
            case 1:
                sb = " order by substr(a.tran_datetime, 1, 2) ";
                break;
            case 2:
                sb = " order by a.business_date ";
                break;
            case 3:
                sb = " order by to_char(a.business_date,'MM') ";
                break;
            default:
                sb = "";
                break;
        }
        return sb;
    }

    private TotalSalesAndProfit queryInfo(String sql, List<Object> paramList){
        return super.queryForObject(sql, TOTAL_SALE_AND_PROFIT_RM, paramList.toArray());
    }
}
