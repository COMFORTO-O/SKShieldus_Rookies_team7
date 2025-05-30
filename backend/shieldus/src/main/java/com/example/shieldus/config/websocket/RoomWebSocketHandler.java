package com.example.shieldus.config.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Component
public class RoomWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String roomId = getRoomId(session);
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String roomId = getRoomId(session);
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());

        String type = jsonNode.get("type").asText(); // "chat" or "code_update" 등

        for (WebSocketSession s : roomSessions.getOrDefault(roomId, List.of())) {
            if (s.isOpen()) {
                ObjectNode outbound = objectMapper.createObjectNode();
                outbound.put("type", type);
                outbound.set("data", jsonNode); // 전체 메시지 포함 (원하면 특정 필드만 넣어도 됨)

                s.sendMessage(new TextMessage(outbound.toString()));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String roomId = getRoomId(session);
        roomSessions.getOrDefault(roomId, List.of()).remove(session);
    }

    private String getRoomId(WebSocketSession session) {
        String uri = session.getUri().toString();
        return uri.substring(uri.lastIndexOf("/") + 1);
    }
}

