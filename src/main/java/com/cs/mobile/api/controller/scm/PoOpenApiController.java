package com.cs.mobile.api.controller.scm;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.controller.common.AbstractApiController;
import com.cs.mobile.api.model.scm.openapi.CheckResult;
import com.cs.mobile.api.model.scm.openapi.PoCountResp;
import com.cs.mobile.api.model.scm.openapi.PoReq;
import com.cs.mobile.api.service.scm.PoOpenApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "PoOpenApi", tags = {"基地回货开放接口"})
@RestController
@RequestMapping("/openapi/scm")
@Slf4j
public class PoOpenApiController extends AbstractApiController {
    @Autowired
    private PoOpenApiService poOpenApiService;

    @ApiOperation(value = "根据基地编码查询基地回货次数", notes = "根据基地编码查询基地回货次数")
    @GetMapping("/getPoCountBySupplier")
    public DataResult<PoCountResp> getPoCountBySupplier(String supplier) {
        return DataHandler.jsonResult(poOpenApiService.getPoCountBySupplier(supplier));
    }

    @ApiOperation(value = "创建审核通过的订单", notes = "创建审核通过的订单")
    @PostMapping("/createPo")
    public DataResult createPo(HttpServletRequest request, HttpServletResponse response, @RequestBody PoReq poReq) {
        try {
            log.debug("传入参数：" + poReq.toString());
            CheckResult checkResult = poOpenApiService.checkData(poReq);
            poOpenApiService.creatPo(poReq, checkResult);
        } catch (Exception e) {
            return super.handleException(request, response, e);
        }
        return DataHandler.jsonResult("订单创建成功");
    }
}
