
package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Entity.User;
import com.softpro.GavaiSwad.Repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Admin Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        List<User> users = userRepository.findAll();

        model.addAttribute("users", users);

        long pendingCount = users.stream()
                .filter(user -> "PENDING".equalsIgnoreCase(
                        user.getAccountStatus()))
                .count();

        long approvedCount = users.stream()
                .filter(user -> "APPROVED".equalsIgnoreCase(
                        user.getAccountStatus()))
                .count();

        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);

        return "admin/dashboard";
    }

    // Approve Customer
    @PostMapping("/approve/{id}")
    public String approveUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "message", "User not found.");
            return "redirect:/admin/dashboard";
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            redirectAttributes.addFlashAttribute(
                    "message", "Admin accounts cannot be approved here.");
            return "redirect:/admin/dashboard";
        }

        user.setAccountStatus("APPROVED");
        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "message", "Customer approved successfully.");

        return "redirect:/admin/dashboard";
    }

    // Block Customer
    @PostMapping("/block/{id}")
    public String blockUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "message", "User not found.");
            return "redirect:/admin/dashboard";
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            redirectAttributes.addFlashAttribute(
                    "message", "Admin accounts cannot be blocked here.");
            return "redirect:/admin/dashboard";
        }

        user.setAccountStatus("BLOCKED");
        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "message", "User blocked successfully.");

        return "redirect:/admin/dashboard";
    }

    // Display Delivery Partners
    @GetMapping("/delivery-partners")
    public String deliveryPartners(Model model) {

        List<User> deliveryPartners = userRepository.findAll()
                .stream()
                .filter(user -> "DELIVERY".equalsIgnoreCase(
                        user.getRole()))
                .toList();

        model.addAttribute("deliveryPartners", deliveryPartners);

        return "admin/delivery-partners";
    }

    // Create Delivery Partner
    @PostMapping("/delivery-partners/create")
    public String createDeliveryPartner(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        if (fullName.isBlank() || email.isBlank()
                || phone.isBlank() || password.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "error", "Please fill all fields.");

            return "redirect:/admin/delivery-partners";
        }

        if (password.length() < 8) {
            redirectAttributes.addFlashAttribute(
                    "error", "Password must contain at least 8 characters.");

            return "redirect:/admin/delivery-partners";
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Email already exists.");

            return "redirect:/admin/delivery-partners";
        }

        User partner = new User();

        partner.setFullName(fullName.trim());
        partner.setEmail(normalizedEmail);
        partner.setPhone(phone.trim());
        partner.setPassword(passwordEncoder.encode(password));
        partner.setRole("DELIVERY");
        partner.setAccountStatus("APPROVED");

        userRepository.save(partner);

        redirectAttributes.addFlashAttribute(
                "message", "Delivery Partner created successfully!");

        return "redirect:/admin/delivery-partners";
    }
}
