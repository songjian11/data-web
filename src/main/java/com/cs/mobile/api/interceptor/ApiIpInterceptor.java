package com.cs.mobile.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.common.RedisService;
import com.cs.mobile.common.utils.net.HttpUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * api请求Ip验证
 * 
 * @author wells
 * @date 2019年5月31日
 */
@Slf4j
public class ApiIpInterceptor implements HandlerInterceptor {
	@Autowired
	RedisService redisService;
	@Value("${wx.message.whiteIp}")
	private String whiteIp;

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
			throws Exception {
		try {
			String[] ipArray = whiteIp.split(",");
			String curIp = HttpUtil.getRemoteHost(httpServletRequest);
			boolean result = false;
			for (String ip : ipArray) {
				if (ip.equals(curIp)) {
					result = true;
					break;
				}
			}
			if (!result) {
				ResponseUtils.fastFail(httpServletResponse, -1, "当前IP没有权限访问");
			}
			return result;
		} catch (Exception e) {
			log.error("IP拦截器出现异常：" + e);
			ResponseUtils.fastFail(httpServletResponse, -1, "用户IP获取失败");
			return false;
		}
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
