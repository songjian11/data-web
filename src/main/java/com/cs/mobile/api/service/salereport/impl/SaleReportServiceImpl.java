package com.cs.mobile.api.service.salereport.impl;

import com.cs.mobile.api.dao.salereport.SaleReportCsmbDao;
import com.cs.mobile.api.dao.salereport.SaleReportRmsDao;
import com.cs.mobile.api.dao.salereport.SaleReportZyDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.reportPage.HomeAppliance;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.model.reportPage.response.HomeApplianceResponse;
import com.cs.mobile.api.model.salereport.*;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.api.model.salereport.response.*;
import com.cs.mobile.api.service.common.CommonService;
import com.cs.mobile.api.service.reportPage.ReportUserDeptService;
import com.cs.mobile.api.service.salereport.SaleReportService;
import com.cs.mobile.common.core.text.Convert;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SaleReportServiceImpl implements SaleReportService{
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
    private SaleReportCsmbDao saleReportCsmbDao;
    @Autowired
    private SaleReportZyDao saleReportZyDao;
    @Autowired
    private SaleReportRmsDao saleReportRmsDao;
    @Autowired
    private ReportUserDeptService reportUserDeptService;
    @Autowired
    private CommonService commonService;

    /**
     * 查询销售模块
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public AllSaleSumResponse queryAllSaleSum(BaseSaleRequest param) throws Exception {
        AllSaleSumResponse allSaleSumResponse = new AllSaleSumResponse();
        //当期数据
        BaseSaleModel baseSaleModel = null;
        //环比数据
        BaseSaleModel monthBaseSaleModel = null;
        //同期和可比数据
        BaseSaleModel compareAndSameSaleModel = null;
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        String hour = DateUtils.parseDateToStr("HH",new Date());
        //构造当期，环比，同期和可比数据
        String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
        if(null == end){
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){//当天
                //查询当天实时数据
                baseSaleModel = saleReportCsmbDao.queryCurCommonBaseData(param,start,null);
                try{
                    changeZyDataSource();
                    //查询环比数据(一个月之前的数据)
                    monthBaseSaleModel = saleReportZyDao.queryHisCommonBaseDataForHour(param,
                            DateUtils.addMonths(start,-1),null,hour);
                    //查询同比和可比
                    compareAndSameSaleModel = saleReportZyDao.queryHisCommonBaseDataForHour(param,
                            DateUtil.toDate(time,"yyyy-MM-dd"),null,hour);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else{//历史
                //查询环比数据(一个月之前的数据)
                monthBaseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtils.addMonths(start,-1),null);
                try{
                    changeDgDataSource();
                    //查询历史某天数据
                    baseSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param,start,null);
                    //查询同比和可比
                    compareAndSameSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param,
                            DateUtil.toDate(time,"yyyy-MM-dd"),null);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else{
            //包含当天的时间段查询
            if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                baseSaleModel = new BaseSaleModel();
                monthBaseSaleModel = new BaseSaleModel();
                compareAndSameSaleModel = new BaseSaleModel();
                //查询当天的数据
                BaseSaleModel cur = saleReportCsmbDao.queryCurCommonBaseData(param,end,null);
                //查询起始时间到昨日的数据
                BaseSaleModel his = null;
                try{
                    //查询同比和可比
                    changeDgDataSource();
                    //查询起始时间到昨日的数据
                    his = saleReportRmsDao.queryHisCommonCompareBaseData(param,start,DateUtils.addDays(end,-1));
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                BigDecimal curSale = BigDecimal.ZERO;
                BigDecimal curCompareSales = BigDecimal.ZERO;
                BigDecimal curSaleIn = BigDecimal.ZERO;
                BigDecimal curCompareSalesIn = BigDecimal.ZERO;
                if(null != cur){
                    curSale = new BigDecimal(StringUtils.isEmpty(cur.getTotalSale())?"0"
                            :cur.getTotalSale());
                    curSaleIn = new BigDecimal(StringUtils.isEmpty(cur.getTotalSaleIn())?"0"
                            :cur.getTotalSaleIn());
                    curCompareSales = new BigDecimal(StringUtils.isEmpty(cur.getTotalCompareSale())?"0"
                            :cur.getTotalCompareSale());
                    curCompareSalesIn = new BigDecimal(StringUtils.isEmpty(cur.getTotalCompareSaleIn())?"0"
                            :cur.getTotalCompareSaleIn());
                }
                BigDecimal hisSaleIn = BigDecimal.ZERO;
                BigDecimal hisCompareSalesIn = BigDecimal.ZERO;
                BigDecimal hisSale = BigDecimal.ZERO;
                BigDecimal hisCompareSales = BigDecimal.ZERO;
                if(null != his){
                    hisSale = new BigDecimal(StringUtils.isEmpty(his.getTotalSale())?"0"
                            :his.getTotalSale());
                    hisSaleIn = new BigDecimal(StringUtils.isEmpty(his.getTotalSaleIn())?"0"
                            :his.getTotalSaleIn());
                    hisCompareSales = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareSale())?"0"
                            :his.getTotalCompareSale());
                    hisCompareSalesIn = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareSaleIn())?"0"
                            :his.getTotalCompareSaleIn());
                }
                //当天销售 + 历史销售
                baseSaleModel.setTotalSale(curSale.add(hisSale).toString());
                baseSaleModel.setTotalSaleIn(curSaleIn.add(hisSaleIn).toString());
                baseSaleModel.setTotalCompareSale(curCompareSales.add(hisCompareSales).toString());
                baseSaleModel.setTotalCompareSaleIn(curCompareSalesIn.add(hisCompareSalesIn).toString());

                //查询环比数据(一个月之前的数据)
                BaseSaleModel curMonthBaseSaleModel = null;
                try{
                    //查询当天环比
                    changeZyDataSource();
                    curMonthBaseSaleModel = saleReportZyDao.queryHisCommonBaseDataForHour(param,
                            DateUtils.addMonths(end,-1),null,hour);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //查询起始时间到昨日的环比
                BaseSaleModel hisMonthBaseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param
                        ,DateUtils.addMonths(start,-1)
                        ,DateUtils.addMonths(DateUtils.addDays(end,-1),-1));

                BigDecimal curMonthSale = BigDecimal.ZERO;
                BigDecimal curMonthSaleIn = BigDecimal.ZERO;
                if(null != curMonthBaseSaleModel){
                    curMonthSale = new BigDecimal(StringUtils.isEmpty(curMonthBaseSaleModel.getTotalSale())
                            ?"0":curMonthBaseSaleModel.getTotalSale());
                    curMonthSaleIn = new BigDecimal(StringUtils.isEmpty(curMonthBaseSaleModel.getTotalSaleIn())
                            ?"0":curMonthBaseSaleModel.getTotalSaleIn());
                }
                BigDecimal hisMonthSale = BigDecimal.ZERO;
                BigDecimal hisMonthSaleIn = BigDecimal.ZERO;
                if(null != hisMonthBaseSaleModel){
                    hisMonthSale = new BigDecimal(StringUtils.isEmpty(hisMonthBaseSaleModel.getTotalSale())
                            ?"0":hisMonthBaseSaleModel.getTotalSale());
                    hisMonthSaleIn = new BigDecimal(StringUtils.isEmpty(hisMonthBaseSaleModel.getTotalSaleIn())
                            ?"0":hisMonthBaseSaleModel.getTotalSaleIn());
                }
                monthBaseSaleModel.setTotalSale(curMonthSale.add(hisMonthSale).toString());
                monthBaseSaleModel.setTotalSaleIn(curMonthSaleIn.add(hisMonthSaleIn).toString());

                //查询同比和可比
                BaseSaleModel curCompareAndSameSaleModel = null;
                try{
                    //查询当天同比和可比
                    changeZyDataSource();
                    curCompareAndSameSaleModel = saleReportZyDao.queryHisCommonBaseDataForHour(param,
                            DateUtils.addYears(end,-1),null,hour);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //查询起始时间到昨日的同比和可比
                BaseSaleModel hisCompareAndSameSaleModel = null;
                try{
                    changeDgDataSource();
                    hisCompareAndSameSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param
                            ,DateUtils.addYears(start,-1)
                            ,DateUtils.addYears(DateUtils.addDays(end,-1),-1));
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                BigDecimal curCompareSale = BigDecimal.ZERO;
                BigDecimal curSameSale = BigDecimal.ZERO;
                BigDecimal curCompareSaleIn = BigDecimal.ZERO;
                BigDecimal curSameSaleIn = BigDecimal.ZERO;
                if(null != curCompareAndSameSaleModel){
                    curCompareSale = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalCompareSale())
                    ?"0":curCompareAndSameSaleModel.getTotalCompareSale());
                    curSameSale = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalSale())
                            ?"0":curCompareAndSameSaleModel.getTotalSale());
                    curCompareSaleIn = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalCompareSaleIn())
                            ?"0":curCompareAndSameSaleModel.getTotalCompareSaleIn());
                    curSameSaleIn = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalSaleIn())
                            ?"0":curCompareAndSameSaleModel.getTotalSaleIn());
                }

                BigDecimal hisCompareSale = BigDecimal.ZERO;
                BigDecimal hisSameSale = BigDecimal.ZERO;
                BigDecimal hisCompareSaleIn = BigDecimal.ZERO;
                BigDecimal hisSameSaleIn = BigDecimal.ZERO;
                if(null != hisCompareAndSameSaleModel){
                    hisCompareSale = new BigDecimal(StringUtils.isEmpty(hisCompareAndSameSaleModel.getTotalCompareSale())
                            ?"0":hisCompareAndSameSaleModel.getTotalCompareSale());
                    hisSameSale = new BigDecimal(StringUtils.isEmpty(hisCompareAndSameSaleModel.getTotalSale())
                            ?"0":hisCompareAndSameSaleModel.getTotalSale());
                    hisCompareSaleIn = new BigDecimal(StringUtils.isEmpty(hisCompareAndSameSaleModel.getTotalCompareSaleIn())
                            ?"0":hisCompareAndSameSaleModel.getTotalCompareSaleIn());
                    hisSameSaleIn = new BigDecimal(StringUtils.isEmpty(hisCompareAndSameSaleModel.getTotalSaleIn())
                            ?"0":hisCompareAndSameSaleModel.getTotalSaleIn());
                }
                compareAndSameSaleModel.setTotalSale(curSameSale.add(hisSameSale).toString());
                compareAndSameSaleModel.setTotalCompareSale(curCompareSale.add(hisCompareSale).toString());
                compareAndSameSaleModel.setTotalSaleIn(curSameSaleIn.add(hisSameSaleIn).toString());
                compareAndSameSaleModel.setTotalCompareSaleIn(curCompareSaleIn.add(hisCompareSaleIn).toString());
            }else{
                try{
                    changeDgDataSource();
                    //查询时间段的数据
                    baseSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param,start,end);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //查询环比数据(一个月之前的数据)
                monthBaseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtils.addMonths(start,-1),DateUtils.addMonths(end,-1));
                try{
                    //查询同比和可比
                    changeDgDataSource();
                    compareAndSameSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param
                            ,DateUtils.addYears(start,-1)
                            ,DateUtils.addYears(end,-1));
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }

        //当期销售额
        allSaleSumResponse.setTotalSale(baseSaleModel.getTotalSale());
        allSaleSumResponse.setTotalSaleIn(baseSaleModel.getTotalSaleIn());
        BigDecimal monthSale = BigDecimal.ZERO;
        BigDecimal monthSaleIn = BigDecimal.ZERO;
        if(null != monthBaseSaleModel){
            monthSale = new BigDecimal(StringUtils.isEmpty(monthBaseSaleModel.getTotalSale())
                    ?"0":monthBaseSaleModel.getTotalSale());
            monthSaleIn = new BigDecimal(StringUtils.isEmpty(monthBaseSaleModel.getTotalSaleIn())
                    ?"0":monthBaseSaleModel.getTotalSaleIn());
        }
        //环比 = 当期销售额 / 月销售额 - 1
        if(monthSale.compareTo(BigDecimal.ZERO) != 0){
            allSaleSumResponse.setSaleMonthRate(new BigDecimal(baseSaleModel.getTotalSale())
                    .divide(monthSale,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        if(monthSaleIn.compareTo(BigDecimal.ZERO) != 0){
            allSaleSumResponse.setSaleMonthRateIn(new BigDecimal(baseSaleModel.getTotalSaleIn())
                    .divide(monthSaleIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        BigDecimal compareSale = BigDecimal.ZERO;
        BigDecimal compareSaleIn = BigDecimal.ZERO;
        if(null != compareAndSameSaleModel){
            compareSale = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCompareSale())
                    ?"0":compareAndSameSaleModel.getTotalCompareSale());
            compareSaleIn = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCompareSaleIn())
                    ?"0":compareAndSameSaleModel.getTotalCompareSaleIn());
        }
        BigDecimal sameSale = BigDecimal.ZERO;
        BigDecimal sameSaleIn = BigDecimal.ZERO;
        if(null != compareAndSameSaleModel){
            sameSale = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalSale())
                    ?"0":compareAndSameSaleModel.getTotalSale());
            sameSaleIn = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalSaleIn())
                    ?"0":compareAndSameSaleModel.getTotalSaleIn());
        }
        //可比 = 当期可比 / 可比 -1
        if(compareSale.compareTo(BigDecimal.ZERO) != 0){
            allSaleSumResponse.setSaleCompareRate(new BigDecimal(baseSaleModel.getTotalCompareSale())
                    .divide(compareSale,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        if(compareSaleIn.compareTo(BigDecimal.ZERO) != 0){
            allSaleSumResponse.setSaleCompareRateIn(new BigDecimal(baseSaleModel.getTotalCompareSaleIn())
                    .divide(compareSaleIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        //同比 = 当期 / 同期 -1
        if(sameSale.compareTo(BigDecimal.ZERO) != 0){
            allSaleSumResponse.setSaleSameRate(new BigDecimal(baseSaleModel.getTotalSale())
                    .divide(sameSale,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        if(sameSaleIn.compareTo(BigDecimal.ZERO) != 0){
            allSaleSumResponse.setSaleSameRateIn(new BigDecimal(baseSaleModel.getTotalSaleIn())
                    .divide(sameSaleIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        return allSaleSumResponse;
    }

    /**
     * 查询毛利额模块
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public AllRateSumResponse queryAllRateSum(BaseSaleRequest param) throws Exception {
        AllRateSumResponse allRateSumResponse = new AllRateSumResponse();
        //当期数据
        BaseSaleModel baseSaleModel = null;
        //环比数据
        BaseSaleModel monthBaseSaleModel = null;
        //同期和可比数据
        BaseSaleModel compareAndSameSaleModel = null;
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        String hour = DateUtils.parseDateToStr("HH",new Date());
        //构造当期，环比，同期和可比最终毛利额
        String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
        if(null == end){
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                //查询当天实时数据
                baseSaleModel = saleReportCsmbDao.queryCurCommonBaseData(param,start,null);
                //当天最终毛利额 = 扫描毛利额
                if(null != baseSaleModel){
                    baseSaleModel.setTotalRate(baseSaleModel.getTotalScanningRate());
                    baseSaleModel.setTotalCompareRate(baseSaleModel.getTotalCompareScanningRate());
                    baseSaleModel.setTotalRateIn(baseSaleModel.getTotalScanningRateIn());
                    baseSaleModel.setTotalCompareRateIn(baseSaleModel.getTotalCompareScanningRateIn());
                }
                try{
                    //查询环比数据(一个月之前的数据)
                    changeZyDataSource();
                    monthBaseSaleModel = saleReportZyDao.queryHisCommonBaseDataForHour(param,
                            DateUtils.addMonths(start,-1),null,hour);
                    //最终环比毛利额 = 环比销售 - 环比成本
                    if(null != monthBaseSaleModel){
                        BigDecimal monthSale = new BigDecimal(StringUtils.isEmpty(monthBaseSaleModel.getTotalSale())
                                ?"0":monthBaseSaleModel.getTotalSale());
                        BigDecimal monthCost = new BigDecimal(StringUtils.isEmpty(monthBaseSaleModel.getTotalCost())
                                ?"0":monthBaseSaleModel.getTotalCost());
                        BigDecimal monthSaleIn = new BigDecimal(StringUtils.isEmpty(monthBaseSaleModel.getTotalSaleIn())
                                ?"0":monthBaseSaleModel.getTotalSaleIn());
                        BigDecimal monthCostIn = new BigDecimal(StringUtils.isEmpty(monthBaseSaleModel.getTotalCostIn())
                                ?"0":monthBaseSaleModel.getTotalCostIn());
                        monthBaseSaleModel.setTotalRate(monthSale.subtract(monthCost).toString());
                        monthBaseSaleModel.setTotalRateIn(monthSaleIn.subtract(monthCostIn).toString());
                    }
                    //查询同比和可比
                    compareAndSameSaleModel = saleReportZyDao.queryHisCommonBaseDataForHour(param,
                            DateUtil.toDate(time,"yyyy-MM-dd"),null,hour);
                    //最终同比/可比毛利额 = 同比/可比销售 - 同比/可比成本
                    if(null != compareAndSameSaleModel){
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalSale())
                                ?"0":compareAndSameSaleModel.getTotalSale());
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCost())
                                ?"0":compareAndSameSaleModel.getTotalCost());
                        BigDecimal saleCompare = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCompareSale())
                                ?"0":compareAndSameSaleModel.getTotalCompareSale());
                        BigDecimal costCompare = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCompareCost())
                                ?"0":compareAndSameSaleModel.getTotalCompareCost());
                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalSaleIn())
                                ?"0":compareAndSameSaleModel.getTotalSaleIn());
                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCostIn())
                                ?"0":compareAndSameSaleModel.getTotalCostIn());
                        BigDecimal saleCompareIn = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCompareSaleIn())
                                ?"0":compareAndSameSaleModel.getTotalCompareSaleIn());
                        BigDecimal costCompareIn = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCompareCostIn())
                                ?"0":compareAndSameSaleModel.getTotalCompareCostIn());

                        compareAndSameSaleModel.setTotalRate(sale.subtract(cost).toString());
                        compareAndSameSaleModel.setTotalCompareRate(saleCompare.subtract(costCompare).toString());
                        compareAndSameSaleModel.setTotalRateIn(saleIn.subtract(costIn).toString());
                        compareAndSameSaleModel.setTotalCompareRateIn(saleCompareIn.subtract(costCompareIn).toString());
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else{
                try{
                    changeDgDataSource();
                    //查询历史某天数据
                    baseSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param,start,null);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //历史最终毛利额 = 前台毛利额
                if(null != baseSaleModel){
                    baseSaleModel.setTotalRate(baseSaleModel.getTotalFrontDeskRate());
                    baseSaleModel.setTotalCompareRate(baseSaleModel.getTotalCompareFrontDeskRate());
                    baseSaleModel.setTotalRateIn(baseSaleModel.getTotalFrontDeskRateIn());
                    baseSaleModel.setTotalCompareRateIn(baseSaleModel.getTotalCompareFrontDeskRateIn());
                }
                //查询环比数据(一个月之前的数据)
                monthBaseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtils.addMonths(start,-1),null);
                //最终环比毛利额 = 前台环比毛利额
                if(null != monthBaseSaleModel){
                    monthBaseSaleModel.setTotalRate(monthBaseSaleModel.getTotalFrontDeskRate());
                    monthBaseSaleModel.setTotalRateIn(monthBaseSaleModel.getTotalFrontDeskRateIn());
                }
                try{
                    //查询同比和可比
                    changeDgDataSource();
                    compareAndSameSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param,
                            DateUtil.toDate(time,"yyyy-MM-dd"),null);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //最终同比/可比毛利额 = 同比/可比前台毛利额
                if(null != compareAndSameSaleModel){
                    compareAndSameSaleModel.setTotalRate(compareAndSameSaleModel.getTotalFrontDeskRate());
                    compareAndSameSaleModel.setTotalCompareRate(compareAndSameSaleModel.getTotalCompareFrontDeskRate());
                    compareAndSameSaleModel.setTotalRateIn(compareAndSameSaleModel.getTotalFrontDeskRateIn());
                    compareAndSameSaleModel.setTotalCompareRateIn(compareAndSameSaleModel.getTotalCompareFrontDeskRateIn());
                }
            }
        }else{
            //包含当天的时间段查询
            if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                baseSaleModel = new BaseSaleModel();
                monthBaseSaleModel = new BaseSaleModel();
                compareAndSameSaleModel = new BaseSaleModel();
                //查询当天的数据
                BaseSaleModel cur = saleReportCsmbDao.queryCurCommonBaseData(param,end,null);
                //查询起始时间到昨日的数据
                BaseSaleModel his = null;
                try{
                    changeDgDataSource();
                    //查询历史某天数据
                    his = saleReportRmsDao.queryHisCommonCompareBaseData(param,start,DateUtils.addDays(end,-1));
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                //当天最终毛利额 = 扫描毛利额
                BigDecimal curRate = BigDecimal.ZERO;
                BigDecimal curCompareRate_1 = BigDecimal.ZERO;
                BigDecimal curRateIn = BigDecimal.ZERO;
                BigDecimal curCompareRate_1In = BigDecimal.ZERO;
                if(null != cur){
                    curRate = new BigDecimal(StringUtils.isEmpty(cur.getTotalScanningRate())?"0"
                            :cur.getTotalScanningRate());
                    curCompareRate_1 = new BigDecimal(StringUtils.isEmpty(cur.getTotalCompareScanningRate())?"0"
                            :cur.getTotalCompareScanningRate());
                    curRateIn = new BigDecimal(StringUtils.isEmpty(cur.getTotalScanningRateIn())?"0"
                            :cur.getTotalScanningRateIn());
                    curCompareRate_1In = new BigDecimal(StringUtils.isEmpty(cur.getTotalCompareScanningRateIn())?"0"
                            :cur.getTotalCompareScanningRateIn());
                }

                //历史最终毛利额 = 前台毛利额
                BigDecimal hisRate = BigDecimal.ZERO;
                BigDecimal hisCompareRate_1 = BigDecimal.ZERO;
                BigDecimal hisRateIn = BigDecimal.ZERO;
                BigDecimal hisCompareRate_1In = BigDecimal.ZERO;
                if(null != his){
                    hisRate = new BigDecimal(StringUtils.isEmpty(his.getTotalFrontDeskRate())?"0"
                            :his.getTotalFrontDeskRate());
                    hisCompareRate_1 = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareFrontDeskRate())?"0"
                            :his.getTotalCompareFrontDeskRate());
                    hisRateIn = new BigDecimal(StringUtils.isEmpty(his.getTotalFrontDeskRateIn())?"0"
                            :his.getTotalFrontDeskRateIn());
                    hisCompareRate_1In = new BigDecimal(StringUtils.isEmpty(his.getTotalCompareFrontDeskRateIn())?"0"
                            :his.getTotalCompareFrontDeskRateIn());
                }
                //当天毛利额 + 历史毛利额
                baseSaleModel.setTotalRate(curRate.add(hisRate).toString());
                baseSaleModel.setTotalCompareRate(curCompareRate_1.add(hisCompareRate_1).toString());
                baseSaleModel.setTotalRateIn(curRateIn.add(hisRateIn).toString());
                baseSaleModel.setTotalCompareRateIn(curCompareRate_1In.add(hisCompareRate_1In).toString());

                //查询环比数据(一个月之前的数据)
                BaseSaleModel curMonthBaseSaleModel = null;
                try{
                    //查询当天环比
                    changeZyDataSource();
                    curMonthBaseSaleModel = saleReportZyDao.queryHisCommonBaseDataForHour(param,
                            DateUtils.addMonths(end,-1),null,hour);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //当天环比最终毛利额 = 环比销售 - 环比成本
                BigDecimal curMonthRate = BigDecimal.ZERO;
                BigDecimal curMonthRateIn = BigDecimal.ZERO;
                if(null != curMonthBaseSaleModel){
                    BigDecimal monthSale = new BigDecimal(StringUtils.isEmpty(curMonthBaseSaleModel.getTotalSale())
                            ?"0":curMonthBaseSaleModel.getTotalSale());
                    BigDecimal monthCost = new BigDecimal(StringUtils.isEmpty(curMonthBaseSaleModel.getTotalCost())
                            ?"0":curMonthBaseSaleModel.getTotalCost());
                    curMonthRate = monthSale.subtract(monthCost);
                    BigDecimal monthSaleIn = new BigDecimal(StringUtils.isEmpty(curMonthBaseSaleModel.getTotalSaleIn())
                            ?"0":curMonthBaseSaleModel.getTotalSaleIn());
                    BigDecimal monthCostIn = new BigDecimal(StringUtils.isEmpty(curMonthBaseSaleModel.getTotalCostIn())
                            ?"0":curMonthBaseSaleModel.getTotalCostIn());
                    curMonthRateIn = monthSaleIn.subtract(monthCostIn);
                }

                //查询起始时间到昨日的环比
                BaseSaleModel hisMonthBaseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param
                        ,DateUtils.addMonths(start,-1)
                        ,DateUtils.addMonths(DateUtils.addDays(end,-1),-1));

                //历史最终毛利额 = 前台毛利额
                BigDecimal hisMonthRate = BigDecimal.ZERO;
                BigDecimal hisMonthRateIn = BigDecimal.ZERO;
                if(null != hisMonthBaseSaleModel){
                    hisMonthRate = new BigDecimal(StringUtils.isEmpty(hisMonthBaseSaleModel.getTotalFrontDeskRate())
                            ?"0":hisMonthBaseSaleModel.getTotalFrontDeskRate());
                    hisMonthRateIn = new BigDecimal(StringUtils.isEmpty(hisMonthBaseSaleModel.getTotalFrontDeskRateIn())
                            ?"0":hisMonthBaseSaleModel.getTotalFrontDeskRateIn());
                }

                monthBaseSaleModel.setTotalRate(curMonthRate.add(hisMonthRate).toString());
                monthBaseSaleModel.setTotalRateIn(curMonthRateIn.add(hisMonthRateIn).toString());

                //查询同比和可比
                BaseSaleModel curCompareAndSameSaleModel = null;
                try{
                    //查询当天同比和可比
                    changeZyDataSource();
                    curCompareAndSameSaleModel = saleReportZyDao.queryHisCommonBaseDataForHour(param,
                            DateUtils.addYears(end,-1),null,hour);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //查询起始时间到昨日的同比和可比
                BaseSaleModel hisCompareAndSameSaleModel = null;
                try{
                    changeDgDataSource();
                    hisCompareAndSameSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param
                            ,DateUtils.addYears(start,-1)
                            ,DateUtils.addYears(DateUtils.addDays(end,-1),-1));
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                BigDecimal curCompareRate = BigDecimal.ZERO;
                BigDecimal curSameRate = BigDecimal.ZERO;
                BigDecimal curCompareRateIn = BigDecimal.ZERO;
                BigDecimal curSameRateIn = BigDecimal.ZERO;
                if(null != curCompareAndSameSaleModel){
                    BigDecimal sale = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalSale())
                            ?"0":curCompareAndSameSaleModel.getTotalSale());
                    BigDecimal cost = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalCost())
                            ?"0":curCompareAndSameSaleModel.getTotalCost());
                    BigDecimal saleCompare = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalCompareSale())
                            ?"0":curCompareAndSameSaleModel.getTotalCompareSale());
                    BigDecimal costCompare = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalCompareCost())
                            ?"0":curCompareAndSameSaleModel.getTotalCompareCost());
                    //当天可比最终毛利额 = 可比销售 - 可比成本
                    curCompareRate = saleCompare.subtract(costCompare);
                    //当天同比最终毛利额 = 可比销售 - 可比成本
                    curSameRate = sale.subtract(cost);

                    BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalSaleIn())
                            ?"0":curCompareAndSameSaleModel.getTotalSaleIn());
                    BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalCostIn())
                            ?"0":curCompareAndSameSaleModel.getTotalCostIn());
                    BigDecimal saleCompareIn = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalCompareSaleIn())
                            ?"0":curCompareAndSameSaleModel.getTotalCompareSaleIn());
                    BigDecimal costCompareIn = new BigDecimal(StringUtils.isEmpty(curCompareAndSameSaleModel.getTotalCompareCostIn())
                            ?"0":curCompareAndSameSaleModel.getTotalCompareCostIn());
                    //当天可比最终毛利额 = 可比销售 - 可比成本
                    curCompareRateIn = saleCompareIn.subtract(costCompareIn);
                    //当天同比最终毛利额 = 可比销售 - 可比成本
                    curSameRateIn = saleIn.subtract(costIn);
                }

                BigDecimal hisCompareRate = BigDecimal.ZERO;
                BigDecimal hisSameRate = BigDecimal.ZERO;
                BigDecimal hisCompareRateIn = BigDecimal.ZERO;
                BigDecimal hisSameRateIn = BigDecimal.ZERO;
                if(null != hisCompareAndSameSaleModel){
                    //历史可比最终毛利额 = 前台毛利额
                    hisCompareRate = new BigDecimal(StringUtils.isEmpty(hisCompareAndSameSaleModel.getTotalCompareFrontDeskRate())
                            ?"0":hisCompareAndSameSaleModel.getTotalCompareFrontDeskRate());
                    //历史同期最终毛利额 = 前台毛利额
                    hisSameRate = new BigDecimal(StringUtils.isEmpty(hisCompareAndSameSaleModel.getTotalFrontDeskRate())
                            ?"0":hisCompareAndSameSaleModel.getTotalFrontDeskRate());

                    hisCompareRateIn = new BigDecimal(StringUtils.isEmpty(hisCompareAndSameSaleModel.getTotalCompareFrontDeskRateIn())
                            ?"0":hisCompareAndSameSaleModel.getTotalCompareFrontDeskRateIn());
                    //历史同期最终毛利额 = 前台毛利额
                    hisSameRateIn = new BigDecimal(StringUtils.isEmpty(hisCompareAndSameSaleModel.getTotalFrontDeskRateIn())
                            ?"0":hisCompareAndSameSaleModel.getTotalFrontDeskRateIn());
                }
                compareAndSameSaleModel.setTotalRate(curCompareRate.add(hisCompareRate).toString());
                compareAndSameSaleModel.setTotalCompareRate(curSameRate.add(hisSameRate).toString());
                compareAndSameSaleModel.setTotalRateIn(curCompareRateIn.add(hisCompareRateIn).toString());
                compareAndSameSaleModel.setTotalCompareRateIn(curSameRateIn.add(hisSameRateIn).toString());
            }else{
                try{
                    changeDgDataSource();
                    //查询时间段的数据
                    baseSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param,start,end);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //历史最终毛利额 = 前台毛利额
                if(null != baseSaleModel){
                    baseSaleModel.setTotalRate(baseSaleModel.getTotalFrontDeskRate());
                    baseSaleModel.setTotalCompareRate(baseSaleModel.getTotalCompareFrontDeskRate());
                    baseSaleModel.setTotalRateIn(baseSaleModel.getTotalFrontDeskRateIn());
                    baseSaleModel.setTotalCompareRateIn(baseSaleModel.getTotalCompareFrontDeskRateIn());
                }
                //查询环比数据(一个月之前的数据)
                monthBaseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtils.addMonths(start,-1),DateUtils.addMonths(end,-1));
                //环比最终毛利额 = 环比前台毛利额
                if(null != monthBaseSaleModel){
                    monthBaseSaleModel.setTotalRate(monthBaseSaleModel.getTotalFrontDeskRate());
                    monthBaseSaleModel.setTotalRateIn(monthBaseSaleModel.getTotalFrontDeskRateIn());
                }
                try{
                    //查询同比和可比
                    changeDgDataSource();
                    compareAndSameSaleModel = saleReportRmsDao.queryHisCommonCompareBaseData(param
                            ,DateUtils.addYears(start,-1)
                            ,DateUtils.addYears(end,-1));
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //最终同比/可比毛利额 = 同比/可比前台毛利额
                if(null != compareAndSameSaleModel){
                    compareAndSameSaleModel.setTotalRate(compareAndSameSaleModel.getTotalFrontDeskRate());
                    compareAndSameSaleModel.setTotalCompareRate(compareAndSameSaleModel.getTotalCompareFrontDeskRate());

                    compareAndSameSaleModel.setTotalRateIn(compareAndSameSaleModel.getTotalFrontDeskRateIn());
                    compareAndSameSaleModel.setTotalCompareRateIn(compareAndSameSaleModel.getTotalCompareFrontDeskRateIn());
                }
            }
        }
        //当期毛利额
        allRateSumResponse.setTotalRate(baseSaleModel.getTotalRate());
        allRateSumResponse.setTotalRateIn(baseSaleModel.getTotalRateIn());
        BigDecimal monthRate = BigDecimal.ZERO;
        BigDecimal monthRateIn = BigDecimal.ZERO;
        if(null != monthBaseSaleModel){
            monthRate = new BigDecimal(StringUtils.isEmpty(monthBaseSaleModel.getTotalRate())
                    ?"0":monthBaseSaleModel.getTotalRate());
            monthRateIn = new BigDecimal(StringUtils.isEmpty(monthBaseSaleModel.getTotalRateIn())
                    ?"0":monthBaseSaleModel.getTotalRateIn());
        }
        //环比 = 当期毛利额 / 月毛利额 - 1
        if(monthRate.compareTo(BigDecimal.ZERO) != 0){
            allRateSumResponse.setRateMonthRate(new BigDecimal(baseSaleModel.getTotalRate())
                    .divide(monthRate,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        if(monthRateIn.compareTo(BigDecimal.ZERO) != 0){
            allRateSumResponse.setRateMonthRateIn(new BigDecimal(baseSaleModel.getTotalRateIn())
                    .divide(monthRateIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        BigDecimal compareRate = BigDecimal.ZERO;
        BigDecimal compareRateIn = BigDecimal.ZERO;
        if(null != compareAndSameSaleModel){
            compareRate = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCompareRate())
                    ?"0":compareAndSameSaleModel.getTotalCompareRate());
            compareRateIn = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalCompareRateIn())
                    ?"0":compareAndSameSaleModel.getTotalCompareRateIn());
        }
        BigDecimal sameRate = BigDecimal.ZERO;
        BigDecimal sameRateIn = BigDecimal.ZERO;
        if(null != compareAndSameSaleModel){
            sameRate = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalRate())
                    ?"0":compareAndSameSaleModel.getTotalRate());
            sameRateIn = new BigDecimal(StringUtils.isEmpty(compareAndSameSaleModel.getTotalRateIn())
                    ?"0":compareAndSameSaleModel.getTotalRateIn());
        }
        //可比 = 当期可比 / 可比 -1
        if(compareRate.compareTo(BigDecimal.ZERO) != 0){
            allRateSumResponse.setRateCompareRate(new BigDecimal(baseSaleModel.getTotalCompareRate())
                    .divide(compareRate,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        if(compareRateIn.compareTo(BigDecimal.ZERO) != 0){
            allRateSumResponse.setRateCompareRateIn(new BigDecimal(baseSaleModel.getTotalCompareRateIn())
                    .divide(compareRateIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        //同比 = 当期 / 同期 -1
        if(sameRate.compareTo(BigDecimal.ZERO) != 0){
            allRateSumResponse.setRateSameRate(new BigDecimal(baseSaleModel.getTotalRate())
                    .divide(sameRate,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        if(sameRateIn.compareTo(BigDecimal.ZERO) != 0){
            allRateSumResponse.setRateSameRateIn(new BigDecimal(baseSaleModel.getTotalRateIn())
                    .divide(sameRateIn,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).toString());
        }
        return allRateSumResponse;
    }

    /**
     * 查询销售达成率
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public AchievingRateResponse querySaleAchievingRate(BaseSaleRequest param) throws Exception {
        AchievingRateResponse achievingRateResponse = new AchievingRateResponse();
        BaseSaleModel baseSaleModel = new BaseSaleModel();
        GoalSale goalSale = null;
        //临时使用
        /*if(StringUtils.isEmpty(param.getProvinceId())
                && StringUtils.isEmpty(param.getAreaId())
                && StringUtils.isEmpty(param.getStoreId())){
            return achievingRateResponse;
        }*/
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        //查询目标值
        try{
            changeDgDataSource();
            goalSale = saleReportRmsDao.queryGoalSale(param,start,end);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询销售金额
        if(null == end){
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                //当天数据
                BaseSaleModel cur = saleReportCsmbDao.queryCurCommonBaseData(param,start,null);
                //月初到昨日
                BaseSaleModel his = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtil.getFirstDayForMonth(start),DateUtils.addDays(start,-1));
                BigDecimal curSale = BigDecimal.ZERO;
                BigDecimal hisSale = BigDecimal.ZERO;
                if(null != cur){
                    curSale = new BigDecimal(StringUtils.isEmpty(cur.getTotalSale())?"0":cur.getTotalSale());
                }
                if(null != his){
                    hisSale = new BigDecimal(StringUtils.isEmpty(his.getTotalSale())?"0":his.getTotalSale());
                }
                baseSaleModel.setTotalSale(curSale.add(hisSale).toString());
            }else{
                //月初到历史某天数据
                baseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param,DateUtil.getFirstDayForMonth(start),start);
            }
        }else{
            if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                BaseSaleModel cur = saleReportCsmbDao.queryCurCommonBaseData(param,end,null);
                //起始时间的月初到昨日
                BaseSaleModel his = saleReportCsmbDao.queryHisCommonBaseData(param,start
                        ,DateUtils.addDays(end,-1));
                BigDecimal curSale = BigDecimal.ZERO;
                if(null != cur){
                    curSale = new BigDecimal(StringUtils.isEmpty(cur.getTotalSale())?"0"
                            :cur.getTotalSale());
                }
                BigDecimal hisSale = BigDecimal.ZERO;
                if(null != his){
                    hisSale = new BigDecimal(StringUtils.isEmpty(his.getTotalSale())?"0"
                            :his.getTotalSale());
                }
                baseSaleModel.setTotalSale(curSale.add(hisSale).toString());
            }else{
                //查询历史时段数据
                //起始时间的月初到结束时间
                baseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param,start,end);
            }
        }

        if(null != baseSaleModel && null != goalSale){
            BigDecimal goal = new BigDecimal(StringUtils.isEmpty(goalSale.getSale())?"0"
                    :goalSale.getSale());
            BigDecimal sale = new BigDecimal(StringUtils.isEmpty(baseSaleModel.getTotalSale())?"0"
                    :baseSaleModel.getTotalSale());
            if(goal.compareTo(BigDecimal.ZERO) != 0){
                achievingRateResponse.setSaleAchievingRate(sale.
                        divide(goal,4,BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        return achievingRateResponse;
    }

    /**
     * 查询毛利额达成率
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public AchievingRateResponse queryRateAchievingRate(BaseSaleRequest param) throws Exception {
        AchievingRateResponse achievingRateResponse = new AchievingRateResponse();
        BaseSaleModel baseSaleModel = new BaseSaleModel();
        GoalSale goalSale = null;
        //临时使用
        /*if(StringUtils.isEmpty(param.getProvinceId())
                && StringUtils.isEmpty(param.getAreaId())
                && StringUtils.isEmpty(param.getStoreId())){
            return achievingRateResponse;
        }*/
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        //查询目标值
        try{
            changeDgDataSource();
            goalSale = saleReportRmsDao.queryGoalSale(param,start,end);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询毛利额
        if(null == end){
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                //当天数据
                BaseSaleModel cur = saleReportCsmbDao.queryCurCommonBaseData(param,start,null);
                //月初到昨日
                BaseSaleModel his = saleReportCsmbDao.queryHisCommonBaseData(param,
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
                baseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param,DateUtil.getFirstDayForMonth(start),start);
                //历史最终毛利额 = 前台毛利额
                if(null != baseSaleModel){
                    baseSaleModel.setTotalRate(baseSaleModel.getTotalFrontDeskRate());
                }
            }
        }else{
            if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                baseSaleModel = new BaseSaleModel();
                BaseSaleModel cur = saleReportCsmbDao.queryCurCommonBaseData(param,end,null);
                BaseSaleModel his = saleReportCsmbDao.queryHisCommonBaseData(param,start
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
                baseSaleModel = saleReportCsmbDao.queryHisCommonBaseData(param,start,end);
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
     * 查询本年和本月销售额
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public SaleMonthAndYearResponse querySaleMonthAndYear(BaseSaleRequest param) throws Exception {
        SaleMonthAndYearResponse saleMonthAndYearResponse = new SaleMonthAndYearResponse();
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        if(null == end){
            //当时间点是当天时
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                //当天数据
                BaseSaleModel cur = saleReportCsmbDao.queryCurCommonBaseData(param,start,null);
                //月初至昨日数据
                BaseSaleModel hisMonth = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtil.getFirstDayForMonth(start),DateUtils.addDays(start,-1));
                //年初至昨日数据
                BaseSaleModel hisYear = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtil.getFirstDayForYear(start),DateUtils.addDays(start,-1));
                BigDecimal curSale = BigDecimal.ZERO;
                BigDecimal curSaleIn = BigDecimal.ZERO;
                if(null != cur){
                    curSale = new BigDecimal(StringUtils.isEmpty(cur.getTotalSale())?"0":cur.getTotalSale());
                    curSaleIn = new BigDecimal(StringUtils.isEmpty(cur.getTotalSaleIn())?"0":cur.getTotalSaleIn());
                }
                BigDecimal hisMonthSale = BigDecimal.ZERO;
                BigDecimal hisMonthSaleIn = BigDecimal.ZERO;
                if(null != hisMonth){
                    hisMonthSale = new BigDecimal(StringUtils.isEmpty(hisMonth.getTotalSale())
                            ?"0":hisMonth.getTotalSale());
                    hisMonthSaleIn = new BigDecimal(StringUtils.isEmpty(hisMonth.getTotalSaleIn())
                            ?"0":hisMonth.getTotalSaleIn());
                }
                BigDecimal hisYearSale = BigDecimal.ZERO;
                BigDecimal hisYearSaleIn = BigDecimal.ZERO;
                if(null != hisYear){
                    hisYearSale = new BigDecimal(StringUtils.isEmpty(hisYear.getTotalSale())
                            ?"0":hisYear.getTotalSale());
                    hisYearSaleIn = new BigDecimal(StringUtils.isEmpty(hisYear.getTotalSaleIn())
                            ?"0":hisYear.getTotalSaleIn());
                }
                saleMonthAndYearResponse.setTotalMonthSale(curSale.add(hisMonthSale).toString());
                saleMonthAndYearResponse.setTotalYearSale(curSale.add(hisYearSale).toString());

                saleMonthAndYearResponse.setTotalMonthSaleIn(curSaleIn.add(hisMonthSaleIn).toString());
                saleMonthAndYearResponse.setTotalYearSaleIn(curSaleIn.add(hisYearSaleIn).toString());
            }else{
                //当时间点是历史某点
                //月初至当天时间
                BaseSaleModel hisMonth = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtil.getFirstDayForMonth(start),start);
                //年初至当天时间
                BaseSaleModel hisYear = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtil.getFirstDayForYear(start),start);

                BigDecimal hisMonthSale = BigDecimal.ZERO;
                BigDecimal hisMonthSaleIn = BigDecimal.ZERO;
                if(null != hisMonth){
                    hisMonthSale = new BigDecimal(StringUtils.isEmpty(hisMonth.getTotalSale())
                            ?"0":hisMonth.getTotalSale());
                    hisMonthSaleIn = new BigDecimal(StringUtils.isEmpty(hisMonth.getTotalSaleIn())
                            ?"0":hisMonth.getTotalSaleIn());
                }
                BigDecimal hisYearSale = BigDecimal.ZERO;
                BigDecimal hisYearSaleIn = BigDecimal.ZERO;
                if(null != hisYear){
                    hisYearSale = new BigDecimal(StringUtils.isEmpty(hisYear.getTotalSale())
                            ?"0":hisYear.getTotalSale());
                    hisYearSaleIn = new BigDecimal(StringUtils.isEmpty(hisYear.getTotalSaleIn())
                            ?"0":hisYear.getTotalSaleIn());
                }
                saleMonthAndYearResponse.setTotalMonthSale(hisMonthSale.toString());
                saleMonthAndYearResponse.setTotalYearSale(hisYearSale.toString());

                saleMonthAndYearResponse.setTotalMonthSaleIn(hisMonthSaleIn.toString());
                saleMonthAndYearResponse.setTotalYearSaleIn(hisYearSaleIn.toString());
            }
        }
        return saleMonthAndYearResponse;
    }

    /**
     * 查询本年和本月毛利额
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public RateMonthAndYearResponse queryRateMonthAndYear(BaseSaleRequest param) throws Exception {
        RateMonthAndYearResponse rateMonthAndYearResponse = new RateMonthAndYearResponse();
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        if(null == end){
            //当时间点是当天时
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                //当天数据
                BaseSaleModel cur = saleReportCsmbDao.queryCurCommonBaseData(param,start,null);
                //月初至昨日数据
                BaseSaleModel hisMonth = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtil.getFirstDayForMonth(start),DateUtils.addDays(start,-1));
                //年初至昨日数据
                BaseSaleModel hisYear = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtil.getFirstDayForYear(start),DateUtils.addDays(start,-1));

                BigDecimal curRate = BigDecimal.ZERO;
                BigDecimal curRateIn = BigDecimal.ZERO;
                if(null != cur){
                    curRate = new BigDecimal(StringUtils.isEmpty(cur.getTotalScanningRate())?"0":cur.getTotalScanningRate());
                    curRateIn = new BigDecimal(StringUtils.isEmpty(cur.getTotalScanningRateIn())?"0":cur.getTotalScanningRateIn());
                }
                BigDecimal hisMonthRate = BigDecimal.ZERO;
                BigDecimal hisMonthRateIn = BigDecimal.ZERO;
                if(null != hisMonth){
                    hisMonthRate = new BigDecimal(StringUtils.isEmpty(hisMonth.getTotalFrontDeskRate())
                            ?"0":hisMonth.getTotalFrontDeskRate());
                    hisMonthRateIn = new BigDecimal(StringUtils.isEmpty(hisMonth.getTotalFrontDeskRateIn())
                            ?"0":hisMonth.getTotalFrontDeskRateIn());
                }
                BigDecimal hisYearRate = BigDecimal.ZERO;
                BigDecimal hisYearRateIn = BigDecimal.ZERO;
                if(null != hisYear){
                    hisYearRate = new BigDecimal(StringUtils.isEmpty(hisYear.getTotalFrontDeskRate())
                            ?"0":hisYear.getTotalFrontDeskRate());
                    hisYearRateIn = new BigDecimal(StringUtils.isEmpty(hisYear.getTotalFrontDeskRateIn())
                            ?"0":hisYear.getTotalFrontDeskRateIn());
                }
                rateMonthAndYearResponse.setTotalMonthRate(curRate.add(hisMonthRate).toString());
                rateMonthAndYearResponse.setTotalYearRate(curRate.add(hisYearRate).toString());

                rateMonthAndYearResponse.setTotalMonthRateIn(curRateIn.add(hisMonthRateIn).toString());
                rateMonthAndYearResponse.setTotalYearRateIn(curRateIn.add(hisYearRateIn).toString());
            }else{
                //当时间点是历史某点
                //月初至当天时间
                BaseSaleModel hisMonth = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtil.getFirstDayForMonth(start),start);
                //年初至当天时间
                BaseSaleModel hisYear = saleReportCsmbDao.queryHisCommonBaseData(param,
                        DateUtil.getFirstDayForYear(start),start);

                BigDecimal hisMonthRate = BigDecimal.ZERO;
                BigDecimal hisMonthRateIn = BigDecimal.ZERO;
                if(null != hisMonth){
                    hisMonthRate = new BigDecimal(StringUtils.isEmpty(hisMonth.getTotalFrontDeskRate())
                            ?"0":hisMonth.getTotalFrontDeskRate());
                    hisMonthRateIn = new BigDecimal(StringUtils.isEmpty(hisMonth.getTotalFrontDeskRateIn())
                            ?"0":hisMonth.getTotalFrontDeskRateIn());
                }
                BigDecimal hisYearRate = BigDecimal.ZERO;
                BigDecimal hisYearRateIn = BigDecimal.ZERO;
                if(null != hisYear){
                    hisYearRate = new BigDecimal(StringUtils.isEmpty(hisYear.getTotalFrontDeskRate())
                            ?"0":hisYear.getTotalFrontDeskRate());
                    hisYearRateIn = new BigDecimal(StringUtils.isEmpty(hisYear.getTotalFrontDeskRateIn())
                            ?"0":hisYear.getTotalFrontDeskRateIn());
                }
                rateMonthAndYearResponse.setTotalMonthRate(hisMonthRate.toString());
                rateMonthAndYearResponse.setTotalYearRate(hisYearRate.toString());

                rateMonthAndYearResponse.setTotalMonthRateIn(hisMonthRateIn.toString());
                rateMonthAndYearResponse.setTotalYearRateIn(hisYearRateIn.toString());
            }
        }
        return rateMonthAndYearResponse;
    }

    /**
     * 查询家电
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public HomeApplianceResponse queryHomeAppliance(BaseSaleRequest param) throws Exception {
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        HomeAppliance homeAppliance = saleReportCsmbDao.queryHomeApplianceInfo(param,start,end);
        if(null != homeAppliance){
            BigDecimal sales = new BigDecimal(StringUtils.isEmpty(homeAppliance.getTotalSale()) ? "0"
                    : homeAppliance.getTotalSale());
            BigDecimal cost = new BigDecimal(StringUtils.isEmpty(homeAppliance.getTotalCost()) ? "0"
                    : homeAppliance.getTotalCost());

            BigDecimal salesIn = new BigDecimal(StringUtils.isEmpty(homeAppliance.getTotalSaleIn()) ? "0"
                    : homeAppliance.getTotalSaleIn());
            BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(homeAppliance.getTotalCostIn()) ? "0"
                    : homeAppliance.getTotalCostIn());

            if(sales.compareTo(BigDecimal.ZERO) != 0){
                BigDecimal rate = new BigDecimal((sales.subtract(cost))
                        .divide(sales,4, BigDecimal.ROUND_HALF_UP).toString());
                homeAppliance.setTotalProfit(rate.toString());
            }
            if(salesIn.compareTo(BigDecimal.ZERO) != 0){
                BigDecimal rateIn = new BigDecimal((salesIn.subtract(costIn))
                        .divide(salesIn,4, BigDecimal.ROUND_HALF_UP).toString());
                homeAppliance.setTotalProfitIn(rateIn.toString());
            }
            homeAppliance.setTotalRate(sales.subtract(cost).toString());
            homeAppliance.setTotalRateIn(salesIn.subtract(costIn).toString());
        }
        HomeApplianceResponse response = new HomeApplianceResponse();
        BeanUtils.copyProperties(homeAppliance, response);
        return response;
    }

    /**
     * 查询渠道构成
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public ChannlResponse queryChannlResponse(BaseSaleRequest param) throws Exception {
        ChannlResponse channlResponse = new ChannlResponse();
        List<ChannlModel> list = null;
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        if(null == end){
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                list = saleReportCsmbDao.queryCurChannl(param,start,null);
            }else{
                try{
                    changeZyDataSource();
                    if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                        list = saleReportZyDao.queryHisChannl(param,start,null);
                    }else{
                        list = saleReportZyDao.queryHisChannlNotDept(param,start,null);
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

            }
        }else{
            if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                list = new ArrayList<>();
                List<ChannlModel> curList = saleReportCsmbDao.queryCurChannl(param,end,null);

                List<ChannlModel> hisList = null;
                try{
                    changeZyDataSource();
                    if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                        hisList = saleReportZyDao.queryHisChannl(param,start,DateUtils.addDays(end,-1));
                    }else{
                        hisList = saleReportZyDao.queryHisChannlNotDept(param,start,DateUtils.addDays(end,-1));
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                if(null != curList && curList.size() > 0) {
                    list.addAll(curList);
                }
                //讲历史数据合并到list中
                for(ChannlModel hisChannlModel : hisList){
                    boolean flag = true;
                    for(ChannlModel channlModel : list){
                        if(channlModel.getType().equalsIgnoreCase(hisChannlModel.getType())){
                            BigDecimal sale = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSale())?"0"
                                    :channlModel.getTotalSale());
                            BigDecimal hisSale = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSale())?"0"
                                    :channlModel.getTotalSale());

                            BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSaleIn())?"0"
                                    :channlModel.getTotalSaleIn());
                            BigDecimal hisSaleIn = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSaleIn())?"0"
                                    :channlModel.getTotalSaleIn());

                            channlModel.setTotalSale(sale.add(hisSale).toString());
                            channlModel.setTotalSaleIn(saleIn.add(hisSaleIn).toString());
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        list.add(hisChannlModel);
                    }
                }
            }else{
                try{
                    changeZyDataSource();
                    if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                        list = saleReportZyDao.queryHisChannl(param,start,end);
                    }else{
                        list = saleReportZyDao.queryHisChannlNotDept(param,start,end);
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }

        if(null != list && list.size() > 0){
            for(ChannlModel channlModel : list){
                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSale())
                        ?"0":channlModel.getTotalSale());

                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSaleIn())
                        ?"0":channlModel.getTotalSaleIn());

                if("weixinPay".equalsIgnoreCase(channlModel.getType())){
                        channlResponse.setWeixinPay(sale.toString());
                    channlResponse.setWeixinPayIn(saleIn.toString());
                }
                if("betterPay".equalsIgnoreCase(channlModel.getType())){
                        channlResponse.setBetterPay(sale.toString());
                    channlResponse.setBetterPayIn(saleIn.toString());
                }
                if("otherPay".equalsIgnoreCase(channlModel.getType())){
                        channlResponse.setOtherPay(sale.toString());
                    channlResponse.setOtherPayIn(saleIn.toString());
                }
                if("jingdongPay".equalsIgnoreCase(channlModel.getType())){
                        channlResponse.setJingdongPay(sale.toString());
                    channlResponse.setJingdongPayIn(saleIn.toString());
                }
                if("scancodePay".equalsIgnoreCase(channlModel.getType())){
                        channlResponse.setScancodePay(sale.toString());
                    channlResponse.setScancodePayIn(saleIn.toString());
                }
                if("normalPay".equalsIgnoreCase(channlModel.getType())){
                        channlResponse.setNormalPay(sale.toString());
                    channlResponse.setNormalPayIn(saleIn.toString());
                }
                if("meituanPay".equalsIgnoreCase(channlModel.getType())){
                        channlResponse.setMeituanPay(sale.toString());
                    channlResponse.setMeituanPayIn(saleIn.toString());
                }
            }
        }
        //明细查询
        ChannlDetailListResponse channlDetailListResponse = queryChannlDetailResponse(param);
        if(null != channlDetailListResponse){
            channlResponse.setWxList(channlDetailListResponse.getWxList());
            channlResponse.setBetterList(channlDetailListResponse.getBetterList());
            channlResponse.setJdList(channlDetailListResponse.getJdList());
            channlResponse.setOtherList(channlDetailListResponse.getOtherList());
            channlResponse.setScanCodeList(channlDetailListResponse.getScanCodeList());
            channlResponse.setNormalList(channlDetailListResponse.getNormalList());
            channlResponse.setMtList(channlDetailListResponse.getMtList());
        }
        return channlResponse;
    }

    /**
     * 查询渠道构成明细
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public ChannlDetailListResponse queryChannlDetailResponse(BaseSaleRequest param) throws Exception {
        ChannlDetailListResponse channlDetailListResponse = new ChannlDetailListResponse();
        List<ChannlModel> list = null;
        List<ChannlDetailResponse> wxList = new ArrayList<>();
        List<ChannlDetailResponse> betterList = new ArrayList<>();
        List<ChannlDetailResponse> otherList = new ArrayList<>();
        List<ChannlDetailResponse> jdList = new ArrayList<>();
        List<ChannlDetailResponse> scanCodeList = new ArrayList<>();
        List<ChannlDetailResponse> normalList = new ArrayList<>();
        List<ChannlDetailResponse> mtList = new ArrayList<>();
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        if(null == end){
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                list = saleReportCsmbDao.queryCurChannlForAll(param,start,null);
            }else{
                try{
                    changeZyDataSource();
                    if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                        list = saleReportZyDao.queryHisAllChannlHaveDept(param,start,null);
                    }else{
                        list = saleReportZyDao.queryHisAllChannlNotDept(param,start,null);
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else{
            if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                list = new ArrayList<>();
                List<ChannlModel> curList = saleReportCsmbDao.queryCurChannlForAll(param,end,null);

                List<ChannlModel> hisList = null;
                try{
                    changeZyDataSource();
                    if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                        hisList = saleReportZyDao.queryHisAllChannlHaveDept(param,start,DateUtils.addDays(end,-1));
                    }else{
                        hisList = saleReportZyDao.queryHisAllChannlNotDept(param,start,DateUtils.addDays(end,-1));
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                if(null != curList && curList.size() > 0) {
                    list.addAll(curList);
                }
                //讲历史数据合并到list中
                for(ChannlModel hisChannlModel : hisList){
                    boolean flag = true;
                    for(ChannlModel channlModel : list){
                        if((null != channlModel.getChannel()
                                && channlModel.getChannel().equalsIgnoreCase(hisChannlModel.getChannel()))){
                            BigDecimal sale = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSale())?"0"
                                    :channlModel.getTotalSale());
                            BigDecimal hisSale = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSale())?"0"
                                    :channlModel.getTotalSale());

                            BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSaleIn())?"0"
                                    :channlModel.getTotalSaleIn());
                            BigDecimal hisSaleIn = new BigDecimal(StringUtils.isEmpty(channlModel.getTotalSaleIn())?"0"
                                    :channlModel.getTotalSaleIn());

                            channlModel.setTotalSale(sale.add(hisSale).toString());
                            channlModel.setTotalSaleIn(saleIn.add(hisSaleIn).toString());
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        list.add(hisChannlModel);
                    }
                }
            }else{
                try{
                    changeZyDataSource();
                    if(null != param.getDeptIds() && param.getDeptIds().size() > 0){
                        list = saleReportZyDao.queryHisAllChannlHaveDept(param,start,end);
                    }else{
                        list = saleReportZyDao.queryHisAllChannlNotDept(param,start,end);
                    }
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }

        if(null != list && list.size() > 0){
            for(ChannlModel channlModel : list){
                ChannlDetailResponse channlDetailResponse = new ChannlDetailResponse();
                if("微信自助".equalsIgnoreCase(channlModel.getChannelParentName())){
                    BeanUtils.copyProperties(channlModel,channlDetailResponse);
                    wxList.add(channlDetailResponse);
                }
                if("BETTER购".equalsIgnoreCase(channlModel.getChannelParentName())){
                    BeanUtils.copyProperties(channlModel,channlDetailResponse);
                    betterList.add(channlDetailResponse);
                }
                if("多点自由购".equalsIgnoreCase(channlModel.getChannelParentName())
                        || "多点O2O".equalsIgnoreCase(channlModel.getChannelParentName())){
                    BeanUtils.copyProperties(channlModel,channlDetailResponse);
                    otherList.add(channlDetailResponse);
                }
                if("京东到家".equalsIgnoreCase(channlModel.getChannelParentName()) ||
                        "京东自助".equalsIgnoreCase(channlModel.getChannelParentName())){
                    BeanUtils.copyProperties(channlModel,channlDetailResponse);
                    jdList.add(channlDetailResponse);
                }
                if("扫码购".equalsIgnoreCase(channlModel.getChannelParentName())){
                    BeanUtils.copyProperties(channlModel,channlDetailResponse);
                    scanCodeList.add(channlDetailResponse);
                }
                if("会员码".equalsIgnoreCase(channlModel.getChannelParentName())
                        || "云POS".equalsIgnoreCase(channlModel.getChannelParentName())
                        || "BKSLS".equalsIgnoreCase(channlModel.getChannel())
                        || null == channlModel.getChannel()){
                    if("BKSLS".equalsIgnoreCase(channlModel.getChannel()) || null == channlModel.getChannel()){
                        channlModel.setChannelName("正常购");
                    }
                    BeanUtils.copyProperties(channlModel,channlDetailResponse);
                    normalList.add(channlDetailResponse);
                }
                if("美团闪购".equalsIgnoreCase(channlModel.getChannelParentName())){
                    BeanUtils.copyProperties(channlModel,channlDetailResponse);
                    mtList.add(channlDetailResponse);
                }
            }
        }
        channlDetailListResponse.setWxList(wxList);
        channlDetailListResponse.setBetterList(betterList);
        channlDetailListResponse.setJdList(jdList);
        channlDetailListResponse.setOtherList(otherList);
        channlDetailListResponse.setScanCodeList(scanCodeList);
        channlDetailListResponse.setNormalList(normalList);
        channlDetailListResponse.setMtList(mtList);
        return channlDetailListResponse;
    }

    /**
     * 查询销售趋势
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public SaleTrendListResponse querySaleTrendResponse(BaseSaleRequest param) throws Exception {
        SaleTrendListResponse saleTrendListResponse = new SaleTrendListResponse();
        List<SaleTrendResponse> list = new ArrayList<>();
        //数据库查询数据
        List<SaleTrendModel> all = new ArrayList<>();
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        //构造销售趋势数据
        if(null == end){
            //单时间点则查询月初到时间点
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                List<SaleTrendModel> cur = saleReportCsmbDao.queryCurSaleTrendModel(param
                        ,start,null);
                List<SaleTrendModel> his = saleReportCsmbDao.queryHisSaleTrendModel(param,
                        DateUtil.getFirstDayForMonth(start),DateUtils.addDays(start,-1));

                if(null != cur && cur.size() > 0){
                    for(SaleTrendModel saleTrendModel : cur){
                        //当天的最终毛利额 = 扫描毛利额
                        saleTrendModel.setTotalRate(saleTrendModel.getTotalRate());
                        saleTrendModel.setTotalRateIn(saleTrendModel.getTotalRateIn());
                    }
                    all.addAll(cur);
                }

                if(null != his && his.size() > 0){
                    for(SaleTrendModel saleTrendModel : his){
                        //历史的最终毛利额 = 前台毛利额
                        saleTrendModel.setTotalRate(saleTrendModel.getTotalFrontDeskRate());
                        saleTrendModel.setTotalRateIn(saleTrendModel.getTotalFrontDeskRateIn());
                    }
                    all.addAll(his);
                }
            }else{
                all = saleReportCsmbDao.queryHisSaleTrendModel(param,
                        DateUtil.getFirstDayForMonth(start),start);
                if(null != all && all.size() > 0){
                    for(SaleTrendModel saleTrendModel : all){
                        //历史的最终毛利额 = 前台毛利额
                        saleTrendModel.setTotalRate(saleTrendModel.getTotalFrontDeskRate());
                        saleTrendModel.setTotalRateIn(saleTrendModel.getTotalFrontDeskRateIn());
                    }
                }
            }
        }else{
            if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                List<SaleTrendModel> cur = saleReportCsmbDao.queryCurSaleTrendModel(param
                        ,end,null);

                List<SaleTrendModel> his = saleReportCsmbDao.queryHisSaleTrendModel(param,
                        start,DateUtils.addDays(end,-1));

                if(null != cur && cur.size() > 0){
                    for(SaleTrendModel saleTrendModel : cur){
                        //当天的最终毛利额 = 扫描毛利额
                        saleTrendModel.setTotalRate(saleTrendModel.getTotalRate());
                        saleTrendModel.setTotalRateIn(saleTrendModel.getTotalRateIn());
                    }
                    all.addAll(cur);
                }

                if(null != his && his.size() > 0){
                    for(SaleTrendModel saleTrendModel : his){
                        //历史的最终毛利额 = 前台毛利额
                        saleTrendModel.setTotalRate(saleTrendModel.getTotalFrontDeskRate());
                        saleTrendModel.setTotalRateIn(saleTrendModel.getTotalFrontDeskRateIn());
                    }
                    all.addAll(his);
                }
            }else{
                all = saleReportCsmbDao.queryHisSaleTrendModel(param,
                        start,end);
                if(null != all && all.size() > 0){
                    for(SaleTrendModel saleTrendModel : all){
                        //历史的最终毛利额 = 前台毛利额
                        saleTrendModel.setTotalRate(saleTrendModel.getTotalFrontDeskRate());
                        saleTrendModel.setTotalRateIn(saleTrendModel.getTotalFrontDeskRateIn());
                    }
                }
            }
        }
        //计算毛利额
        for(SaleTrendModel saleTrendModel : all){
            SaleTrendResponse saleTrendResponse = new SaleTrendResponse();
            BigDecimal sale = new BigDecimal(StringUtils.isEmpty(saleTrendModel.getTotalSale())?"0":
                    saleTrendModel.getTotalSale());
            BigDecimal rate = new BigDecimal(StringUtils.isEmpty(saleTrendModel.getTotalRate())?"0":
                    saleTrendModel.getTotalRate());

            BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(saleTrendModel.getTotalSaleIn())?"0":
                    saleTrendModel.getTotalSaleIn());
            BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(saleTrendModel.getTotalRateIn())?"0":
                    saleTrendModel.getTotalRateIn());

            saleTrendResponse.setTime(saleTrendModel.getTime());
            saleTrendResponse.setTotalSale(sale.toString());
            saleTrendResponse.setTotalSaleIn(saleIn.toString());
            if(sale.compareTo(BigDecimal.ZERO) != 0){
                saleTrendResponse.setTotalProfit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                saleTrendResponse.setTotalProfitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
            }
            list.add(saleTrendResponse);
        }

        //排序
        list = list.stream().sorted(new Comparator<SaleTrendResponse>() {
            @Override
            public int compare(SaleTrendResponse o1, SaleTrendResponse o2) {
                BigDecimal value1 = new BigDecimal(o1.getTime());
                BigDecimal value2 = new BigDecimal(o2.getTime());
                return value1.compareTo(value2);
            }
        }).collect(Collectors.toList());

        saleTrendListResponse.setList(list);
        return saleTrendListResponse;
    }

    /**
     * 查询销售构成
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public SaleCompositionListResponse querySaleComposition(BaseSaleRequest param) throws Exception {
        SaleCompositionListResponse response = new SaleCompositionListResponse();
        List<SaleCompositionResponse> saleCompositionResponseList = new ArrayList<>();
        List<SaleCompositionModel> dataList = null;
        transform(param);
        Date start = param.getStart();
        Date end = param.getEnd();
        //构造销售和毛利额数据
        if(null == end){
            if(DateUtils.getBetweenDay(start,new Date()) >= 0){
                dataList = saleReportCsmbDao.querySaleComposition(param,start,null);
                //设置最终毛利额=扫描毛利额
                if(null != dataList && dataList.size() > 0){
                    for(SaleCompositionModel saleCompositionModel : dataList){
                        saleCompositionModel.setTotalRate(saleCompositionModel.getTotalScanningRate());
                        saleCompositionModel.setTotalRateIn(saleCompositionModel.getTotalScanningRateIn());
                    }
                }
            }else{
                try{
                    changeZyDataSource();
                    dataList = saleReportZyDao.querySaleComposition(param,start,null);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                //设置最终毛利额=扫描毛利额（只存在扫描毛利额）
                if(null != dataList && dataList.size() > 0){
                    for(SaleCompositionModel saleCompositionModel : dataList){
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalCost())
                                ?"0":saleCompositionModel.getTotalCost());
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalSale())
                                ?"0":saleCompositionModel.getTotalSale());

                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalCostIn())
                                ?"0":saleCompositionModel.getTotalCostIn());
                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalSaleIn())
                                ?"0":saleCompositionModel.getTotalSaleIn());
                        saleCompositionModel.setTotalRate(sale.subtract(cost).toString());
                        saleCompositionModel.setTotalRateIn(saleIn.subtract(costIn).toString());
                    }
                }
            }
        }else{
            if(DateUtils.getBetweenDay(end,new Date()) >= 0){
                dataList = new ArrayList<>();
                List<SaleCompositionModel> cur = saleReportCsmbDao.querySaleComposition(param,end,null);
                List<SaleCompositionModel> his = null;
                try{
                    changeZyDataSource();
                    his = saleReportZyDao.querySaleComposition(param,start,end);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                if(null != cur && cur.size() > 0){
                    //设置最终毛利额=扫描毛利额（只存在扫描毛利额）
                    for(SaleCompositionModel saleCompositionModel : cur){
                        saleCompositionModel.setTotalRate(saleCompositionModel.getTotalScanningRate());
                        saleCompositionModel.setTotalRateIn(saleCompositionModel.getTotalScanningRateIn());
                    }
                    dataList.addAll(cur);
                }

                for(SaleCompositionModel hisSaleCompositionModel : his){
                    boolean flag = true;
                    for(SaleCompositionModel saleCompositionModel : dataList){
                        if(saleCompositionModel.getType().equalsIgnoreCase(hisSaleCompositionModel.getType())){
                            BigDecimal hisCost = new BigDecimal(StringUtils.isEmpty(hisSaleCompositionModel.getTotalCost())
                                    ?"0":hisSaleCompositionModel.getTotalCost());
                            BigDecimal hisSale = new BigDecimal(StringUtils.isEmpty(hisSaleCompositionModel.getTotalSale())
                                    ?"0":hisSaleCompositionModel.getTotalSale());
                            BigDecimal rate = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalRate())
                                            ?"0":saleCompositionModel.getTotalRate());
                            BigDecimal sale = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalSale())
                                    ?"0":saleCompositionModel.getTotalSale());

                            BigDecimal hisCostIn = new BigDecimal(StringUtils.isEmpty(hisSaleCompositionModel.getTotalCostIn())
                                    ?"0":hisSaleCompositionModel.getTotalCostIn());
                            BigDecimal hisSaleIn = new BigDecimal(StringUtils.isEmpty(hisSaleCompositionModel.getTotalSaleIn())
                                    ?"0":hisSaleCompositionModel.getTotalSaleIn());
                            BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalRateIn())
                                    ?"0":saleCompositionModel.getTotalRateIn());
                            BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalSaleIn())
                                    ?"0":saleCompositionModel.getTotalSaleIn());

                            saleCompositionModel.setTotalSale(hisSale.add(sale).toString());
                            //设置最终毛利额=当天扫描毛利额 + 历史扫描毛利额
                            saleCompositionModel.setTotalRate(rate.add(hisSale.subtract(hisCost)).toString());

                            saleCompositionModel.setTotalSaleIn(hisSaleIn.add(saleIn).toString());
                            //设置最终毛利额=当天扫描毛利额 + 历史扫描毛利额
                            saleCompositionModel.setTotalRateIn(rateIn.add(hisSaleIn.subtract(hisCostIn)).toString());

                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        BigDecimal hisCost = new BigDecimal(StringUtils.isEmpty(hisSaleCompositionModel.getTotalCost())
                                ?"0":hisSaleCompositionModel.getTotalCost());

                        BigDecimal hisSale = new BigDecimal(StringUtils.isEmpty(hisSaleCompositionModel.getTotalSale())
                                ?"0":hisSaleCompositionModel.getTotalSale());

                        BigDecimal hisCostIn = new BigDecimal(StringUtils.isEmpty(hisSaleCompositionModel.getTotalCostIn())
                                ?"0":hisSaleCompositionModel.getTotalCostIn());

                        BigDecimal hisSaleIn = new BigDecimal(StringUtils.isEmpty(hisSaleCompositionModel.getTotalSaleIn())
                                ?"0":hisSaleCompositionModel.getTotalSaleIn());
                        //设置最终毛利额=扫描毛利额（只存在扫描毛利额）
                        hisSaleCompositionModel.setTotalRate(hisSale.subtract(hisCost).toString());
                        hisSaleCompositionModel.setTotalRateIn(hisSaleIn.subtract(hisCostIn).toString());

                        dataList.add(hisSaleCompositionModel);
                    }
                }

            }else{
                try{
                    changeZyDataSource();
                    dataList = saleReportZyDao.querySaleComposition(param,start,end);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
                if(null != dataList && dataList.size() > 0){
                    for(SaleCompositionModel saleCompositionModel : dataList){
                        //设置最终毛利额=扫描毛利额（只存在扫描毛利额）
                        BigDecimal cost = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalCost())
                                ?"0":saleCompositionModel.getTotalCost());
                        BigDecimal sale = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalSale())
                                ?"0":saleCompositionModel.getTotalSale());

                        BigDecimal costIn = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalCostIn())
                                ?"0":saleCompositionModel.getTotalCostIn());
                        BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalSaleIn())
                                ?"0":saleCompositionModel.getTotalSaleIn());
                        saleCompositionModel.setTotalRate(sale.subtract(cost).toString());
                        saleCompositionModel.setTotalRateIn(saleIn.subtract(costIn).toString());
                    }
                }
            }
        }

        //计算汇总值
        if(null != dataList && dataList.size() > 0){
            for(SaleCompositionModel saleCompositionModel : dataList){
                SaleCompositionResponse saleCompositionResponse = new SaleCompositionResponse();
                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalSale())?"0"
                        :saleCompositionModel.getTotalSale());
                BigDecimal rate = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalRate())?"0"
                        :saleCompositionModel.getTotalRate());

                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalSaleIn())?"0"
                        :saleCompositionModel.getTotalSaleIn());
                BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(saleCompositionModel.getTotalRateIn())?"0"
                        :saleCompositionModel.getTotalRateIn());

                if(sale.compareTo(BigDecimal.ZERO) != 0){
                    saleCompositionResponse.setTotalRateProfit(rate.divide(sale,4,BigDecimal.ROUND_HALF_UP).toString());
                }
                if(saleIn.compareTo(BigDecimal.ZERO) != 0){
                    saleCompositionResponse.setTotalRateProfitIn(rateIn.divide(saleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                }
                saleCompositionResponse.setTotalSale(sale.toString());
                saleCompositionResponse.setTotalSaleIn(saleIn.toString());
                saleCompositionResponse.setType(saleCompositionModel.getType());
                saleCompositionResponseList.add(saleCompositionResponse);
            }
        }
        response.setList(saleCompositionResponseList);
        return response;
    }

    /**
     * 查询单品销售排名
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public ItemSaleRankListResponse queryItemSaleRankList(BaseSaleRequest param) throws Exception {
        ItemSaleRankListResponse itemSaleRankListResponse = new ItemSaleRankListResponse();
        List<ItemSaleRankResponse> dataList = new ArrayList<>();
        List<ItemRankModel> allList = new ArrayList<ItemRankModel>();
        List<ItemRankModel> monthList = new ArrayList<ItemRankModel>();
        List<ItemRankModel> compareList = new ArrayList<ItemRankModel>();
        List<String> deptList = new ArrayList<>();
        transform(param);
        Date start = param.getStart();
        String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
        if(DateUtils.getBetweenDay(start,new Date()) >= 0){
            allList = saleReportCsmbDao.queryCurSaleItemRank(param,start,null);
        }else{
            try{
                changeDgDataSource();
                allList = saleReportRmsDao.queryHisSaleItemRank(param,start,null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }


        if(null != allList && allList.size() > 0){
            String deptStr = getDepts(allList,deptList);
            String itemStr = getItems(allList);

            try{
                changeDgDataSource();
                //查询环比
                monthList = saleReportRmsDao.queryHisAndCompareRateItemRank(param,deptStr,itemStr,
                        DateUtils.addMonths(start,-1),null);
                //查询可比
                compareList = saleReportRmsDao.queryHisAndCompareRateItemRank(param,deptStr,itemStr,
                        DateUtil.toDate(time,"yyyy-MM-dd"),null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            //计算
            Map<String, List<ItemRankModel>> monthMap = null;
            if(null != monthList && monthList.size() > 0){
                monthMap= monthList.stream().collect(Collectors.groupingBy(ItemRankModel::getItemId));
            }

            Map<String, List<ItemRankModel>> compareMap = null;
            if(null != monthList && monthList.size() > 0){
                compareMap= compareList.stream().collect(Collectors.groupingBy(ItemRankModel::getItemId));
            }

            //翻译大类名称
            List<UserDept> userDeptList = reportUserDeptService.getAllDept();
            Map<Integer, List<UserDept>> userDeptMap = null;
            if(null != userDeptList && userDeptList.size() > 0){
                userDeptMap= userDeptList.stream().collect(Collectors.groupingBy(UserDept::getDeptId));
            }

            for(ItemRankModel itemRankModel : allList){
                ItemSaleRankResponse itemSaleRankResponse = new ItemSaleRankResponse();
                BigDecimal sale = new BigDecimal(StringUtils.isEmpty(itemRankModel.getTotalSale())?"0"
                        :itemRankModel.getTotalSale());
                BigDecimal compareSale_1 = new BigDecimal(StringUtils.isEmpty(itemRankModel.getTotalCompareSale())?"0"
                        :itemRankModel.getTotalCompareSale());

                BigDecimal saleIn = new BigDecimal(StringUtils.isEmpty(itemRankModel.getTotalSaleIn())?"0"
                        :itemRankModel.getTotalSaleIn());
                BigDecimal compareSale_1In = new BigDecimal(StringUtils.isEmpty(itemRankModel.getTotalCompareSaleIn())?"0"
                        :itemRankModel.getTotalCompareSaleIn());

                //计算环比
                if(null != monthMap){
                    if(monthMap.containsKey(itemRankModel.getItemId())){
                        ItemRankModel month = monthMap.get(itemRankModel.getItemId()).get(0);
                        if(itemRankModel.getDeptId().equals(month.getDeptId())){
                            BigDecimal monthSale = new BigDecimal(StringUtils.isEmpty(month.getTotalSale())?"0"
                                    :month.getTotalSale());
                            BigDecimal monthSaleIn = new BigDecimal(StringUtils.isEmpty(month.getTotalSaleIn())?"0"
                                    :month.getTotalSaleIn());
                            if(monthSale.compareTo(BigDecimal.ZERO) != 0){
                                itemSaleRankResponse.setMonthSaleProfit(sale.subtract(monthSale)
                                        .divide(monthSale,4,BigDecimal.ROUND_HALF_UP).toString());
                            }

                            if(monthSaleIn.compareTo(BigDecimal.ZERO) != 0){
                                itemSaleRankResponse.setMonthSaleProfitIn(saleIn.subtract(monthSaleIn)
                                        .divide(monthSaleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                    }
                }
                //计算可比
                if(null != compareMap){
                    if(compareMap.containsKey(itemRankModel.getItemId())){
                        ItemRankModel compare = compareMap.get(itemRankModel.getItemId()).get(0);
                        if(itemRankModel.getDeptId().equals(compare.getDeptId())){
                            BigDecimal compareSale = new BigDecimal(StringUtils.isEmpty(compare.getTotalSale())
                                    ?"0":compare.getTotalSale());
                            BigDecimal compareSaleIn = new BigDecimal(StringUtils.isEmpty(compare.getTotalSaleIn())
                                    ?"0":compare.getTotalSaleIn());
                            if(compareSale.compareTo(BigDecimal.ZERO) != 0){
                                itemSaleRankResponse.setCompareSaleProfit(compareSale_1.subtract(compareSale)
                                        .divide(compareSale,4,BigDecimal.ROUND_HALF_UP).toString());
                            }
                            if(compareSaleIn.compareTo(BigDecimal.ZERO) != 0){
                                itemSaleRankResponse.setCompareSaleProfitIn(compareSale_1In.subtract(compareSaleIn)
                                        .divide(compareSaleIn,4,BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                    }
                }
                //设置大类名称
                itemSaleRankResponse.setDeptId(itemRankModel.getDeptId());
                if(null != userDeptMap){
                    if(userDeptMap.containsKey(Integer.valueOf(itemRankModel.getDeptId()).intValue())){
                        itemSaleRankResponse.setDeptName(userDeptMap.get(Integer.valueOf(itemRankModel.getDeptId()).intValue()).get(0).getDeptName());
                    }
                }
                itemSaleRankResponse.setItemId(itemRankModel.getItemId());
                itemSaleRankResponse.setItemName(itemRankModel.getItemName());
                itemSaleRankResponse.setRn(itemRankModel.getRn());
                itemSaleRankResponse.setTotalSale(itemRankModel.getTotalSale());
                itemSaleRankResponse.setTotalSaleIn(itemRankModel.getTotalSaleIn());
                dataList.add(itemSaleRankResponse);
            }
        }
        deptList = deptList.stream().sorted(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                BigDecimal value1 = new BigDecimal(o1);
                BigDecimal value2 = new BigDecimal(o2);
                return value1.compareTo(value2);
            }
        }).collect(Collectors.toList());
        itemSaleRankListResponse.setItems(dataList);
        itemSaleRankListResponse.setDepts(deptList);
        return itemSaleRankListResponse;
    }

    /**
     * 查询单品毛利额排名
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public ItemRateRankListResponse queryItemRateRankList(BaseSaleRequest param) throws Exception {
        ItemRateRankListResponse itemRateRankListResponse = new ItemRateRankListResponse();
        List<ItemRateRankResponse> dataList = new ArrayList<>();
        List<String> deptList = new ArrayList<>();
        transform(param);
        Date start = param.getStart();
        String time = commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start));
        List<ItemRankModel> allList = new ArrayList<ItemRankModel>();
        List<ItemRankModel> monthList = new ArrayList<ItemRankModel>();
        List<ItemRankModel> compareList = new ArrayList<ItemRankModel>();
        if(DateUtils.getBetweenDay(start,new Date()) >= 0){
            allList = saleReportCsmbDao.queryCurRateItemRank(param,start,null);
            if(null != allList && allList.size() > 0){
                for(ItemRankModel itemRankModel : allList){
                    itemRankModel.setTotalRate(itemRankModel.getTotalScanningRate());
                    itemRankModel.setTotalCompareRate(itemRankModel.getTotalCompareScanningRate());
                    itemRankModel.setTotalRateIn(itemRankModel.getTotalScanningRateIn());
                    itemRankModel.setTotalCompareRateIn(itemRankModel.getTotalCompareScanningRateIn());
                }
                String deptStr = getDepts(allList,deptList);
                String itemStr = getItems(allList);

                /*try{
                    changeZyDataSource();
                    //查询环比
                    monthList = saleReportZyDao.queryHisItemRankModelForTime(param,deptStr,itemStr,
                            DateUtils.parseDateToStr("HH:mm:ss", new Date()),
                            DateUtils.addMonths(start,-1),null);
                    //查询可比
                    compareList = saleReportZyDao.queryHisItemRankModelForTime(param,deptStr,itemStr,
                            DateUtils.parseDateToStr("HH:mm:ss", new Date()),
                            DateUtils.addYears(start,-1),null);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }*/

                try{
                    changeDgDataSource();
                    //查询环比
                    monthList = saleReportRmsDao.queryHisAndCompareRateItemRank(param,deptStr,itemStr,
                            DateUtils.addMonths(start,-1),null);
                    //查询可比
                    compareList = saleReportRmsDao.queryHisAndCompareRateItemRank(param,deptStr,itemStr,
                            DateUtil.toDate(time,"yyyy-MM-dd"),null);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                if(null != monthList && monthList.size() > 0){
                    for(ItemRankModel itemRankModel : monthList){
                        itemRankModel.setTotalRate(itemRankModel.getTotalScanningRate());
                        itemRankModel.setTotalRateIn(itemRankModel.getTotalScanningRateIn());
                    }
                }

                if(null != compareList && compareList.size() > 0){
                    for(ItemRankModel itemRankModel : compareList){
                        itemRankModel.setTotalRate(itemRankModel.getTotalCompareScanningRate());
                        itemRankModel.setTotalRateIn(itemRankModel.getTotalCompareScanningRateIn());
                    }
                }
            }
        }else{
            try{
                changeDgDataSource();
                allList = saleReportRmsDao.queryHisRateItemRank(param,start,null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            if(null != allList && allList.size() > 0){
                for(ItemRankModel itemRankModel : allList){
                    itemRankModel.setTotalRate(itemRankModel.getTotalFrontDeskRate());
                    itemRankModel.setTotalCompareRate(itemRankModel.getTotalCompareFrontDeskRate());

                    itemRankModel.setTotalRateIn(itemRankModel.getTotalFrontDeskRateIn());
                    itemRankModel.setTotalCompareRateIn(itemRankModel.getTotalCompareFrontDeskRateIn());
                }
                String deptStr = getDepts(allList,deptList);
                String itemStr = getItems(allList);

                try{
                    changeDgDataSource();
                    //查询环比
                    monthList = saleReportRmsDao.queryHisAndCompareRateItemRank(param,deptStr,itemStr,
                            DateUtils.addMonths(start,-1),null);
                    //查询可比
                    compareList = saleReportRmsDao.queryHisAndCompareRateItemRank(param,deptStr,itemStr,
                            DateUtil.toDate(time,"yyyy-MM-dd"),null);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }

                if(null != monthList && monthList.size() > 0){
                    for(ItemRankModel itemRankModel : monthList){
                        itemRankModel.setTotalRate(itemRankModel.getTotalFrontDeskRate());
                        itemRankModel.setTotalRateIn(itemRankModel.getTotalFrontDeskRateIn());
                    }
                }

                if(null != compareList && compareList.size() > 0){
                    for(ItemRankModel itemRankModel : compareList){
                        itemRankModel.setTotalRate(itemRankModel.getTotalCompareFrontDeskRate());
                        itemRankModel.setTotalRateIn(itemRankModel.getTotalCompareFrontDeskRateIn());
                    }
                }
            }
        }
        //计算
        Map<String, List<ItemRankModel>> monthMap = null;
        if(null != monthList && monthList.size() > 0){
            monthMap= monthList.stream().collect(Collectors.groupingBy(ItemRankModel::getItemId));
        }

        Map<String, List<ItemRankModel>> compareMap = null;
        if(null != monthList && monthList.size() > 0){
            compareMap= compareList.stream().collect(Collectors.groupingBy(ItemRankModel::getItemId));
        }
        if(null != allList && allList.size() > 0){
            //翻译大类名称
            List<UserDept> userDeptList = reportUserDeptService.getAllDept();
            Map<Integer, List<UserDept>> userDeptMap = null;
            if(null != userDeptList && userDeptList.size() > 0){
                userDeptMap= userDeptList.stream().collect(Collectors.groupingBy(UserDept::getDeptId));
            }
            for(ItemRankModel itemRankModel : allList){
                ItemRateRankResponse itemRateRankResponset = new ItemRateRankResponse();

                BigDecimal rate = new BigDecimal(StringUtils.isEmpty(itemRankModel.getTotalRate())?"0"
                        :itemRankModel.getTotalRate());
                BigDecimal compareRate_1 = new BigDecimal(StringUtils.isEmpty(itemRankModel.getTotalCompareRate())?"0"
                        :itemRankModel.getTotalCompareRate());

                BigDecimal rateIn = new BigDecimal(StringUtils.isEmpty(itemRankModel.getTotalRateIn())?"0"
                        :itemRankModel.getTotalRateIn());
                BigDecimal compareRate_1In = new BigDecimal(StringUtils.isEmpty(itemRankModel.getTotalCompareRateIn())?"0"
                        :itemRankModel.getTotalCompareRateIn());

                //计算环比
                if(null != monthMap){
                    if(monthMap.containsKey(itemRankModel.getItemId())){
                        ItemRankModel month = monthMap.get(itemRankModel.getItemId()).get(0);
                        if(itemRankModel.getDeptId().equals(month.getDeptId())){
                            BigDecimal monthRate = new BigDecimal(StringUtils.isEmpty(month.getTotalRate())?"0"
                                    :month.getTotalRate());
                            if(monthRate.compareTo(BigDecimal.ZERO) != 0){
                                itemRateRankResponset.setMonthRateProfit(rate.subtract(monthRate)
                                        .divide(monthRate,4,BigDecimal.ROUND_HALF_UP).toString());
                            }

                            BigDecimal monthRateIn = new BigDecimal(StringUtils.isEmpty(month.getTotalRateIn())?"0"
                                    :month.getTotalRateIn());
                            if(monthRateIn.compareTo(BigDecimal.ZERO) != 0){
                                itemRateRankResponset.setMonthRateProfitIn(rateIn.subtract(monthRateIn)
                                        .divide(monthRateIn,4,BigDecimal.ROUND_HALF_UP).toString());

                            }
                        }
                    }
                }
                //计算可比
                if(null != compareMap){
                    if(compareMap.containsKey(itemRankModel.getItemId())){
                        ItemRankModel compare = compareMap.get(itemRankModel.getItemId()).get(0);
                        if(itemRankModel.getDeptId().equals(compare.getDeptId())){
                            BigDecimal compareRate = new BigDecimal(StringUtils.isEmpty(compare.getTotalRate())
                                    ?"0":compare.getTotalRate());
                            if(compareRate.compareTo(BigDecimal.ZERO) != 0){
                                itemRateRankResponset.setCompareRateProfit(compareRate_1.subtract(compareRate)
                                        .divide(compareRate,4,BigDecimal.ROUND_HALF_UP).toString());
                            }

                            BigDecimal compareRateIn = new BigDecimal(StringUtils.isEmpty(compare.getTotalRateIn())
                                    ?"0":compare.getTotalRateIn());
                            if(compareRateIn.compareTo(BigDecimal.ZERO) != 0){
                                itemRateRankResponset.setCompareRateProfitIn(compareRate_1In.subtract(compareRateIn)
                                        .divide(compareRateIn,4,BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                    }
                }
                //设置大类名称
                itemRateRankResponset.setDeptId(itemRankModel.getDeptId());
                if(null != userDeptMap){
                    if(userDeptMap.containsKey(Integer.valueOf(itemRankModel.getDeptId()).intValue())){
                        itemRateRankResponset.setDeptName(userDeptMap.get(Integer.valueOf(itemRankModel.getDeptId()).intValue()).get(0).getDeptName());
                    }
                }
                itemRateRankResponset.setItemId(itemRankModel.getItemId());
                itemRateRankResponset.setItemName(itemRankModel.getItemName());
                itemRateRankResponset.setRn(itemRankModel.getRn());
                itemRateRankResponset.setTotalRate(itemRankModel.getTotalRate());
                itemRateRankResponset.setTotalRateIn(itemRankModel.getTotalRateIn());
                dataList.add(itemRateRankResponset);
            }
        }
        deptList = deptList.stream().sorted(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                BigDecimal value1 = new BigDecimal(o1);
                BigDecimal value2 = new BigDecimal(o2);
                return value1.compareTo(value2);
            }
        }).collect(Collectors.toList());
        itemRateRankListResponse.setItems(dataList);
        itemRateRankListResponse.setDepts(deptList);
        return itemRateRankListResponse;
    }

    /**
     * 获取单品字符串
     * @param source
     * @return
     */
    private String getItems(List<ItemRankModel> source){
        String value = null;
        StringBuilder sb = new StringBuilder();
        for(ItemRankModel itemRankModel : source){
            sb.append("'").append(itemRankModel.getItemId()).append("',");
        }
        if(StringUtils.isNotEmpty(sb.toString())){
            value = sb.toString();
            value = value.substring(0,value.lastIndexOf(","));
        }
        return value;
    }

    /**
     * 获取大类(大类的字符串和列表)
     * @param source
     * @return
     */
    private String getDepts(List<ItemRankModel> source,List<String> target){
        String value = null;
        if(null != source && source.size() > 0){
            for(ItemRankModel itemRankModel : source){
                if(!target.contains(itemRankModel.getDeptId())){
                    target.add(itemRankModel.getDeptId());
                }
            }
            StringBuilder sb = new StringBuilder();
            for(String str : target){
                sb.append("'").append(str).append("',");
            }
            if(StringUtils.isNotEmpty(sb.toString())){
                value = sb.toString().substring(0,sb.toString().lastIndexOf(","));
            }
        }
        return value;
    }

    /**
     * 翻译大类名称
     * @param list
     * @param deptId
     * @throws Exception
     */
    public String getDeptName(List<UserDept> list,String deptId) throws Exception {
        for(UserDept userDept : list){
            if(Integer.valueOf(deptId).intValue() == userDept.getDeptId().intValue()){
                return userDept.getDeptName();
            }
        }
        return "";
    }

    private void transform(BaseSaleRequest request){
        String provinceId = request.getProvinceId();
        String areaId = request.getAreaId();
        String storeId = request.getStoreId();
        String deptId = request.getDeptId();
        String category = request.getCategory();
        String startDateStr = request.getStartDate();
        String endDateStr = request.getEndDate();

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
}
