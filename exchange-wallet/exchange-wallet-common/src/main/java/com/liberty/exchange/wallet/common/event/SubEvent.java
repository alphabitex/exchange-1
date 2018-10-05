package com.liberty.exchange.wallet.common.event;

import com.liberty.exchange.entity.dto.wallet.CoinInfoDto;
import com.liberty.exchange.entity.dto.wallet.ExchangeTransactionDto;
import org.springframework.boot.ApplicationArguments;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:21
 * Description: SubEvent
 */
public class SubEvent {

    /**
     * 初始化子任务
     *
     * @param applicationArguments applicationArguments
     */
    public void initEvent(ApplicationArguments applicationArguments) {
    }

    /**
     * 充值完成执行子任务
     *
     * @param transactionDto transactionDto
     */
    public void onRecharge(ExchangeTransactionDto transactionDto) {
    }

    /**
     * 提现完成执行子任务
     *
     * @param transactionDto transactionDto
     */
    public void onWithdraw(ExchangeTransactionDto transactionDto) {
    }

    /**
     * 打邮费完成执行子任务
     *
     * @param transactionDto transactionDto
     */
    public void onRechargeGas(ExchangeTransactionDto transactionDto) {
    }

    /**
     * 汇总完成执行子任务
     *
     * @param transactionDto transactionDto
     */
    public void onRechargeAll(ExchangeTransactionDto transactionDto) {
    }

    /**
     * 修改币种信息
     * @param coinInfoDto coinInfoDto
     */
    public void onCoinChange(CoinInfoDto coinInfoDto){

    }
}
