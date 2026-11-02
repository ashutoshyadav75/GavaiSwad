package com.softpro.GavaiSwad.Service;

import com.softpro.GavaiSwad.Entity.CartItem;
import com.softpro.GavaiSwad.Entity.Product;
import com.softpro.GavaiSwad.Entity.User;
import com.softpro.GavaiSwad.Repository.CartItemRepository;
import com.softpro.GavaiSwad.Repository.ProductRepository;
import com.softpro.GavaiSwad.Repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

private final CartItemRepository cartItemRepository;
private final ProductRepository productRepository;
private final UserRepository userRepository;

public CartService(
        CartItemRepository cartItemRepository,
        ProductRepository productRepository,
        UserRepository userRepository) {
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
}



@Transactional
public void updateQuantity(
Authentication authentication,
Long cartItemId,
Integer quantity) {

if (authentication == null || !authentication.isAuthenticated()) {
    throw new IllegalStateException("Please log in first.");
}

if (quantity == null || quantity < 1) {
    throw new IllegalArgumentException("Quantity must be at least 1.");
}

String email = authentication.getName();

User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found."));

CartItem item = cartItemRepository
        .findByIdAndUser_Id(cartItemId, user.getId())
        .orElseThrow(() -> new IllegalArgumentException("Cart item not found."));

Product product = item.getProduct();

if (!Boolean.TRUE.equals(product.getActive())) {
    throw new IllegalArgumentException("This product is not available.");
}

if (quantity > product.getStock()) {
    throw new IllegalArgumentException(
            "Only " + product.getStock() + " items are available.");
}

item.setQuantity(quantity);
cartItemRepository.save(item);

}

@Transactional
public void removeFromCart(
Authentication authentication,
Long cartItemId) {

if (authentication == null || !authentication.isAuthenticated()) {
    throw new IllegalStateException("Please log in first.");
}

String email = authentication.getName();

User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found."));

CartItem item = cartItemRepository
        .findByIdAndUser_Id(cartItemId, user.getId())
        .orElseThrow(() -> new IllegalArgumentException("Cart item not found."));

cartItemRepository.delete(item);

}
public void addToCart(Authentication authentication, Long productId) {

    if (authentication == null || !authentication.isAuthenticated()) {
        throw new IllegalStateException("Please log in to add products to your cart.");
    }

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found."));

    if (!Boolean.TRUE.equals(product.getActive())) {
        throw new IllegalArgumentException("This product is not available.");
    }

    if (product.getStock() == null || product.getStock() <= 0) {
        throw new IllegalArgumentException("This product is out of stock.");
    }

    CartItem cartItem = cartItemRepository
            .findByUser_IdAndProduct_Id(user.getId(), productId)
            .orElse(null);

    if (cartItem != null) {

        if (cartItem.getQuantity() >= product.getStock()) {
            throw new IllegalArgumentException("Available stock limit reached.");
        }

        cartItem.setQuantity(cartItem.getQuantity() + 1);

    } else {

        CartItem newCartItem = new CartItem();
        newCartItem.setUser(user);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(1);

        cartItem = newCartItem;
    }

    cartItemRepository.save(cartItem);
}

}