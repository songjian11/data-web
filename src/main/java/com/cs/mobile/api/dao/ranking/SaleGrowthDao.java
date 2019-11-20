package com.cs.mobile.api.dao.ranking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.goal.Goal;
import com.cs.mobile.api.model.ranking.SaleComStoreDTO;
import com.cs.mobile.api.model.ranking.SaleItemDTO;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.common.constant.UserTypeEnum;

@Repository
public class SaleGrowthDao extends AbstractDao {

	private static final RowMapper<Goal> GOAL_RM = new BeanPropertyRowMapper<>(Goal.class);
	private static final RowMapper<SaleComStoreDTO> COMSTORE_DTO = new BeanPropertyRowMapper<>(SaleComStoreDTO.class);
	private static final RowMapper<SaleItemDTO> ITEM_DTO = new BeanPropertyRowMapper<>(SaleItemDTO.class);

	// 查询店群
	private static final String GET_STOREGROUP = "SELECT GROUP_ID groupId,GROUP_NAME groupName FROM CSMB_STORE_GROUP GROUP BY GROUP_ID,GROUP_NAME ORDER BY GROUP_ID ";
	// 删除点赞
	private static final String DELETE_GIVE_NUM = "delete from CSMB_RANKING_GIVE t where t.GIVE_YM=? and t.USER_ID=? and t.STORE_ID=? and t.COM_ID=? ";
	// 添加点赞
	private static final String ADD_GIVE_NUM = "insert into CSMB_RANKING_GIVE (GIVE_YM, USER_ID, STORE_ID, COM_ID) values (?, ?, ?, ?) ";

