package com.itheima.stock.mq;

import com.github.benmanes.caffeine.cache.Cache;
import com.itheima.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author : itheima
 * @date : 2023/1/14 16:37
 * @description  定义股票相关mq消息监听
 */
@Component
@Slf4j
public class StockMQMsgListener {

    @Autowired
    private Cache<String,Object> caffeineCache;

    @Autowired
    private StockService stockService;

    @RabbitListener(queues = "innerMarketQueue")
    public void refreshInnerMarketInfo(Date startTime){
        //统计当前时间点与发送消息时时间点的差值，如果超过1分钟，则告警
        //获取时间毫秒差值
        long diffTime= DateTime.now().getMillis()-new DateTime(startTime).getMillis();
        if (diffTime > 60000l) {
           log.error("大盘发送消息时间:{},延迟：{}ms",new DateTime(startTime).toString("yyyy-MM-dd HH:mm:ss"),diffTime);
        }
        //刷新缓存
        //剔除旧的数据
        caffeineCache.invalidate("innerMarketKey");
        //调用服务方法，刷新数据
        stockService.getStockMarketInfo();
    }



}
