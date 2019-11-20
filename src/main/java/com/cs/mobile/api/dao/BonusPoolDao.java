package com.cs.mobile.api.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.util.StringUtils;
import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.user.StorePerson;

@Repository
public class BonusPoolDao extends AbstractDao {

	private static final RowMapper<StorePerson> PERSON_RM = new BeanPropertyRowMapper<>(StorePerson.class);

	/**
	 * 店-人员列表
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<StorePerson> getStoreUserList(Map<String,Object> paramMap) throws Exception {
		String changeYm = String.valueOf(paramMap.get("changeYm"));//时间 yyyy-MM
		String storeId = String.valueOf(paramMap.get("storeId"));//大店ID
		String comId = String.valueOf(paramMap.get("comId"));//小店ID
		if(StringUtils.isEmpty(comId)){ comId="ALL"; }//只带大店ID-查询后勤小店

		StringBuffer sql = new StringBuffer();
		String[] param = {changeYm, changeYm, storeId, comId};
		sql.append("select ");
		sql.append("  a.person_id personId,");
		sql.append("  a.store_id storeId,");
		sql.append("  a.store_name storeName,");
		sql.append("  a.com_id comId,");
		sql.append("  a.com_name comName,");
		sql.append("  a.position_id positionId,");
		sql.append("  a.position_name positionName,");
		sql.append("  a.name name,");
		sql.append("  a.gender gender,");
		sql.append("  w.days attendance,");//出勤天数
		sql.append("  a.last_in_date lastInDate, ");
		sql.append("  b.befor_money beforMoney, ");//调整前金额
		sql.append("  nvl(b.STATUS,0) status, ");//状态 0待调整 1 待审核 2 审核通过 3 审核不通过
		sql.append("  b.after_money afterAmount, ");//调整后金额
		sql.append("  b.adjust_desc adjustDesc, ");
		sql.append("  p.position_values positionVal ");//岗位系数
		sql.append("from CSMB_PERSON a ");//店铺人员表
		sql.append("  left join CSMB_BONUS_CHANGE b on (a.person_id=b.person_id and b.change_ym=?) ");//分配金额调整表-月份
		sql.append("  left join CSMB_PERSION_WORK w on (a.person_id=w.person_id and w.work_ym=?) ");//人员出勤天数表-月份
		sql.append("  left join CSMB_POSITION_VALUES p on a.position_id = p.position_id ");//岗位系数配置表-6,3,1分配规则
		sql.append(" where 1=1 ");
		sql.append(" and a.store_id = ? ");//大店
		sql.append(" and a.com_id = ? ");//小店
		return super.queryForList(sql.toString(), PERSON_RM, param);
	}

	/**
	 * 分享金额-提交
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public int submitAmountUpdate(StorePerson person) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into CSMB_BONUS_CHANGE ");
		sql.append(" (CHANGE_YM, PERSON_ID, POSITION_ID, BEFOR_MONEY, AFTER_MONEY, STATUS, ADJUST_DESC) ");
		sql.append("values");
		sql.append("   ( ");
		sql.append("'"+person.getChangeYm() + "',");//年月
		sql.append("'"+person.getPersonId() + "',");//人员ID
		sql.append(person.getPositionId() + ",");//岗位编码
		sql.append(person.getBeforMoney() + ",");//调整前金额
		sql.append(person.getAfterAmount() + ",");//调整后金额
		sql.append(1+",");//状态-1：等待审核 2：审核通过 3：审核不通过
		sql.append("'"+person.getAdjustDesc()+"'");//调整说明
		sql.append(" ) ");
		return jdbcTemplate.update(sql.toString());
	}

	/**
	 * 根据大店ID查询后勤小店店长
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public StorePerson getRearStoreLeader(String storeId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("  a.person_id personId,");
		sql.append("  a.store_id storeId,");
		sql.append("  a.store_name storeName,");
		sql.append("  a.com_id comId,");
		sql.append("  a.com_name comName,");
		sql.append("  a.position_id positionId,");
		sql.append("  a.position_name positionName,");
		sql.append("  a.name name,");
		sql.append("  a.gender gender,");
		sql.append("  a.last_in_date lastInDate ");
		sql.append("from CSMB_PERSON a ");
		sql.append("where a.com_id = 'ALL' ");//后勤小店
		sql.append("  and instr(a.position_name,'店长')>0 ");//店长
		sql.append("  and a.store_id = ? ");//大店ID
		return super.queryForObject(sql.toString(), PERSON_RM, storeId);
	}
}
