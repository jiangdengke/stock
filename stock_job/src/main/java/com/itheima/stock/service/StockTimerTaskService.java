package com.itheima.stock.service;
/**
 * * @Description 定义采集股票数据的定时任务的服务接口
 */
public interface StockTimerTaskService {
    /**
     * 获取国内大盘的实时数据信息
     */
    void getInnerMarketInfo();
    /**
     * 获取国内个股每分钟数据
     */
    void getStockRtInfo();
    /**
     * 获取国内板块数据
     */
    void getBlockRtInfo();
} 