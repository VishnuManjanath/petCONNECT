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
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pets")
@Data
@EqualsAndHashCode(exclude = {"adoptionApplications", "communityPosts", "personalityProfile"})
@ToString(exclude = {"adoptionApplications", "communityPosts", "personalityProfile"})
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Pet name is required")
    @Size(max = 100, message = "Pet name must not exceed 100 characters")
    private String name;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Species is required")
    @Size(max = 50, message = "Species must not exceed 50 characters")
    private String species;

    @Column(length = 100)
    @Size(max = 100, message = "Breed must not exceed 100 characters")
    private String breed;

    @Column(nullable = false)
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be a positive number")
    @Max(value = 30, message = "Age must be realistic")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    @NotNull(message = "Age group is required")
    private PetAgeGroup ageGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Size is required")
    private PetSize size;

    @Column(precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Weight must be positive")
    @DecimalMax(value = "999.99", message = "Weight must be realistic")
    private BigDecimal weight;

    @Column(nullable = false, length = 10)
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Unknown)$", message = "Gender must be Male, Female, or Unknown")
    private String gender;

    @Column(length = 100)
    @Size(max = 100, message = "Color must not exceed 100 characters")
    private String color;

    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Medical history must not exceed 2000 characters")
    private String medicalHistory;

    @Column(name = "special_needs", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Special needs must not exceed 1000 characters")
    private String specialNeeds;

    @Column(name = "house_trained", nullable = false)
    private Boolean houseTrained = false;

    @Column(name = "good_with_kids", nullable = false)
    private Boolean goodWithKids = false;

    @Column(name = "good_with_dogs", nullable = false)
    private Boolean goodWithDogs = false;

    @Column(name = "good_with_cats", nullable = false)
    private Boolean goodWithCats = false;

    @Column(name = "adoption_fee", precision = 8, scale = 2)
    @DecimalMin(value = "0.00", message = "Adoption fee must be positive")
    @DecimalMax(value = "999999.99", message = "Adoption fee must be realistic")
    private BigDecimal adoptionFee = BigDecimal.ZERO;

    @Column(name = "is_available", nullable = false)
    private Boolean available = true;

    @Column(name = "image_url", length = 500)
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id", nullable = false)
    @NotNull(message = "Shelter is required")
    private Shelter shelter;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdoptionApplication> adoptionApplications;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommunityPost> communityPosts;

    @OneToOne(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PersonalityProfile personalityProfile;

    // Helper methods
    public String getDisplayName() {
        return name + " (" + species + ")";
    }

    public String getAgeDisplay() {
        if (age == 1) {
            return "1 year old";
        } else if (age < 1) {
            return "Less than 1 year old";
        } else {
            return age + " years old";
        }
    }

    public String getSizeDisplay() {
        return switch (size) {
            case SMALL -> "Small";
            case MEDIUM -> "Medium";
            case LARGE -> "Large";
            case EXTRA_LARGE -> "Extra Large";
        };
    }

    public boolean hasSpecialNeeds() {
        return specialNeeds != null && !specialNeeds.trim().isEmpty();
    }

    public int getPendingApplicationsCount() {
        return adoptionApplications != null ? 
            (int) adoptionApplications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                .count() : 0;
    }
}
