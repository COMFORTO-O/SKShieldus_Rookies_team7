package com.example.shieldus.config.security.interceptor;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final MemberRepository memberRepository;

    public static final Map<String, Room> roomMap = new ConcurrentHashMap<>();
    public static final Map<String, String> userToRoomId = new ConcurrentHashMap<>();
    @PostMapping
    public Room createRoom(@RequestBody Map<String, String> body ,@AuthenticationPrincipal MemberUserDetails userDetails) {
        String title = body.get("name");
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, title);
        Optional<Member> member = memberRepository.findByEmail(userDetails.getUsername());
        room.setOwner(member.get());

        roomMap.put(roomId, room);
        return room;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return new ArrayList<>(roomMap.values());
    }


}