//package com.itheima.stock.controller;
//
//import com.itheima.stock.pojo.domain.*;
//import com.itheima.stock.service.StockServiceExt;
//import com.itheima.stock.vo.resp.R;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author by itheima
// * @Date 2022/5/30
// * @Description 定义股票web接口
// */
//@RestController
//@RequestMapping("/api/quot")
//public class StockControllerExt {
//
//    @Autowired
//    private StockServiceExt stockService;
//
//
//    /**
//     * 查询最新的股票交易数据，且按照涨幅排序取前10
//     * @return
//     */
//    @GetMapping("/stock/increase")
//    public R<List<StockUpdownDomain>> getStockIncreaseLimit(){
//        return stockService.getStockIncreaseLimit();
//    }
//
//    /**
//     * 外盘指数
//     * 外盘指数行情数据查询 根据时间取前4
//     * @return
//     */
//    @GetMapping("/external/index")
//    public R<List<StockOuterMarketDomain>> getExternalIndex(){
//        return stockService.getExternalIndex();
//    }
//
//    /**
//     * 根据输入的个股代码，进行模糊查询，返回证券代码和证券名称
//     * @param searchStr 个股代码模糊搜索
//     * @return
//     */
//    @GetMapping("/stock/search")
//    public R<List<Map>> searchStock(String searchStr){
//        return stockService.searchStock(searchStr);
//    }
//
//    /**
//     * 个股主营业务接口查询
//     * @param stockCode
//     * @return
//     */
//    @RequestMapping("/stock/describe")
//    public R<StockBusinessDomain> getStockDescribe(@RequestParam("code") String stockCode){
//        //根据股票编号获取所对公司的详情信息
//        return stockService.getStockDescribe(stockCode);
//    }
//
//    /**
//     * 单个个股周K线数据：包含股票ID 最高价 最低价 开盘价 收盘价 均价
//     * 具体：最高和最低是一周内的
//     *      开盘与收盘分别对应周1的开盘价格和周五的收盘价格
//     *      均价就是一周的平均价格
//     *      日期：一周内的最大日期，一般是周五
//     * @param stockCode 股票编码
//     */
//    @RequestMapping("/stock/screen/weekkline")
//    public R<List<Stock4EvrWeekDomain>> getWeekKLinData(@RequestParam("code") String stockCode){
//        return stockService.getWeekKLinData(stockCode);
//    }
//
//    /**
//     * 获取个股最新分时行情数据，包含开盘价、前收盘价、最新价、最高价、最低价、成交金额和成交量信息
//     * @param stockCode 股票编码
//     * @return
//     */
//    @RequestMapping("/stock/screen/second/detail")
//    public R<StockLatestInfoDomain> getStockSecondDetail(@RequestParam("code") String stockCode){
//        return stockService.getStockSecondDetail(stockCode);
//    }
//
//    /**
//     * 获取个股实时交易流水信息，按照时间降序排序，取前10
//     * 单个个股秒级行情数据查询，查询当前分钟内的秒级数据（我们当前以分钟为单位获取数据）
//     * @param code 股票编码
//     * @return
//     */
//    @GetMapping("/stock/screen/second")
//    public R<List<StockShortInfoDomain>> getStockScreenSecond(String code){
//        return stockService.getStockScreenSecond(code);
//    }
//}
