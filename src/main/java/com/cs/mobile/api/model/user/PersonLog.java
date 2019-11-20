package com.cs.mobile.api.model.user;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统用户日志持久类
 * 
 * @author wells.wong
 * @date 2019年04月18日
 */
@Data
@ApiModel(value = "用户日志", description = "用户日志对象")
public class PersonLog implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "工号", required = false)
	private String personId;
	@ApiModelProperty(value = "登陆时间", required = false)
	private Date logDate;
	@ApiModelProperty(value = "登陆页面名", required = false)
	private String logPage;
	@ApiModelProperty(value = "登陆页面地址", required = false)
	private String logUrl;

}
