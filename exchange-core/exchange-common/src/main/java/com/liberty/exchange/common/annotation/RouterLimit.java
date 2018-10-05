package com.liberty.exchange.common.annotation;

import java.lang.annotation.*;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 3:43
 * Description: 限流注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RouterLimit {
    /**
     * 资源的名字
     *
     * @return String
     */
    String name() default "";

    /**
     * 资源的key
     *
     * @return String
     */
    String key() default "";

    /**
     * Key的prefix
     *
     * @return String
     */
    String prefix() default "redis.rateLimit:";

    /**
     * 给定的时间段
     * 单位秒
     *
     * @return int
     */
    int period();

    /**
     * 最多的访问限制次数
     *
     * @return int
     */
    int count();

    /**
     * 类型
     *
     * @return LimitType
     */
    LimitType limitType() default LimitType.IP;
}
