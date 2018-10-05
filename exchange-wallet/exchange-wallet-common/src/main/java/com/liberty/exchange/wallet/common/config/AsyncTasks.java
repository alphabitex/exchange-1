package com.liberty.exchange.wallet.common.config;

import com.liberty.exchange.mapper.wallet.ExchangeUserWalletMapper;
import com.liberty.exchange.wallet.common.service.BaseBizService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * AsyncTasks Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/14
 * Time: 12:05
 * Description: AsyncTasks
 */
@Component
@Slf4j
public class AsyncTasks {
    @Resource
    private BaseBizService baseBizService;
    @Resource
    private ExchangeUserWalletMapper exchangeUserWalletMapper;

    /**
     * 更新地址余额
     *
     * @param address  地址
     * @param coinName 货币
     */
    @Async("commonAsyncTask")
    public void updateUserBalance(String address, String coinName) {
        log.info(coinName + "开始更新" + address + "余额");
        //查询地址余额
        try {
            BigDecimal balance = baseBizService.getBalanceByAddress(address, coinName);
            //更新地址余额
            exchangeUserWalletMapper.updateUserBalance(coinName, balance, address);
        } catch (Exception e) {
            log.error(coinName + "更新地址" + address + "余额失败:" + ExceptionUtils.getFullStackTrace(e));
        }
    }
}
