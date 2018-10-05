package com.liberty.exchange.wallet.common.service;

import com.liberty.exchange.common.base.BaseResponse;
import com.liberty.exchange.common.utils.MonitorUtils;
import com.liberty.exchange.entity.dto.wallet.CoinInfoDto;
import com.liberty.exchange.entity.dto.wallet.ExchangeBlockDto;
import com.liberty.exchange.entity.dto.wallet.ExchangeTransactionDto;
import com.liberty.exchange.entity.pojo.wallet.ExchangeMockCoin;
import com.liberty.exchange.entity.pojo.wallet.ExchangeUserWallet;
import com.liberty.exchange.mapper.wallet.ExchangeMockCoinMapper;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 5:16
 * Description: BaseBizService
 */
@Slf4j
public abstract class BaseBizService {
    @Resource
    private ExchangeMockCoinMapper exchangeMockCoinMapper;

    @Resource
    private WalletProperties walletProperties;

    /**
     * 健康监测
     *
     * @return BaseResponse
     */
    public BaseResponse healthCheck() {
        return BaseResponse.OK(MonitorUtils.getMonitorInfoBean());
    }

    /**
     * 加载货币信息
     * TODO 此处校验应该不通过则直接终止程序
     *
     * @param coinName 货币名字（BTC,ETH:加载所有代币以及以太坊）
     */
    public List<CoinInfoDto> loadCoinInfo(String coinName) {
        List<ExchangeMockCoin> mockCoins = exchangeMockCoinMapper.selectAllByCoinType(walletProperties.getCoinName());
        List<CoinInfoDto> coinInfoDtoList = new ArrayList<>();
        mockCoins.forEach(mockCoin -> {
            coinInfoDtoList.add(new CoinInfoDto(mockCoin));
        });
        log.info("开始校验初始化货币信息");
        coinInfoDtoList.forEach(coinInfoDto -> {
            log.info("货币信息：{}", coinInfoDto);
            if (StringUtils.isEmptyOrWhitespaceOnly(coinInfoDto.getCoinName()))
                log.error("coinInfoDto.getCoinName() is null");
            if (!coinName.equals(coinInfoDto.getCoinName()) && StringUtils.isEmptyOrWhitespaceOnly(coinInfoDto.getContractAddress())) {
                log.error(coinInfoDto.getCoinName() + "没有配置代币地址");
            }

        });
        return coinInfoDtoList;
    }

    /**
     * 创建账号
     *
     * @return BaseResponse
     */
    public abstract ExchangeUserWallet createAccount(int userId, String asset) throws Exception;

    /**
     * 获取区块最新高度
     *
     * @return BigInteger
     */
    public abstract BigInteger getLatestBlockNum() throws Exception;

    /**
     * 根据区块高度获取区块信息,包括解析区块以及格式化区块内所有交易
     *
     * @param height 区块高度
     * @return ExchangeBlockDto
     */
    public abstract ExchangeBlockDto getBlockByNum(BigInteger height) throws Exception;

    /**
     * 根据交易hash查询交易详情
     *
     * @param hash 交易hash
     * @return ExchangeTransactionDto
     */
    public abstract ExchangeTransactionDto getTxByHash(String hash) throws Exception;

    /**
     * 查询地址余额
     *
     * @param address 地址
     * @param asset   资产（eth:为token地址）
     * @return BigDecimal
     */
    public abstract BigDecimal getBalanceByAddress(String address, String asset) throws Exception;

    /**
     * 后台提到总账
     *
     * @param to       接受者
     * @param amount   金额
     * @param coinName 货币名字
     * @return BaseResponse
     */
    public abstract BaseResponse transfer(String to, BigDecimal amount, String coinName, BigDecimal fee);

    /**
     * 用户提现
     *
     * @param to       接受者
     * @param amount   金额
     * @param coinName 货币名字
     * @param fee      交易费
     * @param userId   用户ID
     * @return BaseResponse
     */
    public abstract BaseResponse withdraw(String from, String to, BigDecimal amount, String coinName, BigDecimal fee, Integer userId) throws Exception;

    /**
     * 更新钱包组余额
     */
    public void updateGroupBalance() {
    }

    /**
     * 汇总
     *
     * @param wallet 用户地址
     */
    public void rechargeAll(ExchangeUserWallet wallet) {

    }
}

