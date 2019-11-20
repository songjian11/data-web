package com.cs.mobile.api.model.common;

import java.util.List;

import lombok.Data;

/**
 * Page公用封装类
 * 
 * @author wells.wong
 * @date 2018年11月18日
 */
@Data
public class PageResult<T> {

	private int page = 0;

	private int pageSize = 0;

	private int total = 0;

	private List<T> datas = null;

}
