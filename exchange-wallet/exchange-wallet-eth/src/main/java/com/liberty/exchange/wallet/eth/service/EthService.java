package com.liberty.exchange.wallet.eth.service;

import com.alibaba.fastjson.JSON;
import com.liberty.exchange.common.base.BaseResponse;
import com.liberty.exchange.common.base.BaseResponseCode;
import com.liberty.exchange.common.crypto.EncryptUtils;
import com.liberty.exchange.constant.CoinConstants;
import com.liberty.exchange.constant.WalletConstants;
import com.liberty.exchange.entity.dto.wallet.*;
import com.liberty.exchange.entity.pojo.wallet.ExchangeTrsOrder;
import com.liberty.exchange.entity.pojo.wallet.ExchangeUserWallet;
import com.liberty.exchange.entity.pojo.wallet.ExchangeWalletGroup;
import com.liberty.exchange.mapper.wallet.ExchangeTrsOrderMapper;
import com.liberty.exchange.mapper.wallet.ExchangeUserWalletMapper;
import com.liberty.exchange.mapper.wallet.ExchangeWalletGroupMapper;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import com.liberty.exchange.wallet.common.service.BaseBizService;
import com.liberty.exchange.wallet.common.service.CommonService;
import com.liberty.exchange.wallet.common.utils.DecimalUtils;
import com.liberty.exchange.wallet.eth.event.EthSubEvent;
import com.liberty.exchange.wallet.eth.utils.CommonUtils;
import com.liberty.exchange.wallet.eth.utils.EthUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:51
 * Description: EthService
 */
@Service
@Slf4j
public class EthService extends BaseBizService {
    @Resource
    private EthUtils ethUtils;
    @Resource
    private Web3j web3j;
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private WalletProperties walletProperties;
    @Resource
    private ExchangeTrsOrderMapper exchangeTrsOrderMapper;
    @Resource
    private ExchangeUserWalletMapper exchangeUserWalletMapper;
    @Resource
    private DecimalUtils decimalUtils;
    @Resource
    private ExchangeWalletGroupMapper exchangeWalletGroupMapper;
    @Resource
    private EncryptUtils encryptUtils;
    @Resource
    private CommonService commonService;

    /**
     * 创建账户
     *
     * @param userId userId
     * @param asset  代币名称
     * @return ExchangeUserWallet
     * @throws Exception
     */
    @Override
    public ExchangeUserWallet createAccount(int userId, String asset) throws Exception {

        AccountDto accountDto = ethUtils.generateKeyAddress();
        ExchangeUserWallet exchangeUserWallet = ExchangeUserWallet.builder()
                .coinName(walletProperties.getCoinName())
                .coinType(walletProperties.getCoinName())
                .userId(userId)
                .privateKey(accountDto.getPrivateKey())
                .address(accountDto.getAddress())
                .build();
        if (StringUtils.isNotEmpty(asset)) {
            exchangeUserWallet.setCoinName(asset);
        }
        return exchangeUserWallet;
    }

    @Override
    public BigInteger getLatestBlockNum() throws Exception {
        return web3j.ethBlockNumber().send().getBlockNumber();
    }

