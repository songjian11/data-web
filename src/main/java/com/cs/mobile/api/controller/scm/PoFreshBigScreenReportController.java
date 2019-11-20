//package com.cs.mobile.api.controller.scm;
//
//import com.cs.mobile.api.common.DataHandler;
//import com.cs.mobile.api.common.DataResult;
//import com.cs.mobile.api.controller.common.AbstractApiController;
//import com.cs.mobile.api.model.scm.response.ShipperBonusListResponse;
//import com.cs.mobile.api.model.scm.response.ShopPriceListResponse;
//import com.cs.mobile.api.model.user.UserInfo;
//import com.cs.mobile.api.service.scm.impl.PoFreshBigScreenReportServiceImpl;
//import com.cs.mobile.api.service.user.UserService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//@Slf4j
//@Api(value = "freshBigScreenReport", tags = {"基地回货生鲜数据大屏报表"})
//@RestController
//@RequestMapping("/api/pofresh")
//public class PoFreshBigScreenReportController extends AbstractApiController {
//    @Autowired
//    PoFreshBigScreenReportServiceImpl freshBigScreenReportService;
//    @Autowired
//    UserService userService;
//
//    @ApiOperation(value = "查询商品价格top5趋势列表", notes = "查询商品价格top5趋势列表")
//    @GetMapping("/queryShopPriceTopFiveList")
//    public DataResult<ShopPriceListResponse> queryShopPriceTopFiveList(HttpServletRequest request,
//                                                                      HttpServletResponse response) {
//        ShopPriceListResponse result = null;
//        // 记录访问日志
//        try {
//            UserInfo userInfo = this.getCurUserInfo(request);
//            userService.addPersonLog(userInfo.getPersonId(), "查询商品价格top5趋势列表");
//        } catch (Exception e) {
//            log.error("访问日志保存出错", e);
//        }
//        try {
//            result = freshBigScreenReportService.queryShopPriceTopFiveList();
//        } catch (Exception e) {
//            return super.handleException(request, response, e);
//        }
//        return DataHandler.jsonResult(result);
//    }
//
//    @ApiOperation(value = "采购奖金池top10", notes = "采购奖金池top10")
//    @GetMapping("/queryShipperBonus")
//    public DataResult<ShipperBonusListResponse> queryShipperBonus(HttpServletRequest request,
//                                                                        HttpServletResponse response) {
//        ShipperBonusListResponse result = null;
//        // 记录访问日志
//        try {
//            UserInfo userInfo = this.getCurUserInfo(request);
//            userService.addPersonLog(userInfo.getPersonId(), "采购奖金池top10");
//        } catch (Exception e) {
//            log.error("访问日志保存出错", e);
//        }
//        try {
//            result = freshBigScreenReportService.queryShipperBonus();
//        } catch (Exception e) {
//            return super.handleException(request, response, e);
//        }
//        return DataHandler.jsonResult(result);
//    }
//}
