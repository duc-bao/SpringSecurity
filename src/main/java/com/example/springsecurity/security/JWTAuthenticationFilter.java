package com.example.springsecurity.security;

import com.example.springsecurity.util.CustomUsernameDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private CustomUsernameDetailService customUsernameDetailService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        // lấy token người dùng truyền vào và lấy username dựa trên token đó
        if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)){
            // get username from token
            String username = jwtTokenProvider.extractUsername(token);
            // Load the user associated with token
            UserDetails userDetails = customUsernameDetailService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request,response);
    }
    private String getTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
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
