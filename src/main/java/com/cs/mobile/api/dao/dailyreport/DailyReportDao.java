package com.cs.mobile.api.dao.dailyreport;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.dailyreport.AreaDailyReportModel;
import com.cs.mobile.api.model.dailyreport.AreaPersonModel;
import com.cs.mobile.api.model.dailyreport.CategoryDailyReportModel;
import com.cs.mobile.api.model.dailyreport.CompanyDailyReportModel;
import com.cs.mobile.api.model.dailyreport.ContinuityLossGoodsDetailModel;
import com.cs.mobile.api.model.dailyreport.ContinuityLossGoodsModel;
import com.cs.mobile.api.model.dailyreport.FreshDailyReportModel;
import com.cs.mobile.api.model.dailyreport.GrossProfitGoodsModel;
import com.cs.mobile.api.model.dailyreport.ProvinceDailyReportModel;
import com.cs.mobile.api.model.dailyreport.StoreDailyReportModel;
import com.cs.mobile.api.model.dailyreport.StoreFreshDailyReport;
import com.cs.mobile.api.model.dailyreport.StoreLargeClassMoneyModel;
import com.cs.mobile.api.model.dailyreport.StoreLossGoodsDetailModel;
import com.cs.mobile.api.model.dailyreport.StoreLossGoodsModel;
import com.cs.mobile.api.model.dailyreport.StorePersonModel;
import com.cs.mobile.api.model.dailyreport.request.DailyReportRequest;
import com.cs.mobile.api.model.dailyreport.request.FreshDailyReportRequest;
import com.cs.mobile.api.model.dailyreport.response.ContinuityLossGoodsDetailSumResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreLossGoodsSumResponse;
import com.cs.mobile.common.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DailyReportDao extends AbstractDao {
    private static final RowMapper<CompanyDailyReportModel> TOTAL_DAILYREPORT_COMPANY_RM = new BeanPropertyRowMapper<CompanyDailyReportModel>(CompanyDailyReportModel.class);
    
    private static final RowMapper<CategoryDailyReportModel> TOTAL_DAILYREPORT_CATEGORY_RM = new BeanPropertyRowMapper<CategoryDailyReportModel>(CategoryDailyReportModel.class);
    
    private static final RowMapper<StoreLossGoodsModel> TOTAL_STORELOSSGOODSMODEL_RM = new BeanPropertyRowMapper<StoreLossGoodsModel>(StoreLossGoodsModel.class);
    
    private static final RowMapper<StoreLossGoodsSumResponse> TOTAL_STORELOSSGOODSSUMMODEL_RM = new BeanPropertyRowMapper<StoreLossGoodsSumResponse>(StoreLossGoodsSumResponse.class);

    private static final RowMapper<StoreLossGoodsDetailModel> TOTAL_STORELOSSGOODSDETAILMODEL_RM = new BeanPropertyRowMapper<StoreLossGoodsDetailModel>(StoreLossGoodsDetailModel.class);
    
    private static final RowMapper<GrossProfitGoodsModel> TOTAL_GROSSPROFITGOODSMODEL_RM = new BeanPropertyRowMapper<GrossProfitGoodsModel>(GrossProfitGoodsModel.class);
    
    private static final RowMapper<ContinuityLossGoodsModel> TOTAL_CONTINUITYLOSSGOODSMODEL_RM = new BeanPropertyRowMapper<ContinuityLossGoodsModel>(ContinuityLossGoodsModel.class);

    private static final RowMapper<ContinuityLossGoodsDetailSumResponse> TOTAL_CONTINUITYLOSSGOODSDETAILSUMRESPONSE_RM = new BeanPropertyRowMapper<ContinuityLossGoodsDetailSumResponse>(ContinuityLossGoodsDetailSumResponse.class);
    
    private static final RowMapper<ContinuityLossGoodsDetailModel> TOTAL_CONTINUITYLOSSGOODSDETAILMODEL_RM = new BeanPropertyRowMapper<ContinuityLossGoodsDetailModel>(ContinuityLossGoodsDetailModel.class);

    private static final RowMapper<StoreLargeClassMoneyModel> TOTAL_STORELARGECLASSMONEYMODEL_RM = new BeanPropertyRowMapper<StoreLargeClassMoneyModel>(StoreLargeClassMoneyModel.class);
    
    private static final RowMapper<AreaDailyReportModel> TOTAL_AREADAILYREPORTMODEL_RM = new BeanPropertyRowMapper<AreaDailyReportModel>(AreaDailyReportModel.class);
    
    private static final RowMapper<StoreDailyReportModel> TOTAL_STOREDAILYREPORTMODEL_RM = new BeanPropertyRowMapper<StoreDailyReportModel>(StoreDailyReportModel.class);
    
    private static final RowMapper<FreshDailyReportModel> TOTAL_FRESHDAILYREPORTMODEL_RM = new BeanPropertyRowMapper<FreshDailyReportModel>(FreshDailyReportModel.class);

    private static final RowMapper<ProvinceDailyReportModel> TOTAL_PROVINCEDAILYREPORTMODEL_RM = new BeanPropertyRowMapper<ProvinceDailyReportModel>(ProvinceDailyReportModel.class);
    
    private static final RowMapper<AreaPersonModel> TOTAL_AREA_PERSONMODEL_RM = new BeanPropertyRowMapper<AreaPersonModel>(AreaPersonModel.class);
    //
    private static final RowMapper<StorePersonModel> TOTAL_STORE_PERSONMODEL_RM = new BeanPropertyRowMapper<StorePersonModel>(StorePersonModel.class);

    private static final RowMapper<StoreFreshDailyReport> TOTAL_STOREFRESHDAILYREPORTMODEL_RM = new BeanPropertyRowMapper<StoreFreshDailyReport>(StoreFreshDailyReport.class);

    private static final String GET_DAILYREPORT_COMPANY_PREFIX = "select distinct 省份               as provinceName, " +
            "       属性               as typeName, " +
            "       昨日销售额         as daySale, " +
            "       去年同期销售额     as sameSale, " +
            "       昨日销售增长率     as daySaleRate, " +
            "       当月累计销售额     as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       昨日前台毛利率     as dayProfit, " +
            "       去年同期前台毛利率 as sameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       昨日客流           as kl " +
            //"       昨日品类客流       as categoryKl " +
            //"       日渗透率           as dayKlRate, " +
            //"       月渗透率           as monthKlRate, " +
            //"       rn           " +
            "  from cmx.bip_wzvi_salinv_area_t " +
            "  where 省份 not like '电商事业部' " +
            "  and 省份 not like '家电%' ";

    private static final String GET_DAILYREPORT_COMPANY_PROVINCE_PREFIX = " select distinct 省份 as provinceName, " +
            "               属性 as typeName, " +
            "               null as deptId, " +
            "               '' as deptName, " +
            "               昨日销售额 as daySale, " +
            "               去年同期销售额 as sameSale, " +
            "               昨日销售增长率 as daySaleRate, " +
            "               当月累计销售额 as monthSale, " +
            "               去年同月累计销售额 as monthSameSale, " +
            "               当月累计销售增长率 as monthSaleRate, " +
            "               昨日前台毛利率 as dayProfit, " +
            "               去年同期前台毛利率 as sameProfit, " +
            "               昨日前台毛利率增长 as dayProfitRate, " +
            "               当月累计前台毛利率 as monthProfit, " +
            "               去年同月前台毛利率 as monthSameProfit, " +
            "               当月前台毛利率增长 as monthProfitRate, " +
            "               昨日客流 as kl, " +
            "               null as deptKl, " +
            "               null as dayKlRate, " +
            "               null as monthKlRate， 9999 as rn " +
            "          from cmx.bip_wzvi_salinv_area_t " +
            "         where 省份 not like '家电%' " +
            "           and 省份 not like '电商事业部' ";

    private static final String GET_DAILYREPORT_PROVINCE_PREFIX = "select 省份               as provinceName, " +
            "               属性               as typeName, " +
            "               大类编码           as deptId, " +
            "               大类名称           as deptName, " +
            "               昨日销售额         as daySale, " +
            "               去年同期销售额     as sameSale, " +
            "               昨日销售增长率     as daySaleRate, " +
            "               当月累计销售额     as monthSale, " +
            "               去年同月累计销售额 as monthSameSale, " +
            "               当月累计销售增长率 as monthSaleRate, " +
            "               昨日前台毛利率     as dayProfit, " +
            "               去年同期前台毛利率 as sameProfit, " +
            "               昨日前台毛利率增长 as dayProfitRate, " +
            "               当月累计前台毛利率 as monthProfit, " +
            "               去年同月前台毛利率 as monthSameProfit, " +
            "               当月前台毛利率增长 as monthProfitRate, " +
            "               昨日客流           as kl, " +
            "               昨日大类客流       as deptKl, " +
            "               日渗透率           as dayKlRate, " +
            "               月渗透率           as monthKlRate， rn " +
            "          from cmx.bip_wzvi_salinv_area_dept_t " +
            "         where NOT (大类编码 IS NULL AND 品类 not in ('可比合计', '全比合计')) " +
            "           and 省份 not like '电商事业部%' " +
            "           and 省份 not like '家电%' ";

    private static final String GET_DAILYREPORT_AREA_PREFIX = "select 省份               as provinceName, " +
            "       区域               as areaName, " +
            "       属性               as typeName, " +
            "       大类编码           as deptId, " +
            "       大类名称           as deptName, " +
            "       昨日销售额         as daySale, " +
            "       去年同期销售额     as sameSale, " +
            "       昨日销售增长率     as daySaleRate, " +
            "       当月累计销售额     as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       昨日前台毛利率     as dayProfit, " +
            "       去年同期前台毛利率 as sameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       昨日客流           as kl, " +
            "       昨日大类客流       as deptKl, " +
            "       月至今客流         as monthKl, " +
            "       月大类客流         as monthDeptKl, " +
            "       日渗透率           as dayKlRate, " +
            "       月渗透率           as monthKlRate " +
            //"       rn " +
            "  from cmx.bip_wzvi_salinv_area_region_t " +
            "  where NOT (大类编码 IS NULL AND 品类 like '%合计' and 区域 not like '%合计') " +
            "  and 省份 not like '家电%' " +
            "  AND 省份 NOT LIKE '%电商事业部%' ";

    private static final String GET_DAILYREPORT_CATEGORY_PREFIX = "select 大类编码 as deptId, " +
            "       case " +
            "         when 大类名称 is not null then " +
            "          大类名称 " +
            "         else " +
            "          品类 " +
            "       end as deptName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as sameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as sameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       昨日客流 as kl, " +
            "       昨日大类客流 as deptKl, " +
            "       日渗透率 as dayKlRate, " +
            "       月渗透率 as monthKlRate " +
            "  from cmx.bip_wzvi_salinv_dept_t " +
            " where 品类 not like '家电%'  order by replace(replace(品类,'全比合计'),'可比合计') desc,属性,大类编码";

    private static final String GET_DAILYREPORT_STORE_PREFIX = "select 省份               as provinceName, " +
            "       区域               as areaName, " +
            "       属性               as typeName, " +
            "       门店编码           as storeId, " +
            "       门店名称           as storeName, " +
            "       大类编码           as deptId, " +
            "       大类名称           as deptName, " +
            "       昨日销售额         as daySale, " +
            "       去年同期销售额     as sameSale, " +
            "       昨日销售增长率     as daySaleRate, " +
            "       当月累计销售额     as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       昨日前台毛利率     as dayProfit, " +
            "       去年同期前台毛利率 as sameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       昨日客流           as kl, " +
            "       昨日大类客流       as deptKl, " +
            "       月至今客流         as monthKl, " +
            "       月大类客流         as monthDeptKl, " +
            "       日渗透率           as dayKlRate, " +
            "       月渗透率           as monthKlRate " +
            //"       rn           " +
            "  from cmx.wzvi_salinv_area_store_dept_t " +
            " where NOT (大类编码 IS  NULL AND 品类 IS NOT NULL) " +
            "  and 省份 not like '家电%' " +
            "  AND 省份 NOT LIKE '%电商事业部%' ";

    private static final String GET_DAILYREPORT_FRESH_PROVINCE_PREFIX ="select 省份 as provinceName, " +
            "       '' as areaName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            "  from cmx.bip_wzvi_salinv_area_tt " +
            " where 省份 = '湖南' ";

    private static final String GET_DAILYREPORT_CATEGORY_PROVINCE_PREFIX ="select replace(replace(省份,'可比合计'),'全比合计') as provinceName, " +
            "       '' as areaName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            "  from cmx.bip_wzvi_salinv_area_dept_t " +
            "  where 省份 like '湖南%' ";

    private static final String GET_DAILYREPORT_FRESH_DEPT_PROVINCE_PREFIX ="select 省份 as provinceName, " +
            "       '' as areaName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            "  from cmx.bip_wzvi_salinv_area_dept_t " +
            " where 省份 = '湖南' ";

    private static final String GET_DAILYREPORT_FRESH_THEATER_PREFIX ="select 省份 as provinceName, " +
            "       战区 as areaName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            " from cmx.bip_wzvi_salinv_DOMAIN_t " +
            " where 省份 = '湖南' ";

    private static final String GET_DAILYREPORT_CATEGORY_THEATER_PREFIX ="select 省份               as provinceName, " +
            "       replace(replace(战区,'可比合计'),'全比合计')               as areaName, " +
            "       属性               as typeName, " +
            "       昨日销售额         as daySale, " +
            "       去年同期销售额     as daySameSale, " +
            "       昨日销售增长率     as daySaleRate, " +
            "       昨日前台毛利率     as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率           as dayKlRate, " +
            "       当月累计销售额     as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率           as monthKlRate " +
            "  from cmx.bip_wzvi_salinv_domain_t " +
            " where 省份 like '湖南%' ";

    private static final String GET_DAILYREPORT_FRESH_DEPT_THEATER_PREFIX ="select 省份 as provinceName, " +
            "       战区 as areaName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            " from cmx.bip_wzvi_salinv_domain_dept_t " +
            " where 省份 = '湖南' ";

    private static final String GET_DAILYREPORT_FRESH_AREA_PREFIX ="select 省份 as provinceName, " +
            "       区域 as areaName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            " from cmx.bip_wzvi_salinv_area_region_t " +
            " where 省份 like  '湖南%' ";


    private static final String GET_DAILYREPORT_FRESH_DEPT_AREA_PREFIX="select 省份 as provinceName, " +
            "       区域 as areaName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            " from cmx.bip_wzvi_salinv_area_region_t " +
            " where 省份 like  '湖南%' ";

    private static final String GET_AREA_USERNAME_PREFIX = "select area_name as areaName,user_name as userName from CSMB_AREA_USER order by rn ";

    private static final String GET_FRESH_STORE_REPORT_PREFIX  ="select replace(replace(replace(replace(replace(门店名称,'合计'),'非加工生鲜'),'杂货'),'非食'),'餐饮') as storeName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            " from cmx.wzvi_salinv_area_store_dept_t a " +
            " where 省份 = '湖南' " +
            " and not exists(select 1 from cmx.wzvi_salinv_area_store_dept_t b where b.门店名称 = replace(a.门店名称,'合计') and  b.属性 like '%停业%') " +
            " and 门店编码 is null ";

    private static final String GET_FRESH_DEPT_STORE_REPORT_PREFIX  ="select 门店名称 as storeName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            " from cmx.wzvi_salinv_area_store_dept_t a " +
            " where 省份 = '湖南' " +
            " and not exists(select 1 from cmx.wzvi_salinv_area_store_dept_t b where b.门店名称 = a.门店名称 and  b.属性 like '%停业%') ";

    private static final String GET_STORE_USERNAME_PREFIX = "select a.pt_name userName, b.store_name as storeName " +
            "  from csmb_ssoa_person a, csmb_store b " +
            " where a.cost_id = to_char(b.store_id) " +
            "   and a.store_name = '店长' ";

    private static final String GET_DAILYREPORT_ALL_PROVINCE_PREFIX = "select 省份 as provinceName, " +
            "       '' as areaName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       '' as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       '' as monthKlRate " +
            "  from cmx.bip_wzvi_salinv_area_t " +
            " where 省份 = '湖南' ";

    private static final String GET_DAILYREPORT_ALL_AREA_PREFIX ="select 省份               as provinceName, " +
            "       replace(replace(区域,'可比合计'),'全比合计')               as areaName, " +
            "       属性               as typeName, " +
            "       昨日销售额         as daySale, " +
            "       去年同期销售额     as daySameSale, " +
            "       昨日销售增长率     as daySaleRate, " +
            "       昨日前台毛利率     as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率           as dayKlRate, " +
            "       当月累计销售额     as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率           as monthKlRate " +
            "  from cmx.bip_wzvi_salinv_area_region_t " +
            " where 省份 = '湖南' " +
            " and 区域 like '%合计' ";

    private static final String GET_DAILYREPORT_ALL_THEATER_PREFIX ="select '湖南'               as provinceName, " +
            "       replace(replace(战区,'可比合计'),'全比合计') as areaName, " +
            "       属性               as typeName, " +
            "       昨日销售额         as daySale, " +
            "       去年同期销售额     as daySameSale, " +
            "       昨日销售增长率     as daySaleRate, " +
            "       昨日前台毛利率     as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率           as dayKlRate, " +
            "       当月累计销售额     as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率           as monthKlRate " +
            "  from cmx.bip_wzvi_salinv_DOMAIN_t " +
            " where 品类 is null ";

    private static final String GET_ALL_STORE_REPORT_PREFIX  ="select replace(门店名称, '合计') as storeName, " +
            "       属性 as typeName, " +
            "       昨日销售额 as daySale, " +
            "       去年同期销售额 as daySameSale, " +
            "       昨日销售增长率 as daySaleRate, " +
            "       昨日前台毛利率 as dayProfit, " +
            "       去年同期前台毛利率 as daySameProfit, " +
            "       昨日前台毛利率增长 as dayProfitRate, " +
            "       日渗透率 as dayKlRate, " +
            "       当月累计销售额 as monthSale, " +
            "       去年同月累计销售额 as monthSameSale, " +
            "       当月累计销售增长率 as monthSaleRate, " +
            "       当月累计前台毛利率 as monthProfit, " +
            "       去年同月前台毛利率 as monthSameProfit, " +
            "       当月前台毛利率增长 as monthProfitRate, " +
            "       月渗透率 as monthKlRate " +
            "  from cmx.wzvi_salinv_area_store_dept_t a " +
            " where 省份 = '湖南' " +
            " and 门店名称 != '合计' " +
            " and not exists(select 1 from cmx.wzvi_salinv_area_store_dept_t b where b.门店名称 = replace(a.门店名称,'合计') and  b.属性 like '%停业%') " +
            " and 品类 is null ";

    /**
     *
     * @param areas
     * @return
     */
    public List<StoreFreshDailyReport> queryAllStores(String areas){
        StringBuilder sb = new StringBuilder(GET_ALL_STORE_REPORT_PREFIX);
        //拼接区域
        if(StringUtils.isNotEmpty(areas)){
            sb.append(" and 区域 in (");
            String[] arr = areas.split(",");

            StringBuilder areaSb = new StringBuilder();
            for(int i = 0; i < arr.length; i++){
                areaSb.append("'").append(arr[i]).append("',");
            }

            String str = areaSb.toString();
            if(str.endsWith(",")){
                str = str.substring(0,str.lastIndexOf(","));
            }

            sb.append(str).append(") ");
        }
        return super.queryForList(sb.toString(),TOTAL_STOREFRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询战区数据
     * @param isCompare
     * @return
     */
    public List<FreshDailyReportModel> queryAllTheater(int isCompare){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_ALL_THEATER_PREFIX);
        //是否可比(0-否，1-是)
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询区域数据
     * @param isCompare
     * @return
     */
    public List<FreshDailyReportModel> queryAllArea(int isCompare){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_ALL_AREA_PREFIX);
        //是否可比(0-否，1-是)
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 根据区域查询区域合计
     * @param areas
     * @return
     */
    public List<FreshDailyReportModel> queryArea(String areas){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_ALL_AREA_PREFIX);
        //拼接区域
        if(StringUtils.isNotEmpty(areas)){
            sb.append(" and replace(replace(区域,'可比合计'),'全比合计') in (");
            String[] arr = areas.split(",");

            StringBuilder areaSb = new StringBuilder();
            for(int i = 0; i < arr.length; i++){
                areaSb.append("'").append(arr[i]).append("',");
            }

            String str = areaSb.toString();
            if(str.endsWith(",")){
                str = str.substring(0,str.lastIndexOf(","));
            }

            sb.append(str).append(") ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询省汇总数据
     * @param isCompare
     * @return
     */
    public List<FreshDailyReportModel> queryAllProvince(int isCompare){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_ALL_PROVINCE_PREFIX);
        //是否可比(0-否，1-是)
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    public List<StorePersonModel> queryStorePersonModel(){
        return super.queryForList(GET_STORE_USERNAME_PREFIX,TOTAL_STORE_PERSONMODEL_RM,null);
    }

    /**
     * 查询区域大类的可比和全比数据
     * @param param
     * @param areas
     * @return
     */
    public List<FreshDailyReportModel> querySameAndCompareFreshDeptArea(FreshDailyReportRequest param, String areas){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_FRESH_DEPT_AREA_PREFIX);
        if(StringUtils.isNotEmpty(param.getDeptId())){
            sb.append(" and 大类编码 = '").append(param.getDeptId()).append("' ");
        }
        //拼接区域
        if(StringUtils.isNotEmpty(areas)){
            sb.append(" and 区域 in (");
            String[] arr = areas.split(",");

            StringBuilder areaSb = new StringBuilder();
            for(int i = 0; i < arr.length; i++){
                areaSb.append("'").append(arr[i]).append("',");
            }

            String str = areaSb.toString();
            if(str.endsWith(",")){
                str = str.substring(0,str.lastIndexOf(","));
            }

            sb.append(str).append(") ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询区域的可比和全比
     * @param areas
     * @return
     */
    public List<FreshDailyReportModel> querySameAndCompareFreshArea(String categoryName, String areas){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_FRESH_AREA_PREFIX);
        sb.append(" and 品类 like '").append(categoryName).append("%合计' ");
        //拼接区域
        if(StringUtils.isNotEmpty(areas)){
            sb.append(" and 区域 in (");
            String[] arr = areas.split(",");

            StringBuilder areaSb = new StringBuilder();
            for(int i = 0; i < arr.length; i++){
                areaSb.append("'").append(arr[i]).append("',");
            }

            String str = areaSb.toString();
            if(str.endsWith(",")){
                str = str.substring(0,str.lastIndexOf(","));
            }

            sb.append(str).append(") ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }
    /**
     * 查询门店生鲜战区大类报表数据
     * @param areas
     * @param deptId
     * @return
     */
    public List<StoreFreshDailyReport> queryFreshDeptStore(String areas, String deptId){
        StringBuilder sb = new StringBuilder(GET_FRESH_DEPT_STORE_REPORT_PREFIX);
        //拼接区域
        if(StringUtils.isNotEmpty(areas)){
            sb.append(" and 区域 in (");
            String[] arr = areas.split(",");

            StringBuilder areaSb = new StringBuilder();
            for(int i = 0; i < arr.length; i++){
                areaSb.append("'").append(arr[i]).append("',");
            }

            String str = areaSb.toString();
            if(str.endsWith(",")){
                str = str.substring(0,str.lastIndexOf(","));
            }

            sb.append(str).append(") ");
        }
        //拼接大类
        if(StringUtils.isNotEmpty(deptId)){
            sb.append(" and 大类编码 = ").append(deptId).append(" ");
        }
        return super.queryForList(sb.toString(),TOTAL_STOREFRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询门店生鲜战区报表数据
     * @param areas
     * @return
     */
    public List<StoreFreshDailyReport> queryFreshStore(String areas, String categoryName){
        StringBuilder sb = new StringBuilder(GET_FRESH_STORE_REPORT_PREFIX);
        sb.append(" and 品类 = '").append(categoryName).append("' ");
        //拼接区域
        if(StringUtils.isNotEmpty(areas)){
            sb.append(" and 区域 in (");
            String[] arr = areas.split(",");

            StringBuilder areaSb = new StringBuilder();
            for(int i = 0; i < arr.length; i++){
                areaSb.append("'").append(arr[i]).append("',");
            }

            String str = areaSb.toString();

            if(str.endsWith(",")){
                str = str.substring(0,str.lastIndexOf(","));
            }
            sb.append(str).append(") ");
        }
        return super.queryForList(sb.toString(),TOTAL_STOREFRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询区域负责人
     * @return
     */
    public List<AreaPersonModel> queryAreaPersonModel(){
        return super.queryForList(GET_AREA_USERNAME_PREFIX,TOTAL_AREA_PERSONMODEL_RM,null);
    }
    /**
     * 查询省生鲜汇总
     * @param param
     * @return
     */
    public List<FreshDailyReportModel> queryFreshProvince(FreshDailyReportRequest param){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_FRESH_PROVINCE_PREFIX);
        //是否可比(0-否，1-是)
        int isCompare = param.getIsCompare();
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 根据品类名称查询省份各品类数据
     * @param categoryName
     * @return
     */
    public List<FreshDailyReportModel> queryCategoryProvince(String categoryName, int isCompare){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_CATEGORY_PROVINCE_PREFIX);
        if(StringUtils.isNotEmpty(categoryName)){
            sb.append(" and 品类 like '").append(categoryName).append("%合计' ");
        }
        //是否可比(0-否，1-是)
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询省生鲜大类
     * @param param
     * @return
     */
    public List<FreshDailyReportModel> queryFreshDeptProvince(FreshDailyReportRequest param){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_FRESH_DEPT_PROVINCE_PREFIX);
        //是否可比(0-否，1-是)
        int isCompare = param.getIsCompare();
        if(StringUtils.isNotEmpty(param.getDeptId())){
            sb.append(" and 大类编码 in (").append(param.getDeptId()).append(") ");
        }
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }

        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }


    /**
     * 查询战区生鲜汇总
     * @param param
     * @return
     */
    public List<FreshDailyReportModel> queryFreshTheater(FreshDailyReportRequest param){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_FRESH_THEATER_PREFIX);

        //是否可比(0-否，1-是)
        int isCompare = param.getIsCompare();
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 根据品类查询战区品类合计
     * @param categoryName
     * @param isCompare
     * @return
     */
    public List<FreshDailyReportModel> queryCategoryTheater(String categoryName,int isCompare){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_CATEGORY_THEATER_PREFIX);
        if(StringUtils.isNotEmpty(categoryName)){
            sb.append(" and 品类 like '").append(categoryName).append("%' ");
        }
        //是否可比(0-否，1-是)
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }
    /**
     * 查询战区生鲜大类
     * @param param
     * @return
     */
    public List<FreshDailyReportModel> queryFreshDeptTheater(FreshDailyReportRequest param){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_FRESH_DEPT_THEATER_PREFIX);
        //是否可比(0-否，1-是)
        int isCompare = param.getIsCompare();
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        if(StringUtils.isNotEmpty(param.getDeptId())){
            sb.append(" and 大类编码 in (").append(param.getDeptId()).append(") ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询区域品类汇总
     * @param categoryName
     * @param isCompare
     * @return
     */
    public List<FreshDailyReportModel> queryCategoryArea(String categoryName, int isCompare){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_FRESH_AREA_PREFIX);
        if(StringUtils.isNotEmpty(categoryName)){
            sb.append(" and 品类 like '").append(categoryName).append("%合计' ");
        }
        //是否可比(0-否，1-是)
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询区域生鲜大类
     * @param param
     * @return
     */
    public List<FreshDailyReportModel> queryFreshDeptArea(FreshDailyReportRequest param){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_FRESH_DEPT_AREA_PREFIX);
        //是否可比(0-否，1-是)
        int isCompare = param.getIsCompare();
        if(0 == isCompare){
            sb.append(" and 属性 = '全比' ");
        }else if (1 == isCompare){
            sb.append(" and 属性 = '可比' ");
        }
        if(StringUtils.isNotEmpty(param.getDeptId())){
            sb.append(" and 大类编码 in (").append(param.getDeptId()).append(") ");
        }
        return super.queryForList(sb.toString(),TOTAL_FRESHDAILYREPORTMODEL_RM,null);
    }

    /**
     * 查询全司日报
     * @return
     */
    public PageResult<CompanyDailyReportModel> queryCompanyDailyReport(int page,
                                                                       int pageSize){
        return super.queryByPage(GET_DAILYREPORT_COMPANY_PREFIX, CompanyDailyReportModel.class, page,
                pageSize," (case when 省份 = '全比合计' then '全比' when 省份 = '可比合计' then '可比' else 属性 end),属性,省份 ", null);
    }

    /**
     * 查询省份大类日报
     * @return
     */
    public PageResult<ProvinceDailyReportModel> queryProvinceDailyReport(String provinceName,
                                                                           int key,
                                                                           int page,
                                                                           int pageSize){
        StringBuilder sb = new StringBuilder("select * from (");
        sb.append(GET_DAILYREPORT_PROVINCE_PREFIX);
        if(StringUtils.isNotEmpty(provinceName)){
            String[] arr = provinceName.split(",");
            StringBuilder value = new StringBuilder();
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
                if(1 == key){
                    value.append("'").append(arr[i] + "可比合计").append("',");
                }else if(2 == key){
                    value.append("'").append(arr[i] + "全比合计").append("',");
                }else{
                    value.append("'").append(arr[i] + "可比合计").append("',");
                    value.append("'").append(arr[i] + "全比合计").append("',");
                }
            }
            if(value.toString().endsWith(",")){
                provinceName = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 省份 in (").append(provinceName).append(") ");
        }
        sb.append(" union all ");
        sb.append(GET_DAILYREPORT_COMPANY_PROVINCE_PREFIX);
        if(1 == key){
            sb.append(" and 省份 = '").append("可比合计").append("'");
        }else if(2 == key){
            sb.append(" and 省份 = '").append("全比合计").append("'");
        }else{
            sb.append(" and 省份 in('").append("全比合计").append("','")
                    .append("可比合计").append("')");
        }
        sb.append(") ");
        return super.queryByPage(sb.toString(), ProvinceDailyReportModel.class, page,
                pageSize," replace(replace(provinceName, '全比合计'), '可比合计'), " +
                        "          typeName,deptId, " +
                        "          rn ", Sort.DESC, null);
    }

    /**
     * 查询区域大类日报
     * @param provinceName
     * @return
     */
    public PageResult<AreaDailyReportModel> queryAreaDailyReport(String provinceName,
                                                                   String areaName,
                                                                   int key,
                                                                   int page,
                                                                   int pageSize){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_AREA_PREFIX);
        if(StringUtils.isNotEmpty(provinceName)){
            String[] arr = provinceName.split(",");
            String provinceNameStr = "";
            StringBuilder value = new StringBuilder();
            for(int i = 0;i < arr.length; i++){
                value.append(" '").append(arr[i]).append("',");
            }
            if(value.toString().endsWith(",")){
                provinceNameStr = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 省份 in (").append(provinceNameStr).append(") ");
        }
        if(StringUtils.isNotEmpty(areaName)){
            String[] arr = areaName.split(",");
            StringBuilder value = new StringBuilder();
            for(int i = 0;i < arr.length; i++){
                value.append(" '").append(arr[i]).append("',");
                if(1 == key){
                    value.append(" '").append(arr[i] + "可比合计").append("',");
                }else if(2 == key){
                    value.append(" '").append(arr[i] + "全比合计").append("',");
                }else{
                    value.append(" '").append(arr[i] + "全比合计").append("','").append(arr[i] + "可比合计").append("',");
                }
            }
            if(value.toString().endsWith(",")){
                areaName = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 区域 in (").append(areaName).append(") ");
        }
        if(StringUtils.isNotEmpty(provinceName)){
            String[] arr = provinceName.split(",");
            StringBuilder value = new StringBuilder();
            for(int i = 0;i < arr.length; i++){
                if(1 == key){
                    value.append(" '").append(provinceName + "可比合计").append("',");
                }else if(2 == key){
                    value.append(" '").append(provinceName + "全比合计").append("',");
                }else{
                    value.append(" '").append(provinceName + "全比合计").append("','").append(provinceName + "可比合计").append("',");
                }
            }
            if(value.toString().endsWith(",")){
                provinceName = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" or 省份 in (").append(provinceName).append(") ");
        }
        return super.queryByPage(sb.toString(), AreaDailyReportModel.class, page,
                pageSize, " 省份,replace(replace(区域,'全比合计'),'可比合计'),属性,大类编码 ", null);
    }

    /**
     * 查询大类汇总
     * @return
     */
    public List<CategoryDailyReportModel> queryCategoryDailyReport(){
        return super.queryForList(GET_DAILYREPORT_CATEGORY_PREFIX,TOTAL_DAILYREPORT_CATEGORY_RM);
    }

    /**
     * 查询门店大类日报
     * @return
     */
    public PageResult<StoreDailyReportModel> queryStoreDailyReport(String provinceName,
                                                                   String areaName,
                                                                   String storeName ,
                                                                   int page,
                                                                   int pageSize){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_STORE_PREFIX);
        if(StringUtils.isNotEmpty(provinceName)){
            String[] arr = provinceName.split(",");
            StringBuilder value = new StringBuilder();
            String provinceNameStr = "";
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
            }
            if(value.toString().endsWith(",")){
                provinceNameStr = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 省份 in (").append(provinceNameStr).append(") ");
        }
        if(StringUtils.isNotEmpty(areaName)){
            String[] arr = areaName.split(",");
            StringBuilder value = new StringBuilder();
            String areaNameStr = "";
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
            }
            if(value.toString().endsWith(",")){
                areaNameStr = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 区域 in (").append(areaNameStr).append(") ");
        }
        if(StringUtils.isNotEmpty(storeName)){
            String[] arr = storeName.split(",");
            StringBuilder value = new StringBuilder();
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
                value.append("'").append(arr[i] + "合计").append("',");
            }
            if(value.toString().endsWith(",")){
                storeName = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 门店名称 in (").append(storeName).append(") ");
        }

        if(StringUtils.isNotEmpty(areaName)){
            String[] arr = areaName.split(",");
            StringBuilder value = new StringBuilder();
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i] + "合计").append("',");
            }
            if(value.toString().endsWith(",")){
                areaName = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" or 区域 in (").append(areaName).append(") ");
        }

        if(StringUtils.isNotEmpty(provinceName)){
            String[] arr = provinceName.split(",");
            StringBuilder value = new StringBuilder();
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i] + "合计").append("',");
            }
            if(value.toString().endsWith(",")){
                provinceName = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" or 省份 in (").append(provinceName).append(") ");
        }
        return super.queryByPage(sb.toString(), StoreDailyReportModel.class, page,
                pageSize, " replace(省份,'合计'),replace(区域,'合计'),replace(门店名称,'合计'), 大类编码 ", null);
    }
    
    
    
    /**
     * 门店大类汇总
     * @return
     */
    public List<StoreDailyReportModel> queryStoreCategoryReport(DailyReportRequest request){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_STORE_PREFIX);
        
        if(StringUtils.isNotEmpty(request.getProvinceId())){
            String[] arr = request.getProvinceId().split(",");
            StringBuilder value = new StringBuilder();
            String provinceNameStr = "";
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
            }
            if(value.toString().endsWith(",")){
                provinceNameStr = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 省份 in (").append(provinceNameStr).append(") ");
        }
        
        if(StringUtils.isNotEmpty(request.getAreaId())){
            String[] arr = request.getAreaId().split(",");
            StringBuilder value = new StringBuilder();
            String areaNameStr = "";
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
            }
            if(value.toString().endsWith(",")){
                areaNameStr = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 区域 in (").append(areaNameStr).append(") ");
        }
        
        if(StringUtils.isNotEmpty(request.getStoreId())){
            String[] arr = request.getStoreId().split(",");
            StringBuilder value = new StringBuilder();
            String storeNameStr = "";
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
            }
            if(value.toString().endsWith(",")){
            	storeNameStr = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 门店名称 in (").append(storeNameStr).append(") ");
        }
        
        if(StringUtils.isNotEmpty(request.getDeptId())){
            sb.append(" and 大类编码 = '").append(request.getDeptId()).append("' ");
        }

        return super.queryForList(sb.toString(), TOTAL_STOREDAILYREPORTMODEL_RM,null);
    }
    
    /**
     * 区域大类汇总
     * @return
     */
    public List<AreaDailyReportModel> queryAreaCategoryReport(DailyReportRequest request){
		StringBuilder sb = new StringBuilder(GET_DAILYREPORT_AREA_PREFIX);
		
		if(StringUtils.isNotEmpty(request.getProvinceId())){
            String[] arr = request.getProvinceId().split(",");
            StringBuilder value = new StringBuilder();
            String provinceNameStr = "";
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
            }
            if(value.toString().endsWith(",")){
                provinceNameStr = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 省份 in (").append(provinceNameStr).append(") ");
        }
        
        if(StringUtils.isNotEmpty(request.getAreaId())){
            String[] arr = request.getAreaId().split(",");
            StringBuilder value = new StringBuilder();
            String areaNameStr = "";
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
            }
            if(value.toString().endsWith(",")){
                areaNameStr = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 区域 in (").append(areaNameStr).append(") ");
        }
        
        if(StringUtils.isNotEmpty(request.getDeptId())){
            sb.append(" and 大类编码 = '").append(request.getDeptId()).append("' ");
        }
		return super.queryForList(sb.toString(), TOTAL_AREADAILYREPORTMODEL_RM, null);
	}
    
    /**
     * 省份大类汇总
     * @return
     */
    public List<ProvinceDailyReportModel> queryProvinceCategoryReport(DailyReportRequest request){
        StringBuilder sb = new StringBuilder(GET_DAILYREPORT_PROVINCE_PREFIX);
		if(StringUtils.isNotEmpty(request.getProvinceId())){
            String[] arr = request.getProvinceId().split(",");
            StringBuilder value = new StringBuilder();
            String provinceNameStr = "";
            for(int i = 0;i < arr.length; i++){
                value.append("'").append(arr[i]).append("',");
            }
            if(value.toString().endsWith(",")){
                provinceNameStr = value.toString().substring(0,value.toString().lastIndexOf(","));
            }
            sb.append(" and 省份 in (").append(provinceNameStr).append(") ");
        }
		
        if(StringUtils.isNotEmpty(request.getDeptId())){
            sb.append(" and 大类编码 = '").append(request.getDeptId()).append("' ");
        }
        
        return super.queryForList(sb.toString(), TOTAL_PROVINCEDAILYREPORTMODEL_RM,  null);
    }
    
    /**
     * 全司大类汇总
     * @return
     */
    public List<CategoryDailyReportModel> queryEnterpriseCategoryReport(){
    	StringBuilder sb = new StringBuilder(GET_DAILYREPORT_CATEGORY_PREFIX);
    	
    	return super.queryForList(sb.toString(),TOTAL_DAILYREPORT_CATEGORY_RM,null);
    }
    
    /**
     * 查询门店负毛利商品
     * @return
     */
    public List<StoreLossGoodsModel> queryStoreLossGoodsReport(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
			sb.append(  " SELECT " + 
					" X.DEPT as dept, " + 
					" X.ITEM as item, " + 
					" X.ITEMDESC as itemDesc, " + 
					" X.SALETYPEDESC as saleTypeDesc, " + 
					" X.SALEQTY as saleQty, " + 
					" X.GPRATE as gpRate, " + 
					" X.GP gp, " + 
					" X.GROUPLOSS groupLoss " + 
					" FROM " + 
					" ( " + 
					" SELECT " + 
					" DEPT, " + 
					" ITEM, " + 
					" SUBSTR (DG0102, 1, 8) ITEMDESC, " + 
					" SALE_TYPE_DESC SALETYPEDESC, " + 
					" ROUND(SALE_QTY,2) SALEQTY, " + 
					" ROUND(GP_RATE,2) GPRATE, " + 
					" TO_CHAR(ROUND(GP,2),'fm999999990.999999999') GP, " + 
					" TO_CHAR(ROUND(GROSS_LOSS,2),'fm999999990.999999999') GROUPLOSS , " + 
					" ROW_NUMBER() over(order by GROSS_LOSS) RN " + 
					" FROM " + 
					" RPT_NEGATIVE_GP A, " + 
					" DG001 D " + 
					" WHERE " + 
					" A .ITEM = D .DG0101 " + 
					" AND SALE_DATE = TO_CHAR (TRUNC (SYSDATE) - 1,'YYYYMMDD') ");
			if(StringUtils.isNotEmpty(request.getDeptId())){
	            sb.append(" AND DEPT in( ").append(request.getDeptId()).append(") ");
	        }
			sb.append(
					" ORDER BY GROSS_LOSS " + 
	    			" ) X  " + 
	    			" WHERE rn <= 30 ");
	        return super.queryForList(sb.toString(),TOTAL_STORELOSSGOODSMODEL_RM);
	    }
    
    /**
     * 查询门店负毛利商品汇总
     * @return
     */
    public StoreLossGoodsSumResponse queryStoreLossGoodsSum(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
			sb.append("SELECT    " + 
					"  sum(X.SALEQTY) as saleQtySum,     " + 
					"  sum(X.GPRATE) as gpRateSum,     " + 
					"  sum(X.GP) gpSum,     " + 
					"  sum(X.GROUPLOSS) groupLossSum     " + 
					"  FROM     " + 
					"  (     " + 
					"  SELECT     " + 
					"  DEPT,     " + 
					"  ITEM,     " + 
					"  SUBSTR (DG0102, 1, 8) ITEMDESC,     " + 
					"  SALE_TYPE_DESC SALETYPEDESC,     " + 
					"  ROUND(SALE_QTY,2) SALEQTY,     " + 
					"  ROUND(GP_RATE,2) GPRATE,     " + 
					"  TO_CHAR(ROUND(GP,2),'fm999999990.999999999') GP,     " + 
					"  TO_CHAR(ROUND(GROSS_LOSS,2),'fm999999990.999999999') GROUPLOSS ,     " + 
					"  ROW_NUMBER() over(order by GROSS_LOSS) RN     " + 
					"  FROM     " + 
					"  RPT_NEGATIVE_GP A,     " + 
					"  DG001 D     " + 
					"  WHERE     " + 
					"  A .ITEM = D .DG0101     " + 
					"  AND SALE_DATE = TO_CHAR (TRUNC (SYSDATE) - 1,'YYYYMMDD')");
			if(StringUtils.isNotEmpty(request.getDeptId())){
	            sb.append(" AND DEPT in( ").append(request.getDeptId()).append(") ");
	        }
			sb.append("  ORDER BY GROSS_LOSS     " + 
					  "  ) X ");
	        return super.queryForObject(sb.toString(),TOTAL_STORELOSSGOODSSUMMODEL_RM);
	    }
    
    /**
     * 门店负毛利商品下钻明细
     * @return
     */
    public List<StoreLossGoodsDetailModel> queryStoreLossGoodsDetailReport(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
			sb.append(" select /*+parallel(8)*/ " + 
					" to_char(t.saledate,'yyyy-mm-dd') saleDate, " + 
					" t.item item, " + 
					" t.store store, " + 
					" l.STORE_NAME storeName," + 
					" l.REGION region, " + 
					" l.REGION_NAME regionName, " + 
					" l.AREA area,  " + 
					" l.AREA_NAME areaName, " + 
					" max((select im.item_desc from item_master im where im.item = t.item)) itemDesc, " + 
					" round(sum(t.sale_qty),2) saleQty, " + 
					" round(sum(t.sale_value),2)  salesVolume , " + 
					" round(sum(t.sale_value_in - t.sale_cost_in + t.invadj_cost_in + t.fund_amount_in -t.wac_in),2) grossProfit,  " + 
					" round(sum(t.sale_value_in - t.sale_cost_in + t.invadj_cost_in + t.fund_amount_in -t.wac_in) / sum(t.sale_value),2) gp  " + 
					" from cmx_daily_gp t, rms.v_loc_mgr l " + 
					" where 1=1");
			if(StringUtils.isNotEmpty(request.getStoreId())){
	            sb.append(" AND T.STORE ='").append(request.getStoreId()).append("' ");
	        }
			if(StringUtils.isNotEmpty(request.getItem())){
	            sb.append(" AND T.ITEM ='").append(request.getItem()).append("' ");
	        }
			sb.append("and t.store = l.STORE " + 
					"  and t.saledate BETWEEN TRUNC(SYSDATE) - 30 AND TRUNC(SYSDATE) " + 
					"  group by t.item, " + 
					"          t.store, " + 
					"          t.saledate, " + 
					"          l.REGION_NAME, " + 
					"          l.STORE_NAME, " + 
					"          l.AREA, " + 
					"          l.REGION, " + 
					"          l.AREA_NAME " + 
					" having sum(t.sale_value) <> 0 "+
					" order by saleDate desc ");
	        return super.queryForList(sb.toString(),TOTAL_STORELOSSGOODSDETAILMODEL_RM);
	    }
    
    
    /**
     * 单品毛利率卡片
     * @return
     */
    public List<GrossProfitGoodsModel> queryGrossProfitGoodsReport(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
    	sb.append(" select " +
	    	
				"   (select /*+parallel(8)*/ X.gp     " + 
				"   from (select t.item,     " + 
				"   round(sum(t.sale_value_in - t.sale_cost_in + t.invadj_cost_in + t.fund_amount_in - t.wac_in) / sum(t.sale_value),2) gp     " + 
				"   from cmx_daily_gp t     " + 
				"   where t.store = '");sb.append(request.getStoreId()+"' ");sb.append(
				"   and t.item = '");sb.append(request.getItem()+"' ");sb.append(
				"   and t.saledate BETWEEN TRUNC(SYSDATE) - 30 AND TRUNC(SYSDATE)     " + 
				"   group by t.item, t.store having sum(t.sale_value) <> 0) X) storeGp,     " + 
				
				"   (select /*+parallel(8)*/ y.gp     " + 
				"   from (select t.item, l.REGION region,     " + 
				"   round(sum(t.sale_value_in - t.sale_cost_in + t.invadj_cost_in + t.fund_amount_in - t.wac_in) / sum(t.sale_value),2) gp     " + 
				"   from cmx_daily_gp t, rms.v_loc_mgr l     " + 
				"   where l.REGION = (select a.REGION from rms.v_loc_mgr a  where a.STORE = '");sb.append(request.getStoreId()+"') ");sb.append(
				"   and t.item = '");sb.append(request.getItem()+"' ");sb.append(
				"   and t.store = l.STORE     " + 
				"   and t.saledate BETWEEN TRUNC(SYSDATE) - 30 AND TRUNC(SYSDATE)     " + 
				"   group by t.item, l.REGION having sum(t.sale_value) <> 0) Y) areaGp,     " + 
				
				"   (select /*+parallel(8)*/ z.gp     " + 
				"   from (select t.item, l.AREA area,     " + 
				"   round(sum(t.sale_value_in - t.sale_cost_in + t.invadj_cost_in + t.fund_amount_in - t.wac_in) / sum(t.sale_value),2) gp     " + 
				"   from cmx_daily_gp t, rms.v_loc_mgr l     " + 
				"   where l.AREA = (select a.AREA from rms.v_loc_mgr a where a.STORE = '");sb.append(request.getStoreId()+"') ");sb.append(
				"   and t.item = '");sb.append(request.getItem()+"' ");sb.append(
				"   and t.store = l.STORE     " + 
				"   and t.saledate BETWEEN TRUNC(SYSDATE) - 30 AND TRUNC(SYSDATE)     " + 
				"   group by t.item, l.AREA having sum(t.sale_value) <> 0) z) provinceGp,     " + 
				
				"   (select /*+parallel(8)*/ al.gp " +
				"   from (select t.item,     " + 
				"   round(sum(t.sale_value_in - t.sale_cost_in + t.invadj_cost_in + t.fund_amount_in - t.wac_in) / sum(t.sale_value),2) gp     " + 
				"   from cmx_daily_gp t     " + 
				"   where 1 = 1     " + 
				"   and t.item = '");sb.append(request.getItem()+"' ");sb.append(
				"   and t.saledate BETWEEN TRUNC(SYSDATE) - 30 AND TRUNC(SYSDATE) "+
				"   group by t.item having sum(t.sale_value) <> 0)al) allGp     " + 
				
				"   from dual");
	        return super.queryForList(sb.toString(),TOTAL_GROSSPROFITGOODSMODEL_RM);
	    }
    
    /**
     * 查询门店连续负毛利商品
     * @return
     */
    public List<ContinuityLossGoodsModel> queryStoreContinuityLossGoodsReport(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
			sb.append(" SELECT X.dept    dept, " + 
					  " X.item           item, " + 
					  " X.item_Desc      itemDesc, " + 
					  " X.sale_type_desc saleTypeDesc, " + 
					  " X.ct             frequency " + 
					  " FROM (SELECT dept, " + 
					  " item, " + 
					  " SUBSTR(dg0102, 1, 8) item_desc, " + 
					  " sale_type_desc, " + 
					  " COUNT(1) ct " + 
					  " FROM rpt_negative_gp A, dg001 D " + 
					  " WHERE A.item = D.dg0101 ");
			
			if(StringUtils.isNotEmpty(request.getBeginDate())&&StringUtils.isNotEmpty(request.getEndDate())){
	            sb.append(" AND sale_date BETWEEN '").append(request.getBeginDate()).append("' AND '"+request.getEndDate()+"' ");
	        }
			if(StringUtils.isNotEmpty(request.getDeptId())){
				sb.append(" AND DEPT in( ").append(request.getDeptId()).append(") ");
	        }
			sb.append(" GROUP BY item, " +
					  " SUBSTR(dg0102, 1, 8), "+
					  " sale_type_desc, "+
					  " DEPT " + 
					  " ORDER BY CT DESC) X " + 
					  " WHERE ROWNUM <= 30 " );
	        return super.queryForList(sb.toString(),TOTAL_CONTINUITYLOSSGOODSMODEL_RM);
	    }
    
    /**
     * 门店连续负毛利商品下钻
     * @return
     */
    public List<ContinuityLossGoodsDetailModel> queryContinuityLossGoodsDetailReport(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
			sb.append(" SELECT " + 
					"  sale_date saleDate, " + 
					"  sale_qty saleQty, " + 
					"  ROUND(gp_rate, 2) gpRate, " + 
					"  ROUND(gp, 2) grossMargin, " + 
					"  ROUND(gross_loss, 2) grossLoss " + 
					"  FROM rpt_negative_gp a, dg001 d " + 
					"  where a.item = d.dg0101 ");
			if(StringUtils.isNotEmpty(request.getItem())){
				sb.append(" AND a.item = '").append(request.getItem()).append("' ");
	        }
			if(StringUtils.isNotEmpty(request.getBeginDate())&&StringUtils.isNotEmpty(request.getEndDate())){
	            sb.append(" AND sale_date BETWEEN '").append(request.getBeginDate()).append("' AND '"+request.getEndDate()+"' ");
	        }
			sb.append(" order by sale_date desc " );
	        return super.queryForList(sb.toString(),TOTAL_CONTINUITYLOSSGOODSDETAILMODEL_RM);
	    }
    
    /**
     * 门店连续负毛利商品下钻汇总
     * @return
     */
    public ContinuityLossGoodsDetailSumResponse queryCTGoodsSumReport(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
			sb.append("	SELECT     " + 
					"	sum(sale_qty) saleQtySum,     " + 
					"	sum(ROUND(gp_rate, 2)) gpRateSum,     " + 
					"	round(sum(gross_loss) / sum(gp_rate),2)  grossMarginSum,     " + 
					"	sum(ROUND(gross_loss, 2))  grossLossSum     " + 
					"	FROM rpt_negative_gp a, dg001 d     " + 
					"	where a.item = d.dg0101 ");
			if(StringUtils.isNotEmpty(request.getItem())){
				sb.append(" AND a.item = '").append(request.getItem()).append("' ");
	        }
			if(StringUtils.isNotEmpty(request.getBeginDate())&&StringUtils.isNotEmpty(request.getEndDate())){
	            sb.append(" AND sale_date BETWEEN '").append(request.getBeginDate()).append("' AND '"+request.getEndDate()+"' ");
	        }
			sb.append(" order by sale_date desc " );
	        return super.queryForObject(sb.toString(),TOTAL_CONTINUITYLOSSGOODSDETAILSUMRESPONSE_RM);
	    }
    
    /**
     * 门店大类库存金额
     * @return
     */
    public List<StoreLargeClassMoneyModel> queryStoreLargeClassMoney(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
			sb.append(" SELECT dg0104 dept,  " + 
					"    dc0102 deptName,  " + 
					"    round(real_soh_amt,2) realSohAmt,  " + 
					"    round(real_soh_amt / sum_soh_amt, 4) * 100 amtPercent  " + 
					"    FROM (SELECT dg0104,  " + 
					"           (select dc0102 from dc001 where dc0101 = dg0104) dc0102,  " + 
					"           sum(case  " + 
					"                 when dg001.dg0136 in ('01', '02') then  " + 
					"                  (dg001.dg0120 - non_sellable_qty) * av_cost  " + 
					"                 else  " + 
					"                  0  " + 
					"               end) REAL_SOH_amt  " + 
					"      FROM dg001  " + 
					"     where dg0104 != '9101'  " + 
					"     group by dg0104) a,  " + 
					"    (select sum(case when d.dg0136 in ('01', '02') then (d.dg0120 - d.non_sellable_qty) * d.av_cost else 0 end) sum_soh_amt  " + 
					"      from dg001 d  " + 
					"     where dg0104 != '9101') b" );
	        return super.queryForList(sb.toString(),TOTAL_STORELARGECLASSMONEYMODEL_RM);
	    }
    
    /**
     * 门店大类标准周转天数
     * @return
     */
    public List<StoreLargeClassMoneyModel> queryStoreStandardDays(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
			sb.append("  select   " + 
					"  nvl(is_number(T1.STORES_CODE), 0) STORE,  " + 
					"  nvl(is_number(CATEGORY_CODE), 0) DEPT,  " + 
					"  YEAR,  " + 
					"  TO_CHAR(PERIOD, 'FM00') MONTH,  " + 
					"  SCENE,  " + 
					"  ACCOUNTNAME,  " + 
					"  round(AMOUNT,2)standardDays  " + 
					"  from CMX.CMX_HBL_DATA b, CMX.CMX_HBL_ENTITY T1  " + 
					"  where T1.ENTITY_CODE = B.ENTITY_CODE  " + 
					"  AND ACCOUNTNAME = '库存周转天数'  " + 
					"  and scene = '预算'  " + 
					"  AND YEAR = to_char(sysdate,'yyyy')  " + 
					"  AND PERIOD = to_char(sysdate,'MM')  ");
			if(StringUtils.isNotEmpty(request.getStoreId())){
	            sb.append("and nvl(is_number(T1.STORES_CODE), 0)=' ").append(request.getStoreId()).append("' ");
	        }
	        return super.queryForList(sb.toString(),TOTAL_STORELARGECLASSMONEYMODEL_RM);
	 }
    
    /**
     * 门店大类实际周转天数
     * @return
     */
    public List<StoreLargeClassMoneyModel> queryStoreActualDays(DailyReportRequest request){
    	StringBuilder sb = new StringBuilder();
			sb.append(" select a.loc store,  " + 
					" a.dept dept,  " + 
					" sum(a.soh_amt),  " + 
					" sum(a.total_cost),  " + 
					" round(sum(a.soh_amt) /decode(sum(a.total_cost), 0, null, sum(a.total_cost)),4) actualDays  " + 
					" from cmx.BIP_CHYTB_INV_DAYS_TRUNOVER a  " + 
					" where a.vdate >= to_date(trunc(sysdate, 'mm'))");
			if(StringUtils.isNotEmpty(request.getStoreId())){
	            sb.append("and a.loc=' ").append(request.getStoreId()).append("' ");
	        }
			sb.append("group by a.loc, a.dept");
	        return super.queryForList(sb.toString(),TOTAL_STORELARGECLASSMONEYMODEL_RM);
	 }
    
    
}

	
