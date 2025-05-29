package com.example.shieldus.controller.SocketTest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestPageController {

    @GetMapping("/socket-test")
    public String socketTestPage() {
        return "socket-test";  // resources/templates/socket-test.html
    }
    @GetMapping("/document-sync")
    public String docSyncPage() {
        return "document-sync";
    }
}
