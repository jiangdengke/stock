package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.InnerMarketDomain;
import com.itheima.stock.pojo.domain.OuterMarketDomain;
import com.itheima.stock.pojo.domain.StockCountTime;
import com.itheima.stock.pojo.entity.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 46035
* @description 针对表【stock_market_index_info(国内大盘数据详情表)】的数据库操作Mapper
* @createDate 2023-01-08 15:14:39
* @Entity com.itheima.stock.pojo.entity.StockMarketIndexInfo
*/
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);

    /**
     * 根据指定时间点查询指定大盘编码对应的数据
     * @return
     */
    List<StockCountTime> stockTradeInnerMarketComparison(
            @Param("markedIds") List<String> markedIds, @Param("openTime") Date openTime,@Param("curTime") Date curTime);
    List<InnerMarketDomain> getMarketInfo(@Param("curDate") Date curDate, @Param("marketCodes") List<String> marketCodes);

    /**
     * 统计指定日期范围内 指定大盘每分钟的成交量流水信息
     * @param openDate 起始时间，一般之开盘时间
     * @param endDate 截止时间，一般与openDate同一天
     * @param marketCodes 大盘编码集合
     * @return
     */
    List<Map> getSumAmtInfo(@Param("openDate") Date openDate, @Param("endDate") Date endDate, @Param("marketCodes") List<String> marketCodes);

    /**
     * 批量插入大盘数据
     * @param entities 大盘实体对象集合
     * @return
     */
    int insertBatch(@Param("infos") List<StockMarketIndexInfo> entities);

}
