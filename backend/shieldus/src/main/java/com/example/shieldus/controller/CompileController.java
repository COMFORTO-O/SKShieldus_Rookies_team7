package com.example.shieldus.controller;

import com.example.shieldus.controller.dto.CompileRequestDto;
import com.example.shieldus.controller.dto.CompileResponseDto;
import com.example.shieldus.service.CompileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/compile")
public class CompileController {

    private final CompileService compileService;

    @PostMapping("/test")
    public ResponseEntity<CompileResponseDto> test(@RequestBody CompileRequestDto requestDto) {
        return ResponseEntity.ok(compileService.runTest(requestDto));
    }

    @PostMapping("/score")
    public ResponseEntity<CompileResponseDto> score(@RequestBody CompileRequestDto requestDto) {
        return ResponseEntity.ok(compileService.runScore(requestDto));
    }
}
