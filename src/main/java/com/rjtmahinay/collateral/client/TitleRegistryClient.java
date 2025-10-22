package com.rjtmahinay.collateral.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class TitleRegistryClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${external.title-registry.base-url:http://localhost:8081}")
    private String titleRegistryBaseUrl;
    
    @Value("${external.title-registry.timeout:10}")
    private int timeoutSeconds;
    
    public Mono<TitleVerificationResponse> verifyTitle(String collateralId, String legalDescription) {
        log.info("Verifying title for collateral: {} with legal description: {}", collateralId, legalDescription);
        
        return webClientBuilder.build()
            .post()
            .uri(titleRegistryBaseUrl + "/api/v1/title/verify")
            .bodyValue(TitleVerificationRequest.builder()
                .collateralId(collateralId)
                .legalDescription(legalDescription)
                .build())
            .retrieve()
            .bodyToMono(TitleVerificationResponse.class)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .doOnSuccess(response -> log.info("Title verification completed for collateral: {} - Status: {}", 
                collateralId, response.getStatus()))
            .doOnError(error -> log.error("Title verification failed for collateral: {}", collateralId, error))
            .onErrorReturn(TitleVerificationResponse.builder()
                .collateralId(collateralId)
                .status(TitleStatus.VERIFICATION_FAILED)
                .message("Title verification service unavailable")
                .build());
    }
    
    public Mono<OwnershipDetails> getOwnershipDetails(String collateralId, String titleNumber) {
        log.info("Retrieving ownership details for collateral: {} with title number: {}", collateralId, titleNumber);
        
        return webClientBuilder.build()
            .get()
            .uri(titleRegistryBaseUrl + "/api/v1/title/{titleNumber}/ownership", titleNumber)
            .retrieve()
            .bodyToMono(OwnershipDetails.class)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .doOnSuccess(details -> log.info("Ownership details retrieved for collateral: {}", collateralId))
            .doOnError(error -> log.error("Failed to retrieve ownership details for collateral: {}", collateralId, error))
            .onErrorReturn(OwnershipDetails.builder()
                .collateralId(collateralId)
                .status("UNAVAILABLE")
                .message("Ownership details service unavailable")
                .build());
    }
    
    public Mono<EncumbranceSearchResult> searchExistingEncumbrances(String collateralId, String titleNumber) {
        log.info("Searching existing encumbrances for collateral: {} with title number: {}", collateralId, titleNumber);
        
        return webClientBuilder.build()
            .get()
            .uri(titleRegistryBaseUrl + "/api/v1/title/{titleNumber}/encumbrances", titleNumber)
            .retrieve()
            .bodyToMono(EncumbranceSearchResult.class)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .doOnSuccess(result -> log.info("Encumbrance search completed for collateral: {} - Found: {} encumbrances", 
                collateralId, result.getEncumbrances().size()))
            .doOnError(error -> log.error("Encumbrance search failed for collateral: {}", collateralId, error))
            .onErrorReturn(EncumbranceSearchResult.builder()
                .collateralId(collateralId)
                .status("SEARCH_FAILED")
                .message("Encumbrance search service unavailable")
                .build());
    }
    
    // DTOs for Title Registry API
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TitleVerificationRequest {
        private String collateralId;
        private String legalDescription;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TitleVerificationResponse {
        private String collateralId;
        private TitleStatus status;
        private String titleNumber;
        private String message;
        private boolean isValid;
        private String registeredOwner;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OwnershipDetails {
        private String collateralId;
        private String titleNumber;
        private String currentOwner;
        private String previousOwner;
        private String registrationDate;
        private String status;
        private String message;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EncumbranceSearchResult {
        private String collateralId;
        private String titleNumber;
        private String status;
        private String message;
        private java.util.List<ExistingEncumbrance> encumbrances;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ExistingEncumbrance {
        private String encumbranceId;
        private String type;
        private String amount;
        private String priority;
        private String registrationDate;
        private String status;
    }
    
    public enum TitleStatus {
        VERIFIED,
        INVALID,
        PENDING_VERIFICATION,
        VERIFICATION_FAILED,
        NOT_FOUND
    }
}
