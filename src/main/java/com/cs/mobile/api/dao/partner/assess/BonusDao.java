package com.cs.mobile.api.dao.partner.assess;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.partner.assess.ComBonus;
import com.cs.mobile.api.model.partner.assess.PersonBonusExt;
import com.cs.mobile.api.model.partner.assess.response.BonusHistoryResp;
import com.cs.mobile.api.model.partner.assess.response.PersonBonusItemResp;

/**
 * 奖金DAO
 * 
 * @author wells
 * @date 2019年4月1日
 */
@Repository
public class BonusDao extends AbstractDao {
	private static final RowMapper<ComBonus> COM_BONUS_RM = new BeanPropertyRowMapper<>(ComBonus.class);
	/** 人员奖金明细包含小店奖金信息 **/
	private static final RowMapper<PersonBonusExt> PERSON_BONUS_EXT_RM = new BeanPropertyRowMapper<>(
			PersonBonusExt.class);
	/** 人员奖金明细 **/
	private static final RowMapper<PersonBonusItemResp> PERSON_BONUS_RM = new BeanPropertyRowMapper<>(
			PersonBonusItemResp.class);

	// 获取某个小店下人员奖金情况（不包括大店店长）
	private static final String GET_PERSON_BONUS_BY_NO = "select * from CSMB_PERSON_BONUS WHERE STORE_ID=? AND COM_ID=? AND POSITION_ID<>3 ";

	// 小店店长奖金分配[小店上个月人员奖金情况]
	private static final String GET_LAST_MONTH_COM_PERSON_BONUS = "select * from CSMB_COM_BONUS CB "
			+ "LEFT JOIN CSMB_PERSON_BONUS PB ON CB.STORE_ID=PB.STORE_ID AND CB.COM_ID=PB.COM_ID "
			+ "WHERE CB.STORE_ID=? AND CB.COM_ID=?  and CB.ym=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm')";

	// 获取上个月门店下所有小店包含后勤小店的奖金明细
	private static final String GET_LAST_MONTH_COM_BONUS = "select * from CSMB_COM_BONUS CB "
			+ "RIGHT JOIN CSMB_PERSON_BONUS PB ON CB.STORE_ID=PB.STORE_ID AND CB.COM_ID=PB.COM_ID "
			+ "WHERE CB.STORE_ID=?  and CB.ym=to_char(ADD_MONTHS(sysdate, -1),'yyyy-mm')";

	// 获取小店奖金数据
	private static final String GET_COM_BONUS_BY_COMID = "select * from CSMB_COM_BONUS where com_id=? "
			+ "and store_id=? and ym=? ";
	// 获取大店奖金数据
	private static final String GET_COM_BONUS_BY_STOREID = "select sum(GOAL_PROFIT) as GOAL_PROFIT,sum(ACTUAL_PROFIT) as ACTUAL_PROFIT, "
			+ "sum(GOAL_SALE) as GOAL_SALE,sum(SALE) as SALE,sum(ATTRACT) as ATTRACT,sum(COST) as COST,sum(FRONT_GP) as FRONT_GP, "
			+ "sum(BONUS) as BONUS from CSMB_COM_BONUS where store_id=? and com_id<>'ALL' and ym=? group by 1";
	// 获取区域奖金数据
	private static final String GET_COM_BONUS_BY_AREAID = "select sum(GOAL_PROFIT) as GOAL_PROFIT,sum(ACTUAL_PROFIT) as ACTUAL_PROFIT, "
			+ "sum(GOAL_SALE) as GOAL_SALE,sum(SALE) as SALE,sum(ATTRACT) as ATTRACT,sum(COST) as COST,sum(FRONT_GP) as FRONT_GP, "
			+ "sum(BONUS) as BONUS from CSMB_COM_BONUS where store_id in(select store_Id from csmb_store where area_id=?)  "
			+ "and com_id<>'ALL' and ym=? group by 1";
	// 获取省份奖金数据
	private static final String GET_COM_BONUS_BY_PROVINCEID = "select sum(GOAL_PROFIT) as GOAL_PROFIT,sum(ACTUAL_PROFIT) as ACTUAL_PROFIT, "
			+ "sum(GOAL_SALE) as GOAL_SALE,sum(SALE) as SALE,sum(ATTRACT) as ATTRACT,sum(COST) as COST,sum(FRONT_GP) as FRONT_GP, "
			+ "sum(BONUS) as BONUS from CSMB_COM_BONUS where store_id in(select store_Id from csmb_store where province_id=?)  "
			+ "and com_id<>'ALL' and ym=? group by 1";
	// 获取所有奖金数据
	private static final String GET_ALL_COM_BONUS = "select sum(GOAL_PROFIT) as GOAL_PROFIT,sum(ACTUAL_PROFIT) as ACTUAL_PROFIT, "
			+ "sum(GOAL_SALE) as GOAL_SALE,sum(SALE) as SALE,sum(ATTRACT) as ATTRACT,sum(COST) as COST,sum(FRONT_GP) as FRONT_GP, "
			+ "sum(BONUS) as BONUS from CSMB_COM_BONUS where com_id<>'ALL' and ym=? group by 1";

