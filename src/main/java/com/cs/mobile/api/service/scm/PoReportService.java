package com.cs.mobile.api.service.scm;

import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.scm.response.*;

/**
 * 基地回货报表
 * 
 * @author wells.wong
 * @date 2019年7月23日
 *
 */
public interface PoReportService {
	public PageResult<PoItemReportResp> getItemReportPage(String beginDate, String endDate, String item,
			String supplier, int page, int pageSize) throws Exception;

	public PageResult<PoSupReportResp> getSupReportPage(String beginDate, String endDate, String supplier, int page,
			int pageSize) throws Exception;

	/**
	 * 基地回货单品维度报表
	 *
	 * @param beginDate
	 * @param endDate
	 * @param supplier
	 * @param item
	 * @param poSn
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年8月22日
	 *
	 */
	public PageResult<PoItemDReportResp> getPoItemDReport(String beginDate, String endDate, String supplier,
			String item, String poSn, int page, int pageSize) throws Exception;

	/**
	 * 基地回货基地维度报表
	 *
	 * @param beginDate
	 * @param endDate
	 * @param supplier
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年8月22日
	 *
	 */
	public PageResult<PoSupplierDReportResp> getPoSupplierDReport(String beginDate, String endDate, String supplier,
			int page, int pageSize) throws Exception;

	/**
	 * 基地回货采购员维度报表
	 *
	 * @param beginDate
	 * @param endDate
	 * @param purchaser
	 * @param carType
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年8月22日
	 *
	 */
	public PageResult<PoPurchaserDReportResp> getPoPurchaserDReport(String beginDate, String endDate, String purchaser,
			String carType, int page, int pageSize) throws Exception;
}
