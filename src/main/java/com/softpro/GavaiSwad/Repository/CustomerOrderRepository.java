package com.softpro.GavaiSwad.Repository;

import com.softpro.GavaiSwad.Entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerOrderRepository
        extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUser_IdOrderByCreatedAtDesc(Long userId);

 // Fetch orders assigned to a specific delivery partner
    List<CustomerOrder> findByDeliveryPartner_IdOrderByCreatedAtDesc(Long deliveryPartnerId);
    // Total orders
    long countBy();

    // Pending orders
    long countByOrderStatus(String orderStatus);

    // Delivered orders
    long countByOrderStatusIgnoreCase(String orderStatus);
    
    

    // Total revenue from non-cancelled orders
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM CustomerOrder o
        WHERE UPPER(o.orderStatus) <> 'CANCELLED'
    """)
    BigDecimal getTotalRevenue();

    // Monthly sales report
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM CustomerOrder o
        WHERE o.createdAt >= :startDate
        AND o.createdAt < :endDate
        AND UPPER(o.orderStatus) <> 'CANCELLED'
    """)
    BigDecimal getMonthlySales(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}