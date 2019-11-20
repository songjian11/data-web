package com.cs.mobile.api.model.freshreport.response;

import com.cs.mobile.api.model.salereport.response.ItemSaleRankResponse;
import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ApiModel(value = "FreshRankResponse", description = "生鲜排行榜列表")
public class FreshRankResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "排行榜", required = true)
    private List<FreshRankInfoResponse> list;
    @ApiModelProperty(value = "当前级别（1-省，2-区，3-门店，4-大类）", required = true)
    private int mark = 1;



    public void setTaxData() throws IllegalAccessException {
        if(null != this.list && this.list.size() > 0){
            for(FreshRankInfoResponse freshRankInfoResponse : this.list){
                freshRankInfoResponse.setTaxData();
            }
            list = list.stream().sorted(new Comparator<FreshRankInfoResponse>() {
                @Override
                public int compare(FreshRankInfoResponse o1, FreshRankInfoResponse o2) {
                    BigDecimal value1 = new BigDecimal(StringUtils.isEmpty(o1.getSysRate())?"0":o1.getSysRate());
                    BigDecimal value2 = new BigDecimal(StringUtils.isEmpty(o2.getSysRate())?"0":o2.getSysRate());
                    return value2.compareTo(value1);
                }
            }).collect(Collectors.toList());
        }
    }
}
