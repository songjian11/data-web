package com.cs.mobile.api.controller.scm;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.scm.PoAsnHead;
import com.cs.mobile.api.model.scm.PoDetail;
import com.cs.mobile.api.model.scm.PoHead;
import com.cs.mobile.api.model.scm.PoWareHouse;
import com.cs.mobile.api.model.scm.request.PoAsnReq;
import com.cs.mobile.api.model.scm.request.PoDetailReq;
import com.cs.mobile.api.model.scm.request.PoHeadReq;
import com.cs.mobile.api.model.scm.response.ItemResp;
import com.cs.mobile.api.model.scm.response.OrderListResp;
import com.cs.mobile.api.model.scm.response.PoAsnDetailResp;
import com.cs.mobile.api.model.scm.response.PoPrepareItemResp;
import com.cs.mobile.api.model.scm.response.SupplierResp;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.scm.PoManagerService;
import com.cs.mobile.api.service.user.UserService;
import com.cs.mobile.common.exception.api.ExceptionUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"unchecked", "rawtypes"})
@Api(value = "PoManger", tags = {"基地回货管理接口"})
@RestController
@RequestMapping("/api/po")
@Slf4j

public class PoMangerController extends AbstractApiController {
    @Autowired
    private PoManagerService poManagerService;
    @Autowired
    UserService userService;

