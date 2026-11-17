
package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class budgetDTO {

    @NotBlank(message = "Budget category is required")
    private String category;

    @NotNull(message = "Monthly budget limit is required")
    @DecimalMin(value = "0.01", message = "Budget must be greater than zero")
    private BigDecimal monthlyLimit;

    @NotNull(message = "Budget month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Budget year is required")
    @Min(value = 2000, message = "Invalid budget year")
    private Integer year;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;
}
