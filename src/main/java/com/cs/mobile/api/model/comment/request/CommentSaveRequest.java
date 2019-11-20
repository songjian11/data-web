package com.cs.mobile.api.model.comment.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CommentSaveRequest", description = "吐槽大会保存参数")
public class CommentSaveRequest {
    //吐槽内容
    @ApiModelProperty(value = "吐槽内容", required = true)
    private String comment;
    @ApiModelProperty(value = "吐槽界面类型(0-其它，1-首页概览，2-销售概览，3-商品档案查询，4-报表中心，5-应用中心，默认其它)", required = false)
    private String mark="0";
    @JsonIgnore
    private String personId;
}
