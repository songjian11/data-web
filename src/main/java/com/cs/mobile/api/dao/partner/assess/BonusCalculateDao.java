package com.cs.mobile.api.dao.partner.assess;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.goal.GoalValue;
import com.cs.mobile.api.model.partner.PartnerPerson;
import com.cs.mobile.api.model.partner.assess.AssessMergeCnf;
import com.cs.mobile.api.model.partner.assess.AssessResult;
import com.cs.mobile.api.model.partner.assess.ComBonus;
import com.cs.mobile.api.model.partner.assess.PersonBonus;
import com.cs.mobile.common.utils.DateUtil;

/**
 * 奖金计算DAO
 * 
 * @author wells
 * @date 2019年4月1日
 */
@Repository
public class BonusCalculateDao extends AbstractDao {
	private static final RowMapper<ComBonus> COM_BONUS_RM = new BeanPropertyRowMapper<>(ComBonus.class);
	private static final RowMapper<AssessMergeCnf> ASSESS_MERGER_CNF_RM = new BeanPropertyRowMapper<>(
			AssessMergeCnf.class);
	private static final RowMapper<GoalValue> GOAL_VALUE_RM = new BeanPropertyRowMapper<>(GoalValue.class);
	private static final RowMapper<AssessResult> ASSESS_RESULT_RM = new BeanPropertyRowMapper<>(AssessResult.class);
	private static final RowMapper<PartnerPerson> PARTNER_PERSON_RM = new BeanPropertyRowMapper<>(PartnerPerson.class);
	// 查询上个月小店奖金表数据状态为初始化完成的列表
	private static final String GET_WAIT_DEAL_BONUS_LIST = "SELECT * FROM CSMB_COM_BONUS WHERE DATA_STATUS=0 AND YM=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm')";
	// 查询上个月是否合并了考核结果
	private static final String GET_ASSESS_MERGER_CNF = "SELECT * FROM CSMB_ASSESS_MERGE_CNF WHERE MAIN_YM=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm')";
	// 查询上个月小店各项考核数据
	private static final String GET_LAST_MONTH_ASSESS_RESULT = "SELECT CAR.RESULT_YM,CAR.STORE_ID,CC.COM_ID,CC.COM_NAME,CAR.SUBJECT,SUM(CAR.SUB_VALUES) AS SUB_VALUES "
			+ "FROM CSMB_ASSESS_RESULT CAR RIGHT JOIN CSMB_COMPANY CC "
			+ "ON CAR.STORE_ID=CC.STORE_ID AND CAR.DEPT_ID=CC.DEPT_ID "
			+ "WHERE CAR.RESULT_YM=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') "
			+ "GROUP BY CAR.RESULT_YM,CAR.STORE_ID,CC.COM_ID,CC.COM_NAME,CAR.SUBJECT";

	// 查询合伙人体制内人员
	private static final String GET_PARTNER_PERSON = "SELECT * FROM CSMB_PERSON";

	private static final String GET_TASK_COUNT = "SELECT COUNT(1) FROM CSMB_BONUS_DATA_TASK WHERE STATUS=1";

