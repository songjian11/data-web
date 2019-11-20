package com.cs.mobile.api.service.scm.impl;

import com.cs.mobile.api.dao.scm.StoreOrderReportDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.scm.StoreOrderReport;
import com.cs.mobile.api.model.scm.response.StoreOrderReportDetailResponse;
import com.cs.mobile.api.model.scm.response.StoreOrderReportResponse;
import com.cs.mobile.api.service.scm.StoreOrderReportService;
import com.cs.mobile.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoreOrderReportServiceImpl implements StoreOrderReportService {
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
    @Autowired
    StoreOrderReportDao storeOrderReportDao;

    /**
     * 门店要货报表
     * @param deptIds
     * @return
     * @throws Exception
     */
    @Override
    public StoreOrderReportResponse queryStoreOrderReport(String deptIds) throws Exception {
        StoreOrderReportResponse result = new StoreOrderReportResponse();
        //下单门店
        List<StoreOrderReportDetailResponse> orderList;
        //未下单门店
        List<StoreOrderReportDetailResponse> noOrderList;
        //基础数据po
        List<StoreOrderReport> storeOrderReports = null;
        //解析查询条件deptId，支持多选使用逗号隔开
        List<String> depts = new ArrayList<String>();
        if(StringUtils.isNotEmpty(deptIds)){
            if(deptIds.endsWith(",")){//防止前端没有去除尾部拼接符号
                deptIds = deptIds.substring(0,deptIds.lastIndexOf(","));
            }
            String[] arr = deptIds.split(",");
            if(null != arr && arr.length > 0){
                for(int i=0; i<arr.length; i++){
                    depts.add(arr[i]);
                }
            }
        }

        try{//查询门店要货数据
            changeDgDataSource();
            storeOrderReports = storeOrderReportDao.queryStoreOrderReport(depts);
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceHolder.clearDataSource();
        }

        //合计
        if(null != storeOrderReports && storeOrderReports.size() > 0){
            orderList = new ArrayList<>();
            noOrderList = new ArrayList<>();
            for(StoreOrderReport storeOrderReport : storeOrderReports){//遍历查询数据，汇总下单和未下单门店
                StoreOrderReportDetailResponse detail = new StoreOrderReportDetailResponse();
                detail.setStoreId(storeOrderReport.getStoreId());
                detail.setStoreName(storeOrderReport.getStoreName());
                detail.setOrderNum(storeOrderReport.getOrderNum());
                detail.setSkuNum(storeOrderReport.getSkuNum());

                if(StringUtils.isNotEmpty(storeOrderReport.getOrderNum())
                        && Integer.valueOf(storeOrderReport.getOrderNum()).intValue() > 0){//下单门店
                    orderList.add(detail);
                } else{//未下单门店
                    noOrderList.add(detail);
                }
            }

            result.setOrderStoreNum(orderList.size());
            result.setOrderList(orderList);
            result.setNoOrderStoreNum(noOrderList.size());
            result.setNoOrderList(noOrderList);
        }
        return result;
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
