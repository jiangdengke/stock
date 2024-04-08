package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author by itheima
 * @Date 2022/5/18
 * @Description 定义外盘展示数据实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockOuterMarketDomain {
    /**
     * 外盘名称
     */
    private String name;
    /**
     * 当前点
     */
    private BigDecimal curPoint;
    /**
     * 大盘涨跌
     */
    private BigDecimal upDown;
    /**
     * 大盘涨幅
     */
    private BigDecimal rose;
    /**
     * 当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date curTime;
}
