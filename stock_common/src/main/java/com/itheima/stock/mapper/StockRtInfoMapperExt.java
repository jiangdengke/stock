package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Entity com.itheima.stock.pojo.entity.StockRtInfo
 */
@Mapper
public interface StockRtInfoMapperExt {

    int deleteByPrimaryKey(Long id);

    int insert(StockRtInfo record);

    int insertSelective(StockRtInfo record);

    StockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockRtInfo record);

    int updateByPrimaryKey(StockRtInfo record);

    /**
     * 查询最新的股票交易数据，且按照涨幅排序取前10
     * @param timePoint 时间点，精确到分钟
     * @param limitNum 根据涨幅排序，限制返回的记录数
     * @return
     */
    List<StockUpdownDomain> getStockIncreaseLimit(@Param("timePoint") Date timePoint,
                                                  @Param("limitNum") int limitNum);

    /**
     * 根据指定时间点查询股票的涨幅信息
     * @param timePoint 时间点，精确到分钟
     * @return
     */
    List<StockUpdownDomain> getAllStockUpDownByTime(@Param("timePoint") Date timePoint);

    /**
     * 统计指定日期范围内股票达到涨停或者跌停的数量
     * @param startTime 起始时间
     * @param endTime 终止时间 一般起始时间和终止时间在同一天
     * @param flag 涨跌停表示：1-》涨停 0-》跌停
     * @return
     */
    List<Map> getUpdwonCount(@Param("startTime") Date startTime,@Param("endTime")  Date endTime,@Param("flag") Integer flag);

    /**
     * 统计指定时间点下股票在各个涨跌区间的数量
     * @param timePoint 时间点，精确到分钟
     * @return
     */
    List<Map> getStockUpDownSectionByTime(@Param("timePoint") Date timePoint);

    /**
     * 查询股票分时信息
     * @param startTime 起始时间
     * @param endTime 截止时间，保证与起始时间在同一天，且大于起始时间
     * @param stockCode 股票编码
     * @return
     */
    List<Stock4MinuteDomain> getStockSecondInfoByCodeAndTime(@Param("startTime") Date startTime,
                                                             @Param("endTime")  Date endTime,
                                                             @Param("stockCode") String stockCode);

    /**
     * 查询股票日K线信息
     * @param startTime 起始时间
     * @param endTime 截止时间，保证与起始时间在同一天，且大于起始时间
     * @param stockCode 股票编码
     * @return
     */
    List<Stock4EvrDayDomain> getStockEvyDayInfoByCodeAndTime(@Param("startTime") Date startTime,
                                                             @Param("endTime")  Date endTime,
                                                             @Param("stockCode") String stockCode);

    /**
     * 批量插入
     * @param list
     */
    int insertBatch(@Param("infos") List<StockRtInfo> list);

    /**
     * 获取日期范围查询指定股票的周K线信息
     * @param stockCode 股票编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<Stock4EvrWeekDomain> getWeekLineData(@Param("stockCode") String stockCode,
                                              @Param("startTime") Date startTime,
                                              @Param("endTime") Date endTime);

    /**
     * 股票股票code和时间查询数据
     * @param stockCode
     * @param curDate
     * @return
     */
    StockRtInfo getStockInfoByCodeAndTimePoint(@Param("code") String stockCode,@Param("timePoint") Date curDate);

    /**
     * 查询执行股票编码大于指定时间点的股票流水信息
     * @param code
     * @param startTime
     * @return
     */
    List<StockShortInfoDomain> getStockInfoGt(@Param("stockCode") String code,
                                              @Param("startTime") Date startTime);

    /**
     * 查询股票每周的基础数据，包含每周的开盘时间点和收盘时间点
     * 但是不包含开盘价格和收盘价格
     * @param stockCode 股票编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<Stock4EvrWeekDomain> getHalfWeekLineData(@Param("stockCode") String stockCode,
                                                  @Param("startTime") Date startTime,
                                                  @Param("endTime") Date endTime);

    /**
     * 查询指定股票在指定时间下的数据
     * @param stockCode
     * @param times
     * @return
     *  map接口：
     *      openPrice:xxx
     *      closePrice:xxx
     */
    List<BigDecimal> getStockInfoByCodeAndTimes(@Param("stockCode") String stockCode,
                                                @Param("times") List<Date> times);

    List<Date> getMaxTimeByTimeRange(@Param("start") Date startDate,@Param("end") Date endDate,@Param("code") String stockCode);

    List<Stock4EvrDayDomain> getDLineInfo(@Param("times") List<Date> times,@Param("code") String stockCode);
}




