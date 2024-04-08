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
 * @Description 股票交易流水行情信息（简短信息）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockShortInfoDomain {
    /**
     * 当前时间点 精确到分钟
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;

    /**
     * 交易量
     */
    private Long tradeAmt;

    /**
     * 交易金额
     */
    private BigDecimal tradeVol;

    /**
     * 交易金额
     */
    private BigDecimal tradePrice;

}
