
package com.example.demo.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.OrderStatus;

import lombok.Data;

@Data
public class OrderResponse {

	private Long id;
	private String orderNumber;
	private Long customerId;
	private BigDecimal totalAmount;
	private OrderStatus orderStatus;

	// Delivery details
	private String deliveryName;
	private String deliveryPhone;
	private String deliveryEmail;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private String deliveryInstructions;

	private List<OrderItemResponse> items;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
