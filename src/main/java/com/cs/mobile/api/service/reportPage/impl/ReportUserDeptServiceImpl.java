package com.cs.mobile.api.service.reportPage.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.reportPage.ReportUserDeptDao;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.service.reportPage.ReportUserDeptService;

@Service
public class ReportUserDeptServiceImpl implements ReportUserDeptService {
	@Autowired
	private ReportUserDeptDao reportUserDeptDao;

	@Override
	public List<UserDept> getUserDeptList(String personId) throws Exception {
		return reportUserDeptDao.getUserDeptList(personId);
	}

	public List<UserDept> getAllDept() throws Exception {
		return reportUserDeptDao.getAllDept();
	}

	public Set<String> getAllCategoryByPersonId(String personId) throws Exception {
		List<UserDept> userDeptList = reportUserDeptDao.getUserDeptList(personId);
		List<UserDept> allDeptList = reportUserDeptDao.getAllDept();
		Map<Integer, UserDept> allDeptMap = allDeptList.stream()
				.collect(Collectors.toMap(UserDept::getDeptId, Function.identity(), (key1, key2) -> key2));
		Set<String> result = new HashSet<String>();
		for (UserDept userDept : userDeptList) {
			if (userDept.getDeptId().intValue() == 0) {
				result.clear();// 清空
				result = this.getAllCategory(allDeptList);
				break;
			} else {
				result.add(allDeptMap.get(userDept.getDeptId()).getCategory());
			}
		}
		return result;
	}

	public List<UserDept> getUserDeptByCategory(String personId, String category) throws Exception {
		List<UserDept> userDeptList = reportUserDeptDao.getUserDeptList(personId);
		List<UserDept> allDeptList = reportUserDeptDao.getAllDept();
		List<UserDept> result = new ArrayList<UserDept>();
		Map<Integer, UserDept> allDeptMap = allDeptList.stream()
				.collect(Collectors.toMap(UserDept::getDeptId, Function.identity(), (key1, key2) -> key2));
		for (UserDept userDept : userDeptList) {
			if (userDept.getDeptId().intValue() == 0) {
				result.clear();// 清空
				result = this.allDeptFilter(allDeptList, category);
				break;
			} else {
				if (category.equals(allDeptMap.get(userDept.getDeptId()).getCategory())) {
					result.add(allDeptMap.get(userDept.getDeptId()));
				}
			}
		}
		return result;
	}

	private Set<String> getAllCategory(List<UserDept> allDeptList) throws Exception {
		Set<String> result = new HashSet<String>();
		for (UserDept userDept : allDeptList) {
			result.add(userDept.getCategory());
		}
		return result;
	}

	private List<UserDept> allDeptFilter(List<UserDept> allDeptList, String category) throws Exception {
		List<UserDept> result = new ArrayList<UserDept>();
		for (UserDept userDept : allDeptList) {
			if (category.equals(userDept.getCategory())) {
				result.add(userDept);
			}
		}
		return result;
	}

}
