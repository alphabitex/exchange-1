package com.liberty.exchange.entity.pojo.wallet;

import lombok.*;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ExchangeMockCoin Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/4
 * Time: 10:21
 * Description: ExchangeMockCoin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeMockCoin implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String coinName;
    private int unit;  //精度
    private BigDecimal fee; // 交易手续费，对于以太坊来说就是gasPrice例如:5表示5Gwei
    private String contractAddress;
    private BigDecimal minCollectAmount; //汇总最低金额
}
