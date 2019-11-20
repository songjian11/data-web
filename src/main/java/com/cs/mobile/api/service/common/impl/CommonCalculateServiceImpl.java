package com.cs.mobile.api.service.common.impl;

import com.cs.mobile.api.service.common.CommonCalculateService;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
/**
 * 公共计算方法
 */
@Slf4j
@Service
public class CommonCalculateServiceImpl implements CommonCalculateService {
    //保留小数点位数
    private static final int DECIMALPOINT = 4;

    /**
     * 计算毛利率
     * @param sale(销售金额)
     * @param rate(毛利金额)
     * @return
     */
    @Override
    public String calculateProfit(String sale, String rate){
        String profit = "";
        BigDecimal saleBg = new BigDecimal(StringUtils.isEmpty(sale)?"0":sale);
        BigDecimal rateBg = new BigDecimal(StringUtils.isEmpty(rate)?"0":rate);
        if(saleBg.compareTo(BigDecimal.ZERO) != 0){
            profit = rateBg.divide(saleBg, DECIMALPOINT, BigDecimal.ROUND_HALF_UP).toString();
        }
        return profit;
    }

    /**
     * 计算销售可比率
     * @param compareSale(当期可比销售金额)
     * @param sameCompareSale(同期可比销售金额)
     * @return
     */
    @Override
    public String calculateCompareSaleRate(String compareSale, String sameCompareSale){
        String saleCompareRate = "";
        BigDecimal sameCompareSaleBg = new BigDecimal(StringUtils.isEmpty(sameCompareSale)?"0":sameCompareSale);
        BigDecimal compareSaleBg = new BigDecimal(StringUtils.isEmpty(compareSale)?"0":compareSale);
        if(sameCompareSaleBg.compareTo(BigDecimal.ZERO) != 0){
            //(当期可比销售金额-同期可比销售金额)/同期可比销售金额
            saleCompareRate = compareSaleBg.subtract(sameCompareSaleBg).divide(sameCompareSaleBg, DECIMALPOINT, BigDecimal.ROUND_HALF_UP).toString();
        }
        return saleCompareRate;
    }

    /**
     * 计算毛利率增长率
     * @param profit(当期毛利率)
     * @param sameProfit(同期毛利率)
     * @return
     */
    @Override
    public String calculateAddProfitRate(String profit, String sameProfit) {
        BigDecimal profitBg = new BigDecimal(StringUtils.isEmpty(profit)?"0":profit);
        BigDecimal sameProfitBg = new BigDecimal(StringUtils.isEmpty(sameProfit)?"0":sameProfit);
        return profitBg.subtract(sameProfitBg).toString();
    }

    /**
     * 计算销售金额环比率
     * @param sale(当期销售金额)
     * @param monthSale(环比销售金额)
     * @return
     */
    @Override
    public String calculateMonthSaleRate(String sale, String monthSale){
        String monthSaleRate = "";
        BigDecimal saleBg = new BigDecimal(StringUtils.isEmpty(sale)?"0":sale);
        BigDecimal monthSaleBg = new BigDecimal(StringUtils.isEmpty(monthSale)?"0":monthSale);
        if(monthSaleBg.compareTo(BigDecimal.ZERO) != 0){
            //(当期销售金额-环比销售金额)/环比销售金额
            monthSaleRate = saleBg.subtract(monthSaleBg).divide(monthSaleBg, DECIMALPOINT, BigDecimal.ROUND_HALF_UP).toString();
        }
        return monthSaleRate;
    }

    /**
     * 计算毛利率可比率
     * @param compareProfit(当期可比毛利率)
     * @param sameCompareProfit(同期可比毛利率)
     * @return
     */
    @Override
    public String calculateCompareProfitRate(String compareProfit, String sameCompareProfit){
        String compareProfitcRate = "";
        BigDecimal sameCompareProfitBg = new BigDecimal(StringUtils.isEmpty(sameCompareProfit)?"0":sameCompareProfit);
        BigDecimal compareProfitBg = new BigDecimal(StringUtils.isEmpty(compareProfit)?"0":compareProfit);
        if(sameCompareProfitBg.compareTo(BigDecimal.ZERO) != 0){
            //(当期可比毛利率-同期可比毛利率)/同期可比毛利率
            compareProfitcRate = compareProfitBg.subtract(sameCompareProfitBg).divide(sameCompareProfitBg, DECIMALPOINT, BigDecimal.ROUND_HALF_UP).toString();
        }
        return compareProfitcRate;
    }

