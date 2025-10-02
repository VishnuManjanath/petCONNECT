package com.petconnect.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lifestyle_profiles")
@Data
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
public class LifestyleProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "living_situation", nullable = false, length = 100)
    @NotBlank(message = "Living situation is required")
    @Size(max = 100, message = "Living situation must not exceed 100 characters")
    private String livingSituation;

    @Column(name = "yard_size", length = 50)
    @Size(max = 50, message = "Yard size must not exceed 50 characters")
    private String yardSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false)
    @NotNull(message = "Activity level is required")
    private EnergyLevel activityLevel;

    @Column(name = "experience_level", nullable = false, length = 50)
    @NotBlank(message = "Experience level is required")
    @Size(max = 50, message = "Experience level must not exceed 50 characters")
    private String experienceLevel;

    @Column(name = "time_availability", nullable = false)
    @NotNull(message = "Time availability is required")
    @Min(value = 1, message = "Time availability must be at least 1 hour per day")
    @Max(value = 24, message = "Time availability cannot exceed 24 hours per day")
    private Integer timeAvailability;

    @Column(name = "has_children", nullable = false)
    private Boolean hasChildren = false;

    @Column(name = "children_ages", length = 100)
    @Size(max = 100, message = "Children ages must not exceed 100 characters")
    private String childrenAges;

    @Column(name = "has_other_pets", nullable = false)
    private Boolean hasOtherPets = false;

    @Column(name = "other_pets_description", columnDefinition = "TEXT")
    @Size(max = 500, message = "Other pets description must not exceed 500 characters")
    private String otherPetsDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_pet_age")
    private PetAgeGroup preferredPetAge;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_pet_size")
    private PetSize preferredPetSize;

    @Column(name = "max_adoption_fee", precision = 8, scale = 2)
    @DecimalMin(value = "0.00", message = "Maximum adoption fee must be positive")
    @DecimalMax(value = "999999.99", message = "Maximum adoption fee must be realistic")
    private BigDecimal maxAdoptionFee;

    @Column(name = "special_requirements", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Special requirements must not exceed 1000 characters")
    private String specialRequirements;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "User is required")
    private User user;

    // Helper methods
    public String getActivityLevelDisplay() {
        return switch (activityLevel) {
            case LOW -> "Low - Prefer calm, quiet pets";
            case MODERATE -> "Moderate - Enjoy some activity";
            case HIGH -> "High - Love active pets";
            case VERY_HIGH -> "Very High - Want very energetic pets";
        };
    }

    public String getExperienceLevelDisplay() {
        return switch (experienceLevel.toLowerCase()) {
            case "first_time" -> "First-time pet owner";
            case "some_experience" -> "Some experience with pets";
            case "very_experienced" -> "Very experienced with pets";
            default -> experienceLevel;
        };
    }

    public boolean isCompatibleWithPetSize(PetSize petSize) {
        if (preferredPetSize == null) {
            return true; // No preference means all sizes are acceptable
        }
        return preferredPetSize == petSize;
    }

    public boolean isCompatibleWithPetAge(PetAgeGroup petAge) {
        if (preferredPetAge == null) {
            return true; // No preference means all ages are acceptable
        }
        return preferredPetAge == petAge;
    }

    public boolean canAfford(BigDecimal adoptionFee) {
        if (maxAdoptionFee == null) {
            return true; // No budget limit specified
        }
        return maxAdoptionFee.compareTo(adoptionFee) >= 0;
    }
}
