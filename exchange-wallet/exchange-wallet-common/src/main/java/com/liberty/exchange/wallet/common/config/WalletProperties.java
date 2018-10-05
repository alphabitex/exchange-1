package com.liberty.exchange.wallet.common.config;

import com.google.common.collect.Sets;
import com.liberty.exchange.entity.dto.wallet.CoinInfoDto;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/28
 * Time: 15:23
 * Description: WalletProperties
 */
@ConfigurationProperties(prefix = "wallet",
        ignoreInvalidFields = true)
@Component
@Data
public class WalletProperties {
    private final Async async = new Async();  //异步线程
    private String coinName;  //主币名称
    private int blockSlot = 10; //区块扫描确认数,默认十个确认区块
    private long blockScanFixedDelay = 10 * 1000; //区块扫描周期，默认10秒扫描一次
    private Map<String, CoinInfoDto> coinFactory;
    private final RechargeALL rechargeAll = new RechargeALL(); //汇总选项 默认不需要汇总，并且不需要打邮费
    private Set<String> userAddressList = Sets.newConcurrentHashSet();
    private Set<String> pendingTrsList = Sets.newConcurrentHashSet(); //pending状态中的交易列表  hash+toAddress

    public static class RechargeALL {
        private boolean need = false;
        private boolean needRechargeFee = false;
        private int pendingNum = 5;

        public int getPendingNum() {
            return pendingNum;
        }

        public void setPendingNum(int pendingNum) {
            this.pendingNum = pendingNum;
        }

        public boolean isNeed() {
            return need;
        }

        public void setNeed(boolean need) {
            this.need = need;
        }

        public boolean isNeedRechargeFee() {
            return needRechargeFee;
        }

        public void setNeedRechargeFee(boolean needRechargeFee) {
            this.needRechargeFee = needRechargeFee;
        }
    }

    public static class Async {
        private int corePoolSize = 2;

        private int maxPoolSize = 50;

        private int queueCapacity = 10000;

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }
}
