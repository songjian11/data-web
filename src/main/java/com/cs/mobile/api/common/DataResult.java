package com.cs.mobile.api.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResult<T> implements Serializable {

	private static final long serialVersionUID = 3997124446365032582L;

	@ApiModelProperty(value = "状态码0表示成功,-1表示失败")
	private Integer code;
	@ApiModelProperty(value = "数据")
	private T data;
	@ApiModelProperty(value = "消息提示")
	private String msg;
	@ApiModelProperty(value = "总记录数")
	private int total;
	@ApiModelProperty(value = "当前页")
	private int page;
	@ApiModelProperty(value = "每页数据数")
	private int pageSize;
	@ApiModelProperty(value = "总页数")
	private int totalPage;

	public DataResult() {
		super();
	}

	public DataResult(Integer code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public DataResult(Integer code, String msg, T data) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public DataResult(Integer code, String msg, T data, int total, int page, int pageSize) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
		this.total = total;
		this.page = page;
		this.pageSize = pageSize;
		this.totalPage = (total + pageSize - 1) / pageSize;
	}
}
