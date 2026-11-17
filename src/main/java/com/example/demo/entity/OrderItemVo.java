package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ecommerce_order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItemVo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private OrderVo order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private ProductVo product;

	@Column(name = "product_name", nullable = false)
	private String productName;

	@Column(nullable = false)
	private Integer quantity;

	@Column(name = "unit_price", precision = 12, scale = 2, nullable = false)
	private BigDecimal unitPrice;

	@Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
	private BigDecimal subtotal;
}