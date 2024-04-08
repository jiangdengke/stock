package com.itheima.stock.service.impl;

import com.alibaba.excel.EasyExcel;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.stock.mapper.*;
import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.service.StockService;
import com.itheima.stock.utils.DateTimeUtil;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;
    @Autowired
    private Cache<String, Object> caffeineCache;
    @Autowired
    private StockOuterMarketIndexInfoMapper stockOuterMarketIndexInfoMapper;
    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    /**
     * 获取国内大盘信息
     *
     * @return
     */
    @Override
    public R<List<InnerMarketDomain>> getStockMarketInfo() {
        //第一次访问肯定没有"innerMartetInfo"的值，因此触发回调函数，从数据库中拿数据到缓存中
        //那如何实现刷新缓存呢，当rabbitmq来消息时，清空本地缓存，并从数据库中获得缓存，这样当请求到这里的时候，新数据就已经在本地缓存中了
        R<List<InnerMarketDomain>> result = (R<List<InnerMarketDomain>>) caffeineCache.get("innerMartetInfo", key ->
        {
            //获取国内大盘的编码
            List<String> inner = stockInfoConfig.getInner();
            //获取当前时间
            DateTime dateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
            Date date = dateTime.toDate();
//            date = DateTime.parse("2022-07-07 14:52:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

            List<InnerMarketDomain> innerMarketDomainList = stockMarketIndexInfoMapper.getMarketInfo(date, inner);
            return R.ok(innerMarketDomainList);
        });
        return result;
    }

    /**
     * 需求说明: 沪深两市板块分时行情数据查询，以交易时间和交易总金额降序查询，取前10条数据
     *
     * @return
     */
    @Override
    public R<List<StockBlockDomain>> sectorAllLimit() {

        //获取股票最新交易时间点
        Date lastDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //TODO mock数据,后续删除
       // lastDate = DateTime.parse("2021-12-21 14:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.调用mapper接口获取数据
        List<StockBlockDomain> infos = stockBlockRtInfoMapper.sectorAllLimit(lastDate);
        //2.组装数据
        if (CollectionUtils.isEmpty(infos)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA.getMessage());
        }
        return R.ok(infos);
    }

    @Override
    public R<PageResult> getStockPageInfo(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        //2.获取当前最新的股票交易时间点
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //todo
      //  curDate = DateTime.parse("2022-01-05 14:58:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //3.调用mapper接口查询
        Page<StockUpdownDomain> pageInfo = stockRtInfoMapper.getNewestStockInfo(curDate);//拿到page对象
        List<StockUpdownDomain> result = pageInfo.getResult();
        long totalRows = pageInfo.getTotal();
        int totlePages = pageInfo.getPages();
        PageResult<StockUpdownDomain> updownDomainPageResult = new PageResult<>();
        BeanUtils.copyProperties(pageInfo, updownDomainPageResult);
        updownDomainPageResult.setRows(result);
        updownDomainPageResult.setTotalRows(totalRows);
        updownDomainPageResult.setTotalPages(totlePages);

        return R.ok(updownDomainPageResult);
    }

    /**
     * 统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据
     */
    @Override
    public R<List<StockUpdownDomain>> getStockIncrease() {
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //todo
        //curDate = DateTime.parse("2022-01-05 14:58:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        List<StockUpdownDomain> updownDomainList = stockRtInfoMapper.getStockIncrease(curDate);
        return R.ok(updownDomainList);
    }

    /**
     * 统计最新交易日下股票每分钟涨跌停的数量
     *
     * @return
     */
    @Override
    public R<Map<String, List>> getStockUpdownCount() {
        //1.获取最新的交易时间范围 openTime  curTime
        //1.1 获取最新股票交易时间点
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curTime = curDateTime.toDate();
        //TODO
      //  curTime = DateTime.parse("2021-12-30 14:25:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.2 获取最新交易时间对应的开盘时间
        DateTime openDate = DateTimeUtil.getOpenDate(curDateTime);
        Date openTime = openDate.toDate();
        //TODO
       // openTime = DateTime.parse("2021-12-30 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.查询涨停数据
        //约定mapper中flag入参： 1-》涨停数据 0：跌停
        List<StockCountTime> upCounts = stockRtInfoMapper.getStockUpdownCount(openTime, curTime, 1);
        //3.查询跌停数据
        List<StockCountTime> dwCounts = stockRtInfoMapper.getStockUpdownCount(openTime, curTime, 0);
        //4.组装数据
        HashMap<String, List> mapInfo = new HashMap<>();
        mapInfo.put("upList", upCounts);
        mapInfo.put("downList", dwCounts);
        //5.返回结果
        return R.ok(mapInfo);
    }

    /**
     * 导出涨幅榜当页数据
     */
    @Override
    public void exportStockUpDown(Integer page, Integer pageSize, HttpServletResponse response) {
        //拿到当页信息。
        R<PageResult> stockPageInfo = this.getStockPageInfo(page, pageSize);
        PageResult data = stockPageInfo.getData();
        List rows = data.getRows();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        try {
            String fileName = URLEncoder.encode("股票涨幅信息统计表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), StockUpdownDomain.class).sheet("股票信息涨幅").doWrite(rows);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("当前导出数据异常，当前页：{},每页大小：{},异常信息：{}", page, pageSize, e.getMessage());
        }
    }

    /**
     * 功能描述：统计国内A股大盘T日和T-1日成交量对比功能（成交量为沪市和深市成交量之和）
     */
    @Override
    public R<Map<String, List>> stockTradeInnerMarketComparison() {
        //1.获取T日和T-1日的开始时间和结束时间
        //1.1 获取最近股票有效交易时间点--T日时间范围
        DateTime lastDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        DateTime openDateTime = DateTimeUtil.getOpenDate(lastDateTime);
        //转化成java中Date,这样jdbc默认识别
        Date startTime4T = openDateTime.toDate();
        Date endTime4T = lastDateTime.toDate();
        //TODO  mock数据
        //startTime4T = DateTime.parse("2022-01-03 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //endTime4T = DateTime.parse("2022-01-03 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //1.2 获取T-1日的区间范围
        //获取lastDateTime的上一个股票有效交易日
        DateTime preLastDateTime = DateTimeUtil.getPreviousTradingDay(lastDateTime);
        DateTime preOpenDateTime = DateTimeUtil.getOpenDate(preLastDateTime);
        //转化成java中Date,这样jdbc默认识别
        Date startTime4PreT = preOpenDateTime.toDate();
        Date endTime4PreT = preLastDateTime.toDate();
        //TODO  mock数据
        //startTime4PreT = DateTime.parse("2022-01-02 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //endTime4PreT = DateTime.parse("2022-01-02 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.1 获取大盘的id集合
        List<String> markedIds = stockInfoConfig.getInner();
        //获取当日
        List<StockCountTime> todayCountTimeList = stockMarketIndexInfoMapper.stockTradeInnerMarketComparison(markedIds, startTime4T, endTime4T);
        List<StockCountTime> yesterdayCountTimeList = stockMarketIndexInfoMapper.stockTradeInnerMarketComparison(markedIds, startTime4PreT, endTime4PreT);

        //获取前一天
        HashMap map = new HashMap();
        map.put("amtList", todayCountTimeList);
        map.put("yesAmtList", yesterdayCountTimeList);
        return R.ok(map);
    }

    /**
     * 统计当前时间下（精确到分钟），A股在各个涨跌区间股票的数量；区间：corridor
     */
    @Override
    public R<Map<String, Object>> getCountStockInCorridor() {
        //获取当前最近的交易时间点
        Date date = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
       // date = DateTime.parse("2021-12-30 09:32:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        List<Map> list = stockRtInfoMapper.getCountStockInCorridor(date);
        HashMap<String, Object> map = new HashMap<>();
        //获取指定日期格式的字符串
        String curDateStr = new DateTime(date).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        map.put("time", curDateStr);
        map.put("infos", list);
        return R.ok(map);
    }

    /**
     * 查询个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据
     */
    @Override
    public R<List<Stock4MinuteDomain>> getStockTradeMinute(String code) {
        //获取当前最近的交易时间点和对应的开盘时间点
        //1.获取最新的交易时间范围 openTime  curTime
        //1.1 获取最新股票交易时间点
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curTime = curDateTime.toDate();
        //TODO
        //curTime = DateTime.parse("2021-12-30 14:25:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.2 获取最新交易时间对应的开盘时间
        DateTime openDate = DateTimeUtil.getOpenDate(curDateTime);
        Date openTime = openDate.toDate();
        //TODO
       // openTime = DateTime.parse("2021-12-30 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        List<Stock4MinuteDomain> list = stockRtInfoMapper.getStockTradeMinute(code, openTime, curTime);
        return R.ok(list);
    }

    /**
     * 功能描述：单个个股日K数据查询 ，可以根据时间区间查询数日的K线数据
     * 默认查询历史20天的数据；
     *
     * @param code 股票编码
     * @return
     */
    @Override
    public R<List<Stock4EvrDayDomain>> stockCreenDkLine(String code) {
        //1.获取查询的日期范围
        //1.1 获取截止时间
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());

        Date endTime = endDateTime.toDate();
        System.out.println(endTime);
        //TODO MOCKDATA
       // endTime = DateTime.parse("2022-01-07 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.2 获取开始时间
        DateTime startDateTime = endDateTime.minusDays(10);
        Date startTime = startDateTime.toDate();
        //TODO MOCKDATA
       // startTime = DateTime.parse("2022-01-01 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.调用mapper接口获取查询的集合信息-方案1
//        List<Stock4EvrDayDomain> data= stockRtInfoMapper.getStockInfo4EvrDay(code,startTime,endTime);
        List<Date> dateList = stockRtInfoMapper.getCurrentTimeList(code, startTime, endTime);
        List<Stock4EvrDayDomain> data = stockRtInfoMapper.getStockInfo4EvrDay(dateList, code);
        //3.组装数据，响应
        return R.ok(data);
    }

    @Override
    public R<List<OuterMarketDomain>> getOuterMarketInfo() {
        R<List<OuterMarketDomain>> result = (R<List<OuterMarketDomain>>) caffeineCache.get("outerMartetInfo", key ->
        {
            //获取国内大盘的编码
            List<String> inner = stockInfoConfig.getOuter();
            //获取当前时间
            DateTime dateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
            Date date = dateTime.toDate();
           // date = DateTime.parse("2022-07-07 14:52:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

            List<OuterMarketDomain> innerMarketDomainList = stockOuterMarketIndexInfoMapper.getStockMarketInfo(date, inner);
            return R.ok(innerMarketDomainList);
        });
        return result;
    }

    /**
     * 获取股票联想信息
     *
     * @return
     */
    @Override
    public R<List<Map>> getStockSearch(String searchStr) {
        List<Map> mapList = stockBusinessMapper.getStockSearch(searchStr);
        return R.ok(mapList);
    }

    /**
     * 个股主营业务查询接口
     *
     * @param code
     * @return
     */
    @Override
    public R<StockBusinessDomain> getStockDescribe(String code) {
        StockBusinessDomain stockBussinessDomain = stockBusinessMapper.getStockDescribe(code);
        return R.ok(stockBussinessDomain);
    }

    @Override
    public R<Stock4HourDomain> getSecondDetail(String code) {
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date endTime = endDateTime.toDate();
        //TODO MOCKDATA
       // endTime=DateTime.parse("2021-12-19 09:59:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        Stock4HourDomain stock4HourDomain = stockRtInfoMapper.getSecondDetail(endTime,code);
        return R.ok(stock4HourDomain);
    }

    @Override
    public R<List<StockRtLimit10>> getScreenSecond(String code) {
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date endTime = endDateTime.toDate();
        //TODO MOCKDATA
       // endTime=DateTime.parse("2021-12-19 09:59:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        List<StockRtLimit10> stockRtLimit10s = stockRtInfoMapper.getScreenSecond(endTime,code);
        return R.ok(stockRtLimit10s);
    }
    /**
     * 个股周k数据查询。根据时间区间查询数周的K线数据
     */
/*    @Override
    public R<Stock4WeekDomain> getWeekLine(String code) {
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        int dayOfWeek = endDateTime.getDayOfWeek();//获取当前日期是周几
        DateTime firstDay = endDateTime.minusDays(dayOfWeek-1);
        Date openDate = DateTimeUtil.getOpenDate(firstDay).toDate();  //获取开盘点
        Date endTime = endDateTime.toDate();
        Date closeDate = endDateTime.toDate();
        //TODO MOCKDATA
        endTime=DateTime.parse("2022-01-07 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.2 获取开始时间
        DateTime startDateTime = endDateTime.minusDays(10);
        Date startTime = startDateTime.toDate();
        //TODO MOCKDATA
        startTime=DateTime.parse("2021-01-01 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        List<Timestamp> timestamps =stockRtInfoMapper.getWeekData(code,openDate,closeDate);
        Stock4WeekDomain stock4WeekDomain = stockRtInfoMapper.getStockInfo4Week(code,openDate,closeDate);
        return null;
    }*/
}

