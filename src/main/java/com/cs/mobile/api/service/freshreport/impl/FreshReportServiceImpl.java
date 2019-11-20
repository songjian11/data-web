package com.cs.mobile.api.service.freshreport.impl;

import com.cs.mobile.api.dao.freshreport.FreshReportCsmbDao;
import com.cs.mobile.api.dao.freshreport.FreshReportRmsDao;
import com.cs.mobile.api.dao.freshreport.FreshReportZyDao;
import com.cs.mobile.api.dao.reportPage.ReportPageCsmbDao;
import com.cs.mobile.api.dao.reportPage.ReportPageRmsDao;
import com.cs.mobile.api.dao.reportPage.ReportPageZyDao;
import com.cs.mobile.api.dao.salereport.SaleReportCsmbDao;
import com.cs.mobile.api.dao.salereport.SaleReportRmsDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.freshreport.*;
import com.cs.mobile.api.model.freshreport.request.FreshRankRequest;
import com.cs.mobile.api.model.freshreport.request.FreshReportBaseRequest;
import com.cs.mobile.api.model.freshreport.response.*;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
import com.cs.mobile.api.model.reportPage.MemberPermeabilityPo;
import com.cs.mobile.api.model.reportPage.response.MemberPermeabilityResponse;
import com.cs.mobile.api.model.salereport.BaseSaleModel;
import com.cs.mobile.api.model.salereport.GoalSale;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.api.model.salereport.response.AchievingRateResponse;
import com.cs.mobile.api.service.common.CommonCalculateService;
import com.cs.mobile.api.service.freshreport.FreshReportService;
import com.cs.mobile.api.service.reportPage.ReportPageService;
import com.cs.mobile.api.service.reportPage.ReportUserDeptService;
import com.cs.mobile.api.service.salereport.SaleReportService;
import com.cs.mobile.common.core.text.Convert;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.beans.beancontext.BeanContextServicesSupport;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
//@Slf4j
@Service
public class FreshReportServiceImpl implements FreshReportService{
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
    private DruidProperties druidProperties;
    @Autowired
    private FreshReportCsmbDao freshReportCsmbDao;
    @Autowired
    private FreshReportRmsDao freshReportRmsDao;
    @Autowired
    private FreshReportZyDao freshReportZyDao;
    @Autowired
    private ReportUserDeptService reportUserDeptService;
    @Autowired
    private ReportPageCsmbDao reportPageCsmbDao;
    @Autowired
    private ReportPageRmsDao reportPageRmsDao;
    @Autowired
    private CommonCalculateService calculateService;

    /**
     * 查询生鲜的总销售额
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public MonthStatisticsResponse queryMonthStatisticsResponse(FreshReportBaseRequest param) throws Exception {
        MonthStatisticsResponse response = new MonthStatisticsResponse();
        //生鲜当天数据
        MonthStatistics cur = null;
        //生鲜历史数据
        MonthStatistics his = null;
        //生鲜历史折价额数据
        MonthStatistics hisDiscount = null;
        //总的当天数据
        MonthStatistics curAll = null;
        //总的历史数据
        MonthStatistics hisAll = null;
        transform(param);
        //筛选生鲜大类
        getFreshList(param);
        //判断是否包含当天时间
        if((null == param.getEnd() && DateUtils.getBetweenDay(param.getStart(), new Date()) >= 0)
                || (null != param.getEnd() && DateUtils.getBetweenDay(param.getEnd(), new Date()) >= 0)){
            //查询当天生鲜数据
            cur = freshReportCsmbDao.queryCurrentFreshInfo(param);
            //查询当天总的数据(没有大类条件)
            curAll = freshReportCsmbDao.queryCurrentTotalData(param);
        }
        //查询月初到当天的生鲜数据
        Date date = param.getStart();
        param.setStart(DateUtil.getFirstDayForMonth(date));
        param.setEnd(date);
        //查询历史总数据
        hisAll = freshReportCsmbDao.queryHisAllData(param);
        try{
            changeDgDataSource();
            //查询生鲜历史数据
            his = freshReportRmsDao.queryHisFreshInfo(param);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        try{
            changeZyDataSource();
            //查询生鲜历史折价额
            hisDiscount = freshReportZyDao.queryHisFreshInfo(param);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //历史总的销售额
        BigDecimal hisAllTotalSales = new BigDecimal("0");
        //历史总的毛利额
        BigDecimal hisAllTotalProfitPrice = new BigDecimal("0");
        //历史总的销售额
        BigDecimal hisAllTotalSalesIn = new BigDecimal("0");
        //历史总的毛利额
        BigDecimal hisAllTotalProfitPriceIn = new BigDecimal("0");
        if(null != hisAll){
            hisAllTotalSales = new BigDecimal(StringUtils.isEmpty(hisAll.getTotalSales())?"0":hisAll.getTotalSales());
            hisAllTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(hisAll.getTotalProfitPrice())?"0"
                    :hisAll.getTotalProfitPrice());

            hisAllTotalSalesIn = new BigDecimal(StringUtils.isEmpty(hisAll.getTotalSalesIn())?"0":hisAll.getTotalSalesIn());
            hisAllTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(hisAll.getTotalProfitPriceIn())?"0"
                    :hisAll.getTotalProfitPriceIn());
        }

        //生鲜历史销售额
        BigDecimal hisTotalSales = new BigDecimal("0");
        //生鲜历史毛利额
        BigDecimal hisTotalProfitPrice = new BigDecimal("0");
        //生鲜历史损耗
        BigDecimal hisLossPrice = new BigDecimal("0");
        //生鲜历史折价额
        BigDecimal hisDiscountPrice = new BigDecimal("0");

        //生鲜历史销售额
        BigDecimal hisTotalSalesIn = new BigDecimal("0");
        //生鲜历史毛利额
        BigDecimal hisTotalProfitPriceIn = new BigDecimal("0");
        //生鲜历史损耗
        BigDecimal hisLossPriceIn = new BigDecimal("0");
        //生鲜历史折价额
        BigDecimal hisDiscountPriceIn = new BigDecimal("0");
        if(null != his){
            hisTotalSales = new BigDecimal(StringUtils.isEmpty(his.getTotalSales())?"0":his.getTotalSales());
            hisTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(his.getTotalProfitPrice())?"0"
                    :his.getTotalProfitPrice());
            hisLossPrice = new BigDecimal(StringUtils.isEmpty(his.getLossPrice())?"0":his.getLossPrice());

            hisTotalSalesIn = new BigDecimal(StringUtils.isEmpty(his.getTotalSalesIn())?"0":his.getTotalSalesIn());
            hisTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(his.getTotalProfitPriceIn())?"0"
                    :his.getTotalProfitPriceIn());
            hisLossPriceIn = new BigDecimal(StringUtils.isEmpty(his.getLossPriceIn())?"0":his.getLossPriceIn());
        }
        if(null != hisDiscount){
            hisDiscountPrice = new BigDecimal(StringUtils.isEmpty(hisDiscount.getDiscountPrice())?"0"
                    :hisDiscount.getDiscountPrice());
            hisDiscountPriceIn = new BigDecimal(StringUtils.isEmpty(hisDiscount.getDiscountPriceIn())?"0"
                    :hisDiscount.getDiscountPriceIn());
        }

        //当天总的销售额
        BigDecimal curAllTotalSales = new BigDecimal("0");
        //当天总的毛利额
        BigDecimal curAllTotalProfitPrice = new BigDecimal("0");

        //当天总的销售额
        BigDecimal curAllTotalSalesIn = new BigDecimal("0");
        //当天总的毛利额
        BigDecimal curAllTotalProfitPriceIn = new BigDecimal("0");
        if(null != curAll){
            curAllTotalSales = new BigDecimal(StringUtils.isEmpty(curAll.getTotalSales())?"0":curAll.getTotalSales());
            curAllTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(curAll.getTotalProfitPrice())?"0"
                    :curAll.getTotalProfitPrice());

            curAllTotalSalesIn = new BigDecimal(StringUtils.isEmpty(curAll.getTotalSalesIn())?"0":curAll.getTotalSalesIn());
            curAllTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(curAll.getTotalProfitPriceIn())?"0"
                    :curAll.getTotalProfitPriceIn());
        }

        //生鲜当天销售额
        BigDecimal curTotalSales = new BigDecimal("0");
        //生鲜当天毛利额
        BigDecimal curTotalProfitPrice = new BigDecimal("0");
        //生鲜当天损耗
        BigDecimal curLossPrice = new BigDecimal("0");
        //生鲜当天折价额
        BigDecimal curDiscountPrice = new BigDecimal("0");

        //生鲜当天销售额
        BigDecimal curTotalSalesIn = new BigDecimal("0");
        //生鲜当天毛利额
        BigDecimal curTotalProfitPriceIn = new BigDecimal("0");
        //生鲜当天损耗
        BigDecimal curLossPriceIn = new BigDecimal("0");
        //生鲜当天折价额
        BigDecimal curDiscountPriceIn = new BigDecimal("0");
        if(null != cur){
            curTotalSales = new BigDecimal(StringUtils.isEmpty(cur.getTotalSales())?"0":cur.getTotalSales());
            curTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(cur.getTotalProfitPrice())?"0"
                    :cur.getTotalProfitPrice());
            curLossPrice = new BigDecimal(StringUtils.isEmpty(cur.getLossPrice())?"0":cur.getLossPrice());
            curDiscountPrice = new BigDecimal(StringUtils.isEmpty(cur.getDiscountPrice())?"0":cur.getDiscountPrice());

            curTotalSalesIn = new BigDecimal(StringUtils.isEmpty(cur.getTotalSalesIn())?"0":cur.getTotalSalesIn());
            curTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(cur.getTotalProfitPriceIn())?"0"
                    :cur.getTotalProfitPriceIn());
            curLossPriceIn = new BigDecimal(StringUtils.isEmpty(cur.getLossPriceIn())?"0":cur.getLossPriceIn());
            curDiscountPriceIn = new BigDecimal(StringUtils.isEmpty(cur.getDiscountPriceIn())?"0":cur.getDiscountPriceIn());
        }
        //组装数据
        //总销售额 = 历史总销售额 + 当天总销售额
        BigDecimal allSales = new BigDecimal(hisAllTotalSales.add(curAllTotalSales).toString());
        //总毛利额 = 历史总毛利额 + 当天总毛利额
        BigDecimal allProfit = new BigDecimal(hisAllTotalProfitPrice.add(curAllTotalProfitPrice).toString());
        //生鲜销售额 = 生鲜历史销售额 + 生鲜当天销售额
        BigDecimal sales = new BigDecimal(hisTotalSales.add(curTotalSales).toString());
        //生鲜毛利额 = 生鲜历史毛利额 + 生鲜当天毛利额
        BigDecimal profit = new BigDecimal(hisTotalProfitPrice.add(curTotalProfitPrice).toString());
        //生鲜损耗 = 生鲜历史损耗 + 生鲜当天损耗
        BigDecimal lossPrice = new BigDecimal(hisLossPrice.add(curLossPrice).toString());
        //生鲜折价额 = 生鲜历史折价额 + 生鲜当天折价额
        BigDecimal discountPrice = new BigDecimal(hisDiscountPrice.add(curDiscountPrice).toString());

        //总销售额 = 历史总销售额 + 当天总销售额
        BigDecimal allSalesIn = new BigDecimal(hisAllTotalSalesIn.add(curAllTotalSalesIn).toString());
        //总毛利额 = 历史总毛利额 + 当天总毛利额
        BigDecimal allProfitIn = new BigDecimal(hisAllTotalProfitPriceIn.add(curAllTotalProfitPriceIn).toString());
        //生鲜销售额 = 生鲜历史销售额 + 生鲜当天销售额
        BigDecimal salesIn = new BigDecimal(hisTotalSalesIn.add(curTotalSalesIn).toString());
        //生鲜毛利额 = 生鲜历史毛利额 + 生鲜当天毛利额
        BigDecimal profitIn = new BigDecimal(hisTotalProfitPriceIn.add(curTotalProfitPriceIn).toString());
        //生鲜损耗 = 生鲜历史损耗 + 生鲜当天损耗
        BigDecimal lossPriceIn = new BigDecimal(hisLossPriceIn.add(curLossPriceIn).toString());
        //生鲜折价额 = 生鲜历史折价额 + 生鲜当天折价额
        BigDecimal discountPriceIn = new BigDecimal(hisDiscountPriceIn.add(curDiscountPriceIn).toString());

        //生鲜销售额 = 生鲜历史销售额 + 生鲜当天销售额
        response.setTotalSales(sales.toString());
        response.setTotalSalesIn(salesIn.toString());
        response.setTotalRate(profit.toString());
        response.setTotalRateIn(profitIn.toString());
        if(sales.compareTo(BigDecimal.ZERO) != 0){
            response.setTotalProfitRate(profit.divide(sales,4,BigDecimal.ROUND_HALF_UP).toString());
        }
        if(salesIn.compareTo(BigDecimal.ZERO) != 0){
            response.setTotalProfitRateIn(profitIn.divide(salesIn,4,BigDecimal.ROUND_HALF_UP).toString());
        }
        /*//渗透率 = 生鲜销售额 / 总销售额
        if(allSales.compareTo(new BigDecimal("0")) != 0){
            response.setPermeability(sales.divide(allSales,4,BigDecimal.ROUND_HALF_UP).toString());
        }
        if(allSalesIn.compareTo(new BigDecimal("0")) != 0){
            response.setPermeabilityIn(salesIn.divide(allSalesIn,4,BigDecimal.ROUND_HALF_UP).toString());
        }*/

