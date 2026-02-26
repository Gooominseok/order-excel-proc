package com.ddlab.excel.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformDto {
    private Long id;                // 수정할 때 필요
    private String name;            // 플랫폼 이름 (NAVER 등)
    private String filenamePrefix;
    private String excelPassword;   // 엑셀 비번
    private int headerRowIndex;     // 헤더 위치

    // 컬럼 매핑 (Map으로 받으면 편함)
    private Map<String, String> columnMapping;

    // 상품 규칙 목록
    private List<ProductRuleDto> productRules;

    // 증정 규칙 목록
    private List<GiveawayRuleDto> giveawayRules;
    
    // 금액별 사은품 규칙 (총 주문금액 N원 이상이면 사은품) -> CSV '사은품' 컬럼
    private List<PriceGiftRuleDto> priceGiftRules;

    // --- 내부 DTO ---
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ProductRuleDto {
        private String targetProductName;
        private String finalProductName;
        private int priority;
        private String qtyGroupId;
        private String giveawayRuleId;
        private String highlightColor;
        private int price;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class GiveawayRuleDto {
        private String ruleId;
        private int conditionValue;
        private int giftQty;
        private String giftName;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PriceGiftRuleDto {
        private int minAmount;   // 최소 금액
        private int maxAmount;   // 최대 금액
        private String giftName; // 사은품 이름
    }
}
