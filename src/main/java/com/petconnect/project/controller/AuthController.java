package com.petconnect.project.controller;

import com.petconnect.project.entity.User;
import com.petconnect.project.entity.UserRole;
import com.petconnect.project.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password. Please try again.");
        }
        
        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully.");
        }
        
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("userRoles", UserRole.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute User user,
                              BindingResult bindingResult,
                              @RequestParam String confirmPassword,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("userRoles", UserRole.values());
            return "auth/register";
        }

        // Check if passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("userRoles", UserRole.values());
            return "auth/register";
        }

        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username already exists. Please choose a different one.");
            model.addAttribute("userRoles", UserRole.values());
            return "auth/register";
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already registered. Please use a different email or login.");
            model.addAttribute("userRoles", UserRole.values());
            return "auth/register";
        }

        try {
            // Encode password and save user
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);
            
            // Set default role if not specified
            if (user.getRole() == null) {
                user.setRole(UserRole.ADOPTER);
            }
            
            userRepository.save(user);
            
            log.info("New user registered: {} with role: {}", user.getUsername(), user.getRole());
            
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! Please log in with your credentials.");
            
            return "redirect:/login";
            
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            model.addAttribute("error", "Registration failed. Please try again.");
            model.addAttribute("userRoles", UserRole.values());
            return "auth/register";
        }
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }
}


