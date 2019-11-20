package com.cs.mobile.api.model.salereport;

import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

@Data
public class SaleTrendModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //时间点（yyyyMMdd）
    private String time;
    //销售额
    private String totalSale;
    //最终毛利额
    private String totalRate;
    //前台毛利额
    private String totalFrontDeskRate;
    //扫描毛利额
    private String totalScanningRate;
    //成本
    private String totalCost;

    //含税销售额
    private String totalSaleIn;
    //含税最终毛利额
    private String totalRateIn;
    //含税前台毛利额
    private String totalFrontDeskRateIn;
    //含税扫描毛利额
    private String totalScanningRateIn;
    //含税成本
    private String totalCostIn;

    private void setTaxData() throws IllegalAccessException {
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
    }

    public static void main(String[] args) throws IllegalAccessException {
        SaleTrendModel saleTrendModel = new SaleTrendModel();
        saleTrendModel.setTotalRateIn("1");
        saleTrendModel.setTime("2");
        saleTrendModel.setTotalCostIn("3");
        saleTrendModel.setTotalFrontDeskRateIn("4");
        saleTrendModel.setTotalScanningRateIn("5");
        saleTrendModel.setTotalSaleIn("6");
        System.out.println(saleTrendModel.toString());
        saleTrendModel.setTaxData();
    }
}
