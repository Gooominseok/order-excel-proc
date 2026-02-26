package com.ddlab.excel.controller;

import com.ddlab.excel.domain.Platform;
import com.ddlab.excel.dto.PlatformDto;
import com.ddlab.excel.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    // 1. 모든 플랫폼 목록 가져오기 (이름만)
    @GetMapping("/list")
    public ResponseEntity<List<Platform>> getAllPlatforms() {
        return ResponseEntity.ok(platformService.findAll());
    }

    // 2. 특정 플랫폼 상세 정보 가져오기 (규칙 포함)
    @GetMapping("/{name}")
    public ResponseEntity<PlatformDto> getPlatform(@PathVariable("name") String name) {
        return ResponseEntity.ok(platformService.getPlatformDto(name));
    }

    // 3. 플랫폼 저장/수정하기
    @PostMapping
    public ResponseEntity<Long> savePlatform(@RequestBody PlatformDto dto) {
        Long id = platformService.savePlatform(dto);
        return ResponseEntity.ok(id);
    }
    
    // 4. 삭제하기
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deletePlatform(@PathVariable("name") String name) {
        platformService.deletePlatform(name);
        return ResponseEntity.ok("삭제 완료");
    }
}