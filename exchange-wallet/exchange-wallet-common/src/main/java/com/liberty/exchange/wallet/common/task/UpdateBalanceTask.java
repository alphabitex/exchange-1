package com.liberty.exchange.wallet.common.task;

import com.liberty.exchange.mapper.wallet.ExchangeUserWalletMapper;
import com.liberty.exchange.wallet.common.config.AsyncTasks;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:32
 * Description: UpdateBalanceTask
 */
@EnableScheduling
@Component
public class UpdateBalanceTask {

    @Resource
    private WalletProperties walletProperties;
    @Resource
    private ExchangeUserWalletMapper exchangeUserWalletMapper;
    @Resource
    private AsyncTasks asyncTasks;

    /**
     * 每天凌晨更新用户钱包余额
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateUserWalletBalance() {
        walletProperties.getCoinFactory().keySet().forEach(coinName -> {
            exchangeUserWalletMapper.selectWalletsByCoinName(coinName).forEach(wallet -> {
                asyncTasks.updateUserBalance(wallet.getAddress(), coinName);
            });
        });
    }
}
