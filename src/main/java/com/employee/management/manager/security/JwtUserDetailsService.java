package com.employee.management.manager.security;

import com.employee.management.manager.entity.UserRepository;
import com.employee.management.manager.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Autowired
    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("username " +username);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    // Method to generate a new JWT token
    public String generateToken(UserDetails userDetails) {
        log.debug("userDetails " +userDetails);

        Map<String, Object> claims = new HashMap<>();
        // Add any additional claims to the token if needed
        return doGenerateToken(claims, userDetails.getUsername());
    }

    // Method to create a new JWT token based on claims and subject
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        // Retrieve the secret key from the database (replace "getSecretKeyFromDatabase()" with your method to retrieve the secret)

        String secretKey = getSecretKeyFromDatabase(subject);
        log.debug("subject " +subject);
        log.debug("secretKey " +secretKey);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // Replace EXPIRATION_TIME with your desired expiration time
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // Method to validate a JWT token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        log.debug("username+token " +username+","+token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Method to retrieve the username from a JWT token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        String username = getUsernameFromToken(token);
        String secretKey = getSecretKeyFromDatabase(username);

        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    // Method to check if a JWT token is expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Method to retrieve the expiration date from a JWT token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Method to retrieve the secret key from the database (replace this method with your actual implementation)
    private String getSecretKeyFromDatabase(String username) {
        log.debug("getSecretKeyFromDatabase " +username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getHashedPassword();
        }
        throw new RuntimeException("JWT secret key not found for the user: " + username);
    }

}
