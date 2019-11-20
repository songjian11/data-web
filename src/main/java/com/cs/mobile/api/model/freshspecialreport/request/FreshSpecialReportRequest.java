package com.cs.mobile.api.model.freshspecialreport.request;

import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "freshSpecialReportRequest", description = "生鲜专题报表")
public class FreshSpecialReportRequest extends ReportCommonParam implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "是否查询月（0-否，1-是）", required = true)
    private String isMonth = "0";
    @ApiModelProperty(value = "是否last排名（0-否，1-是,查询排名时使用）", required = false)
    private String isLast = "0";
    @ApiModelProperty(value = "时间(yyyy-MM-dd)", required = true)
    private String startDate;
    @JsonIgnore
    private Date start;
    @JsonIgnore
    private List<String> categorys;
    @JsonIgnore
    private String queryCategorys;
    @JsonIgnore
    private List<String> storeIds;
    @JsonIgnore
    private List<String> areaIds;
    @JsonIgnore
    private List<String> provinceIds;
    @JsonIgnore
    private List<String> deptIds;
}
