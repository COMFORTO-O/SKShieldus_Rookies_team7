package com.example.shieldus.config.websocket;

import com.example.shieldus.config.jwt.JwtTokenProvider;
import com.example.shieldus.config.security.service.MemberUserDetails;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class RoomWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String roomId = getRoomId(session);
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);

        String token = extractTokenFromQuery(session);
        if (token == null) {
            closeSession(session, "토큰 누락");
            return;
        }

        try {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            String username = auth.getName(); // JWT에서 username 추출

            Room room = RoomController.roomMap.get(roomId);
            if (room != null) {
                room.getMemberRoles().putIfAbsent(username, RoomRole.CHAT_ONLY);
                System.out.println("[" + roomId + "] " + username + " 입장");
                System.out.println("현재 권한 목록: " + room.getMemberRoles());
            } else {
                closeSession(session, "방이 존재하지 않음");
            }
        } catch (Exception e) {
            closeSession(session, "잘못된 토큰");
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String roomId = getRoomId(session);
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());

        String type = jsonNode.get("type").asText();

        Object principal = session.getPrincipal();
        if (!(principal instanceof MemberUserDetails userDetails)) {
            System.out.println("인증되지 않은 사용자 요청 차단");
            return;
        }

        String username = userDetails.getUsername();
        System.out.println(username+"asdfsdf");
        Room room = RoomController.roomMap.get(roomId);
        if (room == null) return;

        RoomRole role = room.getMemberRoles().getOrDefault(username, RoomRole.CHAT_ONLY);

        //  권한 확인
        if (type.equals("code_update") && role != RoomRole.CHAT_AND_EDIT) {
            System.out.println("[" + roomId + "] " + username + "는 코드 수정 권한이 없습니다.");
            return;
        }

        //  브로드캐스트
        for (WebSocketSession s : roomSessions.getOrDefault(roomId, List.of())) {
            if (s.isOpen()) {
                ObjectNode outbound = objectMapper.createObjectNode();
                outbound.put("type", type);
                outbound.set("data", jsonNode);

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
        return uri.substring(uri.lastIndexOf("/") + 1, uri.contains("?") ? uri.indexOf("?") : uri.length());
    }

    private String extractTokenFromQuery(WebSocketSession session) {
        String query = session.getUri().getQuery(); // token=eyJ...
        if (query == null) return null;

        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring("token=".length());
            }
        }
        return null;
    }

    private void closeSession(WebSocketSession session, String reason) {
        try {
            System.out.println("연결 종료: " + reason);
            session.close(CloseStatus.BAD_DATA);
        } catch (IOException ignored) {}
    }

    public void broadcastRoleChange(String roomId, String username, RoomRole role) {
        List<WebSocketSession> sessions = roomSessions.getOrDefault(roomId, List.of());

        ObjectNode message = objectMapper.createObjectNode();
        message.put("type", "role_changed");
        ObjectNode data = objectMapper.createObjectNode();
        data.put("username", username);
        data.put("newRole", role.toString());
        message.set("data", data);

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message.toString()));
                }
            } catch (IOException ignored) {}
        }
    }
}
