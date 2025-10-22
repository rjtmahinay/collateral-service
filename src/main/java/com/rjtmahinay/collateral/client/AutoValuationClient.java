package com.rjtmahinay.collateral.client;

import com.rjtmahinay.collateral.model.CollateralType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoValuationClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${external.auto-valuation.base-url:http://localhost:8082}")
    private String autoValuationBaseUrl;
    
    @Value("${external.auto-valuation.timeout:15}")
    private int timeoutSeconds;
    
    public Mono<ValuationResponse> requestValuation(String collateralId, CollateralType type, String location, String description) {
        log.info("Requesting valuation for collateral: {} of type: {} at location: {}", collateralId, type, location);
        
        return webClientBuilder.build()
            .post()
            .uri(autoValuationBaseUrl + "/api/v1/valuation/request")
            .bodyValue(ValuationRequest.builder()
                .collateralId(collateralId)
                .type(type.name())
                .location(location)
                .description(description)
                .requestDate(LocalDateTime.now())
                .build())
            .retrieve()
            .bodyToMono(ValuationResponse.class)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .doOnSuccess(response -> log.info("Valuation completed for collateral: {} - Value: {} {}", 
                collateralId, response.getEstimatedValue(), response.getCurrency()))
            .doOnError(error -> log.error("Valuation failed for collateral: {}", collateralId, error))
            .onErrorReturn(ValuationResponse.builder()
                .collateralId(collateralId)
                .status(ValuationStatus.VALUATION_FAILED)
                .message("Auto valuation service unavailable")
                .build());
    }
    
    public Mono<MarketTrendResponse> getMarketTrends(CollateralType type, String location) {
        log.info("Retrieving market trends for type: {} in location: {}", type, location);
        
        return webClientBuilder.build()
            .get()
            .uri(autoValuationBaseUrl + "/api/v1/market-trends?type={type}&location={location}", type.name(), location)
            .retrieve()
            .bodyToMono(MarketTrendResponse.class)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .doOnSuccess(response -> log.info("Market trends retrieved for type: {} in location: {}", type, location))
            .doOnError(error -> log.error("Failed to retrieve market trends for type: {} in location: {}", type, location, error))
            .onErrorReturn(MarketTrendResponse.builder()
                .type(type.name())
                .location(location)
                .status("UNAVAILABLE")
                .message("Market trends service unavailable")
                .build());
    }
    
    public Mono<ComparableProperty> getComparableProperties(String collateralId, CollateralType type, String location, BigDecimal estimatedValue) {
        log.info("Retrieving comparable properties for collateral: {} of type: {} in location: {}", collateralId, type, location);
        
        return webClientBuilder.build()
            .post()
            .uri(autoValuationBaseUrl + "/api/v1/comparables/search")
            .bodyValue(ComparableSearchRequest.builder()
                .collateralId(collateralId)
                .type(type.name())
                .location(location)
                .estimatedValue(estimatedValue)
                .build())
            .retrieve()
            .bodyToMono(ComparableProperty.class)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .doOnSuccess(response -> log.info("Comparable properties retrieved for collateral: {}", collateralId))
            .doOnError(error -> log.error("Failed to retrieve comparable properties for collateral: {}", collateralId, error))
            .onErrorReturn(ComparableProperty.builder()
                .collateralId(collateralId)
                .status("UNAVAILABLE")
                .message("Comparable properties service unavailable")
                .build());
    }
    
    public Mono<RevaluationResponse> requestRevaluation(String collateralId, String reason) {
        log.info("Requesting revaluation for collateral: {} with reason: {}", collateralId, reason);
        
        return webClientBuilder.build()
            .post()
            .uri(autoValuationBaseUrl + "/api/v1/valuation/revalue")
            .bodyValue(RevaluationRequest.builder()
                .collateralId(collateralId)
                .reason(reason)
                .requestDate(LocalDateTime.now())
                .build())
            .retrieve()
            .bodyToMono(RevaluationResponse.class)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .doOnSuccess(response -> log.info("Revaluation completed for collateral: {} - New Value: {} {}", 
                collateralId, response.getNewValue(), response.getCurrency()))
            .doOnError(error -> log.error("Revaluation failed for collateral: {}", collateralId, error))
            .onErrorReturn(RevaluationResponse.builder()
                .collateralId(collateralId)
                .status(ValuationStatus.VALUATION_FAILED)
                .message("Auto revaluation service unavailable")
                .build());
    }
    
    // DTOs for Auto Valuation API
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ValuationRequest {
        private String collateralId;
        private String type;
        private String location;
        private String description;
        private LocalDateTime requestDate;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ValuationResponse {
        private String collateralId;
        private ValuationStatus status;
        private BigDecimal estimatedValue;
        private BigDecimal lowRange;
        private BigDecimal highRange;
        private String currency;
        private String methodology;
        private LocalDateTime valuationDate;
        private String message;
        private Double confidenceScore;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MarketTrendResponse {
        private String type;
        private String location;
        private String status;
        private String message;
        private BigDecimal averageValue;
        private Double priceChange;
        private String trendDirection;
        private LocalDateTime analysisDate;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ComparableSearchRequest {
        private String collateralId;
        private String type;
        private String location;
        private BigDecimal estimatedValue;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ComparableProperty {
        private String collateralId;
        private String status;
        private String message;
        private java.util.List<PropertyComparable> comparables;
    }
    
    @lombok.Data
    @lombok.Builder  
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PropertyComparable {
        private String propertyId;
        private String address;
        private String type;
        private BigDecimal value;
        private LocalDateTime saleDate;
        private Double similarityScore;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RevaluationRequest {
        private String collateralId;
        private String reason;
        private LocalDateTime requestDate;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RevaluationResponse {
        private String collateralId;
        private ValuationStatus status;
        private BigDecimal previousValue;
        private BigDecimal newValue;
        private String currency;
        private String reason;
        private LocalDateTime revaluationDate;
        private String message;
        private Double valueChangePercentage;
    }
    
    public enum ValuationStatus {
        VALUATION_COMPLETED,
        VALUATION_PENDING,
        VALUATION_FAILED,
        INSUFFICIENT_DATA,
        UNDER_REVIEW
    }
}
