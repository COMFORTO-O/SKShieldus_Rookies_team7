package com.example.shieldus.controller;

import com.example.shieldus.controller.dto.CompileRequestDto;
import com.example.shieldus.controller.dto.CompileResponseDto;
import com.example.shieldus.controller.dto.ResponseDto;
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
    public ResponseEntity<ResponseDto<CompileResponseDto>> test(@RequestBody CompileRequestDto requestDto) {
        CompileResponseDto result = compileService.runTest(requestDto);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    @PostMapping("/score")
    public ResponseEntity<ResponseDto<CompileResponseDto>> score(@RequestBody CompileRequestDto requestDto) {
        CompileResponseDto result = compileService.runScore(requestDto);
        return ResponseEntity.ok(ResponseDto.success(result));
    }
}
