package com.itheima.stock.service.impl;

import com.google.common.collect.Lists;
import com.itheima.stock.mapper.StockBlockRtInfoMapper;
import com.itheima.stock.mapper.StockBusinessMapper;
import com.itheima.stock.mapper.StockMarketIndexInfoMapper;
import com.itheima.stock.mapper.StockRtInfoMapper;
import com.itheima.stock.pojo.entity.StockBlockRtInfo;
import com.itheima.stock.pojo.entity.StockMarketIndexInfo;
import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.service.StockTimerTaskService;
import com.itheima.stock.utils.DateTimeUtil;
import com.itheima.stock.utils.IdWorker;
import com.itheima.stock.utils.ParseType;
import com.itheima.stock.utils.ParserStockInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.core.util.CollectionUtil;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockTimerTaskServiceImpl implements StockTimerTaskService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;
    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private HttpEntity<Object> httpEntity;
    @Override
    public void getInnerMarketInfo() {
        //1.采集原始数据
        String url = stockInfoConfig.getMarketUrl()+String.join(",",stockInfoConfig.getInner());//通过String.join将提取到的字符集合进行拼接
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Referer","http://finance.sina.com.cn/stock/");
//        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0");
//        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        if (responseEntity.getStatusCodeValue()!=200) {//请求出错
            log.error("当前时间点：{}，采集数据失败，http状态码：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),responseEntity.getStatusCodeValue());
            //todo 可以在数据采集失败的时候，对人进行提醒，可通过短信，企业微信等
        }
        String jsData = responseEntity.getBody();
        log.info("当前时间点：{}，采集原始数据内容：{}",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),jsData);
        //2.使用正则对原始数据进行解析
      //  String reg = "var hq_str_(.+)=\"(.+)\";";

        List list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.INNER);
