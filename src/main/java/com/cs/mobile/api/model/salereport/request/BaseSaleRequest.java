package com.cs.mobile.api.model.salereport.request;

import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "BaseSaleRequest", description = "销售专题报表参数")
public class BaseSaleRequest extends ReportCommonParam implements Serializable{
    private static final long serialVersionUID = 1L;
    //开始时间
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd)", required = true)
    private String startDate;

    //结束时间
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd)", required = false)
    private String endDate;

    @ApiModelProperty(value = "全司", required = false)
    private String enterpriseId;

    @JsonIgnore
    private List<String> storeIds;
    @JsonIgnore
    private List<String> areaIds;
    @JsonIgnore
    private List<String> provinceIds;
    @JsonIgnore
    private List<String> categorys;
    @JsonIgnore
    private List<String> deptIds;
    @JsonIgnore
    private List<String> classIds;
    @JsonIgnore
    private List<String> subClassIds;
    @JsonIgnore
    private List<String> ssubClassIds;

    //开始时间
    @JsonIgnore
    private Date start;

    //结束时间
    @JsonIgnore
    private Date end;
}
