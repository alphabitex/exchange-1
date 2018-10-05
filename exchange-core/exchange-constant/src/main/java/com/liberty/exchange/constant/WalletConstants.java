package com.liberty.exchange.constant;

/**
 * WalletConstants Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/4
 * Time: 09:56
 * Description: WalletConstants
 */
public class WalletConstants {

    //交易类型
    public static final int TRADE_RECHARGE = 1; //用户充值
    public static final int TRADE_WITHDRAW = 2; //提现
    public static final int TRADE_RECHARGE_GAS = 3; //充值邮费
    public static final int TRADE_RECHARGE_ALL = 4; //充值总账

    //交易状态
    public static final int TRADE_PENDING = 1; //交易进行中
    public static final int TRADE_SUCCESS = 2; //交易成功
    public static final int TRADE_FAIL = 3; //交易失败


    //钱包组类型
    public static final int WALLET_GROUP_GAS = 1; //邮费钱包组
    public static final int WALLET_GROUP_RECHARGE = 2; //充值钱包组
    public static final int WALLET_GROUP_WITHDRAW = 3; //提现钱包组



}
