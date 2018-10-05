package com.liberty.exchange.common.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 4:44
 * Description: BaseResponseCode
 */
@AllArgsConstructor
@Getter
public enum BaseResponseCode {

    ERROR(500, "服务器错误"),
    OK(200, "ok"),
    AUDIT(501, "无可用钱包组"),
    TRANSFER_ERROR(502, "转账失败");

    private int code;
    private String message;

}
