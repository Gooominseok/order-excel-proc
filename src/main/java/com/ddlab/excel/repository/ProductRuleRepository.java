package com.ddlab.excel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddlab.excel.domain.ProductRule;

@Repository
public interface ProductRuleRepository extends JpaRepository<ProductRule, Long>{

	// 특정 플랫폼의 규칙만 다 가져오기 
	List<ProductRule> findByPlatformId(Long platformId);
}
