
package com.example.demo.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class budgetResponseDTO {

    private Long id;

    private String category;

    private BigDecimal monthlyLimit;

    private Integer month;

    private Integer year;

    private String notes;
}
