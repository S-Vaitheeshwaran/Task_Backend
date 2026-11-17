
package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ecommerce_orders")
@Getter
@Setter
@NoArgsConstructor
public class OrderVo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_number", unique = true, nullable = false)
	private String orderNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private CustomerVo customer;

	@Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false)
	private OrderStatus orderStatus;

	// Delivery details saved with this order
	@Column(name = "delivery_name", nullable = false, length = 150)
	private String deliveryName;

	@Column(name = "delivery_phone", nullable = false, length = 20)
	private String deliveryPhone;

	@Column(name = "delivery_email", nullable = false, length = 150)
	private String deliveryEmail;

	@Column(name = "address_line1", nullable = false, length = 255)
	private String addressLine1;

	@Column(name = "address_line2", length = 255)
	private String addressLine2;

	@Column(name = "delivery_city", nullable = false, length = 100)
	private String city;

	@Column(name = "delivery_state", nullable = false, length = 100)
	private String state;

	@Column(name = "delivery_postal_code", nullable = false, length = 15)
	private String postalCode;

	@Column(name = "delivery_country", nullable = false, length = 100)
	private String country;

	@Column(name = "delivery_instructions", length = 500)
	private String deliveryInstructions;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItemVo> items = new ArrayList<>();

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();

		if (orderStatus == null) {
			orderStatus = OrderStatus.PENDING;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
