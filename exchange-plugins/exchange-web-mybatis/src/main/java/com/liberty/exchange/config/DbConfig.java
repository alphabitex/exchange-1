package com.liberty.exchange.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 18-9-14
 * Time: 上午2:08
 * Description: DbConfig
 */
@Configuration
@MapperScan("com.liberty.exchange.dao.mapper")
public class DbConfig {
}
