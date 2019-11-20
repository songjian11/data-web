package com.cs.mobile.api.service.partner.runindex;

import java.util.List;

import com.cs.mobile.api.model.partner.runindex.RunIndexResult;
import com.cs.mobile.api.model.partner.runindex.RunWorkEffect;
import com.cs.mobile.api.model.user.UserInfo;

public interface RunIndexService {
	/**
	 * 获取经营指数报表数据
	 * 
	 * @param userInfo
	 * @param beginYm
	 * @param endYm
	 * @return
	 * @throws Exception
	 */
	public RunIndexResult getRunReportResult(UserInfo userInfo) throws Exception;

	/**
	 * 获取经营指数劳效趋势数据
	 * 
	 * @param userInfo
	 * @param beginYm
	 * @param endYm
	 * @return
	 * @throws Exception
	 */
	public List<RunWorkEffect> getRunWorkEffectList(UserInfo userInfo, String beginYm, String endYm) throws Exception;
}
