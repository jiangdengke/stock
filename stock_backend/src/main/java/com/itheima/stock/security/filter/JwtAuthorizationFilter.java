package com.itheima.stock.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.stock.security.utils.JwtTokenUtil;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangDk
 * @date 2024/4/3 17:54
 * @description 授权过滤器 过滤一切请求，如果带着token，则去校验。如果没带token，则去校验
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    /**
     *
     * @param request
     * @param response
     * @param filterChain 过滤器链
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //从请求头获取token字符串
        String tokenStr = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        //判断是否为空
        if (StringUtils.isBlank(tokenStr)) {
            //如果票据为空，则放行，此时安全上下文中肯定没有这个票据，后续的过滤器也得不到这个票据
            filterChain.doFilter(request,response);
            return;
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        //检查是否合法
        Claims claims = JwtTokenUtil.checkJWT(tokenStr);
        if (claims==null) {
            //票据不合法，过滤器终止。
            R<Object> error = R.error(ResponseCode.INVALID_TOKEN);
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
            return;
        }
        String username = JwtTokenUtil.getUsername(tokenStr);
        String roles = JwtTokenUtil.getUserRole(tokenStr);
        String strip = StringUtils.strip(roles, "[]");
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(strip);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null, authorities);
        //将封装的票据存到安全上下文，这样后续的认证过滤器就可以从安全上下文中获取权限信息
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //发行请求
        filterChain.doFilter(request,response);
    }
}
