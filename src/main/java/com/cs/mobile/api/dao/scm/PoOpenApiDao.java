package com.cs.mobile.api.dao.scm;

import com.cs.mobile.api.dao.common.AbstractDao;
import org.springframework.stereotype.Repository;

@Repository
public class PoOpenApiDao extends AbstractDao {
    /**
     * @param supplier
     * @return java.lang.Integer
     * @author wells.wong
     * @date 2019/9/10
     */
    public Integer getPoCountBySupplier(String supplier) {
        Object[] args = {supplier};
        StringBuffer sql = new StringBuffer();
        sql.append("select count(1) from csmb_po_asn_head asn_head left join csmb_po_head head on asn_head.po_sn=head" +
                ".po_sn " +
                " where head.supplier=? ");
        return jdbcTemplate.queryForObject(sql.toString(), args, Integer.class);
    }

    /**
     * 根据主档仓库编码获取对外仓库编码
     *
     * @param wh
     * @return java.lang.String
     * @author wells.wong
     * @date 2019/9/29
     */
    public String getPhysicalWh(String wh) {
        Object[] args = {wh};
        StringBuffer sql = new StringBuffer();
        sql.append("select physical_wh from wh where wh=?");
        return jdbcTemplate.queryForObject(sql.toString(), args, String.class);
    }

    /**
     * 根据工号获取姓名
     *
     * @param personId
     * @return java.lang.String
     * @author wells.wong
     * @date 2019/9/29
     */
    public String getPersonName(String personId) {
        String result = null;
        try {
            Object[] args = {personId};
            StringBuffer sql = new StringBuffer();
            sql.append("select pt_name from csmb_ssoa_person where emp_code=?");
            result = jdbcTemplate.queryForObject(sql.toString(), args, String.class);
        } catch (Exception e) {
            return null;
        }
        return result;
    }

}
