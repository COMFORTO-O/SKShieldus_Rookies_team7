package com.example.shieldus.config.security.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompDisconnectEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final Map<String, Room> roomMap = RoomController.roomMap; // 기존 map 그대로 사용
    private final Map<String, String> userToRoomId = RoomController.userToRoomId; // static 맵 공유

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() == null) return;

        String username = accessor.getUser().getName();
        String roomId = RoomController.userToRoomId.remove(username);
        if (roomId == null) return;

        Room room = RoomController.roomMap.get(roomId);
        if (room == null) return;

        // ✅ 방장일 경우: 방 자체 삭제
        if (room.getOwner().getEmail().equals(username)) {
            RoomController.roomMap.remove(roomId);
            System.out.println("방장이 나가서 방 삭제됨: " + roomId);

            // ✅ (선택) 남아 있는 유저들에게 알림
            messagingTemplate.convertAndSend(
                    "/topic/roomDeleted." + roomId,
                    "이 방은 방장이 퇴장하여 종료되었습니다."
            );

            return;
        }

        // ✅ 일반 유저일 경우: 유저만 제거
        room.getMemberRoles().remove(username);
        Map<String, Object> payload = new HashMap<>();
        payload.put("members", room.getMemberRoles());
        payload.put("owner", room.getOwner().getEmail());
        messagingTemplate.convertAndSend("/topic/members." + roomId, payload);
    }
}