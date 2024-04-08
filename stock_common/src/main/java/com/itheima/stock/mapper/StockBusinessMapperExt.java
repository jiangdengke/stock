package com.itheima.stock.mapper;

import com.itheima.stock.pojo.entity.StockBusiness;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.itheima.stock.pojo.entity.StockBusiness
 */
public interface StockBusinessMapperExt {

    int deleteByPrimaryKey(String id);

    int insert(StockBusiness record);

    int insertSelective(StockBusiness record);

    StockBusiness selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockBusiness record);

    int updateByPrimaryKey(StockBusiness record);

    /**
     * 查询股票主营业务数据
     * @return
     */
    List<StockBusiness> getAll();

    /**
     * 查询所有A股股票编码集合
     * @return
     */
    List<String> getAllStockCodes();

    /**
     * 根据输入的个股代码，进行模糊查询，返回证券代码和证券名称
     * @param searchStr 个股代码模糊搜索
     * @return
     */
    List<Map> searchStockLike(@Param("codeStr") String searchStr);

    /**
     * 根据股票编码获取经营信息
     * @param stockCode
     * @return
     */
    StockBusiness getInfoByCode(@Param("code") String stockCode);


}




