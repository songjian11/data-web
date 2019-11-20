package com.cs.mobile.api.dao.partner.assess;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.goal.GoalValue;
import com.cs.mobile.api.model.partner.assess.AssessMergeCnf;
import com.cs.mobile.api.model.partner.assess.AssessResult;
import com.cs.mobile.api.model.partner.assess.BonusReport;
import com.github.pagehelper.util.StringUtil;

/**
 * 奖金报表DAO
 * 
 * @author wells.wong
 * @date 2019年7月9日
 *
 */
@Repository
public class BonusReportDao extends AbstractDao {
	private static final RowMapper<BonusReport> BONUS_REPORT_RM = new BeanPropertyRowMapper<>(BonusReport.class);
	private static final RowMapper<AssessMergeCnf> ASSESS_MERGER_CNF_RM = new BeanPropertyRowMapper<>(
			AssessMergeCnf.class);
	private static final RowMapper<GoalValue> GOAL_VALUE_RM = new BeanPropertyRowMapper<>(GoalValue.class);
	private static final RowMapper<AssessResult> ASSESS_RESULT_RM = new BeanPropertyRowMapper<>(AssessResult.class);
	// 查询上个月是否合并了考核结果
	private static final String GET_ASSESS_MERGER_CNF = "SELECT * FROM CSMB_ASSESS_MERGE_CNF WHERE MAIN_YM=?";

	/**
	 * 查询指定月是否合并了考核结果
	 *
	 * @param ym
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月9日
	 *
	 */
	public List<AssessMergeCnf> getAssessMergerCnfByYm(String ym) throws Exception {
		return super.queryForList(GET_ASSESS_MERGER_CNF, ASSESS_MERGER_CNF_RM, ym);
	}

	/**
	 * 查询指定月份日期的奖金报表数据
	 *
	 * @param ym
	 * @param provinceId
	 * @param areaId
	 * @param storeId
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月9日
	 *
	 */
	public List<BonusReport> getBonusReportByYmd(String ym, String provinceId, String areaId, String storeId)
			throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		sql.append("select s.province_id,s.province_name,s.area_id,s.area_name, "
				+ "b.store_id,b.store_name,b.com_id,b.com_name,b.bonus from csmb_com_bonus b "
				+ "left join csmb_store s on b.store_Id=s.store_id where ym=? ");
		params.add(ym);
		if (StringUtil.isNotEmpty(provinceId)) {
			sql.append(" AND  s.province_id=?");
			params.add(provinceId);
		}
		if (StringUtil.isNotEmpty(areaId)) {
			sql.append(" AND  s.area_id=?");
			params.add(areaId);
		}
		if (StringUtil.isNotEmpty(storeId)) {
			sql.append(" AND  s.store_Id=?");
			params.add(storeId);
		}
		return super.queryForList(sql.toString(), BONUS_REPORT_RM, params.toArray());
	}

	/**
	 * 查询指定月份小店各项目标数据，如果是合并的需要传入多个年月
	 *
	 * @param assessMergeCnfList
	 * @param mainYm
	 * @param provinceId
	 * @param areaId
	 * @param storeId
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月9日
	 *
	 */
	public List<GoalValue> getGoalByYmd(List<AssessMergeCnf> assessMergeCnfList, String mainYm, String provinceId,
			String areaId, String storeId) throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		sql.append("SELECT CG.STORE_ID,CC.COM_ID,CG.SUBJECT,SUM(CG.SUB_VALUES) AS SUB_VALUES FROM CSMB_GOAL CG "
				+ "RIGHT JOIN CSMB_COMPANY CC ON CG.STORE_ID=CC.STORE_ID AND CG.DEPT_ID=CC.DEPT_ID "
				+ "LEFT JOIN CSMB_STORE S ON S.STORE_ID=CG.STORE_ID ");
		sql.append("WHERE 1=1 ");
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
			params.add(mainYm);
			sql.append(" ) ");
		} else {
			sql.append(" AND CG.GOAL_YM =? ");
			params.add(mainYm);
		}
		if (StringUtil.isNotEmpty(provinceId)) {
			sql.append(" AND  s.province_id=?");
			params.add(provinceId);
		}
		if (StringUtil.isNotEmpty(areaId)) {
			sql.append(" AND  s.area_id=?");
			params.add(areaId);
		}
		if (StringUtil.isNotEmpty(storeId)) {
			sql.append(" AND  s.store_Id=?");
			params.add(storeId);
		}
		sql.append(" GROUP BY CG.STORE_ID,CC.COM_ID,CG.SUBJECT ");
		return super.queryForList(sql.toString(), GOAL_VALUE_RM, params.toArray());
	}

	/**
	 * 查询指定月份小店各项考核数据
	 *
	 * @param ym
	 * @param provinceId
	 * @param areaId
	 * @param storeId
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月9日
	 *
	 */
	public List<AssessResult> getAssessResultByYmd(String ym, String provinceId, String areaId, String storeId)
			throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		sql.append(
				"SELECT CAR.RESULT_YM,CAR.STORE_ID,CC.COM_ID,CC.COM_NAME,CAR.SUBJECT,SUM(CAR.SUB_VALUES) AS SUB_VALUES "
						+ "FROM CSMB_ASSESS_RESULT CAR RIGHT JOIN CSMB_COMPANY CC "
						+ "ON CAR.STORE_ID=CC.STORE_ID AND CAR.DEPT_ID=CC.DEPT_ID LEFT JOIN CSMB_STORE S "
						+ "ON S.STORE_ID=CAR.STORE_ID WHERE CAR.RESULT_YM=? ");
		params.add(ym);
		if (StringUtil.isNotEmpty(provinceId)) {
			sql.append(" AND  s.province_id=?");
			params.add(provinceId);
		}
		if (StringUtil.isNotEmpty(areaId)) {
			sql.append(" AND  s.area_id=?");
			params.add(areaId);
		}
		if (StringUtil.isNotEmpty(storeId)) {
			sql.append(" AND  s.store_Id=?");
			params.add(storeId);
		}
		sql.append(" GROUP BY CAR.RESULT_YM,CAR.STORE_ID,CC.COM_ID,CC.COM_NAME,CAR.SUBJECT ");
		return super.queryForList(sql.toString(), ASSESS_RESULT_RM, params.toArray());
	}

}
