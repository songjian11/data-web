package com.cs.mobile.api.dao.towerteam;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.towerteam.TowerTeamConfig;
import com.cs.mobile.api.model.towerteam.TowerTeamResult;
import com.cs.mobile.api.model.towerteam.response.TowerTeamAreaResp;
import com.cs.mobile.api.model.towerteam.response.TowerTeamPositionResp;
import com.cs.mobile.api.model.towerteam.response.TowerTeamResultResp;
import com.cs.mobile.api.model.towerteam.response.TowerTeamStoreResp;

@Repository
public class TowerTeamDao extends AbstractDao {
	private static final RowMapper<TowerTeamAreaResp> TT_AREA_RM = new BeanPropertyRowMapper<>(TowerTeamAreaResp.class);
	private static final RowMapper<TowerTeamResultResp> TT_RESULT_RESP_RM = new BeanPropertyRowMapper<>(
			TowerTeamResultResp.class);
	private static final RowMapper<TowerTeamConfig> TT_CONFIG_RM = new BeanPropertyRowMapper<>(TowerTeamConfig.class);
	private static final RowMapper<TowerTeamPositionResp> TT_POSITION_RESP_RM = new BeanPropertyRowMapper<>(
			TowerTeamPositionResp.class);
	// 获取所有省份
	private static final String GET_ALL_PROVINCE = "select PROVINCE_NAME AS NAME ,PROVINCE_ID AS CODE "
			+ " FROM CSMB_TOWER_TEAM_CONFIG  group by PROVINCE_NAME ,PROVINCE_ID";
	// 根据省份获取所有大区
	private static final String GET_AREAGROUP_BY_P = "SELECT AREA_GROUP_NAME AS NAME,AREA_GROUP_ID AS CODE "
			+ " FROM CSMB_TOWER_TEAM_CONFIG WHERE PROVINCE_ID=? GROUP BY AREA_GROUP_NAME,AREA_GROUP_ID";

	// 根据大区获取所有区域
	private static final String GET_AREA_BY_G = "SELECT AREA_NAME AS NAME,AREA_ID AS CODE FROM CSMB_TOWER_TEAM_CONFIG "
			+ " WHERE AREA_GROUP_ID=? GROUP BY AREA_NAME,AREA_ID";

	// 获取个人报名成功的信息
	private static final String GET_PERSON_RESULT = "SELECT R.*,C.PROVINCE_NAME,C.AREA_GROUP_NAME,C.STORE_NAME,C.POSITION_NAME ,C.AREA_NAME "
			+ " FROM CSMB_TOWER_TEAM_RESULT R "
			+ " LEFT JOIN CSMB_TOWER_TEAM_CONFIG C ON R.STORE_ID=C.STORE_ID AND R.POSITION_ID=C.POSITION_ID "
			+ " WHERE PERSON_ID=? AND STATUS=1";

	// 获取某个门店某个岗位的配置信息
	private static final String GET_ONE_CONFIG_BY_SP = "SELECT * FROM CSMB_TOWER_TEAM_CONFIG WHERE  STORE_ID=? AND POSITION_ID=?";

