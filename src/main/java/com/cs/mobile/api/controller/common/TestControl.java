package com.cs.mobile.api.controller.common;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestControl {
    @RequestMapping(value = "/isok")
    public String isOk(){
        return "ok";
    }
}
