package com.example.shieldus.config.security.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final CodeStorageService codeStorageService;
    private final ChatStorageService chatStorageService;

    // ✅ 방 ID 기반으로 메시지를 해당 방의 채팅방으로 전송
    @MessageMapping("/chat.sendMessage.{roomId}")
    public void sendRoomMessage(@DestinationVariable String roomId,
                                @Payload ChatMessage chatMessage,
                                Principal principal) {
        chatMessage.setSender(principal.getName());
        chatMessage.setType("CHAT");
        System.out.println("[" + roomId + "] " + principal.getName() + ": " + chatMessage.getContent());
        chatStorageService.saveMessage(roomId, chatMessage);
        // ✅ 해당 방 구독자에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chatroom." + roomId, chatMessage);
    }
    @MessageMapping("/code.update.{roomId}")
    public void updateCode(@DestinationVariable String roomId,
                           @Payload CodeMessage codeMessage) {
        codeStorageService.saveCode(roomId, codeMessage.getCode());
        messagingTemplate.convertAndSend("/topic/code." + roomId, codeMessage);
    }

    @MessageMapping("/code.latest.{roomId}")
    public void sendLatestCode(@DestinationVariable String roomId,
                               Principal principal) {
        String latestCode = codeStorageService.getCode(roomId);
        CodeMessage codeMessage = new CodeMessage();
        codeMessage.setCode(latestCode);
        System.out.println("code latest 호출");
        messagingTemplate.convertAndSendToUser(
                principal.getName(), "/queue/initCode", codeMessage);
    }
    @MessageMapping("/chat.history.{roomId}")
    public void sendChatHistory(@DestinationVariable String roomId, Principal principal) {
        List<ChatMessage> messages = chatStorageService.getMessages(roomId);
        System.out.println("chat history 호출");
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/chatHistory",
                messages // List<ChatMessage>
        );
    }
    @MessageMapping("/room.enter.{roomId}")
    public void enterRoom(@DestinationVariable String roomId, Principal principal) {
        String username = principal.getName(); // 로그인한 사용자 식별

        Room room = RoomController.roomMap.get(roomId);
        if (room == null) {
            System.out.println("존재하지 않는 방입니다: " + roomId);
            return;
        }

        // owner이면 아무것도 안함 (또는 CHAT_AND_EDIT 부여)
        if (room.getOwner().getEmail().equals(username)) {
            room.getMemberRoles().put(username, RoomRole.CHAT_AND_EDIT);
            System.out.println("방장 입장: " + username);
        } else {
            // 기본적으로 CHAT_ONLY 권한 부여
            room.getMemberRoles().putIfAbsent(username, RoomRole.CHAT_ONLY);
            System.out.println("참여자 입장: " + username + " → CHAT_ONLY");
        }

        // ✅ 유저 목록 + 권한 정보 전송
        messagingTemplate.convertAndSend(
                "/topic/members." + roomId,
                room.getMemberRoles()); // Map<String, RoomRole>
    }


}
