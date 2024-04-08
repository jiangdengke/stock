package com.itheima.stock.service;

import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.vo.resp.R;

import java.util.List;
import java.util.Map;

/**
 * @author by itheima
 * @Date 2022/5/30
 * @Description 定义股票相关服务接口
 */
public interface StockServiceExt {

    /**
     * 查询最新的股票交易数据，且按照涨幅排序取前10
     * @return
     */
    R<List<StockUpdownDomain>> getStockIncreaseLimit();

    /**
     * 外盘指数
     * 外盘指数行情数据查询 根据时间取前4
     * @return
     */
    R<List<StockOuterMarketDomain>> getExternalIndex();

    /**
     * 根据输入的个股代码，进行模糊查询，返回证券代码和证券名称
     * @param searchStr 个股代码模糊搜索
     * @return
     */
    R<List<Map>> searchStock(String searchStr);

    /**
     * 根据code查询个股主营业务接口查询
     * @param stockCode 股票编码
     * @return
     */
    R<StockBusinessDomain> getStockDescribe(String stockCode);

    /**
     * 单个个股周K线数据：包含股票ID 最高价 最低价 开盘价 收盘价 均价
     * 具体：最高和最低是一周内的
     *      开盘与收盘分别对应周1的开盘价格和周五的收盘价格
     *      均价就是一周的平均价格
     *      日期：一周内的最大日期，一般是周五
     * @param stockCode 股票编码
     */
    R<List<Stock4EvrWeekDomain>> getWeekKLinData(String stockCode);

    /**
     * 获取个股最新分时行情数据，包含开盘价、前收盘价、最新价、最高价、最低价、成交金额和成交量信息
     * @param stockCode 股票编码
     * @return
     */
    R<StockLatestInfoDomain> getStockSecondDetail(String stockCode);

    /**
     * 获取个股实时交易流水信息，按照时间降序排序，取前10
     * 单个个股秒级行情数据查询，查询当前分钟内的秒级数据（我们当前以分钟为单位获取数据）
     * @param code 股票编码
     * @return
     */
    R<List<StockShortInfoDomain>> getStockScreenSecond(String code);

}
