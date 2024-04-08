package com.itheima.stock.pojo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCountTime {

    /**
     * 成交量
     */
    private Long count;
    /**
     * 时间
     */
    private String time;
}
