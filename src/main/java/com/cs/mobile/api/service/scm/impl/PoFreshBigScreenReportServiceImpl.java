package com.cs.mobile.api.service.scm.impl;

import com.cs.mobile.api.dao.scm.PoFreshBigScreenReportDao;
import com.cs.mobile.api.model.scm.*;
import com.cs.mobile.api.model.scm.response.ShipperBonusListResponse;
import com.cs.mobile.api.model.scm.response.ShipperBonusResponse;
import com.cs.mobile.api.model.scm.response.ShopPriceListResponse;
import com.cs.mobile.api.model.scm.response.ShopPriceTrendResponse;
import com.cs.mobile.api.service.scm.PoFreshBigScreenReportService;
import com.cs.mobile.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 生鲜数据大屏报表
 */
@Service
public class PoFreshBigScreenReportServiceImpl implements PoFreshBigScreenReportService {
    @Autowired
    PoFreshBigScreenReportDao freshBigScreenReportDao;

    /**
     * 商品价格top5排名
     * @return
     */
    @Override
    public ShopPriceListResponse queryShopPriceTopFiveList() {
        //商品价格top5趋势返回结果
        ShopPriceListResponse response = new ShopPriceListResponse();
        //商品价格top5列表
        List<ShopPriceTrendResponse> shopPriceTrendResponseList = new ArrayList<>();
        //按照商品名称分组map
        Map<String, List<ShopPriceTrendResponse>> map = new HashMap<>();
        //商品编号
        List<String> itemCodes = new ArrayList<>();
        //暂时是固定查询36，37大类的单品
        List<String> depts = new ArrayList<>();
        depts.add("36");
        depts.add("37");
        //查询商品价格top5的数据信息
        List<ShopInfo> list = freshBigScreenReportDao.queryShopPriceTopFiveInfo(depts, new Date());
        //获取商品编号
        if(null != list && list.size() > 0){
            for(ShopInfo shopInfo : list){
                itemCodes.add(shopInfo.getItem());
            }
        }else{//没有商品信息则直接返回空
            return response;
        }
        //查询商品价格趋势信息根据商品编号
        List<ShopPriceTrend> trends = freshBigScreenReportDao.queryShopPriceTrendInfo(itemCodes, new Date());
        //汇总最终结果
        if(null != trends && trends.size() > 0){
            for(ShopPriceTrend shopPriceTrend : trends){//将po转换bo
                ShopPriceTrendResponse shopPriceTrendResponse = new ShopPriceTrendResponse();
                BeanUtils.copyProperties(shopPriceTrend, shopPriceTrendResponse);
                shopPriceTrendResponseList.add(shopPriceTrendResponse);
            }

            if (shopPriceTrendResponseList.size() > 0){
                //按照商品名称分组
                map = shopPriceTrendResponseList.stream().collect(Collectors.groupingBy(ShopPriceTrendResponse::getItemName));
                //补全没有数据的月份
                map = CompleteMonthForShopPriceTopFive(map);
            }
        }
        response.setDataMap(map);
        return response;
    }