    @Override
    public String calculateProfitRate(String profit, String sameProfit) {
        String profitcRate = "";
        BigDecimal sameProfitBg = new BigDecimal(StringUtils.isEmpty(sameProfit)?"0":sameProfit);
        BigDecimal profitBg = new BigDecimal(StringUtils.isEmpty(profit)?"0":profit);
        if(sameProfitBg.compareTo(BigDecimal.ZERO) != 0){
            //(当期毛利率-同期毛利率)/同期毛利率
            profitcRate = profitBg.subtract(sameProfitBg).divide(sameProfitBg, DECIMALPOINT, BigDecimal.ROUND_HALF_UP).toString();
        }
        return profitcRate;
    }

    /**
     * 计算毛利率环比率
     * @param profit(当期毛利率)
     * @param monthProfit(环比毛利率)
     * @return
     */
    @Override
    public String calculateMonthProfitRate(String profit, String monthProfit){
        String monthProfitRate = "";
        BigDecimal profitBg = new BigDecimal(StringUtils.isEmpty(profit)?"0":profit);
        BigDecimal monthProfitBg = new BigDecimal(StringUtils.isEmpty(monthProfit)?"0":monthProfit);
        if(monthProfitBg.compareTo(BigDecimal.ZERO) != 0){
            //(当期毛利率-环比毛利率)/环比毛利率
            monthProfitRate = profitBg.subtract(monthProfitBg).divide(monthProfitBg, DECIMALPOINT, BigDecimal.ROUND_HALF_UP).toString();
        }
        return monthProfitRate;
    }

    /**
     * 计算毛利额可比率
     * @param compareRate(当期可比毛利额)
     * @param sameCompareRate(同期可比毛利额)
     * @return
     */
    @Override
    public String calculateCompareRate(String compareRate, String sameCompareRate){
        String compareValue = "";
        BigDecimal sameCompareRateBg = new BigDecimal(StringUtils.isEmpty(sameCompareRate)?"0":sameCompareRate);
        BigDecimal compareRateBg = new BigDecimal(StringUtils.isEmpty(compareRate)?"0":compareRate);
        if(sameCompareRateBg.compareTo(BigDecimal.ZERO) != 0){
            //(当期可比毛利额-同期可比毛利额)/同期可比毛利额
            compareValue = compareRateBg.subtract(sameCompareRateBg).divide(sameCompareRateBg, DECIMALPOINT, BigDecimal.ROUND_HALF_UP).toString();
        }
        return compareValue;
    }

    /**
     * 计算毛利额环比率
     * @param rate(当期毛利额)
     * @param monthRate(环比毛利额)
     * @return
     */
    @Override
    public String calculateMonthRate(String rate, String monthRate){
        String monthValue = "";
        BigDecimal rateBg = new BigDecimal(StringUtils.isEmpty(rate)?"0":rate);
        BigDecimal monthRateBg = new BigDecimal(StringUtils.isEmpty(monthRate)?"0":monthRate);
        if(monthRateBg.compareTo(BigDecimal.ZERO) != 0){
            //(当期毛利额-环比毛利额)/环比毛利额
            monthValue = rateBg.subtract(monthRateBg).divide(monthRateBg, DECIMALPOINT, BigDecimal.ROUND_HALF_UP).toString();
        }
        return monthValue;
    }

    /**
     * 计算渗透率
     * @param kl(客流)
     * @param allKl(总客流)
     * @return
     */
    @Override
    public String calculatePermeability(String kl, String allKl){
        String klValue = "";
        BigDecimal klBg = new BigDecimal(StringUtils.isEmpty(kl)?"0":kl);
        BigDecimal allKlBg = new BigDecimal(StringUtils.isEmpty(allKl)?"0":allKl);
        if(allKlBg.compareTo(BigDecimal.ZERO) != 0){
            //客流/总客流
            klValue = klBg.divide(allKlBg, DECIMALPOINT, BigDecimal.ROUND_HALF_UP).toString();
        }
        return klValue;
    }

