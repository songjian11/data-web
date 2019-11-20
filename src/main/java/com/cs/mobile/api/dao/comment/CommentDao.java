package com.cs.mobile.api.dao.comment;

import com.cs.mobile.api.dao.common.AbstractDao;
import com.cs.mobile.api.model.comment.response.CommentResponse;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.common.utils.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDao extends AbstractDao {
    private static final String COMMENT_SAVE_PREFIX = "insert into csmb_comment(person_id,person_comment, mark)VALUES(";

    private static final String COMMENT_QUERY_PREFIX = "SELECT a.id, " +
            " a.person_id as personId, " +
            " b.pt_name as personName, " +
            " b.descr as jobAddr, " +
            " b.store_name as department, " +
            " b.job_desc as jobDesc, " +
            " a.person_comment as personComment, " +
            " to_char(a.time, 'yyyy-mm-dd hh24:mi:ss') as time " +
            " FROM csmb_comment a,csmb_ssoa_person b " +
            " where a.person_id = b.emp_code ";

    public void save(String personId,
                     String comment,
                     String mark){
        StringBuilder sb = new StringBuilder(COMMENT_SAVE_PREFIX);
        if(StringUtils.isNotEmpty(personId)){
            sb.append(" '").append(personId).append("', ");
        }
        if(StringUtils.isNotEmpty(comment)){
            sb.append(" '").append(comment).append("', ");
        }
        sb.append(" '").append(mark).append("')");
        jdbcTemplate.execute(sb.toString());
    }

    public PageResult<CommentResponse> query(int page,
                                             int pageSize,
                                             String mark,
                                             String personId){
        StringBuilder sb = new StringBuilder(COMMENT_QUERY_PREFIX);
        if(StringUtils.isNotEmpty(personId)){
            sb.append(" and a.person_id ='").append(personId).append("' ");
        }
        //吐槽界面类型(0-其它，1-首页概览，2-销售概览，3-商品档案查询，4-报表中心，5-应用中心，默认其它)
        if(StringUtils.isNotEmpty(mark)){
            sb.append(" and a.mark ='").append(mark).append("' ");
        }
        return super.queryByPage(sb.toString(), CommentResponse.class, page, pageSize, "a.time", Sort.DESC,
                null);
    }
}
