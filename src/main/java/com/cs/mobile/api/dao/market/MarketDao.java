package com.cs.mobile.api.dao.market;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.market.*;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Slf4j
@Repository
public class MarketDao extends AbstractDao {
    private static final RowMapper<DepartmentSaleModel> DEPARTMENT_SALE_RM = new BeanPropertyRowMapper<DepartmentSaleModel>(DepartmentSaleModel.class);
    private static final RowMapper<DepartmentGoalModel> DEPARTMENT_GOAL_RM = new BeanPropertyRowMapper<DepartmentGoalModel>(DepartmentGoalModel.class);
    private static final RowMapper<KlModel> KL_RM = new BeanPropertyRowMapper<KlModel>(KlModel.class);
    private static final RowMapper<CategorySaleModel> CATEGORY_SALE_RM = new BeanPropertyRowMapper<CategorySaleModel>(CategorySaleModel.class);
    private static final RowMapper<DepartmentTypeModel> DEPARTMENT_TYPE_RM = new BeanPropertyRowMapper<DepartmentTypeModel>(DepartmentTypeModel.class);
    private static final RowMapper<DeptSaleModel> DEPARTMENT_DEPT_RM = new BeanPropertyRowMapper<DeptSaleModel>(DeptSaleModel.class);
    private static final RowMapper<DeptGoalModel> DEPT_GOAL_RM = new BeanPropertyRowMapper<DeptGoalModel>(DeptGoalModel.class);
    //部门统计
    private static final String DEPARTMENT_SALE_PREFIX = "select /*+PARALLEL(B,8)*/ " +
            " decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept) as purchaseDept, " +
            " decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) as purchaseDeptName, " +
            " sum(b.amt_ecl) as sale, " +
            " sum(b.amt) as saleIn, " +
            " sum(case when b.MALL_NAME = 1 then b.amt_ecl end) as compareSale, " +
            " sum(case when b.MALL_NAME = 1 then b.amt end) as compareSaleIn, " +
            " sum(b.gp_ecl) as rate, " +
            " sum(b.gp) as rateIn, " +
            " sum(case when b.MALL_NAME = 1 then b.gp_ecl end) as compareRate, " +
            " sum(case when b.MALL_NAME = 1 then b.gp end) as compareRateIn, " +
            " COUNT(DISTINCT b.STORE || b.SALEDATE || b.BILLNO || b.POSID) as kl " +
            " from OPT_area_purchase_dept a  " +
            " left join csmb_store_saledetail b " +
            " on b.area = a.area " +
            " and b.dept = a.DEPT ";

    //查询同期部门统计
    private static final String DEPARTMENT_HIS_SALE_PREFIX = "select decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept) as purchaseDept, " +
            "       decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) as purchaseDeptName, " +
            "       sum(k.total_real_amt_ecl) as sale, " +
            "       (sum(k.total_real_amt_ecl) - sum(k.total_cost_ecl)) as rate, " +
            "       sum(k.total_real_amt) as saleIn, " +
            "       (sum(k.total_real_amt) - sum(k.total_cost)) as rateIn, " +
            "       sum(case when k.mall_name = 1 then k.total_real_amt_ecl end) as compareSale, " +
            "       sum(case when k.mall_name = 1 then nvl(k.total_real_amt_ecl,0) - nvl(k.total_cost_ecl,0)end) as compareRate, " +
            "       sum(case when k.mall_name = 1 then k.total_real_amt end) as compareSaleIn, " +
            "       sum(case when k.mall_name = 1 then nvl(k.total_real_amt,0) - nvl(k.total_cost,0)end) as compareRateIn " +
            " from zypp.OPT_area_purchase_dept a  " +
            " left join (select /*+PARALLEL(B,8)*/ c.area, " +
            "       b.dept, " +
            "       b.total_real_amt_ecl, " +
            "       b.total_cost_ecl, " +
            "       b.total_real_amt, " +
            "       b.total_cost, " +
            "       c.mall_name " +
            "  from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM b, zypp.inf_store c " +
            "  where b.store = c.store ";

