package com.cs.mobile.api.model.comment.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CommentResponse", description = "吐槽大会响应结果")
public class CommentResponse {
    private String id;
    //用户ID
    @ApiModelProperty(value = "用户ID", required = true)
    private String personId;
    //用户名
    @ApiModelProperty(value = "用户名", required = true)
    private String personName;
    @ApiModelProperty(value = "所属部门", required = true)
    private String department;
    @ApiModelProperty(value = "工作地址", required = true)
    private String jobAddr;
    @ApiModelProperty(value = "职责描述", required = true)
    private String jobDesc;
    //吐槽内容
    @ApiModelProperty(value = "吐槽内容", required = true)
    private String personComment;
    //吐槽时间
    @ApiModelProperty(value = "吐槽时间", required = true)
    private String time;
}
