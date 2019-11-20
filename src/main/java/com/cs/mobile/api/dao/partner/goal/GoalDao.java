package com.cs.mobile.api.dao.partner.goal;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.goal.Goal;
import com.cs.mobile.api.model.goal.GoalValue;

@Repository
public class GoalDao extends AbstractDao {
	private static final RowMapper<Goal> GOAL_RM = new BeanPropertyRowMapper<>(Goal.class);
	private static final RowMapper<GoalValue> GOAL_VALUE_RM = new BeanPropertyRowMapper<>(GoalValue.class);

	// 获取门店下所有小店当月目标
	private static final String GET_CURMONTH_GOAL_BYSTOREID = "select COMPANY.COM_ID,nvl(sum(sub_values),0) as sub_values from CSMB_GOAL goal "
			+ "left join CSMB_COMPANY company on goal.dept_Id=COMPANY.DEPT_ID and goal.store_id=COMPANY.STORE_ID where goal.subject=? and goal.store_id=? "
			+ "and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM group by COMPANY.COM_ID";
	// 获取小店下所有大类当月目标
	private static final String GET_CURMONTH_GOAL_BYCOMID = "select goal.DEPT_ID,NVL(sum(goal.sub_values),0) as sub_values "
			+ "from CSMB_GOAL goal left join CSMB_COMPANY company "
			+ "on goal.dept_Id=COMPANY.DEPT_ID and goal.store_id=COMPANY.STORE_ID where goal.SUBJECT=?  "
			+ "and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM and COMPANY.COM_ID=? and goal.store_id=?"
			+ "group by goal.DEPT_ID";
	// 获取某个区域下所有大店当月目标
	private static final String GET_CURMONTH_GOAL_BYAREAID = "select goal.STORE_ID,nvl(sum(sub_values),0) as sub_values  "
			+ "from CSMB_GOAL goal left join CSMB_STORE store on goal.STORE_ID=store.STORE_ID "
			+ "left join CSMB_COMPANY company "
			+ "on company.store_id=store.store_id and company.dept_id=goal.dept_id where goal.subject=? "
			+ "and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM and (company.COM_ID<>'ALL' or company.COM_NAME='整店') and store.AREA_ID=? group by goal.STORE_ID";

	// 获取某个省份下所有区域当月目标
	private static final String GET_CURMONTH_GOAL_BYPVID = "select store.AREA_ID,nvl(sum(sub_values),0) as sub_values  "
			+ "from CSMB_GOAL goal left join CSMB_STORE store on goal.STORE_ID=store.STORE_ID "
			+ "left join CSMB_COMPANY company "
			+ "on company.store_id=store.store_id and company.dept_id=goal.dept_id where goal.subject=? "
			+ "and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM and (company.COM_ID<>'ALL' or company.COM_NAME='整店') and store.PROVINCE_ID=? "
			+ "group by store.AREA_ID";

	// 获取全司下所有省份当月目标
	private static final String GET_CURMONTH_GOAL = "select store.PROVINCE_ID,nvl(sum(sub_values),0) as sub_values  "
			+ "from CSMB_GOAL goal left join CSMB_STORE store on goal.STORE_ID=store.STORE_ID "
			+ "left join CSMB_COMPANY company "
			+ "on company.store_id=store.store_id and company.dept_id=goal.dept_id where goal.subject=? "
			+ "and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM and (company.COM_ID<>'ALL' or company.COM_NAME='整店') group by store.PROVINCE_ID";

	// 大店选择区间月的目标劳效
	private static final String GET_SCOPE_WORKRATIO_GOAL = "select goal_ym,sum(sub_values) as sub_values from CSMB_GOAL "
			+ "where GOAL_YM>=? and GOAL_YM<=? and subject='目标劳效' and STORE_ID=? group by goal_ym";
	// 整月所有小店毛利额(即前台毛利)、销售、后台、DC成本、其它费用、折旧费用、租赁费用、销售-水电费用、销售-人力成本目标
	private static final String GET_COM_GP_GOAL = "select goal.subject,NVL(SUM(goal.SUB_VALUES),0) as sub_values "
			+ "from CSMB_GOAL goal left join CSMB_COMPANY company "
			+ "on goal.dept_Id=COMPANY.DEPT_ID and goal.store_id=COMPANY.STORE_ID "
			+ "where goal.GOAL_YM=? and goal.subject "
			+ "in('毛利额','销售','后台','DC成本', '其它费用','折旧费用','租赁费用','销售-水电费用','销售-人力成本','招商收入') "
			+ "and goal.STORE_ID=? and company.COM_ID=? group by goal.subject ";

