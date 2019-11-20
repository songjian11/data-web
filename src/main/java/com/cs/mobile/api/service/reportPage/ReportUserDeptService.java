package com.cs.mobile.api.service.reportPage;

import java.util.List;
import java.util.Set;

import com.cs.mobile.api.model.reportPage.UserDept;

public interface ReportUserDeptService {

	public List<UserDept> getUserDeptList(String personId) throws Exception;

	public List<UserDept> getAllDept() throws Exception;

	public Set<String> getAllCategoryByPersonId(String personId) throws Exception;

	public List<UserDept> getUserDeptByCategory(String personId, String category) throws Exception;
}
