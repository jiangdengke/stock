package com.itheima.stock.face.impl;

import com.itheima.stock.face.StockCacheFace;
import com.itheima.stock.mapper.StockBusinessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : itheima
 * @date : 2023/2/9 11:44
 * @description 定义股票缓存层的实现
 */
@Component("stockCacheFace")
public class StockCacheFaceImpl implements StockCacheFace {

    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Cacheable(cacheNames = "stock" ,key = "'stockCodes'")
    @Override
    public List<String> getAllStockCodeWithPrefix() {
        //1.获取所有个股的集合 3000+
        List<String> allCodes = stockBusinessMapper.getAllStockCodes();
        //添加大盘业务前缀 sh sz
        allCodes=allCodes.stream().map(code->code.startsWith("6")?"sh"+code:"sz"+code).collect(Collectors.toList());
        return allCodes;
    }
}
