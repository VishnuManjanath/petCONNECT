package com.petconnect.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "personality_profiles")
@Data
@EqualsAndHashCode(exclude = {"pet"})
@ToString(exclude = {"pet"})
public class PersonalityProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "energy_level", nullable = false)
    @NotNull(message = "Energy level is required")
    private EnergyLevel energyLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Sociability is required")
    private SociabilityLevel sociability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Trainability is required")
    private TrainabilityLevel trainability;

    @Column(name = "independence_level", nullable = false)
    @NotNull(message = "Independence level is required")
    @Min(value = 1, message = "Independence level must be between 1 and 5")
    @Max(value = 5, message = "Independence level must be between 1 and 5")
    private Integer independenceLevel;

    @Column(name = "playfulness_level", nullable = false)
    @NotNull(message = "Playfulness level is required")
    @Min(value = 1, message = "Playfulness level must be between 1 and 5")
    @Max(value = 5, message = "Playfulness level must be between 1 and 5")
    private Integer playfulnessLevel;

    @Column(name = "affection_level", nullable = false)
    @NotNull(message = "Affection level is required")
    @Min(value = 1, message = "Affection level must be between 1 and 5")
    @Max(value = 5, message = "Affection level must be between 1 and 5")
    private Integer affectionLevel;

    @Column(name = "exercise_needs", nullable = false)
    @NotNull(message = "Exercise needs is required")
    @Min(value = 0, message = "Exercise needs must be positive")
    @Max(value = 480, message = "Exercise needs must be realistic (max 8 hours)")
    private Integer exerciseNeeds; // minutes per day

    @Column(name = "grooming_needs", nullable = false, length = 50)
    @NotBlank(message = "Grooming needs is required")
    @Pattern(regexp = "^(low|moderate|high)$", message = "Grooming needs must be low, moderate, or high")
    private String groomingNeeds;

    @Column(name = "noise_level", nullable = false, length = 50)
    @NotBlank(message = "Noise level is required")
    @Pattern(regexp = "^(quiet|moderate|vocal)$", message = "Noise level must be quiet, moderate, or vocal")
    private String noiseLevel;

    @Column(nullable = false)
    @NotNull(message = "Adaptability is required")
    @Min(value = 1, message = "Adaptability must be between 1 and 5")
    @Max(value = 5, message = "Adaptability must be between 1 and 5")
    private Integer adaptability;

    @Column(name = "special_traits", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Special traits must not exceed 1000 characters")
    private String specialTraits;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, unique = true)
    @NotNull(message = "Pet is required")
    private Pet pet;

    // Helper methods
    public String getEnergyLevelDisplay() {
        return switch (energyLevel) {
            case LOW -> "Low - Calm and relaxed";
            case MODERATE -> "Moderate - Balanced energy";
            case HIGH -> "High - Active and energetic";
            case VERY_HIGH -> "Very High - Extremely active";
        };
    }

    public String getSociabilityDisplay() {
        return switch (sociability) {
            case SHY -> "Shy - Prefers quiet environments";
            case MODERATE -> "Moderate - Comfortable with some social interaction";
            case SOCIAL -> "Social - Enjoys meeting new people";
            case VERY_SOCIAL -> "Very Social - Loves everyone";
        };
    }

    public String getTrainabilityDisplay() {
        return switch (trainability) {
            case EASY -> "Easy - Quick to learn";
            case MODERATE -> "Moderate - Average learning ability";
            case CHALLENGING -> "Challenging - Requires patience";
            case EXPERT_ONLY -> "Expert Only - Needs experienced handler";
        };
    }

    public String getExerciseNeedsDisplay() {
        if (exerciseNeeds < 30) {
            return "Low - " + exerciseNeeds + " minutes daily";
        } else if (exerciseNeeds < 60) {
            return "Moderate - " + exerciseNeeds + " minutes daily";
        } else if (exerciseNeeds < 120) {
            return "High - " + exerciseNeeds + " minutes daily";
        } else {
            return "Very High - " + exerciseNeeds + " minutes daily";
        }
    }

    public String getGroomingNeedsDisplay() {
        return switch (groomingNeeds.toLowerCase()) {
            case "low" -> "Low - Minimal grooming required";
            case "moderate" -> "Moderate - Regular brushing needed";
            case "high" -> "High - Daily grooming required";
            default -> groomingNeeds;
        };
    }

    public String getNoiseDisplay() {
        return switch (noiseLevel.toLowerCase()) {
            case "quiet" -> "Quiet - Rarely makes noise";
            case "moderate" -> "Moderate - Occasional vocalizations";
            case "vocal" -> "Vocal - Frequently communicative";
            default -> noiseLevel;
        };
    }

    public String getIndependenceLevelDisplay() {
        return switch (independenceLevel) {
            case 1 -> "Very dependent - Needs constant attention";
            case 2 -> "Somewhat dependent - Enjoys company";
            case 3 -> "Balanced - Can be alone or with others";
            case 4 -> "Independent - Comfortable alone";
            case 5 -> "Very independent - Prefers solitude";
            default -> "Level " + independenceLevel;
        };
    }

    public String getPlayfulnessDisplay() {
        return switch (playfulnessLevel) {
            case 1 -> "Not playful - Prefers rest";
            case 2 -> "Slightly playful - Occasional play";
            case 3 -> "Moderately playful - Regular play sessions";
            case 4 -> "Very playful - Loves to play";
            case 5 -> "Extremely playful - Always ready for fun";
            default -> "Level " + playfulnessLevel;
        };
    }

    public String getAffectionDisplay() {
        return switch (affectionLevel) {
            case 1 -> "Reserved - Shows little affection";
            case 2 -> "Somewhat affectionate - Occasional cuddles";
            case 3 -> "Moderately affectionate - Enjoys attention";
            case 4 -> "Very affectionate - Loves cuddles";
            case 5 -> "Extremely affectionate - Constant companion";
            default -> "Level " + affectionLevel;
        };
    }
}


