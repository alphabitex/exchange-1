package com.liberty.exchange.entity.dto.wallet;

import lombok.*;

import java.io.Serializable;

/**
 * AccountDto Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 30/07/2018
 * Time: 14:48
 * Description: AccountDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDto implements Serializable {
    private static final long serialVersionUID = 2120869894112984147L;
    private String privateKey;
    private String publicKey;
    private String address;
}
