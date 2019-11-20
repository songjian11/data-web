package com.cs.mobile.api.controller.partner;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.partner.ranking.response.RankingDetailResp;
import com.cs.mobile.api.model.partner.ranking.response.RankingResp;
import com.cs.mobile.api.model.partner.ranking.response.UserRankingResp;
import com.cs.mobile.api.model.partner.ranking.response.UserRankingTimeLineResp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 赛马相关接口
 * 
 * @author wells
 * @date 2019年1月11日
 */
@SuppressWarnings("unchecked")
@Api(value = "RankingController", tags = { "赛马接口" })
@RestController
@RequestMapping("/api/ranking")
public class RankingController extends AbstractApiController {
	@ApiOperation(value = "获取用户所在团队的排名", notes = "获取用户所在团队的排名")
	@GetMapping("/getUserRank")
	public DataResult<UserRankingResp> getUserRank(HttpServletRequest request, HttpServletResponse response,
			String storeId, String comId, String ym) {
		UserRankingResp userRankResp = null;
		try {

		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(userRankResp);
	}

	@ApiOperation(value = "获取TOP排行榜", notes = "获取TOP排行榜")
	@GetMapping("/getTopRankingList")
	public DataResult<List<RankingResp>> getTopRankingList(HttpServletRequest request, HttpServletResponse response,
			String storeId, String comId, int top, String ym) {
		List<RankingResp> rankingRespList = null;
		try {

		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(rankingRespList);
	}

	@ApiOperation(value = "获取排行详情", notes = "获取排行详情")
	@GetMapping("/getRankingDetail")
	public DataResult<RankingDetailResp> getRankingDetail(HttpServletRequest request, HttpServletResponse response,
			String storeId, String comId, String code, String ym) {
		RankingDetailResp rankingDetailResp = null;
		try {

		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(rankingDetailResp);
	}

	@ApiOperation(value = "分页获取排行榜", notes = "分页获取排行榜")
	@GetMapping("/getPageRanking")
	public DataResult<List<RankingResp>> getPageRanking(HttpServletRequest request, HttpServletResponse response,
			String storeId, String comId, int pageNum, int pageSize, String ym) {
		PageResult<RankingResp> userRankResp = new PageResult<RankingResp>();
		try {

		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(userRankResp);
	}

	@ApiOperation(value = "获取近6个月排行榜", notes = "获取近6个月排行榜")
	@GetMapping("/getHalfYearRankingList")
	public DataResult<List<UserRankingTimeLineResp>> getHalfYearRankingList(HttpServletRequest request,
			HttpServletResponse response, String storeId, String comId, String ym) {
		List<UserRankingTimeLineResp> userMonthRankingRespList = null;
		try {

		} catch (Exception e) {
			return super.handleException(request, response, e);
		}
		return DataHandler.jsonResult(userMonthRankingRespList);
	}

}
