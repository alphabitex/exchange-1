package com.liberty.exchange.wallet.eth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:37
 * Description: EthApplication
 */
@SpringBootApplication(scanBasePackages = {"com.liberty.exchange"})
@EnableDiscoveryClient
@EnableAsync
public class EthApplication {
    public static void main(String[] args) {
        SpringApplication.run(EthApplication.class, args);
    }
}
