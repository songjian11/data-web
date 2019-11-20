package com.cs.mobile.api.dao.user;

import java.util.List;

import com.cs.mobile.common.utils.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.PartnerUserInfo;
import com.cs.mobile.api.model.user.PersonLog;
import com.cs.mobile.api.model.user.User;

@Repository
public class UserDao extends AbstractDao {
	private static final RowMapper<User> USER_RM = new BeanPropertyRowMapper<>(User.class);
	private static final RowMapper<PartnerUserInfo> USERINFO_RM = new BeanPropertyRowMapper<>(PartnerUserInfo.class);
	private static final RowMapper<PersonLog> PERSONLOG_RM = new BeanPropertyRowMapper<>(PersonLog.class);
	private static final String GET_USER_LIST_BY_PERSONID = "select person_Id,type,name,org_id from CSMB_USER where person_id=? group by person_Id,type,name,org_id";
	// 大店及小店店长获取用户信息
	private static final String GET_USERINFO_BY_PERSONID = "select pe.PERSON_ID,sg.GROUP_ID,sg.GROUP_NAME, "
			+ "s.store_id,s.store_name,pe.COM_ID,pe.COM_NAME,pe.NAME,s.is_compare from CSMB_PERSON pe "
			+ "left join CSMB_STORE_GROUP sg on pe.STORE_ID=sg.STORE_ID left join CSMB_STORE s "
			+ "on s.store_id=pe.store_id where pe.PERSON_ID=?";
	// 管理员大店用户信息
	private static final String GET_USERINFO_S_BY_PERSONID = "select DISTINCT(us.person_id),s.store_id,s.store_name,us.name "
			+ ",sg.GROUP_ID,sg.GROUP_NAME from csmb_user us  left join CSMB_STORE s "
			+ "on us.org_id=s.store_id  left join CSMB_STORE_GROUP sg  on us.org_id=sg.STORE_ID "
			+ "where us.person_id=? and us.type=? ";
	// 区域管理员（即区域身份）获取用户信息
	private static final String GET_A_USERINFO_BY_PERSONID = "select DISTINCT(us.person_id),s.area_id,s.area_name,us.name from csmb_user us "
			+ "left join CSMB_STORE s on us.org_id=s.area_id where us.person_id=? and us.type=? ";
	// 省份管理员（即省份身份）获取用户信息
	private static final String GET_P_USERINFO_BY_PERSONID = "select DISTINCT(us.person_id),s.province_id,s.province_name,us.name from csmb_user us "
			+ "left join CSMB_STORE s on us.org_id=s.province_id where us.person_id=? and us.type=? ";

	private static final String UPDATE_PASSWORD = "update CSMB_USER set password=? where person_id=?";

	private static final String INSERT_PERSONID_LOG = "insert into CSMB_PERSON_LOG values(?,sysdate,?,'')";

	private static final String GET_USER_ORG_LIST_BY_PERSONID = "select * from CSMB_USER where person_id=? and type=? ";

	private static final String IS_STOREMANAGER_PREFIX = "select count(a.emp_code) " +
			"  from csmb_ssoa_person a " +
			" where 1=1 " +
			" and a.store_name like '%店长%' ";

	public Integer isStoreManager(String personId){
		StringBuilder sb = new StringBuilder(IS_STOREMANAGER_PREFIX);
		sb.append(" and a.emp_code='").append(personId).append("' ");
		return jdbcTemplate.queryForObject(sb.toString(), null, Integer.class);
	}

	public List<User> getUserListByPersonId(String personId) throws Exception {
		return super.queryForList(GET_USER_LIST_BY_PERSONID, USER_RM, personId);
	}

	public List<User> getUserOrgListByPersonId(String personId, int type) throws Exception {
		return super.queryForList(GET_USER_ORG_LIST_BY_PERSONID, USER_RM, personId, type);
	}

	/**
	 * 大店及小店店长获取用户信息
	 * 
	 * @author wells
	 * @param personId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public PartnerUserInfo getUserInfoByPersonId(String personId) throws Exception {
		return super.queryForObject(GET_USERINFO_BY_PERSONID, USERINFO_RM, personId);
	}

	/**
	 * 区域管理员（即区域身份）获取用户信息
	 * 
	 * @author wells
	 * @param personId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public PartnerUserInfo getAUserInfoByPersonId(String personId, int type) throws Exception {
		return super.queryForObject(GET_A_USERINFO_BY_PERSONID, USERINFO_RM, personId, type);
	}

	/**
	 * 省份管理员（即省份身份）获取用户信息
	 * 
	 * @author wells
	 * @param personId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public PartnerUserInfo getPUserInfoByPersonId(String personId, int type) throws Exception {
		return super.queryForObject(GET_P_USERINFO_BY_PERSONID, USERINFO_RM, personId, type);
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
		return super.queryForObject(GET_USERINFO_S_BY_PERSONID, USERINFO_RM, personId, type);
	}

	public void modifyPassword(String personId, String password) throws Exception {
		super.jdbcTemplate.update(UPDATE_PASSWORD, password, personId);
	}

	public void addPersonLog(String personId, String logPage) throws Exception {
		super.jdbcTemplate.update(INSERT_PERSONID_LOG, personId, logPage);
	}
}
