package com.cs.mobile.api.dao.freshSpecialReport;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.freshspecialreport.*;
import com.cs.mobile.api.model.freshspecialreport.request.FreshSpecialReportRequest;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Repository
public class FreshSpecialReportForDayDao extends AbstractDao {
    private static final RowMapper<DeptModel> FRESH_DEPT_RM = new BeanPropertyRowMapper<DeptModel>(DeptModel.class);
    private static final RowMapper<ClassModel> FRESH_CLASS_RM = new BeanPropertyRowMapper<ClassModel>(ClassModel.class);
    private static final RowMapper<SubclassModel> FRESH_SUBCLASS_RM = new BeanPropertyRowMapper<SubclassModel>(SubclassModel.class);
    private static final RowMapper<FreshAllKlModel> FRESH_ALLKL_RM = new BeanPropertyRowMapper<FreshAllKlModel>(FreshAllKlModel.class);
    private static final RowMapper<FreshSaleAndRateModel> TOTAL_FRESHSALEANDRATE_RM = new BeanPropertyRowMapper<FreshSaleAndRateModel>(FreshSaleAndRateModel.class);
    private static final RowMapper<OrderAndStockAmountModel> TOTAL_ORDERANDSTOCKAMOUNT_RM = new BeanPropertyRowMapper<OrderAndStockAmountModel>(OrderAndStockAmountModel.class);
    private static final String GET_FRESH_DEPT_PREFIX = "select dept as deptId, " +
            " dept_name as deptName " +
            " from csmb_code_dept ";

    private static final String GET_FRESH_CLASS_PREFIX = "select dept as deptId, " +
            " class as classId, " +
            " class_name as className " +
            " from csmb_code_class where 1=1 ";

    private static final String GET_FRESH_SUBCLASS_PREFIX = "select dept as deptId, " +
            " class as classId, " +
            " subclass as subclassId, " +
            " sub_name as subclassName " +
            " from csmb_code_subclass where 1=1 ";

    private static final String TOTAL_DEPT_HIS_FRESHSALEANDRATE_PREFIX = "select  max(a.dept) as id, " +
            "  sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_dept_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_DEPT_CURRENT_FRESHSALEANDRATE_PREFIX = "select max(a.DEPT) as id, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " sum(a.AMT_ECL) as sale, " +
            " sum(a.GP_ECL) as rate, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT_ECL end) as compareSale, " +
            " sum(case when a.MALL_NAME = 1 then a.GP_ECL end) as compareRate, " +
            " sum(a.AMT) as saleIn, " +
            " sum(a.GP) as rateIn, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT end) as compareSaleIn, " +
            " sum(case when a.MALL_NAME = 1 then a.GP end) as compareRateIn " +
            " from CSMB_STORE_SALEDETAIL a  " +
            " where 1=1 ";

    private static final String TOTAL_ORDERANDSTOCKAMOUNT_PREFIX = "SELECT sum(nvl(a.stock_on_hand * a.av_cost,0)) as stockAmount, " +
            "       sum(nvl(PO_TOTAL_COST, 0) + nvl(DISTRO_TOTAL_COST, 0)) as orderAmount, " +
            "       sum(nvl(PO_TOTAL_COST_IN, 0) + nvl(DISTRO_TOTAL_COST_IN, 0)) as orderAmountIn " +
            "  FROM zypp.Cal_item_loc_soh_yesterday_all a, zypp.inf_store b " +
            " where a.loc = b.store ";

    private static final String TOTAL_FRESH_ALLKL_PREFIX = "select sum(a.kl) as kl " +
            " from CSMB_STORE_KL a " +
            " where 1=1 ";

    private static final String TOTAL_CLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX = "select a.class as id, " +
            "   sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_class_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_CLASS_CURRENT_FRESHSALEANDRATE_LIST_PREFIX = "select a.class as id, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " sum(a.AMT_ECL) as sale, " +
            " sum(a.GP_ECL) as rate, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT_ECL end) as compareSale, " +
            " sum(case when a.MALL_NAME = 1 then a.GP_ECL end) as compareRate, " +
            " sum(a.AMT) as saleIn, " +
            " sum(a.GP) as rateIn, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT end) as compareSaleIn, " +
            " sum(case when a.MALL_NAME = 1 then a.GP end) as compareRateIn " +
            " from CSMB_STORE_SALEDETAIL a  " +
            " where 1=1 ";

