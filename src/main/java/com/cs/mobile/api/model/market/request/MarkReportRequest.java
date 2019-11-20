package com.cs.mobile.api.model.market.request;

import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "MarkReportRequest", description = "市场报表")
public class MarkReportRequest extends ReportCommonParam implements Serializable {
    private static final long serialVersionUID = 1L;
    //开始时间
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd)", required = true)
    private String startDate;

    @ApiModelProperty(value = "全司", required = false)
    private String enterpriseId;

    @ApiModelProperty(value = "部门ID", required = false)
    private String purchaseDept;

    @ApiModelProperty(value = "是否查询月", required = false)
    private int isMonth = 0;

    @ApiModelProperty(value = "查询数据类型(0-全部，1-分部，2-品类，3-大类)", required = false)
    private int isQuery = 0;

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

    //开始时间
    @JsonIgnore
    private Date start;
}
