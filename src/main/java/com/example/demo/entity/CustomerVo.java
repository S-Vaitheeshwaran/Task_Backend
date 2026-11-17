
package com.example.demo.entity;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ecommerce_customers", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
@Data
@NoArgsConstructor
public class CustomerVo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@Column(length = 15)
	private String phone;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private Boolean active = true;

	@Column(name = "active_token_hash", length = 64)
	private String activeTokenHash;

	@Column(name = "token_expiry")
	private Instant tokenExpiry;

	@Column(name = "reset_otp_hash", length = 64)
	private String resetOtpHash;

	@Column(name = "reset_otp_expiry")
	private LocalDateTime resetOtpExpiry;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
