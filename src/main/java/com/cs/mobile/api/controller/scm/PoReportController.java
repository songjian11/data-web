package com.cs.mobile.api.controller.scm;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cs.mobile.api.model.scm.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.service.scm.PoReportService;
import com.cs.mobile.api.service.user.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({ "unchecked" })
@Api(value = "PoReport", tags = { "基地回货报表接口" })
@RestController
@RequestMapping("/api/poReport")
@Slf4j
public class PoReportController extends AbstractApiController {
	@Autowired
	private PoReportService poReportService;
	@Autowired
	UserService userService;

	@ApiOperation(value = "分页查询单品报表", notes = "分页查询单品报表")
	@GetMapping("/getItemReportPage")
	public DataResult<List<PoItemReportResp>> getItemReportPage(HttpServletRequest request,
			HttpServletResponse response, String beginDate, String endDate, String item, String supplier, int page,
			int pageSize) {
		PageResult<PoItemReportResp> pageResult = null;
		try {
			pageResult = poReportService.getItemReportPage(beginDate, endDate, item, supplier, page, pageSize);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(pageResult);
	}

	@ApiOperation(value = "分页查询基地报表", notes = "分页查询基地报表")
	@GetMapping("/getSupReportPage")
	public DataResult<List<PoSupReportResp>> getSupReportPage(HttpServletRequest request, HttpServletResponse response,
			String beginDate, String endDate, String supplier, int page, int pageSize) {
		PageResult<PoSupReportResp> pageResult = null;
		try {
			pageResult = poReportService.getSupReportPage(beginDate, endDate, supplier, page, pageSize);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(pageResult);
	}

	@ApiOperation(value = "分页查询单品维度报表", notes = "分页查询单品维度报表")
	@GetMapping("/getPoItemDReport")
	public DataResult<List<PoItemDReportResp>> getPoItemDReport(HttpServletRequest request,
			HttpServletResponse response, String beginDate, String endDate, String supplier, String item, String poSn,
			int page, int pageSize, String personId) {
		PageResult<PoItemDReportResp> pageResult = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "基地回货报表-单品维度");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			pageResult = poReportService.getPoItemDReport(beginDate, endDate, supplier, item, poSn, page, pageSize);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(pageResult);
	}

	@ApiOperation(value = "分页查询基地维度报表", notes = "分页查询基地维度报表")
	@GetMapping("/getPoSupplierDReport")
	public DataResult<List<PoSupplierDReportResp>> getPoSupplierDReport(HttpServletRequest request,
			HttpServletResponse response, String beginDate, String endDate, String supplier, int page, int pageSize, String personId) {
		PageResult<PoSupplierDReportResp> pageResult = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "基地回货报表-供应商维度");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			pageResult = poReportService.getPoSupplierDReport(beginDate, endDate, supplier, page, pageSize);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(pageResult);
	}

	@ApiOperation(value = "分页查询采购员维度报表", notes = "分页查询采购员维度报表")
	@GetMapping("/getPoPurchaserDReport")
	public DataResult<List<PoPurchaserDReportResp>> getPoPurchaserDReport(HttpServletRequest request,
			HttpServletResponse response, String beginDate, String endDate, String purchaser, String carType, int page,
			int pageSize, String personId) {
		PageResult<PoPurchaserDReportResp> pageResult = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "基地回货报表-采购员维度");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		try {
			pageResult = poReportService.getPoPurchaserDReport(beginDate, endDate, purchaser, carType, page, pageSize);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(pageResult);
	}
}
