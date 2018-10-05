package com.liberty.exchange.common.annotation;

import java.lang.annotation.*;

/**
 * FeignApi Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/9/13
 * Time: 12:06
 * Description: FeignApi 安全控制，内部feign调用的API外部没法调用
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface FeignApi {
}
