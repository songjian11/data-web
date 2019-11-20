package com.cs.mobile.api.controller.comment;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.comment.request.CommentRequest;
import com.cs.mobile.api.model.comment.request.CommentSaveRequest;
import com.cs.mobile.api.model.comment.response.CommentResponse;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.comment.CommentService;
import com.cs.mobile.api.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "commentControl", tags = { "反馈中心" })
@RestController
@RequestMapping("/api/comment")
@Slf4j
public class CommentControl extends AbstractApiController {
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;

    @ApiOperation(value = "反馈中心保存", notes = "反馈中心保存")
    @PostMapping("/save")
    public DataResult<String> save(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String personId,
                                   @RequestBody @ApiParam(value = "反馈中心参数", required = true, name = "param")
                                           CommentSaveRequest param) {
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "反馈中心-保存");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            UserInfo userInfo = this.getCurUserInfo(request);
            if(null != userInfo){
                param.setPersonId(userInfo.getPersonId());
            }
            commentService.save(param);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult("反馈成功！");
    }

    @ApiOperation(value = "反馈中心查询", notes = "反馈中心查询")
    @PostMapping("/query")
    public DataResult<List<CommentResponse>> query(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   String personId,
                                                   @RequestBody @ApiParam(value = "反馈中心参数", required = true, name = "param") CommentRequest param) {
        PageResult<CommentResponse> pageResult = null;
        // 记录访问日志
        try {
            userService.addPersonLog(personId, "反馈中心-查询");
        } catch (Exception e) {
            log.error("访问日志保存出错", e);
        }
        try {
            pageResult = commentService.query(param);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(pageResult);
    }

}
