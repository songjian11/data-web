package com.cs.mobile.api.service.market.impl;

import com.cs.mobile.api.dao.dailyreport.DailyReportDao;
import com.cs.mobile.api.dao.market.MarketDao;
import com.cs.mobile.api.dao.market.MarketForMonthDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.dailyreport.AreaPersonModel;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.market.*;
import com.cs.mobile.api.model.market.request.MarkReportRequest;
import com.cs.mobile.api.model.market.response.DepartmentSaleListResponse;
import com.cs.mobile.api.model.market.response.DepartmentSaleResponse;
import com.cs.mobile.api.model.market.response.TrendReportListResponse;
import com.cs.mobile.api.model.market.response.TrendReportResponse;
import com.cs.mobile.api.service.common.CommonCalculateService;
import com.cs.mobile.api.service.common.CommonService;
import com.cs.mobile.api.service.market.MarketReportService;
import com.cs.mobile.common.core.text.Convert;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 市场报表
 */
@Slf4j
@Service
public class MarketReportServiceImpl implements MarketReportService {
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
    private MarketDao marketDao;
    @Autowired
    private MarketForMonthDao marketForMonthDao;
    @Autowired
    private CommonService commonService;
    @Autowired
    private CommonCalculateService commonCalculateService;
    @Autowired
    private DailyReportDao dailyReportDao;

    /**
     * 查询大类趋势
     * @param param
     * @return
     * @throws Exception
     */
    public TrendReportListResponse queryDeptTrendReport(MarkReportRequest param) throws Exception {
        TrendReportListResponse response = new TrendReportListResponse();
        if(1 == param.getIsMonth()){//查询月趋势
            return queryMonthDeptTrendReport(param);
        }
        List<TrendReportResponse> result = new ArrayList<>();
        //各部门销售
        List<DeptSaleModel> deptSaleModelList = null;
        //负责人
        List<AreaPersonModel> areaPersonList = null;
        //类型（品类 + 分部 + 大类）
        List<DepartmentTypeModel> types = null;
        transform(param);
        //当期时间
        Date start = param.getStart();
        //获取分部下的大类信息
        getDeptIdByPurchaseDept(param);
        types = marketDao.queryDepartmentType();
        deptSaleModelList = marketDao.queryCurrentDeptSale(start,
                param.getProvinceIds(),
                param.getAreaIds(),
                param.getStoreIds(),
                param.getDeptIds());
        //查询负责人
        areaPersonList = dailyReportDao.queryAreaPersonModel();

        //计算数据
        if(null != deptSaleModelList && deptSaleModelList.size() > 0){
            for(DeptSaleModel deptSaleModel : deptSaleModelList){
                TrendReportResponse trendReportResponse = new TrendReportResponse();
                //名称
                trendReportResponse.setName(deptSaleModel.getDeptId());

                //销售
                trendReportResponse.setSale(deptSaleModel.getSale());
                trendReportResponse.setSaleIn(deptSaleModel.getSaleIn());

                //毛利率
                trendReportResponse.setRate(commonCalculateService.calculateProfit(
                        deptSaleModel.getSale(),
                        deptSaleModel.getRate()
                ));
                trendReportResponse.setRateIn(commonCalculateService.calculateProfit(
                        deptSaleModel.getSaleIn(),
                        deptSaleModel.getRateIn()
                ));

                //可比销售
                trendReportResponse.setCompareSale(deptSaleModel.getCompareSale());
                trendReportResponse.setCompareSaleIn(deptSaleModel.getCompareSaleIn());

                //可比毛利率
                trendReportResponse.setCompareRate(commonCalculateService.calculateProfit(
                        deptSaleModel.getCompareSale(),
                        deptSaleModel.getCompareRate()
                ));
                trendReportResponse.setCompareRateIn(commonCalculateService.calculateProfit(
                        deptSaleModel.getCompareSaleIn(),
                        deptSaleModel.getCompareRateIn()
                ));
                result.add(trendReportResponse);
            }
        }
        //给大类添加负责人，并且按照负责人的排序
        if(null != areaPersonList && areaPersonList.size() > 0){
            //排序集合
            List<TrendReportResponse> sort = new ArrayList<>();
            for(AreaPersonModel areaPersonModel : areaPersonList){
                TrendReportResponse sortResponse = null;
                for(TrendReportResponse trendReportResponse : result){
                    if(!"99".equals(trendReportResponse.getName()) && trendReportResponse.getName().equals(areaPersonModel.getAreaName())){//名字配对
                        sortResponse = trendReportResponse;
                        break;
                    }
                }
                if(null != sortResponse){
                    sort.add(sortResponse);
                }
            }
            result.clear();
            result.addAll(sort);
        }
        result = result.stream().sorted(new Comparator<TrendReportResponse>() {
            @Override
            public int compare(TrendReportResponse o1, TrendReportResponse o2) {
                BigDecimal deptId1 = new BigDecimal(StringUtils.isEmpty(o1.getName())?"0":o1.getName());
                BigDecimal deptId2 = new BigDecimal(StringUtils.isEmpty(o2.getName())?"0":o2.getName());
                return deptId1.compareTo(deptId2);
            }
        }).collect(Collectors.toList());
        /*if(null != types && types.size() > 0 && null != result && result.size() > 0){//翻译大类名称
            for(TrendReportResponse trendReportResponse : result){
                for(DepartmentTypeModel type : types){
                    if(StringUtils.isNotEmpty(trendReportResponse.getName()) && trendReportResponse.getName().equals(type.getDeptId())){
                        trendReportResponse.setName(type.getDeptId() + "-" + type.getDeptName());
                        break;
                    }
                }
            }
        }*/
        response.setList(result);
        return response;
    }

