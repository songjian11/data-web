package com.cs.mobile.api.dao.scm;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.scm.StoreOrderReport;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StoreOrderReportDao extends AbstractDao {
    /**
     * 门店要货报表
     * @param depts
     * @return
     */
    public List<StoreOrderReport> queryStoreOrderReport(List<String> depts){
        StringBuilder sb = new StringBuilder("select  " +
                " shopid as storeId, " +
                " shopname as storeName, " +
                " dept as deptId, " +
                " billcount as orderNum, " +
                " goodscount as skuNum " +
                " from yaohuo_view " +
                " where 1=1 ");
        if(null != depts && depts.size() > 0){
            if(1 == depts.size()){
                sb.append(" and dept= ").append(depts.get(0)).append(" ");
            }else{
                sb.append(" and dept in (").append(listToStrNotHaveQuotationMark(depts)).append(") ");
            }
        }
        return super.queryForList(sb.toString(), new BeanPropertyRowMapper<StoreOrderReport>(StoreOrderReport.class), null);
    }
}
