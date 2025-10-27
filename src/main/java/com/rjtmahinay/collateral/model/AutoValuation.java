package com.rjtmahinay.collateral.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("auto_valuation")
public class AutoValuation {

    @Id
    private Long id;

    @Column("valuation_id")
    private String valuationId;

    @Column("collateral_id")
    private String collateralId;

    @Column("type")
    private String type;

    @Column("location")
    private String location;

    @Column("description")
    private String description;

    @Column("status")
    private ValuationStatus status;

    @Column("estimated_value")
    private BigDecimal estimatedValue;

    @Column("low_range")
    private BigDecimal lowRange;

    @Column("high_range")
    private BigDecimal highRange;

    @Column("currency")
    private String currency;

    @Column("methodology")
    private String methodology;

    @Column("confidence_score")
    private Double confidenceScore;

    @Column("valuation_date")
    private LocalDateTime valuationDate;

    @Column("request_date")
    private LocalDateTime requestDate;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("created_by")
    private String createdBy;

    @Column("updated_by")
    private String updatedBy;

    @Column("message")
    private String message;

    public enum ValuationStatus {
        VALUATION_COMPLETED,
        VALUATION_PENDING,
        VALUATION_FAILED,
        INSUFFICIENT_DATA,
        UNDER_REVIEW
    }
}
