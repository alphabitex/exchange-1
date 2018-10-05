package com.liberty.exchange.constant;

import java.math.BigDecimal;

/**
 * CoinConstants Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/13
 * Time: 16:56
 * Description: CoinConstants
 */
public class CoinConstants {

    //以太坊
    public static final String ETH = "ETH";
    public static final BigDecimal ETH_GAS_LIMIT = new BigDecimal("21000"); //eth 转账gas limit
    public static final BigDecimal ETH_TOKEN_GAS_LIMIT = new BigDecimal("120000"); //eth token转账gas limit
    public static final int ETH_GWEI_UNIT = 9; //eth 1GWei精度
    public static final BigDecimal GAS_PRICE = new BigDecimal("40"); //以太坊转账默认手续费 40Gwei

    //USDT
    public static final String USDT = "USDT";
    public static final String PROPERTYID_USDT = "31";  //usdt的token Id

}
