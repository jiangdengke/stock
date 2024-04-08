package com.itheima.stock.job;

import com.itheima.stock.service.StockTimerTaskService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定义股票相关数据的定时任务
 * @author laofang
 */
@Component
public class StockJob {
    /**
     * 注入股票定时任务服务bean
     */
    @Autowired
    private StockTimerTaskService stockTimerTaskService;

    /**
     * 定义定时任务，采集国内大盘数据
     */
    @XxlJob("getStockInnerMarketInfos")
    public void getStockInnerMarketInfos(){
        stockTimerTaskService.getInnerMarketInfo();
    }
    /**
     * 定义定时任务，采集板块数据
     */
    @XxlJob("getBlockInfo")
    public void getBlockInfo(){
        stockTimerTaskService.getBlockRtInfo();
    }
    /**
     * 定义定时任务，采集个股数据
     */
    @XxlJob("getStockRtInfo")
    public void getStockRtInfo(){
        stockTimerTaskService.getStockRtInfo();
    }
}