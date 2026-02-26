package com.ddlab.excel.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.ddlab.excel.domain.ColumnMapping;
import com.ddlab.excel.domain.DailySales;
import com.ddlab.excel.domain.GiveawayRule;
import com.ddlab.excel.domain.Platform;
import com.ddlab.excel.domain.PriceGiftRule;
import com.ddlab.excel.domain.ProductRule;
import com.ddlab.excel.dto.python.PythonConfigDto;
import com.ddlab.excel.repository.PlatformRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final PlatformRepository platformRepository;
    private final RestTemplate restTemplate; // 파이썬 호출용

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploadFile/";
    private final String PYTHON_URL = "http://localhost:8000/process"; // 파이썬 주소

    private final com.ddlab.excel.repository.DailySalesRepository dailySalesRepository; // ★ 추가!
    
    // ★ 반환 타입이 String이 아니라 byte[] (파일 그 자체)로 변경됨
    public byte[] processExcel(MultipartFile file, String platformName) throws IOException {

        // 1. DB에서 설정 조회
        Platform platform = platformRepository.findByName(platformName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플랫폼: " + platformName));

        // 2. 파일 저장
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) folder.mkdirs();

        String originalName = file.getOriginalFilename();
        String saveName = UUID.randomUUID() + "_" + originalName;
        String savePath = UPLOAD_DIR + saveName;
        file.transferTo(new File(savePath));

        System.out.println("✅ 파일 저장 완료: " + savePath);

        try {
            // 3. Entity -> Python DTO 변환 (메서드 분리)
            PythonConfigDto configDto = convertToPythonDto(platform);

            // 4. 요청 데이터 생성 (JSON Body)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("filePath", savePath);
            requestBody.put("config", configDto);

            // 5. Python 서버 호출
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // POST 요청 보내고 byte[] (파일)로 받기
            ResponseEntity<byte[]> response = restTemplate.postForEntity(PYTHON_URL, entity, byte[].class);

            System.out.println("✅ 파이썬 처리 완료! 데이터 수신함.");
            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("파이썬 서버 처리 중 오류 발생: " + e.getMessage());
        } finally {
            // 6. 임시 파일 삭제 (선택 사항)
            deleteFile(savePath);
        }
    }

    // --- (도우미) Entity를 Python DTO로 변환 ---
 // --- Entity를 Python DTO로 변환하는 메서드 (수정됨) ---
    private PythonConfigDto convertToPythonDto(Platform p) {
        
        // 1. 컬럼 매핑 변환
        Map<String, String> colMap = new HashMap<>();
        if (p.getColumnMapping() != null) {
            ColumnMapping cm = p.getColumnMapping();
            colMap.put("order_no", cm.getOrderNo());
            colMap.put("qty", cm.getQty());
            colMap.put("receiver", cm.getReceiver());
            colMap.put("message", cm.getMessage());
            colMap.put("address", cm.getAddress());
            colMap.put("product_name", cm.getProductName());
            colMap.put("product_option", cm.getProductOption());
            colMap.put("contact1", cm.getContact1());
            colMap.put("order_date", cm.getOrderDate());
        }

        // 2. 상품 규칙 변환 (Stream 대신 for문 사용 -> 에러 해결)
        List<PythonConfigDto.ProductRuleDto> pRules = new ArrayList<>();
        if (p.getProductRules() != null) {
            for (ProductRule r : p.getProductRules()) {
                pRules.add(PythonConfigDto.ProductRuleDto.builder()
                        .productName(r.getTargetProductName())
                        .finalProductName(r.getFinalProductName())
                        .priority(r.getPriority())
                        .qtyGroupId(r.getQtyGroupId())
                        .giveawayRuleId(r.getGiveawayRuleId())
                        .highlightColor(r.getHighlightColor())
                        .price(r.getPrice())
                        .build());
            }
        }

        // 3. 증정 규칙 변환
        List<PythonConfigDto.GiveawayRuleDto> gRules = new ArrayList<>();
        if (p.getGiveawayRules() != null) {
            for (GiveawayRule r : p.getGiveawayRules()) {
                gRules.add(PythonConfigDto.GiveawayRuleDto.builder()
                        .ruleId(r.getRuleId())
                        .conditionValue(r.getConditionValue())
                        .giftQty(r.getGiftQty())
                        .giftName(r.getGiftName())
                        .unitText("개")
                        .build());
            }
        }

     // 4. ★ 금액별 사은품 규칙 (for문 사용 - 추가됨)
        List<PythonConfigDto.PriceGiftRuleDto> prRules = new ArrayList<>();
        for (PriceGiftRule r : p.getPriceGiftRules()) {
            prRules.add(PythonConfigDto.PriceGiftRuleDto.builder()
                    .minAmount(r.getMinAmount())
                    .maxAmount(r.getMaxAmount())
                    .giftName(r.getGiftName())
                    .build());
        }
        
        // 4. 최종 DTO 생성
        return PythonConfigDto.builder()
                .platformName(p.getName())
                .filenamePrefix(p.getFilenamePrefix())
                .headerRowIndex(p.getHeaderRowIndex())
                .excelPassword(p.getExcelPassword())
                .colMap(colMap)
                .productRules(pRules)
                .giveawayRules(gRules)
                .priceGiftRules(prRules) // 필요시 구현
                .highlightRules(new ArrayList<>()) // 필요시 구현
                .build();
    }

    private void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
            System.out.println("🗑️ 임시 파일 삭제 완료");
        } catch (IOException e) {
            System.err.println("파일 삭제 실패: " + e.getMessage());
        }
    }
    
    @org.springframework.transaction.annotation.Transactional
    public void saveSalesToDB(List<MultipartFile> files) throws IOException {
        List<String> savedPaths = new ArrayList<>();
        for (MultipartFile file : files) {
            String savePath = UPLOAD_DIR + UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(savePath));
            savedPaths.add(savePath);
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("filePaths", savedPaths);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "http://localhost:8000/sales/parse",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> list = response.getBody();

            if (list != null) {
                for (Map<String, Object> map : list) {
                    String date = (String) map.get("주문일자");
                    String name = (String) map.get("상품명");
                    int qty = (Integer) map.get("수량");

                    // 이미 DB에 해당 날짜+상품명 기록이 있으면 꺼내오고, 없으면 새로 만듭니다.
                    DailySales ds = dailySalesRepository.findBySaleDateAndProductName(date, name)
                            .orElse(new DailySales());
                    
                    ds.setSaleDate(date);
                    ds.setProductName(name);
                    // ★ 중요: 기존 수량에 더하기 (누적)
                    ds.setQuantity(ds.getQuantity() + qty); 
                    
                    dailySalesRepository.save(ds);
                }
            }
        } finally {
            for (String path : savedPaths) deleteFile(path);
        }
    }

    // 2. 화면에 뿌려줄 통계 데이터 가져오기
    public List<Map<String, Object>> getSalesDashboard(String startDate, String endDate) {
        // 1. DB에서 날짜별 판매량 가져오기
        List<Object[]> rows = dailySalesRepository.findDailySalesInRange(startDate, endDate);
        
        // 데이터 조립을 위한 바구니 준비
        Map<String, Map<String, Integer>> dailyMap = new HashMap<>(); // 상품별 일일 데이터
        Map<String, Integer> totalMap = new HashMap<>();              // 상품별 총 판매량
        
        // 2. 데이터 분류하기
        for (Object[] row : rows) {
            String name = (String) row[0];
            String date = (String) row[1];
            int qty = ((Number) row[2]).intValue();
            
            // 총 판매량 누적
            totalMap.put(name, totalMap.getOrDefault(name, 0) + qty);
            
            // 일별 판매량 기록
            dailyMap.putIfAbsent(name, new HashMap<>());
            dailyMap.get(name).put(date, qty);
        }
        
        // 3. 총 판매량이 많은 순서대로 정렬하기
        List<String> sortedNames = new ArrayList<>(totalMap.keySet());
        sortedNames.sort(String::compareTo); // 내림차순
        
        // 4. 화면으로 보낼 최종 결과물 만들기
        List<Map<String, Object>> result = new ArrayList<>();
        for (String name : sortedNames) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("totalQty", totalMap.get(name));
            map.put("daily", dailyMap.get(name)); // { "2025-09-01": 10, "2025-09-02": 5 ... }
            result.add(map);
        }
        
        return result;
    }
}
