package com.liberty.exchange.entity.pojo.wallet;

import lombok.*;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ExchangeUserWallet Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/29
 * Time: 16:39
 * Description: ExchangeUserWallet
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeUserWallet implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;
    private int userId;
    private String address;
    private String privateKey;
    private BigDecimal nonce;
    private BigDecimal balance;
    private String coinName;
    private String coinType;
}