	/**
	 * 查询上个月小店奖金表数据状态为初始化完成的列表
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public List<ComBonus> getWaitDealBonusList() throws Exception {
		return super.queryForList(GET_WAIT_DEAL_BONUS_LIST, COM_BONUS_RM);
	}

	/**
	 * 更新小店奖金表
	 * 
	 * @param comBonus
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int updateComBonus(ComBonus comBonus) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(
				"UPDATE CSMB_COM_BONUS SET DATA_STATUS=?,AUDIT_STATUS=?,MANAGER_ID=?,MANAGER_NAME=?,GOAL_PROFIT=?,ACTUAL_PROFIT=?,SALE=?,"
						+ "ATTRACT=?,COST=?,FRONT_GP=?,UPDATE_TIME=sysdate "
						+ "WHERE STORE_ID=? AND COM_ID=? AND YM=?");
		Object[] args = { comBonus.getDataStatus(), comBonus.getAuditStatus(), comBonus.getManagerId(),
				comBonus.getManagerName(), comBonus.getGoalProfit(), comBonus.getActualProfit(), comBonus.getSale(),
				comBonus.getAttract(), comBonus.getCost(), comBonus.getFrontGp(), comBonus.getStoreId(),
				comBonus.getComId(), comBonus.getYm() };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 批量更新小店奖金表
	 * 
	 * @param comBonusList
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月4日
	 */
	public void batchUpdateComBonus(List<ComBonus> comBonusList) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(
				"UPDATE CSMB_COM_BONUS SET DATA_STATUS=?,AUDIT_STATUS=?,MANAGER_ID=?,MANAGER_NAME=?,GOAL_PROFIT=?,ACTUAL_PROFIT=?,SALE=?,GOAL_SALE=?,"
						+ "ATTRACT=?,COST=?,FRONT_GP=?,UPDATE_TIME=sysdate "
						+ "WHERE STORE_ID=? AND COM_ID=? AND YM=?");
		jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, comBonusList.get(i).getDataStatus());
				ps.setInt(2, comBonusList.get(i).getAuditStatus());
				ps.setString(3, comBonusList.get(i).getManagerId());
				ps.setString(4, comBonusList.get(i).getManagerName());
				ps.setBigDecimal(5, comBonusList.get(i).getGoalProfit());
				ps.setBigDecimal(6, comBonusList.get(i).getActualProfit());
				ps.setBigDecimal(7, comBonusList.get(i).getSale());
				ps.setBigDecimal(8, comBonusList.get(i).getGoalSale());
				ps.setBigDecimal(9, comBonusList.get(i).getAttract());
				ps.setBigDecimal(10, comBonusList.get(i).getCost());
				ps.setBigDecimal(11, comBonusList.get(i).getFrontGp());
				ps.setInt(12, Integer.parseInt(comBonusList.get(i).getStoreId()));
				ps.setString(13, comBonusList.get(i).getComId());
				ps.setString(14, comBonusList.get(i).getYm());
			}

			@Override
			public int getBatchSize() {
				return comBonusList.size();
			}

		});
	}

	/**
	 * 查询上个月是否合并了考核结果
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public List<AssessMergeCnf> getAssessMergerCnf() throws Exception {
		return super.queryForList(GET_ASSESS_MERGER_CNF, ASSESS_MERGER_CNF_RM);
	}

	/**
	 * 查询上个月小店各项目标数据，如果是合并的需要传入多个年月
	 * 
	 * @param ymStr
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public List<GoalValue> getLastMonthGoal(List<AssessMergeCnf> assessMergeCnfList) throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		sql.append("SELECT CG.STORE_ID,CC.COM_ID,CG.SUBJECT,SUM(CG.SUB_VALUES) AS SUB_VALUES FROM CSMB_GOAL CG "
				+ "RIGHT JOIN CSMB_COMPANY CC ON CG.STORE_ID=CC.STORE_ID AND CG.DEPT_ID=CC.DEPT_ID ");
		sql.append("WHERE 1=1 ");
		String defaultYm = DateUtil.getLastMonth();
		StringBuffer provinceStr = new StringBuffer();
		if (assessMergeCnfList != null && assessMergeCnfList.size() > 0) {
			sql.append(" AND (");
			for (int i = 0; i < assessMergeCnfList.size(); i++) {
				AssessMergeCnf assessMergeCnf = assessMergeCnfList.get(i);
				provinceStr.append(assessMergeCnf.getProvinceId());
				String ymStr = assessMergeCnf.getMergeYm();
				String[] ymArray = ymStr.split(",");
				sql.append(" (S.PROVINCE_ID=? AND ( ");
				sql.append(assessMergeCnf.getProvinceId());
				for (int j = 0; j < ymArray.length; j++) {
					String ym = ymArray[j];
					sql.append(" CG.GOAL_YM =? ");
					params.add(ym);
					if (j != ymArray.length - 1) {
						sql.append(" OR ");
					}
				}
				sql.append(" )) ");
				if (i != assessMergeCnfList.size() - 1) {
					sql.append(" OR ");
					provinceStr.append(",");
				}
			}
			sql.append(" OR (S.PROVINCE_ID NOT IN( ");
			sql.append(provinceStr.toString());
			sql.append(" ) AND CG.GOAL_YM =? ) ");
			params.add(defaultYm);
			sql.append(" ) ");
		} else {
			sql.append(" AND CG.GOAL_YM =? ");
			params.add(defaultYm);
		}
		sql.append(" GROUP BY CG.STORE_ID,CC.COM_ID,CG.SUBJECT ");
		return super.queryForList(sql.toString(), GOAL_VALUE_RM, params.toArray());
	}

	/**
	 * 查询上个月小店各项考核数据
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public List<AssessResult> getLastMonthAssessResult() throws Exception {
		return super.queryForList(GET_LAST_MONTH_ASSESS_RESULT, ASSESS_RESULT_RM);
	}

	/**
	 * 查询合伙人体制内人员
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public List<PartnerPerson> getPartnerPerson() throws Exception {
		return super.queryForList(GET_PARTNER_PERSON, PARTNER_PERSON_RM);
	}

	/**
	 * 查询当前正在执行的任务数量
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int getTaskCount() throws Exception {
		return jdbcTemplate.queryForObject(GET_TASK_COUNT, Integer.class);
	}

	/**
	 * 插入新任务
	 * 
	 * @param batchNo
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int addTask(String batchNo) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO CSMB_BONUS_DATA_TASK(BATCH_NO,STATUS,CREATE_TIME)VALUES(?,1,sysdate)");
		Object[] args = { batchNo };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 更新任务
	 * 
	 * @param batchNo
	 * @param status
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int updateTask(int status) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CSMB_BONUS_DATA_TASK SET STATUS=?,UPDATE_TIME=sysdate WHERE STATUS=1");
		Object[] args = { status };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 添加人员奖金
	 * 
	 * @param personBonus
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月4日
	 */
	public int addPersonBonus(PersonBonus personBonus) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO CSMB_PERSON_BONUS(YM,PERSON_ID,PERSON_NAME,STORE_ID,STORE_NAME,COM_ID,"
				+ "COM_NAME,POSITION_ID,BEFOR_MONEY,AFTER_MONEY,STATUS) VALUES(?,?,?,?,?,?,?,?,?,?,0)");
		Object[] args = { personBonus.getYm(), personBonus.getPersonId(), personBonus.getPersonName(),
				personBonus.getStoreId(), personBonus.getStoreName(), personBonus.getComId(), personBonus.getComName(),
				personBonus.getPositionId(), personBonus.getBeforMoney(), personBonus.getAfterMoney() };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 批量添加人员奖金
	 * 
	 * @param personBonusList
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月4日
	 */
	public void batchAddPersonBonus(List<PersonBonus> personBonusList) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO CSMB_PERSON_BONUS(YM,PERSON_ID,PERSON_NAME,STORE_ID,STORE_NAME,COM_ID,"
				+ "COM_NAME,POSITION_ID,BEFOR_MONEY,AFTER_MONEY,STATUS) VALUES(?,?,?,?,?,?,?,?,?,?,0)");
		jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, personBonusList.get(i).getYm());
				ps.setString(2, personBonusList.get(i).getPersonId());
				ps.setString(3, personBonusList.get(i).getPersonName());
				ps.setString(4, personBonusList.get(i).getStoreId());
				ps.setString(5, personBonusList.get(i).getStoreName());
				ps.setString(6, personBonusList.get(i).getComId());
				ps.setString(7, personBonusList.get(i).getComName());
				ps.setInt(8, Integer.parseInt(personBonusList.get(i).getPositionId()));
				ps.setBigDecimal(9, personBonusList.get(i).getBeforMoney());
				ps.setBigDecimal(10, personBonusList.get(i).getAfterMoney());
			}

			@Override
			public int getBatchSize() {
				return personBonusList.size();
			}

		});
	}
}
