//package com.itheima.stock;
//
//import com.itheima.stock.pojo.vo.StockInfoConfig;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
///**
// * @author : itheima
// * @date : 2023/1/8 17:49
// * @description :
// */
//@SpringBootTest
//public class TestPasswordEncoder {
//
//    @Autowired
//    private StockInfoConfig stockInfoConfig;
//
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    /**
//     * @desc 测试密码加密
//     */
//    @Test
//    public void test01() {
//        String pwd="123456";
//        String encodePwd = passwordEncoder.encode(pwd);
//        //$2a$10$sZ5Dwuu0ZUDakUTdX23f1eDqJQXBKi/jkyTZtUn55u5XlmUvj65C.
//        System.out.println(encodePwd);
//    }
//
//    /**
//     * @desc 测试密码匹配
//     */
//    @Test
//    public void test02() {
//        String pwd="123456";
//        String  enPwd="$2a$10$sZ5Dwuu0ZUDakUTd623f1eDqJQXBKi/jkyTZtUn55u5XlmUvj65C.";
//        boolean isSucess = passwordEncoder.matches(pwd, enPwd);
//        System.out.println(isSucess?"密码匹配成功":"密码匹配失败");
//    }
//
//
//}
