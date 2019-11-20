package com.cs.mobile.api.service.reportPage.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.reportPage.ReportOrgDao;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.reportPage.ReportOrgService;

@Service
public class ReportOrgServiceImpl implements ReportOrgService {
	@Autowired
	private ReportOrgDao reportOrgDao;

	public List<Organization> getStoreByArea(String areaId, UserInfo userInfo) throws Exception {
		return reportOrgDao.getStoreByArea(areaId, userInfo);
	}

	public List<Organization> getAreaByProvince(String provinceId, UserInfo userInfo) throws Exception {
		return reportOrgDao.getAreaByProvince(provinceId, userInfo);
	}

	public List<Organization> getAllProvince(UserInfo userInfo) throws Exception {
		return reportOrgDao.getAllProvince(userInfo);
	}
}
