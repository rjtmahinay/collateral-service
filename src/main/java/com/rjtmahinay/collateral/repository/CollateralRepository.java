package com.rjtmahinay.collateral.repository;

import com.rjtmahinay.collateral.model.Collateral;
import com.rjtmahinay.collateral.model.CollateralStatus;
import com.rjtmahinay.collateral.model.CollateralType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CollateralRepository extends R2dbcRepository<Collateral, Long> {

    Mono<Collateral> findByCollateralId(String collateralId);

    Flux<Collateral> findByCustomerId(String customerId);

    Flux<Collateral> findByAccountId(String accountId);

    Flux<Collateral> findByStatus(CollateralStatus status);

    Flux<Collateral> findByType(CollateralType type);

    @Query("SELECT * FROM collateral WHERE customer_id = :customerId AND status = 'AVAILABLE' AND available_value >= :minValue")
    Flux<Collateral> findAvailableCollateralsByCustomerIdAndMinValue(@Param("customerId") String customerId,
            @Param("minValue") BigDecimal minValue);

    @Query("SELECT * FROM collateral WHERE status = 'ENCUMBERED' OR encumbered_value > 0")
    Flux<Collateral> findEncumberedCollaterals();

    @Query("UPDATE collateral SET market_value = :marketValue, available_value = :marketValue - encumbered_value, updated_at = CURRENT_TIMESTAMP WHERE collateral_id = :collateralId")
    Mono<Integer> updateMarketValueByCollateralId(@Param("collateralId") String collateralId,
            @Param("marketValue") BigDecimal marketValue);

    @Query("UPDATE collateral SET encumbered_value = :encumberedValue, available_value = market_value - :encumberedValue, status = CASE WHEN :encumberedValue > 0 THEN 'ENCUMBERED' ELSE 'AVAILABLE' END, updated_at = CURRENT_TIMESTAMP WHERE collateral_id = :collateralId")
    Mono<Integer> updateEncumberedValueByCollateralId(@Param("collateralId") String collateralId,
            @Param("encumberedValue") BigDecimal encumberedValue);

    Mono<Void> deleteByCollateralId(String collateralId);
}
