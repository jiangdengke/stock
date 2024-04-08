package com.itheima.stock.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jiangDk
 * @date 2024/4/3 20:19
 * @description 用户匿名访问时的处理器
 */
@Slf4j
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 用户未登录是走的方法
     * @param request
     * @param response
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.warn("匿名用户拒绝访问，原因:{}",e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        R<Object> error = R.error(ResponseCode.NOT_PERMISSION);
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
