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
@Table("encumbrance")
public class Encumbrance {

    @Id
    private Long id;

    @Column("encumbrance_id")
    private String encumbranceId;

    @Column("collateral_id")
    private String collateralId;

    @Column("loan_id")
    private String loanId;

    @Column("customer_id")
    private String customerId;

    @Column("amount")
    private BigDecimal amount;

    @Column("currency")
    private String currency;

    @Column("type")
    private EncumbranceType type;

    @Column("status")
    private EncumbranceStatus status;

    @Column("effective_date")
    private LocalDateTime effectiveDate;

    @Column("expiry_date")
    private LocalDateTime expiryDate;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("created_by")
    private String createdBy;

    @Column("updated_by")
    private String updatedBy;

    @Column("description")
    private String description;

    @Column("priority")
    private Integer priority;

    @Column("legal_reference")
    private String legalReference;

    @Column("notes")
    private String notes;
}
