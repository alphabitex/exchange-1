package com.liberty.exchange.common.base;

import lombok.*;

import java.io.Serializable;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 4:43
 * Description: BaseResponse
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private T data;
    private String message;

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(BaseResponseCode baseResponseCode, Throwable throwable) {
        this.code = baseResponseCode.getCode();
        this.message = throwable.getMessage();
    }

    public BaseResponse(T data) {
        this.data = data;
        this.code = BaseResponseCode.OK.getCode();
    }

    public static BaseResponse OK(Object data) {
        return BaseResponse.builder()
                .code(BaseResponseCode.OK.getCode())
                .data(data)
                .build();
    }

    public static BaseResponse ERROR(String message) {
        return BaseResponse.builder()
                .code(BaseResponseCode.ERROR.getCode())
                .message(message)
                .build();
    }

    public static BaseResponse ERROR(BaseResponseCode baseResponseCode) {
        return BaseResponse.builder()
                .code(baseResponseCode.getCode())
                .message(baseResponseCode.getMessage())
                .build();
    }
}