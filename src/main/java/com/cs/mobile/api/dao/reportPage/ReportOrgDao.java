package com.cs.mobile.api.dao.reportPage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.reportPage.CsmbStoreModel;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.common.constant.UserTypeEnum;

/**
 * 报表组织架构DAO
 * 
 * @author wells
 * @date 2019年6月5日
 */
@Repository
public class ReportOrgDao extends AbstractDao {

	private static final RowMapper<Organization> ORG_RM = new BeanPropertyRowMapper<>(Organization.class);
    private static final RowMapper<CsmbStoreModel> CSMBSTOREMODEL = new BeanPropertyRowMapper<CsmbStoreModel>(CsmbStoreModel.class);


	/**
	 * 根据区域ID获取所有门店
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年6月10日
	 */
	public List<Organization> getStoreByArea(String areaId, UserInfo userInfo) throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		Integer type = null;
		if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())
				|| userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())) {// 全司管理员
			sql.append("select STORE_ID as org_id,STORE_NAME as org_name "
					+ "from CSMB_STORE where store_close_date is null and chain=11 AND AREA_ID=? "
					+ "GROUP BY STORE_ID,STORE_NAME order by STORE_ID ");
			params.add(areaId);
		} else {
			sql.append(
					" SELECT CS.STORE_ID as org_id,CS.STORE_NAME as org_name FROM CSMB_USER CU LEFT JOIN CSMB_STORE CS ");
			if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
				sql.append(" ON CU.ORG_ID=CS.PROVINCE_ID ");
				type = UserTypeEnum.MREPORT_PROVINCEADMIN.getType();
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
				sql.append(" ON CU.ORG_ID=CS.AREA_ID ");
				type = UserTypeEnum.MREPORT_AREAADMIN.getType();
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
				sql.append(" ON CU.ORG_ID=CS.STORE_ID ");
				type = UserTypeEnum.MREPORT_STOREADMIN.getType();
			}
			sql.append(" WHERE CU.TYPE=? and CU.person_Id=? ");
			params.add(type);
			params.add(userInfo.getPersonId());
			sql.append(" AND CS.store_close_date is null and chain=11 ");
			sql.append(" AND CS.AREA_ID=? ");
			params.add(areaId);
			sql.append(" GROUP BY CS.STORE_ID,CS.STORE_NAME ");
		}
		return super.queryForList(sql.toString(), ORG_RM, params.toArray());
	}

	/**
	 * 根据省份获取所有区域
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年6月10日
	 */
	public List<Organization> getAreaByProvince(String provinceId, UserInfo userInfo) throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		Integer type = null;
		if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())
				|| userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())) {// 全司管理员
			sql.append("select AREA_ID as org_id,AREA_NAME as org_name "
					+ "from CSMB_STORE where store_close_date is null and chain=11 AND PROVINCE_ID=? "
					+ "GROUP BY AREA_ID,AREA_NAME order by AREA_ID");
			params.add(provinceId);
		} else {
			sql.append(
					" SELECT CS.AREA_ID as org_id,CS.AREA_NAME as org_name FROM CSMB_USER CU LEFT JOIN CSMB_STORE CS ");
			if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
				sql.append(" ON CU.ORG_ID=CS.PROVINCE_ID ");
				type = UserTypeEnum.MREPORT_PROVINCEADMIN.getType();
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
				sql.append(" ON CU.ORG_ID=CS.AREA_ID ");
				type = UserTypeEnum.MREPORT_AREAADMIN.getType();
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
				sql.append(" ON CU.ORG_ID=CS.STORE_ID ");
				type = UserTypeEnum.MREPORT_STOREADMIN.getType();
			}
			sql.append(" WHERE CU.TYPE=? and CU.person_Id=? ");
			params.add(type);
			params.add(userInfo.getPersonId());
			sql.append(" AND CS.store_close_date is null and chain=11 ");
			sql.append(" AND CS.PROVINCE_ID=? ");
			params.add(provinceId);
			sql.append(" GROUP BY CS.AREA_ID,CS.AREA_NAME ");
		}
		return super.queryForList(sql.toString(), ORG_RM, params.toArray());
	}

	/**
	 * 获取所有省份
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年6月10日
	 */
	public List<Organization> getAllProvince(UserInfo userInfo) throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		Integer type = null;
		if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())
				|| userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())) {// 全司管理员,超级管理员
			sql.append("select PROVINCE_ID as org_id,PROVINCE_NAME as org_name "
					+ "from CSMB_STORE where store_close_date is null and chain=11 "
					+ "GROUP BY PROVINCE_ID,PROVINCE_NAME order by province_id");
		} else {
			sql.append(
					"SELECT CS.PROVINCE_ID as org_id,CS.PROVINCE_NAME as org_name FROM CSMB_USER CU LEFT JOIN CSMB_STORE CS ");
			if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
				sql.append("ON CU.ORG_ID=CS.PROVINCE_ID");
				type = UserTypeEnum.MREPORT_PROVINCEADMIN.getType();
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
				sql.append("ON CU.ORG_ID=CS.AREA_ID");
				type = UserTypeEnum.MREPORT_AREAADMIN.getType();
			} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
				sql.append("ON CU.ORG_ID=CS.STORE_ID");
				type = UserTypeEnum.MREPORT_STOREADMIN.getType();
			}
			sql.append(" WHERE CU.TYPE=? and CU.person_Id=? ");
			params.add(type);
			params.add(userInfo.getPersonId());
			sql.append(" AND CS.store_close_date is null and chain=11 ");
			sql.append(" GROUP BY CS.PROVINCE_ID,CS.PROVINCE_NAME ");
		}
		return super.queryForList(sql.toString(), ORG_RM, params.toArray());
	}
	
    /**
	 * 根据组织架构id查组织名称(门店级)
     * @return
     */
    public List<CsmbStoreModel> queryOrganization(){
    	StringBuilder sb = new StringBuilder();
			sb.append("select "+ 
					"  province_id provinceId,  " + 
					"  province_name provinceName, " + 
					"  area_id areaId," + 
					"  area_name areaName, " + 
					"  store_id storeId, " + 
					"  store_name storeName" + 
					"  from csmb_store ");
	        return super.queryForList(sb.toString(),CSMBSTOREMODEL);
	    }

}
