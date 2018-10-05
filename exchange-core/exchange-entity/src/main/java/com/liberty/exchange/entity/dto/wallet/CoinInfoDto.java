package com.liberty.exchange.entity.dto.wallet;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * CoinInfoDto Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/3
 * Time: 17:42
 * Description: CoinInfoDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoinInfoDto implements Serializable {
    private static final long serialVersionUID = 2120869894112984147L;

    private String coinName;
    private BigDecimal fee;
    private Integer unit;  //精度
    private String contractAddress;
    private BigDecimal minCollectAmount; //汇总最低金额
}
