package com.cs.mobile.api.service.reportPage.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.freshreport.FreshReportCsmbDao;
import com.cs.mobile.api.dao.freshreport.FreshReportZyDao;
import com.cs.mobile.api.dao.reportPage.ReportOrgDao;
import com.cs.mobile.api.dao.reportPage.ReportPageCsmbDao;
import com.cs.mobile.api.dao.reportPage.ReportPageRmsDao;
import com.cs.mobile.api.dao.reportPage.ReportPageZyDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.freshreport.MemberModel;
import com.cs.mobile.api.model.freshreport.OrganizationForFresh;
import com.cs.mobile.api.model.freshreport.TurnoverDay;
import com.cs.mobile.api.model.freshreport.response.MemberDetailResponse;
import com.cs.mobile.api.model.freshreport.response.MemberListResponse;
import com.cs.mobile.api.model.freshreport.response.TurnoverDayResponse;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.reportPage.CategoryKlModel;
import com.cs.mobile.api.model.reportPage.CsmbStoreModel;
import com.cs.mobile.api.model.reportPage.Fresh;
import com.cs.mobile.api.model.reportPage.HomeAppliance;
import com.cs.mobile.api.model.reportPage.MemberPermeabilityPo;
import com.cs.mobile.api.model.reportPage.RankDetail;
import com.cs.mobile.api.model.reportPage.ReportDetail;
import com.cs.mobile.api.model.reportPage.TotalSalesAndProfit;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
import com.cs.mobile.api.model.reportPage.request.RankParamRequest;
import com.cs.mobile.api.model.reportPage.request.ReportParamRequest;
import com.cs.mobile.api.model.reportPage.response.FreshResponse;
import com.cs.mobile.api.model.reportPage.response.HomeApplianceResponse;
import com.cs.mobile.api.model.reportPage.response.MemberPermeabilityResponse;
import com.cs.mobile.api.model.reportPage.response.NotFreshResponse;
import com.cs.mobile.api.model.reportPage.response.RankDetailResponse;
import com.cs.mobile.api.model.reportPage.response.RankInfoResponse;
import com.cs.mobile.api.model.reportPage.response.ReportDataResponse;
import com.cs.mobile.api.model.reportPage.response.ReportDetailResponse;
import com.cs.mobile.api.model.reportPage.response.TotalSalesAndProfitResponse;
import com.cs.mobile.api.service.common.CommonCalculateService;
import com.cs.mobile.api.service.common.CommonService;
import com.cs.mobile.api.service.reportPage.ReportPageService;
import com.cs.mobile.api.service.reportPage.ReportUserDeptService;
import com.cs.mobile.common.core.text.Convert;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtil;
import com.cs.mobile.common.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ReportPageServiceImpl implements ReportPageService {
    @Value("${store.db.zyppdb.userName}")
    private String zyuserName;
    @Value("${store.db.zyppdb.password}")
    private String zypassword;
    @Value("${store.db.zyppdb.sid}")
    private String zysid;
    @Value("${store.db.zyppdb.host}")
    private String zyhost;
    @Value("${store.db.zyppdb.port}")
    private String zyport;
    @Value("${store.db.rmsdg.userName}")
    private String dguserName;
    @Value("${store.db.rmsdg.password}")
    private String dgpassword;
    @Value("${store.db.rmsdg.sid}")
    private String dgsid;
    @Value("${store.db.rmsdg.host}")
    private String dghost;
    @Value("${store.db.rmsdg.port}")
    private String dgport;
    @Autowired
    DruidProperties druidProperties;
    @Autowired
    private ReportPageCsmbDao csmb;
    @Autowired
    private ReportPageRmsDao dg;
    @Autowired
    private ReportPageZyDao zy;
    @Autowired
    private ReportOrgDao reportOrgDao;
    @Autowired
    private ReportUserDeptService reportUserDeptService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private FreshReportCsmbDao freshReportCsmbDao;
    @Autowired
    private FreshReportZyDao freshReportZyDao;
    @Autowired
    private CommonCalculateService calculateService;
    
    /**
     * 查询总销售和总毛利率模块
     * @param pageRequest
     * @return
     */
    @Override
    public TotalSalesAndProfitResponse queryTotalSalesAndProfit(PageRequest pageRequest) throws Exception {
        TotalSalesAndProfit totalSalesAndProfit = new TotalSalesAndProfit();
        try{
            TotalSalesAndProfit his = new TotalSalesAndProfit();
            TotalSalesAndProfit now = new TotalSalesAndProfit();
            transform(pageRequest);
            Date start = pageRequest.getStart();
            Date end = pageRequest.getEnd();
            // 查询实时数据
            if((null == pageRequest.getEnd()  && DateUtils.getBetweenDay(pageRequest.getStart(), new Date()) >= 0)
                    || (null != pageRequest.getEnd() && DateUtils.getBetweenDay(pageRequest.getEnd(), new Date()) >= 0)){
                //当天数据
                if(null == pageRequest.getDeptIds() || pageRequest.getDeptIds().size() == 0){
                    now = new TotalSalesAndProfit();
                    //查询不包含大类的数据
                    //查询客流
                    TotalSalesAndProfit kl = csmb.currentSingleKl(pageRequest);
                    //查询销售额，毛利额，可比销售额，可比毛利额,毛利率，扫描毛利率
                    TotalSalesAndProfit gp = csmb.currentTotalGpAndAmtNotDept(pageRequest);
                    BeanUtils.copyProperties(gp,now);
                    now.setCustomerNum(kl.getCustomerNum());
                    now.setTotalCompareRate(now.getTotalCompareScanningRate());
                    now.setTotalCompareRateIn(now.getTotalCompareScanningRateIn());
                }else {
                    //查询包含大类的销售额，毛利额，可比销售额，可比毛利额,毛利率，扫描毛利率
                    now = csmb.currentTotalGpAndAmtHaveDept(pageRequest);
                    if(null != now){
                        now.setTotalCompareRate(now.getTotalCompareScanningRate());
                        now.setTotalCompareRateIn(now.getTotalCompareScanningRateIn());
                    }
                }
            }
            //查询库存数量
            TotalSalesAndProfit stockNum = csmb.getStockNum(pageRequest);
            if(null != pageRequest.getEnd() && null != stockNum){
                stockNum.setStockNum("0");
            }
            if((null == pageRequest.getEnd()  && DateUtils.getBetweenDay(pageRequest.getStart(), new Date()) < 0)
                    || null != pageRequest.getEnd()){

                TotalSalesAndProfit kl = null;
                try{
                    changeZyDataSource();
                    //查询历史客流数据
                    kl = zy.queryHisTotalData(pageRequest, "", -1);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                //查询历史前台毛利额，历史销售额，历史成本，历史可比销售额，历史可比前台毛利额，历史可比成本
                try{
                    changeDgDataSource();
                    his = dg.queryHisCommenData(pageRequest);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                if(null != kl){
                    //计算客流
                    his.setCustomerNum(kl.getCustomerNum());
                }
                if(null != his){//计算历史扫描毛利率,扫描毛利额
                    BigDecimal totalSale = new BigDecimal(StringUtils.isEmpty(his.getTotalSales())?"0": his.getTotalSales());
                    BigDecimal totalCost = new BigDecimal(StringUtils.isEmpty(his.getTotalCost())?"0":his.getTotalCost());
                    BigDecimal totalCompareSale = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareSale())?"0": his.getTotalCompareSale());
                    BigDecimal totalCompareCost = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareCost())?"0": his.getTotalCompareCost());

                    BigDecimal totalSaleIn = new BigDecimal(StringUtils.isEmpty(his.getTotalSalesIn())?"0": his.getTotalSalesIn());
                    BigDecimal totalCostIn = new BigDecimal(StringUtils.isEmpty(his.getTotalCostIn())?"0":his.getTotalCostIn());
                    BigDecimal totalCompareSaleIn = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareSaleIn())?"0": his.getTotalCompareSaleIn());
                    BigDecimal totalCompareCostIn = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareCostIn())?"0": his.getTotalCompareCostIn());
                    if(totalSale.compareTo(BigDecimal.ZERO) != 0){
                        //历史扫描毛利率
                        his.setTotalProfit((totalSale.subtract(totalCost)).divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                        //历史扫描毛利率
                        his.setScanningProfitRate((totalSale.subtract(totalCost)).divide(totalSale,4, BigDecimal.ROUND_HALF_UP).toString());
                    }
                    if(totalSaleIn.compareTo(BigDecimal.ZERO) != 0){
                        //历史扫描毛利率
                        his.setTotalProfitIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                        //历史扫描毛利率
                        his.setScanningProfitRateIn((totalSaleIn.subtract(totalCostIn)).divide(totalSaleIn,4, BigDecimal.ROUND_HALF_UP).toString());
                    }
                    //历史扫描毛利额
                    his.setTotalRate(totalSale.subtract(totalCost).toString());
                    //历史可比扫描毛利额
                    his.setTotalCompareScanningRate(totalCompareSale.subtract(totalCompareCost).toString());
                    //历史扫描毛利额
                    his.setTotalRateIn(totalSaleIn.subtract(totalCostIn).toString());
                    //历史可比扫描毛利额
                    his.setTotalCompareScanningRateIn(totalCompareSaleIn.subtract(totalCompareCostIn).toString());
                }
            }
            //查询库存金额和周转天数
            TotalSalesAndProfit stockPriceAndDayInfo = new TotalSalesAndProfit();

            try{
                //切换数据
                changeDgDataSource();
                //查询库存金额和周转天数
                stockPriceAndDayInfo = dg.getStockPriceAndDay(pageRequest);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != pageRequest.getEnd() && null != stockPriceAndDayInfo){
                stockPriceAndDayInfo.setStockPrice("0");
            }

            String hh = DateUtils.parseDateToStr("HH",new Date());
            //查询同期
            TotalSalesAndProfit sameInfo = querySameInfo(pageRequest,hh);
            //组装可比
            pageRequest.setStart(start);
            pageRequest.setEnd(end);
            TotalSalesAndProfit compareInfo = queryCompareInfo(pageRequest,hh);
            totalSalesAndProfit = buildResult(his, now, stockPriceAndDayInfo, stockNum, sameInfo, compareInfo);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        TotalSalesAndProfitResponse response = new TotalSalesAndProfitResponse();
        BeanUtils.copyProperties(totalSalesAndProfit, response);
        return response;
    }

    /**
     * 查询家电模块
     * @param pageRequest
     * @return
     */
    @Override
    public HomeApplianceResponse queryHomeAppliance(PageRequest pageRequest){
        transform(pageRequest);
        HomeAppliance homeAppliance = csmb.queryHomeApplianceInfo(pageRequest);
        HomeApplianceResponse response = new HomeApplianceResponse();
        BeanUtils.copyProperties(homeAppliance, response);
        return response;
    }

    /**
     * 查询生鲜模块
     * @param pageRequest
     * @return
     */
    @Override
    public FreshResponse queryFresh(PageRequest pageRequest) throws Exception {
        Fresh fresh = null;
        transform(pageRequest);
        //获取生鲜大类
        getFreshList(pageRequest);
        if(null == pageRequest.getDeptIds() || pageRequest.getDeptIds().size() <= 0){
            FreshResponse freshResponse = new FreshResponse();
            freshResponse.setHaveFresh("0");
            return freshResponse;
        }
        if(null == pageRequest.getEnd()){//查询时间点的数据
            if(DateUtils.getBetweenDay(pageRequest.getStart(), new Date()) < 0){
                fresh = queryHistoryFresh(pageRequest,"",1);
            }else{
                fresh = queryCurrentFreshForDay(pageRequest,"",1);
            }
        }else{//查询时间段数据
            if(DateUtils.getBetweenDay(pageRequest.getEnd(), new Date()) < 0){
                fresh = queryHistoryFresh(pageRequest,"",1);
            }else{//查询当前加历史数据总和
                fresh = queryHistoryFreshHaveCurrentDay(pageRequest,"",1);
            }
        }
        FreshResponse response = new FreshResponse();
        if(null != fresh){
            BeanUtils.copyProperties(fresh, response);
        }
        return response;
    }

    /**
     * 查询非生鲜模块
     * @param pageRequest
     * @return
     */
    @Override
    public NotFreshResponse queryNotFresh(PageRequest pageRequest) throws Exception {
        Fresh fresh = new Fresh();
        transform(pageRequest);
        NotFreshResponse response = new NotFreshResponse();
        if(!haveNotFresh(pageRequest)){
            response.setHaveNotFresh("0");
            return response;
        }
        String freshStr = getAllFresh();
        try{
            if(null == pageRequest.getEnd()){//查询时间点的数据
                if(DateUtils.getBetweenDay(pageRequest.getStart(), new Date()) < 0){
                    fresh = queryHistoryFresh(pageRequest,freshStr,2);
                }else{
                    fresh = queryCurrentFreshForDay(pageRequest,freshStr,2);
                }
            }else{//查询时间段数据
                if(DateUtils.getBetweenDay(pageRequest.getEnd(), new Date()) < 0){
                    fresh = queryHistoryFresh(pageRequest,freshStr,2);
                }else{//查询当前加历史数据总和
                    fresh = queryHistoryFreshHaveCurrentDay(pageRequest,freshStr,2);
                }
            }
            if(null != fresh){
                BeanUtils.copyProperties(fresh, response);
            }
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        return response;
    }

    /**
     * 查询时线，日线，月线模块
     * @param param
     * @return
     */
    @Override
    public ReportDataResponse queryReportData(ReportParamRequest param) {
        ReportDataResponse reportDataResponse = new ReportDataResponse();
        transform(param);
        List<ReportDetail> cur = new ArrayList<>();
        //如果查询某个时间点，时线查询当天，日线查询月初到时间点，月线查询年初到时间点
        if(null == param.getEnd()){
            if(2 == param.getType()){
                Date date = param.getStart();
                param.setStart(DateUtil.getFirstDayForMonth(date));
                param.setEnd(date);
            }else if(3 == param.getType()){
                Date date = param.getStart();
                param.setStart(DateUtil.getFirstDayForYear(date));
                param.setEnd(date);
            }
        }else{
            //如果是时间段只能查询日线
            param.setType(2);
        }

        try{
            if(null == param.getEnd()){//查询时间点的数据
                if(DateUtils.getBetweenDay(param.getStart(), new Date()) < 0){
                    //历史数据
                    //查询历史时线,日线,月线
                    if(2 == param.getType()){
                        cur = csmb.queryHistoryReportDetail(param,param.getStart(),param.getEnd(),2);
                        if(null != cur && cur.size() > 0){
                            for(ReportDetail reportDetail : cur){
                                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                                BigDecimal rate = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRate())?"0"
                                        :reportDetail.getTotalFrontDeskRate());
                                BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRateIn())?"0"
                                        :reportDetail.getTotalFrontDeskRateIn());
                                if(sale.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                                if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                            }
                        }
                    }else if(3 == param.getType()){
                        cur = csmb.queryHistoryReportDetail(param,param.getStart(),param.getEnd(),3);
                        if(null != cur && cur.size() > 0){
                            for(ReportDetail reportDetail : cur){
                                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                                BigDecimal rate = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRate())?"0"
                                        :reportDetail.getTotalFrontDeskRate());
                                BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRateIn())?"0"
                                        :reportDetail.getTotalFrontDeskRateIn());
                                if(sale.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                                if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                            }
                        }
                    }else if(1 == param.getType()){
                        try{
                            changeZyDataSource();
                            cur = zy.queryHisReportDetailForHour(param);
                            if(null != cur && cur.size() > 0){
                                for(ReportDetail reportDetail : cur){
                                    BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                                    BigDecimal cost = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalCost())?"0":reportDetail.getTotalCost());
                                    BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                                    BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalCostIn())?"0":reportDetail.getTotalCostIn());
                                    if(sale.compareTo(BigDecimal.ZERO) != 0){
                                        reportDetail.setTotalprofit(sale.subtract(cost).divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                                    }
                                    if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                                        reportDetail.setTotalprofitIn(saleIn.subtract(costIn).divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                                    }
                                }
                            }
                        }catch (Exception e){
                            throw e;
                        }finally {
                            DataSourceHolder.clearDataSource();
                        }
                    }
                }else{
                    //当天数据
                    if(null == param.getDeptIds() || param.getDeptIds().size() == 0){
                        //查询当前日线，时线，月线（不包含大类）
                        cur = csmb.currentSingleReportDetail(param);
                    }else {
                        //查询当前日线，时线，月线（包含大类）
                        cur = csmb.currentReportDetailByDept(param);
                    }
                }
            }else{//查询时间段数据
                if(DateUtils.getBetweenDay(param.getEnd(), new Date()) < 0){
                    //历史数据
                    //查询历史日线，时线，月线
                    if(2 == param.getType()){
                        cur = csmb.queryHistoryReportDetail(param,param.getStart(),param.getEnd(),2);
                        if(null != cur && cur.size() > 0){
                            for(ReportDetail reportDetail : cur){
                                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                                BigDecimal rate = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRate())?"0"
                                        :reportDetail.getTotalFrontDeskRate());
                                BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRateIn())?"0"
                                        :reportDetail.getTotalFrontDeskRateIn());
                                if(sale.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                                if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                            }
                        }
                    }else if(3 == param.getType()){
                        cur = csmb.queryHistoryReportDetail(param,param.getStart(),param.getEnd(),3);
                        if(null != cur && cur.size() > 0){
                            for(ReportDetail reportDetail : cur){
                                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                                BigDecimal rate = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRate())?"0"
                                        :reportDetail.getTotalFrontDeskRate());
                                BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRateIn())?"0"
                                        :reportDetail.getTotalFrontDeskRateIn());
                                if(sale.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                                if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                            }
                        }
                    }else if(1 == param.getType()){
                        try{
                            changeZyDataSource();
                            cur = zy.queryHisReportDetailForHour(param);
                            if(null != cur && cur.size() > 0){
                                for(ReportDetail reportDetail : cur){
                                    BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                                    BigDecimal cost = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalCost())?"0":reportDetail.getTotalCost());
                                    BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                                    BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalCostIn())?"0":reportDetail.getTotalCostIn());
                                    if(sale.compareTo(BigDecimal.ZERO) != 0){
                                        reportDetail.setTotalprofit(sale.subtract(cost).divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                                    }
                                    if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                                        reportDetail.setTotalprofitIn(saleIn.subtract(costIn).divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                                    }
                                }
                            }
                        }catch (Exception e){
                            throw e;
                        }finally {
                            DataSourceHolder.clearDataSource();
                        }
                    }
                }else{//查询当前加历史数据总和
                    //历史数据
                    //查询历史日线，时线，月线
                    cur = new ArrayList<ReportDetail>();

                    List<ReportDetail> res = null;
                    if(null == param.getDeptIds() || param.getDeptIds().size() == 0){
                        //查询当前日线，时线，月线（不包含大类）
                        res = csmb.currentSingleReportDetail(param);
                    }else {
                        //查询当前日线，时线，月线（包含大类）
                        res = csmb.currentReportDetailByDept(param);
                    }

                    List<ReportDetail> hisRes = null;
                    if(2 == param.getType()){
                        hisRes = csmb.queryHistoryReportDetail(param,param.getStart(),param.getEnd(),2);
                        if(null != hisRes && hisRes.size() > 0){
                            for(ReportDetail reportDetail : hisRes){
                                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                                BigDecimal rate = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRate())?"0"
                                        :reportDetail.getTotalFrontDeskRate());
                                BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRateIn())?"0"
                                        :reportDetail.getTotalFrontDeskRateIn());
                                if(sale.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                                if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                            }
                        }
                    }else if(3 == param.getType()){
                        hisRes = csmb.queryHistoryReportDetail(param,param.getStart(),param.getEnd(),3);
                        if(null != hisRes && hisRes.size() > 0){
                            for(ReportDetail reportDetail : hisRes){
                                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                                BigDecimal rate = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRate())?"0"
                                        :reportDetail.getTotalFrontDeskRate());
                                BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRateIn())?"0"
                                        :reportDetail.getTotalFrontDeskRateIn());
                                if(sale.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                                if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                                    reportDetail.setTotalprofitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                                }
                            }
                        }
                    }else if(1 == param.getType()){
                        try{
                            changeZyDataSource();
                            hisRes = zy.queryHisReportDetailForHour(param);
                            if(null != hisRes && hisRes.size() > 0){
                                for(ReportDetail reportDetail : hisRes){
                                    BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                                    BigDecimal cost = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalCost())?"0":reportDetail.getTotalCost());
                                    BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                                    BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalCostIn())?"0":reportDetail.getTotalCostIn());
                                    if(sale.compareTo(BigDecimal.ZERO) != 0){
                                        reportDetail.setTotalprofit(sale.subtract(cost).divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                                    }
                                    if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                                        reportDetail.setTotalprofitIn(saleIn.subtract(costIn).divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                                    }
                                }
                            }
                        }catch (Exception e){
                            throw e;
                        }finally {
                            DataSourceHolder.clearDataSource();
                        }
                    }

                    if(null != res && res.size() > 0){
                        cur.addAll(res);
                    }

                    if(null != hisRes && hisRes.size() > 0){
                        cur.addAll(hisRes);
                    }
                }
            }
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        Date startDate = param.getStart();
        Date endDate = param.getEnd();

        //组装数据，1-时线,2-日线,3-月线
        if(cur != null && cur.size() > 0){
            BigDecimal max = BigDecimal.ZERO;

            if(1 == param.getType()){
                max = new BigDecimal(DateUtils.parseDateToStr("HH",new Date()));
                if(DateUtils.getBetweenDay(startDate,new Date()) < 0){
                    max = new BigDecimal("23");
                }
            }else if(3 == param.getType()){
                max = new BigDecimal(DateUtils.parseDateToStr("MM",endDate));
            }
            //填充没有的时间点数据
            cur = buildReportReportDetailResponseList(cur, param.getType(), max.intValue(), param.getStart(), param.getEnd());
            //排序
            if(2 == param.getType()){
                cur = cur.stream().sorted(new Comparator<ReportDetail>() {
                    @Override
                    public int compare(ReportDetail o1, ReportDetail o2) {
                        BigDecimal value1 = new BigDecimal(o1.getTime());
                        BigDecimal value2 = new BigDecimal(o2.getTime());
                        return value1.compareTo(value2);
                    }
                }).collect(Collectors.toList());
            }else{
                cur = cur.stream().sorted(new Comparator<ReportDetail>() {
                    @Override
                    public int compare(ReportDetail o1, ReportDetail o2) {
                        BigDecimal value1 = new BigDecimal(o1.getTimePoint());
                        BigDecimal value2 = new BigDecimal(o2.getTimePoint());
                        return value1.compareTo(value2);
                    }
                }).collect(Collectors.toList());
            }

            List<ReportDetailResponse> curList = new ArrayList<>();
            for(ReportDetail reportDetail : cur){

                ReportDetailResponse reportDetailResponse = new ReportDetailResponse();
                BeanUtils.copyProperties(reportDetail, reportDetailResponse);
                curList.add(reportDetailResponse);
            }
            reportDataResponse.setCurrent(curList);
        }
        return reportDataResponse;
    }

    /**
     * 查询同期时线，日线，月线模块
     * @param param
     * @return
     */
    @Override
    public ReportDataResponse querySameReportData(ReportParamRequest param) throws Exception {
        ReportDataResponse reportDataResponse = new ReportDataResponse();
        transform(param);
        List<ReportDetail> same = new ArrayList<>();
        //如果查询某个时间点，时线查询当天，日线查询月初到时间点，月线查询年初到时间点
        if(null == param.getEnd()){
            if(2 == param.getType()){
                Date date = param.getStart();
                param.setStart(DateUtil.getFirstDayForMonth(date));
                param.setEnd(date);
            }else if(3 == param.getType()){
                Date date = param.getStart();
                param.setStart(DateUtil.getFirstDayForYear(date));
                param.setEnd(date);
            }
        }else{
            //如果是时间段只能查询日线
            param.setType(2);
        }
        Date startDate = param.getStart();
        Date endDate = param.getEnd();
        same = querySameReportDetail(param);
        //组装数据，1-时线,2-日线,3-月线
        if(same != null && same.size() > 0){
            BigDecimal max = BigDecimal.ZERO;

            if(1 == param.getType()){
                max = new BigDecimal(DateUtils.parseDateToStr("HH",new Date()));
                if(DateUtils.getBetweenDay(startDate,new Date()) < 0){
                    max = new BigDecimal("23");
                }
            }else if(3 == param.getType()){
                max = new BigDecimal(DateUtils.parseDateToStr("MM",endDate));
            }
            //填充没有的时间点数据
            same = buildReportReportDetailResponseList(same, param.getType(), max.intValue(), param.getStart(), param.getEnd());
            //排序
            if(2 == param.getType()){
                same = same.stream().sorted(new Comparator<ReportDetail>() {
                    @Override
                    public int compare(ReportDetail o1, ReportDetail o2) {
                        BigDecimal value1 = new BigDecimal(o1.getTime());
                        BigDecimal value2 = new BigDecimal(o2.getTime());
                        return value1.compareTo(value2);
                    }
                }).collect(Collectors.toList());
            }else{
                same = same.stream().sorted(new Comparator<ReportDetail>() {
                    @Override
                    public int compare(ReportDetail o1, ReportDetail o2) {
                        BigDecimal value1 = new BigDecimal(o1.getTimePoint());
                        BigDecimal value2 = new BigDecimal(o2.getTimePoint());
                        return value1.compareTo(value2);
                    }
                }).collect(Collectors.toList());
            }

            List<ReportDetailResponse> sameList = new ArrayList<>();
            for(ReportDetail reportDetail : same){
                ReportDetailResponse reportDetailResponse = new ReportDetailResponse();
                BeanUtils.copyProperties(reportDetail, reportDetailResponse);
                sameList.add(reportDetailResponse);
            }
            reportDataResponse.setSame(sameList);
        }
        return reportDataResponse;
    }

    /**
     * 查询排行榜模块
     * @param rankParamRequest
     * @return
     */
    @Override
    public RankInfoResponse queryRankInfo(RankParamRequest rankParamRequest) throws Exception{
        RankInfoResponse response = new RankInfoResponse();
        transform(rankParamRequest);
        Date start = rankParamRequest.getStart();
        Date end = rankParamRequest.getEnd();
        //0-全司,1-省份,2-区域,3-门店
        int grade = rankParamRequest.getGrade();
        RankInfoResponse categoryResponse = queryCategoryRankCount(rankParamRequest);
        if(null != categoryResponse){
            response.setDeptSaleUpNum(categoryResponse.getDeptSaleUpNum());
            response.setDeptSaleDownNum(categoryResponse.getDeptSaleDownNum());
            response.setDeptRateUpNum(categoryResponse.getDeptRateUpNum());
            response.setDeptRateDownNum(categoryResponse.getDeptRateDownNum());
            response.setDeptSaleUpNumIn(categoryResponse.getDeptSaleUpNumIn());
            response.setDeptSaleDownNumIn(categoryResponse.getDeptSaleDownNumIn());
            response.setDeptRateUpNumIn(categoryResponse.getDeptRateUpNumIn());
            response.setDeptRateDownNumIn(categoryResponse.getDeptRateDownNumIn());
        }
        rankParamRequest.setStart(start);
        rankParamRequest.setEnd(end);
        if(grade == 2){
            RankInfoResponse storeResponse = queryStoreRankCount(rankParamRequest);
            if(null != storeResponse){
                response.setStoreSaleUpNum(storeResponse.getStoreSaleUpNum());
                response.setStoreSaleDownNum(storeResponse.getStoreSaleDownNum());
                response.setStoreRateUpNum(storeResponse.getStoreRateUpNum());
                response.setStoreRateDownNum(storeResponse.getStoreRateDownNum());
                response.setStoreSaleUpNumIn(storeResponse.getStoreSaleUpNumIn());
                response.setStoreSaleDownNumIn(storeResponse.getStoreSaleDownNumIn());
                response.setStoreRateUpNumIn(storeResponse.getStoreRateUpNumIn());
                response.setStoreRateDownNumIn(storeResponse.getStoreRateDownNumIn());
            }
        }else if(grade < 2){
            RankInfoResponse storeResponse = queryStoreRankCount(rankParamRequest);
            if(null != storeResponse){
                response.setStoreSaleUpNum(storeResponse.getStoreSaleUpNum());
                response.setStoreSaleDownNum(storeResponse.getStoreSaleDownNum());
                response.setStoreRateUpNum(storeResponse.getStoreRateUpNum());
                response.setStoreRateDownNum(storeResponse.getStoreRateDownNum());
                response.setStoreSaleUpNumIn(storeResponse.getStoreSaleUpNumIn());
                response.setStoreSaleDownNumIn(storeResponse.getStoreSaleDownNumIn());
                response.setStoreRateUpNumIn(storeResponse.getStoreRateUpNumIn());
                response.setStoreRateDownNumIn(storeResponse.getStoreRateDownNumIn());
            }
            rankParamRequest.setStart(start);
            rankParamRequest.setEnd(end);
            RankInfoResponse areaResponse = queryAreaRankCount(rankParamRequest);
            if(null != areaResponse){
                response.setAreaSaleUpNum(areaResponse.getAreaSaleUpNum());
                response.setAreaSaleDownNum(areaResponse.getAreaSaleDownNum());
                response.setAreaRateUpNum(areaResponse.getAreaRateUpNum());
                response.setAreaRateDownNum(areaResponse.getAreaRateDownNum());
                response.setAreaSaleUpNumIn(areaResponse.getAreaSaleUpNumIn());
                response.setAreaSaleDownNumIn(areaResponse.getAreaSaleDownNumIn());
                response.setAreaRateUpNumIn(areaResponse.getAreaRateUpNumIn());
                response.setAreaRateDownNumIn(areaResponse.getAreaRateDownNumIn());
            }
        }
        return response;
    }

    /**
     * 查询排行榜列表
     * @param rankParamRequest
     * @return
     */
    @Override
    public RankInfoResponse queryRankList(RankParamRequest rankParamRequest) throws Exception {
        RankInfoResponse response = new RankInfoResponse();
        transform(rankParamRequest);
        //查询类型（1-门店，2-品类，3-区域,4-大类）
        int type = rankParamRequest.getType();
        //用户权限（0-全司,1-省份,2-区域,3-门店）
        int grade = rankParamRequest.getGrade();
        if(2 >= grade){
            if(1 == type){
                response = queryStoreRankList(rankParamRequest);
                response.setMark(1);
            }else if(3 == type){
                response = queryAreaRankList(rankParamRequest);
                response.setMark(3);
            }
        }else if(2 < grade){
            if(1 == type || 3 == type){
                response = queryStoreRankList(rankParamRequest);
                response.setMark(1);
            }
        }

        if(2 == type){
            response = queryCategoryRankList(rankParamRequest);
            response.setMark(2);
        }else if(4 == type){
            response = queryDeptRankList(rankParamRequest);
            response.setMark(4);
        }
        return response;
    }


    /**
     * 查询会员渗透率
     * @param param
     * @return
     */
    @Override
    public MemberPermeabilityResponse queryMember(PageRequest param) throws Exception {
        transform(param);
        MemberPermeabilityResponse memberPermeabilityResponse = new MemberPermeabilityResponse();
        List<MemberPermeabilityPo> po = new ArrayList<>();
        if((param.getEnd() == null && DateUtils.getBetweenDay(param.getStart(), new Date()) < 0)
                || (null != param.getEnd() && DateUtils.getBetweenDay(param.getEnd(), new Date()) < 0) ){
            try{
                //历史数据
                changeZyDataSource();
                po = zy.queryHisMember(param);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else{
            po = csmb.queryMember(param);
        }
        //计算会员数量
        if(null != po){
            BigDecimal count = new BigDecimal("0");
            for(MemberPermeabilityPo mm : po){
                if(mm.getVipMark().toLowerCase().equals("y")){
                    count = count.add(mm.getCount());
                }
            }
            memberPermeabilityResponse.setMember(count.toString());
        }

        //计算非会员数量
        if(null != po){
            BigDecimal count = new BigDecimal("0");
            for(MemberPermeabilityPo mm : po){
                if(mm.getVipMark().toLowerCase().equals("n")){
                    count = count.add(mm.getCount());
                }
            }
            memberPermeabilityResponse.setNoMember(count.toString());
        }

        //计算会员线上数量
        if(null != po){
            BigDecimal count = new BigDecimal("0");
            for(MemberPermeabilityPo mm : po){
                if(mm.getVipMark().toLowerCase().equals("y") && mm.getOnlineMark().toLowerCase().equals("on")){
                    count = count.add(mm.getCount());
                }
            }
            memberPermeabilityResponse.setOnline(count.toString());
        }

        //计算会员线下数量
        if(null != po){
            BigDecimal count = new BigDecimal("0");
            for(MemberPermeabilityPo mm : po){
                if(mm.getVipMark().toLowerCase().equals("y") && mm.getOnlineMark().toLowerCase().equals("off")){
                    count = count.add(mm.getCount());
                }
            }
            memberPermeabilityResponse.setOffline(count.toString());
        }
        //查询明细
        MemberListResponse memberListResponse = queryMemberDetail(param);
        if(null != memberListResponse){
            memberPermeabilityResponse.setOnlineList(memberListResponse.getOnline());
            memberPermeabilityResponse.setOfflineList(memberListResponse.getOffline());
        }
        return memberPermeabilityResponse;
    }

    /**
     * 查询会员渗透率明细
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public MemberListResponse queryMemberDetail(PageRequest param) throws Exception {
        transform(param);
        MemberListResponse memberListResponse = new MemberListResponse();
        List<MemberDetailResponse> online = new ArrayList<>();
        List<MemberDetailResponse> offline = new ArrayList<>();
        List<MemberModel> allList = new ArrayList<>();
        Date start = param.getStart();
        Date end = param.getEnd();
        if(null == end){
            if(DateUtils.getBetweenDay(start, new Date()) >= 0){
                allList = freshReportCsmbDao.queryCurMemberDetail(param,start,null);
            }else{
                try{
                    changeZyDataSource();
                    //为了提高查询性能，把包含大类条件和不包含大类条件的分开查询
                    if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                        allList = freshReportZyDao.queryHisMemberDetailHaveDept(param,start,null);
                    }else{
                        allList = freshReportZyDao.queryHisMemberDetailNotDept(param,start,null);
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else{
            if(DateUtils.getBetweenDay(end, new Date()) >= 0){
                List<MemberModel> current = freshReportCsmbDao.queryCurMemberDetail(param,end,null);
                List<MemberModel> history = null;
                try{
                    changeZyDataSource();
                    //查询起始时间到昨日的数据
                    //为了提高查询性能，把包含大类条件和不包含大类条件的分开查询
                    if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                        history = freshReportZyDao.queryHisMemberDetailHaveDept(param,start,DateUtils.addDays(end,-1));
                    }else{
                        history = freshReportZyDao.queryHisMemberDetailNotDept(param,start,DateUtils.addDays(end,-1));
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                //合并数据
                if(null != current && current.size() > 0){
                    allList.addAll(current);
                }
                if(null != history && history.size() > 0){
                    for(MemberModel his : history){
                        boolean flag = true;
                        for(MemberModel all : allList){
                            if((null != his.getChannel() && his.getChannel().equals(all.getChannel()))
                                    || (null == his.getChannel() && null == all.getChannel())){
                                int hisCount = Integer.valueOf(his.getCount()).intValue();
                                int allCount = Integer.valueOf(all.getCount()).intValue();
                                all.setCount(hisCount + allCount + "");
                                flag = false;
                            }
                        }
                        if(flag){
                            allList.add(his);
                        }
                    }
                }
            }else{
                try{
                    changeZyDataSource();
                    //为了提高查询性能，把包含大类条件和不包含大类条件的分开查询
                    if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                        allList = freshReportZyDao.queryHisMemberDetailHaveDept(param,start,end);
                    }else{
                        allList = freshReportZyDao.queryHisMemberDetailNotDept(param,start,end);
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }
        //汇总线上和线下
        if(null != allList && allList.size() > 0){
            //获取线下标识
            List<String> offList = setOfflineMark();
            for(MemberModel memberModel : allList){
                MemberDetailResponse memberDetailResponse = new MemberDetailResponse();
                //暂时只汇总会员的线上和线下明细
                if("y".equalsIgnoreCase(memberModel.getVipMark())){//会员
                    boolean flag = true;
                    for(String off : offList){//线下会员数量
                        if(StringUtils.isEmpty(memberModel.getChannel()) || off.equalsIgnoreCase(memberModel.getChannel())){
                            if("BKSLS".equalsIgnoreCase(memberModel.getChannel())){
                                memberModel.setChannelName("正常");
                            }
                            BeanUtils.copyProperties(memberModel,memberDetailResponse);
                            offline.add(memberDetailResponse);
                            flag = false;
                            break;
                        }
                    }
                    if(flag){//线上会员数量
                        BeanUtils.copyProperties(memberModel,memberDetailResponse);
                        online.add(memberDetailResponse);
                    }
                }
            }
        }
        memberListResponse.setOnline(online);
        memberListResponse.setOffline(offline);
        return memberListResponse;
    }

    /**
     * 查询实际汇总周转天数
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public TurnoverDayResponse queryActualTurnoverDay(PageRequest param) throws Exception {
        TurnoverDayResponse turnoverDayResponse = new TurnoverDayResponse();
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        TurnoverDay turnoverDay = null;
        try{
            changeDgDataSource();
            if(null == end){
                if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                    //月初到昨日
                    turnoverDay = dg.queryActualTurnoverDay(param,DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1));
                }else{
                    turnoverDay = dg.queryActualTurnoverDay(param,DateUtil.getFirstDayForMonth(start),
                            start);
                }
            }else{
                if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                    //开始时间到昨日
                    turnoverDay = dg.queryActualTurnoverDay(param,start,
                            DateUtils.addDays(end,-1));
                }else{
                    turnoverDay = dg.queryActualTurnoverDay(param,start,
                            end);
                }
            }
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != turnoverDay){
            BigDecimal sale = new BigDecimal(StringUtils.isEmpty(turnoverDay.getSale())?"0":turnoverDay.getSale());
            BigDecimal cost = new BigDecimal(StringUtils.isEmpty(turnoverDay.getCost())?"0":turnoverDay.getCost());
            if(cost.compareTo(BigDecimal.ZERO) != 0){
                turnoverDayResponse.setTurnoverDays(sale.divide(cost,2,BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        return turnoverDayResponse;
    }

    /**
     * 汇总客流和毛利额
     * @param target
     * @param klList
     */
    private void setHisRankKlForStore(List<RankDetail> target,List<RankDetail> klList){
        for(RankDetail value : target){
            for(RankDetail kl : klList){
                if(value.getStoreId().equals(kl.getStoreId())){
                    value.setCustomerNum(kl.getCustomerNum());
                }
            }
            value.setTotalRate(value.getTotalFrontDeskRate());
            value.setTotalCompareRate(value.getTotalCompareFrontDeskRate());

            value.setTotalRateIn(value.getTotalFrontDeskRateIn());
            value.setTotalCompareRateIn(value.getTotalCompareFrontDeskRateIn());
        }
    }
    /**
     * 汇总客流和毛利额
     * @param target
     * @param klList
     */
    private void setHisRankKlForArea(List<RankDetail> target,List<RankDetail> klList){
        for(RankDetail value : target){
            for(RankDetail kl : klList){
                if(value.getAreaId().equals(kl.getAreaId())){
                    value.setCustomerNum(kl.getCustomerNum());
                }
            }
            value.setTotalRate(value.getTotalFrontDeskRate());
            value.setTotalCompareRate(value.getTotalCompareFrontDeskRate());

            value.setTotalRateIn(value.getTotalFrontDeskRateIn());
            value.setTotalCompareRateIn(value.getTotalCompareFrontDeskRateIn());
        }
    }

    /**
     * 查询排行榜门店同期
     * @param rankParamRequest
     * @return
     */
    private List<RankDetail> queryRankSameInfoForStore(RankParamRequest rankParamRequest) throws Exception {
        List<RankDetail> result = new ArrayList<>();
        //包含当天分两步（第一步查询当天对应的同期数据，第二部查询昨天至结束时间的数据）
        if((null == rankParamRequest.getEnd()  && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) >= 0)){
            Date start = rankParamRequest.getStart();
            try{
                //切换数据
                changeZyDataSource();
                //查询历史可比
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
                rankParamRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
                result = zy.querySameRankDetailForTime(rankParamRequest,"00",
                        DateUtils.parseDateToStr("HH",new Date()),1);
                if(null != result && result.size() > 0){
                    for(RankDetail rankDetail : result){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSales())?"0":rankDetail.getTotalSales());
                        BigDecimal compareSale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSales())?"0":rankDetail.getTotalCompareSales());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCost())?"0":rankDetail.getTotalCost());
                        BigDecimal compareCost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCost())?"0":rankDetail.getTotalCompareCost());

                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSalesIn())?"0":rankDetail.getTotalSalesIn());
                        BigDecimal compareSaleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSalesIn())?"0":rankDetail.getTotalCompareSalesIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCostIn())?"0":rankDetail.getTotalCostIn());
                        BigDecimal compareCostIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCostIn())?"0":rankDetail.getTotalCompareCostIn());

                        rankDetail.setTotalRate(sale.subtract(cost).toString());
                        rankDetail.setTotalCompareRate(compareSale.subtract(compareCost).toString());

                        rankDetail.setTotalRateIn(saleIn.subtract(costIn).toString());
                        rankDetail.setTotalCompareRateIn(compareSaleIn.subtract(compareCostIn).toString());
                    }
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if((null != rankParamRequest.getEnd() &&
                DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0)){
            Date start = rankParamRequest.getStart();
            Date end = rankParamRequest.getEnd();
            List<RankDetail> list01 = null;
            try{
                //切换数据
                //当天同期数据
                changeZyDataSource();
                rankParamRequest.setEnd(DateUtils.addYears(end,-1));
                list01 = zy.querySameRankDetailForTime(rankParamRequest,"00",
                        DateUtils.parseDateToStr("HH",new Date()),1);
                if(null != list01 && list01.size() > 0){
                    for(RankDetail rankDetail : list01){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSales())?"0":rankDetail.getTotalSales());
                        BigDecimal compareSale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSales())?"0":rankDetail.getTotalCompareSales());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCost())?"0":rankDetail.getTotalCost());
                        BigDecimal compareCost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCost())?"0":rankDetail.getTotalCompareCost());

                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSalesIn())?"0":rankDetail.getTotalSalesIn());
                        BigDecimal compareSaleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSalesIn())?"0":rankDetail.getTotalCompareSalesIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCostIn())?"0":rankDetail.getTotalCostIn());
                        BigDecimal compareCostIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCostIn())?"0":rankDetail.getTotalCompareCostIn());

                        rankDetail.setTotalRate(sale.subtract(cost).toString());
                        rankDetail.setTotalCompareRate(compareSale.subtract(compareCost).toString());

                        rankDetail.setTotalRateIn(saleIn.subtract(costIn).toString());
                        rankDetail.setTotalCompareRateIn(compareSaleIn.subtract(compareCostIn).toString());
                    }
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //昨日到结束日期的同期数据
            rankParamRequest.setStart(DateUtils.addYears(start,-1));
            rankParamRequest.setEnd(DateUtils.addYears(DateUtils.addDays(end,-1),-1));

            List<RankDetail> list02 = queryHisRankStoreDetailInfo(rankParamRequest, 4);
            for(RankDetail rankDetail : list02){
                rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());

                rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
            }

            if(null != list01 && list01.size() > 0){
                for(RankDetail rankDetail01 : list01){
                    if(null != list02 && list02.size() > 0) {
                        Iterator<RankDetail> it = list02.iterator();
                        while (it.hasNext()) {
                            RankDetail rankDetail02 = it.next();
                            if (rankDetail01.getStoreId().equals(rankDetail02.getStoreId())) {
                                BigDecimal storeSaleValue01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalSales()) ? "0": rankDetail01.getTotalSales());
                                BigDecimal storeCompareSaleValue01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalCompareSales()) ? "0": rankDetail01.getTotalCompareSales());
                                BigDecimal storeRateValue01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalRate()) ? "0": rankDetail01.getTotalRate());
                                BigDecimal storeCompareRateValue01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalCompareRate()) ? "0": rankDetail01.getTotalCompareRate());

                                BigDecimal storeSaleValueIn01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalSalesIn()) ? "0": rankDetail01.getTotalSalesIn());
                                BigDecimal storeCompareSaleValueIn01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalCompareSalesIn()) ? "0": rankDetail01.getTotalCompareSalesIn());
                                BigDecimal storeRateValueIn01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalRateIn()) ? "0": rankDetail01.getTotalRateIn());
                                BigDecimal storeCompareRateValueIn01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalCompareRateIn()) ? "0": rankDetail01.getTotalCompareRateIn());

                                BigDecimal storeSaleValue02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalSales()) ? "0": rankDetail02.getTotalSales());
                                BigDecimal storeCompareSaleValue02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalCompareSales()) ? "0": rankDetail02.getTotalCompareSales());
                                BigDecimal storeRateValue02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalFrontDeskRate())? "0": rankDetail02.getTotalFrontDeskRate());
                                BigDecimal storeCompareRateValue02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalCompareRate())? "0": rankDetail02.getTotalCompareRate());

                                BigDecimal storeSaleValueIn02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalSalesIn()) ? "0": rankDetail02.getTotalSalesIn());
                                BigDecimal storeCompareSaleValueIn02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalCompareSalesIn()) ? "0": rankDetail02.getTotalCompareSalesIn());
                                BigDecimal storeRateValueIn02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalFrontDeskRateIn())? "0": rankDetail02.getTotalFrontDeskRateIn());
                                BigDecimal storeCompareRateValueIn02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalCompareRateIn())? "0": rankDetail02.getTotalCompareRateIn());

                                rankDetail01.setTotalSales(storeSaleValue01.add(storeSaleValue02).toString());
                                rankDetail01.setTotalCompareSales(storeCompareSaleValue01.add(storeCompareSaleValue02).toString());
                                rankDetail01.setTotalRate(storeRateValue01.add(storeRateValue02).toString());
                                rankDetail01.setTotalCompareRate(storeCompareRateValue01.add(storeCompareRateValue02).toString());

                                rankDetail01.setTotalSalesIn(storeSaleValueIn01.add(storeSaleValueIn02).toString());
                                rankDetail01.setTotalCompareSalesIn(storeCompareSaleValueIn01.add(storeCompareSaleValueIn02).toString());
                                rankDetail01.setTotalRateIn(storeRateValueIn01.add(storeRateValueIn02).toString());
                                rankDetail01.setTotalCompareRateIn(storeCompareRateValueIn01.add(storeCompareRateValueIn02).toString());
                                it.remove();
                                break;
                            }
                        }
                    }else{
                        break;
                    }
                }
                if(null != list02 && list02.size() > 0){
                    result.addAll(list02);
                }
                result.addAll(list01);
            }else{
                if(null != list02 && list02.size() > 0){
                    result.addAll(list02);
                }
            }
        }else{

            if(null != rankParamRequest.getEnd()){
                rankParamRequest.setStart(DateUtils.addYears(rankParamRequest.getStart(),-1));
                rankParamRequest.setEnd(DateUtils.addYears(rankParamRequest.getEnd(),-1));
            }else{
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",rankParamRequest.getStart()));
                rankParamRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
            }
            result = queryHisRankStoreDetailInfo(rankParamRequest, 4);
            if(null != result && result.size() > 0){
                for(RankDetail rankDetail : result){
                    rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());
                    rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                    rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
                    rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                }
            }
        }
        return result;
    }

    /**
     * 查询排行榜区域同期
     * @param rankParamRequest
     * @return
     */
    private List<RankDetail> queryRankSameInfoForArea(RankParamRequest rankParamRequest) throws Exception {
        List<RankDetail> result = new ArrayList<>();
        //包含当天分两步（第一步查询当天对应的同期数据，第二部查询昨天至结束时间的数据）
        if((null == rankParamRequest.getEnd()  && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) >= 0)){
            Date start = rankParamRequest.getStart();
            try{
                //切换数据
                changeZyDataSource();
                //查询历史可比
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
                rankParamRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
                result = zy.querySameAreaRankDetailForTime(rankParamRequest,"00",
                        DateUtils.parseDateToStr("HH",new Date()));
                if(null != result && result.size() > 0){
                    for(RankDetail rankDetail : result){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSales())?"0":rankDetail.getTotalSales());
                        BigDecimal compareSale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSales())?"0":rankDetail.getTotalCompareSales());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCost())?"0":rankDetail.getTotalCost());
                        BigDecimal compareCost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCost())?"0":rankDetail.getTotalCompareCost());

                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSalesIn())?"0":rankDetail.getTotalSalesIn());
                        BigDecimal compareSaleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSalesIn())?"0":rankDetail.getTotalCompareSalesIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCostIn())?"0":rankDetail.getTotalCostIn());
                        BigDecimal compareCostIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCostIn())?"0":rankDetail.getTotalCompareCostIn());

                        rankDetail.setTotalRate(sale.subtract(cost).toString());
                        rankDetail.setTotalCompareRate(compareSale.subtract(compareCost).toString());

                        rankDetail.setTotalRateIn(saleIn.subtract(costIn).toString());
                        rankDetail.setTotalCompareRateIn(compareSaleIn.subtract(compareCostIn).toString());
                    }
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if((null != rankParamRequest.getEnd() &&
                DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0)){
            Date start = rankParamRequest.getStart();
            Date end = rankParamRequest.getEnd();
            List<RankDetail> list01 = null;
            try{
                //切换数据
                //当天同期数据
                changeZyDataSource();
                rankParamRequest.setEnd(DateUtils.addYears(end,-1));
                list01 = zy.querySameAreaRankDetailForTime(rankParamRequest,"00",
                        DateUtils.parseDateToStr("HH",new Date()));
                if(null != list01 && list01.size() > 0){
                    for(RankDetail rankDetail : list01){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSales())?"0":rankDetail.getTotalSales());
                        BigDecimal compareSale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSales())?"0":rankDetail.getTotalCompareSales());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCost())?"0":rankDetail.getTotalCost());
                        BigDecimal compareCost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCost())?"0":rankDetail.getTotalCompareCost());

                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSalesIn())?"0":rankDetail.getTotalSalesIn());
                        BigDecimal compareSaleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSalesIn())?"0":rankDetail.getTotalCompareSalesIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCostIn())?"0":rankDetail.getTotalCostIn());
                        BigDecimal compareCostIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCostIn())?"0":rankDetail.getTotalCompareCostIn());

                        rankDetail.setTotalRate(sale.subtract(cost).toString());
                        rankDetail.setTotalCompareRate(compareSale.subtract(compareCost).toString());

                        rankDetail.setTotalRateIn(saleIn.subtract(costIn).toString());
                        rankDetail.setTotalCompareRateIn(compareSaleIn.subtract(compareCostIn).toString());
                    }
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //昨日到结束日期的同期数据
            rankParamRequest.setStart(DateUtils.addYears(start,-1));
            rankParamRequest.setEnd(DateUtils.addYears(DateUtils.addDays(end,-1),-1));

            List<RankDetail> list02 = csmb.queryHisAreaRankDetail(rankParamRequest,rankParamRequest.getStart(),rankParamRequest.getEnd());
            for(RankDetail rankDetail : list02){
                rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());

                rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
            }

            if(null != list01 && list01.size() > 0){
                for(RankDetail rankDetail01 : list01){
                    if(null != list02 && list02.size() > 0) {
                        Iterator<RankDetail> it = list02.iterator();
                        while (it.hasNext()) {
                            RankDetail rankDetail02 = it.next();
                            if (rankDetail01.getAreaId().equals(rankDetail02.getAreaId())) {
                                BigDecimal storeSaleValue01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalSales()) ? "0": rankDetail01.getTotalSales());
                                BigDecimal storeCompareSaleValue01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalCompareSales()) ? "0": rankDetail01.getTotalCompareSales());
                                BigDecimal storeRateValue01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalRate()) ? "0": rankDetail01.getTotalRate());
                                BigDecimal storeCompareRateValue01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalCompareRate()) ? "0": rankDetail01.getTotalCompareRate());

                                BigDecimal storeSaleValueIn01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalSalesIn()) ? "0": rankDetail01.getTotalSalesIn());
                                BigDecimal storeCompareSaleValueIn01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalCompareSalesIn()) ? "0": rankDetail01.getTotalCompareSalesIn());
                                BigDecimal storeRateValueIn01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalRateIn()) ? "0": rankDetail01.getTotalRateIn());
                                BigDecimal storeCompareRateValueIn01 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail01.getTotalCompareRateIn()) ? "0": rankDetail01.getTotalCompareRateIn());

                                BigDecimal storeSaleValue02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalSales()) ? "0": rankDetail02.getTotalSales());
                                BigDecimal storeCompareSaleValue02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalCompareSales()) ? "0": rankDetail02.getTotalCompareSales());
                                BigDecimal storeRateValue02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalFrontDeskRate())? "0": rankDetail02.getTotalFrontDeskRate());
                                BigDecimal storeCompareRateValue02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalCompareRate())? "0": rankDetail02.getTotalCompareRate());

                                BigDecimal storeSaleValueIn02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalSalesIn()) ? "0": rankDetail02.getTotalSalesIn());
                                BigDecimal storeCompareSaleValueIn02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalCompareSalesIn()) ? "0": rankDetail02.getTotalCompareSalesIn());
                                BigDecimal storeRateValueIn02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalFrontDeskRateIn())? "0": rankDetail02.getTotalFrontDeskRateIn());
                                BigDecimal storeCompareRateValueIn02 = new BigDecimal(StringUtils.isEmpty
                                        (rankDetail02.getTotalCompareRateIn())? "0": rankDetail02.getTotalCompareRateIn());

                                rankDetail01.setTotalSales(storeSaleValue01.add(storeSaleValue02).toString());
                                rankDetail01.setTotalCompareSales(storeCompareSaleValue01.add(storeCompareSaleValue02).toString());
                                rankDetail01.setTotalRate(storeRateValue01.add(storeRateValue02).toString());
                                rankDetail01.setTotalCompareRate(storeCompareRateValue01.add(storeCompareRateValue02).toString());

                                rankDetail01.setTotalSalesIn(storeSaleValueIn01.add(storeSaleValueIn02).toString());
                                rankDetail01.setTotalCompareSalesIn(storeCompareSaleValueIn01.add(storeCompareSaleValueIn02).toString());
                                rankDetail01.setTotalRateIn(storeRateValueIn01.add(storeRateValueIn02).toString());
                                rankDetail01.setTotalCompareRateIn(storeCompareRateValueIn01.add(storeCompareRateValueIn02).toString());
                                it.remove();
                                break;
                            }
                        }
                    }else{
                        break;
                    }
                }
                if(null != list02 && list02.size() > 0){
                    result.addAll(list02);
                }
                result.addAll(list01);
            }else{
                if(null != list02 && list02.size() > 0){
                    result.addAll(list02);
                }
            }
        }else{
            if(null != rankParamRequest.getEnd()){
                rankParamRequest.setStart(DateUtils.addYears(rankParamRequest.getStart(),-1));
                rankParamRequest.setEnd(DateUtils.addYears(rankParamRequest.getEnd(),-1));
            }else{
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",rankParamRequest.getStart()));
                rankParamRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
            }
            result = csmb.queryHisAreaRankDetail(rankParamRequest,rankParamRequest.getStart(),rankParamRequest.getEnd());
            if(null != result && result.size() > 0){
                for(RankDetail rankDetail : result){
                    rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());
                    rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                    rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
                    rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                }
            }
        }
        return result;
    }

    /**
     * 查询排行榜大类同期
     * @param rankParamRequest
     * @return
     */
    private List<RankDetail> queryRankSameInfoForCategory(RankParamRequest rankParamRequest) throws Exception {
        List<RankDetail> result = new ArrayList<>();
        //包含当天分两步（第一步查询当天对应的同期数据，第二部查询昨天至结束时间的数据）
        if((null == rankParamRequest.getEnd()  && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) >= 0)){
            Date start = rankParamRequest.getStart();
            try{
                //当天对应的同期数据
                changeZyDataSource();
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
                rankParamRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
                result = zy.queryHisCategoryRank(rankParamRequest,DateUtils.parseDateToStr("HH",new Date()),DateUtil.toDate(time,"yyyy-MM-dd"),null);
                if(null != result && result.size() > 0){
                    for(RankDetail rankDetail : result){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSales())?"0":rankDetail.getTotalSales());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCost())?"0":rankDetail.getTotalCost());
                        BigDecimal compareSale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSales())?"0":rankDetail.getTotalCompareSales());
                        BigDecimal comparCost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCost())?"0":rankDetail.getTotalCompareCost());
                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSalesIn())?"0":rankDetail.getTotalSalesIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCostIn())?"0":rankDetail.getTotalCostIn());
                        BigDecimal compareSaleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSalesIn())?"0":rankDetail.getTotalCompareSalesIn());
                        BigDecimal comparCostIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCostIn())?"0":rankDetail.getTotalCompareCostIn());
                        rankDetail.setTotalRate(sale.subtract(cost).toString());
                        rankDetail.setTotalCompareRate(compareSale.subtract(comparCost).toString());

                        rankDetail.setTotalRateIn(saleIn.subtract(costIn).toString());
                        rankDetail.setTotalCompareRateIn(compareSaleIn.subtract(comparCostIn).toString());
                    }
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if((null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0)){
            //当天对应的同期数据
            Date start = rankParamRequest.getStart();
            Date end = rankParamRequest.getEnd();
            List<RankDetail> list01 = null;
            try{
                //切换数据
                changeZyDataSource();
                rankParamRequest.setEnd(DateUtils.addYears(end,-1));
                list01 = zy.queryHisCategoryRank(rankParamRequest,DateUtils.parseDateToStr("HH",new Date()),start,null);
                if(null != result && result.size() > 0){
                    for(RankDetail rankDetail : result){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSales())?"0":rankDetail.getTotalSales());
                        BigDecimal compareSale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSales())?"0":rankDetail.getTotalCompareSales());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCost())?"0":rankDetail.getTotalCost());
                        BigDecimal comparCost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCost())?"0":rankDetail.getTotalCompareCost());

                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSalesIn())?"0":rankDetail.getTotalSalesIn());
                        BigDecimal compareSaleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSalesIn())?"0":rankDetail.getTotalCompareSalesIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCostIn())?"0":rankDetail.getTotalCostIn());
                        BigDecimal comparCostIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCostIn())?"0":rankDetail.getTotalCompareCostIn());
                        rankDetail.setTotalRate(sale.subtract(cost).toString());
                        rankDetail.setTotalCompareRate(compareSale.subtract(comparCost).toString());

                        rankDetail.setTotalRateIn(saleIn.subtract(costIn).toString());
                        rankDetail.setTotalCompareRateIn(compareSaleIn.subtract(comparCostIn).toString());
                    }
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //昨天至结束时间的数据
            List<RankDetail> list02 = csmb.queryHisCategoryRank(rankParamRequest, DateUtils.addYears(start,-1),DateUtils.addYears(DateUtils.addDays(end,-1),-1));
            for(RankDetail rankDetail : list02){
                rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());

                rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
            }

            if(null != list01 && list01.size() > 0){
                for(RankDetail rankDetail01 : list01){
                    if(null != list02 && list02.size() > 0){
                        Iterator<RankDetail> it = list02.iterator();
                        while (it.hasNext()) {
                            RankDetail rankDetail02 = it.next();
                            if (StringUtils.isNotEmpty(rankDetail01.getDeptId()) && rankDetail01.getDeptId().equals(rankDetail02.getDeptId())) {
                                BigDecimal deptSaleValue01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalSales())?"0"
                                        :rankDetail01.getTotalSales());
                                BigDecimal deptSaleCompareValue01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalCompareSales())?"0"
                                        :rankDetail01.getTotalCompareSales());
                                BigDecimal deptRateValue01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalRate())?"0"
                                        :rankDetail01.getTotalRate());
                                BigDecimal deptRateCompareValue01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalCompareRate())?"0"
                                        :rankDetail01.getTotalCompareRate());

                                BigDecimal deptSaleValue02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalSales())?"0"
                                        :rankDetail02.getTotalSales());
                                BigDecimal deptSaleCompareValue02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalCompareSales())?"0"
                                        :rankDetail02.getTotalCompareSales());
                                BigDecimal deptRateValue02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalRate())?"0"
                                        :rankDetail02.getTotalRate());
                                BigDecimal deptRateCompareValue02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalCompareRate())?"0"
                                        :rankDetail02.getTotalCompareRate());

                                BigDecimal deptSaleValueIn01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalSalesIn())?"0"
                                        :rankDetail01.getTotalSalesIn());
                                BigDecimal deptSaleCompareValueIn01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalCompareSalesIn())?"0"
                                        :rankDetail01.getTotalCompareSalesIn());
                                BigDecimal deptRateValueIn01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalRateIn())?"0"
                                        :rankDetail01.getTotalRateIn());
                                BigDecimal deptRateCompareValueIn01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalCompareRateIn())?"0"
                                        :rankDetail01.getTotalCompareRateIn());

                                BigDecimal deptSaleValueIn02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalSalesIn())?"0"
                                        :rankDetail02.getTotalSalesIn());
                                BigDecimal deptSaleCompareValueIn02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalCompareSalesIn())?"0"
                                        :rankDetail02.getTotalCompareSalesIn());
                                BigDecimal deptRateValueIn02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalRateIn())?"0"
                                        :rankDetail02.getTotalRateIn());
                                BigDecimal deptRateCompareValueIn02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalCompareRateIn())?"0"
                                        :rankDetail02.getTotalCompareRateIn());

                                rankDetail01.setTotalSales(deptSaleValue01.add(deptSaleValue02).toString());
                                rankDetail01.setTotalRate(deptRateValue01.add(deptRateValue02).toString());
                                rankDetail01.setTotalCompareSales(deptSaleCompareValue01.add(deptSaleCompareValue02).toString());
                                rankDetail01.setTotalCompareRate(deptRateCompareValue01.add(deptRateCompareValue02).toString());

                                rankDetail01.setTotalSalesIn(deptSaleValueIn01.add(deptSaleValueIn02).toString());
                                rankDetail01.setTotalRateIn(deptRateValueIn01.add(deptRateValueIn02).toString());
                                rankDetail01.setTotalCompareSalesIn(deptSaleCompareValueIn01.add(deptSaleCompareValueIn02).toString());
                                rankDetail01.setTotalCompareRateIn(deptRateCompareValueIn01.add(deptRateCompareValueIn02).toString());
                                it.remove();
                                break;
                            }
                        }
                    }else{
                        break;
                    }
                }
                if(null != list02 && list02.size() > 0){
                    result.addAll(list02);
                }
                result.addAll(list01);
            }else{
                if(null != list02 && list02.size() > 0){
                    result.addAll(list02);
                }
            }
        }else{
            if(null != rankParamRequest.getEnd()){
                rankParamRequest.setStart(DateUtils.addYears(rankParamRequest.getStart(),-1));
                rankParamRequest.setEnd(DateUtils.addYears(rankParamRequest.getEnd(),-1));
            }else{
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",rankParamRequest.getStart()));
                rankParamRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
            }
            result = csmb.queryHisCategoryRank(rankParamRequest,rankParamRequest.getStart(),rankParamRequest.getEnd());
            if(null != result && result.size() > 0){
                for(RankDetail rankDetail : result){
                    rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());
                    rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());

                    rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
                    rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                }
            }
        }
        return result;
    }

    /**
     * 获取生鲜大类
     * @param pageRequest
     * @throws Exception
     */
    private void getFreshList(PageRequest pageRequest) throws Exception {
        List<String> paramList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        List<String> freshList = new ArrayList<>();
        List<String> allFreshList = new ArrayList<>();
        List<UserDept> allDeptList = reportUserDeptService.getAllDept();
        //根据用户获取权限大类
        List<UserDept> userDeptList = reportUserDeptService.getUserDeptList(pageRequest.getPersonId());
        List<String> list = pageRequest.getDeptIds();
        //获取生鲜的list
        for(UserDept userDept : allDeptList){
            if("生鲜".equals(userDept.getPCategory())){
                allFreshList.add(userDept.getDeptId() + "");
            }
        }
        //用户权限获取生鲜
        if(null != userDeptList && userDeptList.size() > 0){
            for(UserDept userDept : userDeptList){
                if("0".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.addAll(allFreshList);
                    break;
                }else if(allFreshList.contains(String.valueOf(userDept.getDeptId()))){
                    freshList.add(userDept.getDeptId() + "");
                }
            }
        }

        if(null != list && list.size() > 0){
            for(String value : list){
                for(String freshValue : freshList){
                    if(value.equals(freshValue)){
                        paramList.add(value);
                        sb.append(value).append(",");
                        break;
                    }
                }
            }
            if(sb.toString().length() > 0){
                String str = sb.toString();
                pageRequest.setDeptIds(paramList);
                pageRequest.setDeptId(str.substring(0, str.lastIndexOf(",")));
            }else{
                pageRequest.setDeptIds(paramList);
                pageRequest.setDeptId("");
            }
        }else if(null != freshList && freshList.size() > 0){
            pageRequest.setDeptIds(freshList);
            for(String key : freshList){
                sb.append(key).append(",");
            }
            String str = sb.toString();
            pageRequest.setDeptId(str.substring(0, str.lastIndexOf(",")));
        }
    }

    /**
     * 将大类汇总到品类
     * @return
     */
    private List<RankDetail> getTotalDeptName(List<UserDept> allList, List<RankDetail> deptList){
        for(RankDetail rankDetail : deptList){
            for(UserDept userDept : allList){
                if(rankDetail.getDeptId().equals(String.valueOf(userDept.getDeptId()))){
                    rankDetail.setMajorDeptName(userDept.getDeptName());
                }
            }
        }
        return deptList;
    }

    private void transform(PageRequest pageRequest){
        String provinceId = pageRequest.getProvinceId();
        String areaId = pageRequest.getAreaId();
        String storeId = pageRequest.getStoreId();
        String deptId = pageRequest.getDeptId();
        String category = pageRequest.getCategory();
        String startDateStr = pageRequest.getStartDate();
        String endDateStr = pageRequest.getEndDate();

        if(StringUtils.isNotEmpty(startDateStr)){
            pageRequest.setStart(DateUtils.dateTime("yyyy-MM-dd", startDateStr));
        }

        if(StringUtils.isNotEmpty(endDateStr)){
            pageRequest.setEnd(DateUtils.dateTime("yyyy-MM-dd", endDateStr));
        }

        if(null != pageRequest.getEnd()){
            if(DateUtils.getBetweenDay(pageRequest.getEnd(), pageRequest.getStart()) == 0){
                pageRequest.setEnd(null);
            }
        }

        if(StringUtils.isNotEmpty(provinceId)){
            String[] provinceIdArr = Convert.toStrArray(provinceId);
            if(provinceId.endsWith(",")){
                pageRequest.setProvinceId(provinceId.substring(0,provinceId.lastIndexOf(",")));
            }
            pageRequest.setProvinceIds(Arrays.asList(provinceIdArr));
        }

        if(StringUtils.isNotEmpty(areaId)){
            String[] areaIdArr = Convert.toStrArray(areaId);
            if(areaId.endsWith(",")){
                pageRequest.setAreaId(areaId.substring(0,areaId.lastIndexOf(",")));
            }
            pageRequest.setAreaIds(Arrays.asList(areaIdArr));
        }

        if(StringUtils.isNotEmpty(storeId)){
            String[] storeIdArr = Convert.toStrArray(storeId);
            if(storeId.endsWith(",")){
                pageRequest.setStoreId(storeId.substring(0,storeId.lastIndexOf(",")));
            }
            pageRequest.setStoreIds(Arrays.asList(storeIdArr));
        }

        if(StringUtils.isNotEmpty(deptId)){
            String[] deptIdArr = Convert.toStrArray(deptId);
            if(deptId.endsWith(",")){
                pageRequest.setDeptId(deptId.substring(0,deptId.lastIndexOf(",")));
            }
            pageRequest.setDeptIds(Arrays.asList(deptIdArr));
        }

        if(StringUtils.isNotEmpty(category)){
            StringBuilder sb = new StringBuilder();
            String[] categoryArr = Convert.toStrArray(category);
            if(category.endsWith(",")){
                pageRequest.setCategory(category.substring(0,category.lastIndexOf(",")));
            }
            for(int i = 0; i < categoryArr.length; i++){
                sb.append("'").append(categoryArr[i]).append("',");
            }
            if(sb.toString().endsWith(",")){
                pageRequest.setQueryCategorys(sb.toString().substring(0,sb.toString().lastIndexOf(",")));
            }
            pageRequest.setCategorys(Arrays.asList(categoryArr));
        }
    }

    /**
     * 组装总的销售额和毛利率
     */
    private TotalSalesAndProfit buildResult(TotalSalesAndProfit his,
                                            TotalSalesAndProfit now,
                                            TotalSalesAndProfit stockPriceAndDayInfo,
                                            TotalSalesAndProfit stockNum,
                                            TotalSalesAndProfit sameInfo,
                                            TotalSalesAndProfit compareInfo){
        TotalSalesAndProfit response = new TotalSalesAndProfit();
        if(null != his){
            response = his;
            BigDecimal hisSale = new BigDecimal(StringUtils.isEmpty(his.getTotalSales())?"0":his
                    .getTotalSales());
            BigDecimal hisCompareSale = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareSale())?"0":his
                    .getTotalCompareSale());

            BigDecimal hisSaleIn = new BigDecimal(StringUtils.isEmpty(his.getTotalSalesIn())?"0":his
                    .getTotalSalesIn());
            BigDecimal hisCompareSaleIn = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareSaleIn())?"0":his
                    .getTotalCompareSaleIn());

            BigDecimal hisKl = new BigDecimal(StringUtils.isEmpty(his.getCustomerNum())?"0":his
                    .getCustomerNum());
            //扫描毛利额
            BigDecimal hisRate = new BigDecimal(StringUtils.isEmpty(his.getTotalRate())?"0":his
                    .getTotalRate());
            //可比扫描毛利额
            BigDecimal hisCompareScanningRate = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareScanningRate())?"0":his
                    .getTotalCompareScanningRate());
            //前台毛利额
            BigDecimal frontDeskRate = new BigDecimal(StringUtils.isEmpty(his.getTotalFrontDeskRate())?"0":his
                    .getTotalFrontDeskRate());
            //可比前台毛利额
            BigDecimal hisComparefrontDeskRate = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareFrontDeskRate())?"0":his
                    .getTotalCompareFrontDeskRate());
            //扫描毛利额
            BigDecimal hisRateIn = new BigDecimal(StringUtils.isEmpty(his.getTotalRateIn())?"0":his
                    .getTotalRateIn());
            //可比扫描毛利额
            BigDecimal hisCompareScanningRateIn = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareScanningRateIn())?"0":his
                    .getTotalCompareScanningRateIn());
            //前台毛利额
            BigDecimal frontDeskRateIn = new BigDecimal(StringUtils.isEmpty(his.getTotalFrontDeskRateIn())?"0":his
                    .getTotalFrontDeskRateIn());
            //可比前台毛利额
            BigDecimal hisComparefrontDeskRateIn = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareFrontDeskRateIn())?"0":his
                    .getTotalCompareFrontDeskRateIn());

            if(null != now){
                BigDecimal newSale = new BigDecimal(StringUtils.isEmpty(now.getTotalSales())?"0":now
                        .getTotalSales());
                BigDecimal newCompareSale = new BigDecimal(StringUtils.isEmpty(now.getTotalCompareSale())?"0":now
                        .getTotalCompareSale());

                BigDecimal newSaleIn = new BigDecimal(StringUtils.isEmpty(now.getTotalSalesIn())?"0":now
                        .getTotalSalesIn());
                BigDecimal newCompareSaleIn = new BigDecimal(StringUtils.isEmpty(now.getTotalCompareSaleIn())?"0":now
                        .getTotalCompareSaleIn());

                BigDecimal newKl = new BigDecimal(StringUtils.isEmpty(now.getCustomerNum())?"0":now
                        .getCustomerNum());
                //扫描毛利额
                BigDecimal newRate = new BigDecimal(StringUtils.isEmpty(now.getTotalRate())?"0":now
                        .getTotalRate());
                //扫描可比毛利额
                BigDecimal newCompareRate = new BigDecimal(StringUtils.isEmpty(now.getTotalCompareScanningRate())?"0":now
                        .getTotalCompareScanningRate());

                //扫描毛利额
                BigDecimal newRateIn = new BigDecimal(StringUtils.isEmpty(now.getTotalRateIn())?"0":now
                        .getTotalRateIn());
                //扫描可比毛利额
                BigDecimal newCompareRateIn = new BigDecimal(StringUtils.isEmpty(now.getTotalCompareScanningRateIn())?"0":now
                        .getTotalCompareScanningRateIn());

                response.setCustomerNum(newKl.add(hisKl).toString());
                response.setTotalSales(newSale.add(hisSale).toString());
                response.setTotalRate(newRate.add(frontDeskRate).toString());
                response.setTotalCompareRate(hisComparefrontDeskRate.add(newCompareRate).toString());
                response.setTotalCompareSale(newCompareSale.add(hisCompareSale).toString());

                response.setTotalSalesIn(newSaleIn.add(hisSaleIn).toString());
                response.setTotalRateIn(newRateIn.add(frontDeskRateIn).toString());
                response.setTotalCompareRateIn(hisComparefrontDeskRateIn.add(newCompareRateIn).toString());
                response.setTotalCompareSaleIn(newCompareSaleIn.add(hisCompareSaleIn).toString());
                if(newSale.add(hisSale).compareTo(BigDecimal.ZERO) != 0){
                    //扫描毛利率
                    response.setScanningProfitRate(hisRate.add(newRate)
                            .divide(newSale.add(hisSale),4,BigDecimal.ROUND_HALF_UP).toString());
                    //计算前台毛利率
                    response.setTotalProfit(frontDeskRate.add(newRate).divide(
                            hisSale.add(newSale),4,BigDecimal.ROUND_HALF_UP).toString());
                }

                if(newSaleIn.add(hisSaleIn).compareTo(BigDecimal.ZERO) != 0){
                    //扫描毛利率
                    response.setScanningProfitRateIn(hisRateIn.add(newRateIn)
                            .divide(newSaleIn.add(hisSaleIn),4,BigDecimal.ROUND_HALF_UP).toString());
                    //计算前台毛利率
                    response.setTotalProfitIn(frontDeskRateIn.add(newRateIn).divide(
                            hisSaleIn.add(newSaleIn),4,BigDecimal.ROUND_HALF_UP).toString());
                }
            }else{
                //不包含当天，则直接赋值前台毛利额
                response.setTotalRate(his.getTotalFrontDeskRate());
                response.setTotalCompareRate(his.getTotalCompareFrontDeskRate());

                response.setTotalRateIn(his.getTotalFrontDeskRateIn());
                response.setTotalCompareRateIn(his.getTotalCompareFrontDeskRateIn());
                if(hisSale.compareTo(BigDecimal.ZERO) != 0){
                    //扫描毛利率
                    response.setScanningProfitRate(hisRate
                            .divide(hisSale,4,BigDecimal.ROUND_HALF_UP).toString());
                    //计算前台毛利率
                    response.setTotalProfit(frontDeskRate.divide(
                            hisSale,4,BigDecimal.ROUND_HALF_UP).toString());
                }

                if(hisSaleIn.compareTo(BigDecimal.ZERO) != 0){
                    //扫描毛利率
                    response.setScanningProfitRateIn(hisRateIn
                            .divide(hisSaleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                    //计算前台毛利率
                    response.setTotalProfitIn(frontDeskRateIn.divide(
                            hisSaleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        }else{
            response = now;
        }
        //获取库存信息
        response.setStockPrice(StringUtils.isEmpty(stockPriceAndDayInfo.getStockPrice())?"0":stockPriceAndDayInfo.getStockPrice());
        response.setStockNum(StringUtils.isEmpty(stockNum.getStockNum())?"0":stockNum.getStockNum());

        //可比销售额
        BigDecimal curCompareSales= new BigDecimal(StringUtils.isEmpty(response.getTotalCompareSale())?"0":response.getTotalCompareSale());
        //销售额同比=（当期销售额-同期销售额）/同期销售额
        BigDecimal curSales= new BigDecimal(StringUtils.isEmpty(response.getTotalSales())?"0":response.getTotalSales());
        BigDecimal oldSameSales= new BigDecimal(StringUtils.isEmpty(sameInfo.getTotalSales())?"0":sameInfo.getTotalSales());
        BigDecimal sameSalesRate = new BigDecimal("0");

        BigDecimal curCompareSalesIn= new BigDecimal(StringUtils.isEmpty(response.getTotalCompareSaleIn())?"0":response.getTotalCompareSaleIn());
        //销售额同比=（当期销售额-同期销售额）/同期销售额
        BigDecimal curSalesIn = new BigDecimal(StringUtils.isEmpty(response.getTotalSalesIn())?"0":response.getTotalSalesIn());
        BigDecimal oldSameSalesIn = new BigDecimal(StringUtils.isEmpty(sameInfo.getTotalSalesIn())?"0":sameInfo.getTotalSalesIn());
        BigDecimal sameSalesRateIn = new BigDecimal("0");

        if(oldSameSales.compareTo(BigDecimal.ZERO)!=0){
            sameSalesRate = (curSales.subtract(oldSameSales)).divide(oldSameSales,4, BigDecimal.ROUND_HALF_UP);
        }

        if(oldSameSalesIn.compareTo(BigDecimal.ZERO)!=0){
            sameSalesRateIn = (curSalesIn.subtract(oldSameSalesIn)).divide(oldSameSalesIn,4, BigDecimal.ROUND_HALF_UP);
        }

        response.setSalesSameRate(sameSalesRate.toString());
        response.setSalesSameRateIn(sameSalesRateIn.toString());

        //销售额可比=（当期可比销售额-可比销售额）/可比销售额
        BigDecimal oldCompareSales = new BigDecimal(StringUtils.isEmpty(compareInfo.getTotalSales())?"0":compareInfo.getTotalSales());
        BigDecimal oldCompareSalesIn = new BigDecimal(StringUtils.isEmpty(compareInfo.getTotalSalesIn())?"0":compareInfo.getTotalSalesIn());
        if(oldCompareSales.compareTo(BigDecimal.ZERO)!=0){
            response.setSalesCompareRate(curCompareSales.divide(oldCompareSales,4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        if(oldCompareSalesIn.compareTo(BigDecimal.ZERO)!=0){
            response.setSalesCompareRateIn(curCompareSalesIn.divide(oldCompareSalesIn,4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        //当期可比毛利额
//        BigDecimal curCompareRate= new BigDecimal(StringUtils.isEmpty(response.getTotalCompareRate())?"0":response.getTotalCompareRate());
        //毛利率同比率=（当期毛利-同期毛利）/同期毛利
//        BigDecimal curRate= new BigDecimal(StringUtils.isEmpty(response.getTotalRate())?"0":response.getTotalRate());
//        BigDecimal oldSameRate= new BigDecimal(StringUtils.isEmpty(sameInfo.getTotalRate())?"0":sameInfo.getTotalRate());

        //当期可比毛利额
//        BigDecimal curCompareRateIn = new BigDecimal(StringUtils.isEmpty(response.getTotalCompareRateIn())?"0":response.getTotalCompareRateIn());
        //毛利率同比率=（当期毛利-同期毛利）/同期毛利
//        BigDecimal curRateIn = new BigDecimal(StringUtils.isEmpty(response.getTotalRateIn())?"0":response.getTotalRateIn());
//        BigDecimal oldSameRateIn = new BigDecimal(StringUtils.isEmpty(sameInfo.getTotalRateIn())?"0":sameInfo.getTotalRateIn());

        response.setProfitSameRate(calculateService.calculateAddProfitRate(
                calculateService.calculateProfit(response.getTotalSales(),response.getTotalRate()),
                calculateService.calculateProfit(sameInfo.getTotalSales(),sameInfo.getTotalRate())
        ));
        response.setProfitSameRateIn(calculateService.calculateAddProfitRate(
                calculateService.calculateProfit(response.getTotalSalesIn(),response.getTotalRateIn()),
                calculateService.calculateProfit(sameInfo.getTotalSalesIn(),sameInfo.getTotalRateIn())
        ));

        //毛利额可比（当期可比毛利-可比毛利）/可比毛利
//        BigDecimal oldCompareRate = new BigDecimal(StringUtils.isEmpty(compareInfo.getTotalRate())?"0":compareInfo.getTotalRate());
//        BigDecimal oldCompareRateIn = new BigDecimal(StringUtils.isEmpty(compareInfo.getTotalRateIn())?"0":compareInfo.getTotalRateIn());
        response.setProfitCompareRate(
                calculateService.calculateAddProfitRate(
                        calculateService.calculateProfit(
                                response.getTotalCompareSale(),
                                response.getTotalCompareRate()),
                        calculateService.calculateProfit(
                                compareInfo.getTotalSales(),
                                compareInfo.getTotalRate()))
        );

        response.setProfitCompareRateIn(
                calculateService.calculateAddProfitRate(calculateService.calculateProfit(
                        response.getTotalCompareSaleIn(),
                        response.getTotalCompareRateIn()),
                        calculateService.calculateProfit(
                                compareInfo.getTotalSalesIn(),
                                compareInfo.getTotalRateIn()))
        );

        //客单价=当期销售额/客流
        BigDecimal klNum = new BigDecimal(StringUtils.isEmpty(response.getCustomerNum())?"0":response.getCustomerNum());
        if(klNum.compareTo(BigDecimal.ZERO)!=0){
            response.setCustomerSingerPrice(curSales.divide(klNum,2, BigDecimal.ROUND_HALF_UP).toString());
            response.setCustomerSingerPriceIn(curSalesIn.divide(klNum,2, BigDecimal.ROUND_HALF_UP).toString());
        }
        return response;
    }

    /**
     * 查询同比
     * @param pageRequest
     * @return
     */
    private TotalSalesAndProfit querySameInfo(PageRequest pageRequest,String hh) throws Exception {
        TotalSalesAndProfit totalSalesAndProfit = new TotalSalesAndProfit();
        //包含当天分两步（第一步查询当天对应的同期数据，第二部查询昨天至结束时间的数据）
        if((null == pageRequest.getEnd()  && DateUtils.getBetweenDay(pageRequest.getStart(), new Date()) >= 0)){
            Date start = pageRequest.getStart();
            try{
                //切换数据
                changeZyDataSource();
                //查询当天同期数据
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
                pageRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
                totalSalesAndProfit = zy.querySameTotalSalesAndProfitForHour(pageRequest,"00",hh);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if((null != pageRequest.getEnd() && DateUtils.getBetweenDay(pageRequest.getEnd(), new Date()) >= 0)){
            //当天对应的同期数据
            Date start = pageRequest.getStart();
            Date end = pageRequest.getEnd();
            TotalSalesAndProfit totalSalesAndProfit01 = null;
            try{
                //切换数据
                changeZyDataSource();
                //查询当天历史同期
                pageRequest.setEnd(DateUtils.addYears(end,-1));
                totalSalesAndProfit01 = zy.querySameTotalSalesAndProfitForHour(pageRequest,"00",hh);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //昨天至结束时间同期的数据
            pageRequest.setStart(DateUtils.addYears(start,-1));
            pageRequest.setEnd(DateUtils.addYears(DateUtils.addDays(end,-1),-1));
            TotalSalesAndProfit totalSalesAndProfit02 = null;
            try{
                changeDgDataSource();
                totalSalesAndProfit02 = dg.queryHisCommenData(pageRequest);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            BigDecimal totalSales01 = BigDecimal.ZERO;
            BigDecimal totalRate01 = BigDecimal.ZERO;
            BigDecimal totalSalesIn01 = BigDecimal.ZERO;
            BigDecimal totalRateIn01 = BigDecimal.ZERO;
            if(null != totalSalesAndProfit01){
                totalSales01 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit01.getTotalSales())?"0":totalSalesAndProfit01.getTotalSales());
                totalRate01 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit01.getTotalRate())?"0":totalSalesAndProfit01.getTotalRate());
                totalSalesIn01 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit01.getTotalSalesIn())?"0":totalSalesAndProfit01.getTotalSalesIn());
                totalRateIn01 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit01.getTotalRateIn())?"0":totalSalesAndProfit01.getTotalRateIn());
            }

            BigDecimal totalSales02 = BigDecimal.ZERO;
            BigDecimal totalRate02 = BigDecimal.ZERO;

            BigDecimal totalSalesIn02 = BigDecimal.ZERO;
            BigDecimal totalRateIn02 = BigDecimal.ZERO;
            if(null != totalSalesAndProfit02){
                totalSales02 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit02.getTotalSales())?"0"
                        :totalSalesAndProfit02.getTotalSales());
                totalRate02 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit02.getTotalFrontDeskRate())?"0"
                        :totalSalesAndProfit02.getTotalFrontDeskRate());

                totalSalesIn02 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit02.getTotalSalesIn())?"0"
                        :totalSalesAndProfit02.getTotalSalesIn());
                totalRateIn02 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit02.getTotalFrontDeskRateIn())?"0"
                        :totalSalesAndProfit02.getTotalFrontDeskRateIn());
            }

            totalSalesAndProfit.setTotalRate(totalRate01.add(totalRate02).toString());
            totalSalesAndProfit.setTotalSales(totalSales01.add(totalSales02).toString());

            totalSalesAndProfit.setTotalRateIn(totalRateIn01.add(totalRateIn02).toString());
            totalSalesAndProfit.setTotalSalesIn(totalSalesIn01.add(totalSalesIn02).toString());
        }else{
            if(null != pageRequest.getEnd()){
                pageRequest.setStart(DateUtils.addYears(pageRequest.getStart(),-1));
                pageRequest.setEnd(DateUtils.addYears(pageRequest.getEnd(),-1));
            }else{
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getStart()));
                pageRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
            }

            //查询结果
            try{
                //切换数据
                changeDgDataSource();
                //查询历史可比
                totalSalesAndProfit = dg.queryHisCommenData(pageRequest);
                //将毛利额设置为前台毛利额
                totalSalesAndProfit.setTotalRate(totalSalesAndProfit.getTotalFrontDeskRate());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }
        return totalSalesAndProfit;
    }

    /**
     * 查询可比
     * @param pageRequest
     * @return
     */
    private TotalSalesAndProfit queryCompareInfo(PageRequest pageRequest,String hh) throws Exception {
        TotalSalesAndProfit totalSalesAndProfit = new TotalSalesAndProfit();
        //包含当天分两步（第一步查询当天对应的同期数据，第二部查询昨天至结束时间的数据）
        if((null == pageRequest.getEnd()  && DateUtils.getBetweenDay(pageRequest.getStart(), new Date()) >= 0)){
            Date start = pageRequest.getStart();
            try{
                //切换数据
                changeZyDataSource();
                //查询当天同期数据
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
                pageRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
                totalSalesAndProfit = zy.querySameCompareTotalSalesAndProfitForHour(pageRequest,"00",hh);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if((null != pageRequest.getEnd() && DateUtils.getBetweenDay(pageRequest.getEnd(), new Date()) >= 0)){
            //当天对应的同期数据
            Date start = pageRequest.getStart();
            Date end = pageRequest.getEnd();
            TotalSalesAndProfit totalSalesAndProfit01 = null;
            try{
                //切换数据
                changeZyDataSource();
                //查询当天历史同期
                pageRequest.setEnd(DateUtils.addYears(end,-1));
                totalSalesAndProfit01 = zy.querySameCompareTotalSalesAndProfitForHour(pageRequest,"00",hh);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //昨天至结束时间同期的数据
            pageRequest.setStart(DateUtils.addYears(start,-1));
            pageRequest.setEnd(DateUtils.addYears(DateUtils.addDays(end,-1),-1));
            TotalSalesAndProfit totalSalesAndProfit02 = null;
            try{
                changeDgDataSource();
                totalSalesAndProfit02 = dg.queryHisCompareData(pageRequest);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            BigDecimal totalSales01 = BigDecimal.ZERO;
            BigDecimal totalRate01 = BigDecimal.ZERO;

            BigDecimal totalSalesIn01 = BigDecimal.ZERO;
            BigDecimal totalRateIn01 = BigDecimal.ZERO;
            if(null != totalSalesAndProfit01){
                totalSales01 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit01.getTotalSales())?"0"
                        :totalSalesAndProfit01.getTotalSales());
                totalRate01 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit01.getTotalRate())?"0"
                        :totalSalesAndProfit01.getTotalRate());

                totalSalesIn01 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit01.getTotalSalesIn())?"0"
                        :totalSalesAndProfit01.getTotalSalesIn());
                totalRateIn01 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit01.getTotalRateIn())?"0"
                        :totalSalesAndProfit01.getTotalRateIn());
            }

            BigDecimal totalSales02 = BigDecimal.ZERO;
            BigDecimal totalRate02 = BigDecimal.ZERO;

            BigDecimal totalSalesIn02 = BigDecimal.ZERO;
            BigDecimal totalRateIn02 = BigDecimal.ZERO;
            if(null != totalSalesAndProfit02){
                totalSales02 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit02.getTotalSales())?"0"
                        :totalSalesAndProfit02.getTotalSales());
                totalRate02 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit02.getTotalFrontDeskRate())?"0"
                        :totalSalesAndProfit02.getTotalFrontDeskRate());

                totalSalesIn02 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit02.getTotalSalesIn())?"0"
                        :totalSalesAndProfit02.getTotalSalesIn());
                totalRateIn02 = new BigDecimal(StringUtils.isEmpty(totalSalesAndProfit02.getTotalFrontDeskRateIn())?"0"
                        :totalSalesAndProfit02.getTotalFrontDeskRateIn());
            }

            totalSalesAndProfit.setTotalRate(totalRate01.add(totalRate02).toString());
            totalSalesAndProfit.setTotalSales(totalSales01.add(totalSales02).toString());

            totalSalesAndProfit.setTotalRateIn(totalRateIn01.add(totalRateIn02).toString());
            totalSalesAndProfit.setTotalSalesIn(totalSalesIn01.add(totalSalesIn02).toString());
        }else{
            if(null != pageRequest.getEnd()){
                pageRequest.setStart(DateUtils.addYears(pageRequest.getStart(),-1));
                pageRequest.setEnd(DateUtils.addYears(pageRequest.getEnd(),-1));
            }else{
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",pageRequest.getStart()));
                pageRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
            }
            //查询结果
            try{
                //切换数据
                changeDgDataSource();
                //查询历史可比
                totalSalesAndProfit = dg.queryHisCompareData(pageRequest);
                //将毛利额设置为前台毛利额
                totalSalesAndProfit.setTotalRate(totalSalesAndProfit.getTotalFrontDeskRate());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }
        return totalSalesAndProfit;
    }

    /**
     * 查询同比日线，时线，月线（不包含大类）
     * @param param
     * @return
     */
    private List<ReportDetail> querySameReportDetail(ReportParamRequest param) throws Exception {
        List<ReportDetail> result = null;
        if(null != param.getStart()){
            param.setStart(DateUtil.addYear(param.getStart(), -1));
        }
        if(null != param.getEnd()){
            param.setEnd(DateUtil.addYear(param.getEnd(), -1));
        }

        //查询历史时线,日线,月线
        if(2 == param.getType()){
            result = csmb.queryHistoryReportDetail(param,param.getStart(),param.getEnd(),2);
            if(null != result && result.size() > 0){
                for(ReportDetail reportDetail : result){
                    BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                    BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                    BigDecimal rate = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRate())?"0"
                            :reportDetail.getTotalFrontDeskRate());
                    BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRateIn())?"0"
                            :reportDetail.getTotalFrontDeskRateIn());
                    if(sale.compareTo(BigDecimal.ZERO) != 0){
                        reportDetail.setTotalprofit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                    }
                    if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                        reportDetail.setTotalprofitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                    }
                }
            }
        }else if(3 == param.getType()){
            result = csmb.queryHistoryReportDetail(param,param.getStart(),param.getEnd(),3);
            if(null != result && result.size() > 0){
                for(ReportDetail reportDetail : result){
                    BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                    BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                    BigDecimal rate = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRate())?"0"
                            :reportDetail.getTotalFrontDeskRate());
                    BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalFrontDeskRateIn())?"0"
                            :reportDetail.getTotalFrontDeskRateIn());
                    if(sale.compareTo(BigDecimal.ZERO) != 0){
                        reportDetail.setTotalprofit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                    }
                    if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                        reportDetail.setTotalprofitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                    }
                }
            }
        }else if(1 == param.getType()){
            try{
                changeZyDataSource();
                result = zy.queryHisReportDetailForHour(param);
                if(null != result && result.size() > 0){
                    for(ReportDetail reportDetail : result){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSales())?"0":reportDetail.getTotalSales());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalCost())?"0":reportDetail.getTotalCost());
                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalSalesIn())?"0":reportDetail.getTotalSalesIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(reportDetail.getTotalCostIn())?"0":reportDetail.getTotalCostIn());
                        if(sale.compareTo(BigDecimal.ZERO) != 0){
                            reportDetail.setTotalprofit(sale.subtract(cost).divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                        }
                        if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                            reportDetail.setTotalprofitIn(saleIn.subtract(costIn).divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                        }
                    }
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }
        return result;
    }

    private List<RankDetail> queryHisRankStoreDetailInfo(PageRequest pageRequest, int type) {
        List<RankDetail> result = null;
        try{
            //历史数据
            changeDgDataSource();
            result = dg.queryHisRankDetail(pageRequest, type);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        return result;
    }

    /**
     * 查询门店分组数据
     * @param pageRequest
     * @return
     */
    private List<RankDetail> queryCurRankStoreDetailInfo(PageRequest pageRequest, int type) {
        return csmb.queryCurrentRankDetail(pageRequest, type);
    }

    private void changeZyDataSource() {
        GoodsDataSourceConfig goodsDataSourceConfig = new GoodsDataSourceConfig();
        goodsDataSourceConfig.setHost(zyhost);
        goodsDataSourceConfig.setPort(zyport);
        goodsDataSourceConfig.setSid(zysid);
        goodsDataSourceConfig.setStore(zysid);
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, zyuserName, zypassword,
                druidProperties);
        DataSourceHolder.setDataSource(dataSourceBuilder);
    }

    private void changeDgDataSource() {
        GoodsDataSourceConfig goodsDataSourceConfig = new GoodsDataSourceConfig();
        goodsDataSourceConfig.setHost(dghost);
        goodsDataSourceConfig.setPort(dgport);
        goodsDataSourceConfig.setSid(dgsid);
        goodsDataSourceConfig.setStore(dgsid);
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, dguserName, dgpassword,
                druidProperties);
        DataSourceHolder.setDataSource(dataSourceBuilder);
    }

    private String getAllFresh() throws Exception {
        String result = "";
        StringBuilder sb = new StringBuilder();
        List<UserDept> allDeptList = reportUserDeptService.getAllDept();
        //获取生鲜的list
        for(UserDept userDept : allDeptList){
            if("生鲜".equals(userDept.getPCategory())){
                sb.append(userDept.getDeptId()).append(",");
            }
        }
        if(sb.toString().length() > 0){
            String str = sb.toString();
            result = str.substring(0, str.lastIndexOf(","));
        }
        return result;
    }

    /**
     * 填充数据库分组查询没有的时间点数据
     * @param list
     * @param type
     * @param max
     * @param startDate
     * @param endDate
     * @return
     */
    private List<ReportDetail> buildReportReportDetailResponseList(List<ReportDetail> list, int type, int max, Date startDate, Date endDate){
        List<ReportDetail> result = new ArrayList<>();
        //时线
        if(1 == type || 3 == type){
            //如果是时线且是当天则只显示到当前时间点
            if(1 == type && DateUtils.getBetweenDay(startDate, new Date()) >= 0){
                max = Integer.valueOf(DateUtil.getHourForDate(new Date()));
            }
            for(int i = 1; i <= max; i++){
                boolean flag = true;
                String num = null;
                if(1 == type){
                    //如果是时线，则2点到6点之间的数据直接忽略掉
                    if(i > 2 && i < 6){
                        continue;
                    }
                }
                if(i < 10){
                    num = "0" + i;
                }else {
                    num = String.valueOf(i);
                }
                BigDecimal value = new BigDecimal(num);
                for (ReportDetail reportDetail : list){
                    if(value.compareTo(new BigDecimal(reportDetail.getTimePoint())) == 0){
                        flag = false;
                        result.add(reportDetail);
                        break;
                    }
                }
                if(flag){
                    ReportDetail re = new ReportDetail();
                    re.setTimePoint(num);
                    result.add(re);
                }
            }
        }else{//日线
            long count = DateUtils.getBetweenDay(endDate, startDate);
            for(int i = 0; i <= count; i++){
                boolean flag = true;
                Date date = DateUtils.addDays(startDate, i);
                String str = DateUtils.parseDateToStr("yyyyMMdd", date);
                BigDecimal value = new BigDecimal(str);
                for (ReportDetail reportDetail : list){
                    if(value.compareTo(new BigDecimal(reportDetail.getTime())) == 0){
                        flag = false;
                        result.add(reportDetail);
                        break;
                    }
                }
                if(flag){
                    ReportDetail re = new ReportDetail();
                    re.setTime(str);
                    re.setTimePoint(str.substring(6));
                    result.add(re);
                }
            }
        }
        return result;
    }

    /**
     * 查询实时生鲜
     * @param pageRequest
     * @param freshStr
     * @param type
     * @return
     */
    private Fresh queryCurrentFreshForDay(PageRequest pageRequest,String freshStr,int type){
        Fresh fresh = null;
        //当天数据
        fresh = csmb.queryCurrentFreshInfo(pageRequest, freshStr, type);
        if(null != pageRequest.getStart()){
            pageRequest.setStart(DateUtils.addDays(pageRequest.getStart(), -1));
        }
        //查询昨日损耗
        try{
            changeDgDataSource();
            Fresh old = dg.queryHisFreshInfo(pageRequest, freshStr, type);
            if(null != old){//计算昨日损耗率
                BigDecimal lossPrice = new BigDecimal(StringUtil.isEmpty(old.getLossPrice())?"0"
                        :old.getLossPrice());
                BigDecimal totalSale = new BigDecimal(StringUtil.isEmpty(old.getTotalSales())?"0"
                        :old.getTotalSales());

                BigDecimal lossPriceIn = new BigDecimal(StringUtil.isEmpty(old.getLossPriceIn())?"0"
                        :old.getLossPriceIn());
                BigDecimal totalSaleIn = new BigDecimal(StringUtil.isEmpty(old.getTotalSalesIn())?"0"
                        :old.getTotalSalesIn());

                fresh.setLossPrice(lossPrice.toString());
                if(totalSale.compareTo(BigDecimal.ZERO) != 0){
                    fresh.setLossRate(BigDecimal.ZERO.subtract(lossPrice.divide(totalSale,
                            4,BigDecimal.ROUND_HALF_UP)).toString());
                }


                fresh.setLossPriceIn(lossPriceIn.toString());
                if(totalSaleIn.compareTo(BigDecimal.ZERO) != 0){
                    fresh.setLossRateIn(BigDecimal.ZERO.subtract(lossPriceIn.divide(totalSaleIn,
                            4,BigDecimal.ROUND_HALF_UP)).toString());
                }
            }
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        return fresh;
    }

    /**
     * 查询历史生鲜
     * @param pageRequest
     * @param freshStr
     * @param type
     * @return
     */
    private Fresh queryHistoryFresh(PageRequest pageRequest,String freshStr,int type){
        Fresh fresh = null;
        //查询销售额，毛利率
        try{
            changeDgDataSource();
            fresh = dg.queryHisFreshInfo(pageRequest, freshStr, type);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询生鲜客流
        try{
            changeZyDataSource();
            TotalSalesAndProfit his = zy.queryHisTotalData(pageRequest, freshStr, type);
            fresh.setCustomerNum(StringUtils.isEmpty(his.getCustomerNum())?"0":his.getCustomerNum());
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != fresh){
            BigDecimal sales = new BigDecimal(StringUtils.isEmpty(fresh.getTotalSales())? "0" : fresh.getTotalSales());
            BigDecimal loss = new BigDecimal(StringUtils.isEmpty(fresh.getLossPrice()) ? "0" : fresh.getLossPrice());
            BigDecimal profitPrice = new BigDecimal(StringUtils.isEmpty(fresh.getTotalProfitPrice()) ? "0" : fresh.getTotalProfitPrice());

            BigDecimal salesIn = new BigDecimal(StringUtils.isEmpty(fresh.getTotalSalesIn()) ? "0" : fresh.getTotalSalesIn());
            BigDecimal lossIn = new BigDecimal(StringUtils.isEmpty(fresh.getLossPriceIn()) ? "0" : fresh.getLossPriceIn());
            BigDecimal profitPriceIn = new BigDecimal(StringUtils.isEmpty(fresh.getTotalProfitPriceIn()) ? "0" : fresh.getTotalProfitPriceIn());
            BigDecimal kl = new BigDecimal(fresh.getCustomerNum() == null ? "0" : fresh.getCustomerNum());
            if(sales.compareTo(BigDecimal.ZERO)!=0){
                fresh.setLossRate(loss.divide(sales,4, BigDecimal.ROUND_HALF_UP).toString());
                fresh.setTotalProfit(profitPrice.divide(sales,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(salesIn.compareTo(BigDecimal.ZERO)!=0){
                fresh.setLossRateIn(lossIn.divide(salesIn,4, BigDecimal.ROUND_HALF_UP).toString());
                fresh.setTotalProfitIn(profitPriceIn.divide(salesIn,4, BigDecimal.ROUND_HALF_UP).toString());
            }
            if(kl.compareTo(BigDecimal.ZERO)!=0){//计算客单价
                fresh.setCustomerSingerPrice(sales.divide(kl,2, BigDecimal.ROUND_HALF_UP).toString());
                fresh.setCustomerSingerPriceIn(salesIn.divide(kl,2, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        return fresh;
    }

    /**
     * 查询时间段，包含实时的生鲜的数据
     * @param pageRequest
     * @param freshStr
     * @param type
     * @return
     */
    private Fresh queryHistoryFreshHaveCurrentDay(PageRequest pageRequest,String freshStr,int type){
        Fresh fresh = new Fresh();
        //查询当天数据
        Fresh curr = csmb.queryCurrentFreshInfo(pageRequest, freshStr, type);
        //查询历史至昨日数据
        Fresh his = new Fresh();
        try{
            changeDgDataSource();
            his = dg.queryHisFreshInfo(pageRequest, freshStr, type);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询历史生鲜客流
        try{
            changeZyDataSource();
            TotalSalesAndProfit hisTotalSalesAndProfit = zy.queryHisTotalData(pageRequest, freshStr, type);
            his.setCustomerNum(hisTotalSalesAndProfit.getCustomerNum());
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        BigDecimal hisProfitPrice = new BigDecimal(StringUtils.isEmpty(his.getTotalProfitPrice())  ? "0"
                : his.getTotalProfitPrice());
        BigDecimal currProfitPrice = new BigDecimal(StringUtils.isEmpty(curr.getTotalProfitPrice()) ? "0"
                : curr.getTotalProfitPrice());
        BigDecimal hisSales = new BigDecimal(StringUtils.isEmpty(his.getTotalSales()) ? "0"
                : his.getTotalSales());
        BigDecimal currSales = new BigDecimal(StringUtils.isEmpty(curr.getTotalSales()) ? "0"
                : curr.getTotalSales());
        BigDecimal hisKl = new BigDecimal(StringUtils.isEmpty(his.getCustomerNum()) ? "0"
                : his.getCustomerNum());
        BigDecimal currKl = new BigDecimal(StringUtils.isEmpty(curr.getCustomerNum()) ? "0"
                : curr.getCustomerNum());
        BigDecimal hisCost = new BigDecimal(StringUtils.isEmpty(his.getLossPrice()) ? "0" : curr.getTotalSales());

        BigDecimal hisProfitPriceIn = new BigDecimal(StringUtils.isEmpty(his.getTotalProfitPriceIn())  ? "0"
                : his.getTotalProfitPriceIn());
        BigDecimal currProfitPriceIn = new BigDecimal(StringUtils.isEmpty(curr.getTotalProfitPriceIn()) ? "0"
                : curr.getTotalProfitPriceIn());
        BigDecimal hisSalesIn = new BigDecimal(StringUtils.isEmpty(his.getTotalSalesIn()) ? "0"
                : his.getTotalSalesIn());
        BigDecimal currSalesIn = new BigDecimal(StringUtils.isEmpty(curr.getTotalSalesIn()) ? "0"
                : curr.getTotalSalesIn());
        BigDecimal hisCostIn = new BigDecimal(StringUtils.isEmpty(his.getLossPriceIn()) ? "0" : curr.getTotalSalesIn());
        //查询当天的数据
        fresh.setTotalSales(hisSales.add(currSales).toString());
        fresh.setTotalProfitPrice(hisProfitPrice.add(currProfitPrice).toString());

        fresh.setTotalSalesIn(hisSalesIn.add(currSalesIn).toString());
        fresh.setTotalProfitPriceIn(hisProfitPriceIn.add(currProfitPriceIn).toString());

        if(hisSales.add(currSales).compareTo(BigDecimal.ZERO)!=0){
            fresh.setTotalProfit((hisProfitPrice.add(currProfitPrice))
                    .divide((hisSales.add(currSales)),4, BigDecimal.ROUND_HALF_UP).toString());
        }
        if(hisSalesIn.add(currSalesIn).compareTo(BigDecimal.ZERO)!=0){
            fresh.setTotalProfitIn((hisProfitPriceIn.add(currProfitPriceIn))
                    .divide((hisSalesIn.add(currSalesIn)),4, BigDecimal.ROUND_HALF_UP).toString());
        }

        if(hisKl.add(currKl).compareTo(BigDecimal.ZERO) !=0){
            fresh.setCustomerSingerPrice((hisSales.add(currSales))
                    .divide((hisKl.add(currKl)),2, BigDecimal.ROUND_HALF_UP).toString());
            fresh.setCustomerSingerPriceIn((hisSalesIn.add(currSalesIn))
                    .divide((hisKl.add(currKl)),2, BigDecimal.ROUND_HALF_UP).toString());
        }

        fresh.setCustomerNum(hisKl.add(currKl).toString());
        fresh.setLossPrice(hisCost.toString());
        fresh.setLossPriceIn(hisCostIn.toString());
        if(hisSales.add(currSales).compareTo(BigDecimal.ZERO)!=0){
            fresh.setLossRate(BigDecimal.ZERO.subtract(hisCost.divide(hisSales,4, BigDecimal.ROUND_HALF_UP)).toString());
        }
        if(hisSalesIn.add(currSalesIn).compareTo(BigDecimal.ZERO)!=0){
            fresh.setLossRateIn(BigDecimal.ZERO.subtract(hisCostIn.divide(hisSalesIn,4, BigDecimal.ROUND_HALF_UP)).toString());
        }
        return fresh;
    }

    /**
     * 查询品类历史客流
     * @param rankParamRequest
     * @param categoryList
     * @return
     */
    private List<CategoryKlModel> queryHisCategoryKl(RankParamRequest rankParamRequest,List<RankDetail> categoryList,Date start,Date end){
        List<CategoryKlModel> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if(null != categoryList && categoryList.size() > 0){
            for(RankDetail rankDetail : categoryList){
                sb.append("'").append(rankDetail.getDeptName()).append("',");
            }
            if(StringUtils.isNotEmpty(sb.toString())){
                String categoryStr = sb.toString();
                if(sb.toString().endsWith(",")){
                    categoryStr = categoryStr.substring(0,categoryStr.lastIndexOf(","));
                }
                try{
                    changeDgDataSource();
                    result = dg.queryHisCategoryKl(rankParamRequest,categoryStr,start,end);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }
        return result;
    }

    /**
     * 汇总历史品类客流
     * @param rankParamRequest
     * @param categoryList
     * @param start
     * @param end
     */
    private void sumHisCategoryKl(RankParamRequest rankParamRequest,List<RankDetail> categoryList,Date start,Date end){
        //查询品类历史客流
        List<CategoryKlModel> categoryHisKls = queryHisCategoryKl(rankParamRequest,categoryList, start, end);
        //汇总客流数据
        if(null != categoryHisKls && categoryHisKls.size() > 0){
            for(RankDetail rankDetail : categoryList){
                for(CategoryKlModel categoryKlModel : categoryHisKls){
                    if(rankDetail.getDeptName().equals(categoryKlModel.getCate())){
                        //将当日客流和历史客流合并
                        BigDecimal kl = new BigDecimal(StringUtils.isEmpty(rankDetail.getCustomerNum())?"0":rankDetail.getCustomerNum());
                        BigDecimal hisKl = new BigDecimal(StringUtils.isEmpty(categoryKlModel.getKl())?"0":categoryKlModel.getKl());
                        rankDetail.setCustomerNum(hisKl.add(kl).toString());
                    }
                }
            }
        }
    }

    /**
     * 查询品类排行榜
     * @param rankParamRequest
     * @return
     * @throws Exception
     */
    private RankInfoResponse queryCategoryRankList(RankParamRequest rankParamRequest) throws Exception {
        RankInfoResponse response = new RankInfoResponse();
        List<RankDetail> deptList = new ArrayList<>();
        List<RankDetail> deptSameList = new ArrayList<>();
        long upDeptSaleCount = 0;
        long downDeptSaleCount = 0;
        long upDeptRateCount = 0;
        long downDeptRateCount = 0;

        long upDeptSaleCountIn = 0;
        long downDeptSaleCountIn = 0;
        long upDeptRateCountIn = 0;
        long downDeptRateCountIn = 0;
        Date start = rankParamRequest.getStart();
        Date end = rankParamRequest.getEnd();
        //查询品类数据（查询大类汇总到品类）
        if((rankParamRequest.getEnd() == null && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) < 0)
                || (null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) < 0) ){
            //查询历史品类数据(不包含客流)
            deptList = csmb.queryHisCategoryRank(rankParamRequest,start,end);
            if(null != deptList){//设置最终毛利额为前台毛利额
                for(RankDetail rankDetail : deptList){
                    rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());
                    rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                    rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
                    rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                }
            }
        }else if(null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0){
            //查询历史品类数据(不包含客流)
            deptList = csmb.queryHisCategoryRank(rankParamRequest,start,end);
            if(null != deptList){
                for(RankDetail rankDetail : deptList){//设置最终毛利额为前台毛利额
                    rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());
                    rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                    rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
                    rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                }
            }
            //查询当天的数据(包含客流)
            List<RankDetail> curDeptList = csmb.queryCurCategoryRank(rankParamRequest, end,null);
            for(RankDetail cur : curDeptList) {//将历史和实时数据合并
                boolean flag = true;
                for (RankDetail his : deptList) {
                    if (his.getDeptName().equals(cur.getDeptName())) {
                        BigDecimal hisTatolSale = new BigDecimal(StringUtil.isEmpty(his.getTotalSales()) ? "0" : his.getTotalSales());
                        BigDecimal hisTatolRate = new BigDecimal(StringUtil.isEmpty(his.getTotalFrontDeskRate()) ? "0"
                                : his.getTotalFrontDeskRate());
                        BigDecimal hisTatolCompareRate = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRate()) ? "0"
                                : his.getTotalCompareRate());
                        BigDecimal hisTatolCompareSale = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSales()) ? "0" : his.getTotalCompareSales());

                        BigDecimal hisTatolSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalSalesIn()) ? "0" : his.getTotalSalesIn());
                        BigDecimal hisTatolRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalFrontDeskRateIn()) ? "0"
                                : his.getTotalFrontDeskRateIn());
                        BigDecimal hisTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRateIn()) ? "0"
                                : his.getTotalCompareRateIn());
                        BigDecimal hisTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSalesIn()) ? "0" : his.getTotalCompareSalesIn());

                        BigDecimal curTatolSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalSales()) ? "0"
                                : cur.getTotalSales());
                        BigDecimal curTatolCompareSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSales()) ? "0"
                                : cur.getTotalCompareSales());
                        BigDecimal curTatolRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalRate()) ? "0"
                                : cur.getTotalRate());
                        BigDecimal curTatolCompareRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRate()) ? "0"
                                : cur.getTotalCompareRate());

                        BigDecimal curTatolSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalSalesIn()) ? "0"
                                : cur.getTotalSalesIn());
                        BigDecimal curTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSalesIn()) ? "0"
                                : cur.getTotalCompareSalesIn());
                        BigDecimal curTatolRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalRateIn()) ? "0"
                                : cur.getTotalRateIn());
                        BigDecimal curTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRateIn()) ? "0"
                                : cur.getTotalCompareRateIn());
                        BigDecimal curCustomerNum = new BigDecimal(StringUtil.isEmpty(cur.getCustomerNum()) ? "0"
                                : cur.getCustomerNum());

                        his.setTotalRate(hisTatolRate.add(curTatolRate).toString());
                        his.setTotalSales(hisTatolSale.add(curTatolSale).toString());
                        his.setTotalCompareSales(hisTatolCompareSale.add(curTatolCompareSale).toString());
                        his.setTotalCompareRate(hisTatolCompareRate.add(curTatolCompareRate).toString());

                        his.setTotalRateIn(hisTatolRateIn.add(curTatolRateIn).toString());
                        his.setTotalSalesIn(hisTatolSaleIn.add(curTatolSaleIn).toString());
                        his.setTotalCompareSalesIn(hisTatolCompareSaleIn.add(curTatolCompareSaleIn).toString());
                        his.setTotalCompareRateIn(hisTatolCompareRateIn.add(curTatolCompareRateIn).toString());
                        his.setCustomerNum(curCustomerNum.toString());
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    deptList.add(cur);
                }
            }
        }else{
            deptList = csmb.queryCurCategoryRank(rankParamRequest, start,end);
        }

        //只要涉及到历史时间时才查询历史品类客流，然后赋值给deptList
        if(DateUtils.getBetweenDay(start,new Date()) < 0){
            sumHisCategoryKl(rankParamRequest,deptList,start,end);
        }
        //查询同期数据
        deptSameList = queryRankSameInfoForCategory(rankParamRequest);

        //计算品类同比大于等于和小于零的数量
        if(null != deptList && deptList.size() > 0){
            if(null != deptSameList && deptSameList.size() > 0){
                for(RankDetail d : deptList){
                    for(RankDetail s : deptSameList){
                        if(StringUtils.isNotEmpty(d.getDeptName()) && d.getDeptName().equals(s.getDeptName())){
                            BigDecimal deptSaleValue = new BigDecimal(StringUtils.isEmpty(d.getTotalSales())?"0":d.getTotalSales());
                            BigDecimal deptRateValue = new BigDecimal(StringUtils.isEmpty(d.getTotalRate())?"0":d.getTotalRate());
                            BigDecimal deptRateCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRate())?"0":d.getTotalCompareRate());
                            BigDecimal deptSaleCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSales())?"0":d.getTotalCompareSales());
                            BigDecimal deptSaleSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalSales())?"0":s.getTotalSales());
                            BigDecimal deptRateSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalRate())?"0":s.getTotalRate());
                            BigDecimal deptSaleCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSales())?"0":s.getTotalCompareSales());
                            BigDecimal deptRateCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRate())?"0":s.getTotalCompareRate());
                            BigDecimal deptSaleValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalSalesIn())?"0":d.getTotalSalesIn());
                            BigDecimal deptRateValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalRateIn())?"0":d.getTotalRateIn());
                            BigDecimal deptRateCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRateIn())?"0":d.getTotalCompareRateIn());
                            BigDecimal deptSaleCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSalesIn())?"0":d.getTotalCompareSalesIn());
                            BigDecimal deptSaleSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalSalesIn())?"0":s.getTotalSalesIn());
                            BigDecimal deptRateSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalRateIn())?"0":s.getTotalRateIn());
                            BigDecimal deptSaleCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSalesIn())?"0":s.getTotalCompareSalesIn());
                            BigDecimal deptRateCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRateIn())?"0":s.getTotalCompareRateIn());
                            //计算销售额同比
                            if(deptSaleSameValue.compareTo(new BigDecimal("0")) != 0){
                                d.setSaleSameRate(deptSaleValue.subtract(deptSaleSameValue).divide(deptSaleSameValue,
                                        4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            if(deptSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0){
                                d.setSaleCompareRate(deptSaleCompareValue.divide(deptSaleCompareSameValue,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算毛利额同比
                            if(deptRateSameValue.compareTo(new BigDecimal("0")) != 0){
                                d.setProfitSameRate(deptRateValue.subtract(deptRateSameValue).divide(deptRateSameValue,
                                        4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            if(deptRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0){
                                d.setProfitCompareRate(deptRateCompareValue.divide(deptRateCompareSameValue,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算销售额同比
                            if(deptSaleSameValueIn.compareTo(new BigDecimal("0")) != 0){
                                d.setSaleSameRateIn(deptSaleValueIn.subtract(deptSaleSameValueIn).divide(deptSaleSameValueIn,
                                        4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            if(deptSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0){
                                d.setSaleCompareRateIn(deptSaleCompareValueIn.divide(deptSaleCompareSameValueIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算毛利额同比
                            if(deptRateSameValueIn.compareTo(new BigDecimal("0")) != 0){
                                d.setProfitSameRateIn(deptRateValueIn.subtract(deptRateSameValueIn).divide(deptRateSameValueIn,
                                        4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            if(deptRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0){
                                d.setProfitCompareRateIn(deptRateCompareValueIn.divide(deptRateCompareSameValueIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算销售额可比小于零的数量
                            if(deptSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValue.compareTo(deptSaleCompareSameValue) < 0){
                                downDeptSaleCount = downDeptSaleCount + 1;
                            }

                            //计算毛利额可比小于零的数量
                            if(deptRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptRateCompareValue.compareTo(deptRateCompareSameValue) < 0 ){
                                downDeptRateCount = downDeptRateCount + 1;
                            }

                            if(deptSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValue.compareTo(deptSaleCompareSameValue) >= 0){
                                upDeptSaleCount = upDeptSaleCount + 1;
                            }

                            if(deptRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptRateCompareValue.compareTo(deptRateCompareSameValue) >= 0 ){
                                upDeptRateCount = upDeptRateCount + 1;
                            }

                            if(deptSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValue.compareTo(deptSaleCompareSameValue) < 0){
                                downDeptSaleCount = downDeptSaleCount + 1;
                            }

                            //计算销售额可比小于零的数量
                            if(deptSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValueIn.compareTo(deptSaleCompareSameValueIn) < 0){
                                downDeptSaleCountIn = downDeptSaleCountIn + 1;
                            }

                            //计算毛利额可比小于零的数量
                            if(deptRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && deptRateCompareValueIn.compareTo(deptRateCompareSameValueIn) < 0 ){
                                downDeptRateCountIn = downDeptRateCountIn + 1;
                            }

                            if(deptSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValueIn.compareTo(deptSaleCompareSameValueIn) >= 0){
                                upDeptSaleCountIn = upDeptSaleCountIn + 1;
                            }

                            if(deptRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && deptRateCompareValueIn.compareTo(deptRateCompareSameValueIn) >= 0 ){
                                upDeptRateCountIn = upDeptRateCountIn + 1;
                            }
                            break;
                        }
                    }

                }
            }
        }

        //品类排序
        if(deptList.size() > 0) {
            //品类销售额排序
            List<RankDetail> saleList = deptList.stream().sorted(new Comparator<RankDetail>() {
                @Override
                public int compare(RankDetail o1, RankDetail o2) {
                    BigDecimal sales1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalSales()) ? "0" : o1.getTotalSales());
                    BigDecimal sales2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalSales()) ? "0" : o2.getTotalSales());
                    return sales2.compareTo(sales1);
                }
            }).collect(Collectors.toList());

            List<RankDetailResponse> sale = new ArrayList<>();
            for(RankDetail rankDetail : saleList){
                RankDetailResponse rankDetailResponse = new RankDetailResponse();
                BeanUtils.copyProperties(rankDetail, rankDetailResponse);
                sale.add(rankDetailResponse);
            }
            response.setDeptSaleList(sale);

            //品类毛利额排序
            List<RankDetail> rateList = deptList.stream().sorted(new Comparator<RankDetail>() {
                @Override
                public int compare(RankDetail o1, RankDetail o2) {
                    BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalRate()) ? "0" : o1.getTotalRate());
                    BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalRate()) ? "0" : o2.getTotalRate());
                    return rate2.compareTo(rate1);
                }
            }).collect(Collectors.toList());

            List<RankDetailResponse> rate = new ArrayList<>();
            for(RankDetail rankDetail : rateList){
                RankDetailResponse rankDetailResponse = new RankDetailResponse();
                BeanUtils.copyProperties(rankDetail, rankDetailResponse);
                rate.add(rankDetailResponse);
            }
            response.setDeptProfitList(rate);
        }
        response.setDeptSaleDownNum(String.valueOf(downDeptSaleCount));
        response.setDeptSaleUpNum(String.valueOf(upDeptSaleCount));
        response.setDeptRateDownNum(String.valueOf(downDeptRateCount));
        response.setDeptRateUpNum(String.valueOf(upDeptRateCount));

        response.setDeptSaleDownNumIn(String.valueOf(downDeptSaleCountIn));
        response.setDeptSaleUpNumIn(String.valueOf(upDeptSaleCountIn));
        response.setDeptRateDownNumIn(String.valueOf(downDeptRateCountIn));
        response.setDeptRateUpNumIn(String.valueOf(upDeptRateCountIn));
        return response;
    }

    /**
     * 查询品类排行榜数量
     * @param rankParamRequest
     * @return
     * @throws Exception
     */
    private RankInfoResponse queryCategoryRankCount(RankParamRequest rankParamRequest) throws Exception {
        RankInfoResponse response = new RankInfoResponse();
        List<RankDetail> deptList = new ArrayList<>();
        List<RankDetail> deptSameList = new ArrayList<>();
        long upDeptSaleCount = 0;
        long downDeptSaleCount = 0;
        long upDeptRateCount = 0;
        long downDeptRateCount = 0;

        long upDeptSaleCountIn = 0;
        long downDeptSaleCountIn = 0;
        long upDeptRateCountIn = 0;
        long downDeptRateCountIn = 0;
        Date start = rankParamRequest.getStart();
        Date end = rankParamRequest.getEnd();
        //查询品类数据（查询大类汇总到品类）
        if((rankParamRequest.getEnd() == null && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) < 0)
                || (null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) < 0) ){
            //查询历史品类数据(不包含客流)
            deptList = csmb.queryHisCategoryRank(rankParamRequest,start,end);
            if(null != deptList){//设置最终毛利额为前台毛利额
                for(RankDetail rankDetail : deptList){
                    rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());
                    rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                    rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
                    rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                }
            }
        }else if(null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0){
            //查询历史品类数据(不包含客流)
            deptList = csmb.queryHisCategoryRank(rankParamRequest,start,end);
            if(null != deptList){
                for(RankDetail rankDetail : deptList){//设置最终毛利额为前台毛利额
                    rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());
                    rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                    rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
                    rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                }
            }
            //查询当天的数据(包含客流)
            List<RankDetail> curDeptList = csmb.queryCurCategoryRank(rankParamRequest, end,null);
            for(RankDetail cur : curDeptList) {//将历史和实时数据合并
                boolean flag = true;
                for (RankDetail his : deptList) {
                    if (his.getDeptName().equals(cur.getDeptName())) {
                        BigDecimal hisTatolSale = new BigDecimal(StringUtil.isEmpty(his.getTotalSales()) ? "0" : his.getTotalSales());
                        BigDecimal hisTatolRate = new BigDecimal(StringUtil.isEmpty(his.getTotalFrontDeskRate()) ? "0"
                                : his.getTotalFrontDeskRate());
                        BigDecimal hisTatolCompareRate = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRate()) ? "0"
                                : his.getTotalCompareRate());
                        BigDecimal hisTatolCompareSale = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSales()) ? "0" : his.getTotalCompareSales());

                        BigDecimal hisTatolSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalSalesIn()) ? "0" : his.getTotalSalesIn());
                        BigDecimal hisTatolRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalFrontDeskRateIn()) ? "0"
                                : his.getTotalFrontDeskRateIn());
                        BigDecimal hisTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRateIn()) ? "0"
                                : his.getTotalCompareRateIn());
                        BigDecimal hisTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSalesIn()) ? "0" : his.getTotalCompareSalesIn());

                        BigDecimal curTatolSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalSales()) ? "0"
                                : cur.getTotalSales());
                        BigDecimal curTatolCompareSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSales()) ? "0"
                                : cur.getTotalCompareSales());
                        BigDecimal curTatolRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalRate()) ? "0"
                                : cur.getTotalRate());
                        BigDecimal curTatolCompareRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRate()) ? "0"
                                : cur.getTotalCompareRate());

                        BigDecimal curTatolSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalSalesIn()) ? "0"
                                : cur.getTotalSalesIn());
                        BigDecimal curTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSalesIn()) ? "0"
                                : cur.getTotalCompareSalesIn());
                        BigDecimal curTatolRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalRateIn()) ? "0"
                                : cur.getTotalRateIn());
                        BigDecimal curTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRateIn()) ? "0"
                                : cur.getTotalCompareRateIn());

                        his.setTotalRate(hisTatolRate.add(curTatolRate).toString());
                        his.setTotalSales(hisTatolSale.add(curTatolSale).toString());
                        his.setTotalCompareSales(hisTatolCompareSale.add(curTatolCompareSale).toString());
                        his.setTotalCompareRate(hisTatolCompareRate.add(curTatolCompareRate).toString());

                        his.setTotalRateIn(hisTatolRateIn.add(curTatolRateIn).toString());
                        his.setTotalSalesIn(hisTatolSaleIn.add(curTatolSaleIn).toString());
                        his.setTotalCompareSalesIn(hisTatolCompareSaleIn.add(curTatolCompareSaleIn).toString());
                        his.setTotalCompareRateIn(hisTatolCompareRateIn.add(curTatolCompareRateIn).toString());
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    deptList.add(cur);
                }
            }
        }else{
            deptList = csmb.queryCurCategoryRank(rankParamRequest, start,end);
        }

        //只要涉及到历史时间时才查询历史品类客流，然后赋值给deptList
        if(DateUtils.getBetweenDay(start,new Date()) < 0){
            sumHisCategoryKl(rankParamRequest,deptList,start,end);
        }
        //查询同期数据
        deptSameList = queryRankSameInfoForCategory(rankParamRequest);

        //计算品类同比大于等于和小于零的数量
        if(null != deptList && deptList.size() > 0){
            if(null != deptSameList && deptSameList.size() > 0){
                for(RankDetail d : deptList){
                    for(RankDetail s : deptSameList){
                        if(d.getDeptName().equals(s.getDeptName())){
                            BigDecimal deptRateCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRate())?"0":d.getTotalCompareRate());
                            BigDecimal deptSaleCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSales())?"0":d.getTotalCompareSales());
                            BigDecimal deptSaleCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSales())?"0":s.getTotalCompareSales());
                            BigDecimal deptRateCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRate())?"0":s.getTotalCompareRate());
                            BigDecimal deptRateCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRateIn())?"0":d.getTotalCompareRateIn());
                            BigDecimal deptSaleCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSalesIn())?"0":d.getTotalCompareSalesIn());
                            BigDecimal deptSaleCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSalesIn())?"0":s.getTotalCompareSalesIn());
                            BigDecimal deptRateCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRateIn())?"0":s.getTotalCompareRateIn());

                            //计算销售额可比小于零的数量
                            if(deptSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValue.compareTo(deptSaleCompareSameValue) < 0){
                                downDeptSaleCount = downDeptSaleCount + 1;
                            }

                            //计算毛利额可比小于零的数量
                            if(deptRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptRateCompareValue.compareTo(deptRateCompareSameValue) < 0 ){
                                downDeptRateCount = downDeptRateCount + 1;
                            }

                            if(deptSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValue.compareTo(deptSaleCompareSameValue) >= 0){
                                upDeptSaleCount = upDeptSaleCount + 1;
                            }

                            if(deptRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptRateCompareValue.compareTo(deptRateCompareSameValue) >= 0 ){
                                upDeptRateCount = upDeptRateCount + 1;
                            }

                            if(deptSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValue.compareTo(deptSaleCompareSameValue) < 0){
                                downDeptSaleCount = downDeptSaleCount + 1;
                            }

                            //计算销售额可比小于零的数量
                            if(deptSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValueIn.compareTo(deptSaleCompareSameValueIn) < 0){
                                downDeptSaleCountIn = downDeptSaleCountIn + 1;
                            }

                            //计算毛利额可比小于零的数量
                            if(deptRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && deptRateCompareValueIn.compareTo(deptRateCompareSameValueIn) < 0 ){
                                downDeptRateCountIn = downDeptRateCountIn + 1;
                            }

                            if(deptSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && deptSaleCompareValueIn.compareTo(deptSaleCompareSameValueIn) >= 0){
                                upDeptSaleCountIn = upDeptSaleCountIn + 1;
                            }

                            if(deptRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && deptRateCompareValueIn.compareTo(deptRateCompareSameValueIn) >= 0 ){
                                upDeptRateCountIn = upDeptRateCountIn + 1;
                            }
                            break;
                        }
                    }

                }
            }
        }
        response.setDeptSaleDownNum(String.valueOf(downDeptSaleCount));
        response.setDeptSaleUpNum(String.valueOf(upDeptSaleCount));
        response.setDeptRateDownNum(String.valueOf(downDeptRateCount));
        response.setDeptRateUpNum(String.valueOf(upDeptRateCount));

        response.setDeptSaleDownNumIn(String.valueOf(downDeptSaleCountIn));
        response.setDeptSaleUpNumIn(String.valueOf(upDeptSaleCountIn));
        response.setDeptRateDownNumIn(String.valueOf(downDeptRateCountIn));
        response.setDeptRateUpNumIn(String.valueOf(upDeptRateCountIn));
        return response;
    }

    /**
     * 根据下级推上级
     * @param request
     * @param orgList
     */
    private void setUpGradeByDownGrade(PageRequest request, List<OrganizationForFresh> orgList){
        String storeIds = request.getStoreId();
        if(StringUtils.isNotEmpty(storeIds)){
            String[] arr = storeIds.split(",");
            List<String> provinceIList = new ArrayList<>();
            StringBuilder provinceSb = new StringBuilder();
            List<String> areaIdList = new ArrayList<>();
            StringBuilder areaSb = new StringBuilder();
            for(int i=0; i < arr.length; i++){
                for(OrganizationForFresh org : orgList){
                    if(arr[i].equals(org.getStoreId())){
                        if(!provinceIList.contains(arr[i])){
                            provinceIList.add(org.getProvinceId());
                            provinceSb.append(org.getProvinceId()).append(",");
                        }
                        if(!areaIdList.contains(arr[i])){
                            areaIdList.add(org.getAreaId());
                            areaSb.append(org.getAreaId()).append(",");
                        }
                        break;
                    }
                }
            }
            if(provinceIList.size() > 0){
                String idStr = provinceSb.toString();
                request.setProvinceId(idStr.substring(0,idStr.lastIndexOf(",")));
                request.setProvinceIds(provinceIList);
            }
            if(areaIdList.size() > 0){
                String idStr = areaSb.toString();
                request.setAreaId(idStr.substring(0,idStr.lastIndexOf(",")));
                request.setAreaIds(areaIdList);
            }
        }
    }

    /**
     * 查询门店排行榜
     * @param rankParamRequest
     * @return
     * @throws Exception
     */
    private RankInfoResponse queryStoreRankList(RankParamRequest rankParamRequest) throws Exception{
        RankInfoResponse response = new RankInfoResponse();
        List<RankDetail> storeList = new ArrayList<>();
        List<RankDetail> storeSameList = new ArrayList<>();
        long upStoreSaleCount = 0;
        long downStoreSaleCount = 0;
        long upStoreRateCount = 0;
        long downStoreRateCount = 0;

        long upStoreSaleCountIn = 0;
        long downStoreSaleCountIn = 0;
        long upStoreRateCountIn = 0;
        long downStoreRateCountIn = 0;
        //查询数据
        if((rankParamRequest.getEnd() == null && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) < 0)
                || (null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) < 0) ){
            List<RankDetail> kl = null;
            try{
                //查询客流
                changeZyDataSource();
                kl = zy.queryHisRankDetail(rankParamRequest, 4);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            storeList = queryHisRankStoreDetailInfo(rankParamRequest, 4);
            if(null != storeList && null != kl){
                setHisRankKlForStore(storeList,kl);
            }
        }else if(null != rankParamRequest.getEnd()
                && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0){
            List<RankDetail> kl = null;
            try{
                //查询客流
                changeZyDataSource();
                kl = zy.queryHisRankDetail(rankParamRequest, 4);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            storeList = queryHisRankStoreDetailInfo(rankParamRequest, 4);
            if(null != storeList && null != kl){
                setHisRankKlForStore(storeList,kl);
            }

            List<RankDetail> curStoreList = queryCurRankStoreDetailInfo(rankParamRequest, 1);
            for(RankDetail cur : curStoreList){
                boolean flag = true;
                for(RankDetail his : storeList){
                    if(his.getStoreId().equals(cur.getStoreId())){
                        BigDecimal hisTatolSale = new BigDecimal(StringUtil.isEmpty(his.getTotalSales())?"0":his.getTotalSales());
                        BigDecimal hisTatolRate = new BigDecimal(StringUtil.isEmpty(his.getTotalRate())?"0":his.getTotalRate());
                        BigDecimal curTatolSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalSales())?"0":cur.getTotalSales());
                        BigDecimal curTatolRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalRate())?"0":cur.getTotalRate());
                        BigDecimal curTatolCompareRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRate())?"0":cur.getTotalCompareRate());
                        BigDecimal curTatolCompareSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSales())?"0":cur.getTotalCompareSales());
                        BigDecimal hisTatolCompareSale = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSales())?"0":his.getTotalCompareSales());
                        BigDecimal hisTatolComapareRate = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRate())?"0":his.getTotalCompareRate());
                        BigDecimal hisTatolSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalSalesIn())?"0":his.getTotalSalesIn());
                        BigDecimal hisTatolRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalRateIn())?"0":his.getTotalRateIn());
                        BigDecimal curTatolSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalSalesIn())?"0":cur.getTotalSalesIn());
                        BigDecimal curTatolRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalRateIn())?"0":cur.getTotalRateIn());
                        BigDecimal curTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRateIn())?"0":cur.getTotalCompareRateIn());
                        BigDecimal curTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSalesIn())?"0":cur.getTotalCompareSalesIn());
                        BigDecimal hisTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSalesIn())?"0":his.getTotalCompareSalesIn());
                        BigDecimal hisTatolComapareRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRateIn())?"0":his.getTotalCompareRateIn());
                        BigDecimal curCustomerNum = new BigDecimal(StringUtil.isEmpty(cur.getCustomerNum())?"0":cur.getCustomerNum());
                        BigDecimal hisCustomerNum = new BigDecimal(StringUtil.isEmpty(his.getCustomerNum())?"0":his.getCustomerNum());

                        his.setTotalRate(hisTatolRate.add(curTatolRate).toString());
                        his.setTotalSales(hisTatolSale.add(curTatolSale).toString());
                        his.setTotalCompareRate(hisTatolComapareRate.add(curTatolCompareRate).toString());
                        his.setTotalCompareSales(hisTatolCompareSale.add(curTatolCompareSale).toString());

                        his.setTotalRateIn(hisTatolRateIn.add(curTatolRateIn).toString());
                        his.setTotalSalesIn(hisTatolSaleIn.add(curTatolSaleIn).toString());
                        his.setTotalCompareRateIn(hisTatolComapareRateIn.add(curTatolCompareRateIn).toString());
                        his.setTotalCompareSalesIn(hisTatolCompareSaleIn.add(curTatolCompareSaleIn).toString());

                        his.setCustomerNum(hisCustomerNum.add(curCustomerNum).toString());
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    storeList.add(cur);
                }
            }
        }else{
            storeList = queryCurRankStoreDetailInfo(rankParamRequest, 1);
        }

        //查询同期数据
        storeSameList = queryRankSameInfoForStore(rankParamRequest);

        //计算门店同比大于等于和小于零的数量
        if(null != storeList && storeList.size() > 0){
            if(null != storeSameList && storeSameList.size() > 0){
                for(RankDetail d : storeList){
                    for(RankDetail s : storeSameList){
                        if(d.getStoreId().equals(s.getStoreId())){
                            BigDecimal storeSaleValue = new BigDecimal(StringUtils.isEmpty(d.getTotalSales())?"0":d.getTotalSales());
                            BigDecimal storeRateValue = new BigDecimal(StringUtils.isEmpty(d.getTotalRate())?"0":d.getTotalRate());
                            BigDecimal storeRateCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRate())?"0":d.getTotalCompareRate());
                            BigDecimal storeSaleCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSales())?"0":d.getTotalCompareSales());

                            BigDecimal storeSaleSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalSales())?"0":s.getTotalSales());
                            BigDecimal storeRateSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalRate())?"0":s.getTotalRate());
                            BigDecimal storeSaleCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSales())?"0":s.getTotalCompareSales());
                            BigDecimal storeRateCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRate())?"0":s.getTotalCompareRate());

                            BigDecimal storeSaleValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalSalesIn())?"0":d.getTotalSalesIn());
                            BigDecimal storeRateValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalRateIn())?"0":d.getTotalRateIn());
                            BigDecimal storeRateCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRateIn())?"0":d.getTotalCompareRateIn());
                            BigDecimal storeSaleCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSalesIn())?"0":d.getTotalCompareSalesIn());

                            BigDecimal storeSaleSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalSalesIn())?"0":s.getTotalSalesIn());
                            BigDecimal storeRateSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalRateIn())?"0":s.getTotalRateIn());
                            BigDecimal storeSaleCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSalesIn())?"0":s.getTotalCompareSalesIn());
                            BigDecimal storeRateCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRateIn())?"0":s.getTotalCompareRateIn());

                            //计算销售额同比
                            if(storeSaleSameValue.compareTo(new BigDecimal("0")) != 0){
                                d.setSaleSameRate(storeSaleValue.subtract(storeSaleSameValue).divide(storeSaleSameValue, 4, BigDecimal.ROUND_HALF_UP).toString());
                            }
                            if(storeSaleSameValueIn.compareTo(new BigDecimal("0")) != 0){
                                d.setSaleSameRateIn(storeSaleValueIn.subtract(storeSaleSameValueIn).divide(storeSaleSameValueIn, 4, BigDecimal.ROUND_HALF_UP).toString());
                            }
                            //计算销售额可比
                            if(storeSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0){
                                d.setSaleCompareRate(storeSaleCompareValue.divide(storeSaleCompareSameValue,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }
                            if(storeSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0){
                                d.setSaleCompareRateIn(storeSaleCompareValueIn.divide(storeSaleCompareSameValueIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算毛利额同比
                            if(storeRateSameValue.compareTo(new BigDecimal("0")) != 0){
                                d.setProfitSameRate(storeRateValue.subtract(storeRateSameValue).divide(storeRateSameValue, 4, BigDecimal.ROUND_HALF_UP).toString());
                            }
                            if(storeRateSameValueIn.compareTo(new BigDecimal("0")) != 0){
                                d.setProfitSameRateIn(storeRateValueIn.subtract(storeRateSameValueIn).divide(storeRateSameValueIn, 4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            //计算毛利额可比
                            if(storeRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0){
                                d.setProfitCompareRate(storeRateCompareValue.divide(storeRateCompareSameValue,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }
                            if(storeRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0){
                                d.setProfitCompareRateIn(storeRateCompareValueIn.divide(storeRateCompareSameValueIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算销售额可比小于零的数量
                            if(storeSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && storeSaleCompareValue.compareTo(storeSaleCompareSameValue) < 0){
                                downStoreSaleCount = downStoreSaleCount + 1;
                            }

                            if(storeSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && storeSaleCompareValueIn.compareTo(storeSaleCompareSameValueIn) < 0){
                                downStoreSaleCountIn = downStoreSaleCountIn + 1;
                            }

                            //计算毛利额可比小于零的数量
                            if(storeRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && storeRateCompareValue.compareTo(storeRateCompareSameValue) < 0){
                                downStoreRateCount = downStoreRateCount + 1;
                            }
                            if(storeRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && storeRateCompareValueIn.compareTo(storeRateCompareSameValueIn) < 0){
                                downStoreRateCountIn = downStoreRateCountIn + 1;
                            }

                            if(storeSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && storeSaleCompareValue.compareTo(storeSaleCompareSameValue) >= 0){
                                upStoreSaleCount = upStoreSaleCount + 1;
                            }

                            if(storeSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && storeSaleCompareValueIn.compareTo(storeSaleCompareSameValueIn) >= 0){
                                upStoreSaleCountIn = upStoreSaleCountIn + 1;
                            }

                            if(storeRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && storeRateCompareValue.compareTo(storeRateCompareSameValue) >= 0){
                                upStoreRateCount = upStoreRateCount + 1;
                            }
                            if(storeRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && storeRateCompareValueIn.compareTo(storeRateCompareSameValueIn) >= 0){
                                upStoreRateCountIn = upStoreRateCountIn + 1;
                            }
                            break;
                        }
                    }
                }
            }
        }
        response.setStoreSaleDownNum(String.valueOf(downStoreSaleCount));
        response.setStoreSaleUpNum(String.valueOf(upStoreSaleCount));

        response.setStoreRateDownNum(String.valueOf(downStoreRateCount));
        response.setStoreRateUpNum(String.valueOf(upStoreRateCount));

        response.setStoreSaleDownNumIn(String.valueOf(downStoreSaleCountIn));
        response.setStoreSaleUpNumIn(String.valueOf(upStoreSaleCountIn));

        response.setStoreRateDownNumIn(String.valueOf(downStoreRateCountIn));
        response.setStoreRateUpNumIn(String.valueOf(upStoreRateCountIn));
        //门店排序
        if(storeList.size() > 0){
            //门店销售额排序
            List<RankDetail> saleList =  storeList.stream().sorted(new Comparator<RankDetail>() {
                @Override
                public int compare(RankDetail o1, RankDetail o2) {
                    BigDecimal sales1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalSales())?"0":o1.getTotalSales());
                    BigDecimal sales2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalSales())?"0":o2.getTotalSales());
                    return sales2.compareTo(sales1);
                }
            }).collect(Collectors.toList());
            List<RankDetailResponse> sales = new ArrayList<>();
            for(RankDetail rankDetail : saleList){
                RankDetailResponse rankDetailResponse = new RankDetailResponse();
                BeanUtils.copyProperties(rankDetail, rankDetailResponse);
                sales.add(rankDetailResponse);
            }
            response.setStoreSaleList(sales);

            //门店毛利额排序
            List<RankDetail> rateList =  storeList.stream().sorted(new Comparator<RankDetail>() {
                @Override
                public int compare(RankDetail o1, RankDetail o2) {
                    BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalRate())?"0":o1.getTotalRate());
                    BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalRate())?"0":o2.getTotalRate());
                    return rate2.compareTo(rate1);
                }
            }).collect(Collectors.toList());

            List<RankDetailResponse> rate = new ArrayList<>();
            for(RankDetail rankDetail : rateList){
                RankDetailResponse rankDetailResponse = new RankDetailResponse();
                BeanUtils.copyProperties(rankDetail, rankDetailResponse);
                rate.add(rankDetailResponse);
            }
            response.setStoreProfitList(rate);
        }
        return response;
    }

    /**
     * 查询门店排行榜数量
     * @param rankParamRequest
     * @return
     * @throws Exception
     */
    private RankInfoResponse queryStoreRankCount(RankParamRequest rankParamRequest) throws Exception{
        RankInfoResponse response = new RankInfoResponse();
        List<RankDetail> storeList = new ArrayList<>();
        List<RankDetail> storeSameList = new ArrayList<>();
        long upStoreSaleCount = 0;
        long downStoreSaleCount = 0;
        long upStoreRateCount = 0;
        long downStoreRateCount = 0;

        long upStoreSaleCountIn = 0;
        long downStoreSaleCountIn = 0;
        long upStoreRateCountIn = 0;
        long downStoreRateCountIn = 0;
        //查询数据
        if((rankParamRequest.getEnd() == null && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) < 0)
                || (null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) < 0) ){
            storeList = queryHisRankStoreDetailInfo(rankParamRequest, 4);
            if(null != storeList){
                for(RankDetail value : storeList){
                    value.setTotalRate(value.getTotalFrontDeskRate());
                    value.setTotalCompareRate(value.getTotalCompareFrontDeskRate());
                    value.setTotalRateIn(value.getTotalFrontDeskRateIn());
                    value.setTotalCompareRateIn(value.getTotalCompareFrontDeskRateIn());
                }
            }
        }else if(null != rankParamRequest.getEnd()
                && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0){
            storeList = queryHisRankStoreDetailInfo(rankParamRequest, 4);
            if(null != storeList){
                for(RankDetail value : storeList){
                    value.setTotalRate(value.getTotalFrontDeskRate());
                    value.setTotalCompareRate(value.getTotalCompareFrontDeskRate());
                    value.setTotalRateIn(value.getTotalFrontDeskRateIn());
                    value.setTotalCompareRateIn(value.getTotalCompareFrontDeskRateIn());
                }
            }

            List<RankDetail> curStoreList = queryCurRankStoreDetailInfo(rankParamRequest, 1);
            for(RankDetail cur : curStoreList){
                boolean flag = true;
                for(RankDetail his : storeList){
                    if(his.getStoreId().equals(cur.getStoreId())){
                        BigDecimal hisTatolSale = new BigDecimal(StringUtil.isEmpty(his.getTotalSales())?"0":his.getTotalSales());
                        BigDecimal hisTatolRate = new BigDecimal(StringUtil.isEmpty(his.getTotalRate())?"0":his.getTotalRate());
                        BigDecimal curTatolSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalSales())?"0":cur.getTotalSales());
                        BigDecimal curTatolRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalRate())?"0":cur.getTotalRate());
                        BigDecimal curTatolCompareRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRate())?"0":cur.getTotalCompareRate());
                        BigDecimal curTatolCompareSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSales())?"0":cur.getTotalCompareSales());
                        BigDecimal hisTatolCompareSale = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSales())?"0":his.getTotalCompareSales());
                        BigDecimal hisTatolComapareRate = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRate())?"0":his.getTotalCompareRate());
                        BigDecimal hisTatolSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalSalesIn())?"0":his.getTotalSalesIn());
                        BigDecimal hisTatolRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalRateIn())?"0":his.getTotalRateIn());
                        BigDecimal curTatolSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalSalesIn())?"0":cur.getTotalSalesIn());
                        BigDecimal curTatolRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalRateIn())?"0":cur.getTotalRateIn());
                        BigDecimal curTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRateIn())?"0":cur.getTotalCompareRateIn());
                        BigDecimal curTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSalesIn())?"0":cur.getTotalCompareSalesIn());
                        BigDecimal hisTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSalesIn())?"0":his.getTotalCompareSalesIn());
                        BigDecimal hisTatolComapareRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRateIn())?"0":his.getTotalCompareRateIn());

                        his.setTotalRate(hisTatolRate.add(curTatolRate).toString());
                        his.setTotalSales(hisTatolSale.add(curTatolSale).toString());
                        his.setTotalCompareRate(hisTatolComapareRate.add(curTatolCompareRate).toString());
                        his.setTotalCompareSales(hisTatolCompareSale.add(curTatolCompareSale).toString());

                        his.setTotalRateIn(hisTatolRateIn.add(curTatolRateIn).toString());
                        his.setTotalSalesIn(hisTatolSaleIn.add(curTatolSaleIn).toString());
                        his.setTotalCompareRateIn(hisTatolComapareRateIn.add(curTatolCompareRateIn).toString());
                        his.setTotalCompareSalesIn(hisTatolCompareSaleIn.add(curTatolCompareSaleIn).toString());
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    storeList.add(cur);
                }
            }
        }else{
            storeList = queryCurRankStoreDetailInfo(rankParamRequest, 1);
        }

        //查询同期数据
        storeSameList = queryRankSameInfoForStore(rankParamRequest);

        //计算门店同比大于等于和小于零的数量
        if(null != storeList && storeList.size() > 0){
            if(null != storeSameList && storeSameList.size() > 0){
                for(RankDetail d : storeList){
                    for(RankDetail s : storeSameList){
                        if(d.getStoreId().equals(s.getStoreId())){
                            BigDecimal storeRateCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRate())?"0":d.getTotalCompareRate());
                            BigDecimal storeSaleCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSales())?"0":d.getTotalCompareSales());
                            BigDecimal storeSaleCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSales())?"0":s.getTotalCompareSales());
                            BigDecimal storeRateCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRate())?"0":s.getTotalCompareRate());
                            BigDecimal storeRateCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRateIn())?"0":d.getTotalCompareRateIn());
                            BigDecimal storeSaleCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSalesIn())?"0":d.getTotalCompareSalesIn());
                            BigDecimal storeSaleCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSalesIn())?"0":s.getTotalCompareSalesIn());
                            BigDecimal storeRateCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRateIn())?"0":s.getTotalCompareRateIn());

                            //计算销售额可比小于零的数量
                            if(storeSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && storeSaleCompareValue.compareTo(storeSaleCompareSameValue) < 0){
                                downStoreSaleCount = downStoreSaleCount + 1;
                            }

                            if(storeSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && storeSaleCompareValueIn.compareTo(storeSaleCompareSameValueIn) < 0){
                                downStoreSaleCountIn = downStoreSaleCountIn + 1;
                            }

                            //计算毛利额可比小于零的数量
                            if(storeRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && storeRateCompareValue.compareTo(storeRateCompareSameValue) < 0){
                                downStoreRateCount = downStoreRateCount + 1;
                            }
                            if(storeRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && storeRateCompareValueIn.compareTo(storeRateCompareSameValueIn) < 0){
                                downStoreRateCountIn = downStoreRateCountIn + 1;
                            }

                            if(storeSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && storeSaleCompareValue.compareTo(storeSaleCompareSameValue) >= 0){
                                upStoreSaleCount = upStoreSaleCount + 1;
                            }

                            if(storeSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && storeSaleCompareValueIn.compareTo(storeSaleCompareSameValueIn) >= 0){
                                upStoreSaleCountIn = upStoreSaleCountIn + 1;
                            }

                            if(storeRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && storeRateCompareValue.compareTo(storeRateCompareSameValue) >= 0){
                                upStoreRateCount = upStoreRateCount + 1;
                            }
                            if(storeRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && storeRateCompareValueIn.compareTo(storeRateCompareSameValueIn) >= 0){
                                upStoreRateCountIn = upStoreRateCountIn + 1;
                            }
                            break;
                        }
                    }
                }
            }
        }
        response.setStoreSaleDownNum(String.valueOf(downStoreSaleCount));
        response.setStoreSaleUpNum(String.valueOf(upStoreSaleCount));

        response.setStoreRateDownNum(String.valueOf(downStoreRateCount));
        response.setStoreRateUpNum(String.valueOf(upStoreRateCount));

        response.setStoreSaleDownNumIn(String.valueOf(downStoreSaleCountIn));
        response.setStoreSaleUpNumIn(String.valueOf(upStoreSaleCountIn));

        response.setStoreRateDownNumIn(String.valueOf(downStoreRateCountIn));
        response.setStoreRateUpNumIn(String.valueOf(upStoreRateCountIn));
        return response;
    }

    /**
     * 查询区域排行榜
     * @param rankParamRequest
     * @return
     */
    private RankInfoResponse queryAreaRankList(RankParamRequest rankParamRequest) throws Exception {
        RankInfoResponse response = new RankInfoResponse();
        List<RankDetail> areaList = new ArrayList<>();
        List<RankDetail> sameAreaList = new ArrayList<>();
        long upAreaSaleCount = 0;
        long downAreaSaleCount = 0;
        long upAreaRateCount = 0;
        long downAreaRateCount = 0;

        long upAreaSaleCountIn = 0;
        long downAreaSaleCountIn = 0;
        long upAreaRateCountIn = 0;
        long downAreaRateCountIn = 0;
        Date start = rankParamRequest.getStart();
        Date end = rankParamRequest.getEnd();
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        //反推省份和区域
        setUpGradeByDownGrade(rankParamRequest,orgList);
        //查询数据
        if((rankParamRequest.getEnd() == null && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) < 0)
                || (null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) < 0) ){
            List<RankDetail> kl = null;
            try{
                //查询客流
                changeZyDataSource();
                kl = zy.queryHisAreaRankDetail(rankParamRequest);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            areaList = csmb.queryHisAreaRankDetail(rankParamRequest,start,end);
            if(null != areaList && null != kl){
                setHisRankKlForArea(areaList,kl);
            }
        }else if(null != rankParamRequest.getEnd()
                && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0){
            List<RankDetail> kl = null;
            try{
                //查询客流
                changeZyDataSource();
                kl = zy.queryHisAreaRankDetail(rankParamRequest);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            areaList = csmb.queryHisAreaRankDetail(rankParamRequest,start,end);
            if(null != areaList && null != kl){
                setHisRankKlForArea(areaList,kl);
            }

            List<RankDetail> curAreaList = csmb.queryCurrentAreaRankDetail(rankParamRequest);
            for(RankDetail cur : curAreaList){
                boolean flag = true;
                for(RankDetail his : areaList){
                    if(his.getAreaId().equals(cur.getAreaId())){
                        BigDecimal hisTatolSale = new BigDecimal(StringUtil.isEmpty(his.getTotalSales())?"0":his.getTotalSales());
                        BigDecimal hisTatolRate = new BigDecimal(StringUtil.isEmpty(his.getTotalRate())?"0":his.getTotalRate());
                        BigDecimal curTatolSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalSales())?"0":cur.getTotalSales());
                        BigDecimal curTatolRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalRate())?"0":cur.getTotalRate());
                        BigDecimal curTatolCompareRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRate())?"0":cur.getTotalCompareRate());
                        BigDecimal curTatolCompareSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSales())?"0":cur.getTotalCompareSales());
                        BigDecimal hisTatolCompareSale = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSales())?"0":his.getTotalCompareSales());
                        BigDecimal hisTatolComapareRate = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRate())?"0":his.getTotalCompareRate());
                        BigDecimal hisTatolSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalSalesIn())?"0":his.getTotalSalesIn());
                        BigDecimal hisTatolRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalRateIn())?"0":his.getTotalRateIn());
                        BigDecimal curTatolSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalSalesIn())?"0":cur.getTotalSalesIn());
                        BigDecimal curTatolRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalRateIn())?"0":cur.getTotalRateIn());
                        BigDecimal curTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRateIn())?"0":cur.getTotalCompareRateIn());
                        BigDecimal curTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSalesIn())?"0":cur.getTotalCompareSalesIn());
                        BigDecimal hisTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSalesIn())?"0":his.getTotalCompareSalesIn());
                        BigDecimal hisTatolComapareRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRateIn())?"0":his.getTotalCompareRateIn());
                        BigDecimal curCustomerNum = new BigDecimal(StringUtil.isEmpty(cur.getCustomerNum())?"0":cur.getCustomerNum());
                        BigDecimal hisCustomerNum = new BigDecimal(StringUtil.isEmpty(his.getCustomerNum())?"0":his.getCustomerNum());

                        his.setTotalRate(hisTatolRate.add(curTatolRate).toString());
                        his.setTotalSales(hisTatolSale.add(curTatolSale).toString());
                        his.setTotalCompareRate(hisTatolComapareRate.add(curTatolCompareRate).toString());
                        his.setTotalCompareSales(hisTatolCompareSale.add(curTatolCompareSale).toString());

                        his.setTotalRateIn(hisTatolRateIn.add(curTatolRateIn).toString());
                        his.setTotalSalesIn(hisTatolSaleIn.add(curTatolSaleIn).toString());
                        his.setTotalCompareRateIn(hisTatolComapareRateIn.add(curTatolCompareRateIn).toString());
                        his.setTotalCompareSalesIn(hisTatolCompareSaleIn.add(curTatolCompareSaleIn).toString());

                        his.setCustomerNum(hisCustomerNum.add(curCustomerNum).toString());
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    areaList.add(cur);
                }
            }
        }else{
            areaList = csmb.queryCurrentAreaRankDetail(rankParamRequest);
        }

        //查询同期数据
        sameAreaList = queryRankSameInfoForArea(rankParamRequest);

        //计算门店同比大于等于和小于零的数量
        if(null != areaList && areaList.size() > 0){
            if(null != sameAreaList && sameAreaList.size() > 0){
                for(RankDetail d : areaList){
                    for(RankDetail s : sameAreaList){
                        if(d.getAreaId().equals(s.getAreaId())){
                            BigDecimal areaSaleValue = new BigDecimal(StringUtils.isEmpty(d.getTotalSales())?"0":d.getTotalSales());
                            BigDecimal areaRateValue = new BigDecimal(StringUtils.isEmpty(d.getTotalRate())?"0":d.getTotalRate());
                            BigDecimal areaRateCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRate())?"0":d.getTotalCompareRate());
                            BigDecimal areaSaleCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSales())?"0":d.getTotalCompareSales());

                            BigDecimal areaSaleSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalSales())?"0":s.getTotalSales());
                            BigDecimal areaRateSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalRate())?"0":s.getTotalRate());
                            BigDecimal areaSaleCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSales())?"0":s.getTotalCompareSales());
                            BigDecimal areaRateCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRate())?"0":s.getTotalCompareRate());

                            BigDecimal areaSaleValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalSalesIn())?"0":d.getTotalSalesIn());
                            BigDecimal areaRateValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalRateIn())?"0":d.getTotalRateIn());
                            BigDecimal areaRateCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRateIn())?"0":d.getTotalCompareRateIn());
                            BigDecimal areaSaleCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSalesIn())?"0":d.getTotalCompareSalesIn());

                            BigDecimal areaSaleSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalSalesIn())?"0":s.getTotalSalesIn());
                            BigDecimal areaRateSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalRateIn())?"0":s.getTotalRateIn());
                            BigDecimal areaSaleCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSalesIn())?"0":s.getTotalCompareSalesIn());
                            BigDecimal areaRateCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRateIn())?"0":s.getTotalCompareRateIn());

                            //计算销售额同比
                            if(areaSaleSameValue.compareTo(new BigDecimal("0")) != 0){
                                d.setSaleSameRate(areaSaleValue.subtract(areaSaleSameValue).divide(areaSaleSameValue, 4, BigDecimal.ROUND_HALF_UP).toString());
                            }
                            if(areaSaleSameValueIn.compareTo(new BigDecimal("0")) != 0){
                                d.setSaleSameRateIn(areaSaleValueIn.subtract(areaSaleSameValueIn).divide(areaSaleSameValueIn, 4, BigDecimal.ROUND_HALF_UP).toString());
                            }
                            //计算销售额可比
                            if(areaSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0){
                                d.setSaleCompareRate(areaSaleCompareValue.divide(areaSaleCompareSameValue,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }
                            if(areaSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0){
                                d.setSaleCompareRateIn(areaSaleCompareValueIn.divide(areaSaleCompareSameValueIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算毛利额同比
                            if(areaRateSameValue.compareTo(new BigDecimal("0")) != 0){
                                d.setProfitSameRate(areaRateValue.subtract(areaRateSameValue).divide(areaRateSameValue, 4, BigDecimal.ROUND_HALF_UP).toString());
                            }
                            if(areaRateSameValueIn.compareTo(new BigDecimal("0")) != 0){
                                d.setProfitSameRateIn(areaRateValueIn.subtract(areaRateSameValueIn).divide(areaRateSameValueIn, 4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            //计算毛利额可比
                            if(areaRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0){
                                d.setProfitCompareRate(areaRateCompareValue.divide(areaRateCompareSameValue,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }
                            if(areaRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0){
                                d.setProfitCompareRateIn(areaRateCompareValueIn.divide(areaRateCompareSameValueIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算销售额可比小于零的数量
                            if(areaSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && areaSaleCompareValue.compareTo(areaSaleCompareSameValue) < 0){
                                downAreaSaleCount = downAreaSaleCount + 1;
                            }

                            if(areaSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && areaSaleCompareValueIn.compareTo(areaSaleCompareSameValueIn) < 0){
                                downAreaSaleCountIn = downAreaSaleCountIn + 1;
                            }

                            //计算毛利额可比小于零的数量
                            if(areaRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && areaRateCompareValue.compareTo(areaRateCompareSameValue) < 0){
                                downAreaRateCount = downAreaRateCount + 1;
                            }
                            if(areaRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && areaRateCompareValueIn.compareTo(areaRateCompareSameValueIn) < 0){
                                downAreaRateCountIn = downAreaRateCountIn + 1;
                            }

                            if(areaSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && areaSaleCompareValue.compareTo(areaSaleCompareSameValue) >= 0){
                                upAreaSaleCount = upAreaSaleCount + 1;
                            }

                            if(areaSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && areaSaleCompareValueIn.compareTo(areaSaleCompareSameValueIn) >= 0){
                                upAreaSaleCountIn = upAreaSaleCountIn + 1;
                            }

                            if(areaRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && areaRateCompareValue.compareTo(areaRateCompareSameValue) >= 0){
                                upAreaRateCount = upAreaRateCount + 1;
                            }
                            if(areaRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && areaRateCompareValueIn.compareTo(areaRateCompareSameValueIn) >= 0){
                                upAreaRateCountIn = upAreaRateCountIn + 1;
                            }
                            break;
                        }
                    }
                }
            }
        }
        response.setAreaSaleDownNum(String.valueOf(downAreaSaleCount));
        response.setAreaSaleUpNum(String.valueOf(upAreaSaleCount));

        response.setAreaRateDownNum(String.valueOf(downAreaRateCount));
        response.setAreaRateUpNum(String.valueOf(upAreaRateCount));

        response.setAreaSaleDownNumIn(String.valueOf(downAreaSaleCountIn));
        response.setAreaSaleUpNumIn(String.valueOf(upAreaSaleCountIn));

        response.setAreaRateDownNumIn(String.valueOf(downAreaRateCountIn));
        response.setAreaRateUpNumIn(String.valueOf(upAreaRateCountIn));
        //门店排序
        if(areaList.size() > 0){
            //门店销售额排序
            List<RankDetail> saleList =  areaList.stream().sorted(new Comparator<RankDetail>() {
                @Override
                public int compare(RankDetail o1, RankDetail o2) {
                    BigDecimal sales1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalSales())?"0":o1.getTotalSales());
                    BigDecimal sales2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalSales())?"0":o2.getTotalSales());
                    return sales2.compareTo(sales1);
                }
            }).collect(Collectors.toList());
            List<RankDetailResponse> sales = new ArrayList<>();
            for(RankDetail rankDetail : saleList){
                RankDetailResponse rankDetailResponse = new RankDetailResponse();
                BeanUtils.copyProperties(rankDetail, rankDetailResponse);
                sales.add(rankDetailResponse);
            }
            response.setAreaSaleList(sales);

            //门店毛利额排序
            List<RankDetail> rateList =  areaList.stream().sorted(new Comparator<RankDetail>() {
                @Override
                public int compare(RankDetail o1, RankDetail o2) {
                    BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalRate())?"0":o1.getTotalRate());
                    BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalRate())?"0":o2.getTotalRate());
                    return rate2.compareTo(rate1);
                }
            }).collect(Collectors.toList());

            List<RankDetailResponse> rate = new ArrayList<>();
            for(RankDetail rankDetail : rateList){
                RankDetailResponse rankDetailResponse = new RankDetailResponse();
                BeanUtils.copyProperties(rankDetail, rankDetailResponse);
                rate.add(rankDetailResponse);
            }
            response.setAreaProfitList(rate);
        }
        return response;
    }

    /**
     * 查询区域排行榜数量
     * @param rankParamRequest
     * @return
     */
    private RankInfoResponse queryAreaRankCount(RankParamRequest rankParamRequest) throws Exception {
        RankInfoResponse response = new RankInfoResponse();
        List<RankDetail> areaList = new ArrayList<>();
        List<RankDetail> sameAreaList = new ArrayList<>();
        long upAreaSaleCount = 0;
        long downAreaSaleCount = 0;
        long upAreaRateCount = 0;
        long downAreaRateCount = 0;

        long upAreaSaleCountIn = 0;
        long downAreaSaleCountIn = 0;
        long upAreaRateCountIn = 0;
        long downAreaRateCountIn = 0;
        Date start = rankParamRequest.getStart();
        Date end = rankParamRequest.getEnd();
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        //反推省份和区域
        setUpGradeByDownGrade(rankParamRequest,orgList);
        //查询数据
        if((rankParamRequest.getEnd() == null && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) < 0)
                || (null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) < 0) ){
            areaList = csmb.queryHisAreaRankDetail(rankParamRequest,start,end);
            if(null != areaList){
                for(RankDetail value : areaList){
                    value.setTotalRate(value.getTotalFrontDeskRate());
                    value.setTotalCompareRate(value.getTotalCompareFrontDeskRate());
                    value.setTotalRateIn(value.getTotalFrontDeskRateIn());
                    value.setTotalCompareRateIn(value.getTotalCompareFrontDeskRateIn());
                }
            }
        }else if(null != rankParamRequest.getEnd()
                && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0){
            areaList = csmb.queryHisAreaRankDetail(rankParamRequest,start,end);
            if(null != areaList){
                for(RankDetail value : areaList){
                    value.setTotalRate(value.getTotalFrontDeskRate());
                    value.setTotalCompareRate(value.getTotalCompareFrontDeskRate());
                    value.setTotalRateIn(value.getTotalFrontDeskRateIn());
                    value.setTotalCompareRateIn(value.getTotalCompareFrontDeskRateIn());
                }
            }

            List<RankDetail> curAreaList = csmb.queryCurrentAreaRankDetail(rankParamRequest);
            for(RankDetail cur : curAreaList){
                boolean flag = true;
                for(RankDetail his : areaList){
                    if(his.getAreaId().equals(cur.getAreaId())){
                        BigDecimal hisTatolSale = new BigDecimal(StringUtil.isEmpty(his.getTotalSales())?"0":his.getTotalSales());
                        BigDecimal hisTatolRate = new BigDecimal(StringUtil.isEmpty(his.getTotalRate())?"0":his.getTotalRate());
                        BigDecimal curTatolSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalSales())?"0":cur.getTotalSales());
                        BigDecimal curTatolRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalRate())?"0":cur.getTotalRate());
                        BigDecimal curTatolCompareRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRate())?"0":cur.getTotalCompareRate());
                        BigDecimal curTatolCompareSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSales())?"0":cur.getTotalCompareSales());
                        BigDecimal hisTatolCompareSale = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSales())?"0":his.getTotalCompareSales());
                        BigDecimal hisTatolComapareRate = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRate())?"0":his.getTotalCompareRate());
                        BigDecimal hisTatolSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalSalesIn())?"0":his.getTotalSalesIn());
                        BigDecimal hisTatolRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalRateIn())?"0":his.getTotalRateIn());
                        BigDecimal curTatolSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalSalesIn())?"0":cur.getTotalSalesIn());
                        BigDecimal curTatolRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalRateIn())?"0":cur.getTotalRateIn());
                        BigDecimal curTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRateIn())?"0":cur.getTotalCompareRateIn());
                        BigDecimal curTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSalesIn())?"0":cur.getTotalCompareSalesIn());
                        BigDecimal hisTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSalesIn())?"0":his.getTotalCompareSalesIn());
                        BigDecimal hisTatolComapareRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRateIn())?"0":his.getTotalCompareRateIn());

                        his.setTotalRate(hisTatolRate.add(curTatolRate).toString());
                        his.setTotalSales(hisTatolSale.add(curTatolSale).toString());
                        his.setTotalCompareRate(hisTatolComapareRate.add(curTatolCompareRate).toString());
                        his.setTotalCompareSales(hisTatolCompareSale.add(curTatolCompareSale).toString());

                        his.setTotalRateIn(hisTatolRateIn.add(curTatolRateIn).toString());
                        his.setTotalSalesIn(hisTatolSaleIn.add(curTatolSaleIn).toString());
                        his.setTotalCompareRateIn(hisTatolComapareRateIn.add(curTatolCompareRateIn).toString());
                        his.setTotalCompareSalesIn(hisTatolCompareSaleIn.add(curTatolCompareSaleIn).toString());
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    areaList.add(cur);
                }
            }
        }else{
            areaList = csmb.queryCurrentAreaRankDetail(rankParamRequest);
        }

        //查询同期数据
        sameAreaList = queryRankSameInfoForArea(rankParamRequest);

        //计算门店同比大于等于和小于零的数量
        if(null != areaList && areaList.size() > 0){
            if(null != sameAreaList && sameAreaList.size() > 0){
                for(RankDetail d : areaList){
                    for(RankDetail s : sameAreaList){
                        if(d.getAreaId().equals(s.getAreaId())){
                            BigDecimal areaRateCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRate())?"0":d.getTotalCompareRate());
                            BigDecimal areaSaleCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSales())?"0":d.getTotalCompareSales());
                            BigDecimal areaSaleCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSales())?"0":s.getTotalCompareSales());
                            BigDecimal areaRateCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRate())?"0":s.getTotalCompareRate());
                            BigDecimal areaRateCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRateIn())?"0":d.getTotalCompareRateIn());
                            BigDecimal areaSaleCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSalesIn())?"0":d.getTotalCompareSalesIn());
                            BigDecimal areaSaleCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSalesIn())?"0":s.getTotalCompareSalesIn());
                            BigDecimal areaRateCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRateIn())?"0":s.getTotalCompareRateIn());

                            //计算销售额可比小于零的数量
                            if(areaSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && areaSaleCompareValue.compareTo(areaSaleCompareSameValue) < 0){
                                downAreaSaleCount = downAreaSaleCount + 1;
                            }

                            if(areaSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && areaSaleCompareValueIn.compareTo(areaSaleCompareSameValueIn) < 0){
                                downAreaSaleCountIn = downAreaSaleCountIn + 1;
                            }

                            //计算毛利额可比小于零的数量
                            if(areaRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && areaRateCompareValue.compareTo(areaRateCompareSameValue) < 0){
                                downAreaRateCount = downAreaRateCount + 1;
                            }
                            if(areaRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && areaRateCompareValueIn.compareTo(areaRateCompareSameValueIn) < 0){
                                downAreaRateCountIn = downAreaRateCountIn + 1;
                            }

                            if(areaSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && areaSaleCompareValue.compareTo(areaSaleCompareSameValue) >= 0){
                                upAreaSaleCount = upAreaSaleCount + 1;
                            }

                            if(areaSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && areaSaleCompareValueIn.compareTo(areaSaleCompareSameValueIn) >= 0){
                                upAreaSaleCountIn = upAreaSaleCountIn + 1;
                            }

                            if(areaRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0 && areaRateCompareValue.compareTo(areaRateCompareSameValue) >= 0){
                                upAreaRateCount = upAreaRateCount + 1;
                            }
                            if(areaRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0 && areaRateCompareValueIn.compareTo(areaRateCompareSameValueIn) >= 0){
                                upAreaRateCountIn = upAreaRateCountIn + 1;
                            }
                            break;
                        }
                    }
                }
            }
        }
        response.setAreaSaleDownNum(String.valueOf(downAreaSaleCount));
        response.setAreaSaleUpNum(String.valueOf(upAreaSaleCount));

        response.setAreaRateDownNum(String.valueOf(downAreaRateCount));
        response.setAreaRateUpNum(String.valueOf(upAreaRateCount));

        response.setAreaSaleDownNumIn(String.valueOf(downAreaSaleCountIn));
        response.setAreaSaleUpNumIn(String.valueOf(upAreaSaleCountIn));

        response.setAreaRateDownNumIn(String.valueOf(downAreaRateCountIn));
        response.setAreaRateUpNumIn(String.valueOf(upAreaRateCountIn));
        return response;
    }

    /**
     * 查询大类排行榜
     * @param rankParamRequest
     * @return
     */
    private RankInfoResponse queryDeptRankList(RankParamRequest rankParamRequest) throws Exception {
        RankInfoResponse response = new RankInfoResponse();
        List<RankDetail> deptList = new ArrayList<>();
        List<RankDetail> deptSameList = new ArrayList<>();
        //查询数据
        if((rankParamRequest.getEnd() == null && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) < 0)
                || (null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) < 0) ){
            List<RankDetail> kl = csmb.queryHisRankDetailForDept(rankParamRequest);
            deptList = csmb.queryRankDetailForDept(rankParamRequest);
            if(null != deptList && null != kl){
                setHisRankKlForDept(deptList,kl);
            }
        }else if(null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0){
            List<RankDetail> kl = csmb.queryHisRankDetailForDept(rankParamRequest);
            deptList = csmb.queryRankDetailForDept(rankParamRequest);
            if(null != deptList && null != kl){
                setHisRankKlForDept(deptList,kl);
            }
            List<RankDetail> curDeptList = queryCurRankStoreDetailInfo(rankParamRequest, 2);
            for(RankDetail cur : curDeptList) {
                boolean flag = true;
                for (RankDetail his : deptList) {
                    if (his.getDeptId().equals(cur.getDeptId())) {
                        BigDecimal hisTatolSale = new BigDecimal(StringUtil.isEmpty(his.getTotalSales()) ? "0" : his.getTotalSales());
                        BigDecimal hisTatolRate = new BigDecimal(StringUtil.isEmpty(his.getTotalFrontDeskRate()) ? "0"
                                : his.getTotalFrontDeskRate());
                        BigDecimal hisTatolCompareRate = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRate()) ? "0"
                                : his.getTotalCompareRate());
                        BigDecimal hisTatolCompareSale = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSales()) ? "0" : his.getTotalCompareSales());

                        BigDecimal hisTatolSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalSalesIn()) ? "0" : his.getTotalSalesIn());
                        BigDecimal hisTatolRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalFrontDeskRateIn()) ? "0"
                                : his.getTotalFrontDeskRateIn());
                        BigDecimal hisTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareRateIn()) ? "0"
                                : his.getTotalCompareRateIn());
                        BigDecimal hisTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(his.getTotalCompareSalesIn()) ? "0" : his.getTotalCompareSalesIn());

                        BigDecimal curTatolSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalSales()) ? "0"
                                : cur.getTotalSales());
                        BigDecimal curTatolCompareSale = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSales()) ? "0"
                                : cur.getTotalCompareSales());
                        BigDecimal curTatolRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalRate()) ? "0"
                                : cur.getTotalRate());
                        BigDecimal curTatolCompareRate = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRate()) ? "0"
                                : cur.getTotalCompareRate());

                        BigDecimal curTatolSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalSalesIn()) ? "0"
                                : cur.getTotalSalesIn());
                        BigDecimal curTatolCompareSaleIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareSalesIn()) ? "0"
                                : cur.getTotalCompareSalesIn());
                        BigDecimal curTatolRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalRateIn()) ? "0"
                                : cur.getTotalRateIn());
                        BigDecimal curTatolCompareRateIn = new BigDecimal(StringUtil.isEmpty(cur.getTotalCompareRateIn()) ? "0"
                                : cur.getTotalCompareRateIn());
                        BigDecimal curCustomerNum = new BigDecimal(StringUtil.isEmpty(cur.getCustomerNum()) ? "0"
                                : cur.getCustomerNum());

                        his.setTotalRate(hisTatolRate.add(curTatolRate).toString());
                        his.setTotalSales(hisTatolSale.add(curTatolSale).toString());
                        his.setTotalCompareSales(hisTatolCompareSale.add(curTatolCompareSale).toString());
                        his.setTotalCompareRate(hisTatolCompareRate.add(curTatolCompareRate).toString());

                        his.setTotalRateIn(hisTatolRateIn.add(curTatolRateIn).toString());
                        his.setTotalSalesIn(hisTatolSaleIn.add(curTatolSaleIn).toString());
                        his.setTotalCompareSalesIn(hisTatolCompareSaleIn.add(curTatolCompareSaleIn).toString());
                        his.setTotalCompareRateIn(hisTatolCompareRateIn.add(curTatolCompareRateIn).toString());
                        his.setCustomerNum(curCustomerNum.toString());
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    deptList.add(cur);
                }
            }
        }else{
            deptList = queryCurRankStoreDetailInfo(rankParamRequest, 2);
        }

        //查询同期数据
        deptSameList = queryRankSameInfoForDept(rankParamRequest);
        //查询大类
        List<UserDept> allDeptList = reportUserDeptService.getAllDept();
        //翻译名称
        deptList = getTotalDeptName(allDeptList, deptList);
        deptSameList = getTotalDeptName(allDeptList, deptSameList);

        //计算品类同比大于等于和小于零的数量
        if(null != deptList && deptList.size() > 0){
            if(null != deptSameList && deptSameList.size() > 0){
                for(RankDetail d : deptList){
                    for(RankDetail s : deptSameList){
                        if(d.getDeptId().equals(s.getDeptId())){
                            BigDecimal deptSaleValue = new BigDecimal(StringUtils.isEmpty(d.getTotalSales())?"0":d.getTotalSales());
                            BigDecimal deptRateValue = new BigDecimal(StringUtils.isEmpty(d.getTotalRate())?"0":d.getTotalRate());
                            BigDecimal deptRateCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRate())?"0":d.getTotalCompareRate());
                            BigDecimal deptSaleCompareValue = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSales())?"0":d.getTotalCompareSales());
                            BigDecimal deptSaleSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalSales())?"0":s.getTotalSales());
                            BigDecimal deptRateSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalRate())?"0":s.getTotalRate());
                            BigDecimal deptSaleCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSales())?"0":s.getTotalCompareSales());
                            BigDecimal deptRateCompareSameValue = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRate())?"0":s.getTotalCompareRate());
                            BigDecimal deptSaleValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalSalesIn())?"0":d.getTotalSalesIn());
                            BigDecimal deptRateValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalRateIn())?"0":d.getTotalRateIn());
                            BigDecimal deptRateCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareRateIn())?"0":d.getTotalCompareRateIn());
                            BigDecimal deptSaleCompareValueIn = new BigDecimal(StringUtils.isEmpty(d.getTotalCompareSalesIn())?"0":d.getTotalCompareSalesIn());
                            BigDecimal deptSaleSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalSalesIn())?"0":s.getTotalSalesIn());
                            BigDecimal deptRateSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalRateIn())?"0":s.getTotalRateIn());
                            BigDecimal deptSaleCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareSalesIn())?"0":s.getTotalCompareSalesIn());
                            BigDecimal deptRateCompareSameValueIn = new BigDecimal(StringUtils.isEmpty(s.getTotalCompareRateIn())?"0":s.getTotalCompareRateIn());
                            //计算销售额同比
                            if(deptSaleSameValue.compareTo(new BigDecimal("0")) != 0){
                                d.setSaleSameRate(deptSaleValue.subtract(deptSaleSameValue).divide(deptSaleSameValue,
                                        4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            if(deptSaleCompareSameValue.compareTo(BigDecimal.ZERO) != 0){
                                d.setSaleCompareRate(deptSaleCompareValue.divide(deptSaleCompareSameValue,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算毛利额同比
                            if(deptRateSameValue.compareTo(new BigDecimal("0")) != 0){
                                d.setProfitSameRate(deptRateValue.subtract(deptRateSameValue).divide(deptRateSameValue,
                                        4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            if(deptRateCompareSameValue.compareTo(BigDecimal.ZERO) != 0){
                                d.setProfitCompareRate(deptRateCompareValue.divide(deptRateCompareSameValue,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算销售额同比
                            if(deptSaleSameValueIn.compareTo(new BigDecimal("0")) != 0){
                                d.setSaleSameRateIn(deptSaleValueIn.subtract(deptSaleSameValueIn).divide(deptSaleSameValueIn,
                                        4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            if(deptSaleCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0){
                                d.setSaleCompareRateIn(deptSaleCompareValueIn.divide(deptSaleCompareSameValueIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }

                            //计算毛利额同比
                            if(deptRateSameValueIn.compareTo(new BigDecimal("0")) != 0){
                                d.setProfitSameRateIn(deptRateValueIn.subtract(deptRateSameValueIn).divide(deptRateSameValueIn,
                                        4, BigDecimal.ROUND_HALF_UP).toString());
                            }

                            if(deptRateCompareSameValueIn.compareTo(BigDecimal.ZERO) != 0){
                                d.setProfitCompareRateIn(deptRateCompareValueIn.divide(deptRateCompareSameValueIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                            }
                            break;
                        }
                    }

                }
            }
        }
        //大类排序
        if(deptList.size() > 0) {
            //品类销售额排序
            List<RankDetail> saleList = deptList.stream().sorted(new Comparator<RankDetail>() {
                @Override
                public int compare(RankDetail o1, RankDetail o2) {
                    BigDecimal sales1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalSales()) ? "0" : o1.getTotalSales());
                    BigDecimal sales2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalSales()) ? "0" : o2.getTotalSales());
                    return sales2.compareTo(sales1);
                }
            }).collect(Collectors.toList());

            List<RankDetailResponse> sale = new ArrayList<>();
            for(RankDetail rankDetail : saleList){
                RankDetailResponse rankDetailResponse = new RankDetailResponse();
                BeanUtils.copyProperties(rankDetail, rankDetailResponse);
                sale.add(rankDetailResponse);
            }
            response.setMajorDeptSaleList(sale);

            //大类毛利额排序
            List<RankDetail> rateList = deptList.stream().sorted(new Comparator<RankDetail>() {
                @Override
                public int compare(RankDetail o1, RankDetail o2) {
                    BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalRate()) ? "0" : o1.getTotalRate());
                    BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalRate()) ? "0" : o2.getTotalRate());
                    return rate2.compareTo(rate1);
                }
            }).collect(Collectors.toList());

            List<RankDetailResponse> rate = new ArrayList<>();
            for(RankDetail rankDetail : rateList){
                RankDetailResponse rankDetailResponse = new RankDetailResponse();
                BeanUtils.copyProperties(rankDetail, rankDetailResponse);
                rate.add(rankDetailResponse);
            }
            response.setMajorDeptProfitList(rate);
        }
        return response;
    }

    /**
     * 汇总大类毛利额
     * @param target
     * @param klList
     */
    private void setHisRankKlForDept(List<RankDetail> target,List<RankDetail> klList){
        for(RankDetail value : target){
            for(RankDetail kl : klList){
                if(value.getDeptId().equals(kl.getDeptId())){
                    value.setCustomerNum(kl.getCustomerNum());
                }
            }
            value.setTotalRate(value.getTotalFrontDeskRate());
            value.setTotalCompareRate(value.getTotalCompareFrontDeskRate());
            value.setTotalRateIn(value.getTotalFrontDeskRateIn());
            value.setTotalCompareRateIn(value.getTotalCompareFrontDeskRateIn());
        }
    }

    /**
     * 查询排行榜大类同期
     * @param rankParamRequest
     * @return
     */
    private List<RankDetail> queryRankSameInfoForDept(RankParamRequest rankParamRequest) throws Exception {
        List<RankDetail> result = new ArrayList<>();
        //包含当天分两步（第一步查询当天对应的同期数据，第二部查询昨天至结束时间的数据）
        if((null == rankParamRequest.getEnd()  && DateUtils.getBetweenDay(rankParamRequest.getStart(), new Date()) >= 0)){
            Date start = rankParamRequest.getStart();
            try{
                //当天对应的同期数据
                changeZyDataSource();
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
                rankParamRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
                result = zy.querySameRankDetailForTime(rankParamRequest,"00", DateUtils.parseDateToStr("HH",new Date()),2);
                if(null != result && result.size() > 0){
                    for(RankDetail rankDetail : result){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSales())?"0":rankDetail.getTotalSales());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCost())?"0":rankDetail.getTotalCost());
                        BigDecimal compareSale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSales())?"0":rankDetail.getTotalCompareSales());
                        BigDecimal comparCost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCost())?"0":rankDetail.getTotalCompareCost());
                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSalesIn())?"0":rankDetail.getTotalSalesIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCostIn())?"0":rankDetail.getTotalCostIn());
                        BigDecimal compareSaleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSalesIn())?"0":rankDetail.getTotalCompareSalesIn());
                        BigDecimal comparCostIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCostIn())?"0":rankDetail.getTotalCompareCostIn());
                        rankDetail.setTotalRate(sale.subtract(cost).toString());
                        rankDetail.setTotalCompareRate(compareSale.subtract(comparCost).toString());

                        rankDetail.setTotalRateIn(saleIn.subtract(costIn).toString());
                        rankDetail.setTotalCompareRateIn(compareSaleIn.subtract(comparCostIn).toString());
                    }
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if((null != rankParamRequest.getEnd() && DateUtils.getBetweenDay(rankParamRequest.getEnd(), new Date()) >= 0)){
            //当天对应的同期数据
            Date start = rankParamRequest.getStart();
            Date end = rankParamRequest.getEnd();
            List<RankDetail> list01 = null;
            try{
                //切换数据
                changeZyDataSource();
                rankParamRequest.setEnd(DateUtils.addYears(end,-1));
                list01 = zy.querySameRankDetailForTime(rankParamRequest,"00", DateUtils.parseDateToStr("HH",new Date()),2);
                if(null != result && result.size() > 0){
                    for(RankDetail rankDetail : result){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSales())?"0":rankDetail.getTotalSales());
                        BigDecimal compareSale = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSales())?"0":rankDetail.getTotalCompareSales());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCost())?"0":rankDetail.getTotalCost());
                        BigDecimal comparCost = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCost())?"0":rankDetail.getTotalCompareCost());

                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalSalesIn())?"0":rankDetail.getTotalSalesIn());
                        BigDecimal compareSaleIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareSalesIn())?"0":rankDetail.getTotalCompareSalesIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCostIn())?"0":rankDetail.getTotalCostIn());
                        BigDecimal comparCostIn = new BigDecimal(StringUtils.isEmpty(rankDetail.getTotalCompareCostIn())?"0":rankDetail.getTotalCompareCostIn());
                        rankDetail.setTotalRate(sale.subtract(cost).toString());
                        rankDetail.setTotalCompareRate(compareSale.subtract(comparCost).toString());

                        rankDetail.setTotalRateIn(saleIn.subtract(costIn).toString());
                        rankDetail.setTotalCompareRateIn(compareSaleIn.subtract(comparCostIn).toString());
                    }
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //昨天至结束时间的数据
            List<RankDetail> list02 = queryHisRankStoreDetailInfo(rankParamRequest, 5);
            for(RankDetail rankDetail : list02){
                rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());
                rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());

                rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
            }

            if(null != list01 && list01.size() > 0){
                for(RankDetail rankDetail01 : list01){
                    if(null != list02 && list02.size() > 0){
                        Iterator<RankDetail> it = list02.iterator();
                        while (it.hasNext()) {
                            RankDetail rankDetail02 = it.next();
                            if (rankDetail01.getDeptId().equals(rankDetail02.getDeptId())) {
                                BigDecimal deptSaleValue01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalSales())?"0"
                                        :rankDetail01.getTotalSales());
                                BigDecimal deptSaleCompareValue01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalCompareSales())?"0"
                                        :rankDetail01.getTotalCompareSales());
                                BigDecimal deptRateValue01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalRate())?"0"
                                        :rankDetail01.getTotalRate());
                                BigDecimal deptRateCompareValue01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalCompareRate())?"0"
                                        :rankDetail01.getTotalCompareRate());

                                BigDecimal deptSaleValue02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalSales())?"0"
                                        :rankDetail02.getTotalSales());
                                BigDecimal deptSaleCompareValue02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalCompareSales())?"0"
                                        :rankDetail02.getTotalCompareSales());
                                BigDecimal deptRateValue02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalRate())?"0"
                                        :rankDetail02.getTotalRate());
                                BigDecimal deptRateCompareValue02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalCompareRate())?"0"
                                        :rankDetail02.getTotalCompareRate());

                                BigDecimal deptSaleValueIn01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalSalesIn())?"0"
                                        :rankDetail01.getTotalSalesIn());
                                BigDecimal deptSaleCompareValueIn01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalCompareSalesIn())?"0"
                                        :rankDetail01.getTotalCompareSalesIn());
                                BigDecimal deptRateValueIn01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalRateIn())?"0"
                                        :rankDetail01.getTotalRateIn());
                                BigDecimal deptRateCompareValueIn01 = new BigDecimal(StringUtils.isEmpty(rankDetail01.getTotalCompareRateIn())?"0"
                                        :rankDetail01.getTotalCompareRateIn());

                                BigDecimal deptSaleValueIn02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalSalesIn())?"0"
                                        :rankDetail02.getTotalSalesIn());
                                BigDecimal deptSaleCompareValueIn02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalCompareSalesIn())?"0"
                                        :rankDetail02.getTotalCompareSalesIn());
                                BigDecimal deptRateValueIn02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalRateIn())?"0"
                                        :rankDetail02.getTotalRateIn());
                                BigDecimal deptRateCompareValueIn02 = new BigDecimal(StringUtils.isEmpty(rankDetail02.getTotalCompareRateIn())?"0"
                                        :rankDetail02.getTotalCompareRateIn());

                                rankDetail01.setTotalSales(deptSaleValue01.add(deptSaleValue02).toString());
                                rankDetail01.setTotalRate(deptRateValue01.add(deptRateValue02).toString());
                                rankDetail01.setTotalCompareSales(deptSaleCompareValue01.add(deptSaleCompareValue02).toString());
                                rankDetail01.setTotalCompareRate(deptRateCompareValue01.add(deptRateCompareValue02).toString());

                                rankDetail01.setTotalSalesIn(deptSaleValueIn01.add(deptSaleValueIn02).toString());
                                rankDetail01.setTotalRateIn(deptRateValueIn01.add(deptRateValueIn02).toString());
                                rankDetail01.setTotalCompareSalesIn(deptSaleCompareValueIn01.add(deptSaleCompareValueIn02).toString());
                                rankDetail01.setTotalCompareRateIn(deptRateCompareValueIn01.add(deptRateCompareValueIn02).toString());
                                it.remove();
                                break;
                            }
                        }
                    }else{
                        break;
                    }
                }
                if(null != list02 && list02.size() > 0){
                    result.addAll(list02);
                }
                result.addAll(list01);
            }else{
                if(null != list02 && list02.size() > 0){
                    result.addAll(list02);
                }
            }
        }else{
            if(null != rankParamRequest.getEnd()){
                rankParamRequest.setStart(DateUtils.addYears(rankParamRequest.getStart(),-1));
                rankParamRequest.setEnd(DateUtils.addYears(rankParamRequest.getEnd(),-1));
            }else{
                String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",rankParamRequest.getStart()));
                rankParamRequest.setStart(DateUtil.toDate(time,"yyyy-MM-dd"));
            }
            result = csmb.queryHisRankDetailForDept(rankParamRequest);
            if(null != result && result.size() > 0){
                for(RankDetail rankDetail : result){
                    rankDetail.setTotalRate(rankDetail.getTotalFrontDeskRate());
                    rankDetail.setTotalCompareRate(rankDetail.getTotalCompareFrontDeskRate());

                    rankDetail.setTotalRateIn(rankDetail.getTotalFrontDeskRateIn());
                    rankDetail.setTotalCompareRateIn(rankDetail.getTotalCompareFrontDeskRateIn());
                }
            }
        }
        return result;
    }

    /**
     * 设置会员线下标识
     * @return
     */
    private List<String> setOfflineMark(){
        String[] arr = {"YUNPOS","VIP","WX","WX1","WX2","WX3","DD1","JD1","KJG","BKSLS"};
        return Arrays.asList(arr);
    }

    private boolean haveNotFresh(PageRequest pageRequest) throws Exception {
        boolean flag = true;
        List<String> allFreshList = new ArrayList<>();
        List<UserDept> allDeptList = reportUserDeptService.getAllDept();
        //获取用户权限大类
        List<UserDept> userDeptList = reportUserDeptService.getUserDeptList(pageRequest.getPersonId());
        //获取生鲜的list
        for(UserDept userDept : allDeptList){
            if("生鲜".equals(userDept.getPCategory())){
                allFreshList.add(userDept.getDeptId() + "");
            }
        }
        int count = 0;
        if(null != userDeptList && userDeptList.size() > 0){
            for(UserDept userDept : userDeptList){
                if("0".equals(String.valueOf(userDept.getDeptId()))){//0表示所有权限
                    return true;
                }else if(allFreshList.contains(String.valueOf(userDept.getDeptId()))){
                    count = count + 1;
                }
            }
        }else{
            flag = false;
        }
        //相等说明该用户全是生鲜权限
        if(count == userDeptList.size()){
            flag = false;
        }
        return flag;
    }
    
    /**
     * 根据组织架构id查组织名称
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public List<CsmbStoreModel> getOrganization() throws Exception {
        List<CsmbStoreModel> list = null;
        try{
            list = reportOrgDao.queryOrganization();
        }catch (Exception e){
            throw e;
        }finally {
        	//DataSourceHolder.clearDataSource();//使用完后关闭切换数据源
        }
        return list;
    }
    

         
}
