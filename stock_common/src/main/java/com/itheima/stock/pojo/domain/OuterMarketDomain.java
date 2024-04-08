package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OuterMarketDomain {

/*                    "name": "道琼斯",//大盘名称
                  "curPoint": 36302.38,//当前大盘点
                  "upDown": 351.82,//涨跌值
                  "rose": 0.98//涨幅
                  "curTime": "20211231",//当前时间*/

    /**
     * 大盘名称
     */
    private String name;
    /**
     * 当前点
     */
    private BigDecimal curPoint;
    /**
     * 涨跌值
     */
    private BigDecimal upDown;
    /**
     * 涨幅
     */
    private BigDecimal rose;
    /**
     * 当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date curTime;
}
