package com.example.springsecurity.config;

import com.example.springsecurity.security.JWTAuthenticationFilter;
import com.example.springsecurity.util.CustomUsernameDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private CustomUsernameDetailService customUsernameDetailService;
    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authurize) -> authurize
                        .requestMatchers(HttpMethod.GET, "/api/hello").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers("/api/admin").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()).httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return  http.build();
        /* - với phương thức crsf.disable thì là ngăn chặn các request được gửi đến và yêu cầu dùng để xác thực. Khi 1 request được gửi đến
        thì cần phải kiểm tra là khi chúng ta call API coppy token vào 1 browser khác thì  chúng ngăn chặn khi ko cần chứng thực
        * - http.authorizeHttpRequests thì ủy quyền requests
        - .requestsMatchers.permitAll() cho phép tất cả request với endpoint trên mà ko cần xác thực
        - .anyRequest().authenticated() với các request khác thì ta sẽ bắt xác thực.
        - http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) thì ta sẽ không cần lưu trạng
           thái để đảm bảo rằng với mỗi request đến thì ta cần phải xác thực
        - http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) thì ta sẽ thực thi bộ lọc xác thực
         JWT trước và sau đó set và update với 2 tham số đầu tiên là bộ lọc JWT, tham số thứ 2 là xác thực bộ lọc authen với username
         và password
         - DaoAuthentication thì chúng ta sẽ sử dụng Interface UserDetailService và PasswordEncoder để xác thực username và password từ
          request gửi lên
           Quy trình từ: Filter rồi sang UsernamePasswordTOken -> AuthenticationManager để thực hiện ProviderMangager (có trình xác thực
           mặc định là DaoAuthentication) và DaoAuthentication chúng ta sử dụng UserDetail để xác thực khi thành công thì tiếp tục
           và lưu securityCOntext.
        */

    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(CustomUsernameDetailService customUsernameDetailService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUsernameDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return  daoAuthenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return  authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
//        UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("12345")
//                .authorities("ADMIN")
//                .build();
//        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("12345").authorities("user").build();
//        return new InMemoryUserDetailsManager(admin, user);
//    }
}