	// 整月所有门店毛利额(即前台毛利)、销售、后台、DC成本、其它费用、折旧费用、租赁费用、销售-水电费用、销售-人力成本目标
	private static final String GET_STORE_GP_GOAL = "select goal.subject,NVL(SUM(goal.SUB_VALUES),0) as sub_values,store.STORE_ID,COMPANY.COM_ID "
			+ "from CSMB_COMPANY company left join CSMB_STORE store "
			+ "on company.STORE_ID=store.STORE_ID left join CSMB_GOAL goal "
			+ "on goal.dept_Id=COMPANY.DEPT_ID and goal.store_id=COMPANY.STORE_ID "
			+ "where goal.GOAL_YM=? and goal.subject "
			+ "in('毛利额','销售','后台','DC成本', '其它费用','折旧费用','租赁费用','销售-水电费用','销售-人力成本','招商收入') and store.AREA_ID=? "
			+ "group by goal.subject,store.STORE_ID,COMPANY.COM_ID ";

	// 整月所有区域毛利额(即前台毛利)、销售、后台、DC成本、其它费用、折旧费用、租赁费用、销售-水电费用、销售-人力成本目标
	private static final String GET_AREA_GP_GOAL = "select goal.subject,NVL(SUM(goal.SUB_VALUES),0) as sub_values,store.STORE_ID,COMPANY.COM_ID "
			+ "from CSMB_COMPANY company left join CSMB_STORE store "
			+ "on company.STORE_ID=store.STORE_ID left join CSMB_GOAL goal "
			+ "on goal.dept_Id=COMPANY.DEPT_ID and goal.store_id=COMPANY.STORE_ID "
			+ "where goal.GOAL_YM=? and goal.subject "
			+ "in('毛利额','销售','后台','DC成本', '其它费用','折旧费用','租赁费用','销售-水电费用','销售-人力成本','招商收入') and store.PROVINCE_ID=? "
			+ "group by goal.subject,store.STORE_ID,COMPANY.COM_ID ";

	// 整月所有省份毛利额(即前台毛利)、销售、后台、DC成本、其它费用、折旧费用、租赁费用、销售-水电费用、销售-人力成本目标
	private static final String GET_PROVINCE_GP_GOAL = "select goal.subject,NVL(SUM(goal.SUB_VALUES),0) as sub_values,store.STORE_ID,COMPANY.COM_ID "
			+ "from CSMB_COMPANY company left join CSMB_STORE store "
			+ "on company.STORE_ID=store.STORE_ID left join CSMB_GOAL goal "
			+ "on goal.dept_Id=COMPANY.DEPT_ID and goal.store_id=COMPANY.STORE_ID "
			+ "where goal.GOAL_YM=? and goal.subject "
			+ "in('毛利额','销售','后台','DC成本', '其它费用','折旧费用','租赁费用','销售-水电费用','销售-人力成本','招商收入') "
			+ "group by goal.subject,store.STORE_ID,COMPANY.COM_ID  ";

	// 大店下所有小店当月总毛利目标
	private static final String GET_CURMONTH_COM_TGM_GOAL = "select company.com_id, "
			+ "(NVL(SUM(DECODE(goal.subject,'毛利额',goal.sub_values)),0)+ "
			+ "NVL(SUM(DECODE(goal.subject,'后台',goal.sub_values)),0)- "
			+ "NVL(SUM(DECODE(goal.subject,'DC成本',goal.sub_values)),0)) as sub_values  " + " from CSMB_GOAL goal  "
			+ "left join CSMB_COMPANY company on goal.store_id=company.store_id and goal.dept_id=company.dept_id  "
			+ "where goal.SUBJECT in('毛利额','后台','DC成本') and goal.store_id=? and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM "
			+ "group by company.com_id";
	// 小店下所有大类当月总毛利目标
	private static final String GET_CURMONTH_DEPT_TGM_GOAL = "select company.dept_id,( "
			+ "NVL(SUM(DECODE(goal.subject,'毛利额',goal.sub_values)),0)+NVL(SUM(DECODE(goal.subject,'后台',goal.sub_values)),0)-NVL(SUM(DECODE(goal.subject,'DC成本',goal.sub_values)),0))  "
			+ "as sub_values from CSMB_GOAL goal  "
			+ "left join CSMB_COMPANY company on goal.store_id=company.store_id and goal.dept_id=company.dept_id  "
			+ "where goal.SUBJECT in('毛利额','后台','DC成本') and goal.store_id=? and COMPANY.COM_ID=? and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM "
			+ "group by company.dept_id";

