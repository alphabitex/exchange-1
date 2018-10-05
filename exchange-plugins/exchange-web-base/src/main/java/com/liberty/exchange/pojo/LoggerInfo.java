package com.liberty.exchange.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 3:10
 * Description: LoggerInfo
 */
@ConfigurationProperties(prefix = "logger-info",
        ignoreInvalidFields = true)
@Component
@Data
public class LoggerInfo {
    private final Logging logging = new Logging();  //日志


    public static class Logging {
        private final Logstash logstash = new Logstash();

        public Logstash getLogstash() {
            return logstash;
        }

        public static class Logstash {

            private boolean enabled = false;

            private String host = "localhost";

            private String level = "error";

            private int port = 5000;

            private int queueSize = 512;

            private int keepAliveDuration = 5 * 60;

            public int getKeepAliveDuration() {
                return keepAliveDuration;
            }

            public void setKeepAliveDuration(int keepAliveDuration) {
                this.keepAliveDuration = keepAliveDuration;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }

            public int getQueueSize() {
                return queueSize;
            }

            public void setQueueSize(int queueSize) {
                this.queueSize = queueSize;
            }
        }
    }
}
