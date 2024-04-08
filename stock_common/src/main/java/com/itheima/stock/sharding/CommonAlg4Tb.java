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
 * @description  定义公个股流水表的分表算法类：覆盖个股表
 */
public class CommonAlg4Tb implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<Date> {

    /**
     * 精准查询
     * 分库策略：按照月分表
     * 精准查询时走该方法，cur_time条件必须是= 或者 in
     * eg:select * from stock_market_info where cur_time=xxxxx and code=xxx
     * @param tbNames  eg:2023->202301..202312
     * @param shardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> tbNames, PreciseShardingValue<Date> shardingValue) {
        // 获取逻辑表
        String logicTableName = shardingValue.getLogicTableName();
        // 分片键 cur_time
        String columnName = shardingValue.getColumnName();
        // 获取等值查询的条件值
        Date curTime = shardingValue.getValue();
        //获取条件值对应的年月，然后从tb集合中过滤出以该年月结尾的表即可
        String yearMonth = new DateTime(curTime).toString("yyyyMM");
        Optional<String> result = tbNames.stream().filter(tbName -> tbName.endsWith(yearMonth)).findFirst();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * 范围查询：eg:select * from stock_market_info where cur_time begween xxxxx and xxx and code=xxx
     * @param tbNames eg:2023->202301..202312
     * @param shardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> tbNames, RangeShardingValue<Date> shardingValue) {
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
            int startYearMonth = Integer.parseInt(new DateTime(startTime).toString("yyyyMM"));
            //过滤出数据源中年份大于等于startYear 2022数据源即可
            //ds-2021 ds-2022 ds-2023
            tbNames=tbNames.stream()
                    .filter(tbName->Integer.parseInt(tbName.substring(tbName.lastIndexOf("_")+1))>=startYearMonth)
                    .collect(Collectors.toList());

        }
        //判断上限
        if (valueRange.hasUpperBound()) {
            //获取起始值 2022
            Date endTime = valueRange.upperEndpoint();
            //获取条件所属年份
            int endYearMonth = Integer.parseInt(new DateTime(endTime).toString("yyyyMM"));
            //过滤出数据源中年份大于等于startYear 2022数据源即可
            //ds-2021 ds-2022 ds-2023
            tbNames=tbNames.stream().filter(dsName->Integer.parseInt(dsName.substring(dsName.lastIndexOf("_")+1))<=endYearMonth).collect(Collectors.toList());
        }
        return tbNames;
    }
}
