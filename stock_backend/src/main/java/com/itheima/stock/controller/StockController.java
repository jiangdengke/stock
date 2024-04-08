package com.itheima.stock.controller;

import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.service.StockService;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api("/api/quot")
@RestController
@RequestMapping("/api/quot")
public class StockController {
    @Autowired
    private StockService stockService;

    /**
     * 获取国内大盘最新的数据.前端两分钟访问一次这个接口
     * @return
     */
    @ApiOperation(value = "获取国内大盘最新的数据", notes = "获取国内大盘最新的数据", httpMethod = "GET")
    @GetMapping("/index/all")
    public R<List<InnerMarketDomain>> getStockMarketInfo(){
        return stockService.getStockMarketInfo();
    }
    /**
     *需求说明: 获取沪深两市板块最新数据，以交易总金额降序查询，取前10条数据
     * @return
     */
    @ApiOperation(value = "需求说明: 获取沪深两市板块最新数据，以交易总金额降序查询，取前10条数据", notes = "需求说明: 获取沪深两市板块最新数据，以交易总金额降序查询，取前10条数据", httpMethod = "GET")
    @GetMapping("/sector/all")
    public R<List<StockBlockDomain>> sectorAll(){
        return stockService.sectorAllLimit();
    }
    /**
     * 分页查询股票最新数据，并按照涨幅排序查询
     * @param page
     * @param pageSize
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = ""),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "")
    })
    @ApiOperation(value = "分页查询股票最新数据，并按照涨幅排序查询", notes = "分页查询股票最新数据，并按照涨幅排序查询", httpMethod = "GET")
    @GetMapping("/stock/all")
    public R<PageResult> getStockPageInfo(@RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
                                          @RequestParam(name = "pageSize",required = false,defaultValue = "20") Integer pageSize){
        return stockService.getStockPageInfo(page,pageSize);
    }
    /**
     * 统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据
     */
    @ApiOperation(value = "统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据", notes = "统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据", httpMethod = "GET")
    @GetMapping("stock/increase")
    public R<List<StockUpdownDomain>> getStockIncrease(){
        return stockService.getStockIncrease();
    }
    /**
     * 统计最新交易日下股票每分钟涨跌停的数量
     * @return
     */
    @ApiOperation(value = "统计最新交易日下股票每分钟涨跌停的数量", notes = "统计最新交易日下股票每分钟涨跌停的数量", httpMethod = "GET")
    @GetMapping("/stock/updown/count")
    public R<Map<String,List>> getStockUpdownCount(){
        return stockService.getStockUpdownCount();
    }
    /**
     * 导出涨幅榜当页数据
     */
    @GetMapping("/stock/export")
    public void exportStockUpDown(@RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
                                  @RequestParam(name = "pageSize",required = false,defaultValue = "20") Integer pageSize, HttpServletResponse response
                                  ){
        stockService.exportStockUpDown(page,pageSize,response);
    }
    /**
     * 功能描述：统计国内A股大盘T日和T-1日成交量对比功能（成交量为沪市和深市成交量之和）
     */
    @GetMapping("/stock/tradeAmt")
    public R<Map<String,List>> stockTradeInnerMarketComparison(){
        return stockService.stockTradeInnerMarketComparison();
    }
    /**
     * 统计当前时间下（精确到分钟），A股在各个涨跌区间股票的数量；区间：corridor
     */
    @GetMapping("/stock/updown")
    public R<Map<String,Object>> getCountStockInCorridor(){
        return stockService.getCountStockInCorridor();
    }
    /**
     * 查询个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据
     */
    @GetMapping("/stock/screen/time-sharing")
    public R<List<Stock4MinuteDomain>> getStockTradeMinute(@RequestParam("code") String code){
        return stockService.getStockTradeMinute(code);
    }
    /**
     * 单个个股日K 数据查询 ，可以根据时间区间查询数日的K线数据
     * @param stockCode 股票编码
     */
    @RequestMapping("/stock/screen/dkline")
    public R<List<Stock4EvrDayDomain>> getDayKLinData(@RequestParam("code") String stockCode){
        return stockService.stockCreenDkLine(stockCode);
    }
//    /**
//     * 个股周k数据查询。根据时间区间查询数周的K线数据
//     */
//    @GetMapping("/stock/screen/weekkline")
//    public R<Stock4WeekDomain> getWeekLine(@RequestParam("code") String code){
//        return stockService.getWeekLine(code);
//    }
    /**
     * 外盘指数
     */
    @GetMapping("external/index")
    public R<List<OuterMarketDomain>> getOuterMarketInfo(){
        return stockService.getOuterMarketInfo();
    }

    /**
     * 个股主营业务查询接口
     */
    @GetMapping("/stock/describe")
    public R<StockBusinessDomain> getStockDescribe(@RequestParam("code") String code){
        return stockService.getStockDescribe(code);
    }
    /**
     * 股票编码模糊查询
     */
    @GetMapping("/stock/search")
    public R<List<Map>> getStockSearch(@RequestParam("searchStr") String searchStr){
        return stockService.getStockSearch(searchStr);
    }
    /**
     * 获取个股最新分时行情数据，主要包含：
     * 	开盘价、前收盘价、最新价、最高价、最低价、成交金额和成交量、交易时间信息;
     */
    @GetMapping("/stock/screen/second/detail")
    public R<Stock4HourDomain> getSecondDetail(@RequestParam("code") String code){
        return stockService.getSecondDetail(code);
    }
    /**
     * 功能描述：个股交易流水行情数据查询--查询最新交易流水，按照交易时间降序取前10
     */
    @GetMapping("/stock/screen/second")
    public R<List<StockRtLimit10>> getScreenSecond(@RequestParam("code")String code){
        return stockService.getScreenSecond(code);
    }
}