    @ApiOperation(value = "分页查询订单列表", notes = "分页查询订单列表")
    @GetMapping("/getOrderList")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "status", value = "状态", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "beginDate", value = "开始日期", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "endDate", value = "结束日期", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "supplier", value = "基地编码", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "poSn", value = "订单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "page", value = "当前页码", required = true, dataType = "int"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页条数", required = true, dataType =
                    "int")})
    public DataResult<List<OrderListResp>> getOrderList(HttpServletRequest request, HttpServletResponse response,
                                                        String status, String beginDate, String endDate,
                                                        String supplier, String poSn, int page, int pageSize,
                                                        String personId) {
        PageResult<OrderListResp> pageResult = null;
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "基地回货列表页");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            pageResult = poManagerService.getOrderList(userInfo, status, beginDate, endDate, poSn, supplier, page,
                    pageSize);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(pageResult);
    }

    @ApiOperation(value = "分页查询审核订单列表", notes = "分页查询审核订单列表")
    @GetMapping("/getAuditOrderList")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "status", value = "状态", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "beginDate", value = "开始日期", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "endDate", value = "结束日期", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "supplier", value = "基地编码", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "poSn", value = "订单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "page", value = "当前页码", required = true, dataType = "int"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页条数", required = true, dataType =
                    "int")})
    public DataResult<List<OrderListResp>> getAuditOrderList(HttpServletRequest request, HttpServletResponse response,
                                                             String status, String beginDate, String endDate,
                                                             String supplier, String poSn, int page, int pageSize,
                                                             String personId) {
        PageResult<OrderListResp> pageResult = null;
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "基地回货审核列表页");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            pageResult = poManagerService.getAuditOrderList(userInfo, status, beginDate, endDate, poSn, supplier, page,
                    pageSize);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(pageResult);
    }

    @ApiOperation(value = "分页查询发货订单列表", notes = "分页查询发货订单列表")
    @GetMapping("/getMyOrderList")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "status", value = "状态", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "beginDate", value = "开始日期", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "endDate", value = "结束日期", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "supplier", value = "基地编码", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "poSn", value = "订单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "page", value = "当前页码", required = true, dataType = "int"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页条数", required = true, dataType =
                    "int")})
    public DataResult<List<OrderListResp>> getMyOrderList(HttpServletRequest request, HttpServletResponse response,
                                                          String status, String beginDate, String endDate,
                                                          String supplier, String poSn, int page, int pageSize,
                                                          String personId) {
        PageResult<OrderListResp> pageResult = null;
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "基地回货发货列表页");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            pageResult = poManagerService.getMyOrderList(userInfo, status, beginDate, endDate, poSn, supplier, page,
                    pageSize);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(pageResult);
    }

    @ApiOperation(value = "获取订单号/发货单号", notes = "获取订单号/发货单号")
    @GetMapping("/getSn")
    public DataResult<String> getSn(HttpServletRequest request, HttpServletResponse response) {
        String result = null;
        try {
            result = poManagerService.getSn();
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(DataHandler.APP_RETURN_SUCCESS_CODE, "验证通过", result);
    }

    @ApiOperation(value = "基地编码验证", notes = "基地编码验证")
    @GetMapping("/checkSupplier")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "supplier", value = "基地编码", required = true, dataType =
                    "String")})
    public DataResult<SupplierResp> checkSupplier(HttpServletRequest request, HttpServletResponse response,
                                                  String supplier) {
        SupplierResp result = null;
        try {
            result = poManagerService.getSupplierInfo(supplier);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(result);
    }

    @ApiOperation(value = "获取入库仓库", notes = "获取入库仓库")
    @GetMapping("/getWareHouse")
    public DataResult<List<PoWareHouse>> getWareHouse(HttpServletRequest request, HttpServletResponse response) {
        List<PoWareHouse> reusltList = null;
        try {
            reusltList = poManagerService.getAllWh();
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(reusltList);
    }

    @ApiOperation(value = "保存订单头信息", notes = "保存订单头信息")
    @PostMapping("/saveHead")
    public DataResult saveHead(HttpServletRequest request, HttpServletResponse response, PoHeadReq poHeadReq,
                               String personId) {
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "基地回货录单");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            poManagerService.saveHead(userInfo, poHeadReq);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult("保存成功");
    }

    @ApiOperation(value = "商品编码验证", notes = "商品编码验证")
    @GetMapping("/checkItem")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "item", value = "商品编码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "supplier", value = "基地编码", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "whCode", value = "仓库编码", required = true, dataType =
                    "String")})
    public DataResult<ItemResp> checkItem(HttpServletRequest request, HttpServletResponse response, String item,
                                          String supplier, String whCode) {
        ItemResp result = null;
        try {
            result = poManagerService.getItemInfo(item, supplier, whCode);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(result);
    }

    @ApiOperation(value = "根据订单号获取订单头信息", notes = "根据订单号获取订单头信息")
    @GetMapping("/getHead")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "poSn", value = "订单号", required = true, dataType = "String")})
    public DataResult<PoHead> getHead(HttpServletRequest request, HttpServletResponse response, String poSn) {
        PoHead result = null;
        try {
            result = poManagerService.getPoHead(poSn);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(result);
    }

    @ApiOperation(value = "根据订单号获取订单行信息", notes = "根据订单号获取订单行信息")
    @GetMapping("/getDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "poSn", value = "订单号", required = true, dataType = "String")})
    public DataResult<List<PoDetail>> getDetail(HttpServletRequest request, HttpServletResponse response, String poSn) {
        List<PoDetail> reusltList = null;
        try {
            reusltList = poManagerService.getPoDetailList(poSn);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(reusltList);
    }

    @ApiOperation(value = "保存订单行信息", notes = "保存订单行信息")
    @PostMapping("/saveDetail")
    public DataResult saveDetail(HttpServletRequest request, HttpServletResponse response, PoDetailReq poDetailReq) {
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            poManagerService.saveDetail(userInfo, poDetailReq);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult("保存成功");
    }

    @ApiOperation(value = "批量修改订单行信息", notes = "批量修改订单行信息")
    @PostMapping("/batchUpdateDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "updateStr", value = "更新字符串【{'poSn':'11111','item':'111'," +
                    "'poQty':'100'}】", required = true, dataType = "String")})
    public DataResult batchUpdateDetail(HttpServletRequest request, HttpServletResponse response, String updateStr) {
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            poManagerService.batchUpdateDetail(userInfo, updateStr);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult("提交成功");
    }

    @ApiOperation(value = "提交采购订单", notes = "提交采购订单")
    @PostMapping("/submitPo")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "poSn", value = "订单号", required = true, dataType = "String")})
    public DataResult submitPo(HttpServletRequest request, HttpServletResponse response, String poSn) {
        try {
            // 修改相关状态
            poManagerService.updatePoStatus("02", poSn);// 更新为未审核状态
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult("提交成功");
    }

    @ApiOperation(value = "审核采购订单", notes = "审核采购订单")
    @PostMapping("/auditPo")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "poSn", value = "订单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "type", value = "1：通过 2：作废", required = true, dataType =
                    "int")})
    public DataResult auditPo(HttpServletRequest request, HttpServletResponse response, String poSn, int type,
                              String personId) {
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "基地回货审核");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            // 修改相关状态
            if (1 == type) {
                poManagerService.auditPoStatus("03", poSn, userInfo.getPersonId(), userInfo.getName());// 更新为为未发货状态
            } else if (2 == type) {
                poManagerService.auditPoStatus("05", poSn, userInfo.getPersonId(), userInfo.getName());// 更新为作废状态
            } else {
                ExceptionUtils.wapperBussinessException("审核更新类型错误");
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult("提交成功");
    }

    @ApiOperation(value = "根据订单号获取单个准备发货的商品", notes = "根据订单号获取单个准备发货的商品")
    @GetMapping("/getPrepareItem")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "poSn", value = "订单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "historyItem", value = "历史商品【没有传空，多个用逗号隔开】", required =
                    true, dataType = "String")})
    public DataResult<PoPrepareItemResp> getPrepareItem(HttpServletRequest request, HttpServletResponse response,
                                                        String poSn, String historyItem) {
        PoPrepareItemResp result = null;
        try {
            result = poManagerService.getPrepareItem(poSn, historyItem);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(result);
    }

    @ApiOperation(value = "根据订单号查询总发货数量", notes = "根据订单号查询总发货数量")
    @GetMapping("/getPoTotalQty")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "poSn", value = "订单号", required = true, dataType = "String")})
    public DataResult<BigDecimal> getPoTotalQty(HttpServletRequest request, HttpServletResponse response, String poSn) {
        BigDecimal result = null;
        try {
            result = poManagerService.getTotalSurplusQty(poSn);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(DataHandler.APP_RETURN_SUCCESS_CODE, "获取成功", result);
    }

    @ApiOperation(value = "确认发货", notes = "确认发货")
    @PostMapping("/submitPoAsn")
    public DataResult submitPoAsn(HttpServletRequest request, HttpServletResponse response, PoAsnReq poAsnReq,
                                  String personId) {
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "基地回货发货");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            poManagerService.submitPoAsn(userInfo, poAsnReq);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult("提交成功");
    }

    @ApiOperation(value = "获取已发货单的发货行", notes = "获取已发货单的发货行")
    @GetMapping("/getPoAsnDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "poAsnSn", value = "发货单号", required = true, dataType =
                    "String")})
    public DataResult<List<PoAsnDetailResp>> getPoAsnDetail(HttpServletRequest request, HttpServletResponse response,
                                                            String poAsnSn, String personId) {
        List<PoAsnDetailResp> resultList = null;
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "基地回货已发货单详情");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            resultList = poManagerService.getPoAsnDetailList(poAsnSn);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(resultList);
    }

    @ApiOperation(value = "根据发货订单号获取发货单头信息", notes = "根据发货订单号获取发货单头信息")
    @GetMapping("/getAsnHead")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "poAsnSn", value = "发货单号", required = true, dataType =
                    "String")})
    public DataResult<PoAsnHead> getAsnHead(HttpServletRequest request, HttpServletResponse response, String poAsnSn) {
        PoAsnHead reuslt = null;
        try {
            reuslt = poManagerService.getPoAsnHead(poAsnSn);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(reuslt);
    }

}
