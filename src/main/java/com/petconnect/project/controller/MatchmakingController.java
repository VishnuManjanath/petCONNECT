package com.petconnect.project.controller;

import com.petconnect.project.entity.*;
import com.petconnect.project.repository.LifestyleProfileRepository;
import com.petconnect.project.repository.UserRepository;
import com.petconnect.project.service.MatchmakingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/matching")
@RequiredArgsConstructor
@Slf4j
public class MatchmakingController {

    private final UserRepository userRepository;
    private final LifestyleProfileRepository lifestyleProfileRepository;
    private final MatchmakingService matchmakingService;

    @GetMapping("/questionnaire")
    public String showQuestionnaire(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LifestyleProfile profile = user.getLifestyleProfile();
        if (profile == null) {
            profile = new LifestyleProfile();
            profile.setUser(user);
        }

        model.addAttribute("lifestyleProfile", profile);
        model.addAttribute("energyLevels", EnergyLevel.values());
        model.addAttribute("petSizes", PetSize.values());
        model.addAttribute("petAgeGroups", PetAgeGroup.values());
        model.addAttribute("experienceLevels", Arrays.asList("first_time", "some_experience", "very_experienced"));
        model.addAttribute("livingSituations", Arrays.asList("apartment", "house", "farm", "other"));
        model.addAttribute("yardSizes", Arrays.asList("none", "small", "medium", "large"));

        return "matching/questionnaire";
    }

    @PostMapping("/questionnaire")
    public String processQuestionnaire(@Valid @ModelAttribute LifestyleProfile lifestyleProfile,
                                     BindingResult bindingResult,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("energyLevels", EnergyLevel.values());
            model.addAttribute("petSizes", PetSize.values());
            model.addAttribute("petAgeGroups", PetAgeGroup.values());
            model.addAttribute("experienceLevels", Arrays.asList("first_time", "some_experience", "very_experienced"));
            model.addAttribute("livingSituations", Arrays.asList("apartment", "house", "farm", "other"));
            model.addAttribute("yardSizes", Arrays.asList("none", "small", "medium", "large"));
            return "matching/questionnaire";
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already has a lifestyle profile
        LifestyleProfile existingProfile = user.getLifestyleProfile();
        if (existingProfile != null) {
            // Update existing profile
            existingProfile.setLivingSituation(lifestyleProfile.getLivingSituation());
            existingProfile.setYardSize(lifestyleProfile.getYardSize());
            existingProfile.setActivityLevel(lifestyleProfile.getActivityLevel());
            existingProfile.setExperienceLevel(lifestyleProfile.getExperienceLevel());
            existingProfile.setTimeAvailability(lifestyleProfile.getTimeAvailability());
            existingProfile.setHasChildren(lifestyleProfile.getHasChildren());
            existingProfile.setChildrenAges(lifestyleProfile.getChildrenAges());
            existingProfile.setHasOtherPets(lifestyleProfile.getHasOtherPets());
            existingProfile.setOtherPetsDescription(lifestyleProfile.getOtherPetsDescription());
            existingProfile.setPreferredPetAge(lifestyleProfile.getPreferredPetAge());
            existingProfile.setPreferredPetSize(lifestyleProfile.getPreferredPetSize());
            existingProfile.setMaxAdoptionFee(lifestyleProfile.getMaxAdoptionFee());
            existingProfile.setSpecialRequirements(lifestyleProfile.getSpecialRequirements());
            
            lifestyleProfileRepository.save(existingProfile);
        } else {
            // Create new profile
            lifestyleProfile.setUser(user);
            lifestyleProfileRepository.save(lifestyleProfile);
        }

        redirectAttributes.addFlashAttribute("success", "Your lifestyle profile has been saved! Finding your perfect matches...");
        return "redirect:/matching/results";
    }

    @GetMapping("/results")
    public String showMatches(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByIdWithLifestyleProfile(
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"))
                        .getId()
        ).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getLifestyleProfile() == null) {
            return "redirect:/matching/questionnaire";
        }

        List<MatchmakingService.PetMatchResult> matches = matchmakingService.findBestMatches(user);
        
        model.addAttribute("matches", matches);
        model.addAttribute("user", user);
        model.addAttribute("hasMatches", !matches.isEmpty());

        // Categorize matches by quality
        long excellentMatches = matches.stream().filter(m -> m.getCompatibilityScore() >= 85).count();
        long greatMatches = matches.stream().filter(m -> m.getCompatibilityScore() >= 70 && m.getCompatibilityScore() < 85).count();
        long goodMatches = matches.stream().filter(m -> m.getCompatibilityScore() >= 55 && m.getCompatibilityScore() < 70).count();

        model.addAttribute("excellentMatches", excellentMatches);
        model.addAttribute("greatMatches", greatMatches);
        model.addAttribute("goodMatches", goodMatches);

        return "matching/results";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByIdWithLifestyleProfile(
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"))
                        .getId()
        ).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getLifestyleProfile() == null) {
            return "redirect:/matching/questionnaire";
        }

        model.addAttribute("user", user);
        model.addAttribute("profile", user.getLifestyleProfile());

        return "matching/profile";
    }

    @PostMapping("/retake")
    public String retakeQuestionnaire(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete existing profile to force retaking questionnaire
        if (user.getLifestyleProfile() != null) {
            lifestyleProfileRepository.delete(user.getLifestyleProfile());
        }

        return "redirect:/matching/questionnaire";
    }
}
