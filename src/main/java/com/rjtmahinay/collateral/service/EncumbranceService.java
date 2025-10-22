package com.rjtmahinay.collateral.service;

import com.rjtmahinay.collateral.model.Encumbrance;
import com.rjtmahinay.collateral.model.EncumbranceStatus;
import com.rjtmahinay.collateral.repository.EncumbranceRepository;
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
public class EncumbranceService {

    private final EncumbranceRepository encumbranceRepository;
    private final CollateralService collateralService;

    public Mono<Encumbrance> createEncumbrance(Encumbrance encumbrance) {
        log.info("Creating new encumbrance for collateral: {}", encumbrance.getCollateralId());

        encumbrance.setEncumbranceId(generateEncumbranceId());
        encumbrance.setCreatedAt(LocalDateTime.now());
        encumbrance.setUpdatedAt(LocalDateTime.now());

        return encumbranceRepository.save(encumbrance)
                .flatMap(saved -> updateCollateralEncumberedValue(saved.getCollateralId()).thenReturn(saved))
                .doOnSuccess(saved -> log.info("Encumbrance created with ID: {}", saved.getEncumbranceId()))
                .doOnError(error -> log.error("Error creating encumbrance", error));
    }

    public Mono<Encumbrance> updateEncumbrance(String encumbranceId, Encumbrance encumbrance) {
        log.info("Updating encumbrance: {}", encumbranceId);

        return encumbranceRepository.findByEncumbranceId(encumbranceId)
                .switchIfEmpty(Mono.error(new RuntimeException("Encumbrance not found: " + encumbranceId)))
                .flatMap(existing -> {
                    String collateralId = existing.getCollateralId();

                    existing.setAmount(encumbrance.getAmount());
                    existing.setCurrency(encumbrance.getCurrency());
                    existing.setType(encumbrance.getType());
                    existing.setStatus(encumbrance.getStatus());
                    existing.setEffectiveDate(encumbrance.getEffectiveDate());
                    existing.setExpiryDate(encumbrance.getExpiryDate());
                    existing.setUpdatedAt(LocalDateTime.now());
                    existing.setUpdatedBy(encumbrance.getUpdatedBy());
                    existing.setDescription(encumbrance.getDescription());
                    existing.setPriority(encumbrance.getPriority());
                    existing.setLegalReference(encumbrance.getLegalReference());
                    existing.setNotes(encumbrance.getNotes());

                    return encumbranceRepository.save(existing)
                            .flatMap(saved -> updateCollateralEncumberedValue(collateralId).thenReturn(saved));
                })
                .doOnSuccess(updated -> log.info("Encumbrance updated: {}", updated.getEncumbranceId()))
                .doOnError(error -> log.error("Error updating encumbrance: {}", encumbranceId, error));
    }

    public Mono<Encumbrance> getEncumbranceById(String encumbranceId) {
        log.info("Retrieving encumbrance: {}", encumbranceId);
        return encumbranceRepository.findByEncumbranceId(encumbranceId)
                .switchIfEmpty(Mono.error(new RuntimeException("Encumbrance not found: " + encumbranceId)));
    }

    public Flux<Encumbrance> getEncumbrancesByCollateralId(String collateralId) {
        log.info("Retrieving encumbrances for collateral: {}", collateralId);
        return encumbranceRepository.findByCollateralId(collateralId);
    }

    public Flux<Encumbrance> getEncumbrancesByLoanId(String loanId) {
        log.info("Retrieving encumbrances for loan: {}", loanId);
        return encumbranceRepository.findByLoanId(loanId);
    }

    public Flux<Encumbrance> getEncumbrancesByCustomerId(String customerId) {
        log.info("Retrieving encumbrances for customer: {}", customerId);
        return encumbranceRepository.findByCustomerId(customerId);
    }

    public Flux<Encumbrance> getEncumbrancesByStatus(EncumbranceStatus status) {
        log.info("Retrieving encumbrances by status: {}", status);
        return encumbranceRepository.findByStatus(status);
    }

