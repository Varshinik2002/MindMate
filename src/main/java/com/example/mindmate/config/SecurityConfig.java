package com.example.mindmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowCredentials(true);
                // Replace with your frontend URL or "*" for testing
                config.setAllowedOrigins(List.of("https://mindmate-27cv.onrender.com", "http://localhost:3000"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                return config;
            }))
            .authorizeHttpRequests(auth -> auth
                // Frontend pages
                .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/forgot_password.html").permitAll()
                // Static resources
                .requestMatchers("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif").permitAll()
                // API endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/chat/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
