
package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Entity.CustomerOrder;
import com.softpro.GavaiSwad.Entity.User;
import com.softpro.GavaiSwad.Repository.CustomerOrderRepository;
import com.softpro.GavaiSwad.Repository.UserRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
public class AdminOrderController {

    private final CustomerOrderRepository customerOrderRepository;
    private final UserRepository userRepository;

    public AdminOrderController(
            CustomerOrderRepository customerOrderRepository,
            UserRepository userRepository) {

        this.customerOrderRepository = customerOrderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {

        List<CustomerOrder> orders =
                customerOrderRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "createdAt")
                );

        List<User> deliveryPartners = userRepository.findAll()
                .stream()
                .filter(user ->
                        "DELIVERY".equalsIgnoreCase(user.getRole())
                        && "APPROVED".equalsIgnoreCase(
                                user.getAccountStatus()))
                .toList();

        model.addAttribute("orders", orders);
        model.addAttribute("deliveryPartners", deliveryPartners);

        return "admin/orders";
    }

    // Assign Delivery Partner
    @PostMapping("/admin/orders/{id}/assign-delivery")
    public String assignDeliveryPartner(
            @PathVariable Long id,
            @RequestParam Long deliveryPartnerId,
            RedirectAttributes redirectAttributes) {

        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found."));

        User deliveryPartner = userRepository.findById(deliveryPartnerId)
                .orElse(null);

        if (deliveryPartner == null
                || !"DELIVERY".equalsIgnoreCase(deliveryPartner.getRole())
                || !"APPROVED".equalsIgnoreCase(
                        deliveryPartner.getAccountStatus())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Invalid Delivery Partner selected.");

            return "redirect:/admin/orders";
        }

        if ("CANCELLED".equalsIgnoreCase(order.getOrderStatus())
                || "DELIVERED".equalsIgnoreCase(order.getOrderStatus())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cancelled or delivered orders cannot be assigned.");

            return "redirect:/admin/orders";
        }

        order.setDeliveryPartner(deliveryPartner);

        customerOrderRepository.save(order);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Delivery Partner assigned successfully!");

        return "redirect:/admin/orders";
    }

    // Update Order and Payment Status
    @PostMapping("/admin/orders/{id}/update-status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String orderStatus,
            @RequestParam String paymentStatus,
            RedirectAttributes redirectAttributes) {

        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found."));

        if (!Set.of(
                "PLACED", "PROCESSING", "SHIPPED",
                "DELIVERED", "CANCELLED"
        ).contains(orderStatus)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Invalid order status.");

            return "redirect:/admin/orders";
        }

        if (!Set.of(
                "PENDING", "PAID", "FAILED", "REFUNDED"
        ).contains(paymentStatus)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Invalid payment status.");

            return "redirect:/admin/orders";
        }

        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);

        customerOrderRepository.save(order);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Order status updated successfully!");

        return "redirect:/admin/orders";
    }
}
