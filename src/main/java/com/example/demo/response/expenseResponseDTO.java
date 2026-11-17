
package com.example.demo.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class expenseResponseDTO {

    private Long id;

    private String title;

    private String description;

    private BigDecimal amount;

    private String category;

    private LocalDate expenseDate;

    private String paymentMethod;

    private String notes;
}
