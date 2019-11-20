package com.cs.mobile.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.common.RedisService;

import lombok.extern.slf4j.Slf4j;

/**
 * api请求TOKEN验证
 * 
 * @author wells.wong
 * @date 2018年11月18日
 */
@Slf4j
public class ApiTokenInterceptor implements HandlerInterceptor {
	@Autowired
	RedisService redisService;

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
			throws Exception {
		try {
			String personId = httpServletRequest.getParameter("personId");
			if (StringUtils.isEmpty(personId)) {
				ResponseUtils.fastFail(httpServletResponse, -1, "参数不正确");
				log.error("没有传入参数personId");
				return false;
			}
			UserInfo userInfo = (UserInfo) redisService.getObject("user:" + personId);
			if (userInfo == null) {
				ResponseUtils.fastFail(httpServletResponse, 222, "用户信息获取失败");
				log.error("【{}】用户信息获取失败", personId);
				return false;
			}
		} catch (Exception e) {
			log.error("拦截器出现异常：" + e);
			ResponseUtils.fastFail(httpServletResponse, 222, "用户信息获取失败");
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {
	}

}
