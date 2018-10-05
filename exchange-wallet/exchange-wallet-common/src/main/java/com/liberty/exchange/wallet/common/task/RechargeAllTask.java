package com.liberty.exchange.wallet.common.task;

import com.liberty.exchange.entity.dto.wallet.CoinInfoDto;
import com.liberty.exchange.entity.pojo.wallet.ExchangeUserWallet;
import com.liberty.exchange.mapper.wallet.ExchangeUserWalletMapper;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import com.liberty.exchange.wallet.common.service.BaseBizService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:32
 * Description: RechargeAllTask
 */
@EnableScheduling
@Component
public class RechargeAllTask {
    @Resource
    private WalletProperties walletProperties;
    @Resource
    private ExchangeUserWalletMapper exchangeUserWalletMapper;
    @Resource
    private BaseBizService baseBizService;

    /**
     * 每天凌晨执行汇总任务
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void rechargeAll() {
        //币种是否需要进行汇总
        if (walletProperties.getRechargeAll().isNeed()) {
            Set<String> coinSet = walletProperties.getCoinFactory().keySet();
            coinSet.forEach(coinName -> {
                CoinInfoDto coinInfoDto = walletProperties.getCoinFactory().get(coinName);
                List<ExchangeUserWallet> walletList = exchangeUserWalletMapper.selectWalletsByCoinName(coinName);
                walletList.forEach(wallet -> {
                    //汇总任务
                    if (wallet.getBalance().compareTo(coinInfoDto.getMinCollectAmount()) >= 0) {
                        baseBizService.rechargeAll(wallet);
                    }
                });
            });
        }
    }
}

