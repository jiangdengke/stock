package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockRtLimit10 {
    /**
     * 日期，eg:202201280809
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date date;    /**
     * 交易量
     */
    private Long tradeAmt;
    /**
     * 当前交易总金额
     */
    private BigDecimal tradeVol;
    /**
     * 交易价格
     */
    private BigDecimal tradePrice;
}
