package com.liberty.exchange.mapper.wallet;

import com.liberty.exchange.entity.pojo.wallet.ExchangeMockCoin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ExchangeMockCoinMapper extends Mapper<ExchangeMockCoin> {

    /**
     * mock数据
     *
     * @param coinType coinType
     * @return
     */
    List<ExchangeMockCoin> selectAllByCoinType(@Param("coinType") String coinType);

}
