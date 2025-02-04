package com.example.springsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.springsecurity.config.security.ConfigAuthenticationFilter;
import com.example.springsecurity.config.security.JWTAuthenticationFilter;
import com.example.springsecurity.util.CustomUserDetailService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {
    CustomUserDetailService customUserDetailService;
    AuthenticationConfiguration authenticationConfiguration;
    JWTAuthenticationFilter jwtAuthenticationFilter;
    String [] Public_ENDPOINT = {
            "/api/hello", "/api/register","/api/outbound/authentication", "/api/user/info", "/login", "/api/user", "/api/refresh"
    };
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authurize) -> authurize
                        .requestMatchers(HttpMethod.GET,"/api-docs/**", "swagger-resources/*","/configuration/**","/swagger-ui/**" ).permitAll()
                        .requestMatchers(HttpMethod.GET, Public_ENDPOINT)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, Public_ENDPOINT).permitAll()
                        .requestMatchers("/api/admin")
                        .hasAuthority("ROLE_ADMIN")
                        .anyRequest()
                        .authenticated())
                // .httpBasic(Customizer.withDefaults()) HTTP Basic Authentication
                .csrf(AbstractHttpConfigurer::disable);// Tắt cái tấn công endpoint trước tấn công csrf
        // Cấu hình cors
        http.cors(cors -> {
            cors.configurationSource(request -> {
                CorsConfiguration corsConfig = new CorsConfiguration();
                corsConfig.addAllowedOrigin("http://localhost:3000");
                corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                corsConfig.addAllowedHeader("*");
                return corsConfig;
            });
        });
        http.addFilterBefore(
                configAuthenticationFilter(authenticationManager(authenticationConfiguration)),
                UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
        /* ConfigAuthenticationFilter sẽ được gọi mỗi khi có một request đến endpoint /login để thực hiện quá trình đăng nhập
        -Với phương thức httpBasic(Customuzer.withDefaults) khi đó thì chúng ta sẽ dùng xác thực cơ bản của HTTP Basic Authentication
        để xác thực dựa trên username và password đã được mã hóa gửi lên cùng với request, khi tắt httpBasic(Customizer.withDefaults())
        thì nó sẽ ko được xác thực thông qua username, password
        với phương thức crsf.disable thì là ngăn chặn các request được gửi đến và yêu cầu dùng để xác thực. Khi 1 request được gửi đến
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
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }
    @Bean
    public ConfigAuthenticationFilter configAuthenticationFilter(AuthenticationManager authenticationManager){
        return new ConfigAuthenticationFilter(authenticationManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(CustomUserDetailService customUserDetailService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //    @Bean
    //    public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
    //        UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("12345")
    //                .authorities("ADMIN")
    //                .build();
    //        UserDetails user =
    // User.withDefaultPasswordEncoder().username("user").password("12345").authorities("user").build();
    //        return new InMemoryUserDetailsManager(admin, user);
    //    }
}
