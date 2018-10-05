package com.liberty.exchange.entity.dto.wallet;

import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * ExchangeBlockDto Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/29
 * Time: 19:35
 * Description: ExchangeBlockDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeBlockDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigInteger height;
    private String hash;
    private List<ExchangeTransactionDto> trs;
    private int trsNum;
}
