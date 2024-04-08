package com.itheima.stock.mapper;

import com.github.pagehelper.Page;
import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 46035
* @description 针对表【stock_rt_info(个股详情信息表)】的数据库操作Mapper
* @createDate 2023-01-08 15:14:39
* @Entity com.itheima.stock.pojo.entity.StockRtInfo
*/
public interface StockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockRtInfo record);

    int insertSelective(StockRtInfo record);

    StockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockRtInfo record);

    int updateByPrimaryKey(StockRtInfo record);

    /**
     * 查询指定时间点下股票数据集合
     * @param curDate  日期时间
     * @return
     */
    List<StockUpdownDomain> getStockInfoByTime(@Param("curDate") Date curDate);

    /**
     * 统计指定日期范围内股票涨停或者跌停的数量流水
     * @param startDate 开始时间，一般指开盘时间
     * @param endDate 截止时间
     * @param flag 约定：1代表统计涨停 0：跌停
     * @return
     */
    List<StockCountTime> getStockUpdownCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("flag") int flag);
    /**
     * 查询指定时间点下股票的数据，并按照涨幅降序排序
     * @param curDate
     * @return
     */
    Page<StockUpdownDomain> getNewestStockInfo(@Param("curDate") Date curDate);
    /**
     * 统计指定时间点下股票在各个涨跌区间的数量
     * @param curDate
     * @return
     */
    List<Map> getIncreaseRangeInfoByDate(@Param("dateTime") Date curDate);
    /**
     * 查询涨幅榜前四条信息
     * @return
     * @param curDate
     */
    List<StockUpdownDomain> getStockIncrease(Date curDate);
    /**
     * 统计当前时间下（精确到分钟），A股在各个涨跌区间股票的数量；区间：corridor
     */
    List<Map> getCountStockInCorridor(@Param("date") Date date);
    Stock4HourDomain getSecondDetail(@Param("endTime") Date endTime, @Param("code") String code);
    List<StockRtLimit10> getScreenSecond(@Param("endTime") Date endTime, @Param("code") String code);

    /**
     * 根据股票编码查询指定时间范围内的分时数据
     * @return
     */
    List<Stock4EvrDayDomain> getStockInfo4EvrDay(@Param("list") List<Date> list,@Param("stockCode") String stockCode);

    List<Date> getCurrentTimeList(@Param("stockCode") String stockCode,
                                  @Param("startTime") Date startTime,
                                  @Param("endTime") Date endTime);
    List<Stock4MinuteDomain> getStock4MinuteInfo(@Param("openDate") Date openDate, @Param("endDate") Date endDate, @Param("stockCode") String stockCode);
    /**
     * 查询个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据
     */
    List<Stock4MinuteDomain> getStockTradeMinute(@Param("code") String code,@Param("openTime") Date openTime,@Param("curTime") Date curTime);

    /**
     * 根据股票编码查询指定时间范围内的日K线数据
     * @param startDate 开盘时间
     * @param endDate 截止时间，一般与开盘时间同一天
     * @param stockCode 股票编码
     * @return
     */
    List<Stock4EvrDayDomain> getStock4DkLine(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("stockCode") String stockCode);

    /**
     * 批量插入个股数据
     * @param list
     * @return
     */
    int insertBatch(@Param("list") List<StockRtInfo> list);

    List<Date> getMxTime4EvryDay(@Param("stockCode") String stockCode, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<Stock4EvrDayDomain> getStockScreenDkLine2(@Param("stockCode") String stockCode, @Param("mxTimes") List<Date> mxTimes);
}
