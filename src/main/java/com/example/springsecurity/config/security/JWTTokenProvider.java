package com.example.springsecurity.config.security;

import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {
    @Autowired
    private UserRepository userRepository;
    private String secret = "20be3c2ff70a7aadda0d4e59874346de451d01cdec029190d58e04389daf5435";
    private long jwtExpirationDate = 604800016;
    Logger logger = LoggerFactory.getLogger(JWTTokenProvider.class);

    public String genreToken(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        Map<String, Object> claims = new HashMap<>();
        Set<Role> roleList = user.getRoles();
//        if(user != null && user.getRoles().size() > 0){
//            for (Role r : roleList){
//                if (r.getName().equals("ADMIN")){
//                    claims.put("role", "ADMIN");
//                    break;
//                }
//                if(r.getName().equals("USER")){
//                    claims.put("role", "USER");
//                    break;
//                }
//            }
//        }
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        return createToken(claims, username);
    }

    // Tạo token
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationDate))
                .signWith(SignatureAlgorithm.HS256, getSignKey()).compact();
    }

    // Ma hoa key
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Trich xuat tat ca thong tin
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public List<Role> extractRole(String token) {
        List<String> roles = extractClaims(token, claims -> claims.get("roles", List.class));
        return roles.stream().map(role ->
                {
                    Role role1 = new Role();
                    role1.setName(role);
                    return role1;
                }
        ).collect(Collectors.toList());
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
