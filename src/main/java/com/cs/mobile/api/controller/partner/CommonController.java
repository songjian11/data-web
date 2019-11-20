package com.cs.mobile.api.controller.partner;

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
import com.cs.mobile.api.model.common.ComStore;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.service.common.CommonService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@SuppressWarnings("unchecked")
@Api(value = "common", tags = { "公共数据接口" })
@RestController
@RequestMapping("/api/common")
public class CommonController extends AbstractApiController {

	@Autowired
	CommonService commonService;

	@ApiOperation(value = "小店列表", notes = "小店下拉框")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getComStoreList")
	public DataResult<List<ComStore>> getComStoreList(HttpServletRequest request, HttpServletResponse response,
			String parentId) {
		List<ComStore> storeList = null;
		try {
			storeList = commonService.getComStoreList(parentId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(storeList);
	}

	@ApiOperation(value = "根据区域ID获取所有门店", notes = "根据区域ID获取所有门店")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getStoreByArea")
	public DataResult<List<Organization>> getStoreByArea(HttpServletRequest request, HttpServletResponse response,
			String parentId) {
		List<Organization> orgList = null;
		try {
			orgList = commonService.getStoreByArea(parentId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(orgList);
	}

	@ApiOperation(value = "根据省份获取所有区域", notes = "根据省份获取所有区域")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getAreaByProvince")
	public DataResult<List<Organization>> getAreaByProvince(HttpServletRequest request, HttpServletResponse response,
			String parentId) {
		List<Organization> orgList = null;
		try {
			orgList = commonService.getAreaByProvince(parentId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(orgList);
	}

	@ApiOperation(value = "获取所有省份", notes = "获取所有省份")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/getAllProvince")
	public DataResult<List<Organization>> getAllProvince(HttpServletRequest request, HttpServletResponse response) {
		List<Organization> orgList = null;
		try {
			orgList = commonService.getAllProvince();
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(orgList);
	}

}