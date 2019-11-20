package com.cs.mobile.api.model.comment;

import lombok.Data;

import java.util.Date;

@Data
public class CommentModel {
    private String id;
    //用户ID
    private String personId;
    //用户名
    private String personName;
    //吐槽内容
    private String comment;
    //吐槽时间
    private Date time;
    //预备字段=吐槽界面类型(0-其它，1-首页概览，2-销售概览，3-商品档案查询，4-报表中心，5-应用中心)
    private String mark;
}
