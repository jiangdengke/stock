package com.itheima.stock.service;

import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface StockService {

    /**
     * 获取国内大盘信息
     *
     * @return
     */
    R<List<InnerMarketDomain>> getStockMarketInfo();

    /**
     * 需求说明: 获取沪深两市板块最新数据，以交易总金额降序查询，取前10条数据
     *
     * @return
     */
    R<List<StockBlockDomain>> sectorAllLimit();

    /**
     * 分页查询股票最新数据，并按照涨幅排序查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    R<PageResult> getStockPageInfo(Integer page, Integer pageSize);

    /**
     * 统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据
     */
    R<List<StockUpdownDomain>> getStockIncrease();

    /**
     * 统计最新交易日下股票每分钟涨跌停的数量
     *
     * @return
     */
    R<Map<String, List>> getStockUpdownCount();

    /**
     * 导出涨幅榜当页数据
     */
    void exportStockUpDown(Integer page, Integer pageSize, HttpServletResponse response);
    /**
     * 功能描述：统计国内A股大盘T日和T-1日成交量对比功能（成交量为沪市和深市成交量之和）
     */
    R<Map<String, List>> stockTradeInnerMarketComparison();
    /**
     * 统计当前时间下（精确到分钟），A股在各个涨跌区间股票的数量；区间：corridor
     */
    R<Map<String,Object>> getCountStockInCorridor();
    /**
     * 查询个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据
     */
    R<List<Stock4MinuteDomain>> getStockTradeMinute(String code);
    /**
     * 单个个股日K 数据查询 ，可以根据时间区间查询数日的K线数据
     * @param
     */
    R<List<Stock4EvrDayDomain>> stockCreenDkLine(String code);

    /**
     * 获取国外大盘信息
     * @return
     */
    R<List<OuterMarketDomain>> getOuterMarketInfo();

    /**
     * 获取股票联想信息
     * @return
     */
    R<List<Map>> getStockSearch(String searchStr);

    /**
     * 个股主营业务查询接口
     * @param code
     * @return
     */
    R<StockBusinessDomain> getStockDescribe(String code);

    R<Stock4HourDomain> getSecondDetail(String code);

    R<List<StockRtLimit10>> getScreenSecond(String code);
}
