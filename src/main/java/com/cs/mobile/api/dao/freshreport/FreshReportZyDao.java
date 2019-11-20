package com.cs.mobile.api.dao.freshreport;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.freshreport.*;
import com.cs.mobile.api.model.freshreport.request.FreshRankRequest;
import com.cs.mobile.api.model.freshreport.request.FreshReportBaseRequest;
import com.cs.mobile.api.model.reportPage.MemberPermeabilityPo;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
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
public class FreshReportZyDao extends AbstractDao {
    private static final RowMapper<MonthStatistics> TOTAL_FRESH_RM = new BeanPropertyRowMapper<MonthStatistics>(MonthStatistics.class);
    private static final RowMapper<SalesList> TOTAL_FRESH_SALES_RM = new BeanPropertyRowMapper<SalesList>(SalesList.class);
    private static final RowMapper<FreshRankInfo> TOTAL_FRESH_PROVINCE_RM = new BeanPropertyRowMapper<FreshRankInfo>(FreshRankInfo.class);
    private static final RowMapper<FreshKl> TOTAL_FRESH_KL_RM = new BeanPropertyRowMapper<FreshKl>(FreshKl.class);
    private static final RowMapper<FreshDeptKl> TOTAL_FRESHDEPTKL_RM = new BeanPropertyRowMapper<FreshDeptKl>(FreshDeptKl.class);
    private static final RowMapper<MemberModel> TOTAL_MEMBERMODEL_RM = new BeanPropertyRowMapper<MemberModel>(MemberModel.class);
    private static final RowMapper<MemberPermeabilityPo> MEMBER_RM = new BeanPropertyRowMapper<MemberPermeabilityPo>(MemberPermeabilityPo.class);
    private static final RowMapper<FreshRankKlModel> TOTAL_FRESHRANKKL_RM = new BeanPropertyRowMapper<FreshRankKlModel>(FreshRankKlModel.class);
    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX = "SELECT      /*+PARALLEL(A,8)*/ " +
            "         NVL (SUM (a.total_real_amt / (1 + a.sale_vat_rate / 100)), 0) AS discountPrice, " +
            "         NVL (SUM (a.total_real_amt), 0) AS discountPriceIn " +
            "  FROM   zypp.sale_item a, zypp.inf_item b, zypp.inf_store c " +
            " WHERE   a.store = c.store AND a.item = b.item and a.sales_type = '03' ";

    private static final String GET_HIS_TOTAL_SALES_DISCOUNT_PREFIX = "select NVL(SUM(a.total_real_amt_ecl), 0) AS discountPrice,  " +
            " NVL(SUM(a.total_real_amt), 0) AS discountPriceIn  " +
            "  from zypp.cal_store_dept_type_sale_sum a,zypp.inf_store c  " +
            " where a.store = c.store  " +
            " and a.sales_type = '03' ";

    private static final String GET_HIS_TOTAL_SALES_PROFIT_PREFIX_PROVINCE = "SELECT      /*+PARALLEL(A,8)*/ " +
            "         NVL (SUM (a.total_real_amt / (1 + a.sale_vat_rate / 100)), 0) AS discountPrice, " +
            "         NVL (SUM (a.total_real_amt), 0) AS discountPriceIn, " +
            "         c.area AS provinceId " +
            "  FROM   zypp.sale_item a, zypp.inf_item b, zypp.inf_store c " +
            " WHERE   a.store = c.store AND a.item = b.item and a.sales_type = '03' ";

