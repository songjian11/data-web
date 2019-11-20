package com.cs.mobile.api.service.dailyreport;

import java.util.List;

import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.dailyreport.request.DailyReportRequest;
import com.cs.mobile.api.model.dailyreport.request.FreshDailyReportRequest;
import com.cs.mobile.api.model.dailyreport.response.*;
import com.cs.mobile.api.model.reportPage.ReportCommonParam;

public interface DailyReportService {
    /**
     * 查询机构汇总
     * @param request
     * @return
     * @throws Exception
     */
    public PageResult<DailyReportResponse> queryDailyReport(DailyReportRequest request) throws Exception;
    
    
    /***
     * 大类下钻
     * @param request
     * @return
     * @throws Exception
     */
    public CategoryDailyReportListResponse queryCategoryDrillDown(DailyReportRequest request) throws Exception;

    /**
     * 查询战区报表
     * @param param
     * @return
     * @throws Exception
     */
    FreshDailyReportListResponse queryFreshDailyReport(FreshDailyReportRequest param)throws Exception;

    /**
     * 查询门店战区报表
     * @param param
     * @return
     * @throws Exception
     */
    StoreFreshDailyReportListResponse queryStoreFreshDailyReport(FreshDailyReportRequest param)throws Exception;
    
    /***
     * 门店负毛利商品TOP30查询
     * @param request
     * @return
     * @throws Exception
     */
    public StoreLossGoodsListResponse queryStoreLossGoods(DailyReportRequest request) throws Exception;
    
    /***
     * 门店负毛利商品下钻明细
     * @param request
     * @return
     * @throws Exception
     */
    public StoreLossGoodsListResponse getStoreLossGoodsDetail(DailyReportRequest request) throws Exception;
    
    /***
     * 获取商品毛利率卡片
     * @param request
     * @return
     * @throws Exception
     */
    public StoreLossGoodsListResponse getGrossProfitGoods(DailyReportRequest request) throws Exception;
    
    /***
     * 门店连续负毛利商品
     * @param request
     * @return
     * @throws Exception
     */
    public ContinuityLossGoodsListResponse getContinuityLossGoods(DailyReportRequest request) throws Exception;
    
    /***
     * 门店连续负毛利商品下钻
     * @param request
     * @return
     * @throws Exception
     */
    public ContinuityLossGoodsListResponse getContinuityLossGoodsDetail(DailyReportRequest request) throws Exception;
    
    /***
     * 门店大类库存金额
     * @param request
     * @return
     * @throws Exception
     */
    public StoreLargeClassMoneyListResponse getStoreLargeClassMoney(DailyReportRequest request) throws Exception;
    
}
