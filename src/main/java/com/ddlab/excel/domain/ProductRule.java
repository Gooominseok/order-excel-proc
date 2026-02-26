package com.ddlab.excel.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tb_product_rule")
public class ProductRule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "platform_id")
	@JsonIgnore
	private Platform platform;
	
	private String targetProductName;	// 엑셀에 적힌 상품명 (조건)
	private String finalProductName;
	private int priority;				// 우선순위
	private String qtyGroupId;			// 수량 합산 그룹 ID
	private String giveawayRuleId;		// 적용할 증정 규칙 ID
	private String highlightColor;
	private int price;
}
