package com.cs.mobile.api.service.partner.bonus;

import java.util.List;
import java.util.Map;

import com.cs.mobile.api.model.user.StorePerson;

public interface BonusPoolService {

	public List<StorePerson> getStoreUserList(Map<String, Object> paramMap) throws Exception;

	public int submitAmountUpdate(StorePerson person) throws Exception;

	/**
	 * 根据大店编码查询后勤小店店长
	 *
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public StorePerson getRearStoreLeader(String storeId) throws Exception;
}
