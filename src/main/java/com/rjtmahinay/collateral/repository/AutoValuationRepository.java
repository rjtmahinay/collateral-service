package com.rjtmahinay.collateral.repository;

import com.rjtmahinay.collateral.model.AutoValuation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AutoValuationRepository extends ReactiveCrudRepository<AutoValuation, Long> {

    Mono<AutoValuation> findByValuationId(String valuationId);

    Flux<AutoValuation> findByCollateralId(String collateralId);

    Flux<AutoValuation> findByType(String type);

    Flux<AutoValuation> findByLocation(String location);

    Flux<AutoValuation> findByStatus(AutoValuation.ValuationStatus status);

    @Query("SELECT * FROM auto_valuation WHERE collateral_id = :collateralId ORDER BY valuation_date DESC LIMIT 1")
    Mono<AutoValuation> findLatestByCollateralId(String collateralId);

    @Query("SELECT * FROM auto_valuation WHERE type = :type AND location = :location ORDER BY valuation_date DESC")
    Flux<AutoValuation> findByTypeAndLocationOrderByValuationDateDesc(String type, String location);

    @Query("SELECT * FROM auto_valuation WHERE valuation_date >= :fromDate AND valuation_date <= :toDate")
    Flux<AutoValuation> findByValuationDateBetween(java.time.LocalDateTime fromDate, java.time.LocalDateTime toDate);

    @Query("DELETE FROM auto_valuation WHERE collateral_id = :collateralId")
    Mono<Void> deleteByCollateralId(String collateralId);
}
