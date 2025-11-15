package com.macro.mall.portal.config;

import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * mall-security模块相关配置
 * Created by macro on 2019/11/5.
 */
@Configuration
public class MallSecurityConfig {

    @Autowired
    private UmsMemberService memberService;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Bean
    public UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> memberService.loadUserByUsername(username);
    }

    @Bean
    @Primary
    public JwtTokenUtil jwtTokenUtil() {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field secretField = JwtTokenUtil.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(jwtTokenUtil, secret);

            java.lang.reflect.Field expirationField = JwtTokenUtil.class.getDeclaredField("expiration");
            expirationField.setAccessible(true);
            expirationField.set(jwtTokenUtil, expiration);

            java.lang.reflect.Field tokenHeadField = JwtTokenUtil.class.getDeclaredField("tokenHead");
            tokenHeadField.setAccessible(true);
            tokenHeadField.set(jwtTokenUtil, tokenHead);
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure JwtTokenUtil", e);
        }
        return jwtTokenUtil;
    }
}
