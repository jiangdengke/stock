package com.itheima.stock.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : itheima
 * @date : 2023/1/11 9:14
 * @description : 定义股票相关的值对象封装
 */
@ApiModel(description = "定义股票相关的值对象封装")
@Data
@ConfigurationProperties(prefix = "stock")
//@Component
public class StockInfoConfig {

    /**
     * 封装国内A股大盘编码集合
     */
    @ApiModelProperty("封装国内A股大盘编码集合")
    private List<String > inner;

    /**
     * 外盘编码集合
     */
    @ApiModelProperty("外盘编码集合")
    private List<String> outer;

    /**
     * 股票涨幅区间标题集合
     */
    @ApiModelProperty("股票涨幅区间标题集合")
    private List<String> upDownRange;

    /**
     * 大盘 外盘 个股的公共URL
     */
    @ApiModelProperty("大盘 外盘 个股的公共URL")
    private String marketUrl;

    /**
     * 板块采集URL
     */
    @ApiModelProperty("板块采集URL")
    private String blockUrl;


}
