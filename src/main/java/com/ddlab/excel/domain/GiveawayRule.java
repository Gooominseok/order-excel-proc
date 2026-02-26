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
@Table(name = "tb_giveaway_rule")
public class GiveawayRule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "platform_id")
	@JsonIgnore
	private Platform platform;
	
	private String ruleId;			// 규칙 식별자 (ProductRule과 연결용)
	private int conditionValue;		// 기준 수량 (예: 15개마다)
	private int giftQty;			// 사은품 개
	private String giftName;		// 사은품 이름
}
