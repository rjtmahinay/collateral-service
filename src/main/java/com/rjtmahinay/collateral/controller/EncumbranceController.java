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

        @Operation(summary = "Update encumbrance", description = "Updates an existing encumbrance with new information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Encumbrance updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class))),
                        @ApiResponse(responseCode = "404", description = "Encumbrance not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @PutMapping("/{encumbranceId}")
        public Mono<ResponseEntity<Encumbrance>> updateEncumbrance(
                        @Parameter(description = "Unique identifier of the encumbrance to update", required = true) @PathVariable String encumbranceId,
                        @Parameter(description = "Updated encumbrance information", required = true) @RequestBody Encumbrance encumbrance) {
                log.info("REST request to update encumbrance: {}", encumbranceId);

                return encumbranceService.updateEncumbrance(encumbranceId, encumbrance)
                                .map(updated -> ResponseEntity.ok().body(updated))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Delete encumbrance", description = "Permanently removes an encumbrance from the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Encumbrance deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Encumbrance not found")
        })
        @DeleteMapping("/{encumbranceId}")
        public Mono<ResponseEntity<Void>> deleteEncumbrance(
                        @Parameter(description = "Unique identifier of the encumbrance to delete", required = true) @PathVariable String encumbranceId) {
                log.info("REST request to delete encumbrance: {}", encumbranceId);

                return encumbranceService.deleteEncumbrance(encumbranceId)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Get encumbrances by collateral", description = "Retrieves all encumbrances associated with a specific collateral asset")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of encumbrances for the collateral", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class)))
        })
        @GetMapping("/collateral/{collateralId}")
        public Flux<Encumbrance> getEncumbrancesByCollateral(
                        @Parameter(description = "Unique identifier of the collateral", required = true) @PathVariable String collateralId) {
                log.info("REST request to get encumbrances for collateral: {}", collateralId);
                return encumbranceService.getEncumbrancesByCollateralId(collateralId);
        }

        @Operation(summary = "Get active encumbrances by collateral", description = "Retrieves all active encumbrances for a specific collateral asset")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of active encumbrances for the collateral", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class)))
        })
        @GetMapping("/collateral/{collateralId}/active")
        public Flux<Encumbrance> getActiveEncumbrancesByCollateral(
                        @Parameter(description = "Unique identifier of the collateral", required = true) @PathVariable String collateralId) {
                log.info("REST request to get active encumbrances for collateral: {}", collateralId);
                return encumbranceService.getActiveEncumbrancesByCollateral(collateralId);
        }

        @Operation(summary = "Get encumbrances by loan", description = "Retrieves all encumbrances associated with a specific loan")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of encumbrances for the loan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class)))
        })
        @GetMapping("/loan/{loanId}")
        public Flux<Encumbrance> getEncumbrancesByLoan(
                        @Parameter(description = "Unique identifier of the loan", required = true) @PathVariable String loanId) {
                log.info("REST request to get encumbrances for loan: {}", loanId);
                return encumbranceService.getEncumbrancesByLoanId(loanId);
        }

        @Operation(summary = "Get encumbrances by customer", description = "Retrieves all encumbrances associated with a specific customer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of encumbrances for the customer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class)))
        })
        @GetMapping("/customer/{customerId}")
        public Flux<Encumbrance> getEncumbrancesByCustomer(
                        @Parameter(description = "Unique identifier of the customer", required = true) @PathVariable String customerId) {
                log.info("REST request to get encumbrances for customer: {}", customerId);
                return encumbranceService.getEncumbrancesByCustomerId(customerId);
        }

        @Operation(summary = "Get encumbrances by status", description = "Retrieves all encumbrances with a specific status (ACTIVE, RELEASED, EXPIRED, etc.)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of encumbrances with the specified status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class)))
        })
        @GetMapping("/status/{status}")
        public Flux<Encumbrance> getEncumbrancesByStatus(
                        @Parameter(description = "Status of encumbrances to retrieve", required = true) @PathVariable EncumbranceStatus status) {
                log.info("REST request to get encumbrances by status: {}", status);
                return encumbranceService.getEncumbrancesByStatus(status);
        }

        @Operation(summary = "Get expired encumbrances", description = "Retrieves all encumbrances that have passed their expiration date")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of expired encumbrances", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class)))
        })
        @GetMapping("/expired")
        public Flux<Encumbrance> getExpiredEncumbrances() {
                log.info("REST request to get expired encumbrances");
                return encumbranceService.getExpiredEncumbrances();
        }

        @Operation(summary = "Get total encumbered amount", description = "Calculates the total amount of all active encumbrances for a specific collateral")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Total encumbered amount calculated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BigDecimal.class)))
        })
        @GetMapping("/collateral/{collateralId}/total-amount")
        public Mono<ResponseEntity<BigDecimal>> getTotalEncumberedAmount(
                        @Parameter(description = "Unique identifier of the collateral", required = true) @PathVariable String collateralId) {
                log.info("REST request to get total encumbered amount for collateral: {}", collateralId);

                return encumbranceService.getTotalEncumberedAmount(collateralId)
                                .map(total -> ResponseEntity.ok().body(total));
        }

        @Operation(summary = "Release encumbrance", description = "Fully releases an encumbrance, making the collateral available")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Encumbrance released successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class))),
                        @ApiResponse(responseCode = "404", description = "Encumbrance not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid release request")
        })
        @PatchMapping("/{encumbranceId}/release")
        public Mono<ResponseEntity<Encumbrance>> releaseEncumbrance(
                        @Parameter(description = "Unique identifier of the encumbrance to release", required = true) @PathVariable String encumbranceId,
                        @Parameter(description = "Release request details", required = true) @RequestBody ReleaseRequest request) {
                log.info("REST request to release encumbrance: {}", encumbranceId);

                return encumbranceService.releaseEncumbrance(encumbranceId, request.getReleasedBy())
                                .map(released -> ResponseEntity.ok().body(released))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Partially release encumbrance", description = "Partially releases an encumbrance by reducing its amount")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Encumbrance partially released successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Encumbrance.class))),
                        @ApiResponse(responseCode = "404", description = "Encumbrance not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid partial release request")
        })
        @PatchMapping("/{encumbranceId}/partial-release")
        public Mono<ResponseEntity<Encumbrance>> partiallyReleaseEncumbrance(
                        @Parameter(description = "Unique identifier of the encumbrance to partially release", required = true) @PathVariable String encumbranceId,
                        @Parameter(description = "Partial release request details", required = true) @RequestBody PartialReleaseRequest request) {
                log.info("REST request to partially release encumbrance: {} with amount: {}",
                                encumbranceId, request.getReleaseAmount());

                return encumbranceService.partiallyReleaseEncumbrance(
                                encumbranceId,
                                request.getReleaseAmount(),
                                request.getReleasedBy())
                                .map(released -> ResponseEntity.ok().body(released))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Process expired encumbrances", description = "Processes and marks encumbrances as expired based on their expiration dates")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Expired encumbrances processed successfully"),
                        @ApiResponse(responseCode = "500", description = "Error processing expired encumbrances")
        })
        @PostMapping("/expire-encumbrances")
        public Mono<ResponseEntity<Void>> expireEncumbrances() {
                log.info("REST request to process expired encumbrances");

                return encumbranceService.expireEncumbrances()
                                .then(Mono.just(ResponseEntity.ok().<Void>build()));
        }

        @Operation(summary = "Get encumbrance types", description = "Retrieves all available encumbrance types")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of encumbrance types", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EncumbranceType[].class)))
        })
        @GetMapping("/types")
        public Mono<ResponseEntity<EncumbranceType[]>> getEncumbranceTypes() {
                log.info("REST request to get all encumbrance types");
                return Mono.just(ResponseEntity.ok().body(EncumbranceType.values()));
        }

        @Operation(summary = "Get encumbrance statuses", description = "Retrieves all available encumbrance status values")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of encumbrance statuses", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EncumbranceStatus[].class)))
        })
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
