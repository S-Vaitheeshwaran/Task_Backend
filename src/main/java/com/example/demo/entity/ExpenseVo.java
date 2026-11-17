package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "expenses")
@NoArgsConstructor

public class ExpenseVo 
{
	@Id
	@GeneratedValue(strategy =GenerationType.SEQUENCE,generator="expense")
	@SequenceGenerator(name="expense",sequenceName= "expensesSeq" ,initialValue =1000000001,allocationSize =1)
	@Column(name="expense_id")
	private long id;
	
	 @Column(nullable = false, length = 100)
	    private String title;

	    @Column(length = 255)
	    private String description;

	    @Column(nullable = false, precision = 12, scale = 2)
	    private BigDecimal amount;

	    @Column(nullable = false, length = 50)
	    private String category;

	    @Column(name = "expense_date", nullable = false)
	    private LocalDate expenseDate;

	    @Column(name = "payment_method", nullable = false, length = 30)
	    private String paymentMethod;

	    @Column(length = 255)
	    private String notes;
	    
	    @ManyToOne(fetch = FetchType.LAZY,optional = false)
	    @JoinColumn(name = "user_id", nullable = false)
	    private UserVo user;

}
