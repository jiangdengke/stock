package com.itheima.stock.sharding;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : itheima
 * @date : 2023/2/7 11:01
 * @description  定义公共的分库算法类：覆盖个股 大盘 板块相关表
 */
public class CommonAlg4Db implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<Date> {

    /**
     * 精准查询
     * 分库策略：按照年分库
     * 精准查询时走该方法，cur_time条件必须是= 或者 in
     * eg:select * from stock_market_info where cur_time=xxxxx and code=xxx
     * @param dsNames ds-2021 ds-2022 ds-2023
     * @param shardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> dsNames, PreciseShardingValue<Date> shardingValue) {
        // 获取逻辑表
        String logicTableName = shardingValue.getLogicTableName();
        // 分片键 cur_time
        String columnName = shardingValue.getColumnName();
        // 获取等值查询的条件值
        Date curTime = shardingValue.getValue();
        //获取条件值对应的年份，然后从ds集合中过滤出以该年份结尾的数据源即可
        String year=new DateTime(curTime).getYear()+"";
        Optional<String> result = dsNames.stream().filter(dsName -> dsName.endsWith(year)).findFirst();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * 范围查询：eg:select * from stock_market_info where cur_time begween xxxxx and xxx and code=xxx
     * @param dsNames ds-2021 ds-2022 ds-2023
     * @param shardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> dsNames, RangeShardingValue<Date> shardingValue) {
        // 获取逻辑表
        String logicTableName = shardingValue.getLogicTableName();
        // 分片键 cur_time
        String columnName = shardingValue.getColumnName();
        //获取范围数据封装
        Range<Date> valueRange = shardingValue.getValueRange();
        //判断下限
        if (valueRange.hasLowerBound()) {
            //获取起始值 2022
            Date startTime = valueRange.lowerEndpoint();
            //获取条件所属年份
            int startYear = new DateTime(startTime).getYear();
            //过滤出数据源中年份大于等于startYear 2022数据源即可
            //ds-2021 ds-2022 ds-2023
            dsNames=dsNames.stream()
                    .filter(dsName->Integer.parseInt(dsName.substring(dsName.lastIndexOf("-")+1))>=startYear)
                    .collect(Collectors.toList());

        }
        //判断上限
        if (valueRange.hasUpperBound()) {
            //获取起始值 2022
            Date endTime = valueRange.upperEndpoint();
            //获取条件所属年份
            int endYear = new DateTime(endTime).getYear();
            //过滤出数据源中年份大于等于startYear 2022数据源即可
            //ds-2021 ds-2022 ds-2023
            dsNames=dsNames.stream().filter(dsName->Integer.parseInt(dsName.substring(dsName.lastIndexOf("-")+1))<=endYear).collect(Collectors.toList());
        }
        return dsNames;
    }
}
