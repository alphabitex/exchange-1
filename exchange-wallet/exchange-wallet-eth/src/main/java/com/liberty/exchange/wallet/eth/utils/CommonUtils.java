package com.liberty.exchange.wallet.eth.utils;

import com.liberty.exchange.constant.CoinConstants;
import com.liberty.exchange.entity.dto.wallet.ValidateTxDto;
import com.liberty.exchange.mapper.wallet.ExchangeUserWalletMapper;
import com.liberty.exchange.mapper.wallet.ExchangeWalletGroupMapper;
import com.liberty.exchange.wallet.eth.event.EthSubEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * CommonUtils Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/3
 * Time: 18:13
 * Description: CommonUtils
 */
@Component
@Slf4j
public class CommonUtils {
    @Resource
    private EthUtils ethUtils;
    @Resource
    private Web3j web3j;
    @Resource
    private ExchangeUserWalletMapper exchangeUserWalletMapper;
    @Resource
    private ExchangeWalletGroupMapper exchangeWalletGroupMapper;

    /**
     * 解析token转账
     *
     * @param input inputData
     * @return List<Type>
     */
    public List<Type> resolveTokenByTx(String input) {
        String data = input.substring(0, 9);
        data = data + input.substring(17);
        Function function = new Function("transfer", Collections.emptyList(), Arrays.asList(new TypeReference<Address>() {
        }, new TypeReference<Uint256>() {
        }));
        return FunctionReturnDecoder.decode(data, function.getOutputParameters());
    }

    /**
     * 校验交易合法性
     *
     * @param transaction 交易
     * @return boolean true:合法
     * @throws IOException IOException
     */
    public ValidateTxDto isValidStatus(Transaction transaction) throws IOException {
        //校验交易状态
        return ethUtils.validateTx(transaction.getHash());
    }

    /**
     * 获取区块内支持代币的和合法交易
     *
     * @param blockNum 区块高度
     * @return Set<String>
     * @throws IOException IOException
     */
    public Set<String> getEventLogLegalList(BigInteger blockNum) throws IOException {
        DefaultBlockParameter fromBlock = new DefaultBlockParameterNumber(blockNum);
        DefaultBlockParameter to = new DefaultBlockParameterNumber(blockNum);
        EthFilter ethFilter = new EthFilter(fromBlock, to, new ArrayList<>(EthSubEvent.contractList.keySet()));
        List<EthLog.LogResult> list = web3j.ethGetLogs(ethFilter).send().getLogs();
        Set<String> legalTx = new HashSet<>();
        list.forEach(logResult -> {
            EthLog.LogObject logObject = (EthLog.LogObject) logResult;
            legalTx.add(logObject.get().getTransactionHash());
        });
        return legalTx;
    }

    /**
     * 判断是否为支持的token
     *
     * @param address 合约地址
     * @return true:支持
     */
    public boolean isSupportToken(String address) {
        return EthSubEvent.contractList.keySet().contains(address);
    }

    /**
     * 根据合约地址获取
     *
     * @param address 合约地址
     * @return 货币名称
     */
    public String getCoinNameByContractAddress(String address) {
        return EthSubEvent.contractList.get(address);
    }

    /**
     * 获取地址的nonce
     *
     * @param address 以太坊地址
     * @param dbNonce 数据库记录的nonce值
     * @param type    user or group
     * @return BigDecimal
     * @throws Exception Exception
     */
    public BigDecimal getNonceByAddress(String address, BigDecimal dbNonce, String type) throws Exception {
        try {
            BigDecimal nonce = new BigDecimal(ethUtils.getAccountNonce(address));
            if (nonce.compareTo(dbNonce) > 0) {
                //数据库nonce落后了
                if ("user".equals(type)) {
                    exchangeUserWalletMapper.updateNonce(CoinConstants.ETH, address, nonce);
                } else {
                    exchangeWalletGroupMapper.updateNonce(address, CoinConstants.ETH, nonce);
                }
                return nonce;
            } else {
                return dbNonce;
            }

        } catch (Exception e) {
            log.error("获取以太坊地址" + address + " nonce值失败： " + ExceptionUtils.getFullStackTrace(e));
            throw e;
        }
    }
}
