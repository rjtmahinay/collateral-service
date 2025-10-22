package com.rjtmahinay.collateral.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("collateral")
public class Collateral {

    @Id
    private Long id;

    @Column("collateral_id")
    private String collateralId;

    @Column("customer_id")
    private String customerId;

    @Column("account_id")
    private String accountId;

    @Column("type")
    private CollateralType type;

    @Column("description")
    private String description;

    @Column("estimated_value")
    private BigDecimal estimatedValue;

    @Column("market_value")
    private BigDecimal marketValue;

    @Column("currency")
    private String currency;

    @Column("status")
    private CollateralStatus status;

    @Column("location")
    private String location;

    @Column("evaluation_date")
    private LocalDateTime evaluationDate;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("created_by")
    private String createdBy;

    @Column("updated_by")
    private String updatedBy;

    // Additional metadata for encumbrance tracking
    @Column("available_value")
    private BigDecimal availableValue;

    @Column("encumbered_value")
    private BigDecimal encumberedValue;

    @Column("legal_description")
    private String legalDescription;

    @Column("ownership_documents")
    private String ownershipDocuments;

    @Column("last_inspection_date")
    private LocalDateTime lastInspectionDate;

    @Column("risk_rating")
    private String riskRating;
}
