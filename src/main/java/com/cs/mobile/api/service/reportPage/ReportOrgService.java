package com.cs.mobile.api.service.reportPage;

import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.user.UserInfo;

import java.util.List;

public interface ReportOrgService {

	public List<Organization> getStoreByArea(String areaId, UserInfo userInfo) throws Exception;

	public List<Organization> getAreaByProvince(String provinceId, UserInfo userInfo) throws Exception;

	public List<Organization> getAllProvince(UserInfo userInfo) throws Exception;

}
