package com.cs.mobile.api.model.freshspecialreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "freshSpecialReportRankListResponse",description = "生鲜排行榜列表")
public class FreshSpecialReportRankListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "生鲜排行榜明细",required = true)
    private List<FreshSpecialReportRankResponse> list;
    @ApiModelProperty(value = "用户权限等级(1-全司，2-省份，3-区域，4-门店)",required = true)
    private int grade;

    public void setTaxData() throws IllegalAccessException {
        if(null != this.list && this.list.size() > 0){
            for(FreshSpecialReportRankResponse freshSpecialReportRankResponse : this.list){
                freshSpecialReportRankResponse.setTaxData();
            }
        }
    }
}
