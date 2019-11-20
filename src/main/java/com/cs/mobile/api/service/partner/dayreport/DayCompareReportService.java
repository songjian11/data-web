package com.cs.mobile.api.service.partner.dayreport;

import java.util.List;

import com.cs.mobile.api.model.partner.dayreport.response.CompareData;
import com.cs.mobile.api.model.partner.dayreport.response.TimeReport;

/**
 * 当日比较报表服务
 * 
 * @author wells
 * @date 2019年3月14日
 */
public interface DayCompareReportService {
	/**
	 * 获取全司当日时间段报表
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeReport> getAllTimeList() throws Exception;

	/**
	 * 获取某省当日时间段报表
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeReport> getProvinceTimeList(String provinceId) throws Exception;

	/**
	 * 获取某区域当日时间段报表
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeReport> getAreaTimeList(String areaId) throws Exception;

	/**
	 * 获取某门店当日时间段报表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeReport> getStoreTimeList(String storeId) throws Exception;

	/**
	 * 获取某小店当日时间段报表
	 * 
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<TimeReport> getComTimeList(String storeId, String comId) throws Exception;

	/**
	 * 获取全司当日大类报表
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<CompareData> getAllDeptList() throws Exception;

	/**
	 * 获取某省当日大类报表
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<CompareData> getProvinceDeptList(String provinceId) throws Exception;

	/**
	 * 获取某区域当日大类报表
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<CompareData> getAreaDeptList(String areaId) throws Exception;

	/**
	 * 获取某门店当日大类报表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<CompareData> getStoreDeptList(String storeId) throws Exception;

	/**
	 * 获取某小店当日大类报表
	 * 
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月14日
	 */
	public List<CompareData> getComDeptList(String storeId, String comId) throws Exception;
}
