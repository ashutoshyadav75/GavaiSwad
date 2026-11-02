package com.softpro.GavaiSwad.Service;

import com.softpro.GavaiSwad.Repository.CustomerOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AdminReportService {

    private final CustomerOrderRepository orderRepository;

    public AdminReportService(CustomerOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Total Orders
    public long getTotalOrders() {
        return orderRepository.count();
    }

    // Pending Orders
    public long getPendingOrders() {
        return orderRepository.countByOrderStatus("PLACED");
    }

    // Delivered Orders
    public long getDeliveredOrders() {
        return orderRepository.countByOrderStatusIgnoreCase("DELIVERED");
    }

    // Total Revenue (excluding cancelled orders)
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    // Current Month Sales
    public BigDecimal getMonthlySales() {

        LocalDate today = LocalDate.now();

        LocalDateTime startDate = today
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime endDate = today
                .plusMonths(1)
                .withDayOfMonth(1)
                .atStartOfDay();

        BigDecimal sales = orderRepository.getMonthlySales(
                startDate,
                endDate
        );

        return sales != null ? sales : BigDecimal.ZERO;
    }
}