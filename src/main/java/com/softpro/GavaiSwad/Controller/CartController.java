package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Entity.CartItem;
import com.softpro.GavaiSwad.Repository.CartItemRepository;
import com.softpro.GavaiSwad.Repository.UserRepository;
import com.softpro.GavaiSwad.Service.CartService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CartController {

private final CartService cartService;
private final CartItemRepository cartItemRepository;
private final UserRepository userRepository;

public CartController(
        CartService cartService,
        CartItemRepository cartItemRepository,
        UserRepository userRepository) {
    this.cartService = cartService;
    this.cartItemRepository = cartItemRepository;
    this.userRepository = userRepository;
}

@PostMapping("/cart/add")
public String addToCart(
        @RequestParam Long productId,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {

    try {
        cartService.addToCart(authentication, productId);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Product added to your cart!");
    } catch (IllegalArgumentException | IllegalStateException exception) {
        redirectAttributes.addFlashAttribute(
                "errorMessage", exception.getMessage());
    }

    return "redirect:/cart";
}

@GetMapping("/cart")
public String viewCart(Authentication authentication, Model model) {

if (authentication == null || !authentication.isAuthenticated()) {
    return "redirect:/login";
}

Long userId = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalArgumentException("User not found."))
        .getId();

List<CartItem> cartItems = cartItemRepository.findByUser_Id(userId);

BigDecimal cartTotal = BigDecimal.ZERO;

for (CartItem item : cartItems) {
    BigDecimal subtotal = item.getProduct().getPrice()
            .multiply(BigDecimal.valueOf(item.getQuantity().longValue()));

    cartTotal = cartTotal.add(subtotal);
}

model.addAttribute("cartItems", cartItems);
model.addAttribute("cartTotal", cartTotal);

return "cart";

}
@PostMapping("/cart/update")
public String updateQuantity(
@RequestParam Long cartItemId,
@RequestParam Integer quantity,
Authentication authentication,
RedirectAttributes redirectAttributes) {

try {
    cartService.updateQuantity(authentication, cartItemId, quantity);
    redirectAttributes.addFlashAttribute(
            "successMessage", "Cart quantity updated successfully!");
} catch (IllegalArgumentException | IllegalStateException exception) {
    redirectAttributes.addFlashAttribute(
            "errorMessage", exception.getMessage());
}

return "redirect:/cart";

}

@PostMapping("/cart/remove")
public String removeFromCart(
@RequestParam Long cartItemId,
Authentication authentication,
RedirectAttributes redirectAttributes) {

try {
    cartService.removeFromCart(authentication, cartItemId);
    redirectAttributes.addFlashAttribute(
            "successMessage", "Product removed from your cart!");
} catch (IllegalArgumentException | IllegalStateException exception) {
    redirectAttributes.addFlashAttribute(
            "errorMessage", exception.getMessage());
}

return "redirect:/cart";

}


}