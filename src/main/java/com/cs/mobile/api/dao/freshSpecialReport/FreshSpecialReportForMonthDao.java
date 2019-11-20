package com.cs.mobile.api.dao.freshSpecialReport;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.freshspecialreport.*;
import com.cs.mobile.api.model.freshspecialreport.request.FreshSpecialReportRequest;
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

@Slf4j
@Repository
public class FreshSpecialReportForMonthDao extends AbstractDao {
    private static final RowMapper<FreshAllKlModel> FRESH_ALLKL_RM = new BeanPropertyRowMapper<FreshAllKlModel>(FreshAllKlModel.class);
    private static final RowMapper<FreshSaleAndRateModel> TOTAL_FRESHSALEANDRATE_RM = new BeanPropertyRowMapper<FreshSaleAndRateModel>(FreshSaleAndRateModel.class);

    private static final String TOTAL_DEPT_MONTH_FRESHSALEANDRATE_PREFIX = "select  max(a.dept) as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_dept_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_DEPT_SAME_MONTH_FRESHSALEANDRATE_PREFIX = "select  max(a.dept) as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_dept_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_FRESH_MONTH_ALLKL_PREFIX = "select sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'STORE' " +
            " and a.store = b.store ";

    private static final String TOTAL_FRESH_DEPT_MONTH_ALLKL_PREFIX = "select sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'DEPT' " +
            " and a.store = b.store ";

    private static final String TOTAL_FRESH_CLASS_MONTH_ALLKL_LIST_PREFIX = "select a.class as id, " +
            " sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'CLASS' " +
            " and a.store = b.store ";

    private static final String TOTAL_CLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.class as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_class_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_CLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.class as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            " from cmx.Cmx_rpt_store_class_gp_all a  " +
            " where 1=1 ";

    private static final String TOTAL_CLASS_SAME_MONTH_FRESHSALEANDRATE_PREFIX = "select max(a.class) as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_class_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_CLASS_MONTH_FRESHSALEANDRATE_PREFIX = "select max(a.class) as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_class_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_FRESH_CLASS_MONTH_ALLKL_PREFIX = "select /*+PARALLEL(A,4)*/ sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'CLASS' " +
            " and a.store = b.store ";

    private static final String TOTAL_FRESH_SUBCLASS_MONTH_ALLKL_LIST_PREFIX = "select /*+PARALLEL(A,4)*/ a.subclass as id, " +
            " sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'SUBCLASS' " +
            " and a.store = b.store ";

    private static final String TOTAL_SUBCLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.subclass as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_subclass_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_SUBCLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.subclass as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_subclass_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_AREA_CLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.region as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            " from cmx.Cmx_rpt_store_class_gp_all a  " +
            " where 1=1 ";

    private static final String TOTAL_AREA_DEPT_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.region as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            " from cmx.Cmx_rpt_store_dept_gp_all a  " +
            " where 1=1 ";

    private static final String TOTAL_AREA_SUBCLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.region as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_subclass_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_AREA_CLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.region as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_class_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_AREA_DEPT_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.region as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_dept_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_AREA_SUBCLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.region as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_subclass_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_AREA_FRESH_CLASS_MONTH_ALLKL_PREFIX = "select b.region as id, " +
            " sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'CLASS' " +
            " and a.store = b.store ";

    private static final String TOTAL_AREA_FRESH_DEPT_MONTH_ALLKL_PREFIX = "select b.region as id, " +
            " sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'DEPT' " +
            " and a.store = b.store ";

    private static final String TOTAL_AREA_FRESH_SUBCLASS_MONTH_ALLKL_LIST_PREFIX = "select /*+PARALLEL(A,4)*/ b.region as id, " +
            " sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'SUBCLASS' " +
            " and a.store = b.store ";

    private static final String TOTAL_STORE_CLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.store as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            " from cmx.Cmx_rpt_store_class_gp_all a  " +
            " where 1=1 ";

    private static final String TOTAL_STORE_DEPT_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.store as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            " from cmx.Cmx_rpt_store_dept_gp_all a  " +
            " where 1=1 ";

