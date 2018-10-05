package com.liberty.exchange.config;

import com.liberty.exchange.common.sequence.Sequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 3:28
 * Description: SequenceConfig
 */
@Configuration
@Slf4j
public class SequenceConfig {
    @Value("#{T(Math).random()*10}")
    private Double workerId;

    @Value("#{T(Math).random()*10}")
    private Double dataCenterId;

    @Bean
    public Sequence sequence() {
        return new Sequence(workerId, dataCenterId);
    }
}