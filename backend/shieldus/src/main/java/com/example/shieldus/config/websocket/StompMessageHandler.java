package com.example.shieldus.config.websocket;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class StompMessageHandler {

    @MessageMapping("/room/{roomId}/message")
    @SendTo("/topic/room/{roomId}")
    public MessageDto handleMessage(@DestinationVariable String roomId, MessageDto message, Principal principal) {
        message.setSender(principal.getName());
        return message;
    }
}