    private static final String GET_MEMBER_PREFIX_SAME_TIME = "select sum(a.total_real_amt_ecl) as totalSales, " +
            " to_char(a.business_date,'yyyyMMdd') as time, " +
            " (sum(a.total_real_amt_ecl)-sum(a.total_cost_ecl)) as totalProfitPrice, " +
            " sum(a.total_real_amt) as totalSalesIn," +
            " (sum(a.total_real_amt)-sum(a.total_cost)) as totalProfitPriceIn " +
            " from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a,zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_MEMBER_PREFIX_SAME_PROVINCE_TIME_COMPARE = "select sum(a.total_real_amt_ecl) as totalSales, " +
            " b.area  as id, " +
            " b.mall_name as compareMark, " +
            " (sum(a.total_real_amt_ecl)-sum(a.total_cost_ecl)) as totalProfitPrice, " +
            " sum(a.total_real_amt) as totalSalesIn," +
            " (sum(a.total_real_amt)-sum(a.total_cost)) as totalProfitPriceIn " +
            " from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a,zypp.inf_store b " +
            " where a.store = b.store ";
    private static final String GET_MEMBER_PREFIX_SAME_AREA_TIME_COMPARE= "select sum(a.total_real_amt_ecl) as totalSales, " +
            " b.region as id, " +
            " b.mall_name as compareMark, " +
            " (sum(a.total_real_amt_ecl)-sum(a.total_cost_ecl)) as totalProfitPrice, " +
            " sum(a.total_real_amt) as totalSalesIn," +
            " (sum(a.total_real_amt)-sum(a.total_cost)) as totalProfitPriceIn " +
            " from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a,zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_MEMBER_PREFIX_SAME_STORE_TIME_COMPARE = "select sum(a.total_real_amt_ecl) as totalSales, " +
            " b.store as id, " +
            " b.mall_name as compareMark, " +
            " (sum(a.total_real_amt_ecl)-sum(a.total_cost_ecl)) as totalProfitPrice, " +
            " sum(a.total_real_amt) as totalSalesIn," +
            " (sum(a.total_real_amt)-sum(a.total_cost)) as totalProfitPriceIn " +
            " from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a,zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_MEMBER_PREFIX_SAME_DEPT_TIME_COMPARE = "select sum(a.total_real_amt_ecl) as totalSales, " +
            " a.dept as id, " +
            " b.mall_name as compareMark, " +
            " (sum(a.total_real_amt_ecl)-sum(a.total_cost_ecl)) as totalProfitPrice, " +
            " sum(a.total_real_amt) as totalSalesIn," +
            " (sum(a.total_real_amt)-sum(a.total_cost)) as totalProfitPriceIn " +
            " from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM a,zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_FRESH_KL_PREFIX = "select /*+PARALLEL(A,8)*/ COUNT(a.tran_seq_no) AS freshKlCount, " +
            " to_char(a.business_date,'yyyyMMdd') as time, " +
            " COUNT(case when a.vip_no is not null then a.tran_seq_no end) as freshMemberCount " +
            " from zypp.sale_head_f4 a,zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_FRESH_TOTAL_KL_PREFIX = "SELECT /*+PARALLEL(A,8)*/  " +
            " to_char(a.business_date,'yyyyMMdd') as time, " +
            " COUNT(a.tran_seq_no) AS allKlCount " +
            " FROM zypp.sale_head a, zypp.inf_store b  " +
            " WHERE a.store = b.store ";

