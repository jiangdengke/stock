package com.itheima.stock.security.config;

import com.itheima.stock.security.filter.JwtAuthorizationFilter;
import com.itheima.stock.security.filter.JwtLoginAuthenticationFilter;
import com.itheima.stock.security.handler.MyAccessDenieHandler;
import com.itheima.stock.security.handler.MyAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//开启对springSecurity注解功能支持.pre(前) post(后) 这个注解前后都支持
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 定义公共的无需被拦截的资源
     * @return
     */
    private String[] getPubPath(){
        //公共访问资源
        String[] urls = {
                "/**/*.css","/**/*.js","/favicon.ico","/doc.html",
                "/druid/**","/webjars/**","/v2/api-docs","/api/captcha",
                "/swagger/**","/swagger-resources/**","/swagger-ui.html"
        };
        return urls;
    }
    /**
     * 配置资源权限绑定配置
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //登出功能
        http.logout().logoutUrl("/api/logout").invalidateHttpSession(true);
        //开启允许iframe 嵌套。security默认禁用ifram跨域与缓存
        http.headers().frameOptions().disable().cacheControl().disable();
        //session禁用
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();//禁用跨站请求伪造
        http.authorizeRequests()//对资源进行认证处理
                .antMatchers(getPubPath()).permitAll()//公共资源都允许访问
                .anyRequest().authenticated();  //除了上述资源外，其它资源，只有 认证通过后，才能有权访问
        http.addFilterBefore(jwtLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        //将授权过滤器置于所有过滤器之前（主要是自定义的认证过滤器之前）
        http.addFilterBefore(JwtAuthorizationFilter(), JwtLoginAuthenticationFilter.class);
        //配置访问拒绝的处理器
        http.exceptionHandling().accessDeniedHandler(new MyAccessDenieHandler())
        .authenticationEntryPoint(new MyAuthenticationEntryPoint());
    }

    /**
     * 认证过滤器
     * @return
     * @throws Exception
     */
    @Bean
    public JwtLoginAuthenticationFilter jwtLoginAuthenticationFilter() throws Exception {
        //构造认证管理器，并设置认证路径 /myLogin
        JwtLoginAuthenticationFilter authenticationFilter = new JwtLoginAuthenticationFilter("/api/login");
        //设置认证管理器bean
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        //注入redis模版对象
        authenticationFilter.setRedisTemplate(redisTemplate);
        return authenticationFilter;
    }
    /**
     * 授权过滤器
     * 检查jwt的票据是否有效，做相关处理
     */
    @Bean
    public JwtAuthorizationFilter JwtAuthorizationFilter(){
        return new JwtAuthorizationFilter();
    }
    /**
     * 定义密码加密匹配器
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
