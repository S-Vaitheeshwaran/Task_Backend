
package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "ecommerce_cart_items",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"cart_id", "product_id"}
        )
    }
)
@Data
@NoArgsConstructor
public class CartItemVo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartVo cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductVo product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}
