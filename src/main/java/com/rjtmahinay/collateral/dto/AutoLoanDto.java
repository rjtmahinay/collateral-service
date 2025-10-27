package com.rjtmahinay.collateral.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AutoLoanDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleAppraisalRequest {
        private String vin;
        private Integer year;
        private String make;
        private String model;
        private String trim;
        private Integer mileage;
        private String condition;
        private String zipCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleAppraisalResponse {
        private String collateralId;
        private String vin;
        private Integer year;
        private String make;
        private String model;
        private VehicleValuationStatus status;
        private BigDecimal marketValue;
        private BigDecimal loanValue;
        private Double depreciationRate;
        private String currency;
        private LocalDateTime appraisalDate;
        private String message;
        private String condition;
        private BigDecimal estimatedMonthlyDepreciation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleMarketAnalysisResponse {
        private String make;
        private String model;
        private Integer year;
        private String zipCode;
        private String status;
        private String message;
        private BigDecimal averageMarketValue;
        private Double priceChangePercent;
        private String demandLevel;
        private Integer averageDaysOnMarket;
        private String seasonalTrend;
        private LocalDateTime analysisDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleComparableRequest {
        private String collateralId;
        private Integer year;
        private String make;
        private String model;
        private Integer mileage;
        private String zipCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleComparableSalesResponse {
        private String collateralId;
        private String status;
        private String message;
        private List<VehicleComparable> comparables;
        private BigDecimal averageComparablePrice;
        private BigDecimal priceRangeHigh;
        private BigDecimal priceRangeLow;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleComparable {
        private String vin;
        private Integer year;
        private String make;
        private String model;
        private Integer mileage;
        private BigDecimal salePrice;
        private LocalDateTime saleDate;
        private String location;
        private String condition;
        private Double similarityScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanToValueRequest {
        private String collateralId;
        private BigDecimal loanAmount;
        private BigDecimal vehicleValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanToValueResponse {
        private String collateralId;
        private BigDecimal loanAmount;
        private BigDecimal vehicleValue;
        private BigDecimal ltvRatio;
        private BigDecimal ltvPercentage;
        private String riskAssessment;
        private Boolean approved;
        private BigDecimal maxRecommendedLoan;
        private String status;
        private String message;
        private LocalDateTime calculationDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepreciationForecastRequest {
        private String collateralId;
        private Integer year;
        private String make;
        private String model;
        private BigDecimal currentValue;
        private Integer forecastMonths;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepreciationForecastResponse {
        private String collateralId;
        private BigDecimal currentValue;
        private BigDecimal projectedValue;
        private BigDecimal totalDepreciation;
        private BigDecimal depreciationPercentage;
        private Integer forecastMonths;
        private List<MonthlyDepreciation> monthlyForecast;
        private String status;
        private String message;
        private LocalDateTime forecastDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyDepreciation {
        private Integer month;
        private BigDecimal projectedValue;
        private BigDecimal depreciationAmount;
    }

    public enum VehicleValuationStatus {
        APPRAISAL_COMPLETED,
        APPRAISAL_PENDING,
        APPRAISAL_FAILED,
        INVALID_VEHICLE_TYPE,
        INSUFFICIENT_DATA,
        UNDER_REVIEW
    }
}
