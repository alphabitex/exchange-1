package com.liberty.exchange.entity.pojo.wallet;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ExchangeWalletGroup Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/29
 * Time: 16:39
 * Description: ExchangeWalletGroup
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeWalletGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer groupType; //钱包类型  1：邮费钱包组   2：充值钱包组  3：提现钱包组

    private Integer active; //1:激活状态  0：禁用状态

    private String address;

    private String coinName;

    private String coinType;

    private BigDecimal nonce;

    private BigDecimal balance;

    private BigDecimal mainBalance;

    private String privateKey;

    private Date updatedAt;

    private Integer updatedBy;

    private Date createdAt;

    private Integer createdBy;
}
