package com.cs.mobile.api.service.comment;

import com.cs.mobile.api.model.comment.request.CommentRequest;
import com.cs.mobile.api.model.comment.request.CommentSaveRequest;
import com.cs.mobile.api.model.comment.response.CommentResponse;
import com.cs.mobile.api.model.common.PageResult;

public interface CommentService {
    void save(CommentSaveRequest param)throws Exception;

    PageResult<CommentResponse> query(CommentRequest param)throws Exception;
}
