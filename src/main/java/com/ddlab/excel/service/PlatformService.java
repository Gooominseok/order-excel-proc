package com.ddlab.excel.service;

import com.ddlab.excel.domain.*;
import com.ddlab.excel.dto.PlatformDto;
import com.ddlab.excel.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlatformService {

    private final PlatformRepository platformRepository;

    // 1. 목록 조회
    @Transactional(readOnly = true)
    public List<Platform> findAll() {
        return platformRepository.findAll();
    }

    // 2. 상세 조회 (Entity -> DTO 변환)
    @Transactional(readOnly = true)
    public PlatformDto getPlatformDto(String name) {
        Platform p = platformRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플랫폼: " + name));

        // 컬럼 매핑 변환
        Map<String, String> colMap = new HashMap<>();
        if (p.getColumnMapping() != null) {
            colMap.put("orderNo", p.getColumnMapping().getOrderNo());
            colMap.put("qty", p.getColumnMapping().getQty());
            colMap.put("receiver", p.getColumnMapping().getReceiver());
            colMap.put("message", p.getColumnMapping().getMessage());
            colMap.put("address", p.getColumnMapping().getAddress());
            colMap.put("productName", p.getColumnMapping().getProductName());
            colMap.put("productOption", p.getColumnMapping().getProductOption());
            
            // ★ 추가된 컬럼들 (연락처, 가격)
            colMap.put("contact1", p.getColumnMapping().getContact1());
            colMap.put("orderDate", p.getColumnMapping().getOrderDate());
        }

        // 상품 규칙 변환 (색상 포함)
        List<PlatformDto.ProductRuleDto> pRules = p.getProductRules().stream()
                .map(r -> new PlatformDto.ProductRuleDto(
                        r.getTargetProductName(), 
                        r.getFinalProductName(),
                        r.getPriority(), 
                        r.getQtyGroupId(), 
                        r.getGiveawayRuleId(),
                        r.getHighlightColor(), // ★ 추가됨
                        r.getPrice()
                ))
                .collect(Collectors.toList());

        // 증정 규칙 변환
        List<PlatformDto.GiveawayRuleDto> gRules = p.getGiveawayRules().stream()
                .map(r -> new PlatformDto.GiveawayRuleDto(
                        r.getRuleId(), r.getConditionValue(), 
                        r.getGiftQty(),
                        r.getGiftName()))
                .collect(Collectors.toList());

        // 금액별 사은품 규칙 변환
        List<PlatformDto.PriceGiftRuleDto> prRules = p.getPriceGiftRules().stream()
                .map(r -> new PlatformDto.PriceGiftRuleDto(
                        r.getMinAmount(), r.getMaxAmount(), r.getGiftName()))
                .collect(Collectors.toList());

        return PlatformDto.builder()
                .id(p.getId())
                .name(p.getName())
                .filenamePrefix(p.getFilenamePrefix())
                .excelPassword(p.getExcelPassword())
                .headerRowIndex(p.getHeaderRowIndex())
                .columnMapping(colMap)
                .productRules(pRules)
                .giveawayRules(gRules)
                .priceGiftRules(prRules)
                .build();
    }

    // 3. 저장 및 수정 (DTO -> Entity 변환)
    public Long savePlatform(PlatformDto dto) {
        Platform platform = platformRepository.findByName(dto.getName())
                .orElse(new Platform());

        platform.setName(dto.getName());
        platform.setFilenamePrefix(dto.getFilenamePrefix());
        platform.setExcelPassword(dto.getExcelPassword());
        platform.setHeaderRowIndex(dto.getHeaderRowIndex());

        // 컬럼 매핑 저장
        if (dto.getColumnMapping() != null) {
            ColumnMapping cm = platform.getColumnMapping();
            if (cm == null) {
            	cm = new ColumnMapping();
            	cm.setPlatform(platform);
            }
            
            Map<String, String> map = dto.getColumnMapping();
            cm.setOrderNo(map.get("orderNo"));
            cm.setQty(map.get("qty"));
            cm.setReceiver(map.get("receiver"));
            cm.setMessage(map.get("message"));
            cm.setAddress(map.get("address"));
            cm.setProductName(map.get("productName"));
            cm.setProductOption(map.get("productOption"));
            
            // ★ 추가된 컬럼 저장
            cm.setContact1(map.get("contact1"));
            cm.setOrderDate(map.get("orderDate"));
            
            platform.setColumnMapping(cm);
        }

        // 상품 규칙 저장 (색상 포함)
        platform.getProductRules().clear();
        if (dto.getProductRules() != null) {
            for (PlatformDto.ProductRuleDto r : dto.getProductRules()) {
                ProductRule entity = new ProductRule();
                entity.setPlatform(platform); // 부모 연결
                entity.setTargetProductName(r.getTargetProductName());
                entity.setFinalProductName(r.getFinalProductName());
                entity.setPriority(r.getPriority());
                entity.setQtyGroupId(r.getQtyGroupId());
                entity.setGiveawayRuleId(r.getGiveawayRuleId());
                entity.setHighlightColor(r.getHighlightColor());
                entity.setPrice(r.getPrice()); // ★ 화면에서 넘어온 단가(price) DB에 저장!
                platform.addProductRule(entity);
            }
        }
        // 증정 규칙 저장
        platform.getGiveawayRules().clear();
        if (dto.getGiveawayRules() != null) {
            for (PlatformDto.GiveawayRuleDto r : dto.getGiveawayRules()) {
                GiveawayRule entity = new GiveawayRule();
                entity.setRuleId(r.getRuleId());
                entity.setConditionValue(r.getConditionValue());
                entity.setGiftQty(r.getGiftQty());
                entity.setGiftName(r.getGiftName());
                platform.addGiveawayRule(entity);
            }
        }

        // 금액별 사은품 규칙 저장
        platform.getPriceGiftRules().clear();
        if (dto.getPriceGiftRules() != null) {
            for (PlatformDto.PriceGiftRuleDto r : dto.getPriceGiftRules()) {
                PriceGiftRule entity = new PriceGiftRule();
                entity.setMinAmount(r.getMinAmount());
                entity.setMaxAmount(r.getMaxAmount());
                entity.setGiftName(r.getGiftName());
                platform.addPriceGiftRule(entity);
            }
        }

        platformRepository.save(platform);
        return platform.getId();
    }
    
    // 4. 삭제
    public void deletePlatform(String name) {
        Platform p = platformRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플랫폼"));
        platformRepository.delete(p);
    }
}
