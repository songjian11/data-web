package com.cs.mobile.api.service.scm;

import java.math.BigDecimal;
import java.util.List;

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

/**
 * 基地回货管理
 *
 * @author wells.wong
 * @date 2019年7月23日
 */
public interface PoManagerService {
    /**
     * 获取审核订单
     *
     * @param userInfo
     * @param status
     * @param beginDate
     * @param endDate
     * @param poSn
     * @param supplier
     * @param page
     * @param pageSize
     * @return com.cs.mobile.api.model.common.PageResult<com.cs.mobile.api.model.scm.response.OrderListResp>
     * @author wells.wong
     * @date 2019/10/15
     */
    PageResult<OrderListResp> getAuditOrderList(UserInfo userInfo, String status, String beginDate, String endDate,
                                                String poSn, String supplier, int page, int pageSize) throws Exception;

    /**
     * 获取自己发布的订单
     *
     * @param userInfo
     * @param status
     * @param beginDate
     * @param endDate
     * @param poSn
     * @param supplier
     * @param page
     * @param pageSize
     * @return com.cs.mobile.api.model.common.PageResult<com.cs.mobile.api.model.scm.response.OrderListResp>
     * @author wells.wong
     * @date 2019/10/15
     */
    PageResult<OrderListResp> getMyOrderList(UserInfo userInfo, String status, String beginDate, String endDate,
                                             String poSn, String supplier, int page, int pageSize) throws Exception;

    /**
     * 获取单据
     *
     * @param beginDate
     * @param endDate
     * @param poSn
     * @param supplier
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    PageResult<OrderListResp> getOrderList(UserInfo userInfo, String status, String beginDate, String endDate,
                                           String poSn, String supplier, int page, int pageSize) throws Exception;

    /**
     * 获取商品信息
     *
     * @param item
     * @param supplier
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    ItemResp getItemInfo(String item, String supplier, String whCode) throws Exception;

    /**
     * 获取基地信息
     *
     * @param supplier
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    SupplierResp getSupplierInfo(String supplier) throws Exception;

    /**
     * 获取所有仓库
     *
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    List<PoWareHouse> getAllWh() throws Exception;

    /**
     * 保存订单头信息
     *
     * @param poHeadReq
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    void saveHead(UserInfo userInfo, PoHeadReq poHeadReq) throws Exception;

    /**
     * 根据订单号获取订单头信息
     *
     * @param poSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    PoHead getPoHead(String poSn) throws Exception;

    /**
     * 查询单个订单行信息
     *
     * @param poSn
     * @param item
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月27日
     */
    PoDetail getPoDetail(String poSn, String item) throws Exception;

    /**
     * 查询订单行信息列表
     *
     * @param poSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    List<PoDetail> getPoDetailList(String poSn) throws Exception;

    /**
     * 保存订单行信息
     *
     * @param userInfo
     * @param poDetailReq
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    void saveDetail(UserInfo userInfo, PoDetailReq poDetailReq) throws Exception;

    /**
     * 更新订单状态
     *
     * @param status
     * @param poSn
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    void updatePoStatus(String status, String poSn) throws Exception;

    /**
     * 审核订单
     *
     * @param status
     * @param poSn
     * @param auditorId
     * @param auditor
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月27日
     */
    void auditPoStatus(String status, String poSn, String auditorId, String auditor) throws Exception;

    /**
     * 根据订单号获取单个准备发货的商品
     *
     * @param poSn
     * @param historyItem
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    PoPrepareItemResp getPrepareItem(String poSn, String historyItem) throws Exception;

    /**
     * 获取已发货单的发货行
     *
     * @param poAsnSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    List<PoAsnDetailResp> getPoAsnDetailList(String poAsnSn) throws Exception;

    /**
     * 根据发货订单号获取发货单头信息
     *
     * @param poAsnSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    PoAsnHead getPoAsnHead(String poAsnSn) throws Exception;

    /**
     * 根据订单号查询剩余总发货数量
     *
     * @param poSn
     * @return
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月29日
     */
    BigDecimal getTotalSurplusQty(String poSn) throws Exception;

    /**
     * 提交发货单
     *
     * @param userInfo
     * @param poAsnReq
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    void submitPoAsn(UserInfo userInfo, PoAsnReq poAsnReq) throws Exception;

    /**
     * 批量修改订单行信息
     *
     * @param userInfo
     * @param updateStr
     * @throws Exception
     * @author wells.wong
     * @date 2019年7月26日
     */
    void batchUpdateDetail(UserInfo userInfo, String updateStr) throws Exception;

    /**
     * 生产订单序列
     *
     * @param
     * @return java.lang.Long
     * @author wells.wong
     * @date 2019/9/16
     */
    String getSn() throws Exception;
}
