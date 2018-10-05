package com.liberty.exchange.wallet.eth.config;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:39
 * Description: EthConfig
 */
@Configuration
@Slf4j
public class EthConfig {

    @Bean
    public Web3j web3jInstance(@Value("${web3j.endpoint}") String endpoint) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient httpClient = builder.build();
        return Web3j.build(new HttpService(endpoint, httpClient, false));
    }

    @Bean
    public JsonRpcHttpClient jsonrpcClient(@Value("${web3j.endpoint}") String endpoint) throws MalformedURLException {
        log.info("init jsonRpcClient");
        JsonRpcHttpClient jsonrpcClient = new JsonRpcHttpClient(new URL(endpoint));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        jsonrpcClient.setHeaders(headers);
        return jsonrpcClient;
    }
}
