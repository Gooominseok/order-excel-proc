package com.ddlab.excel.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tb_column_mapping")
public class ColumnMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "platform_id")
	@JsonIgnore
	private Platform platform;
	
	// 엑셀 헤더명 저장 
	private String orderNo;			// 주문번호 
	private String receiver;		//수취인 
	private String qty;				//수량 
	private String message;			//배송메시지 
	private String address;			//주소 
	private String productName;		//상품명 
	private String productOption;	//옵션 
	private String contact1;      // 수취인연락처1
    private String orderDate;		// 주문일
}
