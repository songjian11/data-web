package com.cs.mobile.api.service.comment.impl;

import com.cs.mobile.api.dao.comment.CommentDao;
import com.cs.mobile.api.model.comment.request.CommentRequest;
import com.cs.mobile.api.model.comment.request.CommentSaveRequest;
import com.cs.mobile.api.model.comment.response.CommentResponse;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.service.comment.CommentService;
import com.cs.mobile.common.exception.BusinessException;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;
    @Override
    public void save(CommentSaveRequest param)throws Exception {
        if(StringUtils.isEmpty(param.getComment())){
            throw new BusinessException("吐槽内容不能为空");
        }
        commentDao.save(param.getPersonId(), param.getComment(), param.getMark());
    }

    @Override
    public PageResult<CommentResponse> query(CommentRequest param) throws Exception {
        return commentDao.query(param.getPage(), param.getPageSize(),param.getMark(),null);
    }
}
