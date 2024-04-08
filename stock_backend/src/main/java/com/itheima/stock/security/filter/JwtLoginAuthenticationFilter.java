package com.itheima.stock.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.stock.constant.StockConstant;
import com.itheima.stock.security.user.LoginUserDetail;
import com.itheima.stock.security.utils.JwtTokenUtil;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVoExt;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangDk
 * @date 2024/4/2 21:05
 * @description 认证过滤器,认证用户信息，并颁发token
 */

public class JwtLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private RedisTemplate redisTemplate;
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 自定义构造器，传入登录认证的url地址
     * @param loginUrl
     */
    public JwtLoginAuthenticationFilter(String loginUrl) {
        super(loginUrl);
    }

    /**
     * 尝试去认证的方法
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //请求必须是post请求
        if (!request.getMethod().equalsIgnoreCase("POST") || ! (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType()) || MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(request.getContentType())) ) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        //获取post请求ajax传入的数据流
        LoginReqVo reqVo = new ObjectMapper().readValue(request.getInputStream(), LoginReqVo.class);
        //校验验证码是否正确
        String rCheckCode =(String) redisTemplate.opsForValue().get(StockConstant.CHECK_PREFIX + reqVo.getSessionId());
        if (rCheckCode==null || ! rCheckCode.equalsIgnoreCase(reqVo.getCode())) {
            //响应验证码输入错
            String error = new ObjectMapper().writeValueAsString(ResponseCode.CHECK_CODE_ERROR.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(error);
            return null;
//            return R.error(ResponseCode.CHECK_CODE_ERROR.getMessage());
        }
        String username = reqVo.getUsername();
        username = username != null ? username : "";
        username = username.trim();
        String password = reqVo.getPassword();
        password = password != null ? password : "";
        //组装认证的票据对象
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        //交给认证管理器认证票据对象
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * 认证成功后执行的方法
     * @param request
     * @param response
     * @param chain 过滤器链
     * @param authResult 认证对象信息
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        LoginUserDetail principal = (LoginUserDetail) authResult.getPrincipal();
        String username = principal.getUsername();
        Collection<GrantedAuthority> authorities = principal.getAuthorities();
        //生成票据
        String tokenStr = JwtTokenUtil.createToken(username, authorities.toString());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        //构建响应体实体对象
        LoginRespVoExt respVoExt = new LoginRespVoExt();
        BeanUtils.copyProperties(principal,respVoExt);
        respVoExt.setAccessToken(tokenStr);
        R<LoginRespVoExt> ok = R.ok(respVoExt);
        response.getWriter().write(new ObjectMapper().writeValueAsString(ok));
    }

    /**
     * 认证失败后执行的方法
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        R<Object> error = R.error(ResponseCode.ERROR);
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
