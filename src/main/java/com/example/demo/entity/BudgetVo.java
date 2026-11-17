
package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budgets", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id", "category", "budget_month", "budget_year" }) }

)
@Data
@NoArgsConstructor
public class BudgetVo {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "budget")
	@SequenceGenerator(name = "budget", sequenceName = "budgetsSeq", initialValue = 1000000001, allocationSize = 1)
	private Long id;

	@Column(nullable = false)
	private String category;

	@Column(name = "monthly_limit", precision = 12, scale = 2, nullable = false)
	private BigDecimal monthlyLimit;

	@Column(name = "budget_month", nullable = false)
	private Integer month;

	@Column(name = "budget_year", nullable = false)
	private Integer year;

	@Column(name = "notes")
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY,optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserVo user;
}
