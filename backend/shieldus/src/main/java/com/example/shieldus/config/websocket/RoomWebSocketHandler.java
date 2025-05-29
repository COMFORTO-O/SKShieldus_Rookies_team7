package com.example.shieldus.config.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RoomWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String roomId = getRoomId(session);
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String roomId = getRoomId(session);
        for (WebSocketSession s : roomSessions.getOrDefault(roomId, List.of())) {
            if (s.isOpen()) s.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String roomId = getRoomId(session);
        roomSessions.getOrDefault(roomId, List.of()).remove(session);
    }

    private String getRoomId(WebSocketSession session) {
        String uri = session.getUri().toString();
        return uri.substring(uri.lastIndexOf("/") + 1); // /ws/room/{roomId}
    }
}