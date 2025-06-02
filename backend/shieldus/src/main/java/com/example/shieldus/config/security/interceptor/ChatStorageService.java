package com.example.shieldus.config.security.interceptor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatStorageService {
    private final Map<String, List<ChatMessage>> chatMap = new ConcurrentHashMap<>();

    public void saveMessage(String roomId, ChatMessage msg) {
        chatMap.computeIfAbsent(roomId, k -> new ArrayList<>()).add(msg);
    }

    public List<ChatMessage> getMessages(String roomId) {
        return chatMap.getOrDefault(roomId, new ArrayList<>());
    }
}
