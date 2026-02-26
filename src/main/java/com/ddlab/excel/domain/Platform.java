package com.ddlab.excel.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tb_platform")
public class Platform {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String name;	//쇼핑몰 이름
	private String filenamePrefix;
	private String excelPassword;	//엑셀 암호
	private int headerRowIndex;	//헤더 위치 
	
	//연관관계(자식) 
	
	@OneToOne(mappedBy = "platform", cascade = CascadeType.ALL,orphanRemoval = true)
	private ColumnMapping columnMapping;
	
	@OneToMany(mappedBy = "platform", cascade = CascadeType.ALL,orphanRemoval = true)
	private List<ProductRule> productRules = new ArrayList<>();
	
	@OneToMany(mappedBy = "platform", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<GiveawayRule> giveawayRules = new ArrayList<>();

	@OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceGiftRule> priceGiftRules = new ArrayList<>();
    
    public void addProductRule(ProductRule rule) {
        this.productRules.add(rule);
        rule.setPlatform(this); // 자식에게 부모 설정
    }

    public void addGiveawayRule(GiveawayRule rule) {
        this.giveawayRules.add(rule);
        rule.setPlatform(this); // 자식에게 부모 설정
    }
    
    public void addPriceGiftRule(PriceGiftRule rule) {
        this.priceGiftRules.add(rule);
        rule.setPlatform(this);
    }
}
