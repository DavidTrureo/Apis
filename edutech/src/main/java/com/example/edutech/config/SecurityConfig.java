package com.example.edutech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    // Rutas de Swagger UI y la especificación OpenAPI que deben ser públicas.
    private static final String[] SWAGGER_WHITELIST = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-ui.html"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Permite el acceso sin autenticación a las rutas de Swagger.
                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                
                // 2. Por ahora, permite el acceso a todo lo demás.
                //    En el futuro, aquí pondrí poner reglas más específicas como .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS));
        
        return http.build();
    }
}