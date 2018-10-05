package com.liberty.exchange.mapper.wallet;

import com.liberty.exchange.entity.pojo.wallet.ExchangeScanRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface ExchangeScanRecordMapper extends Mapper<ExchangeScanRecord> {

    /**
     * 更新扫块记录
     *
     * @param coinName   货币名称
     * @return int
     */
    int updateBlockHeight(@Param("coinName") String coinName);
}
