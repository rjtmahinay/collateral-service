package com.rjtmahinay.collateral.controller;

import com.rjtmahinay.collateral.dto.AutoLoanDto.*;
import com.rjtmahinay.collateral.service.AutoLoanValuationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auto-loan/valuation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auto Loan Valuation", description = "APIs for vehicle appraisal, market analysis, and loan-to-value calculations")
public class AutoLoanValuationController {

    private final AutoLoanValuationService autoLoanValuationService;

    @Operation(summary = "Appraise vehicle value", description = "Performs a comprehensive appraisal of a vehicle based on VIN and other details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle appraisal completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleAppraisalResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle information")
    })
    @PostMapping("/vehicle/appraise")
    public Mono<ResponseEntity<VehicleAppraisalResponse>> appraiseVehicle(
            @Parameter(description = "Vehicle appraisal request details", required = true) @RequestBody VehicleAppraisalRequest request) {
        log.info("Vehicle appraisal request for VIN: {} - {} {} {}",
                request.getVin(), request.getYear(), request.getMake(), request.getModel());

        return autoLoanValuationService.performVehicleAppraisal(request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/vehicle/market-analysis")
    public Mono<ResponseEntity<VehicleMarketAnalysisResponse>> getVehicleMarketAnalysis(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year,
            @RequestParam String zipCode) {
        log.info("Vehicle market analysis for {} {} {} in area: {}", year, make, model, zipCode);

        return autoLoanValuationService.analyzeVehicleMarket(make, model, year, zipCode)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/vehicle/comparable-sales")
    public Mono<ResponseEntity<VehicleComparableSalesResponse>> findComparableSales(
            @RequestBody VehicleComparableRequest request) {
        log.info("Finding comparable sales for {} {} {} with {} miles",
                request.getYear(), request.getMake(), request.getModel(), request.getMileage());

        return autoLoanValuationService.findComparableVehicleSales(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Calculate loan-to-value ratio", description = "Calculates the loan-to-value ratio for an auto loan against vehicle value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LTV calculation completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanToValueResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid loan or vehicle value data")
    })
    @PostMapping("/loan-to-value/calculate")
    public Mono<ResponseEntity<LoanToValueResponse>> calculateLoanToValue(
            @Parameter(description = "Loan-to-value calculation request", required = true) @RequestBody LoanToValueRequest request) {
        log.info("Calculating LTV for loan amount: {} against vehicle value: {}",
                request.getLoanAmount(), request.getVehicleValue());

        return autoLoanValuationService.calculateAutoLoanLTV(request)
                .map(response -> {
                    if ("ERROR".equals(response.getStatus())) {
                        return ResponseEntity.badRequest().body(response);
                    }
                    return ResponseEntity.ok(response);
                });
    }

    @PostMapping("/depreciation/forecast")
    public Mono<ResponseEntity<DepreciationForecastResponse>> forecastDepreciation(
            @RequestBody DepreciationForecastRequest request) {
        log.info("Forecasting depreciation for {} {} {} over {} months",
                request.getYear(), request.getMake(), request.getModel(), request.getForecastMonths());

        return autoLoanValuationService.forecastVehicleDepreciation(request)
                .map(ResponseEntity::ok);
    }
}