    private static final String TOTAL_STORE_SUBCLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.store as id," +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value,0) - nvl(mn_sale_cost,0) end) as compareRate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_subclass_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_STORE_CLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.store as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_class_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_STORE_DEPT_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.store as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_dept_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_STORE_SUBCLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX = "select a.store as id, " +
            "   sum(mn_ly_sale_value) as sale, " +
            "   sum(nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0)) as rate, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value end) as compareSale, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value,0) - nvl(mn_ly_sale_cost,0) end) as compareRate, " +
            "   sum(mn_ly_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0)) as rateIn, " +
            "   sum(case when a.mall_name = 1 then mn_ly_sale_value_in end) as compareSaleIn, " +
            "   sum(case when a.mall_name = 1 then nvl(mn_ly_sale_value_in,0) - nvl(mn_ly_sale_cost_in,0) end) as compareRateIn " +
            "   from cmx.Cmx_rpt_store_subclass_gp_all a " +
            "  where 1 = 1 ";

    private static final String TOTAL_STORE_FRESH_CLASS_MONTH_ALLKL_PREFIX = "select * from (select a.store as id, " +
            " sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'CLASS' " +
            " and a.store = b.store ";

    private static final String TOTAL_STORE_FRESH_DEPT_MONTH_ALLKL_PREFIX = "select * from (select a.store as id, " +
            " sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'DEPT' " +
            " and a.store = b.store ";

    private static final String TOTAL_STORE_FRESH_SUBCLASS_MONTH_ALLKL_LIST_PREFIX = "select * from (select /*+PARALLEL(A,4)*/ a.store as id, " +
            " sum(kl) as kl " +
            " from zypp.cal_store_cate_kl a,zypp.inf_store b " +
            " where a.cate_level = 'SUBCLASS' " +
            " and a.store = b.store ";

    private static final String TOTAL_ITEM_MONTH_FRESHSALEANDRATE_PREFIX = "select /*+PARALLEL(A,8)*/ a.item as id, " +
            "   sum(mn_sale_value) as sale, " +
            "   sum(nvl(mn_sale_value,0) - nvl(mn_sale_cost,0)) as rate, " +
            "   sum(mn_sale_value_in) as saleIn, " +
            "   sum(nvl(mn_sale_value_in,0) - nvl(mn_sale_cost_in,0)) as rateIn " +
            " from cmx.Cmx_rpt_store_item_gp_all a,rms.MV_LOC_MGR b,rms.ITEM_MASTER c " +
            " where a.store = b.store " +
            " and a.item = c.item ";

    private static final String TOTAL_ITEM_FRESH_MONTH_ALLKL_LIST_PREFIX = "select * from (select /*+PARALLEL(A,8)*/  " +
            " a.item as id, " +
            " c.item_desc as name, " +
            " sum(mn_kl) as kl " +
            " from ZYPP.CAL_store_item_kl_all a,zypp.inf_store b,zypp.inf_item c " +
            " where a.store = b.store " +
            " and a.item=c.item ";

    private static final String TOTAL_HAVE_ITEM_KL_PREFIX = "SELECT COUNT(*) FROM ALL_TABLES WHERE OWNER = UPPER('ZYPP') AND TABLE_NAME = UPPER('CAL_store_item_kl_all') ";

    private static final String TOTAL_HAVE_ITEM_SALE_PREFIX = "SELECT COUNT(*) FROM ALL_TABLES WHERE OWNER = UPPER('cmx') AND TABLE_NAME = UPPER('Cmx_rpt_store_item_gp_all') ";

