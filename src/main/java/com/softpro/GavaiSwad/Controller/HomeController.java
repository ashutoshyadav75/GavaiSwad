
package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Repository.ProductRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Controller
public class HomeController {

    private final ProductRepository productRepository;

    public HomeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {

        // Admin goes to dashboard
        if (authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"))) {

            return "redirect:/admin/dashboard";
        }

        // Fetch latest 8 active products
        model.addAttribute(
                "products",
                productRepository.findByActiveTrue(
                        PageRequest.of(
                                0,
                                8,
                                Sort.by(Sort.Direction.DESC, "createdAt")
                        )
                )
        );

        return "home";
    }
}
