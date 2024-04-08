package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author by itheima
 * @Date 2022/2/28
 * @Description 个股周K数据封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock4EvrWeekDomain {
    /**
     * 股票编码
     */
    private String stockCode;
    /**
     * 开盘价
     */
    private BigDecimal openPrice;
    /**
     * 平均价
     */
    private BigDecimal avgPrice;
    /**
     * 最低价
     */
    private BigDecimal minPrice;
    /**
     * 最高价
     */
    private BigDecimal maxPrice;
    /**
     * 收盘价
     */
    private BigDecimal closePrice;

    /**
     * 一周的最大时间，一般指周五
     */
    @JsonFormat(pattern = "yyy-MM-dd")
    private Date mxTime;

    /**
     * 每周的收盘时间点
     * 临时字段，前端无需展示
     */
    @JsonIgnore
    private Date miTime;

}