	/**
	 * 根据店群和关键字获取（大店）销售增长率排名
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public List<SaleComStoreDTO> getStoreIncreaseList(Map<String, String> paramMap, UserInfo userInfo)
			throws Exception {
		List<String> paramList = new ArrayList<>();
		paramList.add(paramMap.get("YMonth"));// 年月
		paramList.add(paramMap.get("YMonth"));// 年月
		paramList.add(paramMap.get("userId"));// 用户ID
		paramList.add(paramMap.get("groupId"));// 店群ID
		paramList.add(paramMap.get("startDate"));// 开始时间
		paramList.add(paramMap.get("endDate"));// 结束时间

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("       tgroup.GROUP_ID groupId, ");
		sql.append("       tgroup.group_name groupName, ");
		sql.append("       sale.store_id storeId, ");
		sql.append("       store.store_name storeName, ");
		sql.append(
				"       (select max(name) from csmb_person manager where manager.store_id = SALE.store_id and manager.POSITION_ID ='3') storeManager, ");// 3.大店店长
		sql.append("       (select count(give.user_id) from CSMB_RANKING_GIVE give ");
		sql.append("       	where give.store_id = sale.store_id and give.com_id ='0' and give.give_ym = ? ) giveNum,");// 点赞数
		sql.append("       (select decode(count(ifzd.user_id),0,'N','Y') from CSMB_RANKING_GIVE ifzd ");
		sql.append(
				"       	where ifzd.store_id = sale.store_id and ifzd.com_id='0' and ifzd.give_ym = ? and ifzd.user_id=?) ifGive,");// 是否点赞
		sql.append("       sum(sale.sale_value) saleActualValue ");// 销售额
		sql.append("  FROM CSMB_DEPT_SALES_HISTORY sale ");
		sql.append("  LEFT JOIN CSMB_STORE_GROUP tgroup ON SALE.STORE_ID = TGROUP.STORE_ID ");
		sql.append("  LEFT JOIN csmb_store store ON store.STORE_ID = sale.STORE_ID ");
		sql.append(" WHERE tgroup.GROUP_ID = ? ");
		sql.append("   AND to_char(sale.sale_date, 'yyyy-MM-dd') BETWEEN ? and ? ");
		if (!userInfo.getTypeList().contains(UserTypeEnum.PARTNER_ENTERPRISEADMIN.getType())
				&& !userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())) {// 不为全司管理员的时候需要区分省份
			if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_PROVINCEADMIN.getType())) {// 省份管理员
				sql.append(" AND STORE.PROVINCE_ID=? ");
				paramList.add(userInfo.getPartnerUserInfo().getProvinceId());
			} else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_AREAADMIN.getType())) {// 区域管理员
				sql.append(" AND STORE.PROVINCE_ID=(select distinct(province_Id) from csmb_store where area_id=?) ");
				paramList.add(userInfo.getPartnerUserInfo().getAreaId());
			} else {// 大店店长
				sql.append(" AND STORE.PROVINCE_ID=(select distinct(province_Id) from csmb_store where store_Id=?) ");
				paramList.add(userInfo.getPartnerUserInfo().getStoreId());
			}
		}
		sql.append(" GROUP BY tgroup.GROUP_ID,tgroup.group_name,sale.store_id,store.store_name ");
		return super.queryForList(sql.toString(), COMSTORE_DTO, paramList.toArray());
	}

	/**
	 * 根据店群,类别和关键字获取（小店）销售增长率排名
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年3月30日
	 */
	public List<SaleComStoreDTO> getComIncreaseList(Map<String, String> paramMap, UserInfo userInfo) throws Exception {
		List<String> paramList = new ArrayList<>();
		paramList.add(paramMap.get("YMonth"));// 年月
		paramList.add(paramMap.get("YMonth"));// 年月
		paramList.add(paramMap.get("userId"));// 用户ID
		paramList.add(paramMap.get("groupId"));// 店群ID
		paramList.add(paramMap.get("comId"));// 小店ID
		paramList.add(paramMap.get("startDate"));// 开始时间
		paramList.add(paramMap.get("endDate"));// 结束时间

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("       tgroup.GROUP_ID groupId, ");
		sql.append("       tgroup.group_name groupName, ");
		sql.append("       com.store_id storeId, ");
		sql.append(
				"       (select max(store_name) from csmb_store store where store.store_id = com.store_id) storeName, ");
		sql.append("       com.com_id comId, ");
		sql.append("       com.com_name comName, ");
		sql.append("       (select count(give.user_id) from CSMB_RANKING_GIVE give ");
		sql.append(
				"        	where give.store_id = com.store_id and give.com_id = com.com_id and give.give_ym = ? ) giveNum,");// 点赞数
		sql.append("       (select decode(count(ifzd.user_id),0,'N','Y') from CSMB_RANKING_GIVE ifzd ");
		sql.append(
				"       	where ifzd.store_id = com.store_id and ifzd.com_id = com.com_id and ifzd.give_ym = ? and ifzd.user_id=?) ifGive,");// 是否点赞
		sql.append("       (select max(name) from csmb_person manager ");
		sql.append(
				"       	where manager.store_id = com.store_id and manager.com_id = com.com_id and manager.POSITION_ID ='2') storeManager,");// 2.小店店长
		sql.append("       sum(sale.sale_value) saleActualValue ");
		sql.append("  FROM CSMB_DEPT_SALES_HISTORY sale ");
		sql.append("  LEFT JOIN CSMB_STORE_GROUP tgroup ON SALE.STORE_ID = TGROUP.STORE_ID ");
		sql.append(
				"  LEFT JOIN CSMB_COMPANY com ON (SALE.STORE_ID = com.STORE_ID and SALE.dept_id = com.dept_id and com.com_id <> 'ALL') ");
		if (!userInfo.getTypeList().contains(UserTypeEnum.PARTNER_ENTERPRISEADMIN.getType())
				&& !userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())) {// 不为全司管理员的时候需要区分省份
			sql.append(" LEFT JOIN csmb_store STORE ON STORE.STORE_ID=tgroup.STORE_ID ");
		}
		sql.append(" WHERE tgroup.GROUP_ID = ? ");
		sql.append("   AND com.com_id = ? ");
		sql.append("   AND to_char(sale.sale_date, 'yyyy-MM-dd') BETWEEN ? and ? ");
		if (!userInfo.getTypeList().contains(UserTypeEnum.PARTNER_ENTERPRISEADMIN.getType())
				&& !userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())) {// 不为全司管理员的时候需要区分省份
			if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_PROVINCEADMIN.getType())) {// 省份管理员
				sql.append(" AND STORE.PROVINCE_ID=? ");
				paramList.add(userInfo.getPartnerUserInfo().getProvinceId());
			} else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_AREAADMIN.getType())) {// 区域管理员
				sql.append(" AND STORE.PROVINCE_ID=(select distinct(province_Id) from csmb_store where area_id=?) ");
				paramList.add(userInfo.getPartnerUserInfo().getAreaId());
			} else {// 大店店长
				sql.append(" AND STORE.PROVINCE_ID=(select distinct(province_Id) from csmb_store where store_Id=?) ");
				paramList.add(userInfo.getPartnerUserInfo().getStoreId());
			}
		}
		sql.append(" GROUP BY tgroup.GROUP_ID,tgroup.group_name,com.store_id,com.com_id,com.com_name ");
		return super.queryForList(sql.toString(), COMSTORE_DTO, paramList.toArray());
	}

	/**
	 * 排行点赞-先删再加
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月4日
	 */
	public void saveGiveRanking(Map<String, String> paramMap) throws Exception {
		String YMonth = paramMap.get("YMonth");
		String userId = paramMap.get("userId");
		Integer storeId = Integer.valueOf(paramMap.get("storeId"));
		String comId = paramMap.get("comId");
		String ifGive = paramMap.get("ifGive");
		super.jdbcTemplate.update(DELETE_GIVE_NUM, YMonth, userId, storeId, comId);
		if ("Y".equals(ifGive)) {
			super.jdbcTemplate.update(ADD_GIVE_NUM, YMonth, userId, storeId, comId);
		}
	}

	/**
	 * 获取小店列表-下拉框
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年4月01日
	 */
	public List<SaleComStoreDTO> getComStoreList(Map<String, String> paramMap) throws Exception {
		List<String> paramList = new ArrayList<>();
		String groupId = paramMap.get("groupId");// 群店ID
		String storeId = paramMap.get("storeId");// 大店ID
		String comId = paramMap.get("comId");// 小店ID
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("       tgroup.GROUP_ID groupId, ");
		sql.append("       tgroup.group_name groupName, ");
		sql.append("       com.com_id comId, ");
		sql.append("       com.com_name comName ");
		sql.append("  FROM CSMB_COMPANY com ");
		sql.append("  LEFT JOIN CSMB_STORE_GROUP tgroup ON com.store_id = tgroup.store_id ");
		sql.append("  where com.com_id <> 'ALL' ");
		if (StringUtils.isNotEmpty(groupId)) {
			sql.append(" and tgroup.group_id = ? ");
			paramList.add(groupId);
		}
		if (StringUtils.isNotEmpty(storeId)) {
			sql.append(" and com.store_id = ? ");
			paramList.add(storeId);
		}
		if (StringUtils.isNotEmpty(comId)) {
			sql.append(" and com.com_id = ? ");
			paramList.add(comId);
		}
		sql.append(" group by tgroup.GROUP_ID,tgroup.group_name,com.com_id,com.com_name ");
		sql.append(" order by tgroup.GROUP_ID,com.com_id ");
		return super.queryForList(sql.toString(), COMSTORE_DTO, paramList.toArray());
	}

	public List<SaleComStoreDTO> getStoreGroupList(Map<String, String> paramMap) throws Exception {
		return super.queryForList(GET_STOREGROUP, COMSTORE_DTO);
	}

	/**
	 * 获取单品排名Top10-数据
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年4月01日
	 */
	public List<SaleItemDTO> getDeptItemTop10(Map<String, String> paramMap) throws Exception {
		List<String> paramList = new ArrayList<>();
		paramList.add(paramMap.get("comId"));// 小店ID
		paramList.add(paramMap.get("startDate"));// 开始时间
		paramList.add(paramMap.get("endDate"));// 结束时间
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append(
				"       COM_ID comId,DEPT_ID deptId,DC0102 deptName,SD0102 itemId,DG0102 itemName,ROUND(AMT,2) saleValue ");
		sql.append("  FROM (");
		sql.append("     SELECT ");
		sql.append("            COM_ID,DEPT_ID,DC0102,SD0102,DG0102,AMT, ");
		sql.append("            ROW_NUMBER () OVER (PARTITION BY com_id,dept_id ORDER BY amt DESC) rn ");
		sql.append("       FROM ( ");
		sql.append("          SELECT ");
		sql.append("                 com.com_id,com.dept_id,dc.dc0102,sd.sd0102,dg.dg0102, ");
		sql.append("                 SUM(sd.amt / (1+dg.incometaxrate / 100)) amt ");
		sql.append("            FROM csmbcom com,sd001 sd,dg001 dg,dc001 dc ");
		sql.append("           WHERE com.dept_id = dg.dg0104 ");
		sql.append("             AND dg.dg0101 = sd.sd0102 ");
		sql.append("             AND com.com_id = ? ");
		sql.append("             AND sd.sd0101 BETWEEN ? AND ? ");
		sql.append("             AND dc.dc0101 = com.dept_id ");
		sql.append("           GROUP BY com.com_id,com.dept_id,dc.dc0102,sd.sd0102,dg.dg0102 ");
		sql.append("           ) ");
		sql.append("        ) ");
		sql.append(" WHERE rn <= 10 ");
		return super.queryForList(sql.toString(), ITEM_DTO, paramList.toArray());
	}

	/**
	 * 获取大店月目标-数据
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年4月01日
	 */
	public List<Goal> getStoreGoalList(Map<String, String> paramMap) throws Exception {
		StringBuffer sql = new StringBuffer();
		String YMonth = paramMap.get("YMonth");// 年月
		String groupId = paramMap.get("groupId");// 店群ID
		sql.append("SELECT ");
		sql.append("       tgroup.STORE_ID storeId,");
		sql.append("       ROUND(sum(sub_values),4) subValues ");
		sql.append("  from CSMB_GOAL goal  ");
		sql.append("  LEFT JOIN CSMB_STORE_GROUP tgroup ON goal.STORE_ID = tgroup.STORE_ID ");
		sql.append(" where goal.subject='销售' ");
		sql.append("   and goal.GOAL_YM = ? ");
		sql.append("   and tgroup.GROUP_ID = ? ");
		sql.append(" group by tgroup.GROUP_ID,tgroup.STORE_ID ");
		return super.queryForList(sql.toString(), GOAL_RM, YMonth, groupId);
	}

	/**
	 * 获取小店月目标-数据
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author jiangliang
	 * @date 2019年4月01日
	 */
	public Goal getComGoalVal(Map<String, String> paramMap) throws Exception {
		StringBuffer sql = new StringBuffer();
		String YMonth = paramMap.get("YMonth");// 年月
		String storeId = paramMap.get("storeId");// 大店ID
		String comId = paramMap.get("comId");// 小店ID
		sql.append("SELECT ");
		sql.append("       com.STORE_ID storeId,");
		sql.append("       com.com_id comId,");
		sql.append("       ROUND(sum(sub_values),4) subValues ");
		sql.append("  from CSMB_GOAL goal  ");
		sql.append(
				"  LEFT JOIN CSMB_COMPANY com ON (goal.STORE_ID = com.STORE_ID and goal.dept_id = com.dept_id and com.com_id <> 'ALL') ");
		sql.append(" where goal.subject='销售' ");
		sql.append("   and goal.GOAL_YM = ? ");
		sql.append("   and com.STORE_ID = ? ");
		sql.append("   and com.COM_ID = ? ");
		sql.append(" group by com.STORE_ID,com.com_id ");
		return super.queryForObject(sql.toString(), GOAL_RM, YMonth, storeId, comId);
	}
}
