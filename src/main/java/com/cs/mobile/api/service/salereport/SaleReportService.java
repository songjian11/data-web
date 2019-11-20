package com.cs.mobile.api.service.salereport;

import com.cs.mobile.api.model.reportPage.response.HomeApplianceResponse;
import com.cs.mobile.api.model.salereport.request.BaseSaleRequest;
import com.cs.mobile.api.model.salereport.response.*;

public interface SaleReportService {

    /**
     * 查询销售模块
     * @param param
     * @return
     * @throws Exception
     */
    public AllSaleSumResponse queryAllSaleSum(BaseSaleRequest param)throws Exception;

    /**
     * 查询毛利额模块
     * @param param
     * @return
     * @throws Exception
     */
    public AllRateSumResponse queryAllRateSum(BaseSaleRequest param)throws Exception;

    /**
     * 查询销售达成率
     * @param param
     * @return
     * @throws Exception
     */
    public AchievingRateResponse querySaleAchievingRate(BaseSaleRequest param)throws Exception;

    /**
     * 查询毛利额达成率
     * @param param
     * @return
     * @throws Exception
     */
    public AchievingRateResponse queryRateAchievingRate(BaseSaleRequest param)throws Exception;

    /**
     * 查询本年和本月销售额
     * @param param
     * @return
     * @throws Exception
     */
    public SaleMonthAndYearResponse querySaleMonthAndYear(BaseSaleRequest param)throws Exception;

    /**
     * 查询本年和本月毛利额
     * @param param
     * @return
     * @throws Exception
     */
    public RateMonthAndYearResponse queryRateMonthAndYear(BaseSaleRequest param)throws Exception;

    /**
     * 查询家电模块
     * @param param
     * @return
     */
    HomeApplianceResponse queryHomeAppliance(BaseSaleRequest param)throws Exception;

    /**
     * 查询渠道构成
     * @param param
     * @return
     * @throws Exception
     */
    ChannlResponse queryChannlResponse(BaseSaleRequest param)throws Exception;

    /**
     * 查询渠道构成明细
     * @param param
     * @return
     * @throws Exception
     */
    ChannlDetailListResponse queryChannlDetailResponse(BaseSaleRequest param) throws Exception;

    /**
     * 查询销售趋势
     * @param param
     * @return
     * @throws Exception
     */
    SaleTrendListResponse querySaleTrendResponse(BaseSaleRequest param)throws Exception;

    /**
     * 查询销售构成
     * @param param
     * @return
     * @throws Exception
     */
    SaleCompositionListResponse querySaleComposition(BaseSaleRequest param)throws Exception;

    /**
     * 查询单品销售排名
     * @param param
     * @return
     * @throws Exception
     */
    ItemSaleRankListResponse queryItemSaleRankList(BaseSaleRequest param)throws Exception;

    /**
     * 查询单品毛利额排名
     * @param param
     * @return
     * @throws Exception
     */
    ItemRateRankListResponse queryItemRateRankList(BaseSaleRequest param)throws Exception;
}
