package com.liberty.exchange.wallet.eth.utils;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.liberty.exchange.common.base.BaseResponse;
import com.liberty.exchange.common.base.BaseResponseCode;
import com.liberty.exchange.common.crypto.EncryptUtils;
import com.liberty.exchange.constant.CoinConstants;
import com.liberty.exchange.entity.dto.wallet.AccountDto;
import com.liberty.exchange.entity.dto.wallet.ValidateTxDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * EthUtils Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/3
 * Time: 14:13
 * Description: EthUtils
 */
@Component
@Slf4j
public class EthUtils {
    @Resource
    private Web3j web3j;
    @Resource
    private JsonRpcHttpClient jsonRpcHttpClient;
    @Resource
    private EncryptUtils encryptUtils;

    /**
     * 获取最新区块高度
     *
     * @return BigInteger
     * @throws IOException
     */
    public BigInteger getLastBlockNum() throws IOException {
        return web3j.ethBlockNumber().send().getBlockNumber();
    }

    /**
     * 获取区块详情
     *
     * @param blockNum 区块高度
     * @return EthBlock.Block
     * @throws IOException
     */
    public EthBlock.Block getBlockByBlockNum(BigInteger blockNum) throws IOException {
        return web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNum), true).send().getBlock();
    }

    /**
     * 根据hash校验交易状态
     *
     * @param hash
     * @return
     */
    public ValidateTxDto validateTx(String hash) throws IOException {
        Optional<TransactionReceipt> transactionReceipt = web3j.ethGetTransactionReceipt(hash).send().getTransactionReceipt();
        return ValidateTxDto.builder()
                .gasUsed(transactionReceipt.get().getGasUsed())
                .success(transactionReceipt.get().isStatusOK())
                .build();

    }

    /**
     * 转账
     *
     * @param privateKey 发送者私钥
     * @param toAddress  接受者
     * @param amount     金额
     * @param gasPrice   gasPrice
     * @return MessageResult
     */
    public BaseResponse transferEth(String privateKey, String toAddress, BigDecimal amount, BigDecimal gasPrice, BigDecimal nonce) {
        try {
            Credentials credentials = Credentials.create(privateKey);
            BigInteger value = amount.toBigInteger();

            log.info("value={},gasPrice={},gasLimit={},nonce={},address={}", value, gasPrice, CoinConstants.ETH_GAS_LIMIT, nonce, toAddress);
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce.toBigInteger(), gasPrice.toBigInteger(), CoinConstants.ETH_GAS_LIMIT.toBigInteger(), toAddress, value);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String transactionHash = ethSendTransaction.getTransactionHash();
            log.info("txid =" + transactionHash);
            if (StringUtils.isEmpty(transactionHash)) {
                log.error("err:" + ethSendTransaction.getError().getMessage());
                return BaseResponse.ERROR(BaseResponseCode.TRANSFER_ERROR);
            }
            return BaseResponse.OK(transactionHash);
        } catch (Exception e) {
            log.error("交易失败,error:" + ExceptionUtils.getFullStackTrace(e));
            return BaseResponse.ERROR(BaseResponseCode.TRANSFER_ERROR);
        }
    }

    /**
     * 代币转账
     *
     * @param privateKey      私钥
     * @param contractAddress 代币地址
     * @param toAddress       接收者
     * @param amount          金额
     * @param gasPrice        交易费
     * @param nonce           nonce
     * @return
     */
    public BaseResponse transferTokenFromAddress(String privateKey, String contractAddress,
                                                 String toAddress, BigDecimal amount, BigDecimal gasPrice, BigDecimal nonce) {
        try {
            Credentials credentials = Credentials.create(privateKey);
            Function fn = new Function("transfer", Arrays.asList(new Address(toAddress), new Uint256(amount.toBigInteger())),
                    Collections.<TypeReference<?>>emptyList());
            String data = FunctionEncoder.encode(fn);
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce.toBigInteger(), gasPrice.toBigInteger(), CoinConstants.ETH_TOKEN_GAS_LIMIT.toBigInteger(), contractAddress, data);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String transactionHash = ethSendTransaction.getTransactionHash();
            log.info("txid:" + transactionHash);
            if (StringUtils.isEmpty(transactionHash)) {
                log.error("转移代币失败，err:" + ethSendTransaction.getError().getMessage());
                return BaseResponse.ERROR(BaseResponseCode.TRANSFER_ERROR);
            }
            return BaseResponse.OK(transactionHash);
        } catch (Exception e) {
            log.error("转移代币失败，err:" + ExceptionUtils.getFullStackTrace(e));
            return BaseResponse.ERROR(BaseResponseCode.TRANSFER_ERROR);
        }
    }

    /**
     * 查询gas费用
     *
     * @return
     * @throws IOException
     */
    public BigInteger getGasPrice() throws IOException {
        EthGasPrice gasPrice = web3j.ethGasPrice().send();
        return gasPrice.getGasPrice();
    }

    /**
     * 查询代币余额
     *
     * @param address  账户地址
     * @param contract 合约地址
     * @return
     * @throws IOException
     */
    public BigDecimal getTokenBalance(String address, String contract) throws Exception {
        BigInteger balance = BigInteger.ZERO;
        Function fn = new Function("balanceOf", Collections.singletonList(new Address(address)), Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(fn);
        Map<String, String> map = new HashMap<String, String>();
        map.put("to", contract);
        map.put("data", data);
        try {
            String methodName = "eth_call";
            Object[] params = new Object[]{map, "latest"};
            String result = jsonRpcHttpClient.invoke(methodName, params, Object.class).toString();
            if (StringUtils.isNotEmpty(result)) {
                if ("0x".equalsIgnoreCase(result) || result.length() == 2) {
                    result = "0x0";
                }
                balance = Numeric.decodeQuantity(result);
            }
        } catch (Throwable e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            throw new Exception("查询接口ERROR");
        }
        return new BigDecimal(balance);
    }

    /**
     * 获取矿工手续费
     *
     * @param gasLimit gasLimit
     * @return
     * @throws IOException
     */
    public BigDecimal getMinerFee(BigInteger gasLimit) throws IOException {
        return new BigDecimal(getGasPrice().multiply(gasLimit));
    }

    /**
     * 获取用户地址以太坊余额
     *
     * @param address
     * @return
     * @throws IOException
     */
    public BigDecimal getBalance(String address) throws IOException {
        EthGetBalance getBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        return Convert.fromWei(getBalance.getBalance().toString(), Convert.Unit.ETHER);
    }


    /**
     * 获取用户地址的nonce值
     *
     * @param address 用户地址
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public BigInteger getAccountNonce(String address) throws ExecutionException, InterruptedException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                address, DefaultBlockParameterName.LATEST).sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }

    /**
     * 新建以太坊账号
     *
     * @return AccountDto
     * @throws GeneralSecurityException GeneralSecurityException
     * @throws CipherException          CipherException
     */
    public AccountDto generateKeyAddress() throws GeneralSecurityException, CipherException {
        String seed = UUID.randomUUID().toString() + System.currentTimeMillis();
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();
        String privateKey = privateKeyInDec.toString(16);
        WalletFile aWallet = Wallet.createLight(seed, ecKeyPair);
        String sAddress = "0x" + aWallet.getAddress();
        return AccountDto.builder()
                .address(sAddress)
                .privateKey(encryptUtils.encrypt(privateKey))
                .build();
    }

}
