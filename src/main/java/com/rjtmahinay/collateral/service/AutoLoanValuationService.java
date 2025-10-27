package com.rjtmahinay.collateral.service;

import com.rjtmahinay.collateral.dto.AutoLoanDto.*;
import com.rjtmahinay.collateral.model.AutoValuation;
import com.rjtmahinay.collateral.model.CollateralType;
import com.rjtmahinay.collateral.repository.AutoValuationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoLoanValuationService {

    private final AutoValuationRepository autoValuationRepository;

    public Mono<VehicleAppraisalResponse> performVehicleAppraisal(VehicleAppraisalRequest request) {
        log.info("Processing vehicle appraisal for VIN: {} - {} {} {}",
                request.getVin(), request.getYear(), request.getMake(), request.getModel());

        // Generate a collateral ID from VIN for internal use
        String collateralId = "COL-VIN-" + request.getVin();

        // Create and save auto valuation record
        AutoValuation autoValuation = AutoValuation.builder()
                .valuationId(UUID.randomUUID().toString())
                .collateralId(collateralId)
                .type(CollateralType.VEHICLE.name())
                .location(request.getZipCode())
                .description(buildVehicleDescription(request))
                .status(AutoValuation.ValuationStatus.VALUATION_COMPLETED)
                .estimatedValue(calculateEstimatedValue(request.getYear(), request.getMake(), request.getModel()))
                .currency("USD")
                .methodology("Database-based vehicle appraisal")
                .confidenceScore(0.90)
                .valuationDate(LocalDateTime.now())
                .requestDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .message("Vehicle appraisal completed successfully")
                .build();

        return autoValuationRepository.save(autoValuation)
                .map(valuation -> convertAutoValuationToVehicleAppraisalResponse(valuation, request))
                .onErrorReturn(buildErrorResponse(collateralId));
    }

    public Mono<VehicleMarketAnalysisResponse> analyzeVehicleMarket(String make, String model, Integer year,
            String zipCode) {
        log.info("Analyzing vehicle market for {} {} {} in {}", year, make, model, zipCode);

        return autoValuationRepository
                .findByTypeAndLocationOrderByValuationDateDesc(CollateralType.VEHICLE.name(), zipCode)
                .collectList()
                .map(valuations -> {
                    BigDecimal averageValue = valuations.stream()
                            .map(AutoValuation::getEstimatedValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(Math.max(1, valuations.size())), 2,
                                    java.math.RoundingMode.HALF_UP);

                    return VehicleMarketAnalysisResponse.builder()
                            .make(make)
                            .model(model)
                            .year(year)
                            .zipCode(zipCode)
                            .status("SUCCESS")
                            .message("Vehicle market analysis completed")
                            .averageMarketValue(averageValue)
                            .priceChangePercent(5.2) // Mock value
                            .demandLevel(determineDemandLevel(make))
                            .averageDaysOnMarket(calculateAverageDaysOnMarket(make, model))
                            .seasonalTrend(getCurrentSeasonalTrend())
                            .analysisDate(LocalDateTime.now())
                            .build();
                });
    }

    public Mono<VehicleComparableSalesResponse> findComparableVehicleSales(VehicleComparableRequest request) {
        log.info("Finding comparable sales for {} {} {} with {} miles",
                request.getYear(), request.getMake(), request.getModel(), request.getMileage());

        BigDecimal estimatedValue = calculateEstimatedValue(request.getYear(), request.getMake(), request.getModel());

        return autoValuationRepository
                .findByTypeAndLocationOrderByValuationDateDesc(CollateralType.VEHICLE.name(), request.getZipCode())
                .take(5) // Limit to 5 comparables
                .collectList()
                .map(valuations -> {
                    List<VehicleComparable> vehicleComparables = valuations.stream()
                            .map(val -> VehicleComparable.builder()
                                    .vin("VIN" + val.getValuationId().substring(0, 8))
                                    .salePrice(val.getEstimatedValue())
                                    .saleDate(val.getValuationDate())
                                    .location(val.getLocation())
                                    .similarityScore(0.85)
                                    .build())
                            .toList();

                    return VehicleComparableSalesResponse.builder()
                            .collateralId(request.getCollateralId())
                            .status("SUCCESS")
                            .message("Comparable sales found")
                            .comparables(vehicleComparables)
                            .build();
                });
    }

    public Mono<LoanToValueResponse> calculateAutoLoanLTV(LoanToValueRequest request) {
        log.info("Calculating auto loan LTV for collateral: {} - Loan: {}, Value: {}",
                request.getCollateralId(), request.getLoanAmount(), request.getVehicleValue());

        if (request.getVehicleValue().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.just(LoanToValueResponse.builder()
                    .collateralId(request.getCollateralId())
                    .status("ERROR")
                    .message("Vehicle value must be greater than zero")
                    .build());
        }

        BigDecimal ltvRatio = request.getLoanAmount()
                .divide(request.getVehicleValue(), 4, java.math.RoundingMode.HALF_UP);

        return Mono.just(LoanToValueResponse.builder()
                .collateralId(request.getCollateralId())
                .loanAmount(request.getLoanAmount())
                .vehicleValue(request.getVehicleValue())
                .ltvRatio(ltvRatio)
                .ltvPercentage(ltvRatio.multiply(BigDecimal.valueOf(100)))
                .riskAssessment(assessAutoLoanRisk(ltvRatio))
                .approved(isAutoLoanApproved(ltvRatio))
                .maxRecommendedLoan(calculateMaxAutoLoan(request.getVehicleValue()))
                .status("SUCCESS")
                .message("Auto loan LTV calculation completed")
                .calculationDate(LocalDateTime.now())
                .build());
    }

    public Mono<DepreciationForecastResponse> forecastVehicleDepreciation(DepreciationForecastRequest request) {
        log.info("Forecasting vehicle depreciation for {} {} {} over {} months",
                request.getYear(), request.getMake(), request.getModel(), request.getForecastMonths());

        List<MonthlyDepreciation> forecast = generateVehicleDepreciationForecast(
                request.getCurrentValue(),
                request.getForecastMonths(),
                request.getYear(),
                request.getMake());

        BigDecimal finalValue = forecast.get(forecast.size() - 1).getProjectedValue();
        BigDecimal totalDepreciation = request.getCurrentValue().subtract(finalValue);
        BigDecimal depreciationPercentage = totalDepreciation
                .divide(request.getCurrentValue(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return Mono.just(DepreciationForecastResponse.builder()
                .collateralId(request.getCollateralId())
                .currentValue(request.getCurrentValue())
                .projectedValue(finalValue)
                .totalDepreciation(totalDepreciation)
                .depreciationPercentage(depreciationPercentage)
                .forecastMonths(request.getForecastMonths())
                .monthlyForecast(forecast)
                .status("SUCCESS")
                .message("Vehicle depreciation forecast completed")
                .forecastDate(LocalDateTime.now())
                .build());
    }

    // Private helper methods
    private String buildVehicleDescription(VehicleAppraisalRequest request) {
        return String.format("%d %s %s %s, VIN: %s, Mileage: %d",
                request.getYear(),
                request.getMake(),
                request.getModel(),
                request.getTrim() != null ? request.getTrim() : "",
                request.getVin(),
                request.getMileage() != null ? request.getMileage() : 0);
    }

    private VehicleAppraisalResponse convertAutoValuationToVehicleAppraisalResponse(AutoValuation valuation,
            VehicleAppraisalRequest request) {
        return VehicleAppraisalResponse.builder()
                .collateralId(valuation.getCollateralId())
                .vin(request.getVin())
                .year(request.getYear())
                .make(request.getMake())
                .model(request.getModel())
                .status(convertStatus(valuation.getStatus()))
                .marketValue(valuation.getEstimatedValue())
                .loanValue(calculateAutoLoanValue(valuation.getEstimatedValue()))
                .currency(valuation.getCurrency())
                .appraisalDate(valuation.getValuationDate())
                .message(valuation.getMessage())
                .condition(request.getCondition())
                .build();
    }

    private VehicleAppraisalResponse buildErrorResponse(String collateralId) {
        return VehicleAppraisalResponse.builder()
                .collateralId(collateralId)
                .status(VehicleValuationStatus.APPRAISAL_FAILED)
                .message("Vehicle appraisal service temporarily unavailable")
                .build();
    }

    private VehicleValuationStatus convertStatus(AutoValuation.ValuationStatus status) {
        return switch (status) {
            case VALUATION_COMPLETED -> VehicleValuationStatus.APPRAISAL_COMPLETED;
            case VALUATION_PENDING -> VehicleValuationStatus.APPRAISAL_PENDING;
            case VALUATION_FAILED -> VehicleValuationStatus.APPRAISAL_FAILED;
            case INSUFFICIENT_DATA -> VehicleValuationStatus.INSUFFICIENT_DATA;
            case UNDER_REVIEW -> VehicleValuationStatus.UNDER_REVIEW;
        };
    }

    private BigDecimal calculateAutoLoanValue(BigDecimal marketValue) {
        // Auto loans typically have 80-85% LTV
        return marketValue.multiply(BigDecimal.valueOf(0.80));
    }

    private String determineDemandLevel(String make) {
        // Popular auto brands have higher demand
        if (List.of("TOYOTA", "HONDA", "LEXUS", "ACURA").contains(make.toUpperCase())) {
            return "HIGH";
        } else if (List.of("FORD", "CHEVROLET", "NISSAN", "HYUNDAI").contains(make.toUpperCase())) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private Integer calculateAverageDaysOnMarket(String make, String model) {
        // Popular vehicles sell faster
        String demandLevel = determineDemandLevel(make);
        return switch (demandLevel) {
            case "HIGH" -> 25;
            case "MEDIUM" -> 35;
            default -> 50;
        };
    }

    private String getCurrentSeasonalTrend() {
        int month = LocalDateTime.now().getMonthValue();
        if (month >= 3 && month <= 5)
            return "SPRING_BUYING_SEASON";
        if (month >= 6 && month <= 8)
            return "SUMMER_PEAK";
        if (month >= 9 && month <= 11)
            return "FALL_CLEARANCE";
        return "WINTER_SLOW";
    }

    private BigDecimal calculateEstimatedValue(Integer year, String make, String model) {
        int currentYear = LocalDateTime.now().getYear();
        int age = currentYear - year;
        BigDecimal baseValue = BigDecimal.valueOf(25000); // Base value

        // Adjust for brand
        String demandLevel = determineDemandLevel(make);
        BigDecimal brandMultiplier = switch (demandLevel) {
            case "HIGH" -> BigDecimal.valueOf(1.2);
            case "MEDIUM" -> BigDecimal.valueOf(1.0);
            default -> BigDecimal.valueOf(0.8);
        };

        // Apply age depreciation
        BigDecimal ageMultiplier = BigDecimal.valueOf(Math.max(0.2, 1 - (age * 0.12)));

        return baseValue.multiply(brandMultiplier).multiply(ageMultiplier);
    }

    private String assessAutoLoanRisk(BigDecimal ltvRatio) {
        if (ltvRatio.compareTo(BigDecimal.valueOf(0.70)) <= 0)
            return "LOW_RISK";
        if (ltvRatio.compareTo(BigDecimal.valueOf(0.80)) <= 0)
            return "MEDIUM_RISK";
        if (ltvRatio.compareTo(BigDecimal.valueOf(0.85)) <= 0)
            return "HIGH_RISK";
        return "EXCESSIVE_RISK";
    }

    private Boolean isAutoLoanApproved(BigDecimal ltvRatio) {
        return ltvRatio.compareTo(BigDecimal.valueOf(0.85)) <= 0; // Max 85% LTV for auto loans
    }

    private BigDecimal calculateMaxAutoLoan(BigDecimal vehicleValue) {
        return vehicleValue.multiply(BigDecimal.valueOf(0.85)); // 85% max LTV
    }

    private List<MonthlyDepreciation> generateVehicleDepreciationForecast(BigDecimal currentValue, Integer months,
            Integer year, String make) {
        List<MonthlyDepreciation> forecast = new java.util.ArrayList<>();
        BigDecimal value = currentValue;

        // Vehicle-specific depreciation rates
        double monthlyDepreciationRate = getVehicleDepreciationRate(year, make);

        for (int i = 1; i <= months; i++) {
            value = value.multiply(BigDecimal.valueOf(1 - monthlyDepreciationRate));
            forecast.add(MonthlyDepreciation.builder()
                    .month(i)
                    .projectedValue(value)
                    .depreciationAmount(currentValue.subtract(value))
                    .build());
        }

        return forecast;
    }

    private double getVehicleDepreciationRate(Integer year, String make) {
        int currentYear = LocalDateTime.now().getYear();
        int age = currentYear - year;

        // Base depreciation rate
        double baseRate = 0.008; // 0.8% per month

        // Adjust for vehicle age (newer cars depreciate faster)
        if (age < 2)
            baseRate = 0.015; // 1.5% for new cars
        else if (age < 5)
            baseRate = 0.010; // 1.0% for relatively new cars

        // Adjust for brand (luxury and reliable brands depreciate differently)
        String demandLevel = determineDemandLevel(make);
        return switch (demandLevel) {
            case "HIGH" -> baseRate * 0.8; // Slower depreciation for popular brands
            case "MEDIUM" -> baseRate;
            default -> baseRate * 1.2; // Faster depreciation for less popular brands
        };
    }
}