    //查询部门目标值
    private static final String DEPARTMENT_GOAL_PREFIX = " select  " +
            " decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept) as purchaseDept, " +
            " decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) as purchaseDeptName, " +
            " sum(case " +
            "       when k.account_name = '销售收入' then " +
            "        k.amount * 10000 " +
            "     end) as sale, " +
            " sum(case " +
            "       when k.account_name = '销售毛利额' then " +
            "        k.amount * 10000 " +
            "     end) as rate " +
            " from cmx.cmx_area_purchase_dept a  " +
            " left join (select /*+PARALLEL(B,8)*/ b.*, c.area from cmx_hbl_cate_data_manual_imp b,rms.v_bi_inf_store c  " +
            " where b.store = c.store ";
    //查询实时总客流
    private static final String KL_PREFIX = "select COUNT(DISTINCT a.STORE || a.SALEDATE || a.BILLNO || a.POSID) as kl " +
            " from csmb_store_saledetail a " +
            " where 1=1 ";

    //查询品类统计数据
    private static final String CATEGORY_SALE_PREFIX = "select " +
            " a.new_division_name as categoryName, " +
            " sum(c.amt_ecl) as sale, " +
            " sum(c.amt) as saleIn, " +
            " sum(case when c.MALL_NAME = 1 then c.amt_ecl end) as compareSale, " +
            " sum(case when c.MALL_NAME = 1 then c.amt end) as compareSaleIn, " +
            " sum(c.gp_ecl) as rate, " +
            " sum(c.gp) as rateIn, " +
            " sum(case when c.MALL_NAME = 1 then c.gp_ecl end) as compareRate, " +
            " sum(case when c.MALL_NAME = 1 then c.gp end) as compareRateIn, " +
            " COUNT(DISTINCT c.STORE || c.SALEDATE || c.BILLNO || c.POSID) as kl " +
            " from csmb_code_dept a  " +
            " left join csmb_store_saledetail c  " +
            " on c.dept = a.dept ";
    //查询总合计数据
    private static final String ALL_SALE_PREFIX = "select  " +
            " sum(c.amt_ecl) as sale, " +
            " sum(c.amt) as saleIn, " +
            " sum(case when c.MALL_NAME = 1 then c.amt_ecl end) as compareSale, " +
            " sum(case when c.MALL_NAME = 1 then c.amt end) as compareSaleIn, " +
            " sum(c.gp_ecl) as rate, " +
            " sum(c.gp) as rateIn, " +
            " sum(case when c.MALL_NAME = 1 then c.gp_ecl end) as compareRate, " +
            " sum(case when c.MALL_NAME = 1 then c.gp end) as compareRateIn, " +
            " COUNT(DISTINCT c.STORE || c.SALEDATE || c.BILLNO || c.POSID) as kl " +
            " from csmb_store_saledetail c  " +
            " where 1=1 ";
    //查询部门类型(部门 + 品类 + 大类)
    private static final String DEPARTMENT_TYPE_PREFIX = "select a.dept as deptId, " +
            "  a.dept_name as deptName,  " +
            "  a.new_division_name as categoryName,  " +
            "  decode(b.purchase_dept,null,'99',b.purchase_dept) as purchaseDept,  " +
            "  decode(b.purchase_dept_desc,'无','99',b.purchase_dept_desc) as purchaseDeptName " +
            "  from csmb_code_dept a, OPT_area_purchase_dept b " +
            " where a.dept = b.dept " +
            " and b.area = 101 " ;
    //查询大类汇总数据
    private static final String DEPARTMENT_DEPT_PREFIX = "select  " +
            " b.new_division_name as categoryName, " +
            " decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept) as purchaseDept, " +
            " decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) as purchaseDeptName, " +
            " max(b.dept_name) as deptName, " +
            " a.dept as deptId, " +
            " sum(c.amt_ecl) as sale, " +
            " sum(c.amt) as saleIn, " +
            " sum(case when c.MALL_NAME = 1 then c.amt_ecl end) as compareSale, " +
            " sum(case when c.MALL_NAME = 1 then c.amt end) as compareSaleIn, " +
            " sum(c.gp_ecl) as rate, " +
            " sum(c.gp) as rateIn, " +
            " sum(case when c.MALL_NAME = 1 then c.gp_ecl end) as compareRate, " +
            " sum(case when c.MALL_NAME = 1 then c.gp end) as compareRateIn, " +
            " COUNT(DISTINCT c.STORE || c.SALEDATE || c.BILLNO || c.POSID) as kl " +
            " from OPT_area_purchase_dept a  " +
            " left join csmb_store_saledetail c  " +
            " on c.dept = a.dept  " +
            " and c.area = a.area ";

