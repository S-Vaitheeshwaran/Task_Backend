
package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class expenseDTO {

    @NotBlank(message = "Expense title is required")
    @Size(min = 2, max = 100,
          message = "Title must contain 2 to 100 characters")
    private String title;

    @Size(max = 255,
          message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Expense amount is required")
    @DecimalMin(value = "0.01",
                message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Expense date is required")
    @PastOrPresent(message = "Expense date cannot be in the future")
    private LocalDate expenseDate;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Size(max = 255,
          message = "Notes cannot exceed 255 characters")
    private String notes;
}
