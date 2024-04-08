package com.itheima.stock.vo.resp;

import lombok.Data;

/**
 * @author by itheima
 * @Date 2021/12/26
 * @Description 用户详情信息封装
 */

@Data
public class UserInfoRespVo {
    private Long id;

    private String username;

    private String phone;

    private String nickName;

    private String realName;

    private Integer sex;

    private Integer status;

    private String email;
}
