package com.liberty.exchange.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.Duration;
import com.liberty.exchange.pojo.LoggerInfo;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 3:12
 * Description: LoggingConfiguration
 */
@Configuration
public class LoggingConfiguration {

    private static final String LOGSTASH_APPENDER_NAME = "LOGSTASH";
    private static final String ASYNC_LOGSTASH_APPENDER_NAME = "ASYNC_LOGSTASH";
    private final String appName;
    private final LoggerInfo loggerInfo;
    private final Logger log = LoggerFactory.getLogger(LoggingConfiguration.class);

    public LoggingConfiguration(@Value("${spring.application.name}") String appName, LoggerInfo loggerInfo) {
        this.appName = appName;
        this.loggerInfo = loggerInfo;
        if (loggerInfo.getLogging().getLogstash().isEnabled()) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            addLogstashAppender(context);
            addContextListener(context);
        }
    }

    private void addLogstashAppender(LoggerContext context) {
        log.info("Initializing Logstash logging");
        LogstashTcpSocketAppender logstashAppender = new LogstashTcpSocketAppender();
        logstashAppender.setName(LOGSTASH_APPENDER_NAME);
        logstashAppender.setContext(context);
        String customFields = "{\"service_name\":\"" + appName + "\"}";

        LogstashEncoder logstashEncoder = new LogstashEncoder();
        logstashEncoder.setCustomFields(customFields);
        logstashAppender.addDestinations(new InetSocketAddress(loggerInfo.getLogging().getLogstash().getHost(),
                loggerInfo.getLogging().getLogstash().getPort()));

        ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();
        throwableConverter.setRootCauseFirst(true);
        logstashEncoder.setThrowableConverter(throwableConverter);
        logstashEncoder.setCustomFields(customFields);

        ThresholdFilter logLevelFilter = new ThresholdFilter();
        logLevelFilter.setLevel(loggerInfo.getLogging().getLogstash().getLevel());
        logstashAppender.setEncoder(logstashEncoder);
        logstashAppender.setKeepAliveDuration(Duration.buildBySeconds(loggerInfo.getLogging().getLogstash().getKeepAliveDuration()));
        logstashAppender.addFilter(logLevelFilter);
        logstashAppender.start();

        AsyncAppender asyncLogstashAppender = new AsyncAppender();
        asyncLogstashAppender.setContext(context);
        asyncLogstashAppender.setName(ASYNC_LOGSTASH_APPENDER_NAME);
        asyncLogstashAppender.setQueueSize(loggerInfo.getLogging().getLogstash().getQueueSize());
        asyncLogstashAppender.addAppender(logstashAppender);
        asyncLogstashAppender.start();
        context.getLogger("ROOT").setLevel(Level.toLevel(loggerInfo.getLogging().getLogstash().getLevel()));
        context.getLogger("ROOT").addAppender(asyncLogstashAppender);
    }

    private void addContextListener(LoggerContext context) {
        LogbackLoggerContextListener loggerContextListener = new LogbackLoggerContextListener();
        loggerContextListener.setContext(context);
        context.addListener(loggerContextListener);
    }

    class LogbackLoggerContextListener extends ContextAwareBase implements LoggerContextListener {

        @Override
        public boolean isResetResistant() {
            return true;
        }

        @Override
        public void onStart(LoggerContext context) {
            addLogstashAppender(context);
        }

        @Override
        public void onReset(LoggerContext context) {
            addLogstashAppender(context);
        }

        @Override
        public void onStop(LoggerContext context) {
            // Nothing to do.
        }

        @Override
        public void onLevelChange(ch.qos.logback.classic.Logger logger, Level level) {
            // Nothing to do.
        }
    }
}
