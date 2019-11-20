package com.cs.mobile.api.model.salereport.response;

import com.cs.mobile.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "ChannlResponse", description = "渠道构成")
public class ChannlResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //美团购
    @ApiModelProperty(value = "美团购", required = true)
    private String meituanPay = "0";
    //微信购
    @ApiModelProperty(value = "微信购", required = true)
    private String weixinPay = "0";
    //京东购
    @ApiModelProperty(value = "京东购", required = true)
    private String jingdongPay = "0";
    //better购
    @ApiModelProperty(value = "better购", required = true)
    private String betterPay = "0";
    //扫码购
    @ApiModelProperty(value = "扫码购", required = true)
    private String scancodePay = "0";
    //正常
    @ApiModelProperty(value = "正常", required = true)
    private String normalPay = "0";
    //其它
    @ApiModelProperty(value = "其它", required = true)
    private String otherPay = "0";

    //美团购
    @JsonIgnore
    private String meituanPayIn = "0";
    //微信购
    @JsonIgnore
    private String weixinPayIn = "0";
    //京东购
    @JsonIgnore
    private String jingdongPayIn = "0";
    //better购
    @JsonIgnore
    private String betterPayIn = "0";
    //扫码购
    @JsonIgnore
    private String scancodePayIn = "0";
    //正常
    @JsonIgnore
    private String normalPayIn = "0";
    //其它
    @JsonIgnore
    private String otherPayIn = "0";

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

    public String getMeituanPay() {
        BigDecimal value = StringUtils.isEmpty(meituanPay) ? BigDecimal.ZERO : new BigDecimal(meituanPay).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getWeixinPay() {
        BigDecimal value = StringUtils.isEmpty(weixinPay) ? BigDecimal.ZERO : new BigDecimal(weixinPay).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getJingdongPay() {
        BigDecimal value = StringUtils.isEmpty(jingdongPay) ? BigDecimal.ZERO : new BigDecimal(jingdongPay).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getBetterPay() {
        BigDecimal value = StringUtils.isEmpty(betterPay) ? BigDecimal.ZERO : new BigDecimal(betterPay).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getScancodePay() {
        BigDecimal value = StringUtils.isEmpty(scancodePay) ? BigDecimal.ZERO : new BigDecimal(scancodePay).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getNormalPay() {
        BigDecimal value = StringUtils.isEmpty(normalPay) ? BigDecimal.ZERO : new BigDecimal(normalPay).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public String getOtherPay() {
        BigDecimal value = StringUtils.isEmpty(otherPay) ? BigDecimal.ZERO : new BigDecimal(otherPay).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public void setTaxData() throws IllegalAccessException {
        Field[] fields = FieldUtils.getAllFields(this.getClass());
        for(int i=0; i < fields.length; i++){
            Field field = fields[i];
            field.setAccessible(true);
            if(field.getName().contains("In")){
                for(int j=0; j < fields.length; j++){
                    Field ff = fields[j];
                    ff.setAccessible(true);
                    String name = ff.getName() + "In";
                    if(name.equals(field.getName())){
                        ff.set(this,field.get(this));
                        break;
                    }
                }
            }
        }

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
