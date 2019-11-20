package com.cs.mobile.api.service.scm;

import com.cs.mobile.api.model.scm.response.StoreOrderReportResponse;

public interface StoreOrderReportService {/**
 * 门店要货报表
 * @param deptIds
 * @return
 * @throws Exception
 */
StoreOrderReportResponse queryStoreOrderReport(String deptIds)throws Exception;
}
