package com.example.shieldus.config.security;

import com.example.shieldus.config.jwt.JwtTokenProvider;
import com.example.shieldus.config.security.filter.JwtAuthenticationFilter;
import com.example.shieldus.config.security.filter.JwtRequestFilter;
import com.example.shieldus.config.security.utils.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // HttpMethod 임포트 추가
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // CorsConfiguration 임포트 추가
import org.springframework.web.cors.CorsConfigurationSource; // CorsConfigurationSource 임포트 추가
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // UrlBasedCorsConfigurationSource 임포트 추가

import java.util.Arrays; // Arrays 임포트 추가
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RequiredArgsConstructor
public class ServletSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RSAUtil rsaUtil;

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
                // 1. Spring Security의 CORS 설정 활성화 및 구성
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Preflight 요청을 위한 OPTIONS 메서드는 항상 허용 (CORS 설정 시 중요)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/account/login/**").permitAll()
                        .requestMatchers("/api/account/register").permitAll()
                        .requestMatchers("/api/problem/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtRequestFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                // JwtAuthenticationFilter는 UsernamePasswordAuthenticationFilter의 역할을 대체하므로,
                // addFilterAt을 사용하거나, 기존 필터를 비활성화하고 addFilter()를 사용할 수 있습니다.
                // addFilterAt은 해당 위치에 필터를 "추가"하는 것이므로, UsernamePasswordAuthenticationFilter가 여전히 존재할 수 있습니다.
                // 여기서는 UsernamePasswordAuthenticationFilter 대신 JwtAuthenticationFilter를 사용하려는 의도로 보입니다.
                .addFilterAt(new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider, rsaUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. CorsConfigurationSource 빈 정의
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 모든 출처를 허용하려면 (개발 시에만 권장)
        configuration.setAllowedOriginPatterns(List.of("*"));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 허용할 HTTP 헤더
        // "Authorization" 헤더는 JWT 토큰 전송을 위해 중요합니다.
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        // 모든 헤더를 허용하려면
        // configuration.setAllowedHeaders(List.of("*"));

        // 브라우저가 응답 헤더를 읽을 수 있도록 설정 (예: 커스텀 헤더를 클라이언트에서 접근해야 할 경우)
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));


        // 쿠키를 포함한 요청 허용 여부 (credentials)
        // 프론트엔드에서 `withCredentials: true` 옵션을 사용하고, 쿠키 기반 인증이나 세션을 사용한다면 true로 설정합니다.
        // JWT를 헤더로만 주고받는다면 false로 두어도 무방할 수 있지만, 보통 true로 많이 설정합니다.
        configuration.setAllowCredentials(true);

        // Preflight 요청의 결과를 캐시할 시간 (초 단위)
        configuration.setMaxAge(3600L); // 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 설정 적용
        return source;
    }
}