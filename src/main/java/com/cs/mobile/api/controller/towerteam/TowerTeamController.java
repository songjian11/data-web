package com.cs.mobile.api.controller.towerteam;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.towerteam.TowerTeamResult;
import com.cs.mobile.api.model.towerteam.request.TowerTeamSubmitReq;
import com.cs.mobile.api.model.towerteam.response.TowerTeamAreaResp;
import com.cs.mobile.api.model.towerteam.response.TowerTeamResultResp;
import com.cs.mobile.api.model.towerteam.response.TowerTeamStoreResp;
import com.cs.mobile.api.service.towerteam.TowerTeamService;
import com.cs.mobile.common.exception.api.ExceptionUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Api(value = "towerTeam", tags = { "门店支援接口" })
@RestController
@RequestMapping("/api/towerTeam")
public class TowerTeamController extends AbstractApiController {
	@Autowired
	TowerTeamService towerTeamService;
	@Value("${tt.deadline}")
	private String deadLine;

	/**
	 * 获取所有区域
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @author wells
	 * @date 2019年1月18日
	 */
	@ApiOperation(value = "获取所有省份", notes = "获取所有省份")
	@GetMapping("/getAllProvince")
	public DataResult<List<TowerTeamAreaResp>> getAllProvince(HttpServletRequest request,
			HttpServletResponse response) {
		List<TowerTeamAreaResp> list = null;
		try {
			list = towerTeamService.getAllProvince();
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	@ApiOperation(value = "获取所有大区", notes = "获取所有大区")
	@GetMapping("/getAreaGroupByP")
	public DataResult<List<TowerTeamAreaResp>> getAreaGroupByP(HttpServletRequest request, HttpServletResponse response,
			String provinceId) {
		List<TowerTeamAreaResp> list = null;
		try {
			list = towerTeamService.getAreaGroupByP(provinceId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	@ApiOperation(value = "获取所有区域", notes = "获取所有区域")
	@GetMapping("/getAreaByG")
	public DataResult<List<TowerTeamAreaResp>> getAreaByG(HttpServletRequest request, HttpServletResponse response,
			String areaGroupId) {
		List<TowerTeamAreaResp> list = null;
		try {
			list = towerTeamService.getAreaByG(areaGroupId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(list);
	}

	/**
	 * 根据区域信息分页查询门店数据
	 * 
	 * @param request
	 * @param response
	 * @param provinceId
	 * @param areaGroupId
	 * @param areaId
	 * @param page
	 * @param pageSize
	 * @return
	 * @author wells
	 * @date 2019年1月17日
	 */
	@ApiOperation(value = "分页查询门店数据", notes = "分页查询门店数据")
	@GetMapping("/getStoreList")
	public DataResult<List<TowerTeamStoreResp>> getStoreList(HttpServletRequest request, HttpServletResponse response,
			String provinceId, String areaGroupId, String areaId, int page, int pageSize) {
		PageResult<TowerTeamStoreResp> pageResult = null;
		try {
			pageResult = towerTeamService.getConfigByPga(provinceId, areaGroupId, areaId, page, pageSize);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(pageResult);
	}

	/**
	 * 保存提交的报名信息
	 * 
	 * @param request
	 * @param response
	 * @param towerTeamResult
	 * @return
	 * @author wells
	 * @date 2019年1月17日
	 */
	@ApiOperation(value = "保存报名", notes = "保存报名")
	@PostMapping("/saveResult")
	public DataResult saveResult(HttpServletRequest request, HttpServletResponse response,
			TowerTeamSubmitReq towerTeamSubmitReq) {
		try {
			if (this.towerTeamOutOfTime(deadLine)) {
				ExceptionUtils.wapperBussinessException("报名已经截止");
			}
			TowerTeamResult towerTeamResult = new TowerTeamResult();
			BeanUtils.copyProperties(towerTeamSubmitReq, towerTeamResult);
			towerTeamService.submit(towerTeamResult);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult("报名成功");
	}

	/**
	 * 获取个人的报名信息
	 * 
	 * @param request
	 * @param response
	 * @param personId
	 * @return
	 * @author wells
	 * @date 2019年1月17日
	 */
	@ApiOperation(value = "获取个人的报名信息", notes = "获取个人的报名信息")
	@GetMapping("/getPersonResult")
	public DataResult<TowerTeamResultResp> getPersonResult(HttpServletRequest request, HttpServletResponse response,
			String personId) {
		TowerTeamResultResp towerTeamResult = null;
		try {
			towerTeamResult = towerTeamService.getPersonResult(personId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(towerTeamResult);
	}

	/**
	 * 取消报名
	 * 
	 * @param request
	 * @param response
	 * @param personId
	 * @return
	 * @author wells
	 * @date 2019年1月17日
	 */
	@ApiOperation(value = "取消报名", notes = "取消报名")
	@PostMapping("/cancel")
	public DataResult cancel(HttpServletRequest request, HttpServletResponse response, String storeId,
			String positionId, String personId) {
		try {
			if (this.towerTeamOutOfTime(deadLine)) {
				ExceptionUtils.wapperBussinessException("报名已经截止，不能再取消");
			}
			towerTeamService.cancel(storeId, positionId, personId);
		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult("取消成功");
	}

}
