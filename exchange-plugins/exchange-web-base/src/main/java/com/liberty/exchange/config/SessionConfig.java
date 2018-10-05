package com.liberty.exchange.config;

import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 18-9-14
 * Time: 上午12:32
 * Description: session分布式缓存配置
 */
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400,redisFlushMode = RedisFlushMode.IMMEDIATE)
public class SessionConfig {
}