    /**
     * 查询门店大类月初至昨日同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param storeList
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreDeptSameMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end,List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_DEPT_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询门店大类月初至昨日销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param storeList
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreDeptMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end,List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_DEPT_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询门店大类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshAllKlModel> queryStoreDeptFreshAllKl(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_FRESH_DEPT_MONTH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        int sort = Integer.valueOf(param.getIsLast()).intValue();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(1 == sort){
            sb.append(" group by a.store order by kl asc) ");
        }else {
            sb.append(" group by a.store order by kl desc) ");
        }
        sb.append(" where rownum <10 ");
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }
    /**
     * 根据大类查询区域往期数据
     * @param param
     * @param start
     * @param end
     * @param regionList
     * @return
     */
    public List<FreshSaleAndRateModel> queryAreaDeptSameMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end, List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_AREA_DEPT_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }
    /**
     * 根据大类查询区域数据
     * @param param
     * @param start
     * @param end
     * @param regionList
     * @return
     */
    public List<FreshSaleAndRateModel> queryAreaDeptMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end, List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_AREA_DEPT_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询区域大类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshAllKlModel> queryAreaDeptFreshAllKl(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_AREA_FRESH_DEPT_MONTH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        int sort = Integer.valueOf(param.getIsLast()).intValue();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(1 == sort){
            sb.append(" group by b.region order by kl asc ");
        }else {
            sb.append(" group by b.region order by kl desc ");
        }
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }
    /**
     * 判断商品月初至昨日客流汇总表是否存在
     * @return
     */
    public int queryIsHaveItemKlTable(){
        return jdbcTemplate.queryForObject(TOTAL_HAVE_ITEM_KL_PREFIX,null,Integer.class);
    }

    /**
     * 查询商品月初至昨日销售汇总表是否存在
     * @return
     */
    public int queryIsHaveItemSaleTable(){
        return jdbcTemplate.queryForObject(TOTAL_HAVE_ITEM_SALE_PREFIX,null,Integer.class);
    }

    /**
     * 查询商品月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshAllKlModel> queryItemFreshAllKlList(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_ITEM_FRESH_MONTH_ALLKL_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        int sort = Integer.valueOf(param.getIsLast()).intValue();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and c.class = ? ");
            paramList.add(param.getClassId());
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and c.subclass = ? ");
            paramList.add(param.getSubClassId());
        }
        if(1 == sort){
            sb.append(" group by a.item,c.item_desc order by kl asc) ");
        }else {
            sb.append(" group by a.item,c.item_desc order by kl desc) ");
        }
        sb.append(" where rownum <10 ");
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询商品月初至昨日销售额，毛利额
     * @param param
     * @param start
     * @param end
     * @param itemList
     * @return
     */
    public List<FreshSaleAndRateModel> queryItemMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end,List<String> itemList){
        StringBuilder sb = new StringBuilder(TOTAL_ITEM_MONTH_FRESHSALEANDRATE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and c.class = ? ");
            paramList.add(param.getClassId());
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and c.subclass = ? ");
            paramList.add(param.getSubClassId());
        }
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
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询门店小类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshAllKlModel> queryStoreSubClassFreshAllKlList(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_FRESH_SUBCLASS_MONTH_ALLKL_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
        int sort = Integer.valueOf(param.getIsLast()).intValue();
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }

        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }

        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.subclass = ? ");
            paramList.add(param.getSubClassId());
        }
        if(1 == sort){
            sb.append(" group by a.store order by kl asc) ");
        }else {
            sb.append(" group by a.store order by kl desc) ");
        }
        sb.append(" where rownum <10 ");
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询门店中类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshAllKlModel> queryStoreClassFreshAllKl(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_FRESH_CLASS_MONTH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        int sort = Integer.valueOf(param.getIsLast()).intValue();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        if(1 == sort){
            sb.append(" group by a.store order by kl asc) ");
        }else {
            sb.append(" group by a.store order by kl desc) ");
        }
        sb.append(" where rownum <10 ");
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询门店小类月初至昨日同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param storeList
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreSubClassSameMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end, List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_SUBCLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.subclass = ? ");
            paramList.add(param.getSubClassId());
        }
        sb.append("  group by a.store ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询门店中类月初至昨日同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param storeList
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreClassSameMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end,List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_CLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        sb.append(" group by a.store ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询门店小类月初至昨日销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param storeList
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreSubClassMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end,List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_SUBCLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }

        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.subclass = ? ");
            paramList.add(param.getSubClassId());
        }
        sb.append("  group by a.store ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询门店中类月初至昨日销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param storeList
     * @return
     */
    public List<FreshSaleAndRateModel> queryStoreClassMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end,List<String> storeList){
        StringBuilder sb = new StringBuilder(TOTAL_STORE_CLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        sb.append(" group by a.store ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询区域小类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshAllKlModel> queryAreaSubClassFreshAllKlList(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_AREA_FRESH_SUBCLASS_MONTH_ALLKL_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
        int sort = Integer.valueOf(param.getIsLast()).intValue();
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }

        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }

        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.subclass = ? ");
            paramList.add(param.getSubClassId());
        }
        if(1 == sort){
            sb.append(" group by b.region order by kl asc ");
        }else {
            sb.append(" group by b.region order by kl desc ");
        }
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询区域中类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshAllKlModel> queryAreaClassFreshAllKl(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_AREA_FRESH_CLASS_MONTH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        int sort = Integer.valueOf(param.getIsLast()).intValue();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        if(1 == sort){
            sb.append(" group by b.region order by kl asc ");
        }else {
            sb.append(" group by b.region order by kl desc ");
        }
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询区域小类月初至昨日同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param regionList
     * @return
     */
    public List<FreshSaleAndRateModel> queryAreaSubClassSameMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end,List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_AREA_SUBCLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.subclass = ? ");
            paramList.add(param.getSubClassId());
        }
        sb.append("  group by a.region ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询区域中类月初至昨日同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param regionList
     * @return
     */
    public List<FreshSaleAndRateModel> queryAreaClassSameMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end, List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_AREA_CLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        sb.append(" group by a.region ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询区域小类月初至昨日销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param regionList
     * @return
     */
    public List<FreshSaleAndRateModel> queryAreaSubClassMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end,List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_AREA_SUBCLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){
            sb.append(" and a.subclass = ? ");
            paramList.add(param.getSubClassId());
        }
        sb.append("  group by a.region ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询区域中类月初至昨日销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @param regionList
     * @return
     */
    public List<FreshSaleAndRateModel> queryAreaClassMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end, List<String> regionList){
        StringBuilder sb = new StringBuilder(TOTAL_AREA_CLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        sb.append(" group by a.region ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询小类月初至昨日销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshSaleAndRateModel> querySubClassMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_SUBCLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        sb.append("  group by a.subclass ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询小类月初至昨日同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshSaleAndRateModel> querySubClassSameMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_SUBCLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        sb.append("  group by a.subclass ");
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询小类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshAllKlModel> querySubClassFreshAllKlList(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_FRESH_SUBCLASS_MONTH_ALLKL_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }

        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }

        sb.append(" group by a.subclass ");
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询中类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public FreshAllKlModel queryClassFreshAllKl(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_FRESH_CLASS_MONTH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        if(StringUtils.isNotEmpty(param.getClassId())){
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        return super.queryForObject(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询中类月初至昨日销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @return
     */
    public FreshSaleAndRateModel queryClassMonthFreshSaleAndRate(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_CLASS_MONTH_FRESHSALEANDRATE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        return super.queryForObject(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询中类月初至昨日同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @return
     */
    public FreshSaleAndRateModel queryClassSameMonthFreshSaleAndRate(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_CLASS_SAME_MONTH_FRESHSALEANDRATE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
            sb.append(" and a.class = ? ");
            paramList.add(param.getClassId());
        }
        return super.queryForObject(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询中类月初至昨日销售额，毛利额，可比销售额，可比毛利额，中类ID
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshSaleAndRateModel> queryClassMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_CLASS_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询中类月初至昨日同期销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshSaleAndRateModel> queryClassSameMonthFreshSaleAndRateList(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_CLASS_SAME_MONTH_FRESHSALEANDRATE_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
        return super.queryForList(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询中类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public List<FreshAllKlModel> queryClassFreshAllKlList(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_FRESH_CLASS_MONTH_ALLKL_LIST_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }

        sb.append(" group by a.class ");
        return super.queryForList(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询大类月初至昨日客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public FreshAllKlModel queryDeptFreshAllKl(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_FRESH_DEPT_MONTH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
                sb.append(" and a.dept = ? ");
                paramList.add(param.getDeptIds().get(0));
            }else if(param.getDeptIds().size() > 1){
                sb.append(" and a.dept in ( ");
                sb.append(param.getDeptId());
                sb.append(" ) ");
            }
        }
        return super.queryForObject(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询月初至昨日总客流
     * @param param
     * @param start
     * @param end
     * @return
     */
    public FreshAllKlModel queryFreshAllKl(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_FRESH_MONTH_ALLKL_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        sb.append(" and a.business_date <= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", end)).append("' ");
        sb.append(" and a.business_date >= date'").append(DateUtils.parseDateToStr("yyyy-MM-dd", start)).append("' ");
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
        return super.queryForObject(sb.toString(), FRESH_ALLKL_RM, paramList.toArray());
    }

    /**
     * 查询大类月初至昨日销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @return
     */
    public FreshSaleAndRateModel queryDeptSameMonthFreshSaleAndRate(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_DEPT_SAME_MONTH_FRESHSALEANDRATE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
//        sb.append(" and a.month_last_year = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
//                .append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
        return super.queryForObject(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
    }

    /**
     * 查询大类月初至昨日销售额，毛利额，可比销售额，可比毛利额
     * @param param
     * @param start
     * @param end
     * @return
     */
    public FreshSaleAndRateModel queryDeptMonthFreshSaleAndRate(FreshSpecialReportRequest param, Date start, Date end){
        StringBuilder sb = new StringBuilder(TOTAL_DEPT_MONTH_FRESHSALEANDRATE_PREFIX);
        List<Object> paramList = new ArrayList<Object>();
        //sb.append(" and a.month = '").append(DateUtils.parseDateToStr("yyyyMMdd", start)).append("-")
        //.append(DateUtils.parseDateToStr("yyyyMMdd", end)).append("' ");
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
        return super.queryForObject(sb.toString(),TOTAL_FRESHSALEANDRATE_RM, paramList.toArray());
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
