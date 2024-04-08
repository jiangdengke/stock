package com.itheima.stock.service;

import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.R;

import java.util.Map;

/**
 * @author : itheima
 * @date : 2023/1/8 16:07
 * @description : 定义用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名称查询用户信息
     * @param userName
     * @return
     */
    SysUser findByUserName(String userName);


    /**
     * 用户登录功能
     * @param vo
     * @return
     */
    R<LoginRespVo> login(LoginReqVo vo);

    /**
     * 生成图片验证码功能
     * @return
     */
    R<Map> getCaptchaCode();
}
