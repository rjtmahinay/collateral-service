package com.rjtmahinay.collateral.model;

public enum CollateralStatus {
    ACTIVE("Active"),
    PENDING_EVALUATION("Pending Evaluation"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    ENCUMBERED("Encumbered"),
    PARTIALLY_ENCUMBERED("Partially Encumbered"),
    RELEASED("Released"),
    LIQUIDATED("Liquidated"),
    SUSPENDED("Suspended"),
    EXPIRED("Expired"),
    INACTIVE("Inactive");
    
    private final String displayName;
    
    CollateralStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