    private static final String DEPARTMENT_HIS_DEPT_PREFIX = "select  " +
            "       d.new_division_name as categoryName, " +
            "       decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept) as purchaseDept, " +
            "       decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) as purchaseDeptName, " +
            "       max(d.dept_name) as deptName, " +
            "       a.dept as deptId, " +
            "       sum(k.total_real_amt_ecl) as sale, " +
            "       (sum(k.total_real_amt_ecl) - sum(k.total_cost_ecl)) as rate, " +
            "       sum(k.total_real_amt) as saleIn, " +
            "       (sum(k.total_real_amt) - sum(k.total_cost)) as rateIn, " +
            "       sum(case when k.mall_name = 1 then k.total_real_amt_ecl end) as compareSale, " +
            "       sum(case when k.mall_name = 1 then nvl(k.total_real_amt_ecl,0) - nvl(k.total_cost_ecl,0)end) as compareRate, " +
            "       sum(case when k.mall_name = 1 then k.total_real_amt end) as compareSaleIn, " +
            "       sum(case when k.mall_name = 1 then nvl(k.total_real_amt,0) - nvl(k.total_cost,0)end) as compareRateIn " +
            " from zypp.OPT_area_purchase_dept a  " +
            " left join (select /*+PARALLEL(B,8)*/ c.area, " +
            "       b.dept, " +
            "       b.total_real_amt_ecl, " +
            "       b.total_cost_ecl, " +
            "       b.total_real_amt, " +
            "       b.total_cost, " +
            "       c.mall_name " +
            "  from zypp.CAL_STORE_HOUR_DEPT_SALE_SUM b, zypp.inf_store c " +
            " where b.store = c.store ";

    private static final String DEPT_GOAL_PREFIX = "select /*+PARALLEL(B,8)*/  " +
            " b.dept as deptId, " +
            " sum(case " +
            "       when b.account_name = '销售收入' then " +
            "        b.amount * 10000 " +
            "     end) as sale, " +
            " sum(case " +
            "       when b.account_name = '销售毛利额' then " +
            "        b.amount * 10000 " +
            "     end) as rate " +
            " from cmx_hbl_cate_data_manual_imp b,rms.v_bi_inf_store c  " +
            " where b.store = c.store ";

    /**
     * 查询大类目标值
     * @param start
     * @param areas
     * @param regions
     * @param stores
     * @param depts
     * @return
     */
    public List<DeptGoalModel> queryDeptGoal(Date start,
                                             List<String> areas,
                                             List<String> regions,
                                             List<String> stores,
                                             List<String> depts){
        StringBuilder sb = new StringBuilder(DEPT_GOAL_PREFIX);
        sb.append(" and b.year='").append(DateUtils.parseDateToStr("yyyy",start)).append("' ");
        sb.append(" and b.month='").append(DateUtils.parseDateToStr("MM", start)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and c.AREA =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and c.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and c.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and c.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and c.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and c.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" group by b.dept ");
        return super.queryForList(sb.toString(), DEPT_GOAL_RM,null);
    }

