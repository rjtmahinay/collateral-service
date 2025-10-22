package com.rjtmahinay.collateral.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rjtmahinay.collateral.model.Collateral;
import com.rjtmahinay.collateral.model.CollateralStatus;
import com.rjtmahinay.collateral.model.CollateralType;
import com.rjtmahinay.collateral.service.CollateralService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/collaterals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Collateral Management", description = "APIs for managing collaterals including CRUD operations and queries")
public class CollateralController {

    private final CollateralService collateralService;

    @Operation(summary = "Create a new collateral", description = "Creates a new collateral asset for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Collateral created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Collateral.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public Mono<ResponseEntity<Collateral>> createCollateral(
            @Parameter(description = "Collateral details to create", required = true) @RequestBody Collateral collateral) {
        log.info("REST request to create collateral for customer: {}", collateral.getCustomerId());

        return collateralService.createCollateral(collateral)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(summary = "Get collateral by ID", description = "Retrieves a specific collateral by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Collateral found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Collateral.class))),
            @ApiResponse(responseCode = "404", description = "Collateral not found")
    })
    @GetMapping("/{collateralId}")
    public Mono<ResponseEntity<Collateral>> getCollateral(
            @Parameter(description = "Unique identifier of the collateral", required = true) @PathVariable String collateralId) {
        log.info("REST request to get collateral: {}", collateralId);

        return collateralService.getCollateralById(collateralId)
                .map(collateral -> ResponseEntity.ok().body(collateral))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @PutMapping("/{collateralId}")
    public Mono<ResponseEntity<Collateral>> updateCollateral(
            @PathVariable String collateralId,
            @RequestBody Collateral collateral) {
        log.info("REST request to update collateral: {}", collateralId);

        return collateralService.updateCollateral(collateralId, collateral)
                .map(updated -> ResponseEntity.ok().body(updated))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{collateralId}")
    public Mono<ResponseEntity<Void>> deleteCollateral(@PathVariable String collateralId) {
        log.info("REST request to delete collateral: {}", collateralId);

        return collateralService.deleteCollateral(collateralId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get collaterals by customer", description = "Retrieves all collaterals belonging to a specific customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of collaterals for the customer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Collateral.class)))
    })
    @GetMapping("/customer/{customerId}")
    public Flux<Collateral> getCollateralsByCustomer(
            @Parameter(description = "Unique identifier of the customer", required = true) @PathVariable String customerId) {
        log.info("REST request to get collaterals for customer: {}", customerId);
        return collateralService.getCollateralsByCustomerId(customerId);
    }

    @GetMapping("/account/{accountId}")
    public Flux<Collateral> getCollateralsByAccount(@PathVariable String accountId) {
        log.info("REST request to get collaterals for account: {}", accountId);
        return collateralService.getCollateralsByAccountId(accountId);
    }

    @GetMapping("/status/{status}")
    public Flux<Collateral> getCollateralsByStatus(@PathVariable CollateralStatus status) {
        log.info("REST request to get collaterals by status: {}", status);
        return collateralService.getCollateralsByStatus(status);
    }

    @GetMapping("/customer/{customerId}/available")
    public Flux<Collateral> getAvailableCollaterals(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "0") BigDecimal minValue) {
        log.info("REST request to get available collaterals for customer: {} with min value: {}", customerId, minValue);
        return collateralService.getAvailableCollaterals(customerId, minValue);
    }

    @GetMapping("/encumbered")
    public Flux<Collateral> getEncumberedCollaterals() {
        log.info("REST request to get all encumbered collaterals");
        return collateralService.getEncumberedCollaterals();
    }

    @PatchMapping("/{collateralId}/value")
    public Mono<ResponseEntity<Collateral>> updateCollateralValue(
            @PathVariable String collateralId,
            @RequestBody UpdateValueRequest request) {
        log.info("REST request to update value for collateral: {} to {}", collateralId, request.getMarketValue());

        return collateralService.updateCollateralValue(collateralId, request.getMarketValue())
                .map(updated -> ResponseEntity.ok().body(updated))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get collateral types", description = "Retrieves all available collateral types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of collateral types", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CollateralType[].class)))
    })
    @GetMapping("/types")
    public Mono<ResponseEntity<CollateralType[]>> getCollateralTypes() {
        log.info("REST request to get all collateral types");
        return Mono.just(ResponseEntity.ok().body(CollateralType.values()));
    }

    @GetMapping("/statuses")
    public Mono<ResponseEntity<CollateralStatus[]>> getCollateralStatuses() {
        log.info("REST request to get all collateral statuses");
        return Mono.just(ResponseEntity.ok().body(CollateralStatus.values()));
    }

    // External API Integration endpoints

    @PostMapping("/create-with-validation")
    public Mono<ResponseEntity<Collateral>> createCollateralWithValidation(@RequestBody Collateral collateral) {
        log.info("REST request to create collateral with validation for customer: {}", collateral.getCustomerId());

        return collateralService.createCollateralWithValidation(collateral)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    // Inner classes for request bodies
    public static class UpdateValueRequest {
        private BigDecimal marketValue;

        public BigDecimal getMarketValue() {
            return marketValue;
        }

        public void setMarketValue(BigDecimal marketValue) {
            this.marketValue = marketValue;
        }
    }

}
