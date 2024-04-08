package com.itheima.stock.service.impl;

import com.github.pagehelper.PageHelper;
import com.itheima.stock.mapper.StockBusinessMapperExt;
import com.itheima.stock.mapper.StockOuterMarketIndexInfoMapper;
import com.itheima.stock.mapper.StockRtInfoMapperExt;
import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.entity.StockBusiness;
import com.itheima.stock.pojo.entity.StockRtInfo;
import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.service.StockServiceExt;
import com.itheima.stock.log.utils.DateTimeUtil;
import com.itheima.stock.vo.resp.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by itheima
 * @Date 2022/5/30
 * @Description
 */
@Service("stockServiceExt")
@Slf4j
public class StockServiceExtImpl implements StockServiceExt {

    @Autowired
    private StockBusinessMapperExt stockBusinessMapper;

    @Autowired
    private StockInfoConfig stockInfoConfig;

    @Autowired
    private StockRtInfoMapperExt stockRtInfoMapper;

    @Autowired
    private StockOuterMarketIndexInfoMapper outerMarketIndexInfoMapper;

    /**
     * 查询最新的股票交易数据，且按照涨幅排序取前10
     * @return
     */
    @Override
    public R<List<StockUpdownDomain>> getStockIncreaseLimit() {
        //1.获取最近最新的一次股票有效交易时间点（精确分钟）
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //因为对于当前来说，我们没有实现股票信息实时采集的功能，所以最新时间点下的数据
        //在数据库中是没有的，所以，先临时指定一个假数据,后续注释掉该代码即可
        curDate=DateTime.parse("2022-01-05 09:47:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.查询数据
        PageHelper.startPage(1,4);
        List<StockUpdownDomain> infos=stockRtInfoMapper.getAllStockUpDownByTime(curDate);
        //List<StockUpdown4Excel> infos= stockRtInfoMapper.getStockIncreaseLimit(curDate,10);
        return R.ok(infos);
    }

    /**
     * 外盘指数
     * 外盘指数行情数据查询 根据时间和大盘点数取前4
     * @return
     */
    @Override
    public R<List<StockOuterMarketDomain>> getExternalIndex() {
        //获取指定外盘code集合
        List<String> outerCodes = stockInfoConfig.getOuter();
        //因为外盘数据一天仅仅采集单条数据，数据量非常优先，直接降序排序取前4即可
        List<StockOuterMarketDomain> infos=  outerMarketIndexInfoMapper.getExternalIndex(outerCodes);
        return R.ok(infos);
    }

    /**
     * 根据输入的个股代码，进行模糊查询，返回证券代码和证券名称
     * @param searchStr 个股代码模糊搜索
     * @return
     */
    @Override
    public R<List<Map>> searchStock(String searchStr) {
        if (StringUtils.isBlank(searchStr)) {
            return R.error("暂无数据");
        }
        List<Map> maps=this.stockBusinessMapper.searchStockLike(searchStr);
        if (CollectionUtils.isEmpty(maps)) {
            return R.error("暂无数据");
        }
        return R.ok(maps);
    }

    /**
     * 根据code查询个股主营业务接口查询
     * @param stockCode 股票编码
     * @return
     */
    @Override
    public R<StockBusinessDomain> getStockDescribe(String stockCode) {
        //根据股票code查询股票信息
        StockBusiness po=stockBusinessMapper.getInfoByCode(stockCode);
        //组装数据
        if (po==null) {
            return R.error("暂无数据");
        }
        StockBusinessDomain domain = StockBusinessDomain.builder().name(po.getStockName()).code(po.getStockCode())
                .trade(po.getBlockName()).business(po.getBusiness()).build();
        return R.ok(domain);
    }

    /**
     * 单个个股周K线数据：包含股票ID 最高价 最低价 开盘价 收盘价 均价
     * 具体：最高和最低是一周内的
     *      开盘与收盘分别对应周1的开盘价格和周五的收盘价格
     *      均价就是一周的平均价格
     *      日期：一周内的最大日期，一般是周五
     * @param stockCode 股票编码
     */
//    @Override
//    public R<List<Stock4EvrWeekDomain>> getWeekKLinData(String stockCode) {
//        //指定默认日期范围下的数据
//        DateTime curDateTime = DateTime.now();
//        //截止时间
//        Date endTime = curDateTime.toDate();
//        //开始时间
//        Date startTime = curDateTime.minusWeeks(10).toDate();
//        startTime=DateTime.parse("2022-01-01 09:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
//        //根据指定日期范围查询周K线数据
//        List<Stock4EvrWeekDomain> infos=stockRtInfoMapper.getWeekLineData(stockCode,startTime,endTime);
//        return R.ok(infos);
//    }
    @Override
    public R<List<Stock4EvrWeekDomain>> getWeekKLinData(String stockCode) {
        //指定默认日期范围下的数据
        DateTime curDateTime = DateTime.now();
        //截止时间
        Date endTime = curDateTime.toDate();
//        endTime=DateTime.parse("2022-05-22 09:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //开始时间
        Date startTime = curDateTime.minusWeeks(10).toDate();
//        startTime=DateTime.parse("2021-01-01 09:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //根据指定日期范围查询周K线数据
//        List<Stock4EvrWeekDomain> infos=stockRtInfoMapper.getWeekLineData(stockCode,startTime,endTime);
        List<Stock4EvrWeekDomain> infos=stockRtInfoMapper.getHalfWeekLineData(stockCode,startTime,endTime);
        //获取每周的开盘时间点和收盘时间点的集合
        //每周开盘时间点
        List<Date> everyOpenTime = infos.stream().map(info -> info.getMiTime()).collect(Collectors.toList());
        //每周收盘盘时间点 最大开盘时间点对应的股票信息就包含了开盘价和收盘价信息
        List<Date> everyCloseTime = infos.stream().map(info -> info.getMxTime()).collect(Collectors.toList());
//        Map<String,List<Date>> map=new HashMap<>();
//        map.put("closes",new ArrayList<>());
//        map.put("opens",new ArrayList<>());
//        infos.stream().forEach(info -> {
//            map.get("opens").add(info.getMiTime());
//            map.get("closes").add(info.getMxTime());
//        });
//        //分别查询指定股票在指定时间点下的数据集合
        List<BigDecimal> openPrices=stockRtInfoMapper.getStockInfoByCodeAndTimes(stockCode,everyOpenTime);
        List<BigDecimal> closePrices=stockRtInfoMapper.getStockInfoByCodeAndTimes(stockCode,everyCloseTime);
        //将获取的开盘价格和收盘价格合并到基础查询结果中
        for (int i = 0; i < infos.size(); i++) {
            infos.get(i).setOpenPrice(openPrices.get(i));
            infos.get(i).setClosePrice(closePrices.get(i));
        }
        return R.ok(infos);
    }





    /**
     * 获取个股最新分时行情数据，包含开盘价、前收盘价、最新价、最高价、最低价、成交金额和成交量信息
     * @param stockCode 股票编码
     * @return
     */
    @Override
    public R<StockLatestInfoDomain> getStockSecondDetail(String stockCode) {
        //获取最近交易时间点
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
//        curDate=DateTime.parse("2021-12-30 09:32:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //根据股票编码和时间点查询信息
        StockRtInfo sri=stockRtInfoMapper.getStockInfoByCodeAndTimePoint(stockCode,curDate);
        if (sri==null) {
            return R.error("暂无数据");
        }
        StockLatestInfoDomain info = StockLatestInfoDomain.builder().preClosePrice(sri.getPreClosePrice())
                .openPrice(sri.getOpenPrice()).tradePrice(sri.getCurPrice())
                .highPrice(sri.getMaxPrice()).lowPrice(sri.getMinPrice())
                .tradeAmt(sri.getTradeAmount()).tradeVol(sri.getTradeVolume())
                .curDate(sri.getCurTime()).build();
        return R.ok(info);
    }

    /**
     * 获取个股实时交易流水信息，按照时间降序排序，取前10
     * 单个个股秒级行情数据查询，查询当前分钟内的秒级数据（我们当前以分钟为单位获取数据）
     * @param code 股票编码
     * @return
     */
    @Override
    public R<List<StockShortInfoDomain>> getStockScreenSecond(String code) {
        //业务需求是按照时间降序排序，取前10，也就是举例当前有效交易日前推10分钟即可
        //1.1 获取最近有效的股票交易时间点
        DateTime lastDate4Stock = DateTimeUtil.getLastDate4Stock(DateTime.now());
        //1.2 时间点前推10分钟
        Date startTime = lastDate4Stock.minusMinutes(11).toDate();
        //todo mock data
//        startTime=DateTime.parse("2022-03-21 14:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.查询：我们只需查询cur_time大于startTime的股票信息即可
        List<StockShortInfoDomain> infos=stockRtInfoMapper.getStockInfoGt(code,startTime);
        //3.组装数据
        return R.ok(infos);
    }

}
