package com.exchange.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * RegistryApplication Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/20
 * Time: 21:03
 * Description: RegistryApplication
 */
@SpringBootApplication
@EnableEurekaServer
public class RegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegistryApplication.class, args);
    }
}
