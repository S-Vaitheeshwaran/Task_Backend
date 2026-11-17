
package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class incomeDTO {

    @NotBlank(message = "Income title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Income amount is required")
    @DecimalMin(value = "0.01", message = "Income amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Income category is required")
    private String category;

    @NotNull(message = "Income date is required")
    @PastOrPresent(message = "Income date cannot be in the future")
    private LocalDate incomeDate;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;
}
