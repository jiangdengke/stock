package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.OuterMarketDomain;
import com.itheima.stock.pojo.domain.StockOuterMarketDomain;
import com.itheima.stock.pojo.entity.StockMarketIndexInfo;
import com.itheima.stock.pojo.entity.StockOuterMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Entity com.itheima.stock.pojo.entity.StockOuterMarketIndexInfo
 */
public interface StockOuterMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockOuterMarketIndexInfo record);

    int insertSelective(StockOuterMarketIndexInfo record);

    StockOuterMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockOuterMarketIndexInfo record);

    int updateByPrimaryKey(StockOuterMarketIndexInfo record);

    /**
     * 根据日期和大盘点数降序排序，取前4条外盘数据
     * @param outerCodes 外盘code集合
     * @return
     */
    List<StockOuterMarketDomain> getExternalIndex(@Param("codes") List<String> outerCodes);

    /**
     * 批量插入外盘数据
     * @param list
     * @return
     */
    int insertBatch(@Param("infos") List<StockMarketIndexInfo> list);
    List<OuterMarketDomain> getStockMarketInfo(@Param("date") Date date, @Param("outer") List<String> outer);

}




