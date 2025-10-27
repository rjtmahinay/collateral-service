package com.rjtmahinay.collateral.controller;

import com.rjtmahinay.collateral.model.AutoValuation;
import com.rjtmahinay.collateral.repository.AutoValuationRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auto-valuations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auto Valuation Management", description = "APIs for managing auto valuation data")
public class AutoValuationController {

    private final AutoValuationRepository autoValuationRepository;

    @Operation(summary = "Create a new auto valuation", description = "Creates a new auto valuation record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Auto valuation created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoValuation.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public Mono<ResponseEntity<AutoValuation>> createAutoValuation(
            @Parameter(description = "Auto valuation details to create", required = true) @RequestBody AutoValuation autoValuation) {
        log.info("REST request to create auto valuation for collateral: {}", autoValuation.getCollateralId());

        autoValuation.setValuationId(UUID.randomUUID().toString());
        autoValuation.setCreatedAt(LocalDateTime.now());
        autoValuation.setUpdatedAt(LocalDateTime.now());

        return autoValuationRepository.save(autoValuation)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(summary = "Get auto valuation by ID", description = "Retrieves a specific auto valuation by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auto valuation found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoValuation.class))),
            @ApiResponse(responseCode = "404", description = "Auto valuation not found")
    })
    @GetMapping("/{valuationId}")
    public Mono<ResponseEntity<AutoValuation>> getAutoValuation(
            @Parameter(description = "Unique identifier of the auto valuation", required = true) @PathVariable String valuationId) {
        log.info("REST request to get auto valuation: {}", valuationId);

        return autoValuationRepository.findByValuationId(valuationId)
                .map(valuation -> ResponseEntity.ok().body(valuation))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update auto valuation", description = "Updates an existing auto valuation record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auto valuation updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoValuation.class))),
            @ApiResponse(responseCode = "404", description = "Auto valuation not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{valuationId}")
    public Mono<ResponseEntity<AutoValuation>> updateAutoValuation(
            @Parameter(description = "Unique identifier of the auto valuation to update", required = true) @PathVariable String valuationId,
            @Parameter(description = "Updated auto valuation information", required = true) @RequestBody AutoValuation autoValuation) {
        log.info("REST request to update auto valuation: {}", valuationId);

        return autoValuationRepository.findByValuationId(valuationId)
                .flatMap(existing -> {
                    autoValuation.setId(existing.getId());
                    autoValuation.setValuationId(valuationId);
                    autoValuation.setCreatedAt(existing.getCreatedAt());
                    autoValuation.setUpdatedAt(LocalDateTime.now());
                    return autoValuationRepository.save(autoValuation);
                })
                .map(updated -> ResponseEntity.ok().body(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete auto valuation", description = "Permanently removes an auto valuation record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Auto valuation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Auto valuation not found")
    })
    @DeleteMapping("/{valuationId}")
    public Mono<ResponseEntity<Void>> deleteAutoValuation(
            @Parameter(description = "Unique identifier of the auto valuation to delete", required = true) @PathVariable String valuationId) {
        log.info("REST request to delete auto valuation: {}", valuationId);

        return autoValuationRepository.findByValuationId(valuationId)
                .flatMap(existing -> autoValuationRepository.deleteById(existing.getId()))
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get auto valuations by collateral", description = "Retrieves all auto valuations for a specific collateral")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of auto valuations for the collateral", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoValuation.class)))
    })
    @GetMapping("/collateral/{collateralId}")
    public Flux<AutoValuation> getAutoValuationsByCollateral(
            @Parameter(description = "Unique identifier of the collateral", required = true) @PathVariable String collateralId) {
        log.info("REST request to get auto valuations for collateral: {}", collateralId);
        return autoValuationRepository.findByCollateralId(collateralId);
    }

    @Operation(summary = "Get auto valuations by type", description = "Retrieves all auto valuations for a specific type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of auto valuations for the type", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoValuation.class)))
    })
    @GetMapping("/type/{type}")
    public Flux<AutoValuation> getAutoValuationsByType(
            @Parameter(description = "Collateral type", required = true) @PathVariable String type) {
        log.info("REST request to get auto valuations for type: {}", type);
        return autoValuationRepository.findByType(type);
    }

    @Operation(summary = "Get auto valuations by location", description = "Retrieves all auto valuations for a specific location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of auto valuations for the location", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoValuation.class)))
    })
    @GetMapping("/location/{location}")
    public Flux<AutoValuation> getAutoValuationsByLocation(
            @Parameter(description = "Location identifier", required = true) @PathVariable String location) {
        log.info("REST request to get auto valuations for location: {}", location);
        return autoValuationRepository.findByLocation(location);
    }

    @Operation(summary = "Get auto valuations by status", description = "Retrieves all auto valuations with a specific status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of auto valuations with the specified status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoValuation.class)))
    })
    @GetMapping("/status/{status}")
    public Flux<AutoValuation> getAutoValuationsByStatus(
            @Parameter(description = "Valuation status", required = true) @PathVariable AutoValuation.ValuationStatus status) {
        log.info("REST request to get auto valuations by status: {}", status);
        return autoValuationRepository.findByStatus(status);
    }

    @Operation(summary = "Get latest auto valuation for collateral", description = "Retrieves the most recent auto valuation for a specific collateral")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest auto valuation found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoValuation.class))),
            @ApiResponse(responseCode = "404", description = "No auto valuation found for collateral")
    })
    @GetMapping("/collateral/{collateralId}/latest")
    public Mono<ResponseEntity<AutoValuation>> getLatestAutoValuationByCollateral(
            @Parameter(description = "Unique identifier of the collateral", required = true) @PathVariable String collateralId) {
        log.info("REST request to get latest auto valuation for collateral: {}", collateralId);

        return autoValuationRepository.findLatestByCollateralId(collateralId)
                .map(valuation -> ResponseEntity.ok().body(valuation))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all auto valuation statuses", description = "Retrieves all available auto valuation status values")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of auto valuation statuses", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoValuation.ValuationStatus[].class)))
    })
    @GetMapping("/statuses")
    public Mono<ResponseEntity<AutoValuation.ValuationStatus[]>> getAutoValuationStatuses() {
        log.info("REST request to get all auto valuation statuses");
        return Mono.just(ResponseEntity.ok().body(AutoValuation.ValuationStatus.values()));
    }
}
