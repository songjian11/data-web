package com.cs.mobile.api.service.scm.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.cs.mobile.api.dao.scm.PoMangerDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.scm.ItemTaxRate;
import com.cs.mobile.api.model.scm.PoAsnDetail;
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
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.DateUtil;

@Service
public class PoManagerServiceImpl implements PoManagerService {
    @Autowired
    private PoMangerDao poMangerDao;
    @Value("${store.db.rmsdg.userName}")
    private String dguserName;
    @Value("${store.db.rmsdg.password}")
    private String dgpassword;
    @Value("${store.db.rmsdg.sid}")
    private String dgsid;
    @Value("${store.db.rmsdg.host}")
    private String dghost;
    @Value("${store.db.rmsdg.port}")
    private String dgport;
    @Autowired
    private DruidProperties druidProperties;

    public PageResult<OrderListResp> getAuditOrderList(UserInfo userInfo, String status, String beginDate,
                                                       String endDate,
                                                       String poSn, String supplier, int page, int pageSize) throws Exception {
        return poMangerDao.getAuditOrderList(userInfo, status, beginDate, endDate, poSn, supplier, page, pageSize);
    }

    public PageResult<OrderListResp> getMyOrderList(UserInfo userInfo, String status, String beginDate,
                                                    String endDate,
                                                    String poSn, String supplier, int page, int pageSize) throws Exception {
        return poMangerDao.getMyOrderList(userInfo, status, beginDate, endDate, poSn, supplier, page, pageSize);
    }

    @Override
    public PageResult<OrderListResp> getOrderList(UserInfo userInfo, String status, String beginDate, String endDate,
                                                  String poSn, String supplier, int page, int pageSize) throws Exception {
        return poMangerDao.getOrderList(userInfo, status, beginDate, endDate, poSn, supplier, page, pageSize);
    }