    /**
     * 根据区块高度获取区块信息,包括解析区块以及格式化区块内所有交易
     *
     * @param height 区块高度
     * @return ExchangeBlockDto
     * @throws Exception Exception
     */
    @Override
    public ExchangeBlockDto getBlockByNum(BigInteger height) throws Exception {
        EthBlock.Block block = ethUtils.getBlockByBlockNum(height);
        if (null == block || !StringUtils.isNotEmpty(block.getHash()))
            throw new Exception("获取区块：" + height + "失败");
        log.info(">>>>>>>>>>开始解析区块：height=" + block.getNumber() + "<<<<<<<<<<");
        log.info("原始区块" + block.getNumber() + "包含" + block.getTransactions().size() + "笔交易");
        ExchangeBlockDto blockDto = ExchangeBlockDto.builder()
                .hash(block.getHash())
                .height(block.getNumber())
                .build();
        List<ExchangeTransactionDto> txList = new ArrayList<>();

        Set<String> legalTokenTrans = new HashSet<>();
        boolean checkEventLog = false;  //默认掉接口查看event_log

        for (Object trx : block.getTransactions()) {
            Transaction transaction = ((EthBlock.TransactionObject) trx).get();

            //没有to则直接返回
            if (!StringUtils.isNotEmpty(transaction.getTo())) {
                continue;
            }

            String input = transaction.getInput();

            //构建交易
            ExchangeTransactionDto transactionDto = ExchangeTransactionDto.builder()
                    .from(transaction.getFrom())
                    .coinType(CoinConstants.ETH)
                    .to(transaction.getTo())
                    .nonce(new BigDecimal(transaction.getNonce()))
                    .hash(transaction.getHash())
                    .amount(new BigDecimal(transaction.getValue()))
                    .coinName(CoinConstants.ETH)
                    .trsType(WalletConstants.TRADE_RECHARGE)
                    .build();

            String tokenToAddress = "";
            List<Type> params = new ArrayList<>();
            if (StringUtils.isNotEmpty(input) && input.length() >= 138) {
                params = commonUtils.resolveTokenByTx(input);
                // 充币地址
                tokenToAddress = params.get(0).getValue().toString();
            }


            //判断数据库记录了此交易
            if (walletProperties.getPendingTrsList().contains(transactionDto.getHash() + transaction.getTo()) ||
                    walletProperties.getPendingTrsList().contains(transactionDto.getHash() + tokenToAddress)) {
                ExchangeTrsOrder exchangeTrsOrder = exchangeTrsOrderMapper.selectByHashAndCoinType(transaction.getHash(), CoinConstants.ETH);
                if (null != exchangeTrsOrder) {
                    //已经处理过的充值交易则移除
                    if (WalletConstants.TRADE_RECHARGE == exchangeTrsOrder.getTransactionType()) continue;
                    //交易必须正在进行中
                    if (WalletConstants.TRADE_PENDING != exchangeTrsOrder.getStatus()) continue;
                        // 正在进行中的交易
                    else {
                        transactionDto.setTrsType(exchangeTrsOrder.getTransactionType());
                        transactionDto.setTo(exchangeTrsOrder.getToAddress());
                        transactionDto.setCoinName(exchangeTrsOrder.getCoinName());
                        txList.add(transactionDto);
                        continue;
                    }
                }
            }


            //判断是否以太坊充值
            if (walletProperties.getUserAddressList().contains(transaction.getTo())) {
                ExchangeUserWallet userWallet = exchangeUserWalletMapper.selectWalletByAddressAndCoinName(transaction.getTo(), CoinConstants.ETH);
                if (null != userWallet) {
                    // 检验合法性
                    ValidateTxDto validateTxDto = commonUtils.isValidStatus(transaction);
                    transactionDto.setFee(new BigDecimal(validateTxDto.getGasUsed()));
                    transactionDto.setUserId(userWallet.getUserId());
                    if (validateTxDto.isSuccess()) {
                        txList.add(transactionDto);
                    }
                    continue;
                }
            }

            //是否支持此种token充值
            if (!commonUtils.isSupportToken(transaction.getTo())) continue;
            //校验input参数
            if (StringUtils.isNotEmpty(tokenToAddress)) {
                // 充币地址
                if (!walletProperties.getUserAddressList().contains(tokenToAddress)) continue;
                BigDecimal amount = new BigDecimal(params.get(1).getValue().toString());
                String contractAddress = transaction.getTo();
                String coinName = EthSubEvent.contractList.get(contractAddress);
                if (StringUtils.isEmpty(coinName)) continue;
                transactionDto.setCoinName(coinName);
                transactionDto.setAmount(amount);
                transactionDto.setTo(tokenToAddress);
                ExchangeUserWallet tokenWallet = exchangeUserWalletMapper.selectWalletByAddressAndCoinName(tokenToAddress, coinName);
                //如果不是自己用户的token充值直接退出
                if (null == tokenWallet) continue;
                transactionDto.setUserId(tokenWallet.getUserId());
                //已经查询过event_log了
                if (!checkEventLog) {
                    legalTokenTrans = commonUtils.getEventLogLegalList(block.getNumber());
                    checkEventLog = true;
                }
                //校验交易是否成功
                ValidateTxDto validateTxDto = commonUtils.isValidStatus(transaction);
                transactionDto.setFee(new BigDecimal(validateTxDto.getGasUsed()));
                //判断token转账合法性
                if (validateTxDto.isSuccess() && legalTokenTrans.contains(transaction.getHash())) {
                    txList.add(transactionDto);
                }
            }
        }

        //按照精度转换一下金额
        for (ExchangeTransactionDto tx : txList) {
            tx.setAmount(decimalUtils.reconvert(tx.getCoinName(), tx.getAmount()));
        }
        blockDto.setTrs(txList);
        return blockDto;
    }

