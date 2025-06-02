package com.example.shieldus.config.security;


import com.example.shieldus.config.jwt.JwtTokenProvider;
import com.example.shieldus.config.security.filter.JwtAuthenticationFilter;
import com.example.shieldus.config.security.filter.JwtRequestFilter;
import com.example.shieldus.config.security.service.MemberUserDetailsService;
import com.example.shieldus.config.security.utils.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
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
    private final MemberUserDetailsService memberUserDetailsService;

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

                .addFilterBefore(new JwtRequestFilter(jwtTokenProvider, memberUserDetailsService), UsernamePasswordAuthenticationFilter.class)
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

    @Bean
    public ChannelInterceptor jwtWebSocketChannelInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                // STOMP CONNECT 메시지 처리 (연결 시 인증)
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 1. HTTP 핸드셰이크에서 이미 인증된 경우 (JWT 쿠키 사용 시)
                    //    SecurityContextHolder에서 Authentication 객체를 가져와 WebSocket 세션에 연결
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.isAuthenticated() && accessor.getUser() == null) {
                        accessor.setUser(authentication);
                        System.out.println("WebSocket authenticated via HTTP handshake: " + authentication.getName());
                    }

                    // 2. STOMP CONNECT 헤더에 JWT를 포함하는 경우 (Authorization: Bearer <token>)
                    //    만약 JWT를 쿠키로 보내지 않고 Authorization 헤더로 보낸다면 이 로직을 활성화해야 합니다.
                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String jwt = authorizationHeader.substring(7);
                        try {
                            // TODO: JwtRequestFilter와 같은 로직 구현
                        } catch (Exception e) {
                            System.err.println("Invalid JWT for WebSocket connection in STOMP header: " + e.getMessage());
                        }
                    } else if (accessor.getUser() == null) {
                        // 쿠키로도, 헤더로도 인증 정보가 없는 경우 (필요에 따라 연결 거부)
                        System.err.println("WebSocket connection denied: No authentication info found.");
                    }
                }
                return message;
            }
        };
    }

    /*
    * STOMP 용 RequestMatcher
    * */
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages =
                new MessageMatcherDelegatingAuthorizationManager.Builder();
        messages
                .simpDestMatchers("/app/**").authenticated()
                .simpSubscribeDestMatchers("/topic/**", "/queue/**").permitAll()
                .anyMessage().authenticated();
        return messages.build();
    }
}
