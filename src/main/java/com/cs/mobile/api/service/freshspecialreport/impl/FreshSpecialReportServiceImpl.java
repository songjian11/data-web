package com.cs.mobile.api.service.freshspecialreport.impl;

import com.cs.mobile.api.dao.freshSpecialReport.FreshSpecialReportForDayDao;
import com.cs.mobile.api.dao.freshSpecialReport.FreshSpecialReportForMonthDao;
import com.cs.mobile.api.dao.freshreport.FreshReportCsmbDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.freshreport.OrganizationForFresh;
import com.cs.mobile.api.model.freshspecialreport.*;
import com.cs.mobile.api.model.freshspecialreport.request.FreshSpecialReportRequest;
import com.cs.mobile.api.model.freshspecialreport.response.*;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.service.common.CommonCalculateService;
import com.cs.mobile.api.service.freshspecialreport.FreshSpecialReportService;
import com.cs.mobile.common.core.text.Convert;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Slf4j
@Service
public class FreshSpecialReportServiceImpl implements FreshSpecialReportService {
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
    private CommonCalculateService calculateService;
    @Autowired
    private FreshSpecialReportForDayDao freshSpecialReportForDayDao;
    @Autowired
    private FreshSpecialReportForMonthDao freshSpecialReportForMonthDao;
    @Autowired
    private FreshReportCsmbDao freshReportCsmbDao;

