package com.cs.mobile.api.service.freshspecialreport;

import com.cs.mobile.api.model.freshspecialreport.request.FreshSpecialReportRequest;
import com.cs.mobile.api.model.freshspecialreport.response.FreshSpecialReportRankListResponse;
import com.cs.mobile.api.model.freshspecialreport.response.FreshSpecialReportResponse;
import com.cs.mobile.api.model.freshspecialreport.response.FreshSpecialReportTrendListResponse;

public interface FreshSpecialReportService {
    /**
     * 查询专题报表页
     * @param param
     * @return
     * @throws Exception
     */
    FreshSpecialReportResponse queryFreshSpecialReport(FreshSpecialReportRequest param)throws Exception;

    /**
     * 查询区域生鲜专题报表趋势
     * @param param
     * @return
     * @throws Exception
     */
    FreshSpecialReportTrendListResponse queryAreaFreshSpecialReportTrend(FreshSpecialReportRequest param)throws Exception;

    /**
     * 查询门店生鲜专题报表趋势
     * @param param
     * @return
     * @throws Exception
     */
    FreshSpecialReportTrendListResponse queryStoreFreshSpecialReportTrend(FreshSpecialReportRequest param)throws Exception;

    /**
     * 查询商品生鲜专题报表趋势
     * @param param
     * @return
     * @throws Exception
     */
    FreshSpecialReportTrendListResponse queryShopFreshSpecialReportTrend(FreshSpecialReportRequest param)throws Exception;

    /**
     * 查询区域生鲜专题排行榜
     * @param param
     * @return
     * @throws Exception
     */
    FreshSpecialReportRankListResponse queryAreaFreshSpecialReportRank(FreshSpecialReportRequest param)throws Exception;

    /**
     * 查询门店生鲜专题排行榜
     * @param param
     * @return
     * @throws Exception
     */
    FreshSpecialReportRankListResponse queryStoreFreshSpecialReportRank(FreshSpecialReportRequest param)throws Exception;

    /**
     * 查询商品生鲜专题排行榜
     * @param param
     * @return
     * @throws Exception
     */
    FreshSpecialReportRankListResponse queryShopFreshSpecialReportRank(FreshSpecialReportRequest param)throws Exception;
}
