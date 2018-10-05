package com.liberty.exchange.interceptor;

import com.google.common.collect.ImmutableList;
import com.liberty.exchange.common.annotation.LimitType;
import com.liberty.exchange.common.annotation.RouterLimit;
import com.liberty.exchange.common.exception.RateLimitException;
import com.liberty.exchange.common.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 3:38
 * Description: 限流拦截器
 */
@Aspect
@Configuration
@Slf4j
public class RateLimitInterceptor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Value("${ratelimit.period:60}")
    private int period;
    @Value("${ratelimit.count:10000}")
    private int count;

    private static final String keyPrefix = "redis.rateLimit:";


    @Around("(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)) || " +
            "@annotation(com.liberty.exchange.common.annotation.RouterLimit)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RouterLimit limitAnnotation = method.getAnnotation(RouterLimit.class);
        LimitType limitType = LimitType.IP;
        String name = "";
        String key = "";
        int limitPeriod = period;
        int limitCount = count;
        String prefix = keyPrefix;
        if (null != limitAnnotation) {
            name = limitAnnotation.name();
            key = limitAnnotation.key();
            limitPeriod = limitAnnotation.period();
            limitCount = limitAnnotation.count();
            prefix = limitAnnotation.prefix();
            limitType = limitAnnotation.limitType();
        }
        switch (limitType) {
            case IP:
                key = RequestUtil.remoteIp(request);
                break;
            case CUSTOMER:
                //  如果此处可根据表达式或者一些规则生成
                key = buildKey(key);
                break;
            default:
                key = StringUtils.upperCase(method.getName());
        }
        ImmutableList<String> keys = ImmutableList.of(StringUtils.join(prefix, key));
        String luaScript = buildLuaScript();
        RedisScript<Number> redisScript = new DefaultRedisScript<>(luaScript, Number.class);
        Number count = redisTemplate.execute(redisScript, keys, limitCount, limitPeriod);
        log.info("Access try count is {} for name={} and key = {}", count, name, key);
        if (count != null && count.intValue() <= limitCount) {
            return pjp.proceed();
        } else {
            throw new RateLimitException("暂时加入黑名单");
        }
    }

    public String buildKey(String key) {
        return key;
    }

    /**
     * 限流 脚本
     *
     * @return lua脚本
     */
    public String buildLuaScript() {
        // 调用不超过最大值，则直接返回
        // 执行计算器自加
        // 从第一次调用开始限流，设置对应键值的过期
        return "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                "\nredis.call('expire',KEYS[1],ARGV[2])" +
                "\nend" +
                "\nreturn c;";
    }
}
