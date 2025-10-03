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
@Table(name = "adoption_applications", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"applicant_id", "pet_id"}))
@Data
@EqualsAndHashCode(exclude = {"applicant", "pet", "shelter"})
@ToString(exclude = {"applicant", "pet", "shelter"})
public class AdoptionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "application_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime applicationDate;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Column(name = "reviewer_notes", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Reviewer notes must not exceed 2000 characters")
    private String reviewerNotes;

    @Column(name = "applicant_message", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Applicant message must not exceed 1000 characters")
    private String applicantMessage;

    @Column(name = "home_visit_required", nullable = false)
    private Boolean homeVisitRequired = false;

    @Column(name = "home_visit_completed", nullable = false)
    private Boolean homeVisitCompleted = false;

    @Column(name = "home_visit_date")
    private LocalDateTime homeVisitDate;

    @Column(name = "references_checked", nullable = false)
    private Boolean referencesChecked = false;

    @Column(name = "background_check_completed", nullable = false)
    private Boolean backgroundCheckCompleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    @NotNull(message = "Applicant is required")
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    @NotNull(message = "Pet is required")
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id", nullable = false)
    @NotNull(message = "Shelter is required")
    private Shelter shelter;

    // Helper methods
    public String getStatusDisplay() {
        return switch (status) {
            case PENDING -> "Pending Review";
            case APPROVED -> "Approved";
            case REJECTED -> "Rejected";
            case WITHDRAWN -> "Withdrawn";
        };
    }

    public String getStatusBadgeClass() {
        return switch (status) {
            case PENDING -> "badge-warning";
            case APPROVED -> "badge-success";
            case REJECTED -> "badge-danger";
            case WITHDRAWN -> "badge-secondary";
        };
    }

    public boolean isPending() {
        return status == ApplicationStatus.PENDING;
    }

    public boolean isApproved() {
        return status == ApplicationStatus.APPROVED;
    }

    public boolean isRejected() {
        return status == ApplicationStatus.REJECTED;
    }

    public boolean isWithdrawn() {
        return status == ApplicationStatus.WITHDRAWN;
    }

    public boolean canBeModified() {
        return status == ApplicationStatus.PENDING;
    }

    public boolean requiresAction() {
        return isPending() && (!homeVisitRequired || homeVisitCompleted) && 
               (!referencesChecked || !backgroundCheckCompleted);
    }

    public String getApplicationProgress() {
        if (status != ApplicationStatus.PENDING) {
            return getStatusDisplay();
        }

        StringBuilder progress = new StringBuilder();
        int completed = 0;
        int total = 0;

        if (homeVisitRequired) {
            total++;
            if (homeVisitCompleted) {
                completed++;
                progress.append("✓ Home visit completed ");
            } else {
                progress.append("⏳ Home visit pending ");
            }
        }

        total++;
        if (referencesChecked) {
            completed++;
            progress.append("✓ References checked ");
        } else {
            progress.append("⏳ References pending ");
        }

        total++;
        if (backgroundCheckCompleted) {
            completed++;
            progress.append("✓ Background check completed");
        } else {
            progress.append("⏳ Background check pending");
        }

        return progress.toString() + " (" + completed + "/" + total + ")";
    }
}


