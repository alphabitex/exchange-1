package com.liberty.exchange.mapper.wallet;

import com.liberty.exchange.entity.pojo.wallet.ExchangeUserWallet;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * ExchangeUserWalletMapper.xml Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/29
 * Time: 16:44
 * Description: ExchangeUserWalletMapper.xml
 */
@Repository
public interface ExchangeUserWalletMapper extends Mapper<ExchangeUserWallet> {

    /**
     * 根据用户ID和代币名称查询钱包
     *
     * @param userId   用户ID
     * @param coinName 货币名称
     * @return ExchangeUserWallet
     */
    ExchangeUserWallet selectWalletByUserAndCoinName(@Param("userId") int userId, @Param("coinName") String coinName);

    /**
     * 根据用户ID和代币种类查询钱包
     *
     * @param userId   用户ID
     * @param coinType 货币种类
     * @return ExchangeUserWallet
     */
    ExchangeUserWallet selectWalletByUserAndCoinType(@Param("userId") int userId, @Param("coinType") String coinType);

    /**
     * 根据钱包地址和代币名字查询钱包
     *
     * @param address  地址
     * @param coinName 货币种类
     * @return ExchangeUserWallet
     */
    ExchangeUserWallet selectWalletByAddressAndCoinName(@Param("address") String address, @Param("coinName") String coinName);


    /**
     * 根据coinName查询用户钱包
     *
     * @param coinName coinName
     * @return
     */
    List<ExchangeUserWallet> selectWalletsByCoinName(@Param("coinName") String coinName);

    /**
     * 根据coinType查询用户钱包
     *
     * @param coinType coinType
     * @return List<String>
     */
    List<String> selectWalletsByCinType(@Param("coinType") String coinType);

    /**
     * 更新用户钱包余额
     *
     * @param coinName 货币名称
     * @param balance
     * @return
     */
    int updateUserBalance(@Param("coinName") String coinName, @Param("balance") BigDecimal balance, @Param("address") String address);

    /**
     * 更新以太坊nonce
     *
     * @param coinType
     * @param address
     * @return
     */
    int updateUserNonce(@Param("coinType") String coinType, @Param("address") String address);

    /**
     * 更新以太坊账户nonce
     *
     * @param coinType 主币类型
     * @param address  地址
     * @param nonce    nonce
     * @return
     */
    int updateNonce(@Param("coinType") String coinType, @Param("address") String address, @Param("nonce") BigDecimal nonce);

    /**
     * 更新用户钱包余额
     *
     * @param id      id
     * @param balance 金额
     * @return
     */
    int updateUserUsdtBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);


}
