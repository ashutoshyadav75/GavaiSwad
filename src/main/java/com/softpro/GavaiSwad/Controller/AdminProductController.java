
package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Entity.Product;
import com.softpro.GavaiSwad.Repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;

    public AdminProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Display all products
    @GetMapping
    public String productList(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";
    }

    // Add new product
    @PostMapping("/save")
    public String saveProduct(
            String name,
            String category,
            String description,
            BigDecimal price,
            Integer stock,
            String imageUrl,
            RedirectAttributes redirectAttributes) {

        if (name == null || name.isBlank()
                || category == null || category.isBlank()
                || price == null
                || price.compareTo(BigDecimal.ZERO) <= 0
                || stock == null || stock < 0) {

            redirectAttributes.addFlashAttribute(
                    "error", "Please enter valid product details.");
            return "redirect:/admin/products";
        }

        Product product = new Product();
        product.setName(name.trim());
        product.setCategory(category.trim());
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setActive(true);

        productRepository.save(product);

        redirectAttributes.addFlashAttribute(
                "message", "Product added successfully!");

        return "redirect:/admin/products";
    }

    // Open edit product page
    @GetMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Product not found!");
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);

        return "admin/edit-product";
    }

    // Update existing product
    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            String name,
            String category,
            String description,
            BigDecimal price,
            Integer stock,
            String imageUrl,
            RedirectAttributes redirectAttributes) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Product not found!");
            return "redirect:/admin/products";
        }

        if (name == null || name.isBlank()
                || category == null || category.isBlank()
                || price == null
                || price.compareTo(BigDecimal.ZERO) <= 0
                || stock == null || stock < 0) {

            redirectAttributes.addFlashAttribute(
                    "error", "Please enter valid product details!");
            return "redirect:/admin/products";
        }

        product.setName(name.trim());
        product.setCategory(category.trim());
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);

        productRepository.save(product);

        redirectAttributes.addFlashAttribute(
                "message", "Product updated successfully!");

        return "redirect:/admin/products";
    }

    // Soft delete product
    @PostMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Product not found!");
            return "redirect:/admin/products";
        }

        product.setActive(false);
        productRepository.save(product);

        redirectAttributes.addFlashAttribute(
                "message", "Product deactivated successfully!");

        return "redirect:/admin/products";
    }
}
