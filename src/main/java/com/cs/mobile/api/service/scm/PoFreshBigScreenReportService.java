package com.cs.mobile.api.service.scm;

import com.cs.mobile.api.model.scm.response.ShipperBonusListResponse;
import com.cs.mobile.api.model.scm.response.ShopPriceListResponse;

/**
 * 生鲜数据大屏报表
 */
public interface PoFreshBigScreenReportService {
    /**
     * 商品价格top5排名
     * @return
     */
    ShopPriceListResponse queryShopPriceTopFiveList();

    /**
     * 查询发货人奖金排名数据
     * @return
     */
    ShipperBonusListResponse queryShipperBonus();
}
