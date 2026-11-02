package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Entity.CartItem;
import com.softpro.GavaiSwad.Entity.CustomerOrder;
import com.softpro.GavaiSwad.Entity.User;
import com.softpro.GavaiSwad.Repository.CartItemRepository;
import com.softpro.GavaiSwad.Repository.UserRepository;
import com.softpro.GavaiSwad.Service.CheckoutService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CheckoutController {

private final CheckoutService checkoutService;
private final UserRepository userRepository;
private final CartItemRepository cartItemRepository;

public CheckoutController(
        CheckoutService checkoutService,
        UserRepository userRepository,
        CartItemRepository cartItemRepository) {
    this.checkoutService = checkoutService;
    this.userRepository = userRepository;
    this.cartItemRepository = cartItemRepository;
}

@GetMapping("/checkout")
public String checkoutPage(Authentication authentication, Model model) {

    User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalStateException("User account not found."));

    List<CartItem> cartItems =
            cartItemRepository.findByUser_Id(user.getId());

    if (cartItems.isEmpty()) {
        return "redirect:/cart";
    }

    BigDecimal cartTotal = BigDecimal.ZERO;

    for (CartItem item : cartItems) {
        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity().longValue()));

        cartTotal = cartTotal.add(subtotal);
    }

    model.addAttribute("cartItems", cartItems);
    model.addAttribute("cartTotal", cartTotal);
    model.addAttribute("user", user);

    return "checkout";
}

@PostMapping("/checkout/place-order")
public String placeOrder(
        Authentication authentication,
        @RequestParam String fullName,
        @RequestParam String phone,
        @RequestParam String deliveryAddress,
        RedirectAttributes redirectAttributes) {

    try {
        CustomerOrder order = checkoutService.placeCodOrder(
                authentication,
                fullName,
                phone,
                deliveryAddress
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your order has been placed successfully!"
        );

        return "redirect:/orders/" + order.getId();

    } catch (IllegalArgumentException | IllegalStateException exception) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
        );

        return "redirect:/checkout";
    }
}

}