	// 某个区域下所有大店当月总毛利目标
	private static final String GET_CURMONTH_STORE_TGM_GOAL = "select store.STORE_ID, "
			+ "(NVL(SUM(DECODE(goal.subject,'毛利额',goal.sub_values)),0)+ "
			+ "NVL(SUM(DECODE(goal.subject,'后台',goal.sub_values)),0)- "
			+ "NVL(SUM(DECODE(goal.subject,'DC成本',goal.sub_values)),0)) as sub_values  "
			+ " from CSMB_GOAL goal  left join CSMB_STORE store on goal.STORE_ID=store.STORE_ID "
			+ "where goal.SUBJECT in('毛利额','后台','DC成本') and store.AREA_ID=? and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM "
			+ "group by store.STORE_ID";

	// 某个省份下所有区域当月总毛利目标
	private static final String GET_CURMONTH_AREA_TGM_GOAL = "select store.AREA_ID, "
			+ "(NVL(SUM(DECODE(goal.subject,'毛利额',goal.sub_values)),0)+ "
			+ "NVL(SUM(DECODE(goal.subject,'后台',goal.sub_values)),0)- "
			+ "NVL(SUM(DECODE(goal.subject,'DC成本',goal.sub_values)),0)) as sub_values  "
			+ " from CSMB_GOAL goal  left join CSMB_STORE store on goal.STORE_ID=store.STORE_ID "
			+ "where goal.SUBJECT in('毛利额','后台','DC成本') and store.PROVINCE_ID=? and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM "
			+ "group by store.AREA_ID";

	// 全司下所有省份当月总毛利目标
	private static final String GET_CURMONTH_PROVINCE_TGM_GOAL = "select store.PROVINCE_ID, "
			+ "(NVL(SUM(DECODE(goal.subject,'毛利额',goal.sub_values)),0)+ "
			+ "NVL(SUM(DECODE(goal.subject,'后台',goal.sub_values)),0)- "
			+ "NVL(SUM(DECODE(goal.subject,'DC成本',goal.sub_values)),0)) as sub_values  "
			+ " from CSMB_GOAL goal  left join CSMB_STORE store on goal.STORE_ID=store.STORE_ID "
			+ "where goal.SUBJECT in('毛利额','后台','DC成本') and to_char(sysdate,'yyyy-mm')=goal.GOAL_YM "
			+ "group by store.PROVINCE_ID";

	/**
	 * 获取门店下所有小店当月目标
	 * 
	 * @param storeId
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public List<Goal> getCurMonthGoalListByStoreId(String storeId, String subject) throws Exception {
		return super.queryForList(GET_CURMONTH_GOAL_BYSTOREID, GOAL_RM, subject, storeId);
	}

	/**
	 * 获取小店下所有大类当月目标
	 * 
	 * @param storeId
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public List<Goal> getCurMonthGoalListByComId(String storeId, String comId, String subject) throws Exception {
		return super.queryForList(GET_CURMONTH_GOAL_BYCOMID, GOAL_RM, subject, comId, storeId);
	}

	/**
	 * 获取某个区域下所有大店当月目标
	 * 
	 * @param areaId
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public List<Goal> getCurMonthGoalByAreaId(String areaId, String subject) throws Exception {
		return super.queryForList(GET_CURMONTH_GOAL_BYAREAID, GOAL_RM, subject, areaId);
	}

	/**
	 * 获取某个省份下所有区域当月目标
	 * 
	 * @param provinceId
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public List<Goal> getCurMonthGoalByPvid(String provinceId, String subject) throws Exception {
		return super.queryForList(GET_CURMONTH_GOAL_BYPVID, GOAL_RM, subject, provinceId);
	}

	/**
	 * 获取全司下所有省份当月目标
	 * 
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public List<Goal> getCurMonthGoal(String subject) throws Exception {
		return super.queryForList(GET_CURMONTH_GOAL, GOAL_RM, subject);
	}

	/**
	 * 大店选择区间月的目标劳效
	 * 
	 * @param storeId
	 * @param beginYm
	 * @param endYm
	 * @return
	 * @throws Exception
	 */

