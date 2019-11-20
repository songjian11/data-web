package com.cs.mobile.api.controller.common;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.cs.mobile.api.model.reportPage.UserDept;
import com.cs.mobile.api.model.user.User;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.common.RedisService;
import com.cs.mobile.api.service.reportPage.ReportUserDeptService;
import com.cs.mobile.api.service.user.UserService;
import com.cs.mobile.common.constant.UserTypeEnum;
import com.cs.mobile.common.exception.api.BussinessException;
import com.cs.mobile.common.exception.api.ErrorCode;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.bean.BeanUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * controller需继承AbstractApiController
 * 
 * 放置公用的方法：接口异常跟踪
 * 
 * @author wells.wong
 * @date 2018年11月18日
 */
@Slf4j
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AbstractApiController {
	@Autowired
	RedisService redisService;
	@Autowired
	UserService userService;
	@Autowired
	ReportUserDeptService reportUserDeptService;

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public DataResult handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		return this.handleException(request, response, ex, DataHandler.APP_RETURN_FAIL_CODE);
	}

	@ResponseBody
	public DataResult handleException(HttpServletRequest request, HttpServletResponse response, Exception ex,
			Integer code) {

		StringBuffer print = new StringBuffer();
		print.append("request:{");
		print.append("url:").append(request.getRequestURI());
		print.append("}");

		// 打印出调用参数
		Map<String, String[]> tmp = request.getParameterMap();
		Map<String, String[]> paramsMap = tmp instanceof TreeMap ? tmp : new TreeMap<>(tmp);

		print.append(";params:{");
		boolean flag = false;
		for (Entry<String, String[]> entry : paramsMap.entrySet()) {
			for (String value : entry.getValue()) {
				if (flag) {
					print.append(",");
				} else {
					flag = true;
				}
				print.append(entry.getKey()).append(":").append(value);
			}
		}
		print.append("}");
		if (ex instanceof BussinessException) {
			BussinessException c = (BussinessException) ex;
			if (c.getCode() != null) {
				code = c.getCode().getCode();
			}
			log.error(c.getLocalizedMessage() + ": {}\n", print.toString(), c.getCause());
		} else {
			log.error("====api异常:", ex);
		}
		return DataHandler.errorJsonResult(code, ex.getMessage(), ex);
	}

	/**
	 * 获取当前用户信息 异常情况已经在拦截器处理，因此这里直接获取
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public UserInfo getCurUserInfo(HttpServletRequest request) throws Exception {
		UserInfo result = null;
		try {
			result = (UserInfo) redisService.getObject("user:" + request.getParameter("personId"));
			if (result == null) {
				ErrorCode code = new ErrorCode(222, "用户信息获取失败");
				ExceptionUtils.wapperBussinessException(code);
			}
		} catch (Exception e) {
			log.error("获取用户信息时出错啦", e);
			ErrorCode code = new ErrorCode(222, "用户信息获取失败");
			ExceptionUtils.wapperBussinessException(code);
		}
		return result;
	}

	/**
	 * 公共获取用户数据权限范围<br>
	 * 1：指定某个小店<br>
	 * 2：指定某个大店<br>
	 * 3：指定某个区域<br>
	 * 4：指定某个省份<br>
	 * 5：全司
	 * 
	 * @param userInfo
	 * @param provinceId
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月9日
	 */
	public Map getPartnerUserRoleMap(UserInfo userInfo, String provinceId, String areaId, String storeId, String comId)
			throws Exception {
		Map result = new HashMap();
		if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_COM.getType())) {// 小店店长
			result.put("type", 1);
			result.put("storeId", userInfo.getPartnerUserInfo().getStoreId());
			result.put("comId", userInfo.getPartnerUserInfo().getComId());
		} else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_SOTRE.getType())
				|| userInfo.getTypeList().contains(UserTypeEnum.PARTNER_STOREADMIN.getType())
				|| userInfo.getTypeList().contains(UserTypeEnum.PARTNER_COMMON.getType())) {// 大店店长或者管理员大店店长或者合伙人
			if (StringUtils.isEmpty(comId)) {// 大店查看所有小店
				result.put("type", 2);
				result.put("storeId", userInfo.getPartnerUserInfo().getStoreId());
			} else {// 大店指定某个小店
				result.put("type", 1);
				result.put("storeId", userInfo.getPartnerUserInfo().getStoreId());
				result.put("comId", comId);
			}
		} else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_AREAADMIN.getType())) {// 区域管理员
			if (StringUtils.isNotEmpty(storeId) && StringUtils.isNotEmpty(comId)) {// 指定某个小店
				result.put("type", 1);
				result.put("storeId", storeId);
				result.put("comId", comId);
			} else if (StringUtils.isNotEmpty(storeId)) {// 指定某个门店
				result.put("type", 2);
				result.put("storeId", storeId);
			} else {// 门店管理员查看自己所在区域的所有门店
				result.put("type", 3);
				result.put("areaId", userInfo.getPartnerUserInfo().getAreaId());
			}
		} else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_PROVINCEADMIN.getType())) {// 省份管理员
			if (StringUtils.isNotEmpty(areaId) && StringUtils.isNotEmpty(storeId) && StringUtils.isNotEmpty(comId)) {// 指定某个小店
				result.put("type", 1);
				result.put("storeId", storeId);
				result.put("comId", comId);
			} else if (StringUtils.isNotEmpty(areaId) && StringUtils.isNotEmpty(storeId)) {// 指定某个门店
				result.put("type", 2);
				result.put("storeId", storeId);
			} else if (StringUtils.isNotEmpty(areaId)) {// 指定某个区域
				result.put("type", 3);
				result.put("areaId", areaId);
			} else {// 区域管理员查看自己所在省份的所有区域
				result.put("type", 4);
				result.put("provinceId", userInfo.getPartnerUserInfo().getProvinceId());
			}
		} else if (userInfo.getTypeList().contains(UserTypeEnum.PARTNER_ENTERPRISEADMIN.getType())
				|| userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())) {// 省份管理员、超级管理员
			if (StringUtils.isNotEmpty(provinceId) && StringUtils.isNotEmpty(areaId) && StringUtils.isNotEmpty(storeId)
					&& StringUtils.isNotEmpty(comId)) {// 指定某个小店
				result.put("type", 1);
				result.put("storeId", storeId);
				result.put("comId", comId);
			} else if (StringUtils.isNotEmpty(provinceId) && StringUtils.isNotEmpty(areaId)
					&& StringUtils.isNotEmpty(storeId)) {// 指定某个门店
				result.put("type", 2);
				result.put("storeId", storeId);
			} else if (StringUtils.isNotEmpty(provinceId) && StringUtils.isNotEmpty(areaId)) {// 指定某个区域
				result.put("type", 3);
				result.put("areaId", areaId);
			} else if (StringUtils.isNotEmpty(provinceId)) {// 指定某个省份
				result.put("type", 4);
				result.put("provinceId", provinceId);
			} else {// 省份管理员查看全司所有省份
				result.put("type", 5);
			}
		}
		return result;
	}

	/**
	 * 报表权限通用处理器
	 * 
	 * @param param
	 * @param userInfo
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年6月5日
	 */
	public ReportCommonParam reportRoleHandler(ReportCommonParam param, UserInfo userInfo) throws Exception {
		ReportCommonParam result = new ReportCommonParam();
		List<User> userOrgList = null;
		BeanUtils.copyProperties(param, result);
		if (userInfo.getTypeList().contains(UserTypeEnum.SUPERADMIN.getType())
				|| userInfo.getTypeList().contains(UserTypeEnum.MREPORT_ALL.getType())) {// 全司管理员、超级管理员
			// do nothing
		} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_PROVINCEADMIN.getType())) {// 省份管理员
			userOrgList = userService.getUserOrgListByPersonId(userInfo.getPersonId(),
					UserTypeEnum.MREPORT_PROVINCEADMIN.getType());
			String provinceIds = param.getProvinceId();
			if (StringUtils.isEmpty(provinceIds)) {
				result.setProvinceId(this.orgListToString(userOrgList));
			}

		} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_AREAADMIN.getType())) {// 区域管理员
			userOrgList = userService.getUserOrgListByPersonId(userInfo.getPersonId(),
					UserTypeEnum.MREPORT_AREAADMIN.getType());
			String areaIds = param.getAreaId();
			if (StringUtils.isEmpty(areaIds)) {
				result.setAreaId(this.orgListToString(userOrgList));
			}
		} else if (userInfo.getTypeList().contains(UserTypeEnum.MREPORT_STOREADMIN.getType())) {// 门店管理员
			userOrgList = userService.getUserOrgListByPersonId(userInfo.getPersonId(),
					UserTypeEnum.MREPORT_STOREADMIN.getType());
			String storeIds = param.getStoreId();
			if (StringUtils.isEmpty(storeIds)) {
				result.setStoreId(this.orgListToString(userOrgList));
			}
		} else {
			ExceptionUtils.wapperBussinessException("你没有权限，请联系管理员");
		}
		String deptIds = param.getDeptId();
		String category = param.getCategory();
		List<UserDept> userDeptList = reportUserDeptService.getUserDeptList(userInfo.getPersonId());
		List<UserDept> allDeptList = reportUserDeptService.getAllDept();
		if (StringUtils.isEmpty(deptIds)) {// 传入的大类参数为空的时候才需要设置大类，否则以传入的大类参数为准
			UserDept userDept = null;
			if (StringUtils.isNotEmpty(category)) {// 过滤品类条件
				result.setDeptId(this.deptFilter(category, allDeptList, userDeptList));
			} else {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < userDeptList.size(); i++) {
					userDept = userDeptList.get(i);
					if (userDept.getDeptId().intValue() == 0) {
						result.setDeptId(null);// 大类为0表示是全部大类，因此不需要设置大类条件
						break;
					} else {
						sb.append(userDept.getDeptId());
						if (i < userDeptList.size() - 1) {
							sb.append(",");
						}
					}
				}
				result.setDeptId(sb.toString());
			}
		}else{//如果大类不为空，则取权限内的大类
			String[] strArr = deptIds.split(",");
			List<String> deptList = new ArrayList<>();

			for(int i=0;i<strArr.length;i++){
				deptList.add(strArr[i]);
			}

			if (StringUtils.isNotEmpty(category)) {// 过滤品类条件
				String[] categoryArr = category.split(",");
				for(int i = 0; i < categoryArr.length; i++){
					String deptStr = this.deptFilter(categoryArr[i], allDeptList, userDeptList);
					if(StringUtils.isNotEmpty(deptStr)){
						String[] deptArr = deptStr.split(",");
						List<String> depts = new ArrayList<>();
						for(int k=0; k<deptArr.length; k++){
							depts.add(deptArr[k]);
						}
						if(deptList.size() > 0){
							boolean isHave = false;
							for(String exists : deptList){
								if(depts.contains(exists)){
									isHave = true;
									break;
								}
							}
							if(!isHave){
								deptList.addAll(depts);
							}
						}
					}
				}
				StringBuilder sb = new StringBuilder();
				for(String deptId : deptList){
					sb.append(deptId).append(",");
				}
				if(sb.toString().length() > 0){
					result.setDeptId(sb.toString().substring(0,sb.toString().lastIndexOf(",")));
				}else{//表示大类没有交集，不允许查询结果
					result.setDeptId("-9999");
				}
			}/*else{// 品类为空,过滤大类条件
				StringBuilder sb = new StringBuilder();

				if(null != userDeptList && userDeptList.size() > 0){
					if(1 == userDeptList.size() && "0".equals(userDeptList.get(0).getDeptId())){//零表示全司
						sb.append(deptIds);
					}else{
						for(String dept : deptList){
							for(UserDept userDept : userDeptList){
								if(dept.equals(userDept.getDeptId())){
									sb.append(dept).append(",");
									break;
								}
							}
						}
					}
				}

				if(sb.toString().length() > 0){
					result.setDeptId(sb.toString().substring(0,sb.toString().lastIndexOf(",")));
				}else{//表示大类没有交集，不允许查询结果
					result.setDeptId("-9999");
				}
			}*/
		}
		return result;
	}

	private String deptFilter(String category, List<UserDept> allDeptList, List<UserDept> userDeptList) {
		StringBuffer sb = new StringBuffer();
		List<UserDept> list = new ArrayList<UserDept>();

		Map<String, List<UserDept>> categoryDeptMap = allDeptList.stream()
				.collect(Collectors.groupingBy(UserDept::getCategory));

		String[] paramCategroyArray = category.split(",");
		for (String cat : paramCategroyArray) {
			if (categoryDeptMap.containsKey(cat)) {
				list.addAll(categoryDeptMap.get(cat));
			}
		}

		Map<Integer, String> userDeptMap = list.stream()
				.collect(Collectors.toMap(UserDept::getDeptId, UserDept::getDeptName));

		UserDept userDept = null;
		for (int i = 0; i < userDeptList.size(); i++) {
			userDept = userDeptList.get(i);
			if (userDept.getDeptId().intValue() == 0) {
				sb.setLength(0);// 清空
				sb = this.deptListToString(list);// 大类为0表示是全部大类，因此将过滤后的所有大类作为查询条件
				break;
			} else if (userDeptMap.containsKey(userDept.getDeptId())) {
				sb.append(userDept.getDeptId());
				if (i < userDeptList.size() - 1) {
					sb.append(",");
				}
			}
		}
		return sb.toString();
	}

	private StringBuffer deptListToString(List<UserDept> list) {
		StringBuffer sb = new StringBuffer();
		UserDept userDept = null;
		for (int i = 0; i < list.size(); i++) {
			userDept = list.get(i);
			sb.append(userDept.getDeptId());
			if (i < list.size() - 1) {
				sb.append(",");
			}
		}
		return sb;
	}

	private String orgListToString(List<User> userOrgList) {
		StringBuffer sb = new StringBuffer();
		User user = null;
		for (int i = 0; i < userOrgList.size(); i++) {
			user = userOrgList.get(i);
			sb.append(user.getOrgId());
			if (i < userOrgList.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * 检查门店支援报名是否截止
	 * 
	 * @param deadLine
	 * @return
	 * @author wells
	 * @date 2019年1月21日
	 */
	public boolean towerTeamOutOfTime(String deadLine) {
		return (DateUtil.getNowDate().getTime() >= DateUtil.toDate(deadLine, "yyyy-MM-dd hh:mm:ss").getTime());
	}

	/**
	 * 将reportCommonParam的数据赋值给obj
	 * @param reportCommonParam
	 * @param target
	 * @throws Exception
	 */
	public void setValue(ReportCommonParam reportCommonParam, Object target) throws Exception {
		if(null != reportCommonParam && null != target){
			Field[] fields = FieldUtils.getAllFields(target.getClass());
			if(null != fields && fields.length > 0){
				for(int i = 0;i < fields.length; i++){
					fields[i].setAccessible(true);
					if(fields[i].getName().equals("category")){
						fields[i].set(target,reportCommonParam.getCategory());
					}else if(fields[i].getName().equals("deptId")){
						fields[i].set(target,reportCommonParam.getDeptId());
					}else if(fields[i].getName().equals("storeId")){
						fields[i].set(target,reportCommonParam.getStoreId());
					}else if(fields[i].getName().equals("areaId")){
						fields[i].set(target,reportCommonParam.getAreaId());
					}else if(fields[i].getName().equals("provinceId")){
						fields[i].set(target,reportCommonParam.getProvinceId());
					}
				}
			}
		}
	}

	/**
	 * 战报专用
	 * @param param
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	protected ReportCommonParam getDepts(ReportCommonParam param, UserInfo userInfo) throws Exception {
		ReportCommonParam result = new ReportCommonParam();
		BeanUtils.copyProperties(param, result);
		String deptIds = param.getDeptId();
		String category = param.getCategory();
		List<UserDept> userDeptList = reportUserDeptService.getUserDeptList(userInfo.getPersonId());
		List<UserDept> allDeptList = reportUserDeptService.getAllDept();
		if (StringUtils.isEmpty(deptIds)) {// 传入的大类参数为空的时候才需要设置大类，否则以传入的大类参数为准
			UserDept userDept = null;
			if (StringUtils.isNotEmpty(category)) {// 过滤品类条件
				result.setDeptId(deptFilter(category, allDeptList, userDeptList));
			} else {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < userDeptList.size(); i++) {
					userDept = userDeptList.get(i);
					if (userDept.getDeptId().intValue() == 0) {
						result.setDeptId(null);// 大类为0表示是全部大类，因此不需要设置大类条件
						break;
					} else {
						sb.append(userDept.getDeptId());
						if (i < userDeptList.size() - 1) {
							sb.append(",");
						}
					}
				}
				result.setDeptId(sb.toString());
			}
		}else{//如果大类不为空，则取权限内的大类
			String[] strArr = deptIds.split(",");
			List<String> deptList = new ArrayList<>();

			for(int i=0;i<strArr.length;i++){
				deptList.add(strArr[i]);
			}

			if (StringUtils.isNotEmpty(category)) {// 过滤品类条件
				String[] categoryArr = category.split(",");
				for(int i = 0; i < categoryArr.length; i++){
					String deptStr = this.deptFilter(categoryArr[i], allDeptList, userDeptList);
					if(StringUtils.isNotEmpty(deptStr)){
						String[] deptArr = deptStr.split(",");
						List<String> depts = new ArrayList<>();
						for(int k=0; k<deptArr.length; k++){
							depts.add(deptArr[k]);
						}
						if(deptList.size() > 0){
							boolean isHave = false;
							for(String exists : deptList){
								if(depts.contains(exists)){
									isHave = true;
									break;
								}
							}
							if(!isHave){
								deptList.addAll(depts);
							}
						}
					}
				}
				StringBuilder sb = new StringBuilder();
				for(String deptId : deptList){
					sb.append(deptId).append(",");
				}
				if(sb.toString().length() > 0){
					result.setDeptId(sb.toString().substring(0,sb.toString().lastIndexOf(",")));
				}else{//表示大类没有交集，不允许查询结果
					result.setDeptId("-9999");
				}
			}
		}
		return result;
	}
}
