package com.cs.mobile.api.dao.market;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.market.CategorySaleModel;
import com.cs.mobile.api.model.market.DepartmentSaleModel;
import com.cs.mobile.api.model.market.DeptSaleModel;
import com.cs.mobile.api.model.market.KlModel;
import com.cs.mobile.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class MarketForMonthDao extends AbstractDao {
    private static final RowMapper<DepartmentSaleModel> DEPARTMENT_SALE_RM = new BeanPropertyRowMapper<DepartmentSaleModel>(DepartmentSaleModel.class);
    private static final RowMapper<KlModel> KL_RM = new BeanPropertyRowMapper<KlModel>(KlModel.class);
    private static final RowMapper<CategorySaleModel> CATEGORY_SALE_RM = new BeanPropertyRowMapper<CategorySaleModel>(CategorySaleModel.class);
    private static final RowMapper<DeptSaleModel> DEPARTMENT_DEPT_RM = new BeanPropertyRowMapper<DeptSaleModel>(DeptSaleModel.class);
    //部门统计
    private static final String DEPARTMENT_SALE_PREFIX = "select  " +
            " decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept) as purchaseDept, " +
            " decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) as purchaseDeptName, " +
            " sum(d.mn_sale_value) as sale,  " +
            " sum(nvl(d.mn_sale_value,0) - nvl(d.mn_sale_cost,0) - nvl(d.mn_invadj_cost,0) - nvl(d.mn_wac,0) - nvl(d.mn_fund_amount,0)) as rate,  " +
            " sum(case when d.mall_name = 1 then d.mn_sale_value end) as compareSale,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_sale_value,0) - nvl(d.mn_sale_cost,0) - nvl(d.mn_invadj_cost,0) - nvl(d.mn_wac,0) - nvl(d.mn_fund_amount,0) end) as compareRate,  " +
            " sum(d.mn_sale_value_in) as saleIn,  " +
            " sum(nvl(d.mn_sale_value_in,0) - nvl(d.mn_sale_cost_in,0) - nvl(d.mn_invadj_cost_in,0) - nvl(d.mn_wac_in,0) - nvl(d.mn_fund_amount_in,0)) as rateIn,  " +
            " sum(case when d.mall_name = 1 then mn_sale_value_in end) as compareSaleIn,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_sale_value_in,0) - nvl(d.mn_sale_cost_in,0) - nvl(d.mn_invadj_cost_in,0) - nvl(d.mn_wac_in,0) - nvl(d.mn_fund_amount_in,0) end) as compareRateIn  " +
            " from cmx.cmx_area_purchase_dept a  " +
            " left join (select b.dept, " +
            "       b.area, " +
            "       b.mn_sale_value, " +
            "       b.mn_sale_cost, " +
            "       b.mn_invadj_cost, " +
            "       b.mn_wac, " +
            "       b.mn_fund_amount, " +
            "       b.mn_sale_value_in, " +
            "       b.mn_sale_cost_in, " +
            "       b.mn_invadj_cost_in, " +
            "       b.mn_wac_in, " +
            "       b.mn_fund_amount_in, " +
            "       b.mall_name " +
            "  from cmx.cmx_rpt_store_dept_gp_all b " +
            " where 1=1 ";

    //查询同期部门统计
    private static final String DEPARTMENT_HIS_SALE_PREFIX = "select  " +
            " decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept) as purchaseDept, " +
            " decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) as purchaseDeptName, " +
            " sum(d.mn_ly_sale_value) as sale,  " +
            " sum(nvl(d.mn_ly_sale_value,0) - nvl(d.mn_ly_sale_cost,0) - nvl(d.mn_ly_invadj_cost,0) - nvl(d.mn_ly_wac,0) - nvl(d.mn_ly_fund_amount,0)) as rate,  " +
            " sum(case when d.mall_name = 1 then d.mn_ly_sale_value end) as compareSale,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_ly_sale_value,0) - nvl(d.mn_ly_sale_cost,0) - nvl(d.mn_ly_invadj_cost,0) - nvl(d.mn_ly_wac,0) - nvl(d.mn_ly_fund_amount,0) end) as compareRate,  " +
            " sum(d.mn_ly_sale_value_in) as saleIn,  " +
            " sum(nvl(d.mn_ly_sale_value_in,0) - nvl(d.mn_ly_sale_cost_in,0) - nvl(d.mn_ly_invadj_cost_in,0) - nvl(d.mn_ly_wac_in,0) - nvl(d.mn_ly_fund_amount_in,0)) as rateIn,  " +
            " sum(case when d.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_ly_sale_value_in,0) - nvl(d.mn_ly_sale_cost_in,0) - nvl(d.mn_ly_invadj_cost_in,0) - nvl(d.mn_ly_wac_in,0) - nvl(d.mn_ly_fund_amount_in,0) end) as compareRateIn  " +
            " from cmx.cmx_area_purchase_dept a  " +
            " left join (select b.dept, " +
            "       b.area, " +
            "       b.mn_ly_sale_value, " +
            "       b.mn_ly_sale_cost, " +
            "       b.mn_ly_invadj_cost, " +
            "       b.mn_ly_wac, " +
            "       b.mn_ly_fund_amount, " +
            "       b.mn_ly_sale_value_in, " +
            "       b.mn_ly_sale_cost_in, " +
            "       b.mn_ly_invadj_cost_in, " +
            "       b.mn_ly_wac_in, " +
            "       b.mn_ly_fund_amount_in, " +
            "       b.mall_name " +
            "  from cmx.cmx_rpt_store_dept_gp_all b  " +
            " where 1=1 ";

    //查询总客流
    private static final String ALL_KL_PREFIX = "select sum(a.kl) as kl  " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.store = b.store " +
            " and a.cate_level ='STORE' ";

    private static final String CATEGORY_KL_PREFIX = "select a.new_division_name as id, " +
            " sum(a.kl) as kl  " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.store = b.store " +
            " and a.cate_level ='NEW_DIVISION_NAME' ";

    private static final String DEPT_KL_PREFIX = "select a.dept as id, " +
            " sum(a.kl) as kl  " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.store = b.store " +
            " and a.cate_level ='DEPT' ";

    private static final String DEPT_SALE_PREFIX = "select  " +
            " decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept) as purchaseDept, " +
            " decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) as purchaseDeptName, " +
            " a.dept as deptId,  " +
            " sum(d.mn_sale_value) as sale,  " +
            " sum(nvl(d.mn_sale_value,0) - nvl(d.mn_sale_cost,0) - nvl(d.mn_invadj_cost,0) - nvl(d.mn_wac,0) - nvl(d.mn_fund_amount,0)) as rate,  " +
            " sum(case when d.mall_name = 1 then d.mn_sale_value end) as compareSale,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_sale_value,0) - nvl(d.mn_sale_cost,0) - nvl(d.mn_invadj_cost,0) - nvl(d.mn_wac,0) - nvl(d.mn_fund_amount,0) end) as compareRate,  " +
            " sum(d.mn_sale_value_in) as saleIn,  " +
            " sum(nvl(d.mn_sale_value_in,0) - nvl(d.mn_sale_cost_in,0) - nvl(d.mn_invadj_cost_in,0) - nvl(d.mn_wac_in,0) - nvl(d.mn_fund_amount_in,0)) as rateIn,  " +
            " sum(case when d.mall_name = 1 then mn_sale_value_in end) as compareSaleIn,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_sale_value_in,0) - nvl(d.mn_sale_cost_in,0) - nvl(d.mn_invadj_cost_in,0) - nvl(d.mn_wac_in,0) - nvl(d.mn_fund_amount_in,0) end) as compareRateIn  " +
            " from cmx.cmx_area_purchase_dept a  " +
            " left join (select b.dept, " +
            "       b.area, " +
            "       b.mn_sale_value, " +
            "       b.mn_sale_cost, " +
            "       b.mn_invadj_cost, " +
            "       b.mn_wac, " +
            "       b.mn_fund_amount, " +
            "       b.mn_sale_value_in, " +
            "       b.mn_sale_cost_in, " +
            "       b.mn_invadj_cost_in, " +
            "       b.mn_wac_in, " +
            "       b.mn_fund_amount_in, " +
            "       b.mall_name " +
            "  from cmx.cmx_rpt_store_dept_gp_all b " +
            " where 1=1 ";

    private static final String DEPT_HIS_SALE_PREFIX = "select  " +
            " decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept) as purchaseDept, " +
            " decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) as purchaseDeptName, " +
            " a.dept as deptId,  " +
            " sum(d.mn_ly_sale_value) as sale,  " +
            " sum(nvl(d.mn_ly_sale_value,0) - nvl(d.mn_ly_sale_cost,0) - nvl(d.mn_ly_invadj_cost,0) - nvl(d.mn_ly_wac,0) - nvl(d.mn_ly_fund_amount,0)) as rate,  " +
            " sum(case when d.mall_name = 1 then d.mn_ly_sale_value end) as compareSale,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_ly_sale_value,0) - nvl(d.mn_ly_sale_cost,0) - nvl(d.mn_ly_invadj_cost,0) - nvl(d.mn_ly_wac,0) - nvl(d.mn_ly_fund_amount,0) end) as compareRate,  " +
            " sum(d.mn_ly_sale_value_in) as saleIn,  " +
            " sum(nvl(d.mn_ly_sale_value_in,0) - nvl(d.mn_ly_sale_cost_in,0) - nvl(d.mn_ly_invadj_cost_in,0) - nvl(d.mn_ly_wac_in,0) - nvl(d.mn_ly_fund_amount_in,0)) as rateIn,  " +
            " sum(case when d.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_ly_sale_value_in,0) - nvl(d.mn_ly_sale_cost_in,0) - nvl(d.mn_ly_invadj_cost_in,0) - nvl(d.mn_ly_wac_in,0) - nvl(d.mn_ly_fund_amount_in,0) end) as compareRateIn  " +
            " from cmx.cmx_area_purchase_dept a  " +
            " left join (select b.dept, " +
            "       b.area, " +
            "       b.mn_ly_sale_value, " +
            "       b.mn_ly_sale_cost, " +
            "       b.mn_ly_invadj_cost, " +
            "       b.mn_ly_wac, " +
            "       b.mn_ly_fund_amount, " +
            "       b.mn_ly_sale_value_in, " +
            "       b.mn_ly_sale_cost_in, " +
            "       b.mn_ly_invadj_cost_in, " +
            "       b.mn_ly_wac_in, " +
            "       b.mn_ly_fund_amount_in, " +
            "       b.mall_name " +
            "  from cmx.cmx_rpt_store_dept_gp_all b " +
            " where 1=1 ";

    //查询品类统计数据
    private static final String CATEGORY_SALE_PREFIX = "select  " +
            " a.new_division_name as categoryName, " +
            " sum(d.mn_sale_value) as sale,  " +
            " sum(nvl(d.mn_sale_value,0) - nvl(d.mn_sale_cost,0) - nvl(d.mn_invadj_cost,0) - nvl(d.mn_wac,0) - nvl(d.mn_fund_amount,0)) as rate,  " +
            " sum(case when d.mall_name = 1 then d.mn_sale_value end) as compareSale,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_sale_value,0) - nvl(d.mn_sale_cost,0) - nvl(d.mn_invadj_cost,0) - nvl(d.mn_wac,0) - nvl(d.mn_fund_amount,0) end) as compareRate,  " +
            " sum(d.mn_sale_value_in) as saleIn,  " +
            " sum(nvl(d.mn_sale_value_in,0) - nvl(d.mn_sale_cost_in,0) - nvl(d.mn_invadj_cost_in,0) - nvl(d.mn_wac_in,0) - nvl(d.mn_fund_amount_in,0)) as rateIn,  " +
            " sum(case when d.mall_name = 1 then mn_sale_value_in end) as compareSaleIn,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_sale_value_in,0) - nvl(d.mn_sale_cost_in,0) - nvl(d.mn_invadj_cost_in,0) - nvl(d.mn_wac_in,0) - nvl(d.mn_fund_amount_in,0) end) as compareRateIn  " +
            " from cmx.cmx_deps a  " +
            " left join (select b.dept, " +
            "       b.mn_sale_value, " +
            "       b.mn_sale_cost, " +
            "       b.mn_invadj_cost, " +
            "       b.mn_wac, " +
            "       b.mn_fund_amount, " +
            "       b.mn_sale_value_in, " +
            "       b.mn_sale_cost_in, " +
            "       b.mn_invadj_cost_in, " +
            "       b.mn_wac_in, " +
            "       b.mn_fund_amount_in, " +
            "       b.mall_name " +
            "  from cmx.cmx_rpt_store_dept_gp_all b " +
            " where 1=1 ";

    private static final String CATEGORY_HIS_SALE_PREFIX = "select  " +
            " a.new_division_name as categoryName, " +
            " sum(d.mn_ly_sale_value) as sale,  " +
            " sum(nvl(d.mn_ly_sale_value,0) - nvl(d.mn_ly_sale_cost,0) - nvl(d.mn_ly_invadj_cost,0) - nvl(d.mn_ly_wac,0) - nvl(d.mn_ly_fund_amount,0)) as rate,  " +
            " sum(case when d.mall_name = 1 then d.mn_ly_sale_value end) as compareSale,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_ly_sale_value,0) - nvl(d.mn_ly_sale_cost,0) - nvl(d.mn_ly_invadj_cost,0) - nvl(d.mn_ly_wac,0) - nvl(d.mn_ly_fund_amount,0) end) as compareRate,  " +
            " sum(d.mn_ly_sale_value_in) as saleIn,  " +
            " sum(nvl(d.mn_ly_sale_value_in,0) - nvl(d.mn_ly_sale_cost_in,0) - nvl(d.mn_ly_invadj_cost_in,0) - nvl(d.mn_ly_wac_in,0) - nvl(d.mn_ly_fund_amount_in,0)) as rateIn,  " +
            " sum(case when d.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn,  " +
            " sum(case when d.mall_name = 1 then nvl(d.mn_ly_sale_value_in,0) - nvl(d.mn_ly_sale_cost_in,0) - nvl(d.mn_ly_invadj_cost_in,0) - nvl(d.mn_ly_wac_in,0) - nvl(d.mn_ly_fund_amount_in,0) end) as compareRateIn  " +
            " from cmx.cmx_deps a  " +
            " left join (select b.dept, " +
            "       b.mn_ly_sale_value, " +
            "       b.mn_ly_sale_cost, " +
            "       b.mn_ly_invadj_cost, " +
            "       b.mn_ly_wac, " +
            "       b.mn_ly_fund_amount, " +
            "       b.mn_ly_sale_value_in, " +
            "       b.mn_ly_sale_cost_in, " +
            "       b.mn_ly_invadj_cost_in, " +
            "       b.mn_ly_wac_in, " +
            "       b.mn_ly_fund_amount_in, " +
            "       b.mall_name " +
            "  from cmx.cmx_rpt_store_dept_gp_all b " +
            " where 1=1 ";

    /**
     * 查询品类往期数据
     * @param start
     * @param end
     * @param areas
     * @param regions
     * @param stores
     * @param categorys
     * @return
     */
    public List<CategorySaleModel> queryHisMonthCategorySales(Date start,
                                                           Date end,
                                                           List<String> areas,
                                                           List<String> regions,
                                                           List<String> stores,
                                                           List<String> categorys){
        StringBuilder sb = new StringBuilder(CATEGORY_HIS_SALE_PREFIX);
        sb.append(" and b.month_last_year   ='").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                .append("-")
                .append(DateUtils.parseDateToStr("yyyyMMdd",end)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and b.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and b.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }

        sb.append(" ) d on d.dept = a.dept where 1=1 ");
        if(null != categorys && categorys.size() > 0){
            if(1 == categorys.size()){
                sb.append(" and a.new_division_name ='").append(categorys.get(0)).append("' ");
            }else{
                sb.append(" and a.new_division_name in (").append(listToStrHaveQuotationMark(categorys)).append(") ");
            }
        }
        sb.append(" group by a.new_division_name ");
        return super.queryForList(sb.toString(),CATEGORY_SALE_RM,null);
    }

    /**
     * 查询品类数据
     * @param start
     * @param end
     * @param areas
     * @param regions
     * @param stores
     * @param categorys
     * @return
     */
    public List<CategorySaleModel> queryMonthCategorySales(Date start,
                                                            Date end,
                                                            List<String> areas,
                                                            List<String> regions,
                                                            List<String> stores,
                                                            List<String> categorys){
        StringBuilder sb = new StringBuilder(CATEGORY_SALE_PREFIX);
        sb.append(" and b.month ='").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                .append("-")
                .append(DateUtils.parseDateToStr("yyyyMMdd",end)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and b.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and b.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }

        sb.append(" ) d on d.dept = a.dept where 1=1 ");
        if(null != categorys && categorys.size() > 0){
            if(1 == categorys.size()){
                sb.append(" and a.new_division_name ='").append(categorys.get(0)).append("' ");
            }else{
                sb.append(" and a.new_division_name in (").append(listToStrHaveQuotationMark(categorys)).append(") ");
            }
        }
        sb.append(" group by a.new_division_name ");
        return super.queryForList(sb.toString(),CATEGORY_SALE_RM,null);
    }

    /**
     * 查询同期大类销售统计
     * @param start(时间)
     * @param end(时间)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @param depts(大类)
     * @return
     */
    public List<DeptSaleModel> queryHisMonthDeptSales(Date start,
                                                                  Date end,
                                                                  List<String> areas,
                                                                  List<String> regions,
                                                                  List<String> stores,
                                                                  List<String> depts){
        StringBuilder sb = new StringBuilder(DEPT_HIS_SALE_PREFIX);
        sb.append(" and b.month_last_year ='").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                .append("-")
                .append(DateUtils.parseDateToStr("yyyyMMdd",end)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and b.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and b.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept =").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" ) d on d.dept = a.dept and d.area = a.area where 1=1 ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and a.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and a.area in (").append(listToStrHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept =").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" group by decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept),decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc),a.dept ");
        return super.queryForList(sb.toString(),DEPARTMENT_DEPT_RM,null);
    }


    /**
     * 查询大类销售数据
     * @param start
     * @param end
     * @param areas
     * @param regions
     * @param stores
     * @param depts
     * @return
     */
    public List<DeptSaleModel> queryMonthDeptSales(Date start,
                                                               Date end,
                                                               List<String> areas,
                                                               List<String> regions,
                                                               List<String> stores,
                                                               List<String> depts){
        StringBuilder sb = new StringBuilder(DEPT_SALE_PREFIX);
        sb.append(" and b.month ='").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                .append("-")
                .append(DateUtils.parseDateToStr("yyyyMMdd",end)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and b.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and b.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept =").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" ) d on d.dept = a.dept and d.area = a.area where 1=1 ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and a.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and a.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept =").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" group by decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept),decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc),a.dept ");
        return super.queryForList(sb.toString(),DEPARTMENT_DEPT_RM,null);
    }

    /**
     * 查询大类客流
     * @param start
     * @param end
     * @param areas
     * @param regions
     * @param stores
     * @param depts
     * @return
     */
    public List<KlModel> queryDeptKl(Date start,
                                     Date end,
                                     List<String> areas,
                                     List<String> regions,
                                     List<String> stores,
                                     List<String> depts){
        StringBuilder sb = new StringBuilder(DEPT_KL_PREFIX);
        if(null != end){
            sb.append(" and a.business_date <=date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
        }
        if(null != start){
            sb.append(" and a.business_date >=date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and b.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and b.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.region=").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store=").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept =").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" group by a.dept ");
        return super.queryForList(sb.toString(), KL_RM, null);
    }

    /**
     * 查询品类客流
     * @param start
     * @param end
     * @param areas
     * @param regions
     * @param stores
     * @param categorys
     * @return
     */
    public List<KlModel> queryCategoryKl(Date start,
                                         Date end,
                                         List<String> areas,
                                         List<String> regions,
                                         List<String> stores,
                                         List<String> categorys){
        StringBuilder sb = new StringBuilder(CATEGORY_KL_PREFIX);
        if(null != end){
            sb.append(" and a.business_date <=date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
        }
        if(null != start){
            sb.append(" and a.business_date >=date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and b.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and b.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.region=").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store=").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        if(null != categorys && categorys.size() > 0){
            if(1 == categorys.size()){
                sb.append(" and a.new_division_name ='").append(categorys.get(0)).append("' ");
            }else{
                sb.append(" and a.new_division_name in (").append(listToStrHaveQuotationMark(categorys)).append(") ");
            }
        }
        sb.append(" group by a.new_division_name ");
        return super.queryForList(sb.toString(), KL_RM, null);
    }

    /**
     * 查询总客流
     * @param start
     * @param end
     * @return
     */
    public KlModel queryAllKl(Date start,
                              Date end,
                              List<String> areas,
                              List<String> regions,
                              List<String> stores){
        StringBuilder sb = new StringBuilder(ALL_KL_PREFIX);
        if(null != end){
            sb.append(" and a.business_date <=date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",end)).append("' ");
        }
        if(null != start){
            sb.append(" and a.business_date >=date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and b.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and b.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.region=").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store=").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        return super.queryForObject(sb.toString(), KL_RM, null);
    }

    /**
     * 查询同期部门销售统计
     * @param start(时间)
     * @param end(时间)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @param depts(大类)
     * @param purchaseDepts(部门)
     * @return
     */
    public List<DepartmentSaleModel> queryHisMonthDepartmentSales(Date start,
                                                                  Date end,
                                                             List<String> areas,
                                                             List<String> regions,
                                                             List<String> stores,
                                                             List<String> depts,
                                                             List<String> purchaseDepts){
        StringBuilder sb = new StringBuilder(DEPARTMENT_HIS_SALE_PREFIX);
        sb.append(" and b.month_last_year ='").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                .append("-")
                .append(DateUtils.parseDateToStr("yyyyMMdd",end)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and b.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and b.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept =").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" ) d on d.dept = a.dept and d.area = a.area where 1=1 ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and a.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and a.area in (").append(listToStrHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != purchaseDepts && purchaseDepts.size() > 0){
            if(1 == purchaseDepts.size()){
                sb.append(" and a.purchase_dept='").append(purchaseDepts.get(0)).append("' ");
            }else{
                sb.append(" and a.purchase_dept in (").append(listToStrHaveQuotationMark(purchaseDepts)).append(") ");
            }
        }
        sb.append(" group by decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept),decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) ");
        return super.queryForList(sb.toString(),DEPARTMENT_SALE_RM,null);
    }

    /**
     * 查询部门统计数据
     * @param start(时间)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @param depts(大类)
     * @param purchaseDepts(部门)
     * @return
     */
    public List<DepartmentSaleModel> queryMonthDepartmentSales(Date start,
                                                                 Date end,
                                                                 List<String> areas,
                                                                 List<String> regions,
                                                                 List<String> stores,
                                                                 List<String> depts,
                                                                 List<String> purchaseDepts){
        StringBuilder sb = new StringBuilder(DEPARTMENT_SALE_PREFIX);
        sb.append(" and b.month ='").append(DateUtils.parseDateToStr("yyyyMMdd",start))
                .append("-")
                .append(DateUtils.parseDateToStr("yyyyMMdd",end)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and b.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and b.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept =").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" ) d on d.dept = a.dept and d.area = a.area where 1=1 ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and a.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and a.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != purchaseDepts && purchaseDepts.size() > 0){
            if(1 == purchaseDepts.size()){
                sb.append(" and a.purchase_dept='").append(purchaseDepts.get(0)).append("' ");
            }else{
                sb.append(" and a.purchase_dept in (").append(listToStrHaveQuotationMark(purchaseDepts)).append(") ");
            }
        }
        sb.append(" group by decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept),decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) ");
        return super.queryForList(sb.toString(),DEPARTMENT_SALE_RM,null);
    }
}