    private static final String TOTAL_CLASS_HIS_FRESHSALEANDRATE_PREFIX = "select max(a.class) as id, " +
            "   sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_class_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_CLASS_CURRENT_FRESHSALEANDRATE_PREFIX = "select max(a.CLASS) as id, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " sum(a.AMT_ECL) as sale, " +
            " sum(a.GP_ECL) as rate, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT_ECL end) as compareSale, " +
            " sum(case when a.MALL_NAME = 1 then a.GP_ECL end) as compareRate, " +
            " sum(a.AMT) as saleIn, " +
            " sum(a.GP) as rateIn, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT end) as compareSaleIn, " +
            " sum(case when a.MALL_NAME = 1 then a.GP end) as compareRateIn " +
            " from CSMB_STORE_SALEDETAIL a  " +
            " where 1=1 ";

    private static final String TOTAL_SUBCLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX = "select a.subclass as id, " +
            "   sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_subclass_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_SUBCLASS_CURRENT_FRESHSALEANDRATE_LIST_PREFIX = "select a.SUBCLASS as id, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " sum(a.AMT_ECL) as sale, " +
            " sum(a.GP_ECL) as rate, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT_ECL end) as compareSale, " +
            " sum(case when a.MALL_NAME = 1 then a.GP_ECL end) as compareRate, " +
            " sum(a.AMT) as saleIn, " +
            " sum(a.GP) as rateIn, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT end) as compareSaleIn, " +
            " sum(case when a.MALL_NAME = 1 then a.GP end) as compareRateIn " +
            " from CSMB_STORE_SALEDETAIL a  " +
            " where 1=1 ";

    private static final String TOTAL_REGION_CURRENT_FRESHSALEANDRATE_LIST_PREFIX = "select a.REGION as id, " +
            " b.area_name as name, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " sum(a.AMT_ECL) as sale, " +
            " sum(a.GP_ECL) as rate, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT_ECL end) as compareSale, " +
            " sum(case when a.MALL_NAME = 1 then a.GP_ECL end) as compareRate, " +
            " sum(a.AMT) as saleIn, " +
            " sum(a.GP) as rateIn, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT end) as compareSaleIn, " +
            " sum(case when a.MALL_NAME = 1 then a.GP end) as compareRateIn " +
            " from CSMB_STORE_SALEDETAIL a, csmb_store b " +
            " where a.STORE = b.store_id " +
            " and a.REGION = b.area_id " +
            " and a.area = b.province_id ";

