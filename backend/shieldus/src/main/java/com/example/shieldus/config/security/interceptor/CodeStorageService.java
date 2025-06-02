package com.example.shieldus.config.security.interceptor;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class CodeStorageService {
    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    public void saveCode(String roomId, String code) {
        codeMap.put(roomId, code);
    }

    public String getCode(String roomId) {
        return codeMap.getOrDefault(roomId, "// 여기에 코드를 작성하세요");
    }
}