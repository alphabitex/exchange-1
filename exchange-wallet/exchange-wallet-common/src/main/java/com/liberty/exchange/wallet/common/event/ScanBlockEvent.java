package com.liberty.exchange.wallet.common.event;

import com.liberty.exchange.entity.dto.wallet.ExchangeTransactionDto;
import com.liberty.exchange.entity.pojo.wallet.ExchangeTrsOrder;
import com.liberty.exchange.mapper.wallet.ExchangeTrsOrderMapper;
import com.liberty.exchange.wallet.common.config.AsyncTasks;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:22
 * Description: ScanBlockEvent
 */
@Component
public class ScanBlockEvent {
    @Resource
    private SubEvent subEvent;
    @Resource
    private ExchangeTrsOrderMapper exchangeTrsOrderMapper;
    @Resource
    private AsyncTasks asyncTasks;
    @Resource
    private WalletProperties walletProperties;

    /**
     * 用户充值
     */
    public void onRecharge(ExchangeTransactionDto transactionDto) {
        if (null != exchangeTrsOrderMapper.selectByHashAndToAddress(transactionDto.getHash(), transactionDto.getTo()))
            return;
        ExchangeTrsOrder exchangeTrsOrder = new ExchangeTrsOrder(transactionDto);
        //异步更新地址余额
        asyncTasks.updateUserBalance(transactionDto.getTo(), transactionDto.getCoinName());
        //保存充值记录
        exchangeTrsOrderMapper.insertSelective(exchangeTrsOrder);
        subEvent.onRecharge(transactionDto);
        removePendingCache(transactionDto);
    }

    /**
     * 用户提现
     */
    public void onWithdraw(ExchangeTransactionDto transactionDto) {
        ExchangeTrsOrder exchangeTrsOrder = exchangeTrsOrderMapper.selectByHashAndToAddress(transactionDto.getHash(), transactionDto.getTo());
        if (null == exchangeTrsOrder) {
            return;
        }
        //修改提现订单
        int i = exchangeTrsOrderMapper.updateOrderStatusById(exchangeTrsOrder.getId());
        removePendingCache(transactionDto);
        subEvent.onWithdraw(transactionDto);
    }

    /**
     * 打邮费
     */
    public void onRechargeGas(ExchangeTransactionDto transactionDto) {
        ExchangeTrsOrder exchangeTrsOrder = exchangeTrsOrderMapper.selectByHashAndToAddress(transactionDto.getHash(), transactionDto.getTo());
        if (null == exchangeTrsOrder) {
            return;
        }
        //修改打邮费订单状态
        exchangeTrsOrderMapper.updateOrderStatusById(exchangeTrsOrder.getId());
        removePendingCache(transactionDto);
        subEvent.onRechargeGas(transactionDto);
    }

    /**
     * 汇总账
     */
    public void onRechargeAll(ExchangeTransactionDto transactionDto) {
        ExchangeTrsOrder exchangeTrsOrder = exchangeTrsOrderMapper.selectByHashAndToAddress(transactionDto.getHash(), transactionDto.getTo());
        if (null == exchangeTrsOrder) {
            return;
        }
        //异步更新用户地址余额
        asyncTasks.updateUserBalance(transactionDto.getFrom(), transactionDto.getCoinName());
        // TODO 是否异步需要更新钱包组余额
        //修改提现订单
        exchangeTrsOrderMapper.updateOrderStatusById(exchangeTrsOrder.getId());
        removePendingCache(transactionDto);
        subEvent.onRechargeAll(transactionDto);
    }

    /**
     * 清楚缓存
     *
     * @param transactionDto transactionDto
     */
    public void removePendingCache(ExchangeTransactionDto transactionDto) {
        walletProperties.getPendingTrsList().remove(transactionDto.getHash() + transactionDto.getTo());
    }
}

