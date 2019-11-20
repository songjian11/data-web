package com.cs.mobile.api.service.user;

import java.util.List;

import com.cs.mobile.api.model.partner.PartnerUserInfo;
import com.cs.mobile.api.model.user.User;
import com.cs.mobile.api.model.user.UserInfo;

/**
 * 用户服务接口
 * 
 * @author wells.wong
 * @date 2018年11月19日
 */
public interface UserService {
	/**
	 * 根据用户工号查询用户
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 */
	public List<User> getUserListByPersonId(String personId) throws Exception;

	public List<User> getUserOrgListByPersonId(String personId, int type) throws Exception;

	/**
	 * 根据用户工号查询用户信息
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 */
	public PartnerUserInfo getUserInfoByPersonId(String personId) throws Exception;

	/**
	 * 门店管理员（即区域身份）获取用户信息
	 * 
	 * @author wells
	 * @param personId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public PartnerUserInfo getAUserInfoByPersonId(String personId, int type) throws Exception;

	/**
	 * 区域管理员（即省份身份）获取用户信息
	 * 
	 * @author wells
	 * @param personId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public PartnerUserInfo getPUserInfoByPersonId(String personId, int type) throws Exception;

	/**
	 * 获取管理员大店店长用户信息
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月12日
	 */
	public PartnerUserInfo getSUserInfoByPersonId(String personId, int type) throws Exception;

	/**
	 * 修改密码
	 * 
	 * @param personId
	 * @param password
	 * @throws Exception
	 */
	public void modifyPassword(String personId, String password) throws Exception;

	/**
	 * 添加用户日志
	 * 
	 * @param personId
	 * @param logPage
	 * @throws Exception
	 */
	public void addPersonLog(String personId, String logPage) throws Exception;

	public boolean isContainHunanReportRole(List<User> userList) throws Exception;

	int isStoreManager(String personId);
}
