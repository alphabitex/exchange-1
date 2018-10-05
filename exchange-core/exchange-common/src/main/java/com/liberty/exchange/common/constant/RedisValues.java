package com.liberty.exchange.common.constant;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 4:35
 * Description: 缓存key过期时间
 */
@Component
public class RedisValues {
    // 根据key设定具体的缓存时间
    private Map<String, Long> expiresMap = new HashMap<>();
    public static final String tenSec = "tenSec";

    /**
     * 初始化  默认单位:秒
     */
    @PostConstruct
    public void init() {
        expiresMap.put(tenSec, 10L);
    }

    public  Map<String, Long> getExpiresMap() {
        return this.expiresMap;
    }
}
