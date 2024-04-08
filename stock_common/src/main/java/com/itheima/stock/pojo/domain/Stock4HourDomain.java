package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Stock4HourDomain {
    /**
     * 日期，eg:202201280809
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date curDate;
    /**
     * 交易量
     */
    private Long tradeAmt;

    /**
     * 最低价
     */
    private BigDecimal lowPrice;

    /**
     * 最高价
     */
    private BigDecimal highPrice;
    /**
     * 开盘价
     */
    private BigDecimal openPrice;
    /**
     * 当前交易总金额
     */
    private BigDecimal tradeVol;
    /**
     * 当前收盘价格指收盘时的价格，如果当天未收盘，则显示最新cur_price）
     */
    private BigDecimal closePrice;
    /**
     * 前收盘价
     */
    private BigDecimal preClosePrice;
}
