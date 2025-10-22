package com.rjtmahinay.collateral.controller;

import com.rjtmahinay.collateral.model.Encumbrance;
import com.rjtmahinay.collateral.model.EncumbranceStatus;
import com.rjtmahinay.collateral.model.EncumbranceType;
import com.rjtmahinay.collateral.service.EncumbranceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/encumbrances")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Encumbrance Management", description = "APIs for managing collateral encumbrances including liens, holds, and releases")
public class EncumbranceController {

    private final EncumbranceService encumbranceService;

    @Operation(summary = "Create a new encumbrance", description = "Creates a new encumbrance on a collateral asset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Encumbrance created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public Mono<ResponseEntity<Encumbrance>> createEncumbrance(
            @Parameter(description = "Encumbrance details to create", required = true) @RequestBody Encumbrance encumbrance) {
        log.info("REST request to create encumbrance for collateral: {}", encumbrance.getCollateralId());

        return encumbranceService.createEncumbrance(encumbrance)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(summary = "Get encumbrance by ID", description = "Retrieves a specific encumbrance by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encumbrance found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class))),
            @ApiResponse(responseCode = "404", description = "Encumbrance not found")
    })
    @GetMapping("/{encumbranceId}")
    public Mono<ResponseEntity<Encumbrance>> getEncumbrance(
            @Parameter(description = "Unique identifier of the encumbrance", required = true) @PathVariable String encumbranceId) {
        log.info("REST request to get encumbrance: {}", encumbranceId);

        return encumbranceService.getEncumbranceById(encumbranceId)
                .map(encumbrance -> ResponseEntity.ok().body(encumbrance))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @PutMapping("/{encumbranceId}")
    public Mono<ResponseEntity<Encumbrance>> updateEncumbrance(
            @PathVariable String encumbranceId,
            @RequestBody Encumbrance encumbrance) {
        log.info("REST request to update encumbrance: {}", encumbranceId);

        return encumbranceService.updateEncumbrance(encumbranceId, encumbrance)
                .map(updated -> ResponseEntity.ok().body(updated))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{encumbranceId}")
    public Mono<ResponseEntity<Void>> deleteEncumbrance(@PathVariable String encumbranceId) {
        log.info("REST request to delete encumbrance: {}", encumbranceId);

        return encumbranceService.deleteEncumbrance(encumbranceId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @GetMapping("/collateral/{collateralId}")
    public Flux<Encumbrance> getEncumbrancesByCollateral(@PathVariable String collateralId) {
        log.info("REST request to get encumbrances for collateral: {}", collateralId);
        return encumbranceService.getEncumbrancesByCollateralId(collateralId);
    }

    @GetMapping("/collateral/{collateralId}/active")
    public Flux<Encumbrance> getActiveEncumbrancesByCollateral(@PathVariable String collateralId) {
        log.info("REST request to get active encumbrances for collateral: {}", collateralId);
        return encumbranceService.getActiveEncumbrancesByCollateral(collateralId);
    }

    @GetMapping("/loan/{loanId}")
    public Flux<Encumbrance> getEncumbrancesByLoan(@PathVariable String loanId) {
        log.info("REST request to get encumbrances for loan: {}", loanId);
        return encumbranceService.getEncumbrancesByLoanId(loanId);
    }

    @GetMapping("/customer/{customerId}")
    public Flux<Encumbrance> getEncumbrancesByCustomer(@PathVariable String customerId) {
        log.info("REST request to get encumbrances for customer: {}", customerId);
        return encumbranceService.getEncumbrancesByCustomerId(customerId);
    }

    @GetMapping("/status/{status}")
    public Flux<Encumbrance> getEncumbrancesByStatus(@PathVariable EncumbranceStatus status) {
        log.info("REST request to get encumbrances by status: {}", status);
        return encumbranceService.getEncumbrancesByStatus(status);
    }

    @GetMapping("/expired")
    public Flux<Encumbrance> getExpiredEncumbrances() {
        log.info("REST request to get expired encumbrances");
        return encumbranceService.getExpiredEncumbrances();
    }

    @GetMapping("/collateral/{collateralId}/total-amount")
    public Mono<ResponseEntity<BigDecimal>> getTotalEncumberedAmount(@PathVariable String collateralId) {
        log.info("REST request to get total encumbered amount for collateral: {}", collateralId);

        return encumbranceService.getTotalEncumberedAmount(collateralId)
                .map(total -> ResponseEntity.ok().body(total));
    }

    @PatchMapping("/{encumbranceId}/release")
    public Mono<ResponseEntity<Encumbrance>> releaseEncumbrance(
            @PathVariable String encumbranceId,
            @RequestBody ReleaseRequest request) {
        log.info("REST request to release encumbrance: {}", encumbranceId);

        return encumbranceService.releaseEncumbrance(encumbranceId, request.getReleasedBy())
                .map(released -> ResponseEntity.ok().body(released))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{encumbranceId}/partial-release")
    public Mono<ResponseEntity<Encumbrance>> partiallyReleaseEncumbrance(
            @PathVariable String encumbranceId,
            @RequestBody PartialReleaseRequest request) {
        log.info("REST request to partially release encumbrance: {} with amount: {}",
                encumbranceId, request.getReleaseAmount());

        return encumbranceService.partiallyReleaseEncumbrance(
                encumbranceId,
                request.getReleaseAmount(),
                request.getReleasedBy())
                .map(released -> ResponseEntity.ok().body(released))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @PostMapping("/expire-encumbrances")
    public Mono<ResponseEntity<Void>> expireEncumbrances() {
        log.info("REST request to process expired encumbrances");

        return encumbranceService.expireEncumbrances()
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @GetMapping("/types")
    public Mono<ResponseEntity<EncumbranceType[]>> getEncumbranceTypes() {
        log.info("REST request to get all encumbrance types");
        return Mono.just(ResponseEntity.ok().body(EncumbranceType.values()));
    }

    @GetMapping("/statuses")
    public Mono<ResponseEntity<EncumbranceStatus[]>> getEncumbranceStatuses() {
        log.info("REST request to get all encumbrance statuses");
        return Mono.just(ResponseEntity.ok().body(EncumbranceStatus.values()));
    }

    // Inner classes for request bodies
    public static class ReleaseRequest {
        private String releasedBy;

        public String getReleasedBy() {
            return releasedBy;
        }

        public void setReleasedBy(String releasedBy) {
            this.releasedBy = releasedBy;
        }
    }

    public static class PartialReleaseRequest {
        private BigDecimal releaseAmount;
        private String releasedBy;

        public BigDecimal getReleaseAmount() {
            return releaseAmount;
        }

        public void setReleaseAmount(BigDecimal releaseAmount) {
            this.releaseAmount = releaseAmount;
        }

        public String getReleasedBy() {
            return releasedBy;
        }

        public void setReleasedBy(String releasedBy) {
            this.releasedBy = releasedBy;
        }
    }
}
