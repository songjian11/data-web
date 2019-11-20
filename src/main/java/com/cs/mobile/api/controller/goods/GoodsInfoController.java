package com.cs.mobile.api.controller.goods;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.controller.mreport.MobileReportController;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.goods.response.GoodsInfoResp;
import com.cs.mobile.api.model.goods.response.GoodsSaleReportResp;
import com.cs.mobile.api.service.goods.GoodsService;
import com.cs.mobile.api.service.user.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Api(value = "商品档案接口", tags = { "商品档案接口" })
@RestController
@RequestMapping("/api/goods")
@Slf4j
public class GoodsInfoController extends AbstractApiController {
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	UserService userService;

	@ApiOperation(value = "根据门店及条形码查询商品信息", notes = "根据门店及条形码查询商品信息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "storeId", value = "门店ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "barcode", value = "条形码", required = true, dataType = "String") })
	@GetMapping("/info")
	public DataResult<GoodsInfoResp> getGoodsInfo(HttpServletRequest request, HttpServletResponse response,
			String storeId, String barcode, String personId) {
		GoodsInfoResp goodsInfoResp = null;
		// 记录访问日志
		try {
			userService.addPersonLog(personId, "商品查询");
		} catch (Exception e) {
			log.error("访问日志保存出错", e);
		}
		
		try {
			goodsInfoResp = goodsService.getGoodsInfo(storeId, barcode);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(goodsInfoResp);
	}

	@ApiOperation(value = "根据商品编码查询商品本月销售报表", notes = "根据商品编码查询商品本月销售报表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "storeId", value = "门店ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "item", value = "商品编码", required = true, dataType = "String") })
	@GetMapping("/monthReprot")
	public DataResult<GoodsSaleReportResp> monthReprot(HttpServletRequest request, HttpServletResponse response,
			String storeId, String item) {
		GoodsSaleReportResp goodsSaleReportResp = null;
		try {
			goodsSaleReportResp = goodsService.getMonthReprot(storeId, item);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(goodsSaleReportResp);
	}

	@ApiOperation(value = "根据商品编码查询商品本年销售报表", notes = "根据商品编码查询商品本年销售报表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "storeId", value = "门店ID", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "item", value = "商品编码", required = true, dataType = "String") })
	@GetMapping("/yearReprot")
	public DataResult<GoodsSaleReportResp> yearReprot(HttpServletRequest request, HttpServletResponse response,
			String storeId, String item) {
		GoodsSaleReportResp goodsSaleReportResp = null;
		try {
			goodsSaleReportResp = goodsService.getYearReprot(storeId, item);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(goodsSaleReportResp);
	}

	@ApiOperation(value = "根据区域ID获取所有门店", notes = "根据区域ID获取所有门店")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "parentId", value = "区域ID", required = true, dataType = "String") })
	@GetMapping("/getStoreByArea")
	public DataResult<List<Organization>> getStoreByArea(HttpServletRequest request, HttpServletResponse response,
			String parentId) {
		List<Organization> orgList = null;
		try {
			orgList = goodsService.getStoreByArea(parentId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(orgList);
	}

	@ApiOperation(value = "根据省份获取所有区域", notes = "根据省份获取所有区域")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "parentId", value = "省份ID", required = true, dataType = "String") })
	@GetMapping("/getAreaByProvince")
	public DataResult<List<Organization>> getAreaByProvince(HttpServletRequest request, HttpServletResponse response,
			String parentId) {
		List<Organization> orgList = null;
		try {
			orgList = goodsService.getAreaByProvince(parentId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(orgList);
	}

	@ApiOperation(value = "获取所有省份", notes = "获取所有省份")
	@GetMapping("/getAllProvince")
	public DataResult<List<Organization>> getAllProvince(HttpServletRequest request, HttpServletResponse response) {
		List<Organization> orgList = null;
		try {
			orgList = goodsService.getAllProvince();
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(orgList);
	}
}
