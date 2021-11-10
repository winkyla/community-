package com.mao.community.config;


import com.mao.community.util.CommunityConstant;
import com.mao.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resource/**");
    }

    //已经写过了登录的拦截器，这里就不适用security的登录认证的config


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"

                )
                .hasAnyAuthority(
                        AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/well"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()//其他请求都允许
                .and().csrf().disable();//禁用csrf

        //权限不够时的处理
        http.exceptionHandling()
                //未登录时的处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                       String xRequestedWith=httpServletRequest.getHeader("x-requested-with");
                       if("XMLHttpRequest".equals(xRequestedWith)){
                           //返回xml的请求（异步请求）
                           httpServletResponse.setContentType("application/plain;charset=utf-8");
                           PrintWriter writer=httpServletResponse.getWriter();
                           writer.write(CommunityUtil.getJSONString(403,"你还未登录!"));
                       }else {
                           httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
                       }
                    }
                })
                //登录了但是权限不足的处理
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith=httpServletRequest.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            //返回xml的请求（异步请求）
                            httpServletResponse.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer=httpServletResponse.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"你没有访问此功能的权限!"));
                        }else {
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/denied");
                        }
                    }
                });

        //Security底层会默认拦截/logout请求，进行退出处理
        //覆盖它默认的逻辑，才能执行我们自己的退出代码
        http.logout().logoutUrl("/securitylogout");//默认是拦截/logout这里我们修改为/securitylogout，虽然该路径并不存在，但只是为了覆盖原路径



    }
}
