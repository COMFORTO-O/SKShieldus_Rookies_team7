package com.example.shieldus.config.websocket;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.repository.problem.ProblemRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class RoomController {
    @Autowired
    ProblemRepository problemRepository;
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
        Room room = new Room(roomId, title, username);
        roomMap.put(roomId, room);
        System.out.println("생성된 방의 권한 목록: " + room.getMemberRoles());
        return room;
    }

    @GetMapping("/api/rooms") // 방 조회
    @ResponseBody
    public Collection<Room> getAllRooms() {
        return roomMap.values();
    }

    @GetMapping("/room/{roomId}")
    public String roomView(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "room";
    }
}