package com.rjtmahinay.collateral.controller;

import com.rjtmahinay.collateral.model.TitleRegistry;
import com.rjtmahinay.collateral.repository.TitleRegistryRepository;
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
@RequestMapping("/api/v1/title-registry")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Title Registry Management", description = "APIs for managing title registry data")
public class TitleRegistryController {

    private final TitleRegistryRepository titleRegistryRepository;

    @Operation(summary = "Create a new title registry record", description = "Creates a new title registry record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Title registry record created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public Mono<ResponseEntity<TitleRegistry>> createTitleRegistry(
            @Parameter(description = "Title registry details to create", required = true) @RequestBody TitleRegistry titleRegistry) {
        log.info("REST request to create title registry for collateral: {}", titleRegistry.getCollateralId());

        titleRegistry.setTitleId(UUID.randomUUID().toString());
        titleRegistry.setCreatedAt(LocalDateTime.now());
        titleRegistry.setUpdatedAt(LocalDateTime.now());

        return titleRegistryRepository.save(titleRegistry)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(summary = "Get title registry by ID", description = "Retrieves a specific title registry record by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Title registry record found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class))),
            @ApiResponse(responseCode = "404", description = "Title registry record not found")
    })
    @GetMapping("/{titleId}")
    public Mono<ResponseEntity<TitleRegistry>> getTitleRegistry(
            @Parameter(description = "Unique identifier of the title registry record", required = true) @PathVariable String titleId) {
        log.info("REST request to get title registry: {}", titleId);

        return titleRegistryRepository.findByTitleId(titleId)
                .map(title -> ResponseEntity.ok().body(title))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update title registry", description = "Updates an existing title registry record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Title registry record updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class))),
            @ApiResponse(responseCode = "404", description = "Title registry record not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{titleId}")
    public Mono<ResponseEntity<TitleRegistry>> updateTitleRegistry(
            @Parameter(description = "Unique identifier of the title registry record to update", required = true) @PathVariable String titleId,
            @Parameter(description = "Updated title registry information", required = true) @RequestBody TitleRegistry titleRegistry) {
        log.info("REST request to update title registry: {}", titleId);

        return titleRegistryRepository.findByTitleId(titleId)
                .flatMap(existing -> {
                    titleRegistry.setId(existing.getId());
                    titleRegistry.setTitleId(titleId);
                    titleRegistry.setCreatedAt(existing.getCreatedAt());
                    titleRegistry.setUpdatedAt(LocalDateTime.now());
                    return titleRegistryRepository.save(titleRegistry);
                })
                .map(updated -> ResponseEntity.ok().body(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete title registry", description = "Permanently removes a title registry record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Title registry record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Title registry record not found")
    })
    @DeleteMapping("/{titleId}")
    public Mono<ResponseEntity<Void>> deleteTitleRegistry(
            @Parameter(description = "Unique identifier of the title registry record to delete", required = true) @PathVariable String titleId) {
        log.info("REST request to delete title registry: {}", titleId);

        return titleRegistryRepository.findByTitleId(titleId)
                .flatMap(existing -> titleRegistryRepository.deleteById(existing.getId()))
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get title registry by title number", description = "Retrieves a title registry record by title number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Title registry record found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class))),
            @ApiResponse(responseCode = "404", description = "Title registry record not found")
    })
    @GetMapping("/title-number/{titleNumber}")
    public Mono<ResponseEntity<TitleRegistry>> getTitleRegistryByTitleNumber(
            @Parameter(description = "Title number", required = true) @PathVariable String titleNumber) {
        log.info("REST request to get title registry by title number: {}", titleNumber);

        return titleRegistryRepository.findByTitleNumber(titleNumber)
                .map(title -> ResponseEntity.ok().body(title))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get title registry records by collateral", description = "Retrieves all title registry records for a specific collateral")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of title registry records for the collateral", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class)))
    })
    @GetMapping("/collateral/{collateralId}")
    public Flux<TitleRegistry> getTitleRegistriesByCollateral(
            @Parameter(description = "Unique identifier of the collateral", required = true) @PathVariable String collateralId) {
        log.info("REST request to get title registry records for collateral: {}", collateralId);
        return titleRegistryRepository.findByCollateralId(collateralId);
    }

    @Operation(summary = "Get title registry records by owner", description = "Retrieves all title registry records for a specific owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of title registry records for the owner", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class)))
    })
    @GetMapping("/owner/{owner}")
    public Flux<TitleRegistry> getTitleRegistriesByOwner(
            @Parameter(description = "Current owner name", required = true) @PathVariable String owner) {
        log.info("REST request to get title registry records for owner: {}", owner);
        return titleRegistryRepository.findByCurrentOwner(owner);
    }

    @Operation(summary = "Get title registry records by status", description = "Retrieves all title registry records with a specific status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of title registry records with the specified status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class)))
    })
    @GetMapping("/status/{status}")
    public Flux<TitleRegistry> getTitleRegistriesByStatus(
            @Parameter(description = "Title status", required = true) @PathVariable TitleRegistry.TitleStatus status) {
        log.info("REST request to get title registry records by status: {}", status);
        return titleRegistryRepository.findByStatus(status);
    }

    @Operation(summary = "Get latest title registry for collateral", description = "Retrieves the most recent title registry record for a specific collateral")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest title registry record found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class))),
            @ApiResponse(responseCode = "404", description = "No title registry record found for collateral")
    })
    @GetMapping("/collateral/{collateralId}/latest")
    public Mono<ResponseEntity<TitleRegistry>> getLatestTitleRegistryByCollateral(
            @Parameter(description = "Unique identifier of the collateral", required = true) @PathVariable String collateralId) {
        log.info("REST request to get latest title registry for collateral: {}", collateralId);

        return titleRegistryRepository.findLatestByCollateralId(collateralId)
                .map(title -> ResponseEntity.ok().body(title))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get verified titles by owner", description = "Retrieves all verified title registry records for a specific owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of verified title registry records for the owner", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class)))
    })
    @GetMapping("/owner/{owner}/verified")
    public Flux<TitleRegistry> getVerifiedTitlesByOwner(
            @Parameter(description = "Owner name", required = true) @PathVariable String owner) {
        log.info("REST request to get verified titles for owner: {}", owner);
        return titleRegistryRepository.findVerifiedTitlesByOwner(owner);
    }

    @Operation(summary = "Get all valid titles", description = "Retrieves all valid and verified title registry records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all valid title registry records", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.class)))
    })
    @GetMapping("/valid")
    public Flux<TitleRegistry> getAllValidTitles() {
        log.info("REST request to get all valid titles");
        return titleRegistryRepository.findAllValidTitles();
    }

    @Operation(summary = "Get all title statuses", description = "Retrieves all available title status values")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of title statuses", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TitleRegistry.TitleStatus[].class)))
    })
    @GetMapping("/statuses")
    public Mono<ResponseEntity<TitleRegistry.TitleStatus[]>> getTitleStatuses() {
        log.info("REST request to get all title statuses");
        return Mono.just(ResponseEntity.ok().body(TitleRegistry.TitleStatus.values()));
    }
}
