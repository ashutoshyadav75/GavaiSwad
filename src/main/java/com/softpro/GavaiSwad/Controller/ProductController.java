
package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {

        // Convert blank filters into null
        if (search != null && search.isBlank()) {
            search = null;
        }

        if (category != null && category.isBlank()) {
            category = null;
        }

        // Validate price range
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0
                || maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0
                || minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            model.addAttribute("products",
                    productRepository.searchProducts(null, null, null, null));

            model.addAttribute("error", "Please enter a valid price range.");
        } else {

            model.addAttribute(
                    "products",
                    productRepository.searchProducts(
                            search,
                            category,
                            minPrice,
                            maxPrice
                    )
            );
        }

        model.addAttribute("selectedCategory", category);
        model.addAttribute("search", search);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "products";
    }
}
