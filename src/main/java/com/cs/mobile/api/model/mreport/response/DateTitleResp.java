package com.cs.mobile.api.model.mreport.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DateTitleResp", description = "日期标题对象")
public class DateTitleResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "当前日期", required = true)
	private String curDate;
	@ApiModelProperty(value = "去年同期", required = true)
	private String lastYearDate;
	@ApiModelProperty(value = "去年同期开始日期", required = true)
	private String lastYearBeginDate;
	@ApiModelProperty(value = "去年同期结束日期", required = true)
	private String lastYearEndDate;

	public String getCurDate() {
		return curDate.substring(2, curDate.length()).replace("-", "/");
	}

	public String getLastYearDate() {
		return lastYearDate.substring(2, lastYearDate.length()).replace("-", "/");
	}

	public String getLastYearBeginDate() {
		return lastYearBeginDate.substring(2, lastYearBeginDate.length()).replace("-", "/");
	}

	public String getLastYearEndDate() {
		return lastYearEndDate.substring(2, lastYearEndDate.length()).replace("-", "/");
	}

}
