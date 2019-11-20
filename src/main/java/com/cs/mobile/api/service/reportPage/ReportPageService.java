package com.cs.mobile.api.service.reportPage;


import java.util.List;

import com.cs.mobile.api.model.freshreport.response.MemberListResponse;
import com.cs.mobile.api.model.freshreport.response.TurnoverDayResponse;
import com.cs.mobile.api.model.reportPage.CsmbStoreModel;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
import com.cs.mobile.api.model.reportPage.request.RankParamRequest;
import com.cs.mobile.api.model.reportPage.request.ReportParamRequest;
import com.cs.mobile.api.model.reportPage.response.FreshResponse;
import com.cs.mobile.api.model.reportPage.response.HomeApplianceResponse;
import com.cs.mobile.api.model.reportPage.response.MemberPermeabilityResponse;
import com.cs.mobile.api.model.reportPage.response.NotFreshResponse;
import com.cs.mobile.api.model.reportPage.response.RankInfoResponse;
import com.cs.mobile.api.model.reportPage.response.ReportDataResponse;
import com.cs.mobile.api.model.reportPage.response.TotalSalesAndProfitResponse;

public interface ReportPageService {
    TotalSalesAndProfitResponse queryTotalSalesAndProfit(PageRequest pageRequest)throws Exception;

    /**
     * 查询家电模块
     * @param pageRequest
     * @return
     */
    HomeApplianceResponse queryHomeAppliance(PageRequest pageRequest)throws Exception;

    /**
     * 查询生鲜模块
     * @param pageRequest
     * @return
     */
    FreshResponse queryFresh(PageRequest pageRequest)throws Exception;

    /**
     * 查询非生鲜模块
     * @param pageRequest
     * @return
     */
    NotFreshResponse queryNotFresh(PageRequest pageRequest)throws Exception;

    /**
     * 查询时线，日线，月线模块
     * @param param
     * @return
     */
    ReportDataResponse queryReportData(ReportParamRequest param)throws Exception;

    /**
     * 查询同期时线，日线，月线模块
     * @param param
     * @return
     */
    ReportDataResponse querySameReportData(ReportParamRequest param)throws Exception;
    /**
     * 查询排行榜模块
     * @param pageRequest
     * @return
     */
    RankInfoResponse queryRankInfo(RankParamRequest pageRequest)throws Exception;

    /**
     * 查询排行榜列表
     * @param rankParamRequest
     * @return
     */
    RankInfoResponse queryRankList(RankParamRequest rankParamRequest)throws Exception;


    /**
     * 查询会员渗透率
     * @param param
     * @return
     */
    MemberPermeabilityResponse queryMember(PageRequest param)throws Exception;

    /**
     * 查询会员渗透率明细
     * @param param
     * @return
     * @throws Exception
     */
    MemberListResponse queryMemberDetail(PageRequest param)throws Exception;

    /**
     * 查询实际汇总周转天数
     * @param param
     * @return
     * @throws Exception
     */
    TurnoverDayResponse queryActualTurnoverDay(PageRequest param)throws Exception;
    
    /**
     * 根据组织架构id查组织名称
     * @param param
     * @return
     * @throws Exception
     */
    public List<CsmbStoreModel> getOrganization()throws Exception;
    
}
