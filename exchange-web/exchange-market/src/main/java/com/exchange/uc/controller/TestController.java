package com.exchange.uc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/21
 * Time: 0:26
 * Description: TestController
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("index")
    public String index(@RequestParam("param") String param) {
        log.info("exchange-market get param..... >>>>>" + param);
        return "hello " + param;
    }
}
