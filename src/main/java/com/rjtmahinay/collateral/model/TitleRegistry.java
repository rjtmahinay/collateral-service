package com.rjtmahinay.collateral.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("title_registry")
public class TitleRegistry {

    @Id
    private Long id;

    @Column("title_id")
    private String titleId;

    @Column("collateral_id")
    private String collateralId;

    @Column("title_number")
    private String titleNumber;

    @Column("legal_description")
    private String legalDescription;

    @Column("status")
    private TitleStatus status;

    @Column("current_owner")
    private String currentOwner;

    @Column("previous_owner")
    private String previousOwner;

    @Column("registration_date")
    private LocalDateTime registrationDate;

    @Column("is_valid")
    private Boolean isValid;

    @Column("verification_date")
    private LocalDateTime verificationDate;

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

    @Column("notes")
    private String notes;

    public enum TitleStatus {
        VERIFIED,
        INVALID,
        PENDING_VERIFICATION,
        VERIFICATION_FAILED,
        NOT_FOUND
    }
}
