package com.cs.mobile.api.service.dailyreport.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.dailyreport.DailyReportDao;
import com.cs.mobile.api.dao.dailyreport.WorstAndBestDao;
import com.cs.mobile.api.dao.freshreport.FreshReportCsmbDao;
import com.cs.mobile.api.dao.goods.GoodsDao;
import com.cs.mobile.api.dao.market.MarketDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
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
import com.cs.mobile.api.model.dailyreport.WorstAndBestModel;
import com.cs.mobile.api.model.dailyreport.request.DailyReportRequest;
import com.cs.mobile.api.model.dailyreport.request.FreshDailyReportRequest;
import com.cs.mobile.api.model.dailyreport.response.AreaDailyReportResponse;
import com.cs.mobile.api.model.dailyreport.response.CategoryDailyReportListResponse;
import com.cs.mobile.api.model.dailyreport.response.CategoryDailyReportResponse;
import com.cs.mobile.api.model.dailyreport.response.CompanyDailyReportResponse;
import com.cs.mobile.api.model.dailyreport.response.ContinuityLossGoodsDetailResponse;
import com.cs.mobile.api.model.dailyreport.response.ContinuityLossGoodsDetailSumResponse;
import com.cs.mobile.api.model.dailyreport.response.ContinuityLossGoodsListResponse;
import com.cs.mobile.api.model.dailyreport.response.ContinuityLossGoodsResponse;
import com.cs.mobile.api.model.dailyreport.response.DailyReportResponse;
import com.cs.mobile.api.model.dailyreport.response.FreshDailyReportListResponse;
import com.cs.mobile.api.model.dailyreport.response.FreshDailyReportResponse;
import com.cs.mobile.api.model.dailyreport.response.GrossProfitGoodsResponse;
import com.cs.mobile.api.model.dailyreport.response.ProvinceDailyReportResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreDailyReportResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreFreshDailyReportListResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreFreshDailyReportResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreLargeClassMoneyListResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreLargeClassMoneyResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreLossGoodsDetailResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreLossGoodsListResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreLossGoodsResponse;
import com.cs.mobile.api.model.dailyreport.response.StoreLossGoodsSumResponse;
import com.cs.mobile.api.model.freshreport.OrganizationForFresh;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.market.DepartmentTypeModel;
import com.cs.mobile.api.service.common.CommonCalculateService;
import com.cs.mobile.api.service.dailyreport.DailyReportService;
import com.cs.mobile.common.core.text.Convert;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class DailyReportServiceImpl implements DailyReportService {
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
    private DailyReportDao dailyReportDao;
    @Autowired
    private FreshReportCsmbDao freshReportCsmbDao;
	@Value("${store.db.cs.userName}")
	private String csUserName;
	@Value("${store.db.cs.password}")
	private String csPassword;
	@Value("${store.db.jd.userName}")
	private String jdUserName;
	@Value("${store.db.jd.password}")
	private String jdPassword;
	
	@Autowired
	GoodsDao goodsDao;
    @Autowired
    CommonCalculateService commonCalculateService;
    @Autowired
    WorstAndBestDao worstAndBestDao;
    @Autowired
    MarketDao marketDao;

    /**
     * 查询日报表
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public PageResult<DailyReportResponse> queryDailyReport(DailyReportRequest request) throws Exception {
        PageResult<DailyReportResponse> result = null;
        transform(request);
        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        //根据下级推上级
        setUpGradeByDownGrade(request, orgList);
        //查询实时日报表
        result = queryCurDailyReport(request,orgList);
        //TODO(查询历史日报表,暂时没有数据)
        return result;
    }

	
	     
    /**
     * 查询大类下钻
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public CategoryDailyReportListResponse queryCategoryDrillDown(DailyReportRequest request) throws Exception {
    	CategoryDailyReportListResponse result = null;
        transform(request);
        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        //根据下级推上级
        setUpGradeByDownGrade(request, orgList);
        
        //大类下钻
        result = queryCategoryList(request,orgList); 
        return result;
    }
    
    /**
     * 查询大类下钻数据集
     * @param request
     * @return
     */
    private CategoryDailyReportListResponse queryCategoryList(DailyReportRequest request,List<OrganizationForFresh> orgList){
        CategoryDailyReportListResponse reportListResponse = null;
        //梯度（1-全司，2-省份，3-区域,4-门店）
        int mark = request.getMark();
        //用户权限（1-全司，2-省份，3-区域,4-门店）
        int grade = request.getGrade();
        if(4 == grade){
        	reportListResponse = queryStoreCategoryReport(request, orgList);
        }else if(3 == grade){
            if(4 == mark){
            	reportListResponse = queryStoreCategoryReport(request, orgList);
            }else{
            	reportListResponse = queryAreaCategoryReport(request, orgList);
            }
        }else if(2 == grade){
            if(4 == mark){
            	reportListResponse = queryStoreCategoryReport(request, orgList);
            }else if(3 == mark){
            	reportListResponse = queryAreaCategoryReport(request, orgList);
            }else{
            	reportListResponse = queryProvinceCategoryReport(request, orgList);
            }
        }else{
            if(4 == mark){
            	reportListResponse = queryStoreCategoryReport(request, orgList);
            }else if(3 == mark){
            	reportListResponse = queryAreaCategoryReport(request, orgList);
            }else if(2 == mark){
            	reportListResponse = queryProvinceCategoryReport(request, orgList);
            }else{
            	reportListResponse = queryEnterpriseCategoryReport();
            }
        }
        return reportListResponse;
    }

    /**
     * 查询战区报表
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public FreshDailyReportListResponse queryFreshDailyReport(FreshDailyReportRequest param) throws Exception {
        FreshDailyReportListResponse freshDailyReportListResponse = new FreshDailyReportListResponse();
        List<FreshDailyReportResponse> provinces = null;
        List<FreshDailyReportResponse> theaters = null;
        List<FreshDailyReportResponse> areas = null;
        //查询报表类型(0-生鲜战报,1-营运战报)
        int isType = param.getIsType();
        //查询类型(0-全部,1-品类,2-大类)
        int isQueryType = param.getIsQueryType();
        if(2 == isQueryType){//查询省份，战区，区域的大类数据
            //查询大类汇总
            //省大类汇总
            provinces = queryFreshDeptProvince(param);
            //战区大类汇总
            theaters = queryFreshDeptTheater(param);
            //区域大类汇总
            areas = queryFreshDeptArea(param);
        }else if(1 == isQueryType){//查询省份，战区，区域的品类数据
            //查询品类汇总
            //省品类汇总
            provinces = queryCategoryProvince(param);
            //战区品类汇总
            theaters = queryCategoryTheater(param);
            //区域品类汇总
            areas = queryCategoryArea(param);
        }else if(0 == isQueryType){//查询省份，战区，区域的所有数据
            //省汇总
            provinces = queryAllProvince(param);
            //战区汇总
            theaters = queryAllTheater(param);
            //区域汇总
            areas = queryAllArea(param);
        }
        //查询报表类型(0-生鲜战报,1-营运战报):营运战报才需要最好和最坏
//        if(1 == isType){
            queryRegionWorstAndBest(param, areas);
            queryTheaterWorstAndBest(param, theaters);
            queryProvinceWorstAndBest(param, provinces);
//        }
        freshDailyReportListResponse = sumFreshReport(provinces, theaters, areas);
        return freshDailyReportListResponse;
    }

    /**
     * 查询门店战区报表
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public StoreFreshDailyReportListResponse queryStoreFreshDailyReport(FreshDailyReportRequest param) throws Exception {
        StoreFreshDailyReportListResponse storeFreshDailyReportListResponse = new StoreFreshDailyReportListResponse();
        List<StoreFreshDailyReportResponse> storeList = new ArrayList<>();
        List<FreshDailyReportResponse> areaList = new ArrayList<>();
        List<StoreFreshDailyReport> stores = null;
        List<FreshDailyReportModel> areas = null;
        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        //查询门店负责人
        List<StorePersonModel> personList = dailyReportDao.queryStorePersonModel();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        //查询类型(0-全部,1-品类,2-大类)
        int isQueryType = param.getIsQueryType();
        //查询报表类型(0-生鲜战报,1-营运战报)
        int isType = param.getIsType();
        if(2 == isQueryType){//查询门店大类数据
            try{
                changeDgDataSource();
                stores = dailyReportDao.queryFreshDeptStore(
                        getNameById(param.getAreaId(), orgList),
                        param.getDeptId());
                areas = dailyReportDao.querySameAndCompareFreshDeptArea(
                        param,
                        getNameById(param.getAreaId(), orgList));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if(1 == isQueryType){//查询门店品类数据
            try{
                changeDgDataSource();
                stores = dailyReportDao.queryFreshStore(getNameById(param.getAreaId(), orgList), param.getCategory());
                areas = dailyReportDao.querySameAndCompareFreshArea(param.getCategory(), getNameById(param.getAreaId(), orgList));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

        }else if(0 == isQueryType){//查询门店数据
            try{
                changeDgDataSource();
                stores = dailyReportDao.queryAllStores(getNameById(param.getAreaId(), orgList));
                areas = dailyReportDao.queryArea(getNameById(param.getAreaId(), orgList));
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }

        }

        //组合门店数据
        if(null != stores && stores.size() > 0){
            for(StoreFreshDailyReport storeFreshDailyReport : stores){
                StoreFreshDailyReportResponse response = new StoreFreshDailyReportResponse();

                if(null != personList && personList.size() > 0){//匹配门店店长
                    for(StorePersonModel storePersonModel : personList){
                        if(storePersonModel.getStoreName().equals(storeFreshDailyReport.getStoreName())){
                            storeFreshDailyReport.setUserName(storePersonModel.getUserName());
                            break;
                        }
                    }
                }

                BeanUtils.copyProperties(storeFreshDailyReport, response);
                storeList.add(response);
            }
        }
        //组合区域数据
        if(null != areas && areas.size() > 0){
            for(FreshDailyReportModel freshDailyReportModel : areas){
                FreshDailyReportResponse response = new FreshDailyReportResponse();
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    if(StringUtils.isNotEmpty(freshDailyReportModel.getAreaName())
                            && freshDailyReportModel.getAreaName().equals(areaPersonModel.getAreaName())){
                        freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                        break;
                    }
                }
                BeanUtils.copyProperties(freshDailyReportModel, response);
                areaList.add(response);
            }
        }

//        if(1 == isType){
            //获取门店最好和最坏
            queryStoreWorstAndBest(param,storeList);
            //获取区域最好和最坏
            queryRegionWorstAndBest(param,areaList);
//        }

        storeFreshDailyReportListResponse.setAreaList(areaList);
        storeFreshDailyReportListResponse.setStoreList(storeList);
        storeFreshDailyReportListResponse.setStoreDayTrendList(sortStoreFreshReport(storeList, 0));
        storeFreshDailyReportListResponse.setStoreMonthTrendList(sortStoreFreshReport(storeList, 1));
        return storeFreshDailyReportListResponse;
    }

    /**
     * 查询门店最好和最坏
     * @param param
     * @param storeList
     */
    private void queryStoreWorstAndBest(FreshDailyReportRequest param,
                                        List<StoreFreshDailyReportResponse> storeList){
        if(null == storeList || 0 == storeList.size()){
            return;
        }
        //最坏
        List<WorstAndBestModel> worsts = new ArrayList<>();
        //月最坏
        List<WorstAndBestModel> monthWorsts = new ArrayList<>();
        //最好
        List<WorstAndBestModel> bests = new ArrayList<>();
        //月最好
        List<WorstAndBestModel> monthBests = new ArrayList<>();
        //类型（品类 + 分部 + 大类）
        List<DepartmentTypeModel> types = null;
        transformFresh(param);
        //查询类型（品类 + 分部 + 大类）
        types = marketDao.queryDepartmentType();
        //查询类型(0-全部,1-品类,2-大类)
        int isQueryType = param.getIsQueryType();
        if(2 == isQueryType){
            //查询门店全比和可比最好和最坏的单品
            worsts = worstAndBestDao.queryWorstItemStore(param.getAreaIds(),param.getDeptIds());
            bests = worstAndBestDao.queryBestItemStore(param.getAreaIds(),param.getDeptIds());
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstItemStore(param.getAreaIds(),param.getDeptIds());
                monthBests = worstAndBestDao.queryMonthBestItemStore(param.getAreaIds(),param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if(1 == isQueryType){
            //查询门店全比和可比最好和最坏的大类
            worsts = worstAndBestDao.queryWorstStore(param.getAreaIds(),param.getDeptIds());
            bests = worstAndBestDao.queryBestStore(param.getAreaIds(),param.getDeptIds());
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstStore(param.getAreaIds(),param.getDeptIds());
                monthBests = worstAndBestDao.queryMonthBestStore(param.getAreaIds(),param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if(0 == isQueryType){
            //查询门店全比和可比最好和最坏的大类
            worsts = worstAndBestDao.queryWorstStore(param.getAreaIds(),null);
            bests = worstAndBestDao.queryBestStore(param.getAreaIds(),null);
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstStore(param.getAreaIds(),null);
                monthBests = worstAndBestDao.queryMonthBestStore(param.getAreaIds(),null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }

        if(1 == isQueryType || 0 == isQueryType){
            //翻译大类名称
            if(null != monthBests && monthBests.size() > 0
                    && null != types && types.size() > 0){
                for(WorstAndBestModel best : monthBests){
                    for(DepartmentTypeModel type : types){
                        if(type.getDeptId().equals(best.getId())){
                            best.setName(type.getDeptName());
                            break;
                        }
                    }
                }
            }
            if(null != monthWorsts && monthWorsts.size() > 0
                    && null != types && types.size() > 0){
                for(WorstAndBestModel worst : monthWorsts){
                    for(DepartmentTypeModel type : types){
                        if(type.getDeptId().equals(worst.getId())){
                            worst.setName(type.getDeptName());
                            break;
                        }
                    }
                }
            }
        }

        //拼接最好和最坏
        for(StoreFreshDailyReportResponse response : storeList){
            if(null != worsts && worsts.size() > 0){
                for(WorstAndBestModel worst : worsts){
                    if(worst.getRelateName().equals(response.getStoreName())){
                        if(2 == isQueryType){
                            response.setDayWorst(worst.getName());
                        }else{
                            response.setDayWorst(worst.getId() + "-" + worst.getName());
                        }
                        break;
                    }
                }
            }

            if(null != monthWorsts && monthWorsts.size() > 0){
                for(WorstAndBestModel monthWorst : monthWorsts){
                    if(monthWorst.getRelateName().equals(response.getStoreName())){
                        if(2 == isQueryType){
                            response.setMonthWorst(monthWorst.getName());
                        }else{
                            response.setMonthWorst(monthWorst.getId() + "-" + monthWorst.getName());
                        }
                        break;
                    }
                }
            }

            if(null != bests && bests.size() > 0){
                for(WorstAndBestModel best : bests){
                    if(best.getRelateName().equals(response.getStoreName())){
                        if(2 == isQueryType){
                            response.setDayBest(best.getName());
                        }else{
                            response.setDayBest(best.getId() + "-" + best.getName());
                        }
                        break;
                    }
                }
            }

            if(null != monthBests && monthBests.size() > 0){
                for(WorstAndBestModel monthBest : monthBests){
                    if(monthBest.getRelateName().equals(response.getStoreName())){
                        if(2 == isQueryType){
                            response.setMonthBest(monthBest.getName());
                        }else{
                            response.setMonthBest(monthBest.getId() + "-" + monthBest.getName());
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 查询区域最好和最坏
     * @param param
     * @param areaList
     */
    private void queryRegionWorstAndBest(FreshDailyReportRequest param,
                                         List<FreshDailyReportResponse> areaList){
        if(null == areaList || 0 == areaList.size()){
            return;
        }
        //最坏
        List<WorstAndBestModel> worsts = new ArrayList<>();
        //月最坏
        List<WorstAndBestModel> monthWorsts = new ArrayList<>();
        //最好
        List<WorstAndBestModel> bests = new ArrayList<>();
        //月最好
        List<WorstAndBestModel> monthBests = new ArrayList<>();
        //类型（品类 + 分部 + 大类）
        List<DepartmentTypeModel> types = null;
        transformFresh(param);
        int isCompare = param.getIsCompare();
        //查询类型（品类 + 分部 + 大类）
        types = marketDao.queryDepartmentType();
        //查询类型(0-全部,1-品类,2-大类)
        int isQueryType = param.getIsQueryType();
        if(2 == isQueryType){
            //查询门店全比和可比最好和最坏的单品
            worsts = worstAndBestDao.queryWorstItemRegion(isCompare,param.getDeptIds());
            bests = worstAndBestDao.queryBestItemRegion(isCompare,param.getDeptIds());
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstItemRegion(isCompare,param.getDeptIds());
                monthBests = worstAndBestDao.queryMonthBestItemRegion(isCompare,param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if(1 == isQueryType){
            //查询门店全比和可比最好和最坏的大类
            worsts = worstAndBestDao.queryWorstRegion(isCompare,param.getDeptIds());
            bests = worstAndBestDao.queryBestRegion(isCompare,param.getDeptIds());
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstRegion(isCompare,param.getDeptIds());
                monthBests = worstAndBestDao.queryMonthBestRegion(isCompare,param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if(0 == isQueryType){
            //查询门店全比和可比最好和最坏的大类
            worsts = worstAndBestDao.queryWorstRegion(isCompare,null);
            bests = worstAndBestDao.queryBestRegion(isCompare,null);
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstRegion(isCompare,null);
                monthBests = worstAndBestDao.queryMonthBestRegion(isCompare,null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }

        if(1 == isQueryType || 0 == isQueryType){
            //翻译大类名称
            if(null != monthBests && monthBests.size() > 0
                    && null != types && types.size() > 0){
                for(WorstAndBestModel best : monthBests){
                    for(DepartmentTypeModel type : types){
                        if(type.getDeptId().equals(best.getId())){
                            best.setName(type.getDeptName());
                            break;
                        }
                    }
                }
            }
            if(null != monthWorsts && monthWorsts.size() > 0
                    && null != types && types.size() > 0){
                for(WorstAndBestModel worst : monthWorsts){
                    for(DepartmentTypeModel type : types){
                        if(type.getDeptId().equals(worst.getId())){
                            worst.setName(type.getDeptName());
                            break;
                        }
                    }
                }
            }
        }

        //拼接最好和最坏
        for(FreshDailyReportResponse response : areaList){

            if(null != worsts && worsts.size() > 0){
                for(WorstAndBestModel worst : worsts){
                    if(worst.getRelateName().equals(response.getAreaName())){
                        if(2 == isQueryType){
                            response.setDayWorst(worst.getName());
                        }else{
                            response.setDayWorst(worst.getId() + "-" + worst.getName());
                        }
                        break;
                    }
                }
            }

            if(null != monthWorsts && monthWorsts.size() > 0){
                for(WorstAndBestModel monthWorst : monthWorsts){
                    if(monthWorst.getRelateName().equals(response.getAreaName())){
                        if(2 == isQueryType){
                            response.setMonthWorst(monthWorst.getName());
                        }else{
                            response.setMonthWorst(monthWorst.getId() + "-" + monthWorst.getName());
                        }
                        break;
                    }
                }
            }

            if(null != bests && bests.size() > 0){
                for(WorstAndBestModel best : bests){
                    if(best.getRelateName().equals(response.getAreaName())){
                        if(2 == isQueryType){
                            response.setDayBest(best.getName());
                        }else{
                            response.setDayBest(best.getId() + "-" + best.getName());
                        }
                        break;
                    }
                }
            }

            if(null != monthBests && monthBests.size() > 0){
                for(WorstAndBestModel monthBest : monthBests){
                    if(monthBest.getRelateName().equals(response.getAreaName())){
                        if(2 == isQueryType){
                            response.setMonthBest(monthBest.getName());
                        }else{
                            response.setMonthBest(monthBest.getId() + "-" + monthBest.getName());
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 查询战区最好和最坏
     * @param param
     * @param theaters
     */
    private void queryTheaterWorstAndBest(FreshDailyReportRequest param,
                                          List<FreshDailyReportResponse> theaters){
        if(null == theaters || 0 == theaters.size()){
            return;
        }
        //最坏
        List<WorstAndBestModel> worsts = new ArrayList<>();
        //月最坏
        List<WorstAndBestModel> monthWorsts = new ArrayList<>();
        //最好
        List<WorstAndBestModel> bests = new ArrayList<>();
        //月最好
        List<WorstAndBestModel> monthBests = new ArrayList<>();
        //类型（品类 + 分部 + 大类）
        List<DepartmentTypeModel> types = null;
        transformFresh(param);
        int isCompare = param.getIsCompare();
        //查询类型（品类 + 分部 + 大类）
        types = marketDao.queryDepartmentType();
        //查询类型(0-全部,1-品类,2-大类)
        int isQueryType = param.getIsQueryType();
        if(2 == isQueryType){
            //查询门店全比和可比最好和最坏的单品
            worsts = worstAndBestDao.queryWorstItemTheater(isCompare,param.getDeptIds());
            bests = worstAndBestDao.queryBestItemTheater(isCompare,param.getDeptIds());
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstItemTheater(isCompare,param.getDeptIds());
                monthBests = worstAndBestDao.queryMonthBestItemTheater(isCompare,param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if(1 == isQueryType){
            //查询门店全比和可比最好和最坏的大类
            worsts = worstAndBestDao.queryWorstTheater(isCompare,param.getDeptIds());
            bests = worstAndBestDao.queryBestTheater(isCompare,param.getDeptIds());
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstTheater(isCompare,param.getDeptIds());
                monthBests = worstAndBestDao.queryMonthBestTheater(isCompare,param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if(0 == isQueryType){
            //查询门店全比和可比最好和最坏的大类
            worsts = worstAndBestDao.queryWorstTheater(isCompare,null);
            bests = worstAndBestDao.queryBestTheater(isCompare,null);
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstTheater(isCompare,null);
                monthBests = worstAndBestDao.queryMonthBestTheater(isCompare,null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }

        if(1 == isQueryType || 0 == isQueryType){
            //翻译大类名称
            if(null != monthBests && monthBests.size() > 0
                    && null != types && types.size() > 0){
                for(WorstAndBestModel best : monthBests){
                    for(DepartmentTypeModel type : types){
                        if(type.getDeptId().equals(best.getId())){
                            best.setName(type.getDeptName());
                            break;
                        }
                    }
                }
            }
            if(null != monthWorsts && monthWorsts.size() > 0
                    && null != types && types.size() > 0){
                for(WorstAndBestModel worst : monthWorsts){
                    for(DepartmentTypeModel type : types){
                        if(type.getDeptId().equals(worst.getId())){
                            worst.setName(type.getDeptName());
                            break;
                        }
                    }
                }
            }
        }

        //拼接最好和最坏
        for(FreshDailyReportResponse response : theaters){
            if(null != worsts && worsts.size() > 0){
                for(WorstAndBestModel worst : worsts){
                    if(worst.getRelateName().equals(response.getAreaName())){
                        if(2 == isQueryType){
                            response.setDayWorst(worst.getName());
                        }else{
                            response.setDayWorst(worst.getId() + "-" + worst.getName());
                        }
                        break;
                    }
                }
            }

            if(null != monthWorsts && monthWorsts.size() > 0){
                for(WorstAndBestModel monthWorst : monthWorsts){
                    if(monthWorst.getRelateName().equals(response.getAreaName())){
                        if(2 == isQueryType){
                            response.setMonthWorst(monthWorst.getName());
                        }else{
                            response.setMonthWorst(monthWorst.getId() + "-" + monthWorst.getName());
                        }
                        break;
                    }
                }
            }

            if(null != bests && bests.size() > 0){
                for(WorstAndBestModel best : bests){
                    if(best.getRelateName().equals(response.getAreaName())){
                        if(2 == isQueryType){
                            response.setDayBest(best.getName());
                        }else{
                            response.setDayBest(best.getId() + "-" + best.getName());
                        }
                        break;
                    }
                }
            }

            if(null != monthBests && monthBests.size() > 0){
                for(WorstAndBestModel monthBest : monthBests){
                    if(monthBest.getRelateName().equals(response.getAreaName())){
                        if(2 == isQueryType){
                            response.setMonthBest(monthBest.getName());
                        }else{
                            response.setMonthBest(monthBest.getId() + "-" + monthBest.getName());
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 查询省份最好和最坏
     * @param param
     * @param provinces
     */
    private void queryProvinceWorstAndBest(FreshDailyReportRequest param,
                                           List<FreshDailyReportResponse> provinces){
        if(null == provinces || 0 == provinces.size()){
            return;
        }
        //最坏
        List<WorstAndBestModel> worsts = new ArrayList<>();
        //月最坏
        List<WorstAndBestModel> monthWorsts = new ArrayList<>();
        //最好
        List<WorstAndBestModel> bests = new ArrayList<>();
        //月最好
        List<WorstAndBestModel> monthBests = new ArrayList<>();
        //类型（品类 + 分部 + 大类）
        List<DepartmentTypeModel> types = null;
        transformFresh(param);
        int isCompare = param.getIsCompare();
        //查询类型（品类 + 分部 + 大类）
        types = marketDao.queryDepartmentType();
        //查询类型(0-全部,1-品类,2-大类)
        int isQueryType = param.getIsQueryType();
        if(2 == isQueryType){
            //查询门店全比和可比最好和最坏的单品
            worsts = worstAndBestDao.queryWorstItemProvince(isCompare,param.getDeptIds());
            bests = worstAndBestDao.queryBestItemProvince(isCompare,param.getDeptIds());
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstItemProvince(isCompare,param.getDeptIds());
                monthBests = worstAndBestDao.queryMonthBestItemProvince(isCompare,param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if(1 == isQueryType){
            //查询门店全比和可比最好和最坏的大类
            worsts = worstAndBestDao.queryWorstProvince(isCompare,param.getDeptIds());
            bests = worstAndBestDao.queryBestProvince(isCompare,param.getDeptIds());
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstProvince(isCompare,param.getDeptIds());
                monthBests = worstAndBestDao.queryMonthBestProvince(isCompare,param.getDeptIds());
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }else if(0 == isQueryType){
            //查询门店全比和可比最好和最坏的大类
            worsts = worstAndBestDao.queryWorstProvince(isCompare,null);
            bests = worstAndBestDao.queryBestProvince(isCompare,null);
            try{
                changeDgDataSource();
                monthWorsts = worstAndBestDao.queryMonthWorstProvince(isCompare,null);
                monthBests = worstAndBestDao.queryMonthBestProvince(isCompare,null);
            }catch (Exception e){
                throw e;
            }finally {
                DataSourceHolder.clearDataSource();
            }
        }

        if(1 == isQueryType || 0 == isQueryType){
            //翻译大类名称
            if(null != monthBests && monthBests.size() > 0
                    && null != types && types.size() > 0){
                for(WorstAndBestModel best : monthBests){
                    for(DepartmentTypeModel type : types){
                        if(type.getDeptId().equals(best.getId())){
                            best.setName(type.getDeptName());
                            break;
                        }
                    }
                }
            }
            if(null != monthWorsts && monthWorsts.size() > 0
                    && null != types && types.size() > 0){
                for(WorstAndBestModel worst : monthWorsts){
                    for(DepartmentTypeModel type : types){
                        if(type.getDeptId().equals(worst.getId())){
                            worst.setName(type.getDeptName());
                            break;
                        }
                    }
                }
            }
        }

        //拼接最好和最坏
        for(FreshDailyReportResponse response : provinces) {
            if (null != worsts && worsts.size() > 0) {
                for (WorstAndBestModel worst : worsts) {
                    if (worst.getRelateName().equals(response.getProvinceName())) {
                        if (2 == isQueryType) {
                            response.setDayWorst(worst.getName());
                        } else {
                            response.setDayWorst(worst.getId() + "-" + worst.getName());
                        }
                        break;
                    }
                }
            }

            if (null != monthWorsts && monthWorsts.size() > 0) {
                for (WorstAndBestModel monthWorst : monthWorsts) {
                    if (monthWorst.getRelateName().equals(response.getProvinceName())) {
                        if (2 == isQueryType) {
                            response.setMonthWorst(monthWorst.getName());
                        } else {
                            response.setMonthWorst(monthWorst.getId() + "-" + monthWorst.getName());
                        }
                        break;
                    }
                }
            }

            if (null != bests && bests.size() > 0) {
                for (WorstAndBestModel best : bests) {
                    if (best.getRelateName().equals(response.getProvinceName())) {
                        if (2 == isQueryType) {
                            response.setDayBest(best.getName());
                        } else {
                            response.setDayBest(best.getId() + "-" + best.getName());
                        }
                        break;
                    }
                }
            }

            if (null != monthBests && monthBests.size() > 0) {
                for (WorstAndBestModel monthBest : monthBests) {
                    if (monthBest.getRelateName().equals(response.getProvinceName())) {
                        if (2 == isQueryType) {
                            response.setMonthBest(monthBest.getName());
                        } else {
                            response.setMonthBest(monthBest.getId() + "-" + monthBest.getName());
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 门店非加工生鲜排序
     * @param list
     * @param isMonth(是否按月排序，0-否，1-是)
     * @return
     */
    private List<StoreFreshDailyReportResponse> sortStoreFreshReport(List<StoreFreshDailyReportResponse> list, final int isMonth){
        List<StoreFreshDailyReportResponse> result = new ArrayList<>();
        if(null != list && list.size() > 0){
            result = list.stream().sorted(new Comparator<StoreFreshDailyReportResponse>() {
                @Override
                public int compare(StoreFreshDailyReportResponse o1, StoreFreshDailyReportResponse o2) {
                    String rateStr1 = "";
                    String rateStr2 = "";
                    if(0 == isMonth){
                        rateStr1 = o1.getDaySaleRate();
                        rateStr2 = o2.getDaySaleRate();
                    }else{
                        rateStr1 = o1.getMonthSaleRate();
                        rateStr2 = o2.getMonthSaleRate();
                    }
                    if(rateStr1.endsWith("%")){
                        rateStr1 = rateStr1.substring(0,rateStr1.lastIndexOf("%"));
                    }
                    if(rateStr2.endsWith("%")){
                        rateStr2 = rateStr2.substring(0,rateStr2.lastIndexOf("%"));
                    }
                    //为空，默认最小（-99999）
                    BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(rateStr1)?"-99999":rateStr1);
                    BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(rateStr2)?"-99999":rateStr2);
                    return rate2.compareTo(rate1);
                }
            }).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 汇总数据
     * @param provinces
     * @param theaters
     * @param areas
     * @return
     */
    private FreshDailyReportListResponse sumFreshReport(List<FreshDailyReportResponse> provinces,
                                                        List<FreshDailyReportResponse> theaters,
                                                        List<FreshDailyReportResponse> areas){
        FreshDailyReportListResponse freshDailyReportListResponse = new FreshDailyReportListResponse();
        List<FreshDailyReportResponse> dayProvinces = null;
        List<FreshDailyReportResponse> monthProvinces = null;
        List<FreshDailyReportResponse> dayTheaters = null;
        List<FreshDailyReportResponse> monthTheaters = null;
        List<FreshDailyReportResponse> dayAreas = null;
        List<FreshDailyReportResponse> monthAreas = null;
        List<FreshDailyReportResponse> dayAreaTrends = null;
        List<FreshDailyReportResponse> monthAreaTrends = null;
        //省数据
        if(null != provinces && provinces.size() > 0){
            dayProvinces = new ArrayList<>();
            monthProvinces = new ArrayList<>();
            dayProvinces.addAll(provinces);
            monthProvinces.addAll(provinces);
        }
        //战区数据
        if(null != theaters && theaters.size() > 0){
            dayTheaters = new ArrayList<>();
            monthTheaters = new ArrayList<>();
            dayTheaters.addAll(theaters);
            monthTheaters.addAll(theaters);
        }
        //区域数据
        if(null != areas && areas.size() > 0){
            dayAreas = new ArrayList<>();
            monthAreas = new ArrayList<>();
            dayAreaTrends = new ArrayList<>();
            monthAreaTrends = new ArrayList<>();

            dayAreas.addAll(areas);
            monthAreas.addAll(areas);
            //区域趋势排名
            dayAreaTrends.addAll(sortFreshReport(areas, 0));
            monthAreaTrends.addAll(sortFreshReport(areas, 1));
        }
        freshDailyReportListResponse.setProvinceDayList(dayProvinces);
        freshDailyReportListResponse.setProvinceMonthList(monthProvinces);
        freshDailyReportListResponse.setTheaterDayList(dayTheaters);
        freshDailyReportListResponse.setTheaterMonthList(monthTheaters);
        freshDailyReportListResponse.setAreaDayList(dayAreas);
        freshDailyReportListResponse.setAreaMonthList(monthAreas);
        freshDailyReportListResponse.setAreaDayTrendList(dayAreaTrends);
        freshDailyReportListResponse.setAreaMonthTrendList(monthAreaTrends);
        return freshDailyReportListResponse;
    }

    /**
     * 非加工生鲜排序
     * @param list
     * @param isMonth(是否按月排序，0-否，1-是)
     * @return
     */
    private List<FreshDailyReportResponse> sortFreshReport(List<FreshDailyReportResponse> list, final int isMonth){
        List<FreshDailyReportResponse> result = new ArrayList<>();
        if(null != list && list.size() > 0){
            result = list.stream().sorted(new Comparator<FreshDailyReportResponse>() {
                @Override
                public int compare(FreshDailyReportResponse o1, FreshDailyReportResponse o2) {
                    String rateStr1 = "";
                    String rateStr2 = "";
                    if(0 == isMonth){
                        rateStr1 = o1.getDaySaleRate();
                        rateStr2 = o2.getDaySaleRate();
                    }else{
                        rateStr1 = o1.getMonthSaleRate();
                        rateStr2 = o2.getMonthSaleRate();
                    }
                    if(rateStr1.endsWith("%")){
                        rateStr1 = rateStr1.substring(0,rateStr1.lastIndexOf("%"));
                    }
                    if(rateStr2.endsWith("%")){
                        rateStr2 = rateStr2.substring(0,rateStr2.lastIndexOf("%"));
                    }
                    BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(rateStr1)?"0":rateStr1);
                    BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(rateStr2)?"0":rateStr2);
                    return rate2.compareTo(rate1);
                }
            }).collect(Collectors.toList());
        }
        return result;
    }


    /**
     * 查询省生鲜汇总
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryFreshProvince(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();
        //是否可比(0-否，1-是)
        int isCompare = param.getIsCompare();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        try{//查询省非加工生鲜品类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryFreshProvince(param);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){//翻译负责人
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getProvinceName())
                                && freshDailyReportModel.getProvinceName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        if(StringUtils.isEmpty(fresh.getAreaName())){//翻译区域名称
                            if(0 == isCompare){
                                fresh.setAreaName("全比合计");
                            }else if(1 == isCompare){
                                fresh.setAreaName("可比合计");
                            }
                        }
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查询省品类汇总
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryCategoryProvince(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        int isCompare = param.getIsCompare();
        try{//查询省品类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryCategoryProvince(param.getCategory(), isCompare);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){//翻译负责人
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getProvinceName())
                                && freshDailyReportModel.getProvinceName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        if(StringUtils.isEmpty(fresh.getAreaName())){//翻译区域名称
                            if(0 == isCompare){
                                fresh.setAreaName("全比合计");
                            }else if(1 == isCompare){
                                fresh.setAreaName("可比合计");
                            }
                        }
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查询省生鲜大类
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryFreshDeptProvince(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();
        //是否可比(0-否，1-是)
        int isCompare = param.getIsCompare();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        try{//查询省非加工生鲜大类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryFreshDeptProvince(param);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){//翻译负责人
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getProvinceName())
                                && freshDailyReportModel.getProvinceName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        if(StringUtils.isEmpty(fresh.getAreaName())){//翻译区域名称
                            if(0 == isCompare){
                                fresh.setAreaName("全比合计");
                            }else if(1 == isCompare){
                                fresh.setAreaName("可比合计");
                            }
                        }
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查询省汇总
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryAllProvince(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        int isCompare = param.getIsCompare();
        try{//查询省品类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryAllProvince(isCompare);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){//翻译负责人
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getProvinceName())
                                && freshDailyReportModel.getProvinceName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        if(StringUtils.isEmpty(fresh.getAreaName())){//翻译区域名称
                            if(0 == isCompare){
                                fresh.setAreaName("全比合计");
                            }else if(1 == isCompare){
                                fresh.setAreaName("可比合计");
                            }
                        }
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }
        return result;
    }


    /**
     * 查询战区所有品类汇总
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryAllTheater(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        int isCompare = param.getIsCompare();
        try{//查询战区非加工生鲜品类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryAllTheater(isCompare);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){//翻译负责人
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getAreaName())
                                && freshDailyReportModel.getAreaName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 通过各战区各品类反推各战区品类汇总数据
     * @param list
     * @return
     */
    private List<FreshDailyReportModel> getAllTheater(List<FreshDailyReportModel> list){
        List<FreshDailyReportModel> result = new ArrayList<>();
        if(null != list && list.size() > 0){
            Map<String, List<FreshDailyReportModel>> map = list.stream().collect(Collectors.groupingBy(FreshDailyReportModel::getAreaName));
            if(null != map && map.size() > 0){
                for(Map.Entry<String, List<FreshDailyReportModel>> entry : map.entrySet()){
                    String theaterName = entry.getKey();
                    List<FreshDailyReportModel> theaters = entry.getValue();
                    if(null != theaters && theaters.size() > 0){
                        FreshDailyReportModel freshDailyReportModel = new FreshDailyReportModel();
                        freshDailyReportModel.setAreaName(theaterName);
                        for(FreshDailyReportModel model : theaters){
                            //设置属性
                            if(StringUtils.isEmpty(freshDailyReportModel.getTypeName())){
                                freshDailyReportModel.setTypeName(model.getTypeName());
                            }
                            //设置省份
                            if(StringUtils.isEmpty(freshDailyReportModel.getProvinceName())){
                                freshDailyReportModel.setProvinceName(model.getProvinceName());
                            }
                            //合计昨日销售额
                            freshDailyReportModel.setDaySale(commonCalculateService.adds(freshDailyReportModel.getDaySale(),
                                    model.getDaySale()));
                            //合计去年同期销售额
                            freshDailyReportModel.setDaySameSale(commonCalculateService.adds(freshDailyReportModel.getDaySameSale(),
                                    model.getDaySameSale()));
                            //合计昨日毛利额
                            freshDailyReportModel.setDayRate(commonCalculateService.adds(
                                    freshDailyReportModel.getDayRate(),
                                    commonCalculateService.dividend(model.getDaySale(), model.getDayProfit(), true)
                            ));
                            //合计去年同期前台毛利额
                            freshDailyReportModel.setDaySameRate(commonCalculateService.adds(
                                    freshDailyReportModel.getDaySameRate(),
                                    commonCalculateService.dividend(model.getDaySameSale(), model.getDaySameRate(), true)
                            ));
                            //合计当月累计销售额
                            freshDailyReportModel.setMonthSale(commonCalculateService.adds(freshDailyReportModel.getMonthSale(),
                                    model.getMonthSale()));
                            //合计去年同月累计销售额
                            freshDailyReportModel.setMonthSameSale(commonCalculateService.adds(freshDailyReportModel.getMonthSameSale(),
                                    model.getMonthSameSale()));
                            //合计当月累计前台毛利额
                            freshDailyReportModel.setMonthRate(commonCalculateService.adds(
                                    freshDailyReportModel.getMonthRate(),
                                    commonCalculateService.dividend(model.getMonthSale(), model.getMonthProfit(), true)
                            ));
                            //合计去年同月前台毛利额
                            freshDailyReportModel.setMonthSameRate(commonCalculateService.adds(
                                    freshDailyReportModel.getMonthSameRate(),
                                    commonCalculateService.dividend(model.getMonthSameSale(), model.getMonthSameRate(), true)
                            ));

                            result.add(freshDailyReportModel);
                        }
                    }
                }

                if(result.size() > 0){
                    for(FreshDailyReportModel freshDailyReportModel : result){
                        //昨日销售增长率
                        freshDailyReportModel.setDaySaleRate(commonCalculateService.calculateSaleRate(
                                freshDailyReportModel.getDaySale(),
                                freshDailyReportModel.getDaySameSale()
                        ));
                        //昨日前台毛利率
                        freshDailyReportModel.setDayProfit(commonCalculateService.calculateProfit(
                                freshDailyReportModel.getDaySale(),
                                freshDailyReportModel.getDayRate()
                        ));
                        //去年同期前台毛利率
                        freshDailyReportModel.setDaySameProfit(commonCalculateService.calculateProfit(
                                freshDailyReportModel.getDaySameSale(),
                                freshDailyReportModel.getDaySameRate()
                        ));
                        //昨日前台毛利率增长
                        freshDailyReportModel.setDayProfitRate(commonCalculateService.calculateAddProfitRate(
                                commonCalculateService.calculateProfit(
                                        freshDailyReportModel.getDaySale(),
                                        freshDailyReportModel.getDayRate()
                                ),
                                commonCalculateService.calculateProfit(
                                        freshDailyReportModel.getDaySameSale(),
                                        freshDailyReportModel.getDaySameRate()
                                )
                        ));
                        //当月累计销售增长率
                        freshDailyReportModel.setMonthSaleRate(commonCalculateService.calculateSaleRate(
                                freshDailyReportModel.getMonthSale(),
                                freshDailyReportModel.getMonthSameSale()
                        ));
                        //当月累计前台毛利率
                        freshDailyReportModel.setMonthProfit(commonCalculateService.calculateProfit(
                                        freshDailyReportModel.getMonthSale(),
                                        freshDailyReportModel.getMonthRate()
                        ));
                        //去年同月前台毛利率
                        freshDailyReportModel.setMonthSameProfit(commonCalculateService.calculateProfit(
                                freshDailyReportModel.getMonthSameSale(),
                                freshDailyReportModel.getMonthSameRate()
                        ));
                        //当月前台毛利率增长
                        freshDailyReportModel.setMonthProfitRate(commonCalculateService.calculateAddProfitRate(
                                commonCalculateService.calculateProfit(
                                        freshDailyReportModel.getMonthSale(),
                                        freshDailyReportModel.getMonthRate()
                                ),
                                commonCalculateService.calculateProfit(
                                        freshDailyReportModel.getMonthSameSale(),
                                        freshDailyReportModel.getMonthSameRate()
                                )
                        ));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查询战区品类数据
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryCategoryTheater(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        int isCompare = param.getIsCompare();
        try{//查询战区品类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryCategoryTheater(param.getCategory(), isCompare);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){//翻译负责人
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getAreaName())
                                && freshDailyReportModel.getAreaName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查询战区生鲜大类
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryFreshDeptTheater(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();

        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        try{//查询战区非加工生鲜大类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryFreshDeptTheater(param);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){//翻译负责人
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getAreaName())
                                && freshDailyReportModel.getAreaName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查询区域品类汇总
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryCategoryArea(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();
        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        try{//查询区域品类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryCategoryArea(param.getCategory(), param.getIsCompare());
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //翻译负责人
        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getAreaName())
                                && freshDailyReportModel.getAreaName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }
        //翻译区域ID
        if(result.size() > 0){
            for(FreshDailyReportResponse freshDailyReportResponse : result){
                //设置是否下钻(0-否，1-是)
                freshDailyReportResponse.setIsDown(1);
                for(OrganizationForFresh organization : orgList){
                    if(freshDailyReportResponse.getAreaName().equals(organization.getAreaName())){
                        freshDailyReportResponse.setAreaId(organization.getAreaId());
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查询区域数据
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryAllArea(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();
        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        try{//查询区域品类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryAllArea(param.getIsCompare());
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        //翻译负责人
        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getAreaName())
                                && freshDailyReportModel.getAreaName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }
        //翻译区域ID
        if(result.size() > 0){
            for(FreshDailyReportResponse freshDailyReportResponse : result){
                //设置是否下钻(0-否，1-是)
                freshDailyReportResponse.setIsDown(1);
                for(OrganizationForFresh organization : orgList){
                    if(freshDailyReportResponse.getAreaName().equals(organization.getAreaName())){
                        freshDailyReportResponse.setAreaId(organization.getAreaId());
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查询区域生鲜大类
     * @param param
     * @return
     */
    private List<FreshDailyReportResponse> queryFreshDeptArea(FreshDailyReportRequest param){
        List<FreshDailyReportResponse> result = new ArrayList<>();

        //查询组织层次
        List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
        //查询区域负责人
        List<AreaPersonModel> areaPersonList = dailyReportDao.queryAreaPersonModel();
        List<FreshDailyReportModel> list = null;
        try{//查询区域非加工生鲜大类汇总数据
            changeDgDataSource();
            list = dailyReportDao.queryFreshDeptArea(param);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //翻译负责人
        if(null != list && list.size() > 0){
            if(null != areaPersonList && areaPersonList.size() > 0){
                for(AreaPersonModel areaPersonModel : areaPersonList){
                    FreshDailyReportModel fresh = null;
                    for(FreshDailyReportModel freshDailyReportModel : list){
                        if(StringUtils.isNotEmpty(freshDailyReportModel.getAreaName())
                                && freshDailyReportModel.getAreaName().equals(areaPersonModel.getAreaName())){
                            freshDailyReportModel.setUserName(areaPersonModel.getUserName());
                            fresh = freshDailyReportModel;
                            break;
                        }
                    }
                    if(null != fresh){
                        FreshDailyReportResponse freshDailyReportResponse = new FreshDailyReportResponse();
                        BeanUtils.copyProperties(fresh, freshDailyReportResponse);
                        result.add(freshDailyReportResponse);
                    }
                }
            }
        }

        //翻译区域ID
        if(result.size() > 0){
            for(FreshDailyReportResponse freshDailyReportResponse : result){
                //设置是否下钻(0-否，1-是)
                freshDailyReportResponse.setIsDown(1);
                for(OrganizationForFresh organization : orgList){
                    if(freshDailyReportResponse.getAreaName().equals(organization.getAreaName())){
                        freshDailyReportResponse.setAreaId(organization.getAreaId());
                        break;
                    }
                }
            }
        }

        return result;
    }


    /**
     * 查询实时日报表
     * @param request
     * @return
     */
    private PageResult<DailyReportResponse> queryCurDailyReport(DailyReportRequest request,List<OrganizationForFresh> orgList){
        PageResult<DailyReportResponse> pageResult = new PageResult<>();
        //梯度（1-全司，2-省份，3-区域,4-门店）
        int mark = request.getMark();
        //用户权限（1-全司，2-省份，3-区域,4-门店）
        int grade = request.getGrade();
        if(4 == grade){
            pageResult = queryCurrentDailyReportForStore(request, orgList);
        }else if(3 == grade){
            if(4 == mark){
                pageResult = queryCurrentDailyReportForStore(request, orgList);
            }else{
                pageResult = queryCurrentDailyReportForArea(request, orgList);
            }
        }else if(2 == grade){
            if(4 == mark){
                pageResult = queryCurrentDailyReportForStore(request, orgList);
            }else if(3 == mark){
                pageResult = queryCurrentDailyReportForArea(request, orgList);
            }else{
                pageResult = queryCurrentDailyReportForProvince(request, orgList);
            }
        }else{
            if(4 == mark){
                pageResult = queryCurrentDailyReportForStore(request, orgList);
            }else if(3 == mark){
                pageResult = queryCurrentDailyReportForArea(request, orgList);
            }else if(2 == mark){
                pageResult = queryCurrentDailyReportForProvince(request, orgList);
            }else{
                pageResult = queryCurrentDailyReportForCompany(request, orgList);
            }
        }
        return pageResult;
    }

    /**
     * 查询实时全司日报表
     * @param request
     * @param orgList
     * @return
     */
    private PageResult<DailyReportResponse> queryCurrentDailyReportForCompany(DailyReportRequest request,List<OrganizationForFresh> orgList){
        PageResult<DailyReportResponse> pageResult = new PageResult<>();
        List<DailyReportResponse> list = new ArrayList<>();
        int page = request.getPage();
        int pageSize = request.getPageSize();
        PageResult<CompanyDailyReportModel> companys = null;
        try{
            changeDgDataSource();
            companys = dailyReportDao.queryCompanyDailyReport(page,pageSize);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != companys){
            if(null != companys.getDatas() && companys.getDatas().size() > 0){
                for(CompanyDailyReportModel companyDailyReportModel : companys.getDatas()){
                    //获取ID
                    if(null != orgList && orgList.size() > 0){
                        for(OrganizationForFresh org : orgList){
                            if(StringUtils.isNotEmpty(companyDailyReportModel.getProvinceName())
                                    && companyDailyReportModel.getProvinceName().equals(org.getProvinceName())){
                                companyDailyReportModel.setProvinceId(org.getProvinceId());
                                break;
                            }
                        }
                    }
                    CompanyDailyReportResponse companyDailyReportResponse = new CompanyDailyReportResponse();
                    BeanUtils.copyProperties(companyDailyReportModel,companyDailyReportResponse);
                    //设置当前梯度（1-全司，2-省份，3-区域,4-门店）
                    companyDailyReportResponse.setMark(1);
                    list.add(companyDailyReportResponse);
                }
            }
            pageResult.setDatas(list);
            pageResult.setPage(companys.getPage());
            pageResult.setPageSize(companys.getPageSize());
            pageResult.setTotal(companys.getTotal());
        }
        return pageResult;
    }

    /**
     * 查询实时省日报表
     * @param request
     * @param orgList
     * @return
     */
    private PageResult<DailyReportResponse> queryCurrentDailyReportForProvince(DailyReportRequest request,List<OrganizationForFresh> orgList){
        PageResult<DailyReportResponse> pageResult = new PageResult<>();
        List<DailyReportResponse> list = new ArrayList<>();
        //0-全部，1-可比，2-全比
        int key = request.getKey();
        int page = request.getPage();
        int pageSize = request.getPageSize();
        PageResult<ProvinceDailyReportModel> provinces = null;
        try{
            changeDgDataSource();
            provinces = dailyReportDao.queryProvinceDailyReport(
                    getNameById(request.getProvinceId(),orgList),
                    key,
                    page,
                    pageSize);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        if(null != provinces) {
            if (null != provinces.getDatas() && provinces.getDatas().size() > 0) {
                for (ProvinceDailyReportModel provinceDailyReportModel : provinces.getDatas()) {
                    //翻译ID
                    if (null != orgList && orgList.size() > 0) {
                        for (OrganizationForFresh org : orgList) {
                            if(StringUtils.isNotEmpty(provinceDailyReportModel.getProvinceName())
                                    && provinceDailyReportModel.getProvinceName().equals(org.getProvinceName())){
                                provinceDailyReportModel.setProvinceId(org.getProvinceId());
                                break;
                            }
                        }
                    }
                    ProvinceDailyReportResponse provinceDailyReportResponse = new ProvinceDailyReportResponse();
                    BeanUtils.copyProperties(provinceDailyReportModel, provinceDailyReportResponse);
                    //设置当前梯度（1-全司，2-省份，3-区域,4-门店）
                    provinceDailyReportResponse.setMark(2);
                    list.add(provinceDailyReportResponse);
                }
            }
            pageResult.setDatas(list);
            pageResult.setPage(provinces.getPage());
            pageResult.setPageSize(provinces.getPageSize());
            pageResult.setTotal(provinces.getTotal());
        }
        return pageResult;
    }

    /**
     * 查询实时区域日报表
     * @param request
     * @param orgList
     * @return
     */
    private PageResult<DailyReportResponse> queryCurrentDailyReportForArea(DailyReportRequest request,List<OrganizationForFresh> orgList){
        PageResult<DailyReportResponse> pageResult = new PageResult<>();
        List<DailyReportResponse> list = new ArrayList<>();
        //0-全部，1-可比，2-全比
        int key = request.getKey();
        int page = request.getPage();
        int pageSize = request.getPageSize();
        PageResult<AreaDailyReportModel> areas = null;
        try{
            changeDgDataSource();
            areas = dailyReportDao.queryAreaDailyReport(
                    getNameById(request.getProvinceId(),orgList),
                    getNameById(request.getAreaId(),orgList),
                    key,
                    page,
                    pageSize);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != areas){
            if(null != areas.getDatas() && areas.getDatas().size() > 0){
                for(AreaDailyReportModel areaDailyReportModel : areas.getDatas()){
                    if(null != orgList && orgList.size() > 0){
                        //翻译ID
                        for(OrganizationForFresh org : orgList){
                            if(StringUtils.isNotEmpty(areaDailyReportModel.getProvinceName())
                                    && areaDailyReportModel.getProvinceName().equals(org.getProvinceName())
                                    && StringUtils.isNotEmpty(areaDailyReportModel.getAreaName())
                                    && areaDailyReportModel.getAreaName().equals(org.getAreaName())){
                                areaDailyReportModel.setProvinceId(org.getProvinceId());
                                areaDailyReportModel.setAreaId(org.getAreaId());
                                break;
                            }
                        }
                    }

                    AreaDailyReportResponse areaDailyReportResponse = new AreaDailyReportResponse();
                    BeanUtils.copyProperties(areaDailyReportModel,areaDailyReportResponse);
                    //设置当前梯度（1-全司，2-省份，3-区域,4-门店）
                    areaDailyReportResponse.setMark(3);
                    list.add(areaDailyReportResponse);
                }
            }
            pageResult.setDatas(list);
            pageResult.setPage(areas.getPage());
            pageResult.setPageSize(areas.getPageSize());
            pageResult.setTotal(areas.getTotal());
        }
        return pageResult;
    }

    /**
     * 查询实时门店日报表
     * @param request
     * @param orgList
     * @return
     */
    private PageResult<DailyReportResponse> queryCurrentDailyReportForStore(DailyReportRequest request,List<OrganizationForFresh> orgList){
        PageResult<DailyReportResponse> pageResult = new PageResult<>();
        List<DailyReportResponse> list = new ArrayList<>();
        int page = request.getPage();
        int pageSize = request.getPageSize();
        PageResult<StoreDailyReportModel> stores = null;
        try{
            changeDgDataSource();
            stores = dailyReportDao.queryStoreDailyReport(
                    getNameById(request.getProvinceId(),orgList),
                    getNameById(request.getAreaId(),orgList),
                    getNameById(request.getStoreId(),orgList),
                    page,
                    pageSize);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != stores){
            if(null != stores.getDatas() && stores.getDatas().size() > 0){
                for(StoreDailyReportModel storeDailyReportModel : stores.getDatas()){
                    if(null != orgList && orgList.size() > 0){
                        //翻译ID
                        for(OrganizationForFresh org : orgList){
                            if(StringUtils.isNotEmpty(storeDailyReportModel.getProvinceName())
                                    && storeDailyReportModel.getProvinceName().equals(org.getProvinceName())
                                    && StringUtils.isNotEmpty(storeDailyReportModel.getAreaName())
                                    && storeDailyReportModel.getAreaName().equals(org.getAreaName())
                                    && StringUtils.isNotEmpty(storeDailyReportModel.getStoreName())
                                    && storeDailyReportModel.getStoreName().equals(org.getStoreName())){
                                storeDailyReportModel.setProvinceId(org.getProvinceId());
                                storeDailyReportModel.setAreaId(org.getAreaId());
                                storeDailyReportModel.setStoreId(org.getStoreId());
                                break;
                            }
                        }
                    }

                    StoreDailyReportResponse storeDailyReportResponse = new StoreDailyReportResponse();
                    BeanUtils.copyProperties(storeDailyReportModel,storeDailyReportResponse);
                    //设置当前梯度（1-全司，2-省份，3-区域,4-门店）
                    storeDailyReportResponse.setMark(4);
                    list.add(storeDailyReportResponse);
                }
            }
            pageResult.setDatas(list);
            pageResult.setPage(stores.getPage());
            pageResult.setPageSize(stores.getPageSize());
            pageResult.setTotal(stores.getTotal());
        }
        return pageResult;
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
     * 根据下级推上级(将原有参数覆盖)
     * @param request
     * @param orgList
     */
    private void setUpGradeByDownGrade(DailyReportRequest request,List<OrganizationForFresh> orgList){
        String storeIds = request.getStoreId();
        String areaIds = request.getAreaId();
        if(StringUtils.isNotEmpty(storeIds)){
            String[] arr = storeIds.split(",");
            Set<String> provinceIdSet = new HashSet<>();
            Set<String> areaIdSet = new HashSet<>();
            for(int i=0; i < arr.length; i++){
                for(OrganizationForFresh org : orgList){
                    if(arr[i].equals(org.getStoreId())){
                        provinceIdSet.add(org.getProvinceId());
                        areaIdSet.add(org.getAreaId());
                        break;
                    }
                }
            }
            if(provinceIdSet.size() > 0){
                String idStr = provinceIdSet.toString();
                request.setProvinceId(idStr.substring(1,idStr.lastIndexOf("]")));
            }
            if(areaIdSet.size() > 0){
                String idStr = areaIdSet.toString();
                request.setAreaId(idStr.substring(1,idStr.lastIndexOf("]")));
            }
        }else if(StringUtils.isNotEmpty(areaIds)){
            String[] arr = areaIds.split(",");
            Set<String> provinceIdSet = new HashSet<>();
            for (int i = 0; i < arr.length; i++) {
                for(OrganizationForFresh org : orgList){
                    if(arr[i].equals(org.getAreaId())){
                        provinceIdSet.add(org.getProvinceId());
                        break;
                    }
                }
            }
            if(provinceIdSet.size() > 0){
                String idStr = provinceIdSet.toString();
                request.setProvinceId(idStr.substring(1,idStr.lastIndexOf("]")));
            }
        }
    }

    private void transform(DailyReportRequest request){
        String provinceId = request.getProvinceId();
        String areaId = request.getAreaId();
        String storeId = request.getStoreId();
        String deptId = request.getDeptId();
        String category = request.getCategory();

        if(StringUtils.isNotEmpty(provinceId)){
            if(provinceId.endsWith(",")){
                request.setProvinceId(provinceId.substring(0,provinceId.lastIndexOf(",")));
            }
        }

        if(StringUtils.isNotEmpty(areaId)){
            if(areaId.endsWith(",")){
                request.setAreaId(areaId.substring(0,areaId.lastIndexOf(",")));
            }
        }

        if(StringUtils.isNotEmpty(storeId)){
            if(storeId.endsWith(",")){
                request.setStoreId(storeId.substring(0,storeId.lastIndexOf(",")));
            }
        }

        if(StringUtils.isNotEmpty(deptId)){
            if(deptId.endsWith(",")){
                request.setDeptId(deptId.substring(0,deptId.lastIndexOf(",")));
            }
        }

        if(StringUtils.isNotEmpty(category)){
            if(category.endsWith(",")){
                request.setCategory(category.substring(0,category.lastIndexOf(",")));
            }
        }
    }

    private void transformFresh(FreshDailyReportRequest request){
        String provinceId = request.getProvinceId();
        String areaId = request.getAreaId();
        String storeId = request.getStoreId();
        String deptId = request.getDeptId();
        String category = request.getCategory();

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
    
    
    
    /**
     * 门店大类汇总
     * @param request
     * @param orgList
     * @return
     */
    private CategoryDailyReportListResponse queryStoreCategoryReport(DailyReportRequest request,List<OrganizationForFresh> orgList){
        CategoryDailyReportListResponse reportListResponse = new CategoryDailyReportListResponse();
        List<StoreDailyReportResponse> dailyReportResponseList = new ArrayList<>();
        List<StoreDailyReportModel> list = null;
        try{
        	 changeDgDataSource();
        	 request.setProvinceId(getNameById(request.getProvinceId(),orgList));
        	 request.setAreaId(getNameById(request.getAreaId(),orgList));
        	 request.setStoreId(getNameById(request.getStoreId(),orgList));
             list = dailyReportDao.queryStoreCategoryReport(request);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != list && list.size() > 0){
            for(StoreDailyReportModel model : list){
                    if(null != orgList && orgList.size() > 0){
                        //翻译ID
                        for(OrganizationForFresh org : orgList){
                        	if(StringUtils.isNotEmpty(model.getProvinceName())
                                    && model.getProvinceName().equals(org.getProvinceName())
                                    && StringUtils.isNotEmpty(model.getAreaName())
                                    && model.getAreaName().equals(org.getAreaName())
                                    && StringUtils.isNotEmpty(model.getStoreName())
                                    && model.getStoreName().equals(org.getStoreName())){
                        		model.setProvinceId(org.getProvinceId());
                        		model.setAreaId(org.getAreaId());
                        		model.setStoreId(org.getStoreId());
                                break;
                            }
                        }
                    }
                    StoreDailyReportResponse storeDailyReportResponse = new StoreDailyReportResponse();
                    BeanUtils.copyProperties(model,storeDailyReportResponse);
                    dailyReportResponseList.add(storeDailyReportResponse);
                }	
        }
        
        reportListResponse.setStoreList(dailyReportResponseList);
        return reportListResponse;
    }
    
    /**
     * 区域大类汇总
     * @param request
     * @param orgList
     * @return
     */
    private CategoryDailyReportListResponse queryAreaCategoryReport(DailyReportRequest request,List<OrganizationForFresh> orgList){
        CategoryDailyReportListResponse reportListResponse = new CategoryDailyReportListResponse();
        List<AreaDailyReportResponse> dailyReportResponseList = new ArrayList<>();
        List<AreaDailyReportModel> list = null;
        try{
        	 changeDgDataSource();
        	 request.setProvinceId(getNameById(request.getProvinceId(),orgList));
        	 request.setAreaId(getNameById(request.getAreaId(),orgList));
             list = dailyReportDao.queryAreaCategoryReport(request);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != list && list.size() > 0){
            for(AreaDailyReportModel model : list){
                    if(null != orgList && orgList.size() > 0){
                        //翻译ID
                        for(OrganizationForFresh org : orgList){
                        	if(StringUtils.isNotEmpty(model.getProvinceName())
                                    && model.getProvinceName().equals(org.getProvinceName())
                                    && StringUtils.isNotEmpty(model.getAreaName())
                                    && model.getAreaName().equals(org.getAreaName())){
                        		model.setProvinceId(org.getProvinceId());
                        		model.setAreaId(org.getAreaId());
                                break;
                            }
                        }
                    }

                    AreaDailyReportResponse areaDailyReportResponse = new AreaDailyReportResponse();
                    BeanUtils.copyProperties(model,areaDailyReportResponse);
                    dailyReportResponseList.add(areaDailyReportResponse);
                }	
        }
        
        reportListResponse.setAreaList(dailyReportResponseList);
        return reportListResponse;
    }
    
    /**
     * 省份大类汇总
     * @param request
     * @param orgList
     * @return
     */
    private CategoryDailyReportListResponse queryProvinceCategoryReport(DailyReportRequest request,List<OrganizationForFresh> orgList){
        CategoryDailyReportListResponse reportListResponse = new CategoryDailyReportListResponse();
        List<ProvinceDailyReportResponse> dailyReportResponseList = new ArrayList<>();
        List<ProvinceDailyReportModel> list = null;
        try{
        	 changeDgDataSource();
        	 request.setProvinceId(getNameById(request.getProvinceId(),orgList));
             list = dailyReportDao.queryProvinceCategoryReport(request);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != list && list.size() > 0){
            for(ProvinceDailyReportModel model : list){
                    if(null != orgList && orgList.size() > 0){
                        //翻译ID
                        for(OrganizationForFresh org : orgList){
                        	if(StringUtils.isNotEmpty(model.getProvinceName())
                                    && model.getProvinceName().equals(org.getProvinceName())){
                        		model.setProvinceId(org.getProvinceId());
                                break;
                            }
                        }
                    }

                    ProvinceDailyReportResponse provinceDailyReportResponse = new ProvinceDailyReportResponse();
                    BeanUtils.copyProperties(model,provinceDailyReportResponse);
                    dailyReportResponseList.add(provinceDailyReportResponse);
                }	
        }
        
        reportListResponse.setProvinceList(dailyReportResponseList);
        return reportListResponse;
    }
    
    /**
     * 全司大类日报
     * @return
     */
    private CategoryDailyReportListResponse queryEnterpriseCategoryReport(){
        CategoryDailyReportListResponse reportListResponse = new CategoryDailyReportListResponse();
        List<CategoryDailyReportResponse> dailyReportResponseList = new ArrayList<>();
        List<CategoryDailyReportModel> list = null;
        try{
            changeDgDataSource();
            list = dailyReportDao.queryEnterpriseCategoryReport();
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }
        if(null != list && list.size() > 0){
            for(CategoryDailyReportModel model : list){
                CategoryDailyReportResponse categoryDailyReportResponse = new CategoryDailyReportResponse();
                BeanUtils.copyProperties(model,categoryDailyReportResponse);
                dailyReportResponseList.add(categoryDailyReportResponse);
            }
        }
        reportListResponse.setEnterpriseList(dailyReportResponseList);
        return reportListResponse;
    }
    
    /**
     * 查询门店日销售负毛利商品TOP30
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public StoreLossGoodsListResponse queryStoreLossGoods(DailyReportRequest request) throws Exception {
    	StoreLossGoodsListResponse reportListResponse = new StoreLossGoodsListResponse();
        List<StoreLossGoodsResponse> dailyReportResponseList = new ArrayList<>();
        List<StoreLossGoodsModel> list = null;
        StoreLossGoodsSumResponse modelSum = new StoreLossGoodsSumResponse();
        try{
        	this.changeDataSource(request.getStoreId());
            list = dailyReportDao.queryStoreLossGoodsReport(request);
            modelSum = dailyReportDao.queryStoreLossGoodsSum(request);
        }catch (Exception e){
            throw e;
        }finally {
        	DataSourceHolder.clearDataSource();//使用完后关闭切换数据源
        }
        //主数据明细
        if(null != list && list.size() > 0){
            for(StoreLossGoodsModel model : list){
            	StoreLossGoodsResponse storeLossGoodsResponse = new StoreLossGoodsResponse();
                BeanUtils.copyProperties(model,storeLossGoodsResponse);
                storeLossGoodsResponse.setUnit("元");
                dailyReportResponseList.add(storeLossGoodsResponse);
            }
        }
        reportListResponse.setStoreLossList(dailyReportResponseList);
        //汇总
        if(!StringUtils.isNull(modelSum)){
        	reportListResponse.setStoreLossSum(modelSum);;
        }
        return reportListResponse;
    }
    
    /**
     * 门店负毛利商品下钻明细
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public StoreLossGoodsListResponse getStoreLossGoodsDetail(DailyReportRequest request) throws Exception {
    	StoreLossGoodsListResponse reportListResponse = new StoreLossGoodsListResponse();
        List<StoreLossGoodsDetailResponse> dailyReportResponseList = new ArrayList<>();
        List<StoreLossGoodsDetailModel> list = null;
        try{
        	changeDgDataSource();
            list = dailyReportDao.queryStoreLossGoodsDetailReport(request);
        }catch (Exception e){
            throw e;
        }finally {
        	DataSourceHolder.clearDataSource();//使用完后关闭切换数据源
        }
        if(null != list && list.size() > 0){
            for(StoreLossGoodsDetailModel model : list){
            	StoreLossGoodsDetailResponse storeLossGoodsDetailResponse = new StoreLossGoodsDetailResponse();
                BeanUtils.copyProperties(model,storeLossGoodsDetailResponse);
                storeLossGoodsDetailResponse.setUnit("元");
                dailyReportResponseList.add(storeLossGoodsDetailResponse);
            }
        }
        reportListResponse.setStoreLossDetailList(dailyReportResponseList);
        return reportListResponse;
    }
    
    /**
     * 单品毛利率卡片
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public StoreLossGoodsListResponse getGrossProfitGoods(DailyReportRequest request) throws Exception {
    	StoreLossGoodsListResponse reportListResponse = new StoreLossGoodsListResponse();
        List<GrossProfitGoodsResponse> dailyReportResponseList = new ArrayList<>();
        List<GrossProfitGoodsModel> list = null;
        try{
        	changeDgDataSource();
            list = dailyReportDao.queryGrossProfitGoodsReport(request);
        }catch (Exception e){
            throw e;
        }finally {
        	DataSourceHolder.clearDataSource();//使用完后关闭切换数据源
        }
        if(null != list && list.size() > 0){
            for(GrossProfitGoodsModel model : list){
            	GrossProfitGoodsResponse grossProfitGoodsResponse = new GrossProfitGoodsResponse();
                BeanUtils.copyProperties(model,grossProfitGoodsResponse);
                dailyReportResponseList.add(grossProfitGoodsResponse);
            }
        }
        reportListResponse.setGrossProfitGoodsList(dailyReportResponseList);
        return reportListResponse;
    }
    
    /**
     * 查询门店连续负毛利商品
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public ContinuityLossGoodsListResponse getContinuityLossGoods(DailyReportRequest request) throws Exception {
    	ContinuityLossGoodsListResponse reportListResponse = new ContinuityLossGoodsListResponse();
        List<ContinuityLossGoodsResponse> dailyReportResponseList = new ArrayList<>();
        List<ContinuityLossGoodsModel> list = null;
        try{
        	this.changeDataSource(request.getStoreId());
            list = dailyReportDao.queryStoreContinuityLossGoodsReport(request);
        }catch (Exception e){
            throw e;
        }finally {
        	DataSourceHolder.clearDataSource();//使用完后关闭切换数据源
        }
        if(null != list && list.size() > 0){
            for(ContinuityLossGoodsModel model : list){
            	ContinuityLossGoodsResponse continuityLossGoodsResponse = new ContinuityLossGoodsResponse();
                BeanUtils.copyProperties(model,continuityLossGoodsResponse);
                dailyReportResponseList.add(continuityLossGoodsResponse);
            }
        }
        reportListResponse.setContinuityLossList(dailyReportResponseList);
        return reportListResponse;
    }
    
    /**
     * 门店连续负毛利商品下钻
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public ContinuityLossGoodsListResponse getContinuityLossGoodsDetail(DailyReportRequest request) throws Exception {
    	ContinuityLossGoodsListResponse reportListResponse = new ContinuityLossGoodsListResponse();
        List<ContinuityLossGoodsDetailResponse> dailyReportResponseList = new ArrayList<>();
        List<ContinuityLossGoodsDetailModel> list = null;
        ContinuityLossGoodsDetailSumResponse modelSum = new ContinuityLossGoodsDetailSumResponse();
        try{
        	this.changeDataSource(request.getStoreId());
            list = dailyReportDao.queryContinuityLossGoodsDetailReport(request);
            modelSum = dailyReportDao.queryCTGoodsSumReport(request);
        }catch (Exception e){
            throw e;
        }finally {
        	DataSourceHolder.clearDataSource();//使用完后关闭切换数据源
        }
        if(null != list && list.size() > 0){
            for(ContinuityLossGoodsDetailModel model : list){
            	ContinuityLossGoodsDetailResponse continuityLossGoodsDetailResponse = new ContinuityLossGoodsDetailResponse();
                BeanUtils.copyProperties(model,continuityLossGoodsDetailResponse);
                continuityLossGoodsDetailResponse.setUnit("元");
                dailyReportResponseList.add(continuityLossGoodsDetailResponse);
            }
        }
        //汇总
        if(!StringUtils.isNull(modelSum)){
        	reportListResponse.setContinuityLossSum(modelSum);;
        }
        reportListResponse.setContinuityLossDetailList(dailyReportResponseList);
        return reportListResponse;
    }
    
    /**
     * 门店大类库存金额
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public StoreLargeClassMoneyListResponse getStoreLargeClassMoney(DailyReportRequest request) throws Exception {
    	StoreLargeClassMoneyListResponse reportListResponse = new StoreLargeClassMoneyListResponse();
        List<StoreLargeClassMoneyResponse> dailyReportResponseList = new ArrayList<>();
        List<StoreLargeClassMoneyModel> storeList = null;
        List<StoreLargeClassMoneyModel> actualDaysList = null;
        List<StoreLargeClassMoneyModel> standardDaysList = null;
        
        try{//切换门店数据源查基础数据
        	this.changeDataSource(request.getStoreId());
        	storeList = dailyReportDao.queryStoreLargeClassMoney(request);
        	if(null != storeList && storeList.size() > 0) {
        		
        	}
        }catch (Exception e){
            throw e;
        }finally {
        	DataSourceHolder.clearDataSource();//使用完后关闭切换数据源
        }
        
        try{//切换rms数据源
        	changeDgDataSource();
        	standardDaysList = dailyReportDao.queryStoreStandardDays(request);
        	actualDaysList = dailyReportDao.queryStoreActualDays(request);
        }catch (Exception e){
            throw e;
        }finally {
        	DataSourceHolder.clearDataSource();//使用完后关闭切换数据源
        }
        
        if(null != storeList && storeList.size() > 0){
            for(StoreLargeClassMoneyModel storeModel : storeList){
            	StoreLargeClassMoneyResponse storeLargeClassMoneyResponse = new StoreLargeClassMoneyResponse();
                BeanUtils.copyProperties(storeModel,storeLargeClassMoneyResponse);
                storeLargeClassMoneyResponse.setUnit("元");
                //放入实际周转天数
                for(StoreLargeClassMoneyModel cdmbModel : actualDaysList){
                	if(request.getStoreId().equals(cdmbModel.getStore())&&storeLargeClassMoneyResponse.getDept().equals(cdmbModel.getDept())) {
                		storeLargeClassMoneyResponse.setActualDays(cdmbModel.getActualDays());
                	}
                	
                }
                
              //放入标准周转天数
                for(StoreLargeClassMoneyModel rmsModel : standardDaysList){
                	if(request.getStoreId().equals(rmsModel.getStore())&&storeLargeClassMoneyResponse.getDept().equals(rmsModel.getDept())) {
                		storeLargeClassMoneyResponse.setStandardDays(rmsModel.getStandardDays());
                	}
                	
                }
                
                dailyReportResponseList.add(storeLargeClassMoneyResponse);
            }
        }
        reportListResponse.setStoreMoneyList(dailyReportResponseList);
        return reportListResponse;
    }
    
    /***
     * 切换数据源
     * @param storeId
     * @throws Exception
     */
    private void changeDataSource(String storeId) throws Exception {
		GoodsDataSourceConfig goodsDataSourceConfig = goodsDao.getDataSourceByStoreId(storeId);
		if (goodsDataSourceConfig != null) {
			if ("11".equals(goodsDataSourceConfig.getChain())) {
				DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, csUserName,
						csPassword, druidProperties);
				DataSourceHolder.setDataSource(dataSourceBuilder);
			} else if ("13".equals(goodsDataSourceConfig.getChain())) {
				DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, jdUserName,
						jdPassword, druidProperties);
				DataSourceHolder.setDataSource(dataSourceBuilder);
			} else {
				ExceptionUtils.wapperBussinessException("暂时不支持该业态");
			}

		} else {
			ExceptionUtils.wapperBussinessException("未获取到门店数据源");
		}
	}
}
