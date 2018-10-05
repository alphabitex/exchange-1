package com.liberty.exchange.entity.pojo.wallet;

import lombok.*;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * ExchangeScanRecord Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/29
 * Time: 16:37
 * Description: ExchangeScanRecord
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeScanRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String coinName;
    private BigInteger height;
    private Date updateTime;
}