    @Override
    public ExchangeTransactionDto getTxByHash(String hash) {
        return null;
    }

    /**
     * 查询地址余额
     *
     * @param address 地址
     * @param asset   资产（eth:为token地址）
     * @return BigDecimal
     * @throws IOException IOException
     */
    @Override
    public BigDecimal getBalanceByAddress(String address, String asset) throws Exception {
        if (CoinConstants.ETH.equals(asset.toUpperCase())) {
            return ethUtils.getBalance(address);
        } else {
            String contractAddress = walletProperties.getCoinFactory().get(asset).getContractAddress();
            BigDecimal balance = ethUtils.getTokenBalance(address, contractAddress);
            return decimalUtils.reconvert(asset, balance);
        }
    }

    @Override
    public BaseResponse transfer(String to, BigDecimal amount, String coinName, BigDecimal fee) {
        return null;
    }

    /**
     * 以太坊提现操作
     *
     * @param toAddress 接受者
     * @param amount    金额
     * @param coinName  货币名字
     * @param gasPrice  gasPrice eg:5代表5GWei
     * @param userId    用户ID
     * @return BaseResponse
     */
    @Override
    public BaseResponse withdraw(String from, String toAddress, BigDecimal amount, String coinName, BigDecimal gasPrice, Integer userId) throws Exception {
        gasPrice = gasPrice.multiply(BigDecimal.TEN.pow(CoinConstants.ETH_GWEI_UNIT));
        BigDecimal fee;
        if (CoinConstants.ETH.equals(coinName)) {
            fee = gasPrice.multiply(CoinConstants.ETH_GAS_LIMIT);
            fee = decimalUtils.reconvert(coinName, fee);
        } else {
            fee = gasPrice.multiply(CoinConstants.ETH_TOKEN_GAS_LIMIT);
            fee = decimalUtils.reconvert(coinName, fee);
        }
        ExchangeWalletGroup exchangeWalletGroup = getEthExchangeGroup(amount, coinName, fee, from);
        if (null == exchangeWalletGroup) return BaseResponse.ERROR(BaseResponseCode.AUDIT);
        amount = decimalUtils.convert(coinName, amount);
        String senderAddress = exchangeWalletGroup.getAddress();
        String privateKey = encryptUtils.decrypt(exchangeWalletGroup.getPrivateKey());
        BigDecimal nonce = commonUtils.getNonceByAddress(senderAddress, exchangeWalletGroup.getNonce(), "group");
        BaseResponse baseResponse;
        //ETH提现
        if (CoinConstants.ETH.equals(coinName)) {
            baseResponse = ethUtils.transferEth(privateKey, toAddress, amount, gasPrice, nonce);
        } else {
            //代币提现
            String contract = walletProperties.getCoinFactory().get(coinName).getContractAddress();
            baseResponse = ethUtils.transferTokenFromAddress(privateKey, contract, toAddress, amount, gasPrice, nonce);
        }
        //发送完成后更新nonce
        if (null != baseResponse && baseResponse.getCode() == 200) {
            amount = decimalUtils.reconvert(coinName, amount);
            exchangeWalletGroupMapper.updateEthGroupNonce(exchangeWalletGroup.getAddress(), CoinConstants.ETH);
            //更新以太坊钱包组账户余额
            if (CoinConstants.ETH.equals(coinName)) {
                BigDecimal slot = amount.add(fee);
                exchangeWalletGroupMapper.updateWhenMainTransfer(senderAddress, CoinConstants.ETH, slot);
            } else {
                //更新代币钱包组代币余额
                exchangeWalletGroupMapper.updateWhenAssetTransfer(senderAddress, exchangeWalletGroup.getCoinName(), amount);
                //更新代币钱包组以太坊余额
                exchangeWalletGroupMapper.updateWhenMainTransfer(senderAddress, CoinConstants.ETH, fee);
            }
            //记录提现
            commonService.recordExchangeTrsOrder(senderAddress, toAddress, amount, coinName, CoinConstants.ETH, fee,
                    (String) baseResponse.getData(), WalletConstants.TRADE_WITHDRAW, userId);
        }
        return baseResponse;
    }

