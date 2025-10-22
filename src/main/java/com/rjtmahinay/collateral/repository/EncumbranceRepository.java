package com.rjtmahinay.collateral.repository;

import com.rjtmahinay.collateral.model.Encumbrance;
import com.rjtmahinay.collateral.model.EncumbranceStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface EncumbranceRepository extends R2dbcRepository<Encumbrance, Long> {

    Mono<Encumbrance> findByEncumbranceId(String encumbranceId);

    Flux<Encumbrance> findByCollateralId(String collateralId);

    Flux<Encumbrance> findByLoanId(String loanId);

    Flux<Encumbrance> findByCustomerId(String customerId);

    Flux<Encumbrance> findByStatus(EncumbranceStatus status);

    @Query("SELECT * FROM encumbrance WHERE collateral_id = :collateralId AND status = 'ACTIVE'")
    Flux<Encumbrance> findActiveEncumbrancesByCollateralId(@Param("collateralId") String collateralId);

    @Query("SELECT * FROM encumbrance WHERE expiry_date < :currentDate AND status = 'ACTIVE'")
    Flux<Encumbrance> findExpiredEncumbrances(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM encumbrance WHERE collateral_id = :collateralId AND status = 'ACTIVE'")
    Mono<BigDecimal> getTotalEncumberedAmountByCollateralId(@Param("collateralId") String collateralId);

    @Query("UPDATE encumbrance SET status = 'RELEASED', updated_at = CURRENT_TIMESTAMP, updated_by = :releasedBy WHERE encumbrance_id = :encumbranceId")
    Mono<Integer> releaseEncumbranceById(@Param("encumbranceId") String encumbranceId,
            @Param("releasedBy") String releasedBy);

    @Query("UPDATE encumbrance SET amount = amount - :releaseAmount, updated_at = CURRENT_TIMESTAMP, updated_by = :releasedBy WHERE encumbrance_id = :encumbranceId")
    Mono<Integer> partiallyReleaseEncumbrance(@Param("encumbranceId") String encumbranceId,
            @Param("releaseAmount") BigDecimal releaseAmount, @Param("releasedBy") String releasedBy);

    @Query("UPDATE encumbrance SET status = 'EXPIRED', updated_at = CURRENT_TIMESTAMP WHERE expiry_date < :currentDate AND status = 'ACTIVE'")
    Mono<Integer> expireEncumbrances(@Param("currentDate") LocalDateTime currentDate);

    Mono<Void> deleteByEncumbranceId(String encumbranceId);
}
