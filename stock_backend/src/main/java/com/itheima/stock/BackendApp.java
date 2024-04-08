package com.itheima.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itheima.stock.mapper")//扫描持久层mapper接口，生成代理对象，并维护到spring的IOC容器中
public class BackendApp {
    public static void main(String[] args) {
        SpringApplication.run(BackendApp.class, args);
    }
}
