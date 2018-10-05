package com.liberty.exchange.wallet.common.utils;

import com.liberty.exchange.wallet.common.config.WalletProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * DecimalUtils Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/17
 * Time: 11:24
 * Description: DecimalUtils
 */
@Component
public class DecimalUtils {

    @Resource
    private WalletProperties walletProperties;


    /**
     * 0.1 >> 100000000000000000
     *
     * @param coinName 货币名称
     * @param number   数量
     * @return BigDecimal
     */
    public BigDecimal convert(String coinName, BigDecimal number) {
        int unit = walletProperties.getCoinFactory().get(coinName).getUnit();
        return number.multiply(BigDecimal.TEN.pow(unit));
    }

    /**
     * 100000000000000000 >> 0.1
     *
     * @param coinName 货币名称
     * @param number   数量
     * @return BigDecimal
     */
    public BigDecimal reconvert(String coinName, BigDecimal number) {
        int unit = walletProperties.getCoinFactory().get(coinName).getUnit();
        return number.divide(BigDecimal.TEN.pow(unit),unit,BigDecimal.ROUND_HALF_UP);
    }
}
