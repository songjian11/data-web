package com.cs.mobile.api.dao.dailyreport;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.dailyreport.WorstAndBestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class WorstAndBestDao extends AbstractDao {
    private static final RowMapper<WorstAndBestModel> WORST_BEST_RM = new BeanPropertyRowMapper<WorstAndBestModel>(WorstAndBestModel.class);
    //查询省份最坏的大类
    private static final String GET_WORST_PROVINCE_PREFIX = "select h.* " +
            "  from (select b.province_id as relateId, " +
            "               max(b.province_name) as relateName, " +
            "               a.dept_id as id, " +
            "               max(c.dept_name) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.province_id ORDER BY SUM(a.amt) asc) as rn " +
            "          from CSMB_DEPT_SALES a,csmb_store b,csmb_code_dept c " +
            "          where a.store_id = b.store_id " +
            "          and a.dept_id = c.dept ";

    //查询省份最好的大类
    private static final String GET_BEST_PROVINCE_PREFIX = "select h.* " +
            "  from (select b.province_id as relateId, " +
            "               max(b.province_name) as relateName, " +
            "               a.dept_id as id, " +
            "               max(c.dept_name) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.province_id ORDER BY SUM(a.amt) desc) as rn " +
            "          from CSMB_DEPT_SALES a,csmb_store b,csmb_code_dept c " +
            "          where a.store_id = b.store_id " +
            "          and a.dept_id = c.dept ";

    //查询省份月最好的大类
    private static final String GET_MONTH_BEST_PROVINCE_PREFIX = "select h.* from (select  " +
            " a.area as relateId, " +
            " max(a.area_name) as relateName, " +
            " a.dept as id, " +
            " ROW_NUMBER() OVER(PARTITION BY a.area ORDER BY sum(a.mn_sale_value_in) desc) as rn " +
            " from cmx.cmx_rpt_store_dept_gp_all a " +
            " where 1=1 ";

    //查询省份月最坏的大类
    private static final String GET_MONTH_WORST_PROVINCE_PREFIX = "select h.* from (select  " +
            " a.area as relateId, " +
            " max(a.area_name) as relateName, " +
            " a.dept as id, " +
            " ROW_NUMBER() OVER(PARTITION BY a.area ORDER BY sum(a.mn_sale_value_in) asc) as rn " +
            " from cmx.cmx_rpt_store_dept_gp_all a " +
            " where 1=1 ";

    //查询战区最坏的大类
    private static final String GET_WORST_THEATER_PREFIX = "select h.* " +
            "  from (select '' as relateId, " +
            "               d.domain as relateName, " +
            "               a.dept_id as id, " +
            "               max(c.dept_name) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY d.domain ORDER BY SUM(a.amt) asc) as rn " +
            "          from CSMB_DEPT_SALES a,csmb_store b,csmb_code_dept c,csmb_domain_region d " +
            "          where a.store_id = b.store_id " +
            "          and a.dept_id = c.dept " +
            "          and b.area_id = d.region ";

    //查询战区最好的大类
    private static final String GET_BEST_THEATER_PREFIX = "select h.* " +
            "  from (select '' as relateId, " +
            "               d.domain as relateName, " +
            "               a.dept_id as id, " +
            "               max(c.dept_name) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY d.domain ORDER BY SUM(a.amt) desc) as rn " +
            "          from CSMB_DEPT_SALES a,csmb_store b,csmb_code_dept c,csmb_domain_region d " +
            "          where a.store_id = b.store_id " +
            "          and a.dept_id = c.dept " +
            "          and b.area_id = d.region ";

    //查询战区月最好的大类
    private static final String GET_MONTH_BEST_THEATER_PREFIX = "select h.* from (select  " +
            " '' as relateId, " +
            " max(b.domain) as relateName, " +
            " a.dept as id, " +
            " ROW_NUMBER() OVER(PARTITION BY b.domain ORDER BY sum(a.mn_sale_value_in) desc) as rn " +
            " from cmx.cmx_rpt_store_dept_gp_all a, rms.BIP_CHYTB_DOMAIN_REGION b " +
            " where 1=1 ";

    //查询战区月最坏的大类
    private static final String GET_MONTH_WORST_THEATER_PREFIX = "select h.* from (select  " +
            " '' as relateId, " +
            " max(b.domain) as relateName, " +
            " a.dept as id, " +
            " ROW_NUMBER() OVER(PARTITION BY b.domain ORDER BY sum(a.mn_sale_value_in) asc) as rn " +
            " from cmx.cmx_rpt_store_dept_gp_all a, rms.BIP_CHYTB_DOMAIN_REGION b " +
            " where 1=1 ";

    //查询区域最坏的大类
    private static final String GET_WORST_REGION_PREFIX = "select h.* " +
            "  from (select b.area_id as relateId, " +
            "               max(b.area_name) as relateName, " +
            "               a.dept_id as id, " +
            "               max(c.dept_name) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.area_id ORDER BY SUM(a.amt) asc) as rn " +
            "          from CSMB_DEPT_SALES a,csmb_store b,csmb_code_dept c " +
            "          where a.store_id = b.store_id " +
            "          and a.dept_id = c.dept ";

    //查询区域最好的大类
    private static final String GET_BEST_REGION_PREFIX = "select h.* " +
            "  from (select b.area_id as relateId, " +
            "               max(b.area_name) as relateName, " +
            "               a.dept_id as id, " +
            "               max(c.dept_name) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.area_id ORDER BY SUM(a.amt) desc) as rn " +
            "          from CSMB_DEPT_SALES a,csmb_store b,csmb_code_dept c " +
            "          where a.store_id = b.store_id " +
            "          and a.dept_id = c.dept ";

    //查询区域月最好的大类
    private static final String GET_MONTH_BEST_REGION_PREFIX = "select h.* " +
            "  from (select a.region as relateId, " +
            "               max(a.region_name) as relateName, " +
            "               a.dept as id, " +
            "               ROW_NUMBER() OVER(PARTITION BY a.region ORDER BY sum(a.mn_sale_value_in) desc) as rn " +
            "          from cmx.cmx_rpt_store_dept_gp_all a " +
            "         where 1=1 ";

    //查询区域月最坏的大类
    private static final String GET_MONTH_WORST_REGION_PREFIX = "select h.* " +
            "  from (select a.region as relateId, " +
            "               max(a.region_name) as relateName, " +
            "               a.dept as id, " +
            "               ROW_NUMBER() OVER(PARTITION BY a.region ORDER BY sum(a.mn_sale_value_in) asc) as rn " +
            "          from cmx.cmx_rpt_store_dept_gp_all a " +
            "         where 1=1 ";

    //查询省份最坏的单品
    private static final String GET_WORST_ITEM_PROVINCE_PREFIX = "select h.* " +
            "  from (select /*+PARALLEL(A,8)*/ b.province_id as relateId, " +
            "               max(b.province_name) as relateName, " +
            "               a.ITEM as id, " +
            "               max(a.ITEM_dESC) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.province_id ORDER BY SUM(a.amt) asc) as rn " +
            "          from csmb_store_saledetail a,csmb_store b " +
            "          where a.STORE = b.store_id ";

    //查询省份最好的单品
    private static final String GET_BEST_ITEM_PROVINCE_PREFIX = "select h.* " +
            "  from (select /*+PARALLEL(A,8)*/ b.province_id as relateId, " +
            "               max(b.province_name) as relateName, " +
            "               a.ITEM as id, " +
            "               max(a.ITEM_dESC) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.province_id ORDER BY SUM(a.amt) desc) as rn " +
            "          from csmb_store_saledetail a,csmb_store b " +
            "          where a.STORE = b.store_id ";

    //查询省份月最好的单品
//    private static final String GET_MONTH_BEST_ITEM_PROVINCE_PREFIX = "select h.* from (select /*+PARALLEL(A,8)*/  " +
//            "       b.area as relateId, " +
//            "       max(b.area_name) as relateName, " +
//            "       a.ITEM as id, " +
//            "       max(c.item_desc) as name, " +
//            "       ROW_NUMBER() OVER(PARTITION BY b.area ORDER BY SUM(a.mn_sale_value_in) desc) as rn " +
//            "  from cmx.cmx_rpt_store_item_gp_all a, rms.mv_loc_mgr b, rms.item_master c " +
//            " where a.store = b.store " +
//            "   and a.item = c.item ";
    private static final String GET_MONTH_BEST_ITEM_PROVINCE_PREFIX = "select relate_id as relateId, relate_name as relateName, id, name " +
            " from cmx.cmx_rpt_item_sale_top a " +
            " where a.org_type='AREA' ";

    //查询省份月最坏的单品
//    private static final String GET_MONTH_WORST_ITEM_PROVINCE_PREFIX = "select h.* from (select /*+PARALLEL(A,8)*/  " +
//            "       b.area as relateId, " +
//            "       max(b.area_name) as relateName, " +
//            "       a.ITEM as id, " +
//            "       max(c.item_desc) as name, " +
//            "       ROW_NUMBER() OVER(PARTITION BY b.area ORDER BY SUM(a.mn_sale_value_in) asc) as rn " +
//            "  from cmx.cmx_rpt_store_item_gp_all a, rms.mv_loc_mgr b, rms.item_master c " +
//            " where a.store = b.store " +
//            "   and a.item = c.item ";
    private static final String GET_MONTH_WORST_ITEM_PROVINCE_PREFIX = "select relate_id as relateId, relate_name as relateName, id, name " +
            " from cmx.cmx_rpt_item_sale_top a " +
            " where a.org_type='AREA' ";

    //查询战区最坏的单品
    private static final String GET_WORST_ITEM_THEATER_PREFIX = "select h.* " +
            "  from (select /*+PARALLEL(A,8)*/ '' as relateId, " +
            "               max(c.domain) as relateName, " +
            "               a.ITEM as id, " +
            "               max(a.ITEM_dESC) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY c.domain ORDER BY SUM(a.amt) asc) as rn " +
            "          from csmb_store_saledetail a,csmb_store b,csmb_domain_region c " +
            "          where a.STORE = b.store_id " +
            "          and a.REGION = c.region ";

    //查询战区最好的单品
    private static final String GET_BEST_ITEM_THEATER_PREFIX = "select h.* " +
            "  from (select /*+PARALLEL(A,8)*/ '' as relateId, " +
            "               max(c.domain) as relateName, " +
            "               a.ITEM as id, " +
            "               max(a.ITEM_dESC) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY c.domain ORDER BY SUM(a.amt) desc) as rn " +
            "          from csmb_store_saledetail a,csmb_store b,csmb_domain_region c " +
            "          where a.STORE = b.store_id " +
            "          and a.REGION = c.region ";

    //查询战区月最好的单品
//    private static final String GET_MONTH_BEST_ITEM_THEATER_PREFIX = "select h.* from (select /*+PARALLEL(A,8)*/  " +
//            "       '' as relateId, " +
//            "       max(d.domain) as relateName, " +
//            "       a.ITEM as id, " +
//            "       max(c.item_desc) as name, " +
//            "       ROW_NUMBER() OVER(PARTITION BY d.domain ORDER BY SUM(a.mn_sale_value_in) desc) as rn " +
//            "  from cmx.cmx_rpt_store_item_gp_all a, rms.mv_loc_mgr b, rms.item_master c, rms.BIP_CHYTB_DOMAIN_REGION d " +
//            " where a.store = b.store " +
//            "   and a.item = c.item " +
//            "   and b.region = d.region ";
    private static final String GET_MONTH_BEST_ITEM_THEATER_PREFIX = "select relate_id as relateId, relate_name as relateName, id, name " +
            " from cmx.cmx_rpt_item_sale_top a " +
            " where a.org_type='THEATER' ";

    //查询战区月最坏的单品
//    private static final String GET_MONTH_WORST_ITEM_THEATER_PREFIX = "select h.* from (select /*+PARALLEL(A,8)*/  " +
//            "       '' as relateId, " +
//            "       max(d.domain) as relateName, " +
//            "       a.ITEM as id, " +
//            "       max(c.item_desc) as name, " +
//            "       ROW_NUMBER() OVER(PARTITION BY d.domain ORDER BY SUM(a.mn_sale_value_in) asc) as rn " +
//            "  from cmx.cmx_rpt_store_item_gp_all a, rms.mv_loc_mgr b, rms.item_master c, rms.BIP_CHYTB_DOMAIN_REGION d " +
//            " where a.store = b.store " +
//            "   and a.item = c.item " +
//            "   and b.region = d.region ";
    private static final String GET_MONTH_WORST_ITEM_THEATER_PREFIX = "select relate_id as relateId, relate_name as relateName, id, name " +
            " from cmx.cmx_rpt_item_sale_top a " +
            " where a.org_type='THEATER' ";

    //查询区域最坏的单品
    private static final String GET_WORST_ITEM_REGION_PREFIX = "select h.* " +
            "  from (select /*+PARALLEL(A,8)*/ b.area_id as relateId, " +
            "               max(b.area_name) as relateName, " +
            "               a.ITEM as id, " +
            "               max(a.ITEM_dESC) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.area_id ORDER BY SUM(a.amt) asc) as rn " +
            "          from csmb_store_saledetail a,csmb_store b " +
            "          where a.STORE = b.store_id ";

    //查询区域最好的单品
    private static final String GET_BEST_ITEM_REGION_PREFIX = "select h.* " +
            "  from (select /*+PARALLEL(A,8)*/ b.area_id as relateId, " +
            "               max(b.area_name) as relateName, " +
            "               a.ITEM as id, " +
            "               max(a.ITEM_dESC) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.area_id ORDER BY SUM(a.amt) desc) as rn " +
            "          from csmb_store_saledetail a,csmb_store b " +
            "          where a.STORE = b.store_id ";

    //查询区域月最好的单品
//    private static final String GET_MONTH_BEST_ITEM_REGION_PREFIX = "select h.* from (select /*+PARALLEL(A,8)*/  " +
//            "       b.region as relateId, " +
//            "       max(b.region_name) as relateName, " +
//            "       a.ITEM as id, " +
//            "       max(c.item_desc) as name, " +
//            "       ROW_NUMBER() OVER(PARTITION BY b.region ORDER BY SUM(a.mn_sale_value_in) desc) as rn " +
//            "  from cmx.cmx_rpt_store_item_gp_all a, rms.mv_loc_mgr b, rms.item_master c " +
//            " where a.store = b.store " +
//            "   and a.item = c.item ";
    private static final String GET_MONTH_BEST_ITEM_REGION_PREFIX = "select relate_id as relateId, relate_name as relateName, id, name " +
            " from cmx.cmx_rpt_item_sale_top a " +
            " where a.org_type='REGION' ";

    //查询区域月最坏的单品
//    private static final String GET_MONTH_WORST_ITEM_REGION_PREFIX = "select h.* from (select /*+PARALLEL(A,8)*/  " +
//            "       b.region as relateId, " +
//            "       max(b.region_name) as relateName, " +
//            "       a.ITEM as id, " +
//            "       max(c.item_desc) as name, " +
//            "       ROW_NUMBER() OVER(PARTITION BY b.region ORDER BY SUM(a.mn_sale_value_in) asc) as rn " +
//            "  from cmx.cmx_rpt_store_item_gp_all a, rms.mv_loc_mgr b, rms.item_master c " +
//            " where a.store = b.store " +
//            "   and a.item = c.item ";
    private static final String GET_MONTH_WORST_ITEM_REGION_PREFIX = "select relate_id as relateId, relate_name as relateName, id, name " +
            " from cmx.cmx_rpt_item_sale_top a " +
            " where a.org_type='REGION' ";
    //查询门店最坏大类
    private static final String GET_WORST_STORE_PREFIX = "select h.* " +
            "  from (select b.store_id as relateId, " +
            "               max(b.store_name) as relateName, " +
            "               a.dept_id as id, " +
            "               max(c.dept_name) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.store_id ORDER BY SUM(a.amt) asc) as rn " +
            "          from CSMB_DEPT_SALES a,csmb_store b,csmb_code_dept c " +
            "          where a.store_id = b.store_id " +
            "          and a.dept_id = c.dept ";
    //查询门店最好大类
    private static final String GET_BEST_STORE_PREFIX = "select h.* " +
            "  from (select b.store_id as relateId, " +
            "               max(b.store_name) as relateName, " +
            "               a.dept_id as id, " +
            "               max(c.dept_name) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.store_id ORDER BY SUM(a.amt) desc) as rn " +
            "          from CSMB_DEPT_SALES a,csmb_store b,csmb_code_dept c " +
            "          where a.store_id = b.store_id " +
            "          and a.dept_id = c.dept ";
    //查询门店月最坏大类
    private static final String GET_MONTH_WORST_STORE_PREFIX = "select h.* " +
            "  from (select a.store as relateId, " +
            "               max(a.store_name) as relateName, " +
            "               a.dept as id, " +
            "               ROW_NUMBER() OVER(PARTITION BY a.store ORDER BY sum(a.mn_sale_value_in) asc) as rn " +
            "          from cmx.cmx_rpt_store_dept_gp_all a " +
            "         where 1=1 ";

    //查询门店月最好大类
    private static final String GET_MONTH_BEST_STORE_PREFIX = "select h.* " +
            "  from (select a.store as relateId, " +
            "               max(a.store_name) as relateName, " +
            "               a.dept as id, " +
            "               ROW_NUMBER() OVER(PARTITION BY a.store ORDER BY sum(a.mn_sale_value_in) desc) as rn " +
            "          from cmx.cmx_rpt_store_dept_gp_all a " +
            "         where 1=1 ";

    //查询门店最坏单品
    private static final String GET_WORST_ITEM_STORE_PREFIX = "select h.* " +
            "  from (select /*+PARALLEL(A,8)*/ b.store_id as relateId, " +
            "               max(b.store_name) as relateName, " +
            "               a.ITEM as id, " +
            "               max(a.ITEM_dESC) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.store_id ORDER BY SUM(a.amt) asc) as rn " +
            "          from csmb_store_saledetail a,csmb_store b " +
            "          where a.STORE = b.store_id ";

    //查询门店最好单品
    private static final String GET_BEST_ITEM_STORE_PREFIX = "select h.* " +
            "  from (select /*+PARALLEL(A,8)*/ b.store_id as relateId, " +
            "               max(b.store_name) as relateName, " +
            "               a.ITEM as id, " +
            "               max(a.ITEM_dESC) as name, " +
            "               ROW_NUMBER() OVER(PARTITION BY b.store_id ORDER BY SUM(a.amt) desc) as rn " +
            "          from csmb_store_saledetail a,csmb_store b " +
            "          where a.STORE = b.store_id ";

    //查询门店月最坏单品
//    private static final String GET_MONTH_WORST_ITEM_STORE_PREFIX = "select h.* from (select /*+PARALLEL(A,8)*/  " +
//            "       b.store as relateId, " +
//            "       max(b.store_name) as relateName, " +
//            "       a.ITEM as id, " +
//            "       max(c.item_desc) as name, " +
//            "       ROW_NUMBER() OVER(PARTITION BY b.store ORDER BY SUM(a.mn_sale_value_in) asc) as rn " +
//            "  from cmx.cmx_rpt_store_item_gp_all a, rms.mv_loc_mgr b, rms.item_master c " +
//            " where a.store = b.store " +
//            "   and a.item = c.item ";
    private static final String GET_MONTH_WORST_ITEM_STORE_PREFIX = "select relate_id as relateId, relate_name as relateName, id, name " +
            " from cmx.cmx_rpt_item_sale_top a " +
            " where a.org_type='STORE' " +
            " and a.type =1 ";

    //查询门店月最好单品
//    private static final String GET_MONTH_BEST_ITEM_STORE_PREFIX = "select h.* from (select /*+PARALLEL(A,8)*/  " +
//            "       b.store as relateId, " +
//            "       max(b.store_name) as relateName, " +
//            "       a.ITEM as id, " +
//            "       max(c.item_desc) as name, " +
//            "       ROW_NUMBER() OVER(PARTITION BY b.store ORDER BY SUM(a.mn_sale_value_in) desc) as rn " +
//            "  from cmx.cmx_rpt_store_item_gp_all a, rms.mv_loc_mgr b, rms.item_master c " +
//            " where a.store = b.store " +
//            "   and a.item = c.item ";
    private static final String GET_MONTH_BEST_ITEM_STORE_PREFIX = "select relate_id as relateId, relate_name as relateName, id, name " +
            " from cmx.cmx_rpt_item_sale_top a " +
            " where a.org_type='STORE' " +
            " and a.type =2 ";

    /**
     * 查询门店月最好单品
     * @param regions
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthBestItemStore(List<String> regions,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_BEST_ITEM_STORE_PREFIX);
        if(null != depts && depts.size() > 0){
            sb.append(" and a.dept=").append(depts.get(0)).append(" ");
        }
        if(null != regions && regions.size() > 0){
            sb.append(" and a.parent_id=").append(regions.get(0)).append(" ");
        }
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询门店月最坏单品
     * @param regions
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthWorstItemStore(List<String> regions,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_WORST_ITEM_STORE_PREFIX);
        if(null != depts && depts.size() > 0){
            sb.append(" and a.dept=").append(depts.get(0)).append(" ");
        }
        if(null != regions && regions.size() > 0){
            sb.append(" and a.parent_id=").append(regions.get(0)).append(" ");
        }
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }


    /**
     * 查询门店最好单品
     * @param regions
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryBestItemStore(List<String> regions,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_BEST_ITEM_STORE_PREFIX);
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.area_id =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.area_id in （").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        sb.append(" group by b.store_id, a.ITEM) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询门店最坏单品
     * @param regions
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryWorstItemStore(List<String> regions,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_WORST_ITEM_STORE_PREFIX);
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.area_id =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.area_id in （").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        sb.append(" group by b.store_id, a.ITEM) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询门店月最好大类
     * @param regions
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthBestStore(List<String> regions,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_BEST_STORE_PREFIX);
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }

        sb.append(" and a.area = 101 ");

        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and a.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and a.region in （").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        sb.append(" group by a.store,a.dept) h where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询门店月最坏大类
     * @param regions
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthWorstStore(List<String> regions,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_WORST_STORE_PREFIX);
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }

        sb.append(" and a.area = 101 ");

        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and a.region =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and a.region in （").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        sb.append(" group by a.store,a.dept) h where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询门店最好大类
     * @param regions
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryBestStore(List<String> regions,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_BEST_STORE_PREFIX);
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept_id=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept_id in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.area_id =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.area_id in （").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.store_id, a.dept_id) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询门店最坏大类
     * @param regions
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryWorstStore(List<String> regions,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_WORST_STORE_PREFIX);
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept_id=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept_id in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        if(null != regions && regions.size() > 0){
            if(1 == regions.size()){
                sb.append(" and b.area_id =").append(regions.get(0)).append(" ");
            }else{
                sb.append(" and b.area_id in （").append(listToStrNotHaveQuotationMark(regions)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.store_id, a.dept_id) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询区域月最好单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthBestItemRegion(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_BEST_ITEM_REGION_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.is_compare = 1 ");
        }else{
            sb.append(" and a.is_compare = 0");
        }
        sb.append(" and a.type =2 ");
        if(null != depts && depts.size() > 0){
            sb.append(" and a.dept=").append(depts.get(0)).append(" ");
        }
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询区域月最坏单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthWorstItemRegion(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_WORST_ITEM_REGION_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.is_compare = 1 ");
        }else{
            sb.append(" and a.is_compare = 0 ");
        }
        sb.append(" and a.type = 1 ");
        if(null != depts && depts.size() > 0){
            sb.append(" and a.dept=").append(depts.get(0)).append(" ");
        }
//        sb.append(" and b.area = 101 ");
//        sb.append(" group by b.region, a.item) h where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询区域最好单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryBestItemRegion(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_BEST_ITEM_REGION_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.area_id, a.ITEM) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(), WORST_BEST_RM,null);
    }

    /**
     * 查询区域最坏单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryWorstItemRegion(int isCompare, List<String> depts){
        StringBuilder sb = new StringBuilder(GET_WORST_ITEM_REGION_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.area_id, a.ITEM) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(), WORST_BEST_RM,null);
    }
    /**
     * 查询战区月最好单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthBestItemTheater(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_BEST_ITEM_THEATER_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.is_compare = 1 ");
        }else{
            sb.append(" and a.is_compare = 0 ");
        }
        sb.append(" and a.type =2 ");
        if(null != depts && depts.size() > 0){
            sb.append(" and a.dept=").append(depts.get(0)).append(" ");
        }
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询战区月最坏单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthWorstItemTheater(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_WORST_ITEM_THEATER_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.is_compare = 1 ");
        }else{
            sb.append(" and a.is_compare = 0 ");
        }
        sb.append(" and a.type = 1 ");
        if(null != depts && depts.size() > 0){
            sb.append(" and a.dept=").append(depts.get(0)).append(" ");
        }
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询战区最好单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryBestItemTheater(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_BEST_ITEM_THEATER_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by c.domain, a.ITEM) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(), WORST_BEST_RM,null);
    }

    /**
     * 查询战区最坏单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryWorstItemTheater(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_WORST_ITEM_THEATER_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by c.domain, a.ITEM) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(), WORST_BEST_RM,null);
    }

    /**
     * 查询省月最好单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthBestItemProvince(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_BEST_ITEM_PROVINCE_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.is_compare = 1 ");
        }else{
            sb.append(" and a.is_compare = 0 ");
        }
        sb.append(" and a.type = 2 ");
        if(null != depts && depts.size() > 0){
            sb.append(" and a.dept=").append(depts.get(0)).append(" ");
        }
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询省月最坏单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthWorstItemProvince(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_WORST_ITEM_PROVINCE_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.is_compare = 1 ");
        }else{
            sb.append(" and a.is_compare = 0 ");
        }
        sb.append(" and a.type = 1 ");
        if(null != depts && depts.size() > 0){
            sb.append(" and a.dept=").append(depts.get(0)).append(" ");
        }
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询省最好单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryBestItemProvince(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_BEST_ITEM_PROVINCE_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.province_id, a.ITEM) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询省最坏单品
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryWorstItemProvince(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_WORST_ITEM_PROVINCE_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.province_id, a.ITEM) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询区域月最好
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthBestRegion(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_BEST_REGION_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.mall_name = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and a.area = 101 ");
        sb.append(" group by a.region, a.dept) h where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询区域月最坏
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthWorstRegion(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_WORST_REGION_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.mall_name = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and a.area = 101 ");
        sb.append(" group by a.region, a.dept) h where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询区域最好
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryBestRegion(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_BEST_REGION_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept_id=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept_id in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.area_id, a.dept_id) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(), WORST_BEST_RM,null);
    }

    /**
     * 查询区域最坏
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryWorstRegion(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_WORST_REGION_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept_id=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept_id in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.area_id, a.dept_id) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(), WORST_BEST_RM,null);
    }

    /**
     * 查询战区月最好
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthBestTheater(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_BEST_THEATER_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.mall_name = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and a.area = 101 ");
        sb.append(" group by b.domain, a.dept) h where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询战区月最坏
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryMonthWorstTheater(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_WORST_THEATER_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.mall_name = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and a.area = 101 ");
        sb.append(" group by b.domain, a.dept) h where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询战区最好的大类
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryBestTheater(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_BEST_THEATER_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept_id=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept_id in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by d.domain, a.dept_id) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(), WORST_BEST_RM,null);
    }

    /**
     * 查询战区最坏的大类
     * @param isCompare
     * @param depts
     * @return
     */
    public List<WorstAndBestModel> queryWorstTheater(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_WORST_THEATER_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept_id=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept_id in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by d.domain, a.dept_id) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(), WORST_BEST_RM,null);
    }

    /**
     * 月湖南省最好
     * @param isCompare
     * @return
     */
    public List<WorstAndBestModel> queryMonthBestProvince(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_BEST_PROVINCE_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.mall_name = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and a.area = 101 ");
        sb.append(" group by a.area,a.dept) h where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 月湖南省最差
     * @param isCompare
     * @return
     */
    public List<WorstAndBestModel> queryMonthWorstProvince(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_MONTH_WORST_PROVINCE_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.mall_name = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and a.area = 101 ");
        sb.append(" group by a.area,a.dept) h where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询湖南省最坏的大类
     * @return
     */
    public List<WorstAndBestModel> queryWorstProvince(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_WORST_PROVINCE_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept_id=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept_id in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.province_id, a.dept_id) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }

    /**
     * 查询湖南省最好的大类
     * @return
     */
    public List<WorstAndBestModel> queryBestProvince(int isCompare,List<String> depts){
        StringBuilder sb = new StringBuilder(GET_BEST_PROVINCE_PREFIX);
        if(1 == isCompare){
            sb.append(" and a.MALL_NAME = 1 ");
        }
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and a.dept_id=").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and a.dept_id in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        sb.append(" and b.province_id = 101 ");
        sb.append(" group by b.province_id, a.dept_id) h  where h.rn = 1 ");
        return super.queryForList(sb.toString(),WORST_BEST_RM,null);
    }
}
