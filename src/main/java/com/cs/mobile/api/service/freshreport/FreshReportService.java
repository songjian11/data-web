package com.cs.mobile.api.service.freshreport;

import com.cs.mobile.api.model.freshreport.request.FreshRankRequest;
import com.cs.mobile.api.model.freshreport.request.FreshReportBaseRequest;
import com.cs.mobile.api.model.freshreport.response.*;
import com.cs.mobile.api.model.reportPage.request.PageRequest;
import com.cs.mobile.api.model.reportPage.response.MemberPermeabilityResponse;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.api.model.salereport.response.AchievingRateResponse;

import java.util.List;

public interface FreshReportService {
    /**
     * 查询生鲜的总销售额
     * @param param
     * @return
     * @throws Exception
     */
    MonthStatisticsResponse queryMonthStatisticsResponse(FreshReportBaseRequest param) throws Exception;

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
    MemberListResponse queryMemberDetail(PageRequest param) throws Exception;

    /**
     * 查询销售列表
     * @param param
     * @return
     * @throws Exception
     */
    SalesListResponse querySalesList(FreshReportBaseRequest param)throws Exception;

    /**
     * 查询排行榜
     * @param param
     * @return
     */
    FreshRankResponse queryFreshRank(FreshRankRequest param)throws Exception;

    /**
     * 销售客流总列表
     * @param param
     * @return
     * @throws Exception
     */
    FreshklStaticsListResponse queryFreshklStaticsList(FreshReportBaseRequest param)throws Exception;

    /**
     * 查询海波龙标准周准天数
     * @param param
     * @return
     * @throws Exception
     */
    HblTurnoverDayListResponse queryHblTurnoverDay(FreshReportBaseRequest param)throws Exception;

    /**
     * 查询生鲜客流列表
     * @param param
     * @return
     * @throws Exception
     */
    FreshDeptKlListResponse queryFreshDeptKlList(FreshReportBaseRequest param)throws Exception;

    /**
     * 查询排名名次
     * @param param
     * @param type(1-全司，2-省级，3-区域级，4-门店级)
     * @return
     * @throws Exception
     */
    FreshRankGradeResponse queryFreshRankGrade(FreshReportBaseRequest param,int type)throws Exception;

    /**
     * 查询实际汇总周转天数
     * @param param
     * @return
     * @throws Exception
     */
    TurnoverDayResponse queryActualTurnoverDay(PageRequest param)throws Exception;

    /**
     * 查询毛利额达成率
     * @param param
     * @return
     * @throws Exception
     */
    AchievingRateResponse queryRateAchievingRate(FreshReportBaseRequest param) throws Exception;
}
