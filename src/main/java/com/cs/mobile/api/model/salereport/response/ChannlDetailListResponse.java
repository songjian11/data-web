package com.cs.mobile.api.model.salereport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

@Data
@ApiModel(value = "ChannlDetailListResponse", description = "渠道构成明细列表")
public class ChannlDetailListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "微信购", required = true)
    private List<ChannlDetailResponse> wxList;

    @ApiModelProperty(value = "BETTER购", required = true)
    private List<ChannlDetailResponse> betterList;

    @ApiModelProperty(value = "其它购", required = true)
    private List<ChannlDetailResponse> otherList;

    @ApiModelProperty(value = "京东购", required = true)
    private List<ChannlDetailResponse> jdList;

    @ApiModelProperty(value = "扫码购", required = true)
    private List<ChannlDetailResponse> scanCodeList;

    @ApiModelProperty(value = "正常购", required = true)
    private List<ChannlDetailResponse> normalList;

    @ApiModelProperty(value = "美团购", required = true)
    private List<ChannlDetailResponse> mtList;

    public void setTaxData() throws IllegalAccessException {
        if(null != this.wxList && this.wxList.size() > 0){
            for(ChannlDetailResponse channlDetailResponse : this.wxList){
                channlDetailResponse.setTaxData();
            }
        }

        if(null != this.betterList && this.betterList.size() > 0){
            for(ChannlDetailResponse channlDetailResponse : this.betterList){
                channlDetailResponse.setTaxData();
            }
        }

        if(null != this.otherList && this.otherList.size() > 0){
            for(ChannlDetailResponse channlDetailResponse : this.otherList){
                channlDetailResponse.setTaxData();
            }
        }

        if(null != this.jdList && this.jdList.size() > 0){
            for(ChannlDetailResponse channlDetailResponse : this.jdList){
                channlDetailResponse.setTaxData();
            }
        }

        if(null != this.scanCodeList && this.scanCodeList.size() > 0){
            for(ChannlDetailResponse channlDetailResponse : this.scanCodeList){
                channlDetailResponse.setTaxData();
            }
        }

        if(null != this.normalList && this.normalList.size() > 0){
            for(ChannlDetailResponse channlDetailResponse : this.normalList){
                channlDetailResponse.setTaxData();
            }
        }

        if(null != this.mtList && this.mtList.size() > 0){
            for(ChannlDetailResponse channlDetailResponse : this.mtList){
                channlDetailResponse.setTaxData();
            }
        }
    }
}