    private static final String GET_HIS_FRESH_DEPT_KL_PREFIX = "select  /*+PARALLEL(A,8)*/ " +
            " a.dept as deptId, " +
            " to_char(a.business_date,'yyyyMMdd') as time, " +
            " sum(a.tran_count) as freshKlCount " +
            " from zypp.cal_store_hour_dept_sale_sum a, zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_HIS_FRESH_DEPT_VIP_KL_PREFIX = "select  /*+PARALLEL(A,8)*/ " +
            " a.dept as deptId, " +
            " to_char(a.business_date,'yyyyMMdd') as time, " +
            " sum(a.tran_count) as freshMemberCount " +
            " from zypp.cal_store_hour_dept_sale_sum_vip a, zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_HIS_MEMBER_HAVE_DEPT_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " decode(vip_no, null, 'N', 'Y') as vipMark, " +
            "       decode(a.channel,null,'BKSLS',a.channel) as channel, " +
            " max(d.channel_desc) as channelName, " +
            " count(distinct a.tran_seq_no) as count " +
            "  from zypp.sale_item a, zypp.inf_item b, zypp.inf_store c, zypp.code_channel d " +
            " where a.item = b.item " +
            "   and a.store = c.store  " +
            "   and a.channel = d.channel(+) ";
    private static final String GET_HIS_MEMBER_NOT_DEPT_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " decode(vip_no, null, 'N', 'Y') as vipMark, " +
            "       decode(a.channel,null,'BKSLS',a.channel) as channel, " +
            " max(b.channel_desc) as channelName, " +
            " count(a.tran_seq_no) as count " +
            "  from zypp.sale_head a, zypp.code_channel b,zypp.inf_store c " +
            " where a.store = c.store " +
            " and a.channel = b.channel(+) ";

    private static final String GET_HIS_FRESH_MEMBER_PREFIX ="select /*+PARALLEL(A,8)*/ " +
            " decode(a.vip_no, null, 'N', 'Y') as vipMark, " +
            " (case when a.channel in ('YUNPOS', 'VIP', 'WX', 'WX1', 'WX2', 'WX3', 'DD1', 'JD1', 'KJG') or a.channel is null then 'off' else  'on' end) as onlineMark, " +
            " count(a.tran_seq_no) as count " +
            " from zypp.sale_head_f4 a, zypp.inf_store b, zypp.code_channel c " +
            " where a.store = b.store " +
            " and a.channel = c.channel(+) ";

    private static final String GET_HIS_FRESH_MEMBER_DEPT_PREFIX ="select /*+PARALLEL(A,8)*/ " +
            " decode(a.vip_no, null, 'N', 'Y') as vipMark, " +
            " (case when a.channel in ('YUNPOS', 'VIP', 'WX', 'WX1', 'WX2', 'WX3', 'DD1', 'JD1', 'KJG') or a.channel is null then 'off' else  'on' end) as onlineMark, " +
            " count(a.tran_seq_no) as count " +
            " from zypp.sale_head a, zypp.inf_store b, zypp.code_channel c " +
            " where a.store = b.store " +
            " and a.channel = c.channel(+) ";

    private static final String GET_HIS_FRESH_MEMBER_DETAIL_PREFIX ="select /*+PARALLEL(A,8)*/ " +
            " decode(a.vip_no, null, 'N', 'Y') as vipMark, " +
            " decode(a.channel, null, 'BKSLS', a.channel) as channel, " +
            " max(c.channel_desc) as channelName, " +
            " count(distinct a.tran_seq_no) as count " +
            "  from zypp.sale_head_f4 a, zypp.inf_store b, zypp.code_channel c " +
            " where a.store = b.store " +
            "   and a.channel = c.channel(+) ";


    private static final String GET_FRESH_KL_PROVINCE_4_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " area as id, " +
            " COUNT(a.tran_seq_no) AS kl, " +
            " COUNT(case " +
            "         when a.vip_no is not null then " +
            "          a.tran_seq_no " +
            "       end) as freshMemberCount " +
            "  from zypp.sale_head_f4 a, zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_FRESH_KL_AREA_4_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " b.region as id, " +
            " COUNT(a.tran_seq_no) AS kl, " +
            " COUNT(case " +
            "         when a.vip_no is not null then " +
            "          a.tran_seq_no " +
            "       end) as freshMemberCount " +
            "  from zypp.sale_head_f4 a, zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_FRESH_KL_STORE_4_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " b.store as id, " +
            " COUNT(a.tran_seq_no) AS kl, " +
            " COUNT(case " +
            "         when a.vip_no is not null then " +
            "          a.tran_seq_no " +
            "       end) as freshMemberCount " +
            "  from zypp.sale_head_f4 a, zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_FRESH_KL_PROVINCE_PREFIX = "SELECT /*+PARALLEL(A,8)*/ " +
            " b.area as id, " +
            " COUNT(a.tran_seq_no) AS kl " +
            "  FROM zypp.sale_head a, zypp.inf_store b " +
            " WHERE a.store = b.store ";

