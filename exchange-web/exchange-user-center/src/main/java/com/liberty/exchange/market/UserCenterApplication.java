package com.liberty.exchange.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/21
 * Time: 0:25
 * Description: MarketApplication
 */
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class UserCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }
}
