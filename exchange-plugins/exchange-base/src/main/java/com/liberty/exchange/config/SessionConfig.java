package com.liberty.exchange.config;

import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 18-9-14
 * Time: 上午12:32
 * Description: SessionConfig
 */
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
public class SessionConfig {
}
