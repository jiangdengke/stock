package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.StockBlockDomain;
import com.itheima.stock.pojo.entity.StockBlockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Entity com.itheima.stock.pojo.entity.StockBlockRtInfo
 */
public interface StockBlockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBlockRtInfo record);

    int insertSelective(StockBlockRtInfo record);

    StockBlockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBlockRtInfo record);

    int updateByPrimaryKey(StockBlockRtInfo record);

    /**
     * 查询指定时间点下，根据交易金额降序排序取指定数量的板块数据
     * @return
     */
    List<StockBlockDomain> sectorAllLimit(Date lastDate);
    List<StockBlockDomain> getBlockInfoLimit(@Param("timePoint") Date curDate, @Param("number") Integer number);
    int insertBatch(@Param("infos") List<StockBlockRtInfo> stockBlockRtInfos);
}




