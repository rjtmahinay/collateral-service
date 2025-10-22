package com.rjtmahinay.collateral.model;

public enum CollateralType {
    VEHICLE("Vehicle");

    private final String displayName;

    CollateralType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
