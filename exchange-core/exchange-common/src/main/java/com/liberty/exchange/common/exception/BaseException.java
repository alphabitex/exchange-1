package com.liberty.exchange.common.exception;


import com.liberty.exchange.common.base.BaseResponseCode;

/**
 * BaseException Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/28
 * Time: 11:20
 * Description: BaseException
 */
public class BaseException extends RuntimeException {
    private BaseResponseCode baseResponseCode = BaseResponseCode.ERROR;

    public BaseResponseCode getBaseResponseCode() {
        return baseResponseCode;
    }

    public void setBaseResponseCode(BaseResponseCode baseResponseCode) {
        this.baseResponseCode = baseResponseCode;
    }

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(BaseResponseCode baseResponseCode) {
        super(baseResponseCode.getMessage());
        this.baseResponseCode = baseResponseCode;
    }

    public BaseException(BaseResponseCode baseResponseCode, String message) {
        super(message);
        this.baseResponseCode = baseResponseCode;
    }

    public BaseException(BaseResponseCode baseResponseCode, Throwable cause) {
        super(baseResponseCode.getMessage(), cause);
        this.baseResponseCode = baseResponseCode;
    }


    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
