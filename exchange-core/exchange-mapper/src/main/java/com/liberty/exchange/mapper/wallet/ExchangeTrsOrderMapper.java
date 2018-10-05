package com.liberty.exchange.mapper.wallet;

import com.liberty.exchange.entity.dto.wallet.CachePendingTrsDto;
import com.liberty.exchange.entity.pojo.wallet.ExchangeTrsOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ExchangeTrsOrderMapper extends Mapper<ExchangeTrsOrder> {

    /**
     * 根据hash和接受者地址查询交易记录
     *
     * @param hash      hash
     * @param toAddress 接受者
     * @return ExchangeTrsOrder
     */
    ExchangeTrsOrder selectByHashAndToAddress(@Param("hash") String hash, @Param("toAddress") String toAddress);

    /**
     * 根据hash和接受者地址查询交易记录
     *
     * @param hash     hash
     * @param coinType 货币类型
     * @return ExchangeTrsOrder
     */
    ExchangeTrsOrder selectByHashAndCoinType(@Param("hash") String hash, @Param("coinType") String coinType);

    /**
     * 根据id修改订单成功状态
     *
     * @param id id
     * @return
     */
    int updateOrderStatusById(@Param("id") Long id);

    /**
     * 查询pending状态的交易信息
     *
     * @param coinType coinType
     * @param status   交易状态
     * @return
     */
    List<CachePendingTrsDto> selectPendingTrs(@Param("coinType") String coinType, @Param("status") int status);


}
