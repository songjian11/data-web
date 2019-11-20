package com.cs.mobile.api.interceptor;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * api请求计时
 * 
 * @author wells.wong
 * @date 2018年11月18日
 */
public class ApiTimeInterceptor implements HandlerInterceptor {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final ThreadLocal<Long> time = new ThreadLocal<Long>();

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
			throws Exception {
		time.set(System.currentTimeMillis());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {
		Long now = System.currentTimeMillis();
		Long diff = now - time.get();
		if (diff > 100) {
			StringBuffer print = new StringBuffer("请求：");
			print.append(getIpAddress(httpServletRequest)).append(httpServletRequest.getRequestURI());
			Map<String, String[]> tmp = httpServletRequest.getParameterMap();
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
			print.append("[进入时间:").append(time.get()).append("][离开时间：").append(System.currentTimeMillis())
					.append("][耗时：").append(now - time.get()).append("毫秒]");
			logger.info(print.toString());
		}
	}

	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
