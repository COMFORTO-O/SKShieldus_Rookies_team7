package com.example.shieldus.config.security;


import com.example.shieldus.config.jwt.JwtTokenProvider;
import com.example.shieldus.config.security.filter.JwtAuthenticationFilter;
import com.example.shieldus.config.security.filter.JwtRequestFilter;
import com.example.shieldus.config.security.utils.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RequiredArgsConstructor
public class ServletSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RSAUtil rsaUtil;

    public String WHITE_LIST = "";
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/account/login/**").permitAll() // 로그인 경로는 인증 없이 접근 가능
                        .requestMatchers("/api/account/register").permitAll()
                        .requestMatchers("/api/compile/**").permitAll()
                        .requestMatchers("/api/problem/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .addFilterBefore(new JwtRequestFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider, rsaUtil), UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    /*
     * cors 설정. Allow Origin YML 추출 필요.
     * */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://127.0.0.1:8080"));
        // 허용할 HTTP 메서드 (GET, POST, PUT, DELETE, OPTIONS 등)
        configuration.setAllowedMethods(List.of("*"));
        // 허용할 헤더 (모든 헤더 허용)
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With", // XMLHttpRequest를 사용할 때 자주 포함되는 헤더
                "Cache-Control" // 캐시 제어 관련 헤더
        ));
        configuration.setAllowCredentials(true);
        // 사전 비행(Preflight) 요청의 캐싱 시간 (초 단위)
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 이 CORS 설정을 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