    /**
     * 查询部门趋势
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public TrendReportListResponse queryDepartmentTrendReport(MarkReportRequest param) throws Exception {
        TrendReportListResponse response = new TrendReportListResponse();
        if(1 == param.getIsMonth()){//查询月趋势
            return queryMonthDepartmentTrendReport(param);
        }
        List<TrendReportResponse> result = new ArrayList<>();
        //各部门销售
        List<DepartmentSaleModel> departmentSaleList = null;
        //负责人
        List<AreaPersonModel> areaPersonList = null;
        transform(param);
        //当期时间
        Date start = param.getStart();
        departmentSaleList = marketDao.queryCurrentDepartmentSales(start,
                param.getProvinceIds(),
                param.getAreaIds(),
                param.getStoreIds(),
                null,
                null);
        //查询负责人
        areaPersonList = dailyReportDao.queryAreaPersonModel();
        //计算数据
        if(null != departmentSaleList && departmentSaleList.size() > 0){
            for(DepartmentSaleModel departmentSaleModel : departmentSaleList){
                TrendReportResponse trendReportResponse = new TrendReportResponse();
                //名称
                trendReportResponse.setName(departmentSaleModel.getPurchaseDeptName());

                //销售
                trendReportResponse.setSale(departmentSaleModel.getSale());
                trendReportResponse.setSaleIn(departmentSaleModel.getSaleIn());

                //毛利率
                trendReportResponse.setRate(commonCalculateService.calculateProfit(
                        departmentSaleModel.getSale(),
                        departmentSaleModel.getRate()
                ));
                trendReportResponse.setRateIn(commonCalculateService.calculateProfit(
                        departmentSaleModel.getSaleIn(),
                        departmentSaleModel.getRateIn()
                ));

                //可比销售
                trendReportResponse.setCompareSale(departmentSaleModel.getCompareSale());
                trendReportResponse.setCompareSaleIn(departmentSaleModel.getCompareSaleIn());

                //可比毛利率
                trendReportResponse.setCompareRate(commonCalculateService.calculateProfit(
                        departmentSaleModel.getCompareSale(),
                        departmentSaleModel.getCompareRate()
                ));
                trendReportResponse.setCompareRateIn(commonCalculateService.calculateProfit(
                        departmentSaleModel.getCompareSaleIn(),
                        departmentSaleModel.getCompareRateIn()
                ));
                result.add(trendReportResponse);
            }
        }
        //给部门添加负责人，并且按照负责人的排序
        if(null != areaPersonList && areaPersonList.size() > 0){
            //排序集合
            List<TrendReportResponse> sort = new ArrayList<>();
            for(AreaPersonModel areaPersonModel : areaPersonList){
                TrendReportResponse sortResponse = null;
                for(TrendReportResponse trendReportResponse : result){
                    if(!"99".equals(trendReportResponse.getName()) && trendReportResponse.getName().equals(areaPersonModel.getAreaName())){//名字配对
                        sortResponse = trendReportResponse;
                        break;
                    }
                }
                if(null != sortResponse){
                    sort.add(sortResponse);
                }
            }
            result.clear();
            result.addAll(sort);
        }
        response.setList(result);
        return response;
    }

    /**
     * 查询部门销售统计
     * @return
     * @throws Exception
     */
    @Override
    public DepartmentSaleListResponse queryCurrentDepartmentSales(MarkReportRequest param) throws Exception {
        DepartmentSaleListResponse response = new DepartmentSaleListResponse();
        if(1 == param.getIsMonth()){
            return queryMonthDepartmentSales(param);
        }
        List<DepartmentSaleResponse> result = new ArrayList<>();
        //部门销售
        List<DepartmentSaleModel> departmentSaleList = null;
        //往期销售
        List<DepartmentSaleModel> hisDepartmentSaleList = null;
        //实时总销售
        DepartmentSaleModel allSale = null;
        //历史总销售
        DepartmentSaleModel hisAllSale = null;
        //销售目标
        List<DepartmentGoalModel> goals = null;
        //总客流
        KlModel kl = new KlModel();
        //负责人
        List<AreaPersonModel> areaPersonList = null;
        transform(param);
        //当期时间
        Date start = param.getStart();
        String hour = DateUtils.parseDateToStr("HH",new Date());
        //获取当月天数
        int dayNum = DateUtils.getDaysOfMonth(start);
        //同期时间
        Date hisStart = DateUtil.toDate(commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd",start)));
        //查询负责人
        areaPersonList = dailyReportDao.queryAreaPersonModel();
        //查询部门汇总数据
        departmentSaleList = marketDao.queryCurrentDepartmentSales(start,
                param.getProvinceIds(),
                param.getAreaIds(),
                param.getStoreIds(),
                null,
                null);
        //查询总销售
        allSale = marketDao.queryCurrentAllSale(start,
                param.getProvinceIds(),
                param.getAreaIds(),
                param.getStoreIds());
        //查询目标值
        try{
            changeDgDataSource();
            goals = marketDao.queryDepartmentGoal(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    null,
                    null
                    );
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询同期数据
        try{
            changeZyDataSource();
            hisDepartmentSaleList = marketDao.queryHisDepartmentSales(hisStart,
                    hour,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    null,
                    null
            );
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询同期总数据
        hisAllSale = getTotalDepartmentSale(hisDepartmentSaleList);
        //设置总客流
        if(null != allSale){
            kl.setKl(allSale.getKl());
        }

        //计算各个部门汇总行
        result = sumDepartmentSale(departmentSaleList, hisDepartmentSaleList, goals, null, dayNum);

        //给部门添加负责人，并且按照负责人排序
        result = sortpartmentSale(areaPersonList, result);

        //计算总合计行
        sumTotalDepartmentSale(allSale, hisAllSale, goals, kl, dayNum, result);

        response.setList(result);
        return response;
    }

    /**
     * 查询大类销售统计(大类 + 分部 + 品类汇总数据)
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public DepartmentSaleListResponse queryCurrentDeptSales(MarkReportRequest param) throws Exception {
        DepartmentSaleListResponse response = new DepartmentSaleListResponse();
        if(1 == param.getIsMonth()){
            return queryMonthDeptSales(param);
        }
        List<DepartmentSaleResponse> result = new ArrayList<>();
        List<DepartmentSaleResponse> deptResponse = new ArrayList<>();
        List<DepartmentSaleResponse> departmentResponses = new ArrayList<>();
        List<DepartmentSaleResponse> categoryResponses = new ArrayList<>();
        //大类数据
        List<DeptSaleModel> deptSaleModelList = null;
        //品类数据
        List<CategorySaleModel> categorySaleModelList = null;
        //分部数据
        List<DepartmentSaleModel> departmentSaleModelList = null;
        //往期大类数据
        List<DeptSaleModel> hisDeptSaleModelList = null;
        //往期品类数据
        List<CategorySaleModel> hisCategorySaleModelList = null;
        //往期分部数据
        List<DepartmentSaleModel> hisDepartmentSaleModelList = null;
        //实时总销售
        DepartmentSaleModel allSale = null;
        DepartmentSaleModel hisAllSale = null;
        //大类目标值
        List<DeptGoalModel> deptGoalModelList = null;
        //分部目标值
        List<DepartmentGoalModel> departmentGoalModelList = null;
        //品类目标值
        List<CategoryGoalModel> categoryGoalModelList = null;
        //负责人
        List<AreaPersonModel> areaPersonList = null;
        //类型（品类 + 分部 + 大类）
        List<DepartmentTypeModel> types = null;
        //总客流
        KlModel kl = new KlModel();
        //需要查询的品类
        List<String> categorys = null;
        //需要查询的分部
        List<String> purchaseDepts = null;

        transform(param);

        //当期时间
        Date start = param.getStart();
        String hour = DateUtils.parseDateToStr("HH", new Date());
        //获取当月天数
        int dayNum = DateUtils.getDaysOfMonth(start);
        //用来判断是否是合计行下钻
        String purchaseDept = param.getPurchaseDept();
        //同期时间
        Date hisStart = DateUtil.toDate(commonService.getLastYearDay(DateUtils.parseDateToStr("yyyy-MM-dd", start)));
        //查询负责人
        areaPersonList = dailyReportDao.queryAreaPersonModel();
        //查询类型（品类 + 分部 + 大类）
        types = marketDao.queryDepartmentType();
        //查询总销售
        allSale = marketDao.queryCurrentAllSale(start,
                param.getProvinceIds(),
                param.getAreaIds(),
                param.getStoreIds());
        //获取分部下的大类信息
        getDeptIdByPurchaseDept(param);
        categorys = getCategoryByDepts(types, param.getDeptIds());
        purchaseDepts = getPurchaseDeptByDepts(types, param.getDeptIds());

        //查询数据类型(0-全部，1-分部，2-品类，3-大类)
        if(0 == param.getIsQuery()){//查询大类+分部的数据+品类+合计
            //大类数据
            deptSaleModelList = marketDao.queryCurrentDeptSale(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    param.getDeptIds());

            //查询分部数据
            departmentSaleModelList = marketDao.queryCurrentDepartmentSales(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    null,
                    purchaseDepts);

            //查询所有品类数据
            categorySaleModelList = marketDao.queryCurrentCategorySale(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    categorys);

            try{//查询大类往期数据
                changeZyDataSource();
                hisDeptSaleModelList = marketDao.queryHisDeptSale(hisStart,
                        hour,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            //获取品类往期数据
            hisCategorySaleModelList = getCategorySaleModelList(hisDeptSaleModelList);

            try{
                changeZyDataSource();
                //查询分部往期数据
                hisDepartmentSaleModelList = marketDao.queryHisDepartmentSales(hisStart,
                        hour,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            try{
                changeDgDataSource();
                //大类目标值
                deptGoalModelList = marketDao.queryDeptGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);

                //分部目标值
                departmentGoalModelList = marketDao.queryDepartmentGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        null
                );
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //品类目标值
            categoryGoalModelList = getCategoryGoals(types, deptGoalModelList);
            hisAllSale = getTotalDepartmentSale(hisDepartmentSaleModelList);
            //设置总客流
            if(null != allSale){
                kl.setKl(allSale.getKl());
            }
            //计算大类汇总行
            deptResponse = sumDeptSale(deptSaleModelList, hisDeptSaleModelList, deptGoalModelList, kl, dayNum);
            result.addAll(deptResponse);
            //计算分部汇总行
            departmentResponses = sumDepartmentSale(departmentSaleModelList, hisDepartmentSaleModelList, departmentGoalModelList, null, dayNum);
            result.addAll(departmentResponses);
            //计算品类汇总行
            categoryResponses = sumCategorySale(categorySaleModelList, hisCategorySaleModelList, categoryGoalModelList, kl, dayNum);
            result.addAll(categoryResponses);


            //给部门添加负责人，并且按照负责人排序
            result = sortpartmentSale(areaPersonList, result);

            //计算总合计行
            sumTotalDepartmentSale(allSale, hisAllSale, departmentGoalModelList, kl, dayNum, result);
        }else if(1 == param.getIsQuery()){//查询大类+分部数据
            //大类数据
            deptSaleModelList = marketDao.queryCurrentDeptSale(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    param.getDeptIds());

            //查询分部数据
            departmentSaleModelList = marketDao.queryCurrentDepartmentSales(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    null,
                    purchaseDepts);

            try{//查询大类往期数据
                changeZyDataSource();
                hisDeptSaleModelList = marketDao.queryHisDeptSale(hisStart,
                        hour,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            try{
                changeZyDataSource();
                //查询分部往期数据
                hisDepartmentSaleModelList = marketDao.queryHisDepartmentSales(hisStart,
                        hour,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            try{
                changeDgDataSource();
                //大类目标值
                deptGoalModelList = marketDao.queryDeptGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);

                //分部目标值
                departmentGoalModelList = marketDao.queryDepartmentGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        null
                );
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            //设置总客流
            if(null != allSale){
                kl.setKl(allSale.getKl());
            }
            //计算大类汇总行
            deptResponse = sumDeptSale(deptSaleModelList, hisDeptSaleModelList, deptGoalModelList, kl, dayNum);
            result.addAll(deptResponse);
            //计算分部汇总行
            departmentResponses = sumDepartmentSale(departmentSaleModelList, hisDepartmentSaleModelList, departmentGoalModelList, null, dayNum);
            result.addAll(departmentResponses);

            //给部门添加负责人，并且按照负责人排序
            result = sortpartmentSale(areaPersonList, result);

            if("-1".equals(purchaseDept)){//分部ID为-1的时候，表示查询所有（大类  + 分部 + 合计行）
                hisAllSale = getTotalDepartmentSale(hisDepartmentSaleModelList);
                //计算合计汇总行
                sumTotalDepartmentSale(allSale, hisAllSale, departmentGoalModelList, kl, dayNum, result);
            }
        }else if(2 == param.getIsQuery()){//查询大类数据 + 品类数据 + 分部数据
            //大类数据
            deptSaleModelList = marketDao.queryCurrentDeptSale(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    param.getDeptIds());

            //查询分部数据
            departmentSaleModelList = marketDao.queryCurrentDepartmentSales(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    null,
                    purchaseDepts);

            //查询所有品类数据
            categorySaleModelList = marketDao.queryCurrentCategorySale(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    categorys);

            try{//查询大类往期数据
                changeZyDataSource();
                hisDeptSaleModelList = marketDao.queryHisDeptSale(hisStart,
                        hour,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            //获取品类往期数据
            hisCategorySaleModelList = getCategorySaleModelList(hisDeptSaleModelList);

            try{
                changeZyDataSource();
                //查询分部往期数据
                hisDepartmentSaleModelList = marketDao.queryHisDepartmentSales(hisStart,
                        hour,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            try{
                changeDgDataSource();
                //大类目标值
                deptGoalModelList = marketDao.queryDeptGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);

                //分部目标值
                departmentGoalModelList = marketDao.queryDepartmentGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        null
                );
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //品类目标值
            categoryGoalModelList = getCategoryGoals(types, deptGoalModelList);
            //设置总客流
            if(null != allSale){
                kl.setKl(allSale.getKl());
            }
            //计算大类汇总行
            deptResponse = sumDeptSale(deptSaleModelList, hisDeptSaleModelList, deptGoalModelList, kl, dayNum);
            result.addAll(deptResponse);
            //计算分部汇总行
            departmentResponses = sumDepartmentSale(departmentSaleModelList, hisDepartmentSaleModelList, departmentGoalModelList, null, dayNum);
            result.addAll(departmentResponses);
            //计算品类汇总行
            categoryResponses = sumCategorySale(categorySaleModelList, hisCategorySaleModelList, categoryGoalModelList, kl, dayNum);
            result.addAll(categoryResponses);

            //给部门添加负责人，并且按照负责人排序
            result = sortpartmentSale(areaPersonList, result);
        }else{//查询大类数据
            //大类数据
            deptSaleModelList = marketDao.queryCurrentDeptSale(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    param.getDeptIds());

            try{//查询大类往期数据
                changeZyDataSource();
                hisDeptSaleModelList = marketDao.queryHisDeptSale(hisStart,
                        hour,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            try{
                changeDgDataSource();
                //大类目标值
                deptGoalModelList = marketDao.queryDeptGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

            //设置总客流
            if(null != allSale){
                kl.setKl(allSale.getKl());
            }
            //计算大类汇总行
            deptResponse = sumDeptSale(deptSaleModelList, hisDeptSaleModelList, deptGoalModelList, kl, dayNum);
            result.addAll(deptResponse);

            //给部门添加负责人，并且按照负责人排序
            result = sortpartmentSale(areaPersonList, result);
        }
        if(null != types && types.size() > 0 && null != result && result.size() > 0){//翻译大类名称
            for(DepartmentSaleResponse departmentSaleResponse : result){
                for(DepartmentTypeModel type : types){
                    if(StringUtils.isNotEmpty(departmentSaleResponse.getName()) && departmentSaleResponse.getName().equals(type.getDeptId())){
                        departmentSaleResponse.setName(type.getDeptId() + "-" + type.getDeptName());
                        break;
                    }
                }
            }
        }
        response.setList(result);
        return response;
    }

    /**
     * 查询大类月初至昨日趋势
     * @param param
     * @return
     * @throws Exception
     */
    private TrendReportListResponse queryMonthDeptTrendReport(MarkReportRequest param) throws Exception {
        TrendReportListResponse response = new TrendReportListResponse();
        List<TrendReportResponse> result = new ArrayList<>();
        //各部门销售
        List<DeptSaleModel> deptSaleModelList = null;
        //负责人
        List<AreaPersonModel> areaPersonList = null;
        //类型（品类 + 分部 + 大类）
        List<DepartmentTypeModel> types = null;
        transform(param);
        //当期时间
        Date start = DateUtil.getFirstDayForMonth(param.getStart());
        Date end = DateUtils.addDays(param.getStart(), -1);
        //获取分部下的大类信息
        getDeptIdByPurchaseDept(param);
        types = marketDao.queryDepartmentType();
        try{
            changeDgDataSource();
            deptSaleModelList = marketForMonthDao.queryMonthDeptSales(start,
                    end,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    param.getDeptIds());
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //查询负责人
        areaPersonList = dailyReportDao.queryAreaPersonModel();

        //计算数据
        if(null != deptSaleModelList && deptSaleModelList.size() > 0){
            for(DeptSaleModel deptSaleModel : deptSaleModelList){
                TrendReportResponse trendReportResponse = new TrendReportResponse();
                //名称
                trendReportResponse.setName(deptSaleModel.getDeptId());

                //销售
                trendReportResponse.setSale(deptSaleModel.getSale());
                trendReportResponse.setSaleIn(deptSaleModel.getSaleIn());

                //毛利率
                trendReportResponse.setRate(commonCalculateService.calculateProfit(
                        deptSaleModel.getSale(),
                        deptSaleModel.getRate()
                ));
                trendReportResponse.setRateIn(commonCalculateService.calculateProfit(
                        deptSaleModel.getSaleIn(),
                        deptSaleModel.getRateIn()
                ));

                //可比销售
                trendReportResponse.setCompareSale(deptSaleModel.getCompareSale());
                trendReportResponse.setCompareSaleIn(deptSaleModel.getCompareSaleIn());

                //可比毛利率
                trendReportResponse.setCompareRate(commonCalculateService.calculateProfit(
                        deptSaleModel.getCompareSale(),
                        deptSaleModel.getCompareRate()
                ));
                trendReportResponse.setCompareRateIn(commonCalculateService.calculateProfit(
                        deptSaleModel.getCompareSaleIn(),
                        deptSaleModel.getCompareRateIn()
                ));
                result.add(trendReportResponse);
            }
        }
        //给大类添加负责人
        if(null != areaPersonList && areaPersonList.size() > 0){
            //排序集合
            List<TrendReportResponse> sort = new ArrayList<>();
            for(AreaPersonModel areaPersonModel : areaPersonList){
                TrendReportResponse sortResponse = null;
                for(TrendReportResponse trendReportResponse : result){
                    if(!"99".equals(trendReportResponse.getName()) && trendReportResponse.getName().equals(areaPersonModel.getAreaName())){//名字配对
                        sortResponse = trendReportResponse;
                        break;
                    }
                }
                if(null != sortResponse){
                    sort.add(sortResponse);
                }
            }
            result.clear();
            result.addAll(sort);
        }
        result = result.stream().sorted(new Comparator<TrendReportResponse>() {
            @Override
            public int compare(TrendReportResponse o1, TrendReportResponse o2) {
                BigDecimal deptId1 = new BigDecimal(StringUtils.isEmpty(o1.getName())?"0":o1.getName());
                BigDecimal deptId2 = new BigDecimal(StringUtils.isEmpty(o2.getName())?"0":o2.getName());
                return deptId1.compareTo(deptId2);
            }
        }).collect(Collectors.toList());
        /*if(null != types && types.size() > 0 && null != result && result.size() > 0){//翻译大类名称
            for(TrendReportResponse trendReportResponse : result){
                for(DepartmentTypeModel type : types){
                    if(StringUtils.isNotEmpty(trendReportResponse.getName()) && trendReportResponse.getName().equals(type.getDeptId())){
                        trendReportResponse.setName(type.getDeptId() + "-" + type.getDeptName());
                        break;
                    }
                }
            }
        }*/
        response.setList(result);
        return response;
    }

    /**
     * 查询月出至昨日汇总(大类 + 分部 + 品类汇总数据)
     * @param param
     * @return
     * @throws Exception
     */
    private DepartmentSaleListResponse queryMonthDeptSales(MarkReportRequest param)throws Exception {
        DepartmentSaleListResponse response = new DepartmentSaleListResponse();
        List<DepartmentSaleResponse> result = new ArrayList<>();
        List<DepartmentSaleResponse> deptResponse = new ArrayList<>();
        List<DepartmentSaleResponse> departmentResponses = new ArrayList<>();
        List<DepartmentSaleResponse> categoryResponses = new ArrayList<>();

        //大类数据
        List<DeptSaleModel> deptSaleModelList = null;
        //品类数据
        List<CategorySaleModel> categorySaleModelList = null;
        //分部数据
        List<DepartmentSaleModel> departmentSaleModelList = null;

        //往期大类数据
        List<DeptSaleModel> hisDeptSaleModelList = null;
        //往期品类数据
        List<CategorySaleModel> hisCategorySaleModelList = null;
        //往期分部数据
        List<DepartmentSaleModel> hisDepartmentSaleModelList = null;

        //实时总销售
        DepartmentSaleModel allSale = null;
        DepartmentSaleModel hisAllSale = null;

        //大类目标值
        List<DeptGoalModel> deptGoalModelList = null;
        //分部目标值
        List<DepartmentGoalModel> departmentGoalModelList = null;
        //品类目标值
        List<CategoryGoalModel> categoryGoalModelList = null;

        //负责人
        List<AreaPersonModel> areaPersonList = null;
        //类型（品类 + 分部 + 大类）
        List<DepartmentTypeModel> types = null;

        //总客流
        KlModel kl = new KlModel();
        //大类客流
        List<KlModel> deptKls = null;
        //品类客流
        List<KlModel> categoryKls = null;

        //需要查询的品类
        List<String> categorys = null;
        //需要查询的分部
        List<String> purchaseDepts = null;

        transform(param);

        //当期时间
        Date start = DateUtil.getFirstDayForMonth(param.getStart());
        Date end = DateUtils.addDays(param.getStart(),-1);
        //用来判断是否是合计行下钻
        String purchaseDept = param.getPurchaseDept();
        //月数
        int monthNum = 1;
        //查询负责人
        areaPersonList = dailyReportDao.queryAreaPersonModel();
        //查询类型（品类 + 分部 + 大类）
        types = marketDao.queryDepartmentType();
        //查询总客流
        try{
            changeZyDataSource();
            kl = marketForMonthDao.queryAllKl(start,
                    end,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds());
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //获取分部下的大类信息
        getDeptIdByPurchaseDept(param);
        categorys = getCategoryByDepts(types, param.getDeptIds());
        purchaseDepts = getPurchaseDeptByDepts(types, param.getDeptIds());

        //查询数据类型(0-全部，1-分部，2-品类，3-大类)
        if(0 == param.getIsQuery()){//查询大类+分部的数据+品类+合计
            try{
                changeDgDataSource();
                //查询大类销售数据
                deptSaleModelList = marketForMonthDao.queryMonthDeptSales(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //查询大类同期销售数据
                hisDeptSaleModelList = marketForMonthDao.queryHisMonthDeptSales(DateUtil.addYear(start,-1),
                        DateUtil.addYear(end,-1),
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //查询分部销售数据
                departmentSaleModelList = marketForMonthDao.queryMonthDepartmentSales(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        purchaseDepts);
                //查询分部同期销售数据
                hisDepartmentSaleModelList = marketForMonthDao.queryHisMonthDepartmentSales(DateUtil.addYear(start,-1),
                        DateUtil.addYear(end,-1),
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        purchaseDepts);
                //查询品类销售数据
                categorySaleModelList = marketForMonthDao.queryMonthCategorySales(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        categorys);
                //查询品类同期销售数据
                hisCategorySaleModelList = marketForMonthDao.queryHisMonthCategorySales(DateUtil.addYear(start,-1),
                        DateUtil.addYear(end,-1),
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        categorys);
                //所有大类目标值
                deptGoalModelList = marketDao.queryDeptGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);
                //所有分部目标值
                departmentGoalModelList = marketDao.queryDepartmentGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        null
                );
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            try{
                changeZyDataSource();
                //大类客流
                deptKls = marketForMonthDao.queryDeptKl(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //查询品类客流
                categoryKls = marketForMonthDao.queryCategoryKl(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        categorys);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //品类目标值
            categoryGoalModelList = getCategoryGoals(types, deptGoalModelList);
            //总销售
            allSale = getTotalDepartmentSale(departmentSaleModelList);
            //同期总销售
            hisAllSale = getTotalDepartmentSale(hisDepartmentSaleModelList);
            //将大类客流整合到大类销售中
            if(null != deptSaleModelList && deptSaleModelList.size() > 0 && null != deptKls && deptKls.size() > 0){
                for(DeptSaleModel deptSaleModel : deptSaleModelList){
                    for(KlModel deptKl : deptKls){
                        if(StringUtils.isNotEmpty(deptKl.getId()) && deptKl.getId().equals(deptSaleModel.getDeptId())){
                            deptSaleModel.setKl(deptKl.getKl());
                            break;
                        }
                    }
                }
            }
            //将品类类客流整合到品类销售中
            if(null != categorySaleModelList && categorySaleModelList.size() > 0 && null != categoryKls && categoryKls.size() > 0){
                for(CategorySaleModel categorySaleModel : categorySaleModelList){
                    for(KlModel categoryKl : categoryKls){
                        if(StringUtils.isNotEmpty(categoryKl.getId()) && categoryKl.getId().equals(categorySaleModel.getCategoryName())){
                            categorySaleModel.setKl(categoryKl.getKl());
                            break;
                        }
                    }
                }
            }
            //计算大类汇总行
            deptResponse = sumDeptSale(deptSaleModelList, hisDeptSaleModelList, deptGoalModelList, kl, monthNum);
            result.addAll(deptResponse);
            //计算分部汇总行
            departmentResponses = sumDepartmentSale(departmentSaleModelList, hisDepartmentSaleModelList, departmentGoalModelList, null, monthNum);
            result.addAll(departmentResponses);
            //计算品类汇总行
            categoryResponses = sumCategorySale(categorySaleModelList, hisCategorySaleModelList, categoryGoalModelList, kl, monthNum);
            result.addAll(categoryResponses);
            //给部门添加负责人，并且按照负责人排序
            result = sortpartmentSale(areaPersonList, result);
            if(null != kl){
                allSale.setKl(kl.getKl());
            }
            //计算总合计行
            sumTotalDepartmentSale(allSale, hisAllSale, departmentGoalModelList, kl, monthNum, result);
        }else if(1 == param.getIsQuery()){//查询大类+分部数据
            try{
                changeDgDataSource();
                //查询大类销售数据
                deptSaleModelList = marketForMonthDao.queryMonthDeptSales(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //查询大类同期销售数据
                hisDeptSaleModelList = marketForMonthDao.queryHisMonthDeptSales(DateUtil.addYear(start,-1),
                        DateUtil.addYear(end,-1),
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //查询分部销售数据
                departmentSaleModelList = marketForMonthDao.queryMonthDepartmentSales(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        purchaseDepts);
                //查询分部同期销售数据
                hisDepartmentSaleModelList = marketForMonthDao.queryHisMonthDepartmentSales(DateUtil.addYear(start,-1),
                        DateUtil.addYear(end,-1),
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        purchaseDepts);
                //所有大类目标值
                deptGoalModelList = marketDao.queryDeptGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);
                //所有分部目标值
                departmentGoalModelList = marketDao.queryDepartmentGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        null
                );
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            try{
                changeZyDataSource();
                //大类客流
                deptKls = marketForMonthDao.queryDeptKl(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //将大类客流整合到大类销售中
            if(null != deptSaleModelList && deptSaleModelList.size() > 0 && null != deptKls && deptKls.size() > 0){
                for(DeptSaleModel deptSaleModel : deptSaleModelList){
                    for(KlModel deptKl : deptKls){
                        if(StringUtils.isNotEmpty(deptKl.getId()) && deptKl.getId().equals(deptSaleModel.getDeptId())){
                            deptSaleModel.setKl(deptKl.getKl());
                            break;
                        }
                    }
                }
            }
            //计算大类汇总行
            deptResponse = sumDeptSale(deptSaleModelList, hisDeptSaleModelList, deptGoalModelList, kl, monthNum);
            result.addAll(deptResponse);
            //计算分部汇总行
            departmentResponses = sumDepartmentSale(departmentSaleModelList, hisDepartmentSaleModelList, departmentGoalModelList, null, monthNum);
            result.addAll(departmentResponses);
            //给部门添加负责人，并且按照负责人排序
            result = sortpartmentSale(areaPersonList, result);
            if("-1".equals(purchaseDept)){//分部ID为-1的时候，表示查询所有（大类  + 分部 + 合计行）
                //同期总销售
                hisAllSale = getTotalDepartmentSale(hisDepartmentSaleModelList);
                allSale = getTotalDepartmentSale(departmentSaleModelList);
                if(null != kl){
                    allSale.setKl(kl.getKl());
                }
                sumTotalDepartmentSale(allSale, hisAllSale, departmentGoalModelList, kl, monthNum, result);
            }
        }else if(2 == param.getIsQuery()){//查询大类数据 + 品类数据 + 分部数据
            try{
                changeDgDataSource();
                //查询大类销售数据
                deptSaleModelList = marketForMonthDao.queryMonthDeptSales(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //查询大类同期销售数据
                hisDeptSaleModelList = marketForMonthDao.queryHisMonthDeptSales(DateUtil.addYear(start,-1),
                        DateUtil.addYear(end,-1),
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //查询分部销售数据
                departmentSaleModelList = marketForMonthDao.queryMonthDepartmentSales(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        purchaseDepts);
                //查询分部同期销售数据
                hisDepartmentSaleModelList = marketForMonthDao.queryHisMonthDepartmentSales(DateUtil.addYear(start,-1),
                        DateUtil.addYear(end,-1),
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        purchaseDepts);
                //查询品类销售数据
                categorySaleModelList = marketForMonthDao.queryMonthCategorySales(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        categorys);
                //查询品类同期销售数据
                hisCategorySaleModelList = marketForMonthDao.queryHisMonthCategorySales(DateUtil.addYear(start,-1),
                        DateUtil.addYear(end,-1),
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        categorys);
                //所有大类目标值
                deptGoalModelList = marketDao.queryDeptGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);
                //所有分部目标值
                departmentGoalModelList = marketDao.queryDepartmentGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null,
                        null
                );
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            try{
                changeZyDataSource();
                //大类客流
                deptKls = marketForMonthDao.queryDeptKl(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //查询品类客流
                categoryKls = marketForMonthDao.queryCategoryKl(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        categorys);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //品类目标值
            categoryGoalModelList = getCategoryGoals(types, deptGoalModelList);
            //将大类客流整合到大类销售中
            if(null != deptSaleModelList && deptSaleModelList.size() > 0 && null != deptKls && deptKls.size() > 0){
                for(DeptSaleModel deptSaleModel : deptSaleModelList){
                    for(KlModel deptKl : deptKls){
                        if(StringUtils.isNotEmpty(deptKl.getId()) && deptKl.getId().equals(deptSaleModel.getDeptId())){
                            deptSaleModel.setKl(deptKl.getKl());
                            break;
                        }
                    }
                }
            }
            //将品类类客流整合到品类销售中
            if(null != categorySaleModelList && categorySaleModelList.size() > 0 && null != categoryKls && categoryKls.size() > 0){
                for(CategorySaleModel categorySaleModel : categorySaleModelList){
                    for(KlModel categoryKl : categoryKls){
                        if(StringUtils.isNotEmpty(categoryKl.getId()) && categoryKl.getId().equals(categorySaleModel.getCategoryName())){
                            categorySaleModel.setKl(categoryKl.getKl());
                            break;
                        }
                    }
                }
            }
            //计算大类汇总行
            deptResponse = sumDeptSale(deptSaleModelList, hisDeptSaleModelList, deptGoalModelList, kl, monthNum);
            result.addAll(deptResponse);
            //计算分部汇总行
            departmentResponses = sumDepartmentSale(departmentSaleModelList, hisDepartmentSaleModelList, departmentGoalModelList, null, monthNum);
            result.addAll(departmentResponses);
            //计算品类汇总行
            categoryResponses = sumCategorySale(categorySaleModelList, hisCategorySaleModelList, categoryGoalModelList, kl, monthNum);
            result.addAll(categoryResponses);
            //给部门添加负责人，并且按照负责人排序
            result = sortpartmentSale(areaPersonList, result);
        }else{//查询大类数据
            try{
                changeDgDataSource();
                //查询大类销售数据
                deptSaleModelList = marketForMonthDao.queryMonthDeptSales(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //查询大类同期销售数据
                hisDeptSaleModelList = marketForMonthDao.queryHisMonthDeptSales(DateUtil.addYear(start,-1),
                        DateUtil.addYear(end,-1),
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
                //所有大类目标值
                deptGoalModelList = marketDao.queryDeptGoal(start,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            try{
                changeZyDataSource();
                //大类客流
                deptKls = marketForMonthDao.queryDeptKl(start,
                        end,
                        param.getProvinceIds(),
                        param.getAreaIds(),
                        param.getStoreIds(),
                        param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
            //将大类客流整合到大类销售中
            if(null != deptSaleModelList && deptSaleModelList.size() > 0 && null != deptKls && deptKls.size() > 0){
                for(DeptSaleModel deptSaleModel : deptSaleModelList){
                    for(KlModel deptKl : deptKls){
                        if(StringUtils.isNotEmpty(deptKl.getId()) && deptKl.getId().equals(deptSaleModel.getDeptId())){
                            deptSaleModel.setKl(deptKl.getKl());
                            break;
                        }
                    }
                }
            }
            //计算大类汇总行
            deptResponse = sumDeptSale(deptSaleModelList, hisDeptSaleModelList, deptGoalModelList, kl, monthNum);
            result.addAll(deptResponse);
            //给部门添加负责人，并且按照负责人排序
            result = sortpartmentSale(areaPersonList, result);
        }
        if(null != types && types.size() > 0 && null != result && result.size() > 0){//翻译大类名称
            for(DepartmentSaleResponse departmentSaleResponse : result){
                for(DepartmentTypeModel type : types){
                    if(StringUtils.isNotEmpty(departmentSaleResponse.getName()) && departmentSaleResponse.getName().equals(type.getDeptId())){
                        departmentSaleResponse.setName(type.getDeptId() + "-" + type.getDeptName());
                        break;
                    }
                }
            }
        }
        response.setList(result);
        return response;
    }

    /**
     * 查询部门月初至昨日趋势
     * @param param
     * @return
     * @throws Exception
     */
    private TrendReportListResponse queryMonthDepartmentTrendReport(MarkReportRequest param) throws Exception{
        TrendReportListResponse response = new TrendReportListResponse();
        List<TrendReportResponse> result = new ArrayList<>();
        //各部门销售
        List<DepartmentSaleModel> departmentSaleList = null;
        //负责人
        List<AreaPersonModel> areaPersonList = null;
        transform(param);
        //当期时间
        Date start = DateUtil.getFirstDayForMonth(param.getStart());
        Date end = DateUtils.addDays(param.getStart(), -1);
        try{
            changeDgDataSource();
            departmentSaleList = marketForMonthDao.queryMonthDepartmentSales(start,
                    end,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    null,
                    null);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询负责人
        areaPersonList = dailyReportDao.queryAreaPersonModel();
        //计算数据
        if(null != departmentSaleList && departmentSaleList.size() > 0){
            for(DepartmentSaleModel departmentSaleModel : departmentSaleList){
                TrendReportResponse trendReportResponse = new TrendReportResponse();
                //名称
                trendReportResponse.setName(departmentSaleModel.getPurchaseDeptName());

                //销售
                trendReportResponse.setSale(departmentSaleModel.getSale());
                trendReportResponse.setSaleIn(departmentSaleModel.getSaleIn());

                //毛利率
                trendReportResponse.setRate(commonCalculateService.calculateProfit(
                        departmentSaleModel.getSale(),
                        departmentSaleModel.getRate()
                ));
                trendReportResponse.setRateIn(commonCalculateService.calculateProfit(
                        departmentSaleModel.getSaleIn(),
                        departmentSaleModel.getRateIn()
                ));

                //可比销售
                trendReportResponse.setCompareSale(departmentSaleModel.getCompareSale());
                trendReportResponse.setCompareSaleIn(departmentSaleModel.getCompareSaleIn());

                //可比毛利率
                trendReportResponse.setCompareRate(commonCalculateService.calculateProfit(
                        departmentSaleModel.getCompareSale(),
                        departmentSaleModel.getCompareRate()
                ));
                trendReportResponse.setCompareRateIn(commonCalculateService.calculateProfit(
                        departmentSaleModel.getCompareSaleIn(),
                        departmentSaleModel.getCompareRateIn()
                ));
                result.add(trendReportResponse);
            }
        }
        //给部门添加负责人，并且按照负责人的排序
        if(null != areaPersonList && areaPersonList.size() > 0){
            //排序集合
            List<TrendReportResponse> sort = new ArrayList<>();
            for(AreaPersonModel areaPersonModel : areaPersonList){
                TrendReportResponse sortResponse = null;
                for(TrendReportResponse trendReportResponse : result){
                    if(!"99".equals(trendReportResponse.getName()) && trendReportResponse.getName().equals(areaPersonModel.getAreaName())){//名字配对
                        sortResponse = trendReportResponse;
                        break;
                    }
                }
                if(null != sortResponse){
                    sort.add(sortResponse);
                }
            }
            result.clear();
            result.addAll(sort);
        }
        response.setList(result);
        return response;
    }

    /**
     * 查询月初至昨日分部统计
     * @return
     * @throws Exception
     */
    private DepartmentSaleListResponse queryMonthDepartmentSales(MarkReportRequest param)throws Exception{
        DepartmentSaleListResponse response = new DepartmentSaleListResponse();
        List<DepartmentSaleResponse> result = new ArrayList<>();
        //部门销售
        List<DepartmentSaleModel> departmentSaleList = null;
        //往期销售
        List<DepartmentSaleModel> hisDepartmentSaleList = null;
        //总销售
        DepartmentSaleModel allSale = null;
        //历史总销售
        DepartmentSaleModel hisAllSale = null;
        //销售目标
        List<DepartmentGoalModel> goals = null;
        //总客流
        KlModel kl = new KlModel();
        kl.setKl("1");
        //月数
        int monthNum = 1;
        //负责人
        List<AreaPersonModel> areaPersonList = null;
        transform(param);
        //当期时间
        Date start = DateUtil.getFirstDayForMonth(param.getStart());
        Date end = DateUtils.addDays(param.getStart(), -1);
        Date hisStart = DateUtil.addYear(DateUtil.getFirstDayForMonth(param.getStart()),-1);
        Date hisEnd = DateUtil.addYear(DateUtils.addDays(param.getStart(), -1),-1);
        //查询负责人
        areaPersonList = dailyReportDao.queryAreaPersonModel();
        try{
            changeDgDataSource();
            departmentSaleList = marketForMonthDao.queryMonthDepartmentSales(start,
                    end,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    null,
                    null);

            hisDepartmentSaleList = marketForMonthDao.queryHisMonthDepartmentSales(hisStart,
                    hisEnd,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    null,
                    null);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //获取总销售
        allSale = getTotalDepartmentSale(departmentSaleList);
        //获取历史总销售
        hisAllSale = getTotalDepartmentSale(hisDepartmentSaleList);
        //查询目标值
        try{
            changeDgDataSource();
            goals = marketDao.queryDepartmentGoal(start,
                    param.getProvinceIds(),
                    param.getAreaIds(),
                    param.getStoreIds(),
                    null,
                    null
            );
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //计算各个部门汇总行
        result = sumDepartmentSale(departmentSaleList, hisDepartmentSaleList, goals, null, monthNum);

        //给部门添加负责人，并且按照负责人排序
        result = sortpartmentSale(areaPersonList, result);

        //计算总合计行
        if(null != allSale){
            allSale.setKl("1");
        }
        sumTotalDepartmentSale(allSale, hisAllSale, goals, kl, monthNum, result);
        response.setList(result);
        return response;
    }

    /**
     * 获取合计数据
     * @param saleList
     * @return
     */
    private DepartmentSaleModel getTotalDepartmentSale(List<DepartmentSaleModel> saleList){
        DepartmentSaleModel departmentSaleModel = new DepartmentSaleModel();
        departmentSaleModel.setPurchaseDeptName("合计");
        if(null != saleList && saleList.size() > 0){
            for (DepartmentSaleModel sale : saleList) {
                //合计销售
                departmentSaleModel.setSale(commonCalculateService.adds(departmentSaleModel.getSale(), sale.getSale()));
                departmentSaleModel.setSaleIn(commonCalculateService.adds(departmentSaleModel.getSaleIn(), sale.getSaleIn()));
                departmentSaleModel.setCompareSale(commonCalculateService.adds(departmentSaleModel.getCompareSale(), sale.getCompareSale()));
                departmentSaleModel.setCompareSaleIn(commonCalculateService.adds(departmentSaleModel.getCompareSaleIn(), sale.getCompareSaleIn()));

                //合计毛利额
                departmentSaleModel.setRate(commonCalculateService.adds(departmentSaleModel.getRate(), sale.getRate()));
                departmentSaleModel.setRateIn(commonCalculateService.adds(departmentSaleModel.getRateIn(), sale.getRateIn()));
                departmentSaleModel.setCompareRate(commonCalculateService.adds(departmentSaleModel.getCompareRate(), sale.getCompareRate()));
                departmentSaleModel.setCompareRateIn(commonCalculateService.adds(departmentSaleModel.getCompareRateIn(), sale.getCompareRateIn()));
            }
        }
        return departmentSaleModel;
    }



    /**
     * 计算品类各汇总行
     * @param categorySaleModelList
     * @param hisCategorySaleModelList
     * @param categoryGoalModelList
     * @param kl
     * @param num
     * @return
     */
    private List<DepartmentSaleResponse> sumCategorySale(List<CategorySaleModel> categorySaleModelList,
                                                         List<CategorySaleModel> hisCategorySaleModelList,
                                                         List<CategoryGoalModel> categoryGoalModelList,
                                                         KlModel kl,
                                                         int num){
        List<DepartmentSaleResponse> result = new ArrayList<>();
        if(null != categorySaleModelList && categorySaleModelList.size() > 0){

            Map<String, CategorySaleModel> hisCategorySaleMap = null;
            if(null != hisCategorySaleModelList && hisCategorySaleModelList.size() > 0){
                hisCategorySaleMap = hisCategorySaleModelList.stream().collect(Collectors.toMap(CategorySaleModel::getCategoryName,
                        Function.identity(), (key1, key2) -> key2));
            }

            Map<String,CategoryGoalModel> categoryGoalMap = null;
            if(null != categoryGoalModelList && categoryGoalModelList.size() > 0){
                categoryGoalMap = categoryGoalModelList.stream().collect(Collectors.toMap(CategoryGoalModel::getCategoryName,
                        Function.identity(), (key1, key2) -> key2));
            }

            for(CategorySaleModel categorySaleModel : categorySaleModelList){
                DepartmentSaleResponse departmentSaleResponse = new DepartmentSaleResponse();
                //名称
                departmentSaleResponse.setName(categorySaleModel.getCategoryName());
                //计算渗透率
                if(null != kl){
                    departmentSaleResponse.setPermeability(commonCalculateService.calculatePermeability(
                            categorySaleModel.getKl(),
                            kl.getKl()
                    ));
                }

                if(null != categoryGoalMap && categoryGoalMap.size() > 0){
                    CategoryGoalModel goal = categoryGoalMap.get(categorySaleModel.getCategoryName());
                    if(null != goal){
                        //销售目标值
                        departmentSaleResponse.setSaleGoal(commonCalculateService.calculateGoalSale(
                                goal.getSale(),
                                num
                        ));
                        //销售达成率
                        departmentSaleResponse.setSaleGoalRate(commonCalculateService.calculateSaleAchiRate(
                                categorySaleModel.getSale(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                        departmentSaleResponse.setSaleGoalRateIn(commonCalculateService.calculateSaleAchiRate(
                                categorySaleModel.getSaleIn(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                        departmentSaleResponse.setCompareSaleGoalRate(commonCalculateService.calculateSaleAchiRate(
                                categorySaleModel.getCompareSale(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                        departmentSaleResponse.setCompareSaleGoalRateIn(commonCalculateService.calculateSaleAchiRate(
                                categorySaleModel.getCompareSaleIn(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                    }
                }
                //销售额
                departmentSaleResponse.setSale(categorySaleModel.getSale());
                departmentSaleResponse.setSaleIn(categorySaleModel.getSaleIn());
                departmentSaleResponse.setCompareSale(categorySaleModel.getCompareSale());
                departmentSaleResponse.setCompareSaleIn(categorySaleModel.getCompareSaleIn());
                //毛利额
                departmentSaleResponse.setProfit(categorySaleModel.getRate());
                departmentSaleResponse.setProfitIn(categorySaleModel.getRateIn());
                departmentSaleResponse.setCompareProfit(categorySaleModel.getCompareRate());
                departmentSaleResponse.setCompareProfitIn(categorySaleModel.getCompareRateIn());

                if(null != hisCategorySaleMap && hisCategorySaleMap.size() > 0){
                    CategorySaleModel his = hisCategorySaleMap.get(categorySaleModel.getCategoryName());
                    if(null != his){
                        //往期销售额
                        departmentSaleResponse.setHisSale(his.getSale());
                        departmentSaleResponse.setHisSaleIn(his.getSaleIn());
                        departmentSaleResponse.setCompareHisSale(his.getCompareSale());
                        departmentSaleResponse.setCompareHisSaleIn(his.getCompareSaleIn());
                        //销售增长率
                        departmentSaleResponse.setSaleAddRate(commonCalculateService.calculateSaleRate(
                                categorySaleModel.getSale(),
                                his.getSale()
                        ));
                        departmentSaleResponse.setSaleAddRateIn(commonCalculateService.calculateSaleRate(
                                categorySaleModel.getSaleIn(),
                                his.getSaleIn()
                        ));
                        departmentSaleResponse.setCompareSaleAddRate(commonCalculateService.calculateSaleRate(
                                categorySaleModel.getCompareSale(),
                                his.getCompareSale()
                        ));
                        departmentSaleResponse.setCompareSaleAddRateIn(commonCalculateService.calculateSaleRate(
                                categorySaleModel.getCompareSaleIn(),
                                his.getCompareSaleIn()
                        ));
                        //往期毛利额
                        departmentSaleResponse.setHisProfit(his.getRate());
                        departmentSaleResponse.setHisProfitIn(his.getRateIn());
                        departmentSaleResponse.setCompareHisProfit(his.getCompareRate());
                        departmentSaleResponse.setCompareHisProfitIn(his.getCompareRateIn());
                        //毛利率增长率
                        departmentSaleResponse.setProfitAddRate(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                categorySaleModel.getSale(),
                                                categorySaleModel.getRate()),
                                        commonCalculateService.calculateProfit(
                                                his.getSale(),
                                                his.getRate())));

                        departmentSaleResponse.setProfitAddRateIn(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                categorySaleModel.getSaleIn(),
                                                categorySaleModel.getRateIn()),
                                        commonCalculateService.calculateProfit(
                                                his.getSaleIn(),
                                                his.getRateIn())));

                        departmentSaleResponse.setCompareProfitAddRate(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                categorySaleModel.getCompareSale(),
                                                categorySaleModel.getCompareRate()),
                                        commonCalculateService.calculateProfit(
                                                his.getCompareSale(),
                                                his.getCompareRate())));

                        departmentSaleResponse.setCompareProfitAddRateIn(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                categorySaleModel.getCompareSaleIn(),
                                                categorySaleModel.getCompareRateIn()),
                                        commonCalculateService.calculateProfit(
                                                his.getCompareSaleIn(),
                                                his.getCompareRateIn())));
                    }
                }
                result.add(departmentSaleResponse);
            }
        }
        return result;
    }

    /**
     * 计算各大类汇总行
     * @param deptSaleModelList
     * @param hisDeptSaleModelList
     * @param deptGoalModelList
     * @param kl
     * @param num(天/月数)
     * @return
     */
    private List<DepartmentSaleResponse> sumDeptSale(List<DeptSaleModel> deptSaleModelList,
                                                     List<DeptSaleModel> hisDeptSaleModelList,
                                                     List<DeptGoalModel> deptGoalModelList,
                                                     KlModel kl,
                                                     int num){
        List<DepartmentSaleResponse> result = new ArrayList<>();
        if(null != deptSaleModelList && deptSaleModelList.size() > 0){

            Map<String, DeptSaleModel> hisDeptSaleMap = null;
            if(null != hisDeptSaleModelList && hisDeptSaleModelList.size() > 0){
                hisDeptSaleMap = hisDeptSaleModelList.stream().collect(Collectors.toMap(DeptSaleModel::getDeptId,
                        Function.identity(), (key1, key2) -> key2));
            }
            Map<String,DeptGoalModel> deptGoalMap = null;
            if(null != deptGoalModelList && deptGoalModelList.size() > 0){
                deptGoalMap = deptGoalModelList.stream().collect(Collectors.toMap(DeptGoalModel::getDeptId,
                        Function.identity(), (key1, key2) -> key2));
            }

            for(DeptSaleModel deptSaleModel : deptSaleModelList){
                DepartmentSaleResponse departmentSaleResponse = new DepartmentSaleResponse();
                //ID
                departmentSaleResponse.setId(deptSaleModel.getDeptId());
                //名称
                departmentSaleResponse.setName(deptSaleModel.getDeptId());
                //计算渗透率
                if(null != kl){
                    departmentSaleResponse.setPermeability(commonCalculateService.calculatePermeability(
                            deptSaleModel.getKl(),
                            kl.getKl()
                    ));
                }

                if(null != deptGoalMap && deptGoalMap.size() > 0){
                    DeptGoalModel goal = deptGoalMap.get(deptSaleModel.getDeptId());
                    if(null != goal){
                        //销售目标值
                        departmentSaleResponse.setSaleGoal(commonCalculateService.calculateGoalSale(
                                goal.getSale(),
                                num
                        ));
                        //销售达成率
                        departmentSaleResponse.setSaleGoalRate(commonCalculateService.calculateSaleAchiRate(
                                deptSaleModel.getSale(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                        departmentSaleResponse.setSaleGoalRateIn(commonCalculateService.calculateSaleAchiRate(
                                deptSaleModel.getSaleIn(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                        departmentSaleResponse.setCompareSaleGoalRate(commonCalculateService.calculateSaleAchiRate(
                                deptSaleModel.getCompareSale(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                        departmentSaleResponse.setCompareSaleGoalRateIn(commonCalculateService.calculateSaleAchiRate(
                                deptSaleModel.getCompareSaleIn(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                    }
                }
                //销售额
                departmentSaleResponse.setSale(deptSaleModel.getSale());
                departmentSaleResponse.setSaleIn(deptSaleModel.getSaleIn());
                departmentSaleResponse.setCompareSale(deptSaleModel.getCompareSale());
                departmentSaleResponse.setCompareSaleIn(deptSaleModel.getCompareSaleIn());
                //毛利额
                departmentSaleResponse.setProfit(deptSaleModel.getRate());
                departmentSaleResponse.setProfitIn(deptSaleModel.getRateIn());
                departmentSaleResponse.setCompareProfit(deptSaleModel.getCompareRate());
                departmentSaleResponse.setCompareProfitIn(deptSaleModel.getCompareRateIn());

                if(null != hisDeptSaleMap && hisDeptSaleMap.size() > 0){
                    DeptSaleModel his = hisDeptSaleMap.get(deptSaleModel.getDeptId());
                    if(null != his){
                        //往期销售额
                        departmentSaleResponse.setHisSale(his.getSale());
                        departmentSaleResponse.setHisSaleIn(his.getSaleIn());
                        departmentSaleResponse.setCompareHisSale(his.getCompareSale());
                        departmentSaleResponse.setCompareHisSaleIn(his.getCompareSaleIn());
                        //销售增长率
                        departmentSaleResponse.setSaleAddRate(commonCalculateService.calculateSaleRate(
                                deptSaleModel.getSale(),
                                his.getSale()
                        ));
                        departmentSaleResponse.setSaleAddRateIn(commonCalculateService.calculateSaleRate(
                                deptSaleModel.getSaleIn(),
                                his.getSaleIn()
                        ));
                        departmentSaleResponse.setCompareSaleAddRate(commonCalculateService.calculateSaleRate(
                                deptSaleModel.getCompareSale(),
                                his.getCompareSale()
                        ));
                        departmentSaleResponse.setCompareSaleAddRateIn(commonCalculateService.calculateSaleRate(
                                deptSaleModel.getCompareSaleIn(),
                                his.getCompareSaleIn()
                        ));
                        //往期毛利额
                        departmentSaleResponse.setHisProfit(his.getRate());
                        departmentSaleResponse.setHisProfitIn(his.getRateIn());
                        departmentSaleResponse.setCompareHisProfit(his.getCompareRate());
                        departmentSaleResponse.setCompareHisProfitIn(his.getCompareRateIn());
                        //毛利率增长率
                        departmentSaleResponse.setProfitAddRate(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                deptSaleModel.getSale(),
                                                deptSaleModel.getRate()),
                                        commonCalculateService.calculateProfit(
                                                his.getSale(),
                                                his.getRate())));

                        departmentSaleResponse.setProfitAddRateIn(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                deptSaleModel.getSaleIn(),
                                                deptSaleModel.getRateIn()),
                                        commonCalculateService.calculateProfit(
                                                his.getSaleIn(),
                                                his.getRateIn())));

                        departmentSaleResponse.setCompareProfitAddRate(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                deptSaleModel.getCompareSale(),
                                                deptSaleModel.getCompareRate()),
                                        commonCalculateService.calculateProfit(
                                                his.getCompareSale(),
                                                his.getCompareRate())));

                        departmentSaleResponse.setCompareProfitAddRateIn(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                deptSaleModel.getCompareSaleIn(),
                                                deptSaleModel.getCompareRateIn()),
                                        commonCalculateService.calculateProfit(
                                                his.getCompareSaleIn(),
                                                his.getCompareRateIn())));
                    }
                }
                result.add(departmentSaleResponse);
            }
        }
        return result;
    }

    /**
     * 获取品类目标值
     * @param types
     * @param deptGoalModelList
     * @return
     */
    private List<CategoryGoalModel> getCategoryGoals(List<DepartmentTypeModel> types,
                                                     List<DeptGoalModel> deptGoalModelList){
        List<CategoryGoalModel> result = new ArrayList<>();
        if(null != deptGoalModelList && deptGoalModelList.size() > 0){
            if(null != types && types.size() > 0){
                Map<String, List<DepartmentTypeModel>> categoryMap = types.stream().collect(Collectors.groupingBy(DepartmentTypeModel::getCategoryName));
                for(Map.Entry<String, List<DepartmentTypeModel>> entry : categoryMap.entrySet()){
                    CategoryGoalModel categoryGoalModel = new CategoryGoalModel();
                    String key = entry.getKey();
                    List<DepartmentTypeModel> value = entry.getValue();
                    categoryGoalModel.setCategoryName(key);
                    for(DepartmentTypeModel departmentTypeModel : value){
                        for(DeptGoalModel deptGoalModel : deptGoalModelList){
                            if(departmentTypeModel.getDeptId().equals(deptGoalModel.getDeptId())){
                                categoryGoalModel.setSale(commonCalculateService.adds(categoryGoalModel.getSale(), deptGoalModel.getSale()));
                                categoryGoalModel.setRate(commonCalculateService.adds(categoryGoalModel.getRate(), deptGoalModel.getRate()));
                            }
                        }
                    }
                    result.add(categoryGoalModel);
                }
            }
        }
        return result;
    }

    /**
     * 查询分部汇总数据
     * @param start
     * @param hisStart
     * @param hour
     * @param num(天/月数)
     * @param areas
     * @param regions
     * @param stores
     * @param allSale
     * @return
     */
    private List<DepartmentSaleResponse> queryDepartmentSaleResponse(Date start,
                                                                     Date hisStart,
                                                                     String hour,
                                                                     int num,
                                                                     List<String> areas,
                                                                     List<String> regions,
                                                                     List<String> stores,
                                                                     DepartmentSaleModel allSale){
        List<DepartmentSaleResponse> result = new ArrayList<>();
        //部门销售
        List<DepartmentSaleModel> departmentSaleList = null;
        //往期销售
        List<DepartmentSaleModel> hisDepartmentSaleList = null;
        //销售目标
        List<DepartmentGoalModel> goals = null;
        //总客流
        KlModel kl = new KlModel();
        //查询部门汇总数据
        departmentSaleList = marketDao.queryCurrentDepartmentSales(start,
                areas,
                regions,
                stores,
                null,
                null);

        //查询目标值
        try{
            changeDgDataSource();
            goals = marketDao.queryDepartmentGoal(start,
                    areas,
                    regions,
                    stores,
                    null,
                    null
            );
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //查询同期数据
        try{
            changeZyDataSource();
            hisDepartmentSaleList = marketDao.queryHisDepartmentSales(hisStart,
                    hour,
                    areas,
                    regions,
                    stores,
                    null,
                    null
            );
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //设置总客流
        if(null != allSale){
            kl.setKl(allSale.getKl());
        }

        //计算各个部门汇总行
        result = sumDepartmentSale(departmentSaleList, hisDepartmentSaleList, goals, kl, num);
        return result;
    }

    /**
     * 获取品类合计行(将大类合计到品类)
     * @param deptSales
     * @return
     */
    private List<CategorySaleModel> getCategorySaleModelList(List<DeptSaleModel> deptSales){
        List<CategorySaleModel> result = new ArrayList<>();
        if(null != deptSales && deptSales.size() > 0){
            Map<String, List<DeptSaleModel>> deptMap = deptSales.stream().collect(Collectors.groupingBy(DeptSaleModel::getCategoryName));
            if(null != deptMap && deptMap.size() > 0){
                for(Map.Entry<String, List<DeptSaleModel>> typeEntry : deptMap.entrySet()){
                    CategorySaleModel categorySaleModel = new CategorySaleModel();
                    String categoryName = typeEntry.getKey();
                    List<DeptSaleModel> deptList = deptMap.get(categoryName);
                    categorySaleModel.setCategoryName(categoryName);
                    for(DeptSaleModel dept : deptList){
                        categorySaleModel.setSale(commonCalculateService.adds(categorySaleModel.getSale(), dept.getSale()));
                        categorySaleModel.setSaleIn(commonCalculateService.adds(categorySaleModel.getSaleIn(), dept.getSaleIn()));
                        categorySaleModel.setRate(commonCalculateService.adds(categorySaleModel.getRate(), dept.getRate()));
                        categorySaleModel.setRateIn(commonCalculateService.adds(categorySaleModel.getRateIn(), dept.getRateIn()));
                        categorySaleModel.setCompareSale(commonCalculateService.adds(categorySaleModel.getCompareSale(), dept.getCompareSale()));
                        categorySaleModel.setCompareSaleIn(commonCalculateService.adds(categorySaleModel.getCompareSaleIn(), dept.getCompareSaleIn()));
                        categorySaleModel.setCompareRate(commonCalculateService.adds(categorySaleModel.getCompareRate(), dept.getCompareRate()));
                        categorySaleModel.setCompareRateIn(commonCalculateService.adds(categorySaleModel.getCompareRateIn(), dept.getCompareRateIn()));
                    }
                    result.add(categorySaleModel);
                }
            }
        }
        return result;
    }

    /**
     * 统计合计行
     * @param allSale(部门合计总销售数据)
     * @param hisAllSale(部门合计总数据)
     * @param goals(各部门目标值)
     * @param kl(总客流)
     * @param num(天/月数)
     * @param target(待插入合计行集合)
     */
    private void sumTotalDepartmentSale(DepartmentSaleModel allSale,
                                        DepartmentSaleModel hisAllSale,
                                        List<DepartmentGoalModel> goals,
                                        KlModel kl,
                                        int num,
                                        List<DepartmentSaleResponse> target){
        if(null != allSale){
            //汇总总目标值
            DepartmentGoalModel allGoals = new DepartmentGoalModel();
            if(null != goals && goals.size() > 0){
                for(DepartmentGoalModel goal : goals){
                    allGoals.setSale(commonCalculateService.adds(allGoals.getSale(), goal.getSale()));
                    allGoals.setRate(commonCalculateService.adds(allGoals.getRate(), goal.getRate()));
                }
            }

            DepartmentSaleResponse departmentSaleResponse = new DepartmentSaleResponse();
            departmentSaleResponse.setId("-1");
            departmentSaleResponse.setName("合计");

            //计算渗透率
            if(null != kl){
                departmentSaleResponse.setPermeability(commonCalculateService.calculatePermeability(
                        allSale.getKl(),
                        kl.getKl()
                ));
            }

            if(null != allGoals){
                //销售目标值
                departmentSaleResponse.setSaleGoal(commonCalculateService.calculateGoalSale(
                        allGoals.getSale(),
                        num
                ));
                //销售达成率
                departmentSaleResponse.setSaleGoalRate(commonCalculateService.calculateSaleAchiRate(
                        allSale.getSale(),
                        commonCalculateService.calculateGoalSale(
                                allGoals.getSale(),
                                num
                        )
                ));
                departmentSaleResponse.setCompareSaleGoalRate(commonCalculateService.calculateSaleAchiRate(
                        allSale.getCompareSale(),
                        commonCalculateService.calculateGoalSale(
                                allGoals.getSale(),
                                num
                        )
                ));
                departmentSaleResponse.setSaleGoalRateIn(commonCalculateService.calculateSaleAchiRate(
                        allSale.getSaleIn(),
                        commonCalculateService.calculateGoalSale(
                                allGoals.getSale(),
                                num
                        )
                ));
                departmentSaleResponse.setCompareSaleGoalRateIn(commonCalculateService.calculateSaleAchiRate(
                        allSale.getCompareSaleIn(),
                        commonCalculateService.calculateGoalSale(
                                allGoals.getSale(),
                                num
                        )
                ));
            }
            //销售额
            departmentSaleResponse.setSale(allSale.getSale());
            departmentSaleResponse.setSaleIn(allSale.getSaleIn());
            departmentSaleResponse.setCompareSale(allSale.getCompareSale());
            departmentSaleResponse.setCompareSaleIn(allSale.getCompareSaleIn());
            //毛利额
            departmentSaleResponse.setProfit(allSale.getRate());
            departmentSaleResponse.setProfitIn(allSale.getRateIn());
            departmentSaleResponse.setCompareProfit(allSale.getCompareRate());
            departmentSaleResponse.setCompareProfitIn(allSale.getCompareRateIn());
            if(null != hisAllSale){
                //往期销售额
                departmentSaleResponse.setHisSale(hisAllSale.getSale());
                departmentSaleResponse.setHisSaleIn(hisAllSale.getSaleIn());
                departmentSaleResponse.setCompareHisSale(hisAllSale.getCompareSale());
                departmentSaleResponse.setCompareHisSaleIn(hisAllSale.getCompareSaleIn());
                //销售增长率
                departmentSaleResponse.setSaleAddRate(commonCalculateService.calculateSaleRate(
                        allSale.getSale(),
                        hisAllSale.getSale()
                ));
                departmentSaleResponse.setSaleAddRateIn(commonCalculateService.calculateSaleRate(
                        allSale.getSaleIn(),
                        hisAllSale.getSaleIn()
                ));
                departmentSaleResponse.setCompareSaleAddRate(commonCalculateService.calculateSaleRate(
                        allSale.getCompareSale(),
                        hisAllSale.getCompareSale()
                ));
                departmentSaleResponse.setCompareSaleAddRateIn(commonCalculateService.calculateSaleRate(
                        allSale.getCompareSaleIn(),
                        hisAllSale.getCompareSaleIn()
                ));
                //往期毛利额
                departmentSaleResponse.setHisProfit(hisAllSale.getRate());
                departmentSaleResponse.setHisProfitIn(hisAllSale.getRateIn());
                departmentSaleResponse.setCompareHisProfit(hisAllSale.getCompareRate());
                departmentSaleResponse.setCompareHisProfitIn(hisAllSale.getCompareRateIn());
                //毛利额增长率
                departmentSaleResponse.setProfitAddRate(commonCalculateService.calculateProfitAddRate(
                        allSale.getRate(),
                        hisAllSale.getRate()
                ));
                departmentSaleResponse.setProfitAddRateIn(commonCalculateService.calculateProfitAddRate(
                        allSale.getRateIn(),
                        hisAllSale.getRateIn()
                ));
                departmentSaleResponse.setCompareProfitAddRate(commonCalculateService.calculateProfitAddRate(
                        allSale.getCompareRate(),
                        hisAllSale.getCompareRate()
                ));
                departmentSaleResponse.setCompareProfitAddRateIn(commonCalculateService.calculateProfitAddRate(
                        allSale.getCompareRateIn(),
                        hisAllSale.getCompareRateIn()
                ));
            }
            target.add(departmentSaleResponse);
        }
    }

    /**
     * 给部门添加负责人，并且按照负责人的排序
     * @param areaPersonList(部门负责人对应集合)
     * @param source(部门汇总数据)
     * @return
     */
    private List<DepartmentSaleResponse> sortpartmentSale(List<AreaPersonModel> areaPersonList,
                                                          List<DepartmentSaleResponse> source){
        //排序集合
        List<DepartmentSaleResponse> sort = new ArrayList<>();
        if(null != areaPersonList && areaPersonList.size() > 0){
            for(AreaPersonModel areaPersonModel : areaPersonList){
                DepartmentSaleResponse sortResponse = null;
                for(DepartmentSaleResponse departmentSaleResponse : source){
                    if(!"99".equals(departmentSaleResponse.getName()) && departmentSaleResponse.getName().equals(areaPersonModel.getAreaName())){//名字配对
                        departmentSaleResponse.setPersonName(areaPersonModel.getUserName());
                        sortResponse = departmentSaleResponse;
                        break;
                    }
                }
                if(null != sortResponse){
                    sort.add(sortResponse);
                }
            }
        }
        return sort;
    }

    /**
     * 计算各部门汇总数据
     * @param departmentSaleList(部门汇总)
     * @param hisDepartmentSaleList(往期汇总)
     * @param goals(部门目标值)
     * @param kl(总客流)
     * @param num(天/月数)
     * @return
     */
    private List<DepartmentSaleResponse> sumDepartmentSale(List<DepartmentSaleModel> departmentSaleList,
                                                           List<DepartmentSaleModel> hisDepartmentSaleList,
                                                           List<DepartmentGoalModel> goals,
                                                           KlModel kl,
                                                           int num){
        List<DepartmentSaleResponse> result = new ArrayList<>();
        if(null != departmentSaleList && departmentSaleList.size() > 0){

            Map<String, DepartmentSaleModel> hisDepartmentSaleMap = null;
            if(null != hisDepartmentSaleList && hisDepartmentSaleList.size() > 0){
                hisDepartmentSaleMap = hisDepartmentSaleList.stream().collect(Collectors.toMap(DepartmentSaleModel::getPurchaseDept,
                        Function.identity(), (key1, key2) -> key2));
            }

            Map<String,DepartmentGoalModel> departmentGoalModelMap = null;
            if(null != goals && goals.size() > 0){
                departmentGoalModelMap = goals.stream().collect(Collectors.toMap(DepartmentGoalModel::getPurchaseDept,
                        Function.identity(), (key1, key2) -> key2));
            }

            for(DepartmentSaleModel departmentSaleModel : departmentSaleList){
                DepartmentSaleResponse departmentSaleResponse = new DepartmentSaleResponse();
                //ID
                departmentSaleResponse.setId(departmentSaleModel.getPurchaseDept());
                //名称
                departmentSaleResponse.setName(departmentSaleModel.getPurchaseDeptName());
                //计算渗透率
                if(null != kl){
                    departmentSaleResponse.setPermeability(commonCalculateService.calculatePermeability(
                            departmentSaleModel.getKl(),
                            kl.getKl()
                    ));
                }

                if(null != departmentGoalModelMap && departmentGoalModelMap.size() > 0){
                    DepartmentGoalModel goal = departmentGoalModelMap.get(departmentSaleModel.getPurchaseDept());
                    if(null != goal){
                        //销售目标值
                        departmentSaleResponse.setSaleGoal(commonCalculateService.calculateGoalSale(
                                goal.getSale(),
                                num
                        ));
                        //销售达成率
                        departmentSaleResponse.setSaleGoalRate(commonCalculateService.calculateSaleAchiRate(
                                departmentSaleModel.getSale(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                        departmentSaleResponse.setSaleGoalRateIn(commonCalculateService.calculateSaleAchiRate(
                                departmentSaleModel.getSaleIn(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                        departmentSaleResponse.setCompareSaleGoalRate(commonCalculateService.calculateSaleAchiRate(
                                departmentSaleModel.getCompareSale(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                        departmentSaleResponse.setCompareSaleGoalRateIn(commonCalculateService.calculateSaleAchiRate(
                                departmentSaleModel.getCompareSaleIn(),
                                commonCalculateService.calculateGoalSale(
                                        goal.getSale(),
                                        num
                                )
                        ));
                    }
                }
                //销售额
                departmentSaleResponse.setSale(departmentSaleModel.getSale());
                departmentSaleResponse.setSaleIn(departmentSaleModel.getSaleIn());
                departmentSaleResponse.setCompareSale(departmentSaleModel.getCompareSale());
                departmentSaleResponse.setCompareSaleIn(departmentSaleModel.getCompareSaleIn());
                //毛利额
                departmentSaleResponse.setProfit(departmentSaleModel.getRate());
                departmentSaleResponse.setProfitIn(departmentSaleModel.getRateIn());
                departmentSaleResponse.setCompareProfit(departmentSaleModel.getCompareRate());
                departmentSaleResponse.setCompareProfitIn(departmentSaleModel.getCompareRateIn());

                if(null != hisDepartmentSaleMap && hisDepartmentSaleMap.size() > 0){
                    DepartmentSaleModel his = hisDepartmentSaleMap.get(departmentSaleModel.getPurchaseDept());
                    if(null != his){
                        //往期销售额
                        departmentSaleResponse.setHisSale(his.getSale());
                        departmentSaleResponse.setHisSaleIn(his.getSaleIn());
                        departmentSaleResponse.setCompareHisSale(his.getCompareSale());
                        departmentSaleResponse.setCompareHisSaleIn(his.getCompareSaleIn());
                        //销售增长率
                        departmentSaleResponse.setSaleAddRate(commonCalculateService.calculateSaleRate(
                                departmentSaleModel.getSale(),
                                his.getSale()
                        ));
                        departmentSaleResponse.setSaleAddRateIn(commonCalculateService.calculateSaleRate(
                                departmentSaleModel.getSaleIn(),
                                his.getSaleIn()
                        ));
                        departmentSaleResponse.setCompareSaleAddRate(commonCalculateService.calculateSaleRate(
                                departmentSaleModel.getCompareSale(),
                                his.getCompareSale()
                        ));
                        departmentSaleResponse.setCompareSaleAddRateIn(commonCalculateService.calculateSaleRate(
                                departmentSaleModel.getCompareSaleIn(),
                                his.getCompareSaleIn()
                        ));
                        //往期毛利额
                        departmentSaleResponse.setHisProfit(his.getRate());
                        departmentSaleResponse.setHisProfitIn(his.getRateIn());
                        departmentSaleResponse.setCompareHisProfit(his.getCompareRate());
                        departmentSaleResponse.setCompareHisProfitIn(his.getCompareRateIn());
                        //毛利率增长率
                        departmentSaleResponse.setProfitAddRate(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                departmentSaleModel.getSale(),
                                                departmentSaleModel.getRate()),
                                        commonCalculateService.calculateProfit(
                                                his.getSale(),
                                                his.getRate())));

                        departmentSaleResponse.setProfitAddRateIn(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                departmentSaleModel.getSaleIn(),
                                                departmentSaleModel.getRateIn()),
                                        commonCalculateService.calculateProfit(
                                                his.getSaleIn(),
                                                his.getRateIn())));

                        departmentSaleResponse.setCompareProfitAddRate(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                departmentSaleModel.getCompareSale(),
                                                departmentSaleModel.getCompareRate()),
                                        commonCalculateService.calculateProfit(
                                                his.getCompareSale(),
                                                his.getCompareRate())));

                        departmentSaleResponse.setCompareProfitAddRateIn(
                                commonCalculateService.calculateAddProfitRate(
                                        commonCalculateService.calculateProfit(
                                                departmentSaleModel.getCompareSaleIn(),
                                                departmentSaleModel.getCompareRateIn()),
                                        commonCalculateService.calculateProfit(
                                                his.getCompareSaleIn(),
                                                his.getCompareRateIn())));
                    }
                }
                result.add(departmentSaleResponse);
            }
        }
        return result;
    }

    /**
     * 根据部门反推大类,并且对param重新赋值大类条件(必须放在transform()方法后调用)
     * @param param
     */
    private void getDeptIdByPurchaseDept(MarkReportRequest param){
        if(null != param
                && StringUtils.isNotEmpty(param.getPurchaseDept())
                && !"-1".equals(param.getPurchaseDept())){//-1表示全部
            List<DepartmentTypeModel> types = marketDao.queryDepartmentType();
            List<String> depts = param.getDeptIds();
            List<String> resultDepts = new ArrayList<>();
            StringBuilder deptStr = new StringBuilder();
            if(null != types && types.size() > 0){
                String purchaseDeptStr = param.getPurchaseDept();
                if(purchaseDeptStr.contains("JD002")){//JD001合计为了JD002,所以查询JD002也需要把JD001条件带上
                    if(purchaseDeptStr.endsWith(",")){
                        purchaseDeptStr = purchaseDeptStr + "JD001";
                    }else{
                        purchaseDeptStr = purchaseDeptStr + ",JD001";
                    }
                }

                String[] arr = purchaseDeptStr.split(",");
                for(int i = 0; i < arr.length; i++){
                    for(DepartmentTypeModel type : types){
                        if(arr[i].equals(type.getPurchaseDept())){
                            if(null == depts || depts.size() == 0){//大类ID为空，表示全司权限
                                deptStr.append(type.getDeptId()).append(",");
                            }else if(depts.contains(type.getDeptId())){//判断是否在用户权限内的大类
                                deptStr.append(type.getDeptId()).append(",");
                            }
                        }
                    }
                }

                String result = deptStr.toString();
                if(StringUtils.isNotEmpty(result)){
                    if(result.endsWith(",")){
                        result = result.substring(0, result.lastIndexOf(","));
                    }
                    param.setDeptId(result);
                    resultDepts.addAll(Arrays.asList(result.split(",")));
                    param.setDeptIds(resultDepts);
                }else{//表示没有大类交集，不允许查询结果
                    param.setDeptId("-9999");
                    resultDepts.add("-9999");
                    param.setDeptIds(resultDepts);
                }
            }
        }
    }

    /**
     * 大类反推分部
     * @param types
     * @param depts
     * @return
     */
    private List<String> getPurchaseDeptByDepts(List<DepartmentTypeModel> types,
                                            List<String> depts){
        List<String> purchaseDepts = new ArrayList<>();
        if(null != depts && depts.size() > 0){
            if(1== depts.size() && depts.get(0).equals("-9999")){//大类ID为-9999表示没有权限，查看数据
                purchaseDepts.add("-9999");
                return purchaseDepts;
            }
            for(String deptId : depts){
                if(null != types && types.size() > 0){
                    for(DepartmentTypeModel category : types){
                        if(deptId.equals(category.getDeptId())){
                            String str = category.getPurchaseDept();
                            /*if("JD001".equals(str)){//家电分部有多个，合到一起
                                str = "JD002";
                            }*/
                            if(!purchaseDepts.contains(str)){
                                purchaseDepts.add(str);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return purchaseDepts;
    }

    /**
     * 大类反推品类
     * @param types
     * @param depts
     * @return
     */
    private List<String> getCategoryByDepts(List<DepartmentTypeModel> types,
                                            List<String> depts){
        List<String> categorys = new ArrayList<>();
        if(null != depts && depts.size() > 0){
            if(1== depts.size() && depts.get(0).equals("-9999")){//大类ID为-9999表示没有权限，查看数据
                categorys.add("-9999");
                return categorys;
            }
            for(String deptId : depts){
                if(null != types && types.size() > 0){
                    for(DepartmentTypeModel category : types){
                        if(deptId.equals(category.getDeptId())){
                            if(!categorys.contains(category.getCategoryName())){
                                categorys.add(category.getCategoryName());
                                break;
                            }
                        }
                    }
                }
            }
        }
        return categorys;
    }

    /**
     * 将参数中的字符串转换为List
     * @param param
     */
    private void transform(MarkReportRequest param){
        String provinceId = param.getProvinceId();
        String areaId = param.getAreaId();
        String storeId = param.getStoreId();
        String deptId = param.getDeptId();
        String category = param.getCategory();
        String startDateStr = param.getStartDate();
        String purchaseDept = param.getPurchaseDept();

        if(StringUtils.isNotEmpty(startDateStr)){
            param.setStart(DateUtils.dateTime("yyyy-MM-dd", startDateStr));
        }

        if(StringUtils.isNotEmpty(provinceId)){
            String[] provinceIdArr = Convert.toStrArray(provinceId);
            if(provinceId.endsWith(",")){
                param.setProvinceId(provinceId.substring(0,provinceId.lastIndexOf(",")));
            }
            param.setProvinceIds(Arrays.asList(provinceIdArr));
        }

        if(StringUtils.isNotEmpty(areaId)){
            String[] areaIdArr = Convert.toStrArray(areaId);
            if(areaId.endsWith(",")){
                param.setAreaId(areaId.substring(0,areaId.lastIndexOf(",")));
            }
            param.setAreaIds(Arrays.asList(areaIdArr));
        }

        if(StringUtils.isNotEmpty(storeId)){
            String[] storeIdArr = Convert.toStrArray(storeId);
            if(storeId.endsWith(",")){
                param.setStoreId(storeId.substring(0,storeId.lastIndexOf(",")));
            }
            param.setStoreIds(Arrays.asList(storeIdArr));
        }

        if(StringUtils.isNotEmpty(deptId)){
            String[] deptIdArr = Convert.toStrArray(deptId);
            if(deptId.endsWith(",")){
                param.setDeptId(deptId.substring(0,deptId.lastIndexOf(",")));
            }
            param.setDeptIds(Arrays.asList(deptIdArr));
        }

        if(StringUtils.isNotEmpty(category)){
            String[] categoryArr = Convert.toStrArray(category);
            if(category.endsWith(",")){
                param.setCategory(category.substring(0,category.lastIndexOf(",")));
            }
            param.setCategorys(Arrays.asList(categoryArr));
        }

        if(StringUtils.isNotEmpty(purchaseDept)){
            String[] purchaseDeptArr = Convert.toStrArray(purchaseDept);
            if(purchaseDept.endsWith(",")){
                param.setPurchaseDept(purchaseDept.substring(0,purchaseDept.lastIndexOf(",")));
            }
            param.setPurchaseDepts(Arrays.asList(purchaseDeptArr));
        }
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
