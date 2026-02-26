package com.ddlab.excel.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ddlab.excel.service.ExcelService;
import com.ddlab.excel.service.PlatformService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

	private final ExcelService excelService;
	private final PlatformService platformService;
	
	@PostMapping("/upload")
    public ResponseEntity<byte[]> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("platformName") String platformName
    ) {
    	try {
            // 1. 서비스 호출 (파일 처리)
            byte[] resultFile = excelService.processExcel(file, platformName);

            // ★ 2. DB에서 사장님이 저장한 접두어를 직접 꺼내옵니다.
            String prefix = platformService.getPlatformDto(platformName).getFilenamePrefix();
            
            // 만약 접두어가 비어있으면 기본값 "배송정리" 사용
            if (prefix == null || prefix.trim().isEmpty()) {
                prefix = "배송정리";
            }

            // 3. 파일 다운로드 응답 헤더 설정
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HH"));
            
            // ★ 4. 이제 platformName 대신 사장님이 설정한 prefix를 사용합니다!
            String fileName = prefix + "_" + timestamp + ".xlsx";
            
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resultFile);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
		}
	}
    
    @PostMapping("/sales/upload")
    public ResponseEntity<String> uploadSalesDB(@RequestParam("files") List<MultipartFile> files) {
        try {
            excelService.saveSalesToDB(files);
            return ResponseEntity.ok("DB 저장 완료!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/sales/data")
    public ResponseEntity<List<Map<String, Object>>> getSalesData(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        return ResponseEntity.ok(excelService.getSalesDashboard(startDate, endDate));
    }
	
}
