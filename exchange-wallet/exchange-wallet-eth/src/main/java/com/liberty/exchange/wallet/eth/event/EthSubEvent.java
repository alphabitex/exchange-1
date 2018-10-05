package com.liberty.exchange.wallet.eth.event;

import com.alibaba.fastjson.JSON;
import com.liberty.exchange.common.base.BaseResponse;
import com.liberty.exchange.constant.CoinConstants;
import com.liberty.exchange.constant.WalletConstants;
import com.liberty.exchange.entity.dto.wallet.CoinInfoDto;
import com.liberty.exchange.entity.dto.wallet.ExchangeTransactionDto;
import com.liberty.exchange.entity.pojo.wallet.ExchangeTrsOrder;
import com.liberty.exchange.entity.pojo.wallet.ExchangeUserWallet;
import com.liberty.exchange.entity.pojo.wallet.ExchangeWalletGroup;
import com.liberty.exchange.mapper.wallet.ExchangeUserWalletMapper;
import com.liberty.exchange.mapper.wallet.ExchangeWalletGroupMapper;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import com.liberty.exchange.wallet.common.event.SubEvent;
import com.liberty.exchange.wallet.common.service.CommonService;
import com.liberty.exchange.wallet.common.utils.DecimalUtils;
import com.liberty.exchange.wallet.eth.utils.CommonUtils;
import com.liberty.exchange.wallet.eth.utils.EthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:43
 * Description: EthSubEvent
 */
@Component
@Slf4j
public class EthSubEvent extends SubEvent {
    public static Map<String, String> contractList = new HashMap<>();  // key :合约地址  ，value:货币名称

    @Resource
    private WalletProperties walletProperties;
    @Resource
    private CommonService commonService;
    @Resource
    private ExchangeWalletGroupMapper exchangeWalletGroupMapper;
    @Resource
    private EthUtils ethUtils;
    @Resource
    private ExchangeUserWalletMapper exchangeUserWalletMapper;
    @Resource
    private DecimalUtils decimalUtils;
    @Resource
    private CommonUtils commonUtils;


    @Override
    public void initEvent(ApplicationArguments applicationArguments) {
        log.info("==============加载以太坊所有代币信息");
        Map<String, CoinInfoDto> coinFactory = walletProperties.getCoinFactory();
        coinFactory.forEach((key, value) -> {
            if (null == value.getFee()) {
                //设置以太坊默认gasPrice
                walletProperties.getCoinFactory().get(key).setFee(CoinConstants.GAS_PRICE);
            }
            if (!CoinConstants.ETH.equals(value.getCoinName()))
                contractList.put(value.getContractAddress(), value.getCoinName());
        });
    }

    public void onRecharge(ExchangeTransactionDto transactionDto) {

    }

    public void onWithdraw(ExchangeTransactionDto transactionDto) {
    }

    /**
     * 打邮费完成开始汇总
     *
     * @param transactionDto transactionDto
     */
    public void onRechargeGas(ExchangeTransactionDto transactionDto) {
        String address = transactionDto.getTo();//用户钱包地址
        String coinName = transactionDto.getCoinName();
        ExchangeUserWallet wallet = exchangeUserWalletMapper.selectWalletByAddressAndCoinName(address, coinName);
        BigDecimal amount = wallet.getBalance();
        String contractAddress = walletProperties.getCoinFactory().get(coinName).getContractAddress();
        BigDecimal gasPrice = walletProperties.getCoinFactory().get(coinName).getFee();
        gasPrice = gasPrice.multiply(BigDecimal.TEN.pow(CoinConstants.ETH_GWEI_UNIT));  //gasPrice变成长整数
        BigDecimal fee = gasPrice.multiply(CoinConstants.ETH_TOKEN_GAS_LIMIT);
        fee = decimalUtils.reconvert(coinName, fee);
        BigDecimal nonce;
        try {
            nonce = commonUtils.getNonceByAddress(wallet.getAddress(), wallet.getNonce(), "user");
        } catch (Exception e) {
            return;
        }
        //查询汇总钱包组
        ExchangeWalletGroup group = commonService.chaosGroup(exchangeWalletGroupMapper.selectGroupByGroupType(WalletConstants.WALLET_GROUP_RECHARGE, coinName));
        if (null == group) return;
        BaseResponse res = ethUtils.transferTokenFromAddress(wallet.getPrivateKey(), contractAddress, group.getAddress(), amount, gasPrice, nonce);
        if (null != res && res.getCode() == 200) {
            //插入订单
            commonService.recordExchangeTrsOrder(
                    ExchangeTrsOrder.builder()
                            .amount(amount)
                            .coinName(coinName)
                            .coinType(CoinConstants.ETH)
                            .fromAddress(address)
                            .toAddress(group.getAddress())
                            .fee(fee)
                            .hash(res.getData().toString())
                            .transactionType(WalletConstants.TRADE_RECHARGE_ALL)
                            .userId(wallet.getUserId())
                            .build());
            //更新用户钱包nonce
            exchangeUserWalletMapper.updateUserNonce(CoinConstants.ETH, wallet.getAddress());
        } else {
            log.error("userId={}地址{}在{}汇总时候失败", wallet.getUserId(), wallet.getAddress(), coinName);
            log.error("失败信息:" + JSON.toJSONString(res));
        }

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
     *
     * @param coinInfoDto coinInfoDto
     */
    public void onCoinChange(CoinInfoDto coinInfoDto) {
        if (!CoinConstants.ETH.equals(coinInfoDto.getCoinName())) {
            contractList.put(coinInfoDto.getContractAddress(), coinInfoDto.getCoinName());
        }
    }
}
