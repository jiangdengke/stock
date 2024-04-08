package com.itheima.stock.config;

import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.utils.IdWorker;
import com.itheima.stock.utils.ParserStockInfoUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StockInfoConfig.class)//开启常用参数配置bean
public class CommonConfig {
    /**
     * 基于雪花算法保证生成的id唯一
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        /**
         * 参数一：机房id
         * 参数二：容器id
         */
        return new IdWorker(1L,2L);
    }
    @Bean
    public ParserStockInfoUtil parserStockInfoUtil(IdWorker idWorker){
        return new ParserStockInfoUtil(idWorker);
    }


}
