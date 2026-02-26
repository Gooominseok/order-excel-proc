package com.ddlab.excel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddlab.excel.domain.ColumnMapping;

@Repository
public interface ColumnMappingRepository extends JpaRepository<ColumnMapping, Long>{

	Optional<ColumnMapping> findByPlatformId(Long platformId);
}
