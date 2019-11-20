package com.cs.mobile.api.dao.common;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cs.mobile.api.model.common.DeptInfo;

@Repository
public class DeptInfoDao extends AbstractDao {

	private static final RowMapper<DeptInfo> DEPT_INFO_RM = new BeanPropertyRowMapper<>(DeptInfo.class);
	// 全司下面所有大类，包含大店及小店信息
	private static final String GET_ALL_DEPT_INFO = "select company.store_id,company.com_id,company.dept_id "
			+ "from CSMB_COMPANY company left join csmb_store store on COMPANY.STORE_ID=store.STORE_ID  where company.COM_ID<>'ALL' or company.COM_NAME='整店'";
	// 省份下面所有大类，包含大店及小店信息
	private static final String GET_DEPT_INFO_BY_PROVINCE = "select company.store_id,company.com_id,company.dept_id "
			+ "from CSMB_COMPANY company left join csmb_store store on COMPANY.STORE_ID=store.STORE_ID  "
			+ "where store.province_id=? and (company.COM_ID<>'ALL' or company.COM_NAME='整店') ";
	// 区域查询下面所有大类，包含大店及小店信息
	private static final String GET_DEPT_INFO_BY_AREA = "select company.store_id,company.com_id,company.dept_id "
			+ "from CSMB_COMPANY company left join csmb_store store on COMPANY.STORE_ID=store.STORE_ID  "
			+ "where store.area_id=? and (company.COM_ID<>'ALL' or company.COM_NAME='整店')";
	// 小店查询下面所有大类
	private static final String GET_DEPT_INFO_BY_COM = "select store_id,com_id,dept_id from CSMB_COMPANY where store_id=? and com_id=?";

	/**
	 * 全司下面所有大类，包含大店及小店信息
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月1日
	 */
	public List<DeptInfo> getAllDeptInfo() throws Exception {
		return super.queryForList(GET_ALL_DEPT_INFO, DEPT_INFO_RM);
	}

	/**
	 * 省份下面所有大类，包含大店及小店信息
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月1日
	 */
	public List<DeptInfo> getDeptInfoByProvince(String provinceId) throws Exception {
		return super.queryForList(GET_DEPT_INFO_BY_PROVINCE, DEPT_INFO_RM, provinceId);
	}

	/**
	 * 区域查询下面所有大类，包含大店及小店信息
	 * 
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月1日
	 */
	public List<DeptInfo> getDeptInfoByArea(String areaId) throws Exception {
		return super.queryForList(GET_DEPT_INFO_BY_AREA, DEPT_INFO_RM, areaId);
	}

	/**
	 * 小店查询下面所有大类</br>
	 * 大店查询传入的comId是ALL
	 * 
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年3月1日
	 */
	public List<DeptInfo> getDeptInfoByCom(String storeId, String comId) throws Exception {
		return super.queryForList(GET_DEPT_INFO_BY_COM, DEPT_INFO_RM, storeId, comId);
	}

}
