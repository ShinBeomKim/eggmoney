package com.ssafy.eggmoney.stock.repository;

import com.ssafy.eggmoney.stock.entity.StockPrice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    @Query("select sp from StockPrice sp left join fetch sp.stock s")
    List<StockPrice> findJoinStock(Pageable pageable);

    @Query("select sp from StockPrice sp where sp.stock.id = :stockId and sp.createdAt > :date order by sp.createdAt")
    List<StockPrice> findByStockIdAndDate(@Param("stockId") Long stockId, @Param("date") LocalDateTime date);
}