//        Pattern pattern = Pattern.compile(reg);
//        Matcher matcher = pattern.matcher(jsData);
//        ArrayList<StockMarketIndexInfo> list = new ArrayList<>();
/*        while (matcher.find()){
            //获取大盘的code
            String marketCode = matcher.group(1);
            //获取其它信息，字符串以逗号间隔
            String otherInfo=matcher.group(2);
            //以逗号切割字符串，形成数组
            String[] splitArr = otherInfo.split(",");
            //大盘名称
            String marketName=splitArr[0];
            //获取当前大盘的开盘点数
            BigDecimal openPoint=new BigDecimal(splitArr[1]);
            //前收盘点
            BigDecimal preClosePoint=new BigDecimal(splitArr[2]);
            //获取大盘的当前点数
            BigDecimal curPoint=new BigDecimal(splitArr[3]);
            //获取大盘最高点
            BigDecimal maxPoint=new BigDecimal(splitArr[4]);
            //获取大盘的最低点
            BigDecimal minPoint=new BigDecimal(splitArr[5]);
            //获取成交量
            Long tradeAmt=Long.valueOf(splitArr[8]);
            //获取成交金额
            BigDecimal tradeVol=new BigDecimal(splitArr[9]);
            //时间
            Date curTime = DateTimeUtil.getDateTimeWithoutSecond(splitArr[30] + " " + splitArr[31]).toDate();
            //组装entity对象
            StockMarketIndexInfo info = StockMarketIndexInfo.builder()
                    .id(idWorker.nextId())
                    .marketCode(marketCode)
                    .marketName(marketName)
                    .curPoint(curPoint)
                    .openPoint(openPoint)
                    .preClosePoint(preClosePoint)
                    .maxPoint(maxPoint)
                    .minPoint(minPoint)
                    .tradeVolume(tradeVol)
                    .tradeAmount(tradeAmt)
                    .curTime(curTime)
                    .build();
            //收集封装的对象，方便批量插入
            list.add(info);
        }*/
        log.info("采集的当前大盘数据：{}",list);
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        //4. 调用mybatis批量入库
        int count = stockMarketIndexInfoMapper.insertBatch(list);
        if(count>0){
            //新数据插入成功后，通知backend端刷新mysql数据，同时刷新本地缓存数据
            rabbitTemplate.convertAndSend("stockExchange","inner.market",new Date());
            log.info("当前时间点：{},插入数据：{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
        }else {
            log.error("当前时间点：{},插入数据：{}失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
        }
    }
    /**
     * 获取国内个股每分钟数据
     */
    @Override
    public void getStockRtInfo() {
        //先从数据库中获取本数据库中的股票code
        List<String> listCode = stockBusinessMapper.getAllStockCodes();
        //为了减轻网络io和磁盘io，故将个股数据分批次进行获取和插入数据库
        listCode = listCode.stream().map(code->code.startsWith("6")?"sh"+code:"sz"+code).collect(Collectors.toList());
        //将集合拆分
        List<List<String>> partition = Lists.partition(listCode, 15);
        long startTime = System.currentTimeMillis();

       partition.forEach(codes->{
           /**
            * 多线程方案，线程会不断的创建和销毁，不能得到复用，影响性能
            */
           //这种方式每次来任务就会创建一个线程，复用性差。2.如果多线程使用不当，造成cpu竞争激烈，会导致程序的性能降低
/*           new Thread(()->{
               String url = stockInfoConfig.getMarketUrl()+String.join(",",codes);
               ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
               if (responseEntity.getStatusCodeValue()!=200) {//请求出错
                   log.error("当前时间点：{}，采集数据失败，http状态码：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),responseEntity.getStatusCodeValue());
                   //todo 可以在数据采集失败的时候，对人进行提醒，可通过短信，企业微信等
               }
               String jsData = responseEntity.getBody();
               log.info("当前时间点：{}，采集原始数据内容：{}",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),jsData);
               List list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
//            log.info("采集的当前大盘数据：{}",list);
               if (CollectionUtils.isEmpty(list)){
                   return;
               }
               //4. 调用mybatis批量入库
               int count = stockRtInfoMapper.insertBatch(list);
               if(count>0){
                   log.info("当前时间点：{},插入数据：{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
               }else {
                   log.error("当前时间点：{},插入数据：{}失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
               }
           });*/
           /**
            * 原始方案直接遍历发请求
            */
/*            String url = stockInfoConfig.getMarketUrl()+String.join(",",codes);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue()!=200) {//请求出错
                log.error("当前时间点：{}，采集数据失败，http状态码：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),responseEntity.getStatusCodeValue());
                //todo 可以在数据采集失败的时候，对人进行提醒，可通过短信，企业微信等
            }
            String jsData = responseEntity.getBody();
            log.info("当前时间点：{}，采集原始数据内容：{}",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),jsData);
            List list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
//            log.info("采集的当前大盘数据：{}",list);
            if (CollectionUtils.isEmpty(list)){
                return;
            }
            //4. 调用mybatis批量入库
            int count = stockRtInfoMapper.insertBatch(list);
            if(count>0){
                log.info("当前时间点：{},插入数据：{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
            }else {
                log.error("当前时间点：{},插入数据：{}失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
            }*/
           /**
            * 线程池方案
            */
           threadPoolTaskExecutor.execute(()-> {
               String url = stockInfoConfig.getMarketUrl()+String.join(",",codes);
               ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
               if (responseEntity.getStatusCodeValue()!=200) {//请求出错
                   log.error("当前时间点：{}，采集数据失败，http状态码：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),responseEntity.getStatusCodeValue());
                   //todo 可以在数据采集失败的时候，对人进行提醒，可通过短信，企业微信等
               }
               String jsData = responseEntity.getBody();
               log.info("当前时间点：{}，采集原始数据内容：{}",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),jsData);
               List list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
               if (CollectionUtils.isEmpty(list)){
                   return;
               }
               //4. 调用mybatis批量入库
               int count = stockRtInfoMapper.insertBatch(list);
               if(count>0){
                   log.info("当前时间点：{},插入数据：{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
               }else {
                   log.error("当前时间点：{},插入数据：{}失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
               }
           });
        });
        System.out.println(System.currentTimeMillis()-startTime+"ms");
    }

    /**
     * 获取国内板块数据
     */
    @Override
    public void getBlockRtInfo() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(stockInfoConfig.getBlockUrl(), String.class);
        String jsData = forEntity.getBody();
        List<StockBlockRtInfo> stockBlockRtInfos = parserStockInfoUtil.parse4StockBlock(jsData);
        if(CollectionUtils.isEmpty(stockBlockRtInfos)){
            return;
        }
        //存入数据库
        int count = stockBlockRtInfoMapper.insertBatch(stockBlockRtInfos);
        if(count>0){
            log.info("当前时间点：{},插入数据：{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),stockBlockRtInfos);
        }else {
            log.error("当前时间点：{},插入数据：{}失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),stockBlockRtInfos);
        }
    }

    @PostConstruct//bean初始化时运行
    public void init(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Referer","http://finance.sina.com.cn/stock/");
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0");
        httpEntity = new HttpEntity<>(headers);
    }
}
