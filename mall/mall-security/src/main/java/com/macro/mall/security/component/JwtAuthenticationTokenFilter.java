package com.macro.mall.security.component;

import com.macro.mall.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT登录授权过滤器
 * Created by macro on 2018/4/26.
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(this.tokenHeader);
        LOGGER.info("JWT Filter - Request URI: {}, Auth Header: {}", request.getRequestURI(), authHeader);

        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());// The part after "Bearer "
            LOGGER.info("JWT Filter - Extracted token: {}", authToken);

            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            LOGGER.info("JWT Filter - Username from token: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    LOGGER.info("JWT Filter - UserDetails loaded: {}", userDetails != null ? userDetails.getUsername() : "null");

                    if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        LOGGER.info("JWT Filter - Authentication successful for user: {}", username);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        LOGGER.warn("JWT Filter - Token validation failed for user: {}", username);
                    }
                } catch (Exception e) {
                    LOGGER.error("JWT Filter - Error loading user details for username: {}", username, e);
                }
            } else {
                LOGGER.warn("JWT Filter - Username is null or authentication already exists");
            }
        } else {
            LOGGER.warn("JWT Filter - No valid Authorization header found");
        }
        chain.doFilter(request, response);
    }
}
