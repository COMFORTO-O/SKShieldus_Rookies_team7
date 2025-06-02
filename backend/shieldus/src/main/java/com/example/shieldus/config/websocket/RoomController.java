package com.example.shieldus.config.websocket;


import com.example.shieldus.config.security.service.MemberUserDetails;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class RoomController {

    public static final Map<String, Room> roomMap = new ConcurrentHashMap<>();

    @GetMapping("/rooms")
    public String roomListPage() {
        return "rooms";
    }

    @GetMapping("/api/rooms")
    @ResponseBody
    public List<Room> roomList() {
        return new ArrayList<>(roomMap.values());
    }

    @PostMapping("/api/rooms")
    @ResponseBody
    public Room createRoom(@RequestBody CreateRoomRequestDto dto, @AuthenticationPrincipal MemberUserDetails userDetails) {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, dto.getName(), userDetails.getUsername());
        roomMap.put(roomId, room);
        return room;
    }

    @Getter @Setter
    public static class CreateRoomRequestDto {
        private String name;
    }
    @GetMapping("/room/{roomId}")
    public String enterRoomPage(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "room"; // templates/room.html
    }
}