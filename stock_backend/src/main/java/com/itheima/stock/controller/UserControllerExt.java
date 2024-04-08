package com.itheima.stock.controller;

import com.itheima.stock.service.UserServiceExt;
import com.itheima.stock.vo.req.*;
import com.itheima.stock.vo.resp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author by itheima
 * @Date 2021/12/21
 * @Description
 */
@RestController
@RequestMapping("/api")
public class UserControllerExt {

    /**
     * 注入服务
     */
    @Autowired
    private UserServiceExt userService;

//    @PostMapping("/login")
//    public R<LoginRespVoExt> login(@RequestBody LoginReqVo vo){
//        return userService.login(vo);
//    }

    /**
     * 多条件综合查询用户分页信息，条件包含：分页信息 用户创建日期范围
     * @param userPageReqVo
     * @return
     */
    @PostMapping("/users")
    public R<PageResult>  pageUsers(@RequestBody UserPageReqVo userPageReqVo){
      return this.userService.pageUsers(userPageReqVo);
    }

    /**
     * 添加用户信息
     * @param vo
     * @return
     */
    @PostMapping("/user")
    public R<String> addUser(@RequestBody UserAddReqVo vo){
        return this.userService.addUser(vo);
    }

    /**
     * 更新用户信息
     * @param vo
     * @return
     */
    @PutMapping("/user")
    public R<String> updateUser(@RequestBody UserEditReqVO vo){
        return this.userService.updateUser(vo);
    }

    /**
     * 获取用户具有的角色信息，以及所有角色信息
     * @param userId
     * @return
     */
    @GetMapping("/user/roles/{userId}")
    public R<UserOwnRoleRespVo> getUserOwnRole(@PathVariable("userId")Long userId){
        return this.userService.getUserOwnRole(userId);
    }

    /**
     * 更新用户角色信息
     * @param vo
     * @return
     */
    @PutMapping("/user/roles")
    public R<String> updateUserOwnRoles(@RequestBody UserOwnRoleReqVo vo){
        return this.userService.updateUserOwnRoles(vo);
    }

    /**
     * 批量删除用户信息
     * delete请求可通过请求体携带数据
     * @param userIds
     * @return
     */
    @DeleteMapping("/user")
    public R<String> deleteUsers(@RequestBody List<Long> userIds){
        return this.userService.deleteUsers(userIds);
    }


    /**
     * 根据用户id查询用户信息
     * @param id
     * @return
     */
    @GetMapping("/user/info/{userId}")
    public R<UserInfoRespVo> getUserInfo(@PathVariable("userId") Long id){
        return userService.getUserInfo(id);
    }


}
