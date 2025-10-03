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
@Table(name = "community_posts")
@Data
@EqualsAndHashCode(exclude = {"author", "pet"})
@ToString(exclude = {"author", "pet"})
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @Column(name = "post_type", nullable = false, length = 50)
    @NotBlank(message = "Post type is required")
    @Pattern(regexp = "^(success_story|advice|question|event)$", 
             message = "Post type must be success_story, advice, question, or event")
    private String postType;

    @Column(name = "image_url", length = 500)
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @Column(name = "likes_count", nullable = false)
    @Min(value = 0, message = "Likes count must be non-negative")
    private Integer likesCount = 0;

    @Column(name = "comments_count", nullable = false)
    @Min(value = 0, message = "Comments count must be non-negative")
    private Integer commentsCount = 0;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @NotNull(message = "Author is required")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet; // Optional - for posts about specific pets

    // Helper methods
    public String getPostTypeDisplay() {
        return switch (postType) {
            case "success_story" -> "Success Story";
            case "advice" -> "Advice";
            case "question" -> "Question";
            case "event" -> "Event";
            default -> postType;
        };
    }

    public String getPostTypeBadgeClass() {
        return switch (postType) {
            case "success_story" -> "badge-success";
            case "advice" -> "badge-info";
            case "question" -> "badge-warning";
            case "event" -> "badge-primary";
            default -> "badge-secondary";
        };
    }

    public String getPostTypeIcon() {
        return switch (postType) {
            case "success_story" -> "fas fa-heart";
            case "advice" -> "fas fa-lightbulb";
            case "question" -> "fas fa-question-circle";
            case "event" -> "fas fa-calendar";
            default -> "fas fa-file-text";
        };
    }

    public String getExcerpt(int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }

    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();
        
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (minutes < 1440) { // 24 hours
            long hours = minutes / 60;
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (minutes < 10080) { // 7 days
            long days = minutes / 1440;
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else {
            return createdAt.toLocalDate().toString();
        }
    }

    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }

    public boolean isAboutPet() {
        return pet != null;
    }

    public void incrementLikes() {
        this.likesCount++;
    }

    public void decrementLikes() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }

    public void incrementComments() {
        this.commentsCount++;
    }

    public void decrementComments() {
        if (this.commentsCount > 0) {
            this.commentsCount--;
        }
    }
}


