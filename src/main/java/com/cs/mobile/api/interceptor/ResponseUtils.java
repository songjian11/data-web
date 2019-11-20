package com.cs.mobile.api.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.cs.mobile.api.common.DataResult;

/**
 * 响应工具
 * 
 * @author wells.wong
 * @date 2018年11月30日
 */
public class ResponseUtils {
	public static void fastFail(HttpServletResponse response, int code, String msg) throws IOException {
		response.setHeader("Content-Type", "application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String result = JSON.toJSONString(new DataResult<>(code, msg));
		out.print(result);
		out.close();
	}
}
