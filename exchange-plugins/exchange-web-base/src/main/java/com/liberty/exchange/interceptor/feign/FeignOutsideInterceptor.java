package com.liberty.exchange.interceptor.feign;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 3:53
 * Description: FeignOutsideInterceptor
 */
@Aspect
@Configuration
@Slf4j
public class FeignOutsideInterceptor {

    @Value("${feign.auth-token:0xe9c6f61b80505b211027955df0d45f9c12009da4}")
    private String authToken;
    @Value("${spring.application.name}")
    private String applicationName;

    @Around("@annotation(com.liberty.exchange.common.annotation.FeignApi)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("authToken");
        if (StringUtils.isEmpty(token) || !authToken.equals(token)) {
            log.error("内部feign调用" + applicationName + "失败,token=" + token);
            throw new Exception("feign 内部调用非法token");
        } else {
            return pjp.proceed();
        }
    }
}
