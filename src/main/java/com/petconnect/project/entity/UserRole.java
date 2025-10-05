package com.petconnect.project.entity;

public enum UserRole {
    ADOPTER("Pet Adopter"),
    SHELTER_ADMIN("Shelter Administrator"),
    ADMIN("System Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

