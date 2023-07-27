package com.employee.management.manager.controller;

import com.employee.management.manager.model.User;
import com.employee.management.manager.service.UserService;
import com.employee.management.manager.security.JwtUserDetailsService; // Import the JwtUserDetailsService
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService jwtUserDetailsService; // Use JwtUserDetailsService instead of JwtTokenUtil


    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUserDetailsService jwtUserDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody User user) {


       try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getHashedPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getUsername()); // Use jwtUserDetailsService to load UserDetails
        final String token = jwtUserDetailsService.generateToken(userDetails); // Use jwtUserDetailsService to generate the token

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
