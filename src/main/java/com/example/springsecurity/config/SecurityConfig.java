package com.example.springsecurity.config;

import com.example.springsecurity.util.CustomUsernameDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private CustomUsernameDetailService customUsernameDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authurize) -> authurize
                        .requestMatchers(HttpMethod.GET, "/api/hello").permitAll()
                        .requestMatchers("/api/admin").hasAuthority("ADMIN")
                        .anyRequest().authenticated()).httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return  http.build();
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
//    @Bean
//    public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
//        UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("12345")
//                .authorities("ADMIN")
//                .build();
//        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("12345").authorities("user").build();
//        return new InMemoryUserDetailsManager(admin, user);
//    }
}
