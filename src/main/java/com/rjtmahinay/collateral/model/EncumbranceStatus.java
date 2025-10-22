package com.rjtmahinay.collateral.model;

public enum EncumbranceStatus {
    PENDING("Pending"),
    ACTIVE("Active"),
    RELEASED("Released"),
    PARTIALLY_RELEASED("Partially Released"),
    SUSPENDED("Suspended"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled"),
    DEFAULTED("Defaulted"),
    UNDER_REVIEW("Under Review"),
    TRANSFERRED("Transferred"),
    MODIFIED("Modified"),
    TERMINATED("Terminated");

    private final String displayName;

    EncumbranceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
