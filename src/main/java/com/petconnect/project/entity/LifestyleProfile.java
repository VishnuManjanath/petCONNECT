package com.petconnect.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a user's lifestyle profile for pet matching
 */
@Entity
@Table(name = "lifestyle_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LifestyleProfile {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "Living situation is required")
    @Size(max = 100, message = "Living situation must not exceed 100 characters")
    @Column(name = "living_situation", nullable = false, length = 100)
    private String livingSituation; // apartment, house, farm, etc.

    @Size(max = 50, message = "Yard size must not exceed 50 characters")
    @Column(name = "yard_size", length = 50)
    private String yardSize; // none, small, medium, large

    @NotNull(message = "Activity level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false)
    private EnergyLevel activityLevel;

    @NotBlank(message = "Experience level is required")
    @Size(max = 50, message = "Experience level must not exceed 50 characters")
    @Column(name = "experience_level", nullable = false, length = 50)
    private String experienceLevel; // first_time, some_experience, very_experienced

    @NotNull(message = "Time availability is required")
    @Min(value = 1, message = "Time availability must be at least 1 hour")
    @Max(value = 24, message = "Time availability must not exceed 24 hours")
    @Column(name = "time_availability", nullable = false)
    private Integer timeAvailability; // hours per day

    @Column(name = "has_children", nullable = false)
    private Boolean hasChildren = false;

    @Size(max = 100, message = "Children ages must not exceed 100 characters")
    @Column(name = "children_ages", length = 100)
    private String childrenAges; // age ranges if hasChildren is true

    @Column(name = "has_other_pets", nullable = false)
    private Boolean hasOtherPets = false;

    @Size(max = 500, message = "Other pets description must not exceed 500 characters")
    @Column(name = "other_pets_description", columnDefinition = "TEXT")
    private String otherPetsDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_pet_age")
    private PetAgeGroup preferredPetAge;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_pet_size")
    private PetSize preferredPetSize;

    @DecimalMin(value = "0.0", message = "Maximum adoption fee must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Maximum adoption fee must be a valid monetary amount")
    @Column(name = "max_adoption_fee", precision = 8, scale = 2)
    private BigDecimal maxAdoptionFee;

    @Size(max = 1000, message = "Special requirements must not exceed 1000 characters")
    @Column(name = "special_requirements", columnDefinition = "TEXT")
    private String specialRequirements;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods for compatibility calculations
    public boolean isCompatibleWithPetSize(PetSize petSize) {
        if (preferredPetSize == null) return true;
        return preferredPetSize.equals(petSize);
    }

    public boolean isCompatibleWithPetAge(PetAgeGroup petAge) {
        if (preferredPetAge == null) return true;
        return preferredPetAge.equals(petAge);
    }

    public boolean canAfford(BigDecimal adoptionFee) {
        if (maxAdoptionFee == null || adoptionFee == null) return true;
        return maxAdoptionFee.compareTo(adoptionFee) >= 0;
    }

    public int getExperienceScore() {
        return switch (experienceLevel.toLowerCase()) {
            case "very_experienced" -> 5;
            case "some_experience" -> 3;
            case "first_time" -> 1;
            default -> 2;
        };
    }

    public int getYardScore() {
        if (yardSize == null) return 1;
        return switch (yardSize.toLowerCase()) {
            case "large" -> 5;
            case "medium" -> 4;
            case "small" -> 2;
            case "none" -> 1;
            default -> 2;
        };
    }
}


