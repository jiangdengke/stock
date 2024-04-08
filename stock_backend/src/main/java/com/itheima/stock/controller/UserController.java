package com.itheima.stock.controller;

import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.service.UserService;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author : itheima
 * @date : 2023/1/8 16:11
 * @description : 定义用户web层接口资源bean
 */
@RestController
@RequestMapping("/api")
@Api(tags = "用户相关接口处理器")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据用户名称查询用户信息
     * @param name
     * @return
     */
    @ApiOperation(value = "根据用户名查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "用户名",dataType = "string",required = true,type = "path")
    })
    @GetMapping("/user/{userName}")
    public SysUser getUserByUserName(@PathVariable("userName") String name){
        return userService.findByUserName(name);
    }

    /**
     * 用户登录功能
     * @param vo
     * @return
     */
//    @ApiOperation(value = "用户登录功能")
//    @PostMapping("/login")
//    public R<LoginRespVo> login(@RequestBody LoginReqVo vo){
//        return userService.login(vo);
//    }


    /**
     * 生成图片验证码功能
     * @return
     */
    @ApiOperation(value = "生成图片验证码功能", notes = "生成图片验证码功能", httpMethod = "GET")
    @GetMapping("/captcha")
    public R<Map> getCaptchaCode(){
       return userService.getCaptchaCode();
    }


}
