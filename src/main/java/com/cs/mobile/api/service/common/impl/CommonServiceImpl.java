package com.cs.mobile.api.service.common.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cs.mobile.api.model.common.CsmbOrg;
import com.cs.mobile.api.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.common.CommonDao;
import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.common.CalendarMapping;
import com.cs.mobile.api.model.common.ComStore;
import com.cs.mobile.api.model.common.Organization;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.service.common.CommonService;
import com.cs.mobile.api.service.common.RedisService;
import com.cs.mobile.common.constant.RedisKeyConstants;
import com.cs.mobile.common.utils.DateUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务实现
 * 
 * @author jiangliang
 * @date 2018年11月19日
 */
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private RedisService redisService;

	@Value("${store.db.rmsdg.userName}")
	private String dguserName;
	@Value("${store.db.rmsdg.password}")
	private String dgpassword;
	@Value("${store.db.rmsdg.sid}")
	private String dgsid;
	@Value("${store.db.rmsdg.host}")
	private String dghost;
	@Value("${store.db.rmsdg.port}")
	private String dgport;
	@Autowired
	private DruidProperties druidProperties;

	/**
	 * 根据大店编码查询小店列表
	 *
	 * @param storeId
	 * @return
	 * @throws Exception
	 */
	public List<ComStore> getComStoreList(String storeId) throws Exception {
		return commonDao.getComStoreList(storeId);
	}

	/**
	 * 根据区域ID获取所有门店
	 *
	 * @author wells
	 * @param areaId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getStoreByArea(String areaId) throws Exception {
		return commonDao.getStoreByArea(areaId);
	}

	/**
	 * 根据省份获取所有区域
	 *
	 * @author wells
	 * @param provinceId
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAreaByProvince(String provinceId) throws Exception {
		return commonDao.getAreaByProvince(provinceId);
	}

	/**
	 * 获取所有省份
	 *
	 * @author wells
	 * @return
	 * @throws Exception
	 * @time 2019年1月4日
	 */
	public List<Organization> getAllProvince() throws Exception {
		return commonDao.getAllProvince();
	}

	/**
	 *
	 * 获取当前日期的对应去年日期
	 *
	 * @param curDay
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月19日
	 *
	 */
	public String getLastYearDay(String curDay) throws Exception {
		String result = null;
		Object obj = redisService.getObject(RedisKeyConstants.REPORT_CALENDAR_MAPPING_KEY);
		if (obj == null) {// 缓存中不存在映射数据
			try {
				changeDgDataSource();
				List<CalendarMapping> list = commonDao.getAllCalendarMapping();
				if (list != null && list.size() > 0) {
					Map<String, String> map = list.stream()
							.collect(Collectors.toMap(CalendarMapping::getADay, CalendarMapping::getBDay));
					redisService.setObject(RedisKeyConstants.REPORT_CALENDAR_MAPPING_KEY, map, 24 * 60 * 60);// 缓存1天
					result = map.get(curDay);
				}
			} catch (Exception e) {
				log.error("获取当前日期的对应去年日期异常：" + e);
			} finally {
				DataSourceHolder.clearDataSource();
			}
		} else {// 缓存中存在映射数据
			Map<String, String> map = (Map<String, String>) obj;
			result = map.get(curDay);
		}
		if (result == null) {
			result = DateUtils.parseDateToStr("yyyy-MM-dd",
					DateUtils.addYears(DateUtils.dateTime("yyyy-MM-dd", curDay), -1));
		}
		return result;
	}

	/**
	 * 获取用户的组织机构层,如果用户是全司请传null
	 * @param list(用户组织权限list)
	 * @return
	 */
	@Override
	public List<CsmbOrg> queryAllCsmbOrg(List<User> list) {
		List<CsmbOrg> result = null;
		List<CsmbOrg> csmbOrgs = commonDao.queryAllCsmbOrg();
		if(null == list){
			result = csmbOrgs;
		}else{
			if(null != csmbOrgs && csmbOrgs.size() > 0){
				result = new ArrayList<>();
				//省份
				Map<String,List<CsmbOrg>> provinceMap = csmbOrgs.stream().collect(Collectors.groupingBy(CsmbOrg::getProvinceId));
				//区域
				Map<String,List<CsmbOrg>> areaMap = csmbOrgs.stream().collect(Collectors.groupingBy(CsmbOrg::getAreaId));
				for(User user : list){
					if(provinceMap.containsKey(String.valueOf(user.getOrgId()))){
						result.addAll(provinceMap.get(String.valueOf(user.getOrgId())));
						continue;
					}
					if(areaMap.containsKey(String.valueOf(user.getOrgId()))){
						result.addAll(provinceMap.get(String.valueOf(user.getOrgId())));
						continue;
					}
					for(CsmbOrg csmbOrg : csmbOrgs){//不进行map分组，主要是为了没必要在创建大的对象增加内存压力
						if(csmbOrg.getStoreId().equals(String.valueOf(user.getOrgId()))){
							result.add(csmbOrg);
							break;
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public List<CsmbOrg> queryAllCsmbOrg() {
		return commonDao.queryAllCsmbOrg();
	}

	private void changeDgDataSource() {
		GoodsDataSourceConfig goodsDataSourceConfig = new GoodsDataSourceConfig();
		goodsDataSourceConfig.setHost(dghost);
		goodsDataSourceConfig.setPort(dgport);
		goodsDataSourceConfig.setSid(dgsid);
		goodsDataSourceConfig.setStore(dgsid);
		DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, dguserName, dgpassword,
				druidProperties);
		DataSourceHolder.setDataSource(dataSourceBuilder);
	}

}
