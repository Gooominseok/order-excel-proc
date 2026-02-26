package com.ddlab.excel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddlab.excel.domain.GiveawayRule;

@Repository
public interface GiveawayRuleRepository extends JpaRepository<GiveawayRule, Long>{

	List<GiveawayRule> findByPlatformId(Long platformId);
}
