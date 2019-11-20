package com.cs.mobile.api.model.reportPage.response;

import com.cs.mobile.api.model.reportPage.RankDetail;
import com.cs.mobile.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ApiModel(value = "RankInfoResponse", description = "排行榜")
public class RankInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "当前数据等级(1-门店，2-品类，3-区域,4-大类)", required = true)
    private int mark;
    @ApiModelProperty(value = "权限等级(0-全司,1-省份,2-区域,3-门店)", required = true)
    private int grade;
    @ApiModelProperty(value = "区域销售额同比大于0的数量", required = true)
    private String areaSaleUpNum;
    @ApiModelProperty(value = "区域销售额同比小于0的数量", required = true)
    private String areaSaleDownNum;
    @ApiModelProperty(value = "区域毛利额同比大于0的数量", required = true)
    private String areaRateUpNum;
    @ApiModelProperty(value = "区域毛利额同比小于0的数量", required = true)
    private String areaRateDownNum;

    @ApiModelProperty(value = "门店销售额同比大于0的数量", required = true)
    private String storeSaleUpNum;
    @ApiModelProperty(value = "门店销售额同比小于0的数量", required = true)
    private String storeSaleDownNum;
    @ApiModelProperty(value = "门店毛利额同比大于0的数量", required = true)
    private String storeRateUpNum;
    @ApiModelProperty(value = "门店毛利额同比小于0的数量", required = true)
    private String storeRateDownNum;

    @ApiModelProperty(value = "品类销售额同比大于0的数量", required = true)
    private String deptSaleUpNum;
    @ApiModelProperty(value = "品类销售额同比小于0的数量", required = true)
    private String deptSaleDownNum;
    @ApiModelProperty(value = "品类毛利额同比大于0的数量", required = true)
    private String deptRateUpNum;
    @ApiModelProperty(value = "品类毛利额同比小于0的数量", required = true)
    private String deptRateDownNum;

    @JsonIgnore
    private String areaSaleUpNumIn;
    @JsonIgnore
    private String areaSaleDownNumIn;
    @JsonIgnore
    private String areaRateUpNumIn;
    @JsonIgnore
    private String areaRateDownNumIn;

    @JsonIgnore
    private String storeSaleUpNumIn;
    @JsonIgnore
    private String storeSaleDownNumIn;
    @JsonIgnore
    private String storeRateUpNumIn;
    @JsonIgnore
    private String storeRateDownNumIn;

    @JsonIgnore
    private String deptSaleUpNumIn;
    @JsonIgnore
    private String deptSaleDownNumIn;
    @JsonIgnore
    private String deptRateUpNumIn;
    @JsonIgnore
    private String deptRateDownNumIn;

    @ApiModelProperty(value = "区域销售额", required = true)
    private List<RankDetailResponse> areaSaleList;
    @ApiModelProperty(value = "区域毛利额", required = true)
    private List<RankDetailResponse> areaProfitList;
    @ApiModelProperty(value = "门店销售额", required = true)
    private List<RankDetailResponse> storeSaleList;
    @ApiModelProperty(value = "门店毛利额", required = true)
    private List<RankDetailResponse> storeProfitList;

    @ApiModelProperty(value = "品类销售额", required = true)
    private List<RankDetailResponse> deptSaleList;
    @ApiModelProperty(value = "品类毛利额", required = true)
    private List<RankDetailResponse> deptProfitList;

    @ApiModelProperty(value = "大类销售额", required = true)
    private List<RankDetailResponse> majorDeptSaleList;
    @ApiModelProperty(value = "大类毛利额", required = true)
    private List<RankDetailResponse> majorDeptProfitList;

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

        if(null != this.areaSaleList && this.areaSaleList.size() > 0){
            for(RankDetailResponse rankDetailResponse : this.areaSaleList){
                rankDetailResponse.setTaxData();
            }
            this.areaSaleList = this.areaSaleList.stream().sorted((o1, o2) -> {
                BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalSales()) ? "0" : o1.getTotalSales());
                BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalSales()) ? "0" : o2.getTotalSales());
                return rate2.compareTo(rate1);
            }).collect(Collectors.toList());
        }

        if(null != this.areaProfitList && this.areaProfitList.size() > 0){
            for(RankDetailResponse rankDetailResponse : this.areaProfitList){
                rankDetailResponse.setTaxData();
            }
            this.areaProfitList = this.areaProfitList.stream().sorted((o1, o2) -> {
                BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalRate()) ? "0" : o1.getTotalRate());
                BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalRate()) ? "0" : o2.getTotalRate());
                return rate2.compareTo(rate1);
            }).collect(Collectors.toList());
        }

        if(null != this.storeSaleList && this.storeSaleList.size() > 0){
            for(RankDetailResponse rankDetailResponse : this.storeSaleList){
                rankDetailResponse.setTaxData();
            }
            this.storeSaleList = this.storeSaleList.stream().sorted((o1, o2) -> {
                BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalSales()) ? "0" : o1.getTotalSales());
                BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalSales()) ? "0" : o2.getTotalSales());
                return rate2.compareTo(rate1);
            }).collect(Collectors.toList());
        }

        if(null != this.storeProfitList && this.storeProfitList.size() > 0){
            for(RankDetailResponse rankDetailResponse : this.storeProfitList){
                rankDetailResponse.setTaxData();
            }
            this.storeProfitList = this.storeProfitList.stream().sorted((o1, o2) -> {
                BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalRate()) ? "0" : o1.getTotalRate());
                BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalRate()) ? "0" : o2.getTotalRate());
                return rate2.compareTo(rate1);
            }).collect(Collectors.toList());
        }

        if(null != this.deptSaleList && this.deptSaleList.size() > 0){
            for(RankDetailResponse rankDetailResponse : this.deptSaleList){
                rankDetailResponse.setTaxData();
            }
            this.deptSaleList = this.deptSaleList.stream().sorted((o1, o2) -> {
                BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalSales()) ? "0" : o1.getTotalSales());
                BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalSales()) ? "0" : o2.getTotalSales());
                return rate2.compareTo(rate1);
            }).collect(Collectors.toList());
        }

        if(null != this.deptProfitList && this.deptProfitList.size() > 0){
            for(RankDetailResponse rankDetailResponse : this.deptProfitList){
                rankDetailResponse.setTaxData();
            }
            this.deptProfitList = this.deptProfitList.stream().sorted((o1, o2) -> {
                BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalRate()) ? "0" : o1.getTotalRate());
                BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalRate()) ? "0" : o2.getTotalRate());
                return rate2.compareTo(rate1);
            }).collect(Collectors.toList());
        }

        if(null != this.majorDeptSaleList && this.majorDeptSaleList.size() > 0){
            for(RankDetailResponse rankDetailResponse : this.majorDeptSaleList){
                rankDetailResponse.setTaxData();
            }
            this.majorDeptSaleList = this.majorDeptSaleList.stream().sorted((o1, o2) -> {
                BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalSales()) ? "0" : o1.getTotalSales());
                BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalSales()) ? "0" : o2.getTotalSales());
                return rate2.compareTo(rate1);
            }).collect(Collectors.toList());
        }

        if(null != this.majorDeptProfitList && this.majorDeptProfitList.size() > 0){
            for(RankDetailResponse rankDetailResponse : this.majorDeptProfitList){
                rankDetailResponse.setTaxData();
            }
            this.majorDeptProfitList = this.majorDeptProfitList.stream().sorted((o1, o2) -> {
                BigDecimal rate1 = new BigDecimal(StringUtils.isEmpty(o1.getTotalRate()) ? "0" : o1.getTotalRate());
                BigDecimal rate2 = new BigDecimal(StringUtils.isEmpty(o2.getTotalRate()) ? "0" : o2.getTotalRate());
                return rate2.compareTo(rate1);
            }).collect(Collectors.toList());
        }
    }
}
