package com.cs.mobile.api.service.common;

import java.util.List;

import com.cs.mobile.api.model.common.ComStore;
import com.cs.mobile.api.model.common.CsmbOrg;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.user.User;

/**
 * 用户服务接口
 * 
 * @author jiangliang
 * @date 2018年11月19日
 */
public interface CommonService {

	/**
	 * 根据大店编码查询小店列表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public List<ComStore> getComStoreList(String storeId) throws Exception;

	/**
	 * 根据区域ID获取所有门店
	 * 
	 * @author wells
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getStoreByArea(String areaId) throws Exception;

	/**
	 * 根据省份获取所有区域
	 * 
	 * @author wells
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAreaByProvince(String provinceId) throws Exception;

	/**
	 * 获取所有省份
	 * 
	 * @author wells
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAllProvince() throws Exception;

	/**
	 * 
	 * 获取当前日期的对应去年日期
	 * 
	 * @param curDay
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月19日
	 *
	 */
	public String getLastYearDay(String curDay) throws Exception;

	/**
	 * 获取用户的组织机构层,如果用户是全司权限请传null
	 * @param list(用户组织权限list)
	 * @return
	 */
	List<CsmbOrg> queryAllCsmbOrg(List<User> list);

	/**
	 * 获取组织机构层
	 * @return
	 */
	List<CsmbOrg> queryAllCsmbOrg();
}
