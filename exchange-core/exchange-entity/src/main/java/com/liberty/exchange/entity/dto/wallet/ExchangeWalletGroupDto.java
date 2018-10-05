package com.liberty.exchange.entity.dto.wallet;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ExchangeWalletGroupDto Created with IntelliJ IDEA.
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
public class ExchangeWalletGroupDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer groupType;
    private String coinName;
    private String address;
    private String coinType;
    private BigDecimal amount;
    private BigDecimal fee;
}