    /**
     * 查询发货人奖金排名数据
     * @return
     */
    @Override
    public ShipperBonusListResponse queryShipperBonus() {
        ShipperBonusListResponse shipperBonusListResponse = new ShipperBonusListResponse();
        List<ShipperBonusResponse> shipperBonusResponseList = new ArrayList<>();
        List<String> shipperCodes = new ArrayList<>();
        //查询top10的发货人,从高到底的顺序排列
        List<Shipper> shipperInfos = freshBigScreenReportDao.queryShipperTop10();
        if(null != shipperInfos && shipperInfos.size() > 0){//获取发货人编号
            for(Shipper shipper : shipperInfos){
                shipperCodes.add(shipper.getShipperCode());
            }
        }else{//发货人不存在直接返回为空
            return shipperBonusListResponse;
        }
        //查询发货人的车型和对应发货次数
        List<ShipperBonus> shipperBonusInfos = freshBigScreenReportDao.queryShipperBonus(shipperCodes);
        //查询车型和奖金的对应关系
        List<Bonus> bonuses = freshBigScreenReportDao.queryBonus();
        //合计返回结果
        if(null != shipperBonusInfos && shipperBonusInfos.size() > 0){
            //按照发货人编号分组发货人发货信息
            Map<String, List<ShipperBonus>> shipperMap = shipperBonusInfos.stream().collect(Collectors.groupingBy(ShipperBonus::getShipperCode));
            //按照车型分组奖金信息
            Map<String, Bonus> bonusMap = bonuses.stream().collect(Collectors.toMap(Bonus::getCarType, Function.identity(), (key1, key2) -> key2));
            for(Shipper shipper : shipperInfos){//按照从高到底的顺序遍历，合计返回结果
                ShipperBonusResponse shipperBonusResponse = new ShipperBonusResponse();
                List<ShipperBonus> list = shipperMap.get(shipper.getShipperCode());
                shipperBonusResponse.setShipperName(shipper.getShipperName());
                shipperBonusResponse.setShipperCode(shipper.getShipperCode());
                //合计奖金
                Double money = list.stream().mapToDouble(e -> {
                    if(bonusMap.containsKey(e.getCarType())){
                        Bonus bonusInfo = bonusMap.get(e.getCarType());
                        //车型对应奖金
                        BigDecimal bonusValue = new BigDecimal(StringUtils.isEmpty(bonusInfo.getBonus())?"0":bonusInfo.getBonus());
                        //发货次数
                        BigDecimal carNum = new BigDecimal(StringUtils.isEmpty(e.getCarNum())?"0":e.getCarNum());
                        return bonusValue.multiply(carNum).doubleValue();
                    }
                    return 0.0;
                }).reduce(Double::sum).getAsDouble();
                shipperBonusResponse.setMoney(String.valueOf(money));
                shipperBonusResponseList.add(shipperBonusResponse);
            }
        }
        shipperBonusListResponse.setList(shipperBonusResponseList);
        return shipperBonusListResponse;
    }

    /**
     * 商品价格top5补全月份
     */
    private Map<String, List<ShopPriceTrendResponse>> CompleteMonthForShopPriceTopFive(Map<String, List<ShopPriceTrendResponse>> map){
        Map<String, List<ShopPriceTrendResponse>> result = new HashMap<>();
        if(null != map && map.size() > 0){
           for(Map.Entry<String,List<ShopPriceTrendResponse>> entry : map.entrySet()){
               //每个商品的月数据
               List<ShopPriceTrendResponse> monthList = new ArrayList<>();
               List<ShopPriceTrendResponse> list = entry.getValue();
               //按照月份分组
               Map<String, ShopPriceTrendResponse> monthMap = list.stream()
                       .collect(Collectors.toMap(ShopPriceTrendResponse::getMonth, Function.identity(), (key1, key2) -> key2));
               //补全没有的月份信息
               for(int i=1; i<=12; i++){
                   String value = String.valueOf(i);
                   if(i < 10){
                       value = "0" + i;
                   }
                   if(!monthMap.containsKey(value)){
                       ShopPriceTrendResponse shopPriceTrendResponse = new ShopPriceTrendResponse();
                       shopPriceTrendResponse.setItem(list.get(0).getItem());
                       shopPriceTrendResponse.setItemName(list.get(0).getItemName());
                       shopPriceTrendResponse.setMoney("0");
                       shopPriceTrendResponse.setMonth(value);
                       monthList.add(shopPriceTrendResponse);
                   }
               }

               monthList.addAll(list);
               //按照月份排序
               monthList = monthList.stream().sorted(new Comparator<ShopPriceTrendResponse>() {
                   @Override
                   public int compare(ShopPriceTrendResponse o1, ShopPriceTrendResponse o2) {
                       BigDecimal month1 = new BigDecimal(o1.getMonth());
                       BigDecimal month2 = new BigDecimal(o2.getMonth());
                       return month1.compareTo(month2);
                   }
               }).collect(Collectors.toList());

               result.put(entry.getKey(), monthList);
           }
        }
        return result;
    }
}
