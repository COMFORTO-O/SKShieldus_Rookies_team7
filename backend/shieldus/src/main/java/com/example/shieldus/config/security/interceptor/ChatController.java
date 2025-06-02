package com.example.shieldus.config.security.interceptor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import lombok.Getter;
import lombok.Setter; // Lombok을 사용한다면 DTO에 @Getter, @Setter 추가

import java.security.Principal; // 현재 인증된 사용자 정보 (STOMP 세션의 accessor.getUser())

@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate; // 특정 사용자에게 메시지 보내기 위함

    // SimpMessageSendingOperations는 Spring이 자동으로 주입해줍니다.
    public ChatController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 클라이언트가 /app/chat.sendMessage 로 메시지를 보낼 때 처리
    // /app/** 경로는 WebSocketConfig에서 .simpDestMatchers("/app/**").authenticated()로 설정했으므로 인증 필요
    @MessageMapping("/chat.sendMessage") // 이 부분이 클라이언트의 destination: '/app/chat.sendMessage'와 매핑됩니다.
    @SendTo("/topic/publicChatRoom") // 이 메시지를 /topic/publicChatRoom을 구독하는 모든 클라이언트에게 전송
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        // Principal 객체는 STOMP 세션에 연결된 인증된 사용자 정보 (username)를 가집니다.
        // JwtWebSocketChannelInterceptor에서 accessor.setUser(authentication)을 통해 설정됩니다.
        chatMessage.setSender(principal.getName()); // 메시지 보낸 사람을 실제 인증된 사용자 이름으로 설정
        chatMessage.setType("CHAT"); // 타입 설정 (React에서 구분용)
        System.out.println("Public message received from " + principal.getName() + ": " + chatMessage.getContent());
        return chatMessage; // 이 객체가 /topic/publicChatRoom으로 전송됩니다.
    }

    // 클라이언트가 /app/chat.sendPrivateMessage 로 메시지를 보낼 때 처리
    // @SendToUser를 사용하면 현재 메시지를 보낸 사용자에게만 응답을 보낼 수 있습니다.
    // 기본적으로 /user/{username}/queue/privateMessages 경로로 메시지가 전송됩니다.
    // 여기서 {username}은 Principal.getName() 값입니다.
    @MessageMapping("/chat.sendPrivateMessage") // 이 부분이 클라이언트의 destination: '/app/chat.sendPrivateMessage'와 매핑됩니다.
    @SendToUser("/queue/privateMessages") // 현재 메시지를 보낸 사용자에게만 전송
    public ChatMessage sendPrivateMessage(@Payload ChatMessage chatMessage, Principal principal) {
        chatMessage.setSender(principal.getName()); // 메시지 보낸 사람을 실제 인증된 사용자 이름으로 설정
        chatMessage.setContent("개인 메시지: " + chatMessage.getContent()); // 개인 메시지임을 표시
        chatMessage.setType("PRIVATE"); // 타입 설정 (React에서 구분용)
        System.out.println("Private message received from " + principal.getName() + ": " + chatMessage.getContent());

        // 참고: 특정 다른 사용자에게 개인 메시지를 보내고 싶다면 (예: admin에게만)
        // messagingTemplate.convertAndSendToUser("admin", "/queue/privateMessages", chatMessage);
        // 이 경우, 클라이언트에서 "/user/admin/queue/privateMessages"를 구독해야 합니다.

        return chatMessage; // 이 객체가 현재 사용자에게만 전송됩니다.
    }
}


