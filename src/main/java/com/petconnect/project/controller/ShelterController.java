package com.petconnect.project.controller;

import com.petconnect.project.entity.*;
import com.petconnect.project.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/shelter")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('SHELTER_ADMIN') or hasRole('ADMIN')")
public class ShelterController {

    private final UserRepository userRepository;
    private final ShelterRepository shelterRepository;
    private final PetRepository petRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final PersonalityProfileRepository personalityProfileRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get the shelter managed by this user
        Shelter shelter = shelterRepository.findByAdminUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No shelter found for this user"));

        // Get dashboard statistics
        List<Pet> shelterPets = petRepository.findByShelter(shelter.getId());
        List<AdoptionApplication> applications = adoptionApplicationRepository.findByShelterIdWithDetails(shelter.getId());
        long pendingApplications = adoptionApplicationRepository.countPendingApplicationsByShelter(shelter.getId());

        model.addAttribute("shelter", shelter);
        model.addAttribute("user", user);
        model.addAttribute("pets", shelterPets);
        model.addAttribute("applications", applications);
        model.addAttribute("pendingApplications", pendingApplications);
        model.addAttribute("totalPets", shelterPets.size());
        model.addAttribute("availablePets", shelterPets.stream().filter(pet -> pet.getAvailable()).count());
        model.addAttribute("totalApplications", applications.size());

