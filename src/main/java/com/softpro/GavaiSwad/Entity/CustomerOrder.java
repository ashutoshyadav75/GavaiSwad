package com.softpro.GavaiSwad.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_orders")
public class CustomerOrder {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "user_id", nullable = false)
private User user;

@Column(nullable = false, length = 150)
private String fullName;

@Column(nullable = false, length = 20)
private String phone;

@Column(nullable = false, length = 500)
private String deliveryAddress;

@Column(nullable = false, precision = 12, scale = 2)
private BigDecimal totalAmount;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "delivery_partner_id")
private User deliveryPartner;

@Column(nullable = false, length = 30)
private String paymentMethod = "COD";

@Column(nullable = false, length = 30)
private String paymentStatus = "PENDING";

@Column(nullable = false, length = 30)
private String orderStatus = "PLACED";

@Column(nullable = false)
private LocalDateTime createdAt = LocalDateTime.now();

public CustomerOrder() {
}

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public User getUser() {
    return user;
}

public void setUser(User user) {
    this.user = user;
}

public String getFullName() {
    return fullName;
}

public void setFullName(String fullName) {
    this.fullName = fullName;
}

public String getPhone() {
    return phone;
}

public void setPhone(String phone) {
    this.phone = phone;
}

public String getDeliveryAddress() {
    return deliveryAddress;
}

public void setDeliveryAddress(String deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
}

public BigDecimal getTotalAmount() {
    return totalAmount;
}

public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
}

public String getPaymentMethod() {
    return paymentMethod;
}

public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
}

public String getPaymentStatus() {
    return paymentStatus;
}

public void setPaymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
}

public String getOrderStatus() {
    return orderStatus;
}

public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
}

public LocalDateTime getCreatedAt() {
    return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
}

public User getDeliveryPartner() {
    return deliveryPartner;
}

public void setDeliveryPartner(User deliveryPartner) {
    this.deliveryPartner = deliveryPartner;
}

}