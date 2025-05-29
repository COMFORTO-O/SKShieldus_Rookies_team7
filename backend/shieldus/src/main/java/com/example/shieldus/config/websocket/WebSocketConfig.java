package com.example.shieldus.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final DocumentWebSocketHandler documentWebSocketHandler;
    private final RoomWebSocketHandler roomWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins("*");
        registry.addHandler(documentWebSocketHandler, "/ws/doc")
                .setAllowedOrigins("*");
        registry.addHandler(roomWebSocketHandler, "/ws/room/{roomId}")
                .setAllowedOrigins("*");
    }
}
