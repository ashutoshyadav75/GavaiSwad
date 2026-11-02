package com.softpro.GavaiSwad.Repository;

import com.softpro.GavaiSwad.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

// Find all cart items of a user
List<CartItem> findByUser_Id(Long userId);

// Find a specific product in the user's cart
Optional<CartItem> findByUser_IdAndProduct_Id(Long userId, Long productId);

// Find a cart item belonging to a specific user
Optional<CartItem> findByIdAndUser_Id(Long id, Long userId);

// Count cart items for a user
long countByUser_Id(Long userId);

}