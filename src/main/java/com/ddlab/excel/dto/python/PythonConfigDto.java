package com.ddlab.excel.dto.python;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PythonConfigDto {

	// @JsonProperty: Java 변수명은 camelCase지만, JSON으로 나갈 땐 snake_case로 나감
	
	@JsonProperty("platform_name")
	private String platformName;
	
	@JsonProperty("filename_prefix")
	private String filenamePrefix;
	
	@JsonProperty("excel_password")
	private String excelPassword;
	
	@JsonProperty("header_row_index")
	private int headerRowIndex;
	
	@JsonProperty("col_map")
	private Map<String, String> colMap;
	
	@JsonProperty("product_rules")
	private List<ProductRuleDto> productRules;
	
	@JsonProperty("giveaway_rules")
	private List<GiveawayRuleDto> giveawayRules;
	
	@JsonProperty("price_gift_rules")
    private List<PriceGiftRuleDto> priceGiftRules;

    @JsonProperty("highlight_rules")
    private List<HighlightRuleDto> highlightRules;
    
    // --- 내부 클래스들 (규칙 DTO) ---
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRuleDto{
    	@JsonProperty("product_name") private String productName;
    	@JsonProperty("final_product_name") private String finalProductName;
    	private int priority;
    	@JsonProperty("qty_group_id") private String qtyGroupId;
    	@JsonProperty("giveaway_rule_id") private String giveawayRuleId;
    	@JsonProperty("highlight_color")private String highlightColor;
    	@JsonProperty("price") private int price;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiveawayRuleDto{
    	@JsonProperty("rule_id") private String ruleId;
    	@JsonProperty("condition_value") private int conditionValue;
    	@JsonProperty("gift_qty") private int giftQty;
    	@JsonProperty("gift_name") private String giftName;
    	@JsonProperty("unit_text") private String unitText;
    }
    
    // (필요하면 PriceGiftRuleDto, HighlightRuleDto도 같은 방식으로 추가)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceGiftRuleDto{
    	@JsonProperty("min_amount") private int minAmount;
    	@JsonProperty("max_amount") private int maxAmount;
    	@JsonProperty("gift_name") private String giftName;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HighlightRuleDto{
    	@JsonProperty("trigger_group_id") private String triggerGroupId;
    	@JsonProperty("target_group_id") private String targetGroupId;
    	private String color;
    }
}
