package com.cs.mobile.api.model.dailyreport.response;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreLargeClassMoneyListResponse", description = "门店大类库存金额")
public class StoreLargeClassMoneyListResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "门店大类库存金额", required = true)
    private List<StoreLargeClassMoneyResponse> StoreMoneyList;
	
}