	/**
	 * 分页查询包含当前小店店长、或者合伙人的历史所在小店的记录</br>
	 * modify by wells 20190425 历史记录只查询已经审核通过的
	 * 
	 * @param page
	 * @param pageSize
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月1日
	 */
	public PageResult<BonusHistoryResp> getHistory(int page, int pageSize, String storeId, String comId)
			throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		sql.append("select * from CSMB_COM_BONUS WHERE STORE_ID=? AND COM_ID=? AND AUDIT_STATUS=2");
		params.add(storeId);
		params.add(comId);
		PageResult<BonusHistoryResp> historyPage = super.queryByPage(sql.toString(), BonusHistoryResp.class, page,
				pageSize, "ym", Sort.DESC, params.toArray());
		if (historyPage != null && historyPage.getDatas().size() > 0) {
			List<BonusHistoryResp> historyList = historyPage.getDatas();
			for (BonusHistoryResp bonusHistoryResp : historyList) {
				bonusHistoryResp.setPersonBonusList(
						this.getPersonBonusByNo(bonusHistoryResp.getStoreId(), bonusHistoryResp.getComId()));
			}
		}
		return historyPage;
	}

	/**
	 * 根据小店获取人员奖金情况（不包括大店店长）
	 * 
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public List<PersonBonusItemResp> getPersonBonusByNo(String storeId, String comId) throws Exception {
		return super.queryForList(GET_PERSON_BONUS_BY_NO, PERSON_BONUS_RM, storeId, comId);
	}

	/**
	 * 获取小店店长奖金分配列表[小店上个月人员奖金情况]
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public List<PersonBonusExt> getLastMonthComPersonBonus(String storeId, String comId) throws Exception {
		return super.queryForList(GET_LAST_MONTH_COM_PERSON_BONUS, PERSON_BONUS_EXT_RM, storeId, comId);
	}

	/**
	 * 获取上个月门店下所有小店包含后勤小店的奖金明细
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public List<PersonBonusExt> getLastMonthComBonus(String storeId) throws Exception {
		return super.queryForList(GET_LAST_MONTH_COM_BONUS, PERSON_BONUS_EXT_RM, storeId);
	}

	/**
	 * 调整奖金
	 * 
	 * @param personBonusItemResp
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public int modifyPersonBonus(PersonBonusItemResp personBonusItemResp) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CSMB_PERSON_BONUS SET BEFOR_MONEY=AFTER_MONEY,UPDATE_TIME=sysdate,AFTER_MONEY=?,"
				+ "ADJUST_DESC=?,STATUS=? WHERE YM=? AND PERSON_ID=? AND STORE_ID=? AND COM_ID=?");
		Object[] args = { personBonusItemResp.getAfterMoney(), personBonusItemResp.getAdjustDesc(),
				personBonusItemResp.getStatus(), personBonusItemResp.getYm(), personBonusItemResp.getPersonId(),
				personBonusItemResp.getStoreId(), personBonusItemResp.getComId() };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 更新人员奖金状态
	 * 
	 * @param ym
	 * @param storeId
	 * @param comId
	 * @param status
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public int updatePersonBonusStatus(String ym, String storeId, String comId, int status) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CSMB_PERSON_BONUS SET STATUS=? WHERE YM=? AND STORE_ID=? AND COM_ID=? AND POSITION_ID<>3");
		Object[] args = { status, ym, storeId, comId };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 更新小店奖金审核状态
	 * 
	 * @param personBonusItemResp
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public int updateComBonusStatus(PersonBonusItemResp personBonusItemResp, String reason) throws Exception {
		return this.updateComBonusStatus(personBonusItemResp.getYm(), personBonusItemResp.getStoreId(),
				personBonusItemResp.getComId(), personBonusItemResp.getStatus(), reason);
	}

	/**
	 * 更新小店奖金审核状态
	 * 
	 * @param ym
	 * @param storeId
	 * @param comId
	 * @param status
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public int updateComBonusStatus(String ym, String storeId, String comId, int status, String reason)
			throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CSMB_COM_BONUS SET AUDIT_STATUS=? ,REASON=?, UPDATE_TIME=sysdate "
				+ "WHERE YM=? AND STORE_ID=? AND COM_ID=?");
		Object[] args = { status, reason, ym, storeId, comId };
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 获取小店奖金数据
	 * 
	 * @param comId
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ComBonus getComBonusByComId(String comId, String storeId, String ym) throws Exception {
		return super.queryForObject(GET_COM_BONUS_BY_COMID, COM_BONUS_RM, comId, storeId, ym);
	}

	/**
	 * 获取大店奖金数据
	 * 
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ComBonus getComBonusByStoreId(String storeId, String ym) throws Exception {
		return super.queryForObject(GET_COM_BONUS_BY_STOREID, COM_BONUS_RM, storeId, ym);
	}

	/**
	 * 获取区域奖金数据
	 * 
	 * @param areaId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ComBonus getComBonusByAreaId(String areaId, String ym) throws Exception {
		return super.queryForObject(GET_COM_BONUS_BY_AREAID, COM_BONUS_RM, areaId, ym);
	}

	/**
	 * 获取省份奖金数据
	 * 
	 * @param provinceId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ComBonus getComBonusByProvinceId(String provinceId, String ym) throws Exception {
		return super.queryForObject(GET_COM_BONUS_BY_PROVINCEID, COM_BONUS_RM, provinceId, ym);
	}

	/**
	 * 获取所有奖金数据
	 * 
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ComBonus getAllComBonus(String ym) throws Exception {
		return super.queryForObject(GET_ALL_COM_BONUS, COM_BONUS_RM, ym);
	}
}
