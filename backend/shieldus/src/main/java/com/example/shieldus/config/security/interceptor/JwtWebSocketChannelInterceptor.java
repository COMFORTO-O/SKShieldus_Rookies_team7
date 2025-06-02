package com.example.shieldus.config.security.interceptor;
import com.example.shieldus.config.jwt.JwtTokenProvider; // 경로 확인
import com.example.shieldus.config.security.service.MemberUserDetailsService; // 경로 확인
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.messaging.MessagingException; // 예외 처리를 위해 추가

import java.util.List;
import java.util.Map;

@Component // Spring 빈으로 등록
public class JwtWebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberUserDetailsService memberUserDetailsService;

    public JwtWebSocketChannelInterceptor(JwtTokenProvider jwtTokenProvider, MemberUserDetailsService memberUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberUserDetailsService = memberUserDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // STOMP CONNECT 메시지 처리 (연결 시 인증)
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 1. HTTP 핸드셰이크에서 이미 인증된 경우 (예: 세션 기반 인증 또는 Spring Security 필터 체인에서 처리된 경우)
            //    SecurityContextHolder에서 Authentication 객체를 가져와 WebSocket 세션에 연결
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            if (existingAuth != null && existingAuth.isAuthenticated() && accessor.getUser() == null) {
                accessor.setUser(existingAuth);
                return message; // 이미 인증되었으므로 추가 JWT 처리 없이 반환
            }
            String jwt = null;
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
            }

            // JWT가 추출되었다면 인증 시도
            if (jwt != null) {
                try {
                    if (jwtTokenProvider.validateToken(jwt)) {
                        Authentication tokenAuthentication = jwtTokenProvider.getAuthentication(jwt);
                        UserDetails userDetails = memberUserDetailsService.loadUserByUsername(tokenAuthentication.getName());
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        // WebSocket 세션에 인증 정보 설정 (매우 중요!)
                        accessor.setUser(authentication);
                    } else {
                        // 유효하지 않은 JWT는 연결 거부
                        throw new MessagingException("Invalid JWT for WebSocket connection");
                    }
                } catch (Exception e) {
                    // 인증 실패 시 연결 거부
                    throw new MessagingException("Authentication failed for WebSocket connection", e);
                }
            } else if (accessor.getUser() == null) {
                System.err.println("WebSocket Interceptor: WebSocket connection denied: No authentication info found.");
                throw new MessagingException("No authentication info found for WebSocket connection.");
            }
        }
        return message;
    }
}