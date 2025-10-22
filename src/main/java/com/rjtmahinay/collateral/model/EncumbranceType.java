package com.rjtmahinay.collateral.model;

public enum EncumbranceType {
    MORTGAGE("Mortgage"),
    LIEN("Lien"),
    PLEDGE("Pledge"),
    SECURITY_INTEREST("Security Interest"),
    CHARGE("Charge"),
    HYPOTHECATION("Hypothecation"),
    ASSIGNMENT("Assignment"),
    GUARANTEE("Guarantee"),
    FLOATING_CHARGE("Floating Charge"),
    FIXED_CHARGE("Fixed Charge"),
    DEED_OF_TRUST("Deed of Trust"),
    OTHER("Other");

    private final String displayName;

    EncumbranceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
