package com.liberty.exchange.market.controller;

import com.liberty.exchange.constant.RedisValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = RedisValues.tenSec)
    @RequestMapping("index")
    public String index(@RequestParam("param") String param) {
        log.info("exchange-market get param..... >>>>>" + param);
        return "hello " + param;
    }
}
