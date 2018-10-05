package com.liberty.exchange.config;

import com.liberty.exchange.common.annotation.DataSource;
import com.liberty.exchange.datasource.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 4:21
 * Description: DataSourceAspect
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect {

    @Pointcut("@annotation(com.liberty.exchange.common.annotation.DataSource)")
    public void dsPointCut() {

    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();

        Method method = signature.getMethod();

        DataSource dataSource = method.getAnnotation(DataSource.class);

        if (null != dataSource) {
            DynamicDataSourceContextHolder.setDateSourceType(dataSource.value().name());
        }

        try {
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            DynamicDataSourceContextHolder.clearDateSourceType();
        }
    }
}
