package com.itheima.stock.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author : itheima
 * @date : 2023/1/9 10:04
 * @description : 自定义redis序列化方式，避免使用默认的jdk序列化方式
 *      jdk序列化方式问题：
 *              1.阅读体验很差
 *              2.序列化后内容体积比较大，占用过多内存
 */
@Configuration
public class CacheConfig {


    /**
     * 自定义模板对象，要保证bean的名称必须叫redisTemplate，否则场景依赖会自动装配，导致相同
     * 类型的bean出现多个
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(@Autowired RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        //2.为不同的数据结构设置不同的序列化方案
        //设置key序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        //设置value序列化方式
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        //设置hash中field字段序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        //设置hash中value的序列化方式
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        //5.初始化参数设置
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 构建缓存bean
     * @return
     */
    @Bean
    public Cache<String,Object> caffeineCache(){
        Cache<String, Object> cache = Caffeine
                .newBuilder()
                .maximumSize(200)//设置缓存数量上限
//                .expireAfterAccess(1, TimeUnit.SECONDS)//访问1秒后删除
//                .expireAfterWrite(1,TimeUnit.SECONDS)//写入1秒后删除
                .initialCapacity(100)// 初始的缓存空间大小
                .recordStats()//开启统计
                .build();
        return cache;
    }



}
