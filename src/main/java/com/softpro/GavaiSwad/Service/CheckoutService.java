package com.softpro.GavaiSwad.Service;

import com.softpro.GavaiSwad.Entity.CartItem;
import com.softpro.GavaiSwad.Entity.CustomerOrder;
import com.softpro.GavaiSwad.Entity.OrderItem;
import com.softpro.GavaiSwad.Entity.Product;
import com.softpro.GavaiSwad.Entity.User;

import com.softpro.GavaiSwad.Repository.CartItemRepository;
import com.softpro.GavaiSwad.Repository.CustomerOrderRepository;
import com.softpro.GavaiSwad.Repository.OrderItemRepository;
import com.softpro.GavaiSwad.Repository.ProductRepository;
import com.softpro.GavaiSwad.Repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckoutService {

private final UserRepository userRepository;
private final CartItemRepository cartItemRepository;
private final CustomerOrderRepository customerOrderRepository;
private final OrderItemRepository orderItemRepository;
private final ProductRepository productRepository;

public CheckoutService(
        UserRepository userRepository,
        CartItemRepository cartItemRepository,
        CustomerOrderRepository customerOrderRepository,
        OrderItemRepository orderItemRepository,
        ProductRepository productRepository) {

    this.userRepository = userRepository;
    this.cartItemRepository = cartItemRepository;
    this.customerOrderRepository = customerOrderRepository;
    this.orderItemRepository = orderItemRepository;
    this.productRepository = productRepository;
}

@Transactional
public CustomerOrder placeCodOrder(
        Authentication authentication,
        String fullName,
        String phone,
        String deliveryAddress) {

    if (authentication == null || !authentication.isAuthenticated()
            || authentication.getPrincipal().equals("anonymousUser")) {
        throw new IllegalStateException("Please login before placing an order.");
    }

    if (fullName == null || fullName.isBlank()
            || phone == null || phone.isBlank()
            || deliveryAddress == null || deliveryAddress.isBlank()) {
        throw new IllegalArgumentException("Please fill all delivery details.");
    }

    User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalStateException("User account not found."));
    
 // Save latest delivery details in user profile
    user.setFullName(fullName.trim());
    user.setPhone(phone.trim());
    user.setDeliveryAddress(deliveryAddress.trim());

    userRepository.save(user);

    List<CartItem> cartItems = cartItemRepository.findByUser_Id(user.getId());

    if (cartItems.isEmpty()) {
        throw new IllegalStateException("Your cart is empty.");
    }

    BigDecimal totalAmount = BigDecimal.ZERO;

    for (CartItem cartItem : cartItems) {
        Product product = cartItem.getProduct();

        if (product == null || !Boolean.TRUE.equals(product.getActive())) {
            throw new IllegalStateException("A product in your cart is unavailable.");
        }

        if (cartItem.getQuantity() == null || cartItem.getQuantity() < 1) {
            throw new IllegalStateException("Invalid product quantity.");
        }

        if (product.getStock() == null
                || product.getStock() < cartItem.getQuantity()) {
            throw new IllegalStateException(
                    "Insufficient stock for product: " + product.getName());
        }

        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new IllegalStateException("Invalid product price.");
        }

        BigDecimal itemTotal = product.getPrice().multiply(
                BigDecimal.valueOf(cartItem.getQuantity().longValue()));

        totalAmount = totalAmount.add(itemTotal);
    }

    CustomerOrder order = new CustomerOrder();
    order.setUser(user);
    order.setFullName(fullName.trim());
    order.setPhone(phone.trim());
    order.setDeliveryAddress(deliveryAddress.trim());
    order.setTotalAmount(totalAmount);
    order.setPaymentMethod("COD");
    order.setPaymentStatus("PENDING");
    order.setOrderStatus("PLACED");

    CustomerOrder savedOrder = customerOrderRepository.save(order);

    for (CartItem cartItem : cartItems) {
        Product product = cartItem.getProduct();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setUnitPrice(product.getPrice());

        orderItemRepository.save(orderItem);

        product.setStock(product.getStock() - cartItem.getQuantity());
        productRepository.save(product);
    }

    cartItemRepository.deleteAll(cartItems);

    return savedOrder;
}

}