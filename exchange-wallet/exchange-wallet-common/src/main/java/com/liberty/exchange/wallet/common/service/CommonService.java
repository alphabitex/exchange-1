package com.liberty.exchange.wallet.common.service;

import com.liberty.exchange.constant.WalletConstants;
import com.liberty.exchange.entity.pojo.wallet.ExchangeTrsOrder;
import com.liberty.exchange.entity.pojo.wallet.ExchangeWalletGroup;
import com.liberty.exchange.mapper.wallet.ExchangeTrsOrderMapper;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * CommonService Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/20
 * Time: 11:48
 * Description: CommonService
 */
@Service
public class CommonService {

    @Resource
    private ExchangeTrsOrderMapper exchangeTrsOrderMapper;
    @Resource
    private WalletProperties walletProperties;

    /**
     * 记录提现、充值总账订单
     *
     * @param from     from
     * @param to       to
     * @param amount   提现、汇总金额
     * @param coinName 货币名称
     * @param fee      手续费
     * @param txId     交易id
     * @param txType   交易类型
     * @param userId   用户id 汇总不需要
     */
    public void recordExchangeTrsOrder(String from, String to, BigDecimal amount, String coinName, String coinType, BigDecimal fee, String txId, Integer txType, Integer userId) {
        ExchangeTrsOrder exchangeTrsOrder = ExchangeTrsOrder.builder()
                .amount(amount)
                .coinName(coinName)
                .coinType(coinType)
                .createdTime(new Date())
                .toAddress(to)
                .fromAddress(from)
                .transactionType(txType)
                .fee(fee)
                .hash(txId)
                .status(WalletConstants.TRADE_PENDING)
                .updatedTime(new Date())
                .build();
        if (userId != null) {
            exchangeTrsOrder.setUserId(userId);
        }
        this.recordExchangeTrsOrder(exchangeTrsOrder);
    }

    /**
     * 记录提现、充值总账订单
     *
     * @param exchangeTrsOrder exchangeTrsOrder
     */
    public void recordExchangeTrsOrder(ExchangeTrsOrder exchangeTrsOrder) {
        if (WalletConstants.TRADE_PENDING == exchangeTrsOrder.getStatus()) {
            walletProperties.getPendingTrsList().add(exchangeTrsOrder.getHash() + exchangeTrsOrder.getToAddress());
        }
        exchangeTrsOrderMapper.insert(exchangeTrsOrder);
    }

    /**
     * 混淆钱包组
     *
     * @param list 满足条件的钱包组列表
     * @return ExchangeWalletGroup
     */
    public ExchangeWalletGroup chaosGroup(List<ExchangeWalletGroup> list) {
        if (null == list || list.size() < 1) return null;
        Collections.shuffle(list);
        return list.get(0);
    }

}
