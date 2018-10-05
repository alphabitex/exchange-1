package com.liberty.exchange.entity.pojo.wallet;

import com.liberty.exchange.constant.WalletConstants;
import com.liberty.exchange.entity.dto.wallet.ExchangeTransactionDto;
import lombok.*;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ExchangeTrsOrder Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/3
 * Time: 14:04
 * Description: ExchangeTrsOrder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeTrsOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String hash;
    private int userId;
    private int transactionType;
    private String coinType;
    private String coinName;
    private int status;
    private String fromAddress;
    private String toAddress;
    private BigDecimal amount;
    private BigDecimal fee;
    private Date createdTime;
    private Date updatedTime;

    /**
     * 构建充值交易
     *
     * @param transactionDto transactionDto
     */
    public ExchangeTrsOrder(ExchangeTransactionDto transactionDto) {
        this.hash = transactionDto.getHash();
        this.userId = transactionDto.getUserId();
        this.transactionType = transactionDto.getTrsType();
        this.coinType = transactionDto.getCoinType();
        this.coinName = transactionDto.getCoinName();
        this.status = WalletConstants.TRADE_SUCCESS;
        this.fromAddress = transactionDto.getFrom();
        this.toAddress = transactionDto.getTo();
        this.amount = transactionDto.getAmount();
        this.fee = transactionDto.getFee();
        this.createdTime = new Date();
        this.updatedTime = new Date();
    }

}