    /**
     * 查询钱包组
     *
     * @param amount   金额
     * @param coinName 货币名字
     * @param fee      fee
     * @return ExchangeWalletGroup
     */
    private ExchangeWalletGroup getEthExchangeGroup(BigDecimal amount, String coinName, BigDecimal fee, String from) {
        ExchangeWalletGroup exchangeWalletGroup;
        if (CoinConstants.ETH.equals(coinName)) {
            exchangeWalletGroup = commonService.chaosGroup(exchangeWalletGroupMapper.selectWithdrawGroupForEth(amount.add(fee), from));
        } else {
            exchangeWalletGroup = commonService.chaosGroup(exchangeWalletGroupMapper.selectWithdrawGroupForErc20(amount, fee, coinName, from));
        }
        return exchangeWalletGroup;
    }

    /**
     * 汇总
     *
     * @param wallet 用户钱包
     */
    @Override
    public void rechargeAll(ExchangeUserWallet wallet) {
        String coinName = wallet.getCoinName();
        BigDecimal gasPrice = walletProperties.getCoinFactory().get(coinName).getFee();
        gasPrice = gasPrice.multiply(BigDecimal.TEN.pow(CoinConstants.ETH_GWEI_UNIT));  //gasPrice变成长整数
        BigDecimal fee = gasPrice.multiply(CoinConstants.ETH_GAS_LIMIT);
        fee = decimalUtils.reconvert(coinName, fee);
        BaseResponse baseResponse = null;
        BigDecimal nonce;
        //以太坊汇总
        if (CoinConstants.ETH.equals(coinName)) {
            //查询汇总钱包组
            ExchangeWalletGroup group = commonService.chaosGroup(exchangeWalletGroupMapper.selectGroupByGroupType(WalletConstants.WALLET_GROUP_RECHARGE, coinName));
            if (null == group) return;
            try {
                nonce = commonUtils.getNonceByAddress(wallet.getAddress(), wallet.getNonce(), "user");
            } catch (Exception e) {
                return;
            }
            BigDecimal amount = wallet.getBalance().subtract(fee);
            baseResponse = ethUtils.transferEth(wallet.getPrivateKey(), group.getAddress(), amount, gasPrice, nonce);
            if (null != baseResponse && baseResponse.getCode() == 200) {
                //记录信息
                commonService.recordExchangeTrsOrder(wallet.getAddress(), group.getAddress(), amount, CoinConstants.ETH, CoinConstants.ETH, fee, baseResponse.getData().toString()
                        , WalletConstants.TRADE_RECHARGE_ALL, wallet.getUserId());
                //更新用户钱包nonce
                exchangeUserWalletMapper.updateUserNonce(CoinConstants.ETH, wallet.getAddress());
            } else {
                log.error("userId={}地址{}在ETH汇总时候失败", wallet.getUserId(), wallet.getAddress());
                log.error("失败信息:" + JSON.toJSONString(baseResponse));
            }
        } else {
            //代币汇总,打邮费
            String contractAddress = walletProperties.getCoinFactory().get(coinName).getContractAddress();
            BigDecimal slot = fee.add(fee);
            ExchangeWalletGroup gasGroup = commonService.chaosGroup(
                    exchangeWalletGroupMapper.selectByGroupTypeAndCoinNameAndCoinType(
                            ExchangeWalletGroupDto.builder()
                                    .groupType(WalletConstants.WALLET_GROUP_GAS)
                                    .fee(slot)
                                    .coinName(coinName)
                                    .coinType(CoinConstants.ETH)
                                    .build()
                    ));
            if (null == gasGroup) return;
            try {
                nonce = commonUtils.getNonceByAddress(gasGroup.getAddress(), gasGroup.getNonce(), "group");
            } catch (Exception e) {
                return;
            }
            //打邮费
            baseResponse = ethUtils.transferTokenFromAddress(gasGroup.getPrivateKey(), contractAddress, wallet.getAddress(), fee, gasPrice, nonce);
            if (null != baseResponse && baseResponse.getCode() == 200) {
                //记录信息
                commonService.recordExchangeTrsOrder(gasGroup.getAddress(), wallet.getAddress(), fee, CoinConstants.ETH, CoinConstants.ETH, fee, baseResponse.getData().toString()
                        , WalletConstants.TRADE_RECHARGE_GAS, wallet.getUserId());
                //更新钱包组nonce
                exchangeWalletGroupMapper.updateEthGroupNonce(gasGroup.getAddress(), CoinConstants.ETH);
                //更新eth余额
                exchangeWalletGroupMapper.updateWhenMainTransfer(gasGroup.getAddress(), CoinConstants.ETH, slot);
            } else {
                log.error("userId={}地址{}在ETH汇总打邮费时候失败", wallet.getUserId(), wallet.getAddress());
            }
        }
    }
}

