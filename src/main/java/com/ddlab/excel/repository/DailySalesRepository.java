package com.ddlab.excel.repository;

import com.ddlab.excel.domain.DailySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DailySalesRepository extends JpaRepository<DailySales, Long> {
    
	Optional<DailySales> findBySaleDateAndProductName(String saleDate, String productName);

    // ★ 쿼리 변경: 상품명뿐만 아니라 '주문일자(saleDate)' 별로 그룹을 묶어서 가져옵니다.
    @Query("SELECT d.productName, d.saleDate, SUM(d.quantity) as qty FROM DailySales d " +
           "WHERE d.saleDate >= :startDate AND d.saleDate <= :endDate " +
           "GROUP BY d.productName, d.saleDate " +
           "ORDER BY d.saleDate ASC")
    List<Object[]> findDailySalesInRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
}