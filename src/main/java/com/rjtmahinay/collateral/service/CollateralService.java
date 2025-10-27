package com.rjtmahinay.collateral.service;

import com.rjtmahinay.collateral.model.AutoValuation;
import com.rjtmahinay.collateral.model.Collateral;
import com.rjtmahinay.collateral.model.CollateralStatus;
import com.rjtmahinay.collateral.model.TitleRegistry;
import com.rjtmahinay.collateral.repository.AutoValuationRepository;
import com.rjtmahinay.collateral.repository.CollateralRepository;
import com.rjtmahinay.collateral.repository.TitleRegistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollateralService {

    private final CollateralRepository collateralRepository;
    private final TitleRegistryRepository titleRegistryRepository;
    private final AutoValuationRepository autoValuationRepository;

    public Mono<Collateral> createCollateral(Collateral collateral) {
        log.info("Creating new collateral for customer: {}", collateral.getCustomerId());

        collateral.setCollateralId(generateCollateralId());
        collateral.setCreatedAt(LocalDateTime.now());
        collateral.setUpdatedAt(LocalDateTime.now());
        collateral.setAvailableValue(collateral.getMarketValue());
        collateral.setEncumberedValue(BigDecimal.ZERO);

        return collateralRepository.save(collateral)
                .doOnSuccess(saved -> log.info("Collateral created with ID: {}", saved.getCollateralId()));
    }

    public Mono<Collateral> updateCollateral(String collateralId, Collateral collateral) {
        log.info("Updating collateral: {}", collateralId);

        return collateralRepository.findByCollateralId(collateralId)
                .switchIfEmpty(Mono.error(new RuntimeException("Collateral not found: " + collateralId)))
                .flatMap(existing -> {
                    existing.setDescription(collateral.getDescription());
                    existing.setEstimatedValue(collateral.getEstimatedValue());
                    existing.setMarketValue(collateral.getMarketValue());
                    existing.setCurrency(collateral.getCurrency());
                    existing.setStatus(collateral.getStatus());
                    existing.setLocation(collateral.getLocation());
                    existing.setEvaluationDate(collateral.getEvaluationDate());
                    existing.setUpdatedAt(LocalDateTime.now());
                    existing.setUpdatedBy(collateral.getUpdatedBy());
                    existing.setLegalDescription(collateral.getLegalDescription());
                    existing.setOwnershipDocuments(collateral.getOwnershipDocuments());
                    existing.setLastInspectionDate(collateral.getLastInspectionDate());
                    existing.setRiskRating(collateral.getRiskRating());

                    // Recalculate available value
                    existing.setAvailableValue(existing.getMarketValue().subtract(existing.getEncumberedValue()));

                    return collateralRepository.save(existing);
                })
                .doOnSuccess(updated -> log.info("Collateral updated: {}", updated.getCollateralId()));
    }

    public Mono<Collateral> getCollateralById(String collateralId) {
        log.info("Retrieving collateral: {}", collateralId);
        return collateralRepository.findByCollateralId(collateralId)
                .switchIfEmpty(Mono.error(new RuntimeException("Collateral not found: " + collateralId)));
    }

    public Flux<Collateral> getCollateralsByCustomerId(String customerId) {
        log.info("Retrieving collaterals for customer: {}", customerId);
        return collateralRepository.findByCustomerId(customerId);
    }

    public Flux<Collateral> getCollateralsByAccountId(String accountId) {
        log.info("Retrieving collaterals for account: {}", accountId);
        return collateralRepository.findByAccountId(accountId);
    }

    public Flux<Collateral> getCollateralsByStatus(CollateralStatus status) {
        log.info("Retrieving collaterals by status: {}", status);
        return collateralRepository.findByStatus(status);
    }

    public Flux<Collateral> getAvailableCollaterals(String customerId, BigDecimal minValue) {
        log.info("Retrieving available collaterals for customer: {} with min value: {}", customerId, minValue);
        return collateralRepository.findAvailableCollateralsByCustomerIdAndMinValue(customerId, minValue);
    }

    public Flux<Collateral> getEncumberedCollaterals() {
        log.info("Retrieving all encumbered collaterals");
        return collateralRepository.findEncumberedCollaterals();
    }

    public Mono<Collateral> updateCollateralValue(String collateralId, BigDecimal marketValue) {
        log.info("Updating market value for collateral: {} to {}", collateralId, marketValue);

        return collateralRepository.updateMarketValueByCollateralId(collateralId, marketValue)
                .then(collateralRepository.findByCollateralId(collateralId))
                .doOnSuccess(updated -> log.info("Market value updated for collateral: {}", collateralId));
    }

    public Mono<Collateral> updateEncumberedValue(String collateralId, BigDecimal encumberedValue) {
        log.info("Updating encumbered value for collateral: {} to {}", collateralId, encumberedValue);

        return collateralRepository.updateEncumberedValueByCollateralId(collateralId, encumberedValue)
                .then(collateralRepository.findByCollateralId(collateralId))
                .doOnSuccess(updated -> log.info("Encumbered value updated for collateral: {}", collateralId));
    }

    public Mono<Void> deleteCollateral(String collateralId) {
        log.info("Deleting collateral: {}", collateralId);

        return collateralRepository.findByCollateralId(collateralId)
                .switchIfEmpty(Mono.error(new RuntimeException("Collateral not found: " + collateralId)))
                .flatMap(collateral -> collateralRepository.deleteByCollateralId(collateralId))
                .doOnSuccess(v -> log.info("Collateral deleted: {}", collateralId));
    }

    // Database-based Integration Methods

    public Mono<TitleRegistry> verifyCollateralTitle(String collateralId) {
        log.info("Verifying title for collateral: {}", collateralId);

        return getCollateralById(collateralId)
                .flatMap(collateral -> {
                    // Create a new title registry record with verified status
                    TitleRegistry titleRegistry = TitleRegistry.builder()
                            .titleId(UUID.randomUUID().toString())
                            .collateralId(collateralId)
                            .legalDescription(collateral.getLegalDescription())
                            .status(TitleRegistry.TitleStatus.VERIFIED)
                            .isValid(true)
                            .verificationDate(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .message("Title verification completed successfully")
                            .build();

                    return titleRegistryRepository.save(titleRegistry);
                })
                .doOnSuccess(title -> log.info("Title verification completed for collateral: {} - Status: {}",
                        collateralId, title.getStatus()));
    }

    public Mono<TitleRegistry> getOwnershipDetails(String collateralId, String titleNumber) {
        log.info("Retrieving ownership details for collateral: {}", collateralId);

        return titleRegistryRepository.findByTitleNumber(titleNumber)
                .switchIfEmpty(titleRegistryRepository.findLatestByCollateralId(collateralId))
                .doOnSuccess(details -> log.info("Ownership details retrieved for collateral: {}", collateralId));
    }

    public Flux<TitleRegistry> searchExistingTitles(String collateralId) {
        log.info("Searching existing titles for collateral: {}", collateralId);

        return titleRegistryRepository.findByCollateralId(collateralId)
                .doOnComplete(() -> log.info("Title search completed for collateral: {}", collateralId));
    }

    public Mono<Collateral> requestAutoValuation(String collateralId) {
        log.info("Requesting auto valuation for collateral: {}", collateralId);

        return getCollateralById(collateralId)
                .flatMap(collateral -> {
                    // Create a new auto valuation record
                    AutoValuation autoValuation = AutoValuation.builder()
                            .valuationId(UUID.randomUUID().toString())
                            .collateralId(collateralId)
                            .type(collateral.getType().name())
                            .location(collateral.getLocation())
                            .description(collateral.getDescription())
                            .status(AutoValuation.ValuationStatus.VALUATION_COMPLETED)
                            .estimatedValue(collateral.getMarketValue())
                            .lowRange(collateral.getMarketValue().multiply(BigDecimal.valueOf(0.8)))
                            .highRange(collateral.getMarketValue().multiply(BigDecimal.valueOf(1.2)))
                            .currency(collateral.getCurrency())
                            .methodology("Database-based valuation")
                            .confidenceScore(0.85)
                            .valuationDate(LocalDateTime.now())
                            .requestDate(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .message("Auto valuation completed successfully")
                            .build();

                    return autoValuationRepository.save(autoValuation)
                            .flatMap(savedValuation -> {
                                // Update collateral with new valuation
                                collateral.setMarketValue(savedValuation.getEstimatedValue());
                                collateral.setEstimatedValue(savedValuation.getEstimatedValue());
                                collateral.setEvaluationDate(savedValuation.getValuationDate());
                                collateral.setUpdatedAt(LocalDateTime.now());

                                // Recalculate available value
                                collateral.setAvailableValue(
                                        collateral.getMarketValue().subtract(collateral.getEncumberedValue()));

                                return collateralRepository.save(collateral);
                            });
                })
                .doOnSuccess(updated -> log.info("Auto valuation completed for collateral: {}", collateralId));
    }

    public Flux<AutoValuation> getMarketTrends(String collateralId) {
        log.info("Retrieving market trends for collateral: {}", collateralId);

        return getCollateralById(collateralId)
                .flatMapMany(collateral -> autoValuationRepository.findByTypeAndLocationOrderByValuationDateDesc(
                        collateral.getType().name(), collateral.getLocation()))
                .doOnComplete(() -> log.info("Market trends retrieved for collateral: {}", collateralId));
    }

    public Flux<AutoValuation> getComparableProperties(String collateralId) {
        log.info("Retrieving comparable properties for collateral: {}", collateralId);

        return getCollateralById(collateralId)
                .flatMapMany(collateral -> autoValuationRepository.findByTypeAndLocationOrderByValuationDateDesc(
                        collateral.getType().name(), collateral.getLocation()))
                .doOnComplete(() -> log.info("Comparable properties retrieved for collateral: {}", collateralId));
    }

    public Mono<Collateral> requestRevaluation(String collateralId, String reason) {
        log.info("Requesting revaluation for collateral: {} with reason: {}", collateralId, reason);

        return getCollateralById(collateralId)
                .flatMap(collateral -> {
                    // Create a new revaluation record
                    AutoValuation revaluation = AutoValuation.builder()
                            .valuationId(UUID.randomUUID().toString())
                            .collateralId(collateralId)
                            .type(collateral.getType().name())
                            .location(collateral.getLocation())
                            .description(collateral.getDescription())
                            .status(AutoValuation.ValuationStatus.VALUATION_COMPLETED)
                            .estimatedValue(collateral.getMarketValue().multiply(BigDecimal.valueOf(1.05))) // 5%
                                                                                                            // increase
                                                                                                            // for
                                                                                                            // revaluation
                            .lowRange(collateral.getMarketValue().multiply(BigDecimal.valueOf(0.85)))
                            .highRange(collateral.getMarketValue().multiply(BigDecimal.valueOf(1.25)))
                            .currency(collateral.getCurrency())
                            .methodology("Revaluation based on: " + reason)
                            .confidenceScore(0.90)
                            .valuationDate(LocalDateTime.now())
                            .requestDate(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .message("Revaluation completed: " + reason)
                            .build();

                    return autoValuationRepository.save(revaluation)
                            .flatMap(savedRevaluation -> {
                                // Update collateral with new valuation
                                collateral.setMarketValue(savedRevaluation.getEstimatedValue());
                                collateral.setEstimatedValue(savedRevaluation.getEstimatedValue());
                                collateral.setEvaluationDate(savedRevaluation.getValuationDate());
                                collateral.setUpdatedAt(LocalDateTime.now());

                                // Recalculate available value
                                collateral.setAvailableValue(
                                        collateral.getMarketValue().subtract(collateral.getEncumberedValue()));

                                return collateralRepository.save(collateral);
                            });
                })
                .doOnSuccess(updated -> log.info("Revaluation completed for collateral: {}", collateralId));
    }

    public Mono<Collateral> createCollateralWithValidation(Collateral collateral) {
        log.info("Creating collateral with title verification and auto valuation for customer: {}",
                collateral.getCustomerId());

        // First create the basic collateral
        return createCollateral(collateral)
                .flatMap(savedCollateral -> {
                    // Verify title if legal description is provided
                    Mono<Collateral> titleVerificationMono = Mono.just(savedCollateral);
                    if (savedCollateral.getLegalDescription() != null
                            && !savedCollateral.getLegalDescription().isEmpty()) {
                        titleVerificationMono = verifyCollateralTitle(savedCollateral.getCollateralId())
                                .flatMap(titleResponse -> {
                                    if (titleResponse.getStatus() == TitleRegistry.TitleStatus.VERIFIED) {
                                        savedCollateral.setStatus(CollateralStatus.APPROVED);
                                    } else {
                                        savedCollateral.setStatus(CollateralStatus.UNDER_REVIEW);
                                    }
                                    savedCollateral.setUpdatedAt(LocalDateTime.now());
                                    return collateralRepository.save(savedCollateral);
                                })
                                .onErrorReturn(savedCollateral); // Continue even if title verification fails
                    }

                    // Request auto valuation and update the collateral
                    return titleVerificationMono
                            .flatMap(updatedCollateral -> requestAutoValuation(updatedCollateral.getCollateralId())
                                    .onErrorReturn(updatedCollateral)); // Continue even if auto valuation fails
                })
                .doOnSuccess(finalCollateral -> log.info("Collateral created with validation - ID: {}",
                        finalCollateral.getCollateralId()));
    }

    private String generateCollateralId() {
        return "COL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
