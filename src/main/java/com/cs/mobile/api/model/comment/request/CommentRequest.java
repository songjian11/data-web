package com.cs.mobile.api.model.comment.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CommentRequest", description = "吐槽大会查询参数")
public class CommentRequest {
    @ApiModelProperty(value = "页数", required = true)
    private int page=1;
    @ApiModelProperty(value = "每页记录数", required = true)
    private int pageSize=20;
    @ApiModelProperty(value = "吐槽界面类型(0-其它，1-首页概览，2-销售概览，3-商品档案查询，4-报表中心，5-应用中心，默认其它)", required = false)
    private String mark = "0";
}
