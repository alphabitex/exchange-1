package com.liberty.exchange.entity.dto.wallet;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ExchangeTransactionDto Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/29
 * Time: 20:04
 * Description: MonitorInfoBeanDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeTransactionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String hash;
    private int userId;
    private String from;
    private String to;
    private BigDecimal nonce;
    private int trsType; //交易类型 1:充值，2：提现 3：打邮费 4：充值总账
    private BigDecimal amount;
    private String coinName;    //货币名称，OCN，ETH，BTC等
    private String coinType;  //主币类型,ETH,BTC等
    private BigDecimal fee;
}