    /**
     * 多参数相加
     * @param array
     * @return
     */
    @Override
    public String adds(String... array) {
        BigDecimal count = BigDecimal.ZERO;
        for(int i= 0; i<array.length; i++){
            BigDecimal value = new BigDecimal(StringUtils.isEmpty(array[i])?"0" : array[i]);
            count = count.add(value);
        }
        return count.toString();
    }

    /**
     * 计算销售达成率
     * @param sale(销售)
     * @param saleGoal(目标销售)
     * @return
     */
    @Override
    public String calculateSaleAchiRate(String sale, String saleGoal) {
        String saleRate = "";
        BigDecimal curSale = new BigDecimal(StringUtils.isEmpty(sale)?"0":sale);
        BigDecimal goal = new BigDecimal(StringUtils.isEmpty(saleGoal)?"0":saleGoal);
        if(goal.compareTo(BigDecimal.ZERO) != 0){
            saleRate = curSale.divide(goal,DECIMALPOINT,BigDecimal.ROUND_HALF_UP).toString();
        }
        return saleRate;
    }

    /**
     * 计算销售增长率
     * @param sale(销售)
     * @param hisSale(往期销售)
     * @return
     */
    @Override
    public String calculateSaleRate(String sale, String hisSale) {
        String saleRate = "";
        BigDecimal saleD = new BigDecimal(StringUtils.isEmpty(sale)?"0":sale);
        BigDecimal hisSaleD = new BigDecimal(StringUtils.isEmpty(hisSale)?"0":hisSale);
        if(hisSaleD.compareTo(BigDecimal.ZERO) != 0){
            saleRate = saleD.subtract(hisSaleD).divide(hisSaleD,DECIMALPOINT,BigDecimal.ROUND_HALF_UP).toString();
        }
        return saleRate;
    }

    /**
     * 计算毛利额增长率
     * @param rate(毛利额)
     * @param hisRate(往期毛利额)
     * @return
     */
    @Override
    public String calculateProfitAddRate(String rate, String hisRate) {
        String profitRate = "";
        BigDecimal rateD = new BigDecimal(StringUtils.isEmpty(rate)?"0":rate);
        BigDecimal hisRateD = new BigDecimal(StringUtils.isEmpty(hisRate)?"0":hisRate);
        if(hisRateD.compareTo(BigDecimal.ZERO) != 0){
            profitRate = rateD.subtract(hisRateD).divide(hisRateD,DECIMALPOINT,BigDecimal.ROUND_HALF_UP).toString();
        }
        return profitRate;
    }

    /**
     * 计算每日销售目标值
     * @param goalSale(目标销售)
     * @param dayNum(天数)
     * @return
     */
    @Override
    public String calculateGoalSale(String goalSale, int dayNum) {
        String val = "";
        BigDecimal goalD = new BigDecimal(StringUtils.isEmpty(goalSale)?"0":goalSale);
        BigDecimal dayNumD = new BigDecimal(dayNum);
        if(dayNumD.compareTo(BigDecimal.ZERO) != 0){
            val = goalD.divide(dayNumD,DECIMALPOINT,BigDecimal.ROUND_HALF_UP).toString();
        }
        return val;
    }

    /**
     * 获取被除数
     * @param divisor(除数)
     * @param discuss(商)
     * @param flag(discuss是否需要除以100)
     * @return
     */
    @Override
    public String dividend(String divisor, String discuss, boolean flag) {
        String value = "";
        if(StringUtils.isNotEmpty(discuss) && discuss.endsWith("%")){
            discuss = discuss.replace("%","");
        }
        BigDecimal discussBg = null;
        if(flag){
            discussBg = new BigDecimal(StringUtils.isEmpty(discuss)?"0" : discuss).divide(new BigDecimal("100"),DECIMALPOINT,BigDecimal.ROUND_HALF_UP);
        }else{
            discussBg = new BigDecimal(StringUtils.isEmpty(discuss)?"0" : discuss);
        }
        BigDecimal divisorBg = new BigDecimal(StringUtils.isEmpty(divisor)?"0" : divisor);
        value = discussBg.multiply(divisorBg).toString();
        return value;
    }
}
