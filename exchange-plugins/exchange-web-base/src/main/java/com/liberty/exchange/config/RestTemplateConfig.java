package com.liberty.exchange.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 18-9-14
 * Time: 上午12:49
 * Description: RestTemplateConfig
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
