
package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incomes")
@Data
@NoArgsConstructor
public class IncomeVo {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "income")
	@SequenceGenerator(name = "income", sequenceName = "incomesSeq", initialValue = 1000000001, allocationSize = 1)
	private Long id;
	
	@Column(name="title")
	private String title;

	@Column(name="description")
	private String description;

	@Column(precision = 12, scale = 2)
	private BigDecimal amount;

	@Column(name="category")
	private String category;

	@Column(name="incomeDate")
	private LocalDate incomeDate;

	@Column(name="paymentMethod")
	private String paymentMethod;

	@Column(name="notes")
	private String notes;
	
	@ManyToOne(fetch = FetchType.LAZY,optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserVo user;
}
