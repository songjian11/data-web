package com.cs.mobile.api.dao.reportPage;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.reportPage.UserDept;

/**
 * 用户报表大类DAO
 * 
 * @author wells
 * @date 2019年6月5日
 */
@Repository
public class ReportUserDeptDao extends AbstractDao {
	private static final RowMapper<UserDept> USER_DEPT_RM = new BeanPropertyRowMapper<UserDept>(UserDept.class);

	private static final String GET_USER_DEPT_LIST = "select * from  csmb_report_user_dept where person_id=? ";

	private static final String GET_ALL_DEPT = "select dept as dept_id,dept_name,new_division_name as category,ddiv_name as p_category from csmb_code_dept where dept not in(9101,9103)";

	/**
	 * 查找用户大类
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年6月6日
	 */
	public List<UserDept> getUserDeptList(String personId) throws Exception {
		return super.queryForList(GET_USER_DEPT_LIST, USER_DEPT_RM, personId);
	}

	/**
	 * 查找所有大类及品类信息
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年6月6日
	 */
	public List<UserDept> getAllDept() throws Exception {
		return super.queryForList(GET_ALL_DEPT, USER_DEPT_RM);
	}

}
