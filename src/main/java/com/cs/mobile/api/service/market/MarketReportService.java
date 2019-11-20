package com.cs.mobile.api.service.market;

import com.cs.mobile.api.model.market.request.MarkReportRequest;
import com.cs.mobile.api.model.market.response.DepartmentSaleListResponse;
import com.cs.mobile.api.model.market.response.TrendReportListResponse;


/**
 * 市场报表
 */
public interface MarketReportService {
    /**
     * 查询部门统计
     * @param param
     * @return
     * @throws Exception
     */
    DepartmentSaleListResponse queryCurrentDepartmentSales(MarkReportRequest param)throws Exception;

    /**
     * 查询趋势
     * @param param
     * @return
     * @throws Exception
     */
    TrendReportListResponse queryDepartmentTrendReport(MarkReportRequest param)throws Exception;

    /**
     * 查询大类汇总(大类 + 分部 + 品类汇总数据)
     * @param param
     * @return
     * @throws Exception
     */
    DepartmentSaleListResponse queryCurrentDeptSales(MarkReportRequest param)throws Exception;

    /**
     * 查询大类趋势
     * @param param
     * @return
     * @throws Exception
     */
    TrendReportListResponse queryDeptTrendReport(MarkReportRequest param) throws Exception;
}
