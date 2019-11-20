package com.cs.mobile.api.service.scm.impl;

import com.cs.mobile.api.dao.scm.PoMangerDao;
import com.cs.mobile.api.dao.scm.PoOpenApiDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.scm.*;
import com.cs.mobile.api.model.scm.openapi.*;
import com.cs.mobile.api.service.scm.PoManagerService;
import com.cs.mobile.api.service.scm.PoOpenApiService;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PoOpenApiServiceImpl implements PoOpenApiService {
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
    private PoOpenApiDao poOpenApiDao;
    @Autowired
    private PoMangerDao poMangerDao;
    @Autowired
    private DruidProperties druidProperties;
    @Autowired
    private PoManagerService poManagerService;

    /**
     * @param supplier
     * @return com.cs.mobile.api.model.scm.openapi.PoCountResp
     * @author wells.wong
     * @date 2019/9/10
     */
    public PoCountResp getPoCountBySupplier(String supplier) {
        if (StringUtils.isEmpty(supplier)) {
            ExceptionUtils.wapperBussinessException("供应商编码不能为空");
        }
        PoCountResp result = new PoCountResp();
        result.setCount(poOpenApiDao.getPoCountBySupplier(supplier));
        return result;
    }

    public CheckResult checkData(PoReq poReq) throws Exception {
        //检查参数
        this.checkParam(poReq);
        CheckResult checkResult = null;
        try {
            changeDgDataSource();
            String wh = poOpenApiDao.getPhysicalWh(poReq.getHeadReq().getWhCode());
            if (StringUtils.isEmpty(wh)) {
                ExceptionUtils.wapperBussinessException("没有查到对应的仓位编码");
            }
            ItemTaxRate itemTaxRate = poMangerDao.getItemTaxRate(poReq.getDetailReq().getItem());
            if (itemTaxRate == null) {
                ExceptionUtils.wapperBussinessException("没有查到该编码对应的税率");
            }
            checkResult = new CheckResult();
            checkResult.setItemTaxRate(itemTaxRate);
            checkResult.setPhysicalWh(wh);
        } catch (Exception e) {
            throw e;
        } finally {
            DataSourceHolder.clearDataSource();
        }
        return checkResult;
    }

    /**
     * @param poReq
     * @return void
     * @author wells.wong
     * @date 2019/9/10
     */
    @Transactional
    public void creatPo(PoReq poReq, CheckResult checkResult) throws Exception {
        //保存头信息
        PoHead poHead = new PoHead();
        BeanUtils.copyProperties(poReq.getHeadReq(), poHead);
        Date now = DateUtil.getNowDate();
        String poSn = poManagerService.getSn();
        poHead.setPoSn(poSn);
        poHead.setCreateTime(now);
        poHead.setValidityPerioid(DateUtils.addDays(now, 7));// 7天有效期
        poHead.setPoStatus("03");// 开放接口推送过来的订单直接为审核通过的订单，直接进入待发货
        poHead.setAuditorId(poReq.getAuditorId());
        poHead.setAuditor(poReq.getAuditor());
        poHead.setAuditTime(now);
        poHead.setExpArrivalDate(DateUtils.parseDate(poReq.getHeadReq().getExpArrivalDate(), "yyyy-MM-dd"));
        String creatorId = poReq.getCreatorId();
        String creator = poReq.getCreator();
        poHead.setRemark(poReq.getHeadReq().getRemark());
        poHead.setPoType(poReq.getType());//默认为报价系统接入基地采购类型
        poHead.setWhCode(checkResult.getPhysicalWh());//入库仓库编码需要使用转换后的编码
        if ("01".equals(poReq.getType())) {//如果是供应商创建，则同时创建发货单
            poHead.setPoType("03");//传入'01'时需要转换成'03'即报价系统接入供应商采购类型
            if (poReq.getDeptId() != null && 36 == poReq.getDeptId().intValue()) {//供应商的36制单人给赵宁
                creatorId = "T9003013088";
                creator = "赵宁";
            } else if (poReq.getDeptId() != null && 37 == poReq.getDeptId().intValue()) {//供应商的37给罗丹
                creatorId = "T8808225808";
                creator = "罗丹";
            } else {
                ExceptionUtils.wapperBussinessException("供应商类型的订单创建，目前只支持36和37大类");
            }
        } else {
            poHead.setPoType("02");//'02'报价系统接入基地采购类型
            creator = poOpenApiDao.getPersonName(poReq.getCreatorId());
        }
        poHead.setCreatorId(creatorId);
        poHead.setCreator(creator);
        int headSaveResult = poMangerDao.insertHead(poHead);
        if (headSaveResult == 0) {
            ExceptionUtils.wapperBussinessException("保存失败");
        }
        //保存行信息
        PoDetail poDetail = new PoDetail();
        BeanUtils.copyProperties(poReq.getDetailReq(), poDetail);
        poDetail.setPoSn(poSn);
        poDetail.setCreateTime(now);
        poDetail.setCreatorId(creatorId);
        poDetail.setCreator(creator);
        poDetail.setSurplusQty(poDetail.getPoQty());
        poDetail.setTaxRate(checkResult.getItemTaxRate().getVatRate());
        int detailSaveResult = poMangerDao.insertDetail(poDetail);
        if (detailSaveResult == 0) {
            ExceptionUtils.wapperBussinessException("保存失败");
        }

    }

    private void checkParam(PoReq poReq) {
       /* if (StringUtils.isEmpty(poReq.getCreatorId())) {
            ExceptionUtils.wapperBussinessException("创建者ID不能为空");
        }
        if (StringUtils.isEmpty(poReq.getCreator())) {
            ExceptionUtils.wapperBussinessException("创建者姓名不能为空");
        }*/
        /*if (StringUtils.isEmpty(poReq.getBiddingSn())) {
            ExceptionUtils.wapperBussinessException("报价单号不能为空");
        }*/
        if (StringUtils.isEmpty(poReq.getAuditorId())) {
            ExceptionUtils.wapperBussinessException("审核者ID不能为空");
        }
        if (StringUtils.isEmpty(poReq.getAuditor())) {
            ExceptionUtils.wapperBussinessException("审核者姓名不能为空");
        }
        HeadReq headReq = poReq.getHeadReq();
        DetailReq detailReq = poReq.getDetailReq();
        if (headReq == null) {
            ExceptionUtils.wapperBussinessException("头信息参数不正确");
        }
        if (detailReq == null) {
            ExceptionUtils.wapperBussinessException("行信息参数不正确");
        }
        /*if (StringUtils.isEmpty(headReq.getPurchaser())) {
            ExceptionUtils.wapperBussinessException("采购员不能为空");
        }*/
        if (StringUtils.isEmpty(headReq.getSupplier())) {
            ExceptionUtils.wapperBussinessException("基地编码不能为空");
        }
        if (StringUtils.isEmpty(headReq.getSupName())) {
            ExceptionUtils.wapperBussinessException("基地名称不能为空");
        }
        if (StringUtils.isEmpty(headReq.getWhCode())) {
            ExceptionUtils.wapperBussinessException("入库仓库编码不能为空");
        }
        if (StringUtils.isEmpty(headReq.getWhName())) {
            ExceptionUtils.wapperBussinessException("入库仓库名称不能为空");
        }
        if (StringUtils.isEmpty(headReq.getExpArrivalDate())) {
            ExceptionUtils.wapperBussinessException("预计到货日期不能为空");
        }
        if (StringUtils.isEmpty(detailReq.getItem())) {
            ExceptionUtils.wapperBussinessException("商品编码不能为空");
        }
        if (StringUtils.isEmpty(detailReq.getItemDesc())) {
            ExceptionUtils.wapperBussinessException("商品名称不能为空");
        }
        if (detailReq.getUnitPrice() == null) {
            ExceptionUtils.wapperBussinessException("重量单价不能为空");
        }
        if (detailReq.getPerPrice() == null) {
            ExceptionUtils.wapperBussinessException("件数单价不能为空");
        }
        if (StringUtils.isEmpty(detailReq.getUomDesc())) {
            ExceptionUtils.wapperBussinessException("订货单位不能为空");
        }
        if (detailReq.getPoQty() == null) {
            ExceptionUtils.wapperBussinessException("数量不能为空");
        }
        if (detailReq.getStandardOfPackage() == null) {
            ExceptionUtils.wapperBussinessException("标准件不能为空");
        }
        if (detailReq.getNumberOfPackage() == null) {
            ExceptionUtils.wapperBussinessException("件数不能为空");
        }
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
}
