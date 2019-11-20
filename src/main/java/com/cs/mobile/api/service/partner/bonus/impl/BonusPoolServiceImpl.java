package com.cs.mobile.api.service.partner.bonus.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.BonusPoolDao;
import com.cs.mobile.api.model.user.StorePerson;
import com.cs.mobile.api.service.partner.bonus.BonusPoolService;

@Service
public class BonusPoolServiceImpl implements BonusPoolService {

	@Autowired
	BonusPoolDao bonusPoolDao;

	@Override
	public List<StorePerson> getStoreUserList(Map<String, Object> paramMap) throws Exception {
		return bonusPoolDao.getStoreUserList(paramMap);
	}

	@Override
	public int submitAmountUpdate(StorePerson person) throws Exception {
		return bonusPoolDao.submitAmountUpdate(person);
	}

	@Override
	public StorePerson getRearStoreLeader(String storeId) throws Exception {
		return bonusPoolDao.getRearStoreLeader(storeId);
	}
}
