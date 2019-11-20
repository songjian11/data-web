package com.cs.mobile.api.controller.common;

import java.io.File;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.service.common.RedisService;
import com.cs.mobile.common.constant.RedisKeyConstants;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.weixin.corp.core.CorpWxApiClient;
import com.cs.mobile.common.weixin.corp.model.CorpWxSign;
import com.cs.mobile.common.weixin.corp.model.CorpWxUserInfo;
import com.cs.mobile.common.weixin.corp.model.WxConsts;
import com.cs.mobile.common.weixin.corp.model.WxConsts.KefuMsgType;
import com.cs.mobile.common.weixin.corp.model.WxConsts.MediaFileType;
import com.cs.mobile.common.weixin.corp.model.message.MpnewsArticle;
import com.cs.mobile.common.weixin.corp.model.message.WxCpMessage;
import com.cs.mobile.common.weixin.corp.model.message.WxCpMessageSendResult;
import com.cs.mobile.common.weixin.corp.model.message.WxMediaUploadResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"unchecked", "rawtypes"})
@Api(value = "corpWeiXin", tags = {"企业微信接口"})
@RestController
@RequestMapping("/api/weixin")
@Slf4j
public class CorpWeiXinController extends AbstractApiController {
    @Value("${wx.corpId}")
    private String corpId;
    @Value("${wx.appSecret}")
    private String appSecret;
    @Value("${wx.agentId}")
    private Integer agentId;
    @Value("${wx.otherLogo}")
    private String otherLogo;
    @Value("${wx.warnLogo}")
    private String warnLogo;
    @Value("${wx.businessLogo}")
    private String businessLogo;
    @Autowired
    RedisService redisService;

    @ApiOperation(value = "企业微信获取用户信息", notes = "企业微信获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "code", value = "企业微信授权码", required = true, dataType =
                    "String")})
    @GetMapping("/getCorpWxUserInfo")
    public DataResult<CorpWxUserInfo> getCorpWxUserInfo(HttpServletRequest request, HttpServletResponse response,
                                                        String code) {
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

    @ApiOperation(value = "获取调用jsapi的config信息", notes = "获取调用jsapi的config信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "url", value = "当前请求页面url编码后的url", required = true,
                    dataType = "String")})
    @GetMapping("/config")
    public DataResult<CorpWxSign> config(HttpServletRequest request, HttpServletResponse response, String url) {
        CorpWxSign corpWxSign = null;
        try {
            String jsTicket = CorpWxApiClient.getJSTicket(agentId.toString(), corpId, appSecret);
            corpWxSign = new CorpWxSign(corpId, jsTicket, CorpWxApiClient.urlDecodeUTF8(url));
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult(corpWxSign);
    }

    @ApiOperation(value = "发送应用消息", notes = "发送应用消息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "type", value = "消息类型(text,markdown,mpnews,file)",
                    required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "fileName", value = "只有type为file时需要", required = false,
                    dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "logoType", value = "封面类型(1.IT预警、2.业务数据推送、3.其他)", required
                    = false, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "title", value = "消息标题(只有类型为mpnews时才需要必填)", required =
                    false, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "消息内容", required = true, dataType =
                    "String"),
            @ApiImplicitParam(paramType = "query", name = "toUser", value = "发送对象，多个用‘|’隔开", required = true,
                    dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "safe", value = "表示是否是保密消息，0表示否，1表示是，默认0，2" +
                    "表示仅限在企业内分享，注意仅mpnews类型的消息支持safe值为2", required = false, dataType = "String"),
            @ApiImplicitParam(paramType = "form", name = "media", value = "文件消息时的文件", required = false, dataType =
                    "file")})
    @PostMapping("/sendMsg")
    public DataResult sendMsg(MultipartFile media, HttpServletRequest request, HttpServletResponse response,
                              String type, String logoType, String fileName, String safe, String title,
                              String content, String toUser) {
        try {
            WxCpMessage message = null;
            switch (type) {
                case KefuMsgType.TEXT: {// 文本消息
                    message = WxCpMessage.TEXT().agentId(agentId).toUser(toUser).safe(safe).content(content).build();
                    break;
                }
                case KefuMsgType.MARKDOWN: {// markdown消息
                    message = WxCpMessage.MARKDOWN().agentId(agentId).toUser(toUser).content(content).build();
                    break;
                }
                case KefuMsgType.MPNEWS: {// 图文消息
                    String logoPath = "";
                    String mediaKey = "";
                    if ("1".equals(logoType)) {
                        logoPath = warnLogo;
                        mediaKey = "mediaId:warn";
                    } else if ("2".equals(logoType)) {
                        logoPath = businessLogo;
                        mediaKey = "mediaId:business";
                    } else if ("3".equals(logoType)) {
                        logoPath = otherLogo;
                        mediaKey = "mediaId:other";
                    }
                    String mediaId = "";
                    mediaId = (String) redisService.getObject(mediaKey);
                    if (StringUtils.isEmpty(mediaId)) {
                        File file = new File(logoPath);
                        WxMediaUploadResult uploadResult = CorpWxApiClient.mediaUpload(agentId.toString(), corpId,
								appSecret,
                                WxConsts.MediaFileType.IMAGE, file);
                        log.debug(uploadResult.toString());
                        mediaId = uploadResult.getMediaId();
                        if (StringUtils.isEmpty(mediaId)) {
                            ExceptionUtils.wapperBussinessException("封面图片上传失败");
                        }
                        redisService.setObject(mediaKey, mediaId, 3 * 24 * 60 * 60 - 5 * 60 * 60);
                    }
                    MpnewsArticle article =
                            MpnewsArticle.newBuilder().title(title).thumbMediaId(mediaId).content(content)
                                    .build();
                    message =
                            WxCpMessage.MPNEWS().agentId(agentId).toUser(toUser).safe(safe).addArticle(article).build();
                    break;
                }
                case KefuMsgType.FILE: {// 文件消息
                    InputStream fileIn = media.getInputStream();
                    WxMediaUploadResult uploadResult = CorpWxApiClient.mediaUpload(agentId.toString(), corpId,
							appSecret,
                            MediaFileType.FILE,
                            fileIn, fileName, media.getSize());
                    log.debug(uploadResult.toString());
                    String mediaId = uploadResult.getMediaId();
                    message = WxCpMessage.FILE().agentId(agentId).toUser(toUser).safe(safe).mediaId(mediaId).build();
                    break;
                }
                default: {
                    // do nothing
                }
            }
            WxCpMessageSendResult result = CorpWxApiClient.messageSend(agentId.toString(), corpId, appSecret, message);
            if (0 != result.getErrCode().intValue()) {
                ExceptionUtils.wapperBussinessException(result.getErrMsg());
            }
            log.debug(result.toString());
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult("发送成功");
    }
}