    private static final String TOTAL_REGION_DEPT_HIS_FRESHSALEANDRATE_LIST_PREFIX = "select a.region as id, " +
            "   sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_dept_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_REGION_CLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX = "select a.region as id, " +
            "   sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_class_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_REGION_SUBCLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX = "select a.region as id, " +
            "   sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_subclass_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_STORE_CURRENT_FRESHSALEANDRATE_LIST_PREFIX = "select * from (select a.STORE as id, " +
            " b.store_name as name, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " sum(a.AMT_ECL) as sale, " +
            " sum(a.GP_ECL) as rate, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT_ECL end) as compareSale, " +
            " sum(case when a.MALL_NAME = 1 then a.GP_ECL end) as compareRate, " +
            " sum(a.AMT) as saleIn, " +
            " sum(a.GP) as rateIn, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT end) as compareSaleIn, " +
            " sum(case when a.MALL_NAME = 1 then a.GP end) as compareRateIn " +
            " from CSMB_STORE_SALEDETAIL a, csmb_store b " +
            " where a.STORE = b.store_id " +
            " and a.REGION = b.area_id " +
            " and a.area = b.province_id ";

    private static final String TOTAL_STORE_CLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX = "select a.store as id, " +
            "   sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_class_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_STORE_DEPT_HIS_FRESHSALEANDRATE_LIST_PREFIX = "select a.store as id, " +
            "   sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_dept_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_STORE_SUBCLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX = "select a.store as id, " +
            "   sum(td_cp_sale_value) as sale, " +
            "   sum(nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value,0) - nvl(td_cp_sale_cost,0) end) as compareRate, " +
            "   sum(td_cp_sale_value_in) as saleIn, " +
            "   sum(nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then td_cp_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(td_cp_sale_value_in,0) - nvl(td_cp_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_subclass_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_STORE_FRESH_ALLKL_PREFIX = "select a.store as id, " +
            " sum(a.kl) as kl " +
            " from CSMB_STORE_KL a " +
            " where 1=1 ";

    private static final String TOTAL_REGION_FRESH_ALLKL_PREFIX = "select a.region as id, " +
            " sum(a.kl) as kl " +
            " from CSMB_STORE_KL a " +
            " where 1=1 " ;

    private static final String TOTAL_ITEM_CURRENT_FRESHSALEANDRATE_LIST_PREFIX = "select * from (select a.ITEM as id,a.ITEM_dESC as name, " +
            " COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) as kl, " +
            " sum(a.AMT_ECL) as sale, " +
            " sum(a.GP_ECL) as rate, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT_ECL end) as compareSale, " +
            " sum(case when a.MALL_NAME = 1 then a.GP_ECL end) as compareRate, " +
            " sum(a.AMT) as saleIn, " +
            " sum(a.GP) as rateIn, " +
            " sum(case when a.MALL_NAME = 1 then a.AMT end) as compareSaleIn, " +
            " sum(case when a.MALL_NAME = 1 then a.GP end) as compareRateIn " +
            " from CSMB_STORE_SALEDETAIL a " +
            " where 1=1 ";

    private static final String TOTAL_ITEM_YESTERDAY_FRESHSALEANDRATE_LIST_PREFIX = "select /*+PARALLEL(A,8)*/  " +
            "   a.item as id, " +
            "   sum(yd_sale_value) as sale, " +
            "   sum(nvl(yd_sale_value,0) - nvl(yd_sale_cost,0)) as rate, " +
            "   sum(yd_sale_value_in) as saleIn, " +
            "   sum(nvl(yd_sale_value_in,0) - nvl(yd_sale_cost_in,0)) as rateIn " +
            " from cmx.Cmx_rpt_store_item_gp_all a, rms.MV_LOC_MGR b, rms.ITEM_MASTER c " +
            " where a.store = b.store " +
            " and a.item = c.item ";

    private static final String TOTAL_ITEM_ORDERANDSTOCKAMOUNT_PREFIX = "SELECT a.item as id, " +
            "       sum(nvl(a.stock_on_hand,0)) as stock, " +
            "       sum(nvl(a.stock_on_hand * a.av_cost,0)) as stockAmount, " +
            "       sum(nvl(po_qty, 0) + nvl(distro_qty, 0)) as orderNum, " +
            "       sum(nvl(PO_TOTAL_COST, 0) + nvl(DISTRO_TOTAL_COST, 0)) as orderAmount,  " +
            "       sum(nvl(PO_TOTAL_COST_IN, 0) + nvl(DISTRO_TOTAL_COST_IN, 0)) as orderAmountIn " +
            "  FROM zypp.Cal_item_loc_soh_yesterday_all a,zypp.inf_store b " +
            " where a.loc = b.store ";

    /**
     * 根据大类查询往期区域信息
     * @param param
     * @param start
     * @param storeList
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreDeptHisFreshSaleAndRate(FreshSpecialReportRequest param, Date start, List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_DEPT_HIS_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != storeList && storeList.size() > 0){
            if(storeList.size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(storeList.get(0));
            }else if(storeList.size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(listToStr(storeList));
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by a.store ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }
    /**
     * 根据大类查询区域数据
     * @param param
     * @param start
     * @param regionList
     * @return
     */
    public List<FreshSaleAndRateModel> queryRegionDeptHisFreshSaleAndRate(FreshSpecialReportRequest param, Date start, List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_REGION_DEPT_HIS_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != regionList && regionList.size() > 0){
            if(regionList.size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(regionList.get(0));
            }else if(regionList.size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(listToStr(regionList));
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by a.region ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }


    /**
     * 查询商品的库存数量，库存金额，订货数量，订货金额
     * @param param
     * @param start
     * @param itemList
     * @return
     */
    public List<OrderAndStockAmountModel> queryItemOrderAndStockAmount(FreshSpecialReportRequest param, Date start,List<String> itemList){
        StringBuilder sb = new StringBuilder(TOTAL_ITEM_ORDERANDSTOCKAMOUNT_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.soh_bak_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.loc = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.loc in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }

        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.class = '").append(param.getClassId()).append("' ");
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.subclass = '").append(param.getSubClassId()).append("' ");
        }
        //拼接商品
        if(null != itemList && itemList.size() > 0){
            if(itemList.size() == 1){
                sb.append(" and a.item = ? ");
                paramList.add(itemList.get(0));
            }else if(itemList.size() > 1){
                sb.append(" and a.item in ( ");
                sb.append(itemListToStr(itemList));
                sb.append(" ) ");
            }
        }

        sb.append(" group by a.item ");
        return super.queryForList(sb.toString(), TOTAL_ORDERANDSTOCKAMOUNT_RM, paramList.toArray());
    }

    /**
     * 查询商品昨日销售金额和毛利额
     * @param param
     * @param start
     * @param itemList
     * @return
     */
    public List<FreshSaleAndRateModel> queryItemYesterdayFreshSaleAndRate(FreshSpecialReportRequest param, Date start, List<String> itemList){
        StringBuilder sb = new StringBuilder(TOTAL_ITEM_YESTERDAY_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.yesterday = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
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
                sb.append(" and c.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and c.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and c.class = '").append(param.getClassId()).append("' ");
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and c.subclass = '").append(param.getSubClassId()).append("' ");
        }

        //拼接商品
        if(null != itemList && itemList.size() > 0){
            if(itemList.size() == 1){
                sb.append(" and a.item = ? ");
                paramList.add(itemList.get(0));
            }else if(itemList.size() > 1){
                sb.append(" and a.item in ( ");
                sb.append(itemListToStr(itemList));
                sb.append(" ) ");
            }
        }

        sb.append(" group by a.item ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询商品实时销售额，毛利额，客流，可比销售额，可比毛利额，商品ID，商品名称
     * @param param
     * @param start
     * @return
     */
    public List<FreshSaleAndRateModel> queryItemCurrentFreshSaleAndRate(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_ITEM_CURRENT_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        int sort = Integer.valueOf(param.getIsLast()).intValue();
        sb.append(" and a.SALEDATE = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.CLASS = '").append(param.getClassId()).append("' ");
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.SUBCLASS = '").append(param.getSubClassId()).append("' ");
        }
        sb.append(" group by a.ITEM,a.ITEM_dESC ");
        sb.append(" order by COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) ");
        if(0 == sort){
            sb.append(" desc ");
        }else if(1 == sort){
            sb.append(" asc ");
        }
        sb.append(") where rownum <= 10");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询区域生鲜总客流
     * @param param
     * @param start
     * @param regionList
     * @return
     */
    public List<FreshAllKlModel> queryRegionFreshAllKl(FreshSpecialReportRequest param, Date start, List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_REGION_FRESH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        if(null != regionList && regionList.size() > 0){
            if(regionList.size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(regionList.get(0));
            }else if(regionList.size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(listToStr(regionList));
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        sb.append(" group by a.region ");
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询门店生鲜总客流
     * @param param
     * @param start
     * @param storeList
     * @return
     */
    public List<FreshAllKlModel> queryStoreFreshAllKl(FreshSpecialReportRequest param, Date start, List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_FRESH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != storeList && storeList.size() > 0){
            if(storeList.size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(storeList.get(0));
            }else if(storeList.size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(listToStr(storeList));
                sb.append(" ) ");
            }
        }
        sb.append(" group by a.store ");
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询门店小类同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param storeList
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreSubClassHisFreshSaleAndRate(FreshSpecialReportRequest param, Date start, List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_SUBCLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != storeList && storeList.size() > 0){
            if(storeList.size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(storeList.get(0));
            }else if(storeList.size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(listToStr(storeList));
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.CLASS = '").append(param.getClassId()).append("' ");
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.subclass = '").append(param.getSubClassId()).append("' ");
        }
        sb.append(" group by a.store ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询门店中类同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param storeList
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreClassHisFreshSaleAndRate(FreshSpecialReportRequest param, Date start, List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_CLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != storeList && storeList.size() > 0){
            if(storeList.size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(storeList.get(0));
            }else if(storeList.size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(listToStr(storeList));
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.CLASS = '").append(param.getClassId()).append("' ");
        }
        sb.append(" group by a.store ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询门店实时销售额，毛利额，客流，门店ID,门店名称，可比销售额，可比毛利额
     * @param param
     * @param start
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreCurrentFreshSaleAndRate(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_CURRENT_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        int sort = Integer.valueOf(param.getIsLast()).intValue();
        sb.append(" and a.SALEDATE = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.CLASS = '").append(param.getClassId()).append("' ");
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.SUBCLASS = '").append(param.getSubClassId()).append("' ");
        }
        sb.append(" group by a.STORE,b.store_name ");
        sb.append(" order by COUNT(DISTINCT STORE || SALEDATE || BILLNO || POSID) ");
        if(0 == sort){
            sb.append(" desc ");
        }else if(1 == sort){
            sb.append(" asc ");
        }
        sb.append(") where rownum <= 10");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询区域小类同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param regionList
     * @return
     */
    public List<FreshSaleAndRateModel> queryRegionSubClassHisFreshSaleAndRate(FreshSpecialReportRequest param, Date start, List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_REGION_SUBCLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != regionList && regionList.size() > 0){
            if(regionList.size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(regionList.get(0));
            }else if(regionList.size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(listToStr(regionList));
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.CLASS = '").append(param.getClassId()).append("' ");
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.subclass = '").append(param.getSubClassId()).append("' ");
        }
        sb.append(" group by a.region ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询区域中类同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param regionList
     * @return
     */
    public List<FreshSaleAndRateModel> queryRegionClassHisFreshSaleAndRate(FreshSpecialReportRequest param, Date start, List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_REGION_CLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != regionList && regionList.size() > 0){
            if(regionList.size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(regionList.get(0));
            }else if(regionList.size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(listToStr(regionList));
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.CLASS = '").append(param.getClassId()).append("' ");
        }
        sb.append(" group by a.region ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询区域实时销售额，毛利额，客流，可比销售额，可比毛利额
     * @param param
     * @param start
     * @return
     */
    public List<FreshSaleAndRateModel> queryRegionCurrentFreshSaleAndRate(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_REGION_CURRENT_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        int sort = Integer.valueOf(param.getIsLast()).intValue();
        sb.append(" and a.SALEDATE = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.CLASS = '").append(param.getClassId()).append("' ");
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.SUBCLASS = '").append(param.getSubClassId()).append("' ");
        }
        sb.append(" group by a.REGION,b.area_name ");
        sb.append(" order by kl ");
        if(0 == sort){
            sb.append(" desc ");
        }else if(1 == sort){
            sb.append(" asc ");
        }
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 根据大类ID查询大类信息
     * @param deptId
     * @return
     */
    public DeptModel queryDeptInfByDeptId(String deptId){
        StringBuilder sb = new StringBuilder(GET_FRESH_DEPT_PREFIX);
        sb.append(" where dept = '").append(deptId).append("' ");
        return super.queryForObject(sb.toString(), FRESH_DEPT_RM);
    }

    /**
     * 查询中类信息
     * @param deptId
     * @return
     */
    public List<ClassModel> queryClassInfos(String deptId){
        StringBuilder sb = new StringBuilder(GET_FRESH_CLASS_PREFIX);
        sb.append(" and dept = '").append(deptId).append("' ");
        sb.append(" order by class ");
        return super.queryForList(sb.toString(), FRESH_CLASS_RM, null);
    }

    /**
     * 根据大类ID，中类ID查询中类信息
     * @param deptId
     * @param classId
     * @return
     */
    public ClassModel queryClassInfoByClassId(String deptId, String classId){
        StringBuilder sb = new StringBuilder(GET_FRESH_CLASS_PREFIX);
        if(StringUtils.isNotEmpty(deptId)){
            sb.append(" and dept = '").append(deptId).append("' ");
        }
        sb.append(" and class = '").append(classId).append("' ");
        return super.queryForObject(sb.toString(), FRESH_CLASS_RM);
    }

    /**
     * 根据大类ID，中类ID查询小类信息
     * @param deptId
     * @param classId
     * @return
     */
    public List<SubclassModel> querySubclassInfos(String deptId, String classId){
        StringBuilder sb = new StringBuilder(GET_FRESH_SUBCLASS_PREFIX);
        if(StringUtils.isNotEmpty(deptId)){
            sb.append(" and dept = '").append(deptId).append("' ");
        }
        sb.append(" and class = '").append(classId).append("' ");
        sb.append(" order by subclass ");
        return super.queryForList(sb.toString(), FRESH_SUBCLASS_RM);
    }

    /**
     * 查询大类同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @return
     */
    public FreshSaleAndRateModel queryDeptHisFreshSaleAndRate(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_DEPT_HIS_FRESHSALEANDRATE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        return super.queryForObject(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询大类实时销售额，毛利额，客流，可比销售额，可比毛利额
     * @param param
     * @param start
     * @return
     */
    public FreshSaleAndRateModel queryDeptCurrentFreshSaleAndRate(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_DEPT_CURRENT_FRESHSALEANDRATE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.SALEDATE = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        return super.queryForObject(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询库存金额，订货金额
     * @param param
     * @param start
     * @return
     */
    public OrderAndStockAmountModel queryOrderAndStockAmount(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_ORDERANDSTOCKAMOUNT_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.soh_bak_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.loc = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.loc in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }

        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.class = '").append(param.getClassId()).append("' ");
        }
        return super.queryForObject(sb.toString(), TOTAL_ORDERANDSTOCKAMOUNT_RM, paramList.toArray());
    }

    /**
     * 查询总客流
     * @param param
     * @param start
     * @return
     */
    public FreshAllKlModel queryFreshAllKl(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_FRESH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }
        return super.queryForObject(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询中类同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @return
     */
    public List<FreshSaleAndRateModel> queryClassHisFreshSaleAndRateList(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_CLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by a.class ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询中类实时销售额，毛利额，可比销售额，可比毛利额，客流，中类ID
     * @param param
     * @param start
     * @return
     */
    public List<FreshSaleAndRateModel> queryClassCurrentFreshSaleAndRateList(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_CLASS_CURRENT_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.SALEDATE = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" group by a.CLASS ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询中类同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @return
     */
    public FreshSaleAndRateModel queryClassHisFreshSaleAndRate(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_CLASS_HIS_FRESHSALEANDRATE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" and a.class = '").append(param.getClassId()).append("' ");
        return super.queryForObject(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询中类实时销售额，毛利额，可比销售额，可比毛利额，客流，中类ID
     * @param param
     * @param start
     * @return
     */
    public FreshSaleAndRateModel queryClassCurrentFreshSaleAndRate(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_CLASS_CURRENT_FRESHSALEANDRATE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.SALEDATE = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" and a.CLASS = '").append(param.getClassId()).append("' ");
        return super.queryForObject(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询小类同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @return
     */
    public List<FreshSaleAndRateModel> querySubClassHisFreshSaleAndRateList(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_SUBCLASS_HIS_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.today_compare = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" and a.class = '").append(param.getClassId()).append("' ");
        sb.append(" group by a.SUBCLASS ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询小类实时销售额，毛利额，可比销售额，可比毛利额，客流，小类ID
     * @param param
     * @param start
     * @return
     */
    public List<FreshSaleAndRateModel> querySubClassCurrentFreshSaleAndRateList(FreshSpecialReportRequest param, Date start){
        StringBuilder sb = new StringBuilder(TOTAL_SUBCLASS_CURRENT_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.SALEDATE = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != param.getProvinceIds() && param.getProvinceIds().size() > 0){
            if(param.getProvinceIds().size() == 1){
                sb.append(" and a.area = ? ");
                paramList.add(param.getProvinceIds().get(0));
            }else if(param.getProvinceIds().size() > 1){
                sb.append(" and a.area in ( ");
                sb.append(param.getProvinceId());
                sb.append(" ) ");
            }
        }

        //拼接区域
        if(null != param.getAreaIds() && param.getAreaIds().size() > 0){
            if(param.getAreaIds().size() == 1){
                sb.append(" and a.region = ? ");
                paramList.add(param.getAreaIds().get(0));
            }else if(param.getAreaIds().size() > 1){
                sb.append(" and a.region in ( ");
                sb.append(param.getAreaId());
                sb.append(" ) ");
            }
        }

        //拼接门店
        if(null != param.getStoreIds() && param.getStoreIds().size() > 0){
            if(param.getStoreIds().size() == 1){
                sb.append(" and a.store = ? ");
                paramList.add(param.getStoreIds().get(0));
            }else if(param.getStoreIds().size() > 1){
                sb.append(" and a.store in ( ");
                sb.append(param.getStoreId());
                sb.append(" ) ");
            }
        }

        //拼接大类
        if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
            if(param.getDeptIds().size() == 1){
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        sb.append(" and a.class = '").append(param.getClassId()).append("' ");
        sb.append(" group by a.subclass ");
        return super.queryForList(sb.toString(), TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    private String listToStr(List<String> list){
        String result = "";
        StringBuilder sb = new StringBuilder();
        if(null != list && list.size() > 0){
            for(String str : list){
                sb.append(str).append(",");
            }
            result = sb.toString();
        }
        if(result.endsWith(",")){
            result = result.substring(0,result.lastIndexOf(","));
        }
        return result;
    }

    private String itemListToStr(List<String> list){
        String result = "";
        StringBuilder sb = new StringBuilder();
        if(null != list && list.size() > 0){
            for(String str : list){
                sb.append("'").append(str).append("',");
            }
            result = sb.toString();
        }
        if(result.endsWith(",")){
            result = result.substring(0,result.lastIndexOf(","));
        }
        return result;
    }
}
