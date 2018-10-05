package com.liberty.exchange.mapper.wallet;

import com.liberty.exchange.entity.dto.wallet.ExchangeWalletGroupDto;
import com.liberty.exchange.entity.pojo.wallet.ExchangeWalletGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by liuhuan on 2018/9/17.
 */
@Repository
public interface ExchangeWalletGroupMapper extends Mapper<ExchangeWalletGroup> {

    /**
     * 根据钱包组类型查询钱包组
     *
     * @param type 钱包组类型
     * @return List
     */
    List<ExchangeWalletGroup> selectGroupByGroupType(@Param("type") int type, @Param("coinName") String coinName);

    /**
     * 查询邮费钱包组
     *
     * @param coinName 货币名称
     * @param balance  金额
     * @return
     */
    List<ExchangeWalletGroup> selectGasGroupByCoinName(@Param("coinName") String coinName, @Param("balance") BigDecimal balance, @Param("type") int type);

    /**
     * 查询满足条件的钱包组
     *
     * @param exchangeWalletGroupDto exchangeWalletGroupDto
     * @return List
     */
    List<ExchangeWalletGroup> selectByGroupTypeAndCoinNameAndCoinType(ExchangeWalletGroupDto exchangeWalletGroupDto);

    /**
     * 选择以太坊提现钱包组
     *
     * @param amount 提现金额+交易费
     * @param from   钱包组地址
     * @return List
     */
    List<ExchangeWalletGroup> selectWithdrawGroupForEth(@Param("amount") BigDecimal amount, @Param("from") String from);

    /**
     * 选择以太坊代币提现钱包组
     *
     * @param amount   提币金额
     * @param fee      手续费
     * @param coinName 货币名称
     * @param from     钱包组地址
     * @return List
     */
    List<ExchangeWalletGroup> selectWithdrawGroupForErc20(@Param("amount") BigDecimal amount, @Param("fee") BigDecimal fee, @Param("coinName") String coinName, @Param("from") String from);

    /**
     * 更新钱包组余额
     *
     * @param balance 金额
     * @param id      id
     * @param fee     手续费
     * @return int
     */
    int updateUsdtWalletGroupBalance(@Param("balance") BigDecimal balance, @Param("id") Integer id, @Param("fee") BigDecimal fee);

    /**
     * 更新eth钱包的nonce
     *
     * @param address  地址
     * @param coinType 货币类型，ETH
     * @return int
     */
    int updateEthGroupNonce(@Param("address") String address, @Param("coinType") String coinType);

    /**
     * 更新eth钱包的nonce
     *
     * @param address  地址
     * @param coinType 货币类型，ETH
     * @return int
     */
    int updateNonce(@Param("address") String address, @Param("coinType") String coinType, @Param("nonce") BigDecimal nonce);

    /**
     * 提现钱包组主币转账后更新操作
     *
     * @param address  地址
     * @param coinType 主币类型
     * @param slot     减少金额
     * @return int
     */
    int updateWhenMainTransfer(@Param("address") String address, @Param("coinType") String coinType, @Param("slot") BigDecimal slot);

    /**
     * 提现钱包组代币转账后更新操作
     *
     * @param address  地址
     * @param coinName 币种名称
     * @param slot     减少金额
     * @return int
     */
    int updateWhenAssetTransfer(@Param("address") String address, @Param("coinName") String coinName, @Param("slot") BigDecimal slot);

}