    private static final String GET_FRESH_KL_AREA_PREFIX = "SELECT /*+PARALLEL(A,8)*/ " +
            " b.region as id," +
            " COUNT(a.tran_seq_no) AS kl " +
            "  FROM zypp.sale_head a, zypp.inf_store b " +
            " WHERE a.store = b.store";

    private static final String GET_FRESH_KL_STORE_PREFIX = "SELECT /*+PARALLEL(A,8)*/ " +
            " b.store as id, " +
            " COUNT(a.tran_seq_no) AS kl " +
            "  FROM zypp.sale_head a, zypp.inf_store b " +
            " WHERE a.store = b.store ";

    private static final String GET_FRESH_SALE_FOUR_KL_PREFIX = "select /*+PARALLEL(A,8)*/ " +
            " COUNT(a.tran_seq_no) AS kl " +
            "  from zypp.sale_head_f4 a, zypp.inf_store b " +
            " where a.store = b.store ";

    private static final String GET_FRESH_SALE_TOTAL_KL_PREFIX = "SELECT /*+PARALLEL(A,8)*/ " +
            " COUNT(a.tran_seq_no) AS kl " +
            "  FROM zypp.sale_head a, zypp.inf_store b " +
            " WHERE a.store = b.store ";

    /**
     * 查询历史总客流
     * @param request
     * @param start
     * @param end
     * @return
     */
    public FreshRankKlModel queryHisSaleAlllKl(FreshReportBaseRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_FRESH_SALE_TOTAL_KL_PREFIX);
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
                sb.append(" and b.store  = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }
        return super.queryForObject(sb.toString(), TOTAL_FRESHRANKKL_RM, paramList.toArray());
    }

    /**
     * 查询历史生鲜32，35，36，37汇总客流
     * @param request
     * @param start
     * @param end
     * @return
     */
    public FreshRankKlModel queryHisSaleFourKl(FreshReportBaseRequest request, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_FRESH_SALE_FOUR_KL_PREFIX);
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
                sb.append(" and b.store  = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }
        return super.queryForObject(sb.toString(), TOTAL_FRESHRANKKL_RM, paramList.toArray());
    }

    /**
     * 查询排行榜总生鲜客流
     * @param request
     * @return
     */
    public List<FreshRankKlModel> queryHisFreshRankKlForAll(FreshReportBaseRequest request,Date start, Date end, int mark){
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
                sb.append(GET_FRESH_KL_STORE_PREFIX);
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
                sb.append(" and b.store  = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        switch (mark){
            case 1:
                sb.append(" group by b.area ");
                break;
            case 2:
                sb.append(" group by b.region ");
                break;
            case 3:
                sb.append(" group by b.store ");
                break;
            case 4:
                sb.append(" group by b.store ");
                break;
            default:
                sb.append("");
                break;
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHRANKKL_RM,paramList.toArray());
    }

    /**
     * 查询排行榜32，35，36，37的合计客流
     * @param request
     * @param start
     * @param end
     * @param mark
     * @return
     */
    public List<FreshRankKlModel> queryHisFreshRankKlForFour(FreshReportBaseRequest request,Date start, Date end, int mark){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        switch (mark){
            case 1:
                sb.append(GET_FRESH_KL_PROVINCE_4_PREFIX);
                break;
            case 2:
                sb.append(GET_FRESH_KL_AREA_4_PREFIX);
                break;
            case 3:
                sb.append(GET_FRESH_KL_STORE_4_PREFIX);
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
                sb.append(" and b.store  = ? ");
                paramList.add(request.getStoreIds().get(0));
            }else if(request.getStoreIds().size() > 1){
                sb.append(" and b.store in ( ");
                sb.append(request.getStoreId());
                sb.append(" ) ");
            }
        }

