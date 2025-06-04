package com.example.shieldus.controller.socket;

import com.example.shieldus.controller.socket.dto.RoomCreateDto;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.socket.Room;
import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.problem.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
            ;

    public static final Map<String, Room> roomMap = new ConcurrentHashMap<>();
    public static final Map<String, String> userToRoomId = new ConcurrentHashMap<>();
    @PostMapping
    public Room createRoom(@RequestBody RoomCreateDto roomCreateDto , @AuthenticationPrincipal MemberUserDetails userDetails) {
        Long problemId  = roomCreateDto.getProblemId();
        String langauge = roomCreateDto.getLanguage();
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 problemId의 문제가 존재하지 않습니다."));

        String problemTitle = problem.getTitle();
        String roomId = UUID.randomUUID().toString();

        String title = problemTitle+"//"+langauge+"//"+userDetails.getUsername();

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