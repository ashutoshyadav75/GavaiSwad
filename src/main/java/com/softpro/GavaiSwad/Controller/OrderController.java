package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Entity.CustomerOrder;
import com.softpro.GavaiSwad.Entity.User;
import com.softpro.GavaiSwad.Repository.CustomerOrderRepository;
import com.softpro.GavaiSwad.Repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class OrderController {

private final CustomerOrderRepository customerOrderRepository;
private final UserRepository userRepository;

public OrderController(
        CustomerOrderRepository customerOrderRepository,
        UserRepository userRepository) {
    this.customerOrderRepository = customerOrderRepository;
    this.userRepository = userRepository;
}

@GetMapping("/orders/{id}")
public String orderConfirmation(
        @PathVariable Long id,
        Authentication authentication,
        Model model) {
	

	
    User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalStateException("User account not found."));

    CustomerOrder order = customerOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found."));

    // Customers can view only their own orders.
    // Admins can view orders as well.
    boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority ->
                    authority.getAuthority().equals("ROLE_ADMIN"));

    if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
        return "redirect:/products";
    }

    model.addAttribute("order", order);

    return "order-confirmation";
}
@GetMapping("/my-orders")
public String myOrders(Authentication authentication, Model model) {

User user = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User account not found."));

List<CustomerOrder> orders =
        customerOrderRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());

model.addAttribute("orders", orders);

return "my-orders";

}

}