        switch (mark){
            case 1:
                sb.append(" group by b.area ");
                break;
            case 2:
                sb.append(" group by b.region ");
                break;
            case 3:
                sb.append(" group by b.store ");
                break;
            default:
                sb.append("");
                break;
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHRANKKL_RM,paramList.toArray());
    }

    /**
     * 查询历史会员客流（优化版）
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<MemberModel> queryHisFreshMemberDetail(PageRequest param, Date start, Date end){
        List<MemberModel> result = null;
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder(GET_HIS_FRESH_MEMBER_DETAIL_PREFIX);
        if(null == end){
            sb.append(" and a.business_date = date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
        }else{
            sb.append(" and a.business_date <= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
            sb.append(" and a.business_date >= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
        sb.append("  group by decode(a.vip_no, null, 'N', 'Y'),decode(a.channel, null, 'BKSLS', a.channel) ");
        result = super.queryForList(sb.toString(), TOTAL_MEMBERMODEL_RM, paramList.toArray());
        return result;
    }

    /**
     * 查询历史生鲜会员客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<MemberPermeabilityPo> queryHisFreshMember(PageRequest param, Date start, Date end){
        List<MemberPermeabilityPo> result = null;
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder(GET_HIS_FRESH_MEMBER_PREFIX);
        if(null == end){
            sb.append(" and a.business_date = date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
        }else{
            sb.append(" and a.business_date <= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
            sb.append(" and a.business_date >= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
        sb.append(" group by decode(a.vip_no, null, 'N', 'Y'),  " +
                "(case when a.channel in ('YUNPOS', 'VIP', 'WX', 'WX1', 'WX2', 'WX3', 'DD1', 'JD1', 'KJG') or a.channel is null then 'off' else 'on' end) ");
        result = super.queryForList(sb.toString(), MEMBER_RM, paramList.toArray());
        return result;
    }

    /**
     * 查询历史生鲜会员客流(根据大类ID)
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<MemberPermeabilityPo> queryHisFreshMemberForDept(PageRequest param, Date start, Date end){
        List<MemberPermeabilityPo> result = null;
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder(GET_HIS_FRESH_MEMBER_DEPT_PREFIX);
        if(null == end){
            sb.append(" and a.business_date = date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
        }else{
            sb.append(" and a.business_date <= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
            sb.append(" and a.business_date >= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append("and a.t").append(param.getDeptIds().get(0)).append(" is not null ");
            }else if(param.getDeptIds().size() > 1){
                StringBuilder sbb = new StringBuilder(" and (");
                for(String deptId : param.getDeptIds()){
                    sbb.append(" a.t").append(deptId).append(" is not null ").append(" or ");
                }
                if(sbb.toString().endsWith("or ")){
                    String str = sbb.toString();
                    sb.append(str.substring(0,str.lastIndexOf("or "))).append(") ");
                }
            }
        }

        sb.append(" group by decode(a.vip_no, null, 'N', 'Y'),  " +
                "(case when a.channel in ('YUNPOS', 'VIP', 'WX', 'WX1', 'WX2', 'WX3', 'DD1', 'JD1', 'KJG') or a.channel is null then 'off' else 'on' end) ");
        result = super.queryForList(sb.toString(), MEMBER_RM, paramList.toArray());
        return result;
    }
    /**
     * 查询vip数量明细(不包含大类)
     * @param param
     * @return
     */
    public List<MemberModel> queryHisMemberDetailNotDept(PageRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_MEMBER_NOT_DEPT_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.business_date = date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }else{
            sb.append(" and a.business_date <= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
            sb.append(" and a.business_date >= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
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
        sb.append(" group by decode(a.vip_no, null, 'N', 'Y'),decode(a.channel,null,'BKSLS',a.channel) ");
        return super.queryForList(sb.toString(), TOTAL_MEMBERMODEL_RM, paramList.toArray());
    }
    /**
     * 查询vip数量明细(包含大类)
     * @param param
     * @return
     */
    public List<MemberModel> queryHisMemberDetailHaveDept(PageRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(GET_HIS_MEMBER_HAVE_DEPT_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(null == end){
            sb.append(" and a.business_date = date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }else{
            sb.append(" and a.business_date <= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
            sb.append(" and a.business_date >= date '");
            sb.append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
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

        sb.append(" group by decode(a.vip_no, null, 'N', 'Y'),decode(a.channel,null,'BKSLS',a.channel) ");
        return super.queryForList(sb.toString(), TOTAL_MEMBERMODEL_RM, paramList.toArray());
    }
    /**
     * 查询历史生鲜会员大类客流(指定多个时间点，历史某天，昨日，周，月)
     * @param param
     * @param start
     * @return
     */
    public List<FreshDeptKl> queryHisFreshVipDeptKl(FreshReportBaseRequest param, Date start, String hour){
        StringBuilder sb = new StringBuilder(GET_HIS_FRESH_DEPT_VIP_KL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(DateUtils.getBetweenDay(start,new Date()) >= 0){
            sb.append(" and a.hour <= '").append(hour).append("' ");
        }
        sb.append(" and a.business_date in (");
        //当天
        sb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("',");
        //昨日
        sb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addDays(start,-1))).append("',");
        //周
        sb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addWeeks(start,-1))).append("',");
        //月
        sb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addMonths(start,-1))).append("') ");
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
                sb.append(" and b.store = ? ");
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
        sb.append(" group by a.dept,a.business_date");
        return super.queryForList(sb.toString(),TOTAL_FRESHDEPTKL_RM,paramList.toArray());
    }

    /**
     * 查询历史生鲜大类客流(指定多个时间点，历史某天，昨日，周，月)
     * @param param
     * @param start
     * @return
     */
    public List<FreshDeptKl> queryHisFreshDeptKl(FreshReportBaseRequest param,Date start,String hour){
        StringBuilder sb = new StringBuilder(GET_HIS_FRESH_DEPT_KL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        if(DateUtils.getBetweenDay(start,new Date()) >= 0){
            sb.append(" and a.hour <= '").append(hour).append("' ");
        }
        sb.append(" and a.business_date in (");
        //当天
        sb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("',");
        //昨日
        sb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addDays(start,-1))).append("',");
        //周
        sb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addWeeks(start,-1))).append("',");
        //月
        sb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addMonths(start,-1))).append("') ");
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
                sb.append(" and b.store = ? ");
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
        sb.append(" group by a.dept,a.business_date");
        return super.queryForList(sb.toString(),TOTAL_FRESHDEPTKL_RM,paramList.toArray());
    }


    /**
     * 查询客流（1-生鲜客流，2-总可流）
     * @param request
     * @param type
     * @return
     */
    public List<FreshKl> queryHisFreshKlCount(FreshReportBaseRequest request, int type){
        StringBuilder sb = new StringBuilder();
        StringBuilder timeSb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        if(1 == type){
            sb.append(GET_FRESH_KL_PREFIX);
            if((null != request.getStart()
                    && DateUtils.getBetweenDay(request.getStart(),new Date()) >= 0)
                    || (null != request.getEnd()
                    && DateUtils.getBetweenDay(request.getEnd(),new Date()) >= 0)){
                sb.append(" and a.tran_datetime <= '").append(DateUtils.parseDateToStr("HH:mm:ss"
                        ,new Date())).append("' ");
                sb.append(" and a.tran_datetime >= '").append("00:00:00").append("' ");
            }

            //当日
            timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",request.getStart())).append("', ");
            //昨日
            timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addDays(request.getStart()
                    ,-1))).append("', ");
            //周
            timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addWeeks(request.getStart
                    (), -1))).append("', ");
            //月
            timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addMonths(request.getStart
                    (),-1))).append("'");

            sb.append(" and a.business_date in (");
            sb.append(timeSb.toString());
            sb.append(") ");
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
            sb.append(" group by a.business_date");
        }else{
            sb.append(GET_FRESH_TOTAL_KL_PREFIX);
            if((null != request.getStart()
                    && DateUtils.getBetweenDay(request.getStart(),new Date()) >= 0)
                    || (null != request.getEnd()
                    && DateUtils.getBetweenDay(request.getEnd(),new Date()) >= 0)){
                sb.append(" and a.tran_datetime <= '").append(DateUtils.parseDateToStr("HH:mm:ss"
                        ,new Date())).append("' ");
//                sb.append(" and a.tran_datetime >= '").append("00:00:00").append("' ");
            }

            //当日
            timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",request.getStart())).append("', ");
            //昨日
            timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addDays(request.getStart()
                    ,-1))).append("', ");
            //周
            timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addWeeks(request.getStart
                    (), -1))).append("', ");
            //月
            timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addMonths(request.getStart
                    (),-1))).append("'");

            sb.append(" and a.business_date in (");
            sb.append(timeSb.toString());
            sb.append(") ");

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

            sb.append(" group by a.business_date");
        }
        List<FreshKl> list = super.queryForList(sb.toString(),TOTAL_FRESH_KL_RM,paramList.toArray());
        return list;
    }

    public List<SalesList> querySameTotalSalesAndProfitForHour(FreshReportBaseRequest pageRequest, String startHour, String endHour){
        StringBuilder sb = new StringBuilder(GET_MEMBER_PREFIX_SAME_TIME);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.hour <= ? ");
        paramList.add(endHour);
        sb.append(" and a.hour >= ? ");
        paramList.add(startHour);
        StringBuilder timeSb = new StringBuilder();
        //昨日
        timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addDays(pageRequest.getStart(),-1))).append("', ");
        //周
        timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addWeeks(pageRequest.getStart(),-1))).append("', ");
        //月
        timeSb.append(" date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",DateUtils.addMonths(pageRequest.getStart(),-1))).append("'");
        sb.append(" and a.business_date in (").append(timeSb.toString()).append(") ");
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
        sb.append(" group by a.business_date");
        List<SalesList> list = super.queryForList(sb.toString(),TOTAL_FRESH_SALES_RM,paramList.toArray());
        return list;
    }

    /**
     * 查询历史生鲜数据(折价额)
     * @param param
     * @return
     */
    public MonthStatistics queryHisFreshInfo(FreshReportBaseRequest param){
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder();
        sb.append(getHisCommenProfixSql(param, paramList));
        sb.append(getHisCommenSql(param, paramList));
        MonthStatistics monthStatistics = super.queryForObject(sb.toString(), TOTAL_FRESH_RM, paramList.toArray());
        return monthStatistics;
    }

    /**
     * 按照分组查询历史生鲜数据(折价额)
     * @param param
     * @return
     */
    public List<FreshRankInfo> queryHisFreshDiscount(FreshReportBaseRequest param){
        List<Object> paramList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder();
        sb.append(getHisFreshDiscountProfixSql(param, paramList));
        sb.append(getHisCommenSql(param, paramList));
        sb.append(" group by c.area");
        List<FreshRankInfo> list = super.queryForList(sb.toString(), TOTAL_FRESH_PROVINCE_RM, paramList.toArray());
        return list;
    }

    /**
     * 历史数据前缀
     * @param request
     * @return
     */
    public String getHisCommenProfixSql(FreshReportBaseRequest request, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_HIS_TOTAL_SALES_DISCOUNT_PREFIX);
        if(null == request.getEnd()){
            sb.append(" and a.business_date = date '");
            sb.append(DateUtil.getDateFormat(request.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and a.business_date <= date '");
            sb.append(DateUtil.getDateFormat(request.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and a.business_date >= date '");
            sb.append(DateUtil.getDateFormat(request.getStart(),"yyyy-MM-dd")).append("' ");
        }
        return sb.toString();
    }

    /**
     * 按照省级分组查询历史生鲜折价额
     * @param request
     * @return
     */
    public String getHisFreshDiscountProfixSql(FreshReportBaseRequest request, List<Object> paramList){
        StringBuilder sb = new StringBuilder(GET_HIS_TOTAL_SALES_PROFIT_PREFIX_PROVINCE);
        if(null == request.getEnd()){
            sb.append(" and a.business_date = date '");
            sb.append(DateUtil.getDateFormat(request.getStart(),"yyyy-MM-dd")).append("' ");
        }else{
            sb.append(" and a.business_date <= date '");
            sb.append(DateUtil.getDateFormat(request.getEnd(),"yyyy-MM-dd")).append("' ");
            sb.append(" and a.business_date >= date '");
            sb.append(DateUtil.getDateFormat(request.getStart(),"yyyy-MM-dd")).append("' ");
        }
        return sb.toString();
    }

    /**
     *  历史数据
     * @param request
     * @return
     */
    public String getHisCommenSql(FreshReportBaseRequest request, List<Object> paramList){
        StringBuilder sb = new StringBuilder();
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
                sb.append(" and c.store  = ? ");
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
                sb.append(" and a.dept  = ? ");
                paramList.add(request.getDeptIds().get(0));
            }else if(request.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(request.getDeptId());
                sb.append(" ) ");
            }
        }
        return sb.toString();
    }

    public List<FreshRankInfo> queryHisCompareFreshRankForTimeInfo(FreshRankRequest param){
        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        sb.append(getHisCompareFreshRankForTimeProfixSql(param,"00",DateUtils.parseDateToStr("HH",new Date()),param.getMark()));
        sb.append(getHisFreshRankForTimeSql(param, paramList));
        sb.append(getHisCompareFreshRankForTimeGroupSql(param.getMark()));
        List<FreshRankInfo> list = super.queryForList(sb.toString(),TOTAL_FRESH_PROVINCE_RM,paramList.toArray());
        return list;
    }

    /**
     * 查询排行榜前缀sql(同期)
     * @param param
     * @param type
     * @return
     */
    private String getHisCompareFreshRankForTimeProfixSql(FreshReportBaseRequest param,String startHour, String endHour, int type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb.append(GET_MEMBER_PREFIX_SAME_PROVINCE_TIME_COMPARE);
                break;
            case 2:
                sb.append(GET_MEMBER_PREFIX_SAME_AREA_TIME_COMPARE);
                break;
            case 3:
                sb.append(GET_MEMBER_PREFIX_SAME_STORE_TIME_COMPARE);
                break;
            case 4:
                sb.append(GET_MEMBER_PREFIX_SAME_DEPT_TIME_COMPARE);
                break;
            default:
                sb.append("");
                break;
        }
        sb.append(" and a.hour <= '").append(endHour).append("' ");
        sb.append(" and a.hour >= '").append(startHour).append("' ");
        sb.append(" and a.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",param.getStart())).append("' ");
        return sb.toString();
    }

    private String getHisFreshRankForTimeSql(FreshReportBaseRequest param, List<Object> paramList){
        StringBuilder sb = new StringBuilder();
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
                sb.append(" and b.store = ? ");
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
        return sb.toString();
    }

    private String getHisCompareFreshRankForTimeGroupSql(int type){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case 1:
                sb.append(" group by b.area, b.mall_name");
                break;
            case 2:
                sb.append(" group by b.region, b.mall_name");
                break;
            case 3:
                sb.append(" group by b.store, b.mall_name");
                break;
            case 4:
                sb.append(" group by a.dept, b.mall_name");
                break;
            default:
                sb.append("");
                break;
        }
        return sb.toString();
    }
}
