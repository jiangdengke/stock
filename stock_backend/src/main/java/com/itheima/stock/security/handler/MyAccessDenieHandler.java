package com.itheima.stock.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangDk
 * @date 2024/4/3 20:19
 * @description 用户合法但是无权访问资源时的处理器
 */
@Slf4j
public class MyAccessDenieHandler implements AccessDeniedHandler {
    /**
     * 用户合法但是无权访问资源时的处理器
     * @param request
     * @param response
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        log.warn("当前用户访问资源拒绝，原因:{}",e.getMessage());
        R<Object> error = R.error(ResponseCode.ANONMOUSE_NOT_PERMISSION);
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
