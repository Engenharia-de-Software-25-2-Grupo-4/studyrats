package com.example.studyrats.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import com.example.studyrats.service.firebase.FirebaseService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import com.example.studyrats.security.FirebaseAuthFilter;
import org.springframework.context.annotation.Bean;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, FirebaseService firebaseService) throws Exception {

        FirebaseAuthFilter firebaseFilter =
                new FirebaseAuthFilter(firebaseService);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/public/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        firebaseFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}