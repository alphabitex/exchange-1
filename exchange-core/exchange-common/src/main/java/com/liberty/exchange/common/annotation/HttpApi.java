package com.liberty.exchange.common.annotation;

import java.lang.annotation.*;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 4:57
 * Description: api統一注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface HttpApi {

    boolean login() default false; //是否校验登陆信息

    boolean log() default false; //是否记录日志信息

    String functionId() default ""; //接口功能号码
}
