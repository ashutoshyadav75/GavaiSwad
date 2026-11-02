
package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Entity.User;
import com.softpro.GavaiSwad.Repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Display all registered customers
    @GetMapping("/admin/users")
    public String manageUsers(Model model) {

        List<User> users = userRepository.findAll();

        model.addAttribute("users", users);

        return "admin/users";
    }

    // Approve customer account
    @PostMapping("/admin/users/approve/{id}")
    public String approveUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Customer not found!");
            return "redirect:/admin/users";
        }

        user.setAccountStatus("APPROVED");
        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "message", "Customer approved successfully!");

        return "redirect:/admin/users";
    }

    // Block customer account
    @PostMapping("/admin/users/block/{id}")
    public String blockUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Customer not found!");
            return "redirect:/admin/users";
        }

        user.setAccountStatus("BLOCKED");
        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "message", "Customer blocked successfully!");

        return "redirect:/admin/users";
    }

    // Unblock customer account
    @PostMapping("/admin/users/unblock/{id}")
    public String unblockUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Customer not found!");
            return "redirect:/admin/users";
        }

        user.setAccountStatus("APPROVED");
        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "message", "Customer unblocked successfully!");

        return "redirect:/admin/users";
    }
}
