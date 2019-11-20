package com.cs.mobile.api.common;

import org.springframework.dao.EmptyResultDataAccessException;

import com.alibaba.druid.util.StringUtils;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.common.exception.api.BussinessException;

/**
 * api接口请求返回处理
 * 
 * @author wells.wong
 * @date 2018年11月18日
 */
@SuppressWarnings("rawtypes")
public class DataHandler {

	public static final int APP_RETURN_SUCCESS_CODE = 0;// 请求处理成功
	public static final int APP_RETURN_SUCCESS_NO_DATA = 1;// 请求处理成功,但无数据
	public static final int APP_RETURN_FAIL_CODE = -1;// 请求处理失败
	public static final int APP_RETURN_EX_CODE = -2;// 请求处理失败
	public static final String REQUEST_SUCCESS = "SUCCESS";

	/**
	 * 封装app接口返回json结构
	 * 
	 * @param code
	 *            返回代码[0:正常,-1:错误,... 其它自定义]
	 * @param msg
	 *            提示信息
	 * @param data
	 *            响应数据
	 * @return
	 */
	public static DataResult jsonResult(int code, String msg, Object data) {
		return new DataResult<>(code, msg, data);
	}

	public static DataResult jsonResultByPage(int code, String msg, PageResult data) {
		return new DataResult<>(code, msg, data.getDatas(), data.getTotal(), data.getPage(), data.getPageSize());
	}

	public static DataResult exceptionJsonResult(String msg, Throwable e) {
		return errorJsonResult(APP_RETURN_SUCCESS_CODE, msg, e);
	}

	public static DataResult errorJsonResult(String msg, Throwable e) {
		return errorJsonResult(APP_RETURN_FAIL_CODE, msg, e);
	}

	public static DataResult errorJsonResult(int code, String msg, Throwable e) {
		if (e instanceof EmptyResultDataAccessException) {
			return jsonNoDataResult("未找到相应数据");
		}
		if (e instanceof BussinessException) {
			msg = e.getMessage();
		}
		if (StringUtils.isEmpty(msg)) {
			msg = e.getMessage();
		}
		return new DataResult<>(code, msg);
	}

	public static DataResult jsonResult(Object data) {
		if (data instanceof PageResult) {
			return jsonResultByPage(APP_RETURN_SUCCESS_CODE, REQUEST_SUCCESS, (PageResult) data);
		}
		return jsonResult(APP_RETURN_SUCCESS_CODE, REQUEST_SUCCESS, data);
	}

	public static DataResult jsonNoDataResult(String msg) {
		return jsonResult(APP_RETURN_SUCCESS_NO_DATA, msg, null);
	}

	public static DataResult jsonResult(String msg) {
		return jsonResult(APP_RETURN_SUCCESS_CODE, msg, null);
	}
}
