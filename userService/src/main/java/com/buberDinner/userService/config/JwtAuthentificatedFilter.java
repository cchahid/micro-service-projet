package com.buberDinner.userService.config;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.buberDinner.userService.jwt.JwtUtil;
import com.buberDinner.userService.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthentificatedFilter extends OncePerRequestFilter{

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public JwtAuthentificatedFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
           final String authHeader = request.getHeader("Authorization");
           if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
                  filterChain.doFilter(request, response);
                  return;
           }
           final String jwt;
           jwt = authHeader.substring(7) ;
           final String userEmail;
           userEmail = jwtUtil.extractUsername(jwt);
           if (StringUtils.isNotEmpty(userEmail)
           && SecurityContextHolder.getContext().getAuthentication() == null) {
                   UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
                   if(jwtUtil.validateToken(jwt, userDetails)){
                         SecurityContext context = SecurityContextHolder.createEmptyContext();

                         UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                         authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                         context.setAuthentication(authToken);
                         SecurityContextHolder.setContext(context);
                   }
           }
           filterChain.doFilter(request, response);       
    }




}
