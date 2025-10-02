package com.petconnect.project.service;

import com.petconnect.project.entity.*;
import com.petconnect.project.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchmakingService {

    private final PetRepository petRepository;

    /**
     * Finds the best pet matches for a user based on their lifestyle profile
     * and pet personality profiles. Uses a sophisticated scoring algorithm
     * that considers multiple compatibility factors.
     *
     * @param user The user seeking to adopt a pet
     * @return List of pets sorted by compatibility score (highest first)
     */
    public List<PetMatchResult> findBestMatches(User user) {
        log.info("Finding pet matches for user: {}", user.getUsername());
        
        LifestyleProfile lifestyle = user.getLifestyleProfile();
        if (lifestyle == null) {
            log.warn("User {} has no lifestyle profile", user.getUsername());
            return new ArrayList<>();
        }

        // Get all available pets with personality profiles
        List<Pet> availablePets = petRepository.findAvailablePetsWithPersonalityProfiles();
        log.info("Found {} available pets with personality profiles", availablePets.size());

        List<PetMatchResult> matches = new ArrayList<>();

        for (Pet pet : availablePets) {
            if (pet.getPersonalityProfile() != null) {
                double compatibilityScore = calculateCompatibilityScore(lifestyle, pet);
                matches.add(new PetMatchResult(pet, compatibilityScore, generateMatchExplanation(lifestyle, pet)));
            }
        }

        // Sort by compatibility score (highest first)
        matches.sort(Comparator.comparing(PetMatchResult::getCompatibilityScore).reversed());
        
        log.info("Generated {} matches for user {}", matches.size(), user.getUsername());
        return matches;
    }

    /**
     * Calculates compatibility score between user lifestyle and pet personality.
     * Score ranges from 0.0 to 100.0, with higher scores indicating better matches.
     *
     * Scoring factors:
     * - Energy Level Compatibility (25 points)
     * - Size Preference (15 points)
     * - Age Preference (10 points)
     * - Budget Compatibility (10 points)
     * - Living Situation Compatibility (15 points)
     * - Experience Level vs Trainability (15 points)
     * - Social Compatibility (10 points)
     */
    private double calculateCompatibilityScore(LifestyleProfile lifestyle, Pet pet) {
        PersonalityProfile personality = pet.getPersonalityProfile();
        double totalScore = 0.0;

        // 1. Energy Level Compatibility (25 points)
        totalScore += calculateEnergyCompatibility(lifestyle.getActivityLevel(), personality.getEnergyLevel()) * 25;

        // 2. Size Preference (15 points)
        totalScore += calculateSizeCompatibility(lifestyle.getPreferredPetSize(), pet.getSize()) * 15;

        // 3. Age Preference (10 points)
        totalScore += calculateAgeCompatibility(lifestyle.getPreferredPetAge(), pet.getAgeGroup()) * 10;

        // 4. Budget Compatibility (10 points)
        totalScore += calculateBudgetCompatibility(lifestyle.getMaxAdoptionFee(), pet.getAdoptionFee()) * 10;

        // 5. Living Situation Compatibility (15 points)
        totalScore += calculateLivingSituationCompatibility(lifestyle, pet, personality) * 15;

        // 6. Experience Level vs Trainability (15 points)
        totalScore += calculateExperienceCompatibility(lifestyle.getExperienceLevel(), personality.getTrainability()) * 15;

        // 7. Social Compatibility (10 points)
        totalScore += calculateSocialCompatibility(lifestyle, personality) * 10;

        log.debug("Calculated compatibility score {} for pet {} and user lifestyle", totalScore, pet.getName());
        return Math.round(totalScore * 100.0) / 100.0; // Round to 2 decimal places
    }

    private double calculateEnergyCompatibility(EnergyLevel userActivity, EnergyLevel petEnergy) {
        // Perfect match: same energy levels
        if (userActivity == petEnergy) {
            return 1.0;
        }

        // Calculate distance between energy levels
        int userLevel = getEnergyLevelValue(userActivity);
        int petLevel = getEnergyLevelValue(petEnergy);
        int distance = Math.abs(userLevel - petLevel);

        // Score decreases with distance
        return switch (distance) {
            case 1 -> 0.8; // One level difference
            case 2 -> 0.5; // Two levels difference
            case 3 -> 0.2; // Maximum difference
            default -> 0.0;
        };
    }

    private int getEnergyLevelValue(EnergyLevel level) {
        return switch (level) {
            case LOW -> 1;
            case MODERATE -> 2;
            case HIGH -> 3;
            case VERY_HIGH -> 4;
        };
    }

    private double calculateSizeCompatibility(PetSize preferredSize, PetSize petSize) {
        if (preferredSize == null) {
            return 0.8; // No preference is good but not perfect
        }
        return preferredSize == petSize ? 1.0 : 0.3;
    }

    private double calculateAgeCompatibility(PetAgeGroup preferredAge, PetAgeGroup petAge) {
        if (preferredAge == null) {
            return 0.8; // No preference is good but not perfect
        }
        return preferredAge == petAge ? 1.0 : 0.4;
    }

    private double calculateBudgetCompatibility(BigDecimal maxBudget, BigDecimal adoptionFee) {
        if (maxBudget == null) {
            return 0.9; // No budget limit specified
        }
        
        if (adoptionFee == null || adoptionFee.compareTo(BigDecimal.ZERO) == 0) {
            return 1.0; // Free adoption
        }

        if (maxBudget.compareTo(adoptionFee) >= 0) {
            // Calculate how much of budget is used (lower usage = better score)
            double budgetUsage = adoptionFee.divide(maxBudget, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
            return Math.max(0.5, 1.0 - (budgetUsage * 0.3)); // Score between 0.5 and 1.0
        }
        
        return 0.1; // Over budget
    }

    private double calculateLivingSituationCompatibility(LifestyleProfile lifestyle, Pet pet, PersonalityProfile personality) {
        double score = 0.5; // Base score

        String livingSituation = lifestyle.getLivingSituation().toLowerCase();
        String yardSize = lifestyle.getYardSize() != null ? lifestyle.getYardSize().toLowerCase() : "none";

        // Apartment living considerations
        if (livingSituation.contains("apartment")) {
            // Smaller pets and quieter pets are better for apartments
            if (pet.getSize() == PetSize.SMALL || pet.getSize() == PetSize.MEDIUM) {
                score += 0.3;
            }
            if ("quiet".equals(personality.getNoiseLevel())) {
                score += 0.2;
            } else if ("vocal".equals(personality.getNoiseLevel())) {
                score -= 0.2;
            }
        }

        // House with yard considerations
        if (livingSituation.contains("house") && !"none".equals(yardSize)) {
            // Larger pets and more active pets benefit from yards
            if (pet.getSize() == PetSize.LARGE || pet.getSize() == PetSize.EXTRA_LARGE) {
                score += 0.2;
            }
            if (personality.getEnergyLevel() == EnergyLevel.HIGH || personality.getEnergyLevel() == EnergyLevel.VERY_HIGH) {
                score += 0.3;
            }
        }

        return Math.min(1.0, score);
    }

    private double calculateExperienceCompatibility(String experienceLevel, TrainabilityLevel trainability) {
        return switch (experienceLevel.toLowerCase()) {
            case "first_time" -> switch (trainability) {
                case EASY -> 1.0;
                case MODERATE -> 0.6;
                case CHALLENGING -> 0.2;
                case EXPERT_ONLY -> 0.0;
            };
            case "some_experience" -> switch (trainability) {
                case EASY -> 0.9;
                case MODERATE -> 1.0;
                case CHALLENGING -> 0.7;
                case EXPERT_ONLY -> 0.3;
            };
            case "very_experienced" -> switch (trainability) {
                case EASY -> 0.8;
                case MODERATE -> 0.9;
                case CHALLENGING -> 1.0;
                case EXPERT_ONLY -> 1.0;
            };
            default -> 0.5;
        };
    }

    private double calculateSocialCompatibility(LifestyleProfile lifestyle, PersonalityProfile personality) {
        double score = 0.5; // Base score

        // Consider children compatibility
        if (lifestyle.getHasChildren()) {
            // More social and affectionate pets are better with children
            if (personality.getSociability() == SociabilityLevel.SOCIAL || 
                personality.getSociability() == SociabilityLevel.VERY_SOCIAL) {
                score += 0.3;
            }
            if (personality.getAffectionLevel() >= 4) {
                score += 0.2;
            }
        }

        // Consider other pets
        if (lifestyle.getHasOtherPets()) {
            // Moderate to social pets usually do better with other pets
            if (personality.getSociability() == SociabilityLevel.MODERATE || 
                personality.getSociability() == SociabilityLevel.SOCIAL) {
                score += 0.3;
            } else if (personality.getSociability() == SociabilityLevel.SHY) {
                score -= 0.2;
            }
        }

        return Math.min(1.0, score);
    }

    private String generateMatchExplanation(LifestyleProfile lifestyle, Pet pet) {
        PersonalityProfile personality = pet.getPersonalityProfile();
        StringBuilder explanation = new StringBuilder();

        // Energy compatibility
        if (lifestyle.getActivityLevel() == personality.getEnergyLevel()) {
            explanation.append("Perfect energy match! ");
        } else {
            explanation.append("Energy levels complement each other well. ");
        }

        // Size preference
        if (lifestyle.getPreferredPetSize() != null && lifestyle.getPreferredPetSize() == pet.getSize()) {
            explanation.append("Matches your size preference. ");
        }

        // Experience level
        String experience = lifestyle.getExperienceLevel().toLowerCase();
        if ("first_time".equals(experience) && personality.getTrainability() == TrainabilityLevel.EASY) {
            explanation.append("Great for first-time owners! ");
        } else if ("very_experienced".equals(experience) && personality.getTrainability() == TrainabilityLevel.EXPERT_ONLY) {
            explanation.append("Perfect challenge for experienced owners! ");
        }

        // Living situation
        String living = lifestyle.getLivingSituation().toLowerCase();
        if (living.contains("apartment") && pet.getSize() == PetSize.SMALL) {
            explanation.append("Ideal size for apartment living. ");
        }

        // Social aspects
        if (lifestyle.getHasChildren() && personality.getSociability() == SociabilityLevel.VERY_SOCIAL) {
            explanation.append("Loves children and families! ");
        }

        return explanation.toString().trim();
    }

    /**
     * Result class for pet matching
     */
    public static class PetMatchResult {
        private final Pet pet;
        private final double compatibilityScore;
        private final String explanation;

        public PetMatchResult(Pet pet, double compatibilityScore, String explanation) {
            this.pet = pet;
            this.compatibilityScore = compatibilityScore;
            this.explanation = explanation;
        }

        public Pet getPet() {
            return pet;
        }

        public double getCompatibilityScore() {
            return compatibilityScore;
        }

        public String getCompatibilityPercentage() {
            return String.format("%.0f%%", compatibilityScore);
        }

        public String getExplanation() {
            return explanation;
        }

        public String getMatchQuality() {
            if (compatibilityScore >= 85) {
                return "Excellent Match";
            } else if (compatibilityScore >= 70) {
                return "Great Match";
            } else if (compatibilityScore >= 55) {
                return "Good Match";
            } else if (compatibilityScore >= 40) {
                return "Fair Match";
            } else {
                return "Poor Match";
            }
        }

        public String getMatchQualityBadgeClass() {
            if (compatibilityScore >= 85) {
                return "badge-success";
            } else if (compatibilityScore >= 70) {
                return "badge-info";
            } else if (compatibilityScore >= 55) {
                return "badge-primary";
            } else if (compatibilityScore >= 40) {
                return "badge-warning";
            } else {
                return "badge-danger";
            }
        }
    }
}
