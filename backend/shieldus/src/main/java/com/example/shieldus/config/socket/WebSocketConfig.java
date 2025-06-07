package com.example.shieldus.config.socket;

import com.example.shieldus.config.security.interceptor.JwtWebSocketChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // WebSocket 메시지 브로커 기능 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtWebSocketChannelInterceptor jwtWebSocketChannelInterceptor;

    // JwtWebSocketChannelInterceptor 주입
    public WebSocketConfig(JwtWebSocketChannelInterceptor jwtWebSocketChannelInterceptor) {
        this.jwtWebSocketChannelInterceptor = jwtWebSocketChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Simple 메시지 브로커 활성화 (인메모리 브로커)
        // /topic: 1대N 발행/구독 메시지 (public chat 등)
        // /queue: 1대1 메시지 (개인 알림 등)
        config.enableSimpleBroker("/topic", "/queue");

        // 클라이언트에서 메시지를 서버의 @MessageMapping 메서드로 보낼 때 사용하는 prefix
        // 예: /app/chat, /app/send
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // STOMP WebSocket 연결을 위한 엔드포인트 등록
        registry.addEndpoint("/ws") // WebSocket 연결 URL: ws://localhost:8080/ws
                .setAllowedOriginPatterns("*") // 모든 Origin 허용 (운영 환경에서는 특정 도메인만 허용)
                .withSockJS(); // SockJS 지원 (WebSocket을 사용할 수 없는 브라우저를 위한 대체 옵션)
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 클라이언트로부터 들어오는 인바운드 채널에 인터셉터 추가
        // 여기서 JWT 인증 인터셉터를 등록하여 STOMP 메시지 수신 전에 인증 처리
        registration.interceptors(jwtWebSocketChannelInterceptor);
    }

}