    /**
     * 查询专题报表页
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshSpecialReportResponse queryFreshSpecialReport(FreshSpecialReportRequest param) throws Exception {
        FreshSpecialReportResponse freshSpecialReportResponse = new FreshSpecialReportResponse();
        transform(param);
        //是否查询月（0-否，1-是）
        int isMonth = Integer.valueOf(param.getIsMonth()).intValue();
        if(StringUtils.isNotEmpty(param.getClassId())){//查询中类加小类列表
            if(0 == isMonth){//查询日报表
                freshSpecialReportResponse = queryClassAndSubClassForDay(param);
            }else if(1 == isMonth){//查询月报表
                freshSpecialReportResponse = queryClassAndSubClassForMonth(param);
            }
        }else if(StringUtils.isNotEmpty(param.getDeptId())){//查询大类加中类列表
            if(0 == isMonth){//查询日报表
                freshSpecialReportResponse = queryDeptAndClassForDay(param);
            }else if(1 == isMonth){//查询月报表
                freshSpecialReportResponse = queryDeptAndClassForMonth(param);
            }
        }
        return freshSpecialReportResponse;
    }

    /**
     * 查询区域生鲜专题报表趋势
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshSpecialReportTrendListResponse queryAreaFreshSpecialReportTrend(FreshSpecialReportRequest param) throws Exception {
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = new FreshSpecialReportTrendListResponse();
        transform(param);
        //是否查询月（0-否，1-是）
        int isMonth = Integer.valueOf(param.getIsMonth()).intValue();
        if(0 == isMonth){//查询日趋势
            freshSpecialReportTrendListResponse = queryAreaFreshSpecialReportTrendForDay(param);
        }else if(1 == isMonth){//查询月趋势
            freshSpecialReportTrendListResponse = queryAreaFreshSpecialReportTrendForMonth(param);
        }
        return freshSpecialReportTrendListResponse;
    }


    /**
     * 查询门店生鲜专题报表趋势
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshSpecialReportTrendListResponse queryStoreFreshSpecialReportTrend(FreshSpecialReportRequest param) throws Exception {
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = new FreshSpecialReportTrendListResponse();
        transform(param);
        //是否查询月（0-否，1-是）
        int isMonth = Integer.valueOf(param.getIsMonth()).intValue();
        if(0 == isMonth){//查询天趋势
            freshSpecialReportTrendListResponse = queryStoreFreshSpecialReportTrendForDay(param);
        }else if(1 == isMonth){//查询月趋势
            freshSpecialReportTrendListResponse = queryStoreFreshSpecialReportTrendForMonth(param);
        }
        return freshSpecialReportTrendListResponse;
    }

    /**
     * 查询商品生鲜专题报表趋势
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshSpecialReportTrendListResponse queryShopFreshSpecialReportTrend(FreshSpecialReportRequest param) throws Exception {
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = new FreshSpecialReportTrendListResponse();
        transform(param);
        //是否查询月（0-否，1-是）
        int isMonth = Integer.valueOf(param.getIsMonth()).intValue();
        if(0 == isMonth){//查询天趋势
            freshSpecialReportTrendListResponse = queryShopFreshSpecialReportTrendForDay(param);
        }else if(1 == isMonth){//查询月趋势
            freshSpecialReportTrendListResponse = queryShopFreshSpecialReportTrendForMonth(param);
        }
        return freshSpecialReportTrendListResponse;
    }

    /**
     * 查询区域生鲜专题排行榜
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshSpecialReportRankListResponse queryAreaFreshSpecialReportRank(FreshSpecialReportRequest param) throws Exception {
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = new FreshSpecialReportRankListResponse();
        transform(param);
        //是否查询月（0-否，1-是）
        int isMonth = Integer.valueOf(param.getIsMonth()).intValue();
        if(0 == isMonth){//查询日排行榜
            freshSpecialReportRankListResponse = queryAreaFreshSpecialReportRankForDay(param);
        }else if(1 == isMonth){//查询月排行榜
            freshSpecialReportRankListResponse = queryAreaFreshSpecialReportRankForMonth(param);
        }
        return freshSpecialReportRankListResponse;
    }

    /**
     * 查询门店生鲜专题排行榜
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshSpecialReportRankListResponse queryStoreFreshSpecialReportRank(FreshSpecialReportRequest param) throws Exception {
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = new FreshSpecialReportRankListResponse();
        transform(param);
        //是否查询月（0-否，1-是）
        int isMonth = Integer.valueOf(param.getIsMonth()).intValue();
        if(0 == isMonth){//查询日排行榜
            freshSpecialReportRankListResponse = queryStoreFreshSpecialReportRankForDay(param);
        }else if(1 == isMonth){//查询月排行榜
            freshSpecialReportRankListResponse = queryStoreFreshSpecialReportRankForMonth(param);
        }
        return freshSpecialReportRankListResponse;
    }

    /**
     * 查询商品生鲜专题排行榜
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshSpecialReportRankListResponse queryShopFreshSpecialReportRank(FreshSpecialReportRequest param) throws Exception {
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = new FreshSpecialReportRankListResponse();
        transform(param);
        //是否查询月（0-否，1-是）
        int isMonth = Integer.valueOf(param.getIsMonth()).intValue();
        if(0 == isMonth){//查询日排行榜
            freshSpecialReportRankListResponse = queryShopFreshSpecialReportRankForDay(param);
        }else if(1 == isMonth){//查询月排行榜
            freshSpecialReportRankListResponse = queryShopFreshSpecialReportRankForMonth(param);
        }
        return freshSpecialReportRankListResponse;
    }

    /**
     * 查询区域日排行榜
     * @param param
     * @return
     */
    private FreshSpecialReportRankListResponse queryAreaFreshSpecialReportRankForDay(FreshSpecialReportRequest param){
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = new FreshSpecialReportRankListResponse();
        List<FreshSpecialReportRankResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        List<FreshSaleAndRateModel> his = null;
        List<String> regionList = new ArrayList<>();
        FreshAllKlModel allKl = null;
        Date start = param.getStart();
        //查询实时数据
        current = freshSpecialReportForDayDao.queryRegionCurrentFreshSaleAndRate(param, start);
        //获取实时ID值转换为List
        if(null != current && current.size() > 0){
            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                regionList.add(freshSaleAndRateModel.getId());
            }
            //查询总的客流
            allKl = freshSpecialReportForDayDao.queryFreshAllKl(param, start);
            //查询区域日排行榜
            if(StringUtils.isNotEmpty(param.getSubClassId())){//根据小类查询区域同期数据
                try{
                    changeDgDataSource();
                    his = freshSpecialReportForDayDao.queryRegionSubClassHisFreshSaleAndRate(param, DateUtils.addYears(start,-1), regionList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else if(StringUtils.isNotEmpty(param.getClassId())){//根据中类查询区域同期数据
                try{
                    changeDgDataSource();
                    his = freshSpecialReportForDayDao.queryRegionClassHisFreshSaleAndRate(param, DateUtils.addYears(start,-1), regionList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else if(StringUtils.isNotEmpty(param.getDeptId())){//根据大类查询区域同期数据
                try{
                    changeDgDataSource();
                    his = freshSpecialReportForDayDao.queryRegionDeptHisFreshSaleAndRate(param, DateUtils.addYears(start,-1), regionList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }

            //组装数据
            /*Map<String, FreshAllKlModel> klMap = null;
            if(null != allKlList && allKlList.size() > 0){
                klMap = allKlList.stream().collect(Collectors.toMap(FreshAllKlModel::getId,Function.identity(), (key1, key2) -> key2));
            }*/

            Map<String, FreshSaleAndRateModel> hisMap = null;
            if(null != his && his.size() > 0){
                hisMap = his.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,Function.identity(), (key1, key2) -> key2));
            }

            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                FreshSpecialReportRankResponse freshSpecialReportRankResponse = new FreshSpecialReportRankResponse();
                freshSpecialReportRankResponse.setName(freshSaleAndRateModel.getName());
                //销售
                freshSpecialReportRankResponse.setSale(freshSaleAndRateModel.getSale());
                freshSpecialReportRankResponse.setSaleIn(freshSaleAndRateModel.getSaleIn());
                //毛利率
                freshSpecialReportRankResponse.setProfit(calculateService.calculateProfit(freshSaleAndRateModel.getSale(),
                        freshSaleAndRateModel.getRate()));
                freshSpecialReportRankResponse.setProfitIn(calculateService.calculateProfit(freshSaleAndRateModel.getSaleIn(),
                        freshSaleAndRateModel.getRateIn()));
                /*if(null != klMap && klMap.size() > 0){//渗透率
                    if(klMap.containsKey(freshSaleAndRateModel.getId())){
                        FreshAllKlModel kl = klMap.get(freshSaleAndRateModel.getId());
                        freshSpecialReportRankResponse.setPermeability(calculateService.calculatePermeability(freshSaleAndRateModel.getKl(),
                                kl.getKl()));
                    }
                }*/
                if(null != allKl){
                    freshSpecialReportRankResponse.setPermeability(calculateService.calculatePermeability(freshSaleAndRateModel.getKl(),
                            allKl.getKl()));
                }
                if(null != hisMap && hisMap.size() > 0){
                    if(hisMap.containsKey(freshSaleAndRateModel.getId())){
                        FreshSaleAndRateModel hisFresh = hisMap.get(freshSaleAndRateModel.getId());
                        //销售金额可比率
                        freshSpecialReportRankResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(freshSaleAndRateModel.getCompareSale(),
                                hisFresh.getCompareSale()));
                        freshSpecialReportRankResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(freshSaleAndRateModel.getCompareSaleIn(),
                                hisFresh.getCompareSaleIn()));
                    }
                }
                list.add(freshSpecialReportRankResponse);
            }
        }
        freshSpecialReportRankListResponse.setList(list);
        return freshSpecialReportRankListResponse;
    }

    /**
     * 查询区域月排行榜
     * @param param
     * @return
     */
    private FreshSpecialReportRankListResponse queryAreaFreshSpecialReportRankForMonth(FreshSpecialReportRequest param){
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = new FreshSpecialReportRankListResponse();
        List<FreshSpecialReportRankResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        List<FreshSaleAndRateModel> his = null;
        FreshAllKlModel allKl = null;
        List<FreshAllKlModel> klList = null;
        List<String> regionList = new ArrayList<>();
        Date start = param.getStart();
        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        try{
            changeZyDataSource();
            //查询月初至昨日总客流
            allKl = freshSpecialReportForMonthDao.queryFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){//查询小类数据
            try{
                changeZyDataSource();
                //查询小类月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryAreaSubClassFreshAllKlList(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    regionList.add(freshAllKlModel.getId());
                }
                try{
                    changeDgDataSource();
                    //查询小类月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryAreaSubClassMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),regionList);
                    //查询小类月初至昨日的同期销售额和毛利额数据
                    his = freshSpecialReportForMonthDao.queryAreaSubClassSameMonthFreshSaleAndRateList(param,
                            DateUtils.addYears(DateUtil.getFirstDayForMonth(start),-1),
                            DateUtils.addYears(DateUtils.addDays(start,-1),-1),regionList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else if(StringUtils.isNotEmpty(param.getClassId())){//根据中类趋势
            try{
                changeZyDataSource();
                //查询中类月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryAreaClassFreshAllKl(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    regionList.add(freshAllKlModel.getId());
                }

                try{
                    changeDgDataSource();
                    //查询中类月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryAreaClassMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),regionList);
                    //查询中类月初至昨日的同期销售额和毛利额数据
                    his = freshSpecialReportForMonthDao.queryAreaClassSameMonthFreshSaleAndRateList(param,
                            DateUtils.addYears(DateUtil.getFirstDayForMonth(start),-1),
                            DateUtils.addYears(DateUtils.addDays(start,-1),-1),regionList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else if(StringUtils.isNotEmpty(param.getDeptId())){//跟据大类查询区域数据
            try{
                changeZyDataSource();
                //查询大类月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryAreaDeptFreshAllKl(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    regionList.add(freshAllKlModel.getId());
                }

                try{
                    changeDgDataSource();
                    //查询大类月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryAreaDeptMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),regionList);
                    //查询大类月初至昨日的同期销售额和毛利额数据
                    his = freshSpecialReportForMonthDao.queryAreaDeptSameMonthFreshSaleAndRateList(param,
                            DateUtils.addYears(DateUtil.getFirstDayForMonth(start),-1),
                            DateUtils.addYears(DateUtils.addDays(start,-1),-1),regionList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }

        //计算值
        if(null != klList && klList.size() > 0){
            Map<String, FreshSaleAndRateModel> currentMap = null;
            Map<String, FreshSaleAndRateModel> hisMap = null;
            if(null != current && current.size() > 0){
                currentMap = current.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            if(null != his && his.size() > 0){
                hisMap = his.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }

            for(FreshAllKlModel kl : klList){
                FreshSpecialReportRankResponse freshSpecialReportRankResponse = new FreshSpecialReportRankResponse();
                freshSpecialReportRankResponse.setName(getNameById(kl.getId(),orgList));
                if(null != allKl){//计算渗透率
                    freshSpecialReportRankResponse.setPermeability(calculateService.calculatePermeability(
                            kl.getKl(),
                            allKl.getKl()));
                }
                if(null != currentMap && currentMap.size() > 0){
                    if(currentMap.containsKey(kl.getId())){
                        FreshSaleAndRateModel freshSaleAndRateModel = currentMap.get(kl.getId());
                        //销售
                        freshSpecialReportRankResponse.setSale(freshSaleAndRateModel.getSale());
                        freshSpecialReportRankResponse.setSaleIn(freshSaleAndRateModel.getSaleIn());
                        //毛利率
                        freshSpecialReportRankResponse.setProfit(calculateService.calculateProfit(freshSaleAndRateModel.getSale(),
                                freshSaleAndRateModel.getRate()));
                        freshSpecialReportRankResponse.setProfitIn(calculateService.calculateProfit(freshSaleAndRateModel.getSaleIn(),
                                freshSaleAndRateModel.getRateIn()));

                        if(null != hisMap && hisMap.size() > 0){
                            if(hisMap.containsKey(kl.getId())){
                                FreshSaleAndRateModel hisFreshSaleAndRateModel = hisMap.get(kl.getId());
                                //销售可比率
                                freshSpecialReportRankResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                                        freshSaleAndRateModel.getCompareSale(),
                                        hisFreshSaleAndRateModel.getCompareSale()
                                ));
                                freshSpecialReportRankResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                                        freshSaleAndRateModel.getCompareSaleIn(),
                                        hisFreshSaleAndRateModel.getCompareSaleIn()
                                ));
                            }
                        }
                    }
                }
                list.add(freshSpecialReportRankResponse);
            }
        }
        freshSpecialReportRankListResponse.setList(list);
        return freshSpecialReportRankListResponse;
    }

    /**
     * 查询门店生鲜专题日排行榜
     * @param param
     * @return
     */
    private FreshSpecialReportRankListResponse queryStoreFreshSpecialReportRankForDay(FreshSpecialReportRequest param){
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = new FreshSpecialReportRankListResponse();
        List<FreshSpecialReportRankResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        List<FreshSaleAndRateModel> his = null;
        List<String> storeList = new ArrayList<>();
        FreshAllKlModel allKl = null;
        Date start = param.getStart();
        //查询实时数据
        current = freshSpecialReportForDayDao.queryStoreCurrentFreshSaleAndRate(param, start);
        //获取实时ID值转换为List
        if(null != current && current.size() > 0){
            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                storeList.add(freshSaleAndRateModel.getId());
            }
            //查询总的客流
            allKl = freshSpecialReportForDayDao.queryFreshAllKl(param, start);
            //查询区域日排行榜
            if(StringUtils.isNotEmpty(param.getSubClassId())){//根据小类查询区域同期数据
                try{
                    changeDgDataSource();
                    his = freshSpecialReportForDayDao.queryStoreSubClassHisFreshSaleAndRate(param, DateUtils.addYears(start,-1), storeList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else if(StringUtils.isNotEmpty(param.getClassId())){//根据中类查询区域同期数据
                try{
                    changeDgDataSource();
                    his = freshSpecialReportForDayDao.queryStoreClassHisFreshSaleAndRate(param, DateUtils.addYears(start,-1), storeList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }else if(StringUtils.isNotEmpty(param.getDeptId())){//根据大类查询区域同期数据
                try{
                    changeDgDataSource();
                    his = freshSpecialReportForDayDao.queryStoreDeptHisFreshSaleAndRate(param, DateUtils.addYears(start,-1), storeList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }

            //组装数据
            /*Map<String, FreshAllKlModel> klMap = null;
            if(null != allKlList && allKlList.size() > 0){
                klMap = allKlList.stream().collect(Collectors.toMap(FreshAllKlModel::getId,Function.identity(), (key1, key2) -> key2));
            }*/

            Map<String, FreshSaleAndRateModel> hisMap = null;
            if(null != his && his.size() > 0){
                hisMap = his.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,Function.identity(), (key1, key2) -> key2));
            }

            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                FreshSpecialReportRankResponse freshSpecialReportRankResponse = new FreshSpecialReportRankResponse();
                freshSpecialReportRankResponse.setName(freshSaleAndRateModel.getName());
                //销售
                freshSpecialReportRankResponse.setSale(freshSaleAndRateModel.getSale());
                freshSpecialReportRankResponse.setSaleIn(freshSaleAndRateModel.getSaleIn());
                //毛利率
                freshSpecialReportRankResponse.setProfit(calculateService.calculateProfit(freshSaleAndRateModel.getSale(),
                        freshSaleAndRateModel.getRate()));
                freshSpecialReportRankResponse.setProfitIn(calculateService.calculateProfit(freshSaleAndRateModel.getSaleIn(),
                        freshSaleAndRateModel.getRateIn()));
                /*if(null != klMap && klMap.size() > 0){//渗透率
                    if(klMap.containsKey(freshSaleAndRateModel.getId())){
                        FreshAllKlModel kl = klMap.get(freshSaleAndRateModel.getId());
                        freshSpecialReportRankResponse.setPermeability(calculateService.calculatePermeability(freshSaleAndRateModel.getKl(),
                                kl.getKl()));
                    }
                }*/
                if(null != allKl){
                    freshSpecialReportRankResponse.setPermeability(calculateService.calculatePermeability(freshSaleAndRateModel.getKl(),
                            allKl.getKl()));
                }
                if(null != hisMap && hisMap.size() > 0){//销售金额可比率
                    if(hisMap.containsKey(freshSaleAndRateModel.getId())){
                        FreshSaleAndRateModel hisFresh = hisMap.get(freshSaleAndRateModel.getId());
                        log.info("===========================hisFresh:" + hisFresh.toString());
                        log.info("===========================freshSaleAndRateModel:" + freshSaleAndRateModel.toString());
                        freshSpecialReportRankResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(freshSaleAndRateModel.getCompareSale(),
                                hisFresh.getCompareSale()));
                        freshSpecialReportRankResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(freshSaleAndRateModel.getCompareSaleIn(),
                                hisFresh.getCompareSaleIn()));
                    }
                }
                list.add(freshSpecialReportRankResponse);
            }
        }
        freshSpecialReportRankListResponse.setList(list);
        return freshSpecialReportRankListResponse;
    }

    /**
     * 查询门店生鲜专题月排行榜
     * @param param
     * @return
     */
    private FreshSpecialReportRankListResponse queryStoreFreshSpecialReportRankForMonth(FreshSpecialReportRequest param){
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = new FreshSpecialReportRankListResponse();
        List<FreshSpecialReportRankResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        List<FreshSaleAndRateModel> his = null;
        FreshAllKlModel allKl = null;
        List<FreshAllKlModel> klList = null;
        List<String> storeList = new ArrayList<>();
        Date start = param.getStart();
        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();

        try{
            changeZyDataSource();
            //查询月初至昨日总客流
            allKl = freshSpecialReportForMonthDao.queryFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(StringUtils.isNotEmpty(param.getSubClassId())){//查询小类门店趋势
            try{
                changeZyDataSource();
                //查询小类门店月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryStoreSubClassFreshAllKlList(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    storeList.add(freshAllKlModel.getId());
                }

                try{
                    changeDgDataSource();
                    //查询小类门店月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryStoreSubClassMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),storeList);
                    //查询小类门店月初至昨日的同期销售额和毛利额数据
                    his = freshSpecialReportForMonthDao.queryStoreSubClassSameMonthFreshSaleAndRateList(param,
                            DateUtils.addYears(DateUtil.getFirstDayForMonth(start),-1),
                            DateUtils.addYears(DateUtils.addDays(start,-1),-1),storeList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else if(StringUtils.isNotEmpty(param.getClassId())){//根据中类门店趋势
            try{
                changeZyDataSource();
                //查询中类门店月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryStoreClassFreshAllKl(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    storeList.add(freshAllKlModel.getId());
                }

                try{
                    changeDgDataSource();
                    //查询中类门店月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryStoreClassMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),storeList);
                    //查询中类门店月初至昨日的同期销售额和毛利额数据
                    his = freshSpecialReportForMonthDao.queryStoreClassSameMonthFreshSaleAndRateList(param,
                            DateUtils.addYears(DateUtil.getFirstDayForMonth(start),-1),
                            DateUtils.addYears(DateUtils.addDays(start,-1),-1),storeList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else if(StringUtils.isNotEmpty(param.getDeptId())){//根据大类门店趋势
            try{
                changeZyDataSource();
                //查询大类门店月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryStoreDeptFreshAllKl(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    storeList.add(freshAllKlModel.getId());
                }

                try{
                    changeDgDataSource();
                    //查询大类门店月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryStoreDeptMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),storeList);
                    //查询大类门店月初至昨日的同期销售额和毛利额数据
                    his = freshSpecialReportForMonthDao.queryStoreDeptSameMonthFreshSaleAndRateList(param,
                            DateUtils.addYears(DateUtil.getFirstDayForMonth(start),-1),
                            DateUtils.addYears(DateUtils.addDays(start,-1),-1),storeList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }

        //计算值
        if(null != klList && klList.size() > 0){
            Map<String, FreshSaleAndRateModel> currentMap = null;
            Map<String, FreshSaleAndRateModel> hisMap = null;
            if(null != current && current.size() > 0){
                currentMap = current.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            if(null != his && his.size() > 0){
                hisMap = his.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }

            for(FreshAllKlModel kl : klList){
                FreshSpecialReportRankResponse freshSpecialReportRankResponse = new FreshSpecialReportRankResponse();
                freshSpecialReportRankResponse.setName(getNameById(kl.getId(),orgList));
                if(null != allKl){//计算渗透率
                    freshSpecialReportRankResponse.setPermeability(calculateService.calculatePermeability(
                            kl.getKl(),
                            allKl.getKl()));
                }
                if(null != currentMap && currentMap.size() > 0){
                    if(currentMap.containsKey(kl.getId())){
                        FreshSaleAndRateModel freshSaleAndRateModel = currentMap.get(kl.getId());
                        //销售
                        freshSpecialReportRankResponse.setSale(freshSaleAndRateModel.getSale());
                        freshSpecialReportRankResponse.setSaleIn(freshSaleAndRateModel.getSaleIn());
                        //毛利率
                        freshSpecialReportRankResponse.setProfit(calculateService.calculateProfit(freshSaleAndRateModel.getSale(),
                                freshSaleAndRateModel.getRate()));
                        freshSpecialReportRankResponse.setProfitIn(calculateService.calculateProfit(freshSaleAndRateModel.getSaleIn(),
                                freshSaleAndRateModel.getRateIn()));

                        if(null != hisMap && hisMap.size() > 0){
                            if(hisMap.containsKey(kl.getId())){
                                FreshSaleAndRateModel hisFreshSaleAndRateModel = hisMap.get(kl.getId());
                                //销售可比率
                                freshSpecialReportRankResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                                        freshSaleAndRateModel.getCompareSale(),
                                        hisFreshSaleAndRateModel.getCompareSale()
                                ));
                                freshSpecialReportRankResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                                        freshSaleAndRateModel.getCompareSaleIn(),
                                        hisFreshSaleAndRateModel.getCompareSaleIn()
                                ));
                            }
                        }
                    }
                }
                list.add(freshSpecialReportRankResponse);
            }
        }
        freshSpecialReportRankListResponse.setList(list);
        return freshSpecialReportRankListResponse;
    }

    /**
     * 查询商品日排行榜
     * @param param
     * @return
     */
    private FreshSpecialReportRankListResponse queryShopFreshSpecialReportRankForDay(FreshSpecialReportRequest param){
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = new FreshSpecialReportRankListResponse();
        List<FreshSpecialReportRankResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        List<FreshSaleAndRateModel> yesterday = null;
        List<OrderAndStockAmountModel> stocks = null;
        List<String> itemList = new ArrayList<>();
        FreshAllKlModel allKl = null;
        Date start = param.getStart();
        //查询实时数据
        current = freshSpecialReportForDayDao.queryItemCurrentFreshSaleAndRate(param, start);
        //获取实时ID值转换为List
        if(null != current && current.size() > 0){
            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                itemList.add(freshSaleAndRateModel.getId());
            }
            //查询总的客流
            allKl = freshSpecialReportForDayDao.queryFreshAllKl(param, start);
            //查询昨日数据
            try{
                changeDgDataSource();
                yesterday = freshSpecialReportForDayDao.queryItemYesterdayFreshSaleAndRate(param, DateUtils.addDays(start,-1), itemList);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //查询订货量，库存金额
            try{
                changeZyDataSource();
                stocks = freshSpecialReportForDayDao.queryItemOrderAndStockAmount(param,
                        DateUtils.addDays(start,-1), itemList);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //组装数据
            Map<String, FreshSaleAndRateModel> yesterdayMap = null;
            if(null != yesterday && yesterday.size() > 0){
                yesterdayMap = yesterday.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,Function.identity(), (key1, key2) -> key2));
            }
            Map<String, OrderAndStockAmountModel> stockMap = null;
            if(null != stocks && stocks.size() > 0){
                stockMap = stocks.stream().collect(Collectors.toMap(OrderAndStockAmountModel::getId,Function.identity(), (key1, key2) -> key2));
            }
            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                FreshSpecialReportRankResponse freshSpecialReportRankResponse = new FreshSpecialReportRankResponse();
                freshSpecialReportRankResponse.setName(freshSaleAndRateModel.getName());
                //销售
                freshSpecialReportRankResponse.setSale(freshSaleAndRateModel.getSale());
                freshSpecialReportRankResponse.setSaleIn(freshSaleAndRateModel.getSaleIn());
                //毛利率
                freshSpecialReportRankResponse.setProfit(calculateService.calculateProfit(freshSaleAndRateModel.getSale(),
                        freshSaleAndRateModel.getRate()));
                freshSpecialReportRankResponse.setProfitIn(calculateService.calculateProfit(freshSaleAndRateModel.getSaleIn(),
                        freshSaleAndRateModel.getRateIn()));
                if(null != allKl){//渗透率
                    freshSpecialReportRankResponse.setPermeability(calculateService.calculatePermeability(freshSaleAndRateModel.getKl(),
                            allKl.getKl()));
                }
                if(null != yesterdayMap && yesterdayMap.size() > 0){//昨日销售额
                    if(yesterdayMap.containsKey(freshSaleAndRateModel.getId())){
                        FreshSaleAndRateModel yesterdayFresh = yesterdayMap.get(freshSaleAndRateModel.getId());
                        freshSpecialReportRankResponse.setYesterdaySale(yesterdayFresh.getSale());
                        freshSpecialReportRankResponse.setYesterdaySaleIn(yesterdayFresh.getSaleIn());
                    }
                }
                if(null != stockMap && stockMap.size() > 0){//库存数量,订货量，库存金额
                    if(stockMap.containsKey(freshSaleAndRateModel.getId())){
                        OrderAndStockAmountModel orderAndStockAmountModel = stockMap.get(freshSaleAndRateModel.getId());
                        freshSpecialReportRankResponse.setStock(orderAndStockAmountModel.getStock());
                        freshSpecialReportRankResponse.setOrderNum(orderAndStockAmountModel.getOrderNum());
                        freshSpecialReportRankResponse.setStockAmount(orderAndStockAmountModel.getStockAmount());
                        freshSpecialReportRankResponse.setOrderAmount(orderAndStockAmountModel.getOrderAmount());
                        freshSpecialReportRankResponse.setOrderAmountIn(orderAndStockAmountModel.getOrderAmountIn());
                    }
                }
                list.add(freshSpecialReportRankResponse);
            }
        }
        freshSpecialReportRankListResponse.setList(list);
        return freshSpecialReportRankListResponse;
    }

    /**
     * 查询商品月排行榜
     * @param param
     * @return
     */
    private FreshSpecialReportRankListResponse queryShopFreshSpecialReportRankForMonth(FreshSpecialReportRequest param){
        FreshSpecialReportRankListResponse freshSpecialReportRankListResponse = new FreshSpecialReportRankListResponse();
        List<FreshSpecialReportRankResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        List<OrderAndStockAmountModel> stocks = null;
        FreshAllKlModel allKl = null;
        List<FreshAllKlModel> klList = null;
        List<String> itemList = new ArrayList<>();
        Date start = param.getStart();
        int haveKlTable = 0;
        int haveSaleTable = 0;
        try{
            changeZyDataSource();
            //判断商品客流表是否存在
            haveKlTable = freshSpecialReportForMonthDao.queryIsHaveItemKlTable();
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        try{
            changeDgDataSource();
            //判断商品销售表是否存在
            haveSaleTable = freshSpecialReportForMonthDao.queryIsHaveItemSaleTable();
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(0 == haveKlTable || 0 == haveSaleTable){
            freshSpecialReportRankListResponse.setList(list);
            return freshSpecialReportRankListResponse;
        }

        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        try{
            changeZyDataSource();
            //查询月初至昨日总客流
            allKl = freshSpecialReportForMonthDao.queryFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //查询订货量，库存金额
        try{
            changeZyDataSource();
            stocks = freshSpecialReportForDayDao.queryItemOrderAndStockAmount(param,
                    DateUtils.addDays(start,-1), itemList);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        try{
            changeZyDataSource();
            //查询商品月初至昨日客流
            klList = freshSpecialReportForMonthDao.queryItemFreshAllKlList(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(null != klList && klList.size() > 0){
            for(FreshAllKlModel kl : klList){
                itemList.add(kl.getId());
            }

            try{
                changeDgDataSource();
                //查询商品月初至昨日销售额和毛利额
                current = freshSpecialReportForMonthDao.queryItemMonthFreshSaleAndRateList(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1),itemList);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }

        //计算值
        if(null != klList && klList.size() > 0){
            Map<String, FreshSaleAndRateModel> map = null;
            Map<String, OrderAndStockAmountModel> stockMap = null;
            if(null != current && current.size() > 0){
                map = current.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            if(null != stocks && stocks.size() > 0){
                stockMap = stocks.stream().collect(Collectors.toMap(OrderAndStockAmountModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }

            for(FreshAllKlModel kl : klList){
                FreshSpecialReportRankResponse freshSpecialReportRankResponse = new FreshSpecialReportRankResponse();
                freshSpecialReportRankResponse.setName(kl.getName());
                if(null != allKl){//计算渗透率
                    freshSpecialReportRankResponse.setPermeability(calculateService.calculatePermeability(
                            kl.getKl(),
                            allKl.getKl()));
                }
                if(null != map && map.size() > 0){
                    if(map.containsKey(kl.getId())){//获取毛利额
                        FreshSaleAndRateModel freshSaleAndRateModel = map.get(kl.getId());
                        //销售
                        freshSpecialReportRankResponse.setSale(freshSaleAndRateModel.getSale());
                        freshSpecialReportRankResponse.setSaleIn(freshSaleAndRateModel.getSaleIn());
                        //毛利率
                        freshSpecialReportRankResponse.setProfit(calculateService.calculateProfit(
                                freshSaleAndRateModel.getSale(),
                                freshSaleAndRateModel.getRate()
                        ));
                        freshSpecialReportRankResponse.setProfitIn(calculateService.calculateProfit(
                                freshSaleAndRateModel.getSaleIn(),
                                freshSaleAndRateModel.getRateIn()
                        ));

                        //月至昨日销售额
                        freshSpecialReportRankResponse.setMonthSale(freshSaleAndRateModel.getSale());
                        freshSpecialReportRankResponse.setMonthSaleIn(freshSaleAndRateModel.getSaleIn());
                    }
                }

                if(null != stockMap && stockMap.size() > 0){//库存数量,订货量，库存金额
                    if(stockMap.containsKey(kl.getId())){
                        OrderAndStockAmountModel orderAndStockAmountModel = stockMap.get(kl.getId());
                        freshSpecialReportRankResponse.setStock(orderAndStockAmountModel.getStock());
                        freshSpecialReportRankResponse.setOrderNum(orderAndStockAmountModel.getOrderNum());
                        freshSpecialReportRankResponse.setStockAmount(orderAndStockAmountModel.getStockAmount());
                        freshSpecialReportRankResponse.setOrderAmount(orderAndStockAmountModel.getOrderAmount());
                        freshSpecialReportRankResponse.setOrderAmountIn(orderAndStockAmountModel.getOrderAmountIn());
                    }
                }
                list.add(freshSpecialReportRankResponse);
            }
        }
        freshSpecialReportRankListResponse.setList(list);
        return freshSpecialReportRankListResponse;
    }

    /**
     * 查询商品生鲜日趋势
     * @param param
     * @return
     */
    private FreshSpecialReportTrendListResponse queryShopFreshSpecialReportTrendForDay(FreshSpecialReportRequest param){
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = new FreshSpecialReportTrendListResponse();
        List<FreshSpecialReportTrendResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        FreshAllKlModel allKl = null;
        Date start = param.getStart();
        //查询实时数据
        current = freshSpecialReportForDayDao.queryItemCurrentFreshSaleAndRate(param, start);
        allKl = freshSpecialReportForDayDao.queryFreshAllKl(param, start);
        if(null != current && current.size() > 0){
            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                FreshSpecialReportTrendResponse freshSpecialReportTrendResponse = new FreshSpecialReportTrendResponse();
                freshSpecialReportTrendResponse.setName(freshSaleAndRateModel.getName());
                freshSpecialReportTrendResponse.setRate(freshSaleAndRateModel.getRate());
                freshSpecialReportTrendResponse.setRateIn(freshSaleAndRateModel.getRateIn());
                if(null != allKl){
                    freshSpecialReportTrendResponse.setPermeability(calculateService.calculatePermeability(freshSaleAndRateModel.getKl(),
                            allKl.getKl()));
                }
                list.add(freshSpecialReportTrendResponse);
            }
        }
        freshSpecialReportTrendListResponse.setList(list);
        return freshSpecialReportTrendListResponse;
    }

    /**
     * 查询商品生鲜月趋势
     * @param param
     * @return
     */
    private FreshSpecialReportTrendListResponse queryShopFreshSpecialReportTrendForMonth(FreshSpecialReportRequest param){
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = new FreshSpecialReportTrendListResponse();
        List<FreshSpecialReportTrendResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        FreshAllKlModel allKl = null;
        List<FreshAllKlModel> klList = null;
        List<String> itemList = new ArrayList<>();
        Date start = param.getStart();
        int haveKlTable = 0;
        int haveSaleTable = 0;
        try{
            changeZyDataSource();
            //判断商品客流表是否存在
            haveKlTable = freshSpecialReportForMonthDao.queryIsHaveItemKlTable();
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        try{
            changeDgDataSource();
            //判断商品销售表是否存在
            haveSaleTable = freshSpecialReportForMonthDao.queryIsHaveItemSaleTable();
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(0 == haveKlTable || 0 == haveSaleTable){
            freshSpecialReportTrendListResponse.setList(list);
            return freshSpecialReportTrendListResponse;
        }
        try{
            changeZyDataSource();
            //查询月初至昨日总客流
            allKl = freshSpecialReportForMonthDao.queryFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        try{
            changeZyDataSource();
            //查询商品月初至昨日客流
            klList = freshSpecialReportForMonthDao.queryItemFreshAllKlList(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(null != klList && klList.size() > 0){
            for(FreshAllKlModel kl : klList){
                itemList.add(kl.getId());
            }

            try{
                changeDgDataSource();
                //查询商品月初至昨日销售和毛利额
                current = freshSpecialReportForMonthDao.queryItemMonthFreshSaleAndRateList(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1),itemList);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }

        //计算值
        if(null != klList && klList.size() > 0){
            Map<String, FreshSaleAndRateModel> map = null;
            if(null != current && current.size() > 0){
                map = current.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            for(FreshAllKlModel kl : klList){
                FreshSpecialReportTrendResponse freshSpecialReportTrendResponse = new FreshSpecialReportTrendResponse();
                freshSpecialReportTrendResponse.setName(kl.getName());
                if(null != allKl){//计算渗透率
                    freshSpecialReportTrendResponse.setPermeability(calculateService.calculatePermeability(
                            kl.getKl(),
                            allKl.getKl()));
                }
                if(null != map && map.size() > 0){
                    if(map.containsKey(kl.getId())){//获取毛利额
                        FreshSaleAndRateModel freshSaleAndRateModel = map.get(kl.getId());
                        freshSpecialReportTrendResponse.setRate(freshSaleAndRateModel.getRate());
                        freshSpecialReportTrendResponse.setRateIn(freshSaleAndRateModel.getRateIn());
                    }
                }
                list.add(freshSpecialReportTrendResponse);
            }
        }
        freshSpecialReportTrendListResponse.setList(list);
        return freshSpecialReportTrendListResponse;
    }

    /**
     * 查询门店生鲜日趋势
     * @param param
     * @return
     */
    private FreshSpecialReportTrendListResponse queryStoreFreshSpecialReportTrendForDay(FreshSpecialReportRequest param){
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = new FreshSpecialReportTrendListResponse();
        List<FreshSpecialReportTrendResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        FreshAllKlModel allKl = null;
        List<String> storeList = new ArrayList<>();
        Date start = param.getStart();
        current = freshSpecialReportForDayDao.queryStoreCurrentFreshSaleAndRate(param, start);
        if(null != current && current.size() > 0){
            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                storeList.add(freshSaleAndRateModel.getId());
            }
            allKl = freshSpecialReportForDayDao.queryFreshAllKl(param, start);
//            Map<String, FreshAllKlModel> klMap = null;
            /*if(null != allKlList && allKlList.size() > 0){
                klMap = allKlList.stream().collect(Collectors.toMap(FreshAllKlModel::getId,Function.identity(), (key1, key2) -> key2));
            }*/
            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                FreshSpecialReportTrendResponse freshSpecialReportTrendResponse = new FreshSpecialReportTrendResponse();
                freshSpecialReportTrendResponse.setName(freshSaleAndRateModel.getName());
                freshSpecialReportTrendResponse.setRate(freshSaleAndRateModel.getRate());
                freshSpecialReportTrendResponse.setRateIn(freshSaleAndRateModel.getRateIn());
                /*if(null != klMap && klMap.size() > 0){
                    if(klMap.containsKey(freshSaleAndRateModel.getId())){
                        FreshAllKlModel kl = klMap.get(freshSaleAndRateModel.getId());
                        freshSpecialReportTrendResponse.setPermeability(calculateService.calculatePermeability(freshSaleAndRateModel.getKl(),
                                kl.getKl()));
                    }
                }*/
                if(null != allKl){
                    freshSpecialReportTrendResponse.setPermeability(calculateService.calculatePermeability(freshSaleAndRateModel.getKl(),
                            allKl.getKl()));
                }
                list.add(freshSpecialReportTrendResponse);
            }
        }
        freshSpecialReportTrendListResponse.setList(list);
        return freshSpecialReportTrendListResponse;
    }

    /**
     * 查询门店生鲜月趋势
     * @param param
     * @return
     */
    private FreshSpecialReportTrendListResponse queryStoreFreshSpecialReportTrendForMonth(FreshSpecialReportRequest param){
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = new FreshSpecialReportTrendListResponse();
        List<FreshSpecialReportTrendResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        FreshAllKlModel allKl = null;
        List<FreshAllKlModel> klList = null;
        List<String> storeList = new ArrayList<>();
        Date start = param.getStart();
        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        try{
            changeZyDataSource();
            //查询月初至昨日总客流
            allKl = freshSpecialReportForMonthDao.queryFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(StringUtils.isNotEmpty(param.getSubClassId())){//查询小类门店趋势
            try{
                changeZyDataSource();
                //查询小类门店月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryStoreSubClassFreshAllKlList(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    storeList.add(freshAllKlModel.getId());
                }
                try{
                    changeDgDataSource();
                    //查询小类门店月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryStoreSubClassMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),storeList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else if(StringUtils.isNotEmpty(param.getClassId())){//根据中类门店趋势
            try{
                changeZyDataSource();
                //查询中类门店月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryStoreClassFreshAllKl(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    storeList.add(freshAllKlModel.getId());
                }
                try{
                    changeDgDataSource();
                    //查询中类门店月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryStoreClassMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),storeList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else if(StringUtils.isNotEmpty(param.getDeptId())){//根据大类门店趋势
            try{
                changeZyDataSource();
                //查询中类门店月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryStoreDeptFreshAllKl(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    storeList.add(freshAllKlModel.getId());
                }
                try{
                    changeDgDataSource();
                    //查询中类门店月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryStoreDeptMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),storeList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }

        //计算值
        if(null != klList && klList.size() > 0){
            Map<String, FreshSaleAndRateModel> map = null;
            if(null != current && current.size() > 0){
                map = current.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }

            for(FreshAllKlModel kl : klList){
                FreshSpecialReportTrendResponse freshSpecialReportTrendResponse = new FreshSpecialReportTrendResponse();
                freshSpecialReportTrendResponse.setName(getNameById(kl.getId(),orgList));
                if(null != allKl){//计算渗透率
                    freshSpecialReportTrendResponse.setPermeability(calculateService.calculatePermeability(
                            kl.getKl(),
                            allKl.getKl()));
                }
                if(null != map && map.size() > 0){
                    if(map.containsKey(kl.getId())){//获取毛利额
                        FreshSaleAndRateModel freshSaleAndRateModel = map.get(kl.getId());
                        freshSpecialReportTrendResponse.setRate(freshSaleAndRateModel.getRate());
                        freshSpecialReportTrendResponse.setRateIn(freshSaleAndRateModel.getRateIn());
                    }
                }
                list.add(freshSpecialReportTrendResponse);
            }
        }
        freshSpecialReportTrendListResponse.setList(list);
        return freshSpecialReportTrendListResponse;
    }

    /**
     * 查询区域日趋势
     * @param param
     * @return
     */
    private FreshSpecialReportTrendListResponse queryAreaFreshSpecialReportTrendForDay(FreshSpecialReportRequest param){
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = new FreshSpecialReportTrendListResponse();
        List<FreshSpecialReportTrendResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        FreshAllKlModel allKl = null;
        List<String> regionList = new ArrayList<>();
        Date start = param.getStart();
        current = freshSpecialReportForDayDao.queryRegionCurrentFreshSaleAndRate(param, start);
        if(null != current && current.size() > 0){
            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                regionList.add(freshSaleAndRateModel.getId());
            }
            allKl = freshSpecialReportForDayDao.queryFreshAllKl(param, start);
            for(FreshSaleAndRateModel freshSaleAndRateModel : current){
                FreshSpecialReportTrendResponse freshSpecialReportTrendResponse = new FreshSpecialReportTrendResponse();
                freshSpecialReportTrendResponse.setName(freshSaleAndRateModel.getName());
                freshSpecialReportTrendResponse.setRate(freshSaleAndRateModel.getRate());
                freshSpecialReportTrendResponse.setRateIn(freshSaleAndRateModel.getRateIn());
                if(null != allKl){
                    freshSpecialReportTrendResponse.setPermeability(calculateService.calculatePermeability(freshSaleAndRateModel.getKl(),
                            allKl.getKl()));
                }
                list.add(freshSpecialReportTrendResponse);
            }
        }
        freshSpecialReportTrendListResponse.setList(list);
        return freshSpecialReportTrendListResponse;
    }

    /**
     * 查询区域月趋势
     * @param param
     * @return
     */
    private FreshSpecialReportTrendListResponse queryAreaFreshSpecialReportTrendForMonth(FreshSpecialReportRequest param){
        FreshSpecialReportTrendListResponse freshSpecialReportTrendListResponse = new FreshSpecialReportTrendListResponse();
        List<FreshSpecialReportTrendResponse> list = new ArrayList<>();
        List<FreshSaleAndRateModel> current = null;
        FreshAllKlModel allKl = null;
        List<FreshAllKlModel> klList = null;
        List<String> regionList = new ArrayList<>();
        Date start = param.getStart();
        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        try{
            changeZyDataSource();
            //查询月初至昨日总客流
            allKl = freshSpecialReportForMonthDao.queryFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(StringUtils.isNotEmpty(param.getSubClassId())){//查询小类趋势
            try{
                changeZyDataSource();
                //查询小类月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryAreaSubClassFreshAllKlList(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    regionList.add(freshAllKlModel.getId());
                }
                try{
                    changeDgDataSource();
                    //查询小类月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryAreaSubClassMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),regionList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else if(StringUtils.isNotEmpty(param.getClassId())){//根据中类趋势
            try{
                changeZyDataSource();
                //查询中类月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryAreaClassFreshAllKl(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    regionList.add(freshAllKlModel.getId());
                }
                try{
                    changeDgDataSource();
                    //查询中类月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryAreaClassMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),regionList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }else if(StringUtils.isNotEmpty(param.getDeptId())){//根据大类趋势
            try{
                changeZyDataSource();
                //查询中类月初至昨日客流
                klList = freshSpecialReportForMonthDao.queryAreaDeptFreshAllKl(param,
                        DateUtil.getFirstDayForMonth(start),
                        DateUtils.addDays(start,-1));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            if(null != klList && klList.size() > 0){//存在客流说明存在销售
                for(FreshAllKlModel freshAllKlModel : klList){
                    regionList.add(freshAllKlModel.getId());
                }
                try{
                    changeDgDataSource();
                    //查询中类月初至昨日的销售额和毛利额数据
                    current = freshSpecialReportForMonthDao.queryAreaDeptMonthFreshSaleAndRateList(param,
                            DateUtil.getFirstDayForMonth(start),
                            DateUtils.addDays(start,-1),regionList);
                }catch (Exception e){
                    throw e;
                }finally {
                    DataSourceHolder.clearDataSource();
                }
            }
        }

        //计算值
        if(null != klList && klList.size() > 0){
            Map<String, FreshSaleAndRateModel> map = null;
            if(null != current && current.size() > 0){
                map = current.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            for(FreshAllKlModel kl : klList){
                FreshSpecialReportTrendResponse freshSpecialReportTrendResponse = new FreshSpecialReportTrendResponse();
                freshSpecialReportTrendResponse.setName(getNameById(kl.getId(),orgList));
                if(null != allKl){//计算渗透率
                    freshSpecialReportTrendResponse.setPermeability(calculateService.calculatePermeability(
                            kl.getKl(),
                            allKl.getKl()));
                }
                if(null != map && map.size() > 0){
                    if(map.containsKey(kl.getId())){//获取毛利额
                        FreshSaleAndRateModel freshSaleAndRateModel = map.get(kl.getId());
                        freshSpecialReportTrendResponse.setRate(freshSaleAndRateModel.getRate());
                        freshSpecialReportTrendResponse.setRateIn(freshSaleAndRateModel.getRateIn());
                    }
                }
                list.add(freshSpecialReportTrendResponse);
            }
        }
        freshSpecialReportTrendListResponse.setList(list);
        return freshSpecialReportTrendListResponse;
    }

    /**
     * 查询中类加小类日报表
     * @param param
     * @return
     */
    private FreshSpecialReportResponse queryClassAndSubClassForDay(FreshSpecialReportRequest param){
        FreshSpecialReportResponse freshSpecialReportResponse = new FreshSpecialReportResponse();
        List<SubFreshSpecialReportResponse> subFreshSpecialReportList = new ArrayList<>();
        ClassModel classModel = null;
        List<SubclassModel> subclassModelList = null;
        FreshSaleAndRateModel classCurrentFreshSaleAndRateModel = null;
        FreshSaleAndRateModel classHisFreshSaleAndRateModel = null;
        OrderAndStockAmountModel orderAndStockAmountModel = null;
        FreshAllKlModel freshAllKlModel = null;
        List<FreshSaleAndRateModel> subclassCurrentFreshSaleAndRateModelList = null;
        List<FreshSaleAndRateModel> subclassHisFreshSaleAndRateModelList = null;
        Date start = param.getStart();
        //查询中类信息
        classModel = freshSpecialReportForDayDao.queryClassInfoByClassId(param.getDeptId(), param.getClassId());
        //查询小类信息
        subclassModelList = freshSpecialReportForDayDao.querySubclassInfos(param.getDeptId(), param.getClassId());
        //查询中类实时销售和毛利额信息
        classCurrentFreshSaleAndRateModel = freshSpecialReportForDayDao.queryClassCurrentFreshSaleAndRate(param, start);
        //查询中类历史同比销售和毛利额信息
        try{
            changeDgDataSource();
            classHisFreshSaleAndRateModel = freshSpecialReportForDayDao.queryClassHisFreshSaleAndRate(param, DateUtils.addYears(start, -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询昨日库存和订货
        try{
            changeZyDataSource();
            orderAndStockAmountModel = freshSpecialReportForDayDao.queryOrderAndStockAmount(param, DateUtils.addDays(start, -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询总客流
        freshAllKlModel = freshSpecialReportForDayDao.queryFreshAllKl(param, start);
        //查询小类实时销售额和毛利额
        subclassCurrentFreshSaleAndRateModelList = freshSpecialReportForDayDao.querySubClassCurrentFreshSaleAndRateList(param, start);
        //查询小类历史同比销售和毛利额信息
        try{
            changeDgDataSource();
            subclassHisFreshSaleAndRateModelList = freshSpecialReportForDayDao.querySubClassHisFreshSaleAndRateList(param, DateUtils.addYears(start, -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //组装大类数据
        freshSpecialReportResponse.setId(param.getDeptId());
        if(null != classModel){
            freshSpecialReportResponse.setName(classModel.getClassName());
        }
        freshSpecialReportResponse.setFieldName("classId");
        if(null != orderAndStockAmountModel){
            freshSpecialReportResponse.setStockAmount(orderAndStockAmountModel.getStockAmount());
            freshSpecialReportResponse.setOrderAmount(orderAndStockAmountModel.getOrderAmount());
            freshSpecialReportResponse.setOrderAmountIn(orderAndStockAmountModel.getOrderAmountIn());
        }
        if(null != classCurrentFreshSaleAndRateModel){
            //销售额
            freshSpecialReportResponse.setSale(classCurrentFreshSaleAndRateModel.getSale());
            freshSpecialReportResponse.setSaleIn(classCurrentFreshSaleAndRateModel.getSaleIn());
            //可比销售额
            freshSpecialReportResponse.setCompareSale(classCurrentFreshSaleAndRateModel.getCompareSale());
            freshSpecialReportResponse.setCompareSaleIn(classCurrentFreshSaleAndRateModel.getCompareSaleIn());
            //毛利额
            freshSpecialReportResponse.setRate(classCurrentFreshSaleAndRateModel.getRate());
            freshSpecialReportResponse.setRateIn(classCurrentFreshSaleAndRateModel.getRateIn());
            //可比毛利额
            freshSpecialReportResponse.setCompareRate(classCurrentFreshSaleAndRateModel.getCompareRate());
            freshSpecialReportResponse.setCompareRateIn(classCurrentFreshSaleAndRateModel.getCompareRateIn());
            //毛利率
            freshSpecialReportResponse.setProfit(calculateService.calculateProfit(
                    classCurrentFreshSaleAndRateModel.getSale(),classCurrentFreshSaleAndRateModel.getRate()));
            freshSpecialReportResponse.setProfitIn(calculateService.calculateProfit(
                    classCurrentFreshSaleAndRateModel.getSaleIn(),classCurrentFreshSaleAndRateModel.getRateIn()));
            //可比毛利率
            freshSpecialReportResponse.setCompareProfit(calculateService.calculateProfit(
                    classCurrentFreshSaleAndRateModel.getCompareSale(),
                    classCurrentFreshSaleAndRateModel.getCompareRate()));
            freshSpecialReportResponse.setCompareProfitIn(calculateService.calculateProfit(
                    classCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                    classCurrentFreshSaleAndRateModel.getCompareRateIn()));

            if(null != freshAllKlModel){//渗透率
                freshSpecialReportResponse.setPermeability(calculateService.calculatePermeability(
                        classCurrentFreshSaleAndRateModel.getKl()
                        ,freshAllKlModel.getKl()));
            }

            if(null != classHisFreshSaleAndRateModel){
                //同期可比销售金额
                freshSpecialReportResponse.setHisCompareSale(classHisFreshSaleAndRateModel.getCompareSale());
                freshSpecialReportResponse.setHisCompareSaleIn(classHisFreshSaleAndRateModel.getCompareSaleIn());
                //同期可比毛利额
                freshSpecialReportResponse.setHisCompareRate(classHisFreshSaleAndRateModel.getCompareRate());
                freshSpecialReportResponse.setHisCompareRateIn(classHisFreshSaleAndRateModel.getCompareRateIn());
                //销售金额可比率
                freshSpecialReportResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                        classCurrentFreshSaleAndRateModel.getCompareSale(),
                        classHisFreshSaleAndRateModel.getCompareSale()));
                freshSpecialReportResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                        classCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                        classHisFreshSaleAndRateModel.getCompareSaleIn()));
                //同期可比毛利率
                freshSpecialReportResponse.setHisCompareProfit(calculateService.calculateProfit(
                        classHisFreshSaleAndRateModel.getCompareSale(),
                        classHisFreshSaleAndRateModel.getCompareRate()));
                freshSpecialReportResponse.setHisCompareProfitIn(calculateService.calculateProfit(
                        classHisFreshSaleAndRateModel.getCompareSaleIn(),
                        classHisFreshSaleAndRateModel.getCompareRateIn()));
                //毛利率可比率
                freshSpecialReportResponse.setCompareProfitRate(calculateService.calculateAddProfitRate(
                        calculateService.calculateProfit(classCurrentFreshSaleAndRateModel.getCompareSale(),
                                classCurrentFreshSaleAndRateModel.getCompareRate()),
                        calculateService.calculateProfit(classHisFreshSaleAndRateModel.getCompareSale(),
                                classHisFreshSaleAndRateModel.getCompareRate())));
                freshSpecialReportResponse.setCompareProfitRateIn(calculateService.calculateAddProfitRate(
                        calculateService.calculateProfit(classCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                                classCurrentFreshSaleAndRateModel.getCompareRateIn()),
                        calculateService.calculateProfit(classHisFreshSaleAndRateModel.getCompareSaleIn(),
                                classHisFreshSaleAndRateModel.getCompareRateIn())));
            }
        }
        //组装中类数据
        if(null != subclassModelList && subclassModelList.size() > 0){
            Map<String, FreshSaleAndRateModel> hisMap = null;
            Map<String, FreshSaleAndRateModel> currentMap = null;
            if(null != subclassHisFreshSaleAndRateModelList && subclassHisFreshSaleAndRateModelList.size() > 0){
                hisMap = subclassHisFreshSaleAndRateModelList.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            if(null != subclassCurrentFreshSaleAndRateModelList && subclassCurrentFreshSaleAndRateModelList.size() > 0){
                currentMap = subclassCurrentFreshSaleAndRateModelList.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            for(SubclassModel subclassModel : subclassModelList){
                SubFreshSpecialReportResponse subFreshSpecialReportResponse = new SubFreshSpecialReportResponse();
                subFreshSpecialReportResponse.setId(subclassModel.getSubclassId());
                subFreshSpecialReportResponse.setName(subclassModel.getSubclassName());
                subFreshSpecialReportResponse.setFieldName("subClassId");
                if(null != currentMap && currentMap.size() > 0) {
                    if (currentMap.containsKey(subclassModel.getSubclassId())) {
                        FreshSaleAndRateModel current = currentMap.get(subclassModel.getSubclassId());
                        if (null != freshAllKlModel) {//渗透率
                            subFreshSpecialReportResponse.setPermeability(calculateService.calculatePermeability(current.getKl(), freshAllKlModel.getKl()));
                        }
                        //是否有销售(0-否，1-是)
                        subFreshSpecialReportResponse.setHaveSale("1");
                        //销售金额
                        subFreshSpecialReportResponse.setSale(current.getSale());
                        subFreshSpecialReportResponse.setSaleIn(current.getSaleIn());
                        //可比销售金额
                        subFreshSpecialReportResponse.setCompareSale(current.getCompareSale());
                        subFreshSpecialReportResponse.setCompareSaleIn(current.getCompareSaleIn());
                        //毛利率
                        subFreshSpecialReportResponse.setProfit(calculateService.calculateProfit(current.getSale(),
                                current.getRate()));
                        subFreshSpecialReportResponse.setProfitIn(calculateService.calculateProfit(current.getSaleIn(),
                                current.getRateIn()));
                        //可比毛利率
                        subFreshSpecialReportResponse.setCompareProfit(calculateService.calculateProfit(current.getCompareSale(),
                                current.getCompareRate()));
                        subFreshSpecialReportResponse.setCompareProfitIn(calculateService.calculateProfit(current.getCompareSaleIn(),
                                current.getCompareRateIn()));
                        if (null != hisMap && hisMap.size() > 0) {
                            if (hisMap.containsKey(subclassModel.getSubclassId())) {
                                FreshSaleAndRateModel his = hisMap.get(subclassModel.getSubclassId());
                                //同期可比销售金额
                                subFreshSpecialReportResponse.setHisCompareSale(his.getCompareSale());
                                subFreshSpecialReportResponse.setHisCompareSaleIn(his.getCompareSaleIn());
                                //销售金额可比率
                                subFreshSpecialReportResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                                        current.getCompareSale(),
                                        his.getCompareSale()));
                                subFreshSpecialReportResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                                        current.getCompareSaleIn(),
                                        his.getCompareSaleIn()));
                                //同期可比毛利率
                                subFreshSpecialReportResponse.setHisCompareProfit(calculateService.calculateProfit(his.getCompareSale(),
                                        his.getCompareRate()));
                                subFreshSpecialReportResponse.setHisCompareProfitIn(calculateService.calculateProfit(his.getCompareSaleIn(),
                                        his.getCompareRateIn()));
                                //毛利率可比率
                                subFreshSpecialReportResponse.setCompareProfitRate(calculateService.calculateAddProfitRate(
                                        calculateService.calculateProfit(current.getSale(),current.getRate()),
                                        calculateService.calculateProfit(his.getSale(),his.getRate())));
                                subFreshSpecialReportResponse.setCompareProfitRateIn(calculateService.calculateAddProfitRate(
                                        calculateService.calculateProfit(current.getSaleIn(),current.getRateIn()),
                                        calculateService.calculateProfit(his.getSaleIn(),his.getRateIn())));
                            }
                        }
                    }
                }
                subFreshSpecialReportList.add(subFreshSpecialReportResponse);
            }
        }
        freshSpecialReportResponse.setList(subFreshSpecialReportList);
        return freshSpecialReportResponse;
    }

    /**
     * 查询中类加小类月报表
     * @param param
     * @return
     */
    private FreshSpecialReportResponse queryClassAndSubClassForMonth(FreshSpecialReportRequest param){
        FreshSpecialReportResponse freshSpecialReportResponse = new FreshSpecialReportResponse();
        List<SubFreshSpecialReportResponse> subFreshSpecialReportList = new ArrayList<>();
        ClassModel classModel = null;
        List<SubclassModel> subclassModelList = null;
        FreshSaleAndRateModel classCurrentFreshSaleAndRateModel = null;
        FreshSaleAndRateModel classHisFreshSaleAndRateModel = null;
        OrderAndStockAmountModel orderAndStockAmountModel = null;
        FreshAllKlModel freshAllKlModel = null;
        FreshAllKlModel classKlModel = null;
        List<FreshAllKlModel> subclassKlModelList = null;
        List<FreshSaleAndRateModel> subclassCurrentFreshSaleAndRateModelList = null;
        List<FreshSaleAndRateModel> subclassHisFreshSaleAndRateModelList = null;
        Date start = param.getStart();
        //查询大类信息
        classModel = freshSpecialReportForDayDao.queryClassInfoByClassId(param.getDeptId(),param.getClassId());
        //查询中类信息
        subclassModelList = freshSpecialReportForDayDao.querySubclassInfos(param.getDeptId(),param.getClassId());

        try{
            changeDgDataSource();
            //查询中类月初至昨日销售和毛利额信息
            classCurrentFreshSaleAndRateModel = freshSpecialReportForMonthDao.queryClassMonthFreshSaleAndRate(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
            //查询中类月初至昨日同比销售和毛利额信息
            classHisFreshSaleAndRateModel = freshSpecialReportForMonthDao.queryClassSameMonthFreshSaleAndRate(param,
                    DateUtils.addYears(DateUtil.getFirstDayForMonth(start), -1),
                    DateUtils.addYears(DateUtils.addDays(start,-1), -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //查询昨日库存和订货
        try{
            changeZyDataSource();
            orderAndStockAmountModel = freshSpecialReportForDayDao.queryOrderAndStockAmount(param, DateUtils.addDays(start, -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        try{
            changeZyDataSource();
            //查询总客流
            freshAllKlModel = freshSpecialReportForMonthDao.queryFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
            //查询中类客流
            classKlModel = freshSpecialReportForMonthDao.queryClassFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
            //查询小类客流
            subclassKlModelList = freshSpecialReportForMonthDao.querySubClassFreshAllKlList(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        try{
            changeDgDataSource();
            //查询中类月初至昨日销售额和毛利额
            subclassCurrentFreshSaleAndRateModelList = freshSpecialReportForMonthDao.querySubClassMonthFreshSaleAndRateList(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
            //查询中类历史同比销售和毛利额信息
            subclassHisFreshSaleAndRateModelList = freshSpecialReportForMonthDao.querySubClassSameMonthFreshSaleAndRateList(param,
                    DateUtils.addYears(DateUtil.getFirstDayForMonth(start), -1),
                    DateUtils.addYears(DateUtils.addDays(start,-1), -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //组装中类数据
        freshSpecialReportResponse.setId(param.getDeptId());
        if(null != classModel){
            freshSpecialReportResponse.setName(classModel.getClassName());
        }
        freshSpecialReportResponse.setFieldName("classId");
        if(null != orderAndStockAmountModel){
            freshSpecialReportResponse.setStockAmount(orderAndStockAmountModel.getStockAmount());
            freshSpecialReportResponse.setOrderAmount(orderAndStockAmountModel.getOrderAmount());
            freshSpecialReportResponse.setOrderAmountIn(orderAndStockAmountModel.getOrderAmountIn());
        }
        if(null != classKlModel) {
            if (null != classCurrentFreshSaleAndRateModel) {
                //渗透率
                freshSpecialReportResponse.setPermeability(calculateService.calculatePermeability(
                        classKlModel.getKl()
                        ,freshAllKlModel.getKl()));
                //销售额
                freshSpecialReportResponse.setSale(classCurrentFreshSaleAndRateModel.getSale());
                freshSpecialReportResponse.setSaleIn(classCurrentFreshSaleAndRateModel.getSaleIn());
                //可比销售额
                freshSpecialReportResponse.setCompareSale(classCurrentFreshSaleAndRateModel.getCompareSale());
                freshSpecialReportResponse.setCompareSaleIn(classCurrentFreshSaleAndRateModel.getCompareSaleIn());
                //毛利额
                freshSpecialReportResponse.setRate(classCurrentFreshSaleAndRateModel.getRate());
                freshSpecialReportResponse.setRateIn(classCurrentFreshSaleAndRateModel.getRateIn());
                //可比毛利额
                freshSpecialReportResponse.setCompareRate(classCurrentFreshSaleAndRateModel.getCompareRate());
                freshSpecialReportResponse.setCompareRateIn(classCurrentFreshSaleAndRateModel.getCompareRateIn());
                //毛利率
                freshSpecialReportResponse.setProfit(calculateService.calculateProfit(
                        classCurrentFreshSaleAndRateModel.getSale(),classCurrentFreshSaleAndRateModel.getRate()));
                freshSpecialReportResponse.setProfitIn(calculateService.calculateProfit(
                        classCurrentFreshSaleAndRateModel.getSaleIn(),classCurrentFreshSaleAndRateModel.getRateIn()));
                //可比毛利率
                freshSpecialReportResponse.setCompareProfit(calculateService.calculateProfit(
                        classCurrentFreshSaleAndRateModel.getCompareSale(),
                        classCurrentFreshSaleAndRateModel.getCompareRate()));
                freshSpecialReportResponse.setCompareProfitIn(calculateService.calculateProfit(
                        classCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                        classCurrentFreshSaleAndRateModel.getCompareRateIn()));

                if(null != classHisFreshSaleAndRateModel){
                    //同期可比销售金额
                    freshSpecialReportResponse.setHisCompareSale(classHisFreshSaleAndRateModel.getCompareSale());
                    freshSpecialReportResponse.setHisCompareSaleIn(classHisFreshSaleAndRateModel.getCompareSaleIn());
                    //同期可比毛利额
                    freshSpecialReportResponse.setHisCompareRate(classHisFreshSaleAndRateModel.getCompareRate());
                    freshSpecialReportResponse.setHisCompareRateIn(classHisFreshSaleAndRateModel.getCompareRateIn());
                    //销售金额可比率
                    freshSpecialReportResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                            classCurrentFreshSaleAndRateModel.getCompareSale(),
                            classHisFreshSaleAndRateModel.getCompareSale()));
                    freshSpecialReportResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                            classCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                            classHisFreshSaleAndRateModel.getCompareSaleIn()));
                    //同期可比毛利率
                    freshSpecialReportResponse.setHisCompareProfit(calculateService.calculateProfit(
                            classHisFreshSaleAndRateModel.getCompareSale(),
                            classHisFreshSaleAndRateModel.getCompareRate()));
                    freshSpecialReportResponse.setHisCompareProfitIn(calculateService.calculateProfit(
                            classHisFreshSaleAndRateModel.getCompareSaleIn(),
                            classHisFreshSaleAndRateModel.getCompareRateIn()));
                    //毛利率可比率
                    freshSpecialReportResponse.setCompareProfitRate(calculateService.calculateAddProfitRate(
                            calculateService.calculateProfit(classCurrentFreshSaleAndRateModel.getCompareSale(),
                                    classCurrentFreshSaleAndRateModel.getCompareRate()),
                            calculateService.calculateProfit(classHisFreshSaleAndRateModel.getCompareSale(),
                                    classHisFreshSaleAndRateModel.getCompareRate())));
                    freshSpecialReportResponse.setCompareProfitRateIn(calculateService.calculateAddProfitRate(
                            calculateService.calculateProfit(classCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                                    classCurrentFreshSaleAndRateModel.getCompareRateIn()),
                            calculateService.calculateProfit(classHisFreshSaleAndRateModel.getCompareSaleIn(),
                                    classHisFreshSaleAndRateModel.getCompareRateIn())));
                }
            }
        }
        //组装小类数据
        if(null != subclassModelList && subclassModelList.size() > 0){
            Map<String, FreshSaleAndRateModel> hisMap = null;
            Map<String, FreshSaleAndRateModel> currentMap = null;
            Map<String, FreshAllKlModel> klMap = null;
            if(null != subclassHisFreshSaleAndRateModelList && subclassHisFreshSaleAndRateModelList.size() > 0){
                hisMap = subclassHisFreshSaleAndRateModelList.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            if(null != subclassCurrentFreshSaleAndRateModelList && subclassCurrentFreshSaleAndRateModelList.size() > 0){
                currentMap = subclassCurrentFreshSaleAndRateModelList.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            if(null != subclassKlModelList && subclassKlModelList.size() > 0){
                klMap = subclassKlModelList.stream().collect(Collectors.toMap(FreshAllKlModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            for(SubclassModel subclassModel : subclassModelList){
                SubFreshSpecialReportResponse subFreshSpecialReportResponse = new SubFreshSpecialReportResponse();
                subFreshSpecialReportResponse.setId(subclassModel.getSubclassId());
                subFreshSpecialReportResponse.setName(subclassModel.getSubclassName());
                subFreshSpecialReportResponse.setFieldName("subclassId");
                if(null != klMap && klMap.size() > 0) {
                    if (klMap.containsKey(subclassModel.getSubclassId())) {
                        FreshAllKlModel kl = klMap.get(subclassModel.getSubclassId());
                        if (null != freshAllKlModel) {
                            //渗透率
                            subFreshSpecialReportResponse.setPermeability(calculateService.calculatePermeability(kl.getKl(), freshAllKlModel.getKl()));
                        }
                        //是否有销售(0-否，1-是)
                        subFreshSpecialReportResponse.setHaveSale("1");
                        if(null != currentMap && currentMap.containsKey(subclassModel.getSubclassId())){
                            if(currentMap.containsKey(subclassModel.getSubclassId())){
                                FreshSaleAndRateModel current = currentMap.get(subclassModel.getSubclassId());
                                //销售金额
                                subFreshSpecialReportResponse.setSale(current.getSale());
                                subFreshSpecialReportResponse.setSaleIn(current.getSaleIn());
                                //可比销售金额
                                subFreshSpecialReportResponse.setCompareSale(current.getCompareSale());
                                subFreshSpecialReportResponse.setCompareSaleIn(current.getCompareSaleIn());
                                //毛利率
                                subFreshSpecialReportResponse.setProfit(calculateService.calculateProfit(current.getSale(),
                                        current.getRate()));
                                subFreshSpecialReportResponse.setProfitIn(calculateService.calculateProfit(current.getSaleIn(),
                                        current.getRateIn()));
                                //可比毛利率
                                subFreshSpecialReportResponse.setCompareProfit(calculateService.calculateProfit(current.getCompareSale(),
                                        current.getCompareRate()));
                                subFreshSpecialReportResponse.setCompareProfitIn(calculateService.calculateProfit(current.getCompareSaleIn(),
                                        current.getCompareRateIn()));
                                if (null != hisMap && hisMap.size() > 0) {
                                    if (hisMap.containsKey(subclassModel.getSubclassId())) {
                                        FreshSaleAndRateModel his = hisMap.get(subclassModel.getSubclassId());
                                        //同期可比销售金额
                                        subFreshSpecialReportResponse.setHisCompareSale(his.getCompareSale());
                                        subFreshSpecialReportResponse.setHisCompareSaleIn(his.getCompareSaleIn());
                                        //销售金额可比率
                                        subFreshSpecialReportResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                                                current.getCompareSale(),
                                                his.getCompareSale()));
                                        subFreshSpecialReportResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                                                current.getCompareSaleIn(),
                                                his.getCompareSaleIn()));
                                        //同期可比毛利率
                                        subFreshSpecialReportResponse.setHisCompareProfit(calculateService.calculateProfit(his.getCompareSale(),
                                                his.getCompareRate()));
                                        subFreshSpecialReportResponse.setHisCompareProfitIn(calculateService.calculateProfit(his.getCompareSaleIn(),
                                                his.getCompareRateIn()));
                                        //毛利率可比率
                                        subFreshSpecialReportResponse.setCompareProfitRate(calculateService.calculateAddProfitRate(
                                                calculateService.calculateProfit(current.getSale(),current.getRate()),
                                                calculateService.calculateProfit(his.getSale(),his.getRate())));
                                        subFreshSpecialReportResponse.setCompareProfitRateIn(calculateService.calculateAddProfitRate(
                                                calculateService.calculateProfit(current.getSaleIn(),current.getRateIn()),
                                                calculateService.calculateProfit(his.getSaleIn(),his.getRateIn())));
                                    }
                                }
                            }
                        }
                    }
                }
                subFreshSpecialReportList.add(subFreshSpecialReportResponse);
            }
        }
        freshSpecialReportResponse.setList(subFreshSpecialReportList);
        return freshSpecialReportResponse;
    }

    /**
     * 查询大类加中类日报表
     * @param param
     * @return
     */
    private FreshSpecialReportResponse queryDeptAndClassForDay(FreshSpecialReportRequest param){
        FreshSpecialReportResponse freshSpecialReportResponse = new FreshSpecialReportResponse();
        List<SubFreshSpecialReportResponse> subFreshSpecialReportList = new ArrayList<>();
        DeptModel deptModel = null;
        List<ClassModel> classModelList = null;
        FreshSaleAndRateModel deptCurrentFreshSaleAndRateModel = null;
        FreshSaleAndRateModel deptHisFreshSaleAndRateModel = null;
        OrderAndStockAmountModel orderAndStockAmountModel = null;
        FreshAllKlModel freshAllKlModel = null;
        List<FreshSaleAndRateModel> classCurrentFreshSaleAndRateModelList = null;
        List<FreshSaleAndRateModel> classHisFreshSaleAndRateModelList = null;
        Date start = param.getStart();
        //查询大类信息
        deptModel = freshSpecialReportForDayDao.queryDeptInfByDeptId(param.getDeptId());
        //查询中类信息
        classModelList = freshSpecialReportForDayDao.queryClassInfos(param.getDeptId());
        //查询大类实时销售和毛利额信息
        deptCurrentFreshSaleAndRateModel = freshSpecialReportForDayDao.queryDeptCurrentFreshSaleAndRate(param, start);
        //查询大类历史同比销售和毛利额信息
        try{
            changeDgDataSource();
            deptHisFreshSaleAndRateModel = freshSpecialReportForDayDao.queryDeptHisFreshSaleAndRate(param, DateUtils.addYears(start, -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询昨日库存和订货
        try{
            changeZyDataSource();
            orderAndStockAmountModel = freshSpecialReportForDayDao.queryOrderAndStockAmount(param, DateUtils.addDays(start, -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询总客流
        freshAllKlModel = freshSpecialReportForDayDao.queryFreshAllKl(param, start);
        //查询中类实时销售额和毛利额
        classCurrentFreshSaleAndRateModelList = freshSpecialReportForDayDao.queryClassCurrentFreshSaleAndRateList(param, start);
        //查询中类历史同比销售和毛利额信息
        try{
            changeDgDataSource();
            classHisFreshSaleAndRateModelList = freshSpecialReportForDayDao.queryClassHisFreshSaleAndRateList(param, DateUtils.addYears(start, -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //组装大类数据
        freshSpecialReportResponse.setId(param.getDeptId());
        if(null != deptModel){
            freshSpecialReportResponse.setName(deptModel.getDeptName());
        }
        freshSpecialReportResponse.setFieldName("deptId");
        if(null != orderAndStockAmountModel){
            freshSpecialReportResponse.setStockAmount(orderAndStockAmountModel.getStockAmount());
            freshSpecialReportResponse.setOrderAmount(orderAndStockAmountModel.getOrderAmount());
            freshSpecialReportResponse.setOrderAmountIn(orderAndStockAmountModel.getOrderAmountIn());
        }
        if(null != deptCurrentFreshSaleAndRateModel){
            //销售额
            freshSpecialReportResponse.setSale(deptCurrentFreshSaleAndRateModel.getSale());
            freshSpecialReportResponse.setSaleIn(deptCurrentFreshSaleAndRateModel.getSaleIn());
            //可比销售额
            freshSpecialReportResponse.setCompareSale(deptCurrentFreshSaleAndRateModel.getCompareSale());
            freshSpecialReportResponse.setCompareSaleIn(deptCurrentFreshSaleAndRateModel.getCompareSaleIn());
            //毛利额
            freshSpecialReportResponse.setRate(deptCurrentFreshSaleAndRateModel.getRate());
            freshSpecialReportResponse.setRateIn(deptCurrentFreshSaleAndRateModel.getRateIn());
            //可比毛利额
            freshSpecialReportResponse.setCompareRate(deptCurrentFreshSaleAndRateModel.getCompareRate());
            freshSpecialReportResponse.setCompareRateIn(deptCurrentFreshSaleAndRateModel.getCompareRateIn());
            //毛利率
            freshSpecialReportResponse.setProfit(calculateService.calculateProfit(
                    deptCurrentFreshSaleAndRateModel.getSale(),deptCurrentFreshSaleAndRateModel.getRate()));
            freshSpecialReportResponse.setProfitIn(calculateService.calculateProfit(
                    deptCurrentFreshSaleAndRateModel.getSaleIn(),deptCurrentFreshSaleAndRateModel.getRateIn()));
            //可比毛利率
            freshSpecialReportResponse.setCompareProfit(calculateService.calculateProfit(
                    deptCurrentFreshSaleAndRateModel.getCompareSale(),
                    deptCurrentFreshSaleAndRateModel.getCompareRate()));
            freshSpecialReportResponse.setCompareProfitIn(calculateService.calculateProfit(
                    deptCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                    deptCurrentFreshSaleAndRateModel.getCompareRateIn()));

            if(null != freshAllKlModel){//渗透率
                freshSpecialReportResponse.setPermeability(calculateService.calculatePermeability(
                        deptCurrentFreshSaleAndRateModel.getKl()
                        ,freshAllKlModel.getKl()));
            }

            if(null != deptHisFreshSaleAndRateModel){
                //同期可比销售金额
                freshSpecialReportResponse.setHisCompareSale(deptHisFreshSaleAndRateModel.getCompareSale());
                freshSpecialReportResponse.setHisCompareSaleIn(deptHisFreshSaleAndRateModel.getCompareSaleIn());
                //同期可比毛利额
                freshSpecialReportResponse.setHisCompareRate(deptHisFreshSaleAndRateModel.getCompareRate());
                freshSpecialReportResponse.setHisCompareRateIn(deptHisFreshSaleAndRateModel.getCompareRateIn());
                //销售金额可比率
                freshSpecialReportResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                        deptCurrentFreshSaleAndRateModel.getCompareSale(),
                        deptHisFreshSaleAndRateModel.getCompareSale()));
                freshSpecialReportResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                        deptCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                        deptHisFreshSaleAndRateModel.getCompareSaleIn()));
                //同期可比毛利率
                freshSpecialReportResponse.setHisCompareProfit(calculateService.calculateProfit(
                                deptHisFreshSaleAndRateModel.getCompareSale(),
                                deptHisFreshSaleAndRateModel.getCompareRate()));
                freshSpecialReportResponse.setHisCompareProfitIn(calculateService.calculateProfit(
                        deptHisFreshSaleAndRateModel.getCompareSaleIn(),
                        deptHisFreshSaleAndRateModel.getCompareRateIn()));
                //毛利率可比率
                freshSpecialReportResponse.setCompareProfitRate(calculateService.calculateAddProfitRate(
                        calculateService.calculateProfit(deptCurrentFreshSaleAndRateModel.getCompareSale(),
                                deptCurrentFreshSaleAndRateModel.getCompareRate()),
                        calculateService.calculateProfit(deptHisFreshSaleAndRateModel.getCompareSale(),
                                deptHisFreshSaleAndRateModel.getCompareRate())));
                freshSpecialReportResponse.setCompareProfitRateIn(calculateService.calculateAddProfitRate(
                        calculateService.calculateProfit(deptCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                                deptCurrentFreshSaleAndRateModel.getCompareRateIn()),
                        calculateService.calculateProfit(deptHisFreshSaleAndRateModel.getCompareSaleIn(),
                                deptHisFreshSaleAndRateModel.getCompareRateIn())));
            }
        }
        //组装中类数据
        if(null != classModelList && classModelList.size() > 0){
            Map<String, FreshSaleAndRateModel> hisMap = null;
            Map<String, FreshSaleAndRateModel> currentMap = null;
            if(null != classHisFreshSaleAndRateModelList && classHisFreshSaleAndRateModelList.size() > 0){
                hisMap = classHisFreshSaleAndRateModelList.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            if(null != classCurrentFreshSaleAndRateModelList && classCurrentFreshSaleAndRateModelList.size() > 0){
                currentMap = classCurrentFreshSaleAndRateModelList.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            for(ClassModel classModel : classModelList){
                SubFreshSpecialReportResponse subFreshSpecialReportResponse = new SubFreshSpecialReportResponse();
                subFreshSpecialReportResponse.setId(classModel.getClassId());
                subFreshSpecialReportResponse.setName(classModel.getClassName());
                subFreshSpecialReportResponse.setFieldName("classId");
                if(null != currentMap && currentMap.size() > 0) {
                    if (currentMap.containsKey(classModel.getClassId())) {
                        FreshSaleAndRateModel current = currentMap.get(classModel.getClassId());
                        if (null != freshAllKlModel) {//渗透率
                            subFreshSpecialReportResponse.setPermeability(calculateService.calculatePermeability(current.getKl(), freshAllKlModel.getKl()));
                        }
                        //是否有销售(0-否，1-是)
                        subFreshSpecialReportResponse.setHaveSale("1");
                        //销售金额
                        subFreshSpecialReportResponse.setSale(current.getSale());
                        subFreshSpecialReportResponse.setSaleIn(current.getSaleIn());
                        //可比销售金额
                        subFreshSpecialReportResponse.setCompareSale(current.getCompareSale());
                        subFreshSpecialReportResponse.setCompareSaleIn(current.getCompareSaleIn());
                        //毛利率
                        subFreshSpecialReportResponse.setProfit(calculateService.calculateProfit(current.getSale(),
                                current.getRate()));
                        subFreshSpecialReportResponse.setProfitIn(calculateService.calculateProfit(current.getSaleIn(),
                                current.getRateIn()));
                        //可比毛利率
                        subFreshSpecialReportResponse.setCompareProfit(calculateService.calculateProfit(current.getCompareSale(),
                                current.getCompareRate()));
                        subFreshSpecialReportResponse.setCompareProfitIn(calculateService.calculateProfit(current.getCompareSaleIn(),
                                current.getCompareRateIn()));
                        if (null != hisMap && hisMap.size() > 0) {
                            if (hisMap.containsKey(classModel.getClassId())) {
                                FreshSaleAndRateModel his = hisMap.get(classModel.getClassId());
                                //同期可比销售金额
                                subFreshSpecialReportResponse.setHisCompareSale(his.getCompareSale());
                                subFreshSpecialReportResponse.setHisCompareSaleIn(his.getCompareSaleIn());
                                //销售金额可比率
                                subFreshSpecialReportResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                                        current.getCompareSale(),
                                        his.getCompareSale()));
                                subFreshSpecialReportResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                                        current.getCompareSaleIn(),
                                        his.getCompareSaleIn()));
                                //同期可比毛利率
                                subFreshSpecialReportResponse.setHisCompareProfit(calculateService.calculateProfit(his.getCompareSale(),
                                        his.getCompareRate()));
                                subFreshSpecialReportResponse.setHisCompareProfitIn(calculateService.calculateProfit(his.getCompareSaleIn(),
                                        his.getCompareRateIn()));
                                //毛利率可比率
                                subFreshSpecialReportResponse.setCompareProfitRate(calculateService.calculateAddProfitRate(
                                        calculateService.calculateProfit(current.getSale(),current.getRate()),
                                        calculateService.calculateProfit(his.getSale(),his.getRate())));
                                subFreshSpecialReportResponse.setCompareProfitRateIn(calculateService.calculateAddProfitRate(
                                        calculateService.calculateProfit(current.getSaleIn(),current.getRateIn()),
                                        calculateService.calculateProfit(his.getSaleIn(),his.getRateIn())));
                            }
                        }
                    }
                }
                subFreshSpecialReportList.add(subFreshSpecialReportResponse);
            }
        }
        freshSpecialReportResponse.setList(subFreshSpecialReportList);
        return freshSpecialReportResponse;
    }

    /**
     * 查询大类加中类月报表
     * @param param
     * @return
     */
    private FreshSpecialReportResponse queryDeptAndClassForMonth(FreshSpecialReportRequest param){
        FreshSpecialReportResponse freshSpecialReportResponse = new FreshSpecialReportResponse();
        List<SubFreshSpecialReportResponse> subFreshSpecialReportList = new ArrayList<>();
        DeptModel deptModel = null;
        List<ClassModel> classModelList = null;
        FreshSaleAndRateModel deptCurrentFreshSaleAndRateModel = null;
        FreshSaleAndRateModel deptHisFreshSaleAndRateModel = null;
        OrderAndStockAmountModel orderAndStockAmountModel = null;
        FreshAllKlModel freshAllKlModel = null;
        FreshAllKlModel deptKlModel = null;
        List<FreshAllKlModel> classKlModelList = null;
        List<FreshSaleAndRateModel> classCurrentFreshSaleAndRateModelList = null;
        List<FreshSaleAndRateModel> classHisFreshSaleAndRateModelList = null;
        Date start = param.getStart();
        //查询大类信息
        deptModel = freshSpecialReportForDayDao.queryDeptInfByDeptId(param.getDeptId());
        //查询中类信息
        classModelList = freshSpecialReportForDayDao.queryClassInfos(param.getDeptId());

        try{
            changeDgDataSource();
            //查询大类月初至昨日销售和毛利额信息
            deptCurrentFreshSaleAndRateModel = freshSpecialReportForMonthDao.queryDeptMonthFreshSaleAndRate(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
            //查询大类月初至昨日同比销售和毛利额信息
            deptHisFreshSaleAndRateModel = freshSpecialReportForMonthDao.queryDeptSameMonthFreshSaleAndRate(param,
                    DateUtils.addYears(DateUtil.getFirstDayForMonth(start), -1),
                    DateUtils.addYears(DateUtils.addDays(start,-1), -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //查询昨日库存和订货
        try{
            changeZyDataSource();
            orderAndStockAmountModel = freshSpecialReportForDayDao.queryOrderAndStockAmount(param, DateUtils.addDays(start, -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        try{
            changeZyDataSource();
            //查询总客流
            freshAllKlModel = freshSpecialReportForMonthDao.queryFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
            //查询大类客流
            deptKlModel = freshSpecialReportForMonthDao.queryDeptFreshAllKl(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
            //查询中类客流
            classKlModelList = freshSpecialReportForMonthDao.queryClassFreshAllKlList(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        try{
            changeDgDataSource();
            //查询中类月初至昨日销售额和毛利额
            classCurrentFreshSaleAndRateModelList = freshSpecialReportForMonthDao.queryClassMonthFreshSaleAndRateList(param,
                    DateUtil.getFirstDayForMonth(start),
                    DateUtils.addDays(start,-1));
            //查询中类历史同比销售和毛利额信息
            classHisFreshSaleAndRateModelList = freshSpecialReportForMonthDao.queryClassSameMonthFreshSaleAndRateList(param,
                    DateUtils.addYears(DateUtil.getFirstDayForMonth(start), -1),
                    DateUtils.addYears(DateUtils.addDays(start,-1), -1));
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //组装大类数据
        freshSpecialReportResponse.setId(param.getDeptId());
        if(null != deptModel){
            freshSpecialReportResponse.setName(deptModel.getDeptName());
        }
        freshSpecialReportResponse.setFieldName("deptId");
        if(null != orderAndStockAmountModel){
            freshSpecialReportResponse.setStockAmount(orderAndStockAmountModel.getStockAmount());
            freshSpecialReportResponse.setOrderAmount(orderAndStockAmountModel.getOrderAmount());
            freshSpecialReportResponse.setOrderAmountIn(orderAndStockAmountModel.getOrderAmountIn());
        }
        if(null != deptKlModel) {
            if (null != deptCurrentFreshSaleAndRateModel) {
                //渗透率
                freshSpecialReportResponse.setPermeability(calculateService.calculatePermeability(
                        deptKlModel.getKl()
                        ,freshAllKlModel.getKl()));
                //销售额
                freshSpecialReportResponse.setSale(deptCurrentFreshSaleAndRateModel.getSale());
                freshSpecialReportResponse.setSaleIn(deptCurrentFreshSaleAndRateModel.getSaleIn());
                //可比销售额
                freshSpecialReportResponse.setCompareSale(deptCurrentFreshSaleAndRateModel.getCompareSale());
                freshSpecialReportResponse.setCompareSaleIn(deptCurrentFreshSaleAndRateModel.getCompareSaleIn());
                //毛利额
                freshSpecialReportResponse.setRate(deptCurrentFreshSaleAndRateModel.getRate());
                freshSpecialReportResponse.setRateIn(deptCurrentFreshSaleAndRateModel.getRateIn());
                //可比毛利额
                freshSpecialReportResponse.setCompareRate(deptCurrentFreshSaleAndRateModel.getCompareRate());
                freshSpecialReportResponse.setCompareRateIn(deptCurrentFreshSaleAndRateModel.getCompareRateIn());
                //毛利率
                freshSpecialReportResponse.setProfit(calculateService.calculateProfit(
                        deptCurrentFreshSaleAndRateModel.getSale(),deptCurrentFreshSaleAndRateModel.getRate()));
                freshSpecialReportResponse.setProfitIn(calculateService.calculateProfit(
                        deptCurrentFreshSaleAndRateModel.getSaleIn(),deptCurrentFreshSaleAndRateModel.getRateIn()));
                //可比毛利率
                freshSpecialReportResponse.setCompareProfit(calculateService.calculateProfit(
                        deptCurrentFreshSaleAndRateModel.getCompareSale(),
                        deptCurrentFreshSaleAndRateModel.getCompareRate()));
                freshSpecialReportResponse.setCompareProfitIn(calculateService.calculateProfit(
                        deptCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                        deptCurrentFreshSaleAndRateModel.getCompareRateIn()));

                if(null != deptHisFreshSaleAndRateModel){
                    //同期可比销售金额
                    freshSpecialReportResponse.setHisCompareSale(deptHisFreshSaleAndRateModel.getCompareSale());
                    freshSpecialReportResponse.setHisCompareSaleIn(deptHisFreshSaleAndRateModel.getCompareSaleIn());
                    //同期可比毛利额
                    freshSpecialReportResponse.setHisCompareRate(deptHisFreshSaleAndRateModel.getCompareRate());
                    freshSpecialReportResponse.setHisCompareRateIn(deptHisFreshSaleAndRateModel.getCompareRateIn());
                    //销售金额可比率
                    freshSpecialReportResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                            deptCurrentFreshSaleAndRateModel.getCompareSale(),
                            deptHisFreshSaleAndRateModel.getCompareSale()));
                    freshSpecialReportResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                            deptCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                            deptHisFreshSaleAndRateModel.getCompareSaleIn()));
                    //同期可比毛利率
                    freshSpecialReportResponse.setHisCompareProfit(calculateService.calculateProfit(
                            deptHisFreshSaleAndRateModel.getCompareSale(),
                            deptHisFreshSaleAndRateModel.getCompareRate()));
                    freshSpecialReportResponse.setHisCompareProfitIn(calculateService.calculateProfit(
                            deptHisFreshSaleAndRateModel.getCompareSaleIn(),
                            deptHisFreshSaleAndRateModel.getCompareRateIn()));
                    //毛利率可比率
                    freshSpecialReportResponse.setCompareProfitRate(calculateService.calculateAddProfitRate(
                            calculateService.calculateProfit(deptCurrentFreshSaleAndRateModel.getCompareSale(),
                                    deptCurrentFreshSaleAndRateModel.getCompareRate()),
                            calculateService.calculateProfit(deptHisFreshSaleAndRateModel.getCompareSale(),
                                    deptHisFreshSaleAndRateModel.getCompareRate())));
                    freshSpecialReportResponse.setCompareProfitRateIn(calculateService.calculateAddProfitRate(
                            calculateService.calculateProfit(deptCurrentFreshSaleAndRateModel.getCompareSaleIn(),
                                    deptCurrentFreshSaleAndRateModel.getCompareRateIn()),
                            calculateService.calculateProfit(deptHisFreshSaleAndRateModel.getCompareSaleIn(),
                                    deptHisFreshSaleAndRateModel.getCompareRateIn())));
                }
            }
        }
        //组装中类数据
        if(null != classModelList && classModelList.size() > 0){
            Map<String, FreshSaleAndRateModel> hisMap = null;
            Map<String, FreshSaleAndRateModel> currentMap = null;
            Map<String, FreshAllKlModel> klMap = null;
            if(null != classHisFreshSaleAndRateModelList && classHisFreshSaleAndRateModelList.size() > 0){
                hisMap = classHisFreshSaleAndRateModelList.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            if(null != classCurrentFreshSaleAndRateModelList && classCurrentFreshSaleAndRateModelList.size() > 0){
                currentMap = classCurrentFreshSaleAndRateModelList.stream().collect(Collectors.toMap(FreshSaleAndRateModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            if(null != classKlModelList && classKlModelList.size() > 0){
                klMap = classKlModelList.stream().collect(Collectors.toMap(FreshAllKlModel::getId,
                        Function.identity(), (key1, key2) -> key2));
            }
            for(ClassModel classModel : classModelList){
                SubFreshSpecialReportResponse subFreshSpecialReportResponse = new SubFreshSpecialReportResponse();
                subFreshSpecialReportResponse.setId(classModel.getClassId());
                subFreshSpecialReportResponse.setName(classModel.getClassName());
                subFreshSpecialReportResponse.setFieldName("classId");
                if(null != klMap && klMap.size() > 0) {
                    if (klMap.containsKey(classModel.getClassId())) {
                        FreshAllKlModel kl = klMap.get(classModel.getClassId());
                        if (null != freshAllKlModel) {
                            //渗透率
                            subFreshSpecialReportResponse.setPermeability(calculateService.calculatePermeability(kl.getKl(), freshAllKlModel.getKl()));
                        }
                        //是否有销售(0-否，1-是)
                        subFreshSpecialReportResponse.setHaveSale("1");
                        if(null != currentMap && currentMap.containsKey(classModel.getClassId())){
                            if(currentMap.containsKey(classModel.getClassId())){
                                FreshSaleAndRateModel current = currentMap.get(classModel.getClassId());
                                //销售金额
                                subFreshSpecialReportResponse.setSale(current.getSale());
                                subFreshSpecialReportResponse.setSaleIn(current.getSaleIn());
                                //可比销售金额
                                subFreshSpecialReportResponse.setCompareSale(current.getCompareSale());
                                subFreshSpecialReportResponse.setCompareSaleIn(current.getCompareSaleIn());
                                //毛利率
                                subFreshSpecialReportResponse.setProfit(calculateService.calculateProfit(current.getSale(),
                                        current.getRate()));
                                subFreshSpecialReportResponse.setProfitIn(calculateService.calculateProfit(current.getSaleIn(),
                                        current.getRateIn()));
                                //可比毛利率
                                subFreshSpecialReportResponse.setCompareProfit(calculateService.calculateProfit(current.getCompareSale(),
                                        current.getCompareRate()));
                                subFreshSpecialReportResponse.setCompareProfitIn(calculateService.calculateProfit(current.getCompareSaleIn(),
                                        current.getCompareRateIn()));
                                if (null != hisMap && hisMap.size() > 0) {
                                    if (hisMap.containsKey(classModel.getClassId())) {
                                        FreshSaleAndRateModel his = hisMap.get(classModel.getClassId());
                                        //同期可比销售金额
                                        subFreshSpecialReportResponse.setHisCompareSale(his.getCompareSale());
                                        subFreshSpecialReportResponse.setHisCompareSaleIn(his.getCompareSaleIn());
                                        //销售金额可比率
                                        subFreshSpecialReportResponse.setCompareSaleRate(calculateService.calculateCompareSaleRate(
                                                current.getCompareSale(),
                                                his.getCompareSale()));
                                        subFreshSpecialReportResponse.setCompareSaleRateIn(calculateService.calculateCompareSaleRate(
                                                current.getCompareSaleIn(),
                                                his.getCompareSaleIn()));
                                        //同期可比毛利率
                                        subFreshSpecialReportResponse.setHisCompareProfit(calculateService.calculateProfit(his.getCompareSale(),
                                                his.getCompareRate()));
                                        subFreshSpecialReportResponse.setHisCompareProfitIn(calculateService.calculateProfit(his.getCompareSaleIn(),
                                                his.getCompareRateIn()));
                                        //毛利率可比率
                                        subFreshSpecialReportResponse.setCompareProfitRate(calculateService.calculateAddProfitRate(
                                                calculateService.calculateProfit(current.getSale(),current.getRate()),
                                                calculateService.calculateProfit(his.getSale(),his.getRate())));
                                        subFreshSpecialReportResponse.setCompareProfitRateIn(calculateService.calculateAddProfitRate(
                                                calculateService.calculateProfit(current.getSaleIn(),current.getRateIn()),
                                                calculateService.calculateProfit(his.getSaleIn(),his.getRateIn())));
                                    }
                                }
                            }
                        }
                    }
                }
                subFreshSpecialReportList.add(subFreshSpecialReportResponse);
            }
        }
        freshSpecialReportResponse.setList(subFreshSpecialReportList);
        return freshSpecialReportResponse;
    }

    /**
     * 将参数中的字符串转换为List
     * @param pageRequest
     */
    private void transform(FreshSpecialReportRequest pageRequest){
        String provinceId = pageRequest.getProvinceId();
        String areaId = pageRequest.getAreaId();
        String storeId = pageRequest.getStoreId();
        String deptId = pageRequest.getDeptId();
        String category = pageRequest.getCategory();
        String startDateStr = pageRequest.getStartDate();

        if(StringUtils.isNotEmpty(startDateStr)){
            pageRequest.setStart(DateUtils.dateTime("yyyy-MM-dd", startDateStr));
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
     * 根据id获取名称
     * @param ids
     * @return
     */
    private String getNameById(String ids,List<OrganizationForFresh> list){
        String result = "";
        StringBuilder str = new StringBuilder();
        if(StringUtils.isNotEmpty(ids)){
            String[] arr = ids.split(",");
            for(int i = 0; i < arr.length; i++) {
                if (null != list && list.size() > 0) {
                    for (OrganizationForFresh organization : list) {
                        if (arr[i].trim().equals(organization.getProvinceId())) {
                            str.append(organization.getProvinceName()).append(",");
                            break;
                        }else if(arr[i].trim().equals(organization.getAreaId())){
                            str.append(organization.getAreaName()).append(",");
                            break;
                        }else if(arr[i].trim().equals(organization.getStoreId())){
                            str.append(organization.getStoreName()).append(",");
                            break;
                        }
                    }
                }
            }
            if(str.toString().endsWith(",")){
                result = str.toString().substring(0,str.toString().lastIndexOf(","));
            }
        }
        return result;
    }

    /**
     * 切换zypp数据库
     */
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

    /**
     * 切换rms数据库
     */
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