	/**
	 * 获取所有区域
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<TowerTeamAreaResp> getAllProvince() throws Exception {
		return super.queryForList(GET_ALL_PROVINCE, TT_AREA_RM);
	}

	/**
	 * 根据省份获取所有大区
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<TowerTeamAreaResp> getAreaGroupByP(String provinceId) throws Exception {
		return super.queryForList(GET_AREAGROUP_BY_P, TT_AREA_RM, provinceId);
	}

	/**
	 * 根据大区获取所有区域
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<TowerTeamAreaResp> getAreaByG(String areaGroupId) throws Exception {
		return super.queryForList(GET_AREA_BY_G, TT_AREA_RM, areaGroupId);
	}

	/**
	 * 根据区域信息分页获取报名门店列表
	 * 
	 * @param provinceId
	 * @param areaGroupId
	 * @param areaId
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public PageResult<TowerTeamStoreResp> getConfigByPga(String provinceId, String areaGroupId, String areaId, int page,
			int pageSize) throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		sql.append("SELECT STORE_ID,STORE_NAME FROM CSMB_TOWER_TEAM_CONFIG WHERE 1=1 ");
		if (StringUtils.isNotEmpty(provinceId) && !"0".equals(provinceId)) {
			sql.append(" AND PROVINCE_ID=? ");
			params.add(provinceId);
		}
		if (StringUtils.isNotEmpty(areaGroupId) && !"0".equals(areaGroupId)) {
			sql.append(" AND AREA_GROUP_ID=? ");
			params.add(areaGroupId);
		}
		if (StringUtils.isNotEmpty(areaId) && !"0".equals(areaId)) {
			sql.append(" AND AREA_ID=? ");
			params.add(areaId);
		}
		sql.append(" GROUP BY STORE_ID,STORE_NAME");
		PageResult<TowerTeamStoreResp> storePage = super.queryByPage(sql.toString(), TowerTeamStoreResp.class, page,
				pageSize, "STORE_ID", params.toArray());
		if (storePage != null && storePage.getDatas().size() > 0) {
			List<TowerTeamStoreResp> storeList = storePage.getDatas();
			for (TowerTeamStoreResp towerTeamStoreResp : storeList) {
				towerTeamStoreResp.setPositionResp(this.getPositionRespByStoreId(towerTeamStoreResp.getStoreId()));
			}
		}
		return storePage;
	}

	/**
	 * 根据门店获取岗位列表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月28日
	 */
	private List<TowerTeamPositionResp> getPositionRespByStoreId(String storeId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(
				"SELECT POSITION_ID,POSITION_NAME,PERSON_TOTAL,AVAILABLE FROM CSMB_TOWER_TEAM_CONFIG WHERE STORE_ID=? ");
		return super.queryForList(sql.toString(), TT_POSITION_RESP_RM, storeId);
	}

	/**
	 * 保存报名信息
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public int saveResult(TowerTeamResult towerTeamResult) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(
				"INSERT INTO CSMB_TOWER_TEAM_RESULT(PERSON_ID,NAME,PHONE,STORE_ID,GENDER,POSITION_ID,STATUS,CREATE_TIME,UPDATE_TIME)"
						+ "VALUES(?,?,?,?,?,?,?,?,?)");
		Object[] args = { towerTeamResult.getPersonId(), towerTeamResult.getName(), towerTeamResult.getPhone(),
				towerTeamResult.getStoreId(), towerTeamResult.getGender(), towerTeamResult.getPositionId(),
				towerTeamResult.getStatus(), towerTeamResult.getCreateTime(), towerTeamResult.getUpdateTime() };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 更新可用数量
	 * 
	 * @param count
	 * @param storeId
	 * @param positionId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public int updateAvailable(int count, String storeId, String positionId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CSMB_TOWER_TEAM_CONFIG SET AVAILABLE=AVAILABLE+? WHERE STORE_ID=? AND POSITION_ID=?");
		Object[] args = { count, storeId, positionId };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 取消报名
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public int cancel(String personId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CSMB_TOWER_TEAM_RESULT SET STATUS=0 , UPDATE_TIME=SYSDATE WHERE PERSON_ID=? AND STATUS=1");
		Object[] args = { personId };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 获取个人报名成功的信息
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public TowerTeamResultResp getPersonResult(String personId) throws Exception {
		return super.queryForObject(GET_PERSON_RESULT, TT_RESULT_RESP_RM, personId);
	}

	/**
	 * 获取某个门店某个岗位的配置信息
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public TowerTeamConfig getOneConfigBySp(String storeId, String positionId) throws Exception {
		return super.queryForObject(GET_ONE_CONFIG_BY_SP, TT_CONFIG_RM, storeId, positionId);
	}
}
