package com.cs.mobile.api.service.common;

/**
 * 公共计算方法
 */
public interface CommonCalculateService {
    /**
     * 计算毛利率
     * @param sale(销售金额)
     * @param rate(毛利金额)
     * @return
     */
    String calculateProfit(String sale, String rate);

    /**
     * 计算销售可比率
     * @param compareSale(当期可比销售金额)
     * @param sameCompareSale(同期可比销售金额)
     * @return
     */
    String calculateCompareSaleRate(String compareSale, String sameCompareSale);

    /**
     * 计算毛利率增长率
     * @param profit(当期毛利率)
     * @param sameProfit(同期毛利率)
     * @return
     */
    String calculateAddProfitRate(String profit, String sameProfit);

    /**
     * 计算销售金额环比率
     * @param sale(当期销售金额)
     * @param monthSale(环比销售金额)
     * @return
     */
    String calculateMonthSaleRate(String sale, String monthSale);

    /**
     * 计算毛利率可比率
     * @param compareProfit(当期可比毛利率)
     * @param sameCompareProfit(同期可比毛利率)
     * @return
     */
    String calculateCompareProfitRate(String compareProfit, String sameCompareProfit);

    /**
     * 计算毛利率同比率
     * @param profit(当期毛利率)
     * @param sameProfit(同期毛利率)
     * @returnp
     */
    String calculateProfitRate(String profit, String sameProfit);

    /**
     * 计算毛利率环比率
     * @param profit(当期毛利率)
     * @param monthProfit(环比毛利率)
     * @return
     */
    String calculateMonthProfitRate(String profit, String monthProfit);

    /**
     * 计算毛利额可比率
     * @param compareRate(当期可比毛利额)
     * @param sameCompareRate(同期可比毛利额)
     * @return
     */
    String calculateCompareRate(String compareRate, String sameCompareRate);

    /**
     * 计算毛利额环比率
     * @param rate(当期毛利额)
     * @param monthRate(环比毛利额)
     * @return
     */
    String calculateMonthRate(String rate, String monthRate);

    /**
     * 计算渗透率
     * @param kl(客流)
     * @param allKl(总客流)
     * @return
     */
    String calculatePermeability(String kl, String allKl);

    /**
     * 多参数相加
     * @param array
     * @return
     */
    String adds(String...array);

    /**
     * 计算销售达成率
     * @param sale(销售)
     * @param saleGoal(目标销售)
     * @return
     */
    String calculateSaleAchiRate(String sale, String saleGoal);

    /**
     * 计算销售增长率
     * @param sale(销售)
     * @param hisSale(往期销售)
     * @return
     */
    String calculateSaleRate(String sale, String hisSale);

    /**
     * 计算毛利额增长率
     * @param rate(毛利额)
     * @param hisRate(往期毛利额)
     * @return
     */
    String calculateProfitAddRate(String rate, String hisRate);

    /**
     * 计算销售目标值
     * @param goalSale(目标销售)
     * @param dayNum(天数)
     * @return
     */
    String calculateGoalSale(String goalSale, int dayNum);

    /**
     * 获取被除数
     * @param divisor(除数)
     * @param discuss(商)
     * @param flag(discuss是否需要除以100)
     * @return
     */
    String dividend(String divisor, String discuss, boolean flag);
}
