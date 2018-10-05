package com.liberty.exchange.wallet.common.event;

import com.liberty.exchange.constant.WalletConstants;
import com.liberty.exchange.mapper.wallet.ExchangeTrsOrderMapper;
import com.liberty.exchange.mapper.wallet.ExchangeUserWalletMapper;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:21
 * Description: ApplicationListenerEvent
 */
@Component
@Slf4j
public class ApplicationListenerEvent implements ApplicationContextAware {

    @Resource
    private WalletProperties walletProperties;
    @Resource
    private ExchangeUserWalletMapper exchangeUserWalletMapper;
    @Resource
    private ExchangeTrsOrderMapper exchangeTrsOrderMapper;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String coinType = walletProperties.getCoinName();
        //查询所有用户地址加入缓存
        log.info("======开始加载所有{}用户地址进入缓存", coinType);
        exchangeUserWalletMapper.selectWalletsByCinType(coinType).forEach(address -> {
            walletProperties.getUserAddressList().add(address);
        });

        //查询所有pending状态的交易加入缓存
        log.info("======开始加载所有{}币种pending状态的交易", coinType);
        exchangeTrsOrderMapper
                .selectPendingTrs(coinType, WalletConstants.TRADE_PENDING)
                .forEach(tx -> {
                    walletProperties.getPendingTrsList().add(tx.getHash() + tx.getToAddress());
                });
    }
}
