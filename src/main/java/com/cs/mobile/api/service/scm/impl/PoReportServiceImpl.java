package com.cs.mobile.api.service.scm.impl;

import com.cs.mobile.api.model.scm.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.scm.PoReportDao;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.service.scm.PoReportService;

import java.util.ArrayList;
import java.util.List;

@Service
public class PoReportServiceImpl implements PoReportService {
	@Autowired
	private PoReportDao poReportDao;

	@Override
	public PageResult<PoItemReportResp> getItemReportPage(String beginDate, String endDate, String item,
			String supplier, int page, int pageSize) throws Exception {
		return poReportDao.getItemReportPage(beginDate, endDate, item, supplier, page, pageSize);
	}

	@Override
	public PageResult<PoSupReportResp> getSupReportPage(String beginDate, String endDate, String supplier, int page,
			int pageSize) throws Exception {
		return poReportDao.getSupReportPage(beginDate, endDate, supplier, page, pageSize);
	}

	public PageResult<PoItemDReportResp> getPoItemDReport(String beginDate, String endDate, String supplier,
			String item, String poSn, int page, int pageSize) throws Exception {
		return poReportDao.getPoItemDReport(beginDate, endDate, supplier, item, poSn, page, pageSize);
	}

	public PageResult<PoSupplierDReportResp> getPoSupplierDReport(String beginDate, String endDate, String supplier,
			int page, int pageSize) throws Exception {
		return poReportDao.getPoSupplierDReport(beginDate, endDate, supplier, page, pageSize);
	}

	public PageResult<PoPurchaserDReportResp> getPoPurchaserDReport(String beginDate, String endDate, String purchaser,
			String carType, int page, int pageSize) throws Exception {
		return poReportDao.getPoPurchaserDReport(beginDate, endDate, purchaser, carType, page, pageSize);
	}
}
