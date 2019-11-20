package com.cs.mobile.api.model.dailyreport.request;

import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "FreshDailyReportRequest", description = "生鲜日报表")
public class FreshDailyReportRequest extends ReportCommonParam implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "是否可比(0-否，1-是)",required = true)
    private int isCompare;
    @ApiModelProperty(value = "查询类型(0-全部,1-品类,2-大类)",required = true)
    private int isQueryType = 0;
    @ApiModelProperty(value = "查询报表类型(0-生鲜战报,1-营运战报)",required = true)
    private int isType = 1;
    @JsonIgnore
    private List<String> categorys;
    @JsonIgnore
    private List<String> storeIds;
    @JsonIgnore
    private List<String> areaIds;
    @JsonIgnore
    private List<String> provinceIds;
    @JsonIgnore
    private List<String> deptIds;
    @JsonIgnore
    private List<String> purchaseDepts;
}
