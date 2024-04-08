package com.itheima.stock.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by itheima
 * @Date 2021/12/30
 * @Description 登录请求vo
 */
@Data
public class LoginReqVo {
    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;
    /**
     * 密码
     */
    @ApiModelProperty("明文密码")
    private String password;
    /**
     * 验证码
     */
    @ApiModelProperty("验证码")
    private String code;

    /**
     * 会话ID
     */
    @ApiModelProperty("会话ID")
    private String sessionId;
}