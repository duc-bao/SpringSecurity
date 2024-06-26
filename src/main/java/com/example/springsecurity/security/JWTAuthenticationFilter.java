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
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        // lấy token người dùng truyền vào và lấy username dựa trên token đó
        if(authHeader!= null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            // System.out.println("Token: " + token);
            username = jwtTokenProvider.extractUsername(token);
            //System.out.println("Username: " + username);
        }
        // ==> Từ chuỗi token lấy được chúng ta thêm người dùng vào cái request đó
        // Kiểm tra xem user có tồn tại và đã đăng nhập hay chưa
        if(username != null && SecurityContextHolder.getContext().getAuthentication()== null){
            UserDetails userDetails = customUsernameDetailService.loadUserByUsername(username);
            System.out.println("UserDetails: " + userDetails);
            // Kiểm tra tính hợp lệ của token
            if(jwtTokenProvider.validateToken(token, userDetails)){
                // Tạo user với cái quyền của nó
                UsernamePasswordAuthenticationToken auToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auToken);
            }
        }
        filterChain.doFilter(request,response);
    }
    /* Với cơ chế xác thực Authentication với JWT
    Đầu tiên nhận vào request có kèm theo token từ client gửi lên từ đó -> ta sử lí lấy được token
    -> Ta sẽ sử dụng lớp JWTTokenProvider(lớp này có ý nghĩa là tạo token và có thể từ token ta có thể lấy được các Payload với
    các claims mà ta xét) sau khi lấy được username từ lớp JWTTokenProvider ta sẽ kiểm tra và load UserDetails lên
    Sau đó ta sẽ kiểm tra xem token còn hợp lệ hay không nếu hợp lệ thì ta xét UserDetails vào lớp UsernamePasswordAuthenticationToken
    lớp này cho phép ta xác thực bằng Username, và password. Và ta lưu thông tin xác thực vào SecurityContextHolder
    */
}
