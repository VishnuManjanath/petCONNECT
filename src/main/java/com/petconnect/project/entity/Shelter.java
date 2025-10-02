package com.petconnect.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shelters")
@Data
@EqualsAndHashCode(exclude = {"pets", "adoptionApplications"})
@ToString(exclude = {"pets", "adoptionApplications"})
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Shelter name is required")
    @Size(max = 200, message = "Shelter name must not exceed 200 characters")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Address is required")
    private String address;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;

    @Column(name = "zip_code", nullable = false, length = 10)
    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "Please provide a valid ZIP code")
    private String zipCode;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9\\-\\s\\(\\)]{10,20}$", message = "Please provide a valid phone number")
    private String phone;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Column(length = 255)
    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    private String website;

    @Column(name = "license_number", length = 100)
    @Size(max = 100, message = "License number must not exceed 100 characters")
    private String licenseNumber;

    @Column
    @Min(value = 0, message = "Capacity must be a positive number")
    private Integer capacity = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id", nullable = false)
    @NotNull(message = "Admin user is required")
    private User adminUser;

    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pet> pets;

    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdoptionApplication> adoptionApplications;

    // Helper methods
    public String getFullAddress() {
        return address + ", " + city + ", " + state + " " + zipCode;
    }

    public int getCurrentPetCount() {
        return pets != null ? (int) pets.stream().filter(pet -> pet.getAvailable()).count() : 0;
    }

    public boolean isAtCapacity() {
        return capacity > 0 && getCurrentPetCount() >= capacity;
    }
}
