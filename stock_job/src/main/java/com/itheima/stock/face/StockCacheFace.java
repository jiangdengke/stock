package com.itheima.stock.face;

import java.util.List;

/**
 * @author : itheima
 * @date : 2023/2/9 11:43
 * @description :
 */
public interface StockCacheFace {
    /**
     * 获取所有股票编码，并添加上证或者深证的股票前缀编号：sh sz
     * @return
     */
    List<String> getAllStockCodeWithPrefix();
}
