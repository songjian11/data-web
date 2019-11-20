package com.cs.mobile.api.controller.scm;

import com.cs.mobile.api.common.DataHandler;
import com.cs.mobile.api.common.DataResult;
import com.cs.mobile.api.service.scm.PoDataTransferService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基地回货订单数据同步处理
 *
 * @author wells.wong
 * @date 2019/9/3
 */
@Api(value = "PoManger", tags = {"基地回货管理接口"})
@RestController
@RequestMapping("/api/po")
public class PoDataTransferController {
    @Autowired
    private PoDataTransferService poDataTransferService;

    @GetMapping("/doPoAsnDetail")
    public DataResult transferData() {
        poDataTransferService.transferData();
        return DataHandler.jsonResult("同步成功");
    }


}