        //损耗率 = 生鲜损耗 / 生鲜销售额
        if(sales.compareTo(new BigDecimal("0")) != 0){
            BigDecimal lossRate = lossPrice.divide(sales,4,BigDecimal.ROUND_HALF_UP);
            response.setLossRate(BigDecimal.ZERO.subtract(lossRate).toString());
        }
        if(salesIn.compareTo(new BigDecimal("0")) != 0){
            BigDecimal lossRateIn = lossPriceIn.divide(salesIn,4,BigDecimal.ROUND_HALF_UP);
            response.setLossRateIn(BigDecimal.ZERO.subtract(lossRateIn).toString());
        }
        //毛利贡献度 = 生鲜毛利额 / 总毛利额
        if(allProfit.compareTo(new BigDecimal("0")) != 0){
            response.setProfitcontribution(profit.divide(allProfit,4,BigDecimal.ROUND_HALF_UP).toString());
        }
        if(allProfitIn.compareTo(new BigDecimal("0")) != 0){
            response.setProfitcontributionIn(profitIn.divide(allProfitIn,4,BigDecimal.ROUND_HALF_UP).toString());
        }
        //折价率 = 生鲜折价额 / 生鲜销售额
        if(sales.compareTo(new BigDecimal("0")) != 0){
            response.setDiscountRate(discountPrice.divide(sales,4,BigDecimal.ROUND_HALF_UP).toString());
        }
        if(salesIn.compareTo(new BigDecimal("0")) != 0){
            response.setDiscountRateIn(discountPriceIn.divide(salesIn,4,BigDecimal.ROUND_HALF_UP).toString());
        }
        //增加客流渗透率
        response.setPermeabilityRate(queryPermeability(param, date));
        return response;
    }

    /**
     * 查询生鲜的总销售额的渗透率
     */
    private String queryPermeability(FreshReportBaseRequest param, Date start){
        String result = "";
        FreshRankKlModel current = new FreshRankKlModel();
        FreshRankKlModel currentAll = new FreshRankKlModel();
        FreshRankKlModel his = new FreshRankKlModel();
        FreshRankKlModel hisAll = new FreshRankKlModel();
        if(DateUtils.getBetweenDay(start, new Date()) >= 0){
            //查询实时客流
            current = freshReportCsmbDao.querySaleKl(param, start, null);
            //查询实时总客流
            currentAll = freshReportCsmbDao.querySaleTotalKl(param, start, null);
            //查询月初至昨日客流
            if(null != param.getDeptIds() && param.getDeptIds().size() == 1){//查询单一大类客流
                try{
                    changeDgDataSource();
                    his = freshReportRmsDao.queryHisSaleOneKl(param, DateUtil.getFirstDayForMonth(start), DateUtils.addDays(start, -1));
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else{//省，区域，门店客流（32，35，36，37的汇总客流）(性能优化)
                try{
                    changeZyDataSource();
                    his = freshReportZyDao.queryHisSaleFourKl(param, DateUtil.getFirstDayForMonth(start), DateUtils.addDays(start, -1));
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
            //查询月初至昨日总客流
            try{
                changeZyDataSource();
                hisAll = freshReportZyDao.queryHisSaleAlllKl(param, DateUtil.getFirstDayForMonth(start), DateUtils.addDays(start, -1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else{
            //查询历史客流
            if(null != param.getDeptIds() && param.getDeptIds().size() == 1){//查询单一大类客流
                try{
                    changeDgDataSource();
                    his = freshReportRmsDao.queryHisSaleOneKl(param, DateUtil.getFirstDayForMonth(start), start);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else{//省，区域，门店客流（32，35，36，37的汇总客流）(性能优化)
                try{
                    changeZyDataSource();
                    his = freshReportZyDao.queryHisSaleFourKl(param, DateUtil.getFirstDayForMonth(start), start);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
            //查询月初至历史当日总客流
            try{
                changeZyDataSource();
                hisAll = freshReportZyDao.queryHisSaleAlllKl(param, DateUtil.getFirstDayForMonth(start), start);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }
        result = calculateService.calculatePermeability(
                calculateService.adds(
                        (current == null?"":current.getKl()),
                        (his == null?"":his.getKl())),
                calculateService.adds(
                        (currentAll == null?"":currentAll.getKl()),
                        (hisAll==null?"":hisAll.getKl())
                ));
        return result;
    }

    /**
     * 会员渗透率
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public MemberPermeabilityResponse queryMember(PageRequest param) throws Exception {
        transformPage(param);
        //筛选生鲜大类
        getFreshListPage(param);
        MemberPermeabilityResponse memberPermeabilityResponse = new MemberPermeabilityResponse();
        List<MemberPermeabilityPo> po = new ArrayList<>();
        if((param.getEnd() == null && DateUtils.getBetweenDay(param.getStart(), new Date()) < 0)
                || (null != param.getEnd() && DateUtils.getBetweenDay(param.getEnd(), new Date()) < 0) ){
            try{
                //历史数据
                changeZyDataSource();
                if(null != param.getDeptIds() && param.getDeptIds().size() == 1){
                    po = freshReportZyDao.queryHisFreshMemberForDept(param, param.getStart(), param.getEnd());
                }else{
                    po = freshReportZyDao.queryHisFreshMember(param, param.getStart(), param.getEnd());
                }
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else{
            po = reportPageCsmbDao.queryMember(param);
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
//        getFreshListPage(param);
//        transformPage(pageRequest);
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
                    allList = freshReportZyDao.queryHisFreshMemberDetail(param,start,null);
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
                    history = freshReportZyDao.queryHisFreshMemberDetail(param,start,DateUtils.addDays(end,-1));
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
                    allList = freshReportZyDao.queryHisFreshMemberDetail(param,start,DateUtils.addDays(end,-1));
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
                            if("BKSLS".equalsIgnoreCase(memberModel.getChannel())
                                    || StringUtils.isEmpty(memberModel.getChannel())){
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
     * 查询销售列表
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public SalesListResponse querySalesList(FreshReportBaseRequest param) throws Exception {
        SalesListResponse salesListResponse = new SalesListResponse();
        SalesResponse curInfo = new SalesResponse();
        SalesResponse yesterdayInfo = new SalesResponse();
        SalesResponse yesterdayRate = new SalesResponse();
        SalesResponse weekRate = new SalesResponse();
        SalesResponse monthRate = new SalesResponse();
        transform(param);
        //筛选生鲜大类
        getFreshList(param);
        MonthStatistics monthStatistics = null;
        List<SalesList> hisList = new ArrayList<>();
        if(null != param.getStart() && DateUtils.getBetweenDay(param.getStart(),new Date()) >= 0){
            //查询实时数据
           monthStatistics = freshReportCsmbDao.queryCurrentFreshInfo(param);
        }

        //查询昨日，周，月
        if(DateUtils.getBetweenDay(param.getStart(),new Date()) >= 0){
            try{
                changeZyDataSource();
                hisList = freshReportZyDao.querySameTotalSalesAndProfitForHour(param,"00",
                        DateUtils.parseDateToStr("HH",new Date()));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else{
            //历史时间点
            try{
                changeDgDataSource();
                hisList = freshReportRmsDao.queryHisFreshSalesInfo(param);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }

        BigDecimal curTotalSales = new BigDecimal("0");
        BigDecimal curTotalProfitPrice = new BigDecimal("0");
        BigDecimal curTotalSalesIn = new BigDecimal("0");
        BigDecimal curTotalProfitPriceIn = new BigDecimal("0");
        if(null != monthStatistics){
            curTotalSales = new BigDecimal(StringUtils.isEmpty(monthStatistics.getTotalSales())?"0"
                    :monthStatistics.getTotalSales());
            curTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(monthStatistics.getTotalProfitPrice())?"0"
                    :monthStatistics.getTotalProfitPrice());

            curTotalSalesIn = new BigDecimal(StringUtils.isEmpty(monthStatistics.getTotalSalesIn())?"0"
                    :monthStatistics.getTotalSalesIn());
            curTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(monthStatistics.getTotalProfitPriceIn())?"0"
                    :monthStatistics.getTotalProfitPriceIn());
        }

        if(null != hisList && hisList.size() > 0){
            String current = DateUtils.parseDateToStr("yyyyMMdd",param.getStart());
            String yesterday = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addDays(param.getStart(),-1));
            String week = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addWeeks(param.getStart(),-1));
            String month = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addMonths(param.getStart(),-1));

            SalesList hisData = null;
            SalesList yesterdayData = null;
            SalesList weekData = null;
            SalesList monthData = null;
            for(SalesList sales : hisList){
                if(current.equals(sales.getTime())){
                    hisData = sales;
                }else if(yesterday.equals(sales.getTime())){
                    yesterdayData = sales;
                }else if(week.equals(sales.getTime())){
                    weekData = sales;
                }else if(month.equals(sales.getTime())){
                    monthData = sales;
                }
            }
            //计算数据
            BigDecimal hisTotalSales = new BigDecimal("0");
            BigDecimal hisTotalProfitPrice = new BigDecimal("0");
            BigDecimal hisTotalSalesIn = new BigDecimal("0");
            BigDecimal hisTotalProfitPriceIn = new BigDecimal("0");
            if(null != hisData){
                hisTotalSales = new BigDecimal(StringUtils.isEmpty(hisData.getTotalSales())?"0"
                        :hisData.getTotalSales());
                hisTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(hisData.getTotalProfitPrice())?"0"
                        :hisData.getTotalProfitPrice());

                hisTotalSalesIn = new BigDecimal(StringUtils.isEmpty(hisData.getTotalSalesIn())?"0"
                        :hisData.getTotalSalesIn());
                hisTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(hisData.getTotalProfitPriceIn())?"0"
                        :hisData.getTotalProfitPriceIn());
            }

            BigDecimal yesterdayTotalSales = new BigDecimal("0");
            BigDecimal yesterdayTotalProfitPrice = new BigDecimal("0");
            BigDecimal yesterdayTotalSalesIn = new BigDecimal("0");
            BigDecimal yesterdayTotalProfitPriceIn = new BigDecimal("0");
            if(null != yesterdayData){
                yesterdayTotalSales = new BigDecimal(StringUtils.isEmpty(yesterdayData.getTotalSales())?"0"
                        :yesterdayData.getTotalSales());
                yesterdayTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(yesterdayData.getTotalProfitPrice())?"0"
                        :yesterdayData.getTotalProfitPrice());

                yesterdayTotalSalesIn = new BigDecimal(StringUtils.isEmpty(yesterdayData.getTotalSalesIn())?"0"
                        :yesterdayData.getTotalSalesIn());
                yesterdayTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(yesterdayData.getTotalProfitPriceIn())?"0"
                        :yesterdayData.getTotalProfitPriceIn());
            }
            yesterdayInfo.setTotalSales(yesterdayTotalSales.toString());
            yesterdayInfo.setTotalProfitPrice(yesterdayTotalProfitPrice.toString());

            yesterdayInfo.setTotalSalesIn(yesterdayTotalSalesIn.toString());
            yesterdayInfo.setTotalProfitPriceIn(yesterdayTotalProfitPriceIn.toString());

            BigDecimal weekTotalSales = new BigDecimal("0");
            BigDecimal weekTotalProfitPrice = new BigDecimal("0");

            BigDecimal weekTotalSalesIn = new BigDecimal("0");
            BigDecimal weekTotalProfitPriceIn = new BigDecimal("0");
            if(null != weekData){
                weekTotalSales = new BigDecimal(StringUtils.isEmpty(weekData.getTotalSales())?"0"
                        :weekData.getTotalSales());
                weekTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(weekData.getTotalProfitPrice())?"0"
                        :weekData.getTotalProfitPrice());

                weekTotalSalesIn = new BigDecimal(StringUtils.isEmpty(weekData.getTotalSalesIn())?"0"
                        :weekData.getTotalSalesIn());
                weekTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(weekData.getTotalProfitPriceIn())?"0"
                        :weekData.getTotalProfitPriceIn());
            }

            BigDecimal monthTotalSales = new BigDecimal("0");
            BigDecimal monthTotalProfitPrice = new BigDecimal("0");

            BigDecimal monthTotalSalesIn = new BigDecimal("0");
            BigDecimal monthTotalProfitPriceIn = new BigDecimal("0");
            if(null != monthData){
                monthTotalSales = new BigDecimal(StringUtils.isEmpty(monthData.getTotalSales())?"0"
                        :monthData.getTotalSales());
                monthTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(monthData.getTotalProfitPrice())?"0"
                        :monthData.getTotalProfitPrice());

                monthTotalSalesIn = new BigDecimal(StringUtils.isEmpty(monthData.getTotalSalesIn())?"0"
                        :monthData.getTotalSalesIn());
                monthTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(monthData.getTotalProfitPriceIn())?"0"
                        :monthData.getTotalProfitPriceIn());
            }

            //当日销售额 = 实时销售额  + 历史当天销售额（解释：如果查询实时数据则历史为零，如果查询历史时间点则实时为零）
            BigDecimal curAllTotalSales = curTotalSales.add(hisTotalSales);
            BigDecimal curAllTotalSalesIn = curTotalSalesIn.add(hisTotalSalesIn);
            curInfo.setTotalSales(curAllTotalSales.toString());
            curInfo.setTotalSalesIn(curAllTotalSalesIn.toString());
            //当日毛利额 = 实时毛利额  + 历史当天毛利额（解释：如果查询实时数据则历史为零，如果查询历史时间点则实时为零）
            BigDecimal curAllTotalProfitPrice = curTotalProfitPrice.add(hisTotalProfitPrice);
            BigDecimal curAllTotalProfitPriceIn = curTotalProfitPriceIn.add(hisTotalProfitPriceIn);
            curInfo.setTotalProfitPrice(curAllTotalProfitPrice.toString());
            curInfo.setTotalProfitPriceIn(curAllTotalProfitPriceIn.toString());
            //当日毛利率 = 当日毛利额 / 当日销售额
            BigDecimal curProfitRate = new BigDecimal("0");
            BigDecimal curProfitRateIn = new BigDecimal("0");
            if(curAllTotalSales.compareTo(new BigDecimal("0")) != 0){
                curProfitRate = curAllTotalProfitPrice.divide(curAllTotalSales,4,BigDecimal.ROUND_HALF_UP);
                curInfo.setTotalProfit(curProfitRate.toString());
            }
            if(curAllTotalSalesIn.compareTo(new BigDecimal("0")) != 0){
                curProfitRateIn = curAllTotalProfitPriceIn.divide(curAllTotalSalesIn,4,BigDecimal.ROUND_HALF_UP);
                curInfo.setTotalProfitIn(curProfitRateIn.toString());
            }
            //昨日毛利率 = 昨日毛利额 / 昨日销售额
            BigDecimal yRate = new BigDecimal("0");
            BigDecimal yRateIn = new BigDecimal("0");
            if(yesterdayTotalSales.compareTo(new BigDecimal("0")) != 0){
                yRate = yesterdayTotalProfitPrice.divide(yesterdayTotalSales,4,BigDecimal.ROUND_HALF_UP);
                yesterdayInfo.setTotalProfit(yRate.toString());
            }
            if(yesterdayTotalSalesIn.compareTo(new BigDecimal("0")) != 0){
                yRateIn = yesterdayTotalProfitPriceIn.divide(yesterdayTotalSalesIn,4,BigDecimal.ROUND_HALF_UP);
                yesterdayInfo.setTotalProfitIn(yRateIn.toString());
            }
            //周毛利率 = 周毛利额 / 周销售额
            BigDecimal wRate = new BigDecimal("0");
            BigDecimal wRateIn = new BigDecimal("0");
            if(weekTotalSales.compareTo(new BigDecimal("0")) != 0){
                wRate = weekTotalProfitPrice.divide(weekTotalSales,4,BigDecimal.ROUND_HALF_UP);
            }
            if(weekTotalSalesIn.compareTo(new BigDecimal("0")) != 0){
                wRateIn = weekTotalProfitPriceIn.divide(weekTotalSalesIn,4,BigDecimal.ROUND_HALF_UP);
            }
            //月毛利率 = 月毛利额 / 月销售额
            BigDecimal mRate = new BigDecimal("0");
            BigDecimal mRateIn = new BigDecimal("0");
            if(monthTotalSales.compareTo(new BigDecimal("0")) != 0){
                mRate = monthTotalProfitPrice.divide(monthTotalSales,4,BigDecimal.ROUND_HALF_UP);
            }
            if(monthTotalSalesIn.compareTo(new BigDecimal("0")) != 0){
                mRateIn = monthTotalProfitPriceIn.divide(monthTotalSalesIn,4,BigDecimal.ROUND_HALF_UP);
            }
            //销售额日比 = （当日销售额 - 昨日销售额）/ 昨日销售额
            if(yesterdayTotalSales.compareTo(new BigDecimal("0")) != 0){
                yesterdayRate.setTotalSalesRate(curAllTotalSales.subtract(yesterdayTotalSales)
                        .divide(yesterdayTotalSales,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            if(yesterdayTotalSalesIn.compareTo(new BigDecimal("0")) != 0){
                yesterdayRate.setTotalSalesRateIn(curAllTotalSalesIn.subtract(yesterdayTotalSalesIn)
                        .divide(yesterdayTotalSalesIn,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            //毛利额日比 =（当日毛利额 - 昨日毛利额）/ 昨日毛利额
            if(yesterdayTotalProfitPrice.compareTo(new BigDecimal("0")) != 0){
                yesterdayRate.setTotalProfitPriceRate(curAllTotalProfitPrice.subtract(yesterdayTotalProfitPrice)
                        .divide(yesterdayTotalProfitPrice,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            if(yesterdayTotalProfitPriceIn.compareTo(new BigDecimal("0")) != 0){
                yesterdayRate.setTotalProfitPriceRateIn(curAllTotalProfitPriceIn.subtract(yesterdayTotalProfitPriceIn)
                        .divide(yesterdayTotalProfitPriceIn,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            //毛利率日比 =当日毛利率 - 昨日毛利率
            if(yRate.compareTo(new BigDecimal("0")) != 0){
                yesterdayRate.setTotalProfitRate(curProfitRate.subtract(yRate).toString());
            }
            if(yRateIn.compareTo(new BigDecimal("0")) != 0){
                yesterdayRate.setTotalProfitRateIn(curProfitRateIn.subtract(yRateIn).toString());
            }
            //销售额周比 = （当日销售额 - 周销售额）/ 周销售额
            if(weekTotalSales.compareTo(new BigDecimal("0")) != 0){
                weekRate.setTotalSalesRate(curAllTotalSales.subtract(weekTotalSales)
                        .divide(weekTotalSales,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            if(weekTotalSalesIn.compareTo(new BigDecimal("0")) != 0){
                weekRate.setTotalSalesRateIn(curAllTotalSalesIn.subtract(weekTotalSalesIn)
                        .divide(weekTotalSalesIn,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            //毛利额周比 =（当日毛利额 - 周毛利额）/ 周毛利额
            if(weekTotalProfitPrice.compareTo(new BigDecimal("0")) != 0){
                weekRate.setTotalProfitPriceRate(curAllTotalProfitPrice.subtract(weekTotalProfitPrice)
                        .divide(weekTotalProfitPrice,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            if(weekTotalProfitPriceIn.compareTo(new BigDecimal("0")) != 0){
                weekRate.setTotalProfitPriceRateIn(curAllTotalProfitPriceIn.subtract(weekTotalProfitPriceIn)
                        .divide(weekTotalProfitPriceIn,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            //毛利率周比 =当日毛利率 - 周毛利率
            if(wRate.compareTo(new BigDecimal("0")) != 0){
                weekRate.setTotalProfitRate(curProfitRate.subtract(wRate).toString());
            }
            if(wRateIn.compareTo(new BigDecimal("0")) != 0){
                weekRate.setTotalProfitRateIn(curProfitRateIn.subtract(wRateIn).toString());
            }
            //销售额环比 = （当日销售额 - 月销售额）/ 月销售额
            if(monthTotalSales.compareTo(new BigDecimal("0")) != 0){
                monthRate.setTotalSalesRate(curAllTotalSales.subtract(monthTotalSales)
                        .divide(monthTotalSales,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            if(monthTotalSalesIn.compareTo(new BigDecimal("0")) != 0){
                monthRate.setTotalSalesRateIn(curAllTotalSalesIn.subtract(monthTotalSalesIn)
                        .divide(monthTotalSalesIn,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            //毛利额环比 =（当日毛利额 - 月毛利额）/ 月毛利额
            if(monthTotalProfitPrice.compareTo(new BigDecimal("0")) != 0){
                monthRate.setTotalProfitPriceRate(curAllTotalProfitPrice.subtract(monthTotalProfitPrice)
                        .divide(monthTotalProfitPrice,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            if(monthTotalProfitPriceIn.compareTo(new BigDecimal("0")) != 0){
                monthRate.setTotalProfitPriceRateIn(curAllTotalProfitPriceIn.subtract(monthTotalProfitPriceIn)
                        .divide(monthTotalProfitPriceIn,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            //毛利率环比 = 当日毛利率 - 月毛利率
            if(mRate.compareTo(new BigDecimal("0")) != 0){
                monthRate.setTotalProfitRate(curProfitRate.subtract(mRate).toString());
            }
            if(mRateIn.compareTo(new BigDecimal("0")) != 0){
                monthRate.setTotalProfitRateIn(curProfitRateIn.subtract(mRateIn).toString());
            }

        }else{
            curInfo.setTotalSales(curTotalSales.toString());
            curInfo.setTotalProfitPrice(curTotalProfitPrice.toString());
            curInfo.setTotalSalesIn(curTotalSalesIn.toString());
            curInfo.setTotalProfitPriceIn(curTotalProfitPriceIn.toString());
            if(curTotalSales.compareTo(new BigDecimal("0")) != 0){
                curInfo.setTotalProfit(curTotalProfitPrice.divide(curTotalSales,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            if(curTotalSalesIn.compareTo(new BigDecimal("0")) != 0){
                curInfo.setTotalProfitIn(curTotalProfitPriceIn.divide(curTotalSalesIn,4,BigDecimal.ROUND_HALF_UP).toString());
            }
        }

        salesListResponse.setCurrent(curInfo);
        salesListResponse.setYesterday(yesterdayInfo);
        salesListResponse.setYesterdayRate(yesterdayRate);
        salesListResponse.setWeekRate(weekRate);
        salesListResponse.setMonthRate(monthRate);
        return salesListResponse;
    }

    /**
     * 查询排行榜
     * @param param
     * @return
     */
    @Override
    public FreshRankResponse queryFreshRank(FreshRankRequest param) throws Exception{
        FreshRankResponse freshRankResponse = new FreshRankResponse();
        //实时数据
        List<FreshRankInfo> cur = new ArrayList<>();
        //当期数据
        List<FreshRankInfo> his = new ArrayList<>();
        //上月损耗
        List<FreshRankInfo> upMonth = new ArrayList<>();
        //上月折价
        List<FreshRankInfo> hisDiscount = new ArrayList<>();
        //同期
        List<FreshRankInfo> same = new ArrayList<>();
        transform(param);
        //筛选生鲜大类
        getFreshList(param);
        Date time = param.getStart();
        //查询当期数据
        if(null != param.getStart() && DateUtils.getBetweenDay(time,new Date()) >= 0){
            //查询实时数据(包含了折价，损耗为零,扫描毛利额)
            cur = freshReportCsmbDao.queryFreshRankInfo(param);
        }
        param.setEnd(time);
        param.setStart(DateUtil.getFirstDayForMonth(time));
        try{
            changeDgDataSource();
            //查询月初到时间节点数据(不包含了折价,包含损耗，前台毛利额,扫描毛利额)
            his = freshReportRmsDao.queryFreshRankCompareInfo(param);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //查询上个月折价
//        param.setEnd(DateUtils.addMonths(time,-1));
//        param.setStart(DateUtils.addMonths(DateUtil.getFirstDayForMonth(time),-1));

        //查询上个月损耗(损耗率不用了 2019-08-05)
        /*try{
            changeDgDataSource();
            upMonth = freshReportRmsDao.queryFreshRankInfo(param);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }*/

        //当日
        if(null != time && DateUtils.getBetweenDay(time, new Date()) >= 0){
            //查询当天同期的数据
            param.setStart(DateUtils.addYears(time,-1));
            List<FreshRankInfo> same01 = null;
            try{
                changeZyDataSource();
                //查询销售,扫描毛利额
                same01 = freshReportZyDao.queryHisCompareFreshRankForTimeInfo(param);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //查询昨日至月初同期的数据
            param.setStart(DateUtils.addYears(DateUtil.getFirstDayForMonth(time),-1));
            param.setEnd(DateUtils.addYears(DateUtils.addDays(time,-1),-1));
            List<FreshRankInfo> same02 = null;
            try{
                changeDgDataSource();
                //查询销售，扫描毛利额，前台毛利额
                same02 = freshReportRmsDao.queryFreshRankCompareInfo(param);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //合并同期数据
            if(null != same01 && same01.size() > 0){
                //将same02的数据合并到same01中
                for(FreshRankInfo rankInfo01 : same01){
                    if(null != same02 && same02.size() > 0){
                        Iterator<FreshRankInfo> it = same02.iterator();
                        while(it.hasNext()){
                            FreshRankInfo rankInfo02 = it.next();
                            //id相同，可比相同，才认为同一条数据（可比可以为null）
                            if(rankInfo01.getId().equals(rankInfo02.getId())
                                    && ((StringUtils.isNotEmpty(rankInfo01.getCompareMark())
                                    && rankInfo01.getCompareMark().equals(rankInfo02.getCompareMark()))
                                    || (StringUtils.isEmpty(rankInfo01.getCompareMark())
                                    && StringUtils.isEmpty(rankInfo02.getCompareMark())))){

                                BigDecimal sales01 = new BigDecimal(StringUtils.isEmpty(rankInfo01.getTotalSales())?"0"
                                        :rankInfo01.getTotalSales());
                                //扫描毛利额
                                BigDecimal profit01 = new BigDecimal(StringUtils.isEmpty(rankInfo01.getTotalProfitPrice())?"0"
                                        :rankInfo01.getTotalProfitPrice());

                                BigDecimal salesIn01 = new BigDecimal(StringUtils.isEmpty(rankInfo01.getTotalSalesIn())?"0"
                                        :rankInfo01.getTotalSalesIn());
                                //扫描毛利额
                                BigDecimal profitIn01 = new BigDecimal(StringUtils.isEmpty(rankInfo01.getTotalProfitPriceIn())?"0"
                                        :rankInfo01.getTotalProfitPriceIn());

                                BigDecimal sales02 = new BigDecimal(StringUtils.isEmpty(rankInfo02.getTotalSales())?"0"
                                        :rankInfo02.getTotalSales());
                                //历史前台毛利额
                                BigDecimal profit02 = new BigDecimal(StringUtils.isEmpty(rankInfo02.getTotalProfitPrice())?"0"
                                        :rankInfo02.getTotalProfitPrice());

                                BigDecimal salesIn02 = new BigDecimal(StringUtils.isEmpty(rankInfo02.getTotalSalesIn())?"0"
                                        :rankInfo02.getTotalSalesIn());
                                //历史前台毛利额
                                BigDecimal profitIn02 = new BigDecimal(StringUtils.isEmpty(rankInfo02.getTotalProfitPriceIn())?"0"
                                        :rankInfo02.getTotalProfitPriceIn());
                                //历史扫描毛利额
                                BigDecimal totalScanningProfitPrice02 = new BigDecimal(StringUtils.isEmpty(rankInfo02.getTotalScanningProfitPrice())
                                        ?"0":rankInfo02.getTotalScanningProfitPrice());

                                BigDecimal totalScanningProfitPriceIn02 = new BigDecimal(StringUtils.isEmpty(rankInfo02.getTotalScanningProfitPriceIn())
                                        ?"0":rankInfo02.getTotalScanningProfitPriceIn());

                                rankInfo01.setTotalSales(sales01.add(sales02).toString());
                                rankInfo01.setTotalProfitPrice(profit01.add(profit02).toString());
                                rankInfo01.setTotalScanningProfitPrice(totalScanningProfitPrice02.toString());

                                rankInfo01.setTotalSalesIn(salesIn01.add(salesIn02).toString());
                                rankInfo01.setTotalProfitPriceIn(profitIn01.add(profitIn02).toString());
                                rankInfo01.setTotalScanningProfitPriceIn(totalScanningProfitPriceIn02.toString());
                                //合并后删除same02数据
                                it.remove();
                                break;
                            }
                        }
                    }else{
                        break;
                    }
                }
                if(null != same02 && same02.size() > 0){
                    //将不存在same01中的数据合并到same02中
                    same01.addAll(same02);
                }
                same.addAll(same01);
            }else{
                if(null != same02 && same02.size() > 0){
                    same.addAll(same02);
                }
            }
        }else{
            //历史
            param.setStart(DateUtils.addYears(DateUtil.getFirstDayForMonth(time),-1));
            param.setEnd(DateUtils.addYears(time,-1));
            try{
                changeDgDataSource();
                same = freshReportRmsDao.queryFreshRankCompareInfo(param);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }

        //保存实时数据的排行数据
        List<FreshRankInfo> curInfo = new ArrayList<>();
        //保存实时数据的可比排行数据
        List<FreshRankInfo> compare = new ArrayList<>();
        if(null != cur && cur.size() > 0){
            getCompareAndAllRankData(cur,compare,curInfo);
        }

        //保存历史数据的排行数据
        List<FreshRankInfo> hisInfo = new ArrayList<>();
        //保存历史数据的可比排行数据
        List<FreshRankInfo> hisCompare = new ArrayList<>();
        if(null != his && his.size() > 0){
            getCompareAndAllRankData(his,hisCompare,hisInfo);
        }

        //保存同期历史数据的排行数据
        List<FreshRankInfo> hisSameInfo = new ArrayList<>();
        //保存同期历史数据的可比排行数据
        List<FreshRankInfo> hisSameCompare = new ArrayList<>();
        if(null != same && same.size() > 0){
            getCompareAndAllRankData(same,hisSameCompare,hisSameInfo);
        }
        //实时数据和历史数据合并
        if(null != curInfo && curInfo.size() > 0){
            mergeHisAndCurFreshRankInfo(curInfo,hisInfo);
        }
        //实时可比数据和历史可比数据合并
        if(null != compare && compare.size() > 0){
            mergeHisAndCurFreshRankInfo(compare,hisCompare);
        }
        //统计
        List<FreshRankInfoResponse> list = sum(hisInfo,hisCompare,hisSameInfo,hisSameCompare,hisDiscount,upMonth,param.getMark());
        //翻译名称
        setOrganizationName(list, param.getMark());
        //排序
        list = list.stream().sorted(new Comparator<FreshRankInfoResponse>() {
            @Override
            public int compare(FreshRankInfoResponse o1, FreshRankInfoResponse o2) {
                BigDecimal value1 = new BigDecimal(StringUtils.isEmpty(o1.getSysRate())?"0":o1.getSysRate());
                BigDecimal value2 = new BigDecimal(StringUtils.isEmpty(o2.getSysRate())?"0":o2.getSysRate());
                return value2.compareTo(value1);
            }
        }).collect(Collectors.toList());
        freshRankResponse.setList(list);
        //计算渗透率
        setKlPermeability(param, time, freshRankResponse);
        return freshRankResponse;
    }

    /**
     * 查询周转天数
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public HblTurnoverDayListResponse queryHblTurnoverDay(FreshReportBaseRequest param) throws Exception {
        HblTurnoverDayListResponse hblTurnoverDayListResponse = new HblTurnoverDayListResponse();
        List<HblTurnoverDayResponse> response = new ArrayList<>();
        transform(param);
        //筛选生鲜大类
        getFreshList(param);
        List<String> deptList = param.getDeptIds();
        Date start = param.getStart();
        Date end = param.getEnd();
        List<HblTurnoverDay> list = freshReportCsmbDao.queryHblTurnoverDay(param,start);
        List<HblTurnoverDay> actualList = null;
        try{
            changeDgDataSource();
            actualList = freshReportRmsDao.queryActualTurnoverdays(param,DateUtil.getFirstDayForMonth(start),start);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //翻译大类名称
        List<UserDept> allDeptList = reportUserDeptService.getAllDept();
        if(null != deptList && deptList.size() > 0){
            for(String deptId : deptList){
                HblTurnoverDayResponse hblTurnoverDayResponse = new HblTurnoverDayResponse();
                hblTurnoverDayResponse.setDeptId(deptId);

                if(null != allDeptList && allDeptList.size() > 0){
                    for(UserDept userDept : allDeptList){
                        if(userDept.getDeptId().intValue() == Integer.valueOf(deptId).intValue()){
                            hblTurnoverDayResponse.setDeptName(userDept.getDeptName());
                        }
                    }
                }

                if(null != list && list.size() > 0){
                    for(HblTurnoverDay hblTurnoverDay : list){
                        if(deptId.equals(hblTurnoverDay.getDeptId())){
                            hblTurnoverDayResponse.setHblTurnoverDays(hblTurnoverDay.getHblTurnoverDays());
                            break;
                        }
                    }
                }

                if(null != actualList && actualList.size() > 0){
                    for(HblTurnoverDay hblTurnoverDay : actualList){
                        if(deptId.equals(hblTurnoverDay.getDeptId())){
                            hblTurnoverDayResponse.setTurnoverDays(hblTurnoverDay.getTurnoverDays());
                            break;
                        }
                    }
                }
                response.add(hblTurnoverDayResponse);
            }
        }
        hblTurnoverDayListResponse.setResult(response);
        return hblTurnoverDayListResponse;
    }

    /**
     * 销售客流总列表
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshklStaticsListResponse queryFreshklStaticsList(FreshReportBaseRequest param) throws Exception {
        FreshklStaticsListResponse response = new FreshklStaticsListResponse();
        FreshklStaticsResponse cur = new FreshklStaticsResponse();
        FreshklStaticsResponse yestaday = new FreshklStaticsResponse();
        FreshklStaticsResponse dayRate = new FreshklStaticsResponse();
        FreshklStaticsResponse weekRate = new FreshklStaticsResponse();
        FreshklStaticsResponse monthRate = new FreshklStaticsResponse();
        FreshKl curData = null;
        FreshKl curAllData = null;
        List<FreshKl> his = null;
        List<FreshKl> hisAll = null;
        FreshKl yesterdayData = null;
        FreshKl weekData = null;
        FreshKl monthData = null;
        FreshKl yesterdayAllData = null;
        FreshKl weekAllData = null;
        FreshKl monthAllData = null;
        transform(param);
        //筛选生鲜大类
        getFreshList(param);
        //判断是否包含当天时间
        if((null == param.getEnd() && DateUtils.getBetweenDay(param.getStart(), new Date()) >= 0)
                || (null != param.getEnd() && DateUtils.getBetweenDay(param.getEnd(), new Date()) >= 0)){
            //查询当天生鲜数据
            curData = freshReportCsmbDao.queryCurFreshKlCount(param,1);
            //查询当天总的数据(没有大类条件)
            curAllData = freshReportCsmbDao.queryCurFreshKlCount(param,2);
        }
        try{
            changeZyDataSource();
            his = freshReportZyDao.queryHisFreshKlCount(param,1);
            hisAll = freshReportZyDao.queryHisFreshKlCount(param,2);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(null != his && his.size() > 0){
            String current = DateUtils.parseDateToStr("yyyyMMdd",param.getStart());
            String yesterday = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addDays(param.getStart()
                    ,-1));
            String week = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addWeeks(param.getStart()
                    ,-1));
            String month = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addMonths(param.getStart()
                    ,-1));

            for(FreshKl kl : his){
                if(current.equals(kl.getTime())){
                    curData = kl;
                }else if(yesterday.equals(kl.getTime())){
                    yesterdayData = kl;
                }else if(week.equals(kl.getTime())){
                    weekData = kl;
                }else if(month.equals(kl.getTime())){
                    monthData = kl;
                }
            }
        }

        if(null != hisAll && hisAll.size() > 0){
            String current = DateUtils.parseDateToStr("yyyyMMdd",param.getStart());
            String yesterday = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addDays(param.getStart()
                    ,-1));
            String week = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addWeeks(param.getStart()
                    ,-1));
            String month = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addMonths(param.getStart()
                    ,-1));

            for(FreshKl kl : hisAll){
                if(current.equals(kl.getTime())){
                    curAllData = kl;
                }else if(yesterday.equals(kl.getTime())){
                    yesterdayAllData = kl;
                }else if(week.equals(kl.getTime())){
                    weekAllData = kl;
                }else if(month.equals(kl.getTime())){
                    monthAllData = kl;
                }
            }
        }

        //当日客流
        BigDecimal curKl = BigDecimal.ZERO;
        //当日总客流
        BigDecimal curAllKl = BigDecimal.ZERO;
        //当日会员客流
        BigDecimal curMemberKl = BigDecimal.ZERO;

        if(null != curData){
            curKl = new BigDecimal(StringUtils.isEmpty(curData.getFreshKlCount())?"0":curData.getFreshKlCount());
            curMemberKl = new BigDecimal(StringUtils.isEmpty(curData.getFreshMemberCount())?"0":curData.getFreshMemberCount());
        }
        if(null != curAllData){
            curAllKl = new BigDecimal(StringUtils.isEmpty(curAllData.getAllKlCount())?"0":curAllData.getAllKlCount());
        }

        cur.setKl(curKl.toString());

        //昨日客流
        BigDecimal yesterdayKl = BigDecimal.ZERO;
        //昨日总客流
        BigDecimal yesterdayAllKl = BigDecimal.ZERO;
        //昨日会员客流
        BigDecimal yesterdayMemberKl = BigDecimal.ZERO;
        if(null != yesterdayData){
            yesterdayKl = new BigDecimal(StringUtils.isEmpty(yesterdayData.getFreshKlCount())?"0"
                    :yesterdayData.getFreshKlCount());
            yesterdayMemberKl = new BigDecimal(StringUtils.isEmpty(yesterdayData.getFreshMemberCount())?"0"
                    :yesterdayData.getFreshMemberCount());
        }
        yestaday.setKl(yesterdayKl.toString());

        if(null != yesterdayAllData){
            yesterdayAllKl = new BigDecimal(StringUtils.isEmpty(yesterdayAllData.getAllKlCount())?"0":yesterdayAllData
                    .getAllKlCount());
        }

        //周客流
        BigDecimal weekKl = BigDecimal.ZERO;
        //周总客流
        BigDecimal weekAllKl = BigDecimal.ZERO;
        //周会员客流
        BigDecimal weekMemberKl = BigDecimal.ZERO;
        if(null != weekData){
            weekKl = new BigDecimal(StringUtils.isEmpty(weekData.getFreshKlCount())?"0":weekData.getFreshKlCount());
            weekMemberKl = new BigDecimal(StringUtils.isEmpty(weekData.getFreshMemberCount())?"0":weekData.getFreshMemberCount());
        }
        if(null != weekAllData){
            weekAllKl = new BigDecimal(StringUtils.isEmpty(weekAllData.getAllKlCount())?"0":weekAllData.getAllKlCount());
        }

        //月客流
        BigDecimal monthKl = BigDecimal.ZERO;
        //月总客流
        BigDecimal monthAllKl = BigDecimal.ZERO;
        //月会员客流
        BigDecimal monthMemberKl = BigDecimal.ZERO;
        if(null != monthData){
            monthKl = new BigDecimal(StringUtils.isEmpty(monthData.getFreshKlCount())?"0":monthData.getFreshKlCount());
            monthMemberKl = new BigDecimal(StringUtils.isEmpty(monthData.getFreshMemberCount())?"0"
                    :monthData.getFreshMemberCount());
        }
        if(null != monthAllData){
            monthAllKl = new BigDecimal(StringUtils.isEmpty(monthAllData.getAllKlCount())?"0":monthAllData.getAllKlCount());
        }

        //当日渗透率 = 当日客流 / 当日总客流
        BigDecimal curPermeability = BigDecimal.ZERO;
        //当日会员渗透率 = 当日会员客流 / 当日总客流
        BigDecimal curMemberPermeability = BigDecimal.ZERO;
        if(curAllKl.compareTo(BigDecimal.ZERO) != 0){
            curPermeability = curKl.divide(curAllKl,4,BigDecimal.ROUND_HALF_UP);
            cur.setPermeability(curPermeability.toString());
            curMemberPermeability = curMemberKl.divide(curAllKl,4,BigDecimal.ROUND_HALF_UP);
            cur.setMemberPermeability(curMemberPermeability.toString());
        }

        //昨日渗透率 = 昨日客流 / 昨日总客流
        BigDecimal yesterdayPermeability = BigDecimal.ZERO;
        //昨日会员渗透率 = 昨日会员客流 / 昨日总客流
        BigDecimal yesterdayMemberPermeability = BigDecimal.ZERO;
        if(yesterdayAllKl.compareTo(BigDecimal.ZERO) != 0){
            yesterdayPermeability = yesterdayKl.divide(yesterdayAllKl,4,BigDecimal.ROUND_HALF_UP);
            yestaday.setPermeability(yesterdayPermeability.toString());
            yesterdayMemberPermeability = yesterdayMemberKl.divide(yesterdayAllKl,4,BigDecimal.ROUND_HALF_UP);
            yestaday.setMemberPermeability(yesterdayMemberPermeability.toString());
        }

        //周渗透率 = 周客流 / 周总客流
        BigDecimal weekPermeability = BigDecimal.ZERO;
        //周会员渗透率 = 周会员客流 / 周总客流
        BigDecimal weekMemberPermeability = BigDecimal.ZERO;
        if(weekAllKl.compareTo(BigDecimal.ZERO) != 0){
            weekPermeability = weekKl.divide(weekAllKl,4,BigDecimal.ROUND_HALF_UP);
            weekMemberPermeability = weekMemberKl.divide(weekAllKl,4,BigDecimal.ROUND_HALF_UP);
        }

        //月渗透率 = 月客流 / 月总客流
        BigDecimal monthPermeability = BigDecimal.ZERO;
        //月会员渗透率 = 月会员客流 / 月总客流
        BigDecimal monthMemberPermeability = BigDecimal.ZERO;
        if(monthAllKl.compareTo(BigDecimal.ZERO) != 0){
            monthPermeability = monthKl.divide(monthAllKl,4,BigDecimal.ROUND_HALF_UP);
            monthMemberPermeability = monthMemberKl.divide(monthAllKl,4,BigDecimal.ROUND_HALF_UP);
        }

        //客流日比 = （当日客流 - 昨日客流） / 昨日客流
//        BigDecimal curKlRate = BigDecimal.ZERO;
        if(yesterdayKl.compareTo(BigDecimal.ZERO) != 0){
            dayRate.setKlRate(curKl.subtract(yesterdayKl).divide(yesterdayKl,4,BigDecimal.ROUND_HALF_UP).toString());
        }

        //渗透率日比 = （当日渗透率 - 昨日渗透率） / 昨日渗透率
//        BigDecimal curPermeabilityRate = BigDecimal.ZERO;
        if(yesterdayPermeability.compareTo(BigDecimal.ZERO) != 0){
            dayRate.setPermeabilityRate(curPermeability.subtract(yesterdayPermeability)
                    .divide(yesterdayPermeability,4,BigDecimal.ROUND_HALF_UP).toString());
        }

        //会员渗透率日比 = （当日会员渗透率 - 昨日会员渗透率） / 昨日会员渗透率
//        BigDecimal curMemberPermeabilityRate = BigDecimal.ZERO;
        if(yesterdayMemberPermeability.compareTo(BigDecimal.ZERO) != 0){
            dayRate.setMemberPermeabilityRate(curMemberPermeability.subtract(yesterdayMemberPermeability)
                    .divide(yesterdayMemberPermeability,4,BigDecimal.ROUND_HALF_UP).toString());
        }

        //客流周比 = （当日客流 - 周客流） / 周客流
//        BigDecimal weekKlRate = BigDecimal.ZERO;
        if(weekKl.compareTo(BigDecimal.ZERO) != 0){
            weekRate.setKlRate(curKl.subtract(weekKl).divide(weekKl,4,BigDecimal.ROUND_HALF_UP).toString());
        }

        //渗透率周比 =（当日渗透率 - 周渗透率） / 周渗透率
//        BigDecimal weekPermeabilityRate = BigDecimal.ZERO;
        if(weekPermeability.compareTo(BigDecimal.ZERO) != 0){
            weekRate.setPermeabilityRate(curPermeability.subtract(weekPermeability)
                    .divide(weekPermeability,4,BigDecimal.ROUND_HALF_UP).toString());
        }

        //会员渗透率周比 = （当日会员渗透率 - 周会员渗透率） / 周会员渗透率
//        BigDecimal weekMemberPermeabilityRate = BigDecimal.ZERO;
        if(weekMemberPermeability.compareTo(BigDecimal.ZERO) != 0){
            weekRate.setMemberPermeabilityRate(curMemberPermeability.subtract(weekMemberPermeability)
                    .divide(weekMemberPermeability,4,BigDecimal.ROUND_HALF_UP).toString());
        }

        //客流月比 = （当日客流 - 月客流） / 月客流
//        BigDecimal monthKlRate = BigDecimal.ZERO;
        if(monthKl.compareTo(BigDecimal.ZERO) != 0){
            monthRate.setKlRate(curKl.subtract(monthKl).divide(monthKl,4,BigDecimal.ROUND_HALF_UP).toString());
        }

        //渗透率月比 =（当日渗透率 - 月渗透率） / 月渗透率
//        BigDecimal monthPermeabilityRate = BigDecimal.ZERO;
        if(monthPermeability.compareTo(BigDecimal.ZERO) != 0){
            monthRate.setPermeabilityRate(curPermeability.subtract(monthPermeability)
                    .divide(monthPermeability,4,BigDecimal.ROUND_HALF_UP).toString());
        }

        //会员渗透率月比 = （当日会员渗透率 - 月会员渗透率） / 月会员渗透率
//        BigDecimal monthMemberPermeabilityRate = BigDecimal.ZERO;
        if(monthMemberPermeability.compareTo(BigDecimal.ZERO) != 0){
            monthRate.setMemberPermeabilityRate(curMemberPermeability.subtract(monthMemberPermeability)
                    .divide(monthMemberPermeability,4,BigDecimal.ROUND_HALF_UP).toString());
        }

        response.setMonthRate(monthRate);
        response.setWeekRate(weekRate);
        response.setDayRate(dayRate);
        response.setYestaday(yestaday);
        response.setCur(cur);
        return response;
    }

    /**
     * 查询生鲜客流列表
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshDeptKlListResponse queryFreshDeptKlList(FreshReportBaseRequest param) throws Exception {
        transform(param);
        //筛选生鲜大类
        getFreshList(param);
        //大类列表
        List<String> deptList = param.getDeptIds();
        Date start = param.getStart();
        String hour = DateUtils.parseDateToStr("HH",new Date());
//        String hour = DateUtils.parseDateToStr("HH",new Date());
        //实时的生鲜客流和生鲜会员客流
        List<FreshDeptKl> currentKlList = null;
        //实时总客流
        FreshKl currentAllKl = null;
        //历史生鲜客流
        List<FreshDeptKl> hisFreshKlList = null;
        //历史生鲜会员客流
        List<FreshDeptKl> hisFreshVipKlList = null;
        //历史生鲜总客流
        List<FreshKl> hisFreshAllList = null;

        if(DateUtils.getBetweenDay(start,new Date()) >= 0){
            //查询实时
            //查询实时的生鲜客流和生鲜会员客流
            currentKlList = freshReportCsmbDao.queryCurFreshDeptKl(param,start);
            //实时生鲜总客流
            currentAllKl = freshReportCsmbDao.queryCurFreshKlCount(param,2);
        }
        //查询历史
        try{
            changeZyDataSource();
            //查询历史生鲜客流
            hisFreshKlList = freshReportZyDao.queryHisFreshDeptKl(param,start,hour);
            //查询历史生鲜会员客流
            hisFreshVipKlList = freshReportZyDao.queryHisFreshVipDeptKl(param,start,hour);
            //历史生鲜总客流
            hisFreshAllList = freshReportZyDao.queryHisFreshKlCount(param,2);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //计算客流量行
        return sumFreshKl(deptList,currentKlList,currentAllKl,hisFreshKlList,hisFreshVipKlList,hisFreshAllList,start);
    }

    /**
     * 查询排名名次
     * @param param
     * @param type(1-全司，2-省级，3-区域级，4-门店级)
     * @return
     * @throws Exception
     */
    @Override
    public FreshRankGradeResponse queryFreshRankGrade(FreshReportBaseRequest param,int type) throws Exception {
        FreshRankGradeResponse freshRankGradeResponse = new FreshRankGradeResponse();
        transform(param);
        //筛选生鲜大类
        getFreshList(param);
        Date start = param.getStart();
        List<FreshRankGrade> dataList = new ArrayList<FreshRankGrade>();
        List<FreshRankGrade> dataListIn = new ArrayList<FreshRankGrade>();
        //判断权限(全司级别的不排名，其他等级的如果跨多个的也不进行排名)
        //1-全司，2-省级，3-区域级，4-门店级
        //需要排名的id
        String id = "";
        if(1 == type){
            return freshRankGradeResponse;
        }else if(2 == type){
            if(null != param.getProvinceIds() && param.getProvinceIds().size() > 1){
                return freshRankGradeResponse;
            }
            id = param.getProvinceId();
        }else if(3 == type){
            if(null != param.getAreaIds() && param.getAreaIds().size() > 1){
                return freshRankGradeResponse;
            }
            id = param.getAreaId();
        }else if(4 == type){
            if(null != param.getStoreIds() && param.getStoreIds().size() > 1){
                return freshRankGradeResponse;
            }
            id = param.getStoreId();
        }
        //查询月初至时间节点的扫描毛利额和销售额
        //查询实时数据
        if(DateUtils.getBetweenDay(start,new Date()) >= 0){
            //查询今日销售额和扫描毛利额
            List<FreshRankGrade> curFreshRankGradeList = freshReportCsmbDao.queryCurFreshRankGrade(param,start,type);
            //查询历史销售额和扫描毛利额（月初到昨日）
            List<FreshRankGrade> hisFreshRankGradeList = freshReportCsmbDao.queryHisFreshRankGrade(param
                    ,DateUtil.getFirstDayForMonth(start)
                    ,DateUtils.addDays(start,-1)
                    ,type);
            if(null != curFreshRankGradeList && curFreshRankGradeList.size() > 0){
                dataList.addAll(curFreshRankGradeList);
            }
            //将月初到昨日的数据合并到dataList中
            if(null != hisFreshRankGradeList && hisFreshRankGradeList.size() > 0){
                Iterator<FreshRankGrade> it = hisFreshRankGradeList.iterator();
                while(it.hasNext()){
                    //如果dataList为空，则不进行循环合并操作，直接将整个历史List放入dataList中
                    if(dataList.size() == 0){
                        break;
                    }
                    FreshRankGrade hisFreshRankGrade = it.next();
                    for(FreshRankGrade freshRankGrade : dataList){
                        //将月初到昨日的数据合并到dataList中
                        if(hisFreshRankGrade.getId().equals(freshRankGrade.getId())){
                            BigDecimal hisSale = new BigDecimal(StringUtils.isEmpty(hisFreshRankGrade.getTotalSale())?"0":hisFreshRankGrade.getTotalSale());
                            BigDecimal hisRate = new BigDecimal(StringUtils.isEmpty(hisFreshRankGrade.getTotalScanningRate())
                                    ?"0":hisFreshRankGrade.getTotalScanningRate());

                            BigDecimal hisSaleIn = new BigDecimal(StringUtils.isEmpty(hisFreshRankGrade.getTotalSaleIn())?"0":hisFreshRankGrade.getTotalSaleIn());
                            BigDecimal hisRateIn = new BigDecimal(StringUtils.isEmpty(hisFreshRankGrade.getTotalScanningRateIn())
                                    ?"0":hisFreshRankGrade.getTotalScanningRateIn());

                            BigDecimal sale = new BigDecimal(StringUtils.isEmpty(freshRankGrade.getTotalSale())?"0":freshRankGrade.getTotalSale());
                            BigDecimal rate = new BigDecimal(StringUtils.isEmpty(freshRankGrade.getTotalScanningRate())
                                    ?"0":freshRankGrade.getTotalScanningRate());

                            BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(freshRankGrade.getTotalSaleIn())?"0":freshRankGrade.getTotalSaleIn());
                            BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(freshRankGrade.getTotalScanningRateIn())
                                    ?"0":freshRankGrade.getTotalScanningRateIn());

                            freshRankGrade.setTotalSale(hisSale.add(sale).toString());
                            freshRankGrade.setTotalScanningRate(hisRate.add(rate).toString());

                            freshRankGrade.setTotalSaleIn(hisSaleIn.add(saleIn).toString());
                            freshRankGrade.setTotalScanningRateIn(hisRateIn.add(rateIn).toString());
                            //合并后删除
                            it.remove();
                            break;
                        }
                    }
                }
                //将整个历史hisFreshRankGradeList或者将循环后仍然保留的数据直接放入dataList中
                if(hisFreshRankGradeList.size() > 0){
                    dataList.addAll(hisFreshRankGradeList);
                }
            }
        }else{
            //查询历史销售额和扫描毛利额（月初到当天）
            dataList = freshReportCsmbDao.queryHisFreshRankGrade(param
                    ,DateUtil.getFirstDayForMonth(start)
                    ,start
                    ,type);
        }
        //计算扫描毛利率
        if(null != dataList && dataList.size() > 0){
            for(FreshRankGrade freshRankGrade : dataList){
                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(freshRankGrade.getTotalSale())?"0"
                        :freshRankGrade.getTotalSale());
                BigDecimal rate = new BigDecimal(StringUtils.isEmpty(freshRankGrade.getTotalScanningRate())?"0"
                        :freshRankGrade.getTotalScanningRate());
                BigDecimal profit = BigDecimal.ZERO;

                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(freshRankGrade.getTotalSaleIn())?"0"
                        :freshRankGrade.getTotalSaleIn());
                BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(freshRankGrade.getTotalScanningRateIn())?"0"
                        :freshRankGrade.getTotalScanningRateIn());
                BigDecimal profitIn = BigDecimal.ZERO;

                if(sale.compareTo(BigDecimal.ZERO) != 0){
                    profit = rate.divide(sale,4,BigDecimal.ROUND_HALF_UP);
                }
                if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                    profitIn = rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP);
                }
                freshRankGrade.setTotalProfit(profit.toString());
                freshRankGrade.setTotalProfitIn(profitIn.toString());
            }
            //按照扫描毛利率排序
            dataList = dataList.stream().sorted(new Comparator<FreshRankGrade>() {
                @Override
                public int compare(FreshRankGrade o1, FreshRankGrade o2) {
                    BigDecimal value1 = new BigDecimal(o1.getTotalProfit());
                    BigDecimal value2 = new BigDecimal(o2.getTotalProfit());
                    return value2.compareTo(value1);
                }
            }).collect(Collectors.toList());

            dataListIn = dataList.stream().sorted(new Comparator<FreshRankGrade>() {
                @Override
                public int compare(FreshRankGrade o1, FreshRankGrade o2) {
                    BigDecimal value1 = new BigDecimal(o1.getTotalProfitIn());
                    BigDecimal value2 = new BigDecimal(o2.getTotalProfitIn());
                    return value2.compareTo(value1);
                }
            }).collect(Collectors.toList());
            int num = 0;
            for(FreshRankGrade freshRankGrade : dataList){
                if(StringUtils.isNotEmpty(id)){
                    if(id.equals(freshRankGrade.getId())){
                        break;
                    }
                    num = num + 1;
                }
            }
            freshRankGradeResponse.setType(type);
            freshRankGradeResponse.setRankNum(num);
            freshRankGradeResponse.setCount(dataList.size());

            int numIn = 0;
            for(FreshRankGrade freshRankGrade : dataListIn){
                if(StringUtils.isNotEmpty(id)){
                    if(id.equals(freshRankGrade.getId())){
                        break;
                    }
                    numIn = numIn + 1;
                }
            }
            freshRankGradeResponse.setRankNumIn(numIn);
            freshRankGradeResponse.setCountIn(dataListIn.size());
        }
        return freshRankGradeResponse;
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
        transformPage(param);
        //筛选生鲜大类
        getFreshListPage(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        TurnoverDay turnoverDay = null;
        try{
            changeDgDataSource();
            if(null == end){
                if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                    //月初到昨日
                    turnoverDay = reportPageRmsDao.queryActualTurnoverDay(param,DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1));
                }else{
                    turnoverDay = reportPageRmsDao.queryActualTurnoverDay(param,DateUtil.getFirstDayForMonth(start),
                            start);
                }
            }else{
                if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                    //开始时间到昨日
                    turnoverDay = reportPageRmsDao.queryActualTurnoverDay(param,start,
                            DateUtils.addDays(end,-1));
                }else{
                    turnoverDay = reportPageRmsDao.queryActualTurnoverDay(param,start,
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
     * 查询毛利额达成率
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public AchievingRateResponse queryRateAchievingRate(FreshReportBaseRequest param) throws Exception {
        AchievingRateResponse achievingRateResponse = new AchievingRateResponse();
        BaseSaleModel baseSaleModel = new BaseSaleModel();
        GoalSale goalSale = null;
        //临时使用
        if(StringUtils.isEmpty(param.getProvinceId())
                && StringUtils.isEmpty(param.getAreaId())
                && StringUtils.isEmpty(param.getStoreId())){
            return achievingRateResponse;
        }
        transform(param);
        //筛选生鲜大类
        getFreshList(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        //查询目标值
        try{
            changeDgDataSource();
            goalSale = freshReportRmsDao.queryGoalSale(param,start,end);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询毛利额
        if(null == end){
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                //当天数据
                BaseSaleModel cur = freshReportCsmbDao.queryCurBaseSaleModel(param,start,null);
                //月初到昨日
                BaseSaleModel his = freshReportCsmbDao.queryHistoryBaseSaleModel(param,
                        DateUtil.getFirstDayForMonth(start),DateUtils.addDays(start,-1));
                BigDecimal curRate = BigDecimal.ZERO;
                BigDecimal hisRate = BigDecimal.ZERO;
                if(null != cur){
                    curRate = new BigDecimal(StringUtils.isEmpty(cur.getTotalScanningRate())?"0":cur.getTotalScanningRate());
                }
                if(null != his){
                    hisRate = new BigDecimal(StringUtils.isEmpty(his.getTotalFrontDeskRate())?"0":his.getTotalFrontDeskRate());
                }
                //当天最终毛利额 = 扫描毛利额 + 前台毛利额
                baseSaleModel.setTotalRate(curRate.add(hisRate).toString());
            }else{
                //查询历史某天数据
                //月初到历史某天数据
                baseSaleModel = freshReportCsmbDao.queryHistoryBaseSaleModel(param,DateUtil.getFirstDayForMonth(start),start);
                //历史最终毛利额 = 前台毛利额
                if(null != baseSaleModel){
                    baseSaleModel.setTotalRate(baseSaleModel.getTotalFrontDeskRate());
                }
            }
        }else{
            if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                baseSaleModel = new BaseSaleModel();
                BaseSaleModel cur = freshReportCsmbDao.queryCurBaseSaleModel(param,end,null);
                BaseSaleModel his = freshReportCsmbDao.queryHistoryBaseSaleModel(param,start
                        ,DateUtils.addDays(end,-1));
                //当天最终毛利额 = 扫描毛利额
                BigDecimal curRate = BigDecimal.ZERO;
                if(null != cur){
                    curRate = new BigDecimal(StringUtils.isEmpty(cur.getTotalScanningRate())?"0"
                            :cur.getTotalScanningRate());
                }
                //历史最终毛利额 = 前台毛利额
                BigDecimal hisRate = BigDecimal.ZERO;
                if(null != his){
                    hisRate = new BigDecimal(StringUtils.isEmpty(his.getTotalFrontDeskRate())?"0"
                            :his.getTotalFrontDeskRate());
                }
                baseSaleModel.setTotalRate(curRate.add(hisRate).toString());
            }else{
                //查询历史数据
                baseSaleModel = freshReportCsmbDao.queryHistoryBaseSaleModel(param,start,end);
                //历史最终毛利额 = 前台毛利额
                if(null != baseSaleModel){
                    baseSaleModel.setTotalRate(baseSaleModel.getTotalFrontDeskRate());
                }
            }
        }

        if(null != baseSaleModel && null != goalSale){
            BigDecimal goal = new BigDecimal(StringUtils.isEmpty(goalSale.getRate())?"0"
                    :goalSale.getRate());
            BigDecimal rate = new BigDecimal(StringUtils.isEmpty(baseSaleModel.getTotalRate())?"0"
                    :baseSaleModel.getTotalRate());
            if(goal.compareTo(BigDecimal.ZERO) != 0){
                achievingRateResponse.setRateAchievingRate(rate.
                        divide(goal,4,BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        return achievingRateResponse;
    }

    /**
     * 汇总客流
     * @param deptList
     * @param currentKlList
     * @param currentAllKl
     * @param hisFreshKlList
     * @param hisFreshVipKlList
     * @param hisFreshAllList
     * @param time
     * @return
     */
    private FreshDeptKlListResponse sumFreshKl(List<String> deptList,
                                                 List<FreshDeptKl> currentKlList,
                                                 FreshKl currentAllKl,
                                                 List<FreshDeptKl> hisFreshKlList,
                                                 List<FreshDeptKl> hisFreshVipKlList,
                                                 List<FreshKl> hisFreshAllList,
                                                 Date time) throws Exception {
        FreshDeptKlListResponse freshDeptKlListResponse = new FreshDeptKlListResponse();
        //客流量行
        List<FreshDeptKlResponse> kl = new ArrayList<>();
        //渗透率行
        List<FreshDeptPermeabilityResponse> permeability = new ArrayList<>();
        //会员渗透率行
        List<FreshDeptMemberPermeabilityResponse> memberPermeability = new ArrayList<>();

        String day = DateUtils.parseDateToStr("yyyyMMdd",time);
        String yesterday = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addDays(time,-1));
        String week = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addWeeks(time,-1));
        String month = DateUtils.parseDateToStr("yyyyMMdd",DateUtils.addMonths(time,-1));

        if(null != deptList && deptList.size() > 0){
            //实时生鲜总客流
            BigDecimal curAllKl = BigDecimal.ZERO;
            if(null != currentAllKl){
                curAllKl = new BigDecimal(StringUtils.isEmpty(currentAllKl.getAllKlCount())?"0"
                        :currentAllKl.getAllKlCount());
            }
            //历史当天总客流
            BigDecimal hisAllKl = BigDecimal.ZERO;
            //历史昨日总客流
            BigDecimal yesterdayAllKl = BigDecimal.ZERO;
            //历史周总客流
            BigDecimal weekAllKl = BigDecimal.ZERO;
            //历史月总客流
            BigDecimal monthAllKl = BigDecimal.ZERO;
            if(null != hisFreshAllList && hisFreshAllList.size() > 0){
                for(FreshKl freshKl : hisFreshAllList){
                    if(day.equals(freshKl.getTime())){
                        hisAllKl = new BigDecimal(StringUtils.isEmpty(freshKl.getAllKlCount())?"0":freshKl.getAllKlCount());
                    }else if(yesterday.equals(freshKl.getTime())){
                        yesterdayAllKl = new BigDecimal(StringUtils.isEmpty(freshKl.getAllKlCount())?"0":freshKl.getAllKlCount());
                    }else if(week.equals(freshKl.getTime())){
                        weekAllKl = new BigDecimal(StringUtils.isEmpty(freshKl.getAllKlCount())?"0":freshKl.getAllKlCount());
                    }else if(month.equals(freshKl.getTime())){
                        monthAllKl = new BigDecimal(StringUtils.isEmpty(freshKl.getAllKlCount())?"0":freshKl.getAllKlCount());
                    }
                }
            }
            List<UserDept> allDeptList = reportUserDeptService.getAllDept();
            for(String deptId : deptList){
                String deptName = "";
                if(null != allDeptList && allDeptList.size() > 0){
                    for(UserDept userDept : allDeptList){
                        if(Integer.valueOf(deptId).intValue() == userDept.getDeptId().intValue()){
                            deptName = userDept.getDeptName();
                            break;
                        }
                    }
                }

                //实时生鲜客流
                BigDecimal curKl = BigDecimal.ZERO;
                //实时生鲜会员客流
                BigDecimal curVipKl = BigDecimal.ZERO;

                if(null != currentKlList && currentKlList.size() > 0){
                    for(FreshDeptKl freshDeptKl : currentKlList){
                        if(deptId.equals(freshDeptKl.getDeptId())){
                            curKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshKlCount())?"0"
                                    :freshDeptKl.getFreshKlCount());
                            curVipKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshMemberCount())?"0"
                                    :freshDeptKl.getFreshMemberCount());
                        }
                    }
                }

                //历史当天生鲜客流
                BigDecimal hisKl = BigDecimal.ZERO;
                //历史昨日生鲜客流
                BigDecimal yesterdayKl = BigDecimal.ZERO;
                //历史周生鲜客流
                BigDecimal weekKl = BigDecimal.ZERO;
                //历史月生鲜客流
                BigDecimal monthKl = BigDecimal.ZERO;

                //历史当天生鲜会员客流
                BigDecimal hisVipKl = BigDecimal.ZERO;
                //历史昨日生鲜会员客流
                BigDecimal yesterdayVipKl = BigDecimal.ZERO;
                //历史周生鲜会员客流
                BigDecimal weekVipKl = BigDecimal.ZERO;
                //历史月生鲜会员客流
                BigDecimal monthVipKl = BigDecimal.ZERO;

                if(null != hisFreshKlList && hisFreshKlList.size() > 0){
                    for(FreshDeptKl freshDeptKl : hisFreshKlList){
                        if(deptId.equals(freshDeptKl.getDeptId())){
                            if(day.equals(freshDeptKl.getTime())){
                                hisKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshKlCount())?"0":freshDeptKl.getFreshKlCount());
                            }else if(yesterday.equals(freshDeptKl.getTime())){
                                yesterdayKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshKlCount())?"0":freshDeptKl.getFreshKlCount());
                            }else if(week.equals(freshDeptKl.getTime())){
                                weekKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshKlCount())?"0":freshDeptKl.getFreshKlCount());
                            }else if(month.equals(freshDeptKl.getTime())){
                                monthKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshKlCount())?"0":freshDeptKl.getFreshKlCount());
                            }
                        }
                    }
                }

                if(null != hisFreshVipKlList && hisFreshVipKlList.size() > 0){
                    for(FreshDeptKl freshDeptKl : hisFreshVipKlList){
                        if(deptId.equals(freshDeptKl.getDeptId())){
                            if(day.equals(freshDeptKl.getTime())){
                                hisVipKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshMemberCount())?"0":freshDeptKl.getFreshMemberCount());
                            }else if(yesterday.equals(freshDeptKl.getTime())){
                                yesterdayVipKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshMemberCount())?"0":freshDeptKl.getFreshMemberCount());
                            }else if(week.equals(freshDeptKl.getTime())){
                                weekVipKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshMemberCount())?"0":freshDeptKl.getFreshMemberCount());
                            }else if(month.equals(freshDeptKl.getTime())){
                                monthVipKl = new BigDecimal(StringUtils.isEmpty(freshDeptKl.getFreshMemberCount())?"0":freshDeptKl.getFreshMemberCount());
                            }
                        }
                    }
                }

                //计算客流行
                FreshDeptKlResponse freshDeptKlResponse = new FreshDeptKlResponse();
                freshDeptKlResponse.setDeptId(deptId);
                freshDeptKlResponse.setDeptName(deptName);
                //当天生鲜客流 = 实时生鲜客流 + 历史当天生鲜客流
                freshDeptKlResponse.setKl(curKl.add(hisKl).toString());
                //昨日生鲜客流
                freshDeptKlResponse.setYesterDayKl(yesterdayKl.toString());
                //生鲜客流日比 = 当天生鲜客流 / 昨日生鲜客流 -1
                if(yesterdayKl.compareTo(BigDecimal.ZERO) != 0){
                    freshDeptKlResponse.setKlRate(curKl.add(hisKl)
                            .divide(yesterdayKl,2,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                }
                //生鲜客流周比 = 当天生鲜客流 / 周生鲜客流 -1
                if(weekKl.compareTo(BigDecimal.ZERO) != 0){
                    freshDeptKlResponse.setWeekKlRate(curKl.add(hisKl)
                            .divide(weekKl,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                }
                //生鲜客流环比 = 当天生鲜客流 / 月生鲜客流 -1
                if(monthKl.compareTo(BigDecimal.ZERO) != 0){
                    freshDeptKlResponse.setMonthKlRate(curKl.add(hisKl)
                            .divide(monthKl,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                }
                kl.add(freshDeptKlResponse);


                //计算渗透率行
                FreshDeptPermeabilityResponse freshDeptPermeabilityResponse = new FreshDeptPermeabilityResponse();
                freshDeptPermeabilityResponse.setDeptName(deptName);
                freshDeptPermeabilityResponse.setDeptId(deptId);
                //当天生鲜总客流 = 实时生鲜总客流 + 历史当天生鲜总客流
                BigDecimal dayAllKl = curAllKl.add(hisAllKl);
                //当天生鲜渗透率 = 当天生鲜客流 / 当天生鲜总客流
                BigDecimal dayKlRate = BigDecimal.ZERO;
                if(dayAllKl.compareTo(BigDecimal.ZERO) != 0){
                    dayKlRate = curKl.add(hisKl).divide(dayAllKl,4,BigDecimal.ROUND_HALF_UP);
                    freshDeptPermeabilityResponse.setPermeability(dayKlRate.toString());
                }
                //昨日生鲜渗透率 = 昨日生鲜客流 / 昨日生鲜总客流
                BigDecimal yesterdayKlRate = BigDecimal.ZERO;
                if(yesterdayAllKl.compareTo(BigDecimal.ZERO) != 0){
                    yesterdayKlRate = yesterdayKl.divide(yesterdayAllKl,4,BigDecimal.ROUND_HALF_UP);
                    freshDeptPermeabilityResponse.setYesterdayPermeability(yesterdayKlRate.toString());
                }

                //日比 = 当天生鲜渗透率 / 昨日生鲜渗透率 - 1
                if(yesterdayKlRate.compareTo(BigDecimal.ZERO) != 0){
                    freshDeptPermeabilityResponse.setPermeabilityRate(dayKlRate.divide(yesterdayKlRate,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                }

                //周生鲜渗透率 = 周生鲜客流 / 周生鲜总客流
                BigDecimal weekKlRate = BigDecimal.ZERO;
                if(weekAllKl.compareTo(BigDecimal.ZERO) != 0){
                    weekKlRate = weekKl.divide(weekAllKl,4,BigDecimal.ROUND_HALF_UP);
                }

                //周比 = 当天生鲜渗透率 / 周生鲜渗透率 - 1
                if(weekKlRate.compareTo(BigDecimal.ZERO) != 0){
                    freshDeptPermeabilityResponse.setWeekPermeabilityRate(dayKlRate.divide(weekKlRate,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                }

                //月生鲜渗透率 = 月生鲜客流 / 月生鲜总客流
                BigDecimal monthKlRate = BigDecimal.ZERO;
                if(monthAllKl.compareTo(BigDecimal.ZERO) != 0){
                    monthKlRate = monthKl.divide(monthAllKl,4,BigDecimal.ROUND_HALF_UP);
                }
                //环比 = 当天生鲜渗透率 / 月生鲜渗透率 - 1
                if(monthKlRate.compareTo(BigDecimal.ZERO) != 0){
                    freshDeptPermeabilityResponse.setMonthPermeabilityRate(dayKlRate.divide(monthKlRate,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                }
                permeability.add(freshDeptPermeabilityResponse);

                //计算生鲜会员渗透率行
                FreshDeptMemberPermeabilityResponse freshDeptMemberPermeabilityResponse = new FreshDeptMemberPermeabilityResponse();
                freshDeptMemberPermeabilityResponse.setDeptId(deptId);
                freshDeptMemberPermeabilityResponse.setDeptName(deptName);
                //当天生鲜会员渗透率 = 当天生鲜会员客流 / 当天生鲜总客流
                BigDecimal dayVipKlRate = BigDecimal.ZERO;
                if(dayAllKl.compareTo(BigDecimal.ZERO) != 0){
                    dayVipKlRate = curVipKl.add(hisVipKl).divide(dayAllKl,4,BigDecimal.ROUND_HALF_UP);
                    freshDeptMemberPermeabilityResponse.setMemberPermeability(dayVipKlRate.toString());
                }
                //昨日生鲜会员渗透率 = 昨日生鲜会员客流 / 昨日生鲜总客流
                BigDecimal yesterdayVipKlRate = BigDecimal.ZERO;
                if(yesterdayAllKl.compareTo(BigDecimal.ZERO) != 0){
                    yesterdayVipKlRate = yesterdayVipKl.divide(yesterdayAllKl,4,BigDecimal.ROUND_HALF_UP);
                    freshDeptMemberPermeabilityResponse.setYesterdayMemberPermeability(yesterdayVipKlRate.toString());
                }
                //会员日比 = 当天生鲜会员渗透率 / 昨日生鲜会员渗透率 - 1
                if(yesterdayVipKlRate.compareTo(BigDecimal.ZERO) != 0){
                    freshDeptMemberPermeabilityResponse.setMemberPermeabilityRate(dayVipKlRate.divide(yesterdayVipKlRate,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                }

                //周生鲜会员渗透率 = 周生鲜会员客流 / 周生鲜总客流
                BigDecimal weekVipKlRate = BigDecimal.ZERO;
                if(weekAllKl.compareTo(BigDecimal.ZERO) != 0){
                    weekVipKlRate = weekVipKl.divide(weekAllKl,4,BigDecimal.ROUND_HALF_UP);
                }
                //会员周比 = 当天生鲜会员渗透率 / 周生鲜会员渗透率 - 1
                if(weekVipKlRate.compareTo(BigDecimal.ZERO) != 0){
                    freshDeptMemberPermeabilityResponse.setWeekMemberPermeabilityRate(dayVipKlRate.divide(weekVipKlRate,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                }


                //月生鲜会员渗透率 = 月生鲜会员客流 / 月生鲜总客流
                BigDecimal monthVipKlRate = BigDecimal.ZERO;
                if(monthAllKl.compareTo(BigDecimal.ZERO) != 0){
                    monthVipKlRate = monthVipKl.divide(monthAllKl,4,BigDecimal.ROUND_HALF_UP);
                }
                //会员环比 = 当天生鲜会员渗透率 / 月生鲜会员渗透率 - 1
                if(monthVipKlRate.compareTo(BigDecimal.ZERO) != 0){
                    freshDeptMemberPermeabilityResponse.setMonthMemberPermeabilityRate(dayVipKlRate.divide(monthVipKlRate,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
                }
                memberPermeability.add(freshDeptMemberPermeabilityResponse);
            }
        }
        freshDeptKlListResponse.setKl(kl);
        freshDeptKlListResponse.setPermeability(permeability);
        freshDeptKlListResponse.setMemberPermeability(memberPermeability);
        return freshDeptKlListResponse;
    };


    /**
     * 翻译名称
     * @param target
     * @param type
     * @throws Exception
     */
    private void setOrganizationName(List<FreshRankInfoResponse> target, int type) throws Exception {
        if(4 == type){
            List<UserDept> allDeptList = reportUserDeptService.getAllDept();
            if(null != allDeptList && allDeptList.size() > 0){
                for(FreshRankInfoResponse response : target){
                    for(UserDept userDept : allDeptList){
                        if(Integer.valueOf(response.getId()).intValue() == userDept.getDeptId().intValue()){
                            response.setName(userDept.getDeptName());
                            break;
                        }
                    }
                }
            }
        }else {
            List<OrganizationForFresh> list = freshReportCsmbDao.queryAllOrganization();
            for(FreshRankInfoResponse response : target){
                for(OrganizationForFresh organization : list){
                    if(1 == type){
                        if(response.getId().equals(organization.getProvinceId())){
                            response.setName(organization.getProvinceName());
                            break;
                        }
                    }else if(2 == type){
                        if(response.getId().equals(organization.getAreaId())){
                            response.setName(organization.getAreaName());
                            break;
                        }
                    }else if(3 == type){
                        if(response.getId().equals(organization.getStoreId())){
                            response.setName(organization.getStoreName());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 统计
     * @param info
     * @param compare
     * @param sameInfo
     * @param sameCompareInfo
     * @return
     */
    private List<FreshRankInfoResponse> sum(List<FreshRankInfo> info,
                                            List<FreshRankInfo> compare,
                                            List<FreshRankInfo> sameInfo,
                                            List<FreshRankInfo> sameCompareInfo,
                                            List<FreshRankInfo> discount,
                                            List<FreshRankInfo> upMonth,
                                            int type){
        List<FreshRankInfoResponse> list = new ArrayList<FreshRankInfoResponse>();
        for(FreshRankInfo target : info){
            //当期销售额
            BigDecimal totalSales = new BigDecimal(StringUtils.isEmpty(target.getTotalSales())?"0":target.getTotalSales());
            //当期毛利额（扫描 + 前台）
            BigDecimal totalProfitPrice = new BigDecimal(StringUtils.isEmpty(target.getTotalProfitPrice())?"0"
                    :target.getTotalProfitPrice());
            //扫描毛利额（扫描）
            BigDecimal totalScanningProfitPrice = new BigDecimal(StringUtils.isEmpty(target.getTotalScanningProfitPrice())?"0"
                    :target.getTotalScanningProfitPrice());

            BigDecimal totalSalesIn = new BigDecimal(StringUtils.isEmpty(target.getTotalSalesIn())?"0":target.getTotalSalesIn());
            //当期毛利额（扫描 + 前台）
            BigDecimal totalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(target.getTotalProfitPriceIn())?"0"
                    :target.getTotalProfitPriceIn());
            //扫描毛利额（扫描）
            BigDecimal totalScanningProfitPriceIn = new BigDecimal(StringUtils.isEmpty(target.getTotalScanningProfitPriceIn())?"0"
                    :target.getTotalScanningProfitPriceIn());
            //当期毛利率 = 当期毛利额（扫描 + 前台） / 当期销售额
            BigDecimal rate = BigDecimal.ZERO;
            BigDecimal rateIn = BigDecimal.ZERO;
            if(totalSales.compareTo(BigDecimal.ZERO) != 0){
                rate = totalProfitPrice.divide(totalSales,4,BigDecimal.ROUND_HALF_UP);
            }
            if(totalSalesIn.compareTo(BigDecimal.ZERO) != 0){
                rateIn = totalProfitPriceIn.divide(totalSalesIn,4,BigDecimal.ROUND_HALF_UP);
            }
            //系统毛利率 = 扫描毛利额 / 当期销售额
            BigDecimal sysRate = BigDecimal.ZERO;
            BigDecimal sysRateIn = BigDecimal.ZERO;
            if(totalSales.compareTo(BigDecimal.ZERO) != 0){
                sysRate = totalScanningProfitPrice.divide(totalSales,4,BigDecimal.ROUND_HALF_UP);
            }
            if(totalSalesIn.compareTo(BigDecimal.ZERO) != 0){
                sysRateIn = totalScanningProfitPriceIn.divide(totalSalesIn,4,BigDecimal.ROUND_HALF_UP);
            }

            //当期可比销售
            BigDecimal curCompareTotalSales = BigDecimal.ZERO;
            BigDecimal curCompareTotalSalesIn = BigDecimal.ZERO;
            for(FreshRankInfo freshRankInfo : compare){
                if(target.getId().equals(freshRankInfo.getId())){
                    curCompareTotalSales = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSales())?"0": freshRankInfo.getTotalSales());
                    curCompareTotalSalesIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSalesIn())?"0": freshRankInfo.getTotalSalesIn());
                    break;
                }
            }

            //当期可比毛利额
            BigDecimal curCompareTotalProfitPrice = BigDecimal.ZERO;
            BigDecimal curCompareTotalProfitPriceIn = BigDecimal.ZERO;
            for(FreshRankInfo freshRankInfo : compare){
                if(target.getId().equals(freshRankInfo.getId())){
                    curCompareTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalProfitPrice())?"0": freshRankInfo.getTotalProfitPrice());
                    curCompareTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalProfitPriceIn())?"0": freshRankInfo.getTotalProfitPriceIn());
                    break;
                }
            }

            //当期可比毛利率 = 当期可比毛利额 / 当期可比销售额
            BigDecimal rateCompare = BigDecimal.ZERO;
            BigDecimal rateCompareIn = BigDecimal.ZERO;
            if(curCompareTotalSales.compareTo(BigDecimal.ZERO) != 0){
                rateCompare = curCompareTotalProfitPrice.divide(curCompareTotalSales,4,BigDecimal.ROUND_HALF_UP);
            }
            if(curCompareTotalSalesIn.compareTo(BigDecimal.ZERO) != 0){
                rateCompareIn = curCompareTotalProfitPriceIn.divide(curCompareTotalSalesIn,4,BigDecimal.ROUND_HALF_UP);
            }

            //同期可比销售
            BigDecimal sameCompareTotalSales = BigDecimal.ZERO;
            BigDecimal sameCompareTotalSalesIn = BigDecimal.ZERO;
            for(FreshRankInfo freshRankInfo : sameCompareInfo){
                if(target.getId().equals(freshRankInfo.getId())){
                    sameCompareTotalSales = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSales())?"0": freshRankInfo.getTotalSales());
                    sameCompareTotalSalesIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSalesIn())?"0": freshRankInfo.getTotalSalesIn());
                    break;
                }
            }

            //同期可比毛利额
            BigDecimal sameCompareTotalProfitPrice = BigDecimal.ZERO;
            BigDecimal sameCompareTotalProfitPriceIn = BigDecimal.ZERO;
            for(FreshRankInfo freshRankInfo : sameCompareInfo){
                if(target.getId().equals(freshRankInfo.getId())){
                    sameCompareTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalProfitPrice())?"0": freshRankInfo.getTotalProfitPrice());
                    sameCompareTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalProfitPriceIn())?"0": freshRankInfo.getTotalProfitPriceIn());
                    break;
                }
            }

            //同期可比毛利率 = 同期可比毛利额 / 同期可比销售额
            BigDecimal sameRateCompare = BigDecimal.ZERO;
            BigDecimal sameRateCompareIn = BigDecimal.ZERO;
            if(sameCompareTotalSales.compareTo(BigDecimal.ZERO) != 0){
                sameRateCompare = sameCompareTotalProfitPrice.divide(sameCompareTotalSales,4,BigDecimal.ROUND_HALF_UP);
            }
            if(sameCompareTotalSalesIn.compareTo(BigDecimal.ZERO) != 0){
                sameRateCompareIn = sameCompareTotalProfitPriceIn.divide(sameCompareTotalSalesIn,4,BigDecimal.ROUND_HALF_UP);
            }

            //同期销售
            BigDecimal sameTotalSales = BigDecimal.ZERO;
            BigDecimal sameTotalSalesIn = BigDecimal.ZERO;
            for(FreshRankInfo freshRankInfo : sameInfo){
                if(target.getId().equals(freshRankInfo.getId())){
                    sameTotalSales = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSales())?"0": freshRankInfo.getTotalSales());
                    sameTotalSalesIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSalesIn())?"0": freshRankInfo.getTotalSalesIn());
                    break;
                }
            }

            //同期毛利额
            BigDecimal sameTotalProfitPrice = BigDecimal.ZERO;
            BigDecimal sameTotalProfitPriceIn = BigDecimal.ZERO;
            for(FreshRankInfo freshRankInfo : sameInfo){
                if(target.getId().equals(freshRankInfo.getId())){
                    sameTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalProfitPrice())?"0": freshRankInfo.getTotalProfitPrice());
                    sameTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalProfitPriceIn())?"0": freshRankInfo.getTotalProfitPriceIn());
                    break;
                }
            }

            //上月损耗率
            BigDecimal lossPriceRate = BigDecimal.ZERO;
            //上月折价率
            BigDecimal discountRate = BigDecimal.ZERO;
            //上月损耗率
            BigDecimal lossPriceRateIn = BigDecimal.ZERO;
            //上月折价率
            BigDecimal discountRateIn = BigDecimal.ZERO;
            for(FreshRankInfo freshRankInfo : upMonth){
                if(target.getId().equals(freshRankInfo.getId())){
                    //上月损耗
                    BigDecimal lossPrice = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getLossPrice())?"0": freshRankInfo.getLossPrice());
                    //上月销售
                    BigDecimal sales = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSales())?"0": freshRankInfo.getTotalSales());
                    //上月损耗
                    BigDecimal lossPriceIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getLossPriceIn())?"0": freshRankInfo.getLossPriceIn());
                    //上月销售
                    BigDecimal salesIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSalesIn())?"0": freshRankInfo.getTotalSalesIn());
                    //上月损耗率 = 上月损耗 / 上月销售
                    if(sales.compareTo(BigDecimal.ZERO) != 0){
                        lossPriceRate = BigDecimal.ZERO.subtract(lossPrice.divide(sales,4,
                                BigDecimal.ROUND_HALF_UP));
                    }
                    if(salesIn.compareTo(BigDecimal.ZERO) != 0){
                        lossPriceRateIn = BigDecimal.ZERO.subtract(lossPriceIn.divide(salesIn,4,
                                BigDecimal.ROUND_HALF_UP));
                    }
                    //上月折价
                    BigDecimal disc = BigDecimal.ZERO;
                    BigDecimal discIn = BigDecimal.ZERO;
                    for(FreshRankInfo dis : discount){
                        if(target.getId().equals(dis.getId())){
                            disc = new BigDecimal(StringUtils.isEmpty(dis.getDiscountPrice())?"0":dis.getDiscountPrice());
                            discIn = new BigDecimal(StringUtils.isEmpty(dis.getDiscountPriceIn())?"0":dis.getDiscountPriceIn());
                            break;
                        }
                    }
                    //上月折价率 = 上月折价 / 上月销售
                    if(sales.compareTo(BigDecimal.ZERO) != 0){
                        discountRate = disc.divide(sales,4,BigDecimal.ROUND_HALF_UP);
                    }
                    if(salesIn.compareTo(BigDecimal.ZERO) != 0){
                        discountRateIn = discIn.divide(salesIn,4,BigDecimal.ROUND_HALF_UP);
                    }
                    break;
                }
            }

            //同期毛利率 = 同期毛利额 / 同期销售额
            BigDecimal sameRate = BigDecimal.ZERO;
            BigDecimal sameRateIn = BigDecimal.ZERO;
            if(sameTotalSales.compareTo(BigDecimal.ZERO) != 0){
                sameRate = sameTotalProfitPrice.divide(sameTotalSales,4,BigDecimal.ROUND_HALF_UP);
            }
            if(sameTotalSalesIn.compareTo(BigDecimal.ZERO) != 0){
                sameRateIn = sameTotalProfitPriceIn.divide(sameTotalSalesIn,4,BigDecimal.ROUND_HALF_UP);
            }

            //销售可比月增长 = （当期可比销售 - 同期可比销售）/ 同期可比销售
            BigDecimal salesCompareRate = BigDecimal.ZERO;
            BigDecimal salesCompareRateIn = BigDecimal.ZERO;
            if(sameCompareTotalSales.compareTo(BigDecimal.ZERO) !=0){
                salesCompareRate = curCompareTotalSales.subtract(sameCompareTotalSales).divide(sameCompareTotalSales,4,BigDecimal.ROUND_HALF_UP);
            }
            if(sameCompareTotalSalesIn.compareTo(BigDecimal.ZERO) !=0){
                salesCompareRateIn = curCompareTotalSalesIn.subtract(sameCompareTotalSalesIn).divide(sameCompareTotalSalesIn,4,BigDecimal.ROUND_HALF_UP);
            }
            //销售额同比月增长 = (当期销售 - 同期销售) / 同期销售
            BigDecimal salesSameRate = BigDecimal.ZERO;
            BigDecimal salesSameRateIn = BigDecimal.ZERO;
            if(sameTotalSales.compareTo(BigDecimal.ZERO) != 0){
                salesSameRate = totalSales.subtract(sameTotalSales).divide(sameTotalSales,4,BigDecimal.ROUND_HALF_UP);
            }
            if(sameTotalSalesIn.compareTo(BigDecimal.ZERO) != 0){
                salesSameRateIn = totalSalesIn.subtract(sameTotalSalesIn).divide(sameTotalSalesIn,4,BigDecimal.ROUND_HALF_UP);
            }
            //毛利率可比月增长 = 当期可比毛利率 - 同期可比毛利率
            BigDecimal profitCompareRate = BigDecimal.ZERO;
            BigDecimal profitCompareRateIn = BigDecimal.ZERO;
            if(sameRateCompare.compareTo(BigDecimal.ZERO) != 0){
                profitCompareRate = rateCompare.subtract(sameRateCompare);
            }
            if(sameRateCompareIn.compareTo(BigDecimal.ZERO) != 0){
                profitCompareRateIn = rateCompareIn.subtract(sameRateCompareIn);
            }
            //毛利率同比月增长 = 当期毛利率 - 同期毛利率
            BigDecimal profitSameRate = BigDecimal.ZERO;
            BigDecimal profitSameRateIn = BigDecimal.ZERO;
            if(sameRate.compareTo(BigDecimal.ZERO) != 0){
                profitSameRate = rate.subtract(sameRate);
            }
            if(sameRateIn.compareTo(BigDecimal.ZERO) != 0){
                profitSameRateIn = rateIn.subtract(sameRateIn);
            }

            FreshRankInfoResponse freshRankInfoResponse = new FreshRankInfoResponse();
            //设置id
            freshRankInfoResponse.setId(target.getId());
            //设置名称
            if(1 == type){
                freshRankInfoResponse.setType("provinceId");
            }else if(2 == type){
                freshRankInfoResponse.setType("areaId");
            }else if(3 == type){
                freshRankInfoResponse.setType("storeId");
            }else if(4 == type){
                freshRankInfoResponse.setType("deptId");
            }

            //设置总销售
            freshRankInfoResponse.setTotalSales(totalSales.toString());
            //销售可比月增长
            freshRankInfoResponse.setSalesCompareMonthGrowthRate(salesCompareRate.toString());
            //销售同比月增长
            freshRankInfoResponse.setSalesSameMonthGrowthRate(salesSameRate.toString());
            //毛利率
            freshRankInfoResponse.setSysRate(sysRate.toString());
            //毛利率可比月增长
            freshRankInfoResponse.setProfitCompareMonthGrowthRate(profitCompareRate.toString());
            //毛利率同比月增长
            freshRankInfoResponse.setProfitSameMonthGrowthRate(profitSameRate.toString());
            //上月损耗率
            freshRankInfoResponse.setLossRate(lossPriceRate.toString());
            //上月折价率
            freshRankInfoResponse.setDiscountRate(discountRate.toString());

            //设置总销售
            freshRankInfoResponse.setTotalSalesIn(totalSalesIn.toString());
            //销售可比月增长
            freshRankInfoResponse.setSalesCompareMonthGrowthRateIn(salesCompareRateIn.toString());
            //销售同比月增长
            freshRankInfoResponse.setSalesSameMonthGrowthRateIn(salesSameRateIn.toString());
            //毛利率
            freshRankInfoResponse.setSysRateIn(sysRateIn.toString());
            //毛利率可比月增长
            freshRankInfoResponse.setProfitCompareMonthGrowthRateIn(profitCompareRateIn.toString());
            //毛利率同比月增长
            freshRankInfoResponse.setProfitSameMonthGrowthRateIn(profitSameRateIn.toString());
            //上月损耗率
            freshRankInfoResponse.setLossRateIn(lossPriceRateIn.toString());
            //上月折价率
            freshRankInfoResponse.setDiscountRateIn(discountRateIn.toString());

            list.add(freshRankInfoResponse);
        }
        return list;
    }

    /**
     * 将source合并到target
     * @param source
     * @param target
     */
    private void mergeHisAndCurFreshRankInfo(List<FreshRankInfo> source, List<FreshRankInfo> target){
        for(FreshRankInfo sou : source){
            boolean flag = true;
            for(FreshRankInfo tar : target){
                if(sou.getId().equals(tar.getId())){
                    BigDecimal curTotalSales = new BigDecimal(StringUtils.isEmpty(sou.getTotalSales())?"0"
                            :sou.getTotalSales());
                    BigDecimal curTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(sou.getTotalProfitPrice())?"0"
                            :sou.getTotalProfitPrice());

                    BigDecimal curTotalSalesIn = new BigDecimal(StringUtils.isEmpty(sou.getTotalSalesIn())?"0"
                            :sou.getTotalSalesIn());
                    BigDecimal curTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(sou.getTotalProfitPriceIn())?"0"
                            :sou.getTotalProfitPriceIn());

                    BigDecimal totalSales = new BigDecimal(StringUtils.isEmpty(tar.getTotalSales())?"0"
                            :tar.getTotalSales());
                    BigDecimal totalProfitPrice = new BigDecimal(StringUtils.isEmpty(tar.getTotalProfitPrice())?"0"
                            :tar.getTotalProfitPrice());
                    BigDecimal totalScanningProfitPrice = new BigDecimal(StringUtils.isEmpty(tar.getTotalScanningProfitPrice())?"0"
                            :tar.getTotalScanningProfitPrice());

                    BigDecimal totalSalesIn = new BigDecimal(StringUtils.isEmpty(tar.getTotalSalesIn())?"0"
                            :tar.getTotalSalesIn());
                    BigDecimal totalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(tar.getTotalProfitPriceIn())?"0"
                            :tar.getTotalProfitPriceIn());
                    BigDecimal totalScanningProfitPriceIn = new BigDecimal(StringUtils.isEmpty(tar.getTotalScanningProfitPriceIn())?"0"
                            :tar.getTotalScanningProfitPriceIn());

                    tar.setTotalSales(curTotalSales.add(totalSales).toString());
                    tar.setTotalProfitPrice(curTotalProfitPrice.add(totalProfitPrice).toString());
                    tar.setTotalScanningProfitPrice(curTotalProfitPrice.add(totalScanningProfitPrice).toString());

                    tar.setTotalSalesIn(curTotalSalesIn.add(totalSalesIn).toString());
                    tar.setTotalProfitPriceIn(curTotalProfitPriceIn.add(totalProfitPriceIn).toString());
                    tar.setTotalScanningProfitPriceIn(curTotalProfitPriceIn.add(totalScanningProfitPriceIn).toString());
                    flag = false;
                    break;
                }
            }
            if(flag){
                BigDecimal curTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(sou.getTotalProfitPrice())?"0"
                        :sou.getTotalProfitPrice());
                BigDecimal curTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(sou.getTotalProfitPriceIn())?"0"
                        :sou.getTotalProfitPriceIn());
                sou.setTotalScanningProfitPrice(curTotalProfitPrice.toString());
                sou.setTotalScanningProfitPriceIn(curTotalProfitPriceIn.toString());
                target.add(sou);
            }
        }
    }

    /**
     * 获取可比和总数据
     * @param source(包含可比和非可比)
     * @param targetCompare（可比）
     * @param target
     */
    private void getCompareAndAllRankData(List<FreshRankInfo> source,
                                          List<FreshRankInfo> targetCompare,
                                          List<FreshRankInfo> target){
        for(FreshRankInfo freshRankInfo : source){
            //计算可比
            if("1".equals(freshRankInfo.getCompareMark())){
                boolean flag = true;
                for(FreshRankInfo com : targetCompare){
                    if(com.getId().equals(freshRankInfo.getId())){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    FreshRankInfo compare = new FreshRankInfo();
                    BeanUtils.copyProperties(freshRankInfo, compare);
                    targetCompare.add(compare);
                }
            }
            //统计每个的总和
            boolean flag = true;
            for(FreshRankInfo current : target){
                if(current.getId().equals(freshRankInfo.getId())){
                    BigDecimal curTotalSales = new BigDecimal(StringUtils.isEmpty(current.getTotalSales())?"0"
                            :current.getTotalSales());
                    BigDecimal curTotalProfitPrice = new BigDecimal(StringUtils.isEmpty(current
                            .getTotalProfitPrice())
                            ?"0":current.getTotalProfitPrice());
                    BigDecimal curTotalScanningProfitPrice = new BigDecimal(StringUtils.isEmpty(current
                            .getTotalScanningProfitPrice())
                            ?"0":current.getTotalScanningProfitPrice());

                    BigDecimal curTotalSalesIn = new BigDecimal(StringUtils.isEmpty(current.getTotalSalesIn())?"0"
                            :current.getTotalSalesIn());
                    BigDecimal curTotalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(current
                            .getTotalProfitPriceIn())
                            ?"0":current.getTotalProfitPriceIn());
                    BigDecimal curTotalScanningProfitPriceIn = new BigDecimal(StringUtils.isEmpty(current
                            .getTotalScanningProfitPriceIn())
                            ?"0":current.getTotalScanningProfitPriceIn());

                    BigDecimal totalSales = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSales())?"0"
                            : freshRankInfo.getTotalSales());
                    BigDecimal totalProfitPrice = new BigDecimal(StringUtils.isEmpty(freshRankInfo
                            .getTotalProfitPrice())?"0"
                            :freshRankInfo.getTotalProfitPrice());
                    BigDecimal totalScanningProfitPrice = new BigDecimal(StringUtils.isEmpty(freshRankInfo
                            .getTotalScanningProfitPrice())
                            ?"0":freshRankInfo.getTotalScanningProfitPrice());

                    BigDecimal totalSalesIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo.getTotalSalesIn())?"0"
                            : freshRankInfo.getTotalSalesIn());
                    BigDecimal totalProfitPriceIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo
                            .getTotalProfitPriceIn())?"0"
                            :freshRankInfo.getTotalProfitPriceIn());
                    BigDecimal totalScanningProfitPriceIn = new BigDecimal(StringUtils.isEmpty(freshRankInfo
                            .getTotalScanningProfitPriceIn())
                            ?"0":freshRankInfo.getTotalScanningProfitPriceIn());


                    current.setTotalSales(curTotalSales.add(totalSales).toString());
                    current.setTotalProfitPrice(curTotalProfitPrice.add(totalProfitPrice).toString());
                    current.setTotalScanningProfitPrice(curTotalScanningProfitPrice.add(totalScanningProfitPrice).toString());

                    current.setTotalSalesIn(curTotalSalesIn.add(totalSalesIn).toString());
                    current.setTotalProfitPriceIn(curTotalProfitPriceIn.add(totalProfitPriceIn).toString());
                    current.setTotalScanningProfitPriceIn(curTotalScanningProfitPriceIn.add(totalScanningProfitPriceIn).toString());
                    flag = false;
                    break;
                }
            }
            if(flag){
                target.add(freshRankInfo);
            }
        }
    }

    /**
     * 获取生鲜大类(如果没有则拼接所有大类)
     * @param request
     * @throws Exception
     */
    private boolean getFreshList(FreshReportBaseRequest request) throws Exception {
        boolean flag = true;
        List<String> paramList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        List<String> freshList = new ArrayList<>();
//        List<UserDept> allDeptList = reportUserDeptService.getAllDept();
        //根据用户获取权限大类
        List<UserDept> userDeptList = reportUserDeptService.getUserDeptList(request.getPersonId());
        List<String> list = request.getDeptIds();
        //暂时只对这几个大类进行统计
        if(null != userDeptList && userDeptList.size() > 0){
            for(UserDept userDept : userDeptList){
                if("0".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("32");
                    freshList.add("35");
                    freshList.add("36");
                    freshList.add("37");
                    break;
                }
                if("32".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("32");
                    continue;
                }
                if("35".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("35");
                    continue;
                }
                if("36".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("36");
                    continue;
                }
                if("37".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("37");
                    continue;
                }
            }
        }
        if(null != list && list.size() > 0){
            for(String value1 : list){
                for(String value2 : freshList){
                    if(value1.equals(value2)){
                        paramList.add(value1);
                        sb.append(value1).append(",");
                        break;
                    }
                }
            }
            request.setDeptIds(paramList);
            if(sb.toString().length() > 0){
                String str = sb.toString();
                request.setDeptId(str.substring(0, str.lastIndexOf(",")));
            }else{
                flag = false;
            }
        }else if(null != freshList && freshList.size() > 0){
            request.setDeptIds(freshList);
            for(String key : freshList){
                sb.append(key).append(",");
            }
            String str = sb.toString();
            request.setDeptId(str.substring(0, str.lastIndexOf(",")));
        }else{
            flag = false;
        }
        return flag;
    }

    /**
     * 获取生鲜大类
     * @param request
     * @throws Exception
     */
    private boolean getFreshListPage(PageRequest request) throws Exception {
        boolean flag = true;
        List<String> paramList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        List<String> freshList = new ArrayList<>();
//        List<UserDept> allDeptList = reportUserDeptService.getAllDept();
        //根据用户获取权限大类
        List<UserDept> userDeptList = reportUserDeptService.getUserDeptList(request.getPersonId());
        List<String> list = request.getDeptIds();
        //暂时只对这几个大类进行统计
        if(null != userDeptList && userDeptList.size() > 0){
            for(UserDept userDept : userDeptList){
                if("0".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("32");
                    freshList.add("35");
                    freshList.add("36");
                    freshList.add("37");
                    break;
                }
                if("32".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("32");
                    continue;
                }
                if("35".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("35");
                    continue;
                }
                if("36".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("36");
                    continue;
                }
                if("37".equals(String.valueOf(userDept.getDeptId()))){
                    freshList.add("37");
                    continue;
                }
            }
        }
        if(null != list && list.size() > 0){
            for(String value1 : list){
                for(String value2 : freshList){
                    if(value1.equals(value2)){
                        paramList.add(value1);
                        sb.append(value1).append(",");
                        break;
                    }
                }
            }
            request.setDeptIds(paramList);
            if(sb.toString().length() > 0){
                String str = sb.toString();
                request.setDeptId(str.substring(0, str.lastIndexOf(",")));
            }else{
                flag = false;
            }
        }else if(null != freshList && freshList.size() > 0){
            request.setDeptIds(freshList);
            for(String key : freshList){
                sb.append(key).append(",");
            }
            String str = sb.toString();
            request.setDeptId(str.substring(0, str.lastIndexOf(",")));
        }else{
            flag = false;
        }
        return flag;
    }

    private void transform(FreshReportBaseRequest request){
        String provinceId = request.getProvinceId();
        String areaId = request.getAreaId();
        String storeId = request.getStoreId();
        String deptId = request.getDeptId();
        String category = request.getCategory();
        String startDateStr = request.getStartDate();
        String endDateStr = request.getEndDate();

        /*if(StringUtils.isNotEmpty(startDateStr)) {
            if(DateUtils.getBetweenDay(DateUtils.dateTime("yyyy-MM-dd", startDateStr), new Date()) >= 0){
                request.setStart(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startDateStr + " 00:00:00"));
                request.setEnd(new Date());
            }else{
                if(StringUtils.isNotEmpty(endDateStr)){
                    if(DateUtils.getBetweenDay(DateUtils.dateTime("yyyy-MM-dd", endDateStr), new Date()) >= 0){
                        request.setStart(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startDateStr  + " 00:00:00"));
                        request.setEnd(new Date());
                    }else{
                        request.setStart(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startDateStr  + " 00:00:00"));
                        request.setEnd(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endDateStr  + " 23:59:59"));
                    }
                }else{

                }
            }
        }*/

        if(StringUtils.isNotEmpty(startDateStr)){
            request.setStart(DateUtils.dateTime("yyyy-MM-dd", startDateStr));
        }else{
            ExceptionUtils.wapperBussinessException("请选择查询时间");
        }

        if(StringUtils.isNotEmpty(endDateStr)){
            request.setEnd(DateUtils.dateTime("yyyy-MM-dd", endDateStr));
        }

        if(null != request.getEnd()){
            if(DateUtils.getBetweenDay(request.getEnd(), request.getStart()) == 0){
                request.setEnd(null);
            }
        }

        if(StringUtils.isNotEmpty(provinceId)){
            String[] provinceIdArr = Convert.toStrArray(provinceId);
            if(provinceId.endsWith(",")){
                request.setProvinceId(provinceId.substring(0,provinceId.lastIndexOf(",")));
            }
            request.setProvinceIds(Arrays.asList(provinceIdArr));
        }

        if(StringUtils.isNotEmpty(areaId)){
            String[] areaIdArr = Convert.toStrArray(areaId);
            if(areaId.endsWith(",")){
                request.setAreaId(areaId.substring(0,areaId.lastIndexOf(",")));
            }
            request.setAreaIds(Arrays.asList(areaIdArr));
        }

        if(StringUtils.isNotEmpty(storeId)){
            String[] storeIdArr = Convert.toStrArray(storeId);
            if(storeId.endsWith(",")){
                request.setStoreId(storeId.substring(0,storeId.lastIndexOf(",")));
            }
            request.setStoreIds(Arrays.asList(storeIdArr));
        }

        if(StringUtils.isNotEmpty(deptId)){
            String[] deptIdArr = Convert.toStrArray(deptId);
            if(deptId.endsWith(",")){
                request.setDeptId(deptId.substring(0,deptId.lastIndexOf(",")));
            }
            request.setDeptIds(Arrays.asList(deptIdArr));
        }

        if(StringUtils.isNotEmpty(category)){
            String[] categoryArr = Convert.toStrArray(category);
            if(category.endsWith(",")){
                request.setCategory(category.substring(0,category.lastIndexOf(",")));
            }
            request.setCategorys(Arrays.asList(categoryArr));
        }
    }

    private void transformPage(PageRequest request){
        String provinceId = request.getProvinceId();
        String areaId = request.getAreaId();
        String storeId = request.getStoreId();
        String deptId = request.getDeptId();
        String category = request.getCategory();
        String startDateStr = request.getStartDate();
        String endDateStr = request.getEndDate();

        /*if(StringUtils.isNotEmpty(startDateStr)) {
            if(DateUtils.getBetweenDay(DateUtils.dateTime("yyyy-MM-dd", startDateStr), new Date()) >= 0){
                request.setStart(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startDateStr + " 00:00:00"));
                request.setEnd(new Date());
            }else{
                if(StringUtils.isNotEmpty(endDateStr)){
                    if(DateUtils.getBetweenDay(DateUtils.dateTime("yyyy-MM-dd", endDateStr), new Date()) >= 0){
                        request.setStart(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startDateStr  + " 00:00:00"));
                        request.setEnd(new Date());
                    }else{
                        request.setStart(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startDateStr  + " 00:00:00"));
                        request.setEnd(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endDateStr  + " 23:59:59"));
                    }
                }
            }
        }*/

        if(StringUtils.isNotEmpty(startDateStr)){
            request.setStart(DateUtils.dateTime("yyyy-MM-dd", startDateStr));
        }

        if(StringUtils.isNotEmpty(endDateStr)){
            request.setEnd(DateUtils.dateTime("yyyy-MM-dd", endDateStr));
        }

        if(null != request.getEnd()){
            if(DateUtils.getBetweenDay(request.getEnd(), request.getStart()) == 0){
                request.setEnd(null);
            }
        }

        if(StringUtils.isNotEmpty(provinceId)){
            String[] provinceIdArr = Convert.toStrArray(provinceId);
            if(provinceId.endsWith(",")){
                request.setProvinceId(provinceId.substring(0,provinceId.lastIndexOf(",")));
            }
            request.setProvinceIds(Arrays.asList(provinceIdArr));
        }

        if(StringUtils.isNotEmpty(areaId)){
            String[] areaIdArr = Convert.toStrArray(areaId);
            if(areaId.endsWith(",")){
                request.setAreaId(areaId.substring(0,areaId.lastIndexOf(",")));
            }
            request.setAreaIds(Arrays.asList(areaIdArr));
        }

        if(StringUtils.isNotEmpty(storeId)){
            String[] storeIdArr = Convert.toStrArray(storeId);
            if(storeId.endsWith(",")){
                request.setStoreId(storeId.substring(0,storeId.lastIndexOf(",")));
            }
            request.setStoreIds(Arrays.asList(storeIdArr));
        }

        if(StringUtils.isNotEmpty(deptId)){
            String[] deptIdArr = Convert.toStrArray(deptId);
            if(deptId.endsWith(",")){
                request.setDeptId(deptId.substring(0,deptId.lastIndexOf(",")));
            }
            request.setDeptIds(Arrays.asList(deptIdArr));
        }

        if(StringUtils.isNotEmpty(category)){
            String[] categoryArr = Convert.toStrArray(category);
            if(category.endsWith(",")){
                request.setCategory(category.substring(0,category.lastIndexOf(",")));
            }
            request.setCategorys(Arrays.asList(categoryArr));
        }
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
    /**
     * 设置会员线下标识
     * @return
     */
    private List<String> setOfflineMark(){
        String[] arr = {"YUNPOS","VIP","WX","WX1","WX2","WX3","DD1","JD1","KJG","BKSLS"};
        return Arrays.asList(arr);
    }

    /**
     * 排行榜合并客流
     * @param curSource
     * @param hisSource
     * @param target
     */
    private void sumList(List<FreshRankKlModel> curSource,List<FreshRankKlModel> hisSource,List<FreshRankKlModel> target){
        if(null != hisSource && hisSource.size() > 0){
            target.addAll(hisSource);
        }
        if(null != curSource && curSource.size() > 0){
            for(FreshRankKlModel cur : curSource){
                boolean flag = true;
                for(FreshRankKlModel fresh : target){
                    if(cur.getId().equals(fresh.getId())){
                        BigDecimal curKl = new BigDecimal(StringUtils.isEmpty(cur.getKl())?"0":cur.getKl());
                        BigDecimal targetKl = new BigDecimal(StringUtils.isEmpty(fresh.getKl())?"0":fresh.getKl());
                        fresh.setKl(curKl.add(targetKl).toString());
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    target.add(cur);
                }
            }
        }
    }

    /**
     * 设置渗透率（大类条件要么是单个条件，要么是全部32，35，36，37）
     * @param param
     * @param start
     * @param freshRankResponse
     * @throws Exception
     */
    private void setKlPermeability(FreshRankRequest param,Date start,FreshRankResponse freshRankResponse)throws Exception{
        List<FreshRankKlModel> current = new ArrayList<>();
        List<FreshRankKlModel> currentAll = new ArrayList<>();
        List<FreshRankKlModel> his = new ArrayList<>();
        List<FreshRankKlModel> hisAll = new ArrayList<>();
        int mark = param.getMark();
        if(null != start && DateUtils.getBetweenDay(start,new Date()) >= 0){
            //查询实时客流
            current = freshReportCsmbDao.queryCurFreshRankKlCount(param,start,null,1,mark);
            currentAll = freshReportCsmbDao.queryCurFreshRankKlCount(param,start,null,2,mark);
            //查询月初到昨日客流
            if(null != param.getDeptIds() && param.getDeptIds().size() == 1 || 4 == mark){//查询单一生鲜分组客流
                try{
                    changeDgDataSource();
                    his = freshReportRmsDao.queryHisFreshRankKlForOne(param, DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1), mark);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else{//省，区域，门店客流（32，35，36，37的汇总客流）(性能优化)
                try{
                    changeZyDataSource();
                    his = freshReportZyDao.queryHisFreshRankKlForFour(param,DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),mark);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
            try{
                changeZyDataSource();
                //查询月初到昨日总客流
                hisAll = freshReportZyDao.queryHisFreshRankKlForAll(param,DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1),mark);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else{
            //查询月初到历史当日客流
            if(null != param.getDeptIds() && param.getDeptIds().size() == 1 || 4 == mark){//查询单一生鲜分组客流
                try{
                    changeDgDataSource();
                    his = freshReportRmsDao.queryHisFreshRankKlForOne(param,DateUtil.getFirstDayForMonth(start),
                            start, mark);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else{//省，区域，门店客流（32，35，36，37的汇总客流）(性能优化)
                try{
                    changeZyDataSource();
                    his = freshReportZyDao.queryHisFreshRankKlForFour(param,DateUtil.getFirstDayForMonth(start),
                            start, mark);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
            try{
                changeZyDataSource();
                //查询月初到历史当日总客流
                hisAll = freshReportZyDao.queryHisFreshRankKlForAll(param,DateUtil.getFirstDayForMonth(start),
                        start, mark);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }
        //历史和实时合并到kl中
        List<FreshRankKlModel> kl = new ArrayList<>();
        sumList(current, his, kl);
        //历史和实时总客流合并到klAll中
        List<FreshRankKlModel> klAll = new ArrayList<>();
        sumList(currentAll, hisAll, klAll);

        if(null != freshRankResponse.getList() && freshRankResponse.getList().size() > 0){
            List<FreshRankInfoResponse> list = freshRankResponse.getList();
            if(4 == mark){//大类查询时,计算渗透率
                //先计算对应门店的所有客流总和
                BigDecimal allKl = BigDecimal.ZERO;
                for(FreshRankKlModel freshRankKlModel : klAll){
                    BigDecimal count = new BigDecimal(StringUtils.isEmpty(freshRankKlModel.getKl())?"0":freshRankKlModel.getKl());
                    allKl = allKl.add(count);
                }
                //计算对应大类的渗透率
                for(FreshRankKlModel freshRankKlModel : kl){
                    BigDecimal deptKl = new BigDecimal(StringUtils.isEmpty(freshRankKlModel.getKl())?"0":freshRankKlModel.getKl());
                    if(allKl.compareTo(BigDecimal.ZERO) != 0){
                        for(FreshRankInfoResponse freshRankInfoResponse : list){
                            if(null != freshRankInfoResponse.getId() && freshRankInfoResponse.getId().equals(freshRankKlModel.getId())){
                                freshRankInfoResponse.setKlRate(deptKl.divide(allKl,4,BigDecimal.ROUND_HALF_UP).toString());
                                break;
                            }
                        }
                    }
                }
            }else{//省，区域，门店查询时,计算渗透率
                for(FreshRankKlModel freshRankKlModel : kl){
                    BigDecimal deptKl = new BigDecimal(StringUtils.isEmpty(freshRankKlModel.getKl())?"0":freshRankKlModel.getKl());
                    BigDecimal allKl = BigDecimal.ZERO;
                    //获取省，区域，门店总客流
                    for(FreshRankKlModel all : klAll){
                        if(all.getId().equals(freshRankKlModel.getId())){
                            allKl = new BigDecimal(StringUtils.isEmpty(all.getKl())?"0":all.getKl());
                            break;
                        }
                    }
                    //计算省，区域，门店渗透率
                    if(allKl.compareTo(BigDecimal.ZERO) != 0){
                        for(FreshRankInfoResponse freshRankInfoResponse : list){
                            if(null != freshRankInfoResponse.getId() && freshRankInfoResponse.getId().equals(freshRankKlModel.getId())){
                                freshRankInfoResponse.setKlRate(deptKl.divide(allKl,4,BigDecimal.ROUND_HALF_UP).toString());
                                break;
                            }
                        }
                    }
                }
            }
        }

    }
}
