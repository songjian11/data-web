package com.cs.mobile.api.service.user.impl;

import java.util.List;

import com.cs.mobile.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.freshreport.FreshReportCsmbDao;
import com.cs.mobile.api.dao.user.UserDao;
import com.cs.mobile.api.model.freshreport.OrganizationForFresh;
import com.cs.mobile.api.model.partner.PartnerUserInfo;
import com.cs.mobile.api.model.user.User;
import com.cs.mobile.api.service.user.UserService;
import com.cs.mobile.common.constant.UserTypeEnum;

/**
 * 用户服务实现
 * 
 * @author wells.wong
 * @date 2018年11月19日
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;
	@Autowired
	private FreshReportCsmbDao freshReportCsmbDao;

	/**
	 * 根据用户工号查询用户
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 */
	public List<User> getUserListByPersonId(String personId) throws Exception {
		return userDao.getUserListByPersonId(personId);
	}

	public List<User> getUserOrgListByPersonId(String personId, int type) throws Exception {
		return userDao.getUserOrgListByPersonId(personId, type);
	}

	/**
	 * 根据用户工号查询用户信息
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 */
	public PartnerUserInfo getUserInfoByPersonId(String personId) throws Exception {
		return userDao.getUserInfoByPersonId(personId);
	}

	/**
	 * 门店管理员（即区域身份）获取用户信息
	 * 
	 * @author wells
	 * @param personId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public PartnerUserInfo getAUserInfoByPersonId(String personId, int type) throws Exception {
		return userDao.getAUserInfoByPersonId(personId, type);
	}

	/**
	 * 区域管理员（即省份身份）获取用户信息
	 * 
	 * @author wells
	 * @param personId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public PartnerUserInfo getPUserInfoByPersonId(String personId, int type) throws Exception {
		return userDao.getPUserInfoByPersonId(personId, type);
	}

	/**
	 * 获取管理员大店店长用户信息
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月12日
	 */
	public PartnerUserInfo getSUserInfoByPersonId(String personId, int type) throws Exception {
		return userDao.getSUserInfoByPersonId(personId, type);
	}

	/**
	 * 修改密码
	 */
	public void modifyPassword(String personId, String password) throws Exception {
		userDao.modifyPassword(personId, password);
	}

	/**
	 * 添加用户日志
	 */
	public void addPersonLog(String personId, String logPage) throws Exception {
		userDao.addPersonLog(personId, logPage);
	}

	public boolean isContainHunanReportRole(List<User> userList) throws Exception {
		List<OrganizationForFresh> orgList = freshReportCsmbDao.queryAllOrganization();
		for (User user : userList) {
			if (user.getType().intValue() == UserTypeEnum.SUPERADMIN.getType()) {// 全司
				return true;
			} else if (user.getType().intValue() == UserTypeEnum.MREPORT_PROVINCEADMIN.getType()
					&& user.getOrgId() == 101) {// 省份
				return true;
			} else if (user.getType().intValue() == UserTypeEnum.MREPORT_AREAADMIN.getType()
					&& user.getOrgId().toString().substring(0, 3).equals("101")) {// 区域
				return true;
			} else if (user.getType().intValue() == UserTypeEnum.MREPORT_STOREADMIN.getType()) {// 门店
				for (OrganizationForFresh org : orgList) {
					if (org.getProvinceId().equals("101") && user.getOrgId().toString().equals(org.getStoreId())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int isStoreManager(String personId) {
		if(StringUtils.isEmpty(personId)){
			return 0;
		}
		return userDao.isStoreManager(personId);
	}

}
