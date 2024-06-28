package com.example.springsecurity.config.security;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.util.CustomUserDetailService;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);
            // lấy token người dùng truyền vào và lấy username dựa trên token đó
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // get username from token
                String username = jwtTokenProvider.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Load the user associated with token
                    List<Role> roles = jwtTokenProvider.extractRole(token);
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName()))
                            .collect(Collectors.toList());
                    UserDetails userDetails = new UserDetails() {
                        @Override
                        public Collection<? extends GrantedAuthority> getAuthorities() {
                            return authorities;
                        }

                        @Override
                        public String getPassword() {
                            return null;
                        }

                        @Override
                        public String getUsername() {
                            return username;
                        }
                    };
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // BỔ sung thông tin xác thực liên quan đến request chi tiết như IP ...
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("User authentication");
                }
            }

        } catch (Exception e) {
            logger.error("Cannot set user authentication");
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    /* Với cơ chế xác thực Authentication với JWT
    Đầu tiên nhận vào request có kèm theo token từ client gửi lên từ đó -> ta sử lí lấy được token
    -> Ta sẽ sử dụng lớp JWTTokenProvider(lớp này có ý nghĩa là tạo token và có thể từ token ta có thể lấy được các Payload với
    các claims mà ta xét) sau khi lấy được username từ lớp JWTTokenProvider ta sẽ kiểm tra và load UserDetails lên
    Sau đó ta sẽ kiểm tra xem token còn hợp lệ hay không nếu hợp lệ thì ta xét UserDetails vào lớp UsernamePasswordAuthenticationToken
    lớp này cho phép ta xác thực bằng Username, và password. Và ta lưu thông tin xác thực vào SecurityContextHolder
    */
}
