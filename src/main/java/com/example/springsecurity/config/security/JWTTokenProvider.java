package com.example.springsecurity.config.security;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.UserRepository;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTTokenProvider {
    @Autowired
    private UserRepository userRepository;
    @Value("${jwt.secret}")
    private String secret ;
    @Value("${jwt.expirationDate}") // 15 phút
    private long jwtExpirationDate ;
    @Value("${jwt.refreshTokenExpirationDate}") // 30 ngày
    private long refreshTokenExpirationDate ;
    Logger logger = LoggerFactory.getLogger(JWTTokenProvider.class);

    public String genreToken(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        Map<String, Object> claims = new HashMap<>();
        List<Role> roleList = user.getRoles();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("token_version", user.getVersionToken());
        return createToken(claims, username);
    }
    public  String generRefreshToken(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        Map<String, Object> claims = new HashMap<>();
        claims.put("jti", UUID.randomUUID().toString());
        return  createToken(claims, username);
    }
    // Tạo token
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationDate))
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }

    // Ma hoa key
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Trich xuat tat ca thong tin
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }
    public String extractIdToken(String token){
        return extractClaims(token, claims -> claims.get("jti", String.class));
    }
    public List<Role> extractRole(String token) {
        List<String> roles = extractClaims(token, claims -> claims.get("roles", List.class));
        return roles.stream()
                .map(role -> {
                    Role role1 = new Role();
                    role1.setName(role);
                    return role1;
                })
                .collect(Collectors.toList());
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenExpiration(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(getSignKey()).build().parse(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

}
