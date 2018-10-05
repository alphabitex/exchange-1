package com.liberty.exchange.wallet.common.event;

import com.liberty.exchange.entity.dto.wallet.CoinInfoDto;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import com.liberty.exchange.wallet.common.service.BaseBizService;
import com.liberty.exchange.wallet.common.task.ScanBlockTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:30
 * Description: SpringEvent
 */
@Component
@Slf4j
@Order(-1)
public class SpringEvent implements ApplicationRunner {

    @Resource
    private ScanBlockTask scanBlockTask;
    @Resource
    private BaseBizService baseBizService;
    @Resource
    private WalletProperties walletProperties;
    @Resource
    private SubEvent subEvent;


    @Override
    public void run(ApplicationArguments applicationArguments) {
        log.info(">>>>>>>>>>>>>>>>开始加载货币信息");
        //加载货币信息
        List<CoinInfoDto> coinInfoDtoList = baseBizService.loadCoinInfo(walletProperties.getCoinName());
        if (null==coinInfoDtoList){
            System.exit(-1);
        }
        Map<String, CoinInfoDto> map = new HashMap<>();
        coinInfoDtoList.forEach(coinInfoDto -> {
            map.put(coinInfoDto.getCoinName(), coinInfoDto);
        });
        walletProperties.setCoinFactory(map);
        subEvent.initEvent(applicationArguments);
        log.info(">>>>>>>>>>>>>>>>启动扫块线程");
        //启动扫块进程
        Thread thread = new Thread(scanBlockTask);
        thread.start();
    }
}
