package com.example.shieldus.config.websocket;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class RoomController {
    private final Map<String, Room> roomMap = new ConcurrentHashMap<>();

    @GetMapping("/rooms")
    public String roomListPage() {
        return "rooms";
    }
    @Getter
    @Setter
    public static class CreateRoomRequestDto{
        String name;
    }
    @PostMapping("/api/rooms")
    @ResponseBody
    public Room createRoom(@RequestBody CreateRoomRequestDto createRoomRequestDto,@AuthenticationPrincipal UserDetails userDetails) {
        String roomId = UUID.randomUUID().toString();
        System.out.println("testests"+userDetails.getUsername());
        Room room = new Room(roomId, createRoomRequestDto.getName(), userDetails.getUsername());
        roomMap.put(roomId, room);
        return room;
    }

    @GetMapping("/api/rooms")
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