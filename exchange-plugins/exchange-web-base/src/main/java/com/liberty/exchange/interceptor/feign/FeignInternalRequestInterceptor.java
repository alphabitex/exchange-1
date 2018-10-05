package com.liberty.exchange.interceptor.feign;

import com.netflix.ribbon.RequestTemplate;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 3:50
 * Description: FeignInternalRequestInterceptor
 */
@Configuration
public class FeignInternalRequestInterceptor implements RequestInterceptor {

    @Value("${feign.auth-token:0xe9c6f61b80505b211027955df0d45f9c12009da4}")
    private String authToken;


    @Override
    public void apply(feign.RequestTemplate requestTemplate) {
        requestTemplate.header("authToken", authToken);
    }
}