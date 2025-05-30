package com.example.shieldus.config.websocket;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.repository.problem.ProblemRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@RequiredArgsConstructor
@Controller
public class RoomController {
    @Autowired
    ProblemRepository problemRepository;

    private final RoomWebSocketHandler roomWebSocketHandler;
    public static final Map<String, Room> roomMap = new ConcurrentHashMap<>();

    @GetMapping("/rooms")
    public String roomListPage() {
        return "rooms";
    }
    @Getter
    @Setter
    public static class CreateRoomRequestDto{
        Long problemId;
    }
    @PostMapping("/api/rooms") // 방생성
    @ResponseBody
    public Room createRoom(@RequestBody CreateRoomRequestDto createRoomRequestDto,
                           @AuthenticationPrincipal MemberUserDetails userDetails) {

        Long problemId = createRoomRequestDto.getProblemId();
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문제를 찾을 수 없습니다."));

        String username = userDetails.getUsername();
        String title = problem.getTitle() + " : " + username;

        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, title,problemId, username);
        roomMap.put(roomId, room);
        System.out.println("생성된 방의 권한 목록: " + room.getMemberRoles());
        return room;
    }

    @GetMapping("/api/rooms") // 방 조회
    @ResponseBody
    public Collection<Room> getAllRooms() {
        return roomMap.values();
    }

    @GetMapping("/room/{roomId}") //방 입장 (TEST용도)
    public String roomView(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "room";
    }
    @Getter
    @AllArgsConstructor
    public class MemberInfo {
        private String username;
        private RoomRole role;
    }

    @GetMapping("/api/room/{roomId}/members") //입장 시 방 인원 및 권한 조회
    @ResponseBody
    public List<MemberInfo> getRoomMembers(@PathVariable String roomId) {
        Room room = roomMap.get(roomId);
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 방이 존재하지 않습니다");
        }

        // Map<String, RoomRole> → List<MemberInfo>
        return room.getMemberRoles().entrySet().stream()
                .map(entry -> new MemberInfo(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Getter
    public static class RoleChangeRequest {
        private String username;
        private RoomRole role;
    }

    @PostMapping("/api/room/{roomId}/role") // 권한 변경
    @ResponseBody
    public ResponseEntity<String> changeRole(
            @PathVariable String roomId,
            @RequestBody RoleChangeRequest request,
            @AuthenticationPrincipal MemberUserDetails userDetails) {

        Room room = roomMap.get(roomId);
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("방이 존재하지 않습니다.");
        }

        String requester = userDetails.getUsername();

        if (!room.getOwner().equals(requester)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 방 소유자만 변경 가능");
        }

        room.getMemberRoles().put(request.getUsername(), request.getRole());

        // ✅ WebSocket으로 role_changed 브로드캐스트
        roomWebSocketHandler.broadcastRoleChange(roomId, request.getUsername(), request.getRole());

        return ResponseEntity.ok("권한 변경 완료");
    }
}