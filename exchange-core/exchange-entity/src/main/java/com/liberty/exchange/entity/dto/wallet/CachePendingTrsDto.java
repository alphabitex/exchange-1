package com.liberty.exchange.entity.dto.wallet;

import lombok.*;

import java.io.Serializable;

/**
 * CachePendingTrsDto Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/22
 * Time: 21:29
 * Description: CachePendingTrsDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CachePendingTrsDto implements Serializable {
    private static final long serialVersionUID = 2120869894112984147L;

    private String hash;
    private String toAddress;
}
