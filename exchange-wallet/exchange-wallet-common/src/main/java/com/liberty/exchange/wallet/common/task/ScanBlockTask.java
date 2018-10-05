package com.liberty.exchange.wallet.common.task;

import com.liberty.exchange.constant.WalletConstants;
import com.liberty.exchange.entity.dto.wallet.ExchangeBlockDto;
import com.liberty.exchange.entity.dto.wallet.ExchangeTransactionDto;
import com.liberty.exchange.entity.pojo.wallet.ExchangeScanRecord;
import com.liberty.exchange.mapper.wallet.ExchangeScanRecordMapper;
import com.liberty.exchange.wallet.common.config.WalletProperties;
import com.liberty.exchange.wallet.common.event.ScanBlockEvent;
import com.liberty.exchange.wallet.common.service.BaseBizService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.math.BigInteger;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 6:20
 * Description: ScanBlockTask
 */
@Slf4j
@Component
public class ScanBlockTask implements Runnable {

    private static boolean isScanningOrProcessing = false;   //扫描开关
    private static boolean halt = false;

    @Resource
    private WalletProperties walletProperties;
    @Resource
    private BaseBizService baseBizService;
    @Resource
    private ExchangeScanRecordMapper exchangeScanRecordMapper;
    @Resource
    private ScanBlockEvent scanBlockEvent;

    @Override
    public void run() {
        String coinName = walletProperties.getCoinName();
        BigInteger scanningBlock = new BigInteger("0");
        while (true) {
            try {
                if (halt) break;
                if (isScanningOrProcessing) {
                    Thread.sleep(walletProperties.getBlockScanFixedDelay());
                }
                isScanningOrProcessing = true;

                BigInteger latestBlockNum = baseBizService.getLatestBlockNum();
                log.info(coinName + "最新区块高度为：" + latestBlockNum);
                ExchangeScanRecord exchangeScanRecord = exchangeScanRecordMapper.selectByPrimaryKey(walletProperties.getCoinName());
                if (null == exchangeScanRecord) {
                    this.resetSwitch();
                    log.error(coinName + "不存在exchangeScanRecord数据表相应配置");
                    System.exit(-1);
                }
                //数据库记录的区块高度
                BigInteger blockHeightRecord = exchangeScanRecord.getHeight();
                log.info(coinName + "数据库记录区块最新高度=" + blockHeightRecord);

                //判断是否满足扫块条件，不满足则sleep
                if (latestBlockNum.longValue() - blockHeightRecord.longValue() <= walletProperties.getBlockSlot()) {
                    this.resetSwitch();
                    Thread.sleep(walletProperties.getBlockScanFixedDelay());
                    continue;
                }

                //正在扫描的区块高度
                scanningBlock = blockHeightRecord.add(new BigInteger("1"));
                //解析区块,格式化交易数据
                ExchangeBlockDto blockDto = baseBizService.getBlockByNum(scanningBlock);

                log.info(coinName + "区块" + scanningBlock + "解析成功,包含需要的交易数=" + blockDto.getTrs().size());

                //处理交易
                this.broadcastBlock(blockDto);

                log.info(coinName + "扫描区块" + scanningBlock.longValue() + "完成");

                int res = exchangeScanRecordMapper.updateBlockHeight(coinName);

                log.info(coinName + "更新扫描高度" + scanningBlock.longValue() + "完成:" + res);
                if (latestBlockNum.longValue() - scanningBlock.longValue() > walletProperties.getBlockSlot()) {
                    //睡眠300ms
                    Thread.sleep(300);
                } else {
                    Thread.sleep(walletProperties.getBlockScanFixedDelay());
                }
                this.resetSwitch();
            } catch (Exception e) {
                log.error(coinName + "扫描区块" + scanningBlock.longValue() + "失败: " + ExceptionUtils.getFullStackTrace(e));
            }
        }
    }


    /**
     * 程序终止钩子函数
     * 注意停止服务禁止使用kill -9,否则收不到终止信号
     */
    @PreDestroy
    public void destroy() {
        log.info("服务收到停止终止信号，开始执行停止钩子函数");
        halt = true;
        while (true) {
            if (!isScanningOrProcessing) break;
        }
        log.info("钩子函数执行完毕");
    }

    /**
     * 重置开关
     */
    private void resetSwitch() {
        isScanningOrProcessing = false;
    }

    /**
     * 进一步处理交易
     *
     * @param exchangeBlockDto 区块
     */
    private void broadcastBlock(ExchangeBlockDto exchangeBlockDto) {
        if (null == exchangeBlockDto || null == exchangeBlockDto.getTrs() || exchangeBlockDto.getTrs().size() < 1)
            return;
        for (ExchangeTransactionDto transactionDto : exchangeBlockDto.getTrs()) {
            switch (transactionDto.getTrsType()) {
                case WalletConstants.TRADE_RECHARGE:
                    scanBlockEvent.onRecharge(transactionDto);
                    break;
                case WalletConstants.TRADE_WITHDRAW:
                    scanBlockEvent.onWithdraw(transactionDto);
                    break;
                case WalletConstants.TRADE_RECHARGE_GAS:
                    scanBlockEvent.onRechargeGas(transactionDto);
                    break;
                case WalletConstants.TRADE_RECHARGE_ALL:
                    scanBlockEvent.onRechargeAll(transactionDto);
                    break;
                default:
                    break;
            }
        }
    }

}

