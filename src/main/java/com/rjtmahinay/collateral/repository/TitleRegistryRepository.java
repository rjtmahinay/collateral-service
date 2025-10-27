package com.rjtmahinay.collateral.repository;

import com.rjtmahinay.collateral.model.TitleRegistry;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TitleRegistryRepository extends ReactiveCrudRepository<TitleRegistry, Long> {

    Mono<TitleRegistry> findByTitleId(String titleId);

    Mono<TitleRegistry> findByTitleNumber(String titleNumber);

    Flux<TitleRegistry> findByCollateralId(String collateralId);

    Flux<TitleRegistry> findByCurrentOwner(String currentOwner);

    Flux<TitleRegistry> findByStatus(TitleRegistry.TitleStatus status);

    @Query("SELECT * FROM title_registry WHERE legal_description = :legalDescription")
    Flux<TitleRegistry> findByLegalDescription(String legalDescription);

    @Query("SELECT * FROM title_registry WHERE collateral_id = :collateralId ORDER BY verification_date DESC LIMIT 1")
    Mono<TitleRegistry> findLatestByCollateralId(String collateralId);

    @Query("SELECT * FROM title_registry WHERE current_owner = :owner AND status = 'VERIFIED'")
    Flux<TitleRegistry> findVerifiedTitlesByOwner(String owner);

    @Query("SELECT * FROM title_registry WHERE is_valid = true AND status = 'VERIFIED'")
    Flux<TitleRegistry> findAllValidTitles();

    @Query("DELETE FROM title_registry WHERE collateral_id = :collateralId")
    Mono<Void> deleteByCollateralId(String collateralId);
}