	public List<Goal> getScopeWorkRatioTarget(String storeId, String beginYm, String endYm) throws Exception {
		return super.queryForList(GET_SCOPE_WORKRATIO_GOAL, GOAL_RM, beginYm, endYm, storeId);
	}

	/**
	 * 整月小店毛利额(即前台毛利)、销售、后台、DC成本、其它费用、折旧费用、租赁费用、销售-水电费用、销售-人力成本目标
	 * 
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<GoalValue> getComGPGoal(String storeId, String comId, String ym) throws Exception {
		return super.queryForList(GET_COM_GP_GOAL, GOAL_VALUE_RM, ym, storeId, comId);
	}

	/**
	 * 整月门店毛利额(即前台毛利)、销售、后台、DC成本、其它费用、折旧费用、租赁费用、销售-水电费用、销售-人力成本目标
	 * 
	 * @param areaId
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<GoalValue> getStoreGPGoal(String areaId, String ym) throws Exception {
		return super.queryForList(GET_STORE_GP_GOAL, GOAL_VALUE_RM, ym, areaId);
	}

	/**
	 * 整月区域毛利额(即前台毛利)、销售、后台、DC成本、其它费用、折旧费用、租赁费用、销售-水电费用、销售-人力成本目标
	 * 
	 * @param provinceId
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<GoalValue> getAreaGPGoal(String provinceId, String ym) throws Exception {
		return super.queryForList(GET_AREA_GP_GOAL, GOAL_VALUE_RM, ym, provinceId);
	}

	/**
	 * 整月省份毛利额(即前台毛利)、销售、后台、DC成本、其它费用、折旧费用、租赁费用、销售-水电费用、销售-人力成本目标
	 * 
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public List<GoalValue> getProvinceGPGoal(String ym) throws Exception {
		return super.queryForList(GET_PROVINCE_GP_GOAL, GOAL_VALUE_RM, ym);
	}

	/**
	 * 大店下所有小店当月总毛利目标
	 * 
	 * @author wells
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @time 2018年12月29日
	 */
	public List<Goal> getCurMonthComTGMGoal(String storeId) throws Exception {
		return super.queryForList(GET_CURMONTH_COM_TGM_GOAL, GOAL_RM, storeId);
	}

	/**
	 * 小店下所有大类当月总毛利目标
	 * 
	 * @author wells
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @time 2018年12月29日
	 */
	public List<Goal> getCurMonthDeptTGMGoal(String storeId, String comId) throws Exception {
		return super.queryForList(GET_CURMONTH_DEPT_TGM_GOAL, GOAL_RM, storeId, comId);
	}

	/**
	 * 某个区域下所有大店当月总毛利目标
	 * 
	 * @author wells
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @time 2018年12月29日
	 */
	public List<Goal> getCurMonthStoreTGMGoal(String areaId) throws Exception {
		return super.queryForList(GET_CURMONTH_STORE_TGM_GOAL, GOAL_RM, areaId);
	}

	/**
	 * 某个省份下所有区域当月总毛利目标
	 * 
	 * @author wells
	 * @param provinceId
	 * @return
	 * @throws Exception
	 * @time 2018年12月29日
	 */
	public List<Goal> getCurMonthAreaTGMGoal(String provinceId) throws Exception {
		return super.queryForList(GET_CURMONTH_AREA_TGM_GOAL, GOAL_RM, provinceId);
	}

	/**
	 * 全司下所有省份当月总毛利目标
	 * 
	 * @author wells
	 * @return
	 * @throws Exception
	 * @time 2018年12月29日
	 */
	public List<Goal> getCurMonthProvinceTGMGoal() throws Exception {
		return super.queryForList(GET_CURMONTH_PROVINCE_TGM_GOAL, GOAL_RM);
	}
}
