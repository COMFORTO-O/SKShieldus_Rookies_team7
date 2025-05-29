package com.example.shieldus.config.websocket;

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

    @PostMapping("/api/rooms")
    @ResponseBody
    public Room createRoom(@RequestParam String name) {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, name);
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