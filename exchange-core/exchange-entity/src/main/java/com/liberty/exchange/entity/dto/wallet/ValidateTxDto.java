package com.liberty.exchange.entity.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * ValidateTxDto Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/3
 * Time: 11:23
 * Description: ValidateTxDto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateTxDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigInteger gasUsed;
    private boolean success;  //交易状态
}

