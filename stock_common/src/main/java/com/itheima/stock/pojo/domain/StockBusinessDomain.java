package com.itheima.stock.pojo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author by itheima
 * @Date 2022/5/18
 * @Description 个股主营业务描述实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockBusinessDomain {
    /**
     * 股票编码
     */
    private String code;
    /**
     * 公司名称
     */
    private String name;
    /**
     * 所属行业
     */
    private String trade;
    /**
     * 主营业务描述
     */
    private String business;
}
