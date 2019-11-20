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
import com.cs.mobile.api.model.partner.runindex.RunIndexResult;
import com.cs.mobile.api.model.partner.runindex.RunWorkEffect;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.partner.runindex.RunIndexService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 经营指数报表
 * 
 * @author wells.wong
 * @date 2018年11月24日
 */
@SuppressWarnings("unchecked")
@Api(value = "经营指数", tags = { "经营指数接口" })
@RestController
@RequestMapping("/api/run")
public class RunIndexController extends AbstractApiController {
	@Autowired
	RunIndexService runReportService;

	@ApiOperation(value = "获取经营指数", notes = "获取经营指数")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/report")
	public DataResult<RunIndexResult> runReport(HttpServletRequest request, HttpServletResponse response, String token,
			String uid) {
		RunIndexResult runReportResult = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			runReportResult = runReportService.getRunReportResult(userInfo);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(runReportResult);
	}

	@ApiOperation(value = "获取经营指数劳效趋势数据", notes = "获取经营指数劳效趋势数据")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "beginYm", value = "开始年月", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "endYm", value = "结束年月", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "token", value = "用户token", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "uid", value = "用户ID", required = true, dataType = "String") })
	@GetMapping("/trend")
	public DataResult<List<RunWorkEffect>> trend(HttpServletRequest request, HttpServletResponse response,
			String beginYm, String endYm, String token, String uid) {
		List<RunWorkEffect> runWorkEffectList = null;
		try {
			UserInfo userInfo = this.getCurUserInfo(request);
			runWorkEffectList = runReportService.getRunWorkEffectList(userInfo, beginYm, endYm);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(runWorkEffectList);
	}
}
