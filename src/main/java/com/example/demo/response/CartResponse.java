
package com.example.demo.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartResponse {

    private Long cartId;
    private Long customerId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;

    @Data
    @NoArgsConstructor
    public static class CartItemResponse {

        private Long cartItemId;
        private Long productId;
        private String productName;
        private String category;
        private String imageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}
