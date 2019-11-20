package com.cs.mobile.api.dao.partner.assess;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.partner.assess.AssessResult;

/**
 * 全司下所有身份考核数据
 * 
 * @author wells
 * @date 2019年1月9日
 */
@Repository
public class ProvinceAssessDao extends AbstractDao {

	private static final RowMapper<AssessResult> ASSESS_RESULT_RM = new BeanPropertyRowMapper<>(AssessResult.class);

	// 截止到上个月的累计一年的历史数据
	private static final String GET_LAST_YEAR_RESULT = "select * from CSMB_RESULT result "
			+ "left join CSMB_STORE store on result.STORE_ID=store.STORE_ID "
			+ "where result.RESULT_YM>=to_char(ADD_MONTHS(sysdate, -12),'yyyy-mm') "
			+ "AND result.RESULT_YM<=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm') ";

	// 某个月的历史数据
	private static final String GET_RESULT_BY_YM = "select * from CSMB_RESULT result "
			+ "left join CSMB_STORE store on result.STORE_ID=store.STORE_ID where result.RESULT_YM=? ";

	/**
	 * 截止到上个月的累计一年的历史数据
	 * 
	 * @author wells
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public List<AssessResult> getLastYearResult() throws Exception {
		return super.queryForList(GET_LAST_YEAR_RESULT, ASSESS_RESULT_RM);
	}

	/**
	 * 某个月的历史数据
	 * 
	 * @author wells
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public List<AssessResult> getResultByYm(String ym) throws Exception {
		return super.queryForList(GET_RESULT_BY_YM, ASSESS_RESULT_RM, ym);
	}
}
