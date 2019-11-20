package com.cs.mobile.api.controller.common;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.service.common.RedisService;
import com.cs.mobile.common.constant.RedisKeyConstants;
import com.cs.mobile.common.weixin.corp.core.CorpWxApiClient;
import com.cs.mobile.common.weixin.corp.model.CorpWxUserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings({"unchecked", "rawtypes"})
@Api(value = "qywx", tags = {"企业微信授权接口"})
@RestController
@RequestMapping("/api/qywx")
@Slf4j
public class QyWeiXinController extends AbstractApiController {
    @Value("${scmwx.corpId}")
    private String corpId;
    @Value("${scmwx.appSecret}")
    private String appSecret;
    @Value("${scmwx.agentId}")
    private Integer agentId;
    @Autowired
    RedisService redisService;

    @ApiOperation(value = "企业微信获取用户信息", notes = "企业微信获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "code", value = "企业微信授权码", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "appCode", value = "应用编码(BI智慧:csbi，采购工作台:scm)", required =
                    true, dataType = "String"),
    })
    @GetMapping("/getCorpWxUserInfo")
    public DataResult<CorpWxUserInfo> getCorpWxUserInfo(HttpServletRequest request, HttpServletResponse response,
                                                        String code, String appCode) {
        //TODO(根据应用编码查询corpId,appSecret,agentId)
        CorpWxUserInfo corpWxUserInfo = null;
        String userKey = String.format(RedisKeyConstants.TT_CORPWX_USER_KEY, code);// 微信用户缓存KEY
        try {
            Object obj = redisService.getObject(userKey);
            if (obj != null) {
                corpWxUserInfo = (CorpWxUserInfo) obj;
            }
        } catch (Exception e) {
            log.error("getCorpWxUserInfo read redis erro : " + e);
        }
        try {
            if (corpWxUserInfo == null) {// 缓存中无记录
                String userId = CorpWxApiClient.getUserId(agentId.toString(), corpId, appSecret, code);
                corpWxUserInfo = CorpWxApiClient.getUserInfo(agentId.toString(), corpId, code, userId);
                redisService.setObject(userKey, corpWxUserInfo, 5 * 60);// 缓存5分钟
            }
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(corpWxUserInfo);
    }
}
