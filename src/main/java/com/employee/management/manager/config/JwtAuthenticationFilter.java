package com.employee.management.manager.config;

import com.employee.management.manager.service.UserService;
import com.employee.management.manager.security.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUserDetailsService jwtUserDetailsService; // Use JwtUserDetailsService instead of JwtTokenUtil
    private final UserService userService;

    @Autowired
    public JwtAuthenticationFilter(JwtUserDetailsService jwtUserDetailsService, UserService userService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {


        final String requestTokenHeader = request.getHeader("Authorization");

        log.debug("entering");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            log.debug("jwtToken :" +jwtToken);
            try {
                String username = jwtUserDetailsService.getUsernameFromToken(jwtToken);
                log.debug("username :" +username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

                    if (jwtUserDetailsService.validateToken(jwtToken, userDetails)) { // Use jwtUserDetailsService to validate the token
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }

                }
            } catch (ExpiredJwtException e) {
                log.error("Token Expired: " + e);
            }
        }

        chain.doFilter(request, response);
    }
}