    public ItemResp getItemInfo(String item, String supplier, String whCode) throws Exception {
        ItemResp itemResp = null;
        try {
            changeDgDataSource();
            itemResp = poMangerDao.getItemInfo(item, supplier, whCode);
            if (null == itemResp) {
                ExceptionUtils.wapperBussinessException("没有查到该编码对应的商品,或者商品与基地不对应");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            DataSourceHolder.clearDataSource();
        }
        return itemResp;
    }

    public SupplierResp getSupplierInfo(String supplier) throws Exception {
        SupplierResp supplierResp = null;
        try {
            changeDgDataSource();
            supplierResp = poMangerDao.getSupplierInfo(supplier);
            if (null == supplierResp) {
                ExceptionUtils.wapperBussinessException("没有查到该编码对应的基地");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            DataSourceHolder.clearDataSource();
        }
        return supplierResp;
    }

    public List<PoWareHouse> getAllWh() throws Exception {
        return poMangerDao.getAllWh();
    }

    @Transactional
    public void saveHead(UserInfo userInfo, PoHeadReq poHeadReq) throws Exception {
        // 暂时只支持新增，因为考虑到更新的话，基地跟商品的关系乱套了
        PoHead poHead = new PoHead();
        BeanUtils.copyProperties(poHeadReq, poHead);
        Date now = DateUtil.getNowDate();
        poHead.setCreateTime(now);
        poHead.setValidityPerioid(DateUtils.addDays(now, 7));// 7天有效期
        poHead.setPoType("01");// 手工
        poHead.setPoStatus("01");// 未提交
        poHead.setCreatorId(userInfo.getPersonId());
        poHead.setCreator(userInfo.getName());
        poHead.setExpArrivalDate(DateUtils.parseDate(poHeadReq.getExpArrivalDate(), "yyyy-MM-dd"));
        int saveResult = poMangerDao.insertHead(poHead);
        if (saveResult == 0) {
            ExceptionUtils.wapperBussinessException("保存失败");
        }
    }

    public PoHead getPoHead(String poSn) throws Exception {
        return poMangerDao.getPoHead(poSn);
    }

    public PoDetail getPoDetail(String poSn, String item) throws Exception {
        return poMangerDao.getPoDetail(poSn, item);
    }

    public List<PoDetail> getPoDetailList(String poSn) throws Exception {
        return poMangerDao.getPoDetailList(poSn);
    }

    public void saveDetail(UserInfo userInfo, PoDetailReq poDetailReq) throws Exception {
        PoDetail poDetail = new PoDetail();
        BeanUtils.copyProperties(poDetailReq, poDetail);
        ItemTaxRate itemTaxRate = null;
        PoDetail dbPoDetail = poMangerDao.getPoDetail(poDetail.getPoSn(), poDetail.getItem());
        if (null != dbPoDetail) {
            ExceptionUtils.wapperBussinessException("商品不能重复添加");
        }
        try {
            changeDgDataSource();
            itemTaxRate = poMangerDao.getItemTaxRate(poDetailReq.getItem());
            if (null == itemTaxRate) {
                ExceptionUtils.wapperBussinessException("没有查到该编码对应的税率");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            DataSourceHolder.clearDataSource();
        }
        Date now = DateUtil.getNowDate();
        poDetail.setCreateTime(now);
        poDetail.setCreatorId(userInfo.getPersonId());
        poDetail.setCreator(userInfo.getName());
        poDetail.setSurplusQty(poDetail.getPoQty());
        poDetail.setTaxRate(itemTaxRate.getVatRate());
        int saveResult = poMangerDao.insertDetail(poDetail);
        if (saveResult == 0) {
            ExceptionUtils.wapperBussinessException("保存失败");
        }
    }

    public void updatePoStatus(String status, String poSn) throws Exception {
        int updateResult = poMangerDao.updatePoStatus(status, poSn);
        if (updateResult == 0) {
            ExceptionUtils.wapperBussinessException("保存失败");
        }
    }

    public void auditPoStatus(String status, String poSn, String auditorId, String auditor) throws Exception {
        int updateResult = poMangerDao.auditPoStatus(status, poSn, auditorId, auditor);
        if (updateResult == 0) {
            ExceptionUtils.wapperBussinessException("保存失败");
        }
    }

    public PoPrepareItemResp getPrepareItem(String poSn, String historyItem) throws Exception {
        return poMangerDao.getPrepareItem(poSn, historyItem);
    }

    public List<PoAsnDetailResp> getPoAsnDetailList(String poAsnSn) throws Exception {
        return poMangerDao.getPoAsnDetailList(poAsnSn);
    }

    public PoAsnHead getPoAsnHead(String poAsnSn) throws Exception {
        return poMangerDao.getPoAsnHead(poAsnSn);
    }

    public BigDecimal getTotalSurplusQty(String poSn) throws Exception {
        return poMangerDao.getTotalSurplusQty(poSn);
    }

    @Transactional
    public void submitPoAsn(UserInfo userInfo, PoAsnReq poAsnReq) throws Exception {
        PoAsnHead poAsnHead = new PoAsnHead();
        BeanUtils.copyProperties(poAsnReq, poAsnHead);
        List<PoAsnDetail> poAsnDetailList = JSONObject.parseArray(poAsnReq.getItemStr(), PoAsnDetail.class);
        BigDecimal totalQty = BigDecimal.ZERO;
        Date now = DateUtil.getNowDate();
        List<PoDetail> poDetailList = new ArrayList<PoDetail>();
        for (PoAsnDetail poAsnDetail : poAsnDetailList) {
            poAsnDetail.setCreateTime(now);
            poAsnDetail.setCreatorId(userInfo.getPersonId());
            poAsnDetail.setCreator(userInfo.getName());
            poAsnDetail.setPoAsnSn(poAsnReq.getPoAsnSn());
            totalQty = totalQty.add(poAsnDetail.getPoAsnQty());
            PoDetail poDetail = poMangerDao.getPoDetail(poAsnReq.getPoSn(), poAsnDetail.getItem());
            poDetail.setSurplusQty(poDetail.getSurplusQty().subtract(poAsnDetail.getPoAsnQty()));
            poDetailList.add(poDetail);
        }
        poAsnHead.setTotalQty(totalQty);
        poAsnHead.setPoAsnStatus("02");// 已完结
        poAsnHead.setCreateTime(now);
        poAsnHead.setCreatorId(userInfo.getPersonId());
        poAsnHead.setCreator(userInfo.getName());
        int insertAsnHeadResult = poMangerDao.insertAsnHead(poAsnHead);
        int insertAsnDetailResult = poMangerDao.batchInserAsnDetail(poAsnDetailList);
        int batchUpdateSurplusQtyResult = poMangerDao.batchUpdateSurplusQty(poDetailList);
        if (insertAsnHeadResult == 0 || insertAsnDetailResult < poAsnDetailList.size()
                || batchUpdateSurplusQtyResult == 0) {
            ExceptionUtils.wapperBussinessException("保存失败");
        }
        BigDecimal totalSurplusQty = poMangerDao.getTotalSurplusQty(poAsnReq.getPoSn());
        if (totalSurplusQty.compareTo(BigDecimal.ZERO) == 0) {// 当采购单剩余数量为0时，需要更新采购单状态为已完结
            poMangerDao.updatePoStatus("04", poAsnReq.getPoSn());
        }
    }

    public void batchUpdateDetail(UserInfo userInfo, String updateStr) throws Exception {
        List<PoDetail> poDetailList = JSONObject.parseArray(updateStr, PoDetail.class);
        poMangerDao.batchUpdateDetail(poDetailList);
    }

    private void changeDgDataSource() {
        GoodsDataSourceConfig goodsDataSourceConfig = new GoodsDataSourceConfig();
        goodsDataSourceConfig.setHost(dghost);
        goodsDataSourceConfig.setPort(dgport);
        goodsDataSourceConfig.setSid(dgsid);
        goodsDataSourceConfig.setStore(dgsid);
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, dguserName, dgpassword,
                druidProperties);
        DataSourceHolder.setDataSource(dataSourceBuilder);
    }

    /**
     * 生产订单序列
     *
     * @param
     * @return java.lang.Long
     * @author wells.wong
     * @date 2019/9/16
     */
    public String getSn() throws Exception {
        Long sn = poMangerDao.getSn();
        if (sn == null) {
            ExceptionUtils.wapperBussinessException("订单序列生成失败");
        }
        StringBuffer sb = new StringBuffer(sn.toString());
        while (sb.length() < 6) {
            sb.insert(0, "0");
        }
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        return sb.insert(0, date).toString();
    }
}
