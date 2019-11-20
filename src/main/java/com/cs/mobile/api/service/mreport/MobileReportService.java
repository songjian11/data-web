package com.cs.mobile.api.service.mreport;

import java.util.List;

import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.mreport.response.AreaGroupReportResp;
import com.cs.mobile.api.model.mreport.response.DateTitleResp;
import com.cs.mobile.api.model.mreport.response.PermeatioResp;
import com.cs.mobile.api.model.mreport.response.StoreDayDeptReportResp;
import com.cs.mobile.api.model.mreport.response.StoreDayTimeReportResp;

/**
 * 移动报表服务
 * 
 * @author wells
 * @date 2019年1月17日
 */
public interface MobileReportService {
	/**
	 * 获取所有省份
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<Organization> getAllProvince() throws Exception;

	/**
	 * 根据省份获取所有区域
	 * 
	 * @param areaGroupId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<Organization> getAreaByP(String provinceId) throws Exception;

	/**
	 * 分页获取渗透率报表
	 * 
	 * @param provinceId
	 * @param areaGroupId
	 * @param areaId
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public PageResult<PermeatioResp> getPermeatioResp(String provinceId, String areaId, int page, int pageSize)
			throws Exception;

	/**
	 * 获取大区战报表头信息
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月29日
	 */
	public DateTitleResp getDateTitleResp() throws Exception;

	/**
	 * 获取大区战报
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月29日
	 */
	public AreaGroupReportResp getAreaGroupReportResp() throws Exception;

	/**
	 * 门店时段报表
	 * 
	 * @param storeId
	 * @param ymd
	 * @return
	 * @throws Exception
	 */
	public StoreDayTimeReportResp getDayTimeReport(String storeId, String ymd) throws Exception;

	/**
	 * 门店大类报表
	 * 
	 * @param storeId
	 * @param ymd
	 * @return
	 * @throws Exception
	 */
	public StoreDayDeptReportResp getDayDeptReport(String storeId, String ymd) throws Exception;
}
