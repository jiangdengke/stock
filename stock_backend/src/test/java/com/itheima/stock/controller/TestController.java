package com.itheima.stock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : itheima
 * @date : 2023/1/28 10:49
 * @description :
 */
@RestController
public class TestController {

    @GetMapping("/test/1")
    public Map map(){
        HashMap<String, Object> info = new HashMap<>();
        info.put("id",1234567890123456789l);
        info.put("idCard",new Long(1234567890123456789l));
        info.put("name","zhangsan");
        info.put("age",18);
        return info;
    }



    @GetMapping("/test/2")
    public SeUser user(){
        SeUser user = new SeUser();
        user.setId(new Long(1234567890123456789l));
        user.setIdCard(1234567890123456789l);
        user.setName("zhangsan");
        user.setAge(18);
        return user;
    }

    @GetMapping("/test/3")
    public List<Long> list(){
        List<Long> ids = new ArrayList<>();
        ids.add(1234567890123456789l);
        ids.add(1234567890123456789l);
        ids.add(1234567890123456789l);
        return ids;
    }

    @PostMapping("/test/4")
    public SeUser toUser(@RequestBody SeUser user){
        return user;
    }



    public static class SeUser{
        private Long id;
        private long idCard;
        private String name;
        private Integer age;

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public long getIdCard() {
            return idCard;
        }

        public void setIdCard(long idCard) {
            this.idCard = idCard;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }




}