    /**
     * 查询历史大类销售数据
     * @param start(时间)
     * @param hour(小时)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @param depts(大类)
     * @return
     */
    public List<DeptSaleModel> queryHisDeptSale(Date start,
                                                String hour,
                                                List<String> areas,
                                                List<String> regions,
                                                List<String> stores,
                                                List<String> depts){
        StringBuilder sb = new StringBuilder(DEPARTMENT_HIS_DEPT_PREFIX);
        sb.append(" and b.business_date = date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        sb.append(" and b.hour <= '").append(hour).append("' ");

        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and c.area=").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and c.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }

        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and c.region=").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and c.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }

        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and c.store=").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and c.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }

        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }

        sb.append("  ) k on a.area = k.area and a.dept = k.dept ,csmb.csmb_code_dept d where a.dept = d.dept and a.dept not in (74,79,41,9101,9103) ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and a.area=").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and a.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" group by d.new_division_name,decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept),decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc),a.dept ");
        return super.queryForList(sb.toString(),DEPARTMENT_DEPT_RM,null);
    }

    /**
     * 查询实时大类销售汇总
     * @param start(时间)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @param depts(大类)
     * @return
     */
    public List<DeptSaleModel> queryCurrentDeptSale(Date start,
                                             List<String> areas,
                                             List<String> regions,
                                             List<String> stores,
                                             List<String> depts){
        StringBuilder sb = new StringBuilder(DEPARTMENT_DEPT_PREFIX);
        sb.append(" and c.saledate='").append(DateUtils.parseDateToStr("yyyyMMdd",start)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and c.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and c.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and c.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and c.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and c.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and c.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and c.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and c.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" ,csmb_code_dept b where a.dept = b.dept and a.dept not in (74,79,41,9101,9103) ");
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
        sb.append(" group by b.new_division_name,decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept),decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc),a.dept ");
        return super.queryForList(sb.toString(),DEPARTMENT_DEPT_RM,null);
    }

    /**
     * 查询市场类别（部门 + 品类 + 大类）信息
     * @return
     */
    public List<DepartmentTypeModel> queryDepartmentType(){
        StringBuilder sb = new StringBuilder(DEPARTMENT_TYPE_PREFIX);
        return super.queryForList(sb.toString(),DEPARTMENT_TYPE_RM,null);
    }

    /**
     * 查询实时总销售
     * @param start(时间)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @return
     */
    public DepartmentSaleModel queryCurrentAllSale(Date start,
                                                   List<String> areas,
                                                   List<String> regions,
                                                   List<String> stores){
        StringBuilder sb = new StringBuilder(ALL_SALE_PREFIX);
        sb.append(" and c.saledate='").append(DateUtils.parseDateToStr("yyyyMMdd",start)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and c.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and c.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and c.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and c.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and c.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and c.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        return super.queryForObject(sb.toString(),DEPARTMENT_SALE_RM,null);
    }

    /**
     * 查询品类汇总数据
     * @param start(时间)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @return
     */
    public List<CategorySaleModel> queryCurrentCategorySale(Date start,
                                                     List<String> areas,
                                                     List<String> regions,
                                                     List<String> stores,
                                                     List<String> categorys){
        StringBuilder sb = new StringBuilder(CATEGORY_SALE_PREFIX);
        sb.append(" and c.saledate='").append(DateUtils.parseDateToStr("yyyyMMdd",start)).append("' ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and c.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and c.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and c.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and c.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and c.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and c.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        sb.append(" where 1=1 ");
        if(null != categorys && categorys.size() > 0){
            if(1 == categorys.size()){
                sb.append(" and a.new_division_name='").append(categorys.get(0)).append("' ");
            }else{
                sb.append(" and a.new_division_name in (").append(listToStrHaveQuotationMark(categorys)).append(") ");
            }
        }
        sb.append(" group by a.new_division_name order by a.new_division_name desc ");
        return super.queryForList(sb.toString(),CATEGORY_SALE_RM,null);
    }

    /**
     * 查询实时总客流
     * @param start(时间)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @return
     */
    public KlModel queryCurrentKl(Date start,
                                  List<String> areas,
                                  List<String> regions,
                                  List<String> stores){
        StringBuilder sb = new StringBuilder(KL_PREFIX);
        sb.append(" and a.saledate = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("' ");
        //拼接省份
        if(null != areas && areas.size() > 0){
            if(areas.size() == 1){
                sb.append(" and a.area =").append(areas.get(0)).append(" ");
            }else if(areas.size() > 1){
                sb.append(" and a.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }

        //拼接区域
        if(null != regions && regions.size() > 0){
            if(regions.size() == 1){
                sb.append(" and a.region =").append(regions.get(0)).append(" ");
            }else if(regions.size() > 1){
                sb.append(" and a.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        //拼接门店
        if(null != stores && stores.size() > 0){
            if(stores.size() == 1){
                sb.append(" and a.store =").append(stores.get(0)).append(" ");
            }else if(stores.size() > 1){
                sb.append(" and a.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }

        return super.queryForObject(sb.toString(), KL_RM, null);
    }

    /**
     * 查询同期部门销售统计
     * @param start(时间)
     * @param hour(小时)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @param depts(大类)
     * @param purchaseDepts(部门)
     * @return
     */
    public List<DepartmentSaleModel> queryHisDepartmentSales(Date start,
                                                             String hour,
                                                             List<String> areas,
                                                             List<String> regions,
                                                             List<String> stores,
                                                             List<String> depts,
                                                             List<String> purchaseDepts){
        StringBuilder sb = new StringBuilder(DEPARTMENT_HIS_SALE_PREFIX);
        if(null != start){
            sb.append(" and b.business_date =date'").append(DateUtils.parseDateToStr("yyyy-MM-dd",start)).append("' ");
        }
        if(StringUtils.isNotEmpty(hour)){
            sb.append(" and b.hour <='").append(hour).append("' ");
        }

        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and c.area=").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and c.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }

        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and c.region=").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and c.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }

        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and c.store=").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and c.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }

        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(") k on a.area = k.area and a.dept = k.dept where 1=1 ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and a.area=").append(areas.get(0)).append(" ");
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

    /**
     * 查询部门目标值
     * @param start(时间)
     * @param areas(省)
     * @param regions(区域)
     * @param stores(门店)
     * @param depts(大类)
     * @param purchaseDepts(部门)
     * @return
     */
    public List<DepartmentGoalModel> queryDepartmentGoal(Date start,
                                                         List<String> areas,
                                                         List<String> regions,
                                                         List<String> stores,
                                                         List<String> depts,
                                                         List<String> purchaseDepts){
        StringBuilder sb = new StringBuilder(DEPARTMENT_GOAL_PREFIX);
        //拼接省份
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and c.area=").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and c.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }

        if(null != start){
            sb.append(" and b.year='").append(DateUtils.parseDateToStr("yyyy",start)).append("' ");
            sb.append(" and b.month='").append(DateUtils.parseDateToStr("MM",start)).append("' ");
        }

        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and c.region=").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and c.region in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }

        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and c.store=").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and c.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }

        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }

        sb.append(") k on k.dept=a.dept and a.area=k.area where 1=1 ");
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and a.area=").append(areas.get(0)).append(" ");
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
        return super.queryForList(sb.toString(),DEPARTMENT_GOAL_RM,null);
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
    public List<DepartmentSaleModel> queryCurrentDepartmentSales(Date start,
                                                                 List<String> areas,
                                                                 List<String> regions,
                                                                 List<String> stores,
                                                                 List<String> depts,
                                                                 List<String> purchaseDepts){
        StringBuilder sb = new StringBuilder(DEPARTMENT_SALE_PREFIX);
        if(null != start){
            sb.append(" and b.saledate='").append(DateUtils.parseDateToStr("yyyyMMdd",start)).append("' ");
        }
        //拼接区域
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.REGION=").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.REGION in (").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        //拼接门店
        if(null != stores && stores.size() > 0){
            if(1 == stores.size()){
                sb.append(" and b.store =").append(stores.get(0)).append(" ");
            }else{
                sb.append(" and b.store in (").append(listToStrNotHaveQuotationMark(stores)).append(") ");
            }
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and b.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and b.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" where 1=1 ");
        //拼接省份
        if(null != areas && areas.size() > 0){
            if(1 == areas.size()){
                sb.append(" and a.area =").append(areas.get(0)).append(" ");
            }else{
                sb.append(" and a.area in (").append(listToStrNotHaveQuotationMark(areas)).append(") ");
            }
        }

        if(null != purchaseDepts && purchaseDepts.size() > 0){
            if(1 == purchaseDepts.size()){
                sb.append(" and a.purchase_dept ='").append(purchaseDepts.get(0)).append("' ");
            }else{
                sb.append(" and a.purchase_dept in (").append(listToStrHaveQuotationMark(purchaseDepts)).append(") ");
            }
        }
        sb.append(" group by decode(a.purchase_dept,null,'99','JD001','JD002',a.purchase_dept),decode(a.purchase_dept_desc,'无','99','家电3C部','家电部','家电商品部','家电部','餐饮','餐饮部',a.purchase_dept_desc) ");
        return super.queryForList(sb.toString(),DEPARTMENT_SALE_RM,null);
    }
}