        return "shelter/dashboard";
    }

    @PostMapping("/applications/update/{applicationId}")
    public String updateApplicationStatus(@PathVariable UUID applicationId,
                                        @RequestParam ApplicationStatus status,
                                        @RequestParam(required = false) String reviewerNotes,
                                        Authentication authentication,
                                        Model model) {
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AdoptionApplication application = adoptionApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Verify the user has permission to update this application
        Shelter userShelter = shelterRepository.findByAdminUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No shelter found for this user"));
        
        if (!application.getShelter().getId().equals(userShelter.getId())) {
            throw new RuntimeException("Unauthorized to update this application");
        }

        // Update application
        application.setStatus(status);
        application.setReviewDate(LocalDateTime.now());
        if (reviewerNotes != null && !reviewerNotes.trim().isEmpty()) {
            application.setReviewerNotes(reviewerNotes);
        }

        // If approved, mark pet as unavailable
        if (status == ApplicationStatus.APPROVED) {
            Pet pet = application.getPet();
            pet.setAvailable(false);
            petRepository.save(pet);
            
            // Reject all other pending applications for this pet
            List<AdoptionApplication> otherApplications = adoptionApplicationRepository.findByPetId(pet.getId());
            for (AdoptionApplication otherApp : otherApplications) {
                if (!otherApp.getId().equals(applicationId) && otherApp.getStatus() == ApplicationStatus.PENDING) {
                    otherApp.setStatus(ApplicationStatus.REJECTED);
                    otherApp.setReviewerNotes("Pet has been adopted by another applicant");
                    otherApp.setReviewDate(LocalDateTime.now());
                }
            }
            adoptionApplicationRepository.saveAll(otherApplications);
        }

        adoptionApplicationRepository.save(application);
        log.info("Application {} updated to status {} by user {}", applicationId, status, username);

        // Return the updated table row fragment for HTMX
        model.addAttribute("application", application);
        return "shelter/fragments/application-row :: application-row";
    }

    @GetMapping("/applications/{applicationId}/details")
    public String getApplicationDetails(@PathVariable UUID applicationId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AdoptionApplication application = adoptionApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Verify permission
        Shelter userShelter = shelterRepository.findByAdminUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No shelter found for this user"));
        
        if (!application.getShelter().getId().equals(userShelter.getId())) {
            throw new RuntimeException("Unauthorized to view this application");
        }

        model.addAttribute("application", application);
        return "shelter/fragments/application-details :: application-details";
    }

    @GetMapping("/pets")
    public String managePets(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Shelter shelter = shelterRepository.findByAdminUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No shelter found for this user"));

        List<Pet> pets = petRepository.findByShelter(shelter.getId());
        
        model.addAttribute("shelter", shelter);
        model.addAttribute("pets", pets);
        model.addAttribute("newPet", new Pet());

        return "shelter/pets";
    }

    @PostMapping("/pets")
    public String addPet(@Valid @ModelAttribute Pet pet,
                        BindingResult bindingResult,
                        Authentication authentication,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        
        if (bindingResult.hasErrors()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Shelter shelter = shelterRepository.findByAdminUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("No shelter found for this user"));

            List<Pet> pets = petRepository.findByShelter(shelter.getId());
            
            model.addAttribute("shelter", shelter);
            model.addAttribute("pets", pets);
            model.addAttribute("newPet", pet);
            return "shelter/pets";
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Shelter shelter = shelterRepository.findByAdminUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No shelter found for this user"));

        pet.setShelter(shelter);
        pet.setAvailable(true);
        Pet savedPet = petRepository.save(pet);

        // Create a basic personality profile for the pet
        PersonalityProfile profile = new PersonalityProfile();
        profile.setPet(savedPet);
        profile.setEnergyLevel(EnergyLevel.MODERATE);
        profile.setSociability(SociabilityLevel.MODERATE);
        profile.setTrainability(TrainabilityLevel.MODERATE);
        profile.setIndependenceLevel(3);
        profile.setPlayfulnessLevel(3);
        profile.setAffectionLevel(3);
        profile.setExerciseNeeds(60);
        profile.setGroomingNeeds("moderate");
        profile.setNoiseLevel("moderate");
        profile.setAdaptability(3);
        personalityProfileRepository.save(profile);

        redirectAttributes.addFlashAttribute("success", "Pet " + pet.getName() + " has been added successfully!");
        return "redirect:/shelter/pets";
    }

    @GetMapping("/pets/{petId}/edit")
    public String editPetForm(@PathVariable UUID petId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // Verify permission
        Shelter userShelter = shelterRepository.findByAdminUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No shelter found for this user"));
        
        if (!pet.getShelter().getId().equals(userShelter.getId())) {
            throw new RuntimeException("Unauthorized to edit this pet");
        }

        model.addAttribute("pet", pet);
        model.addAttribute("petSizes", PetSize.values());
        model.addAttribute("petAgeGroups", PetAgeGroup.values());

        return "shelter/edit-pet";
    }

    @PostMapping("/pets/{petId}/edit")
    public String updatePet(@PathVariable UUID petId,
                           @Valid @ModelAttribute Pet updatedPet,
                           BindingResult bindingResult,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("pet", updatedPet);
            model.addAttribute("petSizes", PetSize.values());
            model.addAttribute("petAgeGroups", PetAgeGroup.values());
            return "shelter/edit-pet";
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pet existingPet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // Verify permission
        Shelter userShelter = shelterRepository.findByAdminUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No shelter found for this user"));
        
        if (!existingPet.getShelter().getId().equals(userShelter.getId())) {
            throw new RuntimeException("Unauthorized to edit this pet");
        }

        // Update pet details
        existingPet.setName(updatedPet.getName());
        existingPet.setSpecies(updatedPet.getSpecies());
        existingPet.setBreed(updatedPet.getBreed());
        existingPet.setAge(updatedPet.getAge());
        existingPet.setAgeGroup(updatedPet.getAgeGroup());
        existingPet.setSize(updatedPet.getSize());
        existingPet.setWeight(updatedPet.getWeight());
        existingPet.setGender(updatedPet.getGender());
        existingPet.setColor(updatedPet.getColor());
        existingPet.setDescription(updatedPet.getDescription());
        existingPet.setMedicalHistory(updatedPet.getMedicalHistory());
        existingPet.setSpecialNeeds(updatedPet.getSpecialNeeds());
        existingPet.setHouseTrained(updatedPet.getHouseTrained());
        existingPet.setGoodWithKids(updatedPet.getGoodWithKids());
        existingPet.setGoodWithDogs(updatedPet.getGoodWithDogs());
        existingPet.setGoodWithCats(updatedPet.getGoodWithCats());
        existingPet.setAdoptionFee(updatedPet.getAdoptionFee());
        existingPet.setImageUrl(updatedPet.getImageUrl());

        petRepository.save(existingPet);

        redirectAttributes.addFlashAttribute("success", "Pet " + existingPet.getName() + " has been updated successfully!");
        return "redirect:/shelter/pets";
    }

    @PostMapping("/pets/{petId}/toggle-availability")
    @ResponseBody
    public ResponseEntity<String> togglePetAvailability(@PathVariable UUID petId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // Verify permission
        Shelter userShelter = shelterRepository.findByAdminUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No shelter found for this user"));
        
        if (!pet.getShelter().getId().equals(userShelter.getId())) {
            return ResponseEntity.badRequest().body("Unauthorized");
        }

        pet.setAvailable(!pet.getAvailable());
        petRepository.save(pet);

        return ResponseEntity.ok(pet.getAvailable() ? "Available" : "Not Available");
    }
}
