package com.ddlab.excel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddlab.excel.domain.Platform;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long>{

	// "이름으로 찾기" 기능 자동 생성
	Optional<Platform> findByName(String name);
}
