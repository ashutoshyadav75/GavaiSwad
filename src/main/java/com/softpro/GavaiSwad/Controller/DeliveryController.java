
package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Entity.CustomerOrder;
import com.softpro.GavaiSwad.Entity.User;
import com.softpro.GavaiSwad.Repository.CustomerOrderRepository;
import com.softpro.GavaiSwad.Repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {

    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository;

    public DeliveryController(
            CustomerOrderRepository orderRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        User deliveryPartner = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Delivery Partner not found"));

        List<CustomerOrder> orders =
                orderRepository
                        .findByDeliveryPartner_IdOrderByCreatedAtDesc(
                                deliveryPartner.getId());

        model.addAttribute("deliveryPartner", deliveryPartner);
        model.addAttribute("orders", orders);

        return "delivery/dashboard";
    }
}
