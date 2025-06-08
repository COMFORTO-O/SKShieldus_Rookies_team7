package com.example.shieldus.controller.socket;

import com.example.shieldus.controller.socket.dto.ChatMessage;
import com.example.shieldus.controller.socket.dto.CodeMessage;
import com.example.shieldus.controller.socket.dto.RoleChangeRequest;
import com.example.shieldus.entity.socket.Room;
import com.example.shieldus.entity.socket.enumration.RoomRole;
import com.example.shieldus.service.socket.ChatStorageService;
import com.example.shieldus.service.socket.CodeStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final CodeStorageService codeStorageService;
    private final ChatStorageService chatStorageService;

    //  방 ID 기반으로 메시지를 해당 방의 채팅방으로 전송
    @MessageMapping("/chat.sendMessage.{roomId}")
    public void sendRoomMessage(@DestinationVariable String roomId,
                                @Payload ChatMessage chatMessage,
                                Principal principal) {
        chatMessage.setSender(principal.getName());
        chatMessage.setType("CHAT");
        System.out.println("[" + roomId + "] " + principal.getName() + ": " + chatMessage.getContent());
        chatStorageService.saveMessage(roomId, chatMessage);
        //  해당 방 구독자에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chatroom." + roomId, chatMessage);
    }
    // 코드 업데이트 시 방으로 전송
    @MessageMapping("/code.update.{roomId}")
    public void updateCode(@DestinationVariable String roomId,
                           @Payload CodeMessage codeMessage,
                           Principal principal) {
        codeMessage.setSender(principal.getName());
        codeStorageService.saveCode(roomId, codeMessage.getCode());
        messagingTemplate.convertAndSend("/topic/code." + roomId, codeMessage);

    }
    //최신 코드 받아오기
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

        RoomController.userToRoomId.put(username, roomId);

        // owner이면 아무것도 안함 (또는 CHAT_AND_EDIT 부여)
        if (room.getOwner().getEmail().equals(username)) {
            room.getMemberRoles().put(username, RoomRole.CHAT_AND_EDIT);
            System.out.println("방장 입장: " + username);
        } else {
            // 기본적으로 CHAT_ONLY 권한 부여
            room.getMemberRoles().putIfAbsent(username, RoomRole.CHAT_ONLY);
            System.out.println("참여자 입장: " + username + " → CHAT_ONLY");
        }

        // 유저 목록 + 권한 정보 전송
        Map<String, Object> payload = new HashMap<>();
        payload.put("members", room.getMemberRoles());
        payload.put("owner", room.getOwner().getEmail());
        messagingTemplate.convertAndSend("/topic/members." + roomId, payload);
    }

    @MessageMapping("/room.changeRole.{roomId}")
    public void changeRole(@DestinationVariable String roomId,
                           @Payload RoleChangeRequest request,
                           Principal principal) {
        String requester = principal.getName();
        Room room = RoomController.roomMap.get(roomId);
        if (room == null) return;

        // 방장만 변경 가능
        if (!room.getOwner().getEmail().equals(requester)) {
            System.out.println("권한 변경 시도 실패 (방장 아님): " + requester);
            return;
        }

        // 대상 유저가 존재해야 함
        if (!room.getMemberRoles().containsKey(request.getTargetUsername())) {
            System.out.println("대상 유저가 존재하지 않음: " + request.getTargetUsername());
            return;
        }

        // 권한 변경
        room.getMemberRoles().put(request.getTargetUsername(), request.getNewRole());
        System.out.println("권한 변경됨: " + request.getTargetUsername() + " → " + request.getNewRole());

        // 전체 유저에게 권한 목록 broadcast
        Map<String, Object> payload = new HashMap<>();
        payload.put("members", room.getMemberRoles());
        payload.put("owner", room.getOwner().getEmail());
        messagingTemplate.convertAndSend("/topic/members." + roomId, payload);
    }

    @MessageMapping("/room.kick.{roomId}")
    public void kickUser(@DestinationVariable String roomId,
                         @Payload Map<String, String> payload,
                         Principal principal) {
        String targetUsername = payload.get("targetUsername");
        String owner = principal.getName();

        Room room = RoomController.roomMap.get(roomId);
        if (room == null || !room.getOwner().getEmail().equals(owner)) {
            System.out.println("방장 아님");
            return;
        }

        //강제 퇴장 메세지 전송
        messagingTemplate.convertAndSendToUser(
                targetUsername,
                "/queue/kick",
                Map.of(
                        "message", "방장에 의해 강제 퇴장되었습니다."
                )
        );

        System.out.println("강제 퇴장됨: " + targetUsername);
        System.out.println("참여자 목록: " + room.getMemberRoles());
    }

}