    public Flux<Encumbrance> getActiveEncumbrancesByCollateral(String collateralId) {
        log.info("Retrieving active encumbrances for collateral: {}", collateralId);
        return encumbranceRepository.findActiveEncumbrancesByCollateralId(collateralId)
                .sort((e1, e2) -> Integer.compare(e1.getPriority() != null ? e1.getPriority() : 0,
                        e2.getPriority() != null ? e2.getPriority() : 0));
    }

    public Flux<Encumbrance> getExpiredEncumbrances() {
        log.info("Retrieving expired encumbrances");
        LocalDateTime currentDate = LocalDateTime.now();
        return encumbranceRepository.findExpiredEncumbrances(currentDate);
    }

    public Mono<BigDecimal> getTotalEncumberedAmount(String collateralId) {
        log.info("Calculating total encumbered amount for collateral: {}", collateralId);
        return encumbranceRepository.getTotalEncumberedAmountByCollateralId(collateralId);
    }

    public Mono<Encumbrance> releaseEncumbrance(String encumbranceId, String releasedBy) {
        log.info("Releasing encumbrance: {}", encumbranceId);

        return encumbranceRepository.releaseEncumbranceById(encumbranceId, releasedBy)
                .then(encumbranceRepository.findByEncumbranceId(encumbranceId))
                .flatMap(encumbrance -> updateCollateralEncumberedValue(encumbrance.getCollateralId())
                        .thenReturn(encumbrance))
                .doOnSuccess(released -> log.info("Encumbrance released: {}", released.getEncumbranceId()));
    }

    public Mono<Encumbrance> partiallyReleaseEncumbrance(String encumbranceId, BigDecimal releaseAmount,
            String releasedBy) {
        log.info("Partially releasing encumbrance: {} with amount: {}", encumbranceId, releaseAmount);

        return getEncumbranceById(encumbranceId)
                .flatMap(encumbrance -> {
                    if (releaseAmount.compareTo(encumbrance.getAmount()) >= 0) {
                        return releaseEncumbrance(encumbranceId, releasedBy);
                    }

                    return encumbranceRepository.partiallyReleaseEncumbrance(encumbranceId, releaseAmount, releasedBy)
                            .then(encumbranceRepository.findByEncumbranceId(encumbranceId))
                            .flatMap(updated -> updateCollateralEncumberedValue(updated.getCollateralId())
                                    .thenReturn(updated));
                })
                .doOnSuccess(updated -> log.info("Encumbrance partially released: {}", updated.getEncumbranceId()));
    }

    public Mono<Void> expireEncumbrances() {
        log.info("Processing expired encumbrances");

        LocalDateTime currentDate = LocalDateTime.now();
        return encumbranceRepository.expireEncumbrances(currentDate)
                .then()
                .doOnSuccess(v -> log.info("Expired encumbrances processing completed"));
    }

    public Mono<Void> deleteEncumbrance(String encumbranceId) {
        log.info("Deleting encumbrance: {}", encumbranceId);

        return getEncumbranceById(encumbranceId)
                .flatMap(encumbrance -> {
                    String collateralId = encumbrance.getCollateralId();
                    return encumbranceRepository.deleteByEncumbranceId(encumbranceId)
                            .then(updateCollateralEncumberedValue(collateralId));
                })
                .doOnSuccess(v -> log.info("Encumbrance deleted: {}", encumbranceId))
                .doOnError(error -> log.error("Error deleting encumbrance: {}", encumbranceId, error));
    }

    private Mono<Void> updateCollateralEncumberedValue(String collateralId) {
        return getTotalEncumberedAmount(collateralId)
                .flatMap(totalAmount -> collateralService.updateEncumberedValue(collateralId, totalAmount))
                .then();
    }

    private String generateEncumbranceId() {
        return "ENC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
