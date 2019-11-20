package com.cs.mobile.api.dao.partner.assess;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.assess.AssessResult;

/**
 * 区域下门店考核表数据
 * 
 * @author wells
 * @date 2019年1月9日
 */
@Repository
public class StoreAssessDao extends AbstractDao {

	private static final RowMapper<AssessResult> ASSESS_RESULT_RM = new BeanPropertyRowMapper<>(AssessResult.class);

	// 截止到上个月的累计一年的历史数据
	private static final String GET_LAST_YEAR_RESULT = "select * from CSMB_RESULT result "
			+ "left join CSMB_STORE store on result.STORE_ID=store.STORE_ID "
			+ "where result.RESULT_YM>=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "AND result.RESULT_YM<=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm')  and store.AREA_ID=? ";

	// 某个月的历史数据
	private static final String GET_RESULT_BY_YM = "select * from CSMB_RESULT result "
			+ "left join CSMB_STORE store on result.STORE_ID=store.STORE_ID "
			+ "where result.RESULT_YM=? and store.AREA_ID=? ";

	/**
	 * 截止到上个月的累计一年的历史数据
	 * 
	 * @author wells
	 * @param areaId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public List<AssessResult> getLastYearResult(String areaId) throws Exception {
		return super.queryForList(GET_LAST_YEAR_RESULT, ASSESS_RESULT_RM, areaId);
	}

	/**
	 * 某个月的历史数据
	 * 
	 * @author wells
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public List<AssessResult> getResultByYm(String areaId, String ym) throws Exception {
		return super.queryForList(GET_RESULT_BY_YM, ASSESS_RESULT_RM, ym, areaId);
	